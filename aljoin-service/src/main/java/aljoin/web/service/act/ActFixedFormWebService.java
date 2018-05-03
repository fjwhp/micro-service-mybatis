package aljoin.web.service.act;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinActivityLogService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;

@Service
public class ActFixedFormWebService {
    @Resource
    private TaskService taskService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private ActFixedFormServiceImpl actFixedFormService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private ActActivitiWebService actActivitiWebService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;

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
    @Transactional
    public RetMsg jump2Task2(String processInstanceId, Long createUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        Map<String,Object> logMap = new HashMap<String,Object>();
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc().list();
        logMap.put("processInstanceId", processInstanceId);
        if (!activities.isEmpty()) {
            if (!activities.get(0).getActivityType().equals("userTask")) {
                retMsg.setCode(1);// 排他网关不允许撤回
                retMsg.setMessage("当前任务节点不满足撤回条件");
                return retMsg;
            }
            String thistaskId = activities.get(0).getTaskId(); // 当前环节id
            Task pTask = taskService.createTaskQuery().taskId(thistaskId).singleResult();
            logMap.put("preTask", pTask);
            String pretaskId = activities.get(1).getTaskId(); // 上一环节id
            HistoricTaskInstance hisTask =
                historyService.createHistoricTaskInstanceQuery().taskId(thistaskId).singleResult();
            if (null != hisTask.getClaimTime() && !StringUtils.isEmpty(hisTask.getAssignee())) {
                // 任务已被办理人签收不可撤回
                retMsg.setCode(1);
                retMsg.setMessage("任务已被办理人签收不可撤回");
                return retMsg;
            }
            HistoricTaskInstance histask =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskId(pretaskId)
                    .taskAssignee(String.valueOf(createUserId)).singleResult();
            if (null == histask) {
                // 当前用户不是上一环节办理人
                retMsg.setCode(1);
                retMsg.setMessage("当前用户不是上一环节办理人，不可撤回");
                return retMsg;
            }

            List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(thistaskId);
            String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
            List<String> assignees = new ArrayList<String>();
            if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isNotEmpty(currentTaskKey)) {
                List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
                if (preList.size() > 0) {
                    // 历史上级节点
                    HistoricTaskInstance preTask =
                        historyService.createHistoricTaskInstanceQuery().taskId(pretaskId).singleResult();
                    String hispreTaskDefKey = preTask.getTaskDefinitionKey();
                    TaskDefinition taskDefinition = preList.get(0);
                    String targetTaskKey = taskDefinition.getKey();
                    if (!hispreTaskDefKey.equals(targetTaskKey)) {
                        // 查找到的上一环节不是历史中的上一环节，说明该文件不是正常正向流转的，不能撤回
                        retMsg.setCode(1);
                        retMsg.setMessage("已退回的任务不可以撤回");
                        return retMsg;
                    }
                    if (!StringUtils.isEmpty(targetTaskKey)) {
                        // 填写意见
                        Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
                        taskService.addComment(thistaskId, processInstanceId, "注：由操作人撤回");
                        logMap.put("commont", "注：由操作人撤回");
                        actActivitiService.jump2Task2(targetTaskKey, processInstanceId);
                        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                            .taskDefinitionKey(targetTaskKey).singleResult();
                        assignees.add(preTask.getAssignee());
                        // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                        actFixedFormService.deleteOrgnlTaskAuth(task);
                        HistoricTaskInstance hisTsk =
                            historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                        if (null != hisTsk.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                            taskService.unclaim(task.getId());
                        }
                        if (assignees.size() == 1) {
                            if (!assignees.get(0).isEmpty()) {
                                taskService.addUserIdentityLink(task.getId(), assignees.get(0), "candidate");
                                taskService.claim(task.getId(), assignees.get(0));
                                String userFullName = autUserService.selectById(assignees.get(0)).getFullName();
                                actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                            }
                        } else {
                            Set<Long> assigneeIds = new HashSet<Long>();
                            for (String assignee : assignees) {
                                if (!assignee.isEmpty()) {
                                    assigneeIds.add(Long.valueOf(assignee));
                                    taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
                                }
                            }
                            if (assigneeIds.size() > 0) {
                                Where<AutUser> userwhere = new Where<AutUser>();
                                userwhere.in("id", assigneeIds);
                                userwhere.setSqlSelect("id,full_name");
                                List<AutUser> assigneeList = autUserService.selectList(userwhere);
                                String fullName = "";
                                if (assigneeList.size() > 0) {
                                    for (AutUser autUser : assigneeList) {
                                        fullName += autUser.getFullName() + ";";
                                    }
                                    // 当task对应的候选人不止一个时，所有候选人都是当前环节办理人
                                    actAljoinQueryService.updateCurrentUserName(task.getId(), fullName);
                                }
                            }
                        }
                        // actAljoinQueryService.cleanQureyCurrentUser(task.getId());
                        // 记录流程日志
                        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                        taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
                        logMap.put("taskKeyUserMap", taskKeyMap);
                        logMap.put("userTask", task);
                        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_3);
//                        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,
//                            WebConstant.PROCESS_OPERATE_STATUS_3, createUserId, null);
                        actActivitiWebService.insertOrUpdateLog(logMap, createUserId);
                    }
                }

            }

        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
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
    @Transactional
    public RetMsg jump2AppTask2(String processInstanceId, Long createUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        Map<String,Object> logMap = new HashMap<String,Object>();
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc().list();
        if (!activities.isEmpty()) {
            if (!activities.get(0).getActivityType().equals("userTask")) {
                retMsg.setCode(1);// 排他网关不允许撤回
                retMsg.setMessage("当前任务节点不满足撤回条件");
                return retMsg;
            }
            String thistaskId = activities.get(0).getTaskId(); // 当前环节id
            Task pTask = taskService.createTaskQuery().taskId(thistaskId).singleResult();
            logMap.put("processInstanceId", processInstanceId);
            logMap.put("preTask", pTask);
            String pretaskId = activities.get(1).getTaskId(); // 上一环节id
            HistoricTaskInstance hisTsk =
                historyService.createHistoricTaskInstanceQuery().taskId(thistaskId).singleResult();
            if (null != hisTsk.getClaimTime() && StringUtils.isNotEmpty(hisTsk.getAssignee())) {
                // 任务已被办理人签收不可撤回
                retMsg.setCode(1);
                retMsg.setMessage("任务已被办理人签收不可撤回");
                return retMsg;
            }
            HistoricTaskInstance histask =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).taskId(pretaskId)
                    .taskAssignee(String.valueOf(createUserId)).singleResult();
            if (null == histask) {
                // 当前用户不是上一环节办理人
                retMsg.setCode(1);
                retMsg.setMessage("当前用户不是上一环节办理人，不可撤回");
                return retMsg;
            }

            List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(thistaskId);
            String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
            List<String> assignees = new ArrayList<String>();
            if (StringUtils.isNotEmpty(processInstanceId) && StringUtils.isNotEmpty(currentTaskKey)) {
                List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
                if (preList.size() > 0) {
                    // 历史上级节点
                    HistoricTaskInstance preTask =
                        historyService.createHistoricTaskInstanceQuery().taskId(pretaskId).singleResult();
                    String hispreTaskDefKey = preTask.getTaskDefinitionKey();
                    TaskDefinition taskDefinition = preList.get(0);
                    String targetTaskKey = taskDefinition.getKey();
                    if (!hispreTaskDefKey.equals(targetTaskKey)) {
                        // 查找到的上一环节不是历史中的上一环节，说明该文件不是正常正向流转的，不能撤回
                        retMsg.setCode(1);
                        retMsg.setMessage("已退回的任务不可以撤回");
                        return retMsg;
                    }
                    if (!StringUtils.isEmpty(targetTaskKey)) {
                        // 填写意见
                        Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
                        taskService.addComment(thistaskId, processInstanceId, "注：由操作人撤回");
                        logMap.put("commont", "注：由操作人撤回");
                        actActivitiService.jump2Task2(targetTaskKey, processInstanceId);
                        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                            .taskDefinitionKey(targetTaskKey).singleResult();

                        assignees.add(preTask.getAssignee());
                        // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                        actFixedFormService.deleteOrgnlTaskAuth(task);
                        HistoricTaskInstance hisTask =
                            historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                        if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                            taskService.unclaim(task.getId());
                        }
                        if (assignees.size() == 1) {
                            if (!assignees.get(0).isEmpty()) {
                                taskService.addUserIdentityLink(task.getId(), assignees.get(0), "candidate");
                                taskService.claim(task.getId(), assignees.get(0));
                                String userFullName = autUserService.selectById(assignees.get(0)).getFullName();
                                actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                            }
                        } else {
                            Set<Long> assigneeIds = new HashSet<Long>();
                            for (String assignee : assignees) {
                                if (!assignee.isEmpty()) {
                                    assigneeIds.add(Long.valueOf(assignee));
                                    taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
                                }
                            }
                            if (assigneeIds.size() > 0) {
                                Where<AutUser> userwhere = new Where<AutUser>();
                                userwhere.in("id", assigneeIds);
                                userwhere.setSqlSelect("id,full_name");
                                List<AutUser> assigneeList = autUserService.selectList(userwhere);
                                String fullName = "";
                                if (assigneeList.size() > 0) {
                                    for (AutUser autUser : assigneeList) {
                                        fullName += autUser.getFullName() + ";";
                                    }
                                    // 当task对应的候选人不止一个时，所有候选人都是当前环节办理人
                                    actAljoinQueryService.updateCurrentUserName(task.getId(), fullName);
                                }
                            }
                        }
                        // actAljoinQueryService.cleanQureyCurrentUser(task.getId());
                        // 记录流程日志
                        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                        taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
                        logMap.put("taskKeyUserMap", taskKeyMap);
                        logMap.put("userTask", task);
                        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_3);
                        actActivitiWebService.insertOrUpdateLog(logMap, createUserId);
//                        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,
//                            WebConstant.PROCESS_OPERATE_STATUS_3, createUserId, null);

                    }
                }

            }

        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        return retMsg;
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
        retMsg.setCode(1);
        if(StringUtils.isEmpty(taskId)){
            retMsg.setMessage("当前任务id不可为空");
            return retMsg;
        }
        
        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
            retMsg.setMessage("当前任务已被签收，不能撤回");
            return retMsg;
        } 
        // 判断是否可撤回
        Task orgnlTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null == orgnlTask){
            retMsg.setMessage("当前节点已经改变，不能撤回");
            return retMsg;
        }
        if(StringUtils.isEmpty(processInstanceId)){
            processInstanceId = orgnlTask.getId();
        }
        MultiInstanceLoopCharacteristics isMult = activitiService.getMultiInstance(processInstanceId, orgnlTask.getTaskDefinitionKey());
        if (isMult != null) {
            retMsg.setMessage("多实例节点不允许撤回");
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
            retMsg.setMessage("找不到可撤回的目标节点");
            return retMsg;
        }
        String currentTaskKey = orgnlTask.getTaskDefinitionKey();
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
                            && !activityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_1)) {
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
            retMsg.setMessage("找不到可撤回的目标节点");
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
        if (!lastExcutionId.equals(orgnlTask.getExecutionId())) {
            retMsg.setMessage("不在一条执行流上不能撤回");
            return retMsg;
        }
        String activityKey = "";
        String assigneeUserId = "";
        List<HistoricTaskInstance> hisTaskList =
            historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                .taskId(lastLog.getLastTaskId()).finished().orderByHistoricTaskInstanceEndTime().desc().list();
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(orgnlTask.getProcessInstanceId()).finished().list();
        for (HistoricTaskInstance historicTaskInstance : hisTaskList) {
            for (HistoricActivityInstance activity : activities) {
                if (activity.getTaskId() != null && activity.getTaskId().equals(historicTaskInstance.getId())) {
                    MultiInstanceLoopCharacteristics preMulti =
                        actActivitiService.getMultiInstance(orgnlTask.getProcessInstanceId(), activity.getActivityId());
                    if (preMulti != null) {
                        throw new Exception("不允许撤回到多实例(会签)节点");
                    }
                    activityKey = activity.getActivityId();
                    assigneeUserId = activity.getAssignee();    
                    break;
                }
            }
        }
        if(!assigneeUserId.equals(targetUserId)){
            retMsg.setMessage("不是上一环节办理人，无权撤回");
            return retMsg;
        }

        // 到这里，所有判断都通过了，就可以做撤回动作了
        Authentication.setAuthenticatedUserId(targetUserId);
        Map<String, Object> logMap = new HashMap<String, Object>();
        logMap.put("preTask", orgnlTask);
        taskService.addComment(orgnlTask.getId(), processInstanceId, "注：由操作人撤回");
        
        actActivitiService.jump2Task3(activityKey, processInstanceId, orgnlTask.getExecutionId());
        // 跳转后获取当前活动节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
            .executionId(orgnlTask.getExecutionId()).taskDefinitionKey(activityKey).singleResult();
        // 签收
        taskService.claim(task.getId(), targetUserId);

        // 撤回操作后需要修改查询表的当前班里人
        AutUser autUser = autUserService.selectById(targetUserId);

        Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
        queryWhere.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery query = actAljoinQueryService.selectOne(queryWhere);
        query.setCurrentHandleFullUserName(autUser.getFullName());
        actAljoinQueryService.updateById(query);

        Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
        queryHisWhere.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(queryHisWhere);
        queryHis.setCurrentHandleFullUserName(autUser.getFullName());
        actAljoinQueryHisService.updateById(queryHis);

        
        // 记录流程日志
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_3,
//            Long.valueOf(targetUserId), null);
        List<String> userIdList = new ArrayList<String>();
        userIdList.add(targetUserId);
        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
        taskKeyMap.put(task.getTaskDefinitionKey(), userIdList);
        logMap.put("taskKeyUserMap", taskKeyMap);
        logMap.put("userTask", task);
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_3);
        logMap.put("processInstanceId", processInstanceId);
        actActivitiWebService.insertOrUpdateLog(logMap, Long.valueOf(targetUserId));
    
        
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @SuppressWarnings("unchecked")
    public List<FixedFormProcessLog> getFixedLog(String taskId, String processInstanceId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (com.baomidou.mybatisplus.toolkit.StringUtils.isEmpty(processInstanceId)) {
            map = actFixedFormService.getLog(taskId, null);
        } else {
            map = actFixedFormService.getLog(taskId, processInstanceId);
        }

        List<FixedFormProcessLog> logList = null;
        if (null != map && !map.isEmpty()) {
            List<AutUser> assigneeList = new ArrayList<AutUser>();
            List<AutUser> recevieList = new ArrayList<AutUser>();
            if (null != map.get("assigneeIdList")) {
                List<Long> assigneeIdList = (List<Long>)map.get("assigneeIdList");
                if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
                    Where<AutUser> assigneeWhere = new Where<AutUser>();
                    assigneeWhere.in("id", new HashSet<Long>(assigneeIdList));
                    assigneeWhere.setSqlSelect("id,user_name,full_name");
                    assigneeList = autUserService.selectList(assigneeWhere);
                }
            }
            if (null != map.get("recevieUserIdList")) {
                List<Long> recevieUserIdList = (List<Long>)map.get("recevieUserIdList");
                if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
                    Where<AutUser> recevieWhere = new Where<AutUser>();
                    recevieWhere.in("id", recevieUserIdList);
                    recevieWhere.setSqlSelect("id,user_name,full_name");
                    recevieList = autUserService.selectList(recevieWhere);
                }
            }

            if (null != map.get("logList")) {
                logList = (List<FixedFormProcessLog>)map.get("logList");
                if (null != logList && !logList.isEmpty() && null != assigneeList && !assigneeList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        for (AutUser user : assigneeList) {
                            if (null != user && null != user.getId() && null != log && null != log.getOperationId()) {
                                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                                    log.setOperationName(user.getFullName());
                                }
                            }
                        }
                    }
                }
                if (null != logList && !logList.isEmpty() && null != recevieList && !recevieList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        String recUserName = "";
                        for (AutUser user : recevieList) {
                            if (null != user && null != user.getId() && null != log && null != log.getRecevieUserId()) {
                                if (log.getRecevieUserId().contains(String.valueOf(user.getId()))
                                    || log.getRecevieUserId().equals(String.valueOf(user.getId()))) {
                                    recUserName += user.getFullName() + ";";

                                }
                            }
                        }
                        log.setRecevieUserName(recUserName);
                    }
                }
            }
        }
        return logList;
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
     * app端调用自定义流程-撤回
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-07
     */
    public RetMsg appRevoke(String processInstanceId, String taskId, String targetUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (taskList != null && taskList.size() > 1) {
            retMsg.setCode(1);
            retMsg.setMessage("任务流转多个环节，不可撤回！");
            return retMsg;
        }
        Task orgnlTask = null;
        ActAljoinActivityLog actLogs = null;
        if (taskList.size() == 1) {
            // 获取当前流程实例ID
            orgnlTask = taskList.get(0);
            Where<ActAljoinActivityLog> logs = new Where<ActAljoinActivityLog>();
            logs.eq("current_task_id", orgnlTask.getId());
            actLogs = actAljoinActivityLogService.selectOne(logs);
            if (StringUtils.isEmpty(processInstanceId) && null != orgnlTask) {
                processInstanceId = orgnlTask.getProcessInstanceId();
            }

            MultiInstanceLoopCharacteristics isMult =
                activitiService.getMultiInstance(processInstanceId, orgnlTask.getTaskDefinitionKey());
            if (isMult != null) {
                retMsg.setCode(AppConstant.RET_CODE_ERROR);
                retMsg.setMessage("该环节为为多人办理不能撤回");
                return retMsg;
            }

            List<TaskDefinition> preList =
                activitiService.getPreTaskInfo2(orgnlTask.getTaskDefinitionKey(), processInstanceId);
            if (null != preList && preList.isEmpty()) {
                retMsg.setCode(AppConstant.RET_CODE_ERROR);
                retMsg.setMessage("上一节点为空或包含多个任务节点不能撤回");
                return retMsg;
            }
            if (null != preList && preList.size() > 1) {
                retMsg.setCode(AppConstant.RET_CODE_ERROR);
                retMsg.setMessage("上一环节不可撤回!!");
                return retMsg;
            }

            List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc().list();

            if ((null != preList && preList.size() == 1)) {
                String targetTaskKey = preList.get(0).getKey();
                Authentication.setAuthenticatedUserId(targetUserId);

                if (actLogs != null && actLogs.getOperateStatus() != null && actLogs.getOperateStatus() == 2) {
                    retMsg.setCode(AppConstant.RET_CODE_ERROR);
                    retMsg.setMessage("已退回的任务不可以撤回");
                    return retMsg;
                }
                isMult = activitiService.getMultiInstance(processInstanceId, targetTaskKey);
                if (isMult != null) {
                    retMsg.setCode(AppConstant.RET_CODE_ERROR);
                    retMsg.setMessage("上一环节为为多人办理不能撤回");
                    return retMsg;
                }
                List<HistoricActivityInstance> activitiess = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstanceId).orderByHistoricActivityInstanceStartTime().desc()
                    .orderByHistoricActivityInstanceEndTime().desc().list();
                if (!activitiess.isEmpty()) {
                    if (!activitiess.get(0).getActivityType().equals("userTask")) {
                        retMsg.setCode(AppConstant.RET_CODE_ERROR);
                        retMsg.setMessage("当前任务环节不能撤回！！");
                        return retMsg;
                    }
                }
                HistoricTaskInstance hisTask =
                    historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                if (null != hisTask.getClaimTime() && StringUtils.isNotEmpty(hisTask.getAssignee())) {
                    // 任务已被办理人签收不可撤回
                    retMsg.setCode(AppConstant.RET_CODE_ERROR);
                    retMsg.setMessage("任务已被办理人签收不可撤回");
                    return retMsg;
                }

                if (actLogs != null) {
                    if (actLogs.getLastTaskDefKey() != null) {
                        targetTaskKey = actLogs.getLastTaskDefKey();
                    }
                }
                Map<String, Object> logMap = new HashMap<String, Object>();
                logMap.put("preTask", orgnlTask);
                if (activities.size() > 0) {
                    taskService.addComment(activities.get(0).getTaskId(), processInstanceId, "注：由操作人撤回");
                    logMap.put("commont", "注：由操作人撤回");
                }
                actActivitiService.jump2Task3(targetTaskKey, processInstanceId, orgnlTask.getExecutionId());
                // 跳转后获取当前活动节点
                Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                    .executionId(orgnlTask.getExecutionId()).taskDefinitionKey(targetTaskKey).singleResult();
                List<String> userIdList = new ArrayList<String>();
                // 撤回操作后需要修改查询表的当前班里人
                HashSet<Long> userIdSet = new HashSet<Long>();
                userIdSet.add(Long.valueOf(targetUserId));
                userIdList.add(targetUserId);
                Where<AutUser> uwhere = new Where<AutUser>();
                uwhere.setSqlSelect("id,full_name");
                uwhere.in("id", userIdSet);
                List<AutUser> userList = autUserService.selectList(uwhere);
                String userName = "";
                for (AutUser autUser : userList) {
                    userName += autUser.getFullName() + ";";
                }
                actAljoinQueryService.updateCurrentUserName(task.getId(), userName);

                // 签收
                taskService.claim(task.getId(), targetUserId);

                Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                taskKeyMap.put(task.getTaskDefinitionKey(), userIdList);
                logMap.put("processInstanceId", processInstanceId);
                logMap.put("taskKeyUserMap", taskKeyMap);
                logMap.put("userTask", task);
                logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_3);
                // 记录流程日志
                actActivitiWebService.insertOrUpdateLog(logMap, Long.valueOf(targetUserId));
                // actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,
                // WebConstant.PROCESS_OPERATE_STATUS_3,
                // Long.valueOf(targetUserId), null);
            } else {
                retMsg.setCode(AppConstant.RET_CODE_ERROR);
                retMsg.setMessage("上一节点为空或包含多个任务节点不能撤回");
                return retMsg;
            }
        }

        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
