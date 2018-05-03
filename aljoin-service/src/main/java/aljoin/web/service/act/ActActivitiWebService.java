package aljoin.web.service.act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Event;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.act.dao.entity.ActHiActinst;
import aljoin.act.dao.entity.ActHiTaskinst;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinActivityLogService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinDelegateInfoService;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.act.iservice.ActHiActinstService;
import aljoin.act.iservice.ActHiTaskinstService;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;

/**
 * 流程服务类(实现类).
 *
 * 生成待办工作记录的方法抽出来 需要加在线通知 避免各个项目循环依赖
 *
 * @author：wangj
 *
 * @date：2017年12月04日
 */
@Service

public class ActActivitiWebService {

	@Resource
	private HistoryService historyService;
	@Resource
	private ActAljoinFormDataRunService actAljoinFormDataRunService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private ActAljoinQueryHisService actAljoinQueryHisService;
	@Resource
	private ActAljoinCategoryService actAljoinCategoryService;
	@Resource
	private AutDataStatisticsService autDataStatisticsService;
	@Resource
	private TaskService taskService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActActivitiService actActivitiService;
	@Resource
	private ActAljoinActivityLogService actAljoinActivityLogService;
	@Resource
	private ActHiTaskinstService actHiTaskinstService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private ActAljoinDelegateInfoService actAljoinDelegateInfoService;
	@Resource
	private ActHiActinstService actHiActinstService;
	@Resource
	private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;

