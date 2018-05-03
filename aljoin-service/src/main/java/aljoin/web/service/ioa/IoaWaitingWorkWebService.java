package aljoin.web.service.ioa;

import aljoin.act.dao.entity.*;
import aljoin.act.dao.object.SimpleDeptVO;
import aljoin.act.dao.object.SimpleUserVO;
import aljoin.act.iservice.*;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.ioa.iservice.IoaWaitingWorkService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;
import aljoin.web.service.act.ActActivitiWebService;
import aljoin.web.service.act.ActFixedFormWebService;
import com.alibaba.druid.util.StringUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 待办工作(服务实现类).
 * 
 * @author：pengsp @date： 2017-10-19
 */
@Service
public class IoaWaitingWorkWebService {

    @Resource
    private ActActivitiWebService actActivitiWebService;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private ActFixedFormWebService actFixedFormWebService;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private IoaWaitingWorkService ioaWaitingWorkService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;
    @Resource
    private AutDepartmentService autDepartmentService;

    @Transactional
    public RetMsg jumpTask(String taskId, String targetTaskKey, String targetUserId, String thisTaskUserComment,
        CustomUser cuser, Map<String, String> paramMap, ActAljoinFormDataRun entity, String isTask, String nextNode)
        throws Exception {
        RetMsg retMsg = new RetMsg();
        // 获取当前流程实例ID
        Task orgnlTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String,Object> logMap = new HashMap<String,Object>();  
        String processInstanceId = orgnlTask.getProcessInstanceId();
        ProcessInstance instance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
        // 获取任务的授权数据
        Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        taskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
        taskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
        taskAssigneeWhere.eq("task_id", orgnlTask.getTaskDefinitionKey());
        taskAssigneeWhere.setSqlSelect("comment_widget_ids");
        ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

        // 填写意见
        if (StringUtils.isEmpty(thisTaskUserComment)) {
            thisTaskUserComment = "回退";
        }
        logMap.put("commont", thisTaskUserComment);
        Authentication.setAuthenticatedUserId(cuser.getUserId().toString());
        Comment thisTaskUserCommentObj = taskService.addComment(taskId, processInstanceId, thisTaskUserComment);
        thisTaskUserComment = thisTaskUserComment + "(" + cuser.getNickName()
            + DateUtil.datetime2str(thisTaskUserCommentObj.getTime()) + ")";
        // 填写完意见，插入表单
        // 把评论数据保存到对应的评论控件中
        for (Map.Entry<String, String> map : paramMap.entrySet()) {
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            if (!(StringUtils.isEmpty(actAljoinTaskAssignee.getCommentWidgetIds()))
                && (actAljoinTaskAssignee.getCommentWidgetIds().indexOf(formWidgetId) != -1)
                && (!StringUtils.isEmpty(thisTaskUserComment))) {
                if (!StringUtils.isEmpty(formWidgetVal)) {
                    paramMap.put(formWidgetId, formWidgetVal + "\n" + thisTaskUserComment);
                } else {
                    paramMap.put(formWidgetId, thisTaskUserComment);
                }
            }
        }

        actAljoinFormDataDraftService.updateFormData(paramMap, instance, entity, isTask, nextNode, null);

        // 通过跳转进行退回
        actActivitiService.jump2Task3(targetTaskKey, processInstanceId, orgnlTask.getExecutionId());

        // 跳转后获取当前活动节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
            .executionId(orgnlTask.getExecutionId()).taskDefinitionKey(targetTaskKey).singleResult();

        HistoricTaskInstance hisTask =
            historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
        if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
            // 取消签收
            taskService.unclaim(task.getId());
        }

