package aljoin.web.service.pub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.mapper.PubPublicInfoMapper;
import aljoin.pub.dao.object.PubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoDraftService;
import aljoin.pub.iservice.PubPublicInfoReadService;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;

/**
 * 
 * 公共信息表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-12
 */
@Service
public class PubPublicInfoWebService {

	@Resource
	private PubPublicInfoMapper mapper;
	@Resource
	private ResResourceService resResourceService;
	@Resource
	private ActActivitiService activitiService;
	@Resource
	private TaskService taskService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private PubPublicInfoDraftService pubPublicInfoDraftService;
	@Resource
	private PubPublicInfoReadService pubPublicInfoReadService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private ActActivitiWebService actActivitiWebService;
	@Resource
	private PubPublicInfoService pubPublicInfoService;
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private HistoryService historyService;
	@Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
	
	@Transactional
	public RetMsg add(PubPublicInfoVO obj, ActAljoinBpmn bpmn) throws Exception {
		RetMsg retMsg = new RetMsg();
		Map<String, String> map = new HashMap<String, String>();
		if (null != obj) {
			if (StringUtils.isEmpty(obj.getNoticeObjId())) {
				obj.setNoticeObjId("");
			}
			if (StringUtils.isEmpty(obj.getNoticeObjName())) {
				obj.setNoticeObjName("");
			}
			PubPublicInfo pubPublicInfo = new PubPublicInfo();
			Long objId = obj.getId();
			obj.setId(null);
			BeanUtils.copyProperties(obj, pubPublicInfo);
			if (null != pubPublicInfo) {
				DateTime begTime = new DateTime(new Date());
				pubPublicInfo.setPeriodBeginTime(begTime.toDate());
				if (null != obj.getPeriod()) {
					DateTime endTime = begTime.plusDays(obj.getPeriod());
					pubPublicInfo.setPeriodEndTime(endTime.toDate());
				}
				pubPublicInfo.setIsActive(1);
				pubPublicInfo.setPeriodStatus(0);
				// 不走流程直接把审核通过
				if (pubPublicInfo.getProcessId() == null) {
					pubPublicInfo.setAuditStatus(3);
				} else {
					pubPublicInfo.setAuditStatus(1);
				}
				pubPublicInfoService.insert(pubPublicInfo);

				// 如果是草稿编辑提交-需要删除草稿
				if (null != objId) {
					// 草稿箱的附件要挪到发布的那边
					Where<ResResource> attwhere = new Where<ResResource>();
					attwhere.eq("biz_id", objId);
					List<ResResource> oldAttList = resResourceService.selectList(attwhere);
					if (null != oldAttList && !oldAttList.isEmpty()) {
						for (ResResource att : oldAttList) {
							att.setBizId(pubPublicInfo.getId());
						}
						resResourceService.updateBatchById(oldAttList);
					}
					pubPublicInfoDraftService.deleteById(objId);
				}
				// 公告已读未读数据维护
				if (pubPublicInfo.getProcessId() == null) {
					String noticeObjId = pubPublicInfo.getNoticeObjId();
					List<String> objIdList = Arrays.asList(noticeObjId.split(";"));
					if (!objIdList.isEmpty()) {
						String noticeObjName = pubPublicInfo.getNoticeObjName();
						List<String> objNameList = Arrays.asList(noticeObjName.split(";"));
						pubPublicInfoReadService.insertList4pub(objIdList, pubPublicInfo.getId(), objNameList);
					}

				}
				//保存附件
                List<ResResource> resResourceList = obj.getResResourceList();
                List<Long> ids = new ArrayList<Long>();
                if(null != resResourceList && !resResourceList.isEmpty()){
                    for (ResResource resResource : resResourceList) {
                        ids.add(resResource.getId());
                    }
                    Where<ResResource> resResourceWhere = new Where<ResResource>();
                    resResourceWhere.in("id", ids);
                    resResourceList = resResourceService.selectList(resResourceWhere);
                    for (ResResource resResource : resResourceList) {
                        resResource.setBizId(pubPublicInfo.getId());
                        resResource.setFileDesc("新增公共信息附件上传");
                    }
                    resResourceService.updateBatchById(resResourceList);
                }
				// 启动流程
				if (null != bpmn) {
					if (null != pubPublicInfo.getCreateUserId()) {
						Map<String, String> param = new HashMap<String, String>();
						param.put("bizType", "pub");
						param.put("bizId", pubPublicInfo.getId() + "");
						param.put("isUrgent", "1");
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
						ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
								pubPublicInfo.getCreateUserId());
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
						userWhere.eq("id", pubPublicInfo.getCreateUserId());
						AutUser user = autUserService.selectOne(userWhere);
						actActivitiWebService.insertProcessQuery(instance, "1", null, pubPublicInfo.getTitle(),
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
						taskService.claim(task.getId(), String.valueOf(pubPublicInfo.getCreateUserId()));
						String userFullName = "";
						map.put("signInTime", DateUtil.datetime2str(new Date()));
						if (null != task.getAssignee()) {
							userFullName = autUserService.selectById(task.getAssignee()).getFullName();
						}
						actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
						//记录流程日志
						Map<String,Object> logMap = new HashMap<String,Object>();  
	                    logMap.put("instance", instance);
	                    logMap.put("preTask", task);
	                    actActivitiWebService.insertOrUpdateLog(logMap, user.getId());
//				        actActivitiWebService.insertOrUpdateActivityLog(instance.getId(),WebConstant.PROCESS_OPERATE_STATUS_1,obj.getCreateUserId(),null);
						
					}
				}
			}
		}
		retMsg.setCode(0);
		retMsg.setObject(map);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Transactional
	public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId,
														 String message, AutUser createUser) throws Exception {
		RetMsg retMsg = new RetMsg();
		Task tsk = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(null == tsk){
		  retMsg.setCode(1);
		  retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
		  return retMsg;
		}
		String processInstanceId = tsk.getProcessInstanceId();
		Map<String,Object> logMap = new HashMap<String,Object>();  
		logMap.put("processInstanceId", processInstanceId);
		//添加意见
		Authentication.setAuthenticatedUserId(String.valueOf(createUser.getId()));
		taskService.addComment(taskId, processInstanceId, message);
		taskService.setAssignee(taskId, createUser.getId().toString());
		activitiService.completeTask(variables, taskId);
		List<String> userIdList = new ArrayList<String>();
		logMap.put("commont", message);
        logMap.put("preTask", tsk);
		// 环节跳转
		Date date = new Date();
		if (StringUtils.isNotEmpty(bizId)) {
			PubPublicInfo publicInfo = pubPublicInfoService.selectById(Long.parseLong(bizId));
			if (null != publicInfo) {
				if (null != publicInfo.getPeriodEndTime()) {
					DateTime endTime = new DateTime(publicInfo.getPeriodEndTime());
					DateTime dateTime = new DateTime(date);
					if (dateTime.getMillis() > endTime.getMillis()) {
						publicInfo.setPeriodStatus(1);// 已失效
					}
				}

				List<HistoricTaskInstance> instanceList = historyService.createHistoricTaskInstanceQuery()// 获取第一个任务节点
					.processInstanceId(processInstanceId).unfinished().list();
				if (null != instanceList && instanceList.size() == 0) {
					// 如果是结束节点，则记录最后一个办理人
					actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
					publicInfo.setAuditStatus(3);// 审核通过
					publicInfo.setAuditTime(date);
					publicInfo.setAuditReason("审核通过");
					pubPublicInfoService.updateById(publicInfo);
					String noticeObjId = publicInfo.getNoticeObjId();
					List<String> objIdList = Arrays.asList(noticeObjId.split(";"));
					if (!objIdList.isEmpty()) {
						// 顺便在维护公告已读未读数据
						String noticeObjName = publicInfo.getNoticeObjName();
						List<String> objNameList = Arrays.asList(noticeObjName.split(";"));
						pubPublicInfoReadService.insertList4pub(objIdList, publicInfo.getId(), objNameList);
					}
				}
			}

			List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
			if (null != taskList && !taskList.isEmpty()) {
				Task task = taskList.get(0);
				if (null != task && StringUtils.isNotEmpty(userId)) {
					userIdList = Arrays.asList(userId.split(";"));
					if (null != userIdList && !userIdList.isEmpty()) {
						// （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
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
//						actAljoinQueryService.cleanQureyCurrentUser(task.getId());
					}
				}
			}
		}
		logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
        actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
		//记录流程日志
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
		// 环节办理人待办数量-1
		String handle = "";
		// 下一环节办理人待办数量+1
		if (null != userIdList && !userIdList.isEmpty()) {
			handle = StringUtil.list2str(userIdList, ";");
		}
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("processInstanceId", processInstanceId);//流程实例id
		map.put("handle", handle);//下一级办理人
		retMsg.setObject(map);//返回给controller异步调用在线消息

		retMsg.setCode(0);
		retMsg.setMessage("操作成功!");
		return retMsg;
	}

