package aljoin.web.service.off;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;

/**
 *
 * 工作月报表(服务实现类).
 *
 * @author：wangj
 *
 * @date： 2017-10-11
 */

@Service
public class OffMonthReportWebService {
	@Resource
	private AutUserService autUserService;
	@Resource
	private OffMonthReportService offMonthReportService;
	@Resource
	private ActAljoinFixedConfigService actAljoinFixedConfigService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	private ActActivitiService activitiService;
	@Resource
	private TaskService taskService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private ActActivitiWebService actActivitiWebService;
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private ActActivitiService actActivitiService;
	@Resource
	private HistoryService historyService;
	@Resource
	private ActAljoinBpmnRunService actAljoinBpmnRunService;

	@Transactional
	public RetMsg submit(OffMonthReport obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		Map<String, String> map = new HashMap<String, String>();
		ActAljoinBpmn bpmn = null;
		
		if (null != obj && null != obj.getId()) {
			OffMonthReport monthReport = offMonthReportService.selectById(obj.getId());
			if(monthReport.getStatus().equals(1)){
				retMsg.setCode(1);
				retMsg.setMessage("月报已提交！请在待办列表提交处理");
				return retMsg;
			}
            // 查流程
            Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
            configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_MONTHLY_REPORT);
            configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
            ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);
            if(null == config || null == config.getProcessId()){
                retMsg.setCode(1);
                retMsg.setMessage("没有找到月报流程，请先新增流程");
                return retMsg;
            }
            Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
            where.eq("process_id", config.getProcessId());
            where.eq("is_deploy", 1);
            where.eq("is_active", 1);
            bpmn = actAljoinBpmnService.selectOne(where);
            if(null == bpmn){
                retMsg.setCode(1);
                retMsg.setMessage("月报流程不存在，请检查流程配置");
                return retMsg;
            }
            
            // 如果此时流程版本有升级，在run表中插入一条新的记录
            Where<ActAljoinBpmnRun> bpmnRunWhere = new Where<ActAljoinBpmnRun>();
            bpmnRunWhere.eq("orgnl_id", bpmn.getId());
            bpmnRunWhere.eq("version", bpmn.getVersion());
            ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectOne(bpmnRunWhere);
            if(null == bpmnRun){
                bpmnRun = new ActAljoinBpmnRun();
                BeanUtils.copyProperties(bpmn, bpmnRun);
                bpmnRun.setId(null);
                bpmnRun.setOrgnlId(bpmn.getId());
                actAljoinBpmnRunService.insert(bpmnRun);
            }
            
            // 启动流程
            Map<String, String> param = new HashMap<String, String>();
            param.put("bizType", "off");
            param.put("bizId", monthReport.getId() + "");
            param.put("isUrgent", "1");
            ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
                    monthReport.getCreateUserId());
            String businessKey = instance.getBusinessKey();
            map.put("businessKey", businessKey);
            String bpmnId = "";
            if (!StringUtils.isEmpty(businessKey)) {
                String[] key = businessKey.split(",");
                if (key.length >= 1) {
                    bpmnId = key[0];
                }
            }
            map.put("bpmnId", bpmnId);
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.eq("id", monthReport.getCreateUserId());
            AutUser user = autUserService.selectOne(userWhere);
            actActivitiWebService.insertProcessQuery(instance, "1", null, monthReport.getTitle(),
                    user.getFullName(), user.getFullName(), bpmn.getCategoryId(), false);

            Task task = taskService.createTaskQuery().processInstanceId(instance.getProcessInstanceId())
                    .singleResult();
            map.put("id", task.getId());
            map.put("processName", task.getName());
            map.put("taskDefKey", task.getTaskDefinitionKey());

            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
            // 本来第一环节不应该指定办理人，为了避免画出这种流程，加了这句判断
            if (null != hisTask.getClaimTime()) {
                actFixedFormService.deleteOrgnlTaskAuth(task);
                taskService.unclaim(task.getId());
            }
            taskService.claim(task.getId(), String.valueOf(monthReport.getCreateUserId()));
            map.put("signInTime", DateUtil.datetime2str(new Date()));
            String userFullName = "";
            if (null != task.getAssignee()) {
                userFullName = autUserService.selectById(task.getAssignee()).getFullName();
            }
            // 更新当前办理人
            actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
            // 记录流程日志
            Map<String,Object> logMap = new HashMap<String,Object>();  
            logMap.put("instance", instance);
            logMap.put("preTask", task);
            actActivitiWebService.insertOrUpdateLog(logMap, user.getId());