        List<String> assignees = new ArrayList<String>();
        List<HistoricTaskInstance> historicList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey).finished().list();
        for (HistoricTaskInstance historic : historicList) {
            assignees.add(historic.getAssignee());
        }

        // 设置候选
        taskService.addUserIdentityLink(task.getId(), targetUserId, "candidate");

        // 更新查询表
        Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
        actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
        ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);

        Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
        actAljoinQueryHisWhere.eq("process_instance_id", instance.getProcessInstanceId());
        ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);

        if (query != null && queryHis != null) {
            AutUser autUser = autUserService.selectById(targetUserId);
            if (autUser != null) {
                query.setCurrentHandleFullUserName(autUser.getFullName());
                queryHis.setCurrentHandleFullUserName(autUser.getFullName());
                actAljoinQueryService.updateById(query);
                actAljoinQueryHisService.updateById(queryHis);
            }

        }
        logMap.put("processInstanceId", processInstanceId);
        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
        List<String> userIdList = new ArrayList<String>();
        userIdList.add(targetUserId);
        taskKeyMap.put(task.getTaskDefinitionKey(), userIdList);
        logMap.put("taskKeyUserMap", taskKeyMap);
        logMap.put("userTask", task);
        logMap.put("preTask", orgnlTask);
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
        actActivitiWebService.insertOrUpdateLog(logMap, cuser.getUserId());
        // 记录流程日志
