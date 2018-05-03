package aljoin.mee.service;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.entity.MeeMeetingRoom;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.dao.mapper.MeeInsideMeetingMapper;
import aljoin.mee.dao.object.MeeInsideMeetingDO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeLeaderMeetingDO;
import aljoin.mee.dao.object.MeeLeaderMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingCountDO;
import aljoin.mee.dao.object.MeeMeetingCountVO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.mee.iservice.MeeMeetingRoomService;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.ExcelUtil;

/**
 * 
 * 内部会议表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Service
public class MeeInsideMeetingServiceImpl extends
    ServiceImpl<MeeInsideMeetingMapper, MeeInsideMeeting> implements MeeInsideMeetingService {

  @Resource
  private MeeInsideMeetingMapper mapper;
  @Resource
  private ResResourceService resResourceService;
  @Resource
  private AutUserService autUserService;
  @Resource
  private MeeMeetingRoomService meeMeetingRoomService;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;
  @Resource
  private AutDepartmentService autDepartmentService;
  @Resource
  private MeeOutsideMeetingService meeOutsideMeetingService;
  @Resource
  private SysParameterService sysParameterService;
  @Resource
  private AutUserPositionService autUserPositionService;
  @Resource
  private AutPositionService autPositionService;
  @Resource
  private SysMsgModuleInfoService sysMsgModuleInfoService;
  @Resource
  private AutMsgOnlineService autMsgOnlineService;
  @Resource
  private MaiSendBoxService maiSendBoxService;
  @Resource
  private SmsShortMessageService smsShortMessageService;
  /*
   * @Resource private MaiSendBoxWebService maiSendBoxWebService;
   */

  private static final String MEET_ADD = "add";
  private static final String MEET_UPDATE = "update";
  private static final String MEET_CANCEL = "cancel";
  private static final String MEET_NOTICE = "notice";

  @Override
  public Page<MeeInsideMeetingDO> list(PageBean pageBean, MeeInsideMeetingVO obj) throws Exception {
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    if (null != obj) {
      where
          .setSqlSelect("id,meeting_title,meeting_room_name,meeting_situation,meeting_host,meeting_room_id,begin_time,end_time,create_time,is_delete,contacts,create_user_id,create_user_name");
      where.eq("create_user_id", obj.getCreateUserId());
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew("meeting_title like {0} or meeting_host like {1}", "%" + obj.getSearchKey()
            + "%", "%" + obj.getSearchKey() + "%");
      }
      if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
        where.like("meeting_title", obj.getMeetingTitle());
      }
      if (StringUtils.isNotEmpty(obj.getMeetingHost())) {
        where.like("meeting_host", obj.getMeetingHost());
      }
      if (null != obj.getMeetingRoomId()) {
        where.eq("meeting_room_id", obj.getMeetingRoomId());
      }
      if (null != obj.getMeetingSituation()) {
        where.eq("meeting_situation", obj.getMeetingSituation());
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        String startTime = obj.getStartDate() + " 00:00:00";
        where.ge("begin_time", DateUtil.str2datetime(startTime));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        String endTime = obj.getEndDate() + " 23:59:59";
        where.le("begin_time", DateUtil.str2dateOrTime(endTime));
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        // where.between("create_time", obj.getStartDate(),
        // obj.getEndDate());
        String startTime = obj.getEndDate() + " 23:59:59";
        String endTime = obj.getStartDate() + " 00:00:00";
        where.andNew();
        where.between("begin_time", DateUtil.str2dateOrTime(endTime), DateUtil.str2dateOrTime(startTime));
      }

    }

    where.orderBy("begin_time", false);
    Page<MeeInsideMeetingDO> page = new Page<MeeInsideMeetingDO>();
    Page<MeeInsideMeeting> oldPage =
        selectPage(new Page<MeeInsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    List<MeeInsideMeeting> meeInsideMeetingList = oldPage.getRecords();
    List<MeeInsideMeetingDO> meeInsideMeetingDOList = new ArrayList<MeeInsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
        MeeInsideMeetingDO meeInsideMeetingDO = new MeeInsideMeetingDO();
        meeInsideMeetingDO.setNo(i);
        meeInsideMeetingDO.setId(meeInsideMeeting.getId());
        meeInsideMeetingDO.setMeetingTitle(meeInsideMeeting.getMeetingTitle());
        meeInsideMeetingDO.setMeetingRoomName(meeInsideMeeting.getMeetingRoomName());
        DateTime begTime = new DateTime(meeInsideMeeting.getBeginTime());
        meeInsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeInsideMeeting.getEndTime());
        meeInsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeInsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        meeInsideMeetingDO.setMeetingSituation(situation);
        meeInsideMeetingDO.setContacts(meeInsideMeeting.getContacts());
        meeInsideMeetingDO.setCreateUserId(meeInsideMeeting.getCreateUserId());
        meeInsideMeetingDO.setMeetingHost(meeInsideMeeting.getMeetingHost());
        meeInsideMeetingDOList.add(meeInsideMeetingDO);
                if(!userIdList.contains(meeInsideMeeting.getCreateUserId())){
        userIdList.add(meeInsideMeeting.getCreateUserId());
                }
        i++;
      }
    }
    if (null != meeInsideMeetingDOList && !meeInsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,full_name");
      userWhere.eq("is_active", 1);
            if(userIdList.size()>1){
      userWhere.in("id", userIdList);
            }else if(!userIdList.isEmpty()){
                userWhere.eq("id", userIdList.get(0));
            }
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeInsideMeetingDO meeInsideMeetingDO : meeInsideMeetingDOList) {
          for (AutUser user : userList) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeInsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeInsideMeetingDO.getCreateUserId())
                      && user.getId().intValue() == meeInsideMeetingDO.getCreateUserId().intValue()) {
                    meeInsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    page.setRecords(meeInsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public void physicsDeleteById(Long id) throws Exception {
    mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(MeeInsideMeeting obj) throws Exception {
    mapper.copyObject(obj);
  }

  @Override
  public MeeInsideMeetingVO detail(MeeInsideMeeting obj) throws Exception {
    MeeInsideMeetingVO meeInsideMeetingVO = null;
    if (null != obj && null != obj.getId()) {
      MeeInsideMeeting meeInsideMeeting = selectById(obj.getId());
      if (null != meeInsideMeeting) {
        List<String> tmpList = new ArrayList<String>();
        String id = "";
        String[] ids = meeInsideMeeting.getPartyMemebersId().split(";");
        List<Long> receiveIds = new ArrayList<Long>();
        for (int i = 0; i < ids.length; i++) {
          receiveIds.add(Long.valueOf(ids[i]));
        }
        Where<AutUser> where1 = new Where<AutUser>();
        where1.in("id", ids);
        where1.setSqlSelect("id", "user_name", "full_name");
        List<AutUser> userList = autUserService.selectList(where1);
        Map<Long, Integer> rankList = autUserService.getUserRankList(receiveIds, null);
        if (rankList.size() > 0) {
          for (Long entry : rankList.keySet()) {
            String usrid = entry.toString();
            id += usrid + ";";
            for (AutUser autUser : userList) {
              String tmp = autUser.getId().toString();
              if (tmp.equals(usrid)) {
                tmpList.add(autUser.getFullName());
              }
            }
          }
        }
        String names = "";
        for (String name : tmpList) {
          names += name + ";";
        }
        meeInsideMeeting.setPartyMemeberNames(names);
        meeInsideMeeting.setPartyMemebersId(id);
        meeInsideMeetingVO = new MeeInsideMeetingVO();
        BeanUtils.copyProperties(meeInsideMeeting, meeInsideMeetingVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", meeInsideMeeting.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
         meeInsideMeetingVO.setResResourceList(resourceList);
        }
      }
    }
    return meeInsideMeetingVO;
  }

  @Override
  public Page<MeeInsideMeetingDO> personList(PageBean pageBean, MeeInsideMeetingVO obj)
      throws Exception {
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    // where.eq("create_user_id", obj.getCreateUserId());
    where.like("party_memebers_id", String.valueOf(obj.getCreateUserId()));
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        String searchKey = obj.getSearchKey();
        where.andNew();
        where.like("meeting_host", searchKey);
        where.or("meeting_title like {0}", "%" + searchKey + "%");
      }
      if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
        where.andNew();
        where.like("meeting_title", obj.getMeetingTitle());
      }
      if (StringUtils.isNotEmpty(obj.getMeetingHost())) {
        where.andNew();
        where.like("meeting_host", obj.getMeetingHost());
      }
      if (StringUtils.isNotEmpty(obj.getAddress())) {
        where.andNew();
        where.like("address", obj.getAddress());
      }
      if (null != obj.getMeetingRoomId()) {
        where.andNew();
        where.eq("meeting_room_id", obj.getMeetingRoomId());
      }
      if (null != obj.getMeetingSituation()) {
        where.andNew();
        where.eq("meeting_situation", obj.getMeetingSituation());
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        // where.ge("create_time", obj.getStartDate());
        where.andNew();
        where.ge("begin_time", DateUtil.str2datetime(obj.getStartDate() + " 00:00:00"));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        // where.le("create_time", obj.getEndDate());
        where.andNew();
        where.le("end_time", DateUtil.str2datetime(obj.getEndDate() + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        // where.between("create_time", obj.getStartDate(),
        // obj.getEndDate());
        where.andNew();
        where.between("begin_time", DateUtil.str2dateOrTime(obj.getStartDate() + " 00:00:00"), DateUtil.str2dateOrTime(obj.getEndDate() + " 23:59:59"));
        where.or("end_time between {0} and {1}", DateUtil.str2dateOrTime(obj.getStartDate() + " 00:00:00"), DateUtil.str2dateOrTime(obj.getEndDate() + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        // where.between("create_time",
        // DateUtil.getFirstDateOfWeek(PATTERN_BAR_YYYYMMDD),
        // DateUtil.getLastDateOfWeek(PATTERN_BAR_YYYYMMDD));
        where.andNew();
        where.between("begin_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 00:00:00"), DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        where.andNew();
        // where.between("create_time",
        // DateUtil.getFirstDateOfMonth(PATTERN_BAR_YYYYMMDD),
        // DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD));
        where.between("begin_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 00:00:00"), DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
        DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }

    }
    where.orderBy("begin_time", false);
    Page<MeeInsideMeetingDO> page = new Page<MeeInsideMeetingDO>();
    Page<MeeInsideMeeting> oldPage =
        selectPage(new Page<MeeInsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    List<MeeInsideMeeting> meeInsideMeetingList = oldPage.getRecords();
    List<MeeInsideMeetingDO> meeInsideMeetingDOList = new ArrayList<MeeInsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
        MeeInsideMeetingDO meeInsideMeetingDO = new MeeInsideMeetingDO();
        meeInsideMeetingDO.setNo(i);
        meeInsideMeetingDO.setId(meeInsideMeeting.getId());
        meeInsideMeetingDO.setMeetingTitle(meeInsideMeeting.getMeetingTitle());
        meeInsideMeetingDO.setMeetingRoomName(meeInsideMeeting.getMeetingRoomName());
        DateTime begTime = new DateTime(meeInsideMeeting.getBeginTime());
        meeInsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeInsideMeeting.getEndTime());
        meeInsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeInsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        meeInsideMeetingDO.setMeetingSituation(situation);
        meeInsideMeetingDO.setContacts(meeInsideMeeting.getContacts());
        meeInsideMeetingDO.setCreateUserId(meeInsideMeeting.getCreateUserId());
        meeInsideMeetingDO.setMeetingHost(meeInsideMeeting.getMeetingHost());
        meeInsideMeetingDOList.add(meeInsideMeetingDO);
                if(!userIdList.contains(meeInsideMeeting.getCreateUserId())){
        userIdList.add(meeInsideMeeting.getCreateUserId());
                }
        i++;
      }
    }
    if (null != meeInsideMeetingDOList && !meeInsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.setSqlSelect("id,user_name,full_name");
      userWhere.eq("is_active", 1);
            if(userIdList.size()>1){
      userWhere.in("id", userIdList);
            }else{
                userWhere.eq("id", userIdList.get(0));
            }
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeInsideMeetingDO meeInsideMeetingDO : meeInsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeInsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeInsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeInsideMeetingDO.getCreateUserId())
                      && user.getId().intValue() == meeInsideMeetingDO.getCreateUserId().intValue()) {
                    meeInsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeInsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public Page<MeeInsideMeetingDO> managerlist(PageBean pageBean, MeeInsideMeetingVO obj)
      throws Exception {
    Page<MeeInsideMeetingDO> page = new Page<MeeInsideMeetingDO>();
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
        where.like("meeting_title", obj.getMeetingTitle());
      }
      if (StringUtils.isNotEmpty(obj.getMeetingHost())) {
        where.like("meeting_host", obj.getMeetingHost());
      }
      if (StringUtils.isNotEmpty(obj.getAddress())) {
        where.like("address", obj.getAddress());
      }
      if (null != obj.getMeetingRoomId()) {
        where.eq("meeting_room_id", obj.getMeetingRoomId());
      } else {
        MeeMeetingRoom room = new MeeMeetingRoom();
        room.setCreateUserId(obj.getCreateUserId());
        List<MeeMeetingRoom> roomList = meeMeetingRoomService.chargeList(room);
        List<Long> roomIdList = new ArrayList<Long>();
        for (MeeMeetingRoom r : roomList) {
          roomIdList.add(r.getId());
        }
        if (roomIdList != null && !roomIdList.isEmpty()) {
          where.in("meeting_room_id", roomIdList);
        } else {
          return page;
        }
      }
      if (null != obj.getMeetingSituation()) {
        where.andNew();
        where.eq("meeting_situation", obj.getMeetingSituation());
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        where.andNew();
        where.ge("create_time", DateUtil.str2datetime(obj.getStartDate() + " 00:00:00"));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.andNew();
        where.le("create_time", DateUtil.str2datetime(obj.getEndDate() + " 23:59:59"));
      }
      /*
       * if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate()))
       * { where.between("create_time", obj.getStartDate() + " 00:00:00", obj.getEndDate()); }
       */
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.andNew();
        where.between("begin_time", DateUtil.str2dateOrTime(obj.getStartDate() + " 00:00:00"), DateUtil.str2dateOrTime(obj.getEndDate()
            + " 23:59:59"));
        where.or("end_time between {0} and {1}", DateUtil.str2dateOrTime(obj.getStartDate() + " 00:00:00"), DateUtil.str2dateOrTime(obj.getEndDate()
            + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew();
        String searchKey = obj.getSearchKey();
        where.like("meeting_host", searchKey);
        where.or("meeting_title like {0}", "%" + searchKey + "%");
      }
    }
    where.orderBy("create_time", false);

    Page<MeeInsideMeeting> oldPage =
        selectPage(new Page<MeeInsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    List<MeeInsideMeeting> meeInsideMeetingList = oldPage.getRecords();
    List<MeeInsideMeetingDO> meeInsideMeetingDOList = new ArrayList<MeeInsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
        MeeInsideMeetingDO meeInsideMeetingDO = new MeeInsideMeetingDO();
        meeInsideMeetingDO.setNo(i);
        meeInsideMeetingDO.setId(meeInsideMeeting.getId());
        meeInsideMeetingDO.setMeetingTitle(meeInsideMeeting.getMeetingTitle());
        meeInsideMeetingDO.setMeetingRoomName(meeInsideMeeting.getMeetingRoomName());
        DateTime begTime = new DateTime(meeInsideMeeting.getBeginTime());
        meeInsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeInsideMeeting.getEndTime());
        meeInsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeInsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        meeInsideMeetingDO.setMeetingSituation(situation);
        meeInsideMeetingDO.setContacts(meeInsideMeeting.getContacts());
        meeInsideMeetingDO.setCreateUserId(meeInsideMeeting.getCreateUserId());
        meeInsideMeetingDO.setMeetingHost(meeInsideMeeting.getMeetingHost());
        meeInsideMeetingDOList.add(meeInsideMeetingDO);
                if(!userIdList.contains(meeInsideMeeting.getCreateUserId())){
        userIdList.add(meeInsideMeeting.getCreateUserId());
                }
        i++;
      }
    }
    if (null != meeInsideMeetingDOList && !meeInsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("is_active", 1);
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeInsideMeetingDO meeInsideMeetingDO : meeInsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeInsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeInsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeInsideMeetingDO.getCreateUserId())
                      && user.getId().intValue() == meeInsideMeetingDO.getCreateUserId().intValue()) {
                    meeInsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeInsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public RetMsg cancel(MeeInsideMeetingVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      MeeInsideMeeting meeInsideMeeting = selectById(obj.getId());
      if (null != meeInsideMeeting) {
        meeInsideMeeting.setMeetingSituation(3);
        updateById(meeInsideMeeting);
      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public void autoComplete() throws Exception {
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    where.eq("meeting_situation", 1);
    DateTime dateTime = new DateTime(new Date());
    where.andNew().le("end_time", DateUtil.str2datetime(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM) + ":00"));
    where
        .setSqlSelect("id,version,meeting_situation,meeting_room_id,begin_time,end_time,create_time,is_delete,create_user_id,create_user_name");
    List<MeeInsideMeeting> insideMeetingList = selectList(where);
    List<Long> meetingRoomIdList = new ArrayList<Long>();
    if (null != insideMeetingList && !insideMeetingList.isEmpty()) {
      for (MeeInsideMeeting insideMeeting : insideMeetingList) {
        if (null != insideMeeting) {
          if (null != insideMeeting.getMeetingRoomId()) {
            meetingRoomIdList.add(insideMeeting.getMeetingRoomId());
          }
          insideMeeting.setMeetingSituation(2);
        }
      }
      updateBatchById(insideMeetingList);
    }
    if (null != meetingRoomIdList && !meetingRoomIdList.isEmpty()) {
      Where<MeeMeetingRoom> roomWhere = new Where<MeeMeetingRoom>();
      roomWhere.in("id", meetingRoomIdList);
      roomWhere
          .setSqlSelect("id,version,use_count,create_time,is_delete,create_user_id,create_user_name");
      List<MeeMeetingRoom> meeMeetingRoomList = meeMeetingRoomService.selectList(roomWhere);
      if (null != meeMeetingRoomList && !meeMeetingRoomList.isEmpty()) {
        for (MeeMeetingRoom room : meeMeetingRoomList) {
          if (null != room) {
            room.setUseCount(room.getUseCount() + 1);
          }
        }
        meeMeetingRoomService.updateBatchById(meeMeetingRoomList);
      }
    }
  }

  /*
  @SuppressWarnings("unchecked")
  @Override
 public MeeLeaderMeetingRoomCountDO leaderList(MeeInsideMeetingVO obj) throws Exception {
    // 查询出哪些领导
    MeeLeaderMeetingRoomCountDO meetingRoomCountDO = null;
    SysParameter sysParameter = sysParameterService.selectBykey("leader_meeting_member");
    if (null != sysParameter) {
      List<String> userIdList = Arrays.asList(sysParameter.getParamValue().split(";"));
      List<AutUser> userList = new ArrayList<AutUser>();
      if (null != userIdList && !userIdList.isEmpty()) {
        meetingRoomCountDO = new MeeLeaderMeetingRoomCountDO();
        Where<MeeInsideMeeting> insideWhere = new Where<MeeInsideMeeting>();
        Where<MeeOutsideMeeting> outsideWhere = new Where<MeeOutsideMeeting>();
        if (null != obj) { // 添加内、外部会议伪列标识；
          insideWhere
              .setSqlSelect("id,meeting_title,meeting_host,contacts,meeting_room_id,address,meeting_room_name,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation,'inside' ");
          outsideWhere
              .setSqlSelect("id,meeting_title,meeting_host,contacts,address,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation,'outside' ");

          // 1、is_delete = 0，内部会议 AND meeting_situation BETWEEN 1 AND 2 ;外部会议AND audit_status = 3
          // AND meeting_situation BETWEEN 1 AND 2
          insideWhere.between("meeting_situation", 1, 2);
          outsideWhere.eq("audit_status", 3);
          outsideWhere.between("meeting_situation", 1, 2);
          // 2、只针对开始日期时间的条件限制，精确到秒；
          if (StringUtils.isNotEmpty(obj.getThisWeek())) {
            insideWhere.between("begin_time",
                DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
            outsideWhere.between("begin_time",
                DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));

          }
          if (StringUtils.isNotEmpty(obj.getThisMonth())) {
            insideWhere.between("begin_time",
                DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
            outsideWhere.between("begin_time",
                DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
          }
          // 3、多个人id模糊匹配时，and后要加括号
          Where<AutUser> userwhere = new Where<AutUser>();
          userwhere.setSqlSelect("id,user_name,full_name");
          userwhere.in("id", userIdList);
          if (null != obj.getSearchKey()) {
            userwhere.like("full_name", obj.getSearchKey());
          }
          userList = autUserService.selectList(userwhere);
          if (userList != null && !userList.isEmpty()) {
//            insideWhere.and();
//            outsideWhere.and();
              insideWhere.andNew();
              outsideWhere.andNew();
            
            int iloop = 0;
            String userId = "";
            for (AutUser autUser : userList) {
              userId = autUser.getId().toString().trim();
              if (iloop == 0) {
                insideWhere.like("party_memebers_id", userId);
                outsideWhere.like("party_memebers_id", userId);
              } else {
                insideWhere.or("party_memebers_id like {0}", "%" + userId + "%");
                outsideWhere.or("party_memebers_id like {0}", "%" + userId + "%");

              }
              iloop = iloop + 1;
            }
          }
          insideWhere.orderBy("create_time", false);
          outsideWhere.orderBy("create_time", false);
          List<MeeInsideMeeting> meeInsideMeetingList = selectList(insideWhere);
          List<MeeOutsideMeeting> meeOutsideMeetingList =
              meeOutsideMeetingService.selectList(outsideWhere);

          List<Map<Object, Object>> mapList = new ArrayList<Map<Object, Object>>();
          Map<String, Object> map = new HashMap<String, Object>();
          Map<String, Object> map2 = new HashMap<String, Object>();
          Map<Object, Object> mp = new HashMap<Object, Object>();
          Map<Object, Object> mp2 = new HashMap<Object, Object>();
          List<String> theadList = new ArrayList<String>();
          theadList.add("领导人");
          theadList.add("会议次数"); // 按周查询
          if (StringUtils.isNotEmpty(obj.getThisWeek())) {
            theadList.add("周一");
            theadList.add("周二");
            theadList.add("周三");
            theadList.add("周四");
            theadList.add("周五");
            theadList.add("周六");
            theadList.add("周天");
            if ((null != userList && !userList.isEmpty())) {
              List<MeeInsideMeeting> insideMeetingList = null;
              List<MeeOutsideMeeting> outsideMeetingList = null;

              for (AutUser user : userList) {
                Map<String, Object> mapweekinside = new HashMap<String, Object>();
                Map<String, Object> mapweekoutside = new HashMap<String, Object>();
                insideMeetingList = new ArrayList<MeeInsideMeeting>();
                outsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String weekDay = DateUtil.getWeek(meeInsideMeeting.getBeginTime());
                    weekDay = convertWeekDay(weekDay);
                    if ("周天".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    } else if ("周一".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    } else if ("周二".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    } else if ("周三".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    } else if ("周四".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    } else if ("周五".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    } else if ("周六".equals(weekDay)) {
                      insideMeetingList.add(meeInsideMeeting);
                    }
                    mapweekinside.put(weekDay, insideMeetingList); // 每一天对应的会议次数list，先判断无则创建添加，有则取出添加记录；
                    map.put(weekDay, insideMeetingList);
                    mp.put(user.getFullName(), mapweekinside); // 各领导每天的内部会议次数
                  }
                }
                for (MeeOutsideMeeting meeInsideMeeting : meeOutsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    String weekDay = DateUtil.getWeek(meeInsideMeeting.getBeginTime());
                    weekDay = convertWeekDay(weekDay);
                    if ("周天".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    } else if ("周一".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    } else if ("周二".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    } else if ("周三".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    } else if ("周四".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    } else if ("周五".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    } else if ("周六".equals(weekDay)) {
                      outsideMeetingList.add(meeInsideMeeting);
                    }
                    mapweekoutside.put(weekDay, outsideMeetingList);// 每一天对应的会议次数list，先判断无则创建添加，有则取出添加记录；计算出错；
                    map2.put(weekDay, outsideMeetingList); // 用途？？
                    mp2.put(user.getFullName(), mapweekoutside);// 各领导每天的外部会议次数
                  }
                }
              }
            }

            Map<String, Object> mpp = new HashMap<String, Object>();
            Map<String, Object> mpp3 = new HashMap<String, Object>();
            Map<Object, Object> mp3 = new HashMap<Object, Object>();
            // 找出各领导每天的会议记录放在mpp中；
            if (null != mp && !mp.isEmpty()) {
              for (AutUser user : userList) {
                for (Object k : mp.keySet()) {
                  if (null != map && !map.isEmpty()) {
                    for (String key : map.keySet()) {
                      List<MeeInsideMeeting> insideList = (List<MeeInsideMeeting>) map.get(key);
                      List<MeeInsideMeeting> meetingList = new ArrayList<MeeInsideMeeting>();
                      String weekDay = "";
                      for (MeeInsideMeeting insideMeeting : insideList) {
                        weekDay = DateUtil.getWeek(insideMeeting.getBeginTime());
                        weekDay = convertWeekDay(weekDay);
                        if (k.equals(user.getFullName()) && weekDay.equals(key)) {
                          meetingList.add(insideMeeting);
                        }
                      }
                      if (!weekDay.equals("") && !meetingList.isEmpty()) {
                        mpp.put(weekDay, meetingList);
                      }
                    }
                  }
                }
              }
            }


            if ((null != mp && !mp.isEmpty()) || (null != mp2 && !mp2.isEmpty())) {
              mp.putAll(mp2);
              List<MeeInsideMeeting> insideMeetingList = new ArrayList<MeeInsideMeeting>();
              for (Object k : mp.keySet()) {
                Map<String, Object> mo = (Map<String, Object>) mp.get(k);
                if (null != mo && !mo.isEmpty()) {
                  for (String mokey : mo.keySet()) {
                    for (String key : mpp.keySet()) {
                      if (mokey.equals(key)) {
                        insideMeetingList = (List<MeeInsideMeeting>) mpp.get(key);
                      }
                    }

                    if (!insideMeetingList.isEmpty()) {
                      mpp3.put(mokey, insideMeetingList);
                    }
                  }
                }
                mp3.put((String) k, mpp3);
              }
            }

            if (null != mpp3 && !mpp3.isEmpty()) {
              mapList.add(mp3);
            }

            if (null != mapList && !mapList.isEmpty()) {
              meetingRoomCountDO.setMapList(mapList);
            }
          }

          // 按月查询
          if (StringUtils.isNotEmpty(obj.getThisMonth())) {
            if ((null != userList && !userList.isEmpty())) {
              List<MeeInsideMeeting> insideMeetingList = new ArrayList<MeeInsideMeeting>();
              List<MeeOutsideMeeting> insideMeetingList2 = new ArrayList<MeeOutsideMeeting>();
              int month = DateUtil.getDaysOfMonth();
              for (int i = 1; i <= month; i++) {
                theadList.add(i + "");
              }
              for (AutUser user : userList) {
                for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    int day = DateUtil.getDaysOfMonth(meeInsideMeeting.getBeginTime());
                    insideMeetingList.add(meeInsideMeeting);
                    map.put(day + "", insideMeetingList);
                    mp.put(user.getFullName(), map);
                  }
                }
                for (MeeOutsideMeeting meeInsideMeeting : meeOutsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    int day = DateUtil.getDaysOfMonth(meeInsideMeeting.getBeginTime());
                    insideMeetingList2.add(meeInsideMeeting);
                    map2.put(day + "", insideMeetingList2);
                    mp2.put(user.getFullName(), map2);
                  }
                }
              }
            }

            Map<String, Object> mpp = new HashMap<String, Object>();
            Map<String, Object> mpp2 = new HashMap<String, Object>();
            Map<String, Object> mpp3 = new HashMap<String, Object>();
            Map<Object, Object> mp3 = new HashMap<Object, Object>();
            if (null != mp && !mp.isEmpty()) {
              for (Object k : mp.keySet()) {
                if (null != map && !map.isEmpty()) {
                  for (String key : map.keySet()) {
                    List<MeeInsideMeeting> insideList = (List<MeeInsideMeeting>) map.get(key);
                    List<MeeInsideMeeting> meetingList = new ArrayList<MeeInsideMeeting>();
                    for (MeeInsideMeeting insideMeeting : insideList) {
                      for (AutUser user : userList) {
                        int day = DateUtil.getDaysOfMonth(insideMeeting.getBeginTime());
                        if (k.equals(user.getFullName()) && String.valueOf(day).equals(key)) {
                          meetingList.add(insideMeeting);
                          mpp.put(day + "", meetingList);
                        }
                      }
                    }
                  }
                }
              }
            }

            if (null != mp2 && !mp2.isEmpty()) {
              for (Object k : mp2.keySet()) {
                if (null != map2 && !map2.isEmpty()) {
                  for (String key : map2.keySet()) {
                    List<MeeOutsideMeeting> insideList = (List<MeeOutsideMeeting>) map2.get(key);
                    List<MeeOutsideMeeting> meetingList = new ArrayList<MeeOutsideMeeting>();
                    for (MeeOutsideMeeting insideMeeting : insideList) {
                      for (AutUser user : userList) {
                        int day = DateUtil.getDaysOfMonth(insideMeeting.getBeginTime());
                        if (k.equals(user.getFullName()) && String.valueOf(day).equals(key)) {
                          meetingList.add(insideMeeting);
                          mpp2.put(day + "", meetingList);
                        }
                      }
                    }
                  }
                }
              }
            }

            if ((null != mp && !mp.isEmpty()) || (null != mp2 && !mp2.isEmpty())) {
              mp.putAll(mp2);
              for (Object k : mp.keySet()) {
                Map<String, Object> mo = (Map<String, Object>) mp.get(k);
                if (null != mo && !mo.isEmpty()) {
                  for (String mokey : mo.keySet()) {
                    List<MeeInsideMeeting> list1 = new ArrayList<MeeInsideMeeting>();
                    List<MeeInsideMeeting> list2 = new ArrayList<MeeInsideMeeting>();
                    if ((null != mpp && !mpp.isEmpty()) || (null != mpp2 && !mpp2.isEmpty())) {
                      for (String key : mpp.keySet()) {
                        if (mokey.equals(key)) {
                          List<MeeInsideMeeting> insideMeetingList =
                              (List<MeeInsideMeeting>) mpp.get(key);
                          list1 = insideMeetingList;
                        }
                      }

                      for (String key2 : mpp2.keySet()) {
                        if (mokey.equals(key2)) {
                          List<MeeOutsideMeeting> insideMeetingList2 =
                              (List<MeeOutsideMeeting>) mpp2.get(key2);
                          List<MeeInsideMeeting> meetingList = new ArrayList<MeeInsideMeeting>();
                          for (MeeOutsideMeeting meeInsideMeeting : insideMeetingList2) {
                            MeeInsideMeeting meeInsideMeeting1 = new MeeInsideMeeting();
                            BeanUtils.copyProperties(meeInsideMeeting, meeInsideMeeting1);
                            meetingList.add(meeInsideMeeting1);
                          }
                          list2 = meetingList;
                        }
                      }

                      if ((null != list1 && !list1.isEmpty() && null != list2 && !list2.isEmpty())) {
                        list1.addAll(list2);
                        mpp3.put(mokey, list1);
                      }

                      if ((null == list1 || list1.isEmpty()) && (null != list2 && !list2.isEmpty())) {
                        mpp3.put(mokey, list2);
                      }

                      if ((null != list1 && !list1.isEmpty()) && (null == list2 || list2.isEmpty())) {
                        mpp3.put(mokey, list1);
                      }
                      mp3.put((String) k, mpp3);
                    }
                  }
                }

              }
            }

            if (null != mpp3 && !mpp3.isEmpty()) {
              mapList.add(mp3);
            }

            if (null != mapList && !mapList.isEmpty()) {
              meetingRoomCountDO.setMapList(mapList);
            }
          }
          meetingRoomCountDO.setTheadList(theadList);
        } else {
          throw new Exception("参数传递异常");
        }
      } else {
        throw new Exception("请选择要统计的领导人");
      }
    } else {
      throw new Exception("系统参数表中未配置'leader_meeting_member'");
    }
    return meetingRoomCountDO;
  }
   */
/*
  @SuppressWarnings("unchecked")
  @Override
  public MeeLeaderMeetingRoomCountDO leaderList(MeeInsideMeetingVO obj) throws Exception {// 查询出哪些领导
    MeeLeaderMeetingRoomCountDO meetingRoomCountDO = null;
    SysParameter sysParameter = sysParameterService.selectBykey("leader_meeting_member");
    if (null != sysParameter) {
      List<String> userIdList = Arrays.asList(sysParameter.getParamValue().split(";"));
      List<AutUser> userList = new ArrayList<AutUser>();
      if (null != userIdList && !userIdList.isEmpty()) {
        meetingRoomCountDO = new MeeLeaderMeetingRoomCountDO();
        Where<MeeInsideMeeting> insideWhere = new Where<MeeInsideMeeting>();
        Where<MeeOutsideMeeting> outsideWhere = new Where<MeeOutsideMeeting>();
        if (null != obj) {
          // 添加内、外部会议伪列标识；
          insideWhere
              .setSqlSelect("id,meeting_title,meeting_host,contacts,meeting_room_id,address,meeting_room_name,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation,'inside' as flag ");
          outsideWhere
              .setSqlSelect("id,meeting_title,meeting_host,contacts,address,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation,'outside' as flag ");

          // 1、is_delete = 0，内部会议 AND meeting_situation BETWEEN 1 AND 2 ;外部会议AND audit_status = 3
          // AND meeting_situation BETWEEN 1 AND 2
          insideWhere.between("meeting_situation", 1, 2);
          outsideWhere.eq("audit_status", 3);
          outsideWhere.between("meeting_situation", 1, 2);
          // 2、只针对开始日期时间的条件限制，精确到秒；
          if (StringUtils.isNotEmpty(obj.getThisWeek())) {
            insideWhere.between("begin_time",
                DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
            outsideWhere.between("begin_time",
                DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));

          }
          if (StringUtils.isNotEmpty(obj.getThisMonth())) {
            insideWhere.between("begin_time",
                DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
            outsideWhere.between("begin_time",
                DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS),
                DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
          }
          // 3、多个人id模糊匹配时，and后要加括号
          Where<AutUser> userwhere = new Where<AutUser>();
          userwhere.setSqlSelect("id,user_name,full_name");
          userwhere.in("id", userIdList);
          if (null != obj.getSearchKey()) {
            userwhere.like("full_name", obj.getSearchKey());
          }
          userList = autUserService.selectList(userwhere);
          if (userList != null && !userList.isEmpty()) {
            insideWhere.andNew();
            outsideWhere.andNew();
            int loop = 0;
            String userId = "";
            for (AutUser autUser : userList) {
              userId = autUser.getId().toString().trim();
              if (loop == 0) {
                insideWhere.like("party_memebers_id", userId);
                outsideWhere.like("party_memebers_id", userId);
              } else {
                insideWhere.or("party_memebers_id like {0}", "%" + userId + "%");
                outsideWhere.or("party_memebers_id like {0}", "%" + userId + "%");
              }
              loop = loop + 1;
            }
          }
          insideWhere.orderBy("begin_time", true);
          outsideWhere.orderBy("begin_time", true);
          List<MeeInsideMeeting> meeInsideMeetingList = selectList(insideWhere);
          List<MeeOutsideMeeting> meeOutsideMeetingList = meeOutsideMeetingService.selectList(outsideWhere);
          Map<Object, Object> leaderMapData = new HashMap<Object, Object>(); //没有用
          List<String> theadList = new ArrayList<String>();
          theadList.add("领导人");
          theadList.add("会议次数");
          // 按周查询
          if (StringUtils.isNotEmpty(obj.getThisWeek())) {
            // 列标题
            theadList.add("周一");
            theadList.add("周二");
            theadList.add("周三");
            theadList.add("周四");
            theadList.add("周五");
            theadList.add("周六");
            theadList.add("周天");
            if ((null != userList && !userList.isEmpty())) {
              Map<String, Map<String, List<Object>>> leaderWeekDataMap =
                  new HashMap<String, Map<String, List<Object>>>();
              for (AutUser user : userList) {
                // 一周内每天对应的会议记录数；
                Map<String, List<MeeInsideMeeting>> insideMeetingWeekListDataMap =
                    new HashMap<String, List<MeeInsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况；
                List<MeeInsideMeeting> initInsideMeetingList = new ArrayList<MeeInsideMeeting>();
                insideMeetingWeekListDataMap.put("周一", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周二", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周三", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周四", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周五", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周六", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周日", initInsideMeetingList);
                for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String weekDay = DateUtil.getWeek(meeInsideMeeting.getBeginTime());
                    weekDay = convertWeekDay(weekDay);
                    List<MeeInsideMeeting> tempMeeInsideMeetingList =
                        insideMeetingWeekListDataMap.get(weekDay);
                    // 每一天对应的会议次数list，先判断无则创建添加，有则取出添加记录；
                    if (tempMeeInsideMeetingList == null || tempMeeInsideMeetingList.isEmpty()) {
                      tempMeeInsideMeetingList = new ArrayList<MeeInsideMeeting>();
                      tempMeeInsideMeetingList.add(meeInsideMeeting);
                    } else {
                      tempMeeInsideMeetingList.add(meeInsideMeeting);
                    }
                    insideMeetingWeekListDataMap.put(weekDay, tempMeeInsideMeetingList);
                    leaderMapData.put(user.getFullName(), insideMeetingWeekListDataMap); // 各领导每天的内部会议次数
                  }
                }

                // 一周内每天 对应的外部会议记录条数
                Map<String, List<MeeOutsideMeeting>> outsideMeetingWeekListDataMap =
                    new HashMap<String, List<MeeOutsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况； initOutsideMeetingList = null 是否直接赋值为null更合理；
                List<MeeOutsideMeeting> initOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                outsideMeetingWeekListDataMap.put("周一", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周二", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周三", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周四", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周五", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周六", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周日", initOutsideMeetingList);
                for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
                  if (meeOutsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String weekDay = DateUtil.getWeek(meeOutsideMeeting.getBeginTime());
                    weekDay = convertWeekDay(weekDay);
                    List<MeeOutsideMeeting> tempMeeOutsideMeetingList =
                        outsideMeetingWeekListDataMap.get(weekDay);

                    if (tempMeeOutsideMeetingList == null || tempMeeOutsideMeetingList.isEmpty()) {
                      tempMeeOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    } else {
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    }

                    outsideMeetingWeekListDataMap.put(weekDay, tempMeeOutsideMeetingList);

                    leaderMapData.put(user.getFullName(), outsideMeetingWeekListDataMap); // 各领导
                                                                                          // 每天的外部会议次数
                  }
                }
                // 对每位领导 一个星期内各天 内部、外部会议 次数的合并；
                Map<String, List<Object>> inOutMeetingWeekListDataMap =
                    new HashMap<String, List<Object>>();
                for (Map.Entry<String, List<MeeInsideMeeting>> insideMeetingWeekListEntry : insideMeetingWeekListDataMap
                    .entrySet()) {
                  String weekKey = insideMeetingWeekListEntry.getKey();
                  List<MeeInsideMeeting> insideMeetingList = insideMeetingWeekListEntry.getValue();
                  List<Object> objectList = new ArrayList<Object>();
                  if (insideMeetingList != null && !insideMeetingList.isEmpty()) {
                    objectList.addAll(insideMeetingList);
                  }
                  List<MeeOutsideMeeting> outsideMeetingList =
                      outsideMeetingWeekListDataMap.get(weekKey);
                  if (outsideMeetingList != null && !outsideMeetingList.isEmpty()) {
                    objectList.addAll(outsideMeetingList);
                  }

                  inOutMeetingWeekListDataMap.put(weekKey, objectList);
                }
                leaderWeekDataMap.put(user.getFullName(), inOutMeetingWeekListDataMap);
              }

              // 测试后台数据
              for (Map.Entry<String, Map<String, List<Object>>> entryUserWeekDataList : leaderWeekDataMap
                  .entrySet()) { 
                String userName = entryUserWeekDataList.getKey();
                Map<String, List<Object>> entryWeekDataList = entryUserWeekDataList.getValue();
                for (Map.Entry<String, List<Object>> entryDataList : entryWeekDataList.entrySet()) {
                  String weekName = entryDataList.getKey();
                  List<Object> dataList = entryDataList.getValue();
                  if (dataList != null && !dataList.isEmpty()) {
                    for (Object object : dataList) {
                      // System.out.println(userName+"---"+weekName+"---"+((MeeInsideMeeting)object).getId().toString()+"---"+((MeeInsideMeeting)object).getPartyMemeberNames());
                      System.out.println(userName + "---" + weekName + "---");
                    }
                  }
                }
              }

              // 兼容处理,需要前端修改；
              Map<Object, Object> convertMap = new HashMap<Object, Object>();
              List<Map<Object, Object>> convertMapList = new ArrayList<Map<Object, Object>>();
              if (null != leaderWeekDataMap && !leaderWeekDataMap.isEmpty()) {
                for (Map.Entry<String, Map<String, List<Object>>> entry : leaderWeekDataMap
                    .entrySet()) {
                  convertMap.put((Object) entry.getKey(), (Object) entry.getValue());
                  convertMapList.add(convertMap);
                  meetingRoomCountDO.setMapList(convertMapList);
                }
              }
            }

          }

          // 按月查询
          if (StringUtils.isNotEmpty(obj.getThisMonth())) {
            //设置个性化标题
            int month = DateUtil.getDaysOfMonth();
            for (int i = 1; i <= month; i++) {
              theadList.add(i + "");
            }
            if ((null != userList && !userList.isEmpty())) {
              //Map<人，Map<天，List<Object>>>
              Map<String, Map<String, List<Object>>> leaderMonthDayDataMap = new HashMap<String, Map<String, List<Object>>>();
              for (AutUser user : userList) {
                // 一个月内每天对应的 内部会议 记录数；
                Map<String, List<MeeInsideMeeting>> insideMeetingMonthListDataMap = new HashMap<String, List<MeeInsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况；
                List<MeeInsideMeeting> initInsideMeetingList = new ArrayList<MeeInsideMeeting>();
                for (int i = 1; i <= month; i++) {
                  insideMeetingMonthListDataMap.put(i+"", initInsideMeetingList);
                }
                
                for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String monthDay = String.valueOf(DateUtil.getDaysOfMonth(meeInsideMeeting.getBeginTime()));
                    List<MeeInsideMeeting> tempInsideMeetingDayList = insideMeetingMonthListDataMap.get(monthDay);
                    // 每一天对应的会议次数list，先判断无则创建添加，有则取出添加记录；
                    if (tempInsideMeetingDayList == null || tempInsideMeetingDayList.isEmpty()) {
                      tempInsideMeetingDayList = new ArrayList<MeeInsideMeeting>();
                      tempInsideMeetingDayList.add(meeInsideMeeting);
                    } else {
                      tempInsideMeetingDayList.add(meeInsideMeeting);
                    }
                    insideMeetingMonthListDataMap.put(monthDay, tempInsideMeetingDayList);
                    leaderMapData.put(user.getFullName(), insideMeetingMonthListDataMap); // 各领导每天的内部会议次数
                  }
                }
                // 一个月内每天对应的 外部会议 记录数；
                Map<String, List<MeeOutsideMeeting>> outsideMeetingMonthListDataMap = new HashMap<String, List<MeeOutsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况； initOutsideMeetingList = null 是否直接赋值为null更合理；
                List<MeeOutsideMeeting> initOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                for (int i = 1; i <= month; i++) {
                  outsideMeetingMonthListDataMap.put(i+"", initOutsideMeetingList);
                }
                for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
                  if (meeOutsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String monthDay = String.valueOf(DateUtil.getDaysOfMonth(meeOutsideMeeting.getBeginTime()));
                    List<MeeOutsideMeeting> tempMeeOutsideMeetingList = outsideMeetingMonthListDataMap.get(monthDay);
                    if (tempMeeOutsideMeetingList == null || tempMeeOutsideMeetingList.isEmpty()) {
                      tempMeeOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    } else {
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    }
                    outsideMeetingMonthListDataMap.put(monthDay, tempMeeOutsideMeetingList);
                    leaderMapData.put(user.getFullName(), outsideMeetingMonthListDataMap); // 各领导
                  }
                }
                
                // 对每位领导 一个星期内各天 内部、外部会议 次数的合并；
                Map<String, List<Object>> inOutMeetingMonthDayListDataMap =  new HashMap<String, List<Object>>();
                for (Map.Entry<String, List<MeeInsideMeeting>> insideMeetingMonthDayListEntry : insideMeetingMonthListDataMap.entrySet()) {
                  String monthDayKey = insideMeetingMonthDayListEntry.getKey();
                  List<MeeInsideMeeting> insideMeetingList = insideMeetingMonthDayListEntry.getValue();
                  List<Object> objectList = new ArrayList<Object>();
                  if (insideMeetingList != null && !insideMeetingList.isEmpty()) {
                    objectList.addAll(insideMeetingList);
                  }
                  List<MeeOutsideMeeting> outsideMeetingList = outsideMeetingMonthListDataMap.get(monthDayKey);
                  if (outsideMeetingList != null && !outsideMeetingList.isEmpty()) {
                    objectList.addAll(outsideMeetingList);
                  }

                  inOutMeetingMonthDayListDataMap.put(monthDayKey, objectList);
                }
                leaderMonthDayDataMap.put(user.getFullName(), inOutMeetingMonthDayListDataMap);
              }
           // 兼容处理,需要前端修改；
              Map<Object, Object> convertMap = new HashMap<Object, Object>();
              List<Map<Object, Object>> convertMapList = new ArrayList<Map<Object, Object>>();
              if (null != leaderMonthDayDataMap && !leaderMonthDayDataMap.isEmpty()) {
                for (Map.Entry<String, Map<String, List<Object>>> entry : leaderMonthDayDataMap
                    .entrySet()) {
                  convertMap.put((Object) entry.getKey(), (Object) entry.getValue());
                  convertMapList.add(convertMap);
                  meetingRoomCountDO.setMapList(convertMapList);
                }
              }
            }
          }
          //返回标题
          meetingRoomCountDO.setTheadList(theadList);
        } else {
          throw new Exception("参数传递异常");
        }
      } else {
        throw new Exception("请选择要统计的领导人");
      }
    } else {
      throw new Exception("系统参数表中未配置'leader_meeting_member'");
    }
    return meetingRoomCountDO;
  }*/

  @Override
  public MeeLeaderMeetingRoomCountDO leaderList(MeeInsideMeetingVO obj) throws Exception {// 查询出哪些领导
    MeeLeaderMeetingRoomCountDO meetingRoomCountDO = null;
    SysParameter sysParameter = sysParameterService.selectBykey("leader_meeting_member");
    if (null != sysParameter) {
      List<String> userIdList = Arrays.asList(sysParameter.getParamValue().split(";"));
      List<AutUser> userList = new ArrayList<AutUser>();
      if (null != userIdList && !userIdList.isEmpty()) {
        meetingRoomCountDO = new MeeLeaderMeetingRoomCountDO();
        Where<MeeInsideMeeting> insideWhere = new Where<MeeInsideMeeting>();
        Where<MeeOutsideMeeting> outsideWhere = new Where<MeeOutsideMeeting>();
        if (null != obj) {
          // 添加内、外部会议伪列标识；
          insideWhere
              .setSqlSelect("id,meeting_title,meeting_host,contacts,meeting_room_id,address,meeting_room_name,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation ");
          outsideWhere
              .setSqlSelect("id,meeting_title,meeting_host,contacts,address,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation ");

          // 1、is_delete = 0，内部会议 AND meeting_situation BETWEEN 1 AND 2 ;外部会议AND audit_status = 3
          // AND meeting_situation BETWEEN 1 AND 2
          insideWhere.between("meeting_situation", 1, 2);
          outsideWhere.eq("audit_status", 3);
          outsideWhere.between("meeting_situation", 1, 2);
          // 2、只针对开始日期时间的条件限制，精确到秒；
          if (StringUtils.isNotEmpty(obj.getThisWeek())) {
            String firstDateOfWeekStr = DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS) ;
            String lastDateOfWeekStr = DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS);
            insideWhere.between("begin_time",DateUtil.str2datetime(firstDateOfWeekStr),DateUtil.str2datetime(lastDateOfWeekStr));
            outsideWhere.between("begin_time",DateUtil.str2datetime(firstDateOfWeekStr),DateUtil.str2datetime(lastDateOfWeekStr));
          }
          if (StringUtils.isNotEmpty(obj.getThisMonth())) {
            String firstDateOfMonthStr = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS) ; 
            String lastDateOfMonthStr  = DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS);
            insideWhere.between("begin_time",DateUtil.str2datetime(firstDateOfMonthStr),DateUtil.str2datetime(lastDateOfMonthStr));
            outsideWhere.between("begin_time",DateUtil.str2datetime(firstDateOfMonthStr),DateUtil.str2datetime(lastDateOfMonthStr));
          }
          // 3、多个人id模糊匹配时，and后要加括号
          Where<AutUser> userwhere = new Where<AutUser>();
          userwhere.setSqlSelect("id,user_name,full_name");
          userwhere.in("id", userIdList);
          if (null != obj.getSearchKey()) {
            userwhere.like("full_name", obj.getSearchKey());
          }
          userList = autUserService.selectList(userwhere);
          if (userList != null && !userList.isEmpty()) {
            insideWhere.andNew();
            outsideWhere.andNew();
            int loop = 0;
            String userId = "";
            for (AutUser autUser : userList) {
              userId = autUser.getId().toString().trim();
              if (loop == 0) {
                insideWhere.like("party_memebers_id", userId);
                outsideWhere.like("party_memebers_id", userId);
              } else {
                insideWhere.or("party_memebers_id like {0}", "%" + userId + "%");
                outsideWhere.or("party_memebers_id like {0}", "%" + userId + "%");
              }
              loop = loop + 1;
            }
          }
          insideWhere.orderBy("begin_time", true);
          outsideWhere.orderBy("begin_time", true);
          List<MeeInsideMeeting> meeInsideMeetingList = selectList(insideWhere);
          List<MeeOutsideMeeting> meeOutsideMeetingList = meeOutsideMeetingService.selectList(outsideWhere);
          //根据搜索条件的userId查找人员排序； 
          Map<Long, Integer> userIdRankMap = new HashMap<Long, Integer>();
          if(userList != null && !userList.isEmpty()){
            List<Long> userIdLongList = new ArrayList<Long>();
            for (String idStr : userIdList) {
              userIdLongList.add(Long.valueOf(idStr.trim()));
            }
            userIdRankMap = autUserService.getUserRankList(userIdLongList, "");
          }
          
          
          
//          Map<Object, Object> leaderMapData = new HashMap<Object, Object>(); //没有用
          List<String> theadList = new ArrayList<String>();
          theadList.add("领导人");
          theadList.add("会议次数");
          // 按周查询
          if (StringUtils.isNotEmpty(obj.getThisWeek())) {
            // 列标题
            theadList.add("周一");
            theadList.add("周二");
            theadList.add("周三");
            theadList.add("周四");
            theadList.add("周五");
            theadList.add("周六");
            theadList.add("周天");
            if ((null != userList && !userList.isEmpty())) {
              Map<String, Map<String, List<Object>>> leaderWeekDataMap =
                  new HashMap<String, Map<String, List<Object>>>();
              for (AutUser user : userList) {
                // 一周内每天对应的会议记录数；
                Map<String, List<MeeInsideMeeting>> insideMeetingWeekListDataMap =
                    new HashMap<String, List<MeeInsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况；
                List<MeeInsideMeeting> initInsideMeetingList = new ArrayList<MeeInsideMeeting>();
                insideMeetingWeekListDataMap.put("周一", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周二", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周三", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周四", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周五", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周六", initInsideMeetingList);
                insideMeetingWeekListDataMap.put("周日", initInsideMeetingList);
                for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String weekDay = DateUtil.getWeek(meeInsideMeeting.getBeginTime());
                    weekDay = convertWeekDay(weekDay);
                    List<MeeInsideMeeting> tempMeeInsideMeetingList =
                        insideMeetingWeekListDataMap.get(weekDay);
                    // 每一天对应的会议次数list，先判断无则创建添加，有则取出添加记录；
                    if (tempMeeInsideMeetingList == null || tempMeeInsideMeetingList.isEmpty()) {
                      tempMeeInsideMeetingList = new ArrayList<MeeInsideMeeting>();
                      tempMeeInsideMeetingList.add(meeInsideMeeting);
                    } else {
                      tempMeeInsideMeetingList.add(meeInsideMeeting);
                    }
                    insideMeetingWeekListDataMap.put(weekDay, tempMeeInsideMeetingList);
//                    leaderMapData.put(user.getFullName(), insideMeetingWeekListDataMap); // 各领导每天的内部会议次数
                  }
                }

                // 一周内每天 对应的外部会议记录条数
                Map<String, List<MeeOutsideMeeting>> outsideMeetingWeekListDataMap =
                    new HashMap<String, List<MeeOutsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况； initOutsideMeetingList = null 是否直接赋值为null更合理；
                List<MeeOutsideMeeting> initOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                outsideMeetingWeekListDataMap.put("周一", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周二", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周三", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周四", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周五", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周六", initOutsideMeetingList);
                outsideMeetingWeekListDataMap.put("周日", initOutsideMeetingList);
                for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
                  if (meeOutsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String weekDay = DateUtil.getWeek(meeOutsideMeeting.getBeginTime());
                    weekDay = convertWeekDay(weekDay);
                    List<MeeOutsideMeeting> tempMeeOutsideMeetingList =
                        outsideMeetingWeekListDataMap.get(weekDay);

                    if (tempMeeOutsideMeetingList == null || tempMeeOutsideMeetingList.isEmpty()) {
                      tempMeeOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    } else {
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    }
                    outsideMeetingWeekListDataMap.put(weekDay, tempMeeOutsideMeetingList);
//                    leaderMapData.put(user.getFullName(), outsideMeetingWeekListDataMap); // 各领导 每天的外部会议次数
                  }
                }
                // 对每位领导 一个星期内各天 内部、外部会议 次数的合并；
                Map<String, List<Object>> inOutMeetingWeekListDataMap =
                    new HashMap<String, List<Object>>();
                for (Map.Entry<String, List<MeeInsideMeeting>> insideMeetingWeekListEntry : insideMeetingWeekListDataMap
                    .entrySet()) {
                  String weekKey = insideMeetingWeekListEntry.getKey();
                  List<MeeInsideMeeting> insideMeetingList = insideMeetingWeekListEntry.getValue();
                  List<Object> objectList = new ArrayList<Object>();
                  if (insideMeetingList != null && !insideMeetingList.isEmpty()) {
                    List<MeeInsideMeetingVO> meeInsideMeetingVOList = new ArrayList<MeeInsideMeetingVO>();
                    MeeInsideMeetingVO meeInsideMeetingVO = new MeeInsideMeetingVO();
                    for (MeeInsideMeeting meeInsideMeeting : insideMeetingList) {
                      BeanUtils.copyProperties(meeInsideMeeting,meeInsideMeetingVO);
                      //添加内部会议标识；
                      meeInsideMeetingVO.setFlag("inside");
                      meeInsideMeetingVOList.add(meeInsideMeetingVO);
                    }
                    objectList.addAll(meeInsideMeetingVOList);
                  }
                  List<MeeOutsideMeeting> outsideMeetingList = outsideMeetingWeekListDataMap.get(weekKey);
                  if (outsideMeetingList != null && !outsideMeetingList.isEmpty()) {
                    List<MeeOutsideMeetingVO> meeOutsideMeetingVOList = new ArrayList<MeeOutsideMeetingVO>();
                    MeeOutsideMeetingVO meeOutsideMeetingVO = new MeeOutsideMeetingVO();
                    for (MeeOutsideMeeting meeOutsideMeeting : outsideMeetingList) {
                      BeanUtils.copyProperties(meeOutsideMeeting,meeOutsideMeetingVO);
                      //添加内部会议标识；
                      meeOutsideMeetingVO.setFlag("outside");
                      meeOutsideMeetingVOList.add(meeOutsideMeetingVO);
                    }
                    
                    objectList.addAll(meeOutsideMeetingVOList);
                  }

                  inOutMeetingWeekListDataMap.put(weekKey, objectList);
                }
                Integer userRank = userIdRankMap.get(user.getId());
                leaderWeekDataMap.put(user.getFullName()+"("+userRank.toString()+")", inOutMeetingWeekListDataMap);
              }

              // 兼容处理,需要前端修改；
              Map<Object, Object> convertMap = new HashMap<Object, Object>();
              List<Map<Object, Object>> convertMapList = new ArrayList<Map<Object, Object>>();
              if (null != leaderWeekDataMap && !leaderWeekDataMap.isEmpty()) {
                for (Map.Entry<String, Map<String, List<Object>>> entry : leaderWeekDataMap
                    .entrySet()) {
                  convertMap.put((Object) entry.getKey(), (Object) entry.getValue());
//                  convertMapList.add(convertMap);
//                  meetingRoomCountDO.setMapList(convertMapList);
                }
                convertMapList.add(convertMap);
                meetingRoomCountDO.setMapList(convertMapList);
              }
            }

          }

          // 按月查询
          if (StringUtils.isNotEmpty(obj.getThisMonth())) {
            //设置个性化标题
            int month = DateUtil.getDaysOfMonth();
            for (int i = 1; i <= month; i++) {
              theadList.add(i + "");
            }
            if ((null != userList && !userList.isEmpty())) {
              //Map<人，Map<天，List<Object>>>
              Map<String, Map<String, List<Object>>> leaderMonthDayDataMap = new HashMap<String, Map<String, List<Object>>>();
              for (AutUser user : userList) {
                // 一个月内每天对应的 内部会议 记录数；
                Map<String, List<MeeInsideMeeting>> insideMeetingMonthListDataMap = new HashMap<String, List<MeeInsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况；
                List<MeeInsideMeeting> initInsideMeetingList = new ArrayList<MeeInsideMeeting>();
                for (int i = 1; i <= month; i++) {
                  insideMeetingMonthListDataMap.put(i+"", initInsideMeetingList);
                }
                
                for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
                  if (meeInsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String monthDay = String.valueOf(DateUtil.getDaysOfMonth(meeInsideMeeting.getBeginTime()));
                    List<MeeInsideMeeting> tempInsideMeetingDayList = insideMeetingMonthListDataMap.get(monthDay);
                    // 每一天对应的会议次数list，先判断无则创建添加，有则取出添加记录；
                    if (tempInsideMeetingDayList == null || tempInsideMeetingDayList.isEmpty()) {
                      tempInsideMeetingDayList = new ArrayList<MeeInsideMeeting>();
                      tempInsideMeetingDayList.add(meeInsideMeeting);
                    } else {
                      tempInsideMeetingDayList.add(meeInsideMeeting);
                    }
                    insideMeetingMonthListDataMap.put(monthDay, tempInsideMeetingDayList);
//                    leaderMapData.put(user.getFullName(), insideMeetingMonthListDataMap); // 各领导每天的内部会议次数
                  }
                }
                // 一个月内每天对应的 外部会议 记录数；
                Map<String, List<MeeOutsideMeeting>> outsideMeetingMonthListDataMap = new HashMap<String, List<MeeOutsideMeeting>>();
                // 初始化的目的是为了避免某一天未安排会议，在前端无法按顺序遍历的情况； initOutsideMeetingList = null 是否直接赋值为null更合理；
                List<MeeOutsideMeeting> initOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                for (int i = 1; i <= month; i++) {
                  outsideMeetingMonthListDataMap.put(i+"", initOutsideMeetingList);
                }
                for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
                  if (meeOutsideMeeting.getPartyMemebersId().indexOf(String.valueOf(user.getId())) > -1) {
                    // 以开始日期时间作为会议的开始日；
                    String monthDay = String.valueOf(DateUtil.getDaysOfMonth(meeOutsideMeeting.getBeginTime()));
                    List<MeeOutsideMeeting> tempMeeOutsideMeetingList = outsideMeetingMonthListDataMap.get(monthDay);
                    if (tempMeeOutsideMeetingList == null || tempMeeOutsideMeetingList.isEmpty()) {
                      tempMeeOutsideMeetingList = new ArrayList<MeeOutsideMeeting>();
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    } else {
                      tempMeeOutsideMeetingList.add(meeOutsideMeeting);
                    }
                    outsideMeetingMonthListDataMap.put(monthDay, tempMeeOutsideMeetingList);
//                    leaderMapData.put(user.getFullName(), outsideMeetingMonthListDataMap); // 各领导
                  }
                }
                
                // 对每位领导 一个星期内各天 内部、外部会议 次数的合并；
                Map<String, List<Object>> inOutMeetingMonthDayListDataMap =  new HashMap<String, List<Object>>();
                for (Map.Entry<String, List<MeeInsideMeeting>> insideMeetingMonthDayListEntry : insideMeetingMonthListDataMap.entrySet()) {
                  String monthDayKey = insideMeetingMonthDayListEntry.getKey();
                  List<MeeInsideMeeting> insideMeetingList = insideMeetingMonthDayListEntry.getValue();
                  List<Object> objectList = new ArrayList<Object>();
                  if (insideMeetingList != null && !insideMeetingList.isEmpty()) {

                    List<MeeInsideMeetingVO> meeInsideMeetingVOList = new ArrayList<MeeInsideMeetingVO>();
                    MeeInsideMeetingVO meeInsideMeetingVO = new MeeInsideMeetingVO();
                    for (MeeInsideMeeting meeInsideMeeting : insideMeetingList) {
                      BeanUtils.copyProperties(meeInsideMeeting,meeInsideMeetingVO);
                      //添加内部会议标识；
                      meeInsideMeetingVO.setFlag("inside");
                      meeInsideMeetingVOList.add(meeInsideMeetingVO);
                    }
                    objectList.addAll(meeInsideMeetingVOList);
//                    objectList.addAll(insideMeetingList);
                  }
                  List<MeeOutsideMeeting> outsideMeetingList = outsideMeetingMonthListDataMap.get(monthDayKey);
                  if (outsideMeetingList != null && !outsideMeetingList.isEmpty()) {

                    List<MeeOutsideMeetingVO> meeOutsideMeetingVOList = new ArrayList<MeeOutsideMeetingVO>();
                    MeeOutsideMeetingVO meeOutsideMeetingVO = new MeeOutsideMeetingVO();
                    for (MeeOutsideMeeting meeOutsideMeeting : outsideMeetingList) {
                      BeanUtils.copyProperties(meeOutsideMeeting,meeOutsideMeetingVO);
                      //添加内部会议标识；
                      meeOutsideMeetingVO.setFlag("outside");
                      meeOutsideMeetingVOList.add(meeOutsideMeetingVO);
                    }
                    objectList.addAll(meeOutsideMeetingVOList);
                  }

                  inOutMeetingMonthDayListDataMap.put(monthDayKey, objectList);
                }
                
                Integer userRank = userIdRankMap.get(user.getId());
                leaderMonthDayDataMap.put(user.getFullName()+"("+userRank.toString()+")", inOutMeetingMonthDayListDataMap);
              }
           // 兼容处理,需要前端修改；
              Map<Object, Object> convertMap = new HashMap<Object, Object>();
              List<Map<Object, Object>> convertMapList = new ArrayList<Map<Object, Object>>();
              if (null != leaderMonthDayDataMap && !leaderMonthDayDataMap.isEmpty()) {
                for (Map.Entry<String, Map<String, List<Object>>> entry : leaderMonthDayDataMap
                    .entrySet()) {
                  convertMap.put((Object) entry.getKey(), (Object) entry.getValue());
//                  convertMapList.add(convertMap);
//                  meetingRoomCountDO.setMapList(convertMapList);
                }
                convertMapList.add(convertMap);
                meetingRoomCountDO.setMapList(convertMapList);
              }
            }
          }
          //返回标题
          meetingRoomCountDO.setTheadList(theadList);
        } else {
          throw new Exception("参数传递异常");
        }
      } else {
        throw new Exception("请选择要统计的领导人");
      }
    } else {
      throw new Exception("系统参数表中未配置'leader_meeting_member'");
    }
    return meetingRoomCountDO;
  }
    @Override
    public List<MeeMeetingCountDO> meetingCountList(MeeMeetingCountVO obj, String weets) throws Exception {
        List<MeeMeetingCountDO> boxList = new ArrayList<MeeMeetingCountDO>();
        SimpleDateFormat df = new SimpleDateFormat(WebConstant.PATTERN_BAR_YYYYMMDD);
        SimpleDateFormat dfs = new SimpleDateFormat(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS);
        if (null != obj) {
            Date startTime = null;
            Date endTime = null;
            if ((obj.getBegTime() != "" && "".equals(obj.getBegTime()))
                    || (obj.getEndTime() != null && !"".equals(obj.getEndTime()))) {
                startTime = df.parse(obj.getBegTime());
                endTime = df.parse(obj.getEndTime());
            } else {
                if ("0".equals(weets)) {
                    // 30天
                    Calendar cal = Calendar.getInstance();
                    Date newDate=new Date();                    
                    cal.setTime(df.parse(df.format(newDate)));
                    
                    endTime = cal.getTime();        
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -30);
                    startTime = cal.getTime();
                    
                }
                if ("1".equals(weets)) {
                    // 本周
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                    startTime = cal.getTime();
                    cal.setTime(df.parse(df.format(startTime)));
                    startTime=cal.getTime();
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    endTime = cal.getTime();
                }
                if ("2".equals(weets)) {
                    // 本月
                    Calendar cal = Calendar.getInstance();
                    String newDate = df.format(new Date());
                    String[] newTime = newDate.split("-");
                    newDate = newTime[0] + "-" + newTime[1] + "-01";
                    cal.setTime(df.parse(newDate));
                    startTime = cal.getTime();
                    cal.add(Calendar.MONTH, 1);
                    endTime = cal.getTime();
                }
            }
            List<AutUser> userList = new ArrayList<AutUser>();
            if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
                AutDepartment autDepart = autDepartmentService.selectById(obj.getDeptId());
                userList = autDepartmentService.getChildDeptUserList(autDepart);
            } else {
                Where<AutUser> auWhere = new Where<AutUser>();
                auWhere.eq("is_active", 1);
                if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                    auWhere.like("full_name", obj.getFullName());
                }
                userList = autUserService.selectList(auWhere);
            }
            if (userList != null && userList.size() > 0) {
                Where<MeeInsideMeeting> intWhere = new Where<MeeInsideMeeting>();
                Where<MeeOutsideMeeting> outWhere = new Where<MeeOutsideMeeting>();
                Where<AutUserPosition> upWhere = new Where<AutUserPosition>();
                upWhere.eq("is_active", 1);
                upWhere.setSqlSelect("position_id,user_id");
                List<AutUserPosition> upList = autUserPositionService.selectList(upWhere);
                Where<AutPosition> apWhere = new Where<AutPosition>();
                apWhere.eq("is_active", 1);
                apWhere.setSqlSelect("id,position_name");
                List<AutPosition> apList = autPositionService.selectList(apWhere);
                intWhere.between("begin_time", startTime,  dfs.parse(df.format(endTime)+" 23:59:59"));
                outWhere.between("begin_time", startTime, dfs.parse(df.format(endTime)+" 23:59:59"));
                if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                    outWhere.like("party_memeber_names", obj.getFullName());
                    intWhere.like("party_memeber_names", obj.getFullName());
                }
                outWhere.eq("audit_status", 3);
                intWhere.where("meeting_situation <> {0}", 3);
                intWhere.setSqlSelect("party_memebers_id");
                outWhere.setSqlSelect("party_memebers_id");
                List<MeeInsideMeeting> intList = this.selectList(intWhere);
                List<MeeOutsideMeeting> outList = meeOutsideMeetingService.selectList(outWhere);
                // String userIds = "";
                if (userList != null && userList.size() > 0) {
                    for (AutUser autUser : userList) {
                        int intNo = 0;
                        int outNo = 0;
                        MeeMeetingCountDO meeDo = new MeeMeetingCountDO();
                        if (intList != null && intList.size() > 0) {
                            for (int j = 0; j < intList.size(); j++) {
                                String ids = intList.get(j).getPartyMemebersId();
                                if (ids.indexOf(autUser.getId().toString()) > -1) {
                                    intNo += 1;
                                }
                            }
                        }
                        if (outList != null && outList.size() > 0) {
                            for (int j = 0; j < outList.size(); j++) {
                                String ids = outList.get(j).getPartyMemebersId();
                                if (ids.indexOf(autUser.getId().toString()) > -1) {
                                    outNo += 1;
                                }
                            }
                        }
                        meeDo.setInSide(intNo);
                        meeDo.setUserId(autUser.getId());
                        meeDo.setFullName(autUser.getFullName());
                        meeDo.setOutSide(outNo);
                        String positionName = "";
                        if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                            String userName = autUser.getFullName();
                            if (userName.indexOf(obj.getFullName()) > -1) {
                                if (upList != null && upList.size() > 0 && apList != null && apList.size() > 0) {
                                    for (AutUserPosition aup : upList) {
                                        String aID = aup.getUserId().toString();
                                        String bID = autUser.getId().toString();
                                        if (aID.equals(bID)) {
                                            String aPid = aup.getPositionId().toString();
                                            for (AutPosition ap : apList) {
                                                String bPid = ap.getId().toString();
                                                if (aPid.equals(bPid)) {
                                                    positionName += ap.getPositionName() + ";";
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                meeDo.setPositionNames(positionName);
                                boxList.add(meeDo);
                            }
                        } else {
                            if (upList != null && upList.size() > 0 && apList != null && apList.size() > 0) {
                                for (AutUserPosition aup : upList) {
                                    String aID = aup.getUserId().toString();
                                    String bID = autUser.getId().toString();
                                    if (aID.equals(bID)) {
                                        String aPid = aup.getPositionId().toString();
                                        for (AutPosition ap : apList) {
                                            String bPid = ap.getId().toString();
                                            if (aPid.equals(bPid)) {
                                                positionName += ap.getPositionName() + ";";
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            meeDo.setPositionNames(positionName);
                            boxList.add(meeDo);
                        }
                    }

        }

      }

    }
    return boxList;
  }

    @Override
    public MeeMeetingCountDO meetingCount(MeeMeetingCountVO obj, String weets) throws Exception {
        MeeMeetingCountDO meeBox = new MeeMeetingCountDO();
        // SimpleDateFormat df = new
        // SimpleDateFormat(WebConstant.PATTERN_BAR_YYYYMMDD);
        meeBox.setOutSide(0);
        meeBox.setInSide(0);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        Date endTime = null;
        if (null != obj) {          
            if ("0".equals(weets)) {
                // 30天              
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                //cal.add(Calendar.DAY_OF_MONTH, 1);
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();
                cal.setTime(endTime);
                //cal.add(Calendar.DAY_OF_MONTH, -1);
                meeBox.setShowDate(df.format(startTime)+"至"+df.format(cal.getTime()));
            }
            
            
            // List<AutUser> userList = new ArrayList<AutUser>();
            Where<MeeInsideMeeting> intWhere = new Where<MeeInsideMeeting>();
            Where<MeeOutsideMeeting> outWhere = new Where<MeeOutsideMeeting>();
            Where<AutUserPosition> upWhere = new Where<AutUserPosition>();
            upWhere.eq("is_active", 1);
            upWhere.setSqlSelect("position_id,user_id");
            // List<AutUserPosition> upList =
            // autUserPositionService.selectList(upWhere);
            Where<AutPosition> apWhere = new Where<AutPosition>();
            apWhere.eq("is_active", 1);
            apWhere.setSqlSelect("id,position_name");
            // List<AutPosition> apList =
            // autPositionService.selectList(apWhere);
            intWhere.between("begin_time", startTime,dfs.parse(df.format(endTime)+" 23:59:59"));
            outWhere.between("begin_time", startTime, dfs.parse(df.format(endTime)+" 23:59:59"));
            outWhere.eq("audit_status", 3);
            intWhere.where("meeting_situation <> {0}", 3);
            intWhere.setSqlSelect("party_memebers_id");
            outWhere.setSqlSelect("party_memebers_id");
            List<MeeInsideMeeting> intList = this.selectList(intWhere);
            List<MeeOutsideMeeting> outList = meeOutsideMeetingService.selectList(outWhere);
            if (intList != null && intList.size() > 0) {
                meeBox.setInSide(intList.size());
            }
            if (outList != null && outList.size() > 0) {
                meeBox.setOutSide(outList.size());
            }
            
        }
        
        return meeBox;
    }

  public void sortList(List<MeeMeetingCountDO> list, MeeMeetingCountVO obj) {
    if (null != obj) {
      String inSort = obj.getInSort();
      String outSort = obj.getOutSort();
      // 内部会议降序
      if (StringUtils.isNotEmpty(inSort)) {
        if (inSort.equals("1")) {
          Comparator<MeeMeetingCountDO> meetingCountDOComparator =
              new Comparator<MeeMeetingCountDO>() {
                @Override
                public int compare(MeeMeetingCountDO o1, MeeMeetingCountDO o2) {
                  if (o1.getInSide() != o2.getInSide()) {
                    return o1.getInSide() - o2.getInSide();
                  } else if (o1.getInSide() != o2.getInSide()) {
                    return o1.getInSide() - o2.getInSide();
                  } else if (o1.getInSide() != o2.getInSide()) {
                    return o1.getInSide() - o2.getInSide();
                  }
                  return 0;
                }
              };
          Collections.sort(list, meetingCountDOComparator);
        } else {
          // 内部会议升序
          Comparator<MeeMeetingCountDO> meetingCountDOComparator =
              new Comparator<MeeMeetingCountDO>() {
                @Override
                public int compare(MeeMeetingCountDO o1, MeeMeetingCountDO o2) {
                  if (o1.getInSide() != o2.getInSide()) {
                    return o2.getInSide() - o1.getInSide();
                  } else if (o1.getInSide() != o2.getInSide()) {
                    return o2.getInSide() - o1.getInSide();
                  } else if (o1.getInSide() != o2.getInSide()) {
                    return o2.getInSide() - o1.getInSide();
                  }
                  return 0;
                }
              };
          Collections.sort(list, meetingCountDOComparator);
        }
        // 外部会议降序
      } else if (StringUtils.isNotEmpty(outSort)) {
        if ("1".equals(outSort)) {
          Comparator<MeeMeetingCountDO> meetingCountDOComparator =
              new Comparator<MeeMeetingCountDO>() {
                @Override
                public int compare(MeeMeetingCountDO o1, MeeMeetingCountDO o2) {
                  if (o1.getOutSide() != o2.getOutSide()) {
                    return o1.getOutSide() - o2.getOutSide();
                  } else if (o1.getOutSide() != o2.getOutSide()) {
                    return o1.getOutSide() - o2.getOutSide();
                  } else if (o1.getOutSide() != o2.getOutSide()) {
                    return o1.getOutSide() - o2.getOutSide();
                  }
                  return 0;
                }
              };
          Collections.sort(list, meetingCountDOComparator);
        } else {
          // 总附件大小升序
          Comparator<MeeMeetingCountDO> meetingCountDOComparator =
              new Comparator<MeeMeetingCountDO>() {
                @Override
                public int compare(MeeMeetingCountDO o1, MeeMeetingCountDO o2) {
                  if (o1.getOutSide() != o2.getOutSide()) {
                    return o2.getOutSide() - o1.getOutSide();
                  } else if (o1.getOutSide() != o2.getOutSide()) {
                    return o2.getOutSide() - o1.getOutSide();
                  } else if (o1.getOutSide() != o2.getOutSide()) {
                    return o2.getOutSide() - o1.getOutSide();
                  }
                  return 0;
                }
              };
          Collections.sort(list, meetingCountDOComparator);
        }
      }
    }
  }

  @Override
  public void export(HttpServletResponse response, MeeMeetingCountVO obj, String weets)
      throws Exception {
    List<MeeMeetingCountDO> mailCountList = meetingCountList(obj, weets);
    if (null != mailCountList && !mailCountList.isEmpty()) {
      String[][] headers =
          { {"No", "序号"}, {"FullName", "用户名"}, {"DeptName", "所属机构"}, {"InSide", "内部会议"},
              {"OutSide", "外部会议"}};
      List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
      for (MeeMeetingCountDO entity : mailCountList) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; i < headers.length; i++) {
          MeeMeetingCountDO schedulingVO = entity;
          String methodName = "get" + headers[i][0];
          Class<?> clazz = MeeMeetingCountDO.class;
          Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
          if (null != method) {
            map.put(headers[i][0], method.invoke(schedulingVO));
          }
        }
        dataset.add(map);
      }
      ExcelUtil.exportExcel("会议统计", headers, dataset, response);
    }
  }

  private String convertWeekDay(String weekDay) {
    String newWeekDay = "";
    if (weekDay.equals("星期一")) {
      newWeekDay = "周一";
    }
    if (weekDay.equals("星期二")) {
      newWeekDay = "周二";
    }
    if (weekDay.equals("星期三")) {
      newWeekDay = "周三";
    }
    if (weekDay.equals("星期四")) {
      newWeekDay = "周四";
    }
    if (weekDay.equals("星期五")) {
      newWeekDay = "周五";
    }
    if (weekDay.equals("星期六")) {
      newWeekDay = "周六";
    }
    if (weekDay.equals("星期日")) {
      newWeekDay = "周天";
    }
    return newWeekDay;
  }

  @Override
  public List<MeeLeaderMeetingDO> leaderCount(MeeInsideMeetingVO obj) throws Exception {
    MeeLeaderMeetingDO leaderMeetInfo = null;
    List<MeeLeaderMeetingDO> leaderMeetInfoList = new ArrayList<MeeLeaderMeetingDO>();
    SysParameter sysParameter = sysParameterService.selectBykey("leader_meeting_member");
    if (null != sysParameter) {
      // String leader = sysParameter.getParamValue();
      List<String> userIdList = Arrays.asList(sysParameter.getParamValue().split(";"));
      List<AutUser> userList = new ArrayList<AutUser>();
      // List<String> userIdsList = new ArrayList<String>();
      if (null != userIdList && !userIdList.isEmpty()) {
        if (null != obj) {
          Where<AutUser> userwhere = new Where<AutUser>();
          userwhere.setSqlSelect("id,user_name,full_name");
          userwhere.in("id", userIdList);
          if (null != obj.getSearchKey()) {
            userwhere.like("full_name", obj.getSearchKey());
          }
          userList = autUserService.selectList(userwhere);
          userIdList = new ArrayList<String>();
          // userIdList = new ArrayList<String>();
          int i = 0;
          for (AutUser u : userList) {
            ++i;
            Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
            Where<MeeOutsideMeeting> outWhere = new Where<MeeOutsideMeeting>();
            if (StringUtils.isNotEmpty(obj.getThisWeek())) {
              where.between("begin_time",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
              where.or("end_time between {0} and {1}",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));

              outWhere.between("begin_time",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
              outWhere.or("end_time between {0} and {1}",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));

            }
            if (StringUtils.isNotEmpty(obj.getThisMonth())) {
              where.between("begin_time",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
              where.or("end_time between {0} and {1}",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));

              outWhere.between("begin_time",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
              outWhere.or("end_time between {0} and {1}",
                  DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                      DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));

            }

            where.and();
            where.like("party_memebers_id", String.valueOf(u.getId()));

            outWhere.and();
            outWhere.like("party_memebers_id", String.valueOf(u.getId()));

            Integer meeInsideCount = selectCount(where);
            Integer meeOutsideCount = meeOutsideMeetingService.selectCount(outWhere);

            leaderMeetInfo = new MeeLeaderMeetingDO();
            leaderMeetInfo.setNo(i);
            leaderMeetInfo.setId(u.getId());
            leaderMeetInfo.setFullName(u.getFullName());
            leaderMeetInfo.setInsideCount(meeInsideCount);
            leaderMeetInfo.setOusideCount(meeOutsideCount);
            leaderMeetInfo.setUseCount(meeOutsideCount + meeInsideCount);

            Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
            w2.setSqlSelect("id,dept_id,user_id");
            w2.eq("user_id", u.getId());
            List<AutDepartmentUser> autDepartmentUser = autDepartmentUserService.selectList(w2);
            List<Long> deptIdList = new ArrayList<Long>();
            for (AutDepartmentUser deptUser : autDepartmentUser) {
              deptIdList.add(deptUser.getDeptId());
            }
            List<AutDepartment> deptList = new ArrayList<AutDepartment>();
            if (null != deptIdList && !deptIdList.isEmpty()) {
              Where<AutDepartment> w3 = new Where<AutDepartment>();
              w3.setSqlSelect("id,dept_name");
              w3.in("id", deptIdList);
              deptList = autDepartmentService.selectList(w3);
            }
            if (null != deptList && !deptList.isEmpty()) {
              leaderMeetInfo.setUserDepart(deptList.get(0).getDeptName());
            }
            leaderMeetInfoList.add(leaderMeetInfo);
          }
        }
      }
    }
    return leaderMeetInfoList;
  }

  @Override
  public Page<MeeInsideMeetingDO> leaderPersonList(PageBean pageBean, MeeLeaderMeetingDO obj)
      throws Exception {
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    if (null != obj) {
      where.like("party_memebers_id", String.valueOf(obj.getId()));
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        where.and();
        where.between("begin_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 00:00:00"), DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        where.and();
        where.between("begin_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 00:00:00"), DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)
            + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }

    }
    where.orderBy("create_time", false);
    Page<MeeInsideMeetingDO> page = new Page<MeeInsideMeetingDO>();
    Page<MeeInsideMeeting> oldPage =
        selectPage(new Page<MeeInsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    List<MeeInsideMeeting> meeInsideMeetingList = oldPage.getRecords();
    List<MeeInsideMeetingDO> meeInsideMeetingDOList = new ArrayList<MeeInsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
        MeeInsideMeetingDO meeInsideMeetingDO = new MeeInsideMeetingDO();
        meeInsideMeetingDO.setNo(i);
        meeInsideMeetingDO.setId(meeInsideMeeting.getId());
        meeInsideMeetingDO.setMeetingTitle(meeInsideMeeting.getMeetingTitle());
        meeInsideMeetingDO.setMeetingRoomName(meeInsideMeeting.getMeetingRoomName());
        DateTime begTime = new DateTime(meeInsideMeeting.getBeginTime());
        meeInsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeInsideMeeting.getEndTime());
        meeInsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeInsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        meeInsideMeetingDO.setMeetingSituation(situation);
        meeInsideMeetingDO.setContacts(meeInsideMeeting.getContacts());
        meeInsideMeetingDO.setCreateUserId(meeInsideMeeting.getCreateUserId());
        meeInsideMeetingDO.setMeetingHost(meeInsideMeeting.getMeetingHost());
        meeInsideMeetingDOList.add(meeInsideMeetingDO);
        userIdList.add(meeInsideMeeting.getCreateUserId());
        i++;
      }
    }
    if (null != meeInsideMeetingDOList && !meeInsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("is_active", 1);
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeInsideMeetingDO meeInsideMeetingDO : meeInsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeInsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeInsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeInsideMeetingDO.getCreateUserId())
                      && user.getId().intValue() == meeInsideMeetingDO.getCreateUserId().intValue()) {
                    meeInsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeInsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public void pushInsideMeeting() throws Exception {
    Date tomorrow = DateUtil.getAfter(new Date(), DateUtil.UNIT_DAY, 1);
    if (null != tomorrow) {
      String timeBegin = DateUtil.date2str(tomorrow) + " 00:00:00";
      String timeEnd = DateUtil.date2str(tomorrow) + " 23:59:59";
      Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
      where.eq("meeting_situation", 1);
      where.ge("begin_time", DateUtil.str2datetime(timeBegin));
      where.le("begin_time", DateUtil.str2datetime(timeEnd));
      where.andNew();
      where.eq("is_warn_msg", 1);
      where.or("is_warn_online = {0}", 1);
      where.or("is_warn_mail = {0}", 1);
      where
          .setSqlSelect("id,meeting_title,address,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation,create_user_id");
      List<MeeInsideMeeting> meetList = selectList(where);
      if (meetList != null && !meetList.isEmpty()) {
        Where<AutUser> userwhere = new Where<AutUser>();
        userwhere.setSqlSelect("id,user_name,full_name");
        userwhere.eq("user_name", WebConstant.SADMIN);
        AutUser user = autUserService.selectOne(userwhere);

        Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
        departmentUserWhere.eq("user_id", user.getId());
        departmentUserWhere.setSqlSelect("id,is_leader");
        List<AutDepartmentUser> departmentUserList =
            autDepartmentUserService.selectList(departmentUserWhere);

        String partyMemberNames = "";
        String partyMemberUserName = "";
        Where<MaiSendBox> maiSendBoxWhere = new Where<MaiSendBox>();
        maiSendBoxWhere.eq("send_user_id", user.getId());
        maiSendBoxWhere.setSqlSelect("id,send_user_id,mail_size");
        List<MaiSendBox> maiSendBoxList = maiSendBoxService.selectList(maiSendBoxWhere);
        List<AutUser> userlist = new ArrayList<AutUser>();
        for (MeeInsideMeeting mee : meetList) {
          AutUser user1 = autUserService.selectById(mee.getCreateUserId());
          String[] receivePersons = mee.getPartyMemebersId().split(";");
          String receivePerson = "";
          for (int i = 0; i < receivePersons.length; i++) {
            receivePerson += receivePersons[i] + ",";
          }
          Where<AutUser> userWhere = new Where<AutUser>();
          userWhere.setSqlSelect("id,user_name,full_name");
          userWhere.in("id", receivePerson);
          userlist = autUserService.selectList(userWhere);
          partyMemberNames = "";
          partyMemberUserName = "";
          for (AutUser autuser : userlist) {
            if (mee.getPartyMemebersId().contains(autuser.getId().toString())) {
              partyMemberNames += autuser.getUserName() + ";";
              partyMemberUserName += autuser.getFullName() + ";";
            }
          }
          if (mee.getIsWarnMail() != 0) {
            mailWarn4Push(mee, user1, MEET_NOTICE, partyMemberNames, partyMemberUserName,
                departmentUserList, maiSendBoxList);
          }
          if (mee.getIsWarnMsg() != 0) {
            SmsShortMessage smsShortMessage = smaWarn(mee, user1, MEET_NOTICE);
            smsShortMessageService.add(smsShortMessage,user1);
          }
          if (mee.getIsWarnOnline() != 0) {
            AutMsgOnlineRequestVO requestVO = onlineWarn(mee, user1, MEET_NOTICE);
            autMsgOnlineService.pushMessageToUserList(requestVO);
          }
        }
      }
    }
  }

  @Override
  public AutMsgOnlineRequestVO onlineWarn(MeeInsideMeeting obj, AutUser createuser, String type)
      throws Exception {
    Where<AutUser> userWhere = new Where<AutUser>();
    userWhere.eq("id", obj.getCreateUserId());
    AutUser user = autUserService.selectOne(userWhere);
    // 调用消息模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", user.getFullName());
    list.put("meetingRoom", obj.getMeetingRoomName());
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();
    AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
    if (type.equals(MEET_ADD)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }

    // 封装推送信息请求信息
    requestVO.setFromUserId(createuser.getId());
    requestVO.setFromUserFullName(createuser.getFullName());
    requestVO.setFromUserName(createuser.getUserName());
    if (null != templateList && !templateList.isEmpty()) {
      requestVO.setMsgContent(templateList.get(0));
    }
    // 会议详情-页面跳转
    requestVO.setGoUrl("mee/meeInsideMeeting/meeInsideManagePageDetail?id=" + obj.getId()
        + "&isDraft=0");
    requestVO.setMsgType(WebConstant.ONLINE_MSG_MEETING);
    List<String> receiveUserIdList = Arrays.asList(obj.getPartyMemebersId().split(";"));
    requestVO.setToUserId(receiveUserIdList);
    // 调用推送在线消息接口
    return requestVO;
  }

  /**
   * 邮件提醒
   * 
   * @param obj
   * @param createuser
   * @throws Exception
   */
  @Override
  public MaiWriteVO mailWarn(MeeInsideMeeting obj, AutUser createuser, String type)
      throws Exception {
    // 调用消息模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", obj.getMeetingRoomName());
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();

    String partyMemberUserName = "";
    List<String> receivePerson = Arrays.asList(obj.getPartyMemebersId().split(";"));
    Where<AutUser> userWhere = new Where<AutUser>();
    userWhere.setSqlSelect("id,user_name,full_name");
    userWhere.in("id", receivePerson);
    List<AutUser> userlist = autUserService.selectList(userWhere);
    String partyMemberNames = "";
    for (AutUser user1 : userlist) {
      for (String id : receivePerson) {
        if (id.equals(String.valueOf(user1.getId()))) {
          partyMemberNames += user1.getUserName() + ";";
          partyMemberUserName += user1.getFullName() + ";";
        }
      }
    }
    MaiWriteVO mai = new MaiWriteVO();
    MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
    MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
    maiReceiveBoxSearch.setAttachmentCount(0);
    maiReceiveBox.setReceiveUserIds(obj.getPartyMemebersId());
    maiReceiveBox.setReceiveFullNames(partyMemberUserName);
    maiReceiveBox.setReceiveUserNames(partyMemberNames);
    if (type.equals(MEET_ADD)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }
    if (null != templateList && !templateList.isEmpty()) {
        maiReceiveBoxSearch.setSubjectText(templateList.get(1));
      maiReceiveBox.setMailContent(templateList.get(0));
    } else {
      throw new Exception("消息模板未设置");
    }
    maiReceiveBox.setSendId(obj.getCreateUserId());
    maiReceiveBox.setSendUserName(createuser.getUserName());
    maiReceiveBoxSearch.setSendFullName(createuser.getFullName());
    mai.setMaiReceiveBox(maiReceiveBox);
    mai.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
    return mai;
  }

  @Override
  public SmsShortMessage smaWarn(MeeInsideMeeting obj, AutUser createuser, String type)
      throws Exception {
    // 调用模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", obj.getMeetingRoomName());
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();
    if (type.equals(MEET_ADD)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }
    SmsShortMessage smsShortMessage = new SmsShortMessage();
    String userids = obj.getPartyMemebersId();
    String userNames = obj.getPartyMemeberNames();
    if (userids.endsWith(";")) {
      userids = userids.substring(0, userids.length() - 1);
    }
    if (userNames.endsWith(";")) {
      userNames = userNames.substring(0, userNames.length() - 1);
    }
    smsShortMessage.setReceiverId(userids);
    smsShortMessage.setReceiverName(userNames);
    smsShortMessage.setContent(templateList.get(0));
    String[] receiveId = obj.getPartyMemebersId().split(";");
    smsShortMessage.setSendNumber(receiveId.length);
    smsShortMessage.setIsActive(1);
    smsShortMessage.setSendTime(new Date());
    smsShortMessage.setSendStatus(0);
    if (templateList != null && !templateList.isEmpty()) {
      smsShortMessage.setTheme(templateList.get(0));
    } else {
      throw new Exception("消息模板未设置");
    }

    // smsShortMessageService.add(smsShortMessage);
    return smsShortMessage;
  }

  @Override
  public MeeInsideMeetingVO getDetail4App(MeeInsideMeeting obj) throws Exception {
    MeeInsideMeetingVO meeInsideMeetingVO = null;
    if (null != obj && null != obj.getId()) {
      MeeInsideMeeting meeInsideMeeting = selectById(obj.getId());
      if (null != meeInsideMeeting) {
        meeInsideMeetingVO = new MeeInsideMeetingVO();
        BeanUtils.copyProperties(meeInsideMeeting, meeInsideMeetingVO);

        /*Where<ResAttachment> where = new Where<ResAttachment>();
        where.eq("resource_id", meeInsideMeeting.getId());
        List<ResAttachment> attachmentList = resAttachmentService.selectList(where);
        if (null != attachmentList && !attachmentList.isEmpty()) {
          SysParameter parameter = sysParameterService.selectBykey("ImgServer");
          String rootPath = "";
          if (null != parameter
              && com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(parameter.getParamValue())) {
            rootPath = parameter.getParamValue();
          }
          for (ResAttachment resAttachment : attachmentList) {
            String attachPath = resAttachment.getAttachPath();
            if (StringUtils.isNotEmpty(attachPath)) {
              if (attachPath.indexOf(File.separator) > -1) {
                String path = attachPath.substring(0, 20);
                if (path.equals("D:" + File.separator + File.separator + "aljoin" + File.separator
                    + File.separator + "upload" + File.separator + File.separator)) {
                  attachPath = attachPath.replace(path, "");
                }
              }
              if (attachPath.indexOf(File.pathSeparator) > -1) {
                attachPath = attachPath.replaceAll(File.pathSeparator, "/");
              }
            }
            resAttachment.setAttachPath(rootPath + attachPath);
          }
          meeInsideMeetingVO.setAttachmentList(attachmentList);
        }*/
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", meeInsideMeeting.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
          /*SysParameter parameter = sysParameterService.selectBykey("FastdfsServer");
          String rootPath = "";
          if (null != parameter
              && com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(parameter.getParamValue())) {
              rootPath = parameter.getParamValue();
          }
          for (ResResource resResource : resourceList) {
              int ts = (int)(System.currentTimeMillis() / 1000);
              String token = ProtoCommon.getToken(resResource.getFileName(), ts, ClientGlobal.getG_secret_key());
              String fullPath = rootPath + resResource.getGroupName() + File.separator + resResource.getFileName() + "?token=" + token + "&ts=" + ts; 
              resResource.setFileName(fullPath);
          }*/
          meeInsideMeetingVO.setResResourceList(resourceList);
        }
      }
    }
    return meeInsideMeetingVO;
  }

  @Override
  public List<MeeLeaderMeetingRoomCountDO> getAppRoom() throws Exception {
    List<MeeLeaderMeetingRoomCountDO> roomList = new ArrayList<MeeLeaderMeetingRoomCountDO>();
    Where<MeeMeetingRoom> roomWhere = new Where<MeeMeetingRoom>();
    roomWhere.setSqlSelect("meeting_room_name,use_count");
    List<MeeMeetingRoom> list = meeMeetingRoomService.selectList(roomWhere);
    if (list != null && list.size() > 0) {
      for (MeeMeetingRoom meeMeetingRoom : list) {
        MeeLeaderMeetingRoomCountDO contdo = new MeeLeaderMeetingRoomCountDO();
        contdo.setFullName(meeMeetingRoom.getMeetingRoomName());
        contdo.setUseCount(meeMeetingRoom.getUseCount());
        roomList.add(contdo);
      }
    }
    return roomList;
  }

  public void mailWarn4Push(MeeInsideMeeting obj, AutUser createuser, String type,
      String partyMemberNames, String partyMemberUserName,
      List<AutDepartmentUser> departmentUserList, List<MaiSendBox> maiSendBoxList) throws Exception {
    // 调用消息模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", obj.getMeetingRoomName());
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();

    MaiWriteVO mai = new MaiWriteVO();
    MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
    MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
    maiReceiveBoxSearch.setAttachmentCount(0);
    maiReceiveBox.setReceiveUserIds(obj.getPartyMemebersId());
    maiReceiveBox.setReceiveFullNames(partyMemberUserName);
    maiReceiveBox.setReceiveUserNames(partyMemberNames);
    if (type.equals(MEET_ADD)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList =
          sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
              WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }
    if (null != templateList && !templateList.isEmpty()) {
        maiReceiveBoxSearch.setSubjectText(templateList.get(1));
      maiReceiveBox.setMailContent(templateList.get(0));
    } else {
      throw new Exception("消息模板未设置");
    }
    // maiReceiveBox.setMailContent(obj.getMeetingContent());
    maiReceiveBox.setSendId(obj.getCreateUserId());
    maiReceiveBox.setSendUserName(createuser.getUserName());
    maiReceiveBoxSearch.setSendFullName(createuser.getFullName());
    mai.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
    mai.setMaiReceiveBox(maiReceiveBox);
    maiSendBoxService.add4Push(mai, createuser, departmentUserList, maiSendBoxList);
  }
}

