package aljoin.ioa.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.iservice.AutDepartmentService;
import org.activiti.bpmn.model.MultiInstanceLoopCharacteristics;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.el.JuelExpression;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinBpmnUser;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.act.dao.entity.ActHiTaskinst;
import aljoin.act.dao.entity.ActRuIdentitylink;
import aljoin.act.dao.entity.ActRuTask;
import aljoin.act.dao.object.SimpleDeptVO;
import aljoin.act.dao.object.SimpleUserVO;
import aljoin.act.dao.object.TaskVO;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinActivityLogService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.act.iservice.ActAljoinDelegateInfoService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.iservice.ActAljoinTaskAssigneeService;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.act.iservice.ActHiTaskinstService;
import aljoin.act.iservice.ActRuIdentitylinkService;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.act.util.ActUtil;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.config.AljoinSetting;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaCircula;
import aljoin.ioa.dao.object.AllTaskShowVO;
import aljoin.ioa.dao.object.AppWaitTaskDO;
import aljoin.ioa.dao.object.AppWaitTaskVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.ioa.iservice.IoaCirculaService;
import aljoin.ioa.iservice.IoaWaitingWorkService;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.object.AppConstant;
import aljoin.object.CustomerTaskDefinition;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;

/**
 * 待办工作(服务实现类).
 *
 * @author：pengsp @date： 2017-10-19
 */
@Service
public class IoaWaitingWorkServiceImpl implements IoaWaitingWorkService {

    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private ManagementService managementService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActAljoinCategoryService actAljoinCategoryService;
    @Resource
    private HistoryService historyService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private ActAljoinBpmnUserService actAljoinBpmnUserService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinDelegateInfoService actAljoinDelegateInfoService;
    @Resource
    private IdentityService identityService;
    @Resource
    private ActAljoinFixedConfigService actAljoinFixedConfigService;
    @Resource
    private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
    @Resource
    private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
    @Resource
    private ActRuTaskService actRuTaskService;
    @Resource
    private AljoinSetting aljoinSetting;
    @Resource
    private ActHiTaskinstService actHiTaskinstService;
    @Resource
    private ActFixedFormServiceImpl actFixedFormService;
    @Resource
	private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private ActAljoinActivityLogService actAljoinActivityLogService;
    @Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
    @Resource
    private ActRuIdentitylinkService actRuIdentitylinkService;
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    @Resource
    private IoaCirculaService ioaCirculaService;
    @Resource
	private AutDepartmentService autDepartmentService;

    /*
     * 构造待办查询条件
     */
    public int makeQueryWhere(Where<ActAljoinQuery> where, WaitTaskShowVO obj) {
        int flag = 0;
        if (!StringUtils.isEmpty(obj.getTitle())) {
            where.like("process_title", obj.getTitle());
            flag++;
        }

		if (!StringUtils.isEmpty(obj.getUrgency())) {
			where.eq("urgent_status", obj.getUrgency());
			flag++;
		}
		if (!StringUtils.isEmpty(obj.getReferenceNumber())) {
			where.eq("reference_number", obj.getReferenceNumber());
			flag++;
		}
		if (!StringUtils.isEmpty(obj.getSerialNumber())) {
			where.eq("getSerialNumber", obj.getSerialNumber());
			flag++;
		}

		if (!StringUtils.isEmpty(obj.getFounder())) {
			where.like("create_full_user_name", obj.getFounder());
			flag++;
		}

		if (!StringUtils.isEmpty(obj.getFormType())) {
			where.like("process_category_ids", obj.getFormType());
			flag++;
		}

		/*
		 * if (obj.getFillingDate() != null) { SimpleDateFormat str = new
		 * SimpleDateFormat("yyyy-MM-dd"); String fillingDate =
		 * str.format(obj.getFillingDate()); where.like("start_time",
		 * fillingDate); flag++; }
		 */

		if (!StringUtils.isEmpty(obj.getProcessName())) {
			where.like("process_name", obj.getProcessName());
			flag++;
		}

		return flag;
	}

	@Override
	public Page<WaitTaskShowVO> list(PageBean pageBean, String userId, WaitTaskShowVO obj) throws Exception {
		Page<WaitTaskShowVO> page = new Page<WaitTaskShowVO>();
		List<WaitTaskShowVO> voList = new ArrayList<WaitTaskShowVO>();

		// 到流程查询表查询满足条件的流程实例ID
		Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
		int flag = makeQueryWhere(where, obj);

		List<ActAljoinQuery> queryList = new ArrayList<ActAljoinQuery>();
		List<String> processInstanceIds = new ArrayList<String>();
		// 如果有查询条件，把查询流程表的数据保存到map中（后面需要用到，不用重复查询）
		Map<String, ActAljoinQuery> actAljoinQueryMap = new HashMap<String, ActAljoinQuery>();
		if (flag > 0) {
			where.setSqlSelect(
					"process_instance_id,urgent_status,process_title,current_handle_full_user_name,start_time,create_full_user_name,process_category_ids");
			queryList = actAljoinQueryService.selectList(where);
			for (ActAljoinQuery query : queryList) {
				processInstanceIds.add(query.getProcessInstanceId());
				actAljoinQueryMap.put(query.getProcessInstanceId(), query);
			}
		}

		String processId = "";
		if (flag > 0 && processInstanceIds.size() <= 0) {
			return page;
		}
		// 构造查询条件
		ActRuTask actRuTask = new ActRuTask();
		if (!StringUtils.isEmpty(userId)) {
			actRuTask.setAssignee(userId);
		}
		if (!StringUtils.isEmpty(processId)) {
			actRuTask.setProcDefId(processId);
		}
		// 当流程id为空时，要返回一个空的page
		if (processInstanceIds.size() > 0) {
			actRuTask.setProcessInstanceIds(processInstanceIds);
		}

		if (null != obj.getReceiveBegTime()) {
			actRuTask.setCreateBegTime(obj.getReceiveBegTime());
		}
		if (null != obj.getReceiveEndTime()) {
			actRuTask.setCreateEndTime(obj.getReceiveEndTime());
		}
		actRuTask.setDbType(aljoinSetting.getDbType());

		// 查询出自己作为候选或者已经签收的任务的分页信息
		Page<ActRuTask> ruTaskPage = actRuTaskService.list(pageBean, actRuTask);
		// 待办任务记录总数
		int count = pageBean.getIsSearchCount().intValue() == 1 ? ruTaskPage.getTotal() : 0;
		// 待办任务列表
		List<ActRuTask> taskList = ruTaskPage.getRecords();

		Map<String, HistoricProcessInstance> historicProcessInstanceMap = new HashMap<String, HistoricProcessInstance>();
		Map<String, String> instanceIdCategoryNameMap = new HashMap<String, String>();
		Map<String, String> taskIdDelegateNameMap = new HashMap<String, String>();
		Map<String, HistoricTaskInstance> taskIdTaskInstanceMap = new HashMap<String, HistoricTaskInstance>();
		Map<String, String> taskIdPreHandleUserName = new HashMap<String, String>();
		Map<String, Set<Long>> taskIdPreHandleUserIdMap = new HashMap<String, Set<Long>>();
		Map<String, String> logMap = new HashMap<String, String>();
		Map<String, Long> lastMap = new HashMap<String, Long>();
		Map<String, String> userIdAndNameMap = new HashMap<String, String>();
		Set<Long> allUserIdSet = new HashSet<Long>();
		if (taskList.size() > 0) {
			// 查询任务的所有流程实例
			Set<String> processInstanceIdSet = new HashSet<String>();
			List<String> processInstanceIdList = new ArrayList<String>();
			List<String> taskIdList = new ArrayList<String>();

			for (ActRuTask task : taskList) {
				processInstanceIdSet.add(task.getProcInstId());
				taskIdList.add(task.getId());
				processInstanceIdList.add(task.getProcInstId());

				// 获取上级办理人
				List<TaskDefinition> defList = actActivitiService.getPreTaskInfo2(task.getTaskDefKey(),
						task.getProcInstId());

				Set<Long> uIdSet = new HashSet<Long>();
				// 对于自由流程，取defList的时候会返回assignee
				for (TaskDefinition def : defList) {
					if (def.getAssigneeExpression() != null
							&& !StringUtils.isEmpty(def.getAssigneeExpression().getExpressionText())) {
						uIdSet.add(Long.parseLong(def.getAssigneeExpression().getExpressionText()));
						allUserIdSet.add(Long.parseLong(def.getAssigneeExpression().getExpressionText()));
					}
				}
				/*
				 * if (uIdSet.size() == 0) { // 如果defList已经待会了办理人信息，就不需要再次查询
				 * List<String> allKeyList = new ArrayList<String>(); for
				 * (TaskDefinition taskDefinition : defList) {
				 * allKeyList.add(taskDefinition.getKey()); }
				 * 
				 * HistoricActivityInstanceQuery query =
				 * historyService.createHistoricActivityInstanceQuery()
				 * .processInstanceId(task.getProcInstId()).finished().
				 * activityType("userTask")
				 * .orderByHistoricActivityInstanceEndTime().desc();
				 * List<HistoricActivityInstance> actList = new
				 * ArrayList<HistoricActivityInstance>();
				 * 
				 * if (defList.size() == 1) { actList =
				 * query.activityId(defList.get(0).getKey()).listPage(0, 1); }
				 * else if (defList.size() > 1) { actList = query.list();
				 * List<HistoricActivityInstance> newActList = new
				 * ArrayList<HistoricActivityInstance>(); for
				 * (HistoricActivityInstance hai : actList) { if
				 * (allKeyList.contains(hai.getActivityId())) {
				 * newActList.add(hai); } } actList = newActList; } else { //
				 * 回退到第一个节点的时候 actList = query.listPage(0, 1); }
				 * 
				 * for (HistoricActivityInstance historicActivityInstance :
				 * actList) { if
				 * (!StringUtils.isEmpty(historicActivityInstance.getAssignee())
				 * ) { uIdSet.add(Long.parseLong(historicActivityInstance.
				 * getAssignee()));
				 * allUserIdSet.add(Long.parseLong(historicActivityInstance.
				 * getAssignee())); } } }
				 */
				taskIdPreHandleUserIdMap.put(task.getId(), uIdSet);
			}

			if (taskIdList != null && taskIdList.size() > 0) {
				Where<ActAljoinActivityLog> actLogWhere = new Where<ActAljoinActivityLog>();
				actLogWhere.in("current_task_id", taskIdList);
				actLogWhere.setSqlSelect("current_task_id,last_task_id");
				List<ActAljoinActivityLog> logList = actAljoinActivityLogService.selectList(actLogWhere);
				String lastTask = "";
				if (logList != null && logList.size() > 0) {

					for (ActAljoinActivityLog actAljoinActivityLog : logList) {
						lastTask = actAljoinActivityLog.getLastTaskId() + ",";
						logMap.put(actAljoinActivityLog.getCurrentTaskId(), actAljoinActivityLog.getLastTaskId());
					}
					if (lastTask != null && "".equals(lastTask)) {
						Where<ActAljoinActivityLog> lastLogWhere = new Where<ActAljoinActivityLog>();
						actLogWhere.in("current_task_id", lastTask);
						actLogWhere.setSqlSelect("current_task_id,last_task_id");
						List<ActAljoinActivityLog> lastLogList = actAljoinActivityLogService.selectList(lastLogWhere);

						for (ActAljoinActivityLog actAljoinActivityLog : lastLogList) {
							allUserIdSet.add(Long.valueOf(actAljoinActivityLog.getOperateUserId()));
							lastMap.put(actAljoinActivityLog.getCurrentTaskId(),
									Long.valueOf(actAljoinActivityLog.getOperateUserId()));
						}
					}
				}
			}
			// 一次查询所有上次办理人
			if (allUserIdSet.size() > 0) {
				Where<AutUser> autUserWhere = new Where<AutUser>();
				autUserWhere.in("id", allUserIdSet);
				autUserWhere.setSqlSelect("id,full_name");
				List<AutUser> autUserList = autUserService.selectList(autUserWhere);

				for (AutUser autUser : autUserList) {
					userIdAndNameMap.put(autUser.getId().toString(), autUser.getFullName());
				}
				for (Map.Entry<String, Set<Long>> entry : taskIdPreHandleUserIdMap.entrySet()) {
					Set<Long> set = entry.getValue();
					String userFullNames = "";
					for (Long uid : set) {
						userFullNames += userIdAndNameMap.get(uid.toString()) + ",";
					}
					if (!"".equals(userFullNames)) {
						userFullNames = userFullNames.substring(0, userFullNames.length() - 1);
					}
					taskIdPreHandleUserName.put(entry.getKey(), userFullNames);
				}

			}

			List<HistoricProcessInstance> historicProcessInstanceList = historyService
					.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();

			for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
				historicProcessInstanceMap.put(historicProcessInstance.getId(), historicProcessInstance);
			}
			// 如果上的查询条件没有流程查询表的数据，在去取，如果上面的流程查询map中有数据，则不需要在去查询
			if (actAljoinQueryMap.size() == 0) {
				Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
				queryWhere.setSqlSelect(
						"process_instance_id,urgent_status,process_title,current_handle_full_user_name,start_time,create_full_user_name,process_category_ids");
				queryWhere.in("process_instance_id", processInstanceIdSet);
				List<ActAljoinQuery> actAljoinQueryList = actAljoinQueryService.selectList(queryWhere);
				for (ActAljoinQuery actAljoinQuery : actAljoinQueryList) {
					actAljoinQueryMap.put(actAljoinQuery.getProcessInstanceId(), actAljoinQuery);
				}
			}
			// 一次性获取流程分类
			List<Long> categoryIdList = new ArrayList<Long>();

			for (Map.Entry<String, ActAljoinQuery> entry : actAljoinQueryMap.entrySet()) {
				ActAljoinQuery aq = entry.getValue();
				String categoryIds = aq.getProcessCategoryIds();
				if (!StringUtils.isEmpty(categoryIds)) {
					String[] categoryIdsArr = categoryIds.split(",");
					categoryIds = categoryIdsArr[categoryIdsArr.length - 1];
					instanceIdCategoryNameMap.put(entry.getKey(), categoryIds);
					categoryIdList.add(Long.parseLong(categoryIds));
				}

			}
			Where<ActAljoinCategory> actAljoinCategoryWhere = new Where<ActAljoinCategory>();
			actAljoinCategoryWhere.in("id", categoryIdList);
			actAljoinCategoryWhere.setSqlSelect("id,category_name");
			List<ActAljoinCategory> actAljoinCategoryList = actAljoinCategoryService.selectList(actAljoinCategoryWhere);
			for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
				for (Map.Entry<String, String> entry : instanceIdCategoryNameMap.entrySet()) {
					if ((actAljoinCategory.getId().toString()).equals(entry.getValue())) {
						instanceIdCategoryNameMap.put(entry.getKey(), actAljoinCategory.getCategoryName());
					}
				}
			}

