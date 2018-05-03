package aljoin.web.service.att;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.att.dao.entity.AttSignInOut;
import aljoin.att.dao.entity.AttSignInOutHis;
import aljoin.att.dao.object.AttSignInOutVO;
import aljoin.att.iservice.AttSignInOutHisService;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 
 * 签到、退表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-27
 */
@Service
public class AttSignInOutWebService {
	private final static Logger log = LoggerFactory.getLogger(AttSignInOutWebService.class);

	@Resource
	private AttSignInOutHisService attSignInOutHisService;
	@Resource
	private AttSignInOutService attSignInOutService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActActivitiService activitiService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	private ActAljoinFixedConfigService actAljoinFixedConfigService;
	@Resource
	private TaskService taskService;
	@Resource
	private ActActivitiWebService activitiWebService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private HistoryService historyService;
	@Resource
    private ActActivitiWebService actActivitiWebService;
	@Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;
	
	// private static String currMonth = "";

	public RetMsg signInPatch(AttSignInOutVO obj) throws Exception {

		RetMsg retMsg = new RetMsg();
		Map<String, String> map = new HashMap<String, String>();
		Date date = new Date();
		ActAljoinBpmn bpmn = null;
		Long signUserId = 0L;
		try {
			// 获取所有要补签的记录
			List<AttSignInOut> attSignInOutList = obj.getAttSignInOutList();

			Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
			configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_ATTEND_SUPPLEMENT);
			configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
			ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);
			if (null != config && null != config.getProcessId()) {
				Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
				where.eq("process_id", config.getProcessId());
				where.eq("is_deploy", 1);
				where.eq("is_active", 1);
				where.setSqlSelect("id,category_id,process_id,process_name");
				bpmn = actAljoinBpmnService.selectOne(where);
			}

