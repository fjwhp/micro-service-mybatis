package aljoin.web.service.off;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.entity.OffSchedulingHis;
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.off.iservice.OffSchedulingHisService;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.util.DateUtil;

@Service
public class OffSchedulingWebService {

	@Resource
	private OffSchedulingService offSchedulingService;
	@Resource
	private OffSchedulingHisService offSchedulingHisService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private AutMsgOnlineService autMsgOnlineService;
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
	private SysMsgModuleInfoService sysMsgModuleInfoService;
	@Resource
	private SmsShortMessageService smsShortMessageService;

	@Transactional
	public RetMsg add(OffSchedulingVO obj, AutUser createuser) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj) {
			// 向日程安排表插入记录
			if (StringUtils.isEmpty(obj.getPlace())) {
				obj.setPlace("");
				retMsg.setCode(1);
				retMsg.setMessage("地点不能为空");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getContent())) {
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("内容不能为空");
				return retMsg;
			}
			if (null != obj.getStartDateStr() && StringUtils.isNotEmpty(obj.getStartDateStr())) {
				Date startDate = DateUtil.str2date(obj.getStartDateStr());
				obj.setStartDate(startDate);
				String startTime = obj.getStartDateStr().substring(obj.getStartDateStr().indexOf(" ") + 1,
						obj.getStartDateStr().length());
				obj.setStartHourMin(startTime);
			} else {
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("开始时间不能为空");
				return retMsg;
			}
			if (null != obj.getEndDateStr() && StringUtils.isNotEmpty(obj.getEndDateStr())) {
				Date endDate = DateUtil.str2date(obj.getEndDateStr());
				obj.setEndDate(endDate);
				String endTime = obj.getEndDateStr().substring(obj.getEndDateStr().indexOf(" ") + 1,
						obj.getEndDateStr().length());
				obj.setEndHourMin(endTime);
			} else {
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("结束时间不能为空");
				return retMsg;
			}
			Date beginTime = DateUtil.str2datetime(obj.getStartDateStr() + ":00");
			Date endTime = DateUtil.str2datetime(obj.getEndDateStr() + ":00");
			if (endTime.before(beginTime) || endTime.equals(beginTime)) {
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("结束时间必须大于开始时间");
				return retMsg;
			}
			if (obj.getSharedPersonId() != null && StringUtils.isEmpty(obj.getSharedPersonId())) {
				obj.setSharedPersonId("");
				if (null == obj.getType()) {
					obj.setType(2);
				}
			} else {
				if (null == obj.getType()) {
					obj.setType(1);
				}
			}
			if (StringUtils.isEmpty(obj.getSharedPersonName())) {
				obj.setSharedPersonName("null");
			}
			if (StringUtils.isEmpty(obj.getSharedPersonId())){
			  obj.setSharedPersonId("null");
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
			OffScheduling offScheduling = new OffScheduling();
			BeanUtils.copyProperties(obj, offScheduling);
			offSchedulingService.insert(offScheduling);

			// 向日程安排历史表插入记录
			OffSchedulingHis schedulingHis = new OffSchedulingHis();
			BeanUtils.copyProperties(offScheduling, schedulingHis);
            retMsg.setObject(offScheduling);
			offSchedulingHisService.insert(schedulingHis);

//			if (offScheduling.getIsWarnMail() == 1 && offScheduling.getType() != 3) {// 邮件提醒
//				maiWarn(offScheduling, createuser);
//			}
//			if (offScheduling.getIsWarnMsg() == 1 && offScheduling.getType() != 3) {// 短信提醒
//				smaWarn(offScheduling, createuser);
//			}
//			if (offScheduling.getIsWarnOnline() == 1 && offScheduling.getType() != 3) {// 在线短信提醒
//				onlineWarn(offScheduling, createuser);
//			}
		}
		// 删除日程安排表中的过期数据
		offSchedulingService.deleteOldData();
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Transactional
	public RetMsg update(OffSchedulingVO obj, AutUser user) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			OffScheduling schedule = offSchedulingService.selectById(obj.getId());
			if (null != schedule) {
				if (schedule.getType() == 1) {
					retMsg.setCode(1);
					retMsg.setMessage("共享计划不能修改");
					return retMsg;
				}
				if (StringUtils.isNotEmpty(obj.getTheme())) {
					schedule.setTheme(obj.getTheme());
				}
				if (StringUtils.isNotEmpty(obj.getPlace())) {
					schedule.setPlace(obj.getPlace());
				}
				if (null != obj.getStartDateStr() && StringUtils.isNotEmpty(obj.getStartDateStr())) {
					Date startDate = DateUtil.str2date(obj.getStartDateStr());
					obj.setStartDate(startDate);
					String startTime = obj.getStartDateStr().substring(obj.getStartDateStr().indexOf(" ") + 1,
							obj.getStartDateStr().length());
					obj.setStartHourMin(startTime);
				} else {
					obj.setContent("");
					retMsg.setCode(1);
					retMsg.setMessage("开始时间不能为空");
					return retMsg;
				}
				if (null != obj.getEndDateStr() && StringUtils.isNotEmpty(obj.getEndDateStr())) {
					Date endDate = DateUtil.str2date(obj.getEndDateStr());
					obj.setEndDate(endDate);
					String endTime = obj.getEndDateStr().substring(obj.getEndDateStr().indexOf(" ") + 1,
							obj.getEndDateStr().length());
					obj.setEndHourMin(endTime);
				} else {
					obj.setContent("");
					retMsg.setCode(1);
					retMsg.setMessage("结束时间不能为空");
					return retMsg;
				}
				Date beginTime = DateUtil.str2datetime(obj.getStartDateStr() + ":00");
				Date endTime = DateUtil.str2datetime(obj.getEndDateStr() + ":00");
				if (endTime.before(beginTime) || endTime.equals(beginTime)) {
					obj.setContent("");
					retMsg.setCode(1);
					retMsg.setMessage("结束时间必须大于开始时间");
					return retMsg;
				}
				if (null != obj.getStartDate()) {
					schedule.setStartDate(obj.getStartDate());
				}
				if (null != obj.getEndDate()) {
					schedule.setEndDate(obj.getEndDate());
				}
				if (StringUtils.isNotEmpty(obj.getStartHourMin())) {
					schedule.setStartHourMin(obj.getStartHourMin());
				}
				if (StringUtils.isNotEmpty(obj.getEndHourMin())) {
					schedule.setEndHourMin(obj.getEndHourMin());
				}
				if (StringUtils.isNotEmpty(obj.getContent())) {
					schedule.setContent(obj.getContent());
				}
				if (null != obj.getSharedPersonId() && StringUtils.isNotEmpty(obj.getSharedPersonId())) {
					schedule.setSharedPersonId(obj.getSharedPersonId());
					if (null == obj.getType()) {
						schedule.setType(1);
					}
				} else {
					if (null == obj.getType()) {
						schedule.setType(2);
					}
				}
				if (null != obj.getSharedPersonName() && StringUtils.isNotEmpty(obj.getSharedPersonName())) {
					schedule.setSharedPersonName(obj.getSharedPersonName());
				}
				if (null != obj.getIsWarnMail()) {
					schedule.setIsWarnMail(obj.getIsWarnMail());
				}
				if (null != obj.getIsWarnMsg()) {
					schedule.setIsWarnMsg(obj.getIsWarnMsg());
				}
				if (null != obj.getIsWarnOnline()) {
					schedule.setIsWarnOnline(obj.getIsWarnOnline());
				}
				offSchedulingService.updateById(schedule);
				retMsg.setObject(schedule);
				if (null != schedule.getId()) {
					OffSchedulingHis offSchedulingHis = offSchedulingHisService.selectById(schedule.getId());
					if (null != offSchedulingHis) {
						if (StringUtils.isNotEmpty(schedule.getTheme())) {
							offSchedulingHis.setTheme(schedule.getTheme());
						}
						if (StringUtils.isNotEmpty(schedule.getPlace())) {
							offSchedulingHis.setPlace(schedule.getPlace());
						}
						if (null != schedule.getStartDate()) {
							offSchedulingHis.setStartDate(schedule.getStartDate());
						}
						if (null != schedule.getEndDate()) {
							offSchedulingHis.setEndDate(schedule.getEndDate());
						}
						if (StringUtils.isNotEmpty(schedule.getStartHourMin())) {
							offSchedulingHis.setStartHourMin(schedule.getStartHourMin());
						}
						if (StringUtils.isNotEmpty(schedule.getEndHourMin())) {
							offSchedulingHis.setEndHourMin(schedule.getEndHourMin());
						}
						if (StringUtils.isNotEmpty(schedule.getContent())) {
							offSchedulingHis.setContent(schedule.getContent());
						}
						if (null != obj.getSharedPersonId()) {
							offSchedulingHis.setSharedPersonId(schedule.getSharedPersonId());
							if (null == obj.getType()) {
								offSchedulingHis.setType(1);
							}
						} else {
							if (null == obj.getType()) {
								offSchedulingHis.setType(2);
							}
						}
						if (null != obj.getSharedPersonName()) {
							offSchedulingHis.setSharedPersonName(schedule.getSharedPersonName());
						}
						if (null != schedule.getIsWarnMail()) {
							offSchedulingHis.setIsWarnMail(schedule.getIsWarnMail());
						}
						if (null != schedule.getIsWarnMsg()) {
							offSchedulingHis.setIsWarnMsg(schedule.getIsWarnMsg());
						}
						if (null != schedule.getIsWarnOnline()) {
							offSchedulingHis.setIsWarnOnline(schedule.getIsWarnOnline());
						}
						offSchedulingHisService.updateById(offSchedulingHis);
					}
				}
			}
			/*if (schedule.getIsWarnMail() == 1 && schedule.getType() != 3) {// 邮件提醒
				maiWarn(schedule, user);
			}
			if (schedule.getIsWarnMsg() == 1 && schedule.getType() != 3) {// 短信提醒
				smaWarn(schedule, user);
			}
			if (schedule.getIsWarnOnline() == 1 && schedule.getType() != 3) {// 在线短信提醒
				onlineWarn(schedule, user);
			}*/
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	public AutMsgOnlineRequestVO onlineWarn(OffScheduling offScheduling, AutUser user) throws Exception {
		Map<String, String> list = new HashMap<String, String>();
		list.put("theme", offScheduling.getTheme());
		list.put("create", user.getFullName());
		list.put("startTime", DateUtil.date2str(offScheduling.getStartDate()) + " " + offScheduling.getStartHourMin());
		list.put("endTime", DateUtil.date2str(offScheduling.getEndDate()) + " " + offScheduling.getEndHourMin());
		list.put("Share", offScheduling.getSharedPersonName());
		List<String> templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
				WebConstant.TEMPLATE_PLAN_NAME, WebConstant.TEMPLATE_BEHAVIOR_XTRC);

		String sharedPersonIds = offScheduling.getSharedPersonId();
		AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
		if (null != sharedPersonIds && StringUtils.isNotEmpty(sharedPersonIds)) {
			if (templateList != null && !templateList.isEmpty()) {
				requestVO.setMsgContent(templateList.get(0));
			} else {
				requestVO.setMsgContent(WebConstant.ONLINE_MSG_WORKPLAN_TITILE_PRE + offScheduling.getTheme());
			}
			// 封装推送信息请求信息
			requestVO.setFromUserId(offScheduling.getCreateUserId());
			requestVO.setFromUserFullName(user.getFullName());
			requestVO.setFromUserName(offScheduling.getCreateUserName());
			requestVO.setGoUrl("off/offScheduling/offSchedulingDetailPage?id=" + offScheduling.getId());
			requestVO.setMsgType(WebConstant.ONLINE_MSG_WORKPLAN);
			// 前端传来的1个或多个userId,截取存进List
			List<String> receiveUserIdList = Arrays.asList(sharedPersonIds.split(";"));
			requestVO.setToUserId(receiveUserIdList);
			// 调用推送在线消息接口
//			autMsgOnlineService.pushMessageToUserList(requestVO);
		}
    return requestVO;
	}

	public MaiWriteVO maiWarn(OffScheduling offScheduling, AutUser user) throws Exception {
		String sharedPersonIds = offScheduling.getSharedPersonId();
		String sharedPersonFullNames = offScheduling.getSharedPersonName();
		String sharedPresonNames = "";
		List<String> receivePerson = Arrays.asList(sharedPersonIds.split(";"));
		Where<AutUser> userWhere = new Where<AutUser>();
		userWhere.in("id", receivePerson);
		List<AutUser> userlist = autUserService.selectList(userWhere);
		for (AutUser user1 : userlist) {
			for (String id : receivePerson) {
				if (id.equals(String.valueOf(user1.getId()))) {
					sharedPresonNames += user1.getUserName() + ";";
				}
			}
		}
		MaiWriteVO mai = new MaiWriteVO();
		MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
		MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
		
		maiReceiveBox.setReceiveUserIds(sharedPersonIds);
		maiReceiveBox.setReceiveFullNames(sharedPersonFullNames);
		maiReceiveBox.setReceiveUserNames(sharedPresonNames);
		maiReceiveBox.setSendId(offScheduling.getCreateUserId());
		maiReceiveBox.setSendUserName(user.getUserName());
		maiReceiveBoxSearch.setSendFullName(user.getFullName());
		maiReceiveBoxSearch.setAttachmentCount(0);
		Map<String, String> list = new HashMap<String, String>();
		list.put("theme", offScheduling.getTheme());
		list.put("create", user.getFullName());
		list.put("startTime", DateUtil.date2str(offScheduling.getStartDate()) + " " + offScheduling.getStartHourMin());
		list.put("endTime", DateUtil.date2str(offScheduling.getEndDate()) + " " + offScheduling.getEndHourMin());
		list.put("Share", offScheduling.getSharedPersonName());
		List<String> templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
				WebConstant.TEMPLATE_PLAN_NAME, WebConstant.TEMPLATE_BEHAVIOR_XTRC);
		if (templateList != null && !templateList.isEmpty()) {
		    maiReceiveBoxSearch.setSubjectText(templateList.get(1));
			maiReceiveBox.setMailContent(templateList.get(0));
		} else {
		    maiReceiveBoxSearch.setSubjectText(WebConstant.ONLINE_MSG_WORKPLAN_TITILE_PRE + offScheduling.getTheme());
			maiReceiveBox.setMailContent(offScheduling.getContent());
		}
		mai.setMaiReceiveBox(maiReceiveBox);
		mai.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
