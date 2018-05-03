package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinActivityLogService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.dao.config.Where;
import aljoin.object.*;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.activiti.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.javax.el.ExpressionFactory;
import org.activiti.engine.impl.javax.el.ValueExpression;
import org.activiti.engine.impl.juel.ExpressionFactoryImpl;
import org.activiti.engine.impl.juel.SimpleContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 *
 * 固定表单流程审批公用方法(服务实现类).
 *
 * @author：wangj.
 *
 * @date： 2017-10-12
 */
@Service
public class ActFixedFormServiceImpl {
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;

    /**
     *
     * 填写意见
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public RetMsg addComment(String taskId, String message) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        taskService.addComment(taskId, processInstanceId, message);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     *
     * 根据任务ID查看流程图
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public void showImg(HttpServletRequest request, HttpServletResponse response, String taskId) throws Exception {
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addDateHeader("expires", 0);
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        InputStream is = actActivitiService.getRuImageInputStream(processInstanceId, true);
        OutputStream os = response.getOutputStream();
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "PNG", os);
        os.flush();
        os.close();
    }

    /**
     *
     * 获取流程日志
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public Map<String, Object> getLogInfoByTaskId(String taskId, String processName) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        List<Long> assigneeIdList = new ArrayList<Long>();
        List<Long> recevieUserIdList = new ArrayList<Long>();
        List<Comment> commentList = activitiService.getCommentInfo(taskId);
        // String key = "";
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<HistoricActivityInstance> activitieList =
            historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        if (null != activitieList && !activitieList.isEmpty()) {
            for (HistoricActivityInstance historicActivityInstance : activitieList) {
                FixedFormProcessLog log = new FixedFormProcessLog();
                if (null != historicActivityInstance) {
                    log.setTaskId(StringUtils.isNotEmpty(historicActivityInstance.getTaskId())
                        ? historicActivityInstance.getTaskId() : "");
                    log.setProcessInstanceId(processInstanceId);
                    if (StringUtils.isNotEmpty(historicActivityInstance.getAssignee())) {
                        log.setOperationId(StringUtils.isNotEmpty(historicActivityInstance.getAssignee())
                            ? historicActivityInstance.getAssignee() : "");
                        assigneeIdList.add(Long.parseLong(log.getOperationId()));
                    }
                    if (StringUtils.isNotEmpty(historicActivityInstance.getActivityName())) {
                        log.setDirection(processName + " ----> 提交 ----> " + historicActivityInstance.getActivityName());
                    }

                    List<TaskDefinition> taskDefinitionList = activitiService.getNextTaskInfo(processInstanceId, false);
                    if (null != taskDefinitionList && !taskDefinitionList.isEmpty()) {
                        if (null != taskDefinitionList.get(0).getAssigneeExpression()) {
                            log.setRecevieUserId(String.valueOf(taskDefinitionList.get(0).getAssigneeExpression()));
                            recevieUserIdList
                                .add(Long.parseLong(String.valueOf(taskDefinitionList.get(0).getAssigneeExpression())));
                        }
                    }

                    if (null != commentList && !commentList.isEmpty()) {
                        for (Comment comment : commentList) {
                            if (comment.getTaskId().equals(historicActivityInstance.getTaskId())) {
                                log.setComment(comment.getFullMessage());
                            }
                        }
                    }

                    if (null != historicActivityInstance.getStartTime()) {
                        log.setOperationTime(historicActivityInstance.getStartTime());
                    }

                    logList.add(log);
                }
            }
        }
        map.put("assigneeIdList", assigneeIdList);
        map.put("recevieUserIdList", recevieUserIdList);
        map.put("logList", logList);
        return map;
    }

    /**
     *
     * 获取上一个节点的信息
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public RetMsg getPreTaskInfo(String taskId) throws Exception {
        // 如果没有上级节点
        if (StringUtils.isEmpty(taskId)) {
            throw new Exception("无上级节点，不能退回");
        }

        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        List<TaskDefinition> preList = null;
        List<CustomerTaskDefinition> list = new ArrayList<CustomerTaskDefinition>();
        List<Long> userIdList = new ArrayList<Long>();
        List<Long> deptIdList = new ArrayList<Long>();
        if (StringUtils.isNotEmpty(processInstanceId) && null != currentTask && !currentTask.isEmpty()) {
            String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
            if (StringUtils.isNotEmpty(currentTaskKey)) {
                preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
                List<HistoricTaskInstance> taskList =
                    historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
                if (null != preList && !preList.isEmpty()) {
                    for (TaskDefinition definition : preList) {
                        for (HistoricTaskInstance historicTaskInstance : taskList) {
                            if (historicTaskInstance.getTaskDefinitionKey().equals(definition.getKey())) {
                                CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
                                taskDefinition
                                    .setKey(StringUtils.isNotEmpty(definition.getKey()) ? definition.getKey() : "");
                                if (null != definition.getAssigneeExpression()) {
                                    taskDefinition.setAssignee(String.valueOf(definition.getAssigneeExpression()));
                                    userIdList.add(Long.valueOf(taskDefinition.getAssignee()));
                                }

                                if (null != definition.getCandidateGroupIdExpressions()) {
                                    String deptId = "";
                                    String departId = "";
                                    Iterator<Expression> it = definition.getCandidateGroupIdExpressions().iterator();
                                    while (it.hasNext()) {
                                        departId = it.next() + "";
                                        deptId += departId + ";";
                                        deptIdList.add(Long.valueOf(departId));
                                    }
                                    taskDefinition.setDeptId(deptId);
                                }
                                taskDefinition.setNextNodeName(null != definition.getNameExpression()
                                    ? String.valueOf(definition.getNameExpression()) : "");
                                list.add(taskDefinition);
                            }
                        }
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setObject(list);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     *
     * 检查当前任务节点是否被签收
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public RetMsg isClaim(String taskId, String processInstanceId, String userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 当前环节是否已签收
        if (StringUtils.isNotEmpty(taskId) && StringUtils.isNotEmpty(userId)) {
            TaskQuery query = taskService.createTaskQuery().taskId(taskId);
            if (null != query) {
                HistoricTaskInstance hisTask =
                    historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())
                    && hisTask.getAssignee().equals(userId)) {
                    // 已签收
                    retMsg.setCode(0);
                } else if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
                    // 未签收
                    retMsg.setCode(1);
                }
            }
        }
        // 是否可撤回
        if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isNotEmpty(userId)) {
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc().list();
            if (!activities.isEmpty()) {
                if (!activities.get(0).getActivityType().equals("userTask")) {
                    // 排他网关不允许撤回
                    retMsg.setCode(3);
                    return retMsg;
                }
                // 当前环节id
                String thistaskId = activities.get(0).getTaskId();
                // 上一环节id
                String pretaskId = activities.get(1).getTaskId();
                // 当前环节是否被签收
                TaskQuery query = taskService.createTaskQuery().taskId(thistaskId);
                if (query != null) {
                    Task curtask = query.singleResult();
                    if (StringUtils.isNotEmpty(curtask.getAssignee())) {
                        // 任务已被办理人签收不可撤回
                        retMsg.setCode(2);
                    } else {
                        List<TaskDefinition> preList =
                            activitiService.getPreTaskInfo(curtask.getTaskDefinitionKey(), processInstanceId);
                        if (preList.size() <= 0) {
                            // 当前节点没有上一环节，不能撤回
                            retMsg.setCode(3);
                        } else {
                            HistoricTaskInstance histask =
                                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                                    .taskId(pretaskId).taskAssignee(userId).singleResult();
                            if (null == histask) {
                                // 当前用户不是上一环节办理人
                                retMsg.setCode(3);
                            } else {
                                if (!histask.getTaskDefinitionKey().equals(preList.get(0).getKey())) {
                                    // 历史中的上一环节跟取到的上一环节不是一个环节，说明不是正向流程走来的，不能撤回
                                    retMsg.setCode(3);
                                } else {
                                    // 可以撤回
                                    retMsg.setCode(4);
                                }
                            }
                        }
                    }
                } else {
                    // 不可撤回
                    retMsg.setCode(2);
                }
            }
        }
        return retMsg;
    }

    public RetMsg isIoaClaim(String taskId, String processInstanceId, String userId) throws Exception {
        // 1:未签收; 0:已签收; 2:不可撤回
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isNotEmpty(taskId)) {
            HistoricTaskInstance hisTask =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())
                && hisTask.getAssignee().equals(userId)) {
                // 已签收
                retMsg.setCode(2);
                return retMsg;
            }
            // 判断是否可撤回
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null == task) {
                // 当前节点已经改变
                retMsg.setCode(2);
                return retMsg;
            }
            if (StringUtils.isEmpty(processInstanceId)) {
                processInstanceId = task.getId();
            }
            MultiInstanceLoopCharacteristics isMult =
                activitiService.getMultiInstance(processInstanceId, task.getTaskDefinitionKey());
            if (isMult != null) {
                // 多实例节点不允许撤回
                retMsg.setCode(2);
                return retMsg;
            }
            Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
            // logWhere.setSqlSelect(
            // "id,operate_user_id,operate_full_name,operate_time,operate_status,receive_user_ids,receive_full_names,comment,last_task_name,last_task_id,current_task_name,current_task_def_key,current_task_id,next_task_def_key,next_task_name,proc_inst_id");
            logWhere.eq("proc_inst_id", processInstanceId);
            logWhere.ne("operate_status", WebConstant.PROCESS_OPERATE_STATUS_6)
                .ne("operate_status", WebConstant.PROCESS_OPERATE_STATUS_7)
                .ne("operate_status", WebConstant.PROCESS_OPERATE_STATUS_5);
            logWhere.orderBy("operate_time", false);
            List<ActAljoinActivityLog> actAljoinActivityLogList = actAljoinActivityLogService.selectList(logWhere);
            if (actAljoinActivityLogList.size() == 0) {
                retMsg.setCode(2);
                return retMsg;
            }
            String currentTaskKey = task.getTaskDefinitionKey();
            ActAljoinActivityLog lastLog = null;
            for (ActAljoinActivityLog actAljoinActivityLog : actAljoinActivityLogList) {
                if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)
                    && actAljoinActivityLog.getCurrentTaskDefKey().equals(currentTaskKey)) {
                    lastLog = new ActAljoinActivityLog();
                    lastLog = actAljoinActivityLog;
                    Integer count = 0;
                    for (ActAljoinActivityLog activityLog : actAljoinActivityLogList) {
                        // 在提交动作之后
                        if (activityLog.getOperateTime().after(actAljoinActivityLog.getOperateTime())) {
                            // 该节点提交到当前节点
                            if (activityLog.getLastTaskDefKey().equals(lastLog.getLastTaskDefKey())
                                && activityLog.getCurrentTaskDefKey().equals(currentTaskKey)
                                && activityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)) {
                                count += 1;
                            }
                            // 当前节点退回或撤回到该节点
                            if (activityLog.getLastTaskDefKey().equals(currentTaskKey)
                                && activityLog.getCurrentTaskDefKey().equals(lastLog.getLastTaskDefKey())
                                && (activityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_2)
                                    || activityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_3))) {
                                count -= 1;
                            }
                        }
                    }
                    if (count >= 0) {
                        break;
                    }
                }
            }
            if (null == lastLog) {
                retMsg.setCode(2);
                return retMsg;
            }
            if (!userId.equals(lastLog.getOperateUserId())){
                retMsg.setCode(2);
                return retMsg;
            }
            // 不在一条执行流上不能退回
            String lastExcutionId = "";
            for (ActAljoinActivityLog actAljoinActivityLog : actAljoinActivityLogList) {
                if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)
                    && actAljoinActivityLog.getCurrentTaskDefKey().equals(lastLog.getLastTaskDefKey())) {
                    lastExcutionId = actAljoinActivityLog.getExecutionId();
                    break;
                }
            }
            if (!lastExcutionId.equals(task.getExecutionId())) {
                retMsg.setCode(2);
                return retMsg;
            }
        }
        retMsg.setCode(0);
        return retMsg;
    }

    /**
     *
     * 获取下一个任务节点信息
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public RetMsg getNextTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
        List<CustomerTaskDefinition> taskDefinitionList = new ArrayList<CustomerTaskDefinition>();
        if (null != list && !list.isEmpty()) {
            for (TaskDefinition definition : list) {
                if (null != definition) {
                    CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
                    taskDefinition.setKey(definition.getKey());
                    taskDefinition.setAssignee(String.valueOf(definition.getAssigneeExpression()));
                    taskDefinition.setNextNodeName(String.valueOf(definition.getNameExpression()));
                    taskDefinitionList.add(taskDefinition);
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setObject(taskDefinitionList);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    public Map<String, Object> getLog(String tasksId, String processInstanceId) throws Exception {
        if (null == processInstanceId) {
            if (null == tasksId || StringUtils.isEmpty(tasksId)) {
                throw new Exception("流程实例id或任务id至少有一个不为空");
            }
            processInstanceId = taskService.createTaskQuery().taskId(tasksId).singleResult().getProcessInstanceId();
        }
        Map<String, Object> map1 = new HashMap<String, Object>();
        List<String> recevieUserIdList = new ArrayList<String>();
        List<Long> assigneeIdList = new ArrayList<Long>();
        List<Comment> list = new ArrayList<Comment>();
        List<HistoricActivityInstance> activitieList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<Event> events = new ArrayList<Event>();
        // 根据流程实例id取出意见
        list = taskService.getProcessInstanceComments(processInstanceId);
        // 构造日志列表
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        List<HistoricActivityInstance> activityList = new ArrayList<HistoricActivityInstance>();
        // 流转日志只包含用户任务节点
        for (int i = 0; i < activitieList.size(); i++) {
            HistoricActivityInstance historicActivityInstance = activitieList.get(i);
            if (historicActivityInstance.getActivityType().contains("userTask")) {
                activityList.add(historicActivityInstance);
            }
        }
        String preActivityId = "";
        // 循环用户任务节点
        int index = 0;
        String indexUser = "";
        for (int i = 0; i < activityList.size(); i++) {
            HistoricActivityInstance historicActivityInstance = activityList.get(i);
            FixedFormProcessLog log = new FixedFormProcessLog();
            String taskId = historicActivityInstance.getTaskId();
            String nextTaskName = "";
            String nextActivityId = "";
            // 根据任务id取操作人选择的接收人
            if (i < activityList.size() - 1) {
                HistoricActivityInstance his = activityList.get(i + 1);
                nextActivityId = his.getActivityId();
                nextTaskName = " --> " + his.getActivityName();

                events = taskService.getTaskEvents(his.getTaskId());
                String receivedIds = "";
                for (Event event : events) {
                    // 获取任务接收人（候选人）
                    if (event.getAction().equals("AddUserLink")) {
                        String receivedId = event.getMessage().substring(0, event.getMessage().indexOf("_"));
                        recevieUserIdList.add(receivedId);
                        receivedIds += receivedId + ";";
                    }
                }
                log.setRecevieUserId(receivedIds);
            }
            if (!historicActivityInstance.getActivityId().equals(preActivityId)
                && !historicActivityInstance.getActivityId().equals(nextActivityId)) {
                log.setDirection(historicActivityInstance.getActivityName() + nextTaskName);
            } else {
                if (index == 0) {
                    index = i;
                }
                log.setDirection(historicActivityInstance.getActivityName());
                indexUser += log.getRecevieUserId();
                log.setRecevieUserId("");
            }
            for (Comment comment : list) {
                if (comment.getTaskId().equals(taskId)) {
                    log.setComment(comment.getFullMessage());
                    log.setOperationTime(comment.getTime());
                    if (null != comment.getUserId()) {
                        log.setOperationId(comment.getUserId());
                        assigneeIdList.add(Long.valueOf(comment.getUserId()));
                    }
                }
            }
            if (null != historicActivityInstance.getAssignee()
                && StringUtils.isNotEmpty(historicActivityInstance.getAssignee())) {
                log.setOperationId(historicActivityInstance.getAssignee());
            }

            if (historicActivityInstance.getAssignee() != null) {
                assigneeIdList.add(Long.valueOf(historicActivityInstance.getAssignee()));
            }
            log.setTaskId(taskId);
            logList.add(log);

            preActivityId = historicActivityInstance.getActivityId();
        }
        if (index > 0) {
            FixedFormProcessLog log = logList.get(index - 1);
            log.setRecevieUserId(indexUser + log.getRecevieUserId());
        }
        map1.put("assigneeIdList", assigneeIdList);
        map1.put("recevieUserIdList", recevieUserIdList);
        map1.put("logList", logList);
        return map1;
    }

    public Map<String, Object> getAppLog(String tasksId, String processInstanceId) throws Exception {
        if (null == processInstanceId) {
            if (null == tasksId || StringUtils.isEmpty(tasksId)) {
                throw new Exception("流程实例id或任务id至少有一个不为空");
            }
            processInstanceId = taskService.createTaskQuery().taskId(tasksId).singleResult().getProcessInstanceId();
        }
        Map<String, Object> map1 = new HashMap<String, Object>();
        List<String> recevieUserIdList = new ArrayList<String>();
        List<Long> assigneeIdList = new ArrayList<Long>();
        List<Comment> list = new ArrayList<Comment>();
        List<HistoricActivityInstance> activitieList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().asc().list();
        List<Event> events = new ArrayList<Event>();
        // 根据流程实例id取出意见
        list = taskService.getProcessInstanceComments(processInstanceId);
        // 构造日志列表
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        List<HistoricActivityInstance> activityList = new ArrayList<HistoricActivityInstance>();
        // 流转日志只包含用户任务节点
        for (int i = 0; i < activitieList.size(); i++) {
            HistoricActivityInstance historicActivityInstance = activitieList.get(i);
            if (historicActivityInstance.getActivityType().contains("userTask")) {
                activityList.add(historicActivityInstance);
            }
        }
        String preActivityId = "";
        // 循环用户任务节点
        for (int i = 0; i < activityList.size(); i++) {
            HistoricActivityInstance historicActivityInstance = activityList.get(i);
            FixedFormProcessLog log = new FixedFormProcessLog();
            String taskId = historicActivityInstance.getTaskId();
            String nextActivityId = "";
            // 根据任务id取操作人选择的接收人
            if (i < activityList.size() - 1) {
                HistoricActivityInstance his = activityList.get(i + 1);
                nextActivityId = his.getActivityId();
                events = taskService.getTaskEvents(his.getTaskId());
                String receivedIds = "";
                for (Event event : events) {
                    // 获取任务接收人（候选人）
                    if (event.getAction().equals("AddUserLink")) {
                        String receivedId = event.getMessage().substring(0, event.getMessage().indexOf("_"));
                        recevieUserIdList.add(receivedId);
                        receivedIds += receivedId + ";";
                    }
                }
                log.setRecevieUserId(receivedIds);
            }
            if (!historicActivityInstance.getActivityId().equals(preActivityId)
                && !historicActivityInstance.getActivityId().equals(nextActivityId)) {
                log.setDirection(historicActivityInstance.getActivityName());
            } else {
                log.setDirection(historicActivityInstance.getActivityName());
            }
            for (Comment comment : list) {
                if (comment.getTaskId().equals(taskId)) {
                    log.setComment(comment.getFullMessage());
                    log.setOperationTime(comment.getTime());
                    log.setOperationId(comment.getUserId());
                }
            }
            if (null != historicActivityInstance.getAssignee()
                && StringUtils.isNotEmpty(historicActivityInstance.getAssignee())) {
                log.setOperationId(historicActivityInstance.getAssignee());
            }

            if (historicActivityInstance.getAssignee() != null) {
                assigneeIdList.add(Long.valueOf(historicActivityInstance.getAssignee()));
            }
            log.setTaskId(taskId);
            logList.add(log);

            preActivityId = historicActivityInstance.getActivityId();
        }
        map1.put("assigneeIdList", assigneeIdList);
        map1.put("recevieUserIdList", recevieUserIdList);
        map1.put("logList", logList);
        return map1;
    }

    /**
     *
     * 获取流程日志
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public Map<String, Object> getLogInfoByProcessInstanceId(String processInstanceId, String processName)
        throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        List<Long> assigneeIdList = new ArrayList<Long>();
        List<Long> recevieUserIdList = new ArrayList<Long>();
        List<Comment> commentList = null;
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (null != taskList && !taskList.isEmpty()) {
            Task task = taskList.get(0);
            if (null != task && StringUtils.isNotEmpty(task.getId())) {
                commentList = activitiService.getCommentInfo(task.getId());
            }
        }
        List<HistoricActivityInstance> activitieList =
            historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        List<TaskDefinition> taskDefinitionList = activitiService.getNextTaskInfo(processInstanceId, false);
        if (null != activitieList && !activitieList.isEmpty()) {
            for (HistoricActivityInstance historicActivityInstance : activitieList) {
                FixedFormProcessLog log = new FixedFormProcessLog();
                if (null != historicActivityInstance) {
                    log.setTaskId(StringUtils.isNotEmpty(historicActivityInstance.getTaskId())
                        ? historicActivityInstance.getTaskId() : "");
                    log.setProcessInstanceId(processInstanceId);
                    if (StringUtils.isNotEmpty(historicActivityInstance.getAssignee())) {
                        log.setOperationId(StringUtils.isNotEmpty(historicActivityInstance.getAssignee())
                            ? historicActivityInstance.getAssignee() : "");
                        assigneeIdList.add(Long.parseLong(log.getOperationId()));
                    }
                    if (StringUtils.isNotEmpty(historicActivityInstance.getActivityName())) {
                        log.setDirection(processName + " ----> 提交 ----> " + historicActivityInstance.getActivityName());
                    }
                    if (null != taskDefinitionList && !taskDefinitionList.isEmpty()) {
                        log.setRecevieUserId(String.valueOf(taskDefinitionList.get(0).getAssigneeExpression()));
                        recevieUserIdList.add(Long.parseLong(log.getRecevieUserId()));
                    }
                    if (null != commentList && !commentList.isEmpty()) {
                        for (Comment comment : commentList) {
                            if (comment.getTaskId().equals(historicActivityInstance.getTaskId())) {
                                log.setComment(comment.getFullMessage());
                            }
                        }
                    }
                }
            }
        }
        map.put("assigneeIdList", assigneeIdList);
        map.put("recevieUserIdList", recevieUserIdList);
        map.put("logList", logList);
        return map;
    }

    public List<TaskDefinition> getNextTaskInfo2(String processInstanceId, boolean withCondition, String activityId,
        Map<String, String> paramMap) throws Exception {
        String id = null;
        List<TaskDefinition> taskList = new ArrayList<TaskDefinition>();
        // 获取流程发布Id
        String definitionId = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getProcessDefinitionId();
        ProcessDefinitionEntity processDefinitionEntity =
            (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                .getDeployedProcessDefinition(definitionId);
        // 获取流程所有节点
        List<ActivityImpl> activitiList = processDefinitionEntity.getActivities();
        // 遍历所有节点信息
        for (ActivityImpl activityImpl : activitiList) {
            id = activityImpl.getId();
            if (activityId.equals(id)) {
                // 获取下一个节点信息
                taskList = nextTaskDefinition(activityImpl, activityImpl.getId(), null, processInstanceId,
                    withCondition, paramMap);
                break;
            }
        }
        // EndEvent_1efz7il
        // if (taskList.size() == 0) {
        // // 获取最后一个结束节点
        // String endEventId = "";
        // for (ActivityImpl activityImpl : activitiList) {
        // // 获取结束节点的id
        // if (activityImpl.getId().startsWith("EndEvent_")) {
        // endEventId = activityImpl.getId();
        // }
        // }
        // TaskDefinition def = new TaskDefinition(null);
        // def.setKey(endEventId);
        // Expression expression = new JuelExpression(null, "结束");
        // def.setNameExpression(expression);
        // taskList.add(def);
        // }
        return taskList;
    }

    private List<TaskDefinition> nextTaskDefinition(ActivityImpl activityImpl, String activityId, String elString,
        String processInstanceId, boolean withCondition, Map<String, String> paramMap) throws Exception {
        List<TaskDefinition> resultList = new ArrayList<TaskDefinition>();
        PvmActivity ac = null;
        Object s = null;
        // 如果遍历节点为用户任务并且节点不是当前节点信息
        if ("userTask".equals(activityImpl.getProperty("type")) && !activityId.equals(activityImpl.getId())) {
            // 获取该节点下一个节点信息
            TaskDefinition taskDefinition =
                ((UserTaskActivityBehavior)activityImpl.getActivityBehavior()).getTaskDefinition();
            List<TaskDefinition> tempList = new ArrayList<TaskDefinition>();
            tempList.add(taskDefinition);
            return tempList;
            // 当前节点为exclusiveGateway
        } else if ("exclusiveGateway".equals(activityImpl.getProperty("type"))) {
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            // 如果排他网关只有一条线路信息
            if (outTransitions.size() == 1) {
                List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)outTransitions.get(0).getDestination(),
                    activityId, elString, processInstanceId, withCondition, paramMap);
                resultList.addAll(tempList);
            } else if (outTransitions.size() > 1) {
                // 如果排他网关有多条线路信息
                for (PvmTransition tr1 : outTransitions) {
                    // 获取排他网关线路判断条件信息
                    s = tr1.getProperty("conditionText");
                    // 判断el表达式是否成立,线获取流程参数
                    if (withCondition && s != null) {
                        if (isCondition(paramMap, StringUtils.trim(s.toString()))) {
                            List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)tr1.getDestination(),
                                activityId, elString, processInstanceId, withCondition, paramMap);
                            resultList.addAll(tempList);
                        }
                    } else {
                        List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)tr1.getDestination(),
                            activityId, elString, processInstanceId, withCondition, paramMap);
                        resultList.addAll(tempList);
                    }
                }
            }
        } else {// 获取节点所有流向线路信息
            List<PvmTransition> outTransitions = activityImpl.getOutgoingTransitions();
            List<PvmTransition> outTransitionsTemp = null;
            for (PvmTransition tr : outTransitions) {
                // 获取线路的终点节点
                ac = tr.getDestination();
                // 如果箭头有条件，判断条件是否成立
                if (tr.getProperty("conditionText") != null && withCondition) {
                    if (!isCondition(paramMap, (String)tr.getProperty("conditionText"))) {
                        // 根据参数判断条件不成立，不返回下一个任务节点
                        continue;
                    }
                }
                // 如果流向线路为排他网关
                if ("exclusiveGateway".equals(ac.getProperty("type"))
                    || "parallelGateway".equals(ac.getProperty("type"))) {
                    outTransitionsTemp = ac.getOutgoingTransitions();
                    // 如果排他网关只有一条线路信息
                    if (outTransitionsTemp.size() == 1) {
                        List<TaskDefinition> tempList =
                            nextTaskDefinition((ActivityImpl)outTransitionsTemp.get(0).getDestination(), activityId,
                                elString, processInstanceId, withCondition, paramMap);
                        resultList.addAll(tempList);
                        // 如果排他网关有多条线路信息
                    } else if (outTransitionsTemp.size() > 1) {
                        for (PvmTransition tr1 : outTransitionsTemp) {
                            // 获取排他网关线路判断条件信息
                            s = tr1.getProperty("conditionText");
                            if (withCondition && s != null) {
                                // 判断el表达式是否成立
                                if (isCondition(paramMap, StringUtils.trim(s.toString()))) {
                                    List<TaskDefinition> tempList =
                                        nextTaskDefinition((ActivityImpl)tr1.getDestination(), activityId, elString,
                                            processInstanceId, withCondition, paramMap);
                                    resultList.addAll(tempList);
                                }
                            } else {
                                List<TaskDefinition> tempList = nextTaskDefinition((ActivityImpl)tr1.getDestination(),
                                    activityId, elString, processInstanceId, withCondition, paramMap);
                                resultList.addAll(tempList);
                            }
                        }
                    }
                } else if ("userTask".equals(ac.getProperty("type"))) {
                    TaskDefinition tdf = null;
                    if (((ActivityImpl)ac).getActivityBehavior() instanceof UserTaskActivityBehavior) {
                        // 普通节点
                        tdf = ((UserTaskActivityBehavior)((ActivityImpl)ac).getActivityBehavior()).getTaskDefinition();
                    } else if (((ActivityImpl)ac).getActivityBehavior() instanceof ParallelMultiInstanceBehavior) {
                        // 多实例（并行）
                        tdf = ((UserTaskActivityBehavior)(((ParallelMultiInstanceBehavior)((ActivityImpl)ac)
                            .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition();
                    } else if (((ActivityImpl)ac).getActivityBehavior() instanceof SequentialMultiInstanceBehavior) {
                        // 多实例（串行）
                        tdf = ((UserTaskActivityBehavior)(((SequentialMultiInstanceBehavior)((ActivityImpl)ac)
                            .getActivityBehavior()).getInnerActivityBehavior())).getTaskDefinition();
                    }

                    resultList.add(tdf);
                }
            }
        }
        return resultList;
    }

    private boolean isCondition(Map<String, String> param, String el) {
        ExpressionFactory factory = new ExpressionFactoryImpl();
        SimpleContext context = new SimpleContext();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            context.setVariable(entry.getKey(), factory.createValueExpression(entry.getValue(), String.class));
        }
        ValueExpression e = factory.createValueExpression(context, el, boolean.class);
        return (Boolean)e.getValue(context);
    }

    public void showHisImg(HttpServletRequest request, HttpServletResponse response, String processInstanceId)
        throws IOException {
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addDateHeader("expires", 0);
        InputStream is = actActivitiService.getHisImageInputStream(processInstanceId, "");
        OutputStream os = response.getOutputStream();
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "PNG", os);
        os.flush();
        os.close();
    }

    public Map<String, Object> getLogInfoByPins(String processInstanceId, String processName) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        List<Long> assigneeIdList = new ArrayList<Long>();
        List<Long> recevieUserIdList = new ArrayList<Long>();
        List<HistoricActivityInstance> hais = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).activityType("userTask").list();
        // 3）查询每个历史任务的批注
        List<Comment> commentList = new ArrayList<Comment>();
        for (HistoricActivityInstance hai : hais) {
            String historytaskId = hai.getTaskId();
            List<Comment> comments = taskService.getTaskComments(historytaskId);
            // 4）如果当前任务有批注信息，添加到集合中
            if (comments != null && comments.size() > 0) {
                commentList.addAll(comments);
            }
        }
        List<HistoricActivityInstance> activitieList =
            historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
        if (null != activitieList && !activitieList.isEmpty()) {
            for (HistoricActivityInstance historicActivityInstance : activitieList) {
                if (!historicActivityInstance.getActivityType().equals("startEvent")
                    && !historicActivityInstance.getActivityType().equals("endEvent")) {
                    FixedFormProcessLog log = new FixedFormProcessLog();
                    if (null != historicActivityInstance) {
                        log.setTaskId(StringUtils.isNotEmpty(historicActivityInstance.getTaskId())
                            ? historicActivityInstance.getTaskId() : "");
                        log.setProcessInstanceId(processInstanceId);
                        if (StringUtils.isNotEmpty(historicActivityInstance.getAssignee())) {
                            log.setOperationId(StringUtils.isNotEmpty(historicActivityInstance.getAssignee())
                                ? historicActivityInstance.getAssignee() : "");
                            assigneeIdList.add(Long.parseLong(log.getOperationId()));
                        }
                        if (StringUtils.isNotEmpty(historicActivityInstance.getActivityName())) {
                            log.setDirection(
                                processName + " ----> 提交 ----> " + historicActivityInstance.getActivityName());
                        }

                        if (null != commentList && !commentList.isEmpty()) {
                            for (Comment comment : commentList) {
                                if (comment.getTaskId().equals(historicActivityInstance.getTaskId())) {
                                    log.setComment(comment.getFullMessage());
                                }
                            }
                        }

                        if (null != historicActivityInstance.getStartTime()) {
                            log.setOperationTime(historicActivityInstance.getStartTime());
                        }
                        logList.add(log);
                    }
                }
            }
        }
        map.put("assigneeIdList", assigneeIdList);
        map.put("recevieUserIdList", recevieUserIdList);
        map.put("logList", logList);
        return map;
    }

    public void deleteOrgnlTaskAuth(Task userTask) {
        // 删除原来候选人信息(含分组和候选人)
        List<IdentityLink> identityLinkList = taskService.getIdentityLinksForTask(userTask.getId());
        for (IdentityLink identityLink : identityLinkList) {
            if (identityLink.getType().equals("candidate")) {
                if (StringUtils.isNotEmpty(identityLink.getGroupId())) {
                    taskService.deleteGroupIdentityLink(userTask.getId(), identityLink.getGroupId(), "candidate");
                } else if (StringUtils.isNotEmpty(identityLink.getUserId())) {
                    taskService.deleteCandidateUser(userTask.getId(), identityLink.getUserId());
                }
            }
        }
    }

    /**
     *
     * 撤回
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public RetMsg revoke(String processInstanceId, String taskId, String targetUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        Task orgnlTask = null;
        if (StringUtils.isNotEmpty(taskId)) {
            // 获取当前流程实例ID
            orgnlTask = taskService.createTaskQuery().taskId(taskId).singleResult();
            processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        }
        MultiInstanceLoopCharacteristics isMult =
            activitiService.getMultiInstance(processInstanceId, orgnlTask.getTaskDefinitionKey());
        if (isMult != null) {
            retMsg.setCode(1);
            retMsg.setMessage("该环节为为多人办理不能撤回");
            return retMsg;
        }
        List<TaskDefinition> preList =
            activitiService.getPreTaskInfo2(orgnlTask.getTaskDefinitionKey(), processInstanceId);
        if (null != preList && preList.isEmpty()) {
            retMsg.setCode(1);
            retMsg.setMessage("上一节点为空或包含多个任务节点不能撤回");
            return retMsg;
        }
        if (null != preList && preList.size() > 1) {
            retMsg.setCode(1);
            retMsg.setMessage("上一环节不可撤回!!");
            return retMsg;
        }
        List<TaskDefinition> defList =
            actActivitiService.getPreTaskInfo2(orgnlTask.getTaskDefinitionKey(), processInstanceId);
        if (defList.size() != 1) {
            retMsg.setCode(1);
            retMsg.setMessage("流程不满足撤回条件:无或含有多个上级任务节点!!");
            return retMsg;
        }
        if ((null != preList && preList.size() == 1)) {
            String targetTaskKey = preList.get(0).getKey();
            Authentication.setAuthenticatedUserId(targetUserId);
            HistoricTaskInstance preHisTask =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            String newKey = preHisTask.getTaskDefinitionKey();
            if (newKey.equals(targetTaskKey)) {
                retMsg.setCode(1);
                retMsg.setMessage("已退回的任务不可以撤回");
                return retMsg;
            }
            isMult = activitiService.getMultiInstance(processInstanceId, targetTaskKey);
            if (isMult != null) {
                retMsg.setCode(1);
                retMsg.setMessage("上一环节为为多人办理不能撤回");
                return retMsg;
            }
            List<HistoricActivityInstance> activitiess = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc()
                .orderByHistoricActivityInstanceEndTime().desc().list();
            if (!activitiess.isEmpty()) {
                if (!activitiess.get(0).getActivityType().equals("userTask")) {
                    retMsg.setCode(1);
                    retMsg.setMessage("当前任务环节不能撤回！！");
                    return retMsg;
                }
            }
            HistoricTaskInstance hisTask =
                historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
                // 任务已被办理人签收不可撤回
                retMsg.setCode(1);
                retMsg.setMessage("任务已被办理人签收不可撤回");
                return retMsg;
            }

            // 通过跳转进行退回
            actActivitiService.jump2Task3(targetTaskKey, processInstanceId, orgnlTask.getExecutionId());
            // 跳转后获取当前活动节点
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .executionId(orgnlTask.getExecutionId()).taskDefinitionKey(targetTaskKey).singleResult();
            // 签收
            taskService.claim(task.getId(), targetUserId);
        } else {
            retMsg.setCode(1);
            retMsg.setMessage("上一节点为空或包含多个任务节点不能撤回");
            return retMsg;
        }

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     *
     * 根据流程实例ID查看流程图
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public void showImgfromproInsId(HttpServletRequest request, HttpServletResponse response, String processInstanceId)
        throws Exception {
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addDateHeader("expires", 0);
        InputStream is = actActivitiService.getRuImageInputStream(processInstanceId, true);
        if (is == null) {
            is = actActivitiService.getHisImageInputStream(processInstanceId, "");
        }
        OutputStream os = response.getOutputStream();
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "PNG", os);
        os.flush();
        os.close();
    }

    /**
     *
     * @描述: 所有固定模块调用的回退时显示上一环节办理人
     *
     *      @return： RetMsg
     *
     *      @author： sunlinan
     *
     * @date 2017年12月20日
     */
    public RetMsg getPreTaskInfo2(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 获取当前任务
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        // 流程实例id
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();

        // 上一级任务节点
        List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);

