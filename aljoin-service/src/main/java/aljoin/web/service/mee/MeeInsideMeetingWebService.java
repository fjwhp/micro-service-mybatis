package aljoin.web.service.mee;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.iservice.MeeInsideMeetingDraftService;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.mee.iservice.MeeMeetingRoomService;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.web.service.off.OffSchedulingWebService;

/**
 * 
 * 内部会议Service（把需要调用其他模块的方法抽出来，避免循环调用）
 *
 * 										@return：Page<MeeInsideMeeting>
 *
 * @author：sln
 *
 * @date：2017-10-12
 */
@Component
public class MeeInsideMeetingWebService {

	@Resource
	private MeeInsideMeetingService meeInsideMeetingService;
	@Resource
	private ResResourceService resResourceService;
	@Resource
	private OffSchedulingWebService offSchedulingWebService;
	@Resource
	private OffSchedulingService offSchedulingService;
	@Resource
	private MeeInsideMeetingDraftService meeInsideMeetingDraftService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private MeeMeetingRoomService meeMeetingRoomService;

//	private static final String MEET_ADD = "add";
//	private static final String MEET_UPDATE = "update";
//	private static final String MEET_CANCEL = "cancel";

	/**
	 * 
	 * 新增会议日程
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public void addOffscheduling(MeeInsideMeeting meeInsideMeeting) throws Exception {
		if (null != meeInsideMeeting) {
			OffSchedulingVO offScheduling = new OffSchedulingVO();
			offScheduling.setBizId(meeInsideMeeting.getId());
			offScheduling.setBizType("in");
			offScheduling.setTheme(meeInsideMeeting.getMeetingTitle());
			;
			offScheduling.setPlace(meeInsideMeeting.getAddress());
			offScheduling.setIsWarnOnline(meeInsideMeeting.getIsWarnOnline());
			offScheduling.setIsWarnMsg(meeInsideMeeting.getIsWarnMsg());
			offScheduling.setIsWarnMail(meeInsideMeeting.getIsWarnMail());
			offScheduling.setSharedPersonName("");
			offScheduling.setSharedPersonId("");
			offScheduling.setType(3);
			offScheduling.setSharedPersonId(meeInsideMeeting.getPartyMemebersId());
			offScheduling.setSharedPersonName(meeInsideMeeting.getPartyMemeberNames());
			offScheduling.setContent(meeInsideMeeting.getMeetingContent());
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

	/**
	 * 
	 * 更新会议日程
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	public void updateOffscheduling(MeeInsideMeeting meeInsideMeeting) throws Exception {
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

	/**
	 * 
	 * 会议变更
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	@Transactional
	public RetMsg update(MeeInsideMeetingVO obj, AutUser createUser) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			MeeInsideMeeting meeInsideMeeting = meeInsideMeetingService.selectById(obj.getId());
			if (meeInsideMeeting.getBeginTime().before(new Date())
					|| meeInsideMeeting.getBeginTime().equals(new Date())) {
				retMsg.setCode(1);
				retMsg.setMessage("会议已经开始不可变更");
				return retMsg;
			}
			if (StringUtils.isEmpty(obj.getPartyMemebersId())) {
				obj.setPartyMemebersId("");
				retMsg.setCode(1);
				retMsg.setMessage("参会人员不能为空");
				return retMsg;
			}
			if (null == obj.getBeginTime()) {
				retMsg.setCode(1);
				retMsg.setMessage("会议开始时间不能为空");
				return retMsg;
			}
			if (null == obj.getEndTime()) {
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
				if (StringUtils.isNotEmpty(obj.getPartyMemeberNames())){
					meeInsideMeeting.setPartyMemeberNames(obj.getPartyMemeberNames());
				}
				if (StringUtils.isNotEmpty(obj.getPartyMemebersId())){
					meeInsideMeeting.setPartyMemebersId(obj.getPartyMemebersId());
				}
				if (null != obj.getNewMember() && StringUtils.isNotEmpty(obj.getNewMember())) {
					meeInsideMeeting.setNewMember(obj.getNewMember());
				}
				if (null != obj.getDelMember() && StringUtils.isNotEmpty(obj.getDelMember())) {
					meeInsideMeeting.setDelMember(obj.getDelMember());
				}
				if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
					meeInsideMeeting.setMeetingRoomName(obj.getMeetingRoomName());
				}
				if (obj.getMeetingRoomId() != null) {
					meeInsideMeeting.setMeetingRoomId(obj.getMeetingRoomId());
				}
				meeInsideMeetingService.updateById(meeInsideMeeting);
				// 变更日程内部会议
				updateOffscheduling(meeInsideMeeting);
//				if (StringUtils.isNotEmpty(obj.getNewMember())){
//					meeInsideMeeting.setPartyMemebersId(obj.getNewMember());
//					meeInsideMeeting.setPartyMemeberNames(obj.getNewMemberName());
					// 提醒
//					if (0 != meeInsideMeeting.getIsWarnMail()) {
//						meeInsideMeetingService.mailWarn(meeInsideMeeting, createUser, MEET_ADD);
//					}
//					if (0 != meeInsideMeeting.getIsWarnMsg()) {
//						meeInsideMeetingService.smaWarn(meeInsideMeeting, createUser, MEET_ADD);
//					}
//					if (0 != meeInsideMeeting.getIsWarnOnline()) {
//						meeInsideMeetingService.onlineWarn(meeInsideMeeting, createUser, MEET_ADD);
//					}
//				}
//				if (StringUtils.isNotEmpty(obj.getDelMember())){
//					meeInsideMeeting.setPartyMemebersId(obj.getDelMember());
//					meeInsideMeeting.setPartyMemeberNames(obj.getDelMember());
					// 提醒
//					if (0 != meeInsideMeeting.getIsWarnMail()) {
//						meeInsideMeetingService.mailWarn(meeInsideMeeting, createUser, MEET_CANCEL);
//					}
//					if (0 != meeInsideMeeting.getIsWarnMsg()) {
//						meeInsideMeetingService.smaWarn(meeInsideMeeting, createUser, MEET_CANCEL);
//					}
//					if (0 != meeInsideMeeting.getIsWarnOnline()) {
//						meeInsideMeetingService.onlineWarn(meeInsideMeeting, createUser, MEET_CANCEL);
//					}
//				}
				retMsg.setObject(meeInsideMeeting);
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
                        resResource.setFileDesc("内部会议编辑附件上传");
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
	 * 
	 * 新建内部会议
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	@Transactional
	public RetMsg add(MeeInsideMeetingVO obj, AutUser createUser) throws Exception {
		RetMsg retMsg = new RetMsg();
		MeeInsideMeeting meeInsideMeeting = new MeeInsideMeeting();
		
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
			if(null == obj.getStatus()) {
			  Where<MeeInsideMeeting> meewhere = new Where<MeeInsideMeeting>();
		        meewhere.eq("meeting_situation", 1);
		        meewhere.eq("meeting_room_id", obj.getMeetingRoomId());
		        meewhere.between("begin_time", obj.getBeginTime(), obj.getEndTime());
		        meewhere.orNew().between("end_time", obj.getBeginTime(), obj.getEndTime());
		        meewhere.eq("meeting_situation", 1);
		        meewhere.eq("meeting_room_id", obj.getMeetingRoomId());
		        meewhere.orNew().le("begin_time", obj.getBeginTime());
		        meewhere.and().ge("end_time", obj.getEndTime());
		        meewhere.eq("meeting_situation", 1);
		        meewhere.eq("meeting_room_id", obj.getMeetingRoomId());
//		        meewhere.setSqlSelect("meeting_room_id","party_memebers_id","begin_time","end_time","meeting_content","meeting_situation");
		        int mee = meeInsideMeetingService.selectCount(meewhere);
			  if (mee != 0) {
			    retMsg.setCode(2);
			    retMsg.setMessage("会议室被占用，是否继续申请？");
			    return retMsg;
			  }
			}
			if (StringUtils.isEmpty(obj.getPartyMemeberNames())) {
				obj.setPartyMemeberNames("");
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
			
			Long objId = obj.getId();
			obj.setId(null);
			BeanUtils.copyProperties(obj, meeInsideMeeting);
			retMsg.setObject(meeInsideMeeting);
			meeInsideMeetingService.insert(meeInsideMeeting);
			// 提醒
//			if (0 != meeInsideMeeting.getIsWarnOnline()) {
//				meeInsideMeetingService.onlineWarn(meeInsideMeeting, createUser, MEET_ADD);
//			}
//			if (0 != meeInsideMeeting.getIsWarnMsg()) {
//				meeInsideMeetingService.smaWarn(meeInsideMeeting, createUser, MEET_ADD);
//			}
//			if (0 != meeInsideMeeting.getIsWarnMail()) {
//				meeInsideMeetingService.mailWarn(meeInsideMeeting, createUser, MEET_ADD);
//			}
			// 删除草稿箱记录
			if (null != objId) {
				meeInsideMeetingDraftService.deleteById(objId);
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
			// 新增日程内部会议
			addOffscheduling(meeInsideMeeting);
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
                    resResource.setFileDesc("新增内部会议附件上传");
                }
                resResourceService.updateBatchById(resResourceList);
            }
		}
		
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 取消会议
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：sln
	 *
	 * @date：2017-11-21
	 */
	@Transactional
	public RetMsg cancel(MeeInsideMeetingVO obj, AutUser user) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			MeeInsideMeeting meeInsideMeeting = meeInsideMeetingService.selectById(obj.getId());
			if (meeInsideMeeting != null && meeInsideMeeting.getMeetingSituation() != 1){
				if (meeInsideMeeting.getMeetingSituation() == 2){
					retMsg.setMessage("会议已经完成，不可取消");
				}else{
					retMsg.setMessage("会议已被其他管理员取消，请勿重复操作");
				}
				retMsg.setCode(1);
				return retMsg;
			}
			if (meeInsideMeeting.getBeginTime().before(new Date())
					|| meeInsideMeeting.getBeginTime().equals(new Date())) {
				retMsg.setCode(1);
				retMsg.setMessage("会议已经开始，不可取消");
				return retMsg;
			}
			if (null != meeInsideMeeting) {
				meeInsideMeeting.setMeetingSituation(3);
				meeInsideMeetingService.updateById(meeInsideMeeting);
			}
			// 删除对应日程
			offSchedulingService.deleteOffSchedule(meeInsideMeeting.getId());
			retMsg.setObject(meeInsideMeeting);
			/*if (null != obj.getIsWarnMail()) {
				meeInsideMeetingService.mailWarn(meeInsideMeeting, user, MEET_CANCEL);
			}
			if (null != meeInsideMeeting.getIsWarnMsg()) {
				meeInsideMeetingService.smaWarn(meeInsideMeeting, user, MEET_CANCEL);
			}
			if (null != obj.getIsWarnOnline()) {
				meeInsideMeetingService.onlineWarn(meeInsideMeeting, user, MEET_CANCEL);
			}*/
			
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功"); 
		return retMsg;
	}

}
