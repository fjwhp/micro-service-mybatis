package aljoin.web.service.ioa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.WebConstant;
import aljoin.web.service.act.ActActivitiWebService;
import aljoin.web.service.act.ActAljoinFormDataDraftWebService;

/**
 *
 * 新建工作服务
 *
 * @author：zhongjy
 *
 * @date：2017年12月1日 下午4:34:35
 */
@Service
public class IoaCreateWorkWebService {

	@Resource
	private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private ActAljoinQueryHisService actAljoinQueryHisService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActActivitiWebService actActivitiWebService;
	@Resource
	private ActAljoinFormDataRunService actAljoinFormDataRunService;
	@Resource
	private ActAljoinFormDataDraftWebService actAljoinFormDataDraftWebService;
    @Resource
    private TaskService taskService;

	/**
	 *
	 * 提交
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年10月13日 上午10:26:29
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String,String> doCreateWork(ActAljoinFormDataRun entity, Map<String, String> paramMap, Map<String, String> param,
		Long userId, String fullName) throws Exception {
		Map<String, Object> retMap = actAljoinFormDataDraftService.doCreateWork(entity, paramMap, param, userId,
			fullName);

		ProcessInstance instance = (ProcessInstance) retMap.get("instance");
		ActAljoinBpmnRun bpmn = (ActAljoinBpmnRun) retMap.get("bpmn");
		ActAljoinFormRun runForm = (ActAljoinFormRun) retMap.get("runForm");
		Set<Long> currentHandleUserIdSetfterCompleted = (Set<Long>) retMap.get("currentHandleUserIdSetfterCompleted");
		String isCycleContinue = (String) retMap.get("isCycleContinue");
		retMap.put("operateStatus",  WebConstant.PROCESS_OPERATE_STATUS_1);
		// 插入流程查询表数据
		String activityId = param.get("activityId");
		if (StringUtils.isEmpty(activityId)) {
			// 插入流程查询表
			// String fullName=autUserService.selectById(userId).getFullName();
			actActivitiWebService.insertProcessQuery(instance, param.get("isUrgent"), runForm, null, fullName, fullName,
				bpmn.getCategoryId(), false);
			//记录流程日志
//			actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_1,userId,null);
			actActivitiWebService.insertOrUpdateLog(retMap, userId);
		} else {
			// 查询表
			Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
			actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
			ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
			// 历史查询表
			Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
			actAljoinQueryHisWhere.eq("process_instance_id", instance.getProcessInstanceId());
			ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);

			// 下个节点
			String nextNode = param.get("nextNode");
			// 如果下个节点是结束节点，不要更改当前办理人
			if (!nextNode.startsWith("EndEvent_")) {
				// 修改流程查询表
				//Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
				//actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
				//ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
				if (query != null && "0".equals(isCycleContinue)) {
					// 流程提交下一级后需要设置流程办理人
					if (currentHandleUserIdSetfterCompleted.size() > 0) {
						Where<AutUser> autUserWhere = new Where<AutUser>();
						autUserWhere.setSqlSelect("full_name");
						autUserWhere.in("id", currentHandleUserIdSetfterCompleted);
						String currentHandleFullUserName = "";
						List<AutUser> autUserList = autUserService.selectList(autUserWhere);
						for (AutUser autUser : autUserList) {
							currentHandleFullUserName += autUser.getFullName() + ",";
						}
						if (StringUtils.isNotEmpty(currentHandleFullUserName)) {
							currentHandleFullUserName = currentHandleFullUserName.substring(0,
								currentHandleFullUserName.length() - 1);
						}

						query.setCurrentHandleFullUserName(currentHandleFullUserName);
						queryHis.setCurrentHandleFullUserName(currentHandleFullUserName);
					} else {
						query.setCurrentHandleFullUserName("");
						queryHis.setCurrentHandleFullUserName("");
					}
					//actAljoinQueryService.updateById(query);
					//actAljoinQueryHisService.updateById(queryHis);
				}
			} else {
				// 如果是结束节点，则记录最后一个办理人
				//Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
				//actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
				//ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
				if (query != null) {
					query.setCurrentHandleFullUserName(fullName);
					queryHis.setCurrentHandleFullUserName(fullName);
					//actAljoinQueryService.updateById(query);
					//actAljoinQueryHisService.updateById(queryHis);
				}
			}
			if(query != null && queryHis != null){
				// 判断标题是否有修改
				Where<ActAljoinFormDataRun> actAljoinFormDataRunWhere = new Where<ActAljoinFormDataRun>();
				actAljoinFormDataRunWhere.eq("form_id", runForm.getId());
				actAljoinFormDataRunWhere.eq("proc_inst_id", instance.getProcessInstanceId());
				List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService
					.selectList(actAljoinFormDataRunWhere);
				for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
					if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_title_") && StringUtils.isNotEmpty(actAljoinFormDataRun.getFormWidgetValue())) {
						// 标题
						query.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
						queryHis.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
						break;
					}
				}


				actAljoinQueryService.updateById(query);
				actAljoinQueryHisService.updateById(queryHis);
			}
			actActivitiWebService.insertOrUpdateLog(retMap, userId);
			//记录流程日志
//			actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_1,userId,null);
		}
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("processInstanceId", instance.getId());
		resultMap.put("businessKey", instance.getBusinessKey());
		return resultMap;
	}

	/**
	 *
	 * app提交
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017年10月13日 上午10:26:29
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String,String> doAppCreateWork(ActAljoinFormDataRun entity, Map<String, String> paramMap, Map<String, String> param,
		Long userId, String fullName) throws Exception {
		Map<String, Object> retMap = actAljoinFormDataDraftService.doAppCreateWork(entity, paramMap, param, userId,
			fullName);

		ProcessInstance instance = (ProcessInstance) retMap.get("instance");
		ActAljoinBpmnRun bpmn = (ActAljoinBpmnRun) retMap.get("bpmn");
		ActAljoinFormRun runForm = (ActAljoinFormRun) retMap.get("runForm");
		Set<Long> currentHandleUserIdSetfterCompleted = (Set<Long>) retMap.get("currentHandleUserIdSetfterCompleted");
		String isCycleContinue = (String) retMap.get("isCycleContinue");

		// 插入流程查询表数据
		String activityId = param.get("taskId");
		if (StringUtils.isEmpty(activityId)) {
			// 插入流程查询表
			// String fullName=autUserService.selectById(userId).getFullName();
			actActivitiWebService.insertProcessQuery(instance, param.get("isUrgent"), runForm, null, fullName, fullName,
				bpmn.getCategoryId(), false);
			//记录流程日志
			actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_1,userId,null);
		} else {
			// 查询表
			Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
			actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
			ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
			// 历史查询表
			Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
			actAljoinQueryHisWhere.eq("process_instance_id", instance.getProcessInstanceId());
			ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);

			// 下个节点
			String nextNode = param.get("nextTaskKey");
			if(StringUtils.isNotEmpty(nextNode)){
				// 如果下个节点是结束节点，不要更改当前办理人
				if (!nextNode.startsWith("EndEvent_")) {
         /* // 修改流程查询表
          Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
          actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
          ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);*/
					if (query != null && "0".equals(isCycleContinue)) {
						// 流程提交下一级后需要设置流程办理人
						if (currentHandleUserIdSetfterCompleted.size() > 0) {
							Where<AutUser> autUserWhere = new Where<AutUser>();
							autUserWhere.setSqlSelect("full_name");
							autUserWhere.in("id", currentHandleUserIdSetfterCompleted);
							String currentHandleFullUserName = "";
							List<AutUser> autUserList = autUserService.selectList(autUserWhere);
							for (AutUser autUser : autUserList) {
								currentHandleFullUserName += autUser.getFullName() + ",";
							}
							if (StringUtils.isNotEmpty(currentHandleFullUserName)) {
								currentHandleFullUserName = currentHandleFullUserName.substring(0,
									currentHandleFullUserName.length() - 1);
							}

							query.setCurrentHandleFullUserName(currentHandleFullUserName);
							queryHis.setCurrentHandleFullUserName(currentHandleFullUserName);
						} else {
							query.setCurrentHandleFullUserName("");
							queryHis.setCurrentHandleFullUserName("");
						}
            /*actAljoinQueryService.updateById(query);
            actAljoinQueryHisService.updateById(queryHis);*/
					}
				} else {
         /* // 如果是结束节点，则记录最后一个办理人
          Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
          actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
          ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);*/
					if (query != null) {
            /*Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
            actAljoinQueryHisWhere.eq("process_instance_id", instance.getProcessInstanceId());
            ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);*/
						query.setCurrentHandleFullUserName(fullName);
						queryHis.setCurrentHandleFullUserName(fullName);
            /*actAljoinQueryService.updateById(query);
            actAljoinQueryHisService.updateById(queryHis);*/
					}
				}
			}

			if(query != null && queryHis != null){
				// 判断标题是否有修改
				Where<ActAljoinFormDataRun> actAljoinFormDataRunWhere = new Where<ActAljoinFormDataRun>();
				actAljoinFormDataRunWhere.eq("form_id", runForm.getId());
				actAljoinFormDataRunWhere.eq("proc_inst_id", instance.getProcessInstanceId());
				List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService
					.selectList(actAljoinFormDataRunWhere);
				for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
					if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_title_") && StringUtils.isNotEmpty(actAljoinFormDataRun.getFormWidgetValue())) {
						// 标题
						query.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
						queryHis.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
						break;
					}
				}
				actAljoinQueryService.updateById(query);
				actAljoinQueryHisService.updateById(queryHis);
			}

			//记录流程日志
			actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_1,userId,null);
		}
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("processInstanceId", instance.getId());
		resultMap.put("businessKey", instance.getBusinessKey());
		return resultMap;
	}

	/**
	 *
	 * @描述：提交
	 *
	 * @返回：void
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017年10月13日 上午10:26:29
	 */
	@Transactional
	public Map<String,String> doAddSign(ActAljoinFormDataRun entity, Map<String, String> paramMap, Map<String, String> param,
		Long userId, String fullName) throws Exception {
		Map<String, Object> retMap = actAljoinFormDataDraftWebService.doAddSign(entity, paramMap, param, userId,
			fullName);

		ProcessInstance instance = (ProcessInstance) retMap.get("instance");
		ActAljoinFormRun runForm = (ActAljoinFormRun) retMap.get("runForm");
		// 查询表
		Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
		actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
		ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
		// 历史查询表
		Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
		actAljoinQueryHisWhere.eq("process_instance_id", instance.getProcessInstanceId());
		ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);

		String taskId = param.get("activityId");
		//记录流程日志
		actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_4,userId,taskId);
		Set<Long> currentHandleUserIdSetfterCompleted = actActivitiWebService.getCurrentHandlers(taskId,instance.getProcessInstanceId(),userId);
		if (currentHandleUserIdSetfterCompleted.size() > 0) {
			Where<AutUser> autUserWhere = new Where<AutUser>();
			autUserWhere.setSqlSelect("full_name");
			autUserWhere.in("id", currentHandleUserIdSetfterCompleted);
			String currentHandleFullUserName = "";
			List<AutUser> autUserList = autUserService.selectList(autUserWhere);
			for (AutUser autUser : autUserList) {
				currentHandleFullUserName += autUser.getFullName() + ",";
			}
			if (StringUtils.isNotEmpty(currentHandleFullUserName)) {
				currentHandleFullUserName = currentHandleFullUserName.substring(0,
					currentHandleFullUserName.length() - 1);
			}
			query.setCurrentHandleFullUserName(currentHandleFullUserName);
			queryHis.setCurrentHandleFullUserName(currentHandleFullUserName);
		} else {
			query.setCurrentHandleFullUserName("");
			queryHis.setCurrentHandleFullUserName("");
		}

		if(query != null && queryHis != null){
			// 判断标题是否有修改
			Where<ActAljoinFormDataRun> actAljoinFormDataRunWhere = new Where<ActAljoinFormDataRun>();
			actAljoinFormDataRunWhere.eq("form_id", runForm.getId());
			actAljoinFormDataRunWhere.eq("proc_inst_id", instance.getProcessInstanceId());
			List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService
				.selectList(actAljoinFormDataRunWhere);
			for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
				if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_title_") && StringUtils.isNotEmpty(actAljoinFormDataRun.getFormWidgetValue())) {
					// 标题
					query.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
					queryHis.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
					break;
				}
			}

			actAljoinQueryService.updateById(query);
			actAljoinQueryHisService.updateById(queryHis);
		}
		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("processInstanceId", instance.getId());
		resultMap.put("businessKey", instance.getBusinessKey());
		resultMap.put("taskAuth",String.valueOf(retMap.get("taskAuth")));
		return resultMap;
	}


	/**
	 *
	 * @描述：加签
	 *
	 * @返回：void
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017年10月13日 上午10:26:29
	 */
	@Transactional
	public Map<String,String> doAppAddSign(ActAljoinFormDataRun entity, Map<String, String> paramMap, Map<String, String> param,
		Long userId, String fullName) throws Exception {
		Map<String, Object> retMap = actAljoinFormDataDraftWebService.doAppAddSign(entity, paramMap, param, userId,
			fullName);

		ProcessInstance instance = (ProcessInstance) retMap.get("instance");
		ActAljoinFormRun runForm = (ActAljoinFormRun) retMap.get("runForm");

		// 查询表
		Where<ActAljoinQuery> actAljoinQueryWhere = new Where<ActAljoinQuery>();
		actAljoinQueryWhere.eq("process_instance_id", instance.getProcessInstanceId());
		ActAljoinQuery query = actAljoinQueryService.selectOne(actAljoinQueryWhere);
		// 历史查询表
		Where<ActAljoinQueryHis> actAljoinQueryHisWhere = new Where<ActAljoinQueryHis>();
		actAljoinQueryHisWhere.eq("process_instance_id", instance.getProcessInstanceId());
		ActAljoinQueryHis queryHis = actAljoinQueryHisService.selectOne(actAljoinQueryHisWhere);
		String taskId = param.get("taskId");
		//记录流程日志
		actActivitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(), WebConstant.PROCESS_OPERATE_STATUS_4,userId,taskId);
		Set<Long> currentHandleUserIdSetfterCompleted = actActivitiWebService.getCurrentHandlers(taskId,instance.getProcessInstanceId(),userId);
		if (currentHandleUserIdSetfterCompleted.size() > 0) {
			Where<AutUser> autUserWhere = new Where<AutUser>();
			autUserWhere.setSqlSelect("full_name");
			autUserWhere.in("id", currentHandleUserIdSetfterCompleted);
			String currentHandleFullUserName = "";
			List<AutUser> autUserList = autUserService.selectList(autUserWhere);
			for (AutUser autUser : autUserList) {
				currentHandleFullUserName += autUser.getFullName() + ",";
			}
			if (StringUtils.isNotEmpty(currentHandleFullUserName)) {
				currentHandleFullUserName = currentHandleFullUserName.substring(0,
					currentHandleFullUserName.length() - 1);
			}
			query.setCurrentHandleFullUserName(currentHandleFullUserName);
			queryHis.setCurrentHandleFullUserName(currentHandleFullUserName);
		} else {
			query.setCurrentHandleFullUserName("");
			queryHis.setCurrentHandleFullUserName("");
		}


		if(query != null && queryHis != null){
			// 判断标题是否有修改
			Where<ActAljoinFormDataRun> actAljoinFormDataRunWhere = new Where<ActAljoinFormDataRun>();
			actAljoinFormDataRunWhere.eq("form_id", runForm.getId());
			actAljoinFormDataRunWhere.eq("proc_inst_id", instance.getProcessInstanceId());
			List<ActAljoinFormDataRun> actAljoinFormDataRunList = actAljoinFormDataRunService
				.selectList(actAljoinFormDataRunWhere);
			for (ActAljoinFormDataRun actAljoinFormDataRun : actAljoinFormDataRunList) {
				if (actAljoinFormDataRun.getFormWidgetId().startsWith("aljoin_form_biz_title_") && StringUtils.isNotEmpty(actAljoinFormDataRun.getFormWidgetValue())) {
					// 标题
					query.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
					queryHis.setProcessTitle(actAljoinFormDataRun.getFormWidgetValue());
					break;
				}
			}
			actAljoinQueryService.updateById(query);
			actAljoinQueryHisService.updateById(queryHis);
		}

		Map<String, String> resultMap = new HashMap<String,String>();
		resultMap.put("processInstanceId", instance.getId());
		resultMap.put("businessKey", instance.getBusinessKey());
		resultMap.put("taskAuth",String.valueOf(retMap.get("taskAuth")));
		return resultMap;
	}
}
