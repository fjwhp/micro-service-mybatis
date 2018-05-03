package aljoin.web.service.act;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFormDataDraft;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.act.dao.entity.ActAljoinFormWidgetRun;
import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.act.iservice.ActAljoinFormRunService;
import aljoin.act.iservice.ActAljoinFormWidgetRunService;
import aljoin.act.iservice.ActAljoinTaskAssigneeService;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.act.iservice.ActTaskAddSignService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.util.DateUtil;

/**
 *
 * 表单数据表(草稿)(服务实现类).
 *
 * @author：zhongjy
 *
 * @date： 2017-09-30
 */
@Service
public class ActAljoinFormDataDraftWebService {
	@Resource
	private ResResourceService resResourceService;
	@Resource
	private ActAljoinFormRunService actAljoinFormRunService;
	@Resource
	private ActActivitiService activitiService;
	@Resource
	private TaskService taskService;
	@Resource
	private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
	@Resource
	private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
	@Resource
	private ActAljoinFormDataRunService actAljoinFormDataRunService;
	@Resource
	private AutDataStatisticsService autDataStatisticsService;
	@Resource
	private ActActivitiWebService actActivitiWebService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
	@Resource
	private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
	@Resource
	private ActRuTaskService actRuTaskService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActTaskAddSignService actTaskAddSignService;
	@Resource
	private RepositoryService repositoryService;
	@Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;

