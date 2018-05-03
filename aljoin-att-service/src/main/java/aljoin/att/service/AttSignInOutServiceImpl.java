package aljoin.att.service;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.att.dao.entity.AttSignInOut;
import aljoin.att.dao.entity.AttSignInOutHis;
import aljoin.att.dao.mapper.AttSignInOutMapper;
import aljoin.att.dao.object.AppAttSignInOutHisDO;
import aljoin.att.dao.object.AppAttSignInOutHisVO;
import aljoin.att.dao.object.AppAttSignInUserInfo;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.att.dao.object.AttSignInOutHisVO;
import aljoin.att.dao.object.AttSignInOutVO;
import aljoin.att.iservice.AttSignInOutHisService;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.CustomerTaskDefinition;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.ExcelUtil;
import aljoin.websocket.WebSocketMsg;
import aljoin.websocket.service.WebSocketService;

/**
 * 
 * 签到、退表(服务实现类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-27
 */
@Service
public class AttSignInOutServiceImpl extends ServiceImpl<AttSignInOutMapper, AttSignInOut>
    implements AttSignInOutService {
    private final static Logger logger = LoggerFactory.getLogger(AttSignInOutServiceImpl.class);

    @Resource
    private AttSignInOutHisService attSignInOutHisService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private AttSignInOutMapper attSignInOutMapper;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private TaskService taskService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private ActFixedFormServiceImpl actFixedFormService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private HistoryService historyService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private SysDataDictService sysDataDictService;
    // @Resource
    // private AutDataStatisticsService autDataStatisticsService;

    private static String currMonth = "";

    @Override
    public Page<AttSignInOut> list(PageBean pageBean, AttSignInOutVO obj) throws Exception {
        Where<AttSignInOut> where = new Where<AttSignInOut>();
        List<AttSignInOutHis> hisList = new ArrayList<AttSignInOutHis>();
        if (null != obj) {
            // 本周
            if (StringUtils.isNotEmpty(obj.getThisWeek())) {
                where.between("sign_date",
                    DateUtil.str2dateOrTime(DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(DateUtil.getLastDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
            }
            // 本月
            if (StringUtils.isNotEmpty(obj.getThisMonth())) {
                where.between("sign_date",
                    DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
            }
            // 上周
            if (StringUtils.isNotEmpty(obj.getLastWeek())) {
                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                hisWhere.between("sign_date",
                    DateUtil.str2dateOrTime(DateUtil.getFirstDateOfLastWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(DateUtil.getLastDateOfLastWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
                hisList = attSignInOutHisService.selectList(hisWhere);
            }
            // 下周
            if (StringUtils.isNotEmpty(obj.getNextWeek())) {
                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                hisWhere.between("sign_date",
                    DateUtil.str2dateOrTime(DateUtil.getFirstDateOfNextWeek(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(DateUtil.getLastDateOfNextWeek(WebConstant.PATTERN_BAR_YYYYMMDD)));
                hisList = attSignInOutHisService.selectList(hisWhere);
            }
            where.eq("sign_user_id", obj.getCreateUserId());
        }
        where.orderBy("sign_date", true);
        Page<AttSignInOut> page
            = selectPage(new Page<AttSignInOut>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        if (null != hisList && !hisList.isEmpty()) {
            List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
            for (AttSignInOutHis his : hisList) {
                AttSignInOut attSignInOut = new AttSignInOut();
                if (null != his) {
                    BeanUtils.copyProperties(his, attSignInOut);
                }
                signInOutList.add(attSignInOut);
            }
            page.setRecords(signInOutList);
        }
        return page;
    }

    @Override
    @Transactional
    public RetMsg add() throws Exception {
        RetMsg retMsg = new RetMsg();
        List<AttSignInOut> attSignInOutList = new ArrayList<AttSignInOut>();
        Where<AutUser> where = new Where<AutUser>();
        where.setSqlSelect("id,user_name,full_name");
        List<AutUser> userList = autUserService.selectList(where);
        int curMonthDays = DateUtil.getDaysOfMonth();
        String partten = WebConstant.PATTERN_BAR_YYYYMMDD;
        DateTimeFormatter format = DateTimeFormat.forPattern(partten);
        if (null != userList && !userList.isEmpty()) {

            SysParameter amSignIn = sysParameterService.selectBykey("am_allow_late");
            SysParameter pmSignIn = sysParameterService.selectBykey("pm_allow_late");
            SysParameter amSignOut = sysParameterService.selectBykey("am_leave_early");
            SysParameter pmSignOut = sysParameterService.selectBykey("pm_leave_early");

            for (AutUser user : userList) {
                for (int i = 0; i < curMonthDays; i++) {
                    AttSignInOut attSignInOut = new AttSignInOut();
                    attSignInOut.setSignUserId(user.getId());// 签到用户ID
                    attSignInOut.setSignUserName(user.getUserName());// 签到用户账号
                    DateTime dateTime = DateTime.parse(DateUtil.getFirstDateOfMonth(partten), format);
                    Date signDate = dateTime.plusDays(i).toDate();
                    attSignInOut.setSignDate(signDate);
                    String week = DateUtil.getWeek(signDate);// 获得星期几
                    attSignInOut.setSignWeek(week);// 星期几
                    boolean isWorkDay = getIsWorkDay(signDate);
                    if (!isWorkDay) {
                        attSignInOut.setIsWorkDay(0);
                    } else {
                        attSignInOut.setIsWorkDay(1);
                    }

                    if (null == amSignIn) {
                        retMsg.setCode(1);
                        retMsg.setMessage(WebConstant.ATT_AM_ALLOWLATE_ERRORMSG);
                        return retMsg;
                    } else {
                        if (null != amSignIn && StringUtils.isNotEmpty(amSignIn.getParamValue())) {
                            attSignInOut.setAmSignInBufferTime(Integer.parseInt(amSignIn.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                        }
                    }

                    if (null == amSignOut) {
                        retMsg.setCode(1);
                        retMsg.setMessage(WebConstant.ATT_AM_LEAVEEARLY_ERRORMSG);
                        return retMsg;
                    } else {
                        if (null != amSignOut && StringUtils.isNotEmpty(amSignOut.getParamValue())) {
                            attSignInOut.setAmSignOutBufferTime(Integer.parseInt(amSignOut.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                        }
                    }

                    if (null == pmSignIn) {
                        retMsg.setCode(1);
                        retMsg.setMessage(WebConstant.ATT_PM_ALLOWLATE_ERRORMSG);
                        return retMsg;
                    } else {
                        if (null != pmSignIn && StringUtils.isNotEmpty(pmSignIn.getParamValue())) {
                            attSignInOut.setPmSignInBufferTime(Integer.parseInt(pmSignIn.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                        }
                    }

                    if (null == pmSignOut) {
                        retMsg.setCode(1);
                        retMsg.setMessage(WebConstant.ATT_PM_LEAVEEARLY_ERRORMSG);
                        return retMsg;
                    } else {
                        if (null != pmSignOut && StringUtils.isNotEmpty(pmSignOut.getParamValue())) {
                            attSignInOut.setPmSignOutBufferTime(Integer.parseInt(pmSignOut.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                        }
                    }
                    attSignInOut.setAmSignInStatus(3);// 上午签到状态
                    attSignInOut.setAmSignInPatchStatus(1);// 上午签到补签状态
                    if (StringUtils.isNotEmpty(amSignIn.getParamValue())) {
                        attSignInOut.setAmSignInBufferTime(Integer.parseInt(amSignIn.getParamValue()));// 上午签到缓冲时间
                    }

                    attSignInOut.setAmSignOutStatus(3);// 上午签退状态
                    attSignInOut.setAmSignOutPatchStatus(1);// 上午签退补签状态
                    if (StringUtils.isNotEmpty(amSignOut.getParamValue())) {
                        attSignInOut.setAmSignInBufferTime(Integer.parseInt(amSignOut.getParamValue()));// 上午签退缓冲时间
                    }
                    attSignInOut.setPmSignInStatus(3);// 下午签到状态
                    attSignInOut.setPmSignInPatchStatus(1);// 下午签到补签状态
                    if (StringUtils.isNotEmpty(pmSignIn.getParamValue())) {
                        attSignInOut.setAmSignInBufferTime(Integer.parseInt(pmSignIn.getParamValue()));// 下午签到缓冲时间
                    }
                    if (StringUtils.isNotEmpty(pmSignOut.getParamValue())) {
                        attSignInOut.setPmSignOutBufferTime(Integer.parseInt(pmSignOut.getParamValue()));// 下午签退缓冲时间
                    }
                    attSignInOut.setPmSignOutStatus(3);// 下午签退状态
                    attSignInOut.setPmSignOutPatchStatus(1);// 下午签退补签时间

                    attSignInOutList.add(attSignInOut);
                }
            }
        }
        if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
            // 删除 att_signin_out表以往的记录
            deleteLastSignInOut();
            // 批量插入本月考勤数据att_signin_out
            insertBatch(attSignInOutList);
            List<AttSignInOutHis> attSignInOutHisList = new ArrayList<AttSignInOutHis>();
            for (AttSignInOut signInOut : attSignInOutList) {
                AttSignInOutHis signInOutHis = new AttSignInOutHis();
                BeanUtils.copyProperties(signInOut, signInOutHis);
                attSignInOutHisList.add(signInOutHis);
            }
            if (null != attSignInOutHisList && !attSignInOutHisList.isEmpty()) {
                // 批量插入历史表 att_signin_out_his
                attSignInOutHisService.insertBatch(attSignInOutHisList);
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    public void deleteLastSignInOut() throws Exception {
        attSignInOutMapper.deleteLastSignInOut();
    }

    @SuppressWarnings("unchecked")
    @Override
    public RetMsg pushMessageToUserList(Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        WebSocketMsg msg = new WebSocketMsg();
        List<String> userNameList = new ArrayList<String>();
        DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_HHMM);
        DateTime dateTime = new DateTime();
        String curPointOfTime = dateTime.toString(WebConstant.PATTERN_BAR_HHMM);
        retMsg = validatePush(format, curPointOfTime, userId);
        if (null != retMsg && null != retMsg.getCode()) {
            if (retMsg.getCode() == 0) {
                if (null != retMsg.getObject()) {
                    Map<String, Object> map = (Map<String, Object>)retMsg.getObject();
                    userNameList = (List<String>)map.get("userNameList");
                    msg.setObject(retMsg.getObject());
                }
                if (null != retMsg.getMessage()) {
                    msg.setCode(WebConstant.SOCKET_CODE_200 + "");
                    msg.setMessage(retMsg.getMessage());
                }
            }
        }

        if (null != msg && null != userNameList && !userNameList.isEmpty()) {
            webSocketService.pushMessageToUserList(msg, userNameList);
        }
        retMsg.setCode(WebConstant.SOCKET_CODE_200);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public AttSignInOutVO signInDetailList(AttSignInOutVO obj) throws Exception {
        List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
        List<AttSignInOutHis> hisList = new ArrayList<AttSignInOutHis>();
        AttSignInOutVO signInOutVO = new AttSignInOutVO();
        List<Long> userIdList = new ArrayList<Long>();
        List<Long> userIdsList = new ArrayList<Long>();
        Map<Long, Integer> rankList = new LinkedHashMap<Long, Integer>();
        // List<AutUser> userList = new ArrayList<AutUser>();
        if (null != obj) {
            int monthDays[] = null;
            String months[] = null;

            Long deptId = obj.getDeptId();
            if (null != deptId) {
                // String month = "";
                // Where<AutDepartmentUser> where2 = new
                // Where<AutDepartmentUser>();
                // where2.eq("dept_id",deptId);
                // where2.setSqlSelect("id,user_id");
                // List<AutDepartmentUser> departmentUserList1 =
                // autDepartmentUserService.selectList(where2);
                AutDepartment au = new AutDepartment();
                au.setId(deptId);
                List<AutUser> departmentUserList1 = autDepartmentService.getChildDeptUserList(au);

                if (null != departmentUserList1 && !departmentUserList1.isEmpty()) {
                    for (AutUser departmentUser : departmentUserList1) {
                        if (null != departmentUser && null != departmentUser.getId()) {
                            userIdList.add(departmentUser.getId());
                        }
                    }
                }

                rankList = autUserService.getUserRankList(userIdList, null);
                if (null != userIdList && !userIdList.isEmpty()) {

                    // Map<Long, Integer> rankList = autUserService.getUserRankList(userIdList, null);
                    // 本月(默认查询本月)
                    if (StringUtils.isNotEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                        && StringUtils.isEmpty(obj.getNextMonth())) {
                        // month =
                        // DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                        currMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM);
                        if (StringUtils.isNotEmpty(currMonth)) {
                            monthDays = new int[] {DateUtil.getDaysOfMonth(DateUtil.getFirstDateOfMonth("yyyy-MM"))};

                            Where<AttSignInOut> where = new Where<AttSignInOut>();
                            where.in("sign_user_id", rankList.keySet());
                            where.setSqlSelect(
                                "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                            where.between("sign_date",
                                DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
                            signInOutList = selectList(where);
                            /*for (Long id : rankList.keySet()) {
                                List<AttSignInOut> signInOut = selectList(where);
                                signInOutList.addAll(signInOut);
                            }*/
                        }
                    }

                    // 上月
                    if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isNotEmpty(obj.getLastMonth())
                        && StringUtils.isEmpty(obj.getNextMonth())) {
                        if (StringUtils.isNotEmpty(currMonth)) {
                            DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                            DateTime d = DateTime.parse(currMonth, format2);
                            String dateStr = d.minusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                            currMonth = dateStr;

                            DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                            String lastBegMonth
                                = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                            String lastEndMonth
                                = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                            DateTime begin = DateTime.parse(lastBegMonth, format);
                            DateTime end = DateTime.parse(lastEndMonth, format);
                            monthDays
                                = new int[] {DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM))};
                            DateTime dateTime = new DateTime();

                            /*for (Long id : rankList.keySet()) {
                              Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                                hisWhere.eq("sign_user_id", id);
                                // month =
                                // DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                                if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                                    hisWhere.between("sign_date",
                                            DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD),
                                            DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                                } else {
                                    hisWhere.between("sign_date", begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD),
                                            end.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                }
                                hisWhere.setSqlSelect(
                                        "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                                List<AttSignInOutHis> signInOut = attSignInOutHisService.selectList(hisWhere);
                                hisList.addAll(signInOut);
                            }*/

                            Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                            hisWhere.in("sign_user_id", rankList.keySet());
                            // month =
                            // DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                            if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                                hisWhere.between("sign_date",
                                    DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                    DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                            } else {
                                hisWhere.between("sign_date",
                                    DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                    DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                            }
                            hisWhere.setSqlSelect(
                                "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                            // hisWhere.orderBy("sign_user_id", true);
                            hisList = attSignInOutHisService.selectList(hisWhere);
                        }
                    }

                    // 下月
                    if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                        && StringUtils.isNotEmpty(obj.getNextMonth())) {
                        if (StringUtils.isNotEmpty(currMonth)) {
                            DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                            DateTime d1 = DateTime.parse(currMonth, format2);
                            String dateStr = d1.plusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                            DateTime d2 = DateTime.parse(currMonth, format2);
                            currMonth = dateStr;
                            DateTime d3
                                = DateTime.parse(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM), format2);
                            if (d2.getMillis() <= d3.getMillis()) {
                                DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                                String lastBegMonth
                                    = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                                String lastEndMonth
                                    = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                                DateTime begin = DateTime.parse(lastBegMonth, format);
                                DateTime end = DateTime.parse(lastEndMonth, format);
                                monthDays = new int[] {
                                    DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM))};
                                DateTime dateTime = new DateTime();

                                /* for (Long id : rankList.keySet()) {
                                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                                hisWhere.eq("sign_user_id", id);
                                if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                                    hisWhere.between("sign_date",
                                            DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD),
                                            DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                                } else {
                                    hisWhere.between("sign_date", begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD),
                                            end.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                }
                                hisWhere.setSqlSelect(
                                        "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                                //                              hisWhere.orderBy("sign_user_id", true);
                                List<AttSignInOutHis> signInOut = attSignInOutHisService.selectList(hisWhere);
                                hisList.addAll(signInOut);
                                //                                hisList = attSignInOutHisService.selectList(hisWhere);
                                 }*/
                                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                                hisWhere.in("sign_user_id", rankList.keySet());
                                if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                                    hisWhere.between("sign_date",
                                        DateUtil
                                            .str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                        DateUtil
                                            .str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                                } else {
                                    hisWhere.between("sign_date",
                                        DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                        DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                                }
                                hisWhere.setSqlSelect(
                                    "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                                // hisWhere.orderBy("sign_user_id", true);
                                hisList = attSignInOutHisService.selectList(hisWhere);
                            }
                        }
                    }

                    // 自选月
                    if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                        && StringUtils.isEmpty(obj.getNextMonth()) && StringUtils.isNotEmpty(obj.getMonthBeg())) {

                        // DateTimeFormatter format2 =
                        // DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                        String dateStr = obj.getMonthBeg();
                        currMonth = dateStr;

                        DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        DateTime begin = DateTime.parse(lastBegMonth, format);
                        DateTime end = DateTime.parse(lastEndMonth, format);
                        monthDays = new int[] {DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM))};
                        DateTime dateTime = new DateTime();

                        /*for (Long id : rankList.keySet()) {
                          Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                            hisWhere.eq("sign_user_id", id);
                            if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                                hisWhere.between("sign_date",
                                        DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD),
                                        DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                            } else {
                                hisWhere.between("sign_date", begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD),
                                        end.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                            }
                            hisWhere.setSqlSelect(
                                    "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week");
                        
                            List<AttSignInOutHis> signInOut = attSignInOutHisService.selectList(hisWhere);
                            hisList.addAll(signInOut);
                        }*/
                        Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                        hisWhere.in("sign_user_id", rankList.keySet());
                        if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                            hisWhere.between("sign_date",
                                DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                        } else {
                            hisWhere.between("sign_date",
                                DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                        }
                        hisWhere.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week");
                        hisList = attSignInOutHisService.selectList(hisWhere);
                    }
                }
            } else {
                // 本月(默认查询本月)
                if (StringUtils.isNotEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                    && StringUtils.isEmpty(obj.getNextMonth())) {
                    currMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM);
                    if (StringUtils.isNotEmpty(currMonth)) {
                        monthDays = new int[] {DateUtil.getDaysOfMonth(DateUtil.getFirstDateOfMonth("yyyy-MM"))};
                        Where<AttSignInOut> where = new Where<AttSignInOut>();
                        where.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                        where.orderBy("sign_user_id", true);
                        // DateTime dateTime = new DateTime();
                        where.between("sign_date",
                            DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                            DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
                        signInOutList = selectList(where);
                    }
                }

                // 上月
                if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isNotEmpty(obj.getLastMonth())
                    && StringUtils.isEmpty(obj.getNextMonth())) {
                    if (StringUtils.isNotEmpty(currMonth)) {
                        DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                        DateTime d = DateTime.parse(currMonth, format2);
                        String dateStr = d.minusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                        currMonth = dateStr;

                        Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                        DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        DateTime begin = DateTime.parse(lastBegMonth, format);
                        DateTime end = DateTime.parse(lastEndMonth, format);
                        monthDays = new int[] {DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM))};
                        DateTime dateTime = new DateTime();
                        if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                            hisWhere.between("sign_date",
                                DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                        } else {
                            hisWhere.between("sign_date",
                                DateUtil.str2dateOrTime(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2dateOrTime(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                        }
                        hisWhere.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                        // hisWhere.orderBy("sign_user_id", true);
                        hisList = attSignInOutHisService.selectList(hisWhere);
                    }
                }

                // 下月
                if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                    && StringUtils.isNotEmpty(obj.getNextMonth())) {
                    if (StringUtils.isNotEmpty(currMonth)) {
                        DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                        DateTime d1 = DateTime.parse(currMonth, format2);
                        String dateStr = d1.plusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                        DateTime d2 = DateTime.parse(currMonth, format2);
                        currMonth = dateStr;
                        DateTime d3
                            = DateTime.parse(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM), format2);
                        if (d2.getMillis() <= d3.getMillis()) {
                            Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                            DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                            String lastBegMonth
                                = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                            String lastEndMonth
                                = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                            DateTime begin = DateTime.parse(lastBegMonth, format);
                            DateTime end = DateTime.parse(lastEndMonth, format);
                            monthDays
                                = new int[] {DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM))};
                            DateTime dateTime = new DateTime();
                            if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                                hisWhere.between("sign_date",
                                    DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD),
                                    DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                            } else {
                                hisWhere.between("sign_date",
                                    DateUtil.str2dateOrTime(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                    DateUtil.str2dateOrTime(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                            }
                            hisWhere.setSqlSelect(
                                "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                            hisWhere.orderBy("sign_user_id", true);
                            hisList = attSignInOutHisService.selectList(hisWhere);
                        }
                    }
                }

                // 自选月
                if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                    && StringUtils.isEmpty(obj.getNextMonth()) && StringUtils.isNotEmpty(obj.getMonthBeg())) {
                    if (StringUtils.isNotEmpty(currMonth)) {
                        // DateTimeFormatter format2 =
                        // DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                        // DateTime d = DateTime.parse(currMonth, format2);
                        String dateStr = obj.getMonthBeg();
                        currMonth = dateStr;

                        DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        DateTime begin = DateTime.parse(lastBegMonth, format);
                        DateTime end = DateTime.parse(lastEndMonth, format);
                        monthDays = new int[] {DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM))};
                        Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                        hisWhere.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week");
                        DateTime dateTime = new DateTime();
                        if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                            hisWhere.between("sign_date",
                                DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                        } else {
                            hisWhere.between("sign_date",
                                DateUtil.str2dateOrTime(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2dateOrTime(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                        }
                        hisList = attSignInOutHisService.selectList(hisWhere);
                    }
                }
            }

            List<String> theadList = new ArrayList<String>();
            List<Map<String, Map<String, Object>>> listMap = new ArrayList<Map<String, Map<String, Object>>>();
            Map<String, Map<String, Object>> maps = new LinkedHashMap<String, Map<String, Object>>();
            if (null != signInOutList && !signInOutList.isEmpty()) {
                int count = 1;
                int i = 0;
                if (null != monthDays && monthDays.length >= 1) {
                    for (int month : monthDays) {
                        Map<String, Object> map = new LinkedHashMap<String, Object>();
                        Map<String, Object> map2 = null;
                        for (Long id : rankList.keySet()) {
                            for (AttSignInOut signInOut : signInOutList) {
                                if (!id.equals(signInOut.getSignUserId())) {
                                    continue;
                                }
                                if (!userIdsList.contains(signInOut.getSignUserId())) {
                                    userIdsList.add(signInOut.getSignUserId());
                                }
                                DateTime dateTime = new DateTime(signInOut.getSignDate());
                                String date = dateTime.toString("MM月dd日");

                                date = date + " " + signInOut.getSignWeek();

                                theadList.add(date);

                                List<String> tdList = new ArrayList<String>();
                                tdList.add(null == signInOut.getAmSignInStatus() ? "0"
                                    : String.valueOf(signInOut.getAmSignInStatus()));
                                tdList.add(null == signInOut.getAmSignOutStatus() ? "0"
                                    : String.valueOf(signInOut.getAmSignOutStatus()));
                                tdList.add(null == signInOut.getPmSignInStatus() ? "0"
                                    : String.valueOf(signInOut.getPmSignInStatus()));
                                tdList.add(null == signInOut.getPmSignOutStatus() ? "0"
                                    : String.valueOf(signInOut.getPmSignOutStatus()));
                                tdList.add(
                                    null == signInOut.getAmSignInIp() ? "" : String.valueOf(signInOut.getAmSignInIp()));
                                tdList.add(null == signInOut.getAmSignOutIp() ? ""
                                    : String.valueOf(signInOut.getAmSignOutIp()));
                                tdList.add(
                                    null == signInOut.getPmSignInIp() ? "" : String.valueOf(signInOut.getPmSignInIp()));
                                tdList.add(null == signInOut.getPmSignOutIp() ? ""
                                    : String.valueOf(signInOut.getPmSignOutIp()));

                                map.put(date, tdList);
                                if (count % month == 0 && count != 1) {
                                    Where<AutUser> userWhere = new Where<AutUser>();
                                    userWhere.eq("id", signInOut.getSignUserId());
                                    userWhere.setSqlSelect("id,user_name,full_name");
                                    AutUser user = autUserService.selectOne(userWhere);
                                    if (null != user && StringUtils.isNotEmpty(user.getFullName())) {
                                        if (null != map && !map.isEmpty()) {
                                            map2 = new LinkedHashMap<String, Object>();
                                            for (String key : map.keySet()) {
                                                map2.put(key, map.get(key));
                                            }
                                            map.clear();
                                        }
                                        maps.put(user.getFullName() + "(" + i + ")", map2);
                                        i++;
                                    }
                                }
                                count++;
                            }
                        }
                    }
                    listMap.add(maps);
                }
            }

            if (null != hisList && !hisList.isEmpty()) {
                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> map2 = null;
                int count = 1;
                int index = 0;
                int i = 0;
                if (null != monthDays && monthDays.length >= 1) {
                    for (int month : monthDays) {
                        for (Long id : rankList.keySet()) {
                            for (AttSignInOutHis signInOut : hisList) {
                                if (!id.equals(signInOut.getSignUserId())) {
                                    continue;
                                }
                                if (!userIdsList.contains(signInOut.getSignUserId())) {
                                    userIdsList.add(signInOut.getSignUserId());
                                }
                                DateTime dateTime = new DateTime(signInOut.getSignDate());
                                String date = dateTime.toString("MM月dd日");

                                date = date + " " + signInOut.getSignWeek();

                                theadList.add(date);

                                List<String> tdList = new ArrayList<String>();
                                tdList.add(
                                    signInOut.getAmSignInStatus() == null ? "0" : signInOut.getAmSignInStatus() + "");
                                tdList.add(
                                    signInOut.getAmSignOutStatus() == null ? "0" : signInOut.getAmSignOutStatus() + "");
                                tdList.add(
                                    signInOut.getPmSignInStatus() == null ? "0" : signInOut.getPmSignInStatus() + "");
                                tdList.add(
                                    signInOut.getPmSignOutStatus() == null ? "0" : signInOut.getPmSignOutStatus() + "");

                                tdList.add(signInOut.getAmSignInIp() == null ? "" : signInOut.getAmSignInIp() + "");
                                tdList.add(signInOut.getAmSignOutIp() == null ? "" : signInOut.getAmSignOutIp() + "");
                                tdList.add(signInOut.getPmSignInIp() == null ? "" : signInOut.getPmSignInIp() + "");
                                tdList.add(signInOut.getPmSignOutIp() == null ? "" : signInOut.getPmSignOutIp() + "");

                                map.put(date, tdList);
                                DateTime signDate = new DateTime(signInOut.getSignDate());
                                if (count != 1 && (count % month == 0)) {
                                    if (null != months) {
                                        if ((months[index].equals(signDate.toString("yyyy-MM")))) {
                                            Where<AutUser> userWhere = new Where<AutUser>();
                                            userWhere.eq("id", signInOut.getSignUserId());
                                            userWhere.setSqlSelect("id,user_name,full_name");
                                            AutUser user = autUserService.selectOne(userWhere);
                                            if (null != map && !map.isEmpty()) {
                                                map2 = new HashMap<String, Object>();
                                                for (String key : map.keySet()) {
                                                    map2.put(key, map.get(key));
                                                }
                                                map.clear();
                                            }
                                            maps.put(user.getFullName() + "(" + i + ")", map2);
                                            i++;
                                        }
                                    } else {
                                        Where<AutUser> userWhere = new Where<AutUser>();
                                        userWhere.eq("id", signInOut.getSignUserId());
                                        userWhere.setSqlSelect("id,user_name,full_name");
                                        AutUser user = autUserService.selectOne(userWhere);
                                        if (null != map && !map.isEmpty()) {
                                            map2 = new HashMap<String, Object>();
                                            for (String key : map.keySet()) {
                                                map2.put(key, map.get(key));
                                            }
                                            map.clear();
                                        }
                                        maps.put(user.getFullName() + "(" + i + ")", map2);
                                        i++;
                                    }
                                }
                                count++;
                            }
                        }
                        index++;
                    }
                    listMap.add(maps);
                }
            }
            /*
             * if (null != userIdsList && !userIdsList.isEmpty()) { HashSet h =
             * new HashSet(userIdsList); userIdsList.clear();
             * userIdsList.addAll(h);
             * 
             * Where<AutUser> userWhere = new Where<AutUser>();
             * userWhere.setSqlSelect("id,user_name,full_name");
             * userWhere.in("id", userIdsList); userList =
             * autUserService.selectList(userWhere); }
             */
            if (null != theadList && !theadList.isEmpty()) {
                HashSet<String> h = new HashSet<String>(theadList);
                theadList.clear();
                theadList.addAll(h);
                Collections.sort(theadList);
                signInOutVO.setTheadList(theadList);
            }
            if (null != listMap && !listMap.isEmpty()) {
                // Map<String, Object> tdMap = new HashMap<String, Object>();
                // Map<String, Map<String, Object>> tdMaps = new HashMap<String,
                // Map<String, Object>>();
                // List<Map<String, Map<String, Object>>> listMaps = new
                // ArrayList<Map<String, Map<String, Object>>>();

                /*
                 * if(null != userList && !userList.isEmpty()){ for(Map<String,
                 * Map<String, Object>> map : listMap){ for(String key :
                 * map.keySet()){ for(AutUser user : userList){ if(null != user
                 * && null != key && null != user.getId()){ String userId =
                 * String.valueOf(user.getId()); if(key.equals(userId)){ tdMap =
                 * map.get(key); tdMaps.put(user.getFullName(),tdMap); } } } } }
                 * listMaps.add(tdMaps); }
                 */
                signInOutVO.setTdMaps(listMap);
            }
        }
        return signInOutVO;
    }

    /*
     * huanghz 1、涉及到当月考勤表att_sign_in_out：只记录当月所有人员每天的打卡信息； att_sign_in_out_his：人员打卡历史记录；
     * 2、根据当月日期在后台计算上月、下月日期条件； 决定查询当月表还是历史表； ==》优化点1 3、考虑兼容oracle与mysql，直接后台关联人员排序表，然后返回分页记录对象 ；
     * 否则根据人员排序查询出翻页数据，然后根据日期决定从哪张考勤表中查询数据； ==》优化点2 根据人员ID去查询时，不要遍历查询db表； 4、 优化点3就是 分页
     * 把数据展示给前端；否则数据量大；
     * 
     * 测试时，如果历史或者当前打卡记录表中无打卡记录数据的情况兼容； 再是根据日期排序时，各个人员每天的打卡记录数据情况要跟日期顺序一一天应； 
     */
    /* @SuppressWarnings("unused")
    @Override
    public Page<AttSignInOutVO> signInDetailListPage(PageBean pageBean, AttSignInOutVO obj)
        throws Exception {
      Page<AttSignInOutVO> page =new Page<AttSignInOutVO>(pageBean.getPageNum(), pageBean.getPageSize()); // 暂时定义；
    
      List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
      List<AttSignInOutHis> attSignInOutHisList = new ArrayList<AttSignInOutHis>();
      AttSignInOutVO signInOutVO = new AttSignInOutVO();
      List<Long> userIdList = new ArrayList<Long>();
      Map<Long, Integer> userIdRankMap = new LinkedHashMap<Long, Integer>();
      int pageTotal = 0 ;
      
      if (null != obj) {
        int monthDays = 0;
        Long deptId = obj.getDeptId();
        if (null != deptId) {
          List<AutUser> autUserList = autDepartmentService.getDeptAndChildForUserListByDeptId(deptId);
          if (null != autUserList && !autUserList.isEmpty()) {
            for (AutUser autUser : autUserList) {
              userIdList.add(autUser.getId());
            }
          }
          // 选择指定部门返回当前部门及其子部门下的全部人员进行排序，并返回翻页数据；限制返回值提高查询效率；
          Page<AutUserRank> autUserRankPage = autUserService.getUserRankPage(pageBean, userIdList);
          pageTotal = autUserRankPage.getTotal();
          List<AutUserRank> autUserRankList = autUserRankPage.getRecords();
          int irank = 1;
          for (AutUserRank autUserRank : autUserRankList) {
            userIdRankMap.put(autUserRank.getId(), irank++);
          }
    
          if (null != userIdRankMap && !userIdRankMap.isEmpty()) {
            // 本月(默认查询本月)
            if (StringUtils.isNotEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                && StringUtils.isEmpty(obj.getNextMonth())) {
                currMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM); //session范围才对； 
                monthDays = DateUtil.getDaysOfMonth(DateUtil.getFirstDateOfMonth("yyyy-MM")); //指变量currMonth的天数； 
    
                Where<AttSignInOut> attSignInOutwhere = new Where<AttSignInOut>();
                attSignInOutwhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                attSignInOutwhere.in("sign_user_id", userIdRankMap.keySet());
                attSignInOutwhere.between("sign_date", DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)), 
                    DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
                signInOutList = selectList(attSignInOutwhere); //要根据userIdRankMap对查询结果标注上排序顺序； [默认要人员id排序，日期排序； ]
            }
    
            // 上月
            if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isNotEmpty(obj.getLastMonth())
                && StringUtils.isEmpty(obj.getNextMonth())) {
              if (StringUtils.isNotEmpty(currMonth)) {
                DateTime curDT = DateTime.parse(currMonth, DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM));
                
                String lastDTStr = curDT.minusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                
                currMonth = lastDTStr;
    
                DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                String lastBegMonthStr = DateUtil.getFirstDateOfMonth(lastDTStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                String lastEndMonthStr = DateUtil.getLastDateOfMonth(lastDTStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                DateTime lastBeginDT = DateTime.parse(lastBegMonthStr, format);
                DateTime lastEndDT = DateTime.parse(lastEndMonthStr, format);
                monthDays = DateUtil.getDaysOfMonth(lastBeginDT.toString(WebConstant.PATTERN_BAR_YYYYMM));
                
                DateTime currentDT = new DateTime();
                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                hisWhere.in("sign_user_id", userIdRankMap.keySet());
                if (currMonth.equals(currentDT.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                  hisWhere.between("sign_date", DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)), 
                      DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                } else {
                  hisWhere.between("sign_date",DateUtil.str2date(lastBeginDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                      DateUtil.str2date(lastEndDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                }
                hisWhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                // hisWhere.orderBy("sign_user_id", true);
                attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);
              }
            }
    
            // 下月
            if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth()) && StringUtils.isNotEmpty(obj.getNextMonth())) {
              if (StringUtils.isNotEmpty(currMonth)) {
                DateTimeFormatter curYMdtf = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                DateTime curYMdt = DateTime.parse(currMonth, curYMdtf);
                String nextYMStr = curYMdt.plusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                DateTime d2 = DateTime.parse(currMonth, curYMdtf);
                currMonth = nextYMStr;
                DateTime d3 = DateTime.parse(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM),curYMdtf);
                if (d2.getMillis() <= d3.getMillis()) {
                  DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                  String lastBegMonth = DateUtil.getFirstDateOfMonth(nextYMStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                  String lastEndMonth = DateUtil.getLastDateOfMonth(nextYMStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                  DateTime begin = DateTime.parse(lastBegMonth, format);
                  DateTime end = DateTime.parse(lastEndMonth, format);
                  monthDays =DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM));
                  DateTime dateTime = new DateTime();
                  Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                  hisWhere.in("sign_user_id", userIdRankMap.keySet());
                  if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                    hisWhere.between("sign_date", DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                        DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
                  } else {
                    hisWhere.between("sign_date",DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                        DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                  }
                  hisWhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                  // hisWhere.orderBy("sign_user_id", true);
                  attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);
                }
              }
            }
    
            // 自选月
            if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                && StringUtils.isEmpty(obj.getNextMonth())
                && StringUtils.isNotEmpty(obj.getMonthBeg())) {
              String dateStr = obj.getMonthBeg();
              currMonth = dateStr;
    
              DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
              String lastBegMonth =
                  DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
              String lastEndMonth =
                  DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
              DateTime begin = DateTime.parse(lastBegMonth, format);
              DateTime end = DateTime.parse(lastEndMonth, format);
              monthDays =DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM));
              DateTime dateTime = new DateTime();
              Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
              hisWhere.in("sign_user_id", userIdRankMap.keySet());
              if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                hisWhere.between("sign_date", DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)), 
                    DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
              } else {
                hisWhere.between("sign_date",DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
              }
              hisWhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week");
              attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);
            }
          }
        } else {
          // 本月(默认查询本月)
          if (StringUtils.isNotEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth()) && StringUtils.isEmpty(obj.getNextMonth())) {
            currMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM);
            if (StringUtils.isNotEmpty(currMonth)) {
              monthDays = DateUtil.getDaysOfMonth(DateUtil.getFirstDateOfMonth("yyyy-MM"));
              
              //抽取：根据当前历史标识，开始、结束日期字符串查询用户ID；
              List<AttSignInOut> attSignInOutToIdList = new ArrayList<AttSignInOut>();
              HashSet<Long> idSet = new HashSet<Long>();
              Where<AttSignInOut> attSignInOutIdWhere = new Where<AttSignInOut>();
              attSignInOutIdWhere.setSqlSelect("id");
              attSignInOutIdWhere.between("sign_date",DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                  DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
              attSignInOutToIdList = selectList(attSignInOutIdWhere);
              for (AttSignInOut attSignInOut : attSignInOutToIdList) {
                idSet.add(attSignInOut.getId());
              }
              //根据人员id条件查询人员排序分页数据 ； 
              userIdRankMap = getUserIdRank(pageBean, new ArrayList<Long>(idSet));
              pageTotal = userIdRankMap.get(0L);
              //查询分页排序数据； 
              Where<AttSignInOut> where = new Where<AttSignInOut>();
              where.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
              where.in("sign_user_id", userIdRankMap.keySet());
              where.between("sign_date",DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                  DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
    //              where.orderBy("sign_user_id", true); //要根据人员排序表排序； 
              signInOutList = selectList(where);
              
            }
          }
          //对历史表查询的合并；    
          // 上月
          if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isNotEmpty(obj.getLastMonth()) && StringUtils.isEmpty(obj.getNextMonth())) {
            if (StringUtils.isNotEmpty(currMonth)) {
              DateTimeFormatter curYMformatter = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
              DateTime currYM = DateTime.parse(currMonth, curYMformatter);
              String currYMStr = currYM.minusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
              currMonth = currYMStr;
              DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
              String lastBegMonthStr = DateUtil.getFirstDateOfMonth(currYMStr, WebConstant.PATTERN_BAR_YYYYMMDD);
              String lastEndMonthStr = DateUtil.getLastDateOfMonth(currYMStr, WebConstant.PATTERN_BAR_YYYYMMDD);
              DateTime lastBeginDT = DateTime.parse(lastBegMonthStr, format);
              DateTime lastEndDT = DateTime.parse(lastEndMonthStr, format);
              monthDays =DateUtil.getDaysOfMonth(lastBeginDT.toString(WebConstant.PATTERN_BAR_YYYYMM));
    
              HashSet<Long> idSet = getUserIdFromHis(lastBeginDT, lastEndDT);
              //根据人员id条件查询人员排序分页数据 ； 
              userIdRankMap = getUserIdRank(pageBean, new ArrayList<Long>(idSet));
              pageTotal = userIdRankMap.get(0L);
              DateTime dateTime = new DateTime();
              Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
              hisWhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
              hisWhere.in("sign_user_id", userIdRankMap.keySet());
              if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                hisWhere.between("sign_date", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)), 
                    DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
              } else {
                hisWhere.between("sign_date", DateUtil.str2dateOrTime(lastBeginDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(lastEndDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
              }
              // hisWhere.orderBy("sign_user_id", true);
              attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);
            }
          }
    
          // 下月
          if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth()) && StringUtils.isNotEmpty(obj.getNextMonth())) {
            if (StringUtils.isNotEmpty(currMonth)) {
              DateTimeFormatter formatterYM = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
              DateTime tempCurrYM = DateTime.parse(currMonth, formatterYM);
              String lastYMStr = tempCurrYM.plusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
              DateTime varCurrMonthYM = DateTime.parse(currMonth, formatterYM);
              currMonth = lastYMStr;
              DateTime currMonthYM = DateTime.parse(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM),formatterYM);
              if (varCurrMonthYM.getMillis() <= currMonthYM.getMillis()) {
                DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                String lastBegMonthStr = DateUtil.getFirstDateOfMonth(lastYMStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                String lastEndMonthStr = DateUtil.getLastDateOfMonth(lastYMStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                DateTime lastMonthBegDT = DateTime.parse(lastBegMonthStr, format);
                DateTime lastMonthEndDT = DateTime.parse(lastEndMonthStr, format);
                monthDays = DateUtil.getDaysOfMonth(lastMonthBegDT.toString(WebConstant.PATTERN_BAR_YYYYMM));
                
                HashSet<Long> idSet = getUserIdFromHis(lastMonthBegDT, lastMonthEndDT);
                //根据人员id条件查询人员排序分页数据 ； 
                userIdRankMap = getUserIdRank(pageBean, new ArrayList<Long>(idSet));
                pageTotal = userIdRankMap.get(0L);
                DateTime dateTime = new DateTime();
                Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
                hisWhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                hisWhere.in("sign_user_id", userIdRankMap.keySet());
                if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                  hisWhere.between("sign_date",DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD),
                      DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));
                } else {
                  hisWhere.between("sign_date", DateUtil.str2dateOrTime(lastMonthBegDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                      DateUtil.str2dateOrTime(lastMonthEndDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                }
                
    //                hisWhere.orderBy("sign_user_id", true);
                attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);
              }
            }
          }
    
          // 自选月
          if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth()) && StringUtils.isEmpty(obj.getNextMonth()) && StringUtils.isNotEmpty(obj.getMonthBeg())) {
            if (StringUtils.isNotEmpty(currMonth)) {
              String dateStr = obj.getMonthBeg();
              currMonth = dateStr;
    
              DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
              String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
              String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
              DateTime begin = DateTime.parse(lastBegMonth, format);
              DateTime end = DateTime.parse(lastEndMonth, format);
              monthDays = DateUtil.getDaysOfMonth(begin.toString(WebConstant.PATTERN_BAR_YYYYMM));
              
              HashSet<Long> idSet = getUserIdFromHis(begin, end);
              //根据人员id条件查询人员排序分页数据 ； 
              userIdRankMap = getUserIdRank(pageBean, new ArrayList<Long>(idSet));
              pageTotal = userIdRankMap.get(0L);
              DateTime dateTime = new DateTime();
              Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
              hisWhere.setSqlSelect("id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week");
              hisWhere.in("sign_user_id", userIdRankMap.keySet());
              if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                hisWhere.between("sign_date", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
              } else {
                hisWhere.between("sign_date",DateUtil.str2dateOrTime(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                    DateUtil.str2dateOrTime(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
              }
              attSignInOutHisList = attSignInOutHisService.selectList(hisWhere);
            }
          }
        }
    
        //无管是当前签到签退表还是历史表，都要根据翻页记录条件查询到对应的人员名称； 
        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.setSqlSelect("id,user_name,full_name");
        userWhere.in("id", userIdRankMap.keySet());
        List<AutUser> autUserList = autUserService.selectList(userWhere);
        Map<Long,String> userIdNameMap = new HashMap<Long, String>();
        for (AutUser autUser : autUserList) {
          userIdNameMap.put(autUser.getId(), autUser.getFullName());
        }
        
        List<String> theadList = new ArrayList<String>();
        List<Map<String, Map<String, Object>>> listMap = new ArrayList<Map<String, Map<String, Object>>>();
        Map<String, Map<String, Object>> userDaySignMap = new LinkedHashMap<String, Map<String, Object>>();
        // 当月签入签退表中的数据； for (AttSignInOut signInOut : signInOutList) { 类型不一样，代码都一样； 
        if (null != signInOutList && !signInOutList.isEmpty()) {  //假如当前月份没有打卡记录
          Map<String, Object> map = null;
          for(int day = 1;day < monthDays;day++){
            for (AttSignInOut signInOut : signInOutList) {
              if(signInOut.getSignDate().getDay() != day){ //以确保底下的map数据是按日期顺序赋值的。
                continue;
              }
              DateTime dateTime = new DateTime(signInOut.getSignDate());
              String date = dateTime.toString("MM月dd日");
              date = date + " " + signInOut.getSignWeek();
              theadList.add(date); 
    
              List<String> tdList = new ArrayList<String>();
              tdList.add(null == signInOut.getAmSignInStatus() ? "0" : String.valueOf(signInOut.getAmSignInStatus()));
              tdList.add(null == signInOut.getAmSignOutStatus() ? "0" : String.valueOf(signInOut.getAmSignOutStatus()));
              tdList.add(null == signInOut.getPmSignInStatus() ? "0" : String.valueOf(signInOut.getPmSignInStatus()));
              tdList.add(null == signInOut.getPmSignOutStatus() ? "0" : String.valueOf(signInOut.getPmSignOutStatus()));
              tdList.add(null == signInOut.getAmSignInIp() ? "" : String.valueOf(signInOut.getAmSignInIp()));
              tdList.add(null == signInOut.getAmSignOutIp() ? "" : String.valueOf(signInOut.getAmSignOutIp()));
              tdList.add(null == signInOut.getPmSignInIp() ? "" : String.valueOf(signInOut.getPmSignInIp()));
              tdList.add(null == signInOut.getPmSignOutIp() ? "" : String.valueOf(signInOut.getPmSignOutIp()));
              map = new LinkedHashMap<String, Object>();
              map.put(date, tdList);
              String persionKey = userIdNameMap.get(signInOut.getSignUserId()) + "(" + userIdRankMap.get(signInOut.getSignUserId()) + ")" ;
              Map<String, Object> dayDataMap = userDaySignMap.get(persionKey);
              if(dayDataMap == null){
                userDaySignMap.put(persionKey, map);
              }else{
                dayDataMap.putAll(map);
                userDaySignMap.put(persionKey,dayDataMap);  
              }
            }
          }
          listMap.add(userDaySignMap);
        }
        // 历史表中签入签退的数据；
        if (null != attSignInOutHisList && !attSignInOutHisList.isEmpty()) { //历史表中某个月没有人员打卡记录时，根据人员排序表的顺序默认都显示未打卡； 
          Map<String, Object> map =null;
          for(int day = 1;day < monthDays;day++){ //对31天的遍历；huanghz 
            for (AttSignInOutHis signInOut : attSignInOutHisList) {
              if(signInOut.getSignDate().getDay() != day){ //以确保底下的map数据是按日期顺序赋值的。
                continue;
              }
              DateTime dateTime = new DateTime(signInOut.getSignDate());
              String date = dateTime.toString("MM月dd日");
              date = date + " " + signInOut.getSignWeek();
              theadList.add(date);
    
              List<String> tdList = new ArrayList<String>();
              tdList.add(signInOut.getAmSignInStatus() == null ? "0" : signInOut.getAmSignInStatus() + "");
              tdList.add(signInOut.getAmSignOutStatus() == null ? "0" : signInOut.getAmSignOutStatus() + "");
              tdList.add(signInOut.getPmSignInStatus() == null ? "0" : signInOut.getPmSignInStatus() + "");
              tdList.add(signInOut.getPmSignOutStatus() == null ? "0" : signInOut.getPmSignOutStatus() + "");
              tdList.add(signInOut.getAmSignInIp() == null ? "" : signInOut.getAmSignInIp() + "");
              tdList.add(signInOut.getAmSignOutIp() == null ? "" : signInOut.getAmSignOutIp() + "");
              tdList.add(signInOut.getPmSignInIp() == null ? "" : signInOut.getPmSignInIp() + "");
              tdList.add(signInOut.getPmSignOutIp() == null ? "" : signInOut.getPmSignOutIp() + "");
              map = new LinkedHashMap<String, Object>();
              map.put(date, tdList);
              String persionKey = userIdNameMap.get(signInOut.getSignUserId()) + "(" + userIdRankMap.get(signInOut.getSignUserId()) + ")" ;
              Map<String, Object> dayDataMap = userDaySignMap.get(persionKey);
              if(dayDataMap == null){
                userDaySignMap.put(persionKey, map);
              }else{
                dayDataMap.putAll(map);
                userDaySignMap.put(persionKey,dayDataMap);  
              }
    //              userDaySignMap.put(userIdNameMap.get(signInOut.getSignUserId()) + "(" + userIdRankMap.get(signInOut.getSignUserId()) + ")", map);
            }
          }
          listMap.add(userDaySignMap);
        }
        
        if (null != theadList && !theadList.isEmpty()) {
          HashSet<String> uniquenessHead = new HashSet<String>(theadList);
          theadList.clear();
          theadList.addAll(uniquenessHead);
          Collections.sort(theadList);
          signInOutVO.setTheadList(theadList);
        }
        if (null != listMap && !listMap.isEmpty()) { //测试确认人员打卡记录与日期重新排序一致？？？ 
          signInOutVO.setTdMaps(listMap);
        }
      }
      // ====================================
      List<AttSignInOutVO> attSignInOutVOList = new ArrayList<AttSignInOutVO>();
      attSignInOutVOList.add(signInOutVO);
      page.setRecords(attSignInOutVOList);
      
      //暂时修改为如果所有人没有打卡记录，总条数显示为0 
      if(signInOutVO.getTdMaps() == null || signInOutVO.getTdMaps().isEmpty()){
        page.setTotal(0);
      }else{
        page.setTotal(pageTotal);
      }
    
      return page;
    }*/

    @SuppressWarnings({"unchecked", "deprecation"})
    @Override
    public Page<AttSignInOutVO> signInDetailListPage(PageBean pageBean, AttSignInOutVO obj) throws Exception {
        Page<AttSignInOutVO> page = new Page<AttSignInOutVO>(pageBean.getPageNum(), pageBean.getPageSize()); // 暂时定义；

        List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
        List<AttSignInOutHis> attSignInOutHisList = new ArrayList<AttSignInOutHis>();
        AttSignInOutVO signInOutVO = new AttSignInOutVO();
        List<Long> userIdList = new ArrayList<Long>();
        Date beginSignDate = null;
        Date endSignDate = null;
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        Map<Long, Integer> userIdRankMap = new LinkedHashMap<Long, Integer>();
        int pageTotal = 0;

        if (null != obj) {
            int monthDays = 0;
            Long deptId = obj.getDeptId();
            if (null != deptId) {
                // 指定部门及其子部门下的人部人员
                List<AutUser> autUserList = autDepartmentService.getDeptAndChildForUserListByDeptId(deptId);
                if (null != autUserList && !autUserList.isEmpty()) {
                    for (AutUser autUser : autUserList) {
                        userIdList.add(autUser.getId());
                    }
                }
            }
            if (StringUtils.isNotEmpty(obj.getThisMonth())) {
                beginSignDate = DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));
                endSignDate = DateUtil.str2date(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD));
                monthDays = DateUtil.getDaysOfMonth(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM));
            } else {
                String queryYM = obj.getQueryYM();
                if (StringUtils.isNotEmpty(queryYM)) {
                    beginSignDate
                        = DateUtil.str2date(DateUtil.getFirstDateOfMonth(queryYM, WebConstant.PATTERN_BAR_YYYYMMDD));
                    endSignDate
                        = DateUtil.str2date(DateUtil.getLastDateOfMonth(queryYM, WebConstant.PATTERN_BAR_YYYYMMDD));
                    monthDays = DateUtil.getDaysOfMonth(queryYM);
                }
            }

            // 根据部门下的人员、当月标识决定从当前表还是历史表中查询出指定日期下的人员id ；
            HashSet<Long> idSet = getPersionFromSignInOut(obj.getThisMonth(), userIdList, beginSignDate, endSignDate);
            // 根据人员id条件查询人员排序且分页数据；
            rtnMap = getUserIdRank(pageBean, new ArrayList<Long>(idSet));
            userIdRankMap = (LinkedHashMap<Long, Integer>)rtnMap.get("userRank");
            pageTotal = (Integer)rtnMap.get("pageTotal");
            if (null != userIdRankMap && !userIdRankMap.isEmpty()) {
                if (StringUtils.isNotEmpty(obj.getThisMonth())) {
                    Where<AttSignInOut> attSignInOutwhere = new Where<AttSignInOut>();
                    attSignInOutwhere.setSqlSelect(
                        "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                    attSignInOutwhere.in("sign_user_id", userIdRankMap.keySet());
                    attSignInOutwhere.between("sign_date", beginSignDate, endSignDate);
                    attSignInOutwhere.orderBy("sign_date", true);// 日期排序；
                    signInOutList = selectList(attSignInOutwhere); // 要根据userIdRankMap对人员标注上排序顺序；由前端排序；
                } else {
                    // 其它情况都要从历史表中查询数据，如果查询历史表的最后一个月是当月的话，是否切换成当前月查询，还是仍然是历史表； ?? &&
                    // StringUtils.isNotEmpty(obj.getQueryYM()) && currMonth.equals(obj.getQueryYM())
                    String queryYM = obj.getQueryYM();
                    if (StringUtils.isNotEmpty(queryYM)) {
                        Where<AttSignInOutHis> attSignInOutHisWhere = new Where<AttSignInOutHis>();
                        attSignInOutHisWhere.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip");
                        attSignInOutHisWhere.in("sign_user_id", userIdRankMap.keySet());
                        attSignInOutHisWhere.between("sign_date", beginSignDate, endSignDate);
                        attSignInOutHisWhere.orderBy("sign_date", true);
                        attSignInOutHisList = attSignInOutHisService.selectList(attSignInOutHisWhere);
                    }
                }
            }

            // 无管是当前签到签退表还是历史表，都要根据翻页记录条件查询到对应的人员名称；
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.in("id", userIdRankMap.keySet());
            List<AutUser> autUserList = autUserService.selectList(userWhere);
            Map<Long, String> userIdNameMap = new HashMap<Long, String>();
            for (AutUser autUser : autUserList) {
                userIdNameMap.put(autUser.getId(), autUser.getFullName());
            }

            List<String> theadList = new ArrayList<String>();
            List<Map<String, Map<String, Object>>> listMap = new ArrayList<Map<String, Map<String, Object>>>();
            Map<String, Map<String, Object>> userDaySignMap = new LinkedHashMap<String, Map<String, Object>>();
            // 当月签入签退表中的数据；
            if (null != signInOutList && !signInOutList.isEmpty()) {
                Map<String, Object> map = null;
                for (int day = 1; day < monthDays; day++) {
                    for (AttSignInOut signInOut : signInOutList) {
                        if (signInOut.getSignDate().getDay() != day) {
                            continue;
                        }
                        DateTime dateTime = new DateTime(signInOut.getSignDate());
                        String date = dateTime.toString("MM月dd日");
                        date = date + " " + signInOut.getSignWeek();
                        theadList.add(date);

                        List<String> tdList = new ArrayList<String>();
                        tdList.add(null == signInOut.getAmSignInStatus() ? "0"
                            : String.valueOf(signInOut.getAmSignInStatus()));
                        tdList.add(null == signInOut.getAmSignOutStatus() ? "0"
                            : String.valueOf(signInOut.getAmSignOutStatus()));
                        tdList.add(null == signInOut.getPmSignInStatus() ? "0"
                            : String.valueOf(signInOut.getPmSignInStatus()));
                        tdList.add(null == signInOut.getPmSignOutStatus() ? "0"
                            : String.valueOf(signInOut.getPmSignOutStatus()));
                        tdList.add(null == signInOut.getAmSignInIp() ? "" : String.valueOf(signInOut.getAmSignInIp()));
                        tdList
                            .add(null == signInOut.getAmSignOutIp() ? "" : String.valueOf(signInOut.getAmSignOutIp()));
                        tdList.add(null == signInOut.getPmSignInIp() ? "" : String.valueOf(signInOut.getPmSignInIp()));
                        tdList
                            .add(null == signInOut.getPmSignOutIp() ? "" : String.valueOf(signInOut.getPmSignOutIp()));
                        map = new LinkedHashMap<String, Object>();
                        map.put(date, tdList);
                        String persionKey = userIdNameMap.get(signInOut.getSignUserId()) + "("
                            + userIdRankMap.get(signInOut.getSignUserId()) + ")";
                        Map<String, Object> dayDataMap = userDaySignMap.get(persionKey);
                        if (dayDataMap == null) {
                            userDaySignMap.put(persionKey, map);
                        } else {
                            dayDataMap.putAll(map);
                            userDaySignMap.put(persionKey, dayDataMap);
                        }
                    }
                }
                listMap.add(userDaySignMap);
            }
            // 历史表中签入签退的数据；
            if (null != attSignInOutHisList && !attSignInOutHisList.isEmpty()) { // 历史表中某个月没有人员打卡记录时，根据人员排序表的顺序默认都显示未打卡；
                Map<String, Object> map = null;
                for (int day = 1; day < monthDays; day++) {
                    for (AttSignInOutHis signInOut : attSignInOutHisList) {
                        // 以确保按日期顺序赋值给map；
                        if (signInOut.getSignDate().getDay() != day) {
                            continue;
                        }
                        DateTime dateTime = new DateTime(signInOut.getSignDate());
                        String date = dateTime.toString("MM月dd日");
                        date = date + " " + signInOut.getSignWeek();
                        theadList.add(date);

                        List<String> tdList = new ArrayList<String>();
                        tdList.add(signInOut.getAmSignInStatus() == null ? "0" : signInOut.getAmSignInStatus() + "");
                        tdList.add(signInOut.getAmSignOutStatus() == null ? "0" : signInOut.getAmSignOutStatus() + "");
                        tdList.add(signInOut.getPmSignInStatus() == null ? "0" : signInOut.getPmSignInStatus() + "");
                        tdList.add(signInOut.getPmSignOutStatus() == null ? "0" : signInOut.getPmSignOutStatus() + "");
                        tdList.add(signInOut.getAmSignInIp() == null ? "" : signInOut.getAmSignInIp() + "");
                        tdList.add(signInOut.getAmSignOutIp() == null ? "" : signInOut.getAmSignOutIp() + "");
                        tdList.add(signInOut.getPmSignInIp() == null ? "" : signInOut.getPmSignInIp() + "");
                        tdList.add(signInOut.getPmSignOutIp() == null ? "" : signInOut.getPmSignOutIp() + "");
                        map = new LinkedHashMap<String, Object>();
                        map.put(date, tdList);
                        String persionKey = userIdNameMap.get(signInOut.getSignUserId()) + "("
                            + userIdRankMap.get(signInOut.getSignUserId()) + ")";
                        Map<String, Object> dayDataMap = userDaySignMap.get(persionKey);
                        if (dayDataMap == null) {
                            userDaySignMap.put(persionKey, map);
                        } else {
                            dayDataMap.putAll(map);
                            userDaySignMap.put(persionKey, dayDataMap);
                        }
                    }
                }
                listMap.add(userDaySignMap);
            }

            if (null != theadList && !theadList.isEmpty()) {
                HashSet<String> uniquenessHead = new HashSet<String>(theadList);
                theadList.clear();
                theadList.addAll(uniquenessHead);
                Collections.sort(theadList);
                signInOutVO.setTheadList(theadList);
            }
            if (null != listMap && !listMap.isEmpty()) {
                signInOutVO.setTdMaps(listMap);
            }
        }
        List<AttSignInOutVO> attSignInOutVOList = new ArrayList<AttSignInOutVO>();
        attSignInOutVOList.add(signInOutVO);
        page.setRecords(attSignInOutVOList);
        page.setTotal(pageTotal);
        return page;
    }

    /**
     * 
     * 根据条件查询出当前打卡表或者历史表中的有打卡的人员ID；
     * 
     * @param currMonthFlag:决定查询的数据源是当前打卡表还是历史表；
     * @param userIdList:根据外部给定的人员id查询；
     * @param beginDate、endDate 打卡的开始、结束日期；
     * @return：HashSet<Long>
     * @author：huanghz
     * @date：2018年1月17日 上午11:18:31
     */
    private HashSet<Long> getPersionFromSignInOut(String currMonth, List<Long> userIdList, Date beginDate,
        Date endDate) {
        HashSet<Long> idSet = new HashSet<Long>();
        if (StringUtils.isNotEmpty(currMonth)) {
            List<AttSignInOut> attSignInOutToIdList = new ArrayList<AttSignInOut>();
            Where<AttSignInOut> attSignInOutIdWhere = new Where<AttSignInOut>();
            attSignInOutIdWhere.setSqlSelect("sign_user_id");
            if (userIdList != null && userIdList.size() > 0) {
                attSignInOutIdWhere.in("sign_user_id", userIdList);
            }
            attSignInOutIdWhere.between("sign_date", beginDate, endDate);
            attSignInOutToIdList = selectList(attSignInOutIdWhere);
            for (AttSignInOut attSignInOut : attSignInOutToIdList) {
                idSet.add(attSignInOut.getSignUserId());
            }
        } else {
            List<AttSignInOutHis> attSignInOutHisToIdList = new ArrayList<AttSignInOutHis>();
            Where<AttSignInOutHis> attSignInOutHisIdWhere = new Where<AttSignInOutHis>();
            attSignInOutHisIdWhere.setSqlSelect("sign_user_id");
            if (userIdList != null && userIdList.size() > 0) {
                attSignInOutHisIdWhere.in("sign_user_id", userIdList);
            }
            attSignInOutHisIdWhere.between("sign_date", beginDate, endDate);
            attSignInOutHisToIdList = attSignInOutHisService.selectList(attSignInOutHisIdWhere);
            for (AttSignInOutHis attSignInOut : attSignInOutHisToIdList) {
                idSet.add(attSignInOut.getSignUserId());
            }
        }
        return idSet;
    }

    /* private HashSet<Long> getUserIdFromHis(DateTime lastMonthBegDT, DateTime lastMonthEndDT)
         throws ParseException {
       List<AttSignInOutHis> attSignInOutHisToIdList = new ArrayList<AttSignInOutHis>();
       HashSet<Long> idSet = new HashSet<Long>();
       DateTime dateTime = new DateTime();
       
       Where<AttSignInOutHis> attSignInOutHisIdWhere = new Where<AttSignInOutHis>();
       attSignInOutHisIdWhere.setSqlSelect("id");
       if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
         attSignInOutHisIdWhere.between("sign_date", DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)), 
             DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
       } else {
         attSignInOutHisIdWhere.between("sign_date", DateUtil.str2dateOrTime(lastMonthBegDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
             DateUtil.str2dateOrTime(lastMonthEndDT.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
       }
       attSignInOutHisToIdList = attSignInOutHisService.selectList(attSignInOutHisIdWhere);
       for (AttSignInOutHis attSignInOut : attSignInOutHisToIdList) {
         idSet.add(attSignInOut.getId());
       }
       return idSet ; 
     }*/
    @SuppressWarnings("unused")
    private HashSet<Long> getUserIdFromHis(Date beginDate, Date endDate) throws ParseException {
        List<AttSignInOutHis> attSignInOutHisToIdList = new ArrayList<AttSignInOutHis>();
        HashSet<Long> idSet = new HashSet<Long>();
        DateTime dateTime = new DateTime();

        Where<AttSignInOutHis> attSignInOutHisIdWhere = new Where<AttSignInOutHis>();
        attSignInOutHisIdWhere.setSqlSelect("id");
        if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
            attSignInOutHisIdWhere.between("sign_date",
                DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                DateUtil.str2dateOrTime(DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)));
        } else {
            attSignInOutHisIdWhere.between("sign_date", beginDate, endDate);
        }
        attSignInOutHisToIdList = attSignInOutHisService.selectList(attSignInOutHisIdWhere);
        for (AttSignInOutHis attSignInOut : attSignInOutHisToIdList) {
            idSet.add(attSignInOut.getId());
        }
        return idSet;
    }

    private Map<String, Object> getUserIdRank(PageBean pageBean, ArrayList<Long> idList) throws Exception {
        int irank = 1;
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        Map<Long, Integer> userIdRankMap = new LinkedHashMap<Long, Integer>();
        Page<AutUserRank> autUserRankPage = autUserService.getUserRankPage(pageBean, idList);
        List<AutUserRank> autUserRankList = autUserRankPage.getRecords();
        for (AutUserRank autUserRank : autUserRankList) {
            userIdRankMap.put(autUserRank.getId(), irank++);
        }
        int pageTotal = autUserRankPage.getTotal();

        rtnMap.put("userRank", userIdRankMap);
        rtnMap.put("pageTotal", pageTotal);
        return rtnMap;
    }

    @Override
    public boolean getIsWorkDay(Date date) throws Exception {
        boolean isWorkDay = true;
        String week = DateUtil.getWeek(date);// 获得星期几
        if (week.equals("星期六") || week.equals("星期日")) {// 判断是否工作日（暂时默认星期一到星期五为工作日）
            isWorkDay = false;
        }
        return isWorkDay;
    }

    /**
     *
     * 获取上午签到打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getAmSignIn(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter amWorkPunchTime = sysParameterService.selectBykey("am_work_punch_time");// 上午上班打卡时间
        SysParameter amWorkPunchBeginTime = sysParameterService.selectBykey("am_work_punch_begtime");// 上午上班开始打卡时间
        SysParameter amAllowLate = sysParameterService.selectBykey("am_allow_late");// 上午允许迟到时间(分钟)

        if (null == amWorkPunchTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_WORK_PUNCH_TIME_ERRORMSG);
            return retMsg;
        }
        if (null == amAllowLate) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_ALLOWLATE_ERRORMSG);
            return retMsg;
        }
        if (null == amWorkPunchBeginTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_WORK_PUNCH_BEGTIME_ERRORMSG);
            return retMsg;
        }
        if (null != amWorkPunchTime && null != amAllowLate) {
            String amWorkPunchTimeStr = amWorkPunchTime.getParamValue();
            String amAllowLateStr = amAllowLate.getParamValue();
            String amWorkPunchBeginTimeStr = amWorkPunchBeginTime.getParamValue();

            if (StringUtils.isNotEmpty(amWorkPunchTimeStr) && StringUtils.isNotEmpty(amAllowLateStr)) {
                String amWorkPunchEndTimeStr = DateTime.parse(amWorkPunchTimeStr, format)
                    .plusMinutes(Integer.parseInt(amAllowLateStr)).toString(WebConstant.PATTERN_BAR_HHMM);
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkBegTime = DateTime.parse(amWorkPunchBeginTimeStr, format);
                DateTime amWorkEndTime = DateTime.parse(amWorkPunchEndTimeStr, format);
                // 准时打卡 和 迟到 是以上下班打卡时间 和 允许迟到分钟数计算得出的
                if (curTime.getMillis() >= amWorkBegTime.getMillis()
                    && curTime.getMillis() <= amWorkEndTime.getMillis()) {// 准时打卡
                    retMsg.setCode(0);
                } else {// 迟到或无打卡
                    retMsg.setCode(1);
                }

            }
        }
        return retMsg;
    }

    /**
     *
     *
     * 获取上午签退打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getAmSignOut(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter amOffworkPunchTime = sysParameterService.selectBykey("am_offwork_punch_time");// 上午下班打卡时间
        SysParameter amLeaveEarly = sysParameterService.selectBykey("am_leave_early");// 上午下班允许早退时间(分钟)
        SysParameter amOffworkPunchEndTime = sysParameterService.selectBykey("am_offwork_punch_endtime");// 上午下班结束打卡时间(分钟)

        if (null == amOffworkPunchTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_OFFWORK_PUNCH_TIME_ERRORMSG);
            return retMsg;
        }
        if (null == amLeaveEarly) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_LEAVEEARLY_ERRORMSG);
            return retMsg;
        }
        if (null == amOffworkPunchEndTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_OFFWORK_PUNCH_ENDTIME_ERRORMSG);
            return retMsg;
        }

        // Map<String, String> map = new HashMap<String, String>();
        if (null != amOffworkPunchTime && null != amLeaveEarly) {
            String amOffworkPunchTimeStr = amOffworkPunchTime.getParamValue();
            String amLeaveEarlyStr = amLeaveEarly.getParamValue();
            String amOffworkPunchEndTimeStr = amOffworkPunchEndTime.getParamValue();

            if (StringUtils.isNotEmpty(amOffworkPunchTimeStr) && StringUtils.isNotEmpty(amLeaveEarlyStr)) {
                String amOffBegTimeStr = DateTime.parse(amOffworkPunchTimeStr, format)
                    .minusMinutes(Integer.parseInt(amLeaveEarlyStr)).toString(WebConstant.PATTERN_BAR_HHMM);
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amOffBegTime = DateTime.parse(amOffBegTimeStr, format);
                DateTime amOffEndTime = DateTime.parse(amOffworkPunchEndTimeStr, format);
                if (curTime.getMillis() >= amOffBegTime.getMillis()
                    && curTime.getMillis() <= amOffEndTime.getMillis()) {// 在设置的时间范围
                                                                         // 就是准时打卡
                    retMsg.setCode(0);
                } else {// 比开始签退时间 早 就是早退
                    retMsg.setCode(1);
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 获取下午签到打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getPmSignIn(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter pmWorkPunchTime = sysParameterService.selectBykey("pm_work_punch_time");// 下午上班打卡时间
        SysParameter pmWorkPunchBeginTime = sysParameterService.selectBykey("pm_work_punch_begtime");// 下午上班开始打卡时间
        SysParameter pmAllowLate = sysParameterService.selectBykey("pm_allow_late");// 下午允许迟到时间(分钟)

        if (null == pmWorkPunchTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_WORK_PUNCH_TIME_ERRORMSG);
            return retMsg;
        }
        if (null == pmAllowLate) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_ALLOWLATE_ERRORMSG);
            return retMsg;
        }
        if (null == pmWorkPunchBeginTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_WORK_PUNCH_BEGTIME_ERRORMSG);
            return retMsg;
        }

        Map<String, String> map = new HashMap<String, String>();
        if (null != pmWorkPunchTime && null != pmAllowLate) {
            String pmWorkPunchTimeStr = pmWorkPunchTime.getParamValue();
            String amWorkPunchBeginTimeStr = pmWorkPunchBeginTime.getParamValue();
            String pmAllowLateStr = pmAllowLate.getParamValue();
            if (StringUtils.isNotEmpty(pmWorkPunchTimeStr) && StringUtils.isNotEmpty(pmAllowLateStr)) {
                String pmWorkEndTimeStr = DateTime.parse(pmWorkPunchTimeStr, format)
                    .plusMinutes(Integer.parseInt(pmAllowLateStr)).toString(WebConstant.PATTERN_BAR_HHMM);
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime pmWorkBegTime = DateTime.parse(amWorkPunchBeginTimeStr, format);
                DateTime pmWorkEndTime = DateTime.parse(pmWorkEndTimeStr, format);
                retMsg.setObject(map);
                if (curTime.getMillis() >= pmWorkBegTime.getMillis()
                    && curTime.getMillis() <= pmWorkEndTime.getMillis()) {// 准时打卡
                    retMsg.setCode(0);
                } else {// 迟到或无打卡
                    retMsg.setCode(1);
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 获取下午签退打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getPmSignOut(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter pmOffworkPunchTime = sysParameterService.selectBykey("pm_offwork_punch_time");// 下午下班打卡时间
        SysParameter pmLeaveEarly = sysParameterService.selectBykey("pm_leave_early");// 下午下班打卡允许早退时间(分钟)
        SysParameter pmOffworkPunchEndTime = sysParameterService.selectBykey("pm_offwork_punch_endtime");// 下午下班结束打卡时间(分钟)

        if (null == pmOffworkPunchTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_OFFWORK_PUNCH_TIME_ERRORMSG);
            return retMsg;
        }
        if (null == pmLeaveEarly) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_LEAVEEARLY_ERRORMSG);
            return retMsg;
        }
        if (null == pmOffworkPunchEndTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_OFFWORK_PUNCH_ENDTIME_ERRORMSG);
            return retMsg;
        }

        Map<String, String> map = new HashMap<String, String>();
        if (null != pmOffworkPunchTime && null != pmLeaveEarly) {
            String pmOffworkPunchTimeStr = pmOffworkPunchTime.getParamValue();
            String pmLeaveEarlyStr = pmLeaveEarly.getParamValue();
            String pmOffworkPunchEndTimeStr = pmOffworkPunchEndTime.getParamValue();
            if (StringUtils.isNotEmpty(pmOffworkPunchTimeStr) && StringUtils.isNotEmpty(pmLeaveEarlyStr)) {
                String pmOffBegTimeStr = DateTime.parse(pmOffworkPunchTimeStr, format)
                    .minusMinutes(Integer.parseInt(pmLeaveEarlyStr)).toString(WebConstant.PATTERN_BAR_HHMM);
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime pmOffBegTime = DateTime.parse(pmOffBegTimeStr, format);
                DateTime pmOffEndTime = DateTime.parse(pmOffworkPunchEndTimeStr, format);
                retMsg.setObject(map);
                if (curTime.getMillis() >= pmOffBegTime.getMillis()
                    && curTime.getMillis() <= pmOffEndTime.getMillis()) {// 准时打卡
                    retMsg.setCode(0);
                } else {// 早退
                    retMsg.setCode(1);
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 获取客户端IP
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    @Override
    public List<AttSignInCount> getAttSignInCountList(AttSignInCount obj) throws Exception {

        List<AttSignInCount> signInCountList = new ArrayList<AttSignInCount>();
        try {
            // 根据部门id找到该部门的用户
            // Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
            // w1.setSqlSelect("id,user_id");
            // w1.eq("dept_id", obj.getDeptId());
            // List<AutDepartmentUser> deptUserList =
            // autDepartmentUserService.selectList(w1);
            // 获取需要查询部门部门的userIdList
            Long deptId = obj.getDeptId();
            // List<Long> deptUserIdList = new ArrayList<Long>();// 领导所在部门所有成员
            AutDepartment au = new AutDepartment();
            au.setId(deptId);
            List<AutUser> autDepartmentUserList = autDepartmentService.getChildDeptUserList(au);

            List<Long> userIdList = new ArrayList<Long>();
            for (AutUser autDepartmentUser : autDepartmentUserList) {
                userIdList.add(autDepartmentUser.getId());
            }
            // 部门有用户才进行查询
            if (userIdList != null && !userIdList.isEmpty()) {
                Map<Long, Integer> rankList = autUserService.getUserRankList(userIdList, null);

                List<AutUser> autUserList = new ArrayList<AutUser>();
                for (Long id : rankList.keySet()) {
                    // 查询用户表，以获取每个userId对应的userName
                    Where<AutUser> w2 = new Where<AutUser>();
                    w2.setSqlSelect("id,user_name,full_name");
                    w2.eq("id", id);
                    AutUser user = autUserService.selectOne(w2);
                    autUserList.add(user);
                }
                // 查询出该部门所有用户签到记录
                Where<AttSignInOut> w3 = new Where<AttSignInOut>();
                w3.setSqlSelect("sign_user_id,sign_user_name,sign_date,"
                    + "am_sign_in_status,am_sign_in_patch_status,am_sign_out_status,am_sign_out_patch_status,"
                    + "pm_sign_in_status,pm_sign_in_patch_status,pm_sign_out_status,pm_sign_out_patch_status,"
                    + "am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");

                w3.in("sign_user_id", rankList.keySet());
                Where<AttSignInOutHis> w4 = new Where<AttSignInOutHis>();
                w4.setSqlSelect("sign_user_id,sign_user_name,sign_date,"
                    + "am_sign_in_status,am_sign_in_patch_status,am_sign_out_status,am_sign_out_patch_status,"
                    + "pm_sign_in_status,pm_sign_in_patch_status,pm_sign_out_status,pm_sign_out_patch_status,"
                    + "am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");

                w4.in("sign_user_id", rankList.keySet());
                Date date = new Date();
                List<AttSignInOut> attSignInOutList = new ArrayList<AttSignInOut>();
                List<AttSignInOutHis> attSignInOutHisList = new ArrayList<AttSignInOutHis>();
                // 默认查询30天前的签到
                if (StringUtils.isEmpty(obj.getThisWeek()) && StringUtils.isEmpty(obj.getThisMonth())
                    && StringUtils.isEmpty(obj.getTime1()) && StringUtils.isEmpty(obj.getTime2())) {
                    Date dateBeforeThirty = DateUtil.getBefore(date, "day", 30);
                    w3.ge("sign_date", dateBeforeThirty);
                    attSignInOutList = selectList(w3);

                    // 查询本周签到
                } else if ("true".equals(obj.getThisWeek())) {
                    String firstDateOfWeek = DateUtil.getFirstDateOfWeek(WebConstant.PATTERN_BAR_YYYYMMDD);
                    /*
                     * String lastDateOfWeek =
                     * DateUtil.getLastDateOfWeek(WebConstant.
                     * PATTERN_BAR_YYYYMMDD);
                     */
                    DateTime dateTime = new DateTime();
                    w3.between("sign_date", DateUtil.str2dateOrTime(firstDateOfWeek),
                        DateUtil.str2dateOrTime(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));

                    attSignInOutList = selectList(w3);
                } else if ("true".equals(obj.getThisMonth())) {
                    // 查询本月签到
                    String firstDateOfMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                    // String lastDateOfMonth =
                    // DateUtil.getLastDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                    DateTime dateTime = new DateTime();

                    w3.between("sign_date", DateUtil.str2dateOrTime(firstDateOfMonth),
                        DateUtil.str2dateOrTime(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));

                    attSignInOutList = selectList(w3);
                } else if (StringUtils.isNotEmpty(obj.getTime1()) && StringUtils.isNotEmpty(obj.getTime2())) {
                    String firstDateOfMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                    if (obj.getTime1().compareTo(firstDateOfMonth) >= 0) {
                        w3.between("sign_date", DateUtil.str2dateOrTime(obj.getTime1()),
                            DateUtil.str2dateOrTime(obj.getTime2()));
                        attSignInOutList = selectList(w3);
                    } else {
                        w4.between("sign_date", DateUtil.str2dateOrTime(obj.getTime1()),
                            DateUtil.str2dateOrTime(obj.getTime2()));
                        attSignInOutHisList = attSignInOutHisService.selectList(w4);
                    }
                } else if (StringUtils.isNotEmpty(obj.getTime1())) {
                    String firstDateOfMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                    if (obj.getTime1().compareTo(firstDateOfMonth) >= 0) {
                        w3.ge("sign_date", DateUtil.str2dateOrTime(obj.getTime1()));
                        attSignInOutList = selectList(w3);
                    } else {
                        w4.ge("sign_date", DateUtil.str2dateOrTime(obj.getTime1()));
                        attSignInOutHisList = attSignInOutHisService.selectList(w4);
                    }
                } else if (StringUtils.isNotEmpty(obj.getTime2())) {
                    String firstDateOfMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD);
                    if (obj.getTime1().compareTo(firstDateOfMonth) >= 0) {
                        w3.le("sign_date", DateUtil.str2dateOrTime(obj.getTime2()));
                        attSignInOutList = selectList(w3);
                    } else {
                        w4.le("sign_date", DateUtil.str2dateOrTime(obj.getTime2()));
                        attSignInOutHisList = attSignInOutHisService.selectList(w4);
                    }
                }
                // w3.orderBy("sign_user_id", true);

                List<AutDepartmentUser> departmentUserList = new ArrayList<AutDepartmentUser>();
                // 部门列表
                AutDepartment dept = new AutDepartment();
                List<AutDepartment> deptList = new ArrayList<AutDepartment>();
                if (obj.getDeptId() != null) {
                    dept.setId(obj.getDeptId());
                    deptList = autDepartmentService.getChildDeptList(dept);
                }
                List<String> deptCodes = new ArrayList<String>();
                for (AutDepartment autDepartment : deptList) {
                    deptCodes.add(autDepartment.getDeptCode());
                }
                // 部门用户表
                if (null != deptList && !deptList.isEmpty()) {
                    Where<AutDepartmentUser> where1 = new Where<AutDepartmentUser>();
                    // where1.setSqlSelect("");
                    where1.in("dept_code", deptCodes);
                    departmentUserList = autDepartmentUserService.selectList(where1);// 当前用户部门列表

                }

                // 如果有查询到签到记录则返回对应值
                if (null != attSignInOutList && attSignInOutList.size() != 0) {
                    for (AutUser autUser : autUserList) {
                        if (autUser != null) {
                            AttSignInCount signInCount = new AttSignInCount();
                            signInCount.setSignUserName(autUser.getFullName());
                            for (AttSignInOut attSignInOut : attSignInOutList) {
                                // 比对是否同1用户
                                if (autUser.getId().equals(attSignInOut.getSignUserId())
                                    || autUser.getId() == attSignInOut.getSignUserId()) {
                                    // 未签到次数
                                    signInCount.setNoneSignInNums(signInCount.getNoneSignInNums()
                                        + (attSignInOut.getAmSignInStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignInStatus().intValue() == 3 ? 1 : 0));
                                    if (attSignInOut.getAmSignInPatchStatus().intValue() == 3
                                        && attSignInOut.getAmSignInStatus().intValue() == 3) {
                                        signInCount.setNoneSignInNums(signInCount.getNoneSignInNums() - 1);
                                    }
                                    if (attSignInOut.getPmSignInPatchStatus().intValue() == 3
                                        && attSignInOut.getPmSignInStatus().intValue() == 3) {
                                        signInCount.setNoneSignInNums(signInCount.getNoneSignInNums() - 1);
                                    }
                                    // 未签退次数
                                    signInCount.setNoneSignOutNums(signInCount.getNoneSignOutNums()
                                        + (attSignInOut.getAmSignOutStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutStatus().intValue() == 3 ? 1 : 0));
                                    if (attSignInOut.getAmSignOutPatchStatus().intValue() == 3
                                        && attSignInOut.getAmSignOutStatus().intValue() == 3) {
                                        signInCount.setNoneSignOutNums(signInCount.getNoneSignOutNums() - 1);
                                    }
                                    if (attSignInOut.getPmSignOutPatchStatus().intValue() == 3
                                        && attSignInOut.getPmSignOutStatus().intValue() == 3) {
                                        signInCount.setNoneSignOutNums(signInCount.getNoneSignOutNums() - 1);
                                    }
                                    // 迟到次数
                                    signInCount.setLateNums(signInCount.getLateNums()
                                        + (attSignInOut.getAmSignInStatus().intValue() == 2 ? 1 : 0)
                                        + (attSignInOut.getPmSignInStatus().intValue() == 2 ? 1 : 0));
                                    if (attSignInOut.getAmSignInPatchStatus().intValue() == 3
                                        && attSignInOut.getAmSignInStatus().intValue() == 2) {
                                        signInCount.setLateNums(signInCount.getLateNums() - 1);
                                    }
                                    if (attSignInOut.getPmSignInPatchStatus().intValue() == 3
                                        && attSignInOut.getPmSignInStatus().intValue() == 2) {
                                        signInCount.setLateNums(signInCount.getLateNums() - 1);
                                    }
                                    // 早退次数
                                    signInCount.setLeaveEarlyNums(signInCount.getLeaveEarlyNums()
                                        + (attSignInOut.getAmSignOutStatus().intValue() == 2 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutStatus().intValue() == 2 ? 1 : 0));
                                    if (attSignInOut.getAmSignOutPatchStatus().intValue() == 3
                                        && attSignInOut.getAmSignOutStatus().intValue() == 2) {
                                        signInCount.setLeaveEarlyNums(signInCount.getLeaveEarlyNums() - 1);
                                    }
                                    if (attSignInOut.getPmSignOutPatchStatus().intValue() == 3
                                        && attSignInOut.getPmSignOutStatus().intValue() == 2) {
                                        signInCount.setLeaveEarlyNums(signInCount.getLeaveEarlyNums() - 1);
                                    }
                                    signInCount.setSignUserId(attSignInOut.getSignUserId());
                                    // 补签次数
                                    signInCount.setSignPatchNums(signInCount.getSignPatchNums()
                                        + (attSignInOut.getAmSignInPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getAmSignOutPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignInPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutPatchStatus().intValue() == 3 ? 1 : 0));
                                }
                            }
                            Long deptid = new Long(0);
                            String deptName = "";
                            for (AutDepartmentUser deptUser : departmentUserList) {
                                if (autUser.getId().equals(deptUser.getUserId())) {
                                    deptid = deptUser.getDeptId();
                                }
                            }
                            for (AutDepartment autdept : deptList) {
                                if (deptid.equals(autdept.getId())) {
                                    deptName = autdept.getDeptName();
                                }
                            }
                            signInCount.setDepartment(deptName);
                            signInCountList.add(signInCount);

                        }

                    }
                    // 未查询到签到记录，则返回对应用户的值为0
                } else if (null != attSignInOutHisList && attSignInOutHisList.size() != 0) {
                    for (AutUser autUser : autUserList) {
                        if (autUser != null) {
                            AttSignInCount signInCount = new AttSignInCount();
                            signInCount.setSignUserName(autUser.getFullName());
                            for (AttSignInOutHis attSignInOut : attSignInOutHisList) {
                                // 比对是否同1用户
                                if (autUser.getId().equals(attSignInOut.getSignUserId())
                                    || autUser.getId() == attSignInOut.getSignUserId()) {
                                    // 未签到次数
                                    signInCount.setNoneSignInNums(signInCount.getNoneSignInNums()
                                        + (attSignInOut.getAmSignInStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignInStatus().intValue() == 3 ? 1 : 0));
                                    // 未签退次数
                                    signInCount.setNoneSignOutNums(signInCount.getNoneSignOutNums()
                                        + (attSignInOut.getAmSignOutStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutStatus().intValue() == 3 ? 1 : 0));
                                    // 迟到次数
                                    signInCount.setLateNums(signInCount.getLateNums()
                                        + (attSignInOut.getAmSignInStatus().intValue() == 2 ? 1 : 0)
                                        + (attSignInOut.getPmSignInStatus().intValue() == 2 ? 1 : 0));
                                    // 早退次数
                                    signInCount.setLeaveEarlyNums(signInCount.getLeaveEarlyNums()
                                        + (attSignInOut.getAmSignOutStatus().intValue() == 2 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutStatus().intValue() == 2 ? 1 : 0));
                                    signInCount.setSignUserId(attSignInOut.getSignUserId());
                                    // 补签次数
                                    signInCount.setSignPatchNums(signInCount.getSignPatchNums()
                                        + (attSignInOut.getAmSignInPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getAmSignOutPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignInPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutPatchStatus().intValue() == 3 ? 1 : 0));
                                }
                            }
                            Long deptid = new Long(0);
                            String deptName = "";
                            for (AutDepartmentUser deptUser : departmentUserList) {
                                if (autUser.getId().equals(deptUser.getUserId())) {
                                    deptid = deptUser.getDeptId();
                                }
                            }
                            for (AutDepartment autdept : deptList) {
                                if (deptid.equals(autdept.getId())) {
                                    deptName = autdept.getDeptName();
                                }
                            }
                            signInCount.setDepartment(deptName);
                            signInCountList.add(signInCount);

                        }

                    }
                    // 未查询到签到记录，则返回对应用户的值为0
                } else {
                    Long deptid = new Long(0);
                    for (AutUser autUser : autUserList) {
                        if (autUser != null) {
                            AttSignInCount signInCount = new AttSignInCount();
                            signInCount.setNoneSignInNums(0);
                            signInCount.setNoneSignOutNums(0);
                            signInCount.setLateNums(0);
                            signInCount.setLeaveEarlyNums(0);
                            signInCount.setSignUserName(autUser.getFullName());
                            String deptName = "";
                            for (AutDepartmentUser deptUser : departmentUserList) {
                                if (autUser.getId().equals(deptUser.getUserId())) {
                                    deptid = deptUser.getDeptId();
                                }
                            }
                            for (AutDepartment autdept : deptList) {
                                if (deptid.equals(autdept.getId())) {
                                    deptName = autdept.getDeptName();
                                }
                            }
                            signInCount.setDepartment(deptName);
                            signInCountList.add(signInCount);

                        }
                    }
                }

            }
            // 排序
            sortList(signInCountList, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return signInCountList;
    }

    /**
     *
     * 推送校验 format 时间格式对象 userId 用户ID (如果是个人登录的话就把当前用户ID传入该参数 只推送给个人,非登录的话 就推送给全部) curPointOfTime 当前时间点（如：8:10）
     * 
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-29
     */
    private RetMsg validatePush(DateTimeFormatter format, String curPointOfTime, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg = getSignInPopup(format, curPointOfTime, userId);
        return retMsg;
    }

    @Override
    public void sortList(List<AttSignInCount> list, AttSignInCount obj) {

        // 未签到降序
        if (StringUtils.checkValNotNull(obj.getOrderByNoneSignIn())) {
            if (obj.getOrderByNoneSignIn().equals(1) || Integer.parseInt(obj.getOrderByNoneSignIn()) == 1) {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getNoneSignInNums() != o2.getNoneSignInNums()) {
                            return o1.getNoneSignInNums() - o2.getNoneSignInNums();
                        } else if (o1.getNoneSignOutNums() != o2.getNoneSignOutNums()) {
                            return o1.getNoneSignOutNums() - o2.getNoneSignOutNums();
                        } else if (o1.getLeaveEarlyNums() != o2.getLeaveEarlyNums()) {
                            return o1.getLeaveEarlyNums() - o2.getLeaveEarlyNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            } else {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getNoneSignInNums() != o2.getNoneSignInNums()) {
                            return o2.getNoneSignInNums() - o1.getNoneSignInNums();
                        } else if (o1.getNoneSignOutNums() != o2.getNoneSignOutNums()) {
                            return o2.getNoneSignOutNums() - o1.getNoneSignOutNums();
                        } else if (o1.getLeaveEarlyNums() != o2.getLeaveEarlyNums()) {
                            return o2.getLeaveEarlyNums() - o1.getLeaveEarlyNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            }
            // 如果传2，则升序；

            // 未签退降序
        } else if (StringUtils.checkValNotNull(obj.getOrderByNoneSignOut())) {
            if (obj.getOrderByNoneSignOut().equals(1) || Integer.parseInt(obj.getOrderByNoneSignOut()) == 1) {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getNoneSignOutNums() != o2.getNoneSignOutNums()) {
                            return o1.getNoneSignOutNums() - o2.getNoneSignOutNums();
                        } else if (o1.getLateNums() != o2.getLateNums()) {
                            return o1.getLateNums() - o2.getLateNums();
                        } else if (o1.getNoneSignInNums() != o2.getNoneSignInNums()) {
                            return o1.getNoneSignInNums() - o2.getNoneSignInNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            } else {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getNoneSignOutNums() != o2.getNoneSignOutNums()) {
                            return o2.getNoneSignOutNums() - o1.getNoneSignOutNums();
                        } else if (o1.getLateNums() != o2.getLateNums()) {
                            return o2.getLateNums() - o1.getLateNums();
                        } else if (o1.getNoneSignInNums() != o2.getNoneSignInNums()) {
                            return o2.getNoneSignInNums() - o1.getNoneSignInNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            }
            // 迟到降序
        } else if (StringUtils.checkValNotNull(obj.getOrderByLate())) {
            if (obj.getOrderByLate().equals(1) || Integer.parseInt(obj.getOrderByLate()) == 1) {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getLateNums() != o2.getLateNums()) {
                            return o1.getLateNums() - o2.getLateNums();
                        } else if (o1.getLeaveEarlyNums() != o2.getLeaveEarlyNums()) {
                            return o1.getLeaveEarlyNums() - o2.getLeaveEarlyNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            } else {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getLateNums() != o2.getLateNums()) {
                            return o2.getLateNums() - o1.getLateNums();
                        } else if (o1.getLeaveEarlyNums() != o2.getLeaveEarlyNums()) {
                            return o2.getLeaveEarlyNums() - o1.getLeaveEarlyNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            }
            // 早退降序
        } else if (StringUtils.checkValNotNull(obj.getOrderByLeaveEarly())) {
            if (obj.getOrderByLeaveEarly().equals(1) || Integer.parseInt(obj.getOrderByLeaveEarly()) == 1) {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getLeaveEarlyNums() != o2.getLeaveEarlyNums()) {
                            return o1.getLeaveEarlyNums() - o2.getLeaveEarlyNums();
                        } else if (o1.getNoneSignInNums() != o2.getNoneSignInNums()) {
                            return o1.getNoneSignInNums() - o2.getNoneSignInNums();
                        } else if (o1.getNoneSignOutNums() != o2.getNoneSignOutNums()) {
                            return o1.getNoneSignOutNums() - o2.getNoneSignOutNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            } else {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getLeaveEarlyNums() != o2.getLeaveEarlyNums()) {
                            return o2.getLeaveEarlyNums() - o1.getLeaveEarlyNums();
                        } else if (o1.getNoneSignInNums() != o2.getNoneSignInNums()) {
                            return o2.getNoneSignInNums() - o1.getNoneSignInNums();
                        } else if (o1.getNoneSignOutNums() != o2.getNoneSignOutNums()) {
                            return o2.getNoneSignOutNums() - o1.getNoneSignOutNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            }
        } else if (StringUtils.checkValNotNull(obj.getOrderByPatch())) {
            if (obj.getOrderByPatch().equals(1) || Integer.parseInt(obj.getOrderByPatch()) == 1) {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getSignPatchNums() != o2.getSignPatchNums()) {
                            return o1.getSignPatchNums() - o2.getSignPatchNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            } else {
                Comparator<AttSignInCount> attSignInCountComparator = new Comparator<AttSignInCount>() {
                    @Override
                    public int compare(AttSignInCount o1, AttSignInCount o2) {
                        if (o1.getSignPatchNums() != o2.getSignPatchNums()) {
                            return o2.getSignPatchNums() - o1.getSignPatchNums();
                        }
                        return 0;
                    }
                };
                Collections.sort(list, attSignInCountComparator);
            }
        }
    }

    @Override
    public void export(HttpServletResponse response, AttSignInCount obj) throws Exception {

        List<AttSignInCount> list = new ArrayList<AttSignInCount>();

        list = getAttSignInCountList(obj);

        String[][] headers = {{"Department", "处室"}, {"SignUserName", "人员"}, {"NoneSignInNums", "未签到"},
            {"NoneSignOutNums", "未签退"}, {"LateNums", "迟到"}, {"LeaveEarlyNums", "早退"}, {"SignPatchNums", "补签"}};
        List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();

        // 利用反射执行实体类attSignInCount的get方法，获得需要的相关表头数据
        if (!list.isEmpty()) {
            for (AttSignInCount entity : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    AttSignInCount attSignInCount = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = attSignInCount.getClass();
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(attSignInCount));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("签到统计", headers, dataset, response);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public AttSignInOutVO personalSignInList(AttSignInOutVO obj) throws Exception {
        AttSignInOutVO offSchedulingVO = null;
        Where<AttSignInOutHis> where = new Where<AttSignInOutHis>();
        Where<AttSignInOut> attWhere = new Where<AttSignInOut>();
        if (null != obj) {
            offSchedulingVO = new AttSignInOutVO();
            List<AttSignInOutHis> schedulingList = null;
            List<AttSignInOut> attSignInOutList = null;
            List<Map<String, Object>> tdMaps = new ArrayList<Map<String, Object>>();
            // DateTime date = new DateTime(new Date());
            // String month = date.toString(WebConstant.PATTERN_BAR_YYYYMM);
            // 本月
            if (StringUtils.isNotEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                && StringUtils.isEmpty(obj.getNextMonth())) {
                currMonth = DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM);
                if (StringUtils.isNotEmpty(currMonth)) {
                    DateTime dateTime = new DateTime();
                    attWhere.between("sign_date",
                        DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                        DateUtil.str2date(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));// DateUtil.getLastDateOfMonth(PATTERN_BAR_YYYYMMDD)
                    attWhere.eq("sign_user_id", obj.getCreateUserId());
                    attWhere.setSqlSelect(
                        "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                    attSignInOutList = selectList(attWhere);
                }
            }

            // 上月
            if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isNotEmpty(obj.getLastMonth())
                && StringUtils.isEmpty(obj.getNextMonth())) {
                if (StringUtils.isNotEmpty(currMonth)) {
                    DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                    DateTime d = DateTime.parse(currMonth, format2);
                    String dateStr = d.minusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                    currMonth = dateStr;

                    DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                    String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                    String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                    DateTime begin = DateTime.parse(lastBegMonth, format);
                    DateTime end = DateTime.parse(lastEndMonth, format);

                    DateTime dateTime = new DateTime();
                    if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                        attWhere.between("sign_date",
                            DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                            DateUtil.str2date(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                        attWhere.eq("sign_user_id", obj.getCreateUserId());
                        attWhere.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                        attSignInOutList = selectList(attWhere);
                    } else {
                        where.between("sign_date", DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                            DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                        where.eq("sign_user_id", obj.getCreateUserId());
                        where.setSqlSelect(
                            "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                        schedulingList = attSignInOutHisService.selectList(where);
                    }
                }
            }

            // 下月
            if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                && StringUtils.isNotEmpty(obj.getNextMonth())) {
                if (StringUtils.isNotEmpty(currMonth)) {
                    DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMM);
                    DateTime d1 = DateTime.parse(currMonth, format2);
                    String dateStr = d1.plusMonths(1).toString(WebConstant.PATTERN_BAR_YYYYMM);
                    DateTime d2 = DateTime.parse(currMonth, format2);
                    currMonth = dateStr;
                    DateTime d3 = DateTime.parse(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMM), format2);
                    if (d2.getMillis() <= d3.getMillis()) {
                        where.eq("sign_user_id", obj.getCreateUserId());
                        DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                        DateTime begin = DateTime.parse(lastBegMonth, format);
                        DateTime end = DateTime.parse(lastEndMonth, format);
                        DateTime dateTime = new DateTime();
                        if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                            attWhere.between("sign_date",
                                DateUtil.str2date(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2date(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                            attWhere.eq("sign_user_id", obj.getCreateUserId());
                            attWhere.setSqlSelect(
                                "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                            attSignInOutList = selectList(attWhere);
                        } else {
                            where.between("sign_date",
                                DateUtil.str2date(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                                DateUtil.str2date(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                            where.eq("sign_user_id", obj.getCreateUserId());
                            where.setSqlSelect(
                                "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                            schedulingList = attSignInOutHisService.selectList(where);
                        }
                    }
                }
            }

            // 自选月
            if (StringUtils.isEmpty(obj.getThisMonth()) && StringUtils.isEmpty(obj.getLastMonth())
                && StringUtils.isEmpty(obj.getNextMonth()) && StringUtils.isNotEmpty(obj.getMonthBeg())) {
                // if(StringUtils.isNotEmpty(currMonth)){
                String dateStr = obj.getMonthBeg();
                currMonth = dateStr;

                DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                String lastBegMonth = DateUtil.getFirstDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                String lastEndMonth = DateUtil.getLastDateOfMonth(dateStr, WebConstant.PATTERN_BAR_YYYYMMDD);
                DateTime begin = DateTime.parse(lastBegMonth, format);
                DateTime end = DateTime.parse(lastEndMonth, format);

                DateTime dateTime = new DateTime();
                if (currMonth.equals(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMM))) {
                    attWhere.between("sign_date",
                        DateUtil.str2dateOrTime(DateUtil.getFirstDateOfMonth(WebConstant.PATTERN_BAR_YYYYMMDD)),
                        DateUtil.str2dateOrTime(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                    attWhere.eq("sign_user_id", obj.getCreateUserId());
                    attWhere.setSqlSelect(
                        "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                    attSignInOutList = selectList(attWhere);
                } else {
                    where.between("sign_date",
                        DateUtil.str2dateOrTime(begin.toString(WebConstant.PATTERN_BAR_YYYYMMDD)),
                        DateUtil.str2dateOrTime(end.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                    where.eq("sign_user_id", obj.getCreateUserId());
                    where.setSqlSelect(
                        "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,sign_week,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");
                    schedulingList = attSignInOutHisService.selectList(where);
                }
            }

            if (null != schedulingList && !schedulingList.isEmpty()) {
                Map<String, Object> map = new HashMap<String, Object>();
                List<AttSignInOutHis> offSchedulingList = new ArrayList<AttSignInOutHis>();
                for (AttSignInOutHis offScheduling : schedulingList) {
                    if (null != offScheduling) {
                        if (null != offScheduling.getSignDate()) {
                            DateTime dateTime = new DateTime(offScheduling.getSignDate());
                            offSchedulingList.add(offScheduling);
                            map.put(dateTime.toString("MM-dd"), offSchedulingList);
                        }
                    }
                }

                if (null != map && !map.isEmpty()) {
                    for (String key : map.keySet()) {
                        List<AttSignInOutHis> schedulings = (List<AttSignInOutHis>)map.get(key);
                        List<AttSignInOutHis> schedulingDetailList = new ArrayList<AttSignInOutHis>();
                        for (AttSignInOutHis offScheduling : schedulings) {
                            DateTime dateTime = new DateTime(offScheduling.getSignDate());
                            if (key.equals(dateTime.toString("MM-dd"))) {
                                schedulingDetailList.add(offScheduling);
                                map.put(key, schedulingDetailList);
                            }
                        }
                    }
                    tdMaps.add(map);
                }

                if (null != tdMaps && !tdMaps.isEmpty()) {
                    offSchedulingVO.setTdMap(tdMaps);
                }
            }

            if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
                Map<String, Object> map = new HashMap<String, Object>();
                List<AttSignInOut> offSchedulingList = new ArrayList<AttSignInOut>();
                for (AttSignInOut offScheduling : attSignInOutList) {
                    if (null != offScheduling) {
                        if (null != offScheduling.getSignDate()) {
                            DateTime dateTime = new DateTime(offScheduling.getSignDate());
                            DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_HHMM);
                            DateTime d1 = new DateTime();
                            String curPointOfTime = d1.toString(WebConstant.PATTERN_BAR_HHMM);
                            DateTimeFormatter format2 = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_YYYYMMDD);
                            DateTime curTime = DateTime.parse(d1.toString(WebConstant.PATTERN_BAR_YYYYMMDD), format2);
                            if (dateTime.getMillis() == curTime.getMillis()) {
                                getSignInOutStatus(format, curPointOfTime, offScheduling);
                            }
                            offSchedulingList.add(offScheduling);
                            map.put(dateTime.toString("MM-dd"), offSchedulingList);
                        }
                    }
                }

                if (null != map && !map.isEmpty()) {
                    for (String key : map.keySet()) {
                        List<AttSignInOut> schedulings = (List<AttSignInOut>)map.get(key);
                        List<AttSignInOut> schedulingDetailList = new ArrayList<AttSignInOut>();
                        for (AttSignInOut offScheduling : schedulings) {
                            DateTime dateTime = new DateTime(offScheduling.getSignDate());
                            if (key.equals(dateTime.toString("MM-dd"))) {
                                schedulingDetailList.add(offScheduling);
                                map.put(key, schedulingDetailList);
                            }
                        }
                    }
                    tdMaps.add(map);
                }
                if (null != tdMaps && !tdMaps.isEmpty()) {
                    offSchedulingVO.setTdMap(tdMaps);
                }
            }
        }

        return offSchedulingVO;
    }

    @Override
    public RetMsg confirmSign(Long userId, HttpServletRequest request) throws Exception {
        RetMsg retMsg = new RetMsg();
        Where<AttSignInOut> where = new Where<AttSignInOut>();
        Where<AttSignInOutHis> hisWhere = new Where<AttSignInOutHis>();
        List<AttSignInOut> signInOutList = new ArrayList<AttSignInOut>();
        List<AttSignInOutHis> signInOutHisList = new ArrayList<AttSignInOutHis>();
        String dateStr = "";
        if (null != userId) {
            DateTime dateTime = new DateTime();
            where.eq("sign_user_id", userId);
            where.eq("sign_date", DateUtil.str2date(dateTime.toString()));
            where.setSqlSelect(
                "id,version,is_delete,create_time,create_user_id,create_user_name,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time");

            hisWhere.eq("sign_user_id", userId);
            hisWhere.eq("sign_date", DateUtil.str2date(dateTime.toString()));
            hisWhere.setSqlSelect(
                "id,version,is_delete,create_time,create_user_id,create_user_name,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_ip,am_sign_out_ip,pm_sign_in_ip,pm_sign_out_ip,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time");

            signInOutList = selectList(where);
            signInOutHisList = attSignInOutHisService.selectList(hisWhere);

            String ip = getIpAddr(request);
            DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_HHMM);
            DateTime d1 = new DateTime();
            String curPointOfTime = d1.toString(WebConstant.PATTERN_BAR_HHMM);
            dateStr = d1.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS);
            if (null != signInOutList && !signInOutList.isEmpty() && null != signInOutHisList
                && !signInOutHisList.isEmpty()) {
                for (AttSignInOut signInOut : signInOutList) {
                    AttSignInOut attSignInOut = selectById(signInOut.getId());

                    AttSignInOutHis attSignInOutHis = signInOutHisList.get(0);
                    AttSignInOutHis signInOutHis = attSignInOutHisService.selectById(attSignInOutHis.getId());

                    if (null != signInOut && null != signInOut.getId()) {
                        retMsg = getSignInOutStatusInfo(format, curPointOfTime);
                        if (null != retMsg) {
                            if (null != retMsg.getCode() && retMsg.getCode() == 10) {
                                retMsg = getAmSignIn(format, curPointOfTime);
                                if (null != retMsg) {
                                    if (null != retMsg.getCode()) {
                                        if (retMsg.getCode() == 0) {
                                            if (attSignInOut.getAmSignInStatus() == 1) {
                                                retMsg.setCode(1);
                                                retMsg.setMessage(WebConstant.ATT_AM_SIGNIN_ERRORMSG);
                                                return retMsg;
                                            }
                                            attSignInOut.setAmSignInStatus(1);
                                            signInOutHis.setAmSignInStatus(1);
                                        }
                                        if (retMsg.getCode() == 1) {
                                            if (attSignInOut.getAmSignInStatus() == 2) {
                                                retMsg.setCode(1);
                                                retMsg.setMessage(WebConstant.ATT_AM_SIGNIN_ERRORMSG);
                                                return retMsg;
                                            }
                                            attSignInOut.setAmSignInStatus(2);
                                            signInOutHis.setAmSignInStatus(2);
                                        }
                                        attSignInOut.setAmSignInTime(d1.toDate());// 签到时间
                                        attSignInOut.setAmSignInIp(ip);// 签到Ip
                                        signInOutHis.setAmSignInTime(d1.toDate());// 签到时间
                                        signInOutHis.setAmSignInIp(ip);// 签到Ip
                                        retMsg.setCode(0);
                                        retMsg.setMessage("签到成功！您的签到时间为：" + dateStr);
                                        updateById(attSignInOut);
                                        attSignInOutHisService.updateById(signInOutHis);
                                        return retMsg;
                                    }
                                }
                            }
                            if (null != retMsg.getCode() && retMsg.getCode() == 20) {
                                retMsg = getAmSignOut(format, curPointOfTime);
                                if (null != retMsg) {
                                    if (null != retMsg.getCode()) {
                                        if (retMsg.getCode() == 0) {
                                            if (attSignInOut.getAmSignOutStatus() == 1) {
                                                retMsg.setCode(1);
                                                retMsg.setMessage(WebConstant.ATT_AM_SIGNOUT_ERRORMSG);
                                                return retMsg;
                                            }
                                            attSignInOut.setAmSignOutStatus(1);// 上午签退状态
                                                                               // 准时打卡
                                            signInOutHis.setAmSignOutStatus(1);
                                        }
                                        if (retMsg.getCode() == 1) {
                                            if (attSignInOut.getAmSignOutStatus() == 2) {
                                                retMsg.setMessage(WebConstant.ATT_AM_SIGNOUT_ERRORMSG);
                                                return retMsg;
                                            }
                                            attSignInOut.setAmSignOutStatus(2);// 上午签退状态
                                                                               // 早退
                                            signInOutHis.setAmSignOutStatus(2);
                                        }

                                        attSignInOut.setAmSignOutTime(d1.toDate());// 签到时间
                                        attSignInOut.setAmSignOutIp(ip);// 签到Ip
                                        signInOutHis.setAmSignOutTime(d1.toDate());// 签到时间
                                        signInOutHis.setAmSignOutIp(ip);// 签到Ip
                                        retMsg.setCode(0);
                                        retMsg.setMessage("签退成功！您的签到时间为：" + dateStr);
                                        updateById(attSignInOut);
                                        attSignInOutHisService.updateById(signInOutHis);
                                        return retMsg;
                                    }
                                }
                            }
                            if (null != retMsg.getCode() && retMsg.getCode() == 30) {
                                retMsg = getPmSignIn(format, curPointOfTime);
                                if (null != retMsg) {
                                    if (null != retMsg.getCode()) {
                                        if (retMsg.getCode() == 0) {
                                            if (attSignInOut.getPmSignInStatus() == 1) {
                                                retMsg.setCode(1);
                                                retMsg.setMessage(WebConstant.ATT_PM_SIGNIN_ERRORMSG);
                                                return retMsg;
                                            }
                                            attSignInOut.setPmSignInStatus(1);// 下午签到状态
                                                                              // 1：准时打卡
                                            signInOutHis.setPmSignInStatus(1);
                                        }
                                        if (retMsg.getCode() == 1) {
                                            if (attSignInOut.getPmSignInStatus() == 2) {
                                                retMsg.setCode(1);
                                                retMsg.setMessage(WebConstant.ATT_PM_SIGNIN_ERRORMSG);
                                                return retMsg;
                                            }
                                            attSignInOut.setPmSignInStatus(2);// 下午签到状态
                                                                              // 2：迟到
                                            signInOutHis.setPmSignInStatus(2);
                                        }
                                        attSignInOut.setPmSignInTime(d1.toDate());// 签到时间
                                        attSignInOut.setPmSignInIp(ip);// 签到Ip
                                        signInOutHis.setPmSignInTime(d1.toDate());// 签到时间
                                        signInOutHis.setPmSignInIp(ip);// 签到Ip
                                        retMsg.setCode(0);
                                        retMsg.setMessage("签到成功！您的签到时间为：" + dateStr);
                                        updateById(attSignInOut);
                                        attSignInOutHisService.updateById(signInOutHis);
                                        return retMsg;
                                    }
                                }
                            }
                            if (null != retMsg.getCode() && retMsg.getCode() == 40) {
                                retMsg = getPmSignOut(format, curPointOfTime);
                                if (retMsg.getCode() == 0) {
                                    if (attSignInOut.getPmSignOutStatus() == 1) {
                                        retMsg.setCode(1);
                                        retMsg.setMessage(WebConstant.ATT_PM_SIGNOUT_ERRORMSG);
                                        return retMsg;
                                    }
                                    attSignInOut.setPmSignOutStatus(1);
                                    signInOutHis.setPmSignOutStatus(1);
                                }

                                if (retMsg.getCode() == 1) {
                                    if (attSignInOut.getPmSignOutStatus() == 2) {
                                        retMsg.setCode(1);
                                        retMsg.setMessage(WebConstant.ATT_PM_SIGNOUT_ERRORMSG);
                                        return retMsg;
                                    }
                                    attSignInOut.setPmSignOutStatus(2);
                                    signInOutHis.setPmSignOutStatus(2);
                                }

                                attSignInOut.setPmSignOutTime(d1.toDate());// 签到时间
                                attSignInOut.setPmSignOutIp(ip);// 签到Ip
                                signInOutHis.setPmSignOutTime(d1.toDate());// 签到时间
                                signInOutHis.setPmSignOutIp(ip);// 签到Ip
                                retMsg.setCode(0);
                                retMsg.setMessage("签退成功！您的签到时间为：" + dateStr);
                                updateById(attSignInOut);
                                attSignInOutHisService.updateById(signInOutHis);
                                return retMsg;
                            }
                        }
                    }
                }
            }
        }
        return retMsg;
    }

    @Override
    public RetMsg getSignInOutStr(Long userId, HttpServletRequest request) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != userId) {
            DateTimeFormatter format = DateTimeFormat.forPattern(WebConstant.PATTERN_BAR_HHMM);
            DateTime d1 = new DateTime();
            String curPointOfTime = d1.toString(WebConstant.PATTERN_BAR_HHMM);
            retMsg = getSignInOutStatusInfo(format, curPointOfTime);
            boolean disabled = true;
            String flag = "";
            Map<String, Object> map = new HashMap<String, Object>();
            if (null != retMsg && null != retMsg.getCode()) {
                if (retMsg.getCode() == 10) {
                    retMsg = getAmSignInInfo(format, curPointOfTime);
                    if (null != retMsg && null != retMsg.getCode()) {
                        flag = WebConstant.ATT_AM_SIGNIN;
                        if (retMsg.getCode() == 0) {
                            disabled = false;
                        } else {
                            disabled = true;
                        }
                    }
                }
                if (retMsg.getCode() == 20) {
                    retMsg = getAmSignOutInfo(format, curPointOfTime);
                    if (null != retMsg && null != retMsg.getCode()) {
                        flag = WebConstant.ATT_AM_SIGNOUT;
                        if (retMsg.getCode() == 0) {
                            disabled = false;
                        } else {
                            disabled = true;
                        }
                    }
                }
                if (retMsg.getCode() == 30) {
                    retMsg = getPmSignInInfo(format, curPointOfTime);
                    if (null != retMsg && null != retMsg.getCode()) {
                        flag = WebConstant.ATT_PM_SIGNIN;
                        if (retMsg.getCode() == 0) {
                            disabled = false;
                        } else {
                            disabled = true;
                        }
                    }
                }
                if (retMsg.getCode() == 40) {
                    retMsg = getPmSignOutInfo(format, curPointOfTime);
                    if (null != retMsg && null != retMsg.getCode()) {
                        flag = WebConstant.ATT_PM_SIGNOUT;
                        if (retMsg.getCode() == 0) {
                            disabled = false;
                        } else {
                            disabled = true;
                        }
                    }
                }
                if (retMsg.getCode() == 50) {
                    flag = WebConstant.ATT_AM_SIGNIN;
                }
            }
            map.put("text", flag);
            map.put("disbled", disabled);
            map.put("curDate1", d1.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
            map.put("curDate2", d1.toString(WebConstant.PATTERN_BAR_HHMMSSYYYYMMDD));
            retMsg.setCode(0);
            retMsg.setObject(map);
        }
        return retMsg;
    }

    @Override
    public AttSignInCount getUserInfo(Long customUserId) throws Exception {

        AttSignInCount signInCount = new AttSignInCount();
        signInCount.setSignUserId(customUserId);
        signInCount.setSignUserName(String.valueOf(customUserId));

        return signInCount;
    }

    @Override
    public AttSignInCount getSignPathUserInfo(Long customUserId) throws Exception {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(WebConstant.PATTERN_BAR_YYYYMMDD);
        String signPathTime = sdf.format(date);
        AutUser autUser = autUserService.selectById(customUserId);

        Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
        w1.setSqlSelect("id,user_id,dept_id");
        w1.eq("user_id", customUserId);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w1);

        AutDepartment dept = new AutDepartment();
        if (null != deptUserList && !deptUserList.isEmpty()) {
            Long deptId = deptUserList.get(0).getDeptId();
            dept = autDepartmentService.selectById(deptId);
        }

        AttSignInCount signPathUserInfo = new AttSignInCount();

        if (StringUtils.checkValNotNull(dept)) {
            signPathUserInfo.setDeptName(dept.getDeptName());
        }
        signPathUserInfo.setSignUserName(autUser.getFullName());
        signPathUserInfo.setTime1(signPathTime);

        return signPathUserInfo;
    }

    public List<AttSignInOutVO> getSignInOutPatchList(AttSignInOutVO obj) throws Exception {
        List<AttSignInOutVO> list = new ArrayList<AttSignInOutVO>();
        if (null != obj && StringUtils.isNotEmpty(obj.getIds())) {
            if (obj.getIds().indexOf(";") > -1 || obj.getIds().endsWith(";")) {
                List<String> signOutIdList = Arrays.asList(obj.getIds().split(";"));
                if (null != signOutIdList && !signOutIdList.isEmpty()) {
                    Where<AttSignInOut> where = new Where<AttSignInOut>();
                    where.in("id", signOutIdList);
                    // where.andNew("am_sign_in_patch_status = {0} or
                    // am_sign_out_patch_status = {1} or pm_sign_in_patch_status
                    // = {2} or pm_sign_out_patch_status = {3}",2,2,2,2);
                    where.setSqlSelect(
                        "id,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_patch_desc,am_sign_out_patch_desc,pm_sign_in_patch_desc,pm_sign_out_patch_desc,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                    List<AttSignInOut> signInOutList = selectList(where);
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        Integer status = 0;
                        for (AttSignInOut attSignInOut : signInOutList) {
                            if (null != attSignInOut) {
                                if (null != attSignInOut.getAmSignInPatchStatus()) {
                                    status = attSignInOut.getAmSignInStatus();
                                    if (attSignInOut.getAmSignInPatchStatus() != 1) {
                                        AttSignInOutVO signInOut = new AttSignInOutVO();
                                        signInOut.setAmSignInProcInstId(attSignInOut.getAmSignInProcInstId());
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignInPatchStatus(attSignInOut.getAmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignInPatchDesc())
                                                ? attSignInOut.getAmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignInTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getAmSignOutPatchStatus()) {
                                    status = attSignInOut.getAmSignOutStatus();
                                    if (attSignInOut.getAmSignOutPatchStatus() != 1) {
                                        AttSignInOutVO signInOut = new AttSignInOutVO();
                                        signInOut.setAmSignOutProcInstId(attSignInOut.getAmSignOutProcInstId());
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignOutPatchStatus(attSignInOut.getAmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignOutPatchDesc())
                                                ? attSignInOut.getAmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignOutTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignInPatchStatus()) {
                                    status = attSignInOut.getPmSignInStatus();
                                    if (attSignInOut.getPmSignInPatchStatus() != 1) {
                                        AttSignInOutVO signInOut = new AttSignInOutVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignInProcInstId(attSignInOut.getPmSignInProcInstId());
                                        signInOut.setPmSignInPatchStatus(attSignInOut.getPmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignInPatchDesc())
                                                ? attSignInOut.getPmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignInTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignOutPatchStatus()) {
                                    status = attSignInOut.getPmSignOutStatus();
                                    if (attSignInOut.getPmSignOutPatchStatus() != 1) {
                                        AttSignInOutVO signInOut = new AttSignInOutVO();
                                        signInOut.setPmSignOutProcInstId(attSignInOut.getPmSignOutProcInstId());
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignOutPatchStatus(attSignInOut.getPmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignOutPatchDesc())
                                                ? attSignInOut.getPmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignOutTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public RetMsg getSignInOutHisPatchList(AttSignInOutHisVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<AttSignInOutHisVO> list = new ArrayList<AttSignInOutHisVO>();
        Map<String, Object> map = new HashMap<String, Object>();
        String processInstanceId = "";
        if (StringUtils.isNotEmpty(obj.getTaskId())) {
            Task task = taskService.createTaskQuery().taskId(obj.getTaskId()).singleResult();
            processInstanceId = task.getProcessInstanceId();
        } else {
            processInstanceId = obj.getProcessId();
        }
        if (null != obj && StringUtils.isNotEmpty(obj.getIds())) {
            if (obj.getIds().indexOf(";") > -1 || obj.getIds().endsWith(";")) {
                List<String> signOutIdList = Arrays.asList(obj.getIds().split(";"));
                if (null != signOutIdList && !signOutIdList.isEmpty()) {
                    Where<AttSignInOutHis> where = new Where<AttSignInOutHis>();
                    where.in("id", signOutIdList);
                    where.setSqlSelect(
                        "id,last_update_user_id,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_patch_desc,am_sign_out_patch_desc,pm_sign_in_patch_desc,pm_sign_out_patch_desc,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                    List<AttSignInOutHis> signInOutList = attSignInOutHisService.selectList(where);
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        Integer status = 0;
                        Long userId = signInOutList.get(0).getLastUpdateUserId();
                        AttSignInCount attSignInCount = getSignPathUserInfo2(userId);
                        map.put("attSignInCount", attSignInCount);
                        for (AttSignInOutHis attSignInOut : signInOutList) {
                            if (null != attSignInOut) {
                                status = attSignInOut.getAmSignInStatus();

                                if (null != attSignInOut.getAmSignInPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getAmSignInProcInstId())) {
                                    if (attSignInOut.getAmSignInPatchStatus() == 2
                                        && attSignInOut.getAmSignInProcInstId().equals(processInstanceId)) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignInPatchStatus(attSignInOut.getAmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignInPatchDesc())
                                                ? attSignInOut.getAmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignInTime());
                                            signInOut.setSignPatchDate(
                                                dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getAmSignOutPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getAmSignOutProcInstId())) {
                                    status = attSignInOut.getAmSignOutStatus();
                                    if (attSignInOut.getAmSignOutPatchStatus() == 2
                                        && attSignInOut.getAmSignOutProcInstId().equals(processInstanceId)) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignOutPatchStatus(attSignInOut.getAmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignOutPatchDesc())
                                                ? attSignInOut.getAmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignOutTime());
                                            signInOut.setSignPatchDate(
                                                dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignInPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getPmSignInProcInstId())) {
                                    status = attSignInOut.getPmSignInStatus();
                                    if (attSignInOut.getPmSignInPatchStatus() == 2
                                        && attSignInOut.getPmSignInProcInstId().equals(processInstanceId)) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignInPatchStatus(attSignInOut.getPmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignInPatchDesc())
                                                ? attSignInOut.getPmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignInTime());
                                            signInOut.setSignPatchDate(
                                                dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignOutPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getPmSignOutProcInstId())) {
                                    status = attSignInOut.getPmSignOutStatus();
                                    if (attSignInOut.getPmSignOutPatchStatus() == 2
                                        && attSignInOut.getPmSignOutProcInstId().equals(processInstanceId)) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignOutPatchStatus(attSignInOut.getPmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignOutPatchDesc())
                                                ? attSignInOut.getPmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignOutTime());
                                            signInOut.setSignPatchDate(
                                                dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        map.put("data", list);
        retMsg.setCode(0);
        retMsg.setObject(map);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<AttSignInOutHisVO> getSignInOutHisDatePatchList(AttSignInOutHisVO obj) throws Exception {
        List<AttSignInOutHisVO> list = new ArrayList<AttSignInOutHisVO>();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (null != obj && StringUtils.isNotEmpty(obj.getIds())) {
            if (obj.getIds().indexOf(";") > -1 || obj.getIds().endsWith(";")) {
                List<String> signOutIdList = Arrays.asList(obj.getIds().split(";"));
                if (null != signOutIdList && !signOutIdList.isEmpty()) {
                    Where<AttSignInOutHis> where = new Where<AttSignInOutHis>();
                    /*
                     * where.in("id", signOutIdList); where.andNew();
                     */
                    where.eq("am_sign_in_proc_inst_id", obj.getTaskId())
                        .or("am_sign_out_proc_inst_id = {0}", obj.getTaskId())
                        .or("pm_sign_in_proc_inst_id = {0}", obj.getTaskId())
                        .or("pm_sign_out_proc_inst_id={0}", obj.getTaskId());
                    where.setSqlSelect(
                        "id,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_patch_desc,am_sign_out_patch_desc,pm_sign_in_patch_desc,pm_sign_out_patch_desc,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                    List<AttSignInOutHis> signInOutList = attSignInOutHisService.selectList(where);
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        for (AttSignInOutHis attSignInOutHis : signInOutList) {
                            if (obj.getTaskId().equals(attSignInOutHis.getAmSignInProcInstId())) {
                                AttSignInOutHisVO vo = new AttSignInOutHisVO();
                                vo.setSignDateStr(sf.format(attSignInOutHis.getSignDate()));// 考勤日期
                                int no = attSignInOutHis.getAmSignInPatchStatus();
                                // am_sign_in_time
                                if (attSignInOutHis.getAmSignInTime() != null) {
                                    vo.setSignPatchDate(sfs.format(attSignInOutHis.getAmSignInTime())); // 签到时间对应打卡信息
                                } else {
                                    vo.setSignPatchDate("无"); // 签到时间对应打卡信息
                                }
                                vo.setPatchReason(attSignInOutHis.getAmSignInPatchDesc()); // 签到时间对应打卡信息
                                if (no == 1) {
                                    vo.setPatchStatus("准时打卡");// 状态
                                }
                                if (no == 2) {
                                    vo.setPatchStatus("迟到");
                                }
                                if (no == 3) {
                                    vo.setPatchStatus("无打卡");
                                }
                                list.add(vo);
                            }
                            if (obj.getTaskId().equals(attSignInOutHis.getAmSignOutProcInstId())) {
                                AttSignInOutHisVO vo = new AttSignInOutHisVO();
                                vo.setSignDateStr(sf.format(attSignInOutHis.getSignDate()));// 考勤日期
                                int no = attSignInOutHis.getAmSignOutPatchStatus();
                                // 准时打卡，2-早退，3-无打卡
                                if (attSignInOutHis.getAmSignOutTime() != null) {
                                    vo.setSignPatchDate(sfs.format(attSignInOutHis.getAmSignOutTime())); // 签到时间对应打卡信息
                                } else {
                                    vo.setSignPatchDate("无"); // 签到时间对应打卡信息
                                }
                                vo.setPatchReason(attSignInOutHis.getAmSignOutPatchDesc()); // 签到时间对应打卡信息
                                if (no == 1) {
                                    vo.setPatchStatus("准时打卡");// 状态
                                }
                                if (no == 2) {
                                    vo.setPatchStatus("早退");
                                }
                                if (no == 3) {
                                    vo.setPatchStatus("无打卡");
                                }
                                list.add(vo);
                            }
                            if (obj.getTaskId().equals(attSignInOutHis.getPmSignInProcInstId())) {
                                AttSignInOutHisVO vo = new AttSignInOutHisVO();
                                vo.setSignDateStr(sf.format(attSignInOutHis.getSignDate()));// 考勤日期
                                int no = attSignInOutHis.getPmSignInPatchStatus();
                                // 准时打卡，2-早退，3-无打卡
                                if (attSignInOutHis.getPmSignInTime() != null) {
                                    vo.setSignPatchDate(sfs.format(attSignInOutHis.getPmSignInTime())); // 签到时间对应打卡信息
                                } else {
                                    vo.setSignPatchDate("无"); // 签到时间对应打卡信息
                                }
                                vo.setPatchReason(attSignInOutHis.getPmSignInPatchDesc()); // 签到时间对应打卡信息
                                if (no == 1) {
                                    vo.setPatchStatus("准时打卡");// 状态
                                }
                                if (no == 2) {
                                    vo.setPatchStatus("迟到");
                                }
                                if (no == 3) {
                                    vo.setPatchStatus("无打卡");
                                }
                                list.add(vo);
                            }
                            if (obj.getTaskId().equals(attSignInOutHis.getPmSignOutProcInstId())) {
                                AttSignInOutHisVO vo = new AttSignInOutHisVO();
                                vo.setSignDateStr(sf.format(attSignInOutHis.getSignDate()));// 考勤日期
                                int no = attSignInOutHis.getPmSignOutPatchStatus();
                                if (attSignInOutHis.getPmSignOutTime() != null) {
                                    vo.setSignPatchDate(sfs.format(attSignInOutHis.getPmSignOutTime())); // 签到时间对应打卡信息
                                } else {
                                    vo.setSignPatchDate("无"); // 签到时间对应打卡信息
                                }
                                vo.setPatchReason(attSignInOutHis.getPmSignOutPatchDesc()); // 签到时间对应打卡信息
                                if (no == 1) {
                                    vo.setPatchStatus("准时打卡");// 状态
                                }
                                if (no == 2) {
                                    vo.setPatchStatus("早退");
                                }
                                if (no == 3) {
                                    vo.setPatchStatus("无打卡");
                                }
                                list.add(vo);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isNotEmpty(taskId)) {
            String processInstanceId
                = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
            // runtimeService.deleteProcessInstance(processInstanceId,
            // "考勤补签流程作废");
            // 流程作废
            runtimeService.deleteProcessInstance(processInstanceId, null);
            Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
            queryWhere.eq("process_instance_id", processInstanceId);
            actAljoinQueryService.delete(queryWhere);
            Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
            queryHisWhere.eq("process_instance_id", processInstanceId);
            actAljoinQueryHisService.delete(queryHisWhere);
            Date date = new Date();
            // 更新表数据
            if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
                if (bizId.indexOf(";") > -1) {
                    List<String> bizIdList = Arrays.asList(bizId.split(";"));
                    Where<AttSignInOut> signWhere = new Where<AttSignInOut>();
                    signWhere.in("id", bizIdList);
                    Where<AttSignInOutHis> signHisWhere = new Where<AttSignInOutHis>();
                    signHisWhere.in("id", bizIdList);
                    // AttSignInOutVO signInOutVO = new AttSignInOutVO();
                    // signInOutVO.setIds(bizId);
                    // AttSignInOutHisVO signInOutHisVO = new AttSignInOutHisVO();
                    // signInOutHisVO.setIds(bizId);
                    List<AttSignInOut> signInOutList = selectList(signWhere);
                    List<AttSignInOutHis> signInOutHisList = attSignInOutHisService.selectList(signHisWhere);

                    for (AttSignInOut signInOut : signInOutList) {
                        if (null != signInOut) {
                            if (null != signInOut.getAmSignInStatus()) {
                                if (2 == signInOut.getAmSignInPatchStatus()
                                    && signInOut.getAmSignInProcInstId() != null) {
                                    if (signInOut.getAmSignInProcInstId().equals(processInstanceId)) {
                                        signInOut.setAmSignInPatchAuditTime(date);
                                        signInOut.setAmSignInPatchStatus(4);
                                    }
                                }
                            }
                            if (null != signInOut.getAmSignOutStatus()) {
                                if (2 == signInOut.getAmSignOutPatchStatus()
                                    && signInOut.getAmSignOutProcInstId() != null) {
                                    if (signInOut.getAmSignOutProcInstId().equals(processInstanceId)) {
                                        signInOut.setAmSignOutPatchAuditTime(date);
                                        signInOut.setAmSignOutPatchStatus(4);
                                    }
                                }
                            }
                            if (null != signInOut.getPmSignInPatchStatus()) {
                                if (2 == signInOut.getPmSignInPatchStatus()
                                    && signInOut.getPmSignInProcInstId() != null) {
                                    if (signInOut.getPmSignInProcInstId().equals(processInstanceId)) {
                                        signInOut.setPmSignInPatchAuditTime(date);
                                        signInOut.setPmSignInPatchStatus(4);
                                    }
                                }
                            }
                            if (null != signInOut.getPmSignOutPatchStatus()) {
                                if (2 == signInOut.getPmSignOutPatchStatus()
                                    && signInOut.getPmSignOutProcInstId() != null) {
                                    if (signInOut.getPmSignOutProcInstId().equals(processInstanceId)) {
                                        signInOut.setPmSignOutPatchAuditTime(date);
                                        signInOut.setPmSignOutPatchStatus(4);
                                    }
                                }
                            }
                        }
                    }
                    updateBatchById(signInOutList);

                    for (AttSignInOutHis signInOutHis : signInOutHisList) {
                        if (null != signInOutHis) {
                            if (null != signInOutHis.getAmSignInStatus()) {
                                if (2 == signInOutHis.getAmSignInPatchStatus()
                                    && signInOutHis.getAmSignInProcInstId() != null) {
                                    if (signInOutHis.getAmSignInProcInstId().equals(processInstanceId)) {
                                        signInOutHis.setAmSignInPatchAuditTime(date);
                                        signInOutHis.setAmSignInPatchStatus(4);
                                    }
                                }
                            }
                            if (null != signInOutHis.getAmSignOutPatchStatus()) {
                                if (2 == signInOutHis.getAmSignOutPatchStatus()
                                    && signInOutHis.getAmSignOutProcInstId() != null) {
                                    if (signInOutHis.getAmSignOutProcInstId().equals(processInstanceId)) {
                                        signInOutHis.setAmSignOutPatchAuditTime(date);
                                        signInOutHis.setAmSignOutPatchStatus(4);
                                    }
                                }
                            }
                            if (null != signInOutHis.getPmSignInPatchStatus()) {
                                if (2 == signInOutHis.getPmSignInPatchStatus()
                                    && signInOutHis.getPmSignInProcInstId() != null) {
                                    if (signInOutHis.getPmSignInProcInstId().equals(processInstanceId)) {
                                        signInOutHis.setPmSignInPatchAuditTime(date);
                                        signInOutHis.setPmSignInPatchStatus(4);
                                    }
                                }
                            }
                            if (null != signInOutHis.getPmSignOutPatchStatus()) {
                                if (2 == signInOutHis.getPmSignOutPatchStatus()
                                    && signInOutHis.getPmSignOutProcInstId() != null) {
                                    if (signInOutHis.getPmSignOutProcInstId().equals(processInstanceId)) {
                                        signInOutHis.setPmSignOutPatchAuditTime(date);
                                        signInOutHis.setPmSignOutPatchStatus(4);
                                    }
                                }
                            }
                        }
                        attSignInOutHisService.updateBatchById(signInOutHisList);
                    }
                }
            }
        }
        // 环节办理人待办数量-1
        // AutDataStatistics aut = new AutDataStatistics();
        // aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
        // aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
        // aut.setBusinessKey(String.valueOf(userId));
        // autDataStatisticsService.minus(aut);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg checkNextTaskInfo(String taskId, String nextStept) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        Map<String, String> variablemap = new HashMap<String, String>();
        variablemap.put("nextStept", String.valueOf(nextStept));
        List<TaskDefinition> list
            = actFixedFormService.getNextTaskInfo2(processInstanceId, true, currentTaskKey, variablemap);
        boolean isOrgn = false;
        String openType = "";
        Map<String, Object> map = new HashMap<String, Object>();
        if (null != list && !list.isEmpty()) {
            String assigneeId = "";
            String assigneeUserId = "";
            String assigneeGroupId = "";
            TaskDefinition definition = list.get(0);
            if (null != definition) {
                // 选项为空时弹出整棵组织机构树
                if (definition.getAssigneeExpression() == null
                    && (definition.getCandidateGroupIdExpressions() == null
                        || definition.getCandidateGroupIdExpressions().size() == 0)
                    && (definition.getCandidateUserIdExpressions() == null
                        || definition.getCandidateUserIdExpressions().size() == 0)) {
                    openType = "1";
                    isOrgn = true;
                }
                List<AutUser> uList = new ArrayList<AutUser>(); // 所有候选用户
                Set<String> uIds = new HashSet<String>(); // 所有候选用户id
                if (null != definition.getAssigneeExpression()) {
                    assigneeId = String.valueOf(definition.getAssigneeExpression());
                    // 受理人
                    List<String> assineedIdList = new ArrayList<String>();
                    if (assigneeId.endsWith(";")) {
                        assineedIdList = Arrays.asList(assigneeId.split(";"));
                    } else {
                        assineedIdList.add(assigneeId);
                    }
//                    Where<AutUser> userWhere = new Where<AutUser>();
//                    userWhere.in("id", assineedIdList);
//                    userWhere.setSqlSelect("id,user_name,full_name");
//                    List<AutUser> assigneedList = autUserService.selectList(userWhere);
                    // 受理人不为空-就只返回受理人
                    if (null != assineedIdList && !assineedIdList.isEmpty()) {
                        uIds.addAll(assineedIdList);
//                        uList.addAll(assigneedList);
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
                    Set<Long> idSet = new HashSet<Long>();
                    for (AutUser user : uList) {
                        idSet.add(user.getId());
                    }
                    Where<AutUser> where = new Where<AutUser>();
                    where.in("id", uIds);
                    where.setSqlSelect("id,user_name,full_name");
                    List<AutUser> autUserList = autUserService.selectList(where);
                    if (idSet.size() > 0) {
                        for (AutUser autUser : autUserList) {
                            if (!idSet.contains(autUser.getId())) {
                                uList.add(autUser);
                            }
                        }
                    } else {
                        uList.addAll(autUserList);
                    }
                    // uList.addAll(autUserService.selectList(where));
                }
                map.put("user", uList);
                if (uList.size() == 0) {
                    // 只选择了部门，把部门id返回去
                    isOrgn = true;
                    openType = "3";
                    map.put("assineedGroupIdList", assineedGroupIdList);
                } else {
                    // 选择部门
                    Where<AutDepartmentUser> departmentWhere = new Where<AutDepartmentUser>();
                    departmentWhere.in("dept_id", assineedGroupIdList);
                    departmentWhere.setSqlSelect("dept_id,id,dept_code,user_id");
                    List<AutDepartmentUser> departmentList = autDepartmentUserService.selectList(departmentWhere);
                    List<AutDepartmentUserVO> departmentUserList = new ArrayList<AutDepartmentUserVO>();
                    if (null != departmentList && !departmentList.isEmpty()) {
                        isOrgn = true;
                        openType = "3";
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
        List<TaskDefinition> preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
        if (preList == null || preList.isEmpty()) {
            map.put("isNotBack", true);
        } else {
            map.put("isNotBack", false);
        }
        map.put("openType", openType);
        map.put("isOrgn", isOrgn);
        retMsg.setCode(0);
        retMsg.setObject(map);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processInstanceId) throws Exception {
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
                List<Long> assigneeIdList = (List<Long>)map.get("assigneeIdList");
                if (null != assigneeIdList && !assigneeIdList.isEmpty()) {
                    Where<AutUser> assigneeWhere = new Where<AutUser>();
                    assigneeWhere.in("id", new HashSet<Long>(assigneeIdList));
                    assigneeWhere.setSqlSelect("id,user_name,full_name");
                    assigneeList = autUserService.selectList(assigneeWhere);
                }
            }
            if (null != map.get("recevieUserIdList")) {
                List<Long> recevieUserIdList = (List<Long>)map.get("recevieUserIdList");
                if (null != recevieUserIdList && !recevieUserIdList.isEmpty()) {
                    Where<AutUser> recevieWhere = new Where<AutUser>();
                    recevieWhere.in("id", recevieUserIdList);
                    recevieWhere.setSqlSelect("id,user_name,full_name");
                    recevieList = autUserService.selectList(recevieWhere);
                }
            }

            if (null != map.get("logList")) {
                logList = (List<FixedFormProcessLog>)map.get("logList");
                if (null != logList && !logList.isEmpty() && null != assigneeList && !assigneeList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        for (AutUser user : assigneeList) {
                            if (null != user && null != user.getId() && null != log && null != log.getOperationId()) {
                                if (String.valueOf(user.getId()).equals(log.getOperationId())) {
                                    log.setOperationName(user.getFullName());
                                }
                            }
                        }
                    }
                }
                if (null != logList && !logList.isEmpty() && null != recevieList && !recevieList.isEmpty()) {
                    for (FixedFormProcessLog log : logList) {
                        String recUserName = "";
                        for (AutUser user : recevieList) {
                            if (null != user && null != user.getId() && null != log && null != log.getRecevieUserId()) {
                                if (log.getRecevieUserId().contains(String.valueOf(user.getId()))
                                    || log.getRecevieUserId().equals(String.valueOf(user.getId()))) {
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

    /**
     * 
     * 清空当前办理人
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年12月7日 下午1:43:16
     */
    @SuppressWarnings("unused")
    private void cleanQueryCurrentUser(String taskId) throws Exception {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Where<ActAljoinQuery> where = new Where<ActAljoinQuery>();
        where.eq("process_instance_id", task.getProcessInstanceId());
        ActAljoinQuery actAljoinQuery = actAljoinQueryService.selectOne(where);
        actAljoinQuery.setCurrentHandleFullUserName("");
        actAljoinQueryService.updateById(actAljoinQuery);
    }

    /**
     *
     * 获取上午签到打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getAmSignInInfo(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter amWorkPunchBegTime = sysParameterService.selectBykey("am_work_punch_begtime");// 上午上班开始打卡时间
        SysParameter amWorkPunchEndTime = sysParameterService.selectBykey("am_work_punch_endtime");// 上午上班结束打卡时间

        if (null == amWorkPunchBegTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_WORK_PUNCH_BEGTIME_ERRORMSG);
            return retMsg;
        }
        if (null == amWorkPunchEndTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_WORK_PUNCH_ENDTIME_ERRORMSG);
            return retMsg;
        }

        if (null != amWorkPunchBegTime && null != amWorkPunchEndTime) {
            String amWorkPunchBegTimeStr = amWorkPunchBegTime.getParamValue();
            String amWorkPunchEndTimeStr = amWorkPunchEndTime.getParamValue();

            if (StringUtils.isNotEmpty(amWorkPunchBegTimeStr) && StringUtils.isNotEmpty(amWorkPunchEndTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkBegTime = DateTime.parse(amWorkPunchBegTimeStr, format);
                DateTime amWorkEndTime = DateTime.parse(amWorkPunchEndTimeStr, format);
                if (curTime.getMillis() >= amWorkBegTime.getMillis()
                    && curTime.getMillis() <= amWorkEndTime.getMillis()) {// 在签到范围
                    retMsg.setCode(0);
                    return retMsg;
                } else {
                    retMsg.setCode(1);
                    return retMsg;
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 获取上午签到打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getAmSignOutInfo(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter amOffWorkPunchBegTime = sysParameterService.selectBykey("am_offwork_punch_begtime");// 上午下班开始打卡时间
        SysParameter amOffWorkPunchEndTime = sysParameterService.selectBykey("am_offwork_punch_endtime");// 上午下班结束打卡时间

        if (null == amOffWorkPunchBegTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_OFFWORK_PUNCH_BEGTIME_ERRORMSG);
            return retMsg;
        }
        if (null == amOffWorkPunchEndTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_OFFWORK_PUNCH_ENDTIME_ERRORMSG);
            return retMsg;
        }

        if (null != amOffWorkPunchBegTime && null != amOffWorkPunchEndTime) {
            String amOffWorkPunchBegTimeStr = amOffWorkPunchBegTime.getParamValue();
            String amOffWorkPunchEndTimeStr = amOffWorkPunchEndTime.getParamValue();

            if (StringUtils.isNotEmpty(amOffWorkPunchBegTimeStr) && StringUtils.isNotEmpty(amOffWorkPunchEndTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkBegTime = DateTime.parse(amOffWorkPunchBegTimeStr, format);
                DateTime amWorkEndTime = DateTime.parse(amOffWorkPunchEndTimeStr, format);
                if (curTime.getMillis() >= amWorkBegTime.getMillis()
                    && curTime.getMillis() <= amWorkEndTime.getMillis()) {// 在签到范围
                    retMsg.setCode(0);
                    return retMsg;
                } else {
                    retMsg.setCode(1);
                    return retMsg;
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 获取上午签到打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getPmSignInInfo(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter pmWorkPunchBegTime = sysParameterService.selectBykey("pm_work_punch_begtime");// 下午上班开始打卡时间
        SysParameter pmWorkPunchEndTime = sysParameterService.selectBykey("pm_work_punch_endtime");// 下午上班结束打卡时间

        if (null == pmWorkPunchBegTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_WORK_PUNCH_BEGTIME_ERRORMSG);
            return retMsg;
        }
        if (null == pmWorkPunchEndTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_WORK_PUNCH_ENDTIME_ERRORMSG);
            return retMsg;
        }

        if (null != pmWorkPunchBegTime && null != pmWorkPunchEndTime) {
            String pmWorkPunchBegTimeStr = pmWorkPunchBegTime.getParamValue();
            String pmWorkPunchEndTimeStr = pmWorkPunchEndTime.getParamValue();

            if (StringUtils.isNotEmpty(pmWorkPunchBegTimeStr) && StringUtils.isNotEmpty(pmWorkPunchEndTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkBegTime = DateTime.parse(pmWorkPunchBegTimeStr, format);
                DateTime amWorkEndTime = DateTime.parse(pmWorkPunchEndTimeStr, format);
                if (curTime.getMillis() >= amWorkBegTime.getMillis()
                    && curTime.getMillis() <= amWorkEndTime.getMillis()) {// 在签到范围
                    retMsg.setCode(0);
                    return retMsg;
                } else {
                    retMsg.setCode(1);
                    return retMsg;
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 获取上午签到打卡
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getPmSignOutInfo(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter pmOffWorkPunchBegTime = sysParameterService.selectBykey("pm_offwork_punch_begtime");// 下午下班开始打卡时间
        SysParameter pmOffWorkPunchEndTime = sysParameterService.selectBykey("pm_offwork_punch_endtime");// 下午下班结束打卡时间

        if (null == pmOffWorkPunchBegTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_OFFWORK_PUNCH_BEGTIME_ERRORMSG);
            return retMsg;
        }
        if (null == pmOffWorkPunchEndTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_OFFWORK_PUNCH_ENDTIME_ERRORMSG);
            return retMsg;
        }

        if (null != pmOffWorkPunchBegTime && null != pmOffWorkPunchEndTime) {
            String pmWorkPunchBegTimeStr = pmOffWorkPunchBegTime.getParamValue();
            String pmWorkPunchEndTimeStr = pmOffWorkPunchEndTime.getParamValue();

            if (StringUtils.isNotEmpty(pmWorkPunchBegTimeStr) && StringUtils.isNotEmpty(pmWorkPunchEndTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkBegTime = DateTime.parse(pmWorkPunchBegTimeStr, format);
                DateTime amWorkEndTime = DateTime.parse(pmWorkPunchEndTimeStr, format);
                if (curTime.getMillis() >= amWorkBegTime.getMillis()
                    && curTime.getMillis() <= amWorkEndTime.getMillis()) {// 在签到范围
                    retMsg.setCode(0);
                    return retMsg;
                } else {
                    retMsg.setCode(1);
                    return retMsg;
                }
            }
        }
        return retMsg;
    }

    /**
     *
     * 根据当前时间获取签到状态
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private void getSignInOutStatus(DateTimeFormatter format, String curPointOfTime, AttSignInOut signInOut)
        throws Exception {
        SysParameter amWorkPunchBegTime = sysParameterService.selectBykey("am_work_punch_begtime");// 上午上班结束打卡时间
        SysParameter amOffWorkPunchBegTime = sysParameterService.selectBykey("am_offwork_punch_begtime");// 上午下班结束打卡时间
        SysParameter pmWorkPunchBegTime = sysParameterService.selectBykey("pm_work_punch_begtime");// 下午上班结束打卡时间
        SysParameter pmOffWorkPunchBegTime = sysParameterService.selectBykey("pm_offwork_punch_begtime");// 下午下班结束打卡时间

        if (null != pmWorkPunchBegTime && null != amOffWorkPunchBegTime && null != pmOffWorkPunchBegTime) {
            String amWorkPunchBegTimeStr = amWorkPunchBegTime.getParamValue();
            String amOffWorkPunchBegTimeStr = amOffWorkPunchBegTime.getParamValue();
            String pmWorkPunchBegTimeStr = pmWorkPunchBegTime.getParamValue();
            String pmOffWorkPunchBegTimeStr = pmOffWorkPunchBegTime.getParamValue();

            if (StringUtils.isNotEmpty(amWorkPunchBegTimeStr) && StringUtils.isNotEmpty(amOffWorkPunchBegTimeStr)
                && StringUtils.isNotEmpty(pmWorkPunchBegTimeStr) && StringUtils.isNotEmpty(pmOffWorkPunchBegTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkBegTime = DateTime.parse(amWorkPunchBegTimeStr, format);
                DateTime amOffWorkBegTime = DateTime.parse(amOffWorkPunchBegTimeStr, format);
                DateTime pmWorkBegTime = DateTime.parse(pmWorkPunchBegTimeStr, format);
                DateTime pmOffWorkBegTime = DateTime.parse(pmOffWorkPunchBegTimeStr, format);

                if (curTime.getMillis() < amWorkBegTime.getMillis()
                    && curTime.getMillis() < amOffWorkBegTime.getMillis()
                    && curTime.getMillis() < pmWorkBegTime.getMillis()
                    && curTime.getMillis() < pmOffWorkBegTime.getMillis()) {
                    if (signInOut.getAmSignInStatus() == 3) {
                        signInOut.setAmSignInStatus(0);
                    }
                    if (signInOut.getAmSignOutStatus() == 3) {
                        signInOut.setAmSignOutStatus(0);
                    }
                    if (signInOut.getPmSignInStatus() == 3) {
                        signInOut.setPmSignInStatus(0);
                    }
                    if (signInOut.getPmSignOutStatus() == 3) {
                        signInOut.setPmSignOutStatus(0);
                    }
                }

                if (curTime.getMillis() >= amWorkBegTime.getMillis()
                    && curTime.getMillis() < amOffWorkBegTime.getMillis()
                    && curTime.getMillis() < pmWorkBegTime.getMillis()
                    && curTime.getMillis() < pmOffWorkBegTime.getMillis()) {
                    if (signInOut.getAmSignOutStatus() == 3) {
                        signInOut.setAmSignOutStatus(0);
                    }
                    if (signInOut.getPmSignInStatus() == 3) {
                        signInOut.setPmSignInStatus(0);
                    }
                    if (signInOut.getPmSignOutStatus() == 3) {
                        signInOut.setPmSignOutStatus(0);
                    }
                }

                if (curTime.getMillis() > amWorkBegTime.getMillis()
                    && curTime.getMillis() >= amOffWorkBegTime.getMillis()
                    && curTime.getMillis() < pmWorkBegTime.getMillis()
                    && curTime.getMillis() < pmOffWorkBegTime.getMillis()) {

                    if (signInOut.getPmSignInStatus() == 3) {
                        signInOut.setPmSignInStatus(0);
                    }
                    if (signInOut.getPmSignOutStatus() == 3) {
                        signInOut.setPmSignOutStatus(0);
                    }
                }

                if (curTime.getMillis() > amWorkBegTime.getMillis()
                    && curTime.getMillis() > amOffWorkBegTime.getMillis()
                    && curTime.getMillis() >= pmWorkBegTime.getMillis()
                    && curTime.getMillis() < pmOffWorkBegTime.getMillis()) {

                    if (signInOut.getPmSignOutStatus() == 3) {
                        signInOut.setPmSignOutStatus(0);
                    }
                }
            }
        }
    }

    /**
     *
     * 获取上午签到弹窗
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-11-06
     */
    private RetMsg getSignInPopup(DateTimeFormatter format, String curPointOfTime, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        // SysParameter amSigninPopupTime =
        // sysParameterService.selectBykey("am_signin_popup_time");//
        // 上午上班打卡弹窗时间
        // SysParameter amSignoutPopupTime =
        // sysParameterService.selectBykey("am_signout_popup_time");//
        // 上午下班打卡弹窗时间
        // SysParameter pmSigninPopupTime =
        // sysParameterService.selectBykey("pm_signin_popup_time");//
        // 下午上班打卡弹窗时间
        // SysParameter pmSignoutPopupTime =
        // sysParameterService.selectBykey("pm_signout_popup_time");//
        // 下午下班打卡弹窗时间
        SysParameter amSigninPopupTime = null;// 上午上班打卡弹窗时间
        SysParameter amSignoutPopupTime = null;// 上午下班打卡弹窗时间
        SysParameter pmSigninPopupTime = null;// 下午上班打卡弹窗时间
        SysParameter pmSignoutPopupTime = null;// 下午下班打卡弹窗时间
        // 一次查出来
        Where<SysParameter> sysParameterWhere = new Where<SysParameter>();
        sysParameterWhere.in("param_key", Arrays.asList("am_signin_popup_time", "am_signout_popup_time",
            "pm_signin_popup_time", "pm_signout_popup_time"));
        sysParameterWhere.eq("is_active", 1);
        sysParameterWhere.setSqlSelect("id,param_key,param_value");
        List<SysParameter> paramList = sysParameterService.selectList(sysParameterWhere);
        for (SysParameter sysParameter : paramList) {
            if ("am_signin_popup_time".equals(sysParameter.getParamKey())) {
                amSigninPopupTime = sysParameter;
            } else if ("am_signout_popup_time".equals(sysParameter.getParamKey())) {
                amSignoutPopupTime = sysParameter;
            } else if ("pm_signin_popup_time".equals(sysParameter.getParamKey())) {
                pmSigninPopupTime = sysParameter;
            } else if ("pm_signout_popup_time".equals(sysParameter.getParamKey())) {
                pmSignoutPopupTime = sysParameter;
            } else {

            }
        }

        if (null == amSigninPopupTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_SIGNIN_POPUP_TIME_ERRORMSG);
            return retMsg;
        }

        if (null == amSignoutPopupTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_AM_SIGNOUT_POPUP_TIME_ERRORMSG);
            return retMsg;
        }

        if (null == pmSigninPopupTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_SIGNIN_POPUP_TIME_ERRORMSG);
            return retMsg;
        }

        if (null == pmSignoutPopupTime) {
            retMsg.setCode(1);
            retMsg.setMessage(WebConstant.ATT_PM_SIGNOUT_POPUP_TIME_ERRORMSG);
            return retMsg;
        }

        if (null != amSigninPopupTime && null != amSignoutPopupTime && null != pmSigninPopupTime
            && null != pmSignoutPopupTime) {
            String amSigninPopupTimeStr = amSigninPopupTime.getParamValue();
            String amSignoutPopupTimeStr = amSignoutPopupTime.getParamValue();
            String pmSigninPopupTimeStr = pmSigninPopupTime.getParamValue();
            String pmSignoutPopupTimeStr = pmSignoutPopupTime.getParamValue();

            List<Long> userIdList = new ArrayList<Long>();

            if (StringUtils.isNotEmpty(amSigninPopupTimeStr) && StringUtils.isNotEmpty(amSignoutPopupTimeStr)
                && StringUtils.isNotEmpty(pmSigninPopupTimeStr) && StringUtils.isNotEmpty(pmSignoutPopupTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amSigninPopupBegTime = DateTime.parse(amSigninPopupTimeStr, format);
                DateTime amSignoutPopupBegTime = DateTime.parse(amSignoutPopupTimeStr, format);
                DateTime pmSigninPopupBegTime = DateTime.parse(pmSigninPopupTimeStr, format);
                DateTime pmSignoutPopupBegTime = DateTime.parse(pmSignoutPopupTimeStr, format);
                Where<AutUser> where = new Where<AutUser>();
                Where<AttSignInOut> signInOutWhere = new Where<AttSignInOut>();
                signInOutWhere.setSqlSelect(
                    "id,sign_user_id,sign_date,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status");
                DateTime dateTime = new DateTime();
                signInOutWhere.eq("sign_date", DateUtil.str2date(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD)));
                List<AttSignInOut> signInOutList = selectList(signInOutWhere);
                List<AttSignInOut> attSignInOutList = new ArrayList<AttSignInOut>();
                Map<String, Object> map = new HashMap<String, Object>();
                if (curTime.getMillis() == amSigninPopupBegTime.getMillis()
                    && curTime.getMillis() < amSignoutPopupBegTime.getMillis()
                    && curTime.getMillis() < pmSigninPopupBegTime.getMillis()
                    && curTime.getMillis() < pmSignoutPopupBegTime.getMillis()) {// 上午签到弹窗
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        for (AttSignInOut attSignInOut : signInOutList) {
                            if (attSignInOut.getAmSignInStatus() == 3) {
                                AttSignInOut signInOut = new AttSignInOut();
                                BeanUtils.copyProperties(attSignInOut, signInOut);
                                attSignInOutList.add(signInOut);
                            }
                        }
                        retMsg.setMessage(WebConstant.ATT_SIGNIN_POPUP_MSG);
                        map.put("msg", WebConstant.ATT_SIGNIN_POPUP_MSG);
                        map.put("signType", WebConstant.ATT_SIGNIN);
                        retMsg.setCode(0);
                    }
                }
                if (curTime.getMillis() > amSigninPopupBegTime.getMillis()
                    && curTime.getMillis() == amSignoutPopupBegTime.getMillis()
                    && curTime.getMillis() < pmSigninPopupBegTime.getMillis()
                    && curTime.getMillis() < pmSignoutPopupBegTime.getMillis()) {// 上午签退弹窗
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        for (AttSignInOut attSignInOut : signInOutList) {
                            if (attSignInOut.getAmSignOutStatus() == 3) {
                                AttSignInOut signInOut = new AttSignInOut();
                                BeanUtils.copyProperties(attSignInOut, signInOut);
                                attSignInOutList.add(signInOut);
                            }
                        }
                        retMsg.setMessage(WebConstant.ATT_SIGNOUT_POPUP_MSG);
                        map.put("msg", WebConstant.ATT_SIGNOUT_POPUP_MSG);
                        map.put("signType", WebConstant.ATT_SIGNOUT);
                        retMsg.setCode(0);
                    }
                }

                if (curTime.getMillis() > amSigninPopupBegTime.getMillis()
                    && curTime.getMillis() > amSignoutPopupBegTime.getMillis()
                    && curTime.getMillis() == pmSigninPopupBegTime.getMillis()
                    && curTime.getMillis() < pmSignoutPopupBegTime.getMillis()) {// 下午签到弹窗
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        for (AttSignInOut attSignInOut : signInOutList) {
                            if (attSignInOut.getPmSignInStatus() == 3) {
                                AttSignInOut signInOut = new AttSignInOut();
                                BeanUtils.copyProperties(attSignInOut, signInOut);
                                attSignInOutList.add(signInOut);
                            }
                        }
                        retMsg.setMessage(WebConstant.ATT_SIGNIN_POPUP_MSG);
                        map.put("msg", WebConstant.ATT_SIGNIN_POPUP_MSG);
                        map.put("signType", WebConstant.ATT_SIGNIN);
                        retMsg.setCode(0);
                    }
                }

                if (curTime.getMillis() > amSigninPopupBegTime.getMillis()
                    && curTime.getMillis() > amSignoutPopupBegTime.getMillis()
                    && curTime.getMillis() > pmSigninPopupBegTime.getMillis()
                    && curTime.getMillis() == pmSignoutPopupBegTime.getMillis()) {// 下午签退弹窗
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        for (AttSignInOut attSignInOut : signInOutList) {
                            if (attSignInOut.getPmSignOutStatus() == 3) {
                                AttSignInOut signInOut = new AttSignInOut();
                                BeanUtils.copyProperties(attSignInOut, signInOut);
                                attSignInOutList.add(signInOut);
                            }
                        }
                        retMsg.setCode(0);
                        retMsg.setMessage(WebConstant.ATT_SIGNOUT_POPUP_MSG);
                        map.put("msg", WebConstant.ATT_SIGNOUT_POPUP_MSG);
                        map.put("signType", WebConstant.ATT_SIGNOUT);
                    }
                }
                if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
                    for (AttSignInOut signInOut : attSignInOutList) {
                        if (null != signInOut && null != signInOut.getSignUserId()) {
                            userIdList.add(signInOut.getSignUserId());
                        } else {
                            retMsg.setCode(1);
                        }
                    }
                }

                if (null != userIdList && !userIdList.isEmpty()) {
                    where.in("id", userIdList);
                }
                where.setSqlSelect("id,user_name,full_name");

                List<AutUser> userList = autUserService.selectList(where);
                List<String> userNameList = new ArrayList<String>();
                if (null != userList && !userList.isEmpty()) {
                    for (AutUser user : userList) {
                        if (null != user && StringUtils.isNotEmpty(user.getUserName())) {
                            userNameList.add(user.getUserName());
                        }
                    }
                    if (null != userNameList && !userNameList.isEmpty()) {
                        retMsg.setCode(0);
                        map.put("userNameList", userNameList);
                    }
                }
                retMsg.setObject(map);
            }
        }
        return retMsg;
    }

    /**
     *
     * 根据当前时间获取签到状态
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    private RetMsg getSignInOutStatusInfo(DateTimeFormatter format, String curPointOfTime) throws Exception {
        RetMsg retMsg = new RetMsg();
        SysParameter amWorkPunchEndTime = sysParameterService.selectBykey("am_work_punch_endtime");// 上午上班结束打卡时间
        SysParameter amOffWorkPunchEndTime = sysParameterService.selectBykey("am_offwork_punch_endtime");// 上午下班结束打卡时间
        SysParameter pmWorkPunchEndTime = sysParameterService.selectBykey("pm_work_punch_endtime");// 下午上班结束打卡时间
        SysParameter pmOffWorkPunchEndTime = sysParameterService.selectBykey("pm_offwork_punch_endtime");// 下午下班结束打卡时间

        if (null != amWorkPunchEndTime && null != pmWorkPunchEndTime && null != amOffWorkPunchEndTime
            && null != pmOffWorkPunchEndTime) {
            String amWorkPunchEndTimeStr = amWorkPunchEndTime.getParamValue();
            String amOffWorkPunchEndTimeStr = amOffWorkPunchEndTime.getParamValue();
            String pmWorkPunchEndTimeStr = pmWorkPunchEndTime.getParamValue();
            String pmOffWorkPunchEndTimeTimeStr = pmOffWorkPunchEndTime.getParamValue();

            if (StringUtils.isNotEmpty(amWorkPunchEndTimeStr) && StringUtils.isNotEmpty(amOffWorkPunchEndTimeStr)
                && StringUtils.isNotEmpty(pmWorkPunchEndTimeStr)
                && StringUtils.isNotEmpty(pmOffWorkPunchEndTimeTimeStr)) {
                DateTime curTime = DateTime.parse(curPointOfTime, format);
                DateTime amWorkEndTime = DateTime.parse(amWorkPunchEndTimeStr, format);
                DateTime amOffWorkEndTime = DateTime.parse(amOffWorkPunchEndTimeStr, format);
                DateTime pmWorkEndTime = DateTime.parse(pmWorkPunchEndTimeStr, format);
                DateTime pmOffWorkEndTime = DateTime.parse(pmOffWorkPunchEndTimeTimeStr, format);

                if (curTime.getMillis() <= amWorkEndTime.getMillis()
                    && curTime.getMillis() < amOffWorkEndTime.getMillis()
                    && curTime.getMillis() < pmWorkEndTime.getMillis()
                    && curTime.getMillis() < pmOffWorkEndTime.getMillis()) {// 上午签到
                    retMsg.setCode(10);
                } else if (curTime.getMillis() > amWorkEndTime.getMillis()
                    && curTime.getMillis() <= amOffWorkEndTime.getMillis()
                    && curTime.getMillis() < pmWorkEndTime.getMillis()
                    && curTime.getMillis() < pmOffWorkEndTime.getMillis()) {// 上午签退
                    retMsg.setCode(20);
                } else if (curTime.getMillis() > amWorkEndTime.getMillis()
                    && curTime.getMillis() > amOffWorkEndTime.getMillis()
                    && curTime.getMillis() <= pmWorkEndTime.getMillis()
                    && curTime.getMillis() < pmOffWorkEndTime.getMillis()) {// 下午签到
                    retMsg.setCode(30);
                } else if (curTime.getMillis() > amWorkEndTime.getMillis()
                    && curTime.getMillis() > amOffWorkEndTime.getMillis()
                    && curTime.getMillis() > pmWorkEndTime.getMillis()
                    && curTime.getMillis() <= pmOffWorkEndTime.getMillis()) {// 下午签退
                    retMsg.setCode(40);
                } else {
                    retMsg.setCode(50);
                }
            }
        }
        return retMsg;
    }

    @Override
    public List<AttSignInCount> getLAttSignInCountList(AttSignInCount obj) throws Exception {
        List<AttSignInCount> signInCountList = new ArrayList<AttSignInCount>();
        List<AutDepartment> depts = new ArrayList<AutDepartment>();
        try {
            if (obj.getDeptId() != null && obj.getDeptId() != null && obj.getDeptId() != 0) {
                Long deptId = obj.getDeptId();
                AutDepartment autDepartment = autDepartmentService.selectById(deptId);
                if (obj.getDeptName() != null && !"".equals(obj.getDeptName())) {
                    /*
                     * List<AutDepartment> deptList =
                     * autDepartmentService.getChildDeptList(autDepartment);
                     */
                    String deptCode = autDepartment.getDeptCode();
                    Where<AutDepartment> w1 = new Where<AutDepartment>();
                    w1.where("dept_code like {0}", deptCode + "%");
                    w1.eq("is_active", 1);
                    w1.like("dept_name", obj.getDeptName());
                    w1.eq("is_delete", 0);
                    w1.setSqlSelect("id,dept_name,dept_code");
                    List<AutDepartment> autDepartmentList = autDepartmentService.selectList(w1);
                    if (autDepartmentList != null) {
                        for (AutDepartment autDepartment2 : autDepartmentList) {
                            depts.add(autDepartment2);
                        }
                    }
                } else {
                    depts = autDepartmentService.getChildDeptList(autDepartment);
                }

            } else {
                if (obj.getDeptName() != null && !"".equals(obj.getDeptName())) {
                    // String deptIds = "";
                    Where<AutDepartment> w1 = new Where<AutDepartment>();
                    w1.eq("is_active", 1);
                    w1.like("dept_name", obj.getDeptName());
                    w1.eq("is_delete", 0);
                    w1.setSqlSelect("id,dept_name,dept_code");
                    depts = autDepartmentService.selectList(w1);
                } else {
                    Where<AutDepartment> w1 = new Where<AutDepartment>();
                    w1.eq("is_active", 1);
                    w1.eq("is_delete", 0);
                    w1.setSqlSelect("id,dept_name,dept_code");
                    depts = autDepartmentService.selectList(w1);
                }
            }
            Date startTime = null;
            Date endTime = null;
            String weets = obj.getThisWeek();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            if ((obj.getTime1() != "" && "".equals(obj.getTime1()))
                || (obj.getTime2() != null && !"".equals(obj.getTime2()))) {
                startTime = df.parse(obj.getTime1());
                df.parse(obj.getTime2());
                endTime = df.parse(obj.getTime2());
                cal.setTime(endTime);
                // cal.add(Calendar.DAY_OF_MONTH, 1);
                endTime = cal.getTime();
            } else {
                if ("0".equals(weets)) {
                    // 30天
                    Date newDate = new Date();
                    cal.setTime(df.parse(df.format(newDate)));
                    endTime = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -30);
                    startTime = cal.getTime();

                }
                if ("1".equals(weets)) {
                    // 本周
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                    startTime = cal.getTime();
                    cal.setTime(df.parse(df.format(startTime)));
                    startTime = cal.getTime();
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                    cal.add(Calendar.WEEK_OF_YEAR, 1);
                    endTime = cal.getTime();
                }
                if ("2".equals(weets)) {
                    // 本月
                    String newDate = df.format(new Date());
                    String[] newTime = newDate.split("-");
                    newDate = newTime[0] + "-" + newTime[1] + "-01";
                    cal.setTime(df.parse(newDate));
                    // cal.set(Calendar.DAY_OF_MONTH, 0);
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.add(Calendar.SECOND, 0);
                    startTime = cal.getTime();
                    cal.add(Calendar.MONTH, 1);
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    endTime = cal.getTime();
                }
            }
            // List<AttSignInOut> attSignList = new ArrayList<AttSignInOut>();
            List<AttSignInOutHis> attSignHisList = new ArrayList<AttSignInOutHis>();
            Map<String, AttSignInCount> userCountList = new HashMap<String, AttSignInCount>();
            String userIds = "";
            Where<AttSignInOutHis> w4 = new Where<AttSignInOutHis>();
            w4.setSqlSelect(
                "sign_user_id,am_sign_in_status,am_sign_in_patch_status,am_sign_out_status,am_sign_out_patch_status,pm_sign_in_status,pm_sign_in_patch_status,pm_sign_out_status,pm_sign_out_patch_status");
            w4.between("sign_date", startTime, dfs.parse(df.format(endTime) + " 23:59:59"));
            attSignHisList = attSignInOutHisService.selectList(w4);

            if (attSignHisList != null && attSignHisList.size() > 0) {
                for (int i = 0; i < attSignHisList.size(); i++) {
                    AttSignInCount userCount = new AttSignInCount();
                    AttSignInOutHis intOut = attSignHisList.get(i);
                    int noSign = 0, noSignOut = 0, late = 0, leaveEarly = 0, retroactive = 0;
                    if (userIds.indexOf(intOut.getSignUserId().toString()) > -1 && !"".equals(userIds)) {
                        userCount = userCountList.get(intOut.getSignUserId().toString());
                        noSign = userCount.getNoneSignInNums();
                        noSignOut = userCount.getNoneSignOutNums();
                        late = userCount.getLateNums();
                        leaveEarly = userCount.getLeaveEarlyNums();
                        retroactive = userCount.getSignPatchNums();
                    } else {
                        userIds += intOut.getSignUserId().toString() + ",";
                    }
                    if ("2".equals(intOut.getAmSignInStatus().toString())) {
                        late += 1;
                    }
                    if ("3".equals(intOut.getAmSignInStatus().toString())) {
                        noSign += 1;
                    }
                    if ("3".equals(intOut.getAmSignInPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    if ("2".equals(intOut.getAmSignOutStatus().toString())) {
                        leaveEarly += 1;
                    }
                    if ("3".equals(intOut.getAmSignOutStatus().toString())) {
                        noSignOut += 1;
                    }
                    if ("3".equals(intOut.getAmSignOutPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    if ("2".equals(intOut.getPmSignInStatus().toString())) {
                        late += 1;
                    }
                    if ("3".equals(intOut.getPmSignInStatus().toString())) {
                        noSign += 1;
                    }
                    if ("3".equals(intOut.getPmSignInPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    if ("2".equals(intOut.getPmSignOutStatus().toString())) {
                        leaveEarly += 1;
                    }
                    if ("3".equals(intOut.getPmSignOutStatus().toString())) {
                        noSignOut += 1;
                    }
                    if ("3".equals(intOut.getPmSignOutPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    userCount.setNoneSignInNums(noSign);
                    userCount.setNoneSignOutNums(noSignOut);
                    userCount.setLateNums(late);
                    userCount.setLeaveEarlyNums(leaveEarly);
                    userCount.setSignPatchNums(retroactive);
                    userCountList.put(intOut.getSignUserId().toString(), userCount);
                }
            }
            attSignHisList.clear();
            for (AutDepartment department : depts) {
                Where<AutDepartment> deptWhere = new Where<AutDepartment>();
                deptWhere.where("dept_code like {0}", department.getDeptCode() + "%");
                deptWhere.eq("is_active", 1);
                deptWhere.eq("is_delete", 0);
                deptWhere.setSqlSelect("id");
                List<AutDepartment> autDepartmentList = autDepartmentService.selectList(deptWhere);
                List<Long> deptIdList = new ArrayList<Long>();
                for (AutDepartment dept : autDepartmentList) {
                    deptIdList.add(dept.getId());
                }
                Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
                w2.in("dept_id", deptIdList);
                List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w2);
                List<Long> userIdList = new ArrayList<Long>();
                for (AutDepartmentUser deptUser : deptUserList) {
                    userIdList.add(deptUser.getUserId());
                }
                AttSignInCount count = new AttSignInCount();
                count.setDeptName(department.getDeptName());
                // 未签到，未签退，迟到，早退，补签
                int noSign = 0, noSignOut = 0, late = 0, leaveEarly = 0, retroactive = 0;
                if (userIdList != null && userIdList.size() > 0) {
                    for (Long autUser : userIdList) {
                        if (userCountList != null && userCountList.size() > 0) {
                            AttSignInCount userCount = userCountList.get(autUser.toString());
                            if (userCount != null) {
                                noSign += userCount.getNoneSignInNums();
                                noSignOut += userCount.getNoneSignOutNums();
                                late += userCount.getLateNums();
                                leaveEarly += userCount.getLeaveEarlyNums();
                                retroactive += userCount.getSignPatchNums();
                            }
                        }
                    }
                }

                count.setNoneSignInNums(noSign);
                count.setNoneSignOutNums(noSignOut);
                count.setLateNums(late);
                count.setLeaveEarlyNums(leaveEarly);
                count.setSignPatchNums(retroactive);
                signInCountList.add(count);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return signInCountList;
    }

    @Override
    public List<AttSignInCount> getLAttSignInCount(AttSignInCount obj) throws Exception {

        List<AttSignInCount> signInCountList = new ArrayList<AttSignInCount>();
        List<AutDepartment> depts = new ArrayList<AutDepartment>();
        try {
            Where<AutDepartment> w1 = new Where<AutDepartment>();
            w1.eq("is_active", 1);
            w1.eq("is_delete", 0);
            w1.setSqlSelect("id,dept_name,dept_code");
            depts = autDepartmentService.selectList(w1);
            Date startTime = null;
            Date endTime = null;
            // 30天
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            Date newDate = new Date();
            cal.setTime(df.parse(df.format(newDate)));
            endTime = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.DAY_OF_MONTH, -30);
            startTime = cal.getTime();
            cal.setTime(endTime);

            // cal.add(Calendar.DAY_OF_MONTH, -1);
            String showDate = df.format(startTime) + "至" + df.format(cal.getTime());
            List<AttSignInOutHis> attSignHisList = new ArrayList<AttSignInOutHis>();
            Map<String, AttSignInCount> userCountList = new HashMap<String, AttSignInCount>();
            String userIds = "";
            Where<AttSignInOutHis> w4 = new Where<AttSignInOutHis>();
            w4.setSqlSelect(
                "sign_user_id,am_sign_in_status,am_sign_in_patch_status,am_sign_out_status,am_sign_out_patch_status,pm_sign_in_status,pm_sign_in_patch_status,pm_sign_out_status,pm_sign_out_patch_status");
            w4.between("sign_date", startTime, dfs.parse(df.format(endTime) + " 23:59:59"));
            attSignHisList = attSignInOutHisService.selectList(w4);
            if (attSignHisList != null && attSignHisList.size() > 0) {
                for (int i = 0; i < attSignHisList.size(); i++) {
                    AttSignInCount userCount = new AttSignInCount();
                    AttSignInOutHis intOut = attSignHisList.get(i);
                    int noSign = 0, noSignOut = 0, late = 0, leaveEarly = 0, retroactive = 0;
                    if (userIds.indexOf(intOut.getSignUserId().toString()) > -1 && !"".equals(userIds)) {
                        userCount = userCountList.get(intOut.getSignUserId().toString());
                        noSign = userCount.getNoneSignInNums();
                        noSignOut = userCount.getNoneSignOutNums();
                        late = userCount.getLateNums();
                        leaveEarly = userCount.getLeaveEarlyNums();
                        retroactive = userCount.getSignPatchNums();
                    } else {
                        userIds += intOut.getSignUserId().toString() + ",";
                    }
                    if ("2".equals(intOut.getAmSignInStatus().toString())) {
                        late += 1;
                    }
                    if ("3".equals(intOut.getAmSignInStatus().toString())) {
                        noSign += 1;
                    }
                    if ("3".equals(intOut.getAmSignInPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    if ("2".equals(intOut.getAmSignOutStatus().toString())) {
                        leaveEarly += 1;
                    }
                    if ("3".equals(intOut.getAmSignOutStatus().toString())) {
                        noSignOut += 1;
                    }
                    if ("3".equals(intOut.getAmSignOutPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    if ("2".equals(intOut.getPmSignInStatus().toString())) {
                        late += 1;
                    }
                    if ("3".equals(intOut.getPmSignInStatus().toString())) {
                        noSign += 1;
                    }
                    if ("3".equals(intOut.getPmSignInPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    if ("2".equals(intOut.getPmSignOutStatus().toString())) {
                        leaveEarly += 1;
                    }
                    if ("3".equals(intOut.getPmSignOutStatus().toString())) {
                        noSignOut += 1;
                    }
                    if ("3".equals(intOut.getPmSignOutPatchStatus().toString())) {
                        retroactive += 1;
                    }
                    userCount.setNoneSignInNums(noSign);
                    userCount.setNoneSignOutNums(noSignOut);
                    userCount.setLateNums(late);
                    userCount.setLeaveEarlyNums(leaveEarly);
                    userCount.setSignPatchNums(retroactive);
                    userCountList.put(intOut.getSignUserId().toString(), userCount);
                }
            }
            attSignHisList.clear();
            for (AutDepartment department : depts) {
                Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
                w2.eq("dept_id", department.getId());
                List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w2);
                List<Long> userIdList = new ArrayList<Long>();
                if (deptUserList == null || deptUserList.size() < 1) {
                    continue;
                }
                for (AutDepartmentUser deptUser : deptUserList) {
                    userIdList.add(deptUser.getUserId());
                }
                AttSignInCount count = new AttSignInCount();
                count.setDeptName(department.getDeptName());
                // 未签到，未签退，迟到，早退，补签
                int noSign = 0, noSignOut = 0, late = 0, leaveEarly = 0, retroactive = 0;
                if (userIdList != null && userIdList.size() > 0) {
                    for (Long autUser : userIdList) {
                        if (userCountList != null && userCountList.size() > 0) {
                            AttSignInCount userCount = userCountList.get(autUser.toString());
                            if (userCount != null) {
                                noSign += userCount.getNoneSignInNums();
                                noSignOut += userCount.getNoneSignOutNums();
                                late += userCount.getLateNums();
                                leaveEarly += userCount.getLeaveEarlyNums();
                                retroactive += userCount.getSignPatchNums();
                            }
                        }
                    }
                }

                count.setShowDate(showDate);
                count.setNoneSignInNums(noSign);
                count.setNoneSignOutNums(noSignOut);
                count.setLateNums(late);
                count.setLeaveEarlyNums(leaveEarly);
                count.setSignPatchNums(retroactive);
                signInCountList.add(count);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return signInCountList;
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
    public void updateSignInOutDate(String signDate, Long signUserId, AttSignInOut attSignInOut, ActAljoinBpmn bpmn,
        Date date, String processInstanceId) {
        // 查询本表中这条需要补签的记录
        AttSignInOut signInOut = new AttSignInOut();
        Where<AttSignInOut> w1 = new Where<AttSignInOut>();
        w1.eq("sign_date", signDate);
        w1.eq("sign_user_id", signUserId);
        w1.setSqlSelect(
            "id,create_time,version,is_delete,create_user_id,create_user_name,sign_user_id,sign_date,am_sign_in_patch_status,am_sign_in_patch_time,am_sign_in_patch_desc,am_sign_out_patch_status,am_sign_out_patch_time,am_sign_out_patch_desc,pm_sign_in_patch_status,pm_sign_in_patch_time,pm_sign_in_patch_desc,pm_sign_out_patch_status,pm_sign_out_patch_time,pm_sign_out_patch_desc,process_id,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
        signInOut = selectOne(w1);
        // 查询历史表
        AttSignInOutHis signInOutHis = new AttSignInOutHis();
        Where<AttSignInOutHis> hisW1 = new Where<AttSignInOutHis>();
        hisW1.eq("sign_date", signDate);
        hisW1.eq("sign_user_id", signUserId);
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
                    updateById(signInOut);
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
                    updateById(signInOut);
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
                    updateById(signInOut);
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
                    updateById(signInOut);
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

    public List<AttSignInOutHisVO> getSignInOutPatchHisList(AttSignInOutHisVO obj) throws Exception {
        List<AttSignInOutHisVO> list = new ArrayList<AttSignInOutHisVO>();
        if (null != obj && StringUtils.isNotEmpty(obj.getIds())) {
            if (obj.getIds().indexOf(";") > -1 || obj.getIds().endsWith(";")) {
                List<String> signOutIdList = Arrays.asList(obj.getIds().split(";"));
                if (null != signOutIdList && !signOutIdList.isEmpty()) {
                    Where<AttSignInOutHis> where = new Where<AttSignInOutHis>();
                    where.in("id", signOutIdList);
                    // where.andNew("am_sign_in_patch_status = {0} or
                    // am_sign_out_patch_status = {1} or pm_sign_in_patch_status
                    // = {2} or pm_sign_out_patch_status = {3}",2,2,2,2);
                    where.setSqlSelect(
                        "id,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_patch_desc,am_sign_out_patch_desc,pm_sign_in_patch_desc,pm_sign_out_patch_desc,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                    List<AttSignInOutHis> signInOutList = attSignInOutHisService.selectList(where);
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        Integer status = 0;
                        for (AttSignInOutHis attSignInOut : signInOutList) {
                            if (null != attSignInOut) {
                                if (null != attSignInOut.getAmSignInPatchStatus()) {
                                    status = attSignInOut.getAmSignInStatus();
                                    if (attSignInOut.getAmSignInPatchStatus() != 1) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignInPatchStatus(attSignInOut.getAmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignInPatchDesc())
                                                ? attSignInOut.getAmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignInTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getAmSignOutPatchStatus()) {
                                    status = attSignInOut.getAmSignOutStatus();
                                    if (attSignInOut.getAmSignOutPatchStatus() != 1) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignOutPatchStatus(attSignInOut.getAmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignOutPatchDesc())
                                                ? attSignInOut.getAmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignOutTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignInPatchStatus()) {
                                    status = attSignInOut.getPmSignInStatus();
                                    if (attSignInOut.getPmSignInPatchStatus() != 1) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignInPatchStatus(attSignInOut.getPmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignInPatchDesc())
                                                ? attSignInOut.getPmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignInTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignOutPatchStatus()) {
                                    status = attSignInOut.getPmSignOutStatus();
                                    if (attSignInOut.getPmSignOutPatchStatus() != 1) {
                                        AttSignInOutHisVO signInOut = new AttSignInOutHisVO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignOutPatchStatus(attSignInOut.getPmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignOutPatchDesc())
                                                ? attSignInOut.getPmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignOutTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        list.add(signInOut);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    @Transactional
    public RetMsg addSign(Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != userId) {
            List<AttSignInOut> attSignInOutList = new ArrayList<AttSignInOut>();
            Where<AttSignInOut> signInOutWhere = new Where<AttSignInOut>();
            signInOutWhere.setSqlSelect("id");

            signInOutWhere.eq("sign_user_id", userId);

            int count = selectCount(signInOutWhere);
            if (count == 0) {
                Where<AutUser> where = new Where<AutUser>();

                where.eq("id", userId);

                List<AutUser> userList = autUserService.selectList(where);
                int curMonthDays = DateUtil.getDaysOfMonth();
                String partten = WebConstant.PATTERN_BAR_YYYYMMDD;
                DateTimeFormatter format = DateTimeFormat.forPattern(partten);
                if (null != userList && !userList.isEmpty()) {

                    SysParameter amSignIn = sysParameterService.selectBykey("am_allow_late");
                    SysParameter amSignOut = sysParameterService.selectBykey("am_leave_early");
                    SysParameter pmSignIn = sysParameterService.selectBykey("pm_allow_late");
                    SysParameter pmSignOut = sysParameterService.selectBykey("pm_leave_early");

                    for (AutUser user : userList) {
                        for (int i = 0; i < curMonthDays; i++) {
                            AttSignInOut attSignInOut = new AttSignInOut();
                            attSignInOut.setSignUserId(user.getId());// 签到用户ID
                            attSignInOut.setSignUserName(user.getUserName());// 签到用户账号
                            DateTime dateTime = DateTime.parse(DateUtil.getFirstDateOfMonth(partten), format);
                            Date signDate = dateTime.plusDays(i).toDate();
                            attSignInOut.setSignDate(signDate);
                            String week = DateUtil.getWeek(signDate);// 获得星期几
                            attSignInOut.setSignWeek(week);// 星期几
                            boolean isWorkDay = getIsWorkDay(signDate);
                            if (!isWorkDay) {
                                attSignInOut.setIsWorkDay(0);
                            } else {
                                attSignInOut.setIsWorkDay(1);
                            }

                            if (null == amSignIn) {
                                retMsg.setCode(1);
                                retMsg.setMessage(WebConstant.ATT_AM_ALLOWLATE_ERRORMSG);
                                return retMsg;
                            } else {
                                if (null != amSignIn && StringUtils.isNotEmpty(amSignIn.getParamValue())) {
                                    attSignInOut.setAmSignInBufferTime(Integer.parseInt(amSignIn.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                                }
                            }

                            if (null == amSignOut) {
                                retMsg.setCode(1);
                                retMsg.setMessage(WebConstant.ATT_AM_LEAVEEARLY_ERRORMSG);
                                return retMsg;
                            } else {
                                if (null != amSignOut && StringUtils.isNotEmpty(amSignOut.getParamValue())) {
                                    attSignInOut.setAmSignOutBufferTime(Integer.parseInt(amSignOut.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                                }
                            }

                            if (null == pmSignIn) {
                                retMsg.setCode(1);
                                retMsg.setMessage(WebConstant.ATT_PM_ALLOWLATE_ERRORMSG);
                                return retMsg;
                            } else {
                                if (null != pmSignIn && StringUtils.isNotEmpty(pmSignIn.getParamValue())) {
                                    attSignInOut.setPmSignInBufferTime(Integer.parseInt(pmSignIn.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                                }
                            }

                            if (null == pmSignOut) {
                                retMsg.setCode(1);
                                retMsg.setMessage(WebConstant.ATT_PM_LEAVEEARLY_ERRORMSG);
                                return retMsg;
                            } else {
                                if (null != pmSignOut && StringUtils.isNotEmpty(pmSignOut.getParamValue())) {
                                    attSignInOut.setPmSignOutBufferTime(Integer.parseInt(pmSignOut.getParamValue()));// 上午签到缓冲时间（暂时设置为30）
                                }
                            }
                            attSignInOut.setAmSignInStatus(3);// 上午签到状态
                            attSignInOut.setAmSignInPatchStatus(1);// 上午签到补签状态
                            if (StringUtils.isNotEmpty(amSignIn.getParamValue())) {
                                attSignInOut.setAmSignInBufferTime(Integer.parseInt(amSignIn.getParamValue()));// 上午签到缓冲时间
                            }

                            attSignInOut.setAmSignOutStatus(3);// 上午签退状态
                            attSignInOut.setAmSignOutPatchStatus(1);// 上午签退补签状态
                            if (StringUtils.isNotEmpty(amSignOut.getParamValue())) {
                                attSignInOut.setAmSignInBufferTime(Integer.parseInt(amSignOut.getParamValue()));// 上午签退缓冲时间
                            }
                            attSignInOut.setPmSignInStatus(3);// 下午签到状态
                            attSignInOut.setPmSignInPatchStatus(1);// 下午签到补签状态
                            if (StringUtils.isNotEmpty(pmSignIn.getParamValue())) {
                                attSignInOut.setAmSignInBufferTime(Integer.parseInt(pmSignIn.getParamValue()));// 下午签到缓冲时间
                            }
                            if (StringUtils.isNotEmpty(pmSignOut.getParamValue())) {
                                attSignInOut.setPmSignOutBufferTime(Integer.parseInt(pmSignOut.getParamValue()));// 下午签退缓冲时间
                            }
                            attSignInOut.setPmSignOutStatus(3);// 下午签退状态
                            attSignInOut.setPmSignOutPatchStatus(1);// 下午签退补签时间

                            attSignInOutList.add(attSignInOut);
                        }
                    }
                }
                if (null != attSignInOutList && !attSignInOutList.isEmpty()) {
                    // 批量插入本月考勤数据att_signin_out
                    insertBatch(attSignInOutList);
                    List<AttSignInOutHis> attSignInOutHisList = new ArrayList<AttSignInOutHis>();
                    for (AttSignInOut signInOut : attSignInOutList) {
                        AttSignInOutHis signInOutHis = new AttSignInOutHis();
                        BeanUtils.copyProperties(signInOut, signInOutHis);
                        attSignInOutHisList.add(signInOutHis);
                    }
                    if (null != attSignInOutHisList && !attSignInOutHisList.isEmpty()) {
                        // 批量插入历史表 att_signin_out_his
                        attSignInOutHisService.insertBatch(attSignInOutHisList);
                    }
                    retMsg.setCode(0);
                    retMsg.setMessage("操作成功");
                }
            }
        }
        return retMsg;
    }

    @Override
    public RetMsg getNextTaskInfo2(String taskId, String nextStept) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<CustomerTaskDefinition> taskDefinitionList = new ArrayList<CustomerTaskDefinition>();
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        Map<String, String> map = new HashMap<String, String>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        map.put("nextStept", String.valueOf(nextStept));
        List<TaskDefinition> list = actFixedFormService.getNextTaskInfo2(processInstanceId, true, currentTaskKey, map);

        if (null != list && !list.isEmpty()) {
            for (TaskDefinition definition : list) {
                if (null != definition) {
                    CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
                    taskDefinition.setKey(definition.getKey());
                    taskDefinition.setAssignee(String.valueOf(definition.getAssigneeExpression()));
                    taskDefinition.setNextNodeName(definition.getNameExpression().getExpressionText());
                    taskDefinitionList.add(taskDefinition);
                }
            }
        }
        resultMap.put("taskDefinitionList", taskDefinitionList);
        resultMap.put("nextStept", nextStept);
        retMsg.setCode(0);
        retMsg.setObject(resultMap);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<AttSignInCount> getAttSignInHisCountList(AttSignInCount obj) {
        List<AttSignInCount> signInCountList = new ArrayList<AttSignInCount>();
        try {
            Long deptId = obj.getDeptId();
            // List<Long> deptUserIdList = new ArrayList<Long>();// 领导所在部门所有成员
            AutDepartment au = new AutDepartment();
            au.setId(deptId);
            List<AutUser> autDepartmentUserList = autDepartmentService.getChildDeptUserList(au);

            List<Long> userIdList = new ArrayList<Long>();
            for (AutUser autDepartmentUser : autDepartmentUserList) {
                userIdList.add(autDepartmentUser.getId());
            }
            // 部门有用户才进行查询
            if (userIdList != null && !userIdList.isEmpty()) {
                // 查询用户表，以获取每个userId对应的userName
                Where<AutUser> w2 = new Where<AutUser>();
                w2.setSqlSelect("id,user_name,full_name");
                w2.in("id", userIdList);
                List<AutUser> autUserList = autUserService.selectList(w2);
                // 查询出该部门所有用户签到记录
                Where<AttSignInOutHis> w3 = new Where<AttSignInOutHis>();
                w3.setSqlSelect("sign_user_id,sign_user_name,sign_date,"
                    + "am_sign_in_status,am_sign_in_patch_status,am_sign_out_status,am_sign_out_patch_status,"
                    + "pm_sign_in_status,pm_sign_in_patch_status,pm_sign_out_status,pm_sign_out_patch_status,"
                    + "am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status");

                w3.in("sign_user_id", userIdList);
                Date date = new Date();
                // 默认查询30天前的签到
                if (StringUtils.isEmpty(obj.getThisWeek()) && StringUtils.isEmpty(obj.getThisMonth())
                    && StringUtils.isEmpty(obj.getTime1()) && StringUtils.isEmpty(obj.getTime2())) {
                    Date dateBeforeThirty = DateUtil.getBefore(date, "day", 30);
                    w3.ge("sign_date", dateBeforeThirty);
                    // 查询本周签到
                } else if (StringUtils.isNotEmpty(obj.getTime1()) && StringUtils.isNotEmpty(obj.getTime2())) {
                    w3.between("sign_date", DateUtil.str2dateOrTime(obj.getTime1()),
                        DateUtil.str2dateOrTime(obj.getTime2()));
                } else if (StringUtils.isNotEmpty(obj.getTime1())) {
                    w3.ge("sign_date", DateUtil.str2dateOrTime(obj.getTime1()));
                } else if (StringUtils.isNotEmpty(obj.getTime2())) {
                    w3.le("sign_date", DateUtil.str2dateOrTime(obj.getTime2()));
                }
                w3.orderBy("sign_user_id", true);

                List<AutDepartmentUser> departmentUserList = new ArrayList<AutDepartmentUser>();
                // 部门列表
                AutDepartment dept = new AutDepartment();
                List<AutDepartment> deptList = new ArrayList<AutDepartment>();
                if (obj.getDeptId() != null) {
                    dept.setId(obj.getDeptId());
                    deptList = autDepartmentService.getChildDeptList(dept);
                }
                // 部门用户表
                if (null != deptList && !deptList.isEmpty()) {
                    Where<AutDepartmentUser> where1 = new Where<AutDepartmentUser>();
                    // where1.setSqlSelect("");
                    where1.and("dept_code like {0}", deptList.get(0).getDeptCode() + "%");
                    departmentUserList = autDepartmentUserService.selectList(where1);// 当前用户部门列表

                }

                List<AttSignInOutHis> attSignInOutList = attSignInOutHisService.selectList(w3);
                // 如果有查询到签到记录则返回对应值
                if (null != attSignInOutList && attSignInOutList.size() != 0) {
                    for (AutUser autUser : autUserList) {
                        if (autUser != null) {
                            AttSignInCount signInCount = new AttSignInCount();
                            signInCount.setSignUserName(autUser.getFullName());
                            for (AttSignInOutHis attSignInOut : attSignInOutList) {
                                // 比对是否同1用户
                                if (autUser.getId().equals(attSignInOut.getSignUserId())
                                    || autUser.getId() == attSignInOut.getSignUserId()) {
                                    // 未签到次数
                                    signInCount.setNoneSignInNums(signInCount.getNoneSignInNums()
                                        + (attSignInOut.getAmSignInStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignInStatus().intValue() == 3 ? 1 : 0));
                                    // 未签退次数
                                    signInCount.setNoneSignOutNums(signInCount.getNoneSignOutNums()
                                        + (attSignInOut.getAmSignOutStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutStatus().intValue() == 3 ? 1 : 0));
                                    // 迟到次数
                                    signInCount.setLateNums(signInCount.getLateNums()
                                        + (attSignInOut.getAmSignInStatus().intValue() == 2 ? 1 : 0)
                                        + (attSignInOut.getPmSignInStatus().intValue() == 2 ? 1 : 0));
                                    // 早退次数
                                    signInCount.setLeaveEarlyNums(signInCount.getLeaveEarlyNums()
                                        + (attSignInOut.getAmSignOutStatus().intValue() == 2 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutStatus().intValue() == 2 ? 1 : 0));
                                    signInCount.setSignUserId(attSignInOut.getSignUserId());
                                    // 补签次数
                                    signInCount.setSignPatchNums(signInCount.getSignPatchNums()
                                        + (attSignInOut.getAmSignInPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getAmSignOutPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignInPatchStatus().intValue() == 3 ? 1 : 0)
                                        + (attSignInOut.getPmSignOutPatchStatus().intValue() == 3 ? 1 : 0));
                                }
                            }
                            Long deptid = new Long(0);
                            String deptName = "";
                            for (AutDepartmentUser deptUser : departmentUserList) {
                                if (autUser.getId().equals(deptUser.getUserId())) {
                                    deptid = deptUser.getDeptId();
                                }
                            }
                            for (AutDepartment autdept : deptList) {
                                if (deptid.equals(autdept.getId())) {
                                    deptName = autdept.getDeptName();
                                }
                            }
                            signInCount.setDepartment(deptName);
                            signInCountList.add(signInCount);

                        }

                    }
                    // 未查询到签到记录，则返回对应用户的值为0
                } else {
                    Long deptid = new Long(0);
                    for (AutUser autUser : autUserList) {
                        if (autUser != null) {
                            AttSignInCount signInCount = new AttSignInCount();
                            signInCount.setNoneSignInNums(0);
                            signInCount.setNoneSignOutNums(0);
                            signInCount.setLateNums(0);
                            signInCount.setLeaveEarlyNums(0);
                            signInCount.setSignUserName(autUser.getFullName());
                            String deptName = "";
                            for (AutDepartmentUser deptUser : departmentUserList) {
                                if (autUser.getId().equals(deptUser.getUserId())) {
                                    deptid = deptUser.getDeptId();
                                }
                            }
                            for (AutDepartment autdept : deptList) {
                                if (deptid.equals(autdept.getId())) {
                                    deptName = autdept.getDeptName();
                                }
                            }
                            signInCount.setDepartment(deptName);
                            signInCountList.add(signInCount);

                        }
                    }
                }

            }
            // 排序
            sortList(signInCountList, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return signInCountList;
    }

    public AttSignInCount getSignPathUserInfo2(Long userId) throws Exception {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(WebConstant.PATTERN_BAR_YYYYMMDD);
        String signPathTime = sdf.format(date);
        AutUser autUser = autUserService.selectById(userId);

        Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
        w1.setSqlSelect("id,user_id,dept_id");
        w1.eq("user_id", userId);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w1);

        AutDepartment dept = new AutDepartment();
        if (null != deptUserList && !deptUserList.isEmpty()) {
            Long deptId = deptUserList.get(0).getDeptId();
            dept = autDepartmentService.selectById(deptId);
        }

        AttSignInCount signPathUserInfo = new AttSignInCount();

        if (StringUtils.checkValNotNull(dept)) {
            signPathUserInfo.setDeptName(dept.getDeptName());
        }
        signPathUserInfo.setSignUserName(autUser.getFullName());
        signPathUserInfo.setTime1(signPathTime);

        return signPathUserInfo;
    }

    @Override
    public RetMsg getAppSignInOutHisPatchList(AppAttSignInOutHisVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<AppAttSignInOutHisDO> list = new ArrayList<AppAttSignInOutHisDO>();
        Map<String, Object> map = new HashMap<String, Object>();
        String processInstanceId = obj.getProcessInstanceId();
        Integer claimStatus = 0;
        if (StringUtils.isNotEmpty(processInstanceId)) {
            Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
            List<HistoricActivityInstance> historicActivityInstanceList
                = historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list();
            int i = 0;
            for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
                if (StringUtils.isNotEmpty(historicActivityInstance.getTaskId())) {
                    if (historicActivityInstance.getTaskId().equals(task.getId())) {
                        if (i != 0) {
                            String activityType = historicActivityInstanceList.get(i - 1).getActivityType();
                            if (activityType.equals("userTask")) {
                                if (StringUtils.isNotEmpty(task.getAssignee())) {
                                    claimStatus = 1;
                                }
                            } else if (activityType.indexOf("Gateway") > -1) {
                                claimStatus = 1;
                            }
                        }
                    }
                } else {
                    claimStatus = 1;
                }
                i++;
            }
            processInstanceId = task.getProcessInstanceId();
        } else {
            processInstanceId = obj.getProcessInstanceId();
        }
        if (null != obj && StringUtils.isNotEmpty(obj.getIds())) {
            if (obj.getIds().indexOf(";") > -1 || obj.getIds().endsWith(";")) {
                List<String> signOutIdList = Arrays.asList(obj.getIds().split(";"));
                if (null != signOutIdList && !signOutIdList.isEmpty()) {
                    Where<AttSignInOutHis> where = new Where<AttSignInOutHis>();
                    where.in("id", signOutIdList);
                    where.setSqlSelect(
                        "id,last_update_user_id,sign_date,am_sign_in_patch_status,am_sign_out_patch_status,pm_sign_in_patch_status,pm_sign_out_patch_status,version,am_sign_in_patch_audit_time,am_sign_out_patch_audit_time,pm_sign_in_patch_audit_time,pm_sign_out_patch_audit_time,am_sign_in_patch_desc,am_sign_out_patch_desc,pm_sign_in_patch_desc,pm_sign_out_patch_desc,am_sign_in_time,am_sign_out_time,pm_sign_in_time,pm_sign_out_time,am_sign_in_status,am_sign_out_status,pm_sign_in_status,pm_sign_out_status,am_sign_in_proc_inst_id,am_sign_out_proc_inst_id,pm_sign_in_proc_inst_id,pm_sign_out_proc_inst_id");
                    List<AttSignInOutHis> signInOutList = attSignInOutHisService.selectList(where);
                    if (null != signInOutList && !signInOutList.isEmpty()) {
                        Integer status = 0;
                        Long userId = signInOutList.get(0).getLastUpdateUserId();
                        AppAttSignInUserInfo attSignInCount = getAppSignPathUserInfo(userId);
                        map.put("attSignInCount", attSignInCount);
                        for (AttSignInOutHis attSignInOut : signInOutList) {
                            if (null != attSignInOut) {
                                status = attSignInOut.getAmSignInStatus();

                                if (null != attSignInOut.getAmSignInPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getAmSignInProcInstId())) {
                                    if (attSignInOut.getAmSignInPatchStatus() == 2
                                        && attSignInOut.getAmSignInProcInstId().equals(processInstanceId)) {
                                        AppAttSignInOutHisDO signInOut = new AppAttSignInOutHisDO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignInPatchStatus(attSignInOut.getAmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignInPatchDesc())
                                                ? attSignInOut.getAmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignInTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        signInOut.setClaimStatus(claimStatus);
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getAmSignOutPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getAmSignOutProcInstId())) {
                                    status = attSignInOut.getAmSignOutStatus();
                                    if (attSignInOut.getAmSignOutPatchStatus() == 2
                                        && attSignInOut.getAmSignOutProcInstId().equals(processInstanceId)) {
                                        AppAttSignInOutHisDO signInOut = new AppAttSignInOutHisDO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setAmSignOutPatchStatus(attSignInOut.getAmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getAmSignOutPatchDesc())
                                                ? attSignInOut.getAmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getAmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getAmSignOutTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        signInOut.setClaimStatus(claimStatus);
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignInPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getPmSignInProcInstId())) {
                                    status = attSignInOut.getPmSignInStatus();
                                    if (attSignInOut.getPmSignInPatchStatus() == 2
                                        && attSignInOut.getPmSignInProcInstId().equals(processInstanceId)) {
                                        AppAttSignInOutHisDO signInOut = new AppAttSignInOutHisDO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignInPatchStatus(attSignInOut.getPmSignInPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignInPatchDesc())
                                                ? attSignInOut.getPmSignInPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LATE;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignInTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignInTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        signInOut.setClaimStatus(claimStatus);
                                        list.add(signInOut);
                                    }
                                }
                                if (null != attSignInOut.getPmSignOutPatchStatus()
                                    && StringUtils.isNotEmpty(attSignInOut.getPmSignOutProcInstId())) {
                                    status = attSignInOut.getPmSignOutStatus();
                                    if (attSignInOut.getPmSignOutPatchStatus() == 2
                                        && attSignInOut.getPmSignOutProcInstId().equals(processInstanceId)) {
                                        AppAttSignInOutHisDO signInOut = new AppAttSignInOutHisDO();
                                        signInOut.setId(attSignInOut.getId());
                                        signInOut.setPmSignOutPatchStatus(attSignInOut.getPmSignOutPatchStatus());
                                        signInOut
                                            .setPatchReason(StringUtils.isNotEmpty(attSignInOut.getPmSignOutPatchDesc())
                                                ? attSignInOut.getPmSignOutPatchDesc() : "");
                                        String patchStatus = "";
                                        if (1 == status) {
                                            patchStatus = WebConstant.ATT_CLOCK_ON_TIME;
                                        } else if (2 == status) {
                                            patchStatus = WebConstant.ATT_LEAVE_EARLY;
                                        } else if (3 == status) {
                                            patchStatus = WebConstant.ATT_NO_CLOCKING;
                                        }
                                        signInOut.setPatchStatus(patchStatus);
                                        if (null != attSignInOut.getPmSignOutTime()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getPmSignOutTime());
                                            signInOut
                                                .setSignPatchDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        } else {
                                            signInOut.setSignPatchDate("无");
                                        }
                                        if (null != attSignInOut.getSignDate()) {
                                            DateTime dateTime = new DateTime(attSignInOut.getSignDate());
                                            signInOut
                                                .setSignDateStr(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
                                        }
                                        signInOut.setClaimStatus(claimStatus);
                                        list.add(signInOut);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        map.put("dataObj", list);
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setObject(map);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    public AppAttSignInUserInfo getAppSignPathUserInfo(Long userId) throws Exception {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(WebConstant.PATTERN_BAR_YYYYMMDD);
        String signPathTime = sdf.format(date);
        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.setSqlSelect("id,user_name,full_name");
        userWhere.eq("id", userId);
        AutUser autUser = autUserService.selectOne(userWhere);

        Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
        w1.setSqlSelect("id,user_id,dept_id");
        w1.eq("user_id", userId);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w1);

        AutDepartment dept = new AutDepartment();
        if (null != deptUserList && !deptUserList.isEmpty()) {
            Long deptId = deptUserList.get(0).getDeptId();
            dept = autDepartmentService.selectById(deptId);
        }

        AppAttSignInUserInfo signPathUserInfo = new AppAttSignInUserInfo();

        if (null != dept) {
            signPathUserInfo.setDeptName(dept.getDeptName());
        }
        signPathUserInfo.setSignUserName(autUser.getFullName());
        signPathUserInfo.setSignPathTime(signPathTime);
        signPathUserInfo.setSignUserId(autUser.getId());

        return signPathUserInfo;
    }

    @Override
    public RetMsg checkAppNextTaskInfo(String taskId, Long currentUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
        Map<String, String> variablemap = new HashMap<String, String>();
        Integer nextStept = getNextStep(currentUserId);
        variablemap.put("nextStept", String.valueOf(nextStept));
        List<TaskDefinition> list
            = actFixedFormService.getNextTaskInfo2(processInstanceId, true, currentTaskKey, variablemap);
        Map<String, Object> map = new HashMap<String, Object>();
        List<CustomerTaskDefinition> nextTaskList = new ArrayList<CustomerTaskDefinition>();

        if (null != list && !list.isEmpty()) {
            String assigneeId = "";
            String assigneeUserId = "";
            String assigneeGroupId = "";
            TaskDefinition definition = list.get(0);
            CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
            if (null != definition) {
                taskDefinition.setKey(definition.getKey());
                taskDefinition.setNextNodeName(definition.getNameExpression().getExpressionText());
                if (definition.getAssigneeExpression() == null
                    && definition.getCandidateGroupIdExpressions().size() == 0
                    && definition.getCandidateUserIdExpressions().size() == 0) {
                    // 选项为空时弹出整棵组织机构树
                    AutDepartmentUserVO autDepartmentUser = new AutDepartmentUserVO();
                    AutOrganVO organVO = autDepartmentUserService.getOrganList(autDepartmentUser);
                    map.put("organ", organVO);
                }
                if (null != definition.getAssigneeExpression()) {
                    taskDefinition.setAssignee(definition.getAssigneeExpression().getExpressionText());
                    assigneeId = String.valueOf(definition.getAssigneeExpression());
                    // 受理人
                    List<String> assineedIdList = new ArrayList<String>();
                    if (assigneeId.endsWith(";")) {
                        assineedIdList = Arrays.asList(assigneeId.split(";"));
                    } else {
                        assineedIdList.add(assigneeId);
                    }
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", assineedIdList);
                    userWhere.setSqlSelect("id,user_name,full_name");
                    List<AutUser> assigneedList = autUserService.selectList(userWhere);
                    // 受理人不为空-就只返回受理人
                    if (null != assigneedList && !assigneedList.isEmpty()) {
                        map.put("user", assigneedList);
                        map.put("isassigneed", true);
                    }
                } else {
                    List<String> uIds = new ArrayList<String>(); // 所有候选用户id
                    List<AutUser> uList = new ArrayList<AutUser>(); // 所有候选用户

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
                    // 选择部门
                    Where<AutDepartmentUser> departmentWhere = new Where<AutDepartmentUser>();
                    departmentWhere.in("dept_id", assineedGroupIdList);
                    departmentWhere.setSqlSelect("dept_id,id,dept_code,user_id");
                    List<AutDepartmentUser> departmentList = autDepartmentUserService.selectList(departmentWhere);
                    List<AutDepartmentUserVO> departmentUserList = new ArrayList<AutDepartmentUserVO>();
                    if (null != departmentList && !departmentList.isEmpty()) {
                        // isOrgn = true;
                        // openType = "3";
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
                        String deptIds = "";
                        String userIds = "";
                        for (AutDepartmentUser departmentUser : departmentUserList) {
                            deptIds += departmentUser.getDeptId() + ";";
                            userIds += departmentUser.getUserId() + ";";
                        }
                        AutDepartmentUserVO autDepartmentUser = new AutDepartmentUserVO();
                        autDepartmentUser.setDepartmentIds(deptIds);
                        autDepartmentUser.setUserIds(userIds);
                        AutOrganVO organVO = autDepartmentUserService.getOrganList(autDepartmentUser);
                        map.put("organ", organVO);
                        map.remove("user");
                    }
                }
            }
            nextTaskList.add(taskDefinition);
        }
        List<CustomerTaskDefinition> definitionList = getPreTaskInfo(taskId);
        map.put("preTaskInfo", definitionList);
        // retMsg = actFixedFormService.getNextTaskInfo(taskId);
        // List<CustomerTaskDefinition> nextTaskList = (List<CustomerTaskDefinition>)retMsg.getObject();
        map.put("nextTask", nextTaskList);
        map.put("nextStept", String.valueOf(nextStept));
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setObject(map);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @SuppressWarnings("unchecked")
    public List<CustomerTaskDefinition> getPreTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = actFixedFormService.getPreTaskInfo2(taskId);
        List<CustomerTaskDefinition> defitionList = new ArrayList<CustomerTaskDefinition>();
        if (retMsg.getCode() == 0) {
            defitionList = (List<CustomerTaskDefinition>)retMsg.getObject();
            List<String> asignee = new ArrayList<String>();
            if (!defitionList.isEmpty()) {
                for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
                    List<String> taskAssignee = Arrays.asList(customerTaskDefinition.getAssignee().split(";"));
                    asignee.addAll(taskAssignee);
                }
            }
            if (!asignee.isEmpty()) {
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                if (asignee.size() > 1) {
                    userWhere.in("id", asignee);
                } else {
                    userWhere.eq("id", asignee.get(0));
                }
                List<AutUser> userList = autUserService.selectList(userWhere);
                for (CustomerTaskDefinition customerTaskDefinition : defitionList) {
                    for (AutUser autUser : userList) {
                        if (customerTaskDefinition.getAssignee().equals(String.valueOf(autUser.getId()))) {
                            customerTaskDefinition.setAssigneeName(autUser.getFullName());
                        }
                    }
                }
            }
        }
        return defitionList;
    }

    public Integer getNextStep(Long createUserId) throws Exception {
        // 查询当前用户所在的部门信息
        Where<AutDepartmentUser> userdeptwhere = new Where<AutDepartmentUser>();
        userdeptwhere.setSqlSelect("dept_id,dept_code");
        userdeptwhere.eq("user_id", createUserId);
        List<AutDepartmentUser> autDepartmentUsers = autDepartmentUserService.selectList(userdeptwhere);
        Integer nextStept = 3;// 默认是其他部门分支
        if (autDepartmentUsers.size() > 0) {
            AutDepartmentUser userDept = autDepartmentUsers.get(0);
            // 研究所数据字典
            List<SysDataDict> labDictList = sysDataDictService.getByCode(WebConstant.DICT_ATT_DEPARTMENT_LAB);
            // 南方中心-办公室-其他数据字典
            List<SysDataDict> dictList = sysDataDictService.getByCode(WebConstant.DICT_ATT_DEPARTMENT);
            Map<String, SysDataDict> map = new HashMap<String, SysDataDict>();
            Set<Long> deptIdList = new HashSet<Long>();
            for (SysDataDict sysDataDict : labDictList) {
                deptIdList.add(Long.valueOf(sysDataDict.getDictKey()));
                map.put(sysDataDict.getDictKey(), sysDataDict);
            }
            for (SysDataDict sysDataDict : dictList) {
                deptIdList.add(Long.valueOf(sysDataDict.getDictKey()));
                map.put(sysDataDict.getDictKey(), sysDataDict);
            }
            if (!deptIdList.isEmpty()) {
                Where<AutDepartment> deptWhere = new Where<AutDepartment>();
                deptWhere.setSqlSelect("id,dept_code");
                deptWhere.in("id", deptIdList);
                List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);
                List<AutDepartment> labDeptList = new ArrayList<AutDepartment>();
                // 先从里面识别出最特殊的：研究所-其他|研究所-办公室
                for (AutDepartment dept : deptList) {
                    for (SysDataDict sysDataDict : labDictList) {
                        if (sysDataDict.getDictKey().equals(dept.getId().toString())) {
                            labDeptList.add(dept);
                        }
                    }
                }
                // 筛选出研究所下的子部门
                List<AutDepartment> subOffice = new ArrayList<AutDepartment>();
                if (labDeptList.size() > 0) {
                    for (AutDepartment autDept : labDeptList) {
                        for (AutDepartment subDept : labDeptList) {
                            if (subDept.getDeptCode().length() < autDept.getDeptCode().length()) {
                                if (!subOffice.contains(subDept)) {
                                    subOffice.add(autDept);
                                }
                            }
                        }
                    }
                }
                // 如果研究所的数据字典有设置
                if (subOffice.size() > 0) {
                    for (AutDepartment dept : deptList) {
                        for (AutDepartment labOffice : subOffice) {
                            if (userDept.getDeptId().equals(labOffice.getId())) {
                                nextStept = map.get(labOffice.getId().toString()).getDictRank();
                                break;
                            } else if (userDept.getDeptCode().startsWith(dept.getDeptCode())
                                || userDept.getDeptCode().equals(dept.getDeptCode())) {
                                nextStept = map.get(dept.getId().toString()).getDictRank();
                                break;
                            }
                        }
                    }
                } else {
                    // 如果研究所的数据字典没有设置-不影响基础功能
                    for (AutDepartment dept : deptList) {
                        if (userDept.getDeptCode().startsWith(dept.getDeptCode())
                            || userDept.getDeptCode().equals(dept.getDeptCode())) {
                            nextStept = map.get(dept.getId().toString()).getDictRank();
                            break;
                        }
                    }
                }
            }
        }
        return nextStept;
    }
}
