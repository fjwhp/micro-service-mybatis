package aljoin.mee.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.entity.MeeMeetingRoom;
import aljoin.mee.dao.mapper.MeeMeetingRoomMapper;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingRoomDO;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.mee.iservice.MeeMeetingRoomService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.util.DateUtil;

/**
 * 
 * 会议室表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Service
public class MeeMeetingRoomServiceImpl extends ServiceImpl<MeeMeetingRoomMapper, MeeMeetingRoom>
    implements MeeMeetingRoomService {

  @Resource
  private MeeMeetingRoomMapper mapper;
  @Resource
  private AutUserService autUserService;

  @Resource
  private MeeInsideMeetingService meeInsideMeetingService;


  @Override
  public Page<MeeMeetingRoomDO> list(PageBean pageBean, MeeMeetingRoom obj) throws Exception {
    Where<MeeMeetingRoom> where = new Where<MeeMeetingRoom>();
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
        where.like("meeting_room_name", obj.getMeetingRoomName());
        where.or("meeting_room_address like {0}", "%" + obj.getMeetingRoomName() + "%");
        where.or("person_charge like {0}", "%" + obj.getMeetingRoomName() + "%");
      }
      if (null != obj.getId()) {
        where.eq("id", obj.getId());
      }
      /*
       * if(StringUtils.isNotEmpty(obj.getMeetingRoomAddress())){
       * where.like("meeting_room_address",obj.getMeetingRoomAddress()); }
       * if(StringUtils.isNotEmpty(obj.getPersonCharge())){
       * where.like("person_charge",obj.getPersonCharge()); }
       */
    }
    where.orderBy("create_time", false);
    Page<MeeMeetingRoom> oldPage =
        selectPage(new Page<MeeMeetingRoom>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    Page<MeeMeetingRoomDO> page = new Page<MeeMeetingRoomDO>();
    List<MeeMeetingRoom> meeMeetingRoomList = oldPage.getRecords();
    List<MeeMeetingRoomDO> meeMeetingRoomDOList = new ArrayList<MeeMeetingRoomDO>();
    // List<Long> userIdList = new ArrayList<Long>();
    if (null != meeMeetingRoomList && !meeMeetingRoomList.isEmpty()) {
      int i = 1;
      for (MeeMeetingRoom meeMeetingRoom : meeMeetingRoomList) {
        if (null != meeMeetingRoom) {
          MeeMeetingRoomDO meeMeetingRoomDO = new MeeMeetingRoomDO();
          meeMeetingRoomDO.setId(meeMeetingRoom.getId());
          meeMeetingRoomDO.setNo(i);
          meeMeetingRoomDO.setMeetingRoomName(meeMeetingRoom.getMeetingRoomName());
          meeMeetingRoomDO.setMeetingRoomAddress(meeMeetingRoom.getMeetingRoomAddress());
          meeMeetingRoomDO.setDeviceDescription(meeMeetingRoom.getDeviceDescription());
          meeMeetingRoomDO.setPersonChargeId(meeMeetingRoom.getPersonChargeId());
          meeMeetingRoomDO.setPersonCharge(meeMeetingRoom.getPersonCharge());
          meeMeetingRoomDO.setPersonNumber(meeMeetingRoom.getPersonNumber());
          meeMeetingRoomDOList.add(meeMeetingRoomDO);
        }
        i++;
      }
    }
    page.setRecords(meeMeetingRoomDOList);
    page.setSize(oldPage.getSize());
    page.setTotal(oldPage.getTotal());
    return page;
  }

  @Override
  public void physicsDeleteById(Long id) throws Exception {
    mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(MeeMeetingRoom obj) throws Exception {
    mapper.copyObject(obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public MeeMeetingRoomCountDO countlist(MeeInsideMeetingVO obj) throws Exception {
    Where<MeeInsideMeeting> where = new Where<MeeInsideMeeting>();
    MeeMeetingRoomCountDO meetingRoomCountDO = null;
    if (null != obj) {
      meetingRoomCountDO = new MeeMeetingRoomCountDO();
      if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
        where.like("meeting_room_name", obj.getMeetingRoomName());
      }
      if (null != obj.getBeginTime() && null == obj.getEndTime()) {
        where.andNew();
        where.ge("begin_time", obj.getBeginTime());
      }
      if (null == obj.getBeginTime() && null != obj.getEndTime()) {
        where.andNew();
        where.le("end_time", obj.getEndTime());
      }
      if (null != obj.getBeginTime() && null != obj.getEndTime()) {
        where.andNew();
        where.between("end_time", obj.getBeginTime(), obj.getEndTime());
        where.or("begin_time between {0} and {1} ", obj.getBeginTime(), obj.getEndTime());
      }
      /*
       * if(null != obj.getBeginTime() && null == obj.getBeginTime()){ where.ge("begin_time",
       * obj.getBeginTime()); } if(null == obj.getBeginTime() && null != obj.getEndTime()){
       * where.le("end_time",obj.getEndTime()); } if(null != obj.getBeginTime() && null !=
       * obj.getEndTime()){ where.between("end_time",obj.getBeginTime(),obj.getEndTime()); }
       */
      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        // where.between("create_time",
        // DateUtil.getFirstDateOfWeek(PATTERN_BAR_YYYYMMDD),DateUtil.getLastDateOfWeek(PATTERN_BAR_YYYYMMDD));

        where.andNew();
        where.between("begin_time", DateUtil.str2date(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2date(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
        where.or("end_time between {0} and {1}",
            DateUtil.str2date(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2date(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        // where.between("create_time",
        // DateUtil.getFirstDateOfMonth(PATTERN_BAR_YYYYMMDD),DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD));
        where.andNew();
        where.between("begin_time", DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
        where.or("end_time between {0} and {1}",
            DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
            DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
      }
      if (null != obj.getMeetingRoomId()) {
        where.andNew();
        where.eq("meeting_room_id", obj.getMeetingRoomId());
      }
      if (StringUtils.isNotEmpty(obj.getSearchKey())) {
        String searchKey = obj.getSearchKey();
        where.andNew(" address like {0} or meeting_room_name like {1} or meeting_title like {2}",
            "%" + searchKey + "%", "%" + searchKey + "%", "%" + searchKey + "%");
      }
      // here.eq("meeting_situation",2);
      where.orderBy("create_time", false);
      where.setSqlSelect(
          "id,create_time,create_user_id,meeting_title,meeting_host,contacts,meeting_room_id,address,meeting_room_name,party_memebers_id,party_memeber_names,attendances,begin_time,end_time,meeting_content,is_warn_msg,is_warn_mail,is_warn_online,meeting_situation");
      List<MeeInsideMeeting> meeInsideMeetingList = meeInsideMeetingService.selectList(where);

      List<Map<Object, Object>> mapList = new ArrayList<Map<Object, Object>>();
      Map<String, Object> map = new HashMap<String, Object>();
      Map<Object, Object> mp = new HashMap<Object, Object>();
      List<String> theadList = new ArrayList<String>();
      theadList.add("会议室");
      theadList.add("使用次数");

      Where<MeeMeetingRoom> roomWhere = new Where<MeeMeetingRoom>();
      roomWhere.setSqlSelect(
          "id,meeting_room_name,person_number,meeting_room_address,person_charge_id,person_charge,device_description,use_count");
      List<MeeMeetingRoom> roomList = selectList(roomWhere);
      List<Map<Object, Object>> newMapList = new ArrayList<Map<Object, Object>>();

      if (StringUtils.isNotEmpty(obj.getThisWeek())) {
        theadList.add("周一");
        theadList.add("周二");
        theadList.add("周三");
        theadList.add("周四");
        theadList.add("周五");
        theadList.add("周六");
        theadList.add("周天");

        if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty() && null != roomList
            && !roomList.isEmpty()) {
          List<MeeInsideMeeting> insideMeetingList = new ArrayList<MeeInsideMeeting>();
          for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
            String weekDay = DateUtil.getWeek(meeInsideMeeting.getBeginTime());
            weekDay = convertWeekDay(weekDay);
            if ("周天".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            } else if ("周一".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            } else if ("周二".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            } else if ("周三".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            } else if ("周四".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            } else if ("周五".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            } else if ("周六".equals(weekDay)) {
              insideMeetingList.add(meeInsideMeeting);
              map.put(weekDay, insideMeetingList);
            }
            mp.put(meeInsideMeeting.getMeetingRoomId(), map);
          }
        }
        if (null != mp && !mp.isEmpty()) {
          Map<Object, Object> m = new HashMap<Object, Object>();
          for (Object k : mp.keySet()) {
            Map<String, Object> mpp = new HashMap<String, Object>();
            if (null != map && !map.isEmpty()) {
              for (String key : map.keySet()) {
                List<MeeInsideMeeting> insideList = (List<MeeInsideMeeting>) map.get(key);
                List<MeeInsideMeeting> meetingList = new ArrayList<MeeInsideMeeting>();
                for (MeeInsideMeeting insideMeeting : insideList) {
                  String weekDay = DateUtil.getWeek(insideMeeting.getBeginTime());
                  weekDay = convertWeekDay(weekDay);
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
            m.put(k, mpp);
          }
          mapList.add(m);
        }
        if (null != roomList && !roomList.isEmpty() && null != mapList && !mapList.isEmpty()) {
          Map<Object, Object> newMap = new HashMap<Object, Object>();
          for (Map<Object, Object> map2 : mapList) {
            if (null != map2) {
              for (Object key : map2.keySet()) {
                for (MeeMeetingRoom room : roomList) {
                  if (null != room && null != key) {
                    if (null != room.getId() && room.getId().equals((Long) key)
                        && room.getId().intValue() == ((Long) key).intValue()) {
                      newMap.put(room.getMeetingRoomName() + "," + room.getUseCount(),
                          (Map<Object, Object>) map2.get(key));
                    }
                  }
                }
              }
            }
          }
          newMapList.add(newMap);
        }
        if (null != newMapList && !newMapList.isEmpty()) {
          meetingRoomCountDO.setMapList(newMapList);
        }
      }

      if (StringUtils.isNotEmpty(obj.getThisMonth())) {
        if (meeInsideMeetingList.size() == 0) {
          int month = DateUtil.getDaysOfMonth();
          for (int i = 1; i <= month; i++) {
            theadList.add(i + "");
          }
        }
        if (null != meeInsideMeetingList && !meeInsideMeetingList.isEmpty()) {
          List<MeeInsideMeeting> insideMeetingList = new ArrayList<MeeInsideMeeting>();
          int month1 = DateUtil.getDaysOfMonth();
          for (int i = 1; i <= month1; i++) {
            theadList.add(i + "");
          }
          for (MeeInsideMeeting meeInsideMeeting : meeInsideMeetingList) {
            int day = DateUtil.getDaysOfMonth(meeInsideMeeting.getBeginTime());
            insideMeetingList.add(meeInsideMeeting);
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
                  int monthDay = DateUtil.getDaysOfMonth(insideMeeting.getBeginTime());
                  Long roomId = (Long) k;
                  if ((roomId.equals(insideMeeting.getMeetingRoomId())
                      && roomId.intValue() == insideMeeting.getMeetingRoomId().intValue())
                      && key.equals(String.valueOf(monthDay))) {
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

        if (null != roomList && !roomList.isEmpty() && null != mapList && !mapList.isEmpty()) {
          Map<Object, Object> newMap = new HashMap<Object, Object>();
          for (Map<Object, Object> map2 : mapList) {
            if (null != map2) {
              for (Object key : map2.keySet()) {
                for (MeeMeetingRoom room : roomList) {
                  if (null != room && null != key) {
                    if (null != room.getId() && room.getId().equals((Long) key)
                        && room.getId().intValue() == ((Long) key).intValue()) {
                      newMap.put(room.getMeetingRoomName() + "," + room.getUseCount(),
                          (Map<Object, Object>) map2.get(key));
                    }
                  }
                }
              }
            }
          }
          newMapList.add(newMap);
        }
        if (null != newMapList && !newMapList.isEmpty()) {
          meetingRoomCountDO.setMapList(newMapList);
        }
      }
      meetingRoomCountDO.setTheadList(theadList);
    }
    return meetingRoomCountDO;
  }

  @Override
  public RetMsg add(MeeMeetingRoom obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (StringUtils.isEmpty(obj.getMeetingRoomName())) {
      obj.setMeetingRoomName("");
      retMsg.setCode(1);
      retMsg.setMessage("会议室名称不能为空");
      return retMsg;
    }
    if (null == obj.getPersonNumber()) {
      obj.setPersonNumber(0);
      retMsg.setCode(1);
      retMsg.setMessage("可容纳人数不能为空");
      return retMsg;
    }
    if (StringUtils.isEmpty(obj.getMeetingRoomAddress())) {
      obj.setMeetingRoomAddress("");
      retMsg.setCode(1);
      retMsg.setMessage("会议地址不能为空");
      return retMsg;
    }
    if (StringUtils.isEmpty(obj.getPersonCharge())) {
      obj.setPersonCharge("");
      retMsg.setCode(1);
      retMsg.setMessage("负责人不能为空");
      return retMsg;
    }
    if (null == obj.getUseCount()) {
      obj.setUseCount(0);
    }
    Where<MeeMeetingRoom> where = new Where<MeeMeetingRoom>();
    where.eq("meeting_room_name", obj.getMeetingRoomName());
    Integer count = selectCount(where);
    if (count > 0) {
      retMsg.setCode(1);
      retMsg.setMessage("会议室名称重复，请重新填写");
      return retMsg;
    }
    insert(obj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  public List<MeeMeetingRoom> roomList(MeeMeetingRoom obj) throws Exception {
    Where<MeeMeetingRoom> where = new Where<MeeMeetingRoom>();
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
        where.like("meeting_room_name", obj.getMeetingRoomName());
      }
      if (StringUtils.isNotEmpty(obj.getMeetingRoomAddress())) {
        where.like("meeting_room_address", obj.getMeetingRoomAddress());
      }
      if (StringUtils.isNotEmpty(obj.getPersonCharge())) {
        where.like("person_charge", obj.getPersonCharge());
      }
    }
    where.orderBy("create_time", false);
    return selectList(where);
  }

  @Override
  public RetMsg update(MeeMeetingRoom obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
      MeeMeetingRoom orgnlObj = selectById(obj.getId());
      if (null != orgnlObj) {
        if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
          orgnlObj.setMeetingRoomName(obj.getMeetingRoomName());
        }
        if (StringUtils.isNotEmpty(obj.getMeetingRoomAddress())) {
          orgnlObj.setMeetingRoomAddress(obj.getMeetingRoomAddress());
        }
        if (null != obj.getPersonNumber()) {
          orgnlObj.setPersonNumber(obj.getPersonNumber());
        }
        if (StringUtils.isNotEmpty(obj.getPersonCharge())) {
          orgnlObj.setPersonCharge(obj.getPersonCharge());
        }
        if (StringUtils.isNotEmpty(obj.getPersonChargeId())) {
          orgnlObj.setPersonChargeId(obj.getPersonChargeId());
        }
//        if (StringUtils.isNotEmpty(obj.getDeviceDescription())) {
          orgnlObj.setDeviceDescription(obj.getDeviceDescription());
//        }
        if (!obj.getMeetingRoomName().equals(orgnlObj.getMeetingRoomName())) {
          Where<MeeMeetingRoom> where = new Where<MeeMeetingRoom>();
          where.eq("meeting_room_name", obj.getMeetingRoomName());
          Integer count = selectCount(where);
          if (count > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("会议室名称重复，请重新填写");
            return retMsg;
          }
        }
        updateById(orgnlObj);
      }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
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
  public List<MeeMeetingRoom> chargeList(MeeMeetingRoom obj) throws Exception {
    Where<MeeMeetingRoom> where = new Where<MeeMeetingRoom>();
    where.setSqlSelect("id,meeting_room_name,person_charge_id");
    if (null != obj) {
      if (StringUtils.isNotEmpty(obj.getMeetingRoomName())) {
        where.like("meeting_room_name", obj.getMeetingRoomName());
      }
      if (StringUtils.isNotEmpty(obj.getMeetingRoomAddress())) {
        where.like("meeting_room_address", obj.getMeetingRoomAddress());
      }
      if (StringUtils.isNotEmpty(obj.getPersonCharge())) {
        where.like("person_charge", obj.getPersonCharge());
      }
    }
    where.like("person_charge_id", String.valueOf(obj.getCreateUserId()));
    where.orderBy("create_time", false);
    return selectList(where);
  }

  @Override
  public MeeMeetingRoom detail(Long id) throws Exception {
    MeeMeetingRoom room = selectById(id);
  //人员排序（收件人）
    List<String> tmpList =new ArrayList<String>();
    String[] ids = room.getPersonChargeId().split(";");
    List<Long> receiveIds = new ArrayList<Long>();
    String allId = "";
    for(int i = 0; i < ids.length; i++) {
      receiveIds.add(Long.valueOf(ids[i]));
    }
    Where<AutUser> where1 = new Where<AutUser>(); 
    where1.in("id", ids);
    where1.setSqlSelect("id","user_name","full_name");
    List<AutUser> userList = autUserService.selectList(where1);
    Map<Long, Integer> rankList = autUserService.getUserRankList(receiveIds, null);
    if (rankList.size() > 0) {
      for(Long entry : rankList.keySet()) {
        String usrid = entry.toString();
        allId += usrid + ";";
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
    room.setPersonCharge(names);
    room.setPersonChargeId(allId);
    return room;
  }
}