//            actActivitiWebService.insertOrUpdateActivityLog(instance.getId(), WebConstant.PROCESS_OPERATE_STATUS_1,obj.getCreateUserId(),null);
            // 更新月报状态
            monthReport.setSubmitTime(new Date());
            monthReport.setStatus(1);
            monthReport.setComment("");
            monthReport.setAuditStatus(1);// 状态审核中
            monthReport.setProcessId(bpmn.getProcessId());
            offMonthReportService.updateById(monthReport);
		}
		retMsg.setObject(map);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 完成当前任务-提交到下一审批环节
	 *
	 * @return：RetMsg
	 *
	 * @author：sunlinan
	 *
	 * @date：2018年2月2日 下午4:07:48
	 */
  @Transactional
  public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId,
      String userId, AutUser createUser, String message) throws Exception {
    RetMsg retMsg = new RetMsg();
    Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
    if (null == currentTask) {
      retMsg.setCode(1);
      retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
      return retMsg;
    }
    String processInstanceId = currentTask.getProcessInstanceId();
    Map<String,Object> logMap = new HashMap<String,Object>();  
    logMap.put("processInstanceId", processInstanceId);
    // 填写意见
    Comment commentObj = null;
    Authentication.setAuthenticatedUserId(createUser.getId().toString());
    commentObj = taskService.addComment(taskId, processInstanceId, message);
    logMap.put("commont", message);
    logMap.put("preTask", currentTask);
    // 如果有触发委托，当前的办理用户会被设置为空，变成候选状态，所有在进行任务进入下一步之前，多进行一次签收操作
    taskService.setAssignee(taskId, createUser.getId().toString());
    // 完成当前任务
    activitiService.completeTask(variables, taskId);
    Date date = new Date();
    if (StringUtils.isNotEmpty(bizId)) {
      OffMonthReport offMonthReport = offMonthReportService.selectById(Long.parseLong(bizId));
      // 更新月报意见域内容
      // 意见---无格式版
      // String oldComment = offMonthReport.getComment();
      String comments = "<div><span>" + message + "</span>" + "<span style='float:right'>（"
          + createUser.getFullName() + " " + DateUtil.datetime2str(commentObj.getTime())
          + "）</span></div>";
      // offMonthReport.setComment(oldComment + comments);

      if (null != offMonthReport) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        if (null == pi) {
          // 如果是结束节点，则记录最后一个办理人
          actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
          logMap.put("nextNode", "EndEvent_");
          offMonthReport.setComment(comments);
          offMonthReport.setAuditStatus(3);// 审核通过
          offMonthReport.setAuditTime(date);
          offMonthReport.setAuditReason("审核通过");
          offMonthReport.setSubmitId(Long.valueOf(createUser.getId()));
          offMonthReport.setSubmitterName(createUser.getFullName());
        }
        offMonthReportService.updateById(offMonthReport);
      }
    }
    // 设置当前任务办理人
    List<String> userIdList = new ArrayList<String>();
    List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    if (null != taskList && !taskList.isEmpty()) {
      Task task = taskList.get(0);
      if (null != task && StringUtils.isNotEmpty(userId)) {
        userIdList = Arrays.asList(userId.split(";"));
        if (null != userIdList && !userIdList.isEmpty()) {
          // 原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
          actFixedFormService.deleteOrgnlTaskAuth(task);
          HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
          if (null != hisTask.getClaimTime() || null != hisTask.getAssignee()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
              taskService.unclaim(task.getId());
          }
          HashSet<Long> userIdSet = new HashSet<Long>();
          for (String uId : userIdList) {
            userIdSet.add(Long.valueOf(uId));
            taskService.addCandidateUser(task.getId(), uId);
          }
          // actAljoinQueryService.cleanQureyCurrentUser(task.getId());
          Where<AutUser> uwhere = new Where<AutUser>();
          uwhere.setSqlSelect("id,full_name");
          uwhere.in("id", userIdSet);
          List<AutUser> userList = autUserService.selectList(uwhere);
          String userName = "";
          for (AutUser autUser : userList) {
            userName += autUser.getFullName() + ";";
          }
          actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
          Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
          taskKeyMap.put(task.getTaskDefinitionKey(), userIdList);
          logMap.put("taskKeyUserMap", taskKeyMap);
          logMap.put("userTask", task);
        }
      }
    }
    // 记录流程日志
    logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
    actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