	public RetMsg delAttach(String attachId, Long userId) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (StringUtils.isNotEmpty(attachId) && null != userId) {
			Where<ResResource> where = new Where<ResResource>();
			where.eq("id", attachId);
			ResResource resResource = resResourceService.selectOne(where);
			if (null != resResource && resResource.getCreateUserId().equals(userId)) {
			    resResourceService.delete(resResource);
			} else {
				retMsg.setCode(1);
				retMsg.setMessage("您没有权限删除");
				return retMsg;
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 *
	 * @描述: 待办工作存稿---->因为要做首页数据统计，抽出来了
	 *
	 * 		@return： Map<String,String>
	 *
	 * 		@author： sunlinan
	 *
	 * @date 2017年12月15日
	 */
	@Transactional
	public Map<String, String> saveDraft(ActAljoinFormDataDraft entity, Map<String, String> paramMap,
		Map<String, String> param, Long userId, String fullName) throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		Map<String, String> dataMap = new HashMap<String, String>();
		Map<String, String> readMap = new HashMap<String, String>();
		// 日志传参
		Map<String, Object> logMap = new HashMap<String,Object>();
		// 存稿启动流程
		ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
		ActAljoinFormRun runForm = actAljoinFormRunService.selectById(entity.getFormId());

		ProcessInstance instance = activitiService.startBpmn(bpmnRun, runForm, param, userId);
		logMap.put("instance", instance);
		// 返回流程实例ID和当然任务ID
		Task task = taskService.createTaskQuery().processInstanceId(instance.getId()).singleResult();
		taskService.setAssignee(task.getId(), userId.toString());
		taskService.claim(task.getId(), userId.toString());

		retMap.put("taskId", task.getId());
		retMap.put("bpmnId", bpmnRun.getId() + "");
		retMap.put("formId", runForm.getId() + "");
		logMap.put("preTask", task);
		List<ActAljoinFormDataDraft> draftList = new ArrayList<ActAljoinFormDataDraft>();
		List<ActAljoinFormDataRun> runList = new ArrayList<ActAljoinFormDataRun>();

		//把数据源和是否已读分别取出来放进map，再把原有的移出
		Iterator<Map.Entry<String, String>> it = paramMap.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, String> map=it.next();
			String formWidgetId = map.getKey();
			String formWidgetVal = map.getValue();
			if(formWidgetId.startsWith("sourceData_")) {
				dataMap.put(formWidgetId, formWidgetVal);
				it.remove();
				paramMap.remove(formWidgetId);
			}
			if(formWidgetId.startsWith("dataReadIs_")) {
				readMap.put(formWidgetId, formWidgetVal);
				it.remove();
				paramMap.remove(formWidgetId);
			}
		}


		/*for (Map.Entry<String, String> map : paramMap.entrySet()) {
		  String formWidgetId = map.getKey();
          String formWidgetVal = map.getValue();
          if(formWidgetId.startsWith("sourceData_")) {
              dataMap.put(formWidgetId, formWidgetVal);
              ids.add(formWidgetId);
          }
          if(formWidgetId.startsWith("dataReadIs_")) {
            readMap.put(formWidgetId, formWidgetVal);
            ids.add(formWidgetId);
          }
		}
		paramMap.remove(ids);*/

		// 保存草稿数据
		for (Map.Entry<String, String> map : paramMap.entrySet()) {
			ActAljoinFormDataDraft draft = new ActAljoinFormDataDraft();
			ActAljoinFormDataRun run = new ActAljoinFormDataRun();
			String formWidgetId = map.getKey();
			String formWidgetVal = map.getValue();
			// 草稿
			draft.setBpmnId(bpmnRun.getId());
			draft.setOperateUserId(entity.getOperateUserId());
			draft.setProcTaskId(instance.getActivityId());
			draft.setProcInstId(instance.getProcessInstanceId());
			draft.setProcDefId(instance.getProcessDefinitionId());
			draft.setFormId(entity.getFormId());
			// 运行时
			run.setBpmnId(entity.getBpmnId());
			run.setOperateUserId(entity.getOperateUserId());
			run.setProcTaskId(instance.getActivityId());
			run.setProcInstId(instance.getProcessInstanceId());
			run.setProcDefId(instance.getProcessDefinitionId());
			run.setFormId(entity.getFormId());
			// 根据表单控件ID和表单ID获取运行时的控件完整信息
			Where<ActAljoinFormWidgetRun> widgetWhere = new Where<ActAljoinFormWidgetRun>();
			ActAljoinFormRun formRun = actAljoinFormRunService.selectById(entity.getFormId());
			widgetWhere.eq("form_id", formRun.getOrgnlId());
			widgetWhere.eq("widget_id", formWidgetId);
			ActAljoinFormWidgetRun runFormWidget = actAljoinFormWidgetRunService.selectOne(widgetWhere);
			// 草稿
			draft.setWidgetId(runFormWidget.getId());
			draft.setFormWidgetId(runFormWidget.getWidgetId());
			draft.setFormWidgetName(runFormWidget.getWidgetName());
			draft.setFormWidgetValue(formWidgetVal);
			//数据源
			if(dataMap != null) {
				for (Map.Entry<String, String> data : dataMap.entrySet()) {
					String formWidgetId1 = data.getKey();
					String formWidgetVal1 = data.getValue();
					if(formWidgetId1.contains(formWidgetId)) {
						draft.setDataResource(formWidgetVal1);
						run.setDataResource(formWidgetVal1);
					}
				}
			}
			//是否读取了数据源
			if(readMap != null) {
				for (Map.Entry<String, String> read : readMap.entrySet()) {
					String formWidgetId1 = read.getKey();
					String formWidgetVal1 = read.getValue();
					if(formWidgetId1.contains(formWidgetId)) {
						draft.setIsRead(Integer.valueOf(formWidgetVal1));
						run.setIsRead(Integer.valueOf(formWidgetVal1));
					}
				}
			}
			// 运行时
			run.setWidgetId(runFormWidget.getId());
			run.setFormWidgetId(runFormWidget.getWidgetId());
			run.setFormWidgetName(runFormWidget.getWidgetName());
			run.setFormWidgetValue(formWidgetVal);

			draftList.add(draft);
			runList.add(run);
		}
		if(draftList.size() > 0){
		    actAljoinFormDataDraftService.insertBatch(draftList);
		}
		if(runList.size() > 0){
		    actAljoinFormDataRunService.insertBatch(runList);
		}

		// String fullName=autUserService.selectById(userId).getFullName();
		activitiService.insertProcessQuery(instance, param.get("isUrgent"), runForm, null, fullName, fullName,
			bpmnRun.getCategoryId());
		//记录流程日志
//		actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_1,userId,null);
		// 首页待办文件数据维护-只有一种情况这里会执行，就是发起流程的时候
		
		actActivitiWebService.insertOrUpdateLog(logMap, userId);
		return retMap;
	}

