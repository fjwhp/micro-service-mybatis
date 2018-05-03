package aljoin.mee.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserRoleService;
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
import aljoin.mee.dao.mapper.MeeOutsideMeetingMapper;
import aljoin.mee.dao.object.AppMeeOutsideMeetingDO;
import aljoin.mee.dao.object.AppMeeOutsideMeetingVO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeLeaderMeetingDO;
import aljoin.mee.dao.object.MeeMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeOutsideMeetingDO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.mee.iservice.MeeMeetingRoomService;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.AppConstant;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 *
 * 外部会议表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Service
public class MeeOutsideMeetingServiceImpl extends
    ServiceImpl<MeeOutsideMeetingMapper, MeeOutsideMeeting> implements MeeOutsideMeetingService {

  @Resource
  private MeeOutsideMeetingMapper mapper;
  @Resource
  private AutUserService autUserService;
  @Resource
  private ResResourceService resResourceService;
  @Resource
  private MeeMeetingRoomService meeMeetingRoomService;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;
  @Resource
  private MeeInsideMeetingService meeInsideMeetingService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private ActAljoinBpmnService actAljoinBpmnService;
  @Resource
  private TaskService taskService;
  @Resource
  private RuntimeService runtimeService;
  @Resource
  private AutUserPositionService autUserPositionService;
  @Resource
  private ActFixedFormServiceImpl actFixedFormService;
  @Resource
  private SysParameterService sysParameterService;
  @Resource
  private SysMsgModuleInfoService sysMsgModuleInfoService;
  @Resource
  private AutMsgOnlineService autMsgOnlineService;
  @Resource
  private MaiSendBoxService maiSendBoxService;
  @Resource
  private AutUserRoleService autUserRoleService;
  @Resource
  private HistoryService historyService;
  @Resource
  private ActAljoinQueryHisService actAljoinQueryHisService;
  @Resource
  private ActAljoinQueryService actAljoinQueryService;
  @Resource
  private SmsShortMessageService smsShortMessageService;
  @Resource
  private AutDataStatisticsService autDataStatisticsService;

  private static final String MEET_ADD = "add";
  private static final String MEET_UPDATE = "update";
  private static final String MEET_CANCEL = "cancel";
  private static final String MEET_NOTICE = "notice";

  @Override
  public Page<MeeOutsideMeetingDO> list(PageBean pageBean, MeeOutsideMeetingVO obj)
      throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    where.setSqlSelect(
        "id,meeting_title,address,meeting_situation,begin_time,end_time,create_time,is_delete,create_user_id,create_user_name,audit_status");
    where.eq("create_user_id", obj.getCreateUserId());
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew();
        where.like("meeting_title", obj.getSearchKey());
        where.or("address like {0}", "%" + obj.getSearchKey() + "%");
      }
      if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
        where.andNew();
        where.like("meeting_title", obj.getMeetingTitle());
      }
      if (StringUtils.isNotEmpty(obj.getAddress())) {
        where.andNew();
        where.like("address", obj.getAddress());
      }
      if (null != obj.getAuditStatus()) {
        where.andNew();
        where.eq("audit_status", obj.getAuditStatus());
      }
      if (null != obj.getMeetingSituation()) {
        where.andNew();
        where.eq("meeting_situation", obj.getMeetingSituation());
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        where.andNew();
        String startTime = obj.getStartDate() + " 00:00:00";
        where.ge("begin_time", DateUtil.str2datetime(startTime));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.andNew();
        String endTime = obj.getEndDate() + " 23:59:59";
        where.le("begin_time", DateUtil.str2datetime(endTime));
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        String startTime = obj.getStartDate() + " 00:00:00";
        String endTime = obj.getEndDate() + " 23:59:59";
        where.andNew();
        where.between("begin_time", DateUtil.str2datetime(startTime), DateUtil.str2datetime(endTime));
      }
    }

    where.orderBy("begin_time", false);
    Page<MeeOutsideMeeting> oldPage = selectPage(
        new Page<MeeOutsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<MeeOutsideMeetingDO> page = new Page<MeeOutsideMeetingDO>();
    List<MeeOutsideMeeting> meeOutsideMeetingList = oldPage.getRecords();
    List<MeeOutsideMeetingDO> meeOutsideMeetingDOList = new ArrayList<MeeOutsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeOutsideMeetingList && !meeOutsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
        MeeOutsideMeetingDO meeOutsideMeetingDO = new MeeOutsideMeetingDO();
        meeOutsideMeetingDO.setNo(i);
        meeOutsideMeetingDO.setId(meeOutsideMeeting.getId());
        meeOutsideMeetingDO.setMeetingTitle(meeOutsideMeeting.getMeetingTitle());
        DateTime begTime = new DateTime(meeOutsideMeeting.getBeginTime());
        meeOutsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeOutsideMeeting.getEndTime());
        meeOutsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));

        Integer meetingSituation = meeOutsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        Integer autStatus = meeOutsideMeeting.getAuditStatus();
        String auditStatus = "";
        if (1 == autStatus) {
          auditStatus = "审核中";
        } else if (2 == autStatus) {
          auditStatus = "审核失败";
        } else if (3 == autStatus) {
          auditStatus = "审核通过";
        }
        meeOutsideMeetingDO.setMeetingSituation(situation);
        meeOutsideMeetingDO.setAuditStatus(auditStatus);
        meeOutsideMeetingDO.setAddress(meeOutsideMeeting.getAddress());
        meeOutsideMeetingDO.setCreateUserId(meeOutsideMeeting.getCreateUserId());
        meeOutsideMeetingDOList.add(meeOutsideMeetingDO);
        if (!userIdList.contains(meeOutsideMeeting.getCreateUserId())) {
          userIdList.add(meeOutsideMeeting.getCreateUserId());
        }
        i++;
      }
    }
    if (null != meeOutsideMeetingDOList && !meeOutsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.setSqlSelect("id,user_name,full_name");
      userWhere.eq("is_active", 1);
      if (userIdList.size() > 1) {
        userWhere.in("id", userIdList);
      } else if (!userIdList.isEmpty()) {
        userWhere.eq("id", userIdList.get(0));
      }
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeOutsideMeetingDO meeOutsideMeetingDO : meeOutsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeOutsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeOutsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeOutsideMeetingDO.getCreateUserId()) && user.getId()
                      .intValue() == meeOutsideMeetingDO.getCreateUserId().intValue()) {
                    meeOutsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeOutsideMeetingDOList);
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
  public void copyObject(MeeOutsideMeeting obj) throws Exception {
    mapper.copyObject(obj);
  }

  @Override
  public MeeOutsideMeetingVO detail(MeeOutsideMeeting obj) throws Exception {
    MeeOutsideMeetingVO meeOutsideMeetingVO = null;
    if (null != obj && null != obj.getId()) {
      MeeOutsideMeeting meeOutsideMeeting = selectById(obj.getId());
      if (null != meeOutsideMeeting) {
        List<String> tmpList = new ArrayList<String>();
        String id = "";
        String[] ids = meeOutsideMeeting.getPartyMemebersId().split(";");
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
        meeOutsideMeeting.setPartyMemeberNames(names);
        meeOutsideMeeting.setPartyMemebersId(id);
        meeOutsideMeetingVO = new MeeOutsideMeetingVO();
        BeanUtils.copyProperties(meeOutsideMeeting, meeOutsideMeetingVO);
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", meeOutsideMeeting.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        if (null != resourceList && !resourceList.isEmpty()) {
            meeOutsideMeetingVO.setResResourceList(resourceList);
        }
      }
    }
    return meeOutsideMeetingVO;
  }

  @Override
  public Page<MeeOutsideMeetingDO> personList(PageBean pageBean, MeeOutsideMeetingVO obj)
      throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    where.like("party_memebers_id", String.valueOf(obj.getCreateUserId()));
    where.setSqlSelect(
        "id,meeting_title,address,meeting_situation,begin_time,end_time,create_time,is_delete,create_user_id,create_user_name,audit_status");
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew();
        String searchKey = obj.getSearchKey();
        where.like("meeting_title", searchKey);
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        where.andNew();
        String startTime = obj.getStartDate() + " 00:00:00";
        where.ge("begin_time", DateUtil.str2datetime(startTime));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.andNew();
        String endTime = obj.getEndDate() + " 23:59:59";
        where.le("end_time", DateUtil.str2datetime(endTime));
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.andNew();
        String startTime = obj.getStartDate() + " 00:00:00";
        String endTime = obj.getEndDate() + " 23:59:59";
        where.between("begin_time", DateUtil.str2datetime(startTime), DateUtil.str2datetime(endTime));
        where.or("end_time between {0} and {1}", DateUtil.str2datetime(startTime), DateUtil.str2datetime(endTime));
      }
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        where.andNew();
        where.between("begin_time",
            DateUtil.str2datetime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2datetime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        where.andNew();
        where.between("begin_time",
            DateUtil.str2datetime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2datetime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
    }
    where.orderBy("begin_time", false);
    Page<MeeOutsideMeeting> oldPage = selectPage(
        new Page<MeeOutsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<MeeOutsideMeetingDO> page = new Page<MeeOutsideMeetingDO>();
    List<MeeOutsideMeeting> meeOutsideMeetingList = oldPage.getRecords();
    List<MeeOutsideMeetingDO> meeOutsideMeetingDOList = new ArrayList<MeeOutsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeOutsideMeetingList && !meeOutsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
        MeeOutsideMeetingDO meeOutsideMeetingDO = new MeeOutsideMeetingDO();
        meeOutsideMeetingDO.setNo(i);
        meeOutsideMeetingDO.setId(meeOutsideMeeting.getId());
        meeOutsideMeetingDO.setMeetingTitle(meeOutsideMeeting.getMeetingTitle());
        DateTime begTime = new DateTime(meeOutsideMeeting.getBeginTime());
        meeOutsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeOutsideMeeting.getEndTime());
        meeOutsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeOutsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        Integer autStatus = meeOutsideMeeting.getAuditStatus();
        String auditStatus = "";
        if (1 == autStatus) {
          auditStatus = "审核中";
        } else if (2 == autStatus) {
          auditStatus = "审核失败";
        } else if (3 == autStatus) {
          auditStatus = "审核通过";
        }
        meeOutsideMeetingDO.setMeetingSituation(situation);
        meeOutsideMeetingDO.setAuditStatus(auditStatus);
        meeOutsideMeetingDO.setAddress(meeOutsideMeeting.getAddress());
        meeOutsideMeetingDO.setCreateUserId(meeOutsideMeeting.getCreateUserId());
        meeOutsideMeetingDOList.add(meeOutsideMeetingDO);
        userIdList.add(meeOutsideMeeting.getCreateUserId());
        i++;
      }
    }
    if (null != meeOutsideMeetingDOList && !meeOutsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("is_active", 1);
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeOutsideMeetingDO meeOutsideMeetingDO : meeOutsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeOutsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeOutsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeOutsideMeetingDO.getCreateUserId()) && user.getId()
                      .intValue() == meeOutsideMeetingDO.getCreateUserId().intValue()) {
                    meeOutsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeOutsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public Page<MeeOutsideMeetingDO> auditlist(PageBean pageBean, MeeOutsideMeetingVO obj)
      throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    if (null != obj) {
      if (null != obj.getAuditStatus()) {
        where.eq("audit_status", obj.getAuditStatus());
      } else {
        where.eq("audit_status", 1);
      }
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        where.andNew();
        String searchKey = obj.getSearchKey();
        where.like("address", searchKey);
        where.or("meeting_title like {0}", "%" + searchKey + "%");
      }
      if (StringUtils.isNotEmpty(obj.getMeetingHost())) {
        where.andNew();
        where.like("meeting_host", obj.getMeetingHost());
      }
      if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
        where.andNew();
        where.like("meeting_title", obj.getMeetingTitle());
      }
      if (StringUtils.isNotEmpty(obj.getAddress())) {
        where.andNew();
        where.like("address", obj.getAddress());
      }
      if (null != obj.getMeetingSituation()) {
        where.andNew();
        where.eq("meeting_situation", obj.getMeetingSituation());
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        // where.ge("create_time",obj.getStartDate());
        where.andNew();
        where.ge("begin_time", DateUtil.str2datetime(obj.getStartDate() + " 00:00:00"));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        // where.le("create_time",obj.getEndDate());
        where.andNew();
        where.le("end_time", DateUtil.str2datetime(obj.getEndDate() + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        // where.between("create_time",obj.getStartDate(),obj.getEndDate());
        where.andNew();
        where.between("begin_time", DateUtil.str2datetime(obj.getStartDate() + " 00:00:00"),
            DateUtil.str2datetime(obj.getEndDate() + " 23:59:59"));
        where.or("end_time between {0} and {1}", DateUtil.str2datetime(obj.getStartDate() + " 00:00:00"),
            DateUtil.str2datetime(obj.getEndDate() + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        // where.between("create_time",
        // DateUtil.getFirstDateOfWeek(PATTERN_BAR_YYYYMMDD),DateUtil.getLastDateOfWeek(PATTERN_BAR_YYYYMMDD));
        where.andNew();
        where.between("begin_time",
            DateUtil.str2datetime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2datetime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        // where.between("create_time",
        // DateUtil.getFirstDateOfMonth(PATTERN_BAR_YYYYMMDD),DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD));
        where.andNew();
        where.between("begin_time",
            DateUtil.str2datetime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2datetime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2datetime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
    }
    where.orderBy("begin_time", false);
    Page<MeeOutsideMeeting> oldPage = selectPage(
        new Page<MeeOutsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<MeeOutsideMeetingDO> page = new Page<MeeOutsideMeetingDO>();
    List<MeeOutsideMeeting> meeOutsideMeetingList = oldPage.getRecords();
    List<MeeOutsideMeetingDO> meeOutsideMeetingDOList = new ArrayList<MeeOutsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeOutsideMeetingList && !meeOutsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
        MeeOutsideMeetingDO meeOutsideMeetingDO = new MeeOutsideMeetingDO();
        meeOutsideMeetingDO.setNo(i);
        meeOutsideMeetingDO.setId(meeOutsideMeeting.getId());
        meeOutsideMeetingDO.setMeetingTitle(meeOutsideMeeting.getMeetingTitle());
        DateTime begTime = new DateTime(meeOutsideMeeting.getBeginTime());
        meeOutsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeOutsideMeeting.getEndTime());
        meeOutsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeOutsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        Integer autStatus = meeOutsideMeeting.getAuditStatus();
        String auditStatus = "";
        if (1 == autStatus) {
          auditStatus = "审核中";
        } else if (2 == autStatus) {
          auditStatus = "审核失败";
        } else if (3 == autStatus) {
          auditStatus = "审核通过";
        }
        meeOutsideMeetingDO.setMeetingSituation(situation);
        meeOutsideMeetingDO.setAuditStatus(auditStatus);
        meeOutsideMeetingDO.setAddress(meeOutsideMeeting.getAddress());
        meeOutsideMeetingDO.setCreateUserId(meeOutsideMeeting.getCreateUserId());
        meeOutsideMeetingDOList.add(meeOutsideMeetingDO);
        if (!userIdList.contains(meeOutsideMeetingDO.getCreateUserId())) {
          userIdList.add(meeOutsideMeetingDO.getCreateUserId());
        }
        i++;
      }
    }
    if (null != meeOutsideMeetingDOList && !meeOutsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.setSqlSelect("id,full_name");
      userWhere.eq("is_active", 1);
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeOutsideMeetingDO meeOutsideMeetingDO : meeOutsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeOutsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeOutsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeOutsideMeetingDO.getCreateUserId()) && user.getId()
                      .intValue() == meeOutsideMeetingDO.getCreateUserId().intValue()) {
                    meeOutsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeOutsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public Page<MeeOutsideMeetingDO> auditPassList(PageBean pageBean, MeeOutsideMeetingVO obj)
      throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getMeetingTitle())) {
        where.like("meeting_title", obj.getMeetingTitle());
      }
      if (StringUtils.isNotEmpty(obj.getAddress())) {
        where.like("address", obj.getAddress());
      }
      if (null != obj.getMeetingSituation()) {
        where.eq("meeting_situation", obj.getMeetingSituation());
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isEmpty(obj.getEndDate())) {
        where.ge("create_time", DateUtil.str2dateOrTime(obj.getStartDate()));
      }
      if (StringUtils.isEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.le("create_time", DateUtil.str2dateOrTime(obj.getEndDate()));
      }
      if (StringUtils.isNotEmpty(obj.getStartDate()) && StringUtils.isNotEmpty(obj.getEndDate())) {
        where.between("create_time", DateUtil.str2dateOrTime(obj.getStartDate()), DateUtil.str2dateOrTime(obj.getEndDate()));
      }
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        where.between("create_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        where.between("create_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
    }
    if (null != obj.getAuditStatus()) {
      where.eq("audit_status", obj.getAuditStatus());
    } else {
      where.eq("audit_status", 1);
    }
    where.orderBy("create_time", false);
    Page<MeeOutsideMeeting> oldPage = selectPage(
        new Page<MeeOutsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<MeeOutsideMeetingDO> page = new Page<MeeOutsideMeetingDO>();
    List<MeeOutsideMeeting> meeOutsideMeetingList = oldPage.getRecords();
    List<MeeOutsideMeetingDO> meeOutsideMeetingDOList = new ArrayList<MeeOutsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeOutsideMeetingList && !meeOutsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
        MeeOutsideMeetingDO meeOutsideMeetingDO = new MeeOutsideMeetingDO();
        meeOutsideMeetingDO.setNo(i);
        meeOutsideMeetingDO.setId(meeOutsideMeeting.getId());
        meeOutsideMeetingDO.setMeetingTitle(meeOutsideMeeting.getMeetingTitle());
        DateTime begTime = new DateTime(meeOutsideMeeting.getBeginTime());
        meeOutsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeOutsideMeeting.getEndTime());
        meeOutsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeOutsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        Integer autStatus = meeOutsideMeeting.getAuditStatus();
        String auditStatus = "";
        if (1 == autStatus) {
          auditStatus = "审核中";
        } else if (2 == autStatus) {
          auditStatus = "审核失败";
        } else if (3 == autStatus) {
          auditStatus = "审核通过";
        }
        meeOutsideMeetingDO.setMeetingSituation(situation);
        meeOutsideMeetingDO.setAuditStatus(auditStatus);
        meeOutsideMeetingDO.setAddress(meeOutsideMeeting.getAddress());
        meeOutsideMeetingDO.setCreateUserId(meeOutsideMeeting.getCreateUserId());
        meeOutsideMeetingDOList.add(meeOutsideMeetingDO);
        userIdList.add(meeOutsideMeeting.getCreateUserId());
        i++;
      }
    }
    if (null != meeOutsideMeetingDOList && !meeOutsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("is_active", 1);
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeOutsideMeetingDO meeOutsideMeetingDO : meeOutsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeOutsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeOutsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeOutsideMeetingDO.getCreateUserId()) && user.getId()
                      .intValue() == meeOutsideMeetingDO.getCreateUserId().intValue()) {
                    meeOutsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeOutsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public RetMsg cancel(MeeOutsideMeetingVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      MeeOutsideMeeting meeOutsideMeeting = selectById(obj.getId());
      if (null != meeOutsideMeeting) {
        meeOutsideMeeting.setMeetingSituation(3);
        updateById(meeOutsideMeeting);

      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public void autoComplete(MeeOutsideMeetingVO obj) throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    where.eq("meeting_situation", 1);
    DateTime dateTime = new DateTime(new Date());
    where.andNew("end_time < {0}", dateTime.toString("yyyy-MM-dd HH:mm"));
    where.setSqlSelect("id,meeting_situation");
    List<MeeOutsideMeeting> outsideMeetingList = selectList(where);
    if (null != outsideMeetingList && !outsideMeetingList.isEmpty()) {
      for (MeeOutsideMeeting outsideMeeting : outsideMeetingList) {
        if (null != outsideMeeting) {
          outsideMeeting.setMeetingSituation(2);
        }

      }
      updateBatchById(outsideMeetingList);
    }
  }

  @Override
  public ActAljoinBpmn submitProcess() throws Exception {
    Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
    where.eq("process_id", "Process_YzL2tlND2E");
    where.eq("is_deploy", 1);
    where.eq("is_active", 1);
    where.setSqlSelect("id,category_id,process_id,process_name");
    ActAljoinBpmn bpmn = actAljoinBpmnService.selectOne(where);
    if (null != bpmn && null != bpmn.getProcessId()) {
      // 启动流程
      @SuppressWarnings("unused")
      ProcessInstance instance = activitiService.startBpmn(bpmn, null, null);
    }
    return bpmn;
  }

  @SuppressWarnings("unchecked")
  @Override
  public MeeMeetingRoomCountDO leaderList(MeeInsideMeetingVO obj) throws Exception {
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    MeeMeetingRoomCountDO meetingRoomCountDO = null;
    if (null != obj) {
      meetingRoomCountDO = new MeeMeetingRoomCountDO();
      if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
        where.like("meeting_room_name", obj.getMeetingRoomName());
      }
      if (null != obj.getBeginTime() && null == obj.getBeginTime()) {
        where.ge("begin_time", obj.getBeginTime());
      }
      if (null == obj.getBeginTime() && null != obj.getEndTime()) {
        where.le("end_time", obj.getEndTime());
      }
      if (null != obj.getBeginTime() && null != obj.getEndTime()) {
        where.between("end_time", obj.getBeginTime(), obj.getEndTime());
      }
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        where.between("create_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        where.between("create_time", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }

      where.orderBy("create_time", false);
      if (null != obj.getCreateUserId()) {
        Where<AutDepartmentUser> where1 = new Where<AutDepartmentUser>();
        where1.eq("user_id", obj.getCreateUserId());
        List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(where1);
        Long deptId = null;
        if (null != departmentUserList && !departmentUserList.isEmpty()) {
          for (AutDepartmentUser departmentUser : departmentUserList) {
            if (null != departmentUser && null != departmentUser.getIsLeader()) {
              if (departmentUser.getIsLeader() == 1) {
                deptId = departmentUser.getDeptId();
              }
            }
          }
          if (null != deptId) {
            Where<AutDepartmentUser> where2 = new Where<AutDepartmentUser>();
            where2.eq("dept_id", deptId);
            List<AutDepartmentUser> departmentUserList1 =
                autDepartmentUserService.selectList(where2);
            if (null != departmentUserList1 && !departmentUserList1.isEmpty()) {
              List<Long> idList = new ArrayList<Long>();
              for (AutDepartmentUser departmentUser : departmentUserList1) {
                if (null != departmentUser && null != departmentUser.getUserId()) {
                  idList.add(departmentUser.getUserId());
                }
              }
              if (null != idList && !idList.isEmpty()) {
                where.in("create_user_id", idList);
              }
            }

          }
        }
      }
      List<MeeInsideMeeting> meeInsideMeetingList = meeInsideMeetingService.selectList(where);

      List<Map<Object, Object>> mapList = new ArrayList<Map<Object, Object>>();
      Map<String, Object> map = new HashMap<String, Object>();
      Map<Object, Object> mp = new HashMap<Object, Object>();
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
          List<MeeInsideMeeting> insideMeetingList = new ArrayList<MeeInsideMeeting>();
          for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
            String weekDay = DateUtil.getWeek(meeInsideMeeting.getCreateTime());
            if ("星期日".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            } else if ("星期一".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            } else if ("星期二".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            } else if ("星期三".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            } else if ("星期四".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            } else if ("星期五".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            } else if ("星期六".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
            }
            map.put(weekDay, insideMeetingList);
            mp.put(meeInsideMeeting.getMeetingRoomId(), map);
          }
        }

        if (null != mp && !mp.isEmpty()) {
          for (Object k : mp.keySet()) {
            Map<String, Object> mpp = new HashMap<String, Object>();
            if (null != map && !map.isEmpty()) {
              for (String key : map.keySet()) {
                List<MeeInsideMeeting> insideList = (List<MeeInsideMeeting>) map.get(key);
                List<MeeInsideMeeting> meetingList = new ArrayList<MeeInsideMeeting>();
                for (MeeInsideMeeting insideMeeting : insideList) {
                  String weekDay = DateUtil.getWeek(insideMeeting.getCreateTime());
                  Long roomId = (Long) k;
                  if ((roomId.equals(insideMeeting.getMeetingRoomId())
                      && roomId.intValue() == insideMeeting.getMeetingRoomId().intValue())
                      && key.equals(weekDay)) {
                    meetingList.add(insideMeeting);
                    mpp.put(weekDay, meetingList);
                  }
                }
              }
            }
            mp.put(k, mpp);
          }
          mapList.add(mp);
        }
        if (null != mapList && !mapList.isEmpty()) {
          meetingRoomCountDO.setMapList(mapList);
        }
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
          List<MeeInsideMeeting> insideMeetingList = new ArrayList<MeeInsideMeeting>();
          for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
            // int monthDay = DateUtil.getDaysOfMonth();
            int day = DateUtil.getDaysOfMonth(meeInsideMeeting.getCreateTime());
            // for(int i = 0;i<monthDay;i++){
            insideMeetingList.add(meeInsideMeeting);
            // }
            map.put(day + "", insideMeetingList);
            mp.put(meeInsideMeeting.getMeetingRoomId(), map);
          }
        }

        if (null != mp && !mp.isEmpty()) {
          for (Object k : mp.keySet()) {
            Map<String, Object> mpp = new HashMap<String, Object>();
            if (null != map && !map.isEmpty()) {
              for (String key : map.keySet()) {
                List<MeeInsideMeeting> insideList = (List<MeeInsideMeeting>) map.get(key);
                List<MeeInsideMeeting> meetingList = new ArrayList<MeeInsideMeeting>();
                for (MeeInsideMeeting insideMeeting : insideList) {
                  int monthDay = DateUtil.getDaysOfMonth(insideMeeting.getCreateTime());
                  Long roomId = (Long) k;
                  if ((roomId.equals(insideMeeting.getMeetingRoomId())
                      && roomId.intValue() == insideMeeting.getMeetingRoomId().intValue())
                      && key.equals(monthDay + "")) {
                    meetingList.add(insideMeeting);
                    mpp.put(monthDay + "", meetingList);
                  }
                }
              }
            }
            mp.put(k, mpp);
          }
          mapList.add(mp);
        }
        if (null != mapList && !mapList.isEmpty()) {
          meetingRoomCountDO.setMapList(mapList);
        }
      }

      Where<MeeMeetingRoom> roomWhere = new Where<MeeMeetingRoom>();
      List<MeeMeetingRoom> roomList = meeMeetingRoomService.selectList(roomWhere);
      if (null != roomList && !roomList.isEmpty()) {
        meetingRoomCountDO.setRoomList(roomList);
      }
    }
    return meetingRoomCountDO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processInstanceId)
      throws Exception {
    // String processName = "外部会议";
    // 取流程名称
    // Where<ActAljoinFixedConfig> configWhere = new
    // Where<ActAljoinFixedConfig>();
    // configWhere.eq("process_code",
    // WebConstant.FIXED_FORM_PROCESS_OUTSIDE_MEETING);
    // configWhere.setSqlSelect("id,process_code,process_id,process_name,is_active");
    // ActAljoinFixedConfig config =
    // actAljoinFixedConfigService.selectOne(configWhere);
    // if (null != config && null != config.getProcessId()) {
    // Where<ActAljoinBpmn> where = new Where<ActAljoinBpmn>();
    // where.eq("process_id", config.getProcessId());
    // where.eq("is_deploy", 1);
    // where.eq("is_active", 1);
    // where.setSqlSelect("id,category_id,process_id,process_name");
    // ActAljoinBpmn bpmn = actAljoinBpmnService.selectOne(where);
    // if (null != bpmn) {
    // processName = bpmn.getProcessName();
    // }
    // }

    Map<String, Object> map = new HashMap<String, Object>();
    if (StringUtils.isEmpty(processInstanceId)) {
      map = actFixedFormService.getLog(taskId, null);
    } else {
      map = actFixedFormService.getLog(taskId, processInstanceId);
    }
    List<FixedFormProcessLog> logList = null;
    if (null != map && !map.isEmpty()) {
      List<AutUser> assigneeList = new ArrayList<AutUser>();
      List<AutUser> recevieList = new ArrayList<AutUser>();
      if (null != map.get("assigneeIdList")) {
        List<Long> assigneeIdList = (List<Long>) map.get("assigneeIdList");
        if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
          Where<AutUser> assigneeWhere = new Where<AutUser>();
          assigneeWhere.in("id", new HashSet<Long>(assigneeIdList));
          assigneeWhere.setSqlSelect("id,user_name,full_name");
          assigneeList = autUserService.selectList(assigneeWhere);
        }
      }
      if (null != map.get("recevieUserIdList")) {
        List<Long> recevieUserIdList = (List<Long>) map.get("recevieUserIdList");
        if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
          Where<AutUser> recevieWhere = new Where<AutUser>();
          recevieWhere.in("id", recevieUserIdList);
          recevieWhere.setSqlSelect("id,user_name,full_name");
          recevieList = autUserService.selectList(recevieWhere);
        }
      }

      if (null != map.get("logList")) {
        logList = (List<FixedFormProcessLog>) map.get("logList");
        if (null != logList && !logList.isEmpty() && null != assigneeList
            && !assigneeList.isEmpty()) {
          for (FixedFormProcessLog log : logList) {
            for (AutUser user : assigneeList) {
              if (null != user && null != user.getId() && null != log
                  && null != log.getOperationId()) {
                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                  log.setOperationName(user.getFullName());
                }
              }
            }
          }
        }
        if (null != logList && !logList.isEmpty() && null != recevieList
            && !recevieList.isEmpty()) {
          for (FixedFormProcessLog log : logList) {
            String recUserName = "";
            for (AutUser user : recevieList) {
              if (null != user && null != user.getId() && null != log
                  && null != log.getRecevieUserId()) {
                if (log.getRecevieUserId().contains(String.valueOf(user.getId()))) {
                  recUserName += user.getFullName() + ";";

                }
              }
            }
            log.setRecevieUserName(recUserName);
          }
        }
      }
    }
    return logList;
  }



  @Override
  public void autoComplete() throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    where.eq("meeting_situation", 1);
    where.eq("audit_status", 3);
    DateTime dateTime = new DateTime();
    String date = dateTime.toString("yyyy-MM-dd");
    where.andNew().le("end_time", DateUtil.str2date(date));
    where.setSqlSelect(
        "id,version,meeting_situation,end_time,create_time,is_delete,create_user_id,create_user_name,audit_status,last_update_time,last_update_user_id,last_update_user_name,last_update_time,last_update_user_id,last_update_user_name");
    List<MeeOutsideMeeting> outsideMeetingList = selectList(where);
    if (null != outsideMeetingList && !outsideMeetingList.isEmpty()) {
      for (MeeOutsideMeeting meeInsideMeeting : outsideMeetingList) {
        meeInsideMeeting.setMeetingSituation(2);
      }
      updateBatchById(outsideMeetingList);
    }
  }

  @Override
  public RetMsg checkNextTaskInfo(String taskId) throws Exception {
    RetMsg retMsg = new RetMsg();
    String processInstanceId =
        taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
    List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
    boolean isOrgn = false;
    String openType = "";
    Map<String, Object> map = new HashMap<String, Object>();
    if (null != list && !list.isEmpty()) {
      String assigneeId = "";
      String assigneeUserId = "";
      String assigneeGroupId = "";
      TaskDefinition definition = list.get(0);

      if (null != definition) {
        if (definition.getAssigneeExpression() == null
            && (definition.getCandidateGroupIdExpressions() == null
                || definition.getCandidateGroupIdExpressions().size() == 0)
            && (definition.getCandidateUserIdExpressions() == null
                || definition.getCandidateUserIdExpressions().size() == 0)) {
          // 选项为空时弹出整棵组织机构树
          openType = "1";
          isOrgn = true;
        }
        Set<String> uIds = new HashSet<String>(); // 所有候选用户id
        List<AutUser> uList = new ArrayList<AutUser>(); // 所有候选用户
        if (null != definition.getAssigneeExpression()) {
          assigneeId = String.valueOf(definition.getAssigneeExpression());
          // 受理人
          List<String> assineedIdList = new ArrayList<String>();
          if (assigneeId.endsWith(";")) {
            assineedIdList = Arrays.asList(assigneeId.split(";"));
          } else {
            assineedIdList.add(assigneeId);
          }
//          Where<AutUser> userWhere = new Where<AutUser>();
//          userWhere.in("id", assineedIdList);
//          userWhere.setSqlSelect("id,user_name,full_name");
//          List<AutUser> assigneedList = autUserService.selectList(userWhere);
          // 受理人不为空-就只返回受理人
          if (null != assineedIdList && !assineedIdList.isEmpty()) {
            uIds.addAll(assineedIdList);
          }
        }
          // 候选用户列表assineedUserIdList
          if (null != definition.getCandidateUserIdExpressions()) {
            Iterator<Expression> it = definition.getCandidateUserIdExpressions().iterator();
            while (it.hasNext()) {
              assigneeUserId += String.valueOf(it.next()) + ";";
            }
          }
          List<String> assineedUserIdList = new ArrayList<String>();
          if (assigneeUserId.endsWith(";")) {
            assineedUserIdList = Arrays.asList(assigneeUserId.split(";"));
          } else {
            assineedUserIdList.add(assigneeUserId);
          }
          Where<AutUser> assigneedUserWhere = new Where<AutUser>();
          assigneedUserWhere.in("id", assineedUserIdList);
          assigneedUserWhere.setSqlSelect("id,user_name,full_name");
          uIds.addAll(assineedUserIdList);

          // 候选组
          if (null != definition.getCandidateGroupIdExpressions()) {
            Iterator<Expression> it = definition.getCandidateGroupIdExpressions().iterator();
            while (it.hasNext()) {
              assigneeGroupId += String.valueOf(it.next()) + ";";
            }
          }
          List<String> assineedGroupIdList = new ArrayList<String>();
          if (assigneeGroupId.endsWith(";")) {
            assineedGroupIdList = Arrays.asList(assigneeGroupId.split(";"));
          } else {
            assineedGroupIdList.add(assigneeGroupId);
          }

          // 候选岗位
          // List<Long> uIdList = new ArrayList<Long>();
          Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
          positionWhere.in("position_id", assineedGroupIdList);
          positionWhere.setSqlSelect("id,position_id,user_id");
          List<AutUserPosition> positionList = autUserPositionService.selectList(positionWhere);
          if (null != positionList && !positionList.isEmpty()) {
            for (AutUserPosition userPosition : positionList) {
              if (null != userPosition && null != userPosition.getUserId()) {
                uIds.add(String.valueOf(userPosition.getUserId()));
              }
            }
          }

          // 候选岗位
          Where<AutUserRole> roleWhere = new Where<AutUserRole>();
          roleWhere.in("role_id", assineedGroupIdList);
          roleWhere.setSqlSelect("id,role_id,user_id");
          List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);

          if (null != roleList && !roleList.isEmpty()) {
            for (AutUserRole userRole : roleList) {
              if (null != userRole && null != userRole.getUserId()) {
                uIds.add(String.valueOf(userRole.getUserId()));
              }
            }
          }
          if (null != uIds && !uIds.isEmpty()) {
            Where<AutUser> where = new Where<AutUser>();
            where.in("id", uIds);
            where.setSqlSelect("id,user_name,full_name");
            uList = autUserService.selectList(where);
          }
          map.put("user", uList);
          if(uList.size() == 0){
            // 只选择了部门，把部门id返回去
            isOrgn = true;
            openType = "3";
            map.put("assineedGroupIdList", assineedGroupIdList);
        } else {
          // 选择部门
          Where<AutDepartmentUser> departmentWhere = new Where<AutDepartmentUser>();
          departmentWhere.in("dept_id", assineedGroupIdList);
          departmentWhere.setSqlSelect("dept_id,id,dept_code,user_id");
          List<AutDepartmentUser> departmentList =
              autDepartmentUserService.selectList(departmentWhere);
          List<AutDepartmentUserVO> departmentUserList = new ArrayList<AutDepartmentUserVO>();
          if (null != departmentList && !departmentList.isEmpty()) {
            openType = "3";
            isOrgn = true;
            List<String> deptIdlist = new ArrayList<String>();
            List<Long> deptUIdList = new ArrayList<Long>();
            List<AutUser> deptUserList = new ArrayList<AutUser>();
            for (AutDepartmentUser department : departmentList) {
              if (null != department && null != department.getUserId()) {
                deptIdlist.add(String.valueOf(department.getId()));
                deptUIdList.add(department.getUserId());
                uIds.add(String.valueOf(department.getUserId()));
              }
            }
            if (null != deptUIdList && !deptUIdList.isEmpty()) {
              Where<AutUser> where = new Where<AutUser>();
              where.in("id", deptUIdList);
              where.setSqlSelect("id,user_name,full_name");
              deptUserList = autUserService.selectList(where);
              uList.addAll(deptUserList);
            }
            List<AutDepartmentUser> deptUser = new ArrayList<AutDepartmentUser>();
            if (null != uIds && !uIds.isEmpty()) {
              Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
              where.setSqlSelect("dept_id,id,dept_code,user_id");
              where.in("user_id", uIds);
              deptUser = autDepartmentUserService.selectList(where);
            }
            if (null != deptUser && !deptUser.isEmpty()) {
              for (AutDepartmentUser departmentUser : deptUser) {
                AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                BeanUtils.copyProperties(departmentUser, departmentUserVO);
                departmentUserList.add(departmentUserVO);
              }
            }
            if (null != deptUserList && !deptUserList.isEmpty()) {
              for (AutUser user : uList) {
                for (AutDepartmentUser departmentUser : departmentList) {
                  if (null != user && null != departmentUser && null != user.getId()
                      && null != departmentUser.getUserId()) {
                    if (user.getId().equals(departmentUser.getUserId())
                        && user.getId().intValue() == departmentUser.getUserId().intValue()) {
                      AutDepartmentUserVO departmentUserVO = new AutDepartmentUserVO();
                      BeanUtils.copyProperties(departmentUser, departmentUserVO);
                      departmentUserVO.setFullName(user.getFullName());
                      departmentUserList.add(departmentUserVO);
                    }
                  }
                }
              }
            }
            if (null != departmentUserList && !departmentUserList.isEmpty()) {
              for (int i = 0; i <= departmentUserList.size() - 1; i++) {
                for (int j = departmentUserList.size() - 1; j > i; j--) {
                  AutDepartmentUserVO autDepartmentUserVO = departmentUserList.get(i);
                  AutDepartmentUserVO autDepartmentUserVO2 = departmentUserList.get(j);
                  if (null != autDepartmentUserVO && null != autDepartmentUserVO2
                      && null != autDepartmentUserVO.getUserId()
                      && null != autDepartmentUserVO2.getUserId()) {
                    if (autDepartmentUserVO.getUserId().equals(autDepartmentUserVO2.getUserId())
                        && autDepartmentUserVO.getUserId().intValue() == autDepartmentUserVO2
                            .getUserId().intValue()) {
                      departmentUserList.remove(i);
                    }
                  }
                }
              }
            }
            map.put("dept", departmentUserList);
          }
        }
      }
    }
    // 判断当前节点是否可回退
    List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
    String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
    List<TaskDefinition> preList =
        activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
    if (preList == null || preList.isEmpty()) {
      map.put("isNotBack", true);
    } else {
      map.put("isNotBack", false);
    }
    map.put("isOrgn", isOrgn);
    map.put("openType", openType);
    retMsg.setCode(0);
    retMsg.setObject(map);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public Page<MeeOutsideMeetingDO> leaderPersonList(PageBean pageBean, MeeLeaderMeetingDO obj)
      throws Exception {
    Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
    if (null != obj) {

      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        where.and();
        where.between("begin_time",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        where.and();
        where.between("begin_time",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
            DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
        where.or("end_time between {0} and {1}",
            DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 00:00:00"),
                DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD) + " 23:59:59"));
      }
    }
    where.and();
    where.like("party_memebers_id", String.valueOf(obj.getId()));
    where.orderBy("create_time", false);
    Page<MeeOutsideMeeting> oldPage = selectPage(
        new Page<MeeOutsideMeeting>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<MeeOutsideMeetingDO> page = new Page<MeeOutsideMeetingDO>();
    List<MeeOutsideMeeting> meeOutsideMeetingList = oldPage.getRecords();
    List<MeeOutsideMeetingDO> meeOutsideMeetingDOList = new ArrayList<MeeOutsideMeetingDO>();
    List<Long> userIdList = new ArrayList<Long>();
    if (null != meeOutsideMeetingList && !meeOutsideMeetingList.isEmpty()) {
      int i = 1;
      for (MeeOutsideMeeting meeOutsideMeeting : meeOutsideMeetingList) {
        MeeOutsideMeetingDO meeOutsideMeetingDO = new MeeOutsideMeetingDO();
        meeOutsideMeetingDO.setNo(i);
        meeOutsideMeetingDO.setId(meeOutsideMeeting.getId());
        meeOutsideMeetingDO.setMeetingTitle(meeOutsideMeeting.getMeetingTitle());
        DateTime begTime = new DateTime(meeOutsideMeeting.getBeginTime());
        meeOutsideMeetingDO.setBeginDate(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        DateTime endTime = new DateTime(meeOutsideMeeting.getEndTime());
        meeOutsideMeetingDO.setEndDate(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMM));
        Integer meetingSituation = meeOutsideMeeting.getMeetingSituation();
        String situation = "";
        if (1 == meetingSituation) {
          situation = "未完成";
        } else if (2 == meetingSituation) {
          situation = "已完成";
        } else if (3 == meetingSituation) {
          situation = "已取消";
        }
        Integer autStatus = meeOutsideMeeting.getAuditStatus();
        String auditStatus = "";
        if (1 == autStatus) {
          auditStatus = "审核中";
        } else if (2 == autStatus) {
          auditStatus = "审核失败";
        } else if (3 == autStatus) {
          auditStatus = "审核通过";
        }
        meeOutsideMeetingDO.setMeetingSituation(situation);
        meeOutsideMeetingDO.setAuditStatus(auditStatus);
        meeOutsideMeetingDO.setAddress(meeOutsideMeeting.getAddress());
        meeOutsideMeetingDO.setCreateUserId(meeOutsideMeeting.getCreateUserId());
        meeOutsideMeetingDOList.add(meeOutsideMeetingDO);
        userIdList.add(meeOutsideMeeting.getCreateUserId());
        i++;
      }
    }
    if (null != meeOutsideMeetingDOList && !meeOutsideMeetingDOList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("is_active", 1);
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (MeeOutsideMeetingDO meeOutsideMeetingDO : meeOutsideMeetingDOList) {
          for (AutUser user : userList) {
            if (null != user && null != meeOutsideMeetingDO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != meeOutsideMeetingDO.getCreateUserId())) {
                  if (user.getId().equals(meeOutsideMeetingDO.getCreateUserId()) && user.getId()
                      .intValue() == meeOutsideMeetingDO.getCreateUserId().intValue()) {
                    meeOutsideMeetingDO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }
    page.setRecords(meeOutsideMeetingDOList);
    page.setTotal(oldPage.getTotal());
    page.setSize(oldPage.getSize());
    page.setCurrent(oldPage.getCurrent());
    return page;
  }

  @Override
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (StringUtils.isNotEmpty(taskId)) {
      String processInstanceId =
          taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
      // runtimeService.deleteProcessInstance(processInstanceId,
      // "外部会议流程作废");
      runtimeService.deleteProcessInstance(processInstanceId, null);
      Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
      queryWhere.eq("process_instance_id", processInstanceId);
      actAljoinQueryService.delete(queryWhere);
      Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
      queryHisWhere.eq("process_instance_id", processInstanceId);
      actAljoinQueryHisService.delete(queryHisWhere);
      Date date = new Date();
      if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
        MeeOutsideMeeting meeOutsideMeeting = selectById(Long.parseLong(bizId));
        if (null != meeOutsideMeeting) {
          meeOutsideMeeting.setAuditStatus(2);// 审核通过
          meeOutsideMeeting.setAuditTime(date);
          meeOutsideMeeting.setAuditReason("流程作废，审核失败");
          updateById(meeOutsideMeeting);
        }
      }
    }
    // 环节办理人待办数量-1
    AutDataStatistics aut = new AutDataStatistics();
    aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
    aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
    aut.setBusinessKey(String.valueOf(userId));
    autDataStatisticsService.minus(aut);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public void pushOutsideMeeting() throws Exception {
    Date tomorrow = DateUtil.getAfter(new Date(), DateUtil.UNIT_DAY, 1);
    if (null != tomorrow) {
      String timeBegin = DateUtil.date2str(tomorrow) + " 00:00:00";
      String timeEnd = DateUtil.date2str(tomorrow) + " 23:59:59";
      Where<MeeOutsideMeeting> where = new Where<MeeOutsideMeeting>();
      where.setSqlSelect(
          "id,meeting_title,address,party_memebers_id,party_memeber_names,meeting_situation,begin_time,end_time,create_time,is_delete,create_user_id,create_user_name,audit_status,is_warn_msg,is_warn_mail,is_warn_online");
      where.eq("meeting_situation", 1);
      where.ge("begin_time", DateUtil.str2datetime(timeBegin));
      where.eq("audit_status", 3);
      where.le("begin_time", DateUtil.str2datetime(timeEnd));
      where.andNew("is_warn_msg = {0}", 1).or("is_warn_online = {0}", 1).or("is_warn_mail = {0}",
          1);
      // where.and();
      // where.eq("is_warn_msg", 1);
      // where.or("is_warn_online = {0}", 1);
      // where.or("is_warn_mail = {0}", 1);
      List<MeeOutsideMeeting> meetList = selectList(where);
      String partyMemberNames = "";
      String partyMemberUserName = "";
      if (meetList != null && !meetList.isEmpty()) {
        Where<AutUser> userwhere = new Where<AutUser>();
        userwhere.eq("user_name", WebConstant.SADMIN);
        AutUser user = autUserService.selectOne(userwhere);
        Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
        departmentUserWhere.eq("user_id", user.getId());
        departmentUserWhere.setSqlSelect("id,is_leader");
        List<AutDepartmentUser> departmentUserList =
            autDepartmentUserService.selectList(departmentUserWhere);
        Where<MaiSendBox> maiSendBoxWhere = new Where<MaiSendBox>();
        maiSendBoxWhere.eq("send_user_id", user.getId());
        maiSendBoxWhere.setSqlSelect("id,send_user_id,mail_size");
        List<MaiSendBox> maiSendBoxList = maiSendBoxService.selectList(maiSendBoxWhere);
        for (MeeOutsideMeeting mee : meetList) {
          AutUser autuser = autUserService.selectById(mee.getCreateUserId());
          String[] receivePersons = mee.getPartyMemebersId().split(";");
          String receivePerson = "";
          for (int i = 0; i < receivePersons.length; i++) {
            receivePerson += receivePersons[i] + ",";
          }
          Where<AutUser> userWhere = new Where<AutUser>();
          userWhere.setSqlSelect("id,user_name,full_name");
          userWhere.in("id", receivePerson);
          List<AutUser> userlist = autUserService.selectList(userWhere);
          partyMemberNames = "";
          partyMemberUserName = "";
          for (AutUser user1 : userlist) {
            if (mee.getPartyMemebersId().contains(autuser.getId().toString())) {
              partyMemberNames += user1.getUserName() + ";";
              partyMemberUserName += user1.getFullName() + ";";
            }
          }
          if (mee.getIsWarnMail() != 0) {
            mailWarn4Push(mee, autuser, MEET_NOTICE, partyMemberNames, partyMemberUserName,
                departmentUserList, maiSendBoxList);
          }
          if (mee.getIsWarnMsg() != 0) {
            SmsShortMessage smsShortMessage = smaWarn(mee, autuser, MEET_NOTICE);
            smsShortMessageService.add(smsShortMessage, autuser);
          }
          if (mee.getIsWarnOnline() != 0) {
            AutMsgOnlineRequestVO requestVO = onlineWarn(mee, autuser, MEET_NOTICE);
            autMsgOnlineService.pushMessageToUserList(requestVO);
          }
        }
      }
    }
  }

  @Override
  public AutMsgOnlineRequestVO onlineWarn(MeeOutsideMeeting obj, AutUser createuser, String type)
      throws Exception {
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", "");
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();

    AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
    if (type.equals(MEET_ADD)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }
    if (null != templateList && !templateList.isEmpty()) {
      requestVO.setMsgContent(templateList.get(0));
    }
    // 封装推送信息请求信息
    requestVO.setFromUserId(obj.getCreateUserId());
    requestVO.setFromUserFullName(createuser.getFullName());
    requestVO.setFromUserName(obj.getCreateUserName());
    // 会议详情-页面跳转
    requestVO.setGoUrl(
        "mee/meeOutsideMeeting/meeOutsideManagePageDetail?id=" + obj.getId() + "&isDraft=0");
    requestVO.setMsgType(WebConstant.ONLINE_MSG_MEETING);
    // 前端传来的1个或多个userId,截取存进List
    List<String> receiveUserIdList = Arrays.asList(obj.getPartyMemebersId().split(";"));
    requestVO.setToUserId(receiveUserIdList);
    // 调用推送在线消息接口
    // autMsgOnlineService.pushMessageToUserList(requestVO);
    return requestVO;
  }

  @Override
  public MaiWriteVO mailWarn(MeeOutsideMeeting obj, AutUser createuser, String type)
      throws Exception {
    String partyMemberUserName = "";
    // 调用消息模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", "");
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();

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
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }
    // maiReceiveBox.setSubjectText(obj.getMeetingTitle());
    maiReceiveBox.setMailContent(obj.getMeetingContent());
    if (null != templateList && !templateList.isEmpty()) {
        maiReceiveBoxSearch.setSubjectText(templateList.get(1));
      maiReceiveBox.setMailContent(templateList.get(0));
    }
    maiReceiveBox.setSendId(obj.getCreateUserId());
    maiReceiveBox.setSendUserName(createuser.getUserName());
    maiReceiveBoxSearch.setSendFullName(createuser.getFullName());
    mai.setMaiReceiveBox(maiReceiveBox);
    mai.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
    // maiSendBoxService.add(mai, createuser);
    return mai;
  }

  @Override
  public MeeOutsideMeetingVO getDetail4App(MeeOutsideMeeting obj) throws Exception {
    MeeOutsideMeetingVO meeOutsideMeetingVO = null;
    if (null != obj && null != obj.getId()) {
      MeeOutsideMeeting meeOutsideMeeting = selectById(obj.getId());
      if (null != meeOutsideMeeting) {
        meeOutsideMeetingVO = new MeeOutsideMeetingVO();
        BeanUtils.copyProperties(meeOutsideMeeting, meeOutsideMeetingVO);
        
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", meeOutsideMeeting.getId());
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
          meeOutsideMeetingVO.setResResourceList(resourceList);
        /*Where<ResAttachment> where = new Where<ResAttachment>();
        where.eq("resource_id", meeOutsideMeeting.getId());
        List<ResAttachment> attachmentList = resAttachmentService.selectList(where);
        if (null != attachmentList && !attachmentList.isEmpty()) {
          SysParameter parameter = sysParameterService.selectBykey("ImgServer");
          String rootPath = "";
          if (null != parameter && com.baomidou.mybatisplus.toolkit.StringUtils
              .isNotEmpty(parameter.getParamValue())) {
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
          meeOutsideMeetingVO.setAttachmentList(attachmentList);*/
        }
      }
    }
    return meeOutsideMeetingVO;
  }

  @Override
  public SmsShortMessage smaWarn(MeeOutsideMeeting obj, AutUser createuser, String type)
      throws Exception {
    // 调用模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", "");
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();
    if (type.equals(MEET_ADD)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_SMS,
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
    }
    // smsShortMessageService.add(smsShortMessage);
    return smsShortMessage;
  }

  @Override
  public RetMsg meeDetail(AppMeeOutsideMeetingVO obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    AppMeeOutsideMeetingDO meeOutsideMeetingVO = null;
    //SysParameter parameter = sysParameterService.selectBykey("FastdfsServer");
    Integer claimStatus = 0;
    if (StringUtils.isNotEmpty(obj.getProcessInstanceId())) {
      Task task = taskService.createTaskQuery().processInstanceId(obj.getProcessInstanceId())
          .singleResult();
      if (StringUtils.isNotEmpty(task.getAssignee())) {
        claimStatus = 1;
      }
    }
    if (null != obj && null != obj.getId()) {
      MeeOutsideMeeting meeOutsideMeeting = selectById(obj.getId());
      if (null != meeOutsideMeeting) {
        List<String> tmpList = new ArrayList<String>();
        String id = "";
        String[] ids = meeOutsideMeeting.getPartyMemebersId().split(";");
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
        meeOutsideMeeting.setPartyMemeberNames(names);
        meeOutsideMeeting.setPartyMemebersId(id);
        meeOutsideMeetingVO = new AppMeeOutsideMeetingDO();
        BeanUtils.copyProperties(meeOutsideMeeting, meeOutsideMeetingVO);
        DateTime begTime = new DateTime(meeOutsideMeeting.getBeginTime());
        DateTime endTime = new DateTime(meeOutsideMeeting.getEndTime());
        meeOutsideMeetingVO.setBeginTime(begTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
        meeOutsideMeetingVO.setEndTime(endTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
        Where<ResResource> where = new Where<ResResource>();
        where.eq("biz_id", meeOutsideMeeting.getId());
        List<ResResource> resourceList = resResourceService.selectList(where);
        //String rootPath = parameter.getParamValue();
        if (null != resourceList && !resourceList.isEmpty()) {
          /*for (ResResource resResource : resourceList) {
              int ts = (int)(System.currentTimeMillis() / 1000);
              String token = ProtoCommon.getToken(resResource.getFileName(), ts, ClientGlobal.getG_secret_key());
              String fullPath = rootPath + resResource.getGroupName() + File.separator + resResource.getFileName() + "?token=" + token + "&ts=" + ts; 
              resResource.setFileName(fullPath);
          }*/
          meeOutsideMeetingVO.setResResourceList(resourceList);
        }
        meeOutsideMeetingVO.setClaimStatus(claimStatus);
      }
    }
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setMessage("操作成功");
    retMsg.setObject(meeOutsideMeetingVO);
    return retMsg;
  }

  public void mailWarn4Push(MeeOutsideMeeting obj, AutUser createuser, String type,
      String partyMemberNames, String partyMemberUserName,
      List<AutDepartmentUser> departmentUserList, List<MaiSendBox> maiSendBoxList)
      throws Exception {
    // 调用消息模板
    Map<String, String> list = new HashMap<String, String>();
    list.put("userName", createuser.getFullName());
    list.put("meetingRoom", "");
    list.put("theme", obj.getMeetingTitle());
    list.put("address", obj.getAddress());
    list.put("startTime", DateUtil.datetime2str(obj.getBeginTime()));
    list.put("endTime", DateUtil.datetime2str(obj.getEndTime()));
    list.put("contact", obj.getContacts());
    List<String> templateList = new ArrayList<String>();

    MaiWriteVO mai = new MaiWriteVO();
    MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
    MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
    maiReceiveBoxSearch.setAttachmentCount(0);
    maiReceiveBox.setReceiveUserIds(obj.getPartyMemebersId());
    maiReceiveBox.setReceiveFullNames(partyMemberUserName);
    maiReceiveBox.setReceiveUserNames(partyMemberNames);
    if (type.equals(MEET_ADD)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_XJHY);
    } else if (type.equals(MEET_UPDATE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYBG);
    } else if (type.equals(MEET_CANCEL)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYQX);
    } else if (type.equals(MEET_NOTICE)) {
      templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MAIL,
          WebConstant.TEMPLATE_MEETING_CODE, WebConstant.TEMPLATE_BEHAVIOR_HYTX);
    }
    // maiReceiveBox.setSubjectText(obj.getMeetingTitle());
    maiReceiveBox.setMailContent(obj.getMeetingContent());
    if (null != templateList && !templateList.isEmpty()) {
        maiReceiveBoxSearch.setSubjectText(templateList.get(1));
      maiReceiveBox.setMailContent(templateList.get(0));
    }
    maiReceiveBox.setSendId(obj.getCreateUserId());
    maiReceiveBox.setSendUserName(createuser.getUserName());
    maiReceiveBoxSearch.setSendFullName(createuser.getFullName());
    mai.setMaiReceiveBox(maiReceiveBox);
    maiSendBoxService.add4Push(mai, createuser, departmentUserList, maiSendBoxList);
    // return mai;
  }

}
