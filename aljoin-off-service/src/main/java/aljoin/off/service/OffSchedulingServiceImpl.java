package aljoin.off.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.entity.OffSchedulingHis;
import aljoin.off.dao.mapper.OffSchedulingMapper;
import aljoin.off.dao.object.MeetScheduleDO;
import aljoin.off.dao.object.OffSchedulingDO;
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.off.iservice.OffSchedulingHisService;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.ExcelUtil;
/**
 * 
 * 日程安排表(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-01
 */
@Service
public class OffSchedulingServiceImpl extends ServiceImpl<OffSchedulingMapper, OffScheduling>
		implements OffSchedulingService {

	@Resource
	private OffSchedulingMapper mapper;
	@Resource
	private OffSchedulingHisService offSchedulingHisService;
	@Resource
	private AutMsgOnlineService autMsgOnlineService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private AutDepartmentService autDepartmentService;
	@Resource
	private SysParameterService sysParameterService;
	@Resource
	private AutUserRankService autUserRankService;

	@Override
	public Page<OffScheduling> list(PageBean pageBean, OffScheduling obj) throws Exception {
		Where<OffScheduling> where = new Where<OffScheduling>();
		where.orderBy("create_time", false);
		Page<OffScheduling> page = selectPage(new Page<OffScheduling>(pageBean.getPageNum(), pageBean.getPageSize()),
				where);
		return page;
	}

	  @Override
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	  @Override
	public void copyObject(OffScheduling obj) throws Exception {
		mapper.copyObject(obj);
	}

/*	@Override
	@Transactional
	public RetMsg add(OffSchedulingVO obj) throws Exception {
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
			
			if(null != obj.getStartDateStr() && StringUtils.isNotEmpty(obj.getStartDateStr())){
				Date startDate = DateUtil.str2date(obj.getStartDateStr());
				obj.setStartDate(startDate);
				String startTime = obj.getStartDateStr().substring(obj.getStartDateStr().indexOf(" ")+1, obj.getStartDateStr().length());
				obj.setStartHourMin(startTime);
			}else{
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("开始时间不能为空");
				return retMsg;
			}
			if(null != obj.getEndDateStr() && StringUtils.isNotEmpty(obj.getEndDateStr())){
				Date endDate = DateUtil.str2date(obj.getEndDateStr());
				obj.setEndDate(endDate);
				String endTime = obj.getEndDateStr().substring(obj.getEndDateStr().indexOf(" ")+1, obj.getEndDateStr().length());
				obj.setEndHourMin(endTime);
			}else{
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("结束时间不能为空");
				return retMsg;
			}
			
			if (StringUtils.isEmpty(obj.getSharedPersonId())) {
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
				obj.setSharedPersonName("");
			}
			Boolean iswarnMail = false;
			Boolean iswarnMsg = false;
			Boolean iswarnOnline = false;
			if (null == obj.getIsWarnMail()) {
				obj.setIsWarnMail(0);
			} else {
				iswarnMail = true;
			}
			if (null == obj.getIsWarnMsg()) {
				obj.setIsWarnMsg(0);
			} else {
				iswarnMsg = true;
			}
			if (null == obj.getIsWarnOnline()) {
				obj.setIsWarnOnline(0);
			} else {
				iswarnOnline = true;
			}

			OffScheduling offScheduling = new OffScheduling();
			BeanUtils.copyProperties(obj, offScheduling);
			insert(offScheduling);

			// 向日程安排历史表插入记录
			OffSchedulingHis schedulingHis = new OffSchedulingHis();
			BeanUtils.copyProperties(offScheduling, schedulingHis);
			offSchedulingHisService.insert(schedulingHis);

			String sharedPersonIds = obj.getSharedPersonId();

			String createUserFullName = "";
			String createUserName = "";
			if (iswarnMail || iswarnMsg || iswarnOnline) {
				if (null != offScheduling.getCreateUserId()) {
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.eq("id", offScheduling.getCreateUserId());
					AutUser user = autUserService.selectOne(userWhere);
					if (null != user && StringUtils.isNotEmpty(user.getFullName())) {
						createUserFullName = user.getFullName();
					}
					if (null != user && StringUtils.isNotEmpty(user.getUserName())){
						createUserName = user.getCreateUserName();
					}
				}
			}

			if (offScheduling.getIsWarnMail() == 1) {// 邮件提醒
				CustomUser customUser = new CustomUser(createUserFullName, createUserFullName, iswarnOnline, iswarnOnline, iswarnOnline, iswarnOnline, null);
				
			}
			if (offScheduling.getIsWarnMsg() == 1) {// 短信提醒

			}
			if (offScheduling.getIsWarnOnline() == 1) {// 在线短信提醒
				AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
				if (null != sharedPersonIds && StringUtils.isNotEmpty(sharedPersonIds)) {

					requestVO.setMsgContent(WebConstant.ONLINE_MSG_WORKPLAN_TITILE_PRE + offScheduling.getTheme());
					// 封装推送信息请求信息
					requestVO.setFromUserId(offScheduling.getCreateUserId());
					requestVO.setFromUserFullName(createUserFullName);
					requestVO.setFromUserName(offScheduling.getCreateUserName());
					requestVO.setGoUrl("off/offScheduling/offSchedulingDetailPage?id=" + offScheduling.getId());
					requestVO.setMsgType(WebConstant.ONLINE_MSG_WORKPLAN);
					// 前端传来的1个或多个userId,截取存进List
					List<String> receiveUserIdList = Arrays.asList(sharedPersonIds.split(";"));
					requestVO.setToUserId(receiveUserIdList);
					// 调用推送在线消息接口
					autMsgOnlineService.pushMessageToUserList(requestVO);
				}
			}
		}
		// 删除日程安排表中的过期数据
		deleteOldData();
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}*/

	@Override
	public List<OffScheduling> list(OffSchedulingVO obj) throws Exception {
		/*** 旧的方法不能用了，后台都要累死了，前端找了新的插件，传日期找出来就行 ***/
		List<OffScheduling> scheduleList = new ArrayList<OffScheduling>();
		if (null != obj) {
			Date rangeBeg = new Date();
			Date rangEnd = new Date();
			if (null != obj.getScheduleStartDate() && StringUtils.isNotEmpty(obj.getScheduleStartDate())) {
				rangeBeg = DateUtil.str2date(obj.getScheduleStartDate());// 日程开始时间
			}
			if (null != obj.getScheduleEndDate() && StringUtils.isNotEmpty(obj.getScheduleEndDate())) {
				rangEnd = DateUtil.str2date(obj.getScheduleEndDate());
			}
			if (rangEnd.before(rangeBeg)) {
				return null;
			}
			Date monthFirstDay = DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));
			// Date monthLastDay =
			// DateUtil.str2date(DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD));

			OffScheduling offScheduling = null;
			if (null != rangeBeg && null != rangEnd) {
				// 查旧数据
				if (rangEnd.before(monthFirstDay)) {
					List<OffSchedulingHis> scheduleHisList = new ArrayList<OffSchedulingHis>();
					Where<OffSchedulingHis> hiswhere = new Where<OffSchedulingHis>();
					hiswhere.setSqlSelect(
							"id,create_user_id,start_date,theme,start_hour_min,end_date,end_hour_min,type,content,place");
					hiswhere.eq("create_user_id", obj.getCreateUserId());
					hiswhere.or("shared_person_id like {0}", "%" + String.valueOf(obj.getCreateUserId()) + "%");
					hiswhere.and();
					hiswhere.ge("start_date", DateUtil.str2dateOrTime(obj.getScheduleStartDate()));
					hiswhere.le("end_date", DateUtil.str2dateOrTime(obj.getScheduleEndDate()));
					scheduleHisList = offSchedulingHisService.selectList(hiswhere);
					if (null != scheduleHisList && !scheduleHisList.isEmpty()) {
						for (OffSchedulingHis offscheduleHis : scheduleHisList) {
							offScheduling = new OffScheduling();
							BeanUtils.copyProperties(offscheduleHis, offScheduling);
							scheduleList.add(offScheduling);
						}
					}
				} else {// 查本月及本月之后的数据
					Where<OffScheduling> where = new Where<OffScheduling>();
					where.setSqlSelect(
							"id,create_user_id,start_date,theme,start_hour_min,end_date,end_hour_min,type,content,place");
					where.eq("create_user_id", obj.getCreateUserId());
					where.or("shared_person_id like {0}", "%" +String.valueOf(obj.getCreateUserId()) + "%");
					where.and();
					where.ge("start_date", rangeBeg);
					where.le("end_date", rangEnd);
					scheduleList = selectList(where);
				}
			}
			deleteOldData();
		}
		return scheduleList;
	}

	/**
	 * 插入和查询的时，删除日程表的旧数据
	 * 
	 * @throws Exception
	 */
	@Override
	public void deleteOldData() throws Exception {
		Where<OffScheduling> where = new Where<OffScheduling>();
		Date lastmonth = DateUtil.getBefore(new Date(), DateUtil.UNIT_MONTH, 1);
		where.le("end_date", lastmonth);
		List<OffScheduling> offschedulingList = selectList(where);
		if (null != offschedulingList && !offschedulingList.isEmpty()) {
			for (OffScheduling offschedule : offschedulingList) {
				physicsDeleteById(offschedule.getId());
			}
		}
	}

	@Override
	@Transactional
	public RetMsg update(OffScheduling obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj && null != obj.getId()) {
			OffScheduling schedule = selectById(obj.getId());
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
				if (null != obj.getSharedPersonId()) {
					schedule.setSharedPersonId(obj.getSharedPersonId());
					if (null == obj.getType()) {
						schedule.setType(1);
					}
				} else {
					if (null == obj.getType()) {
						schedule.setType(2);
					}
				}
				if (null != obj.getSharedPersonName()) {
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
				updateById(schedule);
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
			String sharedPersonIds = obj.getSharedPersonId();

			Boolean iswarnMail = schedule.getIsWarnMail() == 1 ? true : false;
			Boolean iswarnMsg = schedule.getIsWarnMsg() == 1 ? true : false;
			Boolean iswarnOnline = schedule.getIsWarnOnline() == 1 ? true : false;

			String createUserName = "";
			if (iswarnMail || iswarnMsg || iswarnOnline) {
				if (null != schedule.getCreateUserId()) {
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.eq("id", schedule.getCreateUserId());
					AutUser user = autUserService.selectOne(userWhere);
					if (null != user && StringUtils.isNotEmpty(user.getFullName())) {
						createUserName = user.getFullName();
					}
				}
			}

			if (schedule.getIsWarnMail() == 1) {// 邮件提醒
			}
			if (schedule.getIsWarnMsg() == 1) {// 短信提醒

			}
			if (schedule.getIsWarnOnline() == 1) {// 在线短信提醒
				AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
				if (null != sharedPersonIds && StringUtils.isNotEmpty(sharedPersonIds)) {
					// 封装推送信息请求信息
					requestVO.setFromUserId(schedule.getCreateUserId());
					requestVO.setFromUserFullName(createUserName);
					requestVO.setFromUserName(schedule.getCreateUserName());
					requestVO.setGoUrl("");
					requestVO.setMsgType(WebConstant.ONLINE_MSG_TOGETHERWORK);
					// 前端传来的1个或多个userId,截取存进List
					List<String> receiveUserIdList = Arrays.asList(sharedPersonIds.split(";"));
					requestVO.setToUserId(receiveUserIdList);
					// 调用推送在线消息接口
					autMsgOnlineService.pushMessageToUserList(requestVO);
				}
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Transactional
	@Override
	public RetMsg delete(OffScheduling obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if (null != obj) {
			if (null != obj.getId()) {
				OffScheduling offScheduling = selectById(obj.getId());
				if (null != offScheduling) {
					if (offScheduling.getType() == 1) {
						retMsg.setCode(1);
						retMsg.setMessage("共享日程不可删除");
						return retMsg;
					}
					// 在主表中做物理删除，在历史表中做逻辑删除
					physicsDeleteById(offScheduling.getId());
					OffSchedulingHis offSchedulingHis = offSchedulingHisService.selectById(obj.getId());
					if (null != offSchedulingHis && null != offSchedulingHis.getId()) {
						offSchedulingHisService.deleteById(offSchedulingHis.getId());
					}
				}
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public void export(HttpServletResponse response, OffSchedulingVO obj) throws Exception {
		// Where<OffSchedulingHis> where = new Where<OffSchedulingHis>();
		if (null != obj) {
			List<OffScheduling> schedulingList = list(obj);
			/*List<OffSchedulingVO> offSchedulingVOs = new ArrayList<OffSchedulingVO>();
			for(OffScheduling off : schedulingList){
			  OffSchedulingVO offvo = new OffSchedulingVO();
			  BeanUtils.copyProperties(off, offvo);
			  String begindate = DateUtil.date2str(offvo.getStartDate()) +" "+ offvo.getStartHourMin();
			  String enddate = DateUtil.date2str(offvo.getEndDate()) +" "+ offvo.getEndHourMin();
			  offvo.setScheduleStartDate(begindate);
			  offvo.setScheduleEndDate(enddate);
			  offSchedulingVOs.add(offvo);
			}*/
			String[][] headers = { { "Theme", "主题" }, { "Place", "地点" }, 
					{ "StartDateStr", "开始时间" },  { "EndDateStr", "结束时间" },
					{ "Content", "内容" } };
			List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();

			if (null != schedulingList && !schedulingList.isEmpty()) {
				List<OffSchedulingVO> offSchedulingVOList = new ArrayList<OffSchedulingVO>();
				for (OffScheduling entity : schedulingList) {
					OffSchedulingVO offSchedulingVO = new OffSchedulingVO();
					BeanUtils.copyProperties(entity, offSchedulingVO);
					if (null != offSchedulingVO) {
						if (null != offSchedulingVO.getStartDate()) {
						    String begindate = DateUtil.date2str(offSchedulingVO.getStartDate()) +" "+ offSchedulingVO.getStartHourMin();
						    offSchedulingVO.setStartDateStr(begindate);
						}
						if (null != offSchedulingVO.getEndDate()) {
						    String enddate = DateUtil.date2str(offSchedulingVO.getEndDate()) +" "+ offSchedulingVO.getEndHourMin();
							offSchedulingVO.setEndDateStr(enddate);
						}
					}
					offSchedulingVOList.add(offSchedulingVO);
				}
				if (!offSchedulingVOList.isEmpty()) {
					for (OffSchedulingVO entity : offSchedulingVOList) {
						Map<String, Object> map = new HashMap<String, Object>();
						for (int i = 0; i < headers.length; i++) {
							OffSchedulingVO schedulingVO = entity;
							String methodName = "get" + headers[i][0];
							Class<?> clazz = OffSchedulingVO.class;
							Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
							if (null != method) {
								map.put(headers[i][0], method.invoke(schedulingVO));
							}
						}
						dataset.add(map);
					}
					ExcelUtil.exportExcel("日程安排", headers, dataset, response);
				}
			}
		}
	}

	@Override
	public OffSchedulingVO detail(OffScheduling obj) throws Exception {
		OffSchedulingVO offSchedulingVO = new OffSchedulingVO();
		if (null != obj && null != obj.getId()) {
			OffSchedulingHis offSchedulingHis = new OffSchedulingHis();
			OffScheduling offScheduling = selectById(obj.getId());
			if (null == offScheduling) {
				offSchedulingHis = offSchedulingHisService.selectById(obj.getId());
				if (null != offSchedulingHis) {
					BeanUtils.copyProperties(offSchedulingHis, offSchedulingVO);
				}
			} else {
				BeanUtils.copyProperties(offScheduling, offSchedulingVO);
			}
			if (null != offSchedulingVO) {
				if (offSchedulingVO.getType() != null) {
					if (offSchedulingVO.getType() == 1) {
						offSchedulingVO.setTypeStr("共享日程");
					} else if (offSchedulingVO.getType() == 2) {
						offSchedulingVO.setTypeStr("个人计划");
					} else if (offSchedulingVO.getType() == 3) {
						offSchedulingVO.setTypeStr("会议通知");
					}
				}
				if(!"null".equals(offSchedulingVO.getSharedPersonId())){
				//人员排序
	                List<String> tmpList =new ArrayList<String>();
	                String[] ids = offSchedulingVO.getSharedPersonId().split(";");
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
	                offSchedulingVO.setSharedPersonName(names.substring(0,names.length()-1));
	                offSchedulingVO.setSharedPersonId(allId);
                }else{
                  offSchedulingVO.setSharedPersonId("");
                  offSchedulingVO.setSharedPersonName("");
                }
				
				offSchedulingVO.setStartDateStr(DateUtil.date2str(offSchedulingVO.getStartDate()) +" "+ offSchedulingVO.getStartHourMin());
				offSchedulingVO.setEndDateStr(DateUtil.date2str(offSchedulingVO.getEndDate()) +" " + offSchedulingVO.getEndHourMin());

				AutUser user = autUserService.selectById(offSchedulingVO.getId());
				if (null != user && null != user.getFullName()) {
					offSchedulingVO.setCreateFullName(user.getFullName());
				}
				if (null != offSchedulingVO.getCreateTime()) {
					offSchedulingVO.setCreateTimeStr(DateUtil.datetime2numstr(offSchedulingVO.getCreateTime()));
				}

				if (obj.getCreateUserId().equals(offSchedulingVO.getCreateUserId()) && offSchedulingVO.getType() != 1) {
					offSchedulingVO.setIsEdit(1);
				}
				if (null!=offSchedulingVO.getCreateUserId()){
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.eq("id",offSchedulingVO.getCreateUserId());
					AutUser cuser = autUserService.selectOne(userWhere);
					if(null != cuser && StringUtils.isNotEmpty(cuser.getFullName())){
						offSchedulingVO.setCreateFullName(cuser.getFullName());
					}
				}
			}
		}
		return offSchedulingVO;
	}

  @Override
  public Page<OffSchedulingVO> leaderList(PageBean pageBean, OffSchedulingVO obj) throws Exception {
    Page<OffSchedulingVO> page = new Page<OffSchedulingVO>();
    Page<OffScheduling> oldPage = new Page<OffScheduling>();
    Page<OffSchedulingHis> hisPage = new Page<OffSchedulingHis>();
    List<OffSchedulingVO> schedulingVOList = new ArrayList<OffSchedulingVO>();
    Integer total = 0;
    Integer size = 0;

    Boolean isNull = false; // 条件判断是否会导致结果空值
    Set<Long> deptUserIdList = new HashSet<Long>();// 领导所在部门所有成员
    Where<OffScheduling> where = new Where<OffScheduling>();
    Where<OffSchedulingHis> hisWhere = new Where<OffSchedulingHis>();
    where.setSqlSelect(
        "id,theme,start_date,start_hour_min,end_date,end_hour_min,type,create_user_id");
    hisWhere.setSqlSelect(
        "id,theme,start_date,start_hour_min,end_date,end_hour_min,type,create_user_id");
    // 领导权限先判断
    if (null != obj.getCreateUserId()) {// 当前用户id
      // 根据当前登录用户ID查询部门用户表 获得is_leader 字段的值 然后把本部门的用户都查询出来
      Where<AutDepartmentUser> where1 = new Where<AutDepartmentUser>();
      where1.setSqlSelect("id,user_id,dept_id,is_leader");
      where1.eq("user_id", obj.getCreateUserId());
      List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(where1);// 当前用户部门列表
      List<AutDepartmentUser> leaderDeptList = new ArrayList<AutDepartmentUser>();
      List<Long> deptIdList = new ArrayList<Long>();
      if (null != departmentUserList && !departmentUserList.isEmpty()) {
        for (AutDepartmentUser departmentUser : departmentUserList) {
          if (null != departmentUser && null != departmentUser.getIsLeader()) {
            if (departmentUser.getIsLeader() == 1) {
              deptIdList.add(departmentUser.getDeptId());
              leaderDeptList.add(departmentUser);
            }
          }
        }
        if (null != deptIdList && !deptIdList.isEmpty()) {// 如果是领导

          for (long deptId : deptIdList) {
            AutDepartment autDepartmentUser = new AutDepartment();
            autDepartmentUser.setId(deptId);
            List<AutUser> au = autDepartmentService.getChildDeptUserList(autDepartmentUser);
            for (AutUser autuser : au) {
              deptUserIdList.add(autuser.getId());
            }
          }
          where.in("create_user_id", deptUserIdList);
          hisWhere.in("create_user_id", deptUserIdList);
        } else {
          where.andNew("shared_person_id like {0}", "%" + obj.getCreateUserId().toString() + "%")
              .or("create_user_id = {0}", obj.getCreateUserId());
          hisWhere.andNew("shared_person_id like {0}", "%" + obj.getCreateUserId().toString() + "%")
              .or("create_user_id = {0}", obj.getCreateUserId());
        }
      }
    }
    // 发布人
    List<Long> idList = new ArrayList<Long>();
    if (StringUtils.isNotEmpty(obj.getCreateFullName())) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.like("full_name", obj.getCreateFullName());
      userWhere.setSqlSelect("id");
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (AutUser user : userList) {
          idList.add(user.getId());
        }
      }
      if (idList.isEmpty()) {// idList为空表示找不对符合条件的发布人，没有相关记录
        isNull = true;
      } else {
        isNull = false;
        where.andNew();
        hisWhere.andNew();
        where.in("create_user_id", idList);
        hisWhere.in("create_user_id", idList);
      }
    }
    if (StringUtils.isNotEmpty(obj.getTheme())) {
      where.andNew();
      hisWhere.andNew();
      where.like("theme", obj.getTheme());
      hisWhere.like("theme", obj.getTheme());
    }

    if (null != obj.getType()) {
      where.andNew();
      hisWhere.andNew();
      where.eq("type", obj.getType());
      hisWhere.eq("type", obj.getType());
    }

    where.orderBy("start_date", false);
    hisWhere.orderBy("start_date", false);

    Date rangeBeg = null;
    Date rangEnd = null;
    // 日程查询开始时间
    if (null != obj.getScheduleStartDate() && StringUtils.isNotEmpty(obj.getScheduleStartDate())
        && (null == obj.getScheduleEndDate() || StringUtils.isEmpty(obj.getScheduleEndDate()))) {
      rangeBeg = DateUtil.str2date(obj.getScheduleStartDate() + " 00:00:00");
      where.ge("start_date", rangeBeg);
      hisWhere.ge("start_date", rangeBeg);
    }
    // 日程查询结束时间
    if (null != obj.getScheduleEndDate() && StringUtils.isNotEmpty(obj.getScheduleEndDate())
        && (null == obj.getScheduleStartDate()
            && StringUtils.isEmpty(obj.getScheduleStartDate()))) {
      rangEnd = DateUtil.str2datetime(obj.getScheduleEndDate() + " 23:59:59");
      where.andNew();
      hisWhere.andNew();
      where.le("start_date", rangEnd);
      hisWhere.le("start_date", rangEnd);
    }
    if (null != obj.getScheduleEndDate() && StringUtils.isNotEmpty(obj.getScheduleEndDate())
        && (null != obj.getScheduleStartDate()
            && StringUtils.isNotEmpty(obj.getScheduleStartDate()))) {
      rangeBeg = DateUtil.str2date(obj.getScheduleStartDate() + " 00:00:00");
      rangEnd = DateUtil.str2datetime(obj.getScheduleEndDate() + " 23:59:59");
      where.andNew();
      where.ge("start_date", rangeBeg);
      where.and("start_date <={0}", rangEnd);
      hisWhere.andNew();
      hisWhere.ge("start_date", rangeBeg);
      hisWhere.and("start_date <={0}", rangEnd);
    }
    Date monthFirstDay =
        DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));

    Set<Long> userIdList = new HashSet<Long>();
    List<OffScheduling> schedulingList = new ArrayList<OffScheduling>();
    List<OffSchedulingHis> schedulingHisList = new ArrayList<OffSchedulingHis>();

    if (null != rangeBeg && (rangeBeg.after(monthFirstDay) || rangeBeg.equals(monthFirstDay))) {// 只有这种情况是查新表
      if (!isNull) {
        oldPage = selectPage(new Page<OffScheduling>(pageBean.getPageNum(), pageBean.getPageSize()),
            where);
        total = oldPage.getTotal();
        size = oldPage.getSize();
        schedulingList = oldPage.getRecords();
        if (null != schedulingList && !schedulingList.isEmpty()) {
          // int i = 0;
          for (OffScheduling scheduling : schedulingList) {
            userIdList.add(scheduling.getCreateUserId());
            OffSchedulingVO schedulingVO = new OffSchedulingVO();
            if (null != scheduling) {
              BeanUtils.copyProperties(scheduling, schedulingVO);
              DateTime dateTime = new DateTime(scheduling.getStartDate());
              schedulingVO.setStartDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD) + " "
                  + scheduling.getStartHourMin());
              dateTime = new DateTime(scheduling.getEndDate());
              schedulingVO.setEndDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD) + " "
                  + scheduling.getEndHourMin());
              String type = "";
              if (null != scheduling.getType()) {
                if (1 == scheduling.getType()) {
                  type = "共享日程";
                } else if (2 == scheduling.getType()) {
                  type = "个人计划";
                } else if (3 == scheduling.getType()) {
                  type = "会议通知";
                }
              }
              schedulingVO.setTypeStr(type);
            }
            schedulingVOList.add(schedulingVO);
            // i++;
          }
        }

      } else {
        return page;
      }
    } else if (!isNull) {

      // BeanUtils.copyProperties(where, hisWhere);
      hisPage = offSchedulingHisService.selectPage(
          new Page<OffSchedulingHis>(pageBean.getPageNum(), pageBean.getPageSize()), hisWhere);
      total = hisPage.getTotal();
      size = hisPage.getSize();
      schedulingHisList = hisPage.getRecords();
      if (null != schedulingHisList && !schedulingHisList.isEmpty()) {
        for (OffSchedulingHis schedulinghis : schedulingHisList) {
          userIdList.add(schedulinghis.getCreateUserId());
          OffSchedulingVO schedulingVO = new OffSchedulingVO();
          if (null != schedulinghis) {
            BeanUtils.copyProperties(schedulinghis, schedulingVO);
            DateTime dateTime = new DateTime(schedulinghis.getStartDate());
            // schedulingVO.setScheduleStartDate(
            // dateTime.toString(PATTERN_BAR_YYYYMMDD) + " " + schedulinghis.getStartHourMin());
            schedulingVO.setStartDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD) + " "
                + schedulinghis.getStartHourMin());
            dateTime = new DateTime(schedulinghis.getEndDate());
            // schedulingVO.setScheduleEndDate(
            // dateTime.toString(PATTERN_BAR_YYYYMMDD) + " " + schedulinghis.getEndHourMin());
            schedulingVO.setEndDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD) + " "
                + schedulinghis.getEndHourMin());
            String type = "";
            if (null != schedulinghis.getType()) {
              if (1 == schedulinghis.getType()) {
                type = "共享日程";
              } else if (2 == schedulinghis.getType()) {
                type = "个人计划";
              } else if (3 == schedulinghis.getType()) {
                type = "会议通知";
              }
            }
            schedulingVO.setTypeStr(type);
          }
          schedulingVOList.add(schedulingVO);
        }
      }
    } else {
      return page;
    }
    if (null != userIdList && !userIdList.isEmpty()) {
      Where<AutUser> userWhere = new Where<AutUser>();
      userWhere.eq("is_active", 1);
      userWhere.setSqlSelect("id, full_name");
      userWhere.in("id", userIdList);
      List<AutUser> userList = autUserService.selectList(userWhere);
      if (null != userList && !userList.isEmpty()) {
        for (OffSchedulingVO offSchedulingVO : schedulingVOList) {
          for (AutUser user : userList) {
            if (null != user && null != offSchedulingVO) {
              if (StringUtils.isNotEmpty(user.getFullName())) {
                if ((null != user.getId() && null != offSchedulingVO.getCreateUserId())) {
                  if (user.getId().equals(offSchedulingVO.getCreateUserId())
                      && user.getId().intValue() == offSchedulingVO.getCreateUserId().intValue()) {
                    offSchedulingVO.setCreateFullName(user.getFullName());
                  }
                }
              }
            }
          }
        }
      }
    }

    if (null != schedulingVOList) {
      page.setRecords(schedulingVOList);
      page.setTotal(total);
      page.setSize(size);
    }
    return page;
  }

	@Override
	public List<String> indexList4Month(OffSchedulingDO obj) throws Exception {
		List<String> meetDatelist = new ArrayList<String>();
		if (null != obj && obj.getCreateUserId() != null) {
			String monthIndex = obj.getMonth();
			if (null == monthIndex) {
				String today = DateUtil.date2str(new Date());
				monthIndex = today.substring(0, today.lastIndexOf('-'));
			}
			String indexMonthFisrtStr = DateUtil.getFirstDateOfMonth(monthIndex, WebConstant.PATTERN_BAR_YYYYMMDD);
			String curMonthFirstStr = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
			String indexMonthLastStr = DateUtil.getLastDateOfMonth(monthIndex, WebConstant.PATTERN_BAR_YYYYMMDD);

			Date indexMonthFirst = DateUtil.str2date(indexMonthFisrtStr);
			Date curMonthFirst = DateUtil.str2date(curMonthFirstStr);

			List<OffScheduling> scheduleList = new ArrayList<OffScheduling>();
			List<OffSchedulingHis> scheduleHisList = new ArrayList<OffSchedulingHis>();
			if (curMonthFirst.before(indexMonthFirst) || curMonthFirst.equals(indexMonthFirst)) {
				Where<OffScheduling> where = new Where<OffScheduling>();
				where.setSqlSelect(
						"id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type");
				where.andNew("type = {0}",3).isNotNull("shared_person_id").like("shared_person_id", String.valueOf(obj.getCreateUserId()))
				.between("start_date", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				where.orNew("type = {0}",3).isNotNull("shared_person_id").like("shared_person_id", String.valueOf(obj.getCreateUserId()))
                .between("end_date", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
//				where.orNew("end_date between {0} and {1}", indexMonthFisrtStr + " 00:00:00", indexMonthLastStr + " 23:59:59");
				where.orNew("type = {0}",3).isNotNull("shared_person_id").like("shared_person_id", String.valueOf(obj.getCreateUserId()));
				
				where.le("start_date",DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"));
				where.ge("end_date", DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				scheduleList = selectList(where);
			} else {
				Where<OffSchedulingHis> wherehis = new Where<OffSchedulingHis>();
				wherehis.setSqlSelect(
						"id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type");
				
				wherehis.between("start_date", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				wherehis.or("end_date between {0} and {1}", DateUtil.str2dateOrTime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2dateOrTime(indexMonthLastStr + " 23:59:59"));
				wherehis.andNew("type = {0}",3).isNotNull("shared_person_id").like("shared_person_id", String.valueOf(obj.getCreateUserId()));
//				wherehis.eq("type", 3).isNotNull("shared_person_id").like("shared_person_id", String.valueOf(obj.getCreateUserId()));
				scheduleHisList = offSchedulingHisService.selectList(wherehis);
				if (null != scheduleHisList && !scheduleHisList.isEmpty()) {
					for (OffSchedulingHis offscheduleHis : scheduleHisList) {
						OffScheduling offScheduling = new OffScheduling();
						BeanUtils.copyProperties(offscheduleHis, offScheduling);
						scheduleList.add(offScheduling);
					}
				}
			}
			Boolean hasmeet = false;
			List<String> monthDate = DateUtil.getAllTheDateOftheMonthStr(DateUtil.str2date(indexMonthFisrtStr));
			if (null != scheduleList && !scheduleList.isEmpty()) {
				for (String day : monthDate) {
					hasmeet = false;
					String rightday = day.substring(0, day.lastIndexOf(' '));
					Date theday = DateUtil.str2date(rightday);
					for (OffScheduling offsch : scheduleList) {
						if (offsch.getStartDate().equals(theday)) {
							hasmeet = true;
							break;
						}
						if (offsch.getEndDate().equals(theday)) {
							hasmeet = true;
							break;
						}
						if ((offsch.getStartDate().before(theday) && offsch.getEndDate().after(theday))) {
							hasmeet = true;
							break;
						}
					}
					if (hasmeet) {
						meetDatelist.add(rightday);
					}
				}
			}
		}
		return meetDatelist;
	}

	@Override
	public List<OffScheduling> indexList4day(OffSchedulingDO obj) throws Exception {
		List<OffScheduling> offschedule = new ArrayList<OffScheduling>();
		if (null != obj && obj.getCreateUserId() != null) {
			String indexDateStr = obj.getMeetDay();
			if (null == indexDateStr) {
				indexDateStr = DateUtil.date2str(new Date());
			}
			String curMonthFirstStr = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
			Date curMonthFirst = DateUtil.str2date(curMonthFirstStr);

			Date indexDate = DateUtil.str2date(indexDateStr);
			List<OffSchedulingHis> scheduleHisList = new ArrayList<OffSchedulingHis>();
			if (indexDate.equals(curMonthFirst) || indexDate.after(curMonthFirst)) {
				Where<OffScheduling> where = new Where<OffScheduling>();
				where.setSqlSelect(
						"id,biz_type,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_id");
				//where.eq("create_user_id", obj.getCreateUserId());
				where.isNotNull("shared_person_id");
				where.and("shared_person_id like {0}", "%" + obj.getCreateUserId() +"%");
				where.and();
				if (obj.getBizType() != null && StringUtils.isNotEmpty(obj.getBizType())) {
					where.eq("biz_type", obj.getBizType());
				}
				where.eq("type", 3);
				where.le("start_date", indexDate);
				where.ge("end_date", indexDate);
				where.orderBy("start_hour_min");
				// where.or("start_date <= {0} && end_date >=
				// {0}",indexDateStr);
				offschedule = selectList(where);
			} else {
				Where<OffSchedulingHis> wherehis = new Where<OffSchedulingHis>();
				wherehis.setSqlSelect(
						"id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type,biz_id");
//				wherehis.eq("create_user_id", obj.getCreateUserId());
				wherehis.isNotNull("shared_person_id");
				wherehis.and("shared_person_id like {0}", "%" + obj.getCreateUserId() +"%");
				wherehis.and();
				if (obj.getBizType() != null && StringUtils.isNotEmpty(obj.getBizType())) {
					wherehis.eq("biz_type", obj.getBizType());
				}
				wherehis.eq("type", 3);
				wherehis.le("start_date", indexDate);
				wherehis.ge("end_date", indexDate);
				wherehis.orderBy("start_hour_min");
				scheduleHisList = offSchedulingHisService.selectList(wherehis);
				if (null != scheduleHisList && !scheduleHisList.isEmpty()) {
					for (OffSchedulingHis offscheduleHis : scheduleHisList) {
						OffScheduling offScheduling = new OffScheduling();
						BeanUtils.copyProperties(offscheduleHis, offScheduling);
						offschedule.add(offScheduling);
					}
				}
			}
		}
		return offschedule;
	}

	@Override
	public RetMsg leaderScheduleMap(MeetScheduleDO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		HashMap<Object, Object> resultMap = new HashMap<Object, Object>();
		List<String> meetDatelist = new ArrayList<String>();
		List<OffScheduling> leadertodaylist = new ArrayList<OffScheduling>();
		List<String> userIdList = new ArrayList<String>();
		List<AutUser> leaderUserList = new ArrayList<AutUser>();
		List<AutUser> leaderRankList = new ArrayList<AutUser>();
		if (null != obj) {
			// 查询出哪些领导
			SysParameter sysParameter = sysParameterService.selectBykey("leader_meeting_member");
			if (null != sysParameter) {
				String leader = sysParameter.getParamValue();
				if(leader != null && StringUtils.isNotEmpty(leader)){
					userIdList = Arrays.asList(sysParameter.getParamValue().split(";"));
					Where<AutUser> userwhere = new Where<AutUser>();
					userwhere.in("id", userIdList);
					userwhere.setSqlSelect("id,full_name");
					leaderUserList = autUserService.selectList(userwhere);
					// 领导排序
					Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
			        autUserRankWhere.setSqlSelect("id,user_rank");
			        autUserRankWhere.in("id", userIdList);
			        autUserRankWhere.orderBy("user_rank", true);
			        List<AutUserRank> userRankList = autUserRankService.selectList(autUserRankWhere);
			        for (AutUserRank autUserRank : userRankList) {
                      for (AutUser leaderUser : leaderUserList) {
                        if(leaderUser.getId().equals(autUserRank.getId())){
                          leaderRankList.add(leaderUser);
                        }
                      }
                    }
//					Map<Long,Integer> leaderMap = autUserService.getUserRankList(userIdList, "");
					if (null != leaderRankList && !leaderRankList.isEmpty()) {
						obj.setLeaderid(leaderRankList.get(0).getId());
						meetDatelist = leadMonthList(obj, null);
						leadertodaylist = leadDayList(obj,null);
					}
				}
			}
		}
		resultMap.put("leadList", leaderRankList);
		resultMap.put("leadertodaylist", leadertodaylist);
		resultMap.put("meetdatelist", meetDatelist);
		retMsg.setObject(resultMap);
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	@Override
	public List<OffScheduling> leadDayList(MeetScheduleDO obj,List<String> userIdList) throws Exception {
		List<OffScheduling> offschedule = new ArrayList<OffScheduling>();
		if (null != obj && obj.getLeaderid() != null) {
			String indexDateStr = obj.getDate();
			if (null == indexDateStr) {
				indexDateStr = DateUtil.date2str(new Date());
			}
			if(userIdList == null || userIdList.isEmpty()){
				String leaderId = String.valueOf(obj.getLeaderid());
				userIdList = new ArrayList<String>();
				userIdList.add(leaderId);
			}
			String curMonthFirstStr = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
			Date curMonthFirst = DateUtil.str2date(curMonthFirstStr);
			Date indexDate = DateUtil.str2date(indexDateStr);
			List<OffSchedulingHis> scheduleHisList = new ArrayList<OffSchedulingHis>();
			if (indexDate.equals(curMonthFirst) || indexDate.after(curMonthFirst)) {
				Where<OffScheduling> where = new Where<OffScheduling>();
				where.setSqlSelect(
						"id,create_user_id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type,biz_id");
				if (obj.getBizType() != null && StringUtils.isNotEmpty(obj.getBizType())) {
					where.eq("biz_type", obj.getBizType());
				}
				where.le("start_date", DateUtil.str2dateOrTime(indexDateStr));
				where.ge("end_date", DateUtil.str2dateOrTime(indexDateStr));
				where.eq("type", 3);
//				where.and();
//				where.between("start_date", indexDateStr, indexDateStr);
//				where.or("end_date between {0} and {1}", indexDateStr, indexDateStr);
//				where.and();
//				for(String userid :userIdList){
//					where.or("shared_person_id like {0}", "%" + userid +"%");
//				}
				where.isNotNull("shared_person_id");
				if (userIdList != null && !userIdList.isEmpty()){
					String userId = userIdList.get(0);
					where.and();
					where.like("shared_person_id", userId);
					if (userIdList.size()>1){
						for (int i = 1; i < userIdList.size(); i++) {
							where.or("shared_person_id like {0}", "%" + userIdList.get(i) +"%");
						}
					}
				}
//				where.and();
//				where.between("start_date", indexDateStr + " 00:00:00", indexDateStr + " 23:59:59");
//				where.or("end_date between {0} and {1}", indexDateStr + " 00:00:00", indexDateStr + " 23:59:59");
				offschedule = selectList(where);
			} else {
				Where<OffSchedulingHis> wherehis = new Where<OffSchedulingHis>();
				wherehis.setSqlSelect(
						"id,create_user_id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type,biz_id");
				if (obj.getBizType() != null && StringUtils.isNotEmpty(obj.getBizType())) {
					wherehis.eq("biz_type", obj.getBizType());
				}
				wherehis.le("start_date", DateUtil.str2dateOrTime(indexDateStr));
				wherehis.ge("end_date", DateUtil.str2dateOrTime(indexDateStr));
				wherehis.eq("type", 3);
				wherehis.isNotNull("shared_person_id");
//				wherehis.le("start_date", indexDateStr);
//				wherehis.ge("end_date", indexDateStr);
//				wherehis.and();
				
				if (userIdList != null && !userIdList.isEmpty()){
					String userId = userIdList.get(0);
					wherehis.and();
					wherehis.like("shared_person_id", userId);
					if (userIdList.size()>1){
						for (int i = 1; i < userIdList.size(); i++) {
							wherehis.or("shared_person_id like {0}", "%" + userIdList.get(i) +"%");
						}
					}
				}
//				wherehis.or("shared_person_id like {0}", "%" + obj.getLeaderid() +"%");
				scheduleHisList = offSchedulingHisService.selectList(wherehis);
				if (null != scheduleHisList && !scheduleHisList.isEmpty()) {
					for (OffSchedulingHis offscheduleHis : scheduleHisList) {
						OffScheduling offScheduling = new OffScheduling();
						BeanUtils.copyProperties(offscheduleHis, offScheduling);
						offschedule.add(offScheduling);
					}
				}
			}
		}
		return offschedule;
	}
	
	@Override
	public List<String> leadMonthList(MeetScheduleDO obj,List<String> userIdList) throws Exception {
		List<String> meetDatelist = new ArrayList<String>();
		if(null!=obj){
			String monthapp = obj.getMonth();
			if (null == monthapp || StringUtils.isEmpty(monthapp)) {
				String sendDay = obj.getDate();
				String today = "";
				if(sendDay != null && StringUtils.isNotEmpty(sendDay)){
					today = sendDay;
				}else{
					today = DateUtil.date2str(new Date());
				}
				 
				monthapp = today.substring(0, today.lastIndexOf('-'));
			}
			if(userIdList == null || userIdList.isEmpty()){
				String leaderId = String.valueOf(obj.getLeaderid());
				userIdList = new ArrayList<String>();
				userIdList.add(leaderId);
			}
			String indexMonthFisrtStr = DateUtil.getFirstDateOfMonth(monthapp, WebConstant.PATTERN_BAR_YYYYMMDD);
			String curMonthFirstStr = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
			String indexMonthLastStr = DateUtil.getLastDateOfMonth(monthapp, WebConstant.PATTERN_BAR_YYYYMMDD);

			Date indexMonthFirst = DateUtil.str2date(indexMonthFisrtStr);
			Date curMonthFirst = DateUtil.str2date(curMonthFirstStr);

			List<OffScheduling> scheduleList = new ArrayList<OffScheduling>();
			List<OffSchedulingHis> scheduleHisList = new ArrayList<OffSchedulingHis>();
			
			if (curMonthFirst.before(indexMonthFirst) || curMonthFirst.equals(indexMonthFirst)) {
				Where<OffScheduling> where = new Where<OffScheduling>();
				where.setSqlSelect(
						"id,create_user_id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type");
				if(obj.getBizType()!= null && StringUtils.isNotEmpty(obj.getBizType())){
					where.eq("biz_type", obj.getBizType());
				}
				where.eq("type", 3);
				if (userIdList != null && !userIdList.isEmpty()){
					String userId = userIdList.get(0);
					where.like("shared_person_id", userId);
					if (userIdList.size()>1){
						for (int i = 1; i < userIdList.size(); i++) {
			                where.andNew();
							where.or("shared_person_id like {0}", "%" + userIdList.get(i) +"%");
						}
					}
				}
				where.andNew();
				where.between("start_date", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				where.or("end_date between {0} and {1}", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				scheduleList = selectList(where);
			} else {
				Where<OffSchedulingHis> wherehis = new Where<OffSchedulingHis>();
				wherehis.setSqlSelect(
						"id,create_user_id,start_date,theme,start_hour_min,end_date,end_hour_min,type,biz_type");
				if(obj.getBizType()!= null && StringUtils.isNotEmpty(obj.getBizType())){
				  wherehis.andNew();
					wherehis.eq("biz_type", obj.getBizType());
				}
				wherehis.eq("type", 3);
//				wherehis.and();
//				wherehis.in("create_user_id", userIdList);
//				for(String userid :userIdList){
//					wherehis.or("shared_person_id like {0}", "%" +userid +"%");
//				}
				if (userIdList != null && !userIdList.isEmpty()){
					String userId = userIdList.get(0);
					wherehis.like("shared_person_id", userId);
					if (userIdList.size()>1){
						for (int i = 1; i < userIdList.size(); i++) {
			                wherehis.andNew();
							wherehis.or("shared_person_id like {0}", "%" + userIdList.get(i) +"%");
						}
					}
				}
				wherehis.andNew();
				wherehis.between("start_date", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				wherehis.or("end_date between {0} and {1}", DateUtil.str2datetime(indexMonthFisrtStr + " 00:00:00"), DateUtil.str2datetime(indexMonthLastStr + " 23:59:59"));
				scheduleHisList = offSchedulingHisService.selectList(wherehis);
				if (null != scheduleHisList && !scheduleHisList.isEmpty()) {
					for (OffSchedulingHis offscheduleHis : scheduleHisList) {
						OffScheduling offScheduling = new OffScheduling();
						BeanUtils.copyProperties(offscheduleHis, offScheduling);
						scheduleList.add(offScheduling);
					}
				}
			}
			
			Boolean hasmeet = false;
			List<String> monthDate = DateUtil.getAllTheDateOftheMonthStr(DateUtil.str2date(indexMonthFisrtStr));
			if (null != scheduleList && !scheduleList.isEmpty()) {
				for (String day : monthDate) {
					hasmeet = false;
					String rightday = day.substring(0, day.lastIndexOf(' '));
					Date theday = DateUtil.str2date(rightday);
					for (OffScheduling offsch : scheduleList) {
						if (offsch.getStartDate().equals(theday)) {
							hasmeet = true;
							break;
						}
						if (offsch.getEndDate().equals(theday)) {
							hasmeet = true;
							break;
						}
						if ((offsch.getStartDate().before(theday) && offsch.getEndDate().after(theday))) {
							hasmeet = true;
							break;
						}
					}
					if (hasmeet) {
						meetDatelist.add(rightday);
					}
				}
			}
		}
		return meetDatelist;
	}
	
	@Override
	public RetMsg leaderMeetingMap(MeetScheduleDO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		HashMap<Object, Object> resultMap = new HashMap<Object, Object>();
		List<String> meetDatelist = new ArrayList<String>();
		List<OffScheduling> leadertodaylist = new ArrayList<OffScheduling>();
		if (null != obj) {
			meetDatelist = leadMonthList(obj, null);
			leadertodaylist = leadDayList(obj,null);
		}
		resultMap.put("leadertodaylist", leadertodaylist);
		resultMap.put("meetdatelist", meetDatelist);
		retMsg.setObject(resultMap);
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	@Override
	public RetMsg personMap(MeetScheduleDO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		HashMap<Object, Object> resultMap = new HashMap<Object, Object>();
		List<String> meetDatelist = new ArrayList<String>();
		List<OffScheduling> todaylist = new ArrayList<OffScheduling>();
		if (null != obj && null != obj.getLeaderid()) {
			meetDatelist = leadMonthList(obj, null);
			todaylist = leadDayList(obj,null);
		}
		resultMap.put("todaylist", todaylist);
		resultMap.put("meetdatelist", meetDatelist);
		retMsg.setObject(resultMap);
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	@Override
	@Transactional
	public void deleteOffSchedule(Long bizId)throws Exception{
    	if (bizId != null){
    		Where<OffScheduling> where = new Where<OffScheduling>();
    		where.setSqlSelect("id,biz_id");
    		where.eq("biz_id", bizId);
    		OffScheduling off = selectOne(where);
    		if(off != null){
    			// 在主表中做物理删除，在历史表中做逻辑删除
				OffSchedulingHis offSchedulingHis = offSchedulingHisService.selectById(off.getId());
				physicsDeleteById(off.getId());
				if (null != offSchedulingHis && null != offSchedulingHis.getId()) {
					offSchedulingHisService.deleteById(offSchedulingHis.getId());
				}
    		}
    		
    	}
    }
}