	@Transactional
	public RetMsg jump2Task2(String taskId, String bizId, String message, Long createUserId) throws Exception {
		RetMsg retMsg = new RetMsg();
		Task tsk = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = tsk.getProcessInstanceId();
		List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
		List<String> assignees = new ArrayList<String>();
	    Map<String,Object> logMap = new HashMap<String,Object>();  
	    logMap.put("preTask", tsk);
	    logMap.put("processInstanceId", processInstanceId);
		if (org.apache.commons.lang3.StringUtils.isNotEmpty(processInstanceId) && null != currentTask
			&& !currentTask.isEmpty()) {
			String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
			Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
			taskService.addComment(taskId, processInstanceId, message);
			logMap.put("commont", message);
			if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
				List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
				if (null != preList && !preList.isEmpty()) {

					String targetTaskKey = preList.get(0).getKey();
					if (org.apache.commons.lang3.StringUtils.isNotEmpty(targetTaskKey)) {
						List<HistoricTaskInstance> historicList = historyService.createHistoricTaskInstanceQuery()
							.processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey)
							.orderByHistoricTaskInstanceEndTime().desc().finished().list();
						if(historicList.isEmpty()){
							retMsg.setCode(1);
							retMsg.setMessage("历史节点获取失败");
							return retMsg;
						}
						// 理论上historicList永远不为空，为了避免勿操作，判断一下
						HistoricTaskInstance historic = historicList.get(0);
						assignees.add(historic.getAssignee());

						activitiService.jump2Task2(targetTaskKey, processInstanceId);
						Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
							.taskDefinitionKey(targetTaskKey).singleResult();
						// （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
						actFixedFormService.deleteOrgnlTaskAuth(task);
						HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
						if (null != hisTask.getClaimTime()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
							taskService.unclaim(task.getId());
						}
						HashSet<Long> userIdSet = new HashSet<Long>();
						for (String assignee : assignees) {
						    userIdSet.add(Long.valueOf(assignee));
							taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
						}
//						actAljoinQueryService.cleanQureyCurrentUser(task.getId());
						Where<AutUser> uwhere = new Where<AutUser>();
                        uwhere.setSqlSelect("id,full_name");
                        uwhere.in("id", userIdSet);
                        List<AutUser> userList = autUserService.selectList(uwhere);
                        String userName = "";
                        for (AutUser autUser : userList) {
                          userName += autUser.getFullName() + ";";
                        }
                        actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
                        //记录流程日志
                        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                        taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
                        logMap.put("taskKeyUserMap", taskKeyMap);
                        logMap.put("userTask", task);
                        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
                        actActivitiWebService.insertOrUpdateLog(logMap, createUserId);
                        actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
//                        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,WebConstant.PROCESS_OPERATE_STATUS_2,createUserId,null);
					}
				}
			}
		}
		String handle = "";
		// 下一环节办理人待办数量+1
		if (null != assignees && !assignees.isEmpty()) {
			handle = StringUtil.list2str(assignees, ";");
		}
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("processInstanceId", processInstanceId);//流程实例id
		map.put("handle", handle);//下一级办理人
		retMsg.setObject(map);//返回给controller异步调用在线消息

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
    @Transactional
    public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId, String userId,
                                                         String message, AutUser createUser) throws Exception {
        RetMsg retMsg = new RetMsg();
        Task tsk = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null == tsk){
          retMsg.setCode(1);
          retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
          return retMsg;
        }
        Map<String,Object> logMap = new HashMap<String,Object>();  
        String processInstanceId = tsk.getProcessInstanceId();
        logMap.put("processInstanceId", processInstanceId);
        //添加意见
        Authentication.setAuthenticatedUserId(String.valueOf(createUser.getId()));
        taskService.addComment(taskId, processInstanceId, message);
        taskService.setAssignee(taskId, createUser.getId().toString());
        activitiService.completeTask(variables, taskId);
        logMap.put("commont", message);
        logMap.put("preTask", tsk);
        List<String> userIdList = new ArrayList<String>();
        // 环节跳转
        Date date = new Date();
        if (StringUtils.isNotEmpty(bizId)) {
            PubPublicInfo publicInfo = pubPublicInfoService.selectById(Long.parseLong(bizId));
            if (null != publicInfo) {
                if (null != publicInfo.getPeriodEndTime()) {
                    DateTime endTime = new DateTime(publicInfo.getPeriodEndTime());
                    DateTime dateTime = new DateTime(date);
                    if (dateTime.getMillis() > endTime.getMillis()) {
                        publicInfo.setPeriodStatus(1);// 已失效
                    }
                }

                List<HistoricTaskInstance> instanceList = historyService.createHistoricTaskInstanceQuery()// 获取第一个任务节点
                    .processInstanceId(processInstanceId).unfinished().list();
                if (null != instanceList && instanceList.size() == 0) {
                    // 如果是结束节点，则记录最后一个办理人
                    actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
                    publicInfo.setAuditStatus(3);// 审核通过
                    publicInfo.setAuditTime(date);
                    publicInfo.setAuditReason("审核通过");
                    pubPublicInfoService.updateById(publicInfo);
                    String noticeObjId = publicInfo.getNoticeObjId();
                    List<String> objIdList = Arrays.asList(noticeObjId.split(";"));
                    logMap.put("nextNode", "EndEvent_");
                    if (!objIdList.isEmpty()) {
                        // 顺便在维护公告已读未读数据
                        String noticeObjName = publicInfo.getNoticeObjName();
                        List<String> objNameList = Arrays.asList(noticeObjName.split(";"));
                        pubPublicInfoReadService.insertList4pub(objIdList, publicInfo.getId(), objNameList);
                    }
                }
            }

            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
            if (null != taskList && !taskList.isEmpty()) {
                Task task = taskList.get(0);
                if (null != task && org.apache.commons.lang.StringUtils.isNotEmpty(userId)) {
                    userIdList = Arrays.asList(userId.split(";"));
                    if (null != userIdList && !userIdList.isEmpty()) {
                        // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                        actFixedFormService.deleteOrgnlTaskAuth(task);
                        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                        if (null != hisTask.getClaimTime()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                            taskService.unclaim(task.getId());
                        }
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
            //act
        }
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
        actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
        //记录流程日志
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
        String handle = "";
        if (null != userIdList && !userIdList.isEmpty()) {
            handle = StringUtil.list2str(userIdList, ";");
        }
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("processInstanceId", processInstanceId);//流程实例id
        map.put("handle", handle);//下一级办理人
        retMsg.setObject(map);//返回给controller异步调用在线消息
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功!");
        return retMsg;
    }
    
    @Transactional
    public RetMsg jump2AppTask2(String taskId, String message, Long createUserId) throws Exception {
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
            Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
            taskService.addComment(taskId, processInstanceId, message);
            logMap.put("commont", message);
            if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
                List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
                if (null != preList && !preList.isEmpty()) {

                    String targetTaskKey = preList.get(0).getKey();
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(targetTaskKey)) {
                        List<HistoricTaskInstance> historicList = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey)
                            .orderByHistoricTaskInstanceEndTime().desc().finished().list();
                        if(historicList.isEmpty()){
                            retMsg.setCode(1);
                            retMsg.setMessage("历史节点获取失败");
                            return retMsg;
                        }
                        // 理论上historicList永远不为空，为了避免勿操作，判断一下
                        HistoricTaskInstance historic = historicList.get(0);
                        assignees.add(historic.getAssignee());

                        activitiService.jump2Task2(targetTaskKey, processInstanceId);
                        Task task = taskService.createTaskQuery().processInstanceId(processInstanceId)
                            .taskDefinitionKey(targetTaskKey).singleResult();
                        // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                        actFixedFormService.deleteOrgnlTaskAuth(task);
                        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                        if (null != hisTask.getClaimTime()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                            taskService.unclaim(task.getId());
                        }
                        HashSet<Long> userIdSet = new HashSet<Long>();
                        for (String assignee : assignees) {
                        	userIdSet.add(Long.valueOf(assignee));
                            taskService.addUserIdentityLink(task.getId(), assignee, "candidate");
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
			            taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
			            logMap.put("taskKeyUserMap", taskKeyMap);
			            logMap.put("userTask", task);
			            logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
			            actActivitiWebService.insertOrUpdateLog(logMap, createUserId);
			            actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
						// 记录流程日志
//						actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,
//								WebConstant.PROCESS_OPERATE_STATUS_2, createUserId,null);
					 }
                }
            }
        }
        String handle = "";
        // 环节办理人待办数量-1
        // 下一环节办理人待办数量+1
        if (null != assignees && !assignees.isEmpty()) {
            handle = StringUtil.list2str(assignees, ";");
        }
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("processInstanceId", processInstanceId);//流程实例id
        map.put("handle", handle);//下一级办理人
        retMsg.setObject(map);//返回给controller异步调用在线消息

        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
