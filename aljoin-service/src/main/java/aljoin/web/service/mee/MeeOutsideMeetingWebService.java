package aljoin.web.service.mee;

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
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
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
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeOutsideMeetingDraftService;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;
import aljoin.web.service.act.ActActivitiWebService;
import aljoin.web.service.off.OffSchedulingWebService;

@Component
public class MeeOutsideMeetingWebService {

	@Resource
	private MeeOutsideMeetingService meeOutsideMeetingService;
	@Resource
	private ResResourceService resResourceService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActActivitiService activitiService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	private TaskService taskService;
	@Resource
	private ActAljoinFixedConfigService actAljoinFixedConfigService;
	@Resource
	private OffSchedulingWebService offSchedulingWebService;
	@Resource
	private OffSchedulingService offSchedulingService;
	@Resource
	private MeeOutsideMeetingDraftService meeOutsideMeetingDraftService;
	@Resource
	private RuntimeService runtimeService;
	@Resource
	private ActActivitiWebService actActivitiWebService;
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	@Resource
	private AutDataStatisticsService autDataStatisticsService;
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private HistoryService historyService;
	@Resource
    private ActAljoinBpmnRunService actAljoinBpmnRunService;

	@Transactional
	public RetMsg add(MeeOutsideMeetingVO obj, CustomUser createuser) throws Exception {
		RetMsg retMsg = new RetMsg();
		ActAljoinBpmn bpmn = null;
		Map<String, String> map = new HashMap<String, String>();
		if (null != obj) {
			if (StringUtils.isEmpty(obj.getMeetingTitle())) {
				obj.setMeetingTitle("");
				retMsg.setCode(1);
				retMsg.setMessage("会议标题不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getMeetingHost())) {
				obj.setMeetingHost("");
				retMsg.setCode(1);
				retMsg.setMessage("主持人不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getContacts())) {
				obj.setContacts("");
				retMsg.setCode(1);
				retMsg.setMessage("联系人不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getAddress())) {
				obj.setAddress("");
				retMsg.setCode(1);
				retMsg.setMessage("填写地址不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getPartyMemebersId())) {
				obj.setPartyMemebersId("");
				retMsg.setCode(1);
				retMsg.setMessage("参会人员不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getPartyMemeberNames())) {
				obj.setPartyMemeberNames("");
				retMsg.setCode(1);
				retMsg.setMessage("参会人员不能为空");
				return retMsg;
			}
			if (null == obj.getIsWarnMail()) {
				obj.setIsWarnMail(0);
			}
			if (null == obj.getIsWarnMsg()) {
				obj.setIsWarnMsg(0);
			}
			if (null == obj.getIsWarnOnline()) {
				obj.setIsWarnOnline(0);
			}

			if (null == obj.getBeginTime()) {
				// obj.setBeginTime(date);
				retMsg.setCode(1);
				retMsg.setMessage("会议开始时间不能为空");
				return retMsg;
			}
			if (null == obj.getEndTime()) {
				// obj.setEndTime(date);
				retMsg.setCode(1);
				retMsg.setMessage("会议结束时间不能为空");
				return retMsg;
			}
			Date date = new Date();
			if (obj.getBeginTime().before(date)) {
				retMsg.setCode(1);
				retMsg.setMessage("会议时间不能小于当天");
				return retMsg;
			}
			if (obj.getBeginTime().after(obj.getEndTime())) {
				retMsg.setCode(1);
				retMsg.setMessage("会议开始时间不能小于结束时间");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getMeetingContent())) {
				obj.setMeetingContent("");
				retMsg.setCode(1);
				retMsg.setMessage("会议内容不能为空");
				return retMsg;
			}
			if (null == obj.getMeetingSituation()) {
				obj.setMeetingSituation(1);
			}
			if (StringUtils.isEmpty(obj.getAttendances())) {
				obj.setAttendances("");
			}
			Where<ActAljoinFixedConfig> configWhere = new Where<ActAljoinFixedConfig>();
			configWhere.eq("process_code", WebConstant.FIXED_FORM_PROCESS_OUTSIDE_MEETING);
			configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
			ActAljoinFixedConfig config = actAljoinFixedConfigService.selectOne(configWhere);
			if (null != config && null != config.getProcessId()) {
				Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
				where.eq("process_id", config.getProcessId());
				where.eq("is_deploy", 1);
				where.eq("is_active", 1);
				bpmn = actAljoinBpmnService.selectOne(where);
				if (null != bpmn) {
					obj.setAuditStatus(1);// 审核中
					obj.setProcessId(bpmn.getProcessId());
				}else{
	                retMsg.setCode(1);
	                retMsg.setMessage("会议流程未部署！");
	                return retMsg;
	            }
			}else{
              retMsg.setCode(1);
              retMsg.setMessage("会议流程未配置！");
              return retMsg;
            }
			Long objId = obj.getId();
			MeeOutsideMeeting meeInsideMeeting = new MeeOutsideMeeting();
			obj.setId(null);
			BeanUtils.copyProperties(obj, meeInsideMeeting);
			meeOutsideMeetingService.insert(meeInsideMeeting);
			// 如果有草稿，就删除草稿
			if (null != objId) {
                meeOutsideMeetingDraftService.deleteById(objId);
                Where<ResResource> resResourceWhere = new Where<ResResource>();
                resResourceWhere.eq("biz_id", objId);
                List<ResResource> oldResourceList = resResourceService.selectList(resResourceWhere);
                if (null != oldResourceList && !oldResourceList.isEmpty()) {
                    for (ResResource resource : oldResourceList) {
                        resource.setBizId(meeInsideMeeting.getId());
                    }
                    resResourceService.updateBatchById(oldResourceList);
                }
            }
			if (null != bpmn) {
				// 启动流程
				if (null != meeInsideMeeting.getCreateUserId()) {
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
					param.put("bizType", "mee");
					param.put("bizId", meeInsideMeeting.getId() + "");
					param.put("isUrgent", "1");
					ProcessInstance instance = activitiService.startBpmn(bpmnRun, null, param,
							meeInsideMeeting.getCreateUserId());
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
					userWhere.eq("id", meeInsideMeeting.getCreateUserId());
					AutUser user = autUserService.selectOne(userWhere);
					actActivitiWebService.insertProcessQuery(instance, "1", null, meeInsideMeeting.getMeetingTitle(),
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
					
					taskService.claim(task.getId(), String.valueOf(meeInsideMeeting.getCreateUserId()));
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
//			        actActivitiWebService.insertOrUpdateActivityLog(instance.getId(),WebConstant.PROCESS_OPERATE_STATUS_1,obj.getCreateUserId(),null);

				}
			}

			// 保存附件
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
                    resResource.setBizId(meeInsideMeeting.getId());
                    resResource.setFileDesc("新增外部会议附件上传");
                }
                resResourceService.updateBatchById(resResourceList);
            }
		}
		retMsg.setCode(0);
		retMsg.setObject(map);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	public void updateOffscheduling(MeeOutsideMeeting meeInsideMeeting) throws Exception {
		if (null != meeInsideMeeting && null != meeInsideMeeting.getId()) {
			Where<OffScheduling> where = new Where<OffScheduling>();
			where.eq("biz_id", meeInsideMeeting.getId());
			List<OffScheduling> offSchedulingList = offSchedulingService.selectList(where);
			if (null != offSchedulingList && !offSchedulingList.isEmpty()) {
				for (OffScheduling offScheduling : offSchedulingList) {
					if (StringUtils.isNotEmpty(meeInsideMeeting.getMeetingTitle())) {
						offScheduling.setTheme(meeInsideMeeting.getMeetingTitle());
					}
					if (StringUtils.isNotEmpty(meeInsideMeeting.getAddress())) {
						offScheduling.setPlace(meeInsideMeeting.getAddress());
					}
					if (null != meeInsideMeeting.getIsWarnMail()) {
						offScheduling.setIsWarnMail(meeInsideMeeting.getIsWarnMail());
					}
					if (null != meeInsideMeeting.getIsWarnMsg()) {
						offScheduling.setIsWarnMsg(meeInsideMeeting.getIsWarnMsg());
					}
					if (null != meeInsideMeeting.getIsWarnOnline()) {
						offScheduling.setIsWarnOnline(meeInsideMeeting.getIsWarnOnline());
					}
					if (null != meeInsideMeeting.getMeetingContent()) {
						offScheduling.setContent(meeInsideMeeting.getMeetingContent());
					}
					if (StringUtils.isNotEmpty(meeInsideMeeting.getPartyMemebersId())) {
						offScheduling.setSharedPersonId(meeInsideMeeting.getPartyMemebersId());
					}
					if (StringUtils.isNotEmpty(meeInsideMeeting.getPartyMemeberNames())) {
						offScheduling.setSharedPersonName(meeInsideMeeting.getPartyMemeberNames());
					}
					if (null != meeInsideMeeting.getBeginTime() && null != meeInsideMeeting.getEndTime()) {
						DateTime begTime = new DateTime(meeInsideMeeting.getBeginTime());
						DateTime endTime = new DateTime(meeInsideMeeting.getEndTime());
						Date begDate = DateTime.parse(begTime.toString("yyyy-MM-dd")).toDate();
						Date endDate = DateTime.parse(endTime.toString("yyyy-MM-dd")).toDate();
						offScheduling.setStartDate(begDate);
						offScheduling.setEndDate(endDate);
						String startMinHour = begTime.toString("HH:mm");
						String endMinHour = endTime.toString("HH:mm");
						offScheduling.setStartHourMin(startMinHour);
						offScheduling.setEndHourMin(endMinHour);
					}
					offSchedulingService.update(offScheduling);

				}
			}
		}
	}

	public void addOffscheduling(MeeOutsideMeeting meeInsideMeeting) throws Exception {
		if (null != meeInsideMeeting) {
			OffSchedulingVO offScheduling = new OffSchedulingVO();
			offScheduling.setBizId(meeInsideMeeting.getId());
			offScheduling.setBizType("out");
			offScheduling.setTheme(meeInsideMeeting.getMeetingTitle());
			offScheduling.setPlace(meeInsideMeeting.getAddress());
			offScheduling.setIsWarnOnline(meeInsideMeeting.getIsWarnOnline());
			offScheduling.setIsWarnMsg(meeInsideMeeting.getIsWarnMsg());
			offScheduling.setIsWarnMail(meeInsideMeeting.getIsWarnMail());
			offScheduling.setSharedPersonName("");
			offScheduling.setSharedPersonId("");
			offScheduling.setType(3);
			offScheduling.setContent(meeInsideMeeting.getMeetingContent());
			offScheduling.setSharedPersonId(meeInsideMeeting.getPartyMemebersId());
			offScheduling.setSharedPersonName(meeInsideMeeting.getPartyMemeberNames());
			offScheduling.setCreateUserId(meeInsideMeeting.getCreateUserId());
			offScheduling.setCreateUserName(meeInsideMeeting.getCreateUserName());
			if (null != meeInsideMeeting.getBeginTime() && null != meeInsideMeeting.getEndTime()) {
				DateTime begTime = new DateTime(meeInsideMeeting.getBeginTime());
				DateTime endTime = new DateTime(meeInsideMeeting.getEndTime());
				offScheduling.setStartDateStr(begTime.toString("yyyy-MM-dd HH:mm"));
				offScheduling.setEndDateStr(endTime.toString("yyyy-MM-dd HH:mm"));
				Date begDate = DateTime.parse(begTime.toString("yyyy-MM-dd")).toDate();
				Date endDate = DateTime.parse(endTime.toString("yyyy-MM-dd")).toDate();
				offScheduling.setStartDate(begDate);
				offScheduling.setEndDate(endDate);
				String startMinHour = begTime.toString("HH:mm");
				String endMinHour = endTime.toString("HH:mm");
				offScheduling.setStartHourMin(startMinHour);
				offScheduling.setEndHourMin(endMinHour);
			}
			offSchedulingWebService.add(offScheduling, null);
		}
	}

	@Transactional
	public RetMsg update(MeeOutsideMeetingVO obj, AutUser user) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			MeeOutsideMeeting meeInsideMeeting = meeOutsideMeetingService.selectById(obj.getId());
			if (meeInsideMeeting.getBeginTime().before(new Date())
					|| meeInsideMeeting.getBeginTime().equals(new Date())) {
				retMsg.setCode(1);
				retMsg.setMessage("会议已经开始不可变更");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getMeetingTitle())) {
				obj.setMeetingTitle("");
				retMsg.setCode(1);
				retMsg.setMessage("会议标题不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getMeetingHost())) {
				obj.setMeetingHost("");
				retMsg.setCode(1);
				retMsg.setMessage("主持人不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getContacts())) {
				obj.setContacts("");
				retMsg.setCode(1);
				retMsg.setMessage("联系人不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getAddress())) {
				obj.setAddress("");
				retMsg.setCode(1);
				retMsg.setMessage("填写地址不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getPartyMemebersId())) {
				obj.setPartyMemebersId("");
				retMsg.setCode(1);
				retMsg.setMessage("参会人员不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getPartyMemeberNames())) {
				obj.setPartyMemeberNames("");
				retMsg.setCode(1);
				retMsg.setMessage("参会人员不能为空");
				return retMsg;
			}
			if (null == obj.getIsWarnMail()) {
				obj.setIsWarnMail(0);
			}
			if (null == obj.getIsWarnMsg()) {
				obj.setIsWarnMsg(0);
			}
			if (null == obj.getIsWarnOnline()) {
				obj.setIsWarnOnline(0);
			}

			if (null == obj.getBeginTime()) {
				// obj.setBeginTime(date);
				retMsg.setCode(1);
				retMsg.setMessage("会议开始时间不能为空");
				return retMsg;
			}
			if (null == obj.getEndTime()) {
				// obj.setEndTime(date);
				retMsg.setCode(1);
				retMsg.setMessage("会议结束时间不能为空");
				return retMsg;
			}
			Date date = new Date();
			if (obj.getBeginTime().before(date)) {
				retMsg.setCode(1);
				retMsg.setMessage("会议时间不能小于当天");
				return retMsg;
			}
			if (obj.getBeginTime().after(obj.getEndTime())) {
				retMsg.setCode(1);
				retMsg.setMessage("会议开始时间不能小于结束时间");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getMeetingContent())) {
				obj.setMeetingContent("");
				retMsg.setCode(1);
				retMsg.setMessage("会议内容不能为空");
				return retMsg;
			}
			if (null != meeInsideMeeting) {
				if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
					meeInsideMeeting.setMeetingTitle(obj.getMeetingTitle());
				}
				if (StringUtils.isNotEmpty(obj.getMeetingHost())) {
					meeInsideMeeting.setMeetingHost(obj.getMeetingHost());
				}
				if (StringUtils.isNotEmpty(obj.getContacts())) {
					meeInsideMeeting.setContacts(obj.getContacts());
				}
				if (StringUtils.isNotEmpty(obj.getAddress())) {
					meeInsideMeeting.setAddress(obj.getAddress());
				}
				if (StringUtils.isNotEmpty(obj.getPartyMemebersId())) {
					meeInsideMeeting.setPartyMemebersId(obj.getPartyMemebersId());
				}
				if (StringUtils.isNotEmpty(obj.getPartyMemeberNames())) {
					meeInsideMeeting.setPartyMemeberNames(obj.getPartyMemeberNames());
				}
				if (null != obj.getIsWarnMail()) {
					meeInsideMeeting.setIsWarnMail(obj.getIsWarnMail());
				}
				if (null != obj.getIsWarnMsg()) {
					meeInsideMeeting.setIsWarnMsg(obj.getIsWarnMsg());
				}
				if (null != obj.getIsWarnOnline()) {
					meeInsideMeeting.setIsWarnOnline(obj.getIsWarnOnline());
				}
				if (null != obj.getBeginTime()) {
					meeInsideMeeting.setBeginTime(obj.getBeginTime());
				}
				if (null != obj.getEndTime()) {
					meeInsideMeeting.setEndTime(obj.getEndTime());
				}
				if (StringUtils.isNotEmpty(obj.getMeetingContent())) {
					meeInsideMeeting.setMeetingContent(obj.getMeetingContent());
				}
				if (StringUtils.isNotEmpty(obj.getAttendances())) {
					meeInsideMeeting.setAttendances(obj.getAttendances());
				}
				if (null != obj.getDelMember() && StringUtils.isNotEmpty(obj.getDelMember())) {
					meeInsideMeeting.setDelMember(obj.getDelMember());
				}
				if (null != obj.getNewMember() && StringUtils.isNotEmpty(obj.getNewMember())) {
					meeInsideMeeting.setNewMember(obj.getNewMember());
				}
				meeOutsideMeetingService.updateById(meeInsideMeeting);

				// 变更日程内部会议
				updateOffscheduling(meeInsideMeeting);
				retMsg.setObject(meeInsideMeeting);
				// if (StringUtils.isNotEmpty(obj.getNewMember())) {
				// meeInsideMeeting.setPartyMemebersId(obj.getNewMember());
				// meeInsideMeeting.setPartyMemeberNames(obj.getNewMemberName());
				// // 提醒
				// if (0 != meeInsideMeeting.getIsWarnMail()) {
				// meeOutsideMeetingService.mailWarn(meeInsideMeeting, user,
				// MEET_ADD);
				// }
				// if (0 != meeInsideMeeting.getIsWarnMsg()) {
				//
				// }
				// if (0 != meeInsideMeeting.getIsWarnOnline()) {
				// meeOutsideMeetingService.onlineWarn(meeInsideMeeting, user,
				// MEET_ADD);
				// }
				// }
				// if (StringUtils.isNotEmpty(obj.getDelMember())) {
				// meeInsideMeeting.setPartyMemebersId(obj.getDelMember());
				// meeInsideMeeting.setPartyMemeberNames(obj.getDelMember());
				// // 提醒
				// if (0 != meeInsideMeeting.getIsWarnMail()) {
				// meeOutsideMeetingService.mailWarn(meeInsideMeeting, user,
				// MEET_CANCEL);
				// }
				// if (0 != meeInsideMeeting.getIsWarnMsg()) {
				//
				// }
				// if (0 != meeInsideMeeting.getIsWarnOnline()) {
				// meeOutsideMeetingService.onlineWarn(meeInsideMeeting, user,
				// MEET_CANCEL);
				// }
				// }
				//会议草稿新上传附件
                List<ResResource> newResourceList = obj.getResResourceList();
                List<Long> newResourceIds = new ArrayList<Long>();
                if (null != newResourceList && newResourceList.size() > 0) {
                    for (ResResource resResource : newResourceList) {
                        newResourceIds.add(resResource.getId());
                    }
                }
                if (null != newResourceIds && newResourceIds.size() > 0) {
                    List<ResResource> addResource = resResourceService.selectBatchIds(newResourceIds);
                    for (ResResource resResource : addResource) {
                        resResource.setBizId(meeInsideMeeting.getId());
                        resResource.setFileDesc("外部会议编辑附件上传");
                    }
                    resResourceService.updateBatchById(addResource);
                }
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 提交本环节
	 * 
	 * @param variables
	 * @param taskId
	 * @param bizId
	 * @param userId
	 * @param createUser
	 * @return
	 * @throws Exception
	 */
	public RetMsg completeTask(Map<String, Object> variables, String taskId, String bizId, String userId,
			AutUser createUser, String message) throws Exception {
		RetMsg retMsg = new RetMsg();
		HashMap<String, String> map = new HashMap<String, String>();
		Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(null == currentTask){
		  retMsg.setCode(1);
		  retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
		  return retMsg;
		}
		Map<String,Object> logMap = new HashMap<String,Object>();  
		String processInstanceId = currentTask.getProcessInstanceId();
	    logMap.put("processInstanceId", processInstanceId);
		Authentication.setAuthenticatedUserId(createUser.getId().toString());
		taskService.addComment(taskId, processInstanceId, message);
		taskService.setAssignee(taskId, createUser.getId().toString());
		activitiService.completeTask(variables, taskId);
		logMap.put("commont", message);
		logMap.put("preTask", currentTask);
		Date date = new Date();
		if (StringUtils.isNotEmpty(bizId)) {
			MeeOutsideMeeting outsideMeeting = meeOutsideMeetingService.selectById(Long.parseLong(bizId));
			if (null != outsideMeeting) {
				ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
						.singleResult();
				if (null == pi) {
				    logMap.put("nextNode", "EndEvent_");
					// 如果是结束节点，则记录最后一个办理人
					actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
					outsideMeeting.setAuditStatus(3);// 审核通过
					outsideMeeting.setAuditTime(date);
					outsideMeeting.setAuditReason("审核通过");
					meeOutsideMeetingService.updateById(outsideMeeting);
					// 审核通过 -->定时通知-->加入日程
					addOffscheduling(outsideMeeting);
					map.put("isEnd", "1");
				}
			}
		}
		List<String> userIdList = new ArrayList<String>();
		List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		if (null != taskList && !taskList.isEmpty()) {
			Task task = taskList.get(0);
			if (null != task && StringUtils.isNotEmpty(userId)) {
				userIdList = Arrays.asList(userId.split(";"));
				if (null != userIdList && !userIdList.isEmpty()) {
					actFixedFormService.deleteOrgnlTaskAuth(task);
					// 原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
					HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
                    if (null != hisTask.getClaimTime() || null != hisTask.getAssignee()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
                        taskService.unclaim(task.getId());
                    }
					HashSet<Long> userIdSet = new HashSet<Long>();
					for (String uId : userIdList) {
					  userIdSet.add(Long.valueOf(uId));
					  taskService.addCandidateUser(task.getId(), uId);
					}
//					actAljoinQueryService.cleanQureyCurrentUser(task.getId());
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
		logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_1);
		actActivitiWebService.insertOrUpdateLog(logMap, createUser.getId());
		//记录流程日志
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
		String handle = "";
		// 环节办理人待办数量-1
		if (null != userIdList && !userIdList.isEmpty()) {
			handle = StringUtil.list2str(userIdList, ";");
		}

		map.put("processInstanceId", processInstanceId);// 流程实例id
		map.put("handle", handle);// 下一级办理人
		retMsg.setObject(map);// 返回给controller异步调用在线消息
		retMsg.setCode(0);
		retMsg.setMessage("操作成功!");
		return retMsg;
	}

	@Transactional
	public RetMsg cancel(MeeOutsideMeetingVO obj, AutUser user) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			MeeOutsideMeeting meeOutsideMeeting = meeOutsideMeetingService.selectById(obj.getId());
			if (meeOutsideMeeting != null && meeOutsideMeeting.getMeetingSituation() != 1) {
				if (meeOutsideMeeting.getMeetingSituation() == 2) {
					retMsg.setMessage("会议已经完成，不可取消");
				} else {
					retMsg.setMessage("会议已被其他管理员取消，请勿重复操作");
				}
				retMsg.setCode(1);
				return retMsg;
			}
			if (meeOutsideMeeting.getBeginTime().before(new Date())
					|| meeOutsideMeeting.getBeginTime().equals(new Date())) {
				retMsg.setCode(1);
				retMsg.setMessage("会议已经开始不可取消");
				return retMsg;
			}
			if (null != meeOutsideMeeting) {
				meeOutsideMeeting.setMeetingSituation(3);
				meeOutsideMeetingService.updateById(meeOutsideMeeting);
				// 删除对应日程
				offSchedulingService.deleteOffSchedule(meeOutsideMeeting.getId());
				// if (null != obj.getIsWarnMsg()) {
				// meeOutsideMeetingService.smaWarn(meeOutsideMeeting, user,
				// MEET_CANCEL);
				// }
				// if (null != obj.getIsWarnOnline()) {
				// meeOutsideMeetingService.onlineWarn(meeOutsideMeeting, user,
				// MEET_CANCEL);
				// }
				// if (null != obj.getIsWarnMail()) {
				// meeOutsideMeetingService.mailWarn(meeOutsideMeeting, user,
				// MEET_CANCEL);
				// }
				retMsg.setObject(meeOutsideMeeting);
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * app提交本环节
	 *
	 * @param variables
	 * @param taskId
	 * @param bizId
	 * @param userId
	 * @param createUser
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public RetMsg completeAppTask(Map<String, Object> variables, String taskId, String bizId, String userId,
														 AutUser createUser, String message) throws Exception {
		RetMsg retMsg = new RetMsg();
		Map<String,Object> logMap = new HashMap<String,Object>();  
		HashMap<String, String> map = new HashMap<String, String>();
		Task currentTask = taskService.createTaskQuery().taskId(taskId).singleResult();
		if(null == currentTask){
		  retMsg.setCode(1);
		  retMsg.setMessage("任务被撤回或被其他人完成，请刷新待办列表");
		  return retMsg;
		}
		logMap.put("preTask", currentTask);
		String processInstanceId = currentTask.getProcessInstanceId();
		logMap.put("processInstanceId", processInstanceId);
		Authentication.setAuthenticatedUserId(createUser.getId().toString());
		taskService.addComment(taskId, processInstanceId, message);
		logMap.put("commont", message);
		taskService.setAssignee(taskId, createUser.getId().toString());
		activitiService.completeTask(variables, taskId);
		Date date = new Date();
		if (StringUtils.isNotEmpty(bizId)) {
			MeeOutsideMeeting outsideMeeting = meeOutsideMeetingService.selectById(Long.parseLong(bizId));
			if (null != outsideMeeting) {
				ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
					.singleResult();
				if (null == pi) {
					// 如果是结束节点，则记录最后一个办理人
				    logMap.put("nextNode", "EndEvent_");
					actFixedFormService.updateLastAsignee(processInstanceId, createUser.getFullName());
					outsideMeeting.setAuditStatus(3);// 审核通过
					outsideMeeting.setAuditTime(date);
					outsideMeeting.setAuditReason("审核通过");
					meeOutsideMeetingService.updateById(outsideMeeting);
					// 审核通过 -->定时通知-->加入日程
					addOffscheduling(outsideMeeting);
					map.put("isEnd", "1");
				}
			}
		}
		List<String> userIdList = new ArrayList<String>();
		List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
		if (null != taskList && !taskList.isEmpty()) {
			Task task = taskList.get(0);
			if (null != task && StringUtils.isNotEmpty(userId)) {
				userIdList = Arrays.asList(userId.split(";"));
				if (null != userIdList && !userIdList.isEmpty()) {
					actFixedFormService.deleteOrgnlTaskAuth(task);
					// 原来的逻辑是，一个用户签收--没有进行候选操作，多个用户设置候选），改成不管一个还是多个，只设置候选，并清空查询表的当前办理人
					HistoricTaskInstance hisTask = historyService.createHistoricTaskInstanceQuery().taskId(task.getId()).singleResult();
					if (null != hisTask.getClaimTime()){ //unclaim()方法会把task的claimTime字段填值，所以要在确定这份文件有被人签收时解签收
						taskService.unclaim(task.getId());
					}
					HashSet<Long> userIdSet = new HashSet<Long>();
					for (String uId : userIdList) {
					    userIdSet.add(Long.valueOf(uId));
						taskService.addCandidateUser(task.getId(), uId);
					}
//					actAljoinQueryService.cleanQureyCurrentUser(task.getId());
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
//        actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,WebConstant.PROCESS_OPERATE_STATUS_1,createUser.getId(),null);
		String handle = "";
		// 环节办理人待办数量-1
		// 下一环节办理人待办数量+1
		if (null != userIdList && !userIdList.isEmpty()) {
			handle = StringUtil.list2str(userIdList, ";");
		}

		map.put("processInstanceId", processInstanceId);// 流程实例id
		map.put("handle", handle);// 下一级办理人
		retMsg.setObject(map);// 返回给controller异步调用在线消息
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功!");
		return retMsg;
	}
	
	@Transactional
  public RetMsg jump2Task2(String taskId, String bizId, String message, Long createUserId)
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
      Authentication.setAuthenticatedUserId(String.valueOf(createUserId));
      taskService.addComment(taskId, processInstanceId, message);
      logMap.put("commont", message);
      if (org.apache.commons.lang3.StringUtils.isNotEmpty(currentTaskKey)) {
        List<TaskDefinition> preList =
            activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
        if (null != preList && !preList.isEmpty()) {
          String targetTaskKey = preList.get(0).getKey();
          if (org.apache.commons.lang3.StringUtils.isNotEmpty(targetTaskKey)) {
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

            activitiService.jump2Task2(targetTaskKey, processInstanceId);
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
            // actAljoinQueryService.cleanQureyCurrentUser(task.getId());
            Where<AutUser> uwhere = new Where<AutUser>();
            uwhere.setSqlSelect("id,full_name");
            uwhere.in("id", userIdSet);
            List<AutUser> userList = autUserService.selectList(uwhere);
            String userName = "";
            for (AutUser autUser : userList) {
              userName += autUser.getFullName() + ";";
            }
            Map<String, List<String>> taskKeyMap = new HashMap<String, List<String>>();
            taskKeyMap.put(task.getTaskDefinitionKey(), assignees);
            logMap.put("taskKeyUserMap", taskKeyMap);
            logMap.put("userTask", task);
            logMap.put("operateStatus", WebConstant.PROCESS_OPERATE_STATUS_2);
            actActivitiWebService.insertOrUpdateLog(logMap, createUserId);
            actAljoinQueryService.updateCurrentUserName(task.getId(), userName);
            //记录流程日志
//            actActivitiWebService.insertOrUpdateActivityLog(processInstanceId,WebConstant.PROCESS_OPERATE_STATUS_2,createUserId,null);
          }
        }
      }
    }
    // 下一环节办理人待办数量+1
    String handle = "";
    if (null != assignees && !assignees.isEmpty()) {
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
}
