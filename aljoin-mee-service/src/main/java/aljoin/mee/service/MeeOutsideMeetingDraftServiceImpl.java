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
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.dao.entity.MeeOutsideMeetingDraft;
import aljoin.mee.dao.mapper.MeeOutsideMeetingDraftMapper;
import aljoin.mee.dao.object.MeeOutsideMeetingDraftDO;
import aljoin.mee.dao.object.MeeOutsideMeetingDraftVO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeOutsideMeetingDraftService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.util.DateUtil;

/**
 * 
 * 外部会议草稿表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Service
public class MeeOutsideMeetingDraftServiceImpl extends ServiceImpl<MeeOutsideMeetingDraftMapper, MeeOutsideMeetingDraft> implements MeeOutsideMeetingDraftService {

  @Resource
  private MeeOutsideMeetingDraftMapper mapper;
  @Resource
  private ResResourceService resResourceService;
  @Resource
  private AutUserService autUserService;

//  private static  final String PATTERN_BAR_YYYYMMDD = "yyyy-MM-dd";
//  private static  final String PATTERN_SLASH_YYYYMMDD = "yyyy/MM/dd";
//  private static  final String PATTERN_BAR_YYYYMM = "yyyy-MM";
  private static  final String PATTERN_BAR_YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";

  @Override
  public Page<MeeOutsideMeetingDraftDO> list(PageBean pageBean, MeeOutsideMeetingDraftVO obj) throws Exception {
	Where<MeeOutsideMeetingDraft> where = new Where<MeeOutsideMeetingDraft>();
      if(null != obj){
    	  if(StringUtils.isNotEmpty(obj.getMeetingTitle())){
              where.like("meeting_title",obj.getMeetingTitle());
              where.or().like("address","%" + obj.getMeetingTitle() + "%");
          }
          if(StringUtils.isNotEmpty(obj.getAddress())){
              where.andNew();
              where.like("address",obj.getAddress());
          }
          if(StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())){
            where.andNew();
//              where.ge("create_time",obj.getStartDate());
        	  where.ge("begin_time", DateUtil.str2dateOrTime(obj.getStartDate()));
          }
          if(StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())){
            where.andNew();
//              where.le("create_time",obj.getEndDate());
              where.le("end_time", DateUtil.str2dateOrTime(obj.getEndDate()));
          }
          if(StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())){
//              where.between("create_time",obj.getStartDate(),obj.getEndDate());
        	  where.andNew();
        	  where.between("begin_time", DateUtil.str2dateOrTime(obj.getStartDate()), DateUtil.str2dateOrTime(obj.getEndDate()));
        	  where.or("end_time between {0} and {1}", DateUtil.str2dateOrTime(obj.getStartDate()),DateUtil.str2dateOrTime(obj.getEndDate()));
          }
      }
      where.andNew();
    where.eq("create_user_id", obj.getCreateUserId());
	where.orderBy("create_time", false);
	where.setSqlSelect("id,meeting_title,meeting_host,address,begin_time,end_time,create_time,create_user_id");
	Page<MeeOutsideMeetingDraft> oldPage = selectPage(new Page<MeeOutsideMeetingDraft>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	Page<MeeOutsideMeetingDraftDO> page = new Page<MeeOutsideMeetingDraftDO>();
      List<MeeOutsideMeetingDraft> meetingDraftList = oldPage.getRecords();
      List<MeeOutsideMeetingDraftDO>  meetingDraftDOList= new ArrayList<MeeOutsideMeetingDraftDO>();
      List<Long> userIdList = new ArrayList<Long>();
      if(null != meetingDraftList && !meetingDraftList.isEmpty()){
          for(MeeOutsideMeetingDraft meetingDraft : meetingDraftList){
              MeeOutsideMeetingDraftDO meeOutsideMeetingDO = new MeeOutsideMeetingDraftDO();
              meeOutsideMeetingDO.setId(meetingDraft.getId());
              meeOutsideMeetingDO.setMeetingTitle(meetingDraft.getMeetingTitle());
              DateTime begTime = new DateTime(meetingDraft.getBeginTime());
              meeOutsideMeetingDO.setBeginDate(begTime.toString(PATTERN_BAR_YYYYMMDDHHMM));
              DateTime endTime = new DateTime(meetingDraft.getEndTime());
              meeOutsideMeetingDO.setEndDate(endTime.toString(PATTERN_BAR_YYYYMMDDHHMM));
              Integer  meetingSituation = meetingDraft.getMeetingSituation();
              String situation = "";
              if(null != meetingSituation){
            	  if(1==meetingSituation){
                      situation = "未完成";
                  }else if(2 == meetingSituation){
                      situation = "已完成";
                  }else if(3 == meetingSituation){
                      situation = "已取消";
                  }
              }

              meeOutsideMeetingDO.setMeetingSituation(situation);
              meeOutsideMeetingDO.setCreateUserId(meetingDraft.getCreateUserId());
              meeOutsideMeetingDO.setAddress(meetingDraft.getAddress());
              meetingDraftDOList.add(meeOutsideMeetingDO);
              if(!userIdList.contains(meetingDraft.getCreateUserId())){
            	  userIdList.add(meetingDraft.getCreateUserId());
              }
          }
      }
      if(null != meetingDraftDOList && !meetingDraftDOList.isEmpty()){
          Where<AutUser> userWhere = new Where<AutUser>();
          userWhere.eq("is_active",1);
          if(userIdList.size()>0){
        	  userWhere.in("id",userIdList);
          }else if(!userIdList.isEmpty()){
        	  userWhere.eq("id",userIdList.get(0));  
          }
          List<AutUser> userList = autUserService.selectList(userWhere);
          if(null != userList && !userList.isEmpty()){
              for(MeeOutsideMeetingDraftDO meetingDraftDO : meetingDraftDOList){
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
      page.setTotal(oldPage.getTotal());
      page.setSize(oldPage.getSize());
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(MeeOutsideMeetingDraft obj) throws Exception{
  	mapper.copyObject(obj);
  }

    @Override
    @Transactional
    public RetMsg add(MeeOutsideMeetingVO obj) throws Exception {
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
            if(StringUtils.isEmpty(obj.getPartyMemebersId())){
                obj.setPartyMemebersId("");
            }
            if(StringUtils.isEmpty(obj.getPartyMemeberNames())){
                obj.setPartyMemeberNames("");
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
//            Date date  = new Date();
            if(null == obj.getBeginTime()){
//                obj.setBeginTime(date);
                retMsg.setCode(1);
                retMsg.setMessage("会议开始时间不能为空");
                return retMsg;
            }
            if(null == obj.getEndTime()){
//                obj.setEndTime(date);
                retMsg.setCode(1);
                retMsg.setMessage("会议结束时间不能为空");
                return retMsg;
            }
            if(StringUtils.isEmpty(obj.getMeetingContent())){
                obj.setMeetingContent("");
                retMsg.setCode(1);
                retMsg.setMessage("会议内容不能为空");
                return retMsg;
            }
            Date date = new Date();
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
            if(null == obj.getMeetingSituation()){
                obj.setMeetingSituation(1);
            }
            if(StringUtils.isEmpty(obj.getAttendances())){
                obj.setAttendances("");
            }
            MeeOutsideMeetingDraft meeOutsideMeeting = new MeeOutsideMeetingDraft();
            BeanUtils.copyProperties(obj,meeOutsideMeeting);
            insert(meeOutsideMeeting);
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
                    resResource.setBizId(meeOutsideMeeting.getId());
                    resResource.setFileDesc("外部会议草稿箱编辑附件上传");
                }
                resResourceService.updateBatchById(resResourceList);
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public MeeOutsideMeetingVO detail(MeeOutsideMeeting obj) throws Exception {
        MeeOutsideMeetingVO meeOutsideMeetingVO = null;
        if(null != obj && null != obj.getId()){
            MeeOutsideMeetingDraft  meeOutsideMeeting = selectById(obj.getId());
            if(null != meeOutsideMeeting){
                meeOutsideMeetingVO = new MeeOutsideMeetingVO();
                BeanUtils.copyProperties(meeOutsideMeeting,meeOutsideMeetingVO);

                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id",meeOutsideMeeting.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                if(null != resourceList && !resourceList.isEmpty()){
                    meeOutsideMeetingVO.setResResourceList(resourceList);
                }
            }
        }
        return meeOutsideMeetingVO;
    }

    @Override
    public RetMsg update(MeeOutsideMeetingVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if(null != obj && null != obj.getId()){
            MeeOutsideMeetingDraft meeInsideMeeting = selectById(obj.getId());
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
                        resResource.setFileDesc("外部会议草稿箱编辑附件上传");
                    }
                    resResourceService.updateBatchById(addResource);
                }
                /*List<ResAttachment> attachmentList = obj.getAttachmentList();
                List<ResAttachment> addAttachmentList = new ArrayList<ResAttachment>();
                List<Long> delAttachmentIdList = new ArrayList<Long>();
                if(null != attachmentList && !attachmentList.isEmpty()){
                    for(ResAttachment resAttachment : attachmentList){
                        if(null != resAttachment){
                            if(null != resAttachment.getId()){
                                delAttachmentIdList.add(resAttachment.getId());
                            }else{
                            	resAttachment.setResourceId(meeInsideMeeting.getId());
                                resAttachment.setType("mee");
                                resAttachment.setDescription("外部会议草稿箱编辑附件上传");
                                addAttachmentList.add(resAttachment);
                            }
                        }
                    }
                    if(null != delAttachmentIdList && !delAttachmentIdList.isEmpty()){
                        resAttachmentService.deleteBatchIds(delAttachmentIdList);
                    }
                    if(null != addAttachmentList && !addAttachmentList.isEmpty()){
                        resAttachmentService.insertBatch(addAttachmentList);
                    }
                }*/
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(MeeOutsideMeetingVO obj) throws Exception {
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
                Where<MeeOutsideMeetingDraft> pubWhere = new Where<MeeOutsideMeetingDraft>();
                pubWhere.in("id",idArr);
                List<MeeOutsideMeetingDraft> pubPublicInfoList = selectList(pubWhere);
                if(null != pubPublicInfoList && !pubPublicInfoList.isEmpty()){
                    for(MeeOutsideMeetingDraft pubPublicInfo : pubPublicInfoList){
                        if(null != pubPublicInfo){
                            idList.add(pubPublicInfo.getId());
                        }
                    }
                }
                if(null != idList && !idList.isEmpty()){
                    deleteBatchIds(idList);
                }
                
                Where<ResResource> where = new Where<ResResource>();
                where.in("biz_id",idList);
                List<ResResource> resourcesList = resResourceService.selectList(where);
                List<Long> resourcesIds = new ArrayList<Long>();
                for (ResResource resResource : resourcesList) {
                    resourcesIds.add(resResource.getId());
                }
                if(null != resourcesList && !resourcesList.isEmpty()){
                    resResourceService.deleteBatchById(resourcesIds);
                }
                //删除附件(暂时不删除)
                /*Where<ResAttachment> where = new Where<ResAttachment>();
                where.in("resource_id",idList);
                List<ResAttachment> attachmentList = resAttachmentService.selectList(where);
                if(null != attachmentList && !attachmentList.isEmpty()){
                    List<ResAttachment> addAttachmentList = new ArrayList<ResAttachment>();
                    List<Long> delAttachmentIdList = new ArrayList<Long>();
                    if(null != attachmentList && !attachmentList.isEmpty()){
                        for(ResAttachment resAttachment : attachmentList){
                            if(null != resAttachment){
                                if(null != resAttachment.getId()){
                                    delAttachmentIdList.add(resAttachment.getId());
                                }else{
                                    addAttachmentList.add(resAttachment);
                                }
                            }
                        }
                        if(null != delAttachmentIdList && !delAttachmentIdList.isEmpty()){
                            resAttachmentService.deleteBatchIds(delAttachmentIdList);
                        }
                        if(null != addAttachmentList && !addAttachmentList.isEmpty()){
                            resAttachmentService.insertBatch(addAttachmentList);
                        }
                    }
                }*/
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public MeeOutsideMeetingVO detail(MeeOutsideMeetingVO obj) throws Exception {
        MeeOutsideMeetingVO meeOutsideMeetingVO = null;
        if(null != obj && null != obj.getId()){
            MeeOutsideMeetingDraft meeOutsideMeeting = selectById(obj.getId());
            if(null != meeOutsideMeeting){
                meeOutsideMeetingVO = new MeeOutsideMeetingVO();
                BeanUtils.copyProperties(meeOutsideMeeting,meeOutsideMeetingVO);

                Where<ResResource> where = new Where<ResResource>();
                where.eq("biz_id",meeOutsideMeeting.getId());
                List<ResResource> resourceList = resResourceService.selectList(where);
                if(null != resourceList && !resourceList.isEmpty()){
                    meeOutsideMeetingVO.setResResourceList(resourceList);
                }
            }
        }
        return meeOutsideMeetingVO;
    }
}