//        actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(),
//            WebConstant.PROCESS_OPERATE_STATUS_2, cuser.getUserId(), null);
        retMsg.setObject(instance.getId());
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    public RetMsg jumpAppTask(String taskId, String targetTaskKey, String targetUserId, String comment, Long userId,
        Map<String, String> paramMap, ActAljoinFormDataRun entity, String isTask, String nextTaskKey) throws Exception {
        RetMsg retMsg = new RetMsg();

        // 获取当前流程实例ID
        Task orgnlTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = orgnlTask.getProcessInstanceId();
        ProcessInstance instance =
            runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        Map<String, Object> logMap = new HashMap<String, Object>();
        logMap.put("instance", instance);
        ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
        // 获取任务的授权数据
        Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        taskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
        taskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
        taskAssigneeWhere.eq("task_id", orgnlTask.getTaskDefinitionKey());
        taskAssigneeWhere.setSqlSelect("comment_widget_ids");
        ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

        // 填写意见
        if (StringUtils.isEmpty(comment)) {
            comment = "回退";
        }
        logMap.put("commont", comment);
        Where<AutUser> autUserWhere = new Where<AutUser>();
        autUserWhere.setSqlSelect("id,user_name,full_name");
        autUserWhere.eq("id", userId);
        AutUser autUser = autUserService.selectOne(autUserWhere);
        Authentication.setAuthenticatedUserId(userId.toString());
        Comment commentObj = taskService.addComment(taskId, processInstanceId, comment);
        comment = comment + "(" + autUser.getFullName() + DateUtil.datetime2str(commentObj.getTime()) + ")";
        // 填写完意见，插入表单
        // 把评论数据保存到对应的评论控件中
        for (Map.Entry<String, String> map : paramMap.entrySet()) {
            String formWidgetId = map.getKey();
            String formWidgetVal = map.getValue();
            if (!(StringUtils.isEmpty(actAljoinTaskAssignee.getCommentWidgetIds()))
                && (actAljoinTaskAssignee.getCommentWidgetIds().indexOf(formWidgetId) != -1)
                && (!StringUtils.isEmpty(comment))) {
                if (!StringUtils.isEmpty(formWidgetVal)) {
                    paramMap.put(formWidgetId, formWidgetVal + "\n" + comment);
                } else {
                    paramMap.put(formWidgetId, comment);
                }
            }
        }

        actAljoinFormDataDraftService.updateFormData(paramMap, instance, entity, isTask, nextTaskKey, null);

        // 通过跳转进行退回
        actActivitiService.jump2Task3(targetTaskKey, processInstanceId, orgnlTask.getExecutionId());
        // 跳转后获取当前活动节点
        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
            .executionId(orgnlTask.getExecutionId()).taskDefinitionKey(targetTaskKey).singleResult();
        HistoricTaskInstance hisTask =
            historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
        if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
            // 取消签收
            taskService.unclaim(task.getId());
        }
        // taskService.claim(task.getId(), targetUserId);
        List<String> assignees = new ArrayList<String>();
        HashSet<Long> userIdSet = new HashSet<Long>();
        List<HistoricTaskInstance> historicList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey).finished().list();
        for (HistoricTaskInstance historic : historicList) {
            if (historic == null || historic.getAssignee() == null) {
                continue;
            }
            userIdSet.add(Long.valueOf(historic.getAssignee()));
            assignees.add(historic.getAssignee());
        }

        // 设置候选
        taskService.addUserIdentityLink(task.getId(), targetUserId, "candidate");
        Where<AutUser> uwhere = new Where<AutUser>();
        uwhere.setSqlSelect("id,full_name");
        uwhere.in("id", userIdSet);
        List<AutUser> userList = autUserService.selectList(uwhere);
        String userName = "";
        for (AutUser user : userList) {
            userName += user.getFullName() + ";";
        }
        actAljoinQueryService.updateCurrentUserName(task.getId(), userName);

        logMap.put("preTask", task);
        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
        taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
        logMap.put("taskKeyUserMap", taskKeyMap);
        logMap.put("userTask", orgnlTask);
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
        actActivitiWebService.insertOrUpdateLog(logMap, Long.valueOf(userId));
        // 记录流程日志
        // actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,
        // WebConstant.PROCESS_OPERATE_STATUS_2,userId,null);
        retMsg.setObject(instance.getId());
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
     * @date：2017-12-28
     */
    @Transactional
    public RetMsg appJump2Task2(String processInstanceId, Long createUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId).orderByHistoricActivityInstanceEndTime().desc().list();
        if (!activities.isEmpty()) {
            if (!activities.get(0).getActivityType().equals("userTask")) {
                retMsg.setCode(1);// 排他网关不允许撤回
                retMsg.setMessage("当前任务节点不满足撤回条件");
                return retMsg;
            }
            Map<String, Object> logMap = new HashMap<String, Object>();
            List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc().list();
            int i = 0;
            String thistaskId = historicTaskInstanceList.get(i).getId(); // 当前环节id
            String pretaskId = historicTaskInstanceList.get(i + 1).getId(); // 上一环节id
            Task tk = taskService.createTaskQuery().taskId(thistaskId).singleResult();// 当前环节是否被签收
            if (null != tk) {
                HistoricTaskInstance hisTsk =
                    historyService.createHistoricTaskInstanceQuery().taskId(tk.getId()).singleResult();
                if (null != hisTsk.getClaimTime() && !StringUtils.isEmpty(hisTsk.getAssignee())) {
                    // 任务已被办理人签收不可撤回
                    retMsg.setCode(1);
                    retMsg.setMessage("任务已被办理人签收不可撤回");
                    return retMsg;
                }
                Task lastTask = taskService.createTaskQuery().taskId(pretaskId).singleResult();
                logMap.put("preTask", lastTask);
                HistoricTaskInstance histask =
                    historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
                        .taskId(pretaskId).taskAssignee(String.valueOf(createUserId)).singleResult();
                if (null == histask) {
                    // 当前用户不是上一环节办理人
                    retMsg.setCode(1);
                    retMsg.setMessage("当前用户不是上一环节办理人，不可撤回");
                    return retMsg;
                } else {
                    List<HistoricTaskInstance> currentTask = actActivitiService.getCurrentNodeInfo(thistaskId);
                    String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
                    List<String> assignees = new ArrayList<String>();
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(processInstanceId) && null != currentTask
                        && !currentTask.isEmpty()) {
                        // 填写意见
                        Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
                        taskService.addComment(thistaskId, processInstanceId, "注：由操作人撤回");
                        logMap.put("comment", "注：由操作人撤回");
                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
                            List<TaskDefinition> preList =
                                actActivitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
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
                                if (!org.apache.commons.lang3.StringUtils.isEmpty(targetTaskKey)) {
                                    actActivitiService.jump2Task2(targetTaskKey, processInstanceId);
                                    Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                                        .taskDefinitionKey(targetTaskKey).singleResult();

                                    assignees.add(preTask.getAssignee());

                                    // 记录流程日志
                                    // actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,
                                    // WebConstant.PROCESS_OPERATE_STATUS_3,createUserId,null);
                                    // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                                    actFixedFormWebService.deleteOrgnlTaskAuth(task);
                                    HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery()
                                        .taskId(task.getId()).singleResult();
                                    if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                                        taskService.unclaim(task.getId());
                                    }
                                    if (assignees.size() == 1) {
                                        taskService.addCandidateUser(task.getId(), assignees.get(0));
                                        taskService.claim(task.getId(), assignees.get(0));
                                        String userFullName = "";
                                        if (null != task.getAssignee()) {
                                            userFullName = autUserService.selectById(task.getAssignee()).getFullName();
                                        }
                                        actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
                                    } else {
                                        for (String assignee : assignees) {
                                            taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
                                        }
                                    }
                                    actAljoinQueryService.cleanQureyCurrentUser(task.getId());
                                    Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                                    taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
                                    logMap.put("taskKeyUserMap", taskKeyMap);
                                    logMap.put("userTask", task);
                                    logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_3);
                                    actActivitiWebService.insertOrUpdateLog(logMap, createUserId);
                                }
                            }
                        }
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
    * 传阅
    *
    * @return：RetMsg
    *
    * @author：sln
    *
    * @date：2018-04-27
    */
    @Transactional
    public void circulate(String taskIds, String proInstId, String sendIds, AutUser user) throws Exception{
        Map<String, Object> map = ioaWaitingWorkService.circula(taskIds, proInstId, sendIds, user);
        // 传阅记录日志
        if(null != map){
            actActivitiWebService.insertOrUpdateLog(map, user.getId());
        }
    }
    
    /**
    *
    * 分发
    *
    * @return：RetMsg
    *
    * @author：sln
    *
    * @date：2018-04-27
    */
    @Transactional
    public Map<String, Object> distribution(String htmlCode, String sendIds, Long userId, String taskId) throws Exception{
        Map<String, Object> map = ioaWaitingWorkService.distribution(htmlCode, sendIds, userId, taskId);
        // 传阅记录日志
        if(null != map){
            actActivitiWebService.insertOrUpdateLog(map, userId);
        }
        return map;
    }

    /**
     * 扩展选择方式- 用户List
     * @return
     */
    public List<SimpleUserVO>  extSelectionForUserList(List<SimpleUserVO> suvoList,List<Long> userIdList,Long createrId){
        List<Long> suserIdList = new ArrayList<Long>();
        List<Long> uIdList = new ArrayList<Long>();
        if(suvoList.size() > 0){
            for(SimpleUserVO userVO : suvoList){
                suserIdList.add(Long.valueOf(userVO.getUserId()));
            }
            if(suserIdList.size() > userIdList.size()){
                for(Long suserId : suserIdList){
                    if(userIdList.contains(suserId)){
                        uIdList.add(suserId);
                    }
                }
            }
            if(userIdList.size() > suserIdList.size()){
                for(Long suserId : userIdList){
                    if(suserIdList.contains(suserId)){
                        uIdList.add(suserId);
                    }
                }
            }
        }else{
            uIdList = userIdList;
        }
        if(uIdList.size() > 0){

            List<SimpleUserVO> userVOList = new ArrayList<SimpleUserVO>();
            List<Long> deptIdList = new ArrayList<>();
            //获得流程创建人所在部门ID
            Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
            departmentUserWhere.setSqlSelect("id,dept_id,user_id");
            departmentUserWhere.in("user_id",uIdList);
            List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);

            List<Long> usrIdList = new ArrayList<Long>();

            for(AutDepartmentUser departmentUser : departmentUserList){
                if(uIdList.contains(departmentUser.getUserId())){
                    usrIdList.add(departmentUser.getUserId());
                }
            }

            Where<AutUser> userWhere = new Where<>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.in("id",usrIdList);
            List<AutUser> userList = autUserService.selectList(userWhere);
            for(AutUser user : userList){
                SimpleUserVO vo = new SimpleUserVO();
                vo.setUserId(user.getId().toString());
                vo.setUserName(user.getFullName());
                if(null != createrId){
                    if(user.getId().equals(createrId)){
                        vo.setIsCreater("1");
                    }else{
                        vo.setIsCreater("0");
                    }
                }else{
                    vo.setIsCreater("0");
                }

                userVOList.add(vo);
            }
            return  userVOList;
        }
        return null;
    }

    /**
     * 扩展选择方式- 部门List
     * @return
     */
    public List<SimpleDeptVO>   extSelectionForDeptList(List<SimpleDeptVO> sdvoList,Map<String,Object> deptMap){
        List<Long> sdeptIdList = new ArrayList<Long>();
        List<Long> uDeptIdList = new ArrayList<Long>();
        List<Long> deptIdList = (List<Long>)deptMap.get("deptIdList");
        Map<String,List<SimpleUserVO>> deptUserMap = (Map<String,List<SimpleUserVO>>)deptMap.get("deptUserMap");
        if(sdvoList.size() > 0){
            for(SimpleDeptVO deptVO : sdvoList){
                sdeptIdList.add(Long.valueOf(deptVO.getDeptId()));
            }
            if(sdeptIdList.size() > deptIdList.size()){
                for(Long sdeptId : sdeptIdList){
                    if(deptIdList.contains(sdeptId)){
                        uDeptIdList.add(sdeptId);
                    }
                }
            }
            if(deptIdList.size() > sdeptIdList.size()){
                for(Long sdeptId : deptIdList){
                    if(sdeptIdList.contains(sdeptId)){
                        uDeptIdList.add(sdeptId);
                    }
                }
            }

        }else{
            uDeptIdList = deptIdList;
        }

        if(uDeptIdList.size() > 0){
            List<SimpleDeptVO> deptList = new ArrayList<SimpleDeptVO>();
            //获得部门ID对应的所有部门用户
            Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
            departmentWhere.setSqlSelect("id");
            departmentWhere.in("id",uDeptIdList);
            List<AutDepartment> autDepartmentList = autDepartmentService.selectList(departmentWhere);

            //构造部门列表
            for(AutDepartment dept : autDepartmentList){
                if(uDeptIdList.contains(dept.getId())){
                    SimpleDeptVO sdv = new SimpleDeptVO();
                    sdv.setDeptId(String.valueOf(dept.getId()));
                    sdv.setUserList(deptUserMap.get(sdv.getDeptId()));
                    deptList.add(sdv);
                }
            }
            return  deptList;
        }
        return  null;
    }

    private List<Long> getUserIdList(String currentProcessInstanceId,String taskId,Set<String> staffMembersDepartmentTaskKeySet,Set<String> lastlinkDepartmentTaskKeySet,Set<String> createPersonsJobTaskKeySet,Long currentUserId){
        List<Long> userIdList = new ArrayList<>();
        ActAljoinQuery query =  null;
        List<Long> sdmUserList = new ArrayList<>();
        List<Long> ldUserList = new ArrayList<>();
        List<Long> cjUserList = new ArrayList<>();
        if(staffMembersDepartmentTaskKeySet.size() > 0){
            //创建人所在部门人员
            Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
            queryWhere.setSqlSelect("id,create_user_id,create_full_user_name");
            queryWhere.eq("process_instance_id", currentProcessInstanceId);
            query = actAljoinQueryService.selectOne(queryWhere);
            userIdList.add(query.getCreateUserId());
            sdmUserList = getNewUserIdList(userIdList);
        }
        if(lastlinkDepartmentTaskKeySet.size() > 0){
            userIdList.clear();
            //上一个环节办理人人所在部门人员
            Task hisTask = taskService.createTaskQuery().taskId(taskId).singleResult();
            Long lastAssignee = null;
            if(StringUtils.isEmpty(hisTask.getAssignee())){
                lastAssignee = currentUserId;
            }else{
                lastAssignee = Long.valueOf(hisTask.getAssignee());
            }
            userIdList.add(lastAssignee);
            ldUserList = getNewUserIdList(userIdList);

        }
        if(createPersonsJobTaskKeySet.size() > 0){

        }
        if(sdmUserList.size() > 0 && ldUserList.size() > 0 && cjUserList.size() > 0){

        }
        if(sdmUserList.size() > 0 && ldUserList.size() > 0){
            userIdList.clear();
            if(sdmUserList.size() > ldUserList.size() || sdmUserList.size() == ldUserList.size()){
                for(Long userId :  sdmUserList){
                    if(ldUserList.contains(userId)){
                        userIdList.add(userId);
                    }
                }
            }else{
                for(Long userId :  ldUserList){
                    if(sdmUserList.contains(userId)){
                        userIdList.add(userId);
                    }
                }
            }

        }
        return userIdList;
    }

    /**
     *
     * @param currentProcessInstanceId    当前流程实例ID
     * @param taskId                      当前任务ID
     * @return
     */
    public List<Long> getSelectUserIdList(String currentProcessInstanceId,String taskId,Set<String> staffMembersDepartmentTaskKeySet,Set<String> lastlinkDepartmentTaskKeySet,Set<String> createPersonsJobTaskKeySet,Long currentUserId){
        List<Long> userIdList = getUserIdList(currentProcessInstanceId,taskId,staffMembersDepartmentTaskKeySet,lastlinkDepartmentTaskKeySet,createPersonsJobTaskKeySet,currentUserId);
        List<Long> deptIdList = new ArrayList<>();
        //获得流程创建人所在部门ID
        Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
        departmentUserWhere.setSqlSelect("id,dept_id,user_id");
        departmentUserWhere.in("user_id",userIdList);
        List<AutDepartmentUser> autDepartmentUserList = autDepartmentUserService.selectList(departmentUserWhere);

        //获得流程创建人所在部门ID
        for(AutDepartmentUser departmentUser : autDepartmentUserList){
            deptIdList.add(departmentUser.getDeptId());
        }

        //获得流程创建人所在部门人员
        Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
        deptUserWhere.setSqlSelect("id,dept_id,user_id");
        deptUserWhere.in("dept_id",deptIdList);
        List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(deptUserWhere);

        for(AutDepartmentUser departmentUser : departmentUserList){
            userIdList.add(departmentUser.getUserId());
        }
        return  userIdList;
    }

    public Map<String,Object> getSelectDeptIdList(String currentProcessInstanceId,String taskId,Set<String> staffMembersDepartmentTaskKeySet,Set<String> lastlinkDepartmentTaskKeySet,Set<String> createPersonsJobTaskKeySet,Long createrId,Long currentUserId) throws Exception{
        Map<String,Object> map = new HashMap<String,Object>();
        List<Long> userIdList = getUserIdList(currentProcessInstanceId,taskId,staffMembersDepartmentTaskKeySet,lastlinkDepartmentTaskKeySet,createPersonsJobTaskKeySet,currentUserId);
        List<Long> deptIdList = new ArrayList<>();
        //获得流程创建人所在部门ID
        Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
        departmentUserWhere.setSqlSelect("id,dept_id,user_id");
        departmentUserWhere.in("user_id",userIdList);
        List<AutDepartmentUser> autDepartmentUserList = autDepartmentUserService.selectList(departmentUserWhere);

        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.setSqlSelect("id,user_name,full_name");
        userWhere.in("id",userIdList);
        List<AutUser> userList = autUserService.selectList(userWhere);
        Map<Long,String> userMap = new HashMap<Long,String>();
        for(AutUser user :userList){
            userMap.put(user.getId(),user.getFullName());
        }
        Map<String,List<SimpleUserVO>> deptUserMap = new HashMap<String,List<SimpleUserVO>>();
        List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();



        //获得流程创建人所在部门ID
        for(AutDepartmentUser departmentUser : autDepartmentUserList){
            SimpleUserVO suv = new SimpleUserVO();
            suv.setUserId(String.valueOf(departmentUser.getUserId()));
            suv.setDeptId(String.valueOf(departmentUser.getDeptId()));
            suv.setUserName(userMap.get(departmentUser.getUserId()));
            if(null != createrId){
                if(suv.getUserId().equals(createrId)){
                    suv.setIsCreater("1");
                }else{
                    suv.setIsCreater("0");
                }
            }else{
                suv.setIsCreater("0");
            }
            deptUserList.add(suv);
            deptUserMap.put(String.valueOf(departmentUser.getDeptId()),deptUserList);
        }
        for(String key : deptUserMap.keySet()){
            deptIdList.add(Long.valueOf(key));
        }
        map.put("deptIdList",deptIdList);
        map.put("deptUserMap",deptUserMap);
        return  map;
    }

    public List<Long> getNewUserIdList(List<Long> uIdList){
        List<Long> userIdList = new ArrayList<Long>();
        //获得流程创建人所在部门ID
        Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
        departmentUserWhere.setSqlSelect("id,dept_id,user_id");
        departmentUserWhere.in("user_id",uIdList);
        List<AutDepartmentUser> autDepartmentUserList = autDepartmentUserService.selectList(departmentUserWhere);

        for(AutDepartmentUser departmentUser : autDepartmentUserList){
            userIdList.add(departmentUser.getUserId());
        }

        return  userIdList;
    }

}
