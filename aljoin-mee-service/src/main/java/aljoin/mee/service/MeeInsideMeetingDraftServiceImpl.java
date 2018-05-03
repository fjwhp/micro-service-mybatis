package aljoin.mee.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.entity.MeeInsideMeetingDraft;
import aljoin.mee.dao.mapper.MeeInsideMeetingDraftMapper;
import aljoin.mee.dao.object.MeeInsideMeetingDraftDO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.iservice.MeeInsideMeetingDraftService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;

/**
 * 
 * 内部会议草稿表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Service
public class MeeInsideMeetingDraftServiceImpl extends ServiceImpl<MeeInsideMeetingDraftMapper, MeeInsideMeetingDraft> implements MeeInsideMeetingDraftService {

  @Resource
  private MeeInsideMeetingDraftMapper mapper;

    @Resource
    private ResResourceService resResourceService;
    @Resource
    private AutUserService autUserService;

    //private static  final String PATTERN_BAR_YYYYMMDD = "yyyy-MM-dd";
    //private static  final String PATTERN_SLASH_YYYYMMDD = "yyyy/MM/dd";
    //private static  final String PATTERN_BAR_YYYYMM = "yyyy-MM";
    private static  final String PATTERN_BAR_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";

  @Override
  public Page<MeeInsideMeetingDraftDO> list(PageBean pageBean, MeeInsideMeetingVO obj) throws Exception {
	Where<MeeInsideMeetingDraft> where = new Where<MeeInsideMeetingDraft>();
      if(null != obj){
          if(StringUtils.isNotEmpty(obj.getMeetingTitle())){
              where.like("meeting_title",obj.getMeetingTitle());
          }
          if(StringUtils.isNotEmpty(obj.getMeetingHost())){
              where.like("meeting_host",obj.getMeetingHost());
          }
          if(null != obj.getMeetingRoomId()){
              where.eq("meeting_room_id",obj.getMeetingRoomId());
          }
          if(null != obj.getMeetingSituation()){
              where.eq("meeting_situation",obj.getMeetingSituation());
          }
          if(StringUtils.isNotEmpty(obj.getSearchKey())){
              where.andNew("meeting_title like {0} or meeting_host like {1}","%"+obj.getSearchKey()+"%","%"+obj.getSearchKey()+"%");
          }
      }
      where.andNew();
    where.eq("create_user_id", obj.getCreateUserId());
	where.orderBy("create_time", false);
	Page<MeeInsideMeetingDraftDO> page = new Page<MeeInsideMeetingDraftDO>();
	Page<MeeInsideMeetingDraft> oldPage = selectPage(new Page<MeeInsideMeetingDraft>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    List<MeeInsideMeetingDraft> draftList = oldPage.getRecords();
    List<MeeInsideMeetingDraftDO> meetingDraftDOList = new ArrayList<MeeInsideMeetingDraftDO>();
      List<Long> userIdList = new ArrayList<Long>();
    if(null != draftList && !draftList.isEmpty()){
        int i = 1;
        for (MeeInsideMeetingDraft meetingDraft : draftList){
            MeeInsideMeetingDraftDO meetingDraftDO = new MeeInsideMeetingDraftDO();
            meetingDraftDO.setNo(i);
            meetingDraftDO.setId(meetingDraft.getId());
            meetingDraftDO.setMeetingTitle(meetingDraft.getMeetingTitle());
            meetingDraftDO.setMeetingRoomName(meetingDraft.getMeetingRoomName());
            DateTime begTime = new DateTime(meetingDraft.getBeginTime());
            meetingDraftDO.setBeginDate(begTime.toString(PATTERN_BAR_YYYYMMDDHHMM));
            DateTime endTime = new DateTime(meetingDraft.getEndTime());
            meetingDraftDO.setEndDate(endTime.toString(PATTERN_BAR_YYYYMMDDHHMM));
            Integer  meetingSituation = meetingDraft.getMeetingSituation();
            String situation = "";
            if(1==meetingSituation){
                situation = "未完成";
            }else if(2 == meetingSituation){
                situation = "已完成";
            }else if(3 == meetingSituation){
                situation = "已取消";
            }
            meetingDraftDO.setMeetingSituation(situation);
            meetingDraftDO.setContacts(meetingDraft.getContacts());
            meetingDraftDO.setCreateUserId(meetingDraft.getCreateUserId());
            meetingDraftDO.setMeetingHost(meetingDraft.getMeetingHost());
            userIdList.add(meetingDraft.getCreateUserId());
            meetingDraftDOList.add(meetingDraftDO);
            i++;
        }
    }
    if(null != meetingDraftDOList && !meetingDraftDOList.isEmpty()){
        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.eq("is_active",1);
        userWhere.in("id",userIdList);
        List<AutUser> userList = autUserService.selectList(userWhere);
        if(null != userList && !userList.isEmpty()){
            for(MeeInsideMeetingDraftDO meetingDraftDO : meetingDraftDOList){
                for(AutUser user : userList){
                    if(null != user && null != meetingDraftDO){
                        if(StringUtils.isNotEmpty(user.getFullName())){
                            if((null != user.getId() && null != meetingDraftDO.getCreateUserId())){
                                if(user.getId().equals(meetingDraftDO.getCreateUserId()) && user.getId().intValue() == meetingDraftDO.getCreateUserId().intValue()){
                                    meetingDraftDO.setCreateFullName(user.getFullName());
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    page.setRecords(meetingDraftDOList);
    page.setSize(oldPage.getSize());
    page.setTotal(oldPage.getTotal());
      return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(MeeInsideMeetingDraft obj) throws Exception{
  	mapper.copyObject(obj);
  }

    @Override
    @Transactional
    public RetMsg add(MeeInsideMeetingVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj){
            if(StringUtils.isEmpty(obj.getMeetingTitle())){
                obj.setMeetingTitle("");
                retMsg.setCode(1);
                retMsg.setMessage("会议标题不能为空");
                return retMsg;
            }
            if(StringUtils.isEmpty(obj.getMeetingHost())){
                obj.setMeetingHost("");
                retMsg.setCode(1);
                retMsg.setMessage("主持人不能为空");
                return retMsg;
            }
            if(StringUtils.isEmpty(obj.getContacts())){
                obj.setContacts("");
                retMsg.setCode(1);
                retMsg.setMessage("联系人不能为空");
                return retMsg;
            }
            if(StringUtils.isEmpty(obj.getAddress())){
                obj.setAddress("");
                retMsg.setCode(1);
                retMsg.setMessage("填写地址不能为空");
                return retMsg;
            }
            if(null == obj.getIsWarnMail()){
                obj.setIsWarnMail(0);
            }
            if(null == obj.getIsWarnMsg()){
                obj.setIsWarnMsg(0);
            }
            if(null == obj.getIsWarnOnline()){
                obj.setIsWarnOnline(0);
            }
            Date date  = new Date();
            if(null == obj.getBeginTime()){
                obj.setBeginTime(date);
                retMsg.setCode(1);
                retMsg.setMessage("会议开始时间不能为空");
                return retMsg;
            }
            if(null == obj.getEndTime()){
                obj.setEndTime(date);
                retMsg.setCode(1);
                retMsg.setMessage("会议结束时间不能为空");
                return retMsg;
            }
			if (obj.getBeginTime().before(date)){
				retMsg.setCode(1);
				retMsg.setMessage("会议时间不能小于当天");
				return retMsg;
			}
			if (obj.getBeginTime().after(obj.getEndTime())){
				retMsg.setCode(1);
				retMsg.setMessage("会议开始时间不能小于结束时间");
				return retMsg;
			}
            if(StringUtils.isEmpty(obj.getMeetingContent())){
                obj.setMeetingContent("");
                retMsg.setCode(1);
                retMsg.setMessage("会议内容不能为空");
                return retMsg;
            }
            if(StringUtils.isEmpty(obj.getMeetingContent())){
                obj.setMeetingContent("");
                retMsg.setCode(1);
                retMsg.setMessage("会议内容不能为空");
                return retMsg;
            }
            if(null == obj.getMeetingSituation()){
                obj.setMeetingSituation(1);
            }
            if(StringUtils.isEmpty(obj.getAttendances())){
                obj.setAttendances("");
            }
            MeeInsideMeetingDraft meeInsideMeeting = new MeeInsideMeetingDraft();
            BeanUtils.copyProperties(obj,meeInsideMeeting);
            this.insert(meeInsideMeeting);

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
                    resResource.setBizId(meeInsideMeeting.getId());
                    resResource.setFileDesc("内部会议草稿箱编辑附件上传");
                }
                resResourceService.updateBatchById(resResourceList);
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public MeeInsideMeetingVO detail(MeeInsideMeeting obj) throws Exception {
        MeeInsideMeetingVO meeInsideMeetingVO = null;
        if(null != obj && null != obj.getId()){
            MeeInsideMeetingDraft meeInsideMeeting = selectById(obj.getId());
            if(null != meeInsideMeeting){
                meeInsideMeetingVO = new MeeInsideMeetingVO();
                BeanUtils.copyProperties(meeInsideMeeting,meeInsideMeetingVO);

                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id",meeInsideMeeting.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                if(null != resourceList && !resourceList.isEmpty()){
                    meeInsideMeetingVO.setResResourceList(resourceList);
                }
            }
        }
        return meeInsideMeetingVO;
    }

    @Override
    @Transactional
    public RetMsg update(MeeInsideMeetingVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj && null != obj.getId()){
            MeeInsideMeetingDraft meeInsideMeeting = selectById(obj.getId());
            if(null != meeInsideMeeting){
                if(StringUtils.isNotEmpty(obj.getMeetingTitle())){
                    meeInsideMeeting.setMeetingTitle(obj.getMeetingTitle());
                }
                if(StringUtils.isNotEmpty(obj.getMeetingHost())){
                    meeInsideMeeting.setMeetingHost(obj.getMeetingHost());
                }
                if(StringUtils.isNotEmpty(obj.getContacts())){
                    meeInsideMeeting.setContacts(obj.getContacts());
                }
                if(StringUtils.isNotEmpty(obj.getAddress())){
                    meeInsideMeeting.setAddress(obj.getAddress());
                }
                if(StringUtils.isNotEmpty(obj.getPartyMemebersId())){
                    meeInsideMeeting.setPartyMemebersId(obj.getPartyMemebersId());
                }
                if(StringUtils.isNotEmpty(obj.getPartyMemeberNames())){
                    meeInsideMeeting.setPartyMemeberNames(obj.getPartyMemeberNames());
                }
                if(StringUtils.isNotEmpty(obj.getMeetingRoomName())){
                  meeInsideMeeting.setMeetingRoomName(obj.getMeetingRoomName());
                }
                if(null != obj.getMeetingRoomId()){
                  meeInsideMeeting.setMeetingRoomId(obj.getMeetingRoomId());
                }
                if(null != obj.getIsWarnMail()){
                    meeInsideMeeting.setIsWarnMail(obj.getIsWarnMail());
                }
                if(null != obj.getIsWarnMsg()){
                    meeInsideMeeting.setIsWarnMsg(obj.getIsWarnMsg());
                }
                if(null != obj.getIsWarnOnline()){
                    meeInsideMeeting.setIsWarnOnline(obj.getIsWarnOnline());
                }
                if(null != obj.getBeginTime()){
                    meeInsideMeeting.setBeginTime(obj.getBeginTime());
                }
                if(null != obj.getEndTime()){
                    meeInsideMeeting.setEndTime(obj.getEndTime());
                }
                if(StringUtils.isNotEmpty(obj.getMeetingContent())){
                    meeInsideMeeting.setMeetingContent(obj.getMeetingContent());
                }
                if(StringUtils.isNotEmpty(obj.getMeetingRoomName())){
                    meeInsideMeeting.setMeetingRoomName(obj.getMeetingRoomName());
                }
                if(obj.getMeetingRoomId()!=null){
                    meeInsideMeeting.setMeetingRoomId(obj.getMeetingRoomId());
                }
                if(StringUtils.isNotEmpty(obj.getAttendances())){
                    meeInsideMeeting.setAttendances(obj.getAttendances());
                }
                updateById(meeInsideMeeting);
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
                        resResource.setFileDesc("内部会议草稿箱编辑附件上传");
                    }
                    resResourceService.updateBatchById(addResource);
                }
                /*//会议草稿上传附件
                List<ResResource> newResourceList = obj.getResResourceList();
                List<Long> newResourceIds = new ArrayList<Long>();
                for (ResResource resResource : newResourceList) {
                    newResourceIds.add(resResource.getId());
                }
                //会议原有附件
                Where<ResResource> resourceWhere = new Where<ResResource>();
                resourceWhere.eq("biz_id", meeInsideMeeting.getId());
                List<ResResource> oldResourceList = resResourceService.selectList(resourceWhere);
                List<Long> oldResourceIds = new ArrayList<Long>();
                for (ResResource resResource : oldResourceList) {
                    oldResourceIds.add(resResource.getId());
                }
                //去除编辑时删掉的附件
                List<Long> delResourceIdList = new ArrayList<Long>();
                List<Long> resourceIdList = new ArrayList<Long>();
                for (Long oldId : oldResourceIds) {
                    boolean flag = true;
                    for (Long newId : newResourceIds) {
                        if (oldId.equals(newId)) {
                            flag = false;
                            resourceIdList.add(oldId);
                            break;
                        }
                    }
                    if (flag) {
                        delResourceIdList.add(oldId);
                    }
                }
                //获得新增附件
                List<Long> addResourceIdList = new ArrayList<Long>();
                for (Long newId : newResourceIds) {
                    if (!resourceIdList.contains(newId)) {
                        addResourceIdList.add(newId);
                    }
                }
                if (null != addResourceIdList && addResourceIdList.size() > 0) {
                    List<ResResource> addResource = resResourceService.selectBatchIds(addResourceIdList);
                    for (ResResource resResource : addResource) {
                        resResource.setBizId(meeInsideMeeting.getId());
                        resResource.setFileDesc("内部会议草稿箱编辑附件上传");
                    }
                    resResourceService.updateBatchById(addResource);
                }
                if (null != delResourceIdList && delResourceIdList.size() > 0) {
                    resResourceService.deleteBatchById(delResourceIdList);
                }*/
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(MeeInsideMeetingVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj && null != obj.getIds()){
            String ids = obj.getIds();
            String idArr[] = null;
            if(ids.indexOf(";")>-1){
                if(ids.endsWith(";")){
                    idArr = ids.substring(0,ids.lastIndexOf(";")).split(";");
                }else{
                    idArr = ids.split(";");
                }
            }else{
                idArr = new String[1];
                idArr[0] = obj.getIds();
            }
            if(null != idArr){
                List<Long> idList = new ArrayList<Long>();
                Where<MeeInsideMeetingDraft> pubWhere = new Where<MeeInsideMeetingDraft>();
                pubWhere.in("id",idArr);
                List<MeeInsideMeetingDraft> pubPublicInfoList = selectList(pubWhere);
                if(null != pubPublicInfoList && !pubPublicInfoList.isEmpty()){
                    for(MeeInsideMeetingDraft pubPublicInfo : pubPublicInfoList){
                        if(null != pubPublicInfo){
                            idList.add(pubPublicInfo.getId());
                        }
                    }
                }
                if(null != idList && !idList.isEmpty()){
                    deleteBatchIds(idList);
                }
                //删除附件(暂时不删除)
                /*Where<ResResource> where = new Where<ResResource>();
                where.in("biz_id",idList);
                List<ResResource> resourcesList = resResourceService.selectList(where);
                List<Long> resourcesIds = new ArrayList<Long>();
                for (ResResource resResource : resourcesList) {
                    resourcesIds.add(resResource.getId());
                }
                if(null != resourcesList && !resourcesList.isEmpty()){
                    resResourceService.deleteBatchById(resourcesIds);
                }*/
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