			// 找到这些记录，重新set为正常的值，然后updateBatch
			if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
				signUserId = attSignInOutList.get(0).getSignUserId();
				String ids = "";
				for (AttSignInOut attSignInOut : attSignInOutList) {
					if (null != attSignInOut && null != attSignInOut.getId()) {
						ids += attSignInOut.getId() + ";";
					}

					updateSignInOutDate(attSignInOut, bpmn, date, null);
				}
				// 启动流程
				if (null != signUserId) {
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
					Map<String, String> param = new HashMap<String, String>();
					param.put("bizType", "att");
					param.put("bizId", ids);
					param.put("isUrgent", "1");
					ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param, signUserId);
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
					for (AttSignInOut attSignInOut : attSignInOutList) {
						updateSignInOutDate(attSignInOut, bpmn, date, instance.getProcessInstanceId());
					}

					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.setSqlSelect("id,user_name,full_name");
					userWhere.eq("id", signUserId);
					AutUser user = autUserService.selectOne(userWhere);
					activitiWebService.insertProcessQuery(instance, "1", null, user.getFullName() + "考勤补签",
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
					// 流程启动----第一个办理环节是自己----自动签收
					String userFullName = "";
					taskService.claim(task.getId(), String.valueOf(signUserId));
					map.put("signInTime", DateUtil.datetime2str(new Date()));
					if (null != task.getAssignee()) {
						userFullName = autUserService.selectById(task.getAssignee()).getFullName();
						actAljoinQueryService.updateAssigneeName(task.getId(), userFullName);
					}
					//记录流程日志
					Map<String,Object> logMap = new HashMap<String,Object>();  
					logMap.put("instance", instance);
					logMap.put("preTask", task);
					actActivitiWebService.insertOrUpdateLog(logMap, user.getId());
//                    activitiWebService.insertOrUpdateActivityLog(instance.getProcessInstanceId(),WebConstant.PROCESS_OPERATE_STATUS_1,user.getId(),null);
				}
				retMsg.setCode(0);
				retMsg.setObject(map);
				retMsg.setMessage("操作成功");
			} else {
				retMsg.setCode(1);
				retMsg.setMessage("没有获取到需要补签的记录");
			}
		} catch (Exception e) {
			// 修改hou
			log.error("错误信息", e);
			retMsg = RetMsg.getFailRetMsg();
		}
		return retMsg;
	}

	/**
	 *
	 * 修改 考勤数据
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-28
	 */
	public void updateSignInOutDate(AttSignInOut attSignInOut, ActAljoinBpmn bpmn, Date date,
			String processInstanceId) {
		// 查询本表中这条需要补签的记录
		AttSignInOut signInOut = new AttSignInOut();
		Where<AttSignInOut> w1 = new Where<AttSignInOut>();
		if (null != attSignInOut.getSignDate()) {
			w1.eq("sign_date", attSignInOut.getSignDate());
		}
		if (null != attSignInOut.getSignUserId()) {
			w1.eq("sign_user_id", attSignInOut.getSignUserId());
		}
		if (null != attSignInOut.getId()) {
			w1.eq("id", attSignInOut.getId());
		}
		w1.setSqlSelect(
				"id,create_time,version,is_delete,create_user_id,create_user_name,sign_user_id,sign_date,am_sign_in_patch_status,am_sign_in_patch_time,am_sign_in_patch_desc,am_sign_out_patch_status,am_sign_out_patch_time,am_sign_out_patch_desc,pm_sign_in_patch_status,pm_sign_in_patch_time,pm_sign_in_patch_desc,pm_sign_out_patch_status,pm_sign_out_patch_time,pm_sign_out_patch_desc,process_id,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
		signInOut = attSignInOutService.selectOne(w1);
		// 查询历史表
		AttSignInOutHis signInOutHis = new AttSignInOutHis();
		Where<AttSignInOutHis> hisW1 = new Where<AttSignInOutHis>();
		if (null != attSignInOut.getSignDate()) {
			hisW1.eq("sign_date", attSignInOut.getSignDate());
		}
		if (null != attSignInOut.getSignUserId()) {
			hisW1.eq("sign_user_id", attSignInOut.getSignUserId());
		}
		if (null != attSignInOut.getId()) {
			hisW1.eq("id", attSignInOut.getId());
		}

		hisW1.setSqlSelect(
				"id,create_time,version,is_delete,create_user_id,create_user_name,sign_user_id,sign_date,am_sign_in_patch_status,am_sign_in_patch_time,am_sign_in_patch_desc,am_sign_out_patch_status,am_sign_out_patch_time,am_sign_out_patch_desc,pm_sign_in_patch_status,pm_sign_in_patch_time,pm_sign_in_patch_desc,pm_sign_out_patch_status,pm_sign_out_patch_time,pm_sign_out_patch_desc,process_id,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
		signInOutHis = attSignInOutHisService.selectOne(hisW1);

		// 1、更新本表、历史表上午签到状态
		if (null != attSignInOut.getAmSignInStatus()) {
			if (null != bpmn) {
				if (null != signInOut) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOut.setAmSignInProcInstId(processInstanceId);
					} else {
						signInOut.setAmSignInPatchStatus(2);
						// 补签时间
						signInOut.setAmSignInPatchTime(date);
						// 补签描述
						String amSignInPatchDesc = attSignInOut.getAmSignInPatchDesc();
						if (StringUtils.isNotEmpty(amSignInPatchDesc)) {
							signInOut.setAmSignInPatchDesc(amSignInPatchDesc);
						}
						signInOut.setProcessId(bpmn.getProcessId());
					}
					attSignInOutService.updateById(signInOut);
				}
				if (null != signInOutHis) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOutHis.setAmSignInProcInstId(processInstanceId);
					} else {
						signInOutHis.setAmSignInPatchStatus(2);
						signInOutHis.setAmSignInPatchTime(date);
						String amSignInPatchDescHis = attSignInOut.getAmSignInPatchDesc();
						if (StringUtils.isNotEmpty(amSignInPatchDescHis)) {
							signInOutHis.setAmSignInPatchDesc(amSignInPatchDescHis);
						}
						signInOutHis.setProcessId(bpmn.getProcessId());
					}
					attSignInOutHisService.updateById(signInOutHis);

				}
			}

		}
		// 2、更新上午签退状态
		if (null != attSignInOut.getAmSignOutStatus()) {
			if (null != bpmn) {
				if (null != signInOut) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOut.setAmSignOutProcInstId(processInstanceId);
					} else {
						signInOut.setProcessId(bpmn.getProcessId());
						signInOut.setAmSignOutPatchStatus(2);
						String amSignOutPatchDesc = attSignInOut.getAmSignOutPatchDesc();
						if (StringUtils.isNotEmpty(amSignOutPatchDesc)) {
							signInOut.setAmSignOutPatchDesc(amSignOutPatchDesc);
						}
						signInOut.setAmSignOutPatchTime(date);
					}
					attSignInOutService.updateById(signInOut);
				}

				if (null != signInOutHis) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOutHis.setAmSignOutProcInstId(processInstanceId);
					} else {
						signInOutHis.setAmSignOutPatchStatus(2);
						String amSignOutPatchDescHis = attSignInOut.getAmSignOutPatchDesc();
						signInOutHis.setAmSignOutPatchTime(date);
						if (StringUtils.isNotEmpty(amSignOutPatchDescHis)) {
							signInOutHis.setAmSignOutPatchDesc(amSignOutPatchDescHis);
						}
						signInOutHis.setProcessId(bpmn.getProcessId());
					}
					attSignInOutHisService.updateById(signInOutHis);
				}
			}
		}
		// 3、更新下午签到状态
		if (null != attSignInOut.getPmSignInStatus()) {
			if (null != bpmn) {
				if (null != signInOut) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOut.setPmSignInProcInstId(processInstanceId);
					} else {
						signInOut.setPmSignInPatchStatus(2);
						signInOut.setPmSignInPatchTime(date);
						String pmSignInPatchDesc = attSignInOut.getPmSignInPatchDesc();
						if (StringUtils.isNotEmpty(pmSignInPatchDesc)) {
							signInOut.setPmSignInPatchDesc(pmSignInPatchDesc);
						}
						signInOut.setProcessId(bpmn.getProcessId());
					}
					attSignInOutService.updateById(signInOut);
				}
				if (null != signInOutHis) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOutHis.setPmSignInProcInstId(processInstanceId);
					} else {
						signInOutHis.setPmSignInPatchStatus(2);
						signInOutHis.setPmSignInPatchTime(date);
						String pmSignInPatchDescHis = attSignInOut.getPmSignInPatchDesc();
						if (StringUtils.isNotEmpty(pmSignInPatchDescHis)) {
							signInOutHis.setPmSignInPatchDesc(pmSignInPatchDescHis);
						}
						signInOutHis.setProcessId(bpmn.getProcessId());
					}
					attSignInOutHisService.updateById(signInOutHis);
				}
			}
		}
		// 4、更新下午签退状态
		if (null != attSignInOut.getPmSignOutStatus()) {
			if (null != bpmn) {
				if (null != signInOut) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOut.setPmSignOutProcInstId(processInstanceId);
					} else {
						signInOut.setProcessId(bpmn.getProcessId());
						signInOut.setPmSignOutPatchStatus(2);
						signInOut.setPmSignOutPatchTime(date);
						String pmSignOutPatchDesc = attSignInOut.getPmSignOutPatchDesc();
						if (StringUtils.isNotEmpty(pmSignOutPatchDesc)) {
							signInOut.setPmSignOutPatchDesc(pmSignOutPatchDesc);
						}
					}
					attSignInOutService.updateById(signInOut);
				}
				if (null != signInOutHis) {
					if (StringUtils.isNotEmpty(processInstanceId)) {
						signInOutHis.setPmSignOutProcInstId(processInstanceId);
					} else {
						signInOutHis.setPmSignOutPatchStatus(2);
						signInOutHis.setPmSignOutPatchTime(date);
						String pmSignOutPatchDescHis = attSignInOut.getPmSignOutPatchDesc();
						if (StringUtils.isNotEmpty(pmSignOutPatchDescHis)) {
							signInOutHis.setPmSignOutPatchDesc(pmSignOutPatchDescHis);
						}
						signInOutHis.setProcessId(bpmn.getProcessId());
					}
					attSignInOutHisService.updateById(signInOutHis);
				}
			}
		}
	}

	/**
	 *
	 * 任务提交（同意）
	 *
	 * @return：RetMsg
	 *
	 * @author：sunlinan
	 *
	 * @date：2018年2月2日 下午3:46:52
	 */
	@Transactional
    public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId,
            String message, AutUser createUser) throws Exception {
        RetMsg retMsg = new RetMsg();
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null == currentTask){
          retMsg.setCode(1);
          retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
          return retMsg;
        }
        Map<String,Object> logMap = new HashMap<String,Object>();  
        String processInstanceId = currentTask.getProcessInstanceId();
        taskService.addComment(taskId, processInstanceId, message);
        taskService.setAssignee(taskId, createUser.getId().toString());
        activitiService.completeTask(variables, taskId);
        logMap.put("processInstanceId", processInstanceId);
        logMap.put("commont", message);
        logMap.put("preTask", currentTask);
        Date date = new Date();
        if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
            if (bizId.indexOf(";") > -1) {
                Where<AttSignInOut> where = new Where<AttSignInOut>();
                //where.in("id", signOutIdList);
                where.andNew("am_sign_in_proc_inst_id = {0} or am_sign_out_proc_inst_id = {0} or pm_sign_in_proc_inst_id = {0} or pm_sign_out_proc_inst_id = {0}",processInstanceId);
                where.setSqlSelect(
                        "id,create_time,is_delete,create_user_id,create_user_name,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                List<AttSignInOut> attSignInOutList = attSignInOutService.selectList(where);

                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                //hisWhere.in("id", signOutIdList);
                hisWhere.andNew("am_sign_in_proc_inst_id = {0} or am_sign_out_proc_inst_id = {0} or pm_sign_in_proc_inst_id = {0} or pm_sign_out_proc_inst_id = {0}",processInstanceId);
                hisWhere.setSqlSelect(
                        "id,create_time,is_delete,create_user_id,create_user_name,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                List<AttSignInOutHis> attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);

                if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
                    List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
                    List<AttSignInOutHis> signInOutisHisList = new ArrayList<AttSignInOutHis>();
                    ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                            .processInstanceId(processInstanceId).singleResult();
                    if (null == pi) {
                        logMap.put("nextNode", "EndEvent_");
                        // 如果是结束节点，则记录最后一个办理人
                        actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
                        for (AttSignInOut attSignInOut : attSignInOutList) {
                            AttSignInOut signInOut = new AttSignInOut();
                            if(StringUtils.isNotEmpty(attSignInOut.getAmSignInProcInstId())){
                                if (2 == attSignInOut.getAmSignInPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getAmSignInProcInstId())) {
                                    attSignInOut.setAmSignInPatchAuditTime(date);
                                    attSignInOut.setAmSignInPatchStatus(3);
                                }
                            }

                            if(StringUtils.isNotEmpty(attSignInOut.getAmSignOutProcInstId())){
                                if (2 == attSignInOut.getAmSignOutPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getAmSignOutProcInstId())) {
                                    attSignInOut.setAmSignOutPatchAuditTime(date);
                                    attSignInOut.setAmSignOutPatchStatus(3);
                                }
                            }

                            if(StringUtils.isNotEmpty(attSignInOut.getPmSignInProcInstId())){
                                if (2 == attSignInOut.getPmSignInPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getPmSignInProcInstId())) {
                                    attSignInOut.setPmSignInPatchAuditTime(date);
                                    attSignInOut.setPmSignInPatchStatus(3);
                                }
                            }

                            if(StringUtils.isNotEmpty(attSignInOut.getPmSignOutProcInstId())){
                                if (2 == attSignInOut.getPmSignOutPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getPmSignOutProcInstId())) {
                                    attSignInOut.setPmSignOutPatchAuditTime(date);
                                    attSignInOut.setPmSignOutPatchStatus(3);
                                }
                            }

                            BeanUtils.copyProperties(attSignInOut, signInOut);
                            signInOutList.add(signInOut);
                        }
                        attSignInOutService.updateBatchById(signInOutList);

                        for (AttSignInOutHis attSignInOut : attSignInOutHisList) {
                            AttSignInOutHis signInOutHis = new AttSignInOutHis();
                            if(StringUtils.isNotEmpty(attSignInOut.getAmSignInProcInstId())){
                                if (2 == attSignInOut.getAmSignInPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getAmSignInProcInstId())) {
                                    attSignInOut.setAmSignInPatchAuditTime(date);
                                    attSignInOut.setAmSignInPatchStatus(3);
                                }
                            }

                            if(StringUtils.isNotEmpty(attSignInOut.getAmSignOutProcInstId())){
                                if (2 == attSignInOut.getAmSignOutPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getAmSignOutProcInstId())) {
                                    attSignInOut.setAmSignOutPatchAuditTime(date);
                                    attSignInOut.setAmSignOutPatchStatus(3);
                                }
                            }

                            if(StringUtils.isNotEmpty(attSignInOut.getPmSignInProcInstId())){
                                if (2 == attSignInOut.getPmSignInPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getPmSignInProcInstId())) {
                                    attSignInOut.setPmSignInPatchAuditTime(date);
                                    attSignInOut.setPmSignInPatchStatus(3);
                                }
                            }

                            if(StringUtils.isNotEmpty(attSignInOut.getPmSignOutProcInstId())){
                                if (2 == attSignInOut.getPmSignOutPatchStatus()
                                    && processInstanceId.equals(attSignInOut.getPmSignOutProcInstId())) {
                                    attSignInOut.setPmSignOutPatchAuditTime(date);
                                    attSignInOut.setPmSignOutPatchStatus(3);
                                }
                            }
                            BeanUtils.copyProperties(attSignInOut, signInOutHis);
                            signInOutisHisList.add(signInOutHis);
                        }
                        attSignInOutHisService.updateBatchById(signInOutisHisList);
                    }
                }
            }
        }
        List<String> userIdList = new ArrayList<String>();
        HashSet<Long> userIdSet = new HashSet<Long>();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (null != taskList && !taskList.isEmpty()) {
            Task task = taskList.get(0);
            if (null != task && org.apache.commons.lang.StringUtils.isNotEmpty(userId)) {
                userIdList = Arrays.asList(userId.split(";"));
                if (null != userIdList && !userIdList.isEmpty()) {
                    actFixedFormService.deleteOrgnlTaskAuth(task);
                    HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                    if (null != hisTask.getClaimTime() || null != hisTask.getAssignee()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                        taskService.unclaim(task.getId());
                    }
                    for (String uId : userIdList) {
                        userIdSet.add(Long.valueOf(uId));
                        taskService.addCandidateUser(task.getId(), uId);
                    }
//                    actAljoinQueryService.cleanQureyCurrentUser(task.getId());
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
      //记录流程日志
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
        actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
        //记录流程日志
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
        String handle = "";
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

	/**
     *
     * 回退到上一环节
     *
     * @return：RetMsg
     *
     * @author：sunlinan
     *
     * @date：2018年2月2日 下午3:46:52
     */
	@Transactional
    public RetMsg jump2Task2(String taskId, String bizId, String message, Long creatUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        Task preTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = preTask.getProcessInstanceId();
        Map<String,Object> logMap = new HashMap<String,Object>();  
        logMap.put("processInstanceId", processInstanceId);
        logMap.put("preTask", preTask);
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        List<String> assignees = new ArrayList<String>();
        if (StringUtils.isNotEmpty(processInstanceId) && null != currentTask && !currentTask.isEmpty()) {
            String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
            // 填写意见
            Authentication.setAuthenticatedUserId(String.valueOf(creatUserId));
            taskService.addComment(taskId, processInstanceId, message);
            logMap.put("commont", message);
            // 当前环节办理人分配
            if (StringUtils.isNotEmpty(currentTaskKey)) {
                List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
                if (null != preList && !preList.isEmpty()) {
                    String targetTaskKey = preList.get(0).getKey();
                    if (org.apache.commons.lang3.StringUtils.isNotEmpty(targetTaskKey)) {
                        // 找到唯一一个历史上级节点
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
                        HashSet<Long> userIdSet = new HashSet<Long>();
                        // （原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
                        actFixedFormService.deleteOrgnlTaskAuth(task);

                        HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                        if (null != hisTask.getClaimTime()) { // unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                            taskService.unclaim(task.getId());
                        }
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
//                      actAljoinQueryService.cleanQureyCurrentUser(task.getId());
                        // 流转日志记录
                        Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                        taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
                        logMap.put("taskKeyUserMap", taskKeyMap);
                        logMap.put("userTask", task);
//                        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_2,creatUserId,null);
                        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
                        actActivitiWebService.insertOrUpdateLog(logMap, creatUserId);
                    }
                }
            }
        }
        String handle = "";
        if(!assignees.isEmpty()){
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
        Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
        if(null == currentTask){
          retMsg.setCode(AppConstant.RET_CODE_ERROR);
          retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
          return retMsg;
        }
        Map<String,Object> logMap = new HashMap<String,Object>();   
        String processInstanceId = currentTask.getProcessInstanceId();
        logMap.put("processInstanceId", processInstanceId);
        taskService.addComment(taskId, processInstanceId, message);
        logMap.put("commont", message);
        taskService.setAssignee(taskId, createUser.getId().toString());
        activitiService.completeTask(variables, taskId);
        logMap.put("preTask", currentTask);
        Date date = new Date();
        if (bizId != null && bizId.length()>0) {
            Where<AttSignInOut> where = new Where<AttSignInOut>();
            //where.in("id", signOutIdList);
            where.andNew("am_sign_in_proc_inst_id = {0} or am_sign_out_proc_inst_id = {0} or pm_sign_in_proc_inst_id = {0} or pm_sign_out_proc_inst_id = {0}",processInstanceId);
            where.setSqlSelect(
              "id,create_time,is_delete,create_user_id,create_user_name,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
            List<AttSignInOut> attSignInOutList = attSignInOutService.selectList(where);

            Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
            //hisWhere.in("id", signOutIdList);
            hisWhere.andNew("am_sign_in_proc_inst_id = {0} or am_sign_out_proc_inst_id = {0} or pm_sign_in_proc_inst_id = {0} or pm_sign_out_proc_inst_id = {0}",processInstanceId);
            hisWhere.setSqlSelect(
              "id,create_time,is_delete,create_user_id,create_user_name,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
            List<AttSignInOutHis> attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);

            if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
                List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
                List<AttSignInOutHis> signInOutisHisList = new ArrayList<AttSignInOutHis>();
                ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                  .processInstanceId(processInstanceId).singleResult();
                if (null == pi) {
                    // 如果是结束节点，则记录最后一个办理人
                    actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
                    for (AttSignInOut attSignInOut : attSignInOutList) {
                        AttSignInOut signInOut = new AttSignInOut();
                        if(StringUtils.isNotEmpty(attSignInOut.getAmSignInProcInstId())){
                            if (2 == attSignInOut.getAmSignInPatchStatus()
                              && processInstanceId.equals(attSignInOut.getAmSignInProcInstId())) {
                                attSignInOut.setAmSignInPatchAuditTime(date);
                                attSignInOut.setAmSignInPatchStatus(3);
                            }
                        }

                        if(StringUtils.isNotEmpty(attSignInOut.getAmSignOutProcInstId())){
                            if (2 == attSignInOut.getAmSignOutPatchStatus()
                              && processInstanceId.equals(attSignInOut.getAmSignOutProcInstId())) {
                                attSignInOut.setAmSignOutPatchAuditTime(date);
                                attSignInOut.setAmSignOutPatchStatus(3);
                            }
                        }

                        if(StringUtils.isNotEmpty(attSignInOut.getPmSignInProcInstId())){
                            if (2 == attSignInOut.getPmSignInPatchStatus()
                              && processInstanceId.equals(attSignInOut.getPmSignInProcInstId())) {
                                attSignInOut.setPmSignInPatchAuditTime(date);
                                attSignInOut.setPmSignInPatchStatus(3);
                            }
                        }

                        if(StringUtils.isNotEmpty(attSignInOut.getPmSignOutProcInstId())){
                            if (2 == attSignInOut.getPmSignOutPatchStatus()
                              && processInstanceId.equals(attSignInOut.getPmSignOutProcInstId())) {
                                attSignInOut.setPmSignOutPatchAuditTime(date);
                                attSignInOut.setPmSignOutPatchStatus(3);
                            }
                        }

                        BeanUtils.copyProperties(attSignInOut, signInOut);
                        signInOutList.add(signInOut);
                    }
                    attSignInOutService.updateBatchById(signInOutList);

                    for (AttSignInOutHis attSignInOut : attSignInOutHisList) {
                        AttSignInOutHis signInOutHis = new AttSignInOutHis();
                        if(StringUtils.isNotEmpty(attSignInOut.getAmSignInProcInstId())){
                            if (2 == attSignInOut.getAmSignInPatchStatus()
                              && processInstanceId.equals(attSignInOut.getAmSignInProcInstId())) {
                                attSignInOut.setAmSignInPatchAuditTime(date);
                                attSignInOut.setAmSignInPatchStatus(3);
                            }
                        }

                        if(StringUtils.isNotEmpty(attSignInOut.getAmSignOutProcInstId())){
                            if (2 == attSignInOut.getAmSignOutPatchStatus()
                              && processInstanceId.equals(attSignInOut.getAmSignOutProcInstId())) {
                                attSignInOut.setAmSignOutPatchAuditTime(date);
                                attSignInOut.setAmSignOutPatchStatus(3);
                            }
                        }

                        if(StringUtils.isNotEmpty(attSignInOut.getPmSignInProcInstId())){
                            if (2 == attSignInOut.getPmSignInPatchStatus()
                              && processInstanceId.equals(attSignInOut.getPmSignInProcInstId())) {
                                attSignInOut.setPmSignInPatchAuditTime(date);
                                attSignInOut.setPmSignInPatchStatus(3);
                            }
                        }

                        if(StringUtils.isNotEmpty(attSignInOut.getPmSignOutProcInstId())){
                            if (2 == attSignInOut.getPmSignOutPatchStatus()
                              && processInstanceId.equals(attSignInOut.getPmSignOutProcInstId())) {
                                attSignInOut.setPmSignOutPatchAuditTime(date);
                                attSignInOut.setPmSignOutPatchStatus(3);
                            }
                        }
                        BeanUtils.copyProperties(attSignInOut, signInOutHis);
                        signInOutisHisList.add(signInOutHis);
                    }
                    attSignInOutHisService.updateBatchById(signInOutisHisList);
                    logMap.put("nextNode", "EndEvent_");
                }
            }
        }
        List<String> userIdList = new ArrayList<String>();
        HashSet<Long> userIdSet = new HashSet<Long>();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (null != taskList && !taskList.isEmpty()) {
            Task task = taskList.get(0);
            if (null != task && org.apache.commons.lang.StringUtils.isNotEmpty(userId)) {
                userIdList = Arrays.asList(userId.split(";"));
                if (null != userIdList && !userIdList.isEmpty()) {
                    actFixedFormService.deleteOrgnlTaskAuth(task);
                    HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                    if (null != hisTask.getClaimTime()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                        taskService.unclaim(task.getId());
                    }
                    for (String uId : userIdList) {
                        userIdSet.add(Long.valueOf(uId));
                        taskService.addCandidateUser(task.getId(), uId);
                    }
//                    actAljoinQueryService.cleanQureyCurrentUser(task.getId());
                    Where<AutUser> uwhere = new Where<AutUser>();
                    uwhere.setSqlSelect("id,full_name");
                    uwhere.in("id", userIdSet);
                    List<AutUser> userList = autUserService.selectList(uwhere);
                    String userName = "";
                    for (AutUser autUser : userList) {
                        userName += autUser.getFullName() + ";";
                    }
                    actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
                }
                Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
                taskKeyMap.put(task.getTaskDefinitionKey(), userIdList);
                logMap.put("taskKeyUserMap", taskKeyMap);
                logMap.put("userTask", task);
            }
        }
        //记录流程日志
        logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
        actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId, WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
        String handle = "";
        // 下一环节办理人待办数量+1
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
}