			// 根据任务ID获取委托人信息
			Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
			actAljoinDelegateInfoWhere.in("task_id", taskIdList);
			actAljoinDelegateInfoWhere.eq("assignee_user_id", userId);
			actAljoinDelegateInfoWhere.setSqlSelect("task_id,delegate_user_names");
			List<ActAljoinDelegateInfo> actAljoinDelegateInfoList = actAljoinDelegateInfoService
					.selectList(actAljoinDelegateInfoWhere);
			for (ActAljoinDelegateInfo actAljoinDelegateInfo : actAljoinDelegateInfoList) {
				String mapValue = taskIdDelegateNameMap.get(actAljoinDelegateInfo.getTaskId());
				if (StringUtils.isEmpty(mapValue)) {
					taskIdDelegateNameMap.put(actAljoinDelegateInfo.getTaskId(),
							actAljoinDelegateInfo.getDelegateUserNames());
				} else {
					mapValue = mapValue + "," + actAljoinDelegateInfo.getDelegateUserNames();
					taskIdDelegateNameMap.put(actAljoinDelegateInfo.getTaskId(), mapValue);
				}
			}
			for (Map.Entry<String, String> entry : taskIdDelegateNameMap.entrySet()) {
				String key = entry.getKey();
				String[] delegateInfoArr = taskIdDelegateNameMap.get(key).split(",");
				List<String> userNameList = new ArrayList<String>();
				for (int i = 0; i < delegateInfoArr.length; i++) {
					if (!userNameList.contains(delegateInfoArr[i])) {
						userNameList.add(delegateInfoArr[i]);
					}
				}
				int n = 0;
				String newDelegateInfo = "";
				for (String s : userNameList) {
					if (n == 0) {
						newDelegateInfo = s;
					} else {
						newDelegateInfo += "," + s;
					}
					n++;
				}
				if (!"".equals(newDelegateInfo)) {
					newDelegateInfo = "[委托人(" + newDelegateInfo + ")]";
				}
				taskIdDelegateNameMap.put(key, newDelegateInfo);
			}
			// 查询所有任务节点信息
			List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
					.processInstanceIdIn(processInstanceIdList).list();
			for (HistoricTaskInstance historicTaskInstance : taskInstanceList) {
				taskIdTaskInstanceMap.put(historicTaskInstance.getId(), historicTaskInstance);
			}

		}

		for (ActRuTask task : taskList) {
			// 根据流程实例ID获取待办流程实例
			HistoricProcessInstance historicProcessInstance = historicProcessInstanceMap.get(task.getProcInstId());
			// 从上面的map中获取ActAljoinQuery
			ActAljoinQuery actAljoinQuery = actAljoinQueryMap.get(task.getProcInstId());

			if (null != actAljoinQuery) {
				WaitTaskShowVO waitTaskShowVO = new WaitTaskShowVO();
				waitTaskShowVO.setTaskId(task.getId());
				waitTaskShowVO.setFormType(instanceIdCategoryNameMap.get(task.getProcInstId()));// 流程分类
				String str = "";
				if(null != historicProcessInstance){
					str = historicProcessInstance.getBusinessKey();
				}
				waitTaskShowVO.setBusinessKey(str);

				waitTaskShowVO.setUrgency(actAljoinQuery.getUrgentStatus());// 缓急

				Integer uegency = 1;
				if (actAljoinQuery.getUrgentStatus().equals(WebConstant.COMMONLY)) {
					uegency = 1;
				} else if (actAljoinQuery.getUrgentStatus().equals(WebConstant.URGENT)) {
					uegency = 2;
				} else if (actAljoinQuery.getUrgentStatus().equals(WebConstant.ADD_URENT)) {
					uegency = 3;
				}

				waitTaskShowVO.setTitle(actAljoinQuery.getProcessTitle());// 标题
				waitTaskShowVO.setFounder(actAljoinQuery.getCreateFullUserName());// 流程发起人
				waitTaskShowVO.setUrgencyStatus(uegency);// 缓急转换为数字 用于排序

				waitTaskShowVO.setFillingDate(task.getCreateTime());// 任务开始时间

				// waitTaskShowVO.setFormerManager(actAljoinQuery.getCurrentHandleFullUserName());//
				// 前办理人
				// waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));//
				// 前办理人
				// 按照新日志表修改后
				if (logMap.containsKey(task.getId())) {
					String lastID = logMap.get(task.getId());
					if (lastMap.containsKey(lastID)) {
						Long userIds = lastMap.get(lastID);
						if (userIdAndNameMap.containsKey(userIds.toString())) {
							waitTaskShowVO.setFormerManager(userIdAndNameMap.get(userIds.toString()));
						} else {
							waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
						}
					} else {
						waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
					}
				} else {
					waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
				}

				// 查询当前用户对于该任务是否被别人委派的，如果是，在节点后面追加委派人
				waitTaskShowVO.setLink(task.getName() == null ? ""
						: task.getName() + (taskIdDelegateNameMap.get(task.getId()) == null ? ""
								: taskIdDelegateNameMap.get(task.getId())));// 环节

				HistoricTaskInstance taskInstance = taskIdTaskInstanceMap.get(task.getId());
				if (!StringUtils.isEmpty(task.getAssignee())) {
					waitTaskShowVO.setSignInTime(taskInstance.getClaimTime());// 签收时间
				}
				waitTaskShowVO.setProcessInstanceId(task.getProcInstId());// 流程实例ID

				String bpmnId = "";
				if (!StringUtils.isEmpty(str)) {
					String[] key = str.split(",");
					if (key.length >= 1) {
						bpmnId = key[0];
					}
				}
				waitTaskShowVO.setBpmnId(bpmnId);
				waitTaskShowVO.setTaskDefKey(task.getTaskDefKey());
				voList.add(waitTaskShowVO);
			} else {
				count--;
			}
		}
		sortList(voList, obj);
		page.setRecords(voList);
		page.setTotal(count);
		page.setSize(pageBean.getPageSize());
		page.setCurrent(pageBean.getPageNum());
		return page;
	}

	@Override
	public Page<WaitTaskShowVO> waitingList(PageBean pageBean, String userId, WaitTaskShowVO obj) throws Exception {
		Page<WaitTaskShowVO> page = new Page<WaitTaskShowVO>();
		List<WaitTaskShowVO> voList = new ArrayList<WaitTaskShowVO>();

		// 到流程查询表查询满足条件的流程实例ID
		Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
		int flag = makeQueryWhere(where, obj);

		List<ActAljoinQuery> queryList = new ArrayList<ActAljoinQuery>();
		List<String> processInstanceIds = new ArrayList<String>();
		// 如果有查询条件，把查询流程表的数据保存到map中（后面需要用到，不用重复查询）
		Map<String, ActAljoinQuery> actAljoinQueryMap = new HashMap<String, ActAljoinQuery>();
		if (flag > 0) {
			where.setSqlSelect(
					"process_instance_id,urgent_status,process_title,current_handle_full_user_name,start_time,create_full_user_name,process_category_ids");
			queryList = actAljoinQueryService.selectList(where);
			for (ActAljoinQuery query : queryList) {
				processInstanceIds.add(query.getProcessInstanceId());
				actAljoinQueryMap.put(query.getProcessInstanceId(), query);
			}
		}

		String processId = "";
		if (flag > 0 && processInstanceIds.size() <= 0) {
			return page;
		}
		// 查询收文阅件流程ID
		/*
		 * Where<ActAljoinFixedConfig> configWhere = new
		 * Where<ActAljoinFixedConfig>();
		 * configWhere.setSqlSelect("id,process_id");
		 * configWhere.eq("process_code",
		 * WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
		 * List<ActAljoinFixedConfig> configList =
		 * actAljoinFixedConfigService.selectList(configWhere); if (null !=
		 * configList && !configList.isEmpty()) { processId =
		 * configList.get(0).getProcessId(); }
		 */
		// 构造查询条件
		ActRuTask actRuTask = new ActRuTask();
		if (!StringUtils.isEmpty(userId)) {
			actRuTask.setAssignee(userId);
		}
		if (!StringUtils.isEmpty(processId)) {
			actRuTask.setProcDefId(processId);
		}
		// 当流程id为空时，要返回一个空的page
		if (processInstanceIds.size() > 0) {
			actRuTask.setProcessInstanceIds(processInstanceIds);
		}

		if (null != obj.getReceiveBegTime()) {
			actRuTask.setCreateBegTime(obj.getReceiveBegTime());
		}
		if (null != obj.getReceiveEndTime()) {
			actRuTask.setCreateEndTime(obj.getReceiveEndTime());
		}
		actRuTask.setDbType(aljoinSetting.getDbType());

		// 查询出自己作为候选或者已经签收的任务的分页信息
		Page<ActRuTask> ruTaskPage = actRuTaskService.waitingList(pageBean, actRuTask);
		// 待办任务记录总数
		int count = pageBean.getIsSearchCount().intValue() == 1 ? ruTaskPage.getTotal() : 0;
		// 待办任务列表
		List<ActRuTask> taskList = ruTaskPage.getRecords();

		Map<String, HistoricProcessInstance> historicProcessInstanceMap = new HashMap<String, HistoricProcessInstance>();
		Map<String, String> instanceIdCategoryNameMap = new HashMap<String, String>();
		Map<String, String> taskIdDelegateNameMap = new HashMap<String, String>();
		Map<String, HistoricTaskInstance> taskIdTaskInstanceMap = new HashMap<String, HistoricTaskInstance>();
		Map<String, String> taskIdPreHandleUserName = new HashMap<String, String>();
		Map<String, Set<Long>> taskIdPreHandleUserIdMap = new HashMap<String, Set<Long>>();
		Map<String, String> logMap = new HashMap<String, String>();
		Map<String, Long> lastMap = new HashMap<String, Long>();
		Map<String, String> userIdAndNameMap = new HashMap<String, String>();
		Set<Long> allUserIdSet = new HashSet<Long>();
		if (taskList.size() > 0) {
			// 查询任务的所有流程实例
			Set<String> processInstanceIdSet = new HashSet<String>();
			List<String> processInstanceIdList = new ArrayList<String>();
			List<String> taskIdList = new ArrayList<String>();

			for (ActRuTask task : taskList) {
				processInstanceIdSet.add(task.getProcInstId());
				taskIdList.add(task.getId());
				processInstanceIdList.add(task.getProcInstId());

				// 获取上级办理人
				List<TaskDefinition> defList = actActivitiService.getPreTaskInfo2(task.getTaskDefKey(),
						task.getProcInstId());

				Set<Long> uIdSet = new HashSet<Long>();
				// 对于自由流程，取defList的时候会返回assignee
				for (TaskDefinition def : defList) {
					if (def.getAssigneeExpression() != null
							&& !StringUtils.isEmpty(def.getAssigneeExpression().getExpressionText())) {
						uIdSet.add(Long.parseLong(def.getAssigneeExpression().getExpressionText()));
						allUserIdSet.add(Long.parseLong(def.getAssigneeExpression().getExpressionText()));
					}
				}
				/*
				 * if (uIdSet.size() == 0) { // 如果defList已经待会了办理人信息，就不需要再次查询
				 * List<String> allKeyList = new ArrayList<String>(); for
				 * (TaskDefinition taskDefinition : defList) {
				 * allKeyList.add(taskDefinition.getKey()); }
				 * 
				 * HistoricActivityInstanceQuery query =
				 * historyService.createHistoricActivityInstanceQuery()
				 * .processInstanceId(task.getProcInstId()).finished().
				 * activityType("userTask")
				 * .orderByHistoricActivityInstanceEndTime().desc();
				 * List<HistoricActivityInstance> actList = new
				 * ArrayList<HistoricActivityInstance>();
				 * 
				 * if (defList.size() == 1) { actList =
				 * query.activityId(defList.get(0).getKey()).listPage(0, 1); }
				 * else if (defList.size() > 1) { actList = query.list();
				 * List<HistoricActivityInstance> newActList = new
				 * ArrayList<HistoricActivityInstance>(); for
				 * (HistoricActivityInstance hai : actList) { if
				 * (allKeyList.contains(hai.getActivityId())) {
				 * newActList.add(hai); } } actList = newActList; } else { //
				 * 回退到第一个节点的时候 actList = query.listPage(0, 1); }
				 * 
				 * for (HistoricActivityInstance historicActivityInstance :
				 * actList) { if
				 * (!StringUtils.isEmpty(historicActivityInstance.getAssignee())
				 * ) { uIdSet.add(Long.parseLong(historicActivityInstance.
				 * getAssignee()));
				 * allUserIdSet.add(Long.parseLong(historicActivityInstance.
				 * getAssignee())); } } }
				 */
				taskIdPreHandleUserIdMap.put(task.getId(), uIdSet);
			}

			if (taskIdList != null && taskIdList.size() > 0) {
				Where<ActAljoinActivityLog> actLogWhere = new Where<ActAljoinActivityLog>();
				actLogWhere.in("current_task_id", taskIdList);
				actLogWhere.setSqlSelect("current_task_id,last_task_id");
				List<ActAljoinActivityLog> logList = actAljoinActivityLogService.selectList(actLogWhere);
				String lastTask = "";
				if (logList != null && logList.size() > 0) {

					for (ActAljoinActivityLog actAljoinActivityLog : logList) {
						lastTask = actAljoinActivityLog.getLastTaskId() + ",";
						logMap.put(actAljoinActivityLog.getCurrentTaskId(), actAljoinActivityLog.getLastTaskId());
					}
					if (lastTask != null && "".equals(lastTask)) {
						Where<ActAljoinActivityLog> lastLogWhere = new Where<ActAljoinActivityLog>();
						actLogWhere.in("current_task_id", lastTask);
						actLogWhere.setSqlSelect("current_task_id,last_task_id");
						List<ActAljoinActivityLog> lastLogList = actAljoinActivityLogService.selectList(lastLogWhere);

						for (ActAljoinActivityLog actAljoinActivityLog : lastLogList) {
							allUserIdSet.add(Long.valueOf(actAljoinActivityLog.getOperateUserId()));
							lastMap.put(actAljoinActivityLog.getCurrentTaskId(),
									Long.valueOf(actAljoinActivityLog.getOperateUserId()));
						}
					}
				}
			}
			// 一次查询所有上次办理人
			if (allUserIdSet.size() > 0) {
				Where<AutUser> autUserWhere = new Where<AutUser>();
				autUserWhere.in("id", allUserIdSet);
				autUserWhere.setSqlSelect("id,full_name");
				List<AutUser> autUserList = autUserService.selectList(autUserWhere);

				for (AutUser autUser : autUserList) {
					userIdAndNameMap.put(autUser.getId().toString(), autUser.getFullName());
				}
				for (Map.Entry<String, Set<Long>> entry : taskIdPreHandleUserIdMap.entrySet()) {
					Set<Long> set = entry.getValue();
					String userFullNames = "";
					for (Long uid : set) {
						userFullNames += userIdAndNameMap.get(uid.toString()) + ",";
					}
					if (!"".equals(userFullNames)) {
						userFullNames = userFullNames.substring(0, userFullNames.length() - 1);
					}
					taskIdPreHandleUserName.put(entry.getKey(), userFullNames);
				}

			}

			List<HistoricProcessInstance> historicProcessInstanceList = historyService
					.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();

			for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
				historicProcessInstanceMap.put(historicProcessInstance.getId(), historicProcessInstance);
			}
			// 如果上的查询条件没有流程查询表的数据，在去取，如果上面的流程查询map中有数据，则不需要在去查询
			if (actAljoinQueryMap.size() == 0) {
				Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
				queryWhere.setSqlSelect(
						"process_instance_id,urgent_status,process_title,current_handle_full_user_name,start_time,create_full_user_name,process_category_ids");
				queryWhere.in("process_instance_id", processInstanceIdSet);
				List<ActAljoinQuery> actAljoinQueryList = actAljoinQueryService.selectList(queryWhere);
				for (ActAljoinQuery actAljoinQuery : actAljoinQueryList) {
					actAljoinQueryMap.put(actAljoinQuery.getProcessInstanceId(), actAljoinQuery);
				}
			}
			// 一次性获取流程分类
			List<Long> categoryIdList = new ArrayList<Long>();

			for (Map.Entry<String, ActAljoinQuery> entry : actAljoinQueryMap.entrySet()) {
				ActAljoinQuery aq = entry.getValue();
				String categoryIds = aq.getProcessCategoryIds();
				if (!StringUtils.isEmpty(categoryIds)) {
					String[] categoryIdsArr = categoryIds.split(",");
					categoryIds = categoryIdsArr[categoryIdsArr.length - 1];
					instanceIdCategoryNameMap.put(entry.getKey(), categoryIds);
					categoryIdList.add(Long.parseLong(categoryIds));
				}

			}
			Where<ActAljoinCategory> actAljoinCategoryWhere = new Where<ActAljoinCategory>();
			actAljoinCategoryWhere.in("id", categoryIdList);
			actAljoinCategoryWhere.setSqlSelect("id,category_name");
			List<ActAljoinCategory> actAljoinCategoryList = actAljoinCategoryService.selectList(actAljoinCategoryWhere);
			for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
				for (Map.Entry<String, String> entry : instanceIdCategoryNameMap.entrySet()) {
					if ((actAljoinCategory.getId().toString()).equals(entry.getValue())) {
						instanceIdCategoryNameMap.put(entry.getKey(), actAljoinCategory.getCategoryName());
					}
				}
			}

			// 根据任务ID获取委托人信息
			Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
			actAljoinDelegateInfoWhere.in("task_id", taskIdList);
			actAljoinDelegateInfoWhere.eq("assignee_user_id", userId);
			actAljoinDelegateInfoWhere.setSqlSelect("task_id,delegate_user_names");
			List<ActAljoinDelegateInfo> actAljoinDelegateInfoList = actAljoinDelegateInfoService
					.selectList(actAljoinDelegateInfoWhere);
			for (ActAljoinDelegateInfo actAljoinDelegateInfo : actAljoinDelegateInfoList) {
				String mapValue = taskIdDelegateNameMap.get(actAljoinDelegateInfo.getTaskId());
				if (StringUtils.isEmpty(mapValue)) {
					taskIdDelegateNameMap.put(actAljoinDelegateInfo.getTaskId(),
							actAljoinDelegateInfo.getDelegateUserNames());
				} else {
					mapValue = mapValue + "," + actAljoinDelegateInfo.getDelegateUserNames();
					taskIdDelegateNameMap.put(actAljoinDelegateInfo.getTaskId(), mapValue);
				}
			}
			for (Map.Entry<String, String> entry : taskIdDelegateNameMap.entrySet()) {
				String key = entry.getKey();
				String[] delegateInfoArr = taskIdDelegateNameMap.get(key).split(",");
				List<String> userNameList = new ArrayList<String>();
				for (int i = 0; i < delegateInfoArr.length; i++) {
					if (!userNameList.contains(delegateInfoArr[i])) {
						userNameList.add(delegateInfoArr[i]);
					}
				}
				int n = 0;
				String newDelegateInfo = "";
				for (String s : userNameList) {
					if (n == 0) {
						newDelegateInfo = s;
					} else {
						newDelegateInfo += "," + s;
					}
					n++;
				}
				if (!"".equals(newDelegateInfo)) {
					newDelegateInfo = "[委托人(" + newDelegateInfo + ")]";
				}
				taskIdDelegateNameMap.put(key, newDelegateInfo);
			}
			// 查询所有任务节点信息
			List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
					.processInstanceIdIn(processInstanceIdList).list();
			for (HistoricTaskInstance historicTaskInstance : taskInstanceList) {
				taskIdTaskInstanceMap.put(historicTaskInstance.getId(), historicTaskInstance);
			}

		}

		for (ActRuTask task : taskList) {
			// 根据流程实例ID获取待办流程实例
			HistoricProcessInstance historicProcessInstance = historicProcessInstanceMap.get(task.getProcInstId());
			// 从上面的map中获取ActAljoinQuery
			ActAljoinQuery actAljoinQuery = actAljoinQueryMap.get(task.getProcInstId());

			if (null != actAljoinQuery) {
				WaitTaskShowVO waitTaskShowVO = new WaitTaskShowVO();
				waitTaskShowVO.setTaskId(task.getId());
				waitTaskShowVO.setFormType(instanceIdCategoryNameMap.get(task.getProcInstId()));// 流程分类
				String str = historicProcessInstance.getBusinessKey();
				waitTaskShowVO.setBusinessKey(str);

				waitTaskShowVO.setUrgency(actAljoinQuery.getUrgentStatus());// 缓急

				Integer uegency = 1;
				if (actAljoinQuery.getUrgentStatus().equals(WebConstant.COMMONLY)) {
					uegency = 1;
				} else if (actAljoinQuery.getUrgentStatus().equals(WebConstant.URGENT)) {
					uegency = 2;
				} else if (actAljoinQuery.getUrgentStatus().equals(WebConstant.ADD_URENT)) {
					uegency = 3;
				}

				waitTaskShowVO.setTitle(actAljoinQuery.getProcessTitle());// 标题
				waitTaskShowVO.setFounder(actAljoinQuery.getCreateFullUserName());// 流程发起人
				waitTaskShowVO.setUrgencyStatus(uegency);// 缓急转换为数字 用于排序

				waitTaskShowVO.setFillingDate(task.getCreateTime());// 任务开始时间

				// waitTaskShowVO.setFormerManager(actAljoinQuery.getCurrentHandleFullUserName());//
				// 前办理人
				// waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));//
				// 前办理人
				// 按照新日志表修改后
				if (logMap.containsKey(task.getId())) {
					String lastID = logMap.get(task.getId());
					if (lastMap.containsKey(lastID)) {
						Long userIds = lastMap.get(lastID);
						if (userIdAndNameMap.containsKey(userIds.toString())) {
							waitTaskShowVO.setFormerManager(userIdAndNameMap.get(userIds.toString()));
						} else {
							waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
						}
					} else {
						waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
					}
				} else {
					waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
				}

				// 查询当前用户对于该任务是否被别人委派的，如果是，在节点后面追加委派人
				waitTaskShowVO.setLink(task.getName() == null ? ""
						: task.getName() + (taskIdDelegateNameMap.get(task.getId()) == null ? ""
								: taskIdDelegateNameMap.get(task.getId())));// 环节

				HistoricTaskInstance taskInstance = taskIdTaskInstanceMap.get(task.getId());
				if (!StringUtils.isEmpty(task.getAssignee())) {
					waitTaskShowVO.setSignInTime(taskInstance.getClaimTime());// 签收时间
				}
				waitTaskShowVO.setProcessInstanceId(task.getProcInstId());// 流程实例ID

				String bpmnId = "";
				if (!StringUtils.isEmpty(str)) {
					String[] key = str.split(",");
					if (key.length >= 1) {
						bpmnId = key[0];
					}
				}
				waitTaskShowVO.setBpmnId(bpmnId);
				waitTaskShowVO.setTaskDefKey(task.getTaskDefKey());
				voList.add(waitTaskShowVO);
			} else {
				count--;
			}
		}
		sortList(voList, obj);
		page.setRecords(voList);
		page.setTotal(count);
		page.setSize(pageBean.getPageSize());
		page.setCurrent(pageBean.getPageNum());
		return page;
	}

	@Override
	public Page<AllTaskShowVO> getAllTask(PageBean pageBean, AllTaskShowVO obj, Map<String, String> map)
			throws Exception {

		Page<AllTaskShowVO> page = new Page<AllTaskShowVO>();
		List<AllTaskShowVO> voList = new ArrayList<AllTaskShowVO>();
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (obj != null && obj.getDataType().equals("1")) {
			// 查询系统 综合查询
			// 开始索引
			int firstResult = (pageBean.getPageNum() - 1) * pageBean.getPageSize();
			// 查询记录数
			int maxResults = pageBean.getPageSize();

			String userID = map.get("userId").toString();
			Where<ActAljoinBpmnUser> bpmnUserWhere = new Where<ActAljoinBpmnUser>();
			bpmnUserWhere.eq("is_delete", 0);
			bpmnUserWhere.eq("is_active", 1);
			bpmnUserWhere.eq("auth_type", 1);
			bpmnUserWhere.eq("user_id", userID);
			bpmnUserWhere.setSqlSelect("bpmn_id");
			List<ActAljoinBpmnUser> bpmnUserList = actAljoinBpmnUserService.selectList(bpmnUserWhere);
			// 获取管理流程ID

			/*
			 * if (bpmnUserList != null) { for (ActAljoinBpmnUser
			 * actAljoinBpmnUser : bpmnUserList) { BpmnIds +=
			 * actAljoinBpmnUser.getBpmnId() + ","; } }
			 */

			// 获取个人任务
			Where<ActAljoinQueryHis> hiswhere = new Where<ActAljoinQueryHis>();
			Where<ActAljoinQueryHis> hiswheres = new Where<ActAljoinQueryHis>();
			String qbpmn = "";
			List<String> qbpmnList = new ArrayList<String>();
			if (map.containsKey("qBpmn")) {
				if (map.get("qBpmn") != null && !"".equals(map.get("qBpmn").toString())) {
					qbpmn = map.get("qBpmn").toString();
					String sql = "select * from " + managementService.getTableName(HistoricProcessInstance.class)
							+ " H where H.BUSINESS_KEY_ like '" + qbpmn + ",%' ";
					List<HistoricProcessInstance> nativeHisList = historyService
							.createNativeHistoricProcessInstanceQuery().sql(sql).list();
					// qbpmn = "";
					if (nativeHisList != null && nativeHisList.size() > 0) {
						for (HistoricProcessInstance historicProcessInstance : nativeHisList) {
							// qbpmn += historicProcessInstance.getId() + ",";
							qbpmnList.add(historicProcessInstance.getId());
						}

					} else {
						qbpmnList.add("6666666666666666666666666");
					}

					if (qbpmnList.size() > 1000) {
						List<String> tmpqb = new ArrayList<String>();
						int qbNo = 0;
						for (String string : qbpmnList) {
							qbNo += 1;
							tmpqb.add(string);
							if (qbNo == 1000) {
								qbNo = 0;
								hiswheres.in("process_instance_id", tmpqb).or();
								hiswhere.in("process_instance_id", tmpqb).or();
								tmpqb.clear();
							}
						}
						if (qbNo == 1000 || qbNo == 0) {
							hiswhere.eq("is_delete", 0).andNew();
							hiswheres.eq("is_delete", 0).andNew();
						} else {
							hiswheres.in("process_instance_id", tmpqb).andNew();
							hiswhere.in("process_instance_id", tmpqb).andNew();
						}
					} else {
						hiswheres.in("process_instance_id", qbpmnList);
						hiswhere.in("process_instance_id", qbpmnList);
					}

				}
			}
			if (obj.getFormType() != null && !"".equals(obj.getFormType().toString())) {
				// 获取流程
				String tpyeID = obj.getFormType().toString();
				if (!"".equals(tpyeID)) {

				} else {
					tpyeID = "没有数据，查询空白";
				}
				hiswhere.like("process_category_ids", tpyeID);
				hiswheres.like("process_category_ids", tpyeID);
			}
			// Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
			if (obj.getTitle() != null && !"".equals(obj.getTitle())) {
				hiswhere.andNew();
				hiswheres.andNew();
				/*
				 * where.like("process_title", obj.getTitle()).or(
				 * "create_full_user_name like {0}", "%" + obj.getTitle() +
				 * "%");
				 */
				hiswhere.like("process_title", obj.getTitle());
				hiswheres.like("process_title", obj.getTitle());
				hiswhere.andNew();
				hiswheres.andNew();
			}

			// Calendar c = Calendar.getInstance();
			if (map.get("startTime") != null && !"".equals(map.get("startTime").toString())
					&& map.get("endTime") != null && !"".equals(map.get("endTime").toString())) {
				// 时间集合
				// c.setTime(format.parse(map.get("endTime").toString()));
				// c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
				hiswhere.between("last_update_time", formats.parse(map.get("startTime") + " 00:00:00"),
						formats.parse(map.get("endTime").toString() + " 23:59:59"));
				hiswheres.between("last_update_time", formats.parse(map.get("startTime") + " 00:00:00"),
						formats.parse(map.get("endTime").toString() + " 23:59:59"));
			} else if (map.get("startTime") != null && !"".equals(map.get("startTime").toString())) {
				hiswhere.ge("last_update_time", formats.parse(map.get("startTime") + " 00:00:00"));
				hiswheres.ge("last_update_time", formats.parse(map.get("startTime") + " 00:00:00"));
			} else if (map.get("endTime") != null && !"".equals(map.get("endTime").toString())) {
				hiswhere.le("last_update_time", formats.parse(map.get("endTime").toString() + " 23:59:59"));
				hiswheres.le("last_update_time", formats.parse(map.get("endTime").toString() + " 23:59:59"));
			}
			hiswhere.where("1={0}", 1);
			hiswheres.where("1={0}", 1);
			// where.setSqlSelect("process_instance_id");
			List<String> manIds = new ArrayList<String>();
			List<String> processInstanceIds = new ArrayList<String>();
			List<String> bprocessInstanceIds = new ArrayList<String>();
			Set<String> setid = new HashSet<String>();
			Map<String, ActAljoinQueryHis> hisMap = new HashMap<String, ActAljoinQueryHis>();
			List<ActAljoinQueryHis> queryHisList = actAljoinQueryHisService.selectList(hiswhere);
			if (queryHisList != null && queryHisList.size() > 0) {
				for (ActAljoinQueryHis actAljoinQuery : queryHisList) {
					processInstanceIds.add(actAljoinQuery.getProcessInstanceId().toString());
					hisMap.put(actAljoinQuery.getProcessInstanceId().toString(), actAljoinQuery);
				}
				queryHisList.clear();
			}
			// 创建人所在流程
			hiswhere.eq("create_user_id", userID);
			List<ActAljoinQueryHis> myqueryHisList = actAljoinQueryHisService.selectList(hiswhere);
			if (myqueryHisList != null && myqueryHisList.size() > 0) {
				for (ActAljoinQueryHis actAljoinQuery : myqueryHisList) {
					processInstanceIds.add(actAljoinQuery.getProcessInstanceId().toString());
					bprocessInstanceIds.add(actAljoinQuery.getProcessInstanceId().toString());
					hisMap.put(actAljoinQuery.getProcessInstanceId().toString(), actAljoinQuery);
				}
				myqueryHisList.clear();
			}
			// 查询流程ID对应实例
			if (bpmnUserList != null && bpmnUserList.size() > 0) {
			    List<Long> bpmnIdList = new ArrayList<Long>(bpmnUserList.size());
	            for (ActAljoinBpmnUser bpmnUser : bpmnUserList) {
	                bpmnIdList.add(bpmnUser.getBpmnId());
	            }
				String sql = "select * from " + managementService.getTableName(HistoricProcessInstance.class)
						+ " H where ";
				if(bpmnIdList.size()>0){
				    Where<ActAljoinBpmnRun> bpmnRunWhere = new Where<ActAljoinBpmnRun>();
	                bpmnRunWhere.setSqlSelect("id");
	                bpmnRunWhere.in("orgnl_id", bpmnIdList);
	                List<ActAljoinBpmnRun> bpmnRuns = actAljoinBpmnRunService.selectList(bpmnRunWhere);
				    for (int i = 0; i < bpmnRuns.size(); i++) {
	                    if (i == (bpmnRuns.size() - 1)) {
	                        sql += "H.BUSINESS_KEY_ like '%" + bpmnRuns.get(i).getId() + "%' ";
	                    } else {
	                        sql += "H.BUSINESS_KEY_ like '%" + bpmnRuns.get(i).getId() + "%' or ";
	                    }
	                }
	                List<HistoricProcessInstance> nativeHisList = historyService.createNativeHistoricProcessInstanceQuery()
	                        .sql(sql).list();
	                // String hisIds = "";
	                List<String> hisList = new ArrayList<String>();
	                if (nativeHisList != null && nativeHisList.size() > 0) {
	                    for (HistoricProcessInstance actAljoinFormDataRun : nativeHisList) {
	                        manIds.add(actAljoinFormDataRun.getId());
	                        hisList.add(actAljoinFormDataRun.getId());
	                        // hisIds += actAljoinFormDataRun.getId() + ",";
	                    }
	                }
	                if (hisList.size() > 0) {
	                    if (hisList.size() > 1000) {
	                        List<String> tmphis = new ArrayList<String>();
	                        int qbNo = 0;
	                        for (String string : hisList) {
	                            qbNo += 1;
	                            tmphis.add(string);
	                            if (qbNo == 1000) {
	                                qbNo = 0;
	                                hiswheres.in("process_instance_id", tmphis).or();
	                                hiswhere.in("process_instance_id", tmphis).or();
	                                tmphis.clear();
	                            }
	                        }
	                        if (qbNo == 1000 || qbNo == 0) {
	                            hiswheres.eq("is_delete", 0).andNew();
	                        } else {
	                            hiswheres.in("process_instance_id", tmphis).andNew();
	                            hiswhere.in("process_instance_id", tmphis).andNew();
	                        }
	                    } else {
	                        hiswheres.in("process_instance_id", hisList);
	                    }
	                    List<ActAljoinQueryHis> queryHisLists = actAljoinQueryHisService.selectList(hiswheres);
	                    if (queryHisLists != null && queryHisLists.size() > 0) {
	                        for (ActAljoinQueryHis actAljoinQuery : queryHisLists) {
	                            processInstanceIds.add(actAljoinQuery.getProcessInstanceId().toString());
	                            bprocessInstanceIds.add(actAljoinQuery.getProcessInstanceId().toString());
	                            hisMap.put(actAljoinQuery.getProcessInstanceId().toString(), actAljoinQuery);
	                        }
	                        queryHisLists.clear();
	                    }
	                }
				}

			}

			int count = 0;
			List<ActHiTaskinst> hisList = new ArrayList<ActHiTaskinst>();
			if (processInstanceIds != null && processInstanceIds.size() > 0) {
				Where<ActHiTaskinst> taskWhere = new Where<ActHiTaskinst>();
				if (bprocessInstanceIds != null && bprocessInstanceIds.size() > 0) {
					taskWhere.eq("OWNER_", userID).or("ASSIGNEE_={0}", userID).or();
					if (bprocessInstanceIds.size() > 1000) {
						int strNo = 0;
						List<String> strList = new ArrayList<String>();
						for (String tmpStr : bprocessInstanceIds) {
							strNo += 1;
							strList.add(tmpStr);
							if (strNo == 1000) {
								strNo = 0;
								taskWhere.in("PROC_INST_ID_", strList).or();
								strList.clear();
							}
						}
						if (strNo == 1000 || strNo == 0) {
							// quer=quer.processInstanceIdIn(strList);
							taskWhere.where("1={0}", 1).andNew();
						} else {
							taskWhere.in("PROC_INST_ID_", strList).andNew();
						}

					} else {
						taskWhere.in("PROC_INST_ID_", bprocessInstanceIds).andNew();
					}

				} else {
					taskWhere.eq("OWNER_", userID).or("ASSIGNEE_={0}", userID).andNew();
				}
				if (processInstanceIds.size() > 1000) {
					int strNo = 0;
					List<String> strList = new ArrayList<String>();
					for (String tmpStr : processInstanceIds) {
						strNo += 1;
						strList.add(tmpStr);
						if (strNo == 1000) {
							strNo = 0;
							taskWhere.in("PROC_INST_ID_", strList).or();
							strList.clear();
						}
					}
					if (strNo == 1000 || strNo == 0) {
						// quer=quer.processInstanceIdIn(strList);
						taskWhere.where("1={0}", 1);
					} else {
						taskWhere.in("PROC_INST_ID_", strList);
					}

				} else {
					taskWhere.in("PROC_INST_ID_", processInstanceIds);
				}
				taskWhere.setSqlSelect("PROC_INST_ID_");
				hisList = actHiTaskinstService.selectList(taskWhere);
			}
			bprocessInstanceIds.clear();
			processInstanceIds.clear();
			if (hisList != null && hisList.size() > 0) {
				for (ActHiTaskinst historicTaskInstance : hisList) {
					processInstanceIds.add(historicTaskInstance.getProcInstId());
					setid.add(historicTaskInstance.getProcInstId());
				}
			}
			if (processInstanceIds != null && processInstanceIds.size() > 0) {
				count = (int) historyService.createHistoricProcessInstanceQuery().processInstanceIds(setid).finished()
						.count();
				// count =//
				// (int)historyService.createHistoricProcessInstanceQuery().finished().count();
				List<HistoricProcessInstance> historicProcessInstanceList = historyService
						.createHistoricProcessInstanceQuery().processInstanceIds(setid).orderByProcessInstanceEndTime()
						.desc().finished().listPage(firstResult, maxResults);
				// List<HistoricProcessInstance> historicProcessInstanceList=
				// historyService.createHistoricProcessInstanceQuery().finished().listPage(firstResult,
				// maxResults);

				List<Deployment> dlist = repositoryService.createDeploymentQuery().list();
				Map<String, Deployment> dMap = new HashMap<String, Deployment>();
				if (dlist != null && dlist.size() > 0) {
					for (Deployment historicProcessInstance : dlist) {
						dMap.put(historicProcessInstance.getId(), historicProcessInstance);
					}
					dlist.clear();
				}
				List<ActAljoinCategory> categoryList = actAljoinCategoryService.getAllCategoryList();
				Map<String, String> categoryMap = new HashMap<String, String>();
				if (categoryList != null && categoryList.size() > 0) {
					for (ActAljoinCategory actAljoinCategory : categoryList) {
						categoryMap.put(actAljoinCategory.getId().toString(), actAljoinCategory.getCategoryName());
					}
				}

				List<HistoricProcessInstance> intlist = new ArrayList<HistoricProcessInstance>();
				if (setid != null && setid.size() > 0) {
					intlist = historyService.createHistoricProcessInstanceQuery().processInstanceIds(setid).list();
				}
				Map<String, HistoricProcessInstance> intMap = new HashMap<String, HistoricProcessInstance>();
				if (intlist != null && intlist.size() > 0) {
					for (HistoricProcessInstance historicProcessInstance : intlist) {
						intMap.put(historicProcessInstance.getId().toString(), historicProcessInstance);
					}
					intlist.clear();
				}
				Map<String, HistoricProcessInstance> insMap = new HashMap<String, HistoricProcessInstance>();
				if (setid != null && setid.size() > 0) {
					List<HistoricProcessInstance> tinsList = historyService.createHistoricProcessInstanceQuery()
							.processInstanceIds(setid).list();
					if (tinsList != null && tinsList.size() > 0) {
						for (HistoricProcessInstance historicProcessInstance : tinsList) {
							insMap.put(historicProcessInstance.getId().toString(), historicProcessInstance);
						}
						tinsList.clear();
					}
				}
				if (historicProcessInstanceList.size() > 0) {
					for (HistoricProcessInstance historic : historicProcessInstanceList) {
						HistoricProcessInstance historicProcessInstance = null;
						if (insMap.containsKey(historic.getId().toString())) {
							historicProcessInstance = insMap.get(historic.getId().toString());
						} else {
							continue;
						}
						// 获取流程定义
						Deployment deployment = null;
						if (dMap.containsKey(historicProcessInstance.getDeploymentId().toString())) {
							deployment = dMap.get(historicProcessInstance.getDeploymentId().toString());
						} else {
							continue;
						}
						String actAljoinCategory = "";
						if (categoryMap.containsKey(deployment.getCategory().toString())) {
							actAljoinCategory = categoryMap.get(deployment.getCategory().toString());
						}
						AllTaskShowVO allTaskShowVO = new AllTaskShowVO();
						allTaskShowVO.setFormType(actAljoinCategory);// 流程分类
						String retrunTime = "";
						if (historic.getEndTime() != null) {
							retrunTime = formats.format(historic.getEndTime());
						}
						allTaskShowVO.setEndTime(retrunTime);
						allTaskShowVO.setBusinessKey(historic.getBusinessKey());
						allTaskShowVO.setProcessInstanceId(historic.getId().toString());
						String pid = historic.getId().toString();
						if (hisMap != null && hisMap.size() > 0 && hisMap.containsKey(pid)) {
							ActAljoinQueryHis his = hisMap.get(pid);
							if (his != null) {
								allTaskShowVO.setTitle(his.getProcessTitle());// 标题
								allTaskShowVO.setFounder(his.getCreateFullUserName());
								String tmpHand = his.getCurrentHandleFullUserName();
								if (tmpHand != null && !"".equals(tmpHand.trim())) {
									//tmpHand = tmpHand.trim();
									//String tmp = tmpHand.substring(tmpHand.length() - 1, tmpHand.length());									
									if (",".indexOf(tmpHand)==tmpHand.length()-1) {
										tmpHand = tmpHand.substring(0, tmpHand.length() - 1);
									}
								}
								allTaskShowVO.setOperator(tmpHand);
							} else {
								allTaskShowVO.setTitle(historicProcessInstance.getProcessDefinitionName());// 标题
								allTaskShowVO.setFounder(autUserService
										.selectById(historicProcessInstance.getStartUserId()).getFullName());
							}
						} else {
							allTaskShowVO.setTitle(historicProcessInstance.getProcessDefinitionName());// 标题
							allTaskShowVO.setFounder(
									autUserService.selectById(historicProcessInstance.getStartUserId()).getFullName());// 流程发起人
						}
						voList.add(allTaskShowVO);
					}
					page.setRecords(voList);
					page.setTotal(count);
					page.setSize(pageBean.getPageSize());
				}
			}
		} else {
			// 查询旧系统数据

		}
		return page;
	}

	public String getParentId(String parentId) throws Exception {
		String tpyeID = parentId + ",";
		ActAljoinCategory actcategory = new ActAljoinCategory();
		actcategory.setParentId(Long.valueOf(parentId));
		List<ActAljoinCategory> categoryList = actAljoinCategoryService.getByParentId(actcategory);
		if (categoryList != null && categoryList.size() > 0) {
			for (int i = 0; i < categoryList.size(); i++) {
				ActAljoinCategory tmp = categoryList.get(i);
				tpyeID = tpyeID + this.getParentId(tmp.getId().toString());
			}
		}
		return tpyeID;

	}

	@Override
	@Transactional
	public RetMsg claimTask(String userId, String ids) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (!StringUtils.isEmpty(ids)) {
			// 待签收任务id
			List<String> taskIds = Arrays.asList(ids.split(","));
			if (taskIds != null) {
				for (String taskId : taskIds) {
					HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
					if(null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())){
						taskService.claim(taskId, userId);
					}
				}
				// 任务对应的流程实例id
				List<String> processInstanceIds = new ArrayList<String>();

				Where<ActHiTaskinst> acthiProcinstWhere = new Where<ActHiTaskinst>();
				acthiProcinstWhere.setSqlSelect("PROC_INST_ID_,TASK_DEF_KEY_");
				acthiProcinstWhere.in("ID_", taskIds);
				List<ActHiTaskinst> actHiProcinsts = actHiTaskinstService.selectList(acthiProcinstWhere);
				for (ActHiTaskinst actHiProcinst : actHiProcinsts) {
					// 如果任务是会签任务，签收后也不要更改当前的那些办理人
					MultiInstanceLoopCharacteristics multi = actActivitiService
							.getMultiInstance(actHiProcinst.getProcInstId(), actHiProcinst.getTaskDefKey());
					if (multi == null) {
						processInstanceIds.add(actHiProcinst.getProcInstId());
					}
				}
				if (processInstanceIds.size() > 0) {
					// 所有办理人id
					Set<String> assigneeSet = new HashSet<String>();
					// 构造出流程实例+任务+办理人的关系
					List<Task> processTasks = taskService.createTaskQuery().processInstanceIdIn(processInstanceIds)
							.list();
					Map<String, List<String>> proUsersIdMap = new HashMap<String, List<String>>();
					for (String procinstId : processInstanceIds) {
						// 单个流程实例的办理人
						List<String> processTaskAssignees = new ArrayList<String>();
						for (Task processTask : processTasks) {
							if (processTask.getProcessInstanceId().equals(procinstId)) {

								if (!StringUtils.isEmpty(processTask.getAssignee())) {
									// 已经签收，获取办理人（对于签收操作，多实例节点不会执行到这里）
									assigneeSet.add(processTask.getAssignee());
									processTaskAssignees.add(processTask.getAssignee());
								} else {
									// 未签收，获取所有候选人（对于签收操作，多实例节点不会执行到这里）
									List<IdentityLink> linkList = taskService
											.getIdentityLinksForTask(processTask.getId());
									for (IdentityLink identityLink : linkList) {
										if ("candidate".equals(identityLink.getType())
												&& !StringUtils.isEmpty(identityLink.getUserId())) {
											assigneeSet.add(identityLink.getUserId());
											processTaskAssignees.add(identityLink.getUserId());
										}
									}
								}
							}
						}
						// 单个流程实例的办理人放到map
						if (processTaskAssignees.size() > 0) {
							proUsersIdMap.put(procinstId, processTaskAssignees);
						}
					}
					// 一次性查询所有用户
					Where<AutUser> userwhere = new Where<AutUser>();
					userwhere.setSqlSelect("id, full_name");
					userwhere.in("id", assigneeSet);
					List<AutUser> userList = autUserService.selectList(userwhere);
					// 定义map存放流程实例的办理人名称
					HashMap<String, String> map = new HashMap<String, String>();
					for (String procinst : processInstanceIds) {
						List<String> proUsersId = proUsersIdMap.get(procinst);
						String userName = "";
						for (AutUser autUser : userList) {
							if (proUsersId.contains(String.valueOf(autUser.getId()))) {
								userName += autUser.getFullName() + ",";
							}
						}
						if (!StringUtils.isEmpty(userName)) {
							userName = userName.substring(0, userName.length() - 1);
						}
						map.put(procinst, userName);
					}
					actAljoinQueryService.updateAssigneeNameBatch(map, processInstanceIds);
				}

			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("签收成功");
		return retMsg;
	}

	@Override
	public void updateQuery(String taskId) throws Exception {
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
		where.eq("process_instance_id", task.getProcessInstanceId());
		ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(where);
		if (!StringUtils.isEmpty(task.getAssignee())) {
			actAljoinQuery.setCurrentHandleFullUserName(autUserService.selectById(task.getAssignee()).getFullName());
		}
		actAljoinQueryService.updateById(actAljoinQuery);
	}

	@Override
	public RetMsg jumpTask(String taskId, String targetTaskKey, String targetUserId, String thisTaskUserComment,
			Long cuserId, String userName, String nickName, Map<String, String> paramMap, ActAljoinFormDataRun entity,
			String isTask, String nextNode) throws Exception {
		RetMsg retMsg = new RetMsg();
		// 获取当前流程实例ID
		Task orgnlTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = orgnlTask.getProcessInstanceId();
        ProcessInstance instance
            = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());

		// 获取任务的授权数据
		Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        taskAssigneeWhere.eq("bpmn_id", actAljoinBpmnRun.getOrgnlId());
		taskAssigneeWhere.eq("task_id", orgnlTask.getTaskDefinitionKey());
        taskAssigneeWhere.eq("version", actAljoinBpmnRun.getTaskAssigneeVersion());
		taskAssigneeWhere.setSqlSelect("comment_widget_ids");
		ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

		// 填写意见
		if (StringUtils.isEmpty(thisTaskUserComment)) {
			thisTaskUserComment = "回退";
		}
		Authentication.setAuthenticatedUserId(cuserId.toString());
		Comment thisTaskUserCommentObj = taskService.addComment(taskId, processInstanceId, thisTaskUserComment);
		thisTaskUserComment = thisTaskUserComment + "(" + nickName
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
		HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
		if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
			// 取消签收
			taskService.unclaim(task.getId());
		}
		// taskService.claim(task.getId(), targetUserId);
		List<String> assignees = new ArrayList<String>();
		List<HistoricTaskInstance> historicList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey).finished().list();
		for (HistoricTaskInstance historic : historicList) {
			assignees.add(historic.getAssignee());
		}

		// 设置候选
		taskService.addUserIdentityLink(task.getId(), targetUserId, "candidate");
		retMsg.setObject(instance.getId());
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 分为四类：1-自由选择节点，2-排他节点，3-自由节点，4-并行节点
	 */
	@Override
	public Map<String, Object> getNextTaskInfo(String taskId, Map<String, Object> runMap) throws Exception {
		int processType = 0;
		boolean isTask = true;
		int isEl = 0;
		List<TaskDefinition> list = new ArrayList<TaskDefinition>();
		int isFinishMergeTask = 0;
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Map<String,Object> signParamMap = actAljoinTaskSignInfoService.getSignParam(task);
		List<String> elList = actActivitiService.getToNextCondition(taskId);
		// 遍历el表达式，如果表达式以"${targetTask_"开头则需要下个节点的所有表达式都以它开头，程序认为是可以选择下级节点的流程（针对本节点而言）
		for (String el : elList) {
			if (el.startsWith("${targetTask_")) {
				// 可自己选择跳转节点
				// isTask = false;
				isEl++;
			} else {
				// 不能自己跳转节点，通过表达式和表单关系来设置流程变量
				List<ActAljoinFormDataRun> formDataRunList = actActivitiService
						.getFormDataRun(task.getProcessInstanceId());
				Map<String, String> paramMap = actActivitiService.getConditionParamValue(formDataRunList, (String) el);

				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put(entry.getKey(), runMap.get("aljoin_form_" + entry.getKey()));
					runtimeService.setVariables(task.getProcessInstanceId(), variables);
				}
			}
		}
		if (isEl > 0) {
			// 如果el表达式中含有"${targetTask_"开头的流程变量，暂时视为是可选择节点的非自由流程
			processType = 1;
		} else {
			// 否则暂时视为排他节点
			processType = 2;
		}
		// 自由流程，需要回去所有节点（含自己和结束节点）
		if (Integer.parseInt(runMap.get("isFree").toString()) == 1) {
			processType = 3;
			isTask = false;
			// 获取没有完成的任务节点
			// List<HistoricTaskInstance> currentTask =
			// actActivitiService.getCurrentNodeInfo(taskId);
			List<ActivityImpl> actImplList = actActivitiService.getAllActivity(task.getProcessInstanceId());
			// 获取所有任务节点（没有结束节点）---不取自己
			// list =
			// actActivitiService.getAllTaskInfo(task.getProcessInstanceId());
			list = actActivitiService.getAllTaskInfoExcludeSelf(task.getTaskDefinitionKey(),
					task.getProcessInstanceId());
			// 添加结束记节点
			for (ActivityImpl activityImpl : actImplList) {
				if (activityImpl.getId().startsWith("EndEvent_")) {
					TaskDefinition def = new TaskDefinition(null);
					def.setKey(activityImpl.getId());
					Expression expression = new JuelExpression(null, "结束");
					def.setNameExpression(expression);
					list.add(def);
					break;
				}
			}
		} else {
			// 非自由流程，通过正常渠道获取下一个节点或者节点列表
			list = actActivitiService.getNextTaskInfo2(task.getProcessInstanceId(), isEl == 0 ? true : false,
					task.getTaskDefinitionKey());
			// 判断下个节点是否并行网关的结束点
			List<ActivityImpl> actImplList = actActivitiService.getAllActivity(task.getProcessInstanceId());
			if (!task.getExecutionId().equals(task.getProcessInstanceId())) {
				// 执行流ID和流程实例id不相等-》证明当前节点位于子分支中
				for (ActivityImpl activityImpl : actImplList) {
					if (task.getTaskDefinitionKey().equals(activityImpl.getId())) {
						// 如果出口只有一个，并且是连接到并行网关，则标记下个节点为并行网关的结束节点
						if (activityImpl.getOutgoingTransitions().size() == 1 && "parallelGateway".equals(
								activityImpl.getOutgoingTransitions().get(0).getDestination().getProperty("type"))) {
							isFinishMergeTask++;
						}
					}
				}
			}

			if (processType != 1) {
				if (list.size() > 1) {
					// 并行
					processType = 4;
				} else if (list.size() == 1) {
					processType = 2;
				} else {
					throw new Exception("找不到下个节点");
				}
			}
		}
		// 获取节点key,节点名称和几点对应的候选用户ID
		String taskKey = "";
		String taskName = "";
		Map<String, Set<String>> userIdsSetMap = new HashMap<String, Set<String>>();

		// 所有分组ID(含部门，角色，岗位)
		Map<String, Set<String>> allGroupSetMap = new HashMap<String, Set<String>>();
		// 所有候选用户ID（仅含候选用户，不含办理人）
		Map<String, Set<String>> candidateUserIdSetMap = new HashMap<String, Set<String>>();

		// 1.任务类型
		Map<String, String> taskTypeMap = new HashMap<String, String>();
		// 2.打开类型(放到上层处理)
		// Map<String, String> openTypeMap = new HashMap<String, String>();
		// 3.受理人
		Map<String, String> assigneeIdMap = new HashMap<String, String>();
		// 4.当前任务部门列表（含部门下的用户）--任务-部门（也许是角色，岗位）-用户
		Map<String, Map<String, List<String>>> deptListMap = new HashMap<String, Map<String, List<String>>>();

		// 非部门用户ID列表（所有，不分节点）
		Map<String, Set<String>> unDeptUserIdSetMap = new HashMap<String, Set<String>>();

		if (null != list && !list.isEmpty()) {
			List<String> defKeyList = new ArrayList<>();
			for (TaskDefinition definition : list) {
				defKeyList.add(definition.getKey());
			}
			ActAljoinBpmnRun actAljoinBpmnRun = (ActAljoinBpmnRun)runMap.get("actAljoinBpmnRun");
			Where<ActAljoinTaskAssignee> assigneeWhere = new Where<>();
			assigneeWhere.setSqlSelect("id,task_id,assignee_user_ids");
			assigneeWhere.eq("version",actAljoinBpmnRun.getTaskAssigneeVersion());
			assigneeWhere.in("task_id",defKeyList);
			assigneeWhere.eq("bpmn_id",actAljoinBpmnRun.getOrgnlId());
			List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList = actAljoinTaskAssigneeService.selectList(assigneeWhere);
			Map<String,ActAljoinTaskAssignee> assigneeMap = new HashMap<>();
			for(ActAljoinTaskAssignee actAljoinTaskAssignee : actAljoinTaskAssigneeList){
				assigneeMap.put(actAljoinTaskAssignee.getTaskId(),actAljoinTaskAssignee);
			}
			for (TaskDefinition definition : list) {
				if (null != definition) {
					// 默认是普通用户任务
					taskTypeMap.put(definition.getKey(), "1");
					MultiInstanceLoopCharacteristics multi = actActivitiService
							.getMultiInstance(task.getProcessInstanceId(), definition.getKey());
					if (multi != null) {
						if (multi.isSequential()) {
							// 串行多实例
							taskTypeMap.put(definition.getKey(), "3");
						} else if (isTask) {
							// 并行多实例
							taskTypeMap.put(definition.getKey(), "2");
						}
					}

					Set<String> userIdsSet = new HashSet<String>();

					taskKey += definition.getKey() + ",";
					taskName += definition.getNameExpression().getExpressionText() + ",";
					String assignee = "";
					Set<String> unDeptUserIdSet = new HashSet<String>();
					// 获取节点的候选用户、组、办理人信息
					if (definition.getAssigneeExpression() != null
							&& !StringUtils.isEmpty(definition.getAssigneeExpression().getExpressionText())) {
						ActAljoinTaskAssignee actAljoinTaskAssignee = assigneeMap.get(definition.getKey());
						if(null == actAljoinTaskAssignee || StringUtils.isEmpty(actAljoinTaskAssignee.getAssigneeUserIds())){
							assignee = definition.getAssigneeExpression().getExpressionText();
						}else if(!StringUtils.isEmpty(actAljoinTaskAssignee.getAssigneeUserIds())){
							assignee = actAljoinTaskAssignee.getAssigneeUserIds();
						}
						userIdsSet.add(assignee);
						// 如果有受理人则设置受理人
						assigneeIdMap.put(definition.getKey(), assignee);

						unDeptUserIdSet.add(assignee);
					}

					Map<String, List<String>> deptIdUserIdListMap = new HashMap<String, List<String>>();
					Set<String> taskGroupSet = new HashSet<String>();
					Set<Expression> gset = definition.getCandidateGroupIdExpressions();
					// 遍历分组（也许是角色，岗位）列表
					for (Expression expression : gset) {
						// 本部门下的用户ID列表
						List<String> userIdList = new ArrayList<String>();
						List<User> uList = identityService.createUserQuery()
								.memberOfGroup(expression.getExpressionText()).list();
						// 查询是否部门分组
						long count = identityService.createGroupQuery().groupType("DEPARTMENT")
								.groupId(expression.getExpressionText()).count();
						for (User user : uList) {
							userIdsSet.add(user.getId());
							if (count == 1) {
								userIdList.add(user.getId().toString());
							} else {
								// 存放非部门用户
								unDeptUserIdSet.add(user.getId().toString());
							}
						}

						// 是部门才往里面put
						if (count == 1) {
							deptIdUserIdListMap.put(expression.getExpressionText(), userIdList);
						}
						taskGroupSet.add(expression.getExpressionText());
					}

					deptListMap.put(definition.getKey(), deptIdUserIdListMap);
					allGroupSetMap.put(definition.getKey(), taskGroupSet);

					Set<String> candidateUserIdSet = new HashSet<String>();
					Set<Expression> uset = definition.getCandidateUserIdExpressions();
					for (Expression expression : uset) {
						userIdsSet.add(expression.getExpressionText());
						candidateUserIdSet.add(expression.getExpressionText());
						unDeptUserIdSet.add(expression.getExpressionText());
					}
					candidateUserIdSetMap.put(definition.getKey(), candidateUserIdSet);
					userIdsSetMap.put(definition.getKey(), userIdsSet);

					unDeptUserIdSetMap.put(definition.getKey(), unDeptUserIdSet);
				}
			}
		}
		// 构造返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("taskKey", taskKey);
		result.put("taskName", taskName);
		result.put("isTask", isTask);
		result.put("userIdsSetMap", userIdsSetMap);
		result.put("processType", processType);
		result.put("isFinishMergeTask", isFinishMergeTask);

		result.put("taskTypeMap", taskTypeMap);
		result.put("assigneeIdMap", assigneeIdMap);
		result.put("deptListMap", deptListMap);
		result.put("allGroupSetMap", allGroupSetMap);
		result.put("candidateUserIdSetMap", candidateUserIdSetMap);
		result.put("unDeptUserIdSetMap", unDeptUserIdSetMap);
		result.put("isBackOwner", signParamMap.get("isBackOwner"));
		result.put("isSubmit",signParamMap.get("isSubmit"));
		result.put("handler",signParamMap.get("handler"));
		result.put("isAddSign",signParamMap.get("isAddSign"));
		result.put("isPass",signParamMap.get("isPass"));

		// 当前流程实例id
		result.put("currentProcessInstanceId", task.getProcessInstanceId());

		// 是否多实例节点
		result.put("isMultiTask", "0");
		// 该任务完成后是否满足进入下一个节点的条件（控制是否让其选择下一节点意见下一节点的班里人）
		result.put("isMultiTaskCondition", "0");
		MultiInstanceLoopCharacteristics multi = actActivitiService.getMultiInstance(task.getProcessInstanceId(),
				task.getTaskDefinitionKey());
		if (multi != null) {
			// 是否多实例节点
			result.put("isMultiTask", "1");
			// 判断如果当前实例执行，是否满足进入下一个节点的条件
			// 查询出当前执行的数量，检查是否将要完成整个节点进入下一个节点
			// nrOfInstances--总数
			// nrOfCompletedInstances--已完成数
			// nrOfActiveInstances--正在执行数据(没完成数)
			// loopCounter--当前循环索引
			int nrOfInstances = ((Integer) runtimeService.getVariable(task.getExecutionId(), "nrOfInstances"))
					.intValue();
			int nrOfCompletedInstances = ((Integer) runtimeService.getVariable(task.getExecutionId(),
					"nrOfCompletedInstances")).intValue();
			int nrOfActiveInstances = ((Integer) runtimeService.getVariable(task.getExecutionId(),
					"nrOfActiveInstances")).intValue();
			int loopCounter = ((Integer) runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
			Map<String, String> conditionParam = new HashMap<String, String>();
			// 模拟本任务执行完后的参数变化，从而预知流程在本任务执行完后的去向情况，是否达到了往下个节点的条件
			conditionParam.put("nrOfInstances", String.valueOf(nrOfInstances));
			conditionParam.put("nrOfCompletedInstances", String.valueOf(nrOfCompletedInstances + 1));
			if (!multi.isSequential()) {
				// 并行（执行后会减少）
				conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances - 1));
			} else {
				// 串行（总是1）
				conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances));
			}
			conditionParam.put("loopCounter", String.valueOf(loopCounter + 1));
			// 获取任务完成表达式
			String completionCondition = multi.getCompletionCondition();

			boolean isPass = ActUtil.isCondition(conditionParam, completionCondition);
			if (isPass) {
				result.put("isMultiTaskCondition", "1");
			}
		}

		return result;
	}

	@Override
	public Map<String, Object> getTaskUser(String taskId, String nextTaskKey,Long currentUserId) throws Exception {

		TaskVO taskVO = new TaskVO();

		List<SimpleDeptVO> deptList = new ArrayList<SimpleDeptVO>();
		List<String> deptIdList = new ArrayList<String>();
		Map<String, List<String>> deptIdUserIdListMap = new HashMap<String, List<String>>();

		// 获取当前任务
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		MultiInstanceLoopCharacteristics multi = actActivitiService.getMultiInstance(task.getProcessInstanceId(),
				nextTaskKey);
		taskVO.setTaskType("1");
		if (multi != null) {
			if (multi.isSequential()) {
				// 串行
				taskVO.setTaskType("3");
			} else {
				// 并行
				taskVO.setTaskType("2");
			}
		}

		// 获取所有流程节点，判断是否配置返回流程发起人
		Set<String> returnCreaterTaskKeySet = new HashSet<String>();
		// 创建人所在部门人员
		Set<String> staffMembersDepartmentTaskKeySet = new HashSet<String>();
		// 上一个环节办理人人所在部门人员
		Set<String> lastlinkDepartmentTaskKeySet = new HashSet<String>();
		// 创建人所在岗位人员办理
		Set<String> createPersonsJobTaskKeySet = new HashSet<String>();
		AutUser processCreater = null;
		Long bpmnId = Long.valueOf(processInstance.getBusinessKey().split(",")[0]);
		ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
		Where<ActAljoinTaskAssignee> assigneeWhere = new Where<ActAljoinTaskAssignee>();
		assigneeWhere.eq("task_id", nextTaskKey);
		assigneeWhere.eq("is_active", 1);
		assigneeWhere.eq("bpmn_id", actAljoinBpmnRun.getOrgnlId());
		assigneeWhere.eq("version", actAljoinBpmnRun.getTaskAssigneeVersion());
		assigneeWhere.setSqlSelect("task_id,is_return_creater,staff_members_department,lastlink_department,create_persons_job");
		Long createId = null;
		List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList = actAljoinTaskAssigneeService.selectList(assigneeWhere);
		for (ActAljoinTaskAssignee actAljoinTaskAssignee : actAljoinTaskAssigneeList) {
			if (null != actAljoinTaskAssignee.getIsReturnCreater()
				&& actAljoinTaskAssignee.getIsReturnCreater().equals(1)) {
				returnCreaterTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
			}
			if (null != actAljoinTaskAssignee.getStaffMembersDepartment()
				&& actAljoinTaskAssignee.getStaffMembersDepartment().equals(1)) {
				staffMembersDepartmentTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
			}
			if (null != actAljoinTaskAssignee.getLastlinkDepartment()
				&& actAljoinTaskAssignee.getLastlinkDepartment().equals(1)) {
				lastlinkDepartmentTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
			}
			if (null != actAljoinTaskAssignee.getCreatePersonsJob()
				&& actAljoinTaskAssignee.getCreatePersonsJob().equals(1)) {
				createPersonsJobTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
			}
		}
		// 到流程查询表，查询流程发起人(如果下一步节点含有返回发起人的配置才去查询流程的发起人)
		if (returnCreaterTaskKeySet.size() > 0) {
			Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
			queryWhere.eq("process_instance_id", task.getProcessInstanceId());
			ActAljoinQuery query = actAljoinQueryService.selectOne(queryWhere);
			if (query != null) {
				processCreater = new AutUser();
				processCreater.setId(query.getCreateUserId());
				processCreater.setFullName(query.getCreateFullUserName());
				processCreater.setUserName(query.getCreateUserName());
				createId = query.getCreateUserId();
			}
		}

		Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
		taskAssigneeWhere.eq("task_id", nextTaskKey);
		taskAssigneeWhere.eq("is_active", 1);
		taskAssigneeWhere.eq("bpmn_id", actAljoinBpmnRun.getOrgnlId());
		taskAssigneeWhere.eq("version", actAljoinBpmnRun.getTaskAssigneeVersion());
		taskAssigneeWhere.setSqlSelect("assignee_user_ids");
		taskAssigneeWhere.orderBy("id",false);
		ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

		Where<ActAljoinTaskSignInfo> signInfoWhere = new Where<ActAljoinTaskSignInfo>();
		signInfoWhere.setSqlSelect("task_signed_user_id");
		signInfoWhere.eq("task_key",nextTaskKey);
		signInfoWhere.orderBy("id",false);
		ActAljoinTaskSignInfo actAljoinTaskSignInfo = actAljoinTaskSignInfoService.selectOne(signInfoWhere);

		Set<String> unDeptUserSet = new HashSet<String>();
		List<TaskDefinition> list = new ArrayList<TaskDefinition>();
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取所有用户节点
		list = actActivitiService.getAllTaskInfo(task.getProcessInstanceId());
		List<Long> usrIdList = getSelectUserIdList(processInstance.getProcessInstanceId(),taskId,staffMembersDepartmentTaskKeySet,lastlinkDepartmentTaskKeySet,createPersonsJobTaskKeySet,currentUserId);
		Map<String,Object> depatUserMap = getSelectDeptIdList(processInstance.getProcessInstanceId(),taskId,staffMembersDepartmentTaskKeySet,lastlinkDepartmentTaskKeySet,createPersonsJobTaskKeySet,createId,currentUserId);
		List<Long> depatIdList = (List<Long>)depatUserMap.get("deptIdList");
		Map<String,List<SimpleUserVO>> deptUserMap = (Map<String,List<SimpleUserVO>>)depatUserMap.get("deptUserMap");
		for (TaskDefinition definition : list) {
			if (definition.getKey().equals(nextTaskKey)) {
				// 用户id(所有具有权限的用户)
				List<Long> userIdList = new ArrayList<Long>();
				// 分组id
				List<Long> groupIdList = new ArrayList<Long>();


				// 流程分类的候选用户
				Set<Expression> userSet = definition.getCandidateUserIdExpressions();
				for (Expression expression : userSet) {
					userIdList.add(Long.parseLong(expression.getExpressionText()));
					unDeptUserSet.add(expression.getExpressionText());
				}
				// 设置的办理人
				String assigneeUserId = null;
				if (null != definition.getAssigneeExpression()) {
					String assignee = "";
					if(null == actAljoinTaskSignInfo){
						assignee = definition.getAssigneeExpression().getExpressionText();
					}else{
						if(null != actAljoinTaskAssignee && !StringUtils.isEmpty(actAljoinTaskAssignee.getAssigneeUserIds())){
							assignee = actAljoinTaskAssignee.getAssigneeUserIds();
						}
					}

					assigneeUserId = assignee;
					userIdList.add(Long.parseLong(assigneeUserId));
					unDeptUserSet.add(assigneeUserId);
				}
				// 流程分类的候选分组
				Set<Expression> groupSet = definition.getCandidateGroupIdExpressions();
				for (Expression expression : groupSet) {
					groupIdList.add(Long.parseLong(expression.getExpressionText()));
				}

				for (Long gid : groupIdList) {
					// 该分组下的用户ID
					List<String> uidList = new ArrayList<String>();
					// 查询分组小的用户列表
					List<User> ulist = identityService.createUserQuery().memberOfGroup(gid.toString()).list();
					for (User user : ulist) {
						if(null != actAljoinTaskSignInfo && uidList.contains(actAljoinTaskSignInfo.getTaskSignedUserId())){
							uidList.remove(actAljoinTaskSignInfo.getTaskSignedUserId());
						}

						userIdList.add(Long.parseLong(user.getId()));
						uidList.add(user.getId());
					}
					// 判断分组是否部门
					long count = identityService.createGroupQuery().groupId(gid.toString()).groupType("DEPARTMENT")
							.count();
					if (count == 1) {
						// 部门分组
						deptIdList.add(gid.toString());
						// 部门-用户列表
						deptIdUserIdListMap.put(gid.toString(), uidList);
					} else {
						unDeptUserSet.addAll(uidList);
					}
				}

				List<AutUser> userList = new ArrayList<AutUser>();
				if (userIdList.size() > 0) {
					if(null != actAljoinTaskSignInfo && userIdList.contains(actAljoinTaskSignInfo.getTaskSignedUserId())){
						userIdList.remove(actAljoinTaskSignInfo.getTaskSignedUserId());
					}
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.in("id", userIdList);
					userWhere.eq("is_active", 1);
					userWhere.setSqlSelect("id,user_name,full_name");
					userList = autUserService.selectList(userWhere);
				}
				// result.put(definition.getKey(), userList);
				// 把所有用户放到map中
				Map<String, AutUser> userMap = new HashMap<String, AutUser>();
				List<SimpleUserVO> suvList = new ArrayList<SimpleUserVO>();

				// 标志候选用户中是否已经含有流程发起人
				int flag = 0;
				for (AutUser autUser : userList) {
					userMap.put(autUser.getId().toString(), autUser);
					// 构造所有用户列表
					SimpleUserVO suvo = new SimpleUserVO();
					suvo.setUserId(autUser.getId().toString());
					suvo.setUserName(autUser.getFullName());

					if (returnCreaterTaskKeySet.contains(nextTaskKey) && processCreater != null
							&& processCreater.getId().longValue() == autUser.getId().longValue()) {
						// 如果当前节点是有配置返回流程发起人,并且当前用户是流程发起人，则设置标记用户为流程发起人
						suvo.setIsCreater("1");
						flag = 1;
					} else {
						suvo.setIsCreater("0");
					}
					suvList.add(suvo);
				}
				int hasOtherUser = 0;
				if (flag == 0 && returnCreaterTaskKeySet.contains(nextTaskKey) && processCreater != null) {
					// 如果在候选人中没有流程发起人，并且当前节点是有配置返回流程发起人，并且能找到流程发起人
					SimpleUserVO vo = new SimpleUserVO();
					vo.setUserId(processCreater.getId().toString());
					vo.setUserName(processCreater.getFullName());
					vo.setIsCreater("1");
					suvList.add(vo);

					AutUser au = new AutUser();
					au.setId(processCreater.getId());
					au.setUserName(processCreater.getUserName());
					au.setFullName(processCreater.getFullName());
					userList.add(au);

					unDeptUserSet.add(processCreater.getId().toString());

					hasOtherUser = 1;
				}

				result.put(definition.getKey(), userList);
				suvList = extSelectionForUserList(suvList,usrIdList,createId);
				taskVO.setUserList(suvList);

				List<SimpleUserVO> simleUserList = new ArrayList<SimpleUserVO>();
				if(deptIdUserIdListMap.size() > 0){
					// 获取部门下面的所有用户
					for (String s : deptIdList) {
						SimpleDeptVO deptVO = new SimpleDeptVO();
						deptVO.setDeptId(String.valueOf(s));

						// 部门下面用户列表
						List<String> idList = deptIdUserIdListMap.get(s);
						if(null != idList){
							for (String uid : idList) {
								AutUser au = userMap.get(uid);
								if(null != au){
									SimpleUserVO simpleUser = new SimpleUserVO();
									simpleUser.setUserId(au.getId().toString());
									simpleUser.setUserName(au.getFullName());
									simleUserList.add(simpleUser);
								}
							}
						}
						deptVO.setUserList(simleUserList);
						deptList.add(deptVO);
					}
				}
				if(deptList.size()>0){
					deptList = extSelectionForDeptList(deptList,depatUserMap);
					unDeptUserSet = null;
				}
				taskVO.setDeptList(deptList);
				// 获取受理用户
				if (assigneeUserId != null && userMap.get(assigneeUserId) != null) {
					AutUser au = userMap.get(assigneeUserId);
					SimpleUserVO suvo = new SimpleUserVO();
					suvo.setUserId(au.getId().toString());
					suvo.setUserName(au.getFullName());
					taskVO.setDefaultAssigneeUser(suvo);
				}

				String openType = null;
				if (userList.size() == 0 && hasOtherUser == 0) {
					// 没有用户处理，弹出机构树
					openType = "1";
				} else if (taskVO.getDefaultAssigneeUser() != null && groupSet.size() == 0 && userSet.size() == 0
						&& hasOtherUser == 0) {
					// 有受理人,灭有选择部门角色岗位,也没有选择候选人,也乜有选择返回流程发起人
					openType = "2";
				} else if (deptIdList.size() > 0) {
					// 有选择部门，弹出机构树
					openType = "3";
				}  else {
					openType = "4";
				}
				taskVO.setOpenType(openType);
				if(null != actAljoinTaskSignInfo && unDeptUserSet.contains(actAljoinTaskSignInfo.getTaskSignedUserId())){
					unDeptUserSet.remove(actAljoinTaskSignInfo.getTaskSignedUserId());
				}
				taskVO.setUnDeptUserSet(unDeptUserSet);

				// 添加节点其他辅助属性：代开方式，任务类型，默认受理人ID，默认受理人姓名，
				result.put("other_taskvo", taskVO);
			}
		}

		return result;
	}

	@Override
	public RetMsg waitList(PageBean pageBean, String userId, AppWaitTaskVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		Page<AppWaitTaskDO> page = new Page<AppWaitTaskDO>();
		List<AppWaitTaskDO> voList = new ArrayList<AppWaitTaskDO>();

		// 到流程查询表查询满足条件的流程实例ID
		Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
		int flag = makeQueryWhere(where, obj);

		List<ActAljoinQuery> queryList = new ArrayList<ActAljoinQuery>();
		List<String> processInstanceIds = new ArrayList<String>();
		// 如果有查询条件，把查询流程表的数据保存到map中（后面需要用到，不用重复查询）
		Map<String, ActAljoinQuery> actAljoinQueryMap = new HashMap<String, ActAljoinQuery>();
		if (flag > 0) {
			where.setSqlSelect(
					"process_instance_id,urgent_status,process_title,current_handle_full_user_name,start_time,create_full_user_name,process_category_ids");
			queryList = actAljoinQueryService.selectList(where);
			for (ActAljoinQuery query : queryList) {
				processInstanceIds.add(query.getProcessInstanceId());
				actAljoinQueryMap.put(query.getProcessInstanceId(), query);
			}
		}

		String processId = "";
		if (flag > 0 && processInstanceIds.size() <= 0) {
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setObject(page);
			retMsg.setMessage("操作成功");
			return retMsg;
		}
		// 查询收文阅件流程ID
		Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
		configWhere.setSqlSelect("id,process_id");
		configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_READ_RECEIPT);
		List<ActAljoinFixedConfig> configList = actAljoinFixedConfigService.selectList(configWhere);
		if (null != configList && !configList.isEmpty()) {
			processId = configList.get(0).getProcessId();
		}

		// 构造查询条件
		ActRuTask actRuTask = new ActRuTask();
		if (!StringUtils.isEmpty(userId)) {
			actRuTask.setAssignee(userId);
		}
		if (!StringUtils.isEmpty(processId)) {
			actRuTask.setProcDefId(processId);
		}
		if (processInstanceIds.size() > 0) {
			actRuTask.setProcessInstanceIds(processInstanceIds);
		}

		if (null != obj.getReceiveBegTime()) {
			actRuTask.setCreateBegTime(obj.getReceiveBegTime());
		}
		if (null != obj.getReceiveEndTime()) {
			actRuTask.setCreateEndTime(obj.getReceiveEndTime());
		}
		actRuTask.setDbType(aljoinSetting.getDbType());

		// 查询出自己作为候选或者已经签收的任务的分页信息
		Page<ActRuTask> ruTaskPage = actRuTaskService.list(pageBean, actRuTask);
		// 待办任务记录总数
		int count = pageBean.getIsSearchCount().intValue() == 1 ? ruTaskPage.getTotal() : 0;
		// 待办任务列表
		List<ActRuTask> taskList = ruTaskPage.getRecords();

		Map<String, HistoricProcessInstance> historicProcessInstanceMap = new HashMap<String, HistoricProcessInstance>();
		Map<String, String> instanceIdCategoryNameMap = new HashMap<String, String>();
		Map<String, String> taskIdDelegateNameMap = new HashMap<String, String>();
		Map<String, HistoricTaskInstance> taskIdTaskInstanceMap = new HashMap<String, HistoricTaskInstance>();
		Map<String, String> taskIdPreHandleUserName = new HashMap<String, String>();
		Map<String, Set<Long>> taskIdPreHandleUserIdMap = new HashMap<String, Set<Long>>();
		Map<String, String> logMap = new HashMap<String, String>();
		Map<String, Long> lastMap = new HashMap<String, Long>();
		Map<String, String> userIdAndNameMap = new HashMap<String, String>();
		Set<Long> allUserIdSet = new HashSet<Long>();
		if (taskList.size() > 0) {
			// 查询任务的所有流程实例
			Set<String> processInstanceIdSet = new HashSet<String>();
			List<String> processInstanceIdList = new ArrayList<String>();
			List<String> taskIdList = new ArrayList<String>();
			for (ActRuTask task : taskList) {
				processInstanceIdSet.add(task.getProcInstId());
				taskIdList.add(task.getId());
				processInstanceIdList.add(task.getProcInstId());

				// 获取上级办理人
				List<TaskDefinition> defList = actActivitiService.getPreTaskInfo2(task.getTaskDefKey(),
						task.getProcInstId());

				Set<Long> uIdSet = new HashSet<Long>();
				// 对于自由流程，取defList的时候会返回assignee
				for (TaskDefinition def : defList) {
					if (def.getAssigneeExpression() != null
							&& !StringUtils.isEmpty(def.getAssigneeExpression().getExpressionText())) {
						uIdSet.add(Long.parseLong(def.getAssigneeExpression().getExpressionText()));
						allUserIdSet.add(Long.parseLong(def.getAssigneeExpression().getExpressionText()));
					}
				}
				/*
				 * if (uIdSet.size() == 0) { // 如果defList已经待会了办理人信息，就不需要再次查询
				 * List<String> allKeyList = new ArrayList<String>(); for
				 * (TaskDefinition taskDefinition : defList) {
				 * allKeyList.add(taskDefinition.getKey()); }
				 * 
				 * HistoricActivityInstanceQuery query =
				 * historyService.createHistoricActivityInstanceQuery()
				 * .processInstanceId(task.getProcInstId()).finished().
				 * activityType("userTask")
				 * .orderByHistoricActivityInstanceEndTime().desc();
				 * List<HistoricActivityInstance> actList = new
				 * ArrayList<HistoricActivityInstance>();
				 * 
				 * if (defList.size() == 1) { actList =
				 * query.activityId(defList.get(0).getKey()).listPage(0, 1); }
				 * else if (defList.size() > 1) { actList = query.list();
				 * List<HistoricActivityInstance> newActList = new
				 * ArrayList<HistoricActivityInstance>(); for
				 * (HistoricActivityInstance hai : actList) { if
				 * (allKeyList.contains(hai.getActivityId())) {
				 * newActList.add(hai); } } actList = newActList; } else { //
				 * 回退到第一个节点的时候 actList = query.listPage(0, 1); }
				 * 
				 * for (HistoricActivityInstance historicActivityInstance :
				 * actList) { if
				 * (!StringUtils.isEmpty(historicActivityInstance.getAssignee())
				 * ) { uIdSet.add(Long.parseLong(historicActivityInstance.
				 * getAssignee()));
				 * allUserIdSet.add(Long.parseLong(historicActivityInstance.
				 * getAssignee())); } } }
				 */

				taskIdPreHandleUserIdMap.put(task.getId(), uIdSet);
			}

			if (taskIdList != null && taskIdList.size() > 0) {
				Where<ActAljoinActivityLog> actLogWhere = new Where<ActAljoinActivityLog>();
				actLogWhere.in("current_task_id", taskIdList);
				actLogWhere.setSqlSelect("current_task_id,last_task_id");
				List<ActAljoinActivityLog> logList = actAljoinActivityLogService.selectList(actLogWhere);
				String lastTask = "";
				if (logList != null && logList.size() > 0) {

					for (ActAljoinActivityLog actAljoinActivityLog : logList) {
						lastTask = actAljoinActivityLog.getLastTaskId() + ",";
						logMap.put(actAljoinActivityLog.getCurrentTaskId(), actAljoinActivityLog.getLastTaskId());
					}
					if (lastTask != null && "".equals(lastTask)) {
						Where<ActAljoinActivityLog> lastLogWhere = new Where<ActAljoinActivityLog>();
						actLogWhere.in("current_task_id", lastTask);
						actLogWhere.setSqlSelect("current_task_id,last_task_id");
						List<ActAljoinActivityLog> lastLogList = actAljoinActivityLogService.selectList(lastLogWhere);

						for (ActAljoinActivityLog actAljoinActivityLog : lastLogList) {
							allUserIdSet.add(Long.valueOf(actAljoinActivityLog.getOperateUserId()));
							lastMap.put(actAljoinActivityLog.getCurrentTaskId(),
									Long.valueOf(actAljoinActivityLog.getOperateUserId()));
						}
					}
				}
			}

			// 一次查询所有上次办理人
			if (allUserIdSet.size() > 0) {
				Where<AutUser> autUserWhere = new Where<AutUser>();
				autUserWhere.in("id", allUserIdSet);
				autUserWhere.setSqlSelect("id,full_name");
				List<AutUser> autUserList = autUserService.selectList(autUserWhere);

				for (AutUser autUser : autUserList) {
					userIdAndNameMap.put(autUser.getId().toString(), autUser.getFullName());
				}
				for (Map.Entry<String, Set<Long>> entry : taskIdPreHandleUserIdMap.entrySet()) {
					Set<Long> set = entry.getValue();
					String userFullNames = "";
					for (Long uid : set) {
						userFullNames += userIdAndNameMap.get(uid.toString()) + ",";
					}
					if (!"".equals(userFullNames)) {
						userFullNames = userFullNames.substring(0, userFullNames.length() - 1);
					}
					taskIdPreHandleUserName.put(entry.getKey(), userFullNames);
				}

			}

			List<HistoricProcessInstance> historicProcessInstanceList = historyService
					.createHistoricProcessInstanceQuery().processInstanceIds(processInstanceIdSet).list();

			for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
				historicProcessInstanceMap.put(historicProcessInstance.getId(), historicProcessInstance);
			}
			// 如果上的查询条件没有流程查询表的数据，在去取，如果上面的流程查询map中有数据，则不需要在去查询
			if (actAljoinQueryMap.size() == 0) {
				Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
				queryWhere.setSqlSelect(
						"process_instance_id,urgent_status,process_title,current_handle_full_user_name,start_time,create_full_user_name,process_category_ids");
				queryWhere.in("process_instance_id", processInstanceIdSet);
				List<ActAljoinQuery> actAljoinQueryList = actAljoinQueryService.selectList(queryWhere);
				for (ActAljoinQuery actAljoinQuery : actAljoinQueryList) {
					actAljoinQueryMap.put(actAljoinQuery.getProcessInstanceId(), actAljoinQuery);
				}
			}
			// 一次性获取流程分类
			List<Long> categoryIdList = new ArrayList<Long>();

			for (Map.Entry<String, ActAljoinQuery> entry : actAljoinQueryMap.entrySet()) {
				ActAljoinQuery aq = entry.getValue();
				String categoryIds = aq.getProcessCategoryIds();
				if (!StringUtils.isEmpty(categoryIds)) {
					String[] categoryIdsArr = categoryIds.split(",");
					categoryIds = categoryIdsArr[categoryIdsArr.length - 1];
					instanceIdCategoryNameMap.put(entry.getKey(), categoryIds);
					categoryIdList.add(Long.parseLong(categoryIds));
				}

			}
			Where<ActAljoinCategory> actAljoinCategoryWhere = new Where<ActAljoinCategory>();
			actAljoinCategoryWhere.in("id", categoryIdList);
			actAljoinCategoryWhere.setSqlSelect("id,category_name");
			List<ActAljoinCategory> actAljoinCategoryList = actAljoinCategoryService.selectList(actAljoinCategoryWhere);
			for (ActAljoinCategory actAljoinCategory : actAljoinCategoryList) {
				for (Map.Entry<String, String> entry : instanceIdCategoryNameMap.entrySet()) {
					if ((actAljoinCategory.getId().toString()).equals(entry.getValue())) {
						instanceIdCategoryNameMap.put(entry.getKey(), actAljoinCategory.getCategoryName());
					}
				}
			}

			// 根据任务ID获取委托人信息
			Where<ActAljoinDelegateInfo> actAljoinDelegateInfoWhere = new Where<ActAljoinDelegateInfo>();
			actAljoinDelegateInfoWhere.in("task_id", taskIdList);
			actAljoinDelegateInfoWhere.eq("assignee_user_id", userId);
			actAljoinDelegateInfoWhere.setSqlSelect("task_id,delegate_user_names");
			List<ActAljoinDelegateInfo> actAljoinDelegateInfoList = actAljoinDelegateInfoService
					.selectList(actAljoinDelegateInfoWhere);
			for (ActAljoinDelegateInfo actAljoinDelegateInfo : actAljoinDelegateInfoList) {
				String mapValue = taskIdDelegateNameMap.get(actAljoinDelegateInfo.getTaskId());
				if (StringUtils.isEmpty(mapValue)) {
					taskIdDelegateNameMap.put(actAljoinDelegateInfo.getTaskId(),
							actAljoinDelegateInfo.getDelegateUserNames());
				} else {
					mapValue = mapValue + "," + actAljoinDelegateInfo.getDelegateUserNames();
					taskIdDelegateNameMap.put(actAljoinDelegateInfo.getTaskId(), mapValue);
				}
			}
			for (Map.Entry<String, String> entry : taskIdDelegateNameMap.entrySet()) {
				String key = entry.getKey();
				String[] delegateInfoArr = taskIdDelegateNameMap.get(key).split(",");
				List<String> userNameList = new ArrayList<String>();
				for (int i = 0; i < delegateInfoArr.length; i++) {
					if (!userNameList.contains(delegateInfoArr[i])) {
						userNameList.add(delegateInfoArr[i]);
					}
				}
				int n = 0;
				String newDelegateInfo = "";
				for (String s : userNameList) {
					if (n == 0) {
						newDelegateInfo = s;
					} else {
						newDelegateInfo += "," + s;
					}
					n++;
				}
				if (!"".equals(newDelegateInfo)) {
					newDelegateInfo = "[委托人(" + newDelegateInfo + ")]";
				}
				taskIdDelegateNameMap.put(key, newDelegateInfo);
			}
			// 查询所有任务节点信息
			List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
					.processInstanceIdIn(processInstanceIdList).list();
			for (HistoricTaskInstance historicTaskInstance : taskInstanceList) {
				taskIdTaskInstanceMap.put(historicTaskInstance.getId(), historicTaskInstance);
			}

		}

		for (ActRuTask task : taskList) {
			// 根据流程实例ID获取待办流程实例
			HistoricProcessInstance historicProcessInstance = historicProcessInstanceMap.get(task.getProcInstId());
			// 从上面的map中获取ActAljoinQuery
			ActAljoinQuery actAljoinQuery = actAljoinQueryMap.get(task.getProcInstId());

			if (null != actAljoinQuery) {
				Integer uegency = 1;
				AppWaitTaskDO waitTaskShowVO = new AppWaitTaskDO();
				waitTaskShowVO.setTaskId(task.getId());
				waitTaskShowVO.setFormType(instanceIdCategoryNameMap.get(task.getProcInstId()));// 流程分类
				String str = historicProcessInstance.getBusinessKey();
				waitTaskShowVO.setBusinessKey(str);

				waitTaskShowVO.setUrgency(actAljoinQuery.getUrgentStatus());// 缓急
				if (actAljoinQuery.getUrgentStatus().equals(WebConstant.COMMONLY)) {
					uegency = 1;
				} else if (actAljoinQuery.getUrgentStatus().equals(WebConstant.URGENT)) {
					uegency = 2;
				} else if (actAljoinQuery.getUrgentStatus().equals(WebConstant.ADD_URENT)) {
					uegency = 3;
				}
				waitTaskShowVO.setUrgencyStatus(uegency);// 缓急转换为数字 用于排序
				waitTaskShowVO.setTitle(actAljoinQuery.getProcessTitle());// 标题
				waitTaskShowVO.setFounder(actAljoinQuery.getCreateFullUserName());// 流程发起人
				waitTaskShowVO.setReceiveTime(task.getCreateTime());// 任务开始时间

				// waitTaskShowVO.setFormerManager(actAljoinQuery.getCurrentHandleFullUserName());//
				// 前办理人
				// waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));//
				// 前办理人

				if (logMap.containsKey(task.getId())) {
					String lastID = logMap.get(task.getId());
					if (lastMap.containsKey(lastID)) {
						Long userIds = lastMap.get(lastID);
						if (userIdAndNameMap.containsKey(userIds.toString())) {
							waitTaskShowVO.setFormerManager(userIdAndNameMap.get(userIds.toString()));
						} else {
							waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
						}
					} else {
						waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
					}
				} else {
					waitTaskShowVO.setFormerManager(taskIdPreHandleUserName.get(task.getId()));// 前办理人
				}

				// 查询当前用户对于该任务是否被别人委派的，如果是，在节点后面追加委派人
				waitTaskShowVO.setLink(task.getName() == null ? ""
						: task.getName() + (taskIdDelegateNameMap.get(task.getId()) == null ? ""
								: taskIdDelegateNameMap.get(task.getId())));// 环节

				HistoricTaskInstance taskInstance = taskIdTaskInstanceMap.get(task.getId());
				if (!StringUtils.isEmpty(task.getAssignee())) {
					waitTaskShowVO.setSignInTime(taskInstance.getClaimTime());// 签收时间
				}
				waitTaskShowVO.setProcessInstanceId(task.getProcInstId());// 流程实例ID

				String bpmnId = "";
				if (!StringUtils.isEmpty(str)) {
					String[] key = str.split(",");
					if (key.length >= 1) {
						bpmnId = key[0];
					}
				}
				waitTaskShowVO.setBpmnId(bpmnId);
				waitTaskShowVO.setTaskDefKey(task.getTaskDefKey());
				voList.add(waitTaskShowVO);
			} else {
				count--;
			}
		}
		sortList(voList, obj);
		page.setRecords(voList);
		page.setTotal(count);
		page.setSize(pageBean.getPageSize());
		page.setCurrent(pageBean.getPageNum());
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		retMsg.setObject(page);
		return retMsg;
	}

	/**
	 * 待办列表排序. @return：void
	 * 
	 * @author：wangj @date：2017-12-02
	 */
	public void sortList(List<WaitTaskShowVO> list, WaitTaskShowVO obj) {
		// 填报时间升序
		if (!StringUtils.isEmpty(obj.getFillingDateIsAsc()) && obj.getFillingDateIsAsc().equals("1")) {
			Comparator<WaitTaskShowVO> waitTaskShowVOComparator = new Comparator<WaitTaskShowVO>() {
				@Override
				public int compare(WaitTaskShowVO o1, WaitTaskShowVO o2) {
					// 升序
					return o1.getFillingDate().compareTo(o2.getFillingDate());
				}
			};
			Collections.sort(list, waitTaskShowVOComparator);
		}

		// 紧急 降序
		if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("0")) {
			Comparator<WaitTaskShowVO> waitTaskShowVOComparator = new Comparator<WaitTaskShowVO>() {
				@Override
				public int compare(WaitTaskShowVO o1, WaitTaskShowVO o2) {
					if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o1.getUrgencyStatus() - o2.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o1.getUrgencyStatus() - o2.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o1.getUrgencyStatus() - o2.getUrgencyStatus();
					}
					return 0;
				}
			};
			Collections.sort(list, waitTaskShowVOComparator);
		} else if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("1")) {// 紧急
			// 升序
			Comparator<WaitTaskShowVO> attSignInCountComparator = new Comparator<WaitTaskShowVO>() {
				@Override
				public int compare(WaitTaskShowVO o1, WaitTaskShowVO o2) {
					if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o2.getUrgencyStatus() - o1.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o2.getUrgencyStatus() - o1.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o2.getUrgencyStatus() - o1.getUrgencyStatus();
					}
					return 0;
				}
			};
			Collections.sort(list, attSignInCountComparator);
		}
	}

	/*
	 * 构造待办查询条件
	 */
	public int makeQueryWhere(Where<ActAljoinQuery> where, AppWaitTaskVO obj) {
		int flag = 0;
		if (!StringUtils.isEmpty(obj.getTitle())) {
			where.like("process_title", obj.getTitle());
			flag++;
		}

		if (!StringUtils.isEmpty(obj.getUrgency())) {
			where.eq("urgent_status", obj.getUrgency());
			flag++;
		}

		if (!StringUtils.isEmpty(obj.getFounder())) {
			where.like("create_full_user_name", obj.getFounder());
			flag++;
		}

		if (!StringUtils.isEmpty(obj.getFormTypeId())) {
			List<Long> categorySet = new ArrayList<Long>();
			categorySet = getChildCategoryIds(categorySet, Long.valueOf(obj.getFormTypeId()));
			int i = 0;
			for (Long set : categorySet) {
				if (i == 0) {
					where.andNew("process_category_ids like {0}", "%" + set + "%");
				} else if (i > 0) {
					where.or("process_category_ids like {0}", "%" + set + "%");
				}
				i++;
			}
			flag++;
		}
		return flag;
	}

	/**
	 *
	 * 根据父级分类ID获得所有子分类
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-19
	 */
	private List<Long> getChildCategoryIds(List<Long> categorySet, Long categoryId) {
		ActAljoinCategory actAljoinCategory = actAljoinCategoryService.selectById(categoryId);
		List<Long> categoryIdList = new ArrayList<Long>();
		if (null != actAljoinCategory) {
			categoryIdList.add(actAljoinCategory.getId());

			// 获取原来的值和现在的值
			categorySet.addAll(categoryIdList);

			// 获取子分类
			Where<ActAljoinCategory> categoryWhere = new Where<ActAljoinCategory>();
			categoryWhere.setSqlSelect("id,parent_id");
			categoryWhere.eq("parent_id", actAljoinCategory.getId());
			categoryWhere.ne("parent_id", 0);
			categoryWhere.eq("is_active", 1);
			List<ActAljoinCategory> categoryList = actAljoinCategoryService.selectList(categoryWhere);
			if (null != categoryList && !categoryList.isEmpty()) {
				for (ActAljoinCategory category : categoryList) {
					getChildCategoryIds(categorySet, category.getId());
				}
			}
		}
		Map<Long, Long> map = new HashMap<Long, Long>();
		for (Long cid : categorySet) {
			map.put(cid, cid);
		}
		categorySet.clear();
		for (Long key : map.keySet()) {
			categorySet.add(map.get(key));
		}
		Collections.sort(categorySet);
		return categorySet;
	}

	/**
	 * App待办列表排序. @return：void
	 * 
	 * @author：wangj @date：2017-12-19
	 */
	public void sortList(List<AppWaitTaskDO> list, AppWaitTaskVO obj) {
		// 填报时间升序
		if (!StringUtils.isEmpty(obj.getReceiveTimeIsAsc()) && obj.getReceiveTimeIsAsc().equals("1")) {
			Comparator<AppWaitTaskDO> waitTaskShowVOComparator = new Comparator<AppWaitTaskDO>() {
				@Override
				public int compare(AppWaitTaskDO o1, AppWaitTaskDO o2) {
					// 升序
					return o1.getReceiveTime().compareTo(o2.getReceiveTime());
				}
			};
			Collections.sort(list, waitTaskShowVOComparator);
		}

		// 紧急 降序
		if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("0")) {
			Comparator<AppWaitTaskDO> waitTaskShowVOComparator = new Comparator<AppWaitTaskDO>() {
				@Override
				public int compare(AppWaitTaskDO o1, AppWaitTaskDO o2) {
					if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o1.getUrgencyStatus() - o2.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o1.getUrgencyStatus() - o2.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o1.getUrgencyStatus() - o2.getUrgencyStatus();
					}
					return 0;
				}
			};
			Collections.sort(list, waitTaskShowVOComparator);
		} else if (!StringUtils.isEmpty(obj.getUrgencyIsAsc()) && obj.getUrgencyIsAsc().equals("1")) {// 紧急
			// 升序
			Comparator<AppWaitTaskDO> attSignInCountComparator = new Comparator<AppWaitTaskDO>() {
				@Override
				public int compare(AppWaitTaskDO o1, AppWaitTaskDO o2) {
					if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o2.getUrgencyStatus() - o1.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o2.getUrgencyStatus() - o1.getUrgencyStatus();
					} else if (o1.getUrgencyStatus() != o2.getUrgencyStatus()) {
						return o2.getUrgencyStatus() - o1.getUrgencyStatus();
					}
					return 0;
				}
			};
			Collections.sort(list, attSignInCountComparator);
		}
	}

	@Override
	@Transactional
	public RetMsg appClaimTask(String userId, String ids) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (!StringUtils.isEmpty(ids)) {
			// 待签收任务id
			List<String> taskIds = Arrays.asList(ids.split(","));
			if (taskIds != null) {
				for (String taskId : taskIds) {
					Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
					HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
					if (null != task) {
						if (null == hisTask.getClaimTime() || StringUtils.isEmpty(hisTask.getAssignee())) {
							taskService.claim(taskId, userId);
						}
					} else {
						retMsg.setCode(AppConstant.RET_CODE_ERROR);
						retMsg.setMessage("该任务已被撤回不能签收!");
						return retMsg;
					}
				}

				// 任务对应的流程实例id
				List<String> processInstanceIds = new ArrayList<String>();

				Where<ActHiTaskinst> acthiProcinstWhere = new Where<ActHiTaskinst>();
				acthiProcinstWhere.setSqlSelect("PROC_INST_ID_");
				acthiProcinstWhere.in("ID_", taskIds);
				List<ActHiTaskinst> actHiProcinsts = actHiTaskinstService.selectList(acthiProcinstWhere);
				for (ActHiTaskinst actHiProcinst : actHiProcinsts) {
					// 如果任务是会签任务，签收后也不要更改当前的那些办理人
					MultiInstanceLoopCharacteristics multi = actActivitiService
							.getMultiInstance(actHiProcinst.getProcInstId(), actHiProcinst.getTaskDefKey());
					if (multi == null) {
						processInstanceIds.add(actHiProcinst.getProcInstId());
					}
				}
				if (processInstanceIds.size() > 0) {
					Set<String> assigneeSet = new HashSet<String>();
					Set<Long> assigneeIdSet = new HashSet<Long>();
					// 构造出流程实例+任务+办理人的关系
					List<Task> processTasks = taskService.createTaskQuery().processInstanceIdIn(processInstanceIds)
							.list();
					Map<String, List<String>> proUsersIdMap = new HashMap<String, List<String>>();
					for (String procinstId : processInstanceIds) {
						List<String> processTaskAssignees = new ArrayList<String>();
						for (Task processTask : processTasks) {
							if (processTask.getProcessInstanceId().equals(procinstId)) {
								assigneeSet.add(processTask.getAssignee());
								if (!StringUtils.isEmpty(processTask.getAssignee())) {
									assigneeIdSet.add(Long.valueOf(processTask.getAssignee()));
								}
								processTaskAssignees.add(processTask.getAssignee());
							}
						}
						if (processTaskAssignees.size() > 0) {
							proUsersIdMap.put(procinstId, processTaskAssignees);
						}
					}

					Where<AutUser> userwhere = new Where<AutUser>();
					userwhere.setSqlSelect("id, full_name");
					userwhere.in("id", assigneeIdSet);
					List<AutUser> userList = autUserService.selectList(userwhere);
					HashMap<String, String> map = new HashMap<String, String>();
					for (String procinst : processInstanceIds) {
						List<String> proUsersId = proUsersIdMap.get(procinst);
						String userName = "";
						for (AutUser autUser : userList) {
							if (proUsersId.contains(String.valueOf(autUser.getId()))) {
								userName += autUser.getFullName() + ",";
							}
						}
						map.put(procinst, userName);
					}
					actAljoinQueryService.updateAssigneeNameBatch(map, processInstanceIds);
				}
			}
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("签收成功");
		return retMsg;
	}

	@Override
	public Map<String, Object> getAppTaskUser(String taskId, String nextTaskKey) throws Exception {
		TaskVO taskVO = new TaskVO();

		List<SimpleDeptVO> deptList = new ArrayList<SimpleDeptVO>();
		List<String> deptIdList = new ArrayList<String>();
		Map<String, List<String>> deptIdUserIdListMap = new HashMap<String, List<String>>();

		// 获取当前任务
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(task.getProcessInstanceId()).singleResult();

		MultiInstanceLoopCharacteristics multi = actActivitiService.getMultiInstance(task.getProcessInstanceId(),
				nextTaskKey);
		taskVO.setTaskType("1");
		if (multi != null) {
			if (multi.isSequential()) {
				// 串行
				taskVO.setTaskType("3");
			} else {
				// 并行
				taskVO.setTaskType("2");
			}
		}

		// 获取所有流程节点，判断是否配置返回流程发起人
		Set<String> returnCreaterTaskKeySet = new HashSet<String>();
		AutUser processCreater = null;
		Where<ActAljoinTaskAssignee> assigneeWhere = new Where<ActAljoinTaskAssignee>();
		assigneeWhere.eq("task_id", nextTaskKey);
		assigneeWhere.eq("is_active", 1);
		assigneeWhere.eq("bpmn_id", processInstance.getBusinessKey().split(",")[0]);
		assigneeWhere.eq("is_return_creater", 1);
		assigneeWhere.setSqlSelect("task_id");
		List<ActAljoinTaskAssignee> actAljoinTaskAssigneeList = actAljoinTaskAssigneeService.selectList(assigneeWhere);
		for (ActAljoinTaskAssignee actAljoinTaskAssignee : actAljoinTaskAssigneeList) {
			returnCreaterTaskKeySet.add(actAljoinTaskAssignee.getTaskId());
		}
		// 到流程查询表，查询流程发起人(如果下一步节点含有返回发起人的配置才去查询流程的发起人)
		if (returnCreaterTaskKeySet.size() > 0) {
			Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
			queryWhere.eq("process_instance_id", task.getProcessInstanceId());
			ActAljoinQuery query = actAljoinQueryService.selectOne(queryWhere);
			if (query != null) {
				processCreater = new AutUser();
				processCreater.setId(query.getCreateUserId());
				processCreater.setFullName(query.getCreateFullUserName());
				processCreater.setUserName(query.getCreateUserName());
			}
		}

		Set<String> unDeptUserSet = new HashSet<String>();
		List<TaskDefinition> list = new ArrayList<TaskDefinition>();
		Map<String, Object> result = new HashMap<String, Object>();
		// 获取所有用户节点
		list = actActivitiService.getAllTaskInfo(task.getProcessInstanceId());

		for (TaskDefinition definition : list) {
			if (definition.getKey().equals(nextTaskKey)) {
				// 用户id(所有具有权限的用户)
				List<Long> userIdList = new ArrayList<Long>();
				// 分组id
				List<Long> groupIdList = new ArrayList<Long>();

				// 流程分类的候选用户
				Set<Expression> userSet = definition.getCandidateUserIdExpressions();
				for (Expression expression : userSet) {
					userIdList.add(Long.parseLong(expression.getExpressionText()));
					unDeptUserSet.add(expression.getExpressionText());
				}
				// 设置的办理人
				String assigneeUserId = null;
				if (null != definition.getAssigneeExpression()) {
					userIdList.add(Long.parseLong(definition.getAssigneeExpression().getExpressionText()));
					assigneeUserId = definition.getAssigneeExpression().getExpressionText();
					unDeptUserSet.add(assigneeUserId);
				}
				// 流程分类的候选分组
				Set<Expression> groupSet = definition.getCandidateGroupIdExpressions();
				for (Expression expression : groupSet) {
					groupIdList.add(Long.parseLong(expression.getExpressionText()));
				}
				for (Long gid : groupIdList) {
					// 该分组下的用户ID
					List<String> uidList = new ArrayList<String>();
					// 查询分组小的用户列表
					List<User> ulist = identityService.createUserQuery().memberOfGroup(gid.toString()).list();
					for (User user : ulist) {
						userIdList.add(Long.parseLong(user.getId()));
						uidList.add(user.getId());
					}
					// 判断分组是否部门
					long count = identityService.createGroupQuery().groupId(gid.toString()).groupType("DEPARTMENT")
							.count();
					if (count == 1) {
						// 部门分组
						deptIdList.add(gid.toString());
						// 部门-用户列表
						deptIdUserIdListMap.put(gid.toString(), uidList);
					} else {
						unDeptUserSet.addAll(uidList);
					}
				}
				List<AutUser> userList = new ArrayList<AutUser>();
				if (userIdList.size() > 0) {
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.in("id", userIdList);
					userWhere.eq("is_active", 1);
					userWhere.setSqlSelect("id,user_name,full_name");
					userList = autUserService.selectList(userWhere);
				}
				// result.put(definition.getKey(), userList);
				// 把所有用户放到map中
				Map<String, AutUser> userMap = new HashMap<String, AutUser>();
				List<SimpleUserVO> suvList = new ArrayList<SimpleUserVO>();

				// 标志候选用户中是否已经含有流程发起人
				int flag = 0;
				for (AutUser autUser : userList) {
					userMap.put(autUser.getId().toString(), autUser);
					// 构造所有用户列表
					SimpleUserVO suvo = new SimpleUserVO();
					suvo.setUserId(autUser.getId().toString());
					suvo.setUserName(autUser.getFullName());

					if (returnCreaterTaskKeySet.contains(nextTaskKey) && processCreater != null
							&& processCreater.getId().longValue() == autUser.getId().longValue()) {
						// 如果当前节点是有配置返回流程发起人,并且当前用户是流程发起人，则设置标记用户为流程发起人
						suvo.setIsCreater("1");
						flag = 1;
					} else {
						suvo.setIsCreater("0");
					}
					suvList.add(suvo);
				}
				int hasOtherUser = 0;
				if (flag == 0 && returnCreaterTaskKeySet.contains(nextTaskKey) && processCreater != null) {
					// 如果在候选人中没有流程发起人，并且当前节点是有配置返回流程发起人，并且能找到流程发起人
					SimpleUserVO vo = new SimpleUserVO();
					vo.setUserId(processCreater.getId().toString());
					;
					vo.setUserName(processCreater.getFullName());
					vo.setIsCreater("1");
					suvList.add(vo);

					AutUser au = new AutUser();
					au.setId(processCreater.getId());
					au.setUserName(processCreater.getUserName());
					au.setFullName(processCreater.getFullName());
					userList.add(au);

					unDeptUserSet.add(processCreater.getId().toString());

					hasOtherUser = 1;
				}

				// result.put(definition.getKey(), userList);
				taskVO.setUserList(suvList);
				// 获取部门下面的所有用户
				for (String s : deptIdList) {
					SimpleDeptVO deptVO = new SimpleDeptVO();
					deptVO.setDeptId(s);
					List<SimpleUserVO> simleUserList = new ArrayList<SimpleUserVO>();
					// 部门下面用户列表
					List<String> idList = deptIdUserIdListMap.get(s);
					for (String uid : idList) {
						AutUser au = userMap.get(uid);
						SimpleUserVO simpleUser = new SimpleUserVO();
						simpleUser.setUserId(au.getId().toString());
						simpleUser.setUserName(au.getFullName());
						simleUserList.add(simpleUser);
					}
					deptVO.setUserList(simleUserList);
					deptList.add(deptVO);
				}
				taskVO.setDeptList(deptList);

				// 获取受理用户
				if (assigneeUserId != null && userMap.get(assigneeUserId) != null) {
					AutUser au = userMap.get(assigneeUserId);
					SimpleUserVO suvo = new SimpleUserVO();
					suvo.setUserId(au.getId().toString());
					suvo.setUserName(au.getFullName());
					taskVO.setDefaultAssigneeUser(suvo);
				}

				String openType = null;
				if (userList.size() == 0 && hasOtherUser == 0) {
					// 没有用户处理，弹出机构树
					openType = "1";
				} else if (taskVO.getDefaultAssigneeUser() != null && groupSet.size() == 0 && userSet.size() == 0
						&& hasOtherUser == 0) {
					// 有受理人,灭有选择部门角色岗位,也没有选择候选人,也乜有选择返回流程发起人
					openType = "2";
				} else if (deptIdList.size() > 0) {
					// 有选择部门，弹出机构树
					openType = "3";
				} else {
					openType = "4";
				}
				taskVO.setOpenType(openType);
				taskVO.setUnDeptUserSet(unDeptUserSet);

				// 添加节点其他辅助属性：代开方式，任务类型，默认受理人ID，默认受理人姓名，
				SimpleUserVO defaultAssigneeUser = new SimpleUserVO();
				List<SimpleUserVO> usrList = new ArrayList<SimpleUserVO>();
				List<SimpleDeptVO> departList = new ArrayList<SimpleDeptVO>();
				Set<String> unDepartUserSet = new HashSet<String>();
				if (null != taskVO) {
					defaultAssigneeUser = taskVO.getDefaultAssigneeUser();
					usrList = taskVO.getUserList();
					departList = taskVO.getDeptList();
					unDepartUserSet = taskVO.getUnDeptUserSet();
				}
				if (departList.size() == 0 && usrList.size() == 0 && defaultAssigneeUser == null
						&& unDepartUserSet.size() == 0) {
					AutOrganVO organVO = new AutOrganVO();
					AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
					organVO = autDepartmentUserService.getOrganList(departmentUserVO);
					result.put("organ", organVO);
				}
				if (departList.size() > 0) {// 组织机构树
					AutOrganVO organVO = new AutOrganVO();
					AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
					String deptIds = "";
					String userIds = "";
					List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();
					for (SimpleDeptVO simpleDeptVO : deptList) {
						deptIds += simpleDeptVO.getDeptId() + ";";
					}
					if (deptUserList.size() > 0) {
						for (SimpleUserVO simpleUserVO : deptUserList) {
							userIds += simpleUserVO.getUserId() + ";";
						}
					}
					if (userList.size() > 0) {
						for (SimpleUserVO simpleUserVO : usrList) {
							userIds += simpleUserVO.getUserId() + ";";
						}
					}
					if (!StringUtils.isEmpty(deptIds)) {
						departmentUserVO.setDepartmentIds(deptIds);
					}
					if (!StringUtils.isEmpty(userIds)) {
						departmentUserVO.setUserIds(userIds);
					}
					organVO = autDepartmentUserService.getOrganList(departmentUserVO);
					result.put("organ", organVO);
				} else {// 用户列表
					if (null != defaultAssigneeUser) {
						result.put("defaultAssigneeUser", defaultAssigneeUser);
					}
					List<SimpleUserVO> deptUserList = new ArrayList<SimpleUserVO>();
					if (usrList.size() > 0 && unDepartUserSet.size() > 0) {
						Iterator<String> it = unDepartUserSet.iterator();
						while (it.hasNext()) {
							SimpleUserVO simpleUserVO = new SimpleUserVO();
							simpleUserVO.setUserId(it.next());
							deptUserList.add(simpleUserVO);
						}
						List<Long> uIdList = new ArrayList<Long>();
						for (SimpleUserVO simpleUserVO : deptUserList) {
							uIdList.add(Long.valueOf(simpleUserVO.getUserId()));
						}
						Where<AutUser> userWhere = new Where<AutUser>();
						userWhere.setSqlSelect("id,user_name,full_name");
						userWhere.in("id", uIdList);
						List<AutUser> userList1 = autUserService.selectList(userWhere);
						for (SimpleUserVO simpleUserVO : deptUserList) {
							for (AutUser user : userList1) {
								if (String.valueOf(user.getId()).equals(simpleUserVO.getUserId())) {
									simpleUserVO.setUserName(user.getFullName());
								}
							}
						}
						usrList.addAll(deptUserList);
					}

					if (usrList.size() > 0) {
						result.put("userList", usrList);
					}
				}
			}
		}
		return result;
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
	@Override
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
			List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
					.processInstanceId(processInstanceId).orderByHistoricTaskInstanceEndTime().desc().list();
			int i = 0;
			String thistaskId = historicTaskInstanceList.get(i).getId(); // 当前环节id
			String pretaskId = historicTaskInstanceList.get(i + 1).getId(); // 上一环节id
			Task tk = taskService.createTaskQuery().taskId(thistaskId).singleResult();// 当前环节是否被签收
			if (null != tk) {
				HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(thistaskId).singleResult();
				if (null != hisTask.getClaimTime() && !StringUtils.isEmpty(hisTask.getAssignee())) {
					// 任务已被办理人签收不可撤回
					retMsg.setCode(1);
					retMsg.setMessage("任务已被办理人签收不可撤回");
					return retMsg;
				}
				HistoricTaskInstance histask = historyService.createHistoricTaskInstanceQuery()
						.processInstanceId(processInstanceId).taskId(pretaskId)
						.taskAssignee(String.valueOf(createUserId)).singleResult();
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
						if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
							List<TaskDefinition> preList = actActivitiService.getPreTaskInfo(currentTaskKey,
									processInstanceId);
							if (preList.size() > 0) {

								// 历史上级节点
								HistoricTaskInstance preTask = historyService.createHistoricTaskInstanceQuery()
										.taskId(pretaskId).singleResult();
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
									// （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
									actFixedFormService.deleteOrgnlTaskAuth(task);
									HistoricTaskInstance hisTsk = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
									if (null != hisTsk.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
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
								}
							}
						}
					}
				}
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public RetMsg isMyTask(String taskid, String userID) throws Exception {
		RetMsg retMsg = new RetMsg();
		Task task = taskService.createTaskQuery().taskId(taskid).singleResult();
		Boolean isMe = true;// 签收人是否是自己后者获选人
		if (task != null) {
			if (task.getAssignee() != null) {
				String assUser = task.getAssignee();
				if (assUser.equals(userID)) {
					isMe = true;
				} else {
					isMe = false;
				}
			} else {
				isMe = false;
				String userids = "";
				// String deptId = "";
				Where<ActRuIdentitylink> ruIdentitylinkWhere = new Where<ActRuIdentitylink>();
				ruIdentitylinkWhere.eq("TASK_ID_", taskid);
				List<ActRuIdentitylink> linkList = actRuIdentitylinkService.selectList(ruIdentitylinkWhere);
				// 获取当前环节候选人
				if (linkList != null) {
					for (ActRuIdentitylink identityLink : linkList) {
						if (identityLink.getUserId() != null && !"".equals(identityLink.getUserId())) {
							userids = userids + identityLink.getUserId() + ",";
							continue;
						}
						if (identityLink.getGroupId() != null && !"".equals(identityLink.getGroupId())) {
							List<User> userList = identityService.createUserQuery()
									.memberOfGroup(identityLink.getGroupId()).userId(userID).list();
							if (userList != null && userList.size() > 0) {
								isMe = true;
							}
						}
					}
					if (userids.length() > 0) {
						if (userids.indexOf(userID) > -1) {
							isMe = true;
						}
					}
				}
			}
			if (isMe) {
				retMsg.setMessage("操作成功");
				retMsg.setCode(0);
			} else {
				retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
				retMsg.setMessage("有工作已被其他办理人签收！");
			}
		} else {
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage("有工作已被完成或者撤回！");
		}
		return retMsg;
	}

	@Override
	public RetMsg isOverTask(String pid, String userID) throws Exception {
		RetMsg retMsg = new RetMsg();
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceId(pid).list();
		if (list != null && list.size() > 0) {
			retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
		} else {
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage("该笔工作已经归档结束!");
			Where<ActAljoinQueryHis> hisWhere = new Where<ActAljoinQueryHis>();
			hisWhere.eq("process_instance_id", pid);
			ActAljoinQueryHis his = actAljoinQueryHisService.selectOne(hisWhere);
			if (his == null) {
				retMsg.setMessage("该笔工作已经被删除!");
			}
		}
		return retMsg;
	}

	/**
	 * 分为四类：1-自由选择节点，2-排他节点，3-自由节点，4-并行节点
	 */
	@Override
	public Map<String, Object> getNextAppTaskInfo(String taskId, Map<String, Object> runMap) throws Exception {
		int processType = 0;
		boolean isTask = true;
		int isEl = 0;
		List<TaskDefinition> list = new ArrayList<TaskDefinition>();
		int isFinishMergeTask = 0;
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		Map<String,Object> signParamMap = actAljoinTaskSignInfoService.getSignParam(task);
		List<String> elList = actActivitiService.getToNextCondition(taskId);
		// 遍历el表达式，如果表达式以"${targetTask_"开头则需要下个节点的所有表达式都以它开头，程序认为是可以选择下级节点的流程（针对本节点而言）
		for (String el : elList) {
			if (el.startsWith("${targetTask_")) {
				// 可自己选择跳转节点
				// isTask = false;
				isEl++;
			} else {
				// 不能自己跳转节点，通过表达式和表单关系来设置流程变量
				List<ActAljoinFormDataRun> formDataRunList = actActivitiService
						.getFormDataRun(task.getProcessInstanceId());
				Map<String, String> paramMap = actActivitiService.getConditionParamValue(formDataRunList, (String) el);

				for (Map.Entry<String, String> entry : paramMap.entrySet()) {
					Map<String, Object> variables = new HashMap<String, Object>();
					variables.put(entry.getKey(), runMap.get("aljoin_form_" + entry.getKey()));
					runtimeService.setVariables(task.getProcessInstanceId(), variables);
				}
			}
		}
		if (isEl > 0) {
			// 如果el表达式中含有"${targetTask_"开头的流程变量，暂时视为是可选择节点的非自由流程
			processType = 1;
		} else {
			// 否则暂时视为排他节点
			processType = 2;
		}
		// 自由流程，需要回去所有节点（含自己和结束节点）
		if (Integer.parseInt(runMap.get("isFree").toString()) == 1) {
			processType = 3;
			isTask = false;
			// 获取没有完成的任务节点
			// List<HistoricTaskInstance> currentTask =
			// actActivitiService.getCurrentNodeInfo(taskId);
			List<ActivityImpl> actImplList = actActivitiService.getAllActivity(task.getProcessInstanceId());
			// 获取所有任务节点（没有结束节点）---不取自己
			// list =
			// actActivitiService.getAllTaskInfo(task.getProcessInstanceId());
			list = actActivitiService.getAllTaskInfoExcludeSelf(task.getTaskDefinitionKey(),
					task.getProcessInstanceId());
			// 添加结束记节点
			for (ActivityImpl activityImpl : actImplList) {
				if (activityImpl.getId().startsWith("EndEvent_")) {
					TaskDefinition def = new TaskDefinition(null);
					def.setKey(activityImpl.getId());
					Expression expression = new JuelExpression(null, "结束");
					def.setNameExpression(expression);
					list.add(def);
					break;
				}
			}
		} else {
			// 非自由流程，通过正常渠道获取下一个节点或者节点列表
			list = actActivitiService.getNextTaskInfo2(task.getProcessInstanceId(), isEl == 0 ? true : false,
					task.getTaskDefinitionKey());
			// 判断下个节点是否并行网关的结束点
			List<ActivityImpl> actImplList = actActivitiService.getAllActivity(task.getProcessInstanceId());
			if (!task.getExecutionId().equals(task.getProcessInstanceId())) {
				// 执行流ID和流程实例id不相等-》证明当前节点位于子分支中
				for (ActivityImpl activityImpl : actImplList) {
					if (task.getTaskDefinitionKey().equals(activityImpl.getId())) {
						// 如果出口只有一个，并且是连接到并行网关，则标记下个节点为并行网关的结束节点
						if (activityImpl.getOutgoingTransitions().size() == 1 && "parallelGateway".equals(
								activityImpl.getOutgoingTransitions().get(0).getDestination().getProperty("type"))) {
							isFinishMergeTask++;
						}
					}
				}
			}

			if (processType != 1) {
				if (list.size() > 1) {
					// 并行
					processType = 4;
				} else if (list.size() == 1) {
					processType = 2;
				} else {
					throw new Exception("找不到下个节点");
				}
			}
		}
		List<String> taskKeyList = new ArrayList<String>();
		List<String> taskNameList = new ArrayList<String>();
		// 获取节点key,节点名称和几点对应的候选用户ID
		Map<String, Set<String>> userIdsSetMap = new HashMap<String, Set<String>>();

		// 所有分组ID(含部门，角色，岗位)
		Map<String, Set<String>> allGroupSetMap = new HashMap<String, Set<String>>();
		// 所有候选用户ID（仅含候选用户，不含办理人）
		Map<String, Set<String>> candidateUserIdSetMap = new HashMap<String, Set<String>>();

		// 1.任务类型
		Map<String, String> taskTypeMap = new HashMap<String, String>();
		// 2.打开类型(放到上层处理)
		// Map<String, String> openTypeMap = new HashMap<String, String>();
		// 3.受理人
		Map<String, String> assigneeIdMap = new HashMap<String, String>();
		// 4.当前任务部门列表（含部门下的用户）--任务-部门（也许是角色，岗位）-用户
		Map<String, Map<String, List<String>>> deptListMap = new HashMap<String, Map<String, List<String>>>();

		// 非部门用户ID列表（所有，不分节点）
		Map<String, Set<String>> unDeptUserIdSetMap = new HashMap<String, Set<String>>();

		if (null != list && !list.isEmpty()) {
			for (TaskDefinition definition : list) {
				if (null != definition) {
					// 默认是普通用户任务
					taskTypeMap.put(definition.getKey(), "1");
					MultiInstanceLoopCharacteristics multi = actActivitiService
							.getMultiInstance(task.getProcessInstanceId(), definition.getKey());
					if (multi != null) {
						if (multi.isSequential()) {
							// 串行多实例
							taskTypeMap.put(definition.getKey(), "3");
						} else if (isTask) {
							// 并行多实例
							taskTypeMap.put(definition.getKey(), "2");
						}
					}

					Set<String> userIdsSet = new HashSet<String>();

					taskKeyList.add(definition.getKey());
					taskNameList.add(definition.getNameExpression().getExpressionText());

					Set<String> unDeptUserIdSet = new HashSet<String>();

					// 获取节点的候选用户、组、办理人信息
					if (definition.getAssigneeExpression() != null
							&& !StringUtils.isEmpty(definition.getAssigneeExpression().getExpressionText())) {
						userIdsSet.add(definition.getAssigneeExpression().getExpressionText());
						// 如果有受理人则设置受理人
						assigneeIdMap.put(definition.getKey(), definition.getAssigneeExpression().getExpressionText());
						unDeptUserIdSet.add(definition.getAssigneeExpression().getExpressionText());
					}

					Map<String, List<String>> deptIdUserIdListMap = new HashMap<String, List<String>>();
					Set<String> taskGroupSet = new HashSet<String>();
					Set<Expression> gset = definition.getCandidateGroupIdExpressions();
					// 遍历分组（也许是角色，岗位）列表
					for (Expression expression : gset) {
						// 本部门下的用户ID列表
						List<String> userIdList = new ArrayList<String>();
						List<User> uList = identityService.createUserQuery()
								.memberOfGroup(expression.getExpressionText()).list();
						// 查询是否部门分组
						long count = identityService.createGroupQuery().groupType("DEPARTMENT")
								.groupId(expression.getExpressionText()).count();
						for (User user : uList) {
							userIdsSet.add(user.getId());
							if (count == 1) {
								userIdList.add(user.getId().toString());
							} else {
								// 存放非部门用户
								unDeptUserIdSet.add(user.getId().toString());
							}
						}

						// 是部门才往里面put
						if (count == 1) {
							deptIdUserIdListMap.put(expression.getExpressionText(), userIdList);
						}
						taskGroupSet.add(expression.getExpressionText());
					}

					deptListMap.put(definition.getKey(), deptIdUserIdListMap);
					allGroupSetMap.put(definition.getKey(), taskGroupSet);

					Set<String> candidateUserIdSet = new HashSet<String>();
					Set<Expression> uset = definition.getCandidateUserIdExpressions();
					for (Expression expression : uset) {
						userIdsSet.add(expression.getExpressionText());
						candidateUserIdSet.add(expression.getExpressionText());
						unDeptUserIdSet.add(expression.getExpressionText());
					}
					candidateUserIdSetMap.put(definition.getKey(), candidateUserIdSet);
					userIdsSetMap.put(definition.getKey(), userIdsSet);

					unDeptUserIdSetMap.put(definition.getKey(), unDeptUserIdSet);
				}
			}
		}
		// 构造返回结果
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("taskKeyList", taskKeyList);
		result.put("taskNameList", taskNameList);
		result.put("isTask", isTask);
		result.put("userIdsSetMap", userIdsSetMap);
		result.put("processType", processType);
		result.put("isFinishMergeTask", isFinishMergeTask);

		result.put("taskTypeMap", taskTypeMap);
		result.put("assigneeIdMap", assigneeIdMap);
		result.put("deptListMap", deptListMap);
		result.put("allGroupSetMap", allGroupSetMap);
		result.put("candidateUserIdSetMap", candidateUserIdSetMap);
		result.put("unDeptUserIdSetMap", unDeptUserIdSetMap);
		result.put("isBackOwner", signParamMap.get("isBackOwner"));
		result.put("isSubmit",signParamMap.get("isSubmit"));
		// 当前流程实例id
		result.put("currentProcessInstanceId", task.getProcessInstanceId());

		// 是否多实例节点
		result.put("isMultiTask", "0");
		// 该任务完成后是否满足进入下一个节点的条件（控制是否让其选择下一节点意见下一节点的班里人）
		result.put("isMultiTaskCondition", "0");
		MultiInstanceLoopCharacteristics multi = actActivitiService.getMultiInstance(task.getProcessInstanceId(),
				task.getTaskDefinitionKey());
		if (multi != null) {
			// 是否多实例节点
			result.put("isMultiTask", "1");
			// 判断如果当前实例执行，是否满足进入下一个节点的条件
			// 查询出当前执行的数量，检查是否将要完成整个节点进入下一个节点
			// nrOfInstances--总数
			// nrOfCompletedInstances--已完成数
			// nrOfActiveInstances--正在执行数据(没完成数)
			// loopCounter--当前循环索引
			int nrOfInstances = ((Integer) runtimeService.getVariable(task.getExecutionId(), "nrOfInstances"))
					.intValue();
			int nrOfCompletedInstances = ((Integer) runtimeService.getVariable(task.getExecutionId(),
					"nrOfCompletedInstances")).intValue();
			int nrOfActiveInstances = ((Integer) runtimeService.getVariable(task.getExecutionId(),
					"nrOfActiveInstances")).intValue();
			int loopCounter = ((Integer) runtimeService.getVariable(task.getExecutionId(), "loopCounter")).intValue();
			Map<String, String> conditionParam = new HashMap<String, String>();
			// 模拟本任务执行完后的参数变化，从而预知流程在本任务执行完后的去向情况，是否达到了往下个节点的条件
			conditionParam.put("nrOfInstances", String.valueOf(nrOfInstances));
			conditionParam.put("nrOfCompletedInstances", String.valueOf(nrOfCompletedInstances + 1));
			if (!multi.isSequential()) {
				// 并行（执行后会减少）
				conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances - 1));
			} else {
				// 串行（总是1）
				conditionParam.put("nrOfActiveInstances", String.valueOf(nrOfActiveInstances));
			}
			conditionParam.put("loopCounter", String.valueOf(loopCounter + 1));
			// 获取任务完成表达式
			String completionCondition = multi.getCompletionCondition();

			boolean isPass = ActUtil.isCondition(conditionParam, completionCondition);
			if (isPass) {
				result.put("isMultiTaskCondition", "1");
			}
		}

		return result;
	}

	@Override
	public RetMsg jumpAppTask(String taskId, String targetTaskKey, String targetUserId, String comment, Long userId,
			Map<String, String> paramMap, ActAljoinFormDataRun entity, String isTask, String nextTaskKey)
			throws Exception {
		RetMsg retMsg = new RetMsg();
		// 获取当前流程实例ID
		Task orgnlTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		String processInstanceId = orgnlTask.getProcessInstanceId();
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();

		// 获取任务的授权数据
        ActAljoinBpmnRun actAljoinBpmnRun = actAljoinBpmnRunService.selectById(entity.getBpmnId());
		Where<ActAljoinTaskAssignee> taskAssigneeWhere = new Where<ActAljoinTaskAssignee>();
        taskAssigneeWhere.setSqlSelect("comment_widget_ids");
        taskAssigneeWhere.eq("bpmn_id", actAljoinBpmnRun.getOrgnlId());
		taskAssigneeWhere.eq("task_id", orgnlTask.getTaskDefinitionKey());
        taskAssigneeWhere.eq("version", actAljoinBpmnRun.getTaskAssigneeVersion());
		ActAljoinTaskAssignee actAljoinTaskAssignee = actAljoinTaskAssigneeService.selectOne(taskAssigneeWhere);

		// 填写意见
		if (StringUtils.isEmpty(comment)) {
			comment = "回退";
		}
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
		HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
		if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
			// 取消签收
			taskService.unclaim(task.getId());
		}
		// taskService.claim(task.getId(), targetUserId);
		List<String> assignees = new ArrayList<String>();
		List<HistoricTaskInstance> historicList = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).taskDefinitionKey(targetTaskKey).finished().list();
		for (HistoricTaskInstance historic : historicList) {
			assignees.add(historic.getAssignee());
		}

		// 设置候选
		taskService.addUserIdentityLink(task.getId(), targetUserId, "candidate");

		retMsg.setObject(instance.getId());
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public Map<String, Object> getAppPreTaskInfo(String taskId, String bpmnId) throws Exception {
		Map<String, String> retMap = new HashMap<String, String>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<CustomerTaskDefinition> definitionList = new ArrayList<CustomerTaskDefinition>();
		String retFlag = "1";
		if (!StringUtils.isEmpty(taskId)) {
			Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
			if (StringUtils.isEmpty(taskId)) {
				retFlag = "0";
			}
            ActAljoinBpmnRun bpmnRun = actAljoinBpmnRunService.selectById(bpmnId);
            if (bpmnRun.getIsFree() == 1) {
				// （1）自由流程可以退回
				List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
						.processInstanceId(task.getProcessInstanceId()).finished()
						.orderByHistoricActivityInstanceEndTime().desc().list();
				if (activities.size() > 0) {
					// 如果第一个意见完成的节点没有办理人（被撤回的操作）
					int i = 0;

					// 避免出现连续的两次撤回操作上级节点错误
					while (StringUtils.isEmpty(activities.get(i).getAssignee())
							&& !activities.get(i).getActivityId().startsWith("StartEvent_")
							&& !activities.get(i).getTaskId().equals(task.getId())) {
						i += 2;
					}
					if (activities.size() > i) {
						retMap.put("activityKey", activities.get(i).getActivityId());
						retMap.put("activityName", activities.get(i).getActivityName());
						retMap.put("userId", activities.get(i).getAssignee());

						CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
						taskDefinition.setNextNodeName(activities.get(0).getActivityName());
						taskDefinition.setAssignee(activities.get(0).getAssignee());
						taskDefinition.setKey(activities.get(0).getActivityId());
						if (!StringUtils.isEmpty(activities.get(0).getAssignee())) {
							AutUser user = autUserService.selectById(Long.parseLong(activities.get(0).getAssignee()));
							taskDefinition.setAssigneeName(user.getFullName());
							retMap.put("userFullName", user.getFullName());
						}
						definitionList.add(taskDefinition);
					}

				} else {
					retFlag = "0";
				}

			} else {
				// （2）对于非自由流程，当前活动节点只有一个并且上级节点只有一个的可以退回
				// 获取上一个节点(有可能有多个)
				List<TaskDefinition> defList = actActivitiService.getPreTaskInfo(task.getTaskDefinitionKey(),
						task.getProcessInstanceId());
				if (defList.size() != 1) {
					retFlag = "0";
				} else {
					// 在判断当前流程实例是否同时存在活动节点
					List<Task> taskList =
							// taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
							taskService.createTaskQuery().executionId(task.getExecutionId()).list();
					if (taskList.size() == 1) {
						// 满足条件
						retMap.put("activityKey", defList.get(0).getKey());
						retMap.put("activityName", defList.get(0).getNameExpression().getExpressionText());

						// 查询出是谁办理了的
						List<HistoricTaskInstance> hisTaskList = historyService.createHistoricTaskInstanceQuery()
								.processInstanceId(task.getProcessInstanceId())
								.taskDefinitionKey(defList.get(0).getKey()).orderByHistoricTaskInstanceEndTime().desc()
								.list();
						if (hisTaskList.size() == 0) {
							// 如果是任务重找不到通过连线关系的上级节点，就到历史活动节点查新
							List<HistoricActivityInstance> activityInstanceList = historyService
									.createHistoricActivityInstanceQuery()
									.processInstanceId(task.getProcessInstanceId()).activityType("userTask").finished()
									.orderByHistoricActivityInstanceEndTime().desc().list();
							if (activityInstanceList.size() > 0) {
								HistoricActivityInstance activityInstance = activityInstanceList.get(0);
								retMap.put("activityKey", activityInstance.getActivityId());
								retMap.put("activityName", activityInstance.getActivityName());
								retMap.put("userId", activityInstance.getAssignee());

								CustomerTaskDefinition customerTaskDefinition = new CustomerTaskDefinition();
								customerTaskDefinition.setNextNodeName(activityInstance.getActivityName());
								customerTaskDefinition.setAssignee(activityInstance.getAssignee());
								customerTaskDefinition.setKey(activityInstance.getActivityId());

								if (org.apache.commons.lang3.StringUtils.isNotEmpty(activityInstance.getAssignee())) {
									AutUser user = autUserService.selectById(activityInstance.getAssignee());
									customerTaskDefinition.setAssigneeName(user.getFullName());
									retMap.put("userFullName", user.getFullName());
								}
								definitionList.add(customerTaskDefinition);
							}
						} else {
							// 通过连线获取的上级任务
							HistoricTaskInstance hisTask = hisTaskList.get(0);
							retMap.put("userId", hisTask.getAssignee());

							CustomerTaskDefinition customerTaskDefinition = new CustomerTaskDefinition();
							if (!StringUtils.isEmpty(hisTask.getAssignee())) {
								AutUser user = autUserService.selectById(hisTask.getAssignee());
								customerTaskDefinition.setAssigneeName(user.getFullName());
								retMap.put("userFullName", user.getFullName());
							}
							customerTaskDefinition.setAssignee(hisTask.getAssignee());
							customerTaskDefinition.setNextNodeName(hisTask.getName());
							customerTaskDefinition.setKey(hisTask.getTaskDefinitionKey());
							definitionList.add(customerTaskDefinition);
						}
					} else if (taskList.size() > 1) {
						retFlag = "0";
					} else {
						retFlag = "0";
					}
				}
			}
			if (retMap.get("activityKey") != null && retMap.get("activityKey").startsWith("StartEvent_")) {
				retFlag = "0";
			}

			// 对于多实例节点：当前节点是多实例不能退回，上个节点是多实例，也不能退回
			MultiInstanceLoopCharacteristics currentMulti = actActivitiService
					.getMultiInstance(task.getProcessInstanceId(), task.getTaskDefinitionKey());
			MultiInstanceLoopCharacteristics preMulti = null;
			if (retMap.get("activityKey") != null) {
				preMulti = actActivitiService.getMultiInstance(task.getProcessInstanceId(), retMap.get("activityKey"));
			}
			if (currentMulti != null || preMulti != null) {
				retFlag = "0";
			}
		}
		resultMap.put("preTaskInfo", definitionList);
		resultMap.put("retFlag", retFlag);
		return resultMap;
	}

	@Override
	@Transactional
	public Map<String, Object> distribution(String htmlCode, String sendIds, Long userId, String taskId) throws Exception {
		Where<AutUser> userWhere = new Where<AutUser>();
        if (sendIds.indexOf(";") > -1) {
            sendIds = sendIds.replaceAll(";", ",");
        }
        userWhere.in("id", sendIds);
        List<AutUser> list = autUserService.selectList(userWhere);
	    String receiveUserIds = "";
	    String receiveUserNames = "";
	    String fullNames = "";
        for (AutUser autUser : list) {
            receiveUserIds += autUser.getId() + ";";
            receiveUserNames += autUser.getUserName() + ";";
            fullNames += autUser.getFullName() + ";";
        }
        Task task = taskService.createTaskQuery().taskId(taskId).taskAssignee(userId.toString()).singleResult();
        if(task == null ){
            throw new Exception("找不到当前任务或任务未签收！");
        }
		Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
		queryWhere.eq("process_instance_id", task.getProcessInstanceId());
		ActAljoinQuery aljoinQuery = actAljoinQueryService.selectOne(queryWhere);
		
		// 写入邮件(附件是控件，正文是插件，暂时不考虑附件)
		MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
		MaiReceiveBox maiReceive = new MaiReceiveBox();
		maiReceive.setReceiveUserIds(receiveUserIds);
		maiReceive.setReceiveUserNames(receiveUserNames);
		maiReceive.setReceiveFullNames(fullNames);
		maiReceive.setMailSize(0);
		maiReceive.setMailContent(htmlCode);
		maiReceiveBoxSearch.setIsImportant(0);
		maiReceiveBoxSearch.setIsUrgent(0);
        maiReceiveBoxSearch.setSubjectText("分发:" + aljoinQuery.getProcessTitle());
		maiReceiveBoxSearch.setAttachmentCount(0);
		maiReceiveBoxSearch.setIsImportant(0);
		MaiSendBox maiSendBox = new MaiSendBox();
		maiSendBox.setIsCopySmsRemind(0);
		maiSendBox.setIsReceiveSmsRemind(0);
		maiSendBox.setIsReceipt(1);
		
		MaiWriteVO vo = new MaiWriteVO();
		vo.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
		vo.setMaiReceiveBox(maiReceive);
		vo.setMaiSendBox(maiSendBox);
		
		Map<String, Object> logMap = new HashMap<String,Object>();
		logMap.put("maiWriteVO", vo);
        logMap.put("preTask", task);
        logMap.put("processInstanceId", task.getProcessInstanceId());
        logMap.put("receiveUserIds", receiveUserIds);
        logMap.put("receiveFullNames", fullNames);
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_5);
        return logMap;
    }

    @Override
    @Transactional
    public Map<String, Object> circula(String taskIds, String proInstId, String sendIds, AutUser user) throws Exception {
        // 检查流程参数(流程实例Id或任务id至少传入一个)
        if (proInstId == null || "".equals(proInstId)) {
            List<HistoricTaskInstance> hisList =
                historyService.createHistoricTaskInstanceQuery().taskId(taskIds).list();
            if (hisList != null && hisList.size() > 0) {
                proInstId = hisList.get(0).getProcessInstanceId();
            } else {
                throw new Exception("传阅失败：任务不存在，请刷新页面，重新获取任务信息!");
            }
        }
        // 是否传入传阅人员参数
        List<String> sendIdList = Arrays.asList(sendIds.split(";"));
        if (sendIdList.size() == 0) {
            throw new Exception("请选择传阅人员");
        }

        // 筛选掉已经传阅的人员
        Where<IoaCircula> ioacirculaWhere = new Where<IoaCircula>();
        ioacirculaWhere.eq("process_instance_id", proInstId);
        List<IoaCircula> ioacirculaList = ioaCirculaService.selectList(ioacirculaWhere);
        List<String> allreadUserIdList = new ArrayList<String>();
        for (IoaCircula ioaCircula : ioacirculaList) {
            allreadUserIdList.addAll(Arrays.asList(ioaCircula.getCirIds().split(";")));
        }
        List<String> readUserIdList = new ArrayList<String>();
        if (allreadUserIdList.size() > 0) {
            for (String sendId : sendIdList) {
                if (!allreadUserIdList.contains(sendId)) {
                    readUserIdList.add(sendId);
                }
            }
        } else {
            readUserIdList.addAll(sendIdList);
        }
        // 是否传阅给流程已参与人员
        List<HistoricTaskInstance> historicTaskInstances =
            historyService.createHistoricTaskInstanceQuery().processInstanceId(proInstId).list();
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            if(readUserIdList.contains(historicTaskInstance.getAssignee())){
                readUserIdList.remove(historicTaskInstance.getAssignee());
            }
        }
        if (readUserIdList.size() > 0) {
            IoaCircula circula = new IoaCircula();
            circula.setIsDelete(0);
            circula.setProcessInstanceId(proInstId);
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.in("id", readUserIdList);
            List<AutUser> list = autUserService.selectList(userWhere);
            String receiveUserIds = "";
            String fullNames = "";
            for (AutUser autUser : list) {
                receiveUserIds += autUser.getId() + ";";
                fullNames += autUser.getFullName() + ";";
            }
            circula.setCirIds(receiveUserIds);
            circula.setCirNames(fullNames);
            circula.setCreateUserfullName(user.getFullName());
            ioaCirculaService.insert(circula);
            if (!StringUtils.isEmpty(taskIds)) {
                Task task = taskService.createTaskQuery().taskId(taskIds).singleResult();
                // 办理中的传阅记录到流转日志中
                if(task != null){
                    Map<String, Object> logMap = new HashMap<String,Object>();
                    logMap.put("preTask", task);
                    logMap.put("processInstanceId", proInstId);
                    logMap.put("receiveUserIds", receiveUserIds);
                    logMap.put("receiveFullNames", fullNames);
                    logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_6);
                    return logMap;
                }
            } 
        }
        return null;
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
			for(Long suserId : suserIdList){
				if(userIdList.contains(suserId)){
					uIdList.add(suserId);
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

			for(Long sdeptId : sdeptIdList){
				if(deptIdList.contains(sdeptId)){
					uDeptIdList.add(sdeptId);
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