	public Map<String, Object> doAddSign(ActAljoinFormDataRun entity, Map<String, String> paramMap, Map<String, String> param, Long userId, String fullName) throws Exception {
		// 获取流程
		ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
		// 获取运行时表单
		ActAljoinFormRun runForm = actAljoinFormRunService.selectById(entity.getFormId());
		// 任务ID
		String activityId = param.get("activityId");
		// 是否自由流程(用来标记自由流程，当isTask=false时是自由流程)
//		String isTask = param.get("isTask");
		// 加签人员列表
		String signUserIdList = param.get("signUserIdList");
		// 如果有触发委托，当前的办理用户会被设置为空，变成候选状态，所有在进行任务进入下一步之前，多进行一次签收操作
		// 还需要先判断一下任务是否存在,同一个用户同时登陆同时操作的情况下会有问题
		Task task = taskService.createTaskQuery().taskId(activityId).singleResult();
		if(task == null){
			throw new Exception("任务已被处理");
		}
		String taskAuth = task.getTaskDefinitionKey()+",";
		ProcessInstance instance = runtimeService.createProcessInstanceQuery()
			.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 获取任务的授权数据
		Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
		taskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
		taskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
		taskAssigneeWhere.eq("task_id", task.getTaskDefinitionKey());
		taskAssigneeWhere.setSqlSelect("sign_comment_widget_ids");
		ActAljoinTaskAssignee actAljoinTaskAssignee =
			actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

		// 保存评论数据
		String thisTaskUserComment = param.get("thisTaskUserComment");
		Comment thisTaskUserCommentObj = null;

		// 设置评论用户
		Authentication.setAuthenticatedUserId(userId.toString());
		if (StringUtils.isEmpty(thisTaskUserComment)) {
			thisTaskUserComment = "";
		}

		thisTaskUserCommentObj =
			taskService.addComment(task.getId(), instance.getId(), thisTaskUserComment);
		thisTaskUserComment = thisTaskUserComment + "(" + fullName + " "
			+ DateUtil.datetime2str(thisTaskUserCommentObj.getTime()) + ")";

		// 一次获取获取最新的评论控件内容
		Map<String, String> commentKeyValueMap = new HashMap<String, String>();
		Where<ActAljoinFormDataRun> runDataWhere = new Where<ActAljoinFormDataRun>();
		runDataWhere.eq("proc_inst_id", task.getProcessInstanceId());
		runDataWhere.in("form_widget_id", actAljoinTaskAssignee.getSignCommentWidgetIds().split(","));
		List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService.selectList(runDataWhere);
		for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
			commentKeyValueMap.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun.getFormWidgetValue());
		}

		// 把评论数据保存到对应的评论控件中
		for (Map.Entry<String, String> map : paramMap.entrySet()) {
			String formWidgetId = map.getKey();
			String formWidgetVal = map.getValue();
			if ((com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(actAljoinTaskAssignee.getSignCommentWidgetIds()))
				&& (actAljoinTaskAssignee.getSignCommentWidgetIds().indexOf(formWidgetId) != -1)
				&& (com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(thisTaskUserComment))) {
				// 获取最新的评论控件内容
				if(com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(commentKeyValueMap.get(formWidgetId))){
					formWidgetVal = commentKeyValueMap.get(formWidgetId);
				}
				if (com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(formWidgetVal)) {
					paramMap.put(formWidgetId, formWidgetVal + "\n" + thisTaskUserComment);
				} else {
					paramMap.put(formWidgetId, thisTaskUserComment);
				}
			}
		}

		// 判断当前节点是否多实例节点
		MultiInstanceLoopCharacteristics currentMulti =
			activitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());

		List<Long> uidList = new ArrayList<Long>();
		// 分配任务的人员
		List<String> assigneeList = Arrays.asList(signUserIdList.split(";"));
		for(String assignee : assigneeList){
			uidList.add(Long.parseLong(assignee));
			taskAuth += assignee+"#";
		}

		if (currentMulti != null) {

			ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(task.getProcessDefinitionId());

			uidList.add(Long.valueOf(task.getAssignee()));
			Where<AutUser> userWhere = new Where<AutUser>();
			userWhere.setSqlSelect("id,user_name,full_name");
			userWhere.in("id",uidList);
			List<AutUser> userList = autUserService.selectList(userWhere);

			Map<Long,String> userMap = new HashMap<Long,String>();
			for(AutUser autUser : userList){
				userMap.put(autUser.getId(),autUser.getFullName());
			}


			//加签
			actTaskAddSignService.addSignTasksAfter(task,userId.toString(),definitionEntity,assigneeList,bpmnRun,userMap,uidList,1);
		}

		taskAuth = taskAuth.substring(0,taskAuth.lastIndexOf("#"));

		// 更新表单数据
		actAljoinFormDataDraftService.updateFormData(paramMap, instance, entity, null, null,task.getTaskDefinitionKey());

		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("instance", instance);
		retMap.put("bpmn", bpmnRun);
		retMap.put("runForm", runForm);
		retMap.put("taskAuth",taskAuth);
		return retMap;
	}

	public Map<String, Object> doAppAddSign(ActAljoinFormDataRun entity, Map<String, String> paramMap, Map<String, String> param, Long userId, String fullName) throws Exception {
		// 获取流程
		ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
		// 获取运行时表单
		ActAljoinFormRun runForm = actAljoinFormRunService.selectById(entity.getFormId());
		// 任务ID
		String activityId = param.get("taskId");
		// 是否自由流程(用来标记自由流程，当isTask=false时是自由流程)
		//		String isTask = param.get("isTask");
		// 加签人员列表
		String userIds = param.get("userIds");
		// 如果有触发委托，当前的办理用户会被设置为空，变成候选状态，所有在进行任务进入下一步之前，多进行一次签收操作
		// 还需要先判断一下任务是否存在,同一个用户同时登陆同时操作的情况下会有问题
		Task task = taskService.createTaskQuery().taskId(activityId).singleResult();
		if(task == null){
			throw new Exception("任务已被处理");
		}
		String taskAuth = task.getTaskDefinitionKey()+",";
		ProcessInstance instance = runtimeService.createProcessInstanceQuery()
			.processInstanceId(task.getProcessInstanceId()).singleResult();

		// 获取任务的授权数据
		Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
		taskAssigneeWhere.eq("bpmn_id", bpmnRun.getOrgnlId());
		taskAssigneeWhere.eq("version", bpmnRun.getTaskAssigneeVersion());
		taskAssigneeWhere.eq("task_id", task.getTaskDefinitionKey());
		taskAssigneeWhere.setSqlSelect("sign_comment_widget_ids");
		ActAljoinTaskAssignee actAljoinTaskAssignee =
			actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

		// 保存评论数据
		String thisTaskUserComment = param.get("thisTaskUserComment");
		Comment thisTaskUserCommentObj = null;

		// 设置评论用户
		Authentication.setAuthenticatedUserId(userId.toString());
		if (StringUtils.isEmpty(thisTaskUserComment)) {
			thisTaskUserComment = "";
		}

		thisTaskUserCommentObj =
			taskService.addComment(task.getId(), instance.getId(), thisTaskUserComment);
		thisTaskUserComment = thisTaskUserComment + "(" + fullName + " "
			+ DateUtil.datetime2str(thisTaskUserCommentObj.getTime()) + ")";

		// 一次获取获取最新的评论控件内容
		Map<String, String> commentKeyValueMap = new HashMap<String, String>();
		Where<ActAljoinFormDataRun> runDataWhere = new Where<ActAljoinFormDataRun>();
		runDataWhere.eq("proc_inst_id", task.getProcessInstanceId());
		runDataWhere.in("form_widget_id", actAljoinTaskAssignee.getSignCommentWidgetIds().split(","));
		List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService.selectList(runDataWhere);
		for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
			commentKeyValueMap.put(actAljoinFormDataRun.getFormWidgetId(), actAljoinFormDataRun.getFormWidgetValue());
		}

		// 把评论数据保存到对应的评论控件中
		for (Map.Entry<String, String> map : paramMap.entrySet()) {
			String formWidgetId = map.getKey();
			String formWidgetVal = map.getValue();
			if ((com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(actAljoinTaskAssignee.getSignCommentWidgetIds()))
				&& (actAljoinTaskAssignee.getSignCommentWidgetIds().indexOf(formWidgetId) != -1)
				&& (com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(thisTaskUserComment))) {
				// 获取最新的评论控件内容
				if(com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(commentKeyValueMap.get(formWidgetId))){
					formWidgetVal = commentKeyValueMap.get(formWidgetId);
				}
				if (com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(formWidgetVal)) {
					paramMap.put(formWidgetId, formWidgetVal + "\n" + thisTaskUserComment);
				} else {
					paramMap.put(formWidgetId, thisTaskUserComment);
				}
			}
		}

		// 判断当前节点是否多实例节点
		MultiInstanceLoopCharacteristics currentMulti =
			activitiService.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());

		List<Long> uidList = new ArrayList<Long>();
		// 分配任务的人员
		List<String> assigneeList = Arrays.asList(userIds.split(";"));
		for(String assignee : assigneeList){
			uidList.add(Long.parseLong(assignee));
			taskAuth += assignee+"#";
		}

		if (currentMulti != null) {

			ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(task.getProcessDefinitionId());

			uidList.add(Long.valueOf(task.getAssignee()));
			Where<AutUser> userWhere = new Where<AutUser>();
			userWhere.setSqlSelect("id,user_name,full_name");
			userWhere.in("id",uidList);
			List<AutUser> userList = autUserService.selectList(userWhere);

			Map<Long,String> userMap = new HashMap<Long,String>();
			for(AutUser autUser : userList){
				userMap.put(autUser.getId(),autUser.getFullName());
			}

			//加签
			actTaskAddSignService.addSignTasksAfter(task,userId.toString(),definitionEntity,assigneeList,bpmnRun,userMap,uidList,1);
		}

		taskAuth = taskAuth.substring(0,taskAuth.lastIndexOf("#"));

		// 更新表单数据
		actAljoinFormDataDraftService.updateFormData(paramMap, instance, entity, null, null,task.getTaskDefinitionKey());

		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("instance", instance);
		retMap.put("bpmn", bpmnRun);
		retMap.put("runForm", runForm);
		retMap.put("taskAuth",taskAuth);
		return retMap;
	}

}