//    actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
    String handle = "";
    // 下一环节办理人待办数量+1
    if (null != userIdList && !userIdList.isEmpty()) {
      handle = StringUtil.list2str(userIdList, ";");
    }
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("processInstanceId", processInstanceId);// 流程实例id
    map.put("handle", handle);// 下一级办理人
    retMsg.setObject(map);// 返回给controller异步调用在线消息

    retMsg.setCode(0);
    retMsg.setMessage("操作成功!");
    return retMsg;
  }
  
  /**
   * 
   * 退回到上一环节
   *
   * @return：RetMsg
   *
   * @author：sunlinan
   *
   * @date：2018年2月2日 下午4:38:15
   */
  @Transactional
  public RetMsg jump2Task2(String taskId, String bizId, String message, AutUser createUser)
      throws Exception {
    RetMsg retMsg = new RetMsg();
    Task preTask = taskService.createTaskQuery().taskId(taskId).singleResult();
    String processInstanceId = preTask.getProcessInstanceId();
    Map<String,Object> logMap = new HashMap<String,Object>();  
    logMap.put("preTask", preTask);
    logMap.put("processInstanceId", processInstanceId);
    List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
    List<String> assignees = new ArrayList<String>();
    if (org.apache.commons.lang3.StringUtils.isNotEmpty(processInstanceId) && null != currentTask
        && !currentTask.isEmpty()) {
      String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
      // 填写意见
      Authentication.setAuthenticatedUserId(createUser.getId().toString());
      taskService.addComment(taskId, processInstanceId, message);
      logMap.put("commont", message);
      if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
        List<TaskDefinition> preList =
            activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
        if (preList.size() > 0) {
          TaskDefinition taskDefinition = preList.get(0);
          String targetTaskKey = taskDefinition.getKey();
          if (!StringUtils.isEmpty(targetTaskKey)) {
            // 找到唯一一个历史上级节点
            List<HistoricTaskInstance> historicList =
                historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey)
                    .orderByHistoricTaskInstanceEndTime().desc().finished().list();
            if (historicList.isEmpty()) {
              retMsg.setCode(1);
              retMsg.setMessage("历史节点获取失败");
              return retMsg;
            }
            // 理论上historicList永远不为空，为了避免勿操作，判断一下
            HistoricTaskInstance historic = historicList.get(0);
            assignees.add(historic.getAssignee());

            actActivitiService.jump2Task2(targetTaskKey, processInstanceId);
            
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                .taskDefinitionKey(targetTaskKey).singleResult();

            // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
            actFixedFormService.deleteOrgnlTaskAuth(task);
            HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
            if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
              taskService.unclaim(task.getId());
            }
            HashSet<Long> userIdSet = new HashSet<Long>();
            for (String assignee : assignees) {
              userIdSet.add(Long.valueOf(assignee));
              taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
            }
            Where<AutUser> uwhere = new Where<AutUser>();
            uwhere.setSqlSelect("id,full_name");
            uwhere.in("id", userIdSet);
            List<AutUser> userList = autUserService.selectList(uwhere);
            String userName = "";
            for (AutUser autUser : userList) {
              userName += autUser.getFullName() + ";";
            }
            actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
            // actAljoinQueryService.cleanQureyCurrentUser(task.getId());
            Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
            taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
            logMap.put("taskKeyUserMap", taskKeyMap);
            logMap.put("userTask", task);
            logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
            actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
            actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
            // 记录流程日志
//            actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_2,createUser.getId(),null);
          }
        }
      }
    }
    String handle = "";
    if (assignees != null && !assignees.isEmpty()) {
      handle = StringUtil.list2str(assignees, ";");
    }
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("processInstanceId", processInstanceId);// 流程实例id
    map.put("handle", handle);// 下一级办理人
    retMsg.setObject(map);// 返回给controller异步调用在线消息

    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }
  
  /**
   * 
   * App完成当前任务-提交到下一审批环节
   *
   * @return：RetMsg
   *
   * @author：sunlinan
   *
   * @date：2018年2月2日 下午4:41:19
   */
  @Transactional
  public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId,
      String userId, AutUser createUser, String message) throws Exception {
    RetMsg retMsg = new RetMsg();
    Map<String,Object> logMap = new HashMap<String,Object>();
    Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
    if(null == currentTask){
      retMsg.setCode(1);
      retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
      return retMsg;
    }
    String processInstanceId = currentTask.getProcessInstanceId();
    logMap.put("processInstanceId", processInstanceId);
    // 填写意见
    Comment commentObj = null;
    Authentication.setAuthenticatedUserId(createUser.getId().toString());
    commentObj = taskService.addComment(taskId, processInstanceId, message);
    // 完成当前任务
    taskService.setAssignee(taskId, createUser.getId().toString());
    activitiService.completeTask(variables, taskId);
    logMap.put("commont", message);
    logMap.put("preTask", currentTask);
    Date date = new Date();
    if (StringUtils.isNotEmpty(bizId)) {
      OffMonthReport offMonthReport = offMonthReportService.selectById(Long.parseLong(bizId));
      // 更新月报意见域内容
      // 意见---无格式版
      // String oldComment = offMonthReport.getComment();
      String comments = "<div><span>" + message + "</span>" + "<span style='float:right'>（"
          + createUser.getFullName() + " " + DateUtil.datetime2str(commentObj.getTime())
          + "）</span></div>";
      // offMonthReport.setComment(oldComment + comments);
      if (null != offMonthReport) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId).singleResult();
        if (null == pi) {
          // 如果是结束节点，则记录最后一个办理人
          actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
          logMap.put("nextNode", "EndEvent_");
          offMonthReport.setComment(comments);
          offMonthReport.setAuditStatus(3);// 审核通过
          offMonthReport.setAuditTime(date);
          offMonthReport.setAuditReason("审核通过");
          offMonthReport.setSubmitId(Long.valueOf(createUser.getId()));
          offMonthReport.setSubmitterName(createUser.getFullName());
        }
        offMonthReportService.updateById(offMonthReport);
      }
    }
    // 设置当前任务办理人
    List<String> userIdList = new ArrayList<String>();
    List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
    if (null != taskList && !taskList.isEmpty()) {
      Task task = taskList.get(0);
      if (null != task && StringUtils.isNotEmpty(userId)) {
        userIdList = Arrays.asList(userId.split(";"));
        if (null != userIdList && !userIdList.isEmpty()) {
          // 原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
          actFixedFormService.deleteOrgnlTaskAuth(task);
          taskService.unclaim(task.getId());
          HashSet<Long> userIdSet = new HashSet<Long>();
          for (String uId : userIdList) {
            userIdSet.add(Long.valueOf(uId));
            taskService.addCandidateUser(task.getId(), uId);
          }

          Where<AutUser> uwhere = new Where<AutUser>();
          uwhere.setSqlSelect("id,full_name");
          uwhere.in("id", userIdSet);
          List<AutUser> userList = autUserService.selectList(uwhere);
          String userName = "";
          for (AutUser autUser : userList) {
            userName += autUser.getFullName() + ";";
          }
          actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
          Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
          taskKeyMap.put(task.getTaskDefinitionKey(), userIdList);
          logMap.put("taskKeyUserMap", taskKeyMap);
          logMap.put("userTask", task);
        }
      }
    }
    // 记录流程日志
    logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
    actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
//    actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
    // 环节办理人待办数量-1
    String handle = "";
    // 下一环节办理人待办数量+1
    if (null != userIdList && !userIdList.isEmpty()) {
      handle = StringUtil.list2str(userIdList, ";");
    }
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("processInstanceId", processInstanceId);// 流程实例id
    map.put("handle", handle);// 下一级办理人
    retMsg.setObject(map);// 返回给controller异步调用在线消息

    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功!");
    return retMsg;
  }
}