	@Transactional
	public void insertProcessQuery(ProcessInstance processInstance, String isUrgent, ActAljoinFormRun runForm,
																 String processTitle, String createFullUserName, String currentHandleFullUserName, Long categoryId,
																 Boolean isReadWork) throws Exception {

		HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
			.processInstanceId(processInstance.getProcessInstanceId()).singleResult();
		HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery()
			.processInstanceId(processInstance.getProcessInstanceId()).singleResult();

		// 流程启动成功后，插入流程查询数据
		ActAljoinQuery actAljoinQuery = new ActAljoinQuery();
		actAljoinQuery.setProcessInstanceId(historicProcessInstance.getId());
		actAljoinQuery.setProcessName(historicProcessInstance.getProcessDefinitionName());
		// 获取表单标题控件中的标题
		if (StringUtils.isNotEmpty(processTitle)) {
			// 固定表单标题
			actAljoinQuery.setProcessTitle(processTitle);
		} else if (runForm != null) {
			// 有标题控件的自定表单的标题
			Where<ActAljoinFormDataRun> actAljoinFormDataRunWhere = new Where<ActAljoinFormDataRun>();
			actAljoinFormDataRunWhere.eq("form_id", runForm.getId());
			actAljoinFormDataRunWhere.eq("proc_inst_id", historicProcessInstance.getId());
			List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService
				.selectList(actAljoinFormDataRunWhere);
			String documentNumber = "";
			for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
				if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_title_")) {
					// 标题
					actAljoinQuery.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
				} else if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_finish_time_")) {
					// 办结时间
					if (!StringUtils.isEmpty(actAljoinFormDataRun.getFormWidgetValue())) {
						actAljoinQuery.setLimitFinishTime(
							DateUtil.str2datetime(actAljoinFormDataRun.getFormWidgetValue() + ":00"));
					} else {
						actAljoinQuery.setLimitFinishTime(null);
					}
				}else if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_come_text")) {
                    // 来文文号
                    actAljoinQuery.setSerialNumber(actAljoinFormDataRun.getFormWidgetValue());
                } else if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_waternum_") 
                    || actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_writing_")){
                    // 文号
                    if(StringUtils.isNotEmpty(documentNumber)){
                        documentNumber += "; " + actAljoinFormDataRun.getFormWidgetValue();
                    }else{
                        documentNumber += actAljoinFormDataRun.getFormWidgetValue();
                    }
                    actAljoinQuery.setReferenceNumber(documentNumber);
                }
			}

			if (actAljoinQuery.getProcessTitle() == null || actAljoinQuery.getProcessTitle() == "") {
				actAljoinQuery.setProcessTitle(
					"(" + createFullUserName + ")" + historicProcessInstance.getProcessDefinitionName());
			}
		}
		actAljoinQuery.setCreateFullUserName(createFullUserName);
		actAljoinQuery.setCurrentHandleFullUserName(currentHandleFullUserName);
		List<ActAljoinCategory> actAljoinCategoryList = actAljoinCategoryService.getAllParentCategoryList(categoryId);
		String categoryIds = "";
		for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
			categoryIds += actAljoinCategory.getId() + ",";
		}
		if (!"".equals(categoryIds)) {
			categoryIds = categoryIds.substring(0, categoryIds.length() - 1);
		}
		actAljoinQuery.setProcessCategoryIds(categoryIds);
		if (StringUtils.isEmpty(isUrgent) || "1".equals(isUrgent)) {
			actAljoinQuery.setUrgentStatus("一般");
		} else if ("2".equals(isUrgent)) {
			actAljoinQuery.setUrgentStatus("紧急");
		} else if ("3".equals(isUrgent)) {
			actAljoinQuery.setUrgentStatus("加急");
		}
		actAljoinQuery.setStartTime(historicProcessInstance.getStartTime());
		ActAljoinQueryHis actAljoinQueryHis = new ActAljoinQueryHis();
		actAljoinQuery.setStartTask(task.getId());
		BeanUtils.copyProperties(actAljoinQuery, actAljoinQueryHis);
		actAljoinQueryService.insert(actAljoinQuery);
		actAljoinQueryHisService.insert(actAljoinQueryHis);

		List<String> userIdList = new ArrayList<String>();
		userIdList.add(String.valueOf(actAljoinQuery.getCreateUserId()));

		if (isReadWork) {//待阅文件isread为true
			// 首页待阅文件数据维护
			AutDataStatistics aut = new AutDataStatistics();
			aut.setObjectKey(WebConstant.AUTDATA_TOREAD_CODE);
			aut.setObjectName(WebConstant.AUTDATA_TOREAD_NAME);
			autDataStatisticsService.addOrUpdateList(aut, userIdList);
		} else {
			// 首页待办文件数据维护
			AutDataStatistics aut = new AutDataStatistics();
			aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
			aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
			autDataStatisticsService.addOrUpdateList(aut, userIdList);
		}

	}


	/**
	 * 记录流程操作日志
	 *
	 * 		processInstanceId : 流程实例ID
	 * 	  taskId            : 任务ID
	 * 	  operateStatus     : 1:提交 2:退回 3:撤回
	 * 	  operateUserId     : 操作用户ID
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-27
	 */
	@Transactional(rollbackFor = Exception.class)
	public void insertOrUpdateActivityLog(String processInstanceId,Integer operateStatus,Long operateUserId,String currentTaskId) throws Exception{
		//根据实例ID查询意见列表
		List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);
		List<ActAljoinActivityLog> actAljoinActivityLogList = new ArrayList<ActAljoinActivityLog>();
		List<Task> currentTaskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();

		//根据流程实例ID查询历史任务表集合  以便获取当前节点的上个节点信息
		Where<ActHiTaskinst> hiTaskinstWhere = new Where<ActHiTaskinst>();
		hiTaskinstWhere.setSqlSelect("id_,proc_def_id_,task_def_key_,proc_inst_id_,execution_id_,name_,start_time_,end_time_,claim_time_");
		hiTaskinstWhere.eq("proc_inst_id_",processInstanceId);
		hiTaskinstWhere.orderBy("id_",true);
		List<ActHiTaskinst> hiTaskinstList = actHiTaskinstService.selectList(hiTaskinstWhere);

		//标记上个环节是否是多实例会签节点
		boolean preIsMuti = false;
		//标记当前环节是否是多实例会签节点
		boolean currIsMuti = false;

		List<String> currentTaskIdList = new ArrayList<String>();
		for(Task task : currentTaskList) {
			currentTaskIdList.add(task.getId());
			MultiInstanceLoopCharacteristics multi = actActivitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
			//判断当前节点是否是会签节点
			if(null != multi){
				currIsMuti = true;
			}
		}

		//根据用户ID查询用户信息
		Where<AutUser> where = new Where<AutUser>();
		where.setSqlSelect("id,user_name,full_name");
		where.eq("id",operateUserId);
		AutUser operateUser = autUserService.selectOne(where);

		//用于计算上个节点信息
		int taskSize = 0;
		//如果当前节点不是会签节点 直接取当前任务size
		if(!currIsMuti){
			taskSize = currentTaskList.size();
		}else{
			Task tk = currentTaskList.get(0);
			//会签总数
			taskSize = ((Integer) runtimeService.getVariable(tk.getExecutionId(), "nrOfInstances")).intValue();
		}

		//获取当前节点的上个节点信息
		ActHiTaskinst preTask  = null;
		if(hiTaskinstList.size() > 1){
			preTask  = hiTaskinstList.get(hiTaskinstList.size()-taskSize-1);
			MultiInstanceLoopCharacteristics multi = actActivitiService.getMultiInstance(preTask.getProcInstId(), preTask.getTaskDefKey());
			if(null != multi){
				preIsMuti = true;
			}
			//上个节点是会签节点 需要取最后完成的那笔记录作为上个节点(意见是根据上个节点ID去获取的 如果没有拿到最后办理的那笔会签任务 意见获取会不正确)
			if(preIsMuti){
				Where<ActHiTaskinst> actHiTaskinstWhere = new Where<ActHiTaskinst>();
				actHiTaskinstWhere.setSqlSelect("id_,task_def_key_,proc_inst_id_,name_,end_time_");
				actHiTaskinstWhere.eq("proc_inst_id_",processInstanceId);
				actHiTaskinstWhere.isNotNull("end_time_");
				actHiTaskinstWhere.eq("task_def_key_",preTask.getTaskDefKey());
				actHiTaskinstWhere.orderBy("end_time_",false);
				List<ActHiTaskinst> actHiTaskinstList = actHiTaskinstService.selectList(actHiTaskinstWhere);
				if(actHiTaskinstList.size() > 0){
					preTask = actHiTaskinstList.get(0);
				}
			}
		}

        Map<String,String> commentMap = new HashMap<>();
        for(Comment comment : commentList){
            commentMap.put(comment.getTaskId(),comment.getFullMessage());
        }

        //根据任务ID查询委托记录 并修改 接收人信息(任务有可能存在委托给本人代办的情况)
        Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
        actAljoinDelegateInfoWhere.setSqlSelect("id,owner_user_id,owner_user_fullname,last_user_id,last_user_fullname,task_id,task_key,task_name,process_instance_id,assignee_user_id,assignee_user_fullname,has_do,delegate_user_ids,delegate_user_names");
        actAljoinDelegateInfoWhere.eq("process_instance_id",processInstanceId);
        actAljoinDelegateInfoWhere.orderBy("id",true);
        List<ActAljoinDelegateInfo> actAljoinDelegateInfoList = actAljoinDelegateInfoService.selectList(actAljoinDelegateInfoWhere);

        Map<String,ActAljoinDelegateInfo> delegateInfoMap = new HashMap<>();
        for(ActAljoinDelegateInfo delegateInfo : actAljoinDelegateInfoList){
            delegateInfoMap.put(delegateInfo.getTaskId(),delegateInfo);
        }

		//插入日志
		insertActivityLog(processInstanceId,operateStatus,operateUserId,operateUser.getFullName(),preTask,actAljoinActivityLogList,currentTaskList,currIsMuti,commentMap,delegateInfoMap,currentTaskId);

		if(actAljoinActivityLogList.size() > 0){
			actAljoinActivityLogService.insertBatch(actAljoinActivityLogList);
		}

		if(null != preTask){
			//根据 当前任务的上个任务ID 查询出对应记录 然后修改其下个节点信息为当前任务节点
			Where<ActAljoinActivityLog> actAljoinActivityLogWhere = new Where<ActAljoinActivityLog>();
			actAljoinActivityLogWhere.setSqlSelect("id,create_time,last_update_time,version,is_delete,create_user_id,create_user_name,last_update_user_id,last_update_user_name,receive_user_ids,receive_full_names,operate_time,operate_full_name,operate_user_id,comment,last_task_id,current_task_id,current_task_name,current_task_def_key");
			actAljoinActivityLogWhere.eq("proc_inst_id",processInstanceId);
			actAljoinActivityLogWhere.eq("current_task_id",preTask.getId());
			actAljoinActivityLogWhere.isNull("next_task_id");
			List<ActAljoinActivityLog> activityLogList = actAljoinActivityLogService.selectList(actAljoinActivityLogWhere);
			//to 如果下个节点是会签 那任务ID需要拼接用分号隔开
			if(activityLogList.size() > 0 && actAljoinActivityLogList.size() > 0){
				String nextTaskId = actAljoinActivityLogList.get(0).getCurrentTaskId();
				String nextTaskName = actAljoinActivityLogList.get(0).getCurrentTaskName();
				String nextTaskDefKey = actAljoinActivityLogList.get(0).getCurrentTaskDefKey();
				for(ActAljoinActivityLog actAljoinActivityLog : activityLogList){
					actAljoinActivityLog.setNextTaskId(nextTaskId);
					actAljoinActivityLog.setNextTaskName(nextTaskName);
					actAljoinActivityLog.setNextTaskDefKey(nextTaskDefKey);
				}
			}

			if(activityLogList.size()>0){
				actAljoinActivityLogService.updateBatchById(activityLogList);
			}
		}
	}

	public void insertActivityLog(String processInstanceId,Integer operateStatus,Long operateUserId,String operateFullName,ActHiTaskinst preTask,List<ActAljoinActivityLog> actAljoinActivityLogList,List<Task> currentTaskList,boolean currIsMuti,Map<String,String> commentMap,Map<String,ActAljoinDelegateInfo> delegateInfoMap,String currentTaskId){
		List<Event> events = new ArrayList<Event>();
		ActAljoinActivityLog actAljoinActivityLog = new ActAljoinActivityLog();
		actAljoinActivityLog.setId(null);
		actAljoinActivityLog.setProcInstId(processInstanceId);
		actAljoinActivityLog.setOperateStatus(operateStatus);
		actAljoinActivityLog.setOperateUserId("");
		actAljoinActivityLog.setOperateFullName("");
		actAljoinActivityLog.setReceiveUserIds("");
		actAljoinActivityLog.setReceiveFullNames("");
		actAljoinActivityLog.setNextTaskId("");
		actAljoinActivityLog.setNextTaskDefKey("");
		actAljoinActivityLog.setNextTaskName("");
		actAljoinActivityLog.setOperateTime(new Date());

		if(null != operateUserId && StringUtils.isNotEmpty(operateFullName)){
			actAljoinActivityLog.setOperateUserId(String.valueOf(operateUserId));
			actAljoinActivityLog.setOperateFullName(operateFullName);
		}

		if(null != preTask){
			actAljoinActivityLog.setLastTaskId(preTask.getId());
			actAljoinActivityLog.setLastTaskName(preTask.getName());
			actAljoinActivityLog.setLastTaskDefKey(preTask.getTaskDefKey());
		}else{
			Where<ActHiActinst> actinstWhere = new Where<ActHiActinst>();
			actinstWhere.setSqlSelect("id_,proc_inst_id_,act_id_,task_id_,act_name_");
			actinstWhere.eq("proc_inst_id_",processInstanceId);
			actinstWhere.orderBy("id_",true);
			List<ActHiActinst> actHiActinstList = actHiActinstService.selectList(actinstWhere);
			ActHiActinst actHiActinst = actHiActinstList.get(0);
			actAljoinActivityLog.setLastTaskId("");
			actAljoinActivityLog.setLastTaskName(actHiActinst.getActName());
			actAljoinActivityLog.setLastTaskDefKey(actHiActinst.getActId());
		}

		Task task = null;
		//记录会签的总处理人数
		int taskSize = 0;
		String receiveUserIds = "";
		String receiveUserNames = "";
		List<String> receiveUserIdList = new ArrayList<String>();
		if(currentTaskList.size() > 0){
			task = currentTaskList.get(0);
			//如果当前节点是会签节点
			if(currIsMuti){
				//会签总数
				taskSize = ((Integer) runtimeService.getVariable(task.getExecutionId(), "nrOfInstances")).intValue();
			}
			for (Task tsk : currentTaskList) {
				List<IdentityLink> linkList = taskService.getIdentityLinksForTask(tsk.getId());
				for (IdentityLink identityLink : linkList) {
					if("candidate".equals(identityLink.getType()) && com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(identityLink.getUserId())){
						receiveUserIdList.add(identityLink.getUserId());
					}
				}
			}

			actAljoinActivityLog.setExecutionId(task.getExecutionId());
			actAljoinActivityLog.setCurrentTaskDefKey(task.getTaskDefinitionKey());
			actAljoinActivityLog.setProcDefId(task.getProcessDefinitionId());
			actAljoinActivityLog.setCurrentTaskId(task.getId());
			actAljoinActivityLog.setCurrentTaskName(task.getName());

			//如果当前节点是会签节点 并且当前任务列表size不为1 则会签还没到最后一个人办理 则上个节点和下个节点信息均为当前节点 如 会稿 --> 会稿
			if(currIsMuti  && currentTaskList.size() >= 1 && currentTaskList.size() < taskSize){
				Where<ActHiTaskinst> hiTaskinstWhere = new Where<ActHiTaskinst>();
				hiTaskinstWhere.setSqlSelect("id_,task_def_key_,proc_inst_id_,name_,end_time_");
				hiTaskinstWhere.eq("proc_inst_id_",processInstanceId);
				hiTaskinstWhere.isNotNull("end_time_");
				hiTaskinstWhere.eq("task_def_key_",task.getTaskDefinitionKey());
				hiTaskinstWhere.orderBy("end_time_",false);
				List<ActHiTaskinst> hiTaskinstList = actHiTaskinstService.selectList(hiTaskinstWhere);
				if(hiTaskinstList.size() > 0){
					ActHiTaskinst actHiTaskinst = hiTaskinstList.get(0);
					actAljoinActivityLog.setLastTaskId(actHiTaskinst.getId());
					actAljoinActivityLog.setCurrentTaskId(actHiTaskinst.getId());
				}
				actAljoinActivityLog.setLastTaskName(task.getName());
				actAljoinActivityLog.setLastTaskDefKey(task.getTaskDefinitionKey());
			}


			//如果是加签
			if(operateStatus.equals(WebConstant.PROCESS_OPERATE_STATUS_4) && StringUtils.isNotEmpty(currentTaskId)){
				Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
				signInfoWhere.setSqlSelect("id,task_sign_user_ids,task_owner_id,task_id,task_owner_name,task_sign_user_id,task_sign_user_name,task_signed_user_id,task_signed_user_name");
				signInfoWhere.eq("task_id",currentTaskId);
				ActAljoinTaskSignInfo signInfo = actAljoinTaskSignInfoService.selectOne(signInfoWhere);
				List<String> taskSignUserIdList = new ArrayList<>();
				if(null != signInfo && StringUtils.isNotEmpty(signInfo.getTaskSignUserIds())){
                    taskSignUserIdList = Arrays.asList(signInfo.getTaskSignUserIds().split(","));
                }

				receiveUserIdList.clear();
				for(String key : taskSignUserIdList){
					receiveUserIdList.add(key);
				}
			}
		}else{
			Where<ActHiActinst> actinstWhere = new Where<ActHiActinst>();
			actinstWhere.setSqlSelect("id_,proc_inst_id_,act_id_,task_id_,act_name_");
			actinstWhere.eq("proc_inst_id_",processInstanceId);
			actinstWhere.orderBy("id_",true);
			List<ActHiActinst> actHiActinstList = actHiActinstService.selectList(actinstWhere);
			ActHiActinst actHiActinst = actHiActinstList.get(actHiActinstList.size()-1);
			actAljoinActivityLog.setCurrentTaskDefKey(actHiActinst.getActId());
			actAljoinActivityLog.setCurrentTaskId("");
			actAljoinActivityLog.setCurrentTaskName("结束");
		}

		//不是最后一个会签提交的时候 接收人信息置为空
		if(currIsMuti && currentTaskList.size() < taskSize && !operateStatus.equals(WebConstant.PROCESS_OPERATE_STATUS_4)){
			receiveUserIdList.clear();
		}

		if(receiveUserIdList.size() > 0){
			Where<AutUser> userWhere = new Where<AutUser>();
			userWhere.setSqlSelect("id,user_name,full_name");
			userWhere.in("id",receiveUserIdList);
			List<AutUser> userList = autUserService.selectList(userWhere);
			for(AutUser user : userList){
				receiveUserIds += user.getId() + ";";
				receiveUserNames += user.getFullName() + ";";
			}
		}

		actAljoinActivityLog.setReceiveUserIds(receiveUserIds);
		actAljoinActivityLog.setReceiveFullNames(receiveUserNames);

		if(operateStatus == 3){
			actAljoinActivityLog.setReceiveUserIds(operateUserId+";");
			actAljoinActivityLog.setReceiveFullNames(operateFullName+";");
		}

		//插入意见
        String message = "";
        for(String key : commentMap.keySet()){
            if(key.equals(actAljoinActivityLog.getLastTaskId())&& !actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_7)){
                message = commentMap.get(key);
            }
            if(key.equals(actAljoinActivityLog.getCurrentTaskId()) && actAljoinActivityLog.getOperateStatus().equals(WebConstant.PROCESS_OPERATE_STATUS_7)){
                message = commentMap.get(key);
            }

            actAljoinActivityLog.setComment(message);
        }

        //如果有委托记录 则修改 操作人信息
        for(String key :delegateInfoMap.keySet()){
            Set<Long> assigneeIdSet = new HashSet<Long>();
            List<HistoricTaskInstance> histaskList = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
            Map<String,HistoricTaskInstance> histaskMap = new HashMap<String,HistoricTaskInstance>();
            for (HistoricTaskInstance histask : histaskList) {
                histaskMap.put(histask.getId(), histask);
                if(histask != null && histask.getAssignee() != null && StringUtils.isNotEmpty(histask.getAssignee())){
                    assigneeIdSet.add(Long.valueOf(histask.getAssignee()));
                }
            }
            List<AutUser> assigneeList = new ArrayList<AutUser>();
            if(assigneeIdSet.size()>0){
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                userWhere.in("id",assigneeIdSet);
                assigneeList = autUserService.selectList(userWhere);
            }
            Map<String,AutUser> assigneeMap = new HashMap<String,AutUser>();
            for (AutUser autUser : assigneeList) {
                assigneeMap.put(autUser.getId().toString(), autUser);
            }

            if(StringUtils.isNotEmpty(actAljoinActivityLog.getLastTaskId())){
                HistoricTaskInstance historicTaskInstance = histaskMap.get(actAljoinActivityLog.getLastTaskId());
                if(null != historicTaskInstance){
                    AutUser taskAssignee = assigneeMap.get(historicTaskInstance.getAssignee());
                    if(taskAssignee != null){
                        String operaterFullName = "",assigneeNames = "";

                        if(key.equals(actAljoinActivityLog.getLastTaskId())){
                            ActAljoinDelegateInfo actAljoinDelegateInfo = delegateInfoMap.get(key);
                            operaterFullName = null != taskAssignee ? taskAssignee.getFullName() : actAljoinDelegateInfo.getAssigneeUserFullname();
                            String orignalFullName = actAljoinDelegateInfo.getOwnerUserFullname();
                            String orignalId = actAljoinDelegateInfo.getOwnerUserId().toString();

                            if(historicTaskInstance.getAssignee().equals(orignalId)){
                                operaterFullName = "";
                                assigneeNames = "";
                                break;
                            }
                            if(StringUtils.isNotEmpty(orignalFullName) && !historicTaskInstance.getAssignee().equals(orignalId)){
                                assigneeNames += orignalFullName + ",";
                            }
                        }

                        if(StringUtils.isNotEmpty(operaterFullName) && StringUtils.isNotEmpty(assigneeNames)){
                            if(assigneeNames.endsWith(",")){
                                assigneeNames = assigneeNames.substring(0, assigneeNames.length());
                            }
                            operaterFullName += "( " + assigneeNames + " 委托 )";
                            actAljoinActivityLog.setOperateFullName(operaterFullName);
                        }
                    }
                }
            }
        }
		actAljoinActivityLogList.add(actAljoinActivityLog);
	}

	/**
	 * 加签获得当前办理人
	 * @param taskId			当前任务ID
	 * @param currentUserId		当前用户ID
	 * @return
	 * @throws Exception
	 */
	public Set<Long> getCurrentHandlers(String taskId,String processInstanceId,Long currentUserId) throws Exception {
		//获取流程实例的所有任务
		List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).orderByTaskCreateTime().desc().list();
		Task task = taskList.get(0);
		MultiInstanceLoopCharacteristics currentMulti =
			actActivitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
		Set<Long> currentHandleSet = new HashSet<Long>();
		if(null != currentMulti){
			List<IdentityLink> linkList = new ArrayList<IdentityLink>();
			for (Task tsk : taskList) {
				if(linkList.size() > 0){
					linkList.addAll(taskService.getIdentityLinksForTask(tsk.getId()));
				}else{
					linkList = taskService.getIdentityLinksForTask(tsk.getId());
				}
			}
			for (IdentityLink identityLink : linkList) {
				if("candidate".equals(identityLink.getType()) && StringUtils.isNotEmpty(identityLink.getUserId())){
					currentHandleSet.add(Long.parseLong(identityLink.getUserId()));
				}
			}
			Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
			actAljoinDelegateInfoWhere.eq("task_id", taskId);
			actAljoinDelegateInfoWhere.eq("assignee_user_id", currentUserId);
			actAljoinDelegateInfoWhere.setSqlSelect("task_id,delegate_user_names");
			List<ActAljoinDelegateInfo> actAljoinDelegateInfoList = actAljoinDelegateInfoService
				.selectList(actAljoinDelegateInfoWhere);
			for (ActAljoinDelegateInfo actAljoinDelegateInfo : actAljoinDelegateInfoList) {
				if(StringUtils.isNotEmpty(actAljoinDelegateInfo.getDelegateUserIds())){
					String[] delegateUserIdArr = actAljoinDelegateInfo.getDelegateUserIds().split(",");
					for(String userId :delegateUserIdArr){
						currentHandleSet.add(Long.valueOf(userId));
					}
				}
			}
		}else{
			List<IdentityLink> linkList = new ArrayList<IdentityLink>();
			for (Task tsk : taskList) {
				if(linkList.size() > 0){
					linkList.addAll(taskService.getIdentityLinksForTask(tsk.getId()));
				}else{
					linkList = taskService.getIdentityLinksForTask(tsk.getId());
				}
			}
			for (IdentityLink identityLink : linkList) {
				if("candidate".equals(identityLink.getType()) && StringUtils.isNotEmpty(identityLink.getUserId())){
					currentHandleSet.add(Long.parseLong(identityLink.getUserId()));
				}
			}
		}
		return currentHandleSet;
	}
	
	@SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdateLog(Map<String, Object> paramMap, Long operatorId) throws Exception {
        // 流程实例
        ProcessInstance instance = (ProcessInstance)paramMap.get("instance");
        String processInstanceId = "";
        if(instance == null){
            processInstanceId = (String)paramMap.get("processInstanceId"); 
        }else{
            processInstanceId = instance.getId();
        }
        Where<ActAljoinActivityLog> beforeLogWhere = new Where<ActAljoinActivityLog>();
        beforeLogWhere.orderBy("id", true);
        beforeLogWhere.eq("proc_inst_id", processInstanceId);
        // 现有 日志列表
        List<ActAljoinActivityLog> oldLogList = actAljoinActivityLogService.selectList(beforeLogWhere);
        // 提交前节点
        Task preTask = (Task)paramMap.get("preTask");
        // 新建
        AutUser operateUser = autUserService.selectById(operatorId);
        if (oldLogList.size() == 0) {
            // 要建一条新建的日志-由开始节点到第一个任务节点
            ActAljoinActivityLog startLog = new ActAljoinActivityLog();
            startLog.setProcInstId(instance.getId());
            startLog.setProcDefId(instance.getProcessDefinitionKey());
            startLog.setCurrentTaskId(preTask.getId());
            startLog.setCurrentTaskDefKey(preTask.getTaskDefinitionKey());
            startLog.setCurrentTaskName(preTask.getName());
            startLog.setOperateFullName(operateUser.getFullName());
            startLog.setOperateUserId(operateUser.getId().toString());
            startLog.setOperateTime(preTask.getCreateTime());
            startLog.setOperateStatus(WebConstant.PROCESS_OPERATE_STATUS_1);
            startLog.setExecutionId(preTask.getExecutionId());
            // 拿开始节点那个圈圈的数据
            Where<ActHiActinst> actionwhere = new Where<ActHiActinst>();
            actionwhere.setSqlSelect("START_TIME_,PROC_INST_ID_,ACT_NAME_,ACT_ID_");
            actionwhere.eq("PROC_INST_ID_", instance.getProcessInstanceId());
            actionwhere.orderBy("START_TIME_");
            List<ActHiActinst> actionList = actHiActinstService.selectList(actionwhere);
            startLog.setLastTaskName(actionList.get(0).getActName());
            startLog.setLastTaskDefKey(actionList.get(0).getActId());
            actAljoinActivityLogService.insert(startLog);
            return;
        }
        ActAljoinActivityLog log = new ActAljoinActivityLog();
        log.setOperateUserId(operateUser.getId().toString());
        
        // 当前操作用户是否有委托信息
        Where<ActAljoinDelegateInfo> delegateInfoWhere = new Where<ActAljoinDelegateInfo>();
        delegateInfoWhere.setSqlSelect(
            "id,owner_user_id,owner_user_fullname,last_user_id,last_user_fullname,task_id,task_key,task_name,process_instance_id,assignee_user_id,assignee_user_fullname,has_do,delegate_user_ids,delegate_user_names");
        delegateInfoWhere.eq("process_instance_id", processInstanceId);
        delegateInfoWhere.eq("task_id", preTask.getId());
        delegateInfoWhere.eq("is_self_task", 0);
        delegateInfoWhere.orderBy("id", true);
        List<ActAljoinDelegateInfo> delegateInfoList =
            actAljoinDelegateInfoService.selectList(delegateInfoWhere);
        String ownerUserName = "";
        List<String> ownerUserId = new ArrayList<String>();
        for (ActAljoinDelegateInfo delegateInfo : delegateInfoList) {
            if(!ownerUserId.contains(delegateInfo.getOwnerUserId())){
                ownerUserName += delegateInfo.getOwnerUserFullname() + ";";
                ownerUserId.add(ownerUserName);
            }
        }
        if(ownerUserId.size()>0){
            log.setOperateFullName(operateUser.getFullName() + "( " + ownerUserName.subSequence(0, ownerUserName.length()-1) + " 委托 )");
        }else{
            log.setOperateFullName(operateUser.getFullName());
        }
        
        log.setOperateTime(new Date());
        log.setProcInstId(processInstanceId);
        log.setProcDefId(oldLogList.get(0).getProcDefId());
        log.setLastTaskId(preTask.getId());
        log.setLastTaskDefKey(preTask.getTaskDefinitionKey());
        log.setLastTaskName(preTask.getName());
        log.setComment((String)paramMap.get("commont"));
        // 操作
        Integer operateStatus = (Integer)paramMap.get("operateStatus");
        log.setOperateStatus(operateStatus);
        if(operateStatus.equals(WebConstant.PROCESS_OPERATE_STATUS_7)){
            //特送操作特殊处理
        
        }else if(operateStatus.equals(WebConstant.PROCESS_OPERATE_STATUS_6) || operateStatus.equals(WebConstant.PROCESS_OPERATE_STATUS_5) ){
            // 传阅
            String receiveUserIds = (String)paramMap.get("receiveUserIds");
            String receiveFullNames = (String)paramMap.get("receiveFullNames");
            log.setLastTaskDefKey(preTask.getTaskDefinitionKey());
            log.setReceiveFullNames(receiveFullNames);
            log.setReceiveUserIds(receiveUserIds);
            log.setLastTaskId(preTask.getId());
            log.setLastTaskName(preTask.getName());
            log.setCurrentTaskId("");
            log.setCurrentTaskDefKey("");
            log.setCurrentTaskName("");
            actAljoinActivityLogService.insert(log);
            return;
        }else if(operateStatus.equals(WebConstant.PROCESS_OPERATE_STATUS_4)){
            // 加签
        }
        
        // 提交前节点是否是会签节点
        MultiInstanceLoopCharacteristics isPreMult = (MultiInstanceLoopCharacteristics)paramMap.get("isPreMult");
        // 提交前节点是多实例节点
        if (isPreMult != null) {
            Boolean isPrePass = (Boolean)paramMap.get("isPrePass");
            // 当前环节是否提交结束
            if (!isPrePass) {
                // 是否串行
                if (isPreMult.isSequential()) {
                    List<Task> sequetialTask = (List<Task>)paramMap.get("taskList");
                    String assignee = (String)paramMap.get("assignee");
                    AutUser assignUser = autUserService.selectById(assignee);
                    List<ActAljoinActivityLog> taskLogList = new ArrayList<ActAljoinActivityLog>();
                    for (Task userTask : sequetialTask) {
                        ActAljoinActivityLog taskLog = new ActAljoinActivityLog();
                        BeanUtils.copyProperties(log, taskLog);
                        taskLog.setCurrentTaskDefKey(userTask.getTaskDefinitionKey());
                        taskLog.setCurrentTaskId(userTask.getId());
                        taskLog.setCurrentTaskName(userTask.getName());
                        taskLog.setReceiveFullNames(assignUser.getFullName());
                        taskLog.setReceiveUserIds(assignUser.getId().toString());
                        taskLog.setExecutionId(userTask.getExecutionId());
                        taskLogList.add(taskLog);
                    }
                    actAljoinActivityLogService.insertBatch(taskLogList);
                    return;
                } else {
                    log.setCurrentTaskDefKey(preTask.getTaskDefinitionKey());
                    log.setCurrentTaskId(preTask.getId());
                    log.setCurrentTaskName(preTask.getName());
                    log.setExecutionId(preTask.getExecutionId());
                    actAljoinActivityLogService.insert(log);
                    return;
                }
            }
        }
        // 提交后任务用户对应信息
        Map<String, List<String>> taskKeyUserMap = (Map<String, List<String>>)paramMap.get("taskKeyUserMap");
        Set<String> taskKeyList = taskKeyUserMap.keySet();
        ArrayList<Task> taskList = (ArrayList<Task>)paramMap.get("taskList");
        // 并行 目前会签不支持并行
        if (taskKeyList.size() > 1) {
            Set<String> userIdSet = new HashSet<String>();
            for (Task assTask : taskList) {
                // 每个任务的对应处理人
                List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                userIdSet.addAll(userIdList);
            }
            Where<AutUser> userwhere = new Where<AutUser>();
            userwhere.in("id", userIdSet);
            userwhere.setSqlSelect("id,full_name,user_name");
            List<AutUser> userList = autUserService.selectList(userwhere);
            List<ActAljoinActivityLog> taskLogList = new ArrayList<ActAljoinActivityLog>();
            for (Task assTask : taskList) {
                ActAljoinActivityLog taskLog = new ActAljoinActivityLog();
                BeanUtils.copyProperties(log, taskLog);
                String receiveUserIds = "";
                String receiveUserNames = "";
                List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                for (String userId : userIdList) {
                    for (AutUser autUser : userList) {
                        if (userId.equals(autUser.getId().toString())) {
                            receiveUserIds += autUser.getId() + ";";
                            receiveUserNames += autUser.getFullName() + ";";
                        }
                    }
                }
                taskLog.setExecutionId(assTask.getExecutionId());
                taskLog.setReceiveUserIds(receiveUserIds.substring(0, receiveUserIds.length() - 1));
                taskLog.setReceiveFullNames(receiveUserNames.substring(0, receiveUserNames.length() - 1));
                taskLog.setCurrentTaskDefKey(assTask.getTaskDefinitionKey());
                taskLog.setCurrentTaskId(assTask.getId());
                taskLog.setCurrentTaskName(assTask.getName());
                taskLogList.add(taskLog);
            }
            actAljoinActivityLogService.insertBatch(taskLogList);
        } else {
            MultiInstanceLoopCharacteristics isCurrentMult =
                (MultiInstanceLoopCharacteristics)paramMap.get("isCurrentMult");
            // 提交后节点是多实例节点
            if (isCurrentMult != null) {
                // 用户任务map
//                Map<String, Task> userTaskMap = (Map<String, Task>)paramMap.get("userTaskMap");
                // 用户列表
                List<Long> userRankIdList = (List<Long>)paramMap.get("userRankIdList");
                List<ActAljoinActivityLog> taskLogList = new ArrayList<ActAljoinActivityLog>();
                Where<AutUser> userwhere = new Where<AutUser>();
                userwhere.in("id", userRankIdList);
                userwhere.setSqlSelect("id,full_name,user_name");
                List<AutUser> userList = autUserService.selectList(userwhere);
                if (isCurrentMult.isSequential()) {
                    String receiveUserIds = "";
                    String receiveUserNames = "";
                    for (AutUser autUser : userList) {
                        receiveUserIds += autUser.getId() + ";";
                        receiveUserNames += autUser.getFullName() + ";";
                    }
                    log.setReceiveUserIds(receiveUserIds.substring(0, receiveUserIds.length() - 1));
                    log.setReceiveFullNames(receiveUserNames.substring(0, receiveUserNames.length() - 1));
                    for (Task userTask : taskList) {
                        ActAljoinActivityLog taskLog = new ActAljoinActivityLog();
                        BeanUtils.copyProperties(log, taskLog);
                        taskLog.setCurrentTaskDefKey(userTask.getTaskDefinitionKey());
                        taskLog.setCurrentTaskId(userTask.getId());
                        taskLog.setCurrentTaskName(userTask.getName());
                        taskLog.setExecutionId(userTask.getExecutionId());
                        taskLogList.add(taskLog);
                    }
                    // 假装这是个list，一般来说size都为1
                    actAljoinActivityLogService.insertBatch(taskLogList);
                } else {
                    // 并行会签记录第一个任务id
                    String receiveUserIds = "";
                    String receiveUserNames = "";
                    for (AutUser autUser : userList) {
                        receiveUserIds += autUser.getId() + ";";
                        receiveUserNames += autUser.getFullName() + ";";
                    }
                    log.setReceiveUserIds(receiveUserIds.substring(0, receiveUserIds.length() - 1));
                    log.setReceiveFullNames(receiveUserNames.substring(0, receiveUserNames.length() - 1));
                    Task assTask = taskList.get(0);
                    log.setExecutionId(assTask.getExecutionId());
                    log.setCurrentTaskDefKey(assTask.getTaskDefinitionKey());
                    log.setCurrentTaskId(assTask.getId());
                    log.setCurrentTaskName(assTask.getName());
                    actAljoinActivityLogService.insert(log);
                }
            } else {
                // 普通单分支流程
                Task assTask = (Task)paramMap.get("userTask");
                if (assTask == null && taskList != null && taskList.size() > 0) {
                    Set<String> userIdSet = new HashSet<String>();
                    for (Task task : taskList) {
                        if (taskKeyUserMap.get(task.getTaskDefinitionKey()) != null) {
                            userIdSet.addAll(taskKeyUserMap.get(task.getTaskDefinitionKey()));
                        }
                    }
                    Where<AutUser> userwhere = new Where<AutUser>();
                    userwhere.in("id", userIdSet);
                    userwhere.setSqlSelect("id,full_name,user_name");
                    List<AutUser> userList = autUserService.selectList(userwhere);
                    List<ActAljoinActivityLog> taskLogList = new ArrayList<ActAljoinActivityLog>();
                    for (Task task : taskList) {
                        if (taskKeyUserMap.get(task.getTaskDefinitionKey()) != null) {
                            ActAljoinActivityLog taskLog = new ActAljoinActivityLog();
                            BeanUtils.copyProperties(log, taskLog);
                            String receiveUserIds = "";
                            String receiveUserNames = "";
                            taskLog.setCurrentTaskDefKey(task.getTaskDefinitionKey());
                            taskLog.setCurrentTaskId(task.getId());
                            taskLog.setCurrentTaskName(task.getName());
                            taskLog.setExecutionId(task.getExecutionId());
                            List<String> userIdList = taskKeyUserMap.get(task.getTaskDefinitionKey());
                            for (AutUser autUser : userList) {
                                if (userIdList.contains(autUser.getId().toString())) {
                                    receiveUserIds += autUser.getId() + ";";
                                    receiveUserNames += autUser.getFullName() + ";";
                                }
                            }
                            taskLog.setReceiveUserIds(receiveUserIds.substring(0, receiveUserIds.length() - 1));
                            taskLog.setReceiveFullNames(receiveUserNames.substring(0, receiveUserNames.length() - 1));
                            taskLogList.add(taskLog);
                        }
                    }
                    if(taskLogList.size() > 0){
                        // 假装这是个list，一般来说size都为1
                        actAljoinActivityLogService.insertBatch(taskLogList);
                    }else{
                        Set<String> taskUserIdSet = new HashSet<String>();
                        for (String taskkey : taskKeyList) {
                            taskUserIdSet.addAll(taskKeyUserMap.get(taskkey));
                        }
                        
                        if (taskUserIdSet.size() > 0){
                            String receiveUserIds = "";
                            String receiveUserNames = "";
                            Where<AutUser> userwhere1 = new Where<AutUser>();
                            userwhere1.in("id", taskUserIdSet);
                            userwhere1.setSqlSelect("id,full_name,user_name");
                            List<AutUser> userList1 = autUserService.selectList(userwhere1);
                            for (AutUser autUser : userList1) {
                                receiveUserIds += autUser.getId() + ";";
                                receiveUserNames += autUser.getFullName() + ";";
                            }
                            log.setReceiveUserIds(receiveUserIds.substring(0, receiveUserIds.length() - 1));
                            log.setReceiveFullNames(receiveUserNames.substring(0, receiveUserNames.length() - 1));
                        }else{
                            log.setReceiveUserIds("");
                            log.setReceiveFullNames("");
                        }
                        
                        log.setCurrentTaskDefKey("");
                        log.setCurrentTaskId("");
                        log.setCurrentTaskName("并行流程等待其他支流结束");
                        actAljoinActivityLogService.insert(log);
                    }
                    
                } else if (taskList == null || taskList.size() == 0 ) {
                    if (assTask != null) {
                        log.setCurrentTaskDefKey(assTask.getTaskDefinitionKey());
                        log.setCurrentTaskId(assTask.getId());
                        log.setCurrentTaskName(assTask.getName());
                        log.setExecutionId(assTask.getExecutionId());
                        List<String> userIdList = taskKeyUserMap.get(assTask.getTaskDefinitionKey());
                        Where<AutUser> userwhere = new Where<AutUser>();
                        userwhere.in("id", userIdList);
                        userwhere.setSqlSelect("id,full_name,user_name");
                        List<AutUser> userList = autUserService.selectList(userwhere);
                        String receiveUserIds = "";
                        String receiveUserNames = "";
                        for (AutUser autUser : userList) {
                            receiveUserIds += autUser.getId() + ";";
                            receiveUserNames += autUser.getFullName() + ";";
                        }
                        log.setReceiveUserIds(receiveUserIds.substring(0, receiveUserIds.length() - 1));
                        log.setReceiveFullNames(receiveUserNames.substring(0, receiveUserNames.length() - 1));
                        actAljoinActivityLogService.insert(log);
                    } else {
                        String nextNode = (String)paramMap.get("nextNode");
                        log.setCurrentTaskDefKey(nextNode);
                        log.setCurrentTaskId("");
                        log.setCurrentTaskName("结束");
                        actAljoinActivityLogService.insert(log);
                    }
                }
            }
        }

    }
}