        if (preList == null || preList.isEmpty()) {
            retMsg.setCode(1);
            retMsg.setMessage("无上级节点，不可回退");
            return retMsg;
        }
        if (preList.size() > 1) {
            retMsg.setCode(1);
            retMsg.setMessage("当前节点不可回退");
            return retMsg;
        }
        TaskDefinition preTask = preList.get(0);
        String targetTaskKey = preTask.getKey();
        // 用taskKey去历史表中查出该task的最后一次办理时间以及最后办理人，就是要回退的环节
        List<HistoricTaskInstance> historicList =
            historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .taskDefinitionKey(targetTaskKey).finished().orderByHistoricTaskInstanceEndTime().desc().list();
        List<CustomerTaskDefinition> defitionList = new ArrayList<CustomerTaskDefinition>();
        if (historicList.isEmpty()) {
            retMsg.setCode(1);
            retMsg.setMessage("历史节点获取失败");
            return retMsg;
        }
        HistoricTaskInstance historic = historicList.get(0);
        // for (HistoricTaskInstance historic : historicList) {
        if (null != historic.getAssignee()) {
            CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
            taskDefinition
                .setKey(StringUtils.isNotEmpty(historic.getTaskDefinitionKey()) ? historic.getTaskDefinitionKey() : "");
            taskDefinition.setNextNodeName(historic.getName());
            taskDefinition.setAssignee(String.valueOf(historic.getAssignee()));
            defitionList.add(taskDefinition);
        }
        // }
        retMsg.setObject(defitionList);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     *
     * @描述: 流程结束时，更新query表，记录最后一个办理人
     *
     *      @return： void
     *
     *      @author： sunlinan
     *
     * @date 2017年12月22日
     */
    public void updateLastAsignee(String processInstanceId, String fullName) throws Exception {
        Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
        actAljoinQueryWhere.eq("process_instance_id", processInstanceId);
        ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
        if (query != null) {
            Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
            actAljoinQueryHisWhere.eq("process_instance_id", processInstanceId);
            ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);
            query.setCurrentHandleFullUserName(fullName);
            queryHis.setCurrentHandleFullUserName(fullName);
            actAljoinQueryService.updateById(query);
            actAljoinQueryHisService.updateById(queryHis);
        }
    }

    /**
     *
     * App 固定表单流程获取下一个任务节点信息
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-12-22
     */
    public RetMsg getAppNextTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
        List<CustomerTaskDefinition> taskDefinitionList = new ArrayList<CustomerTaskDefinition>();
        if (null != list && !list.isEmpty()) {
            for (TaskDefinition definition : list) {
                if (null != definition) {
                    CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
                    taskDefinition.setKey(definition.getKey());
                    taskDefinition.setAssignee(String.valueOf(definition.getAssigneeExpression()));
                    taskDefinition.setNextNodeName(String.valueOf(definition.getNameExpression()));
                    taskDefinitionList.add(taskDefinition);
                }
            }
        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setObject(taskDefinitionList);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    public List<FixedFormProcessLog> getActivityLog(String taskId, String processInstanceId) throws Exception {
        if (StringUtils.isNotEmpty(taskId) && StringUtils.isEmpty(processInstanceId)) {
            HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
            processInstanceId = task.getProcessInstanceId();
        }
        Where<ActAljoinActivityLog> logWhere = new Where<ActAljoinActivityLog>();
        logWhere.setSqlSelect(
            "id,operate_user_id,operate_full_name,operate_time,operate_status,receive_user_ids,receive_full_names,comment,last_task_name,last_task_id,last_task_def_key,current_task_name,current_task_def_key,current_task_id,next_task_def_key,next_task_name,proc_inst_id");
        logWhere.eq("proc_inst_id", processInstanceId);
        logWhere.orderBy("operate_time", true);
        List<ActAljoinActivityLog> actAljoinActivityLogList = actAljoinActivityLogService.selectList(logWhere);
        List<FixedFormProcessLog> logList = new ArrayList<FixedFormProcessLog>();
        int i = 0;
        for (ActAljoinActivityLog actAljoinActivityLog : actAljoinActivityLogList) {
            i++;
            if ((i == 1 && actAljoinActivityLogList.size() > 1)) {
                continue;
            }
            FixedFormProcessLog fixedFormProcessLog = new FixedFormProcessLog();
            fixedFormProcessLog.setComment(actAljoinActivityLog.getComment());
            fixedFormProcessLog.setOperationId(String.valueOf(actAljoinActivityLog.getOperateUserId()));
            fixedFormProcessLog.setOperationName(actAljoinActivityLog.getOperateFullName());
            fixedFormProcessLog.setProcessInstanceId(processInstanceId);
            fixedFormProcessLog.setTaskId(actAljoinActivityLog.getCurrentTaskId());
            if (actAljoinActivityLogList.size() == 1
                || actAljoinActivityLog.getLastTaskDefKey().equals(actAljoinActivityLog.getCurrentTaskDefKey())
                    && actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)) {
                fixedFormProcessLog.setRecevieUserName("");
            } else {
                fixedFormProcessLog.setRecevieUserName(actAljoinActivityLog.getReceiveFullNames());
            }
            fixedFormProcessLog.setOperationTime(actAljoinActivityLog.getOperateTime());
            String operateStatus = "";
            if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_SUB;
            } else if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_2)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_RET;
            } else if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_3)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_REV;
            } else if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_4)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_SIG;
            } else if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_5)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_DIS;
            } else if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_6)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_CIR;
            } else if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_7)) {
                operateStatus = WebConstant.PROCESS_OPERATE_STATUS_DEV;
            }

            String direction = "";
            if (actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_5)
                || actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_6)) {
                direction = operateStatus;
            } else {
                if (StringUtils.isNotEmpty(actAljoinActivityLog.getLastTaskName())) {
                    if (StringUtils.isNotEmpty(actAljoinActivityLog.getCurrentTaskName())) {
                        direction = actAljoinActivityLog.getLastTaskName() + " --> " + operateStatus + " --> "
                            + actAljoinActivityLog.getCurrentTaskName();
                    } else {
                        direction = actAljoinActivityLog.getLastTaskName();
                    }
                } else {
                    if (StringUtils.isNotEmpty(actAljoinActivityLog.getCurrentTaskName())) {
                        direction = actAljoinActivityLog.getCurrentTaskName();
                    }
                }
            }
            fixedFormProcessLog.setDirection(direction);
            logList.add(fixedFormProcessLog);
        }
        return logList;
    }

    /**
     *
     * 检查当前任务节点是否被签收
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public String appIsClaim(String taskId, String processInstanceId, String userId) throws Exception {
        String message = "0";
        // 待办-当前环节是否已签收
        if (StringUtils.isNotEmpty(taskId) && StringUtils.isNotEmpty(userId)) {
            Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != task) {
                HistoricTaskInstance hisTask =
                    historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                // 已签收
                if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())
                    && hisTask.getAssignee().equals(userId)) {
                    message = "1";
                    return message;
                    // 未签收
                } else if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
                    processInstanceId = task.getProcessInstanceId();
                    MultiInstanceLoopCharacteristics isMult =
                        activitiService.getMultiInstance(processInstanceId, task.getTaskDefinitionKey());
                    // 多任务节点
                    if (isMult != null) {
                        message = "1";
                        return message;
                    }
                    // 被签收但不是当前用户签收
                } else {
                    message = "1";
                    return message;
                }
            }
        }

        // 在办-是否可撤回
        if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isNotEmpty(userId)) {
            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceEndTime().desc().list();
            if (!activities.isEmpty()) {
                // 非正常用户任务节点不允许撤回
                if (!activities.get(0).getActivityType().equals("userTask")) {
                    message = "1";
                    return message;
                }
                // 当前环节id
                String thistaskId = activities.get(0).getTaskId();
                String pretaskId = "";
                String tmpActivityType = activities.get(1).getActivityType();

                if (tmpActivityType != null && tmpActivityType.indexOf("ExclusiveGateway") > -1) {
                    // 排他网关不允许撤回
                    message = "1";
                    return message;
                } else if (tmpActivityType.indexOf("userTask") > -1) {
                    // 上一环节id
                    pretaskId = activities.get(1).getTaskId();
                } else {
                    message = "1";
                    return message;
                }
                // 当前环节是否被签收
                List<Task> curtasks = taskService.createTaskQuery().taskId(thistaskId).list();
                List<TaskDefinition> preList = null;
                if (curtasks.size() == 1) {
                    if (StringUtils.isNotEmpty(curtasks.get(0).getAssignee())) {
                        // 当前任务已被办理人签收不可撤回
                        message = "1";
                        return message;
                    }

                    MultiInstanceLoopCharacteristics isMult =
                        activitiService.getMultiInstance(processInstanceId, curtasks.get(0).getTaskDefinitionKey());
                    if (isMult != null) {
                        // 当前节点为多任务节点
                        message = "1";
                        return message;
                    }

                    preList = activitiService.getPreTaskInfo(curtasks.get(0).getTaskDefinitionKey(), processInstanceId);
                    if (preList.size() != 1) {
                        // 上一节点为多任务节点，不可撤回 或 当前节点没有上一环节，不能撤回
                        message = "1";
                        return message;
                    }

                    Where<ActAljoinActivityLog> log = new Where<ActAljoinActivityLog>();
                    log.eq("current_task_id", curtasks.get(0).getId());
                    log.eq("operate_status", WebConstant.PROCESS_OPERATE_STATUS_2);
                    ActAljoinActivityLog actLog = actAljoinActivityLogService.selectOne(log);
                    if (actLog != null) {
                        // 已退回的任务不可以撤回
                        message = "1";
                        return message;
                    }

                    HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(processInstanceId).taskId(pretaskId).taskAssignee(userId).singleResult();
                    if (null == histask) {
                        // 当前用户不是上一环节办理人
                        message = "1";
                        return message;
                    }

                    HistoricTaskInstance currentTaskCandidate = historyService.createHistoricTaskInstanceQuery()
                        .taskId(thistaskId).taskCandidateUser(userId).singleResult();
                    if (null != currentTaskCandidate) {
                        // 当前用户是当前任务的候选人
                        message = "1";
                        return message;
                    }
                } else {
                    List<HistoricActivityInstance> activitiess = historyService.createHistoricActivityInstanceQuery()
                        .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc()
                        .orderByHistoricActivityInstanceEndTime().desc().list();
                    if (!activitiess.isEmpty()) {
                        if (!activitiess.get(0).getActivityType().equals("userTask")) {
                            message = "1";
                            return message;
                        }
                    }

                    HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(processInstanceId).taskId(pretaskId).taskAssignee(userId).singleResult();

                    if (null == histask) {
                        // 当前用户不是上一环节办理人
                        message = "1";
                        return message;
                    } else {
                        // 可以撤回
                        message = "0";
                        return message;
                    }
                }
            }
        }
        return message;
    }
}