//		maiSendBoxService.add(mai, user);
		return mai;
	}

	public SmsShortMessage smaWarn(OffScheduling offScheduling, AutUser user) throws Exception {
		// 调用模板
		Map<String, String> list = new HashMap<String, String>();
		list.put("theme", offScheduling.getTheme());
		list.put("create", user.getFullName());
		list.put("startTime", DateUtil.date2str(offScheduling.getStartDate()) + " " + offScheduling.getStartHourMin());
		list.put("endTime", DateUtil.date2str(offScheduling.getEndDate()) + " " + offScheduling.getEndHourMin());
		list.put("Share", offScheduling.getSharedPersonName());
		List<String> templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
				WebConstant.TEMPLATE_PLAN_NAME, WebConstant.TEMPLATE_BEHAVIOR_XTRC);
		SmsShortMessage smsShortMessage = new SmsShortMessage();
		String userids = offScheduling.getSharedPersonId();
		String userNames = offScheduling.getSharedPersonName();
		if(userids != null && userids != "") {
		  userids = userids.substring(0, userids.length() - 1);
		}
		if(userNames != null && userNames != "") {
		  userNames = userNames.substring(0, userNames.length() - 1);
        }
		smsShortMessage.setReceiverId(userids);
		smsShortMessage.setReceiverName(userNames);
		smsShortMessage.setContent(templateList.get(0));
		smsShortMessage.setSendNumber(templateList.size());
		String[] receiveId = offScheduling.getSharedPersonId().split(";");
		smsShortMessage.setSendNumber(receiveId.length);
		smsShortMessage.setIsActive(1);
		smsShortMessage.setSendTime(new Date());
		smsShortMessage.setSendStatus(0);
		if(templateList != null && !templateList.isEmpty()){
			smsShortMessage.setTheme(templateList.get(0));
		}
//		smsShortMessageService.add(smsShortMessage,user);
		return smsShortMessage;
	}
}
