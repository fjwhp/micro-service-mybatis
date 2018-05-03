package aljoin.off.service;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.Expression;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActActivitiService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.iservice.AutDataStatisticsService;
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
import aljoin.off.dao.entity.OffDailylog;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.dao.entity.OffMonthReportDetail;
import aljoin.off.dao.mapper.OffMonthReportMapper;
import aljoin.off.dao.object.AppOffMonthReportDO;
import aljoin.off.dao.object.AppOffMonthReportDetail;
import aljoin.off.dao.object.AppOffMonthReportDetailDO;
import aljoin.off.dao.object.AppOffMonthReportVO;
import aljoin.off.dao.object.OffDailylogVO;
import aljoin.off.dao.object.OffMonthReportCountDO;
import aljoin.off.dao.object.OffMonthReportDO;
import aljoin.off.dao.object.OffMonthReportDetailVO;
import aljoin.off.dao.object.OffMonthReportVO;
import aljoin.off.iservice.OffDailylogService;
import aljoin.off.iservice.OffMonthReportDetailService;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.ExcelUtil;

/**
 *
 * 工作月报表(服务实现类).
 *
 * @author：wangj
 *
 *               @date： 2017-10-11
 */

@Service
public class OffMonthReportServiceImpl extends ServiceImpl<OffMonthReportMapper, OffMonthReport>
    implements OffMonthReportService {

    @Resource
    private OffMonthReportMapper mapper;
    @Resource
    private AutUserService autUserService;
    @Resource
    private OffMonthReportDetailService offMonthReportDetailService;
    @Resource
    private ResResourceService resResourceService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private OffDailylogService offDailylogService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private TaskService taskService;
    @Resource
    private ActActivitiService actActivitiService;
    @Resource
    private ActFixedFormServiceImpl actFixedFormService;
    @Resource
    private HistoryService historyService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private ActAljoinQueryHisService actAljoinQueryHisService;
    @Resource
    private ActAljoinQueryService actAljoinQueryService;
    @Resource
    private AutDataStatisticsService autDataStatisticsService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private IdentityService identityService;

    @Override
    public Page<OffMonthReportDO> list(PageBean pageBean, OffMonthReport obj) throws Exception {
        Where<OffMonthReport> where = new Where<OffMonthReport>();
        where.setSqlSelect(
            "id,create_user_id,create_user_name,title,audit_status,month,status,submit_id,belong_dept,submit_time");
        if (StringUtils.isNotEmpty(obj.getTitle())) {
            where.like("title", obj.getTitle());
        }
        if (null != obj.getStatus()) {
            if (obj.getStatus() == 0) {// 未提交
                where.eq("status", 0);
                where.eq("audit_status", 0);
            } else if (obj.getStatus() == 1) {// 审核中
                where.eq("status", 1);
                where.eq("audit_status", 1);
            } else if (obj.getStatus() == 2) {// 审核成功
                where.eq("status", 1);
                where.eq("audit_status", 3);
            } else if (obj.getStatus() == 3) {// 审核失败
                where.eq("status", 0);
                where.eq("audit_status", 2);
            }
        }
        if (StringUtils.isNotEmpty(obj.getMonth())) {
            where.eq("month", obj.getMonth());
        }
        where.eq("create_user_id", obj.getCreateUserId());
        where.orderBy("month", false);
        Page<OffMonthReportDO> page = new Page<OffMonthReportDO>();
        Page<OffMonthReport> oldPage =
            selectPage(new Page<OffMonthReport>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<OffMonthReport> offMonthReportList = oldPage.getRecords();
        List<OffMonthReportDO> reportDOList = new ArrayList<OffMonthReportDO>();
        List<Long> userIdList = new ArrayList<Long>();
        if (null != offMonthReportList && !offMonthReportList.isEmpty()) {
            int i = 0;
            String status = "";
            for (OffMonthReport monthReport : offMonthReportList) {
                if (null != monthReport) {
                    OffMonthReportDO offMonthReportDO = new OffMonthReportDO();
                    offMonthReportDO.setId(monthReport.getId());
                    offMonthReportDO.setNo(i);
                    offMonthReportDO.setTitle(monthReport.getTitle());
                    offMonthReportDO.setMonth(monthReport.getMonth());
                    offMonthReportDO.setCreateUserId(monthReport.getCreateUserId());
                    if (null != monthReport.getSubmitTime()) {
                        String dateTime = DateUtil.date2str(monthReport.getSubmitTime());
                        offMonthReportDO.setSubmitDate(dateTime);
                    }
                    userIdList.add(monthReport.getCreateUserId());
                    if (monthReport.getStatus() == 0) {
                        if (monthReport.getAuditStatus() == 0) {
                            status = "未提交";
                        } else {
                            status = "审核失败";
                        }
                    } else {
                        if (monthReport.getAuditStatus() == 1) {
                            status = "审核中";
                        } else if (monthReport.getAuditStatus() == 3) {
                            status = "审核通过";
                        }
                    }
                    offMonthReportDO.setStatus(status);
                    status = "";
                    reportDOList.add(offMonthReportDO);
                }
                i++;
            }
        }
        if (null != userIdList && !userIdList.isEmpty()) {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,full_name");
            userWhere.eq("is_active", 1);
            if (userIdList.size() > 1) {
                userWhere.in("id", userIdList);
            } else {
                userWhere.eq("id", userIdList.get(0));
            }
            List<AutUser> userList = autUserService.selectList(userWhere);
            if (null != userList && !userList.isEmpty()) {
                for (OffMonthReportDO offMonthReportDO : reportDOList) {
                    for (AutUser user : userList) {
                        if (null != user && null != offMonthReportDO) {
                            if (user.getId().equals(offMonthReportDO.getCreateUserId())
                                && user.getId().intValue() == offMonthReportDO.getCreateUserId().intValue()) {
                                offMonthReportDO.setCreateFullName(user.getFullName());
                            }
                        }
                    }
                }
            }

        }
        page.setCurrent(oldPage.getCurrent());
        page.setRecords(reportDOList);
        page.setTotal(oldPage.getTotal());
        page.setSize(oldPage.getSize());
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(OffMonthReport obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public OffMonthReportVO detail(OffMonthReport obj) throws Exception {
        OffMonthReportVO offMonthReportVO = null;
        if (null != obj && null != obj.getId()) {
            offMonthReportVO = new OffMonthReportVO();

            // 月报主体
            OffMonthReport offMonthReport = selectById(obj.getId());
            String days = DateUtil.getFirstDateOfMonth(offMonthReport.getMonth(), "yyyy-MM-dd");
            List<String> monthDate = DateUtil.getAllTheDateOftheMonthStr(DateUtil.str2date(days));
            BeanUtils.copyProperties(offMonthReport, offMonthReportVO);
            AutUser user = autUserService.selectById(offMonthReport.getCreateUserId());
            offMonthReportVO.setFullName(user.getFullName());

            // 月报附件
            Where<ResResource> resourceMRwhere = new Where<ResResource>();
            resourceMRwhere.eq("biz_id", obj.getId());
            List<ResResource> resourceMRList = resResourceService.selectList(resourceMRwhere);
            offMonthReportVO.setResResourceMRList(resourceMRList);

            // 月报-相关日志
            Where<OffMonthReportDetail> where = new Where<OffMonthReportDetail>();
            where.eq("month_report_id", obj.getId());
            where.orderBy("work_date", true);
            List<OffMonthReportDetail> monthReportDetailList = offMonthReportDetailService.selectList(where);
            List<OffMonthReportDetailVO> monthReportDetailVOList = new ArrayList<OffMonthReportDetailVO>();
            List<Long> dailylogIdList = new ArrayList<Long>();
            if (null != monthReportDetailList && !monthReportDetailList.isEmpty()) {
                Boolean flag = false;
                for (String day : monthDate) {
                    OffMonthReportDetailVO monthReportDetailVO = new OffMonthReportDetailVO();
                    OffMonthReportDetail oMReportDetail = new OffMonthReportDetail();
                    oMReportDetail.setWorkDate(day);
                    oMReportDetail.setContent("");
                    for (OffMonthReportDetail monthReportDetail : monthReportDetailList) {
                        if (null != monthReportDetail) {
                            if (day.equals(monthReportDetail.getWorkDate())) {
                                monthReportDetailVO.setOffMonthReportDetail(monthReportDetail);
                                monthReportDetailVOList.add(monthReportDetailVO);
                                dailylogIdList.add(monthReportDetail.getDailylogId());
                                flag = true;
                                break;
                            } else {
                                flag = false;
                            }

                        }
                    }
                    if (!flag) {
                        monthReportDetailVO.setOffMonthReportDetail(oMReportDetail);
                        monthReportDetailVOList.add(monthReportDetailVO);
                    }
                }
                offMonthReportVO.setMonthReportDetailList(monthReportDetailVOList);
            }

            // 月报日志附件
            if (obj.getComment() != "月报领导查看") {
                if (null != dailylogIdList && !dailylogIdList.isEmpty()) {
                    Where<ResResource> resourceWhere = new Where<ResResource>();
                    resourceWhere.in("biz_id", dailylogIdList);
                    List<ResResource> allResourceList = resResourceService.selectList(resourceWhere);
                    if (null != allResourceList && !allResourceList.isEmpty()) {
                        for (OffMonthReportDetailVO monthReportDetailVO : monthReportDetailVOList) {
                            List<ResResource> resourceList = new ArrayList<ResResource>();
                            if (null != monthReportDetailVO.getOffMonthReportDetail().getId()) {
                                for (ResResource resResource : allResourceList) {
                                    if (null != monthReportDetailVO && null != resResource) {
                                        OffMonthReportDetail reportDetail =
                                            monthReportDetailVO.getOffMonthReportDetail();
                                        if (null != reportDetail) {
                                            if (reportDetail.getDailylogId().equals(resResource.getBizId())
                                                && reportDetail.getDailylogId().intValue() == resResource.getBizId()
                                                    .intValue()) {
                                                resourceList.add(resResource);
                                                monthReportDetailVO.setResResourceList(resourceList);
                                            }
                                            // attachmentList.clear();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return offMonthReportVO;
    }

    @Override
    public Page<OffMonthReportDO> recevieList(PageBean pageBean, OffMonthReportVO obj) throws Exception {
        Where<OffMonthReport> where = new Where<OffMonthReport>();
        Page<OffMonthReportDO> page = new Page<OffMonthReportDO>();
        where.setSqlSelect("id,create_user_id,create_user_name,title,month,status,submit_id,belong_dept,submit_time");
        if (null != obj && null != obj.getCreateUserId()) {
            if (StringUtils.isNotEmpty(obj.getTitle())) {
                where.like("title", obj.getTitle());
            }
            if (StringUtils.isNotEmpty(obj.getMonth())) {
                where.eq("month", obj.getMonth());
            }
            if (StringUtils.isNotEmpty(obj.getBegTime()) && StringUtils.isEmpty(obj.getEndTime())) {
                where.ge("submit_time", DateUtil.str2dateOrTime(obj.getBegTime() + " 00:00:00"));
            }
            if (StringUtils.isEmpty(obj.getBegTime()) && StringUtils.isNotEmpty(obj.getEndTime())) {
                where.le("submit_time", DateUtil.str2datetime(obj.getEndTime() + " 23:59:59"));
            }
            if (StringUtils.isNotEmpty(obj.getBegTime()) && StringUtils.isNotEmpty(obj.getEndTime())) {
                where.between("submit_time", DateUtil.str2datetime(obj.getBegTime() + " 00:00:00"),
                    DateUtil.str2datetime(obj.getEndTime() + " 23:59:59"));
            }
            where.eq("submit_id", obj.getCreateUserId());
            where.eq("status", 1);
            where.eq("audit_status", 3);
            where.orderBy("create_time", false);

            Page<OffMonthReport> oldPage =
                selectPage(new Page<OffMonthReport>(pageBean.getPageNum(), pageBean.getPageSize()), where);
            List<OffMonthReport> offMonthReportList = oldPage.getRecords();
            List<OffMonthReportDO> reportDOList = new ArrayList<OffMonthReportDO>();
            List<Long> userIdList = new ArrayList<Long>();
            if (null != offMonthReportList && !offMonthReportList.isEmpty()) {
                int i = 0;
                String status = "";
                for (OffMonthReport monthReport : offMonthReportList) {
                    if (null != monthReport) {
                        OffMonthReportDO offMonthReportDO = new OffMonthReportDO();
                        offMonthReportDO.setId(monthReport.getId());
                        offMonthReportDO.setNo(i);
                        offMonthReportDO.setTitle(monthReport.getTitle());
                        offMonthReportDO.setMonth(monthReport.getMonth());
                        offMonthReportDO.setCreateUserId(monthReport.getCreateUserId());
                        offMonthReportDO.setDeptName(monthReport.getBelongDept());
                        userIdList.add(monthReport.getCreateUserId());
                        if (null != monthReport.getSubmitTime()) {
                            String dateTime = DateUtil.datetime2str(monthReport.getSubmitTime());
                            offMonthReportDO.setSubmitDate(dateTime);
                        } else {
                            offMonthReportDO.setSubmitDate("");
                        }
                        if (monthReport.getStatus() == 0) {
                            status = "未提交";
                        } else if (monthReport.getStatus() == 1) {
                            status = "已提交";
                        } else {
                            status = "未提交";
                        }
                        offMonthReportDO.setStatus(status);
                        status = "";
                        reportDOList.add(offMonthReportDO);
                    }
                    i++;
                }
            }
            if (null != userIdList && !userIdList.isEmpty()) {
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.eq("is_active", 1);
                userWhere.in("id", userIdList);
                List<AutUser> userList = autUserService.selectList(userWhere);
                if (null != userList && !userList.isEmpty()) {
                    for (OffMonthReportDO offMonthReportDO : reportDOList) {
                        for (AutUser user : userList) {
                            if (null != user && null != offMonthReportDO) {
                                if (user.getId().equals(offMonthReportDO.getCreateUserId())
                                    && user.getId().intValue() == offMonthReportDO.getCreateUserId().intValue()) {
                                    offMonthReportDO.setCreateFullName(user.getFullName());
                                }
                            }
                        }
                    }
                }

            }
            page.setCurrent(oldPage.getCurrent());
            page.setRecords(reportDOList);
            page.setTotal(oldPage.getTotal());
            page.setSize(oldPage.getSize());

        }
        return page;
    }

    @Override
    public Page<OffMonthReportDO> managerList(PageBean pageBean, OffMonthReport obj) throws Exception {
        Where<OffMonthReport> where = new Where<OffMonthReport>();
        where.setSqlSelect("id,create_user_id,create_user_name,title,month,status,submit_id,belong_dept,submit_time");
        if (StringUtils.isNotEmpty(obj.getTitle())) {
            where.like("title", obj.getTitle());
        }
        if (StringUtils.isNotEmpty(obj.getMonth())) {
            where.eq("month", obj.getMonth());
        }

        int dept = 0;
        // if (StringUtils.isNotEmpty(obj.getBelongDeptId())) {
        // where.like("belong_dept_id",obj.getBelongDeptId());
        // dept = 1;
        // String deptIdStr = obj.getBelongDeptId();
        // Long deptId = new Long(0);
        // if (obj.getBelongDeptId().endsWith(";")) {
        // deptId = Long.valueOf(deptIdStr.substring(0,
        // deptIdStr.indexOf(";")));
        // } else {
        // deptId = Long.valueOf(deptIdStr);
        // }
        // List<Long> deptUserIdList = new ArrayList<Long>();// 领导所在部门所有成员
        // AutDepartment autDepartmentUser = new AutDepartment();
        // autDepartmentUser.setId(deptId);
        // List<AutUser> au =
        // autDepartmentService.getChildDeptUserList(autDepartmentUser);
        // for (AutUser autuser : au) {
        // deptUserIdList.add(autuser.getId());
        // }
        // if (deptUserIdList != null && !deptUserIdList.isEmpty()) {
        // where.in("create_user_id", deptUserIdList);
        // } else {
        // dept = 2;
        // }
        // }
        String deptIdStr = obj.getBelongDeptId();
        Long deptId = new Long(0);
        if (StringUtils.isNotEmpty(deptIdStr)) {
            if (obj.getBelongDeptId().endsWith(";")) {
                deptId = Long.valueOf(deptIdStr.substring(0, deptIdStr.indexOf(";")));
            } else {
                deptId = Long.valueOf(deptIdStr);
            }
        }
        AutDepartment autDepartmentUser = new AutDepartment();
        autDepartmentUser.setId(deptId);
        List<AutUser> au = autDepartmentService.getChildDeptUserList(autDepartmentUser);
        List<Long> deptUserIdList = new ArrayList<Long>();
        for (AutUser autuser : au) {
            deptUserIdList.add(autuser.getId());
        }
        if (deptUserIdList != null && !deptUserIdList.isEmpty()) {
            where.in("create_user_id", deptUserIdList);
        } else {
            dept = 2;
        }
        where.eq("status", 1);
        where.eq("audit_status", 3);
        where.orderBy("create_time", false);
        Page<OffMonthReportDO> page = new Page<OffMonthReportDO>();
        Page<OffMonthReport> oldPage = new Page<OffMonthReport>();
        if (dept == 2) {
            return page;
        } else {
            oldPage = selectPage(new Page<OffMonthReport>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        }

        List<OffMonthReport> offMonthReportList = oldPage.getRecords();
        List<OffMonthReportDO> reportDOList = new ArrayList<OffMonthReportDO>();
        List<Long> userIdList = new ArrayList<Long>();
        if (null != offMonthReportList && !offMonthReportList.isEmpty()) {
            int i = 0;
            String status = "";
            for (OffMonthReport monthReport : offMonthReportList) {
                if (null != monthReport) {
                    OffMonthReportDO offMonthReportDO = new OffMonthReportDO();
                    offMonthReportDO.setId(monthReport.getId());
                    offMonthReportDO.setNo(i);
                    offMonthReportDO.setTitle(monthReport.getTitle());
                    offMonthReportDO.setMonth(monthReport.getMonth());
                    offMonthReportDO.setCreateUserId(monthReport.getCreateUserId());
                    offMonthReportDO.setDeptName(monthReport.getBelongDept());
                    if (null != monthReport.getSubmitTime()) {
                        String dateTime = DateUtil.date2str(monthReport.getSubmitTime());
                        offMonthReportDO.setSubmitDate(dateTime);
                    }
                    userIdList.add(monthReport.getCreateUserId());
                    if (monthReport.getStatus() == 0) {
                        status = "未提交";
                    } else if (monthReport.getStatus() == 1) {
                        status = "已提交";
                    } else {
                        status = "未提交";
                    }
                    offMonthReportDO.setStatus(status);
                    status = "";
                    reportDOList.add(offMonthReportDO);
                }
                i++;
            }
        }
        if (null != userIdList && !userIdList.isEmpty()) {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.in("id", userIdList);
            List<AutUser> userList = autUserService.selectList(userWhere);
            if (null != userList && !userList.isEmpty()) {
                for (OffMonthReportDO offMonthReportDO : reportDOList) {
                    for (AutUser user : userList) {
                        if (null != user && null != offMonthReportDO) {
                            if (user.getId().equals(offMonthReportDO.getCreateUserId())
                                && user.getId().intValue() == offMonthReportDO.getCreateUserId().intValue()) {
                                offMonthReportDO.setCreateFullName(user.getFullName());
                            }
                        }
                    }
                }
            }

        }
        page.setRecords(reportDOList);
        page.setTotal(oldPage.getTotal());
        page.setSize(oldPage.getSize());
        return page;
    }

    @Override
    public OffMonthReportVO submitDetail(OffMonthReport obj) throws Exception {
        OffMonthReportVO offMonthReportVO = new OffMonthReportVO();
        Where<OffMonthReport> reportWhere = new Where<OffMonthReport>();
        // reportWhere.eq("status",1);
        reportWhere.eq("id", obj.getId());
        List<OffMonthReport> reportList = selectList(reportWhere);
        List<Long> reportIdList = new ArrayList<Long>();
        if (null != reportList && !reportList.isEmpty()) {
            for (OffMonthReport report : reportList) {
                if (null != report && null != report.getId()) {
                    reportIdList.add(report.getId());
                    // offMonthReportVO.setTitle(report.getTitle());
                    BeanUtils.copyProperties(report, offMonthReportVO);
                }
            }
        }
        if (null != reportIdList && !reportIdList.isEmpty()) {
            Where<OffMonthReportDetail> where = new Where<OffMonthReportDetail>();
            where.in("month_report_id", reportIdList);
            where.orderBy("work_date", true);
            List<OffMonthReportDetail> monthReportDetailList = offMonthReportDetailService.selectList(where);
            List<OffMonthReportDetailVO> monthReportDetailVOList = new ArrayList<OffMonthReportDetailVO>();
            List<Long> dailylogIdList = new ArrayList<Long>();
            if (null != monthReportDetailList && !monthReportDetailList.isEmpty()) {
                for (OffMonthReportDetail monthReportDetail : monthReportDetailList) {
                    if (null != monthReportDetail) {
                        OffMonthReportDetailVO monthReportDetailVO = new OffMonthReportDetailVO();
                        monthReportDetailVO.setOffMonthReportDetail(monthReportDetail);
                        monthReportDetailVOList.add(monthReportDetailVO);
                        dailylogIdList.add(monthReportDetail.getDailylogId());
                    }
                }
                offMonthReportVO.setMonthReportDetailList(monthReportDetailVOList);
            }

            if (null != dailylogIdList && !dailylogIdList.isEmpty()) {
                Where<ResResource> resourceWhere = new Where<ResResource>();
                resourceWhere.in("biz_id", dailylogIdList);
                List<ResResource> allResourceList = resResourceService.selectList(resourceWhere);
                if (null != allResourceList && !allResourceList.isEmpty()) {
                    List<ResResource> resourceList = new ArrayList<ResResource>();
                    for (OffMonthReportDetailVO monthReportDetailVO : monthReportDetailVOList) {
                        for (ResResource resResource : allResourceList) {
                            if (null != monthReportDetailVO && null != resResource) {
                                OffMonthReportDetail reportDetail = monthReportDetailVO.getOffMonthReportDetail();
                                if (null != reportDetail) {
                                    if (reportDetail.getDailylogId().equals(resResource.getBizId()) && reportDetail
                                        .getDailylogId().intValue() == resResource.getBizId().intValue()) {
                                        resourceList.add(resResource);
                                        monthReportDetailVO.setResResourceList(resourceList);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // 月报附件
        Where<ResResource> resourceMRwhere = new Where<ResResource>();
        resourceMRwhere.eq("biz_id", obj.getId());
        List<ResResource> resourceMRList = resResourceService.selectList(resourceMRwhere);
        offMonthReportVO.setResResourceMRList(resourceMRList);

        return offMonthReportVO;
    }

    @Override
    public List<OffMonthReportCountDO> reportCountList(OffMonthReportVO obj) throws Exception {
        List<OffMonthReportCountDO> reportList = new ArrayList<OffMonthReportCountDO>();
        if (null != obj) {
            Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
            departmentWhere.eq("id", obj.getDeptId());
            departmentWhere.setSqlSelect("id,dept_name");
            AutDepartment department = autDepartmentService.selectOne(departmentWhere);
            String deptName = "";
            if (null != department && StringUtils.isNotEmpty(department.getDeptName())) {
                deptName = department.getDeptName();
            }
            // 根据部门id找到该部门的用户
            Where<AutDepartmentUser> deptWhere = new Where<AutDepartmentUser>();
            deptWhere.setSqlSelect("id,user_id");
            deptWhere.eq("dept_id", obj.getDeptId());
            List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(deptWhere);

            // 获取需要查询部门部门的userIdList
            List<Long> userIdList = new ArrayList<Long>();
            for (AutDepartmentUser autDepartmentUser : deptUserList) {
                userIdList.add(autDepartmentUser.getUserId());
            }

            // 部门有用户才进行查询
            if (userIdList != null && !userIdList.isEmpty()) {
                // 查询用户表，以获取每个userId对应的userName
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                if (StringUtils.isNotEmpty(obj.getFullName())) {
                    userWhere.like("full_name", obj.getFullName());
                }
                userWhere.in("id", userIdList);
                List<AutUser> autUserList = autUserService.selectList(userWhere);

                Where<OffMonthReport> where = new Where<OffMonthReport>();
                where.setSqlSelect("id,submit_id");
                // 默认查询30天前的签到
                if (StringUtils.isEmpty(obj.getThisWeek()) && StringUtils.isEmpty(obj.getThisMonth())
                    && StringUtils.isEmpty(obj.getBegTime()) && StringUtils.isEmpty(obj.getEndTime())) {
                    Date dateBeforeThirty = DateUtil.getBefore(new Date(), "day", 30);
                    where.ge("create_time", dateBeforeThirty);
                    // 查询本周签到
                } else if (StringUtils.isNotEmpty(obj.getThisWeek())) {
                    String firstDateOfWeek = DateUtil.getFirstDateOfWeek("yyyy-MM-dd");
                    String lastDateOfWeek = DateUtil.getLastDateOfWeek("yyyy-MM-dd");
                    where.between("create_time", DateUtil.str2dateOrTime(firstDateOfWeek),
                        DateUtil.str2dateOrTime(lastDateOfWeek));
                } else if (StringUtils.isNotEmpty(obj.getThisMonth())) {
                    // 查询本月签到
                    String firstDateOfMonth = DateUtil.getFirstDateOfMonth("yyyy-MM-dd");
                    String lastDateOfMonth = DateUtil.getLastDateOfMonth("yyyy-MM-dd");
                    where.between("create_time", DateUtil.str2dateOrTime(firstDateOfMonth),
                        DateUtil.str2dateOrTime(lastDateOfMonth));
                }
                if (StringUtils.isNotEmpty(obj.getBegTime()) && StringUtils.isEmpty(obj.getEndTime())) {
                    where.ge("create_time", DateUtil.str2dateOrTime(obj.getBegTime()));
                }
                if (StringUtils.isEmpty(obj.getBegTime()) && StringUtils.isNotEmpty(obj.getEndTime())) {
                    where.le("create_time", DateUtil.str2dateOrTime(obj.getEndTime()));
                }
                if (StringUtils.isNotEmpty(obj.getBegTime()) && StringUtils.isNotEmpty(obj.getEndTime())) {
                    where.between("create_time", DateUtil.str2datetime(obj.getBegTime()),
                        DateUtil.str2datetime(obj.getEndTime()));
                }
                List<OffMonthReport> offMonthReportList = selectList(where);
                if (null != offMonthReportList && !offMonthReportList.isEmpty()) {
                    int index = 1;
                    OffMonthReportCountDO monthReportCountDO = new OffMonthReportCountDO();
                    for (AutUser user : autUserList) {
                        int unSubmitCount = 0;
                        int submitCount = 0;
                        for (OffMonthReport report : offMonthReportList) {
                            if (null != report) {
                                // 比对是否同1用户
                                if (user.getId().equals(report.getSubmitId())
                                    && user.getId().intValue() == report.getSubmitId().intValue()) {
                                    monthReportCountDO.setNo(index);
                                    monthReportCountDO.setDeptName(deptName);
                                    monthReportCountDO.setFullName(user.getFullName());
                                    monthReportCountDO.setOffice("");
                                    if (report.getStatus() == 0) {
                                        unSubmitCount++;
                                    } else if (report.getStatus() == 1) {
                                        submitCount++;
                                    }
                                    monthReportCountDO.setUserId(user.getId());
                                    monthReportCountDO.setUnSubmitNumber(unSubmitCount);
                                    monthReportCountDO.setSubmitNumber(submitCount);
                                }
                            }
                        }
                    }
                    index++;
                    if (null != monthReportCountDO.getUserId()) {
                        reportList.add(monthReportCountDO);
                    }
                    sortList(reportList, obj);
                }
            }
        }
        return reportList;
    }

    @Override
    public void export(HttpServletResponse response, OffMonthReportVO obj) throws Exception {
        List<OffMonthReportCountDO> reportCountList = reportCountList(obj);
        if (null != reportCountList && !reportCountList.isEmpty()) {
            String[][] headers = {{"No", "序号"}, {"FullName", "用户名"}, {"DeptName", "所属机构"}, {"SendNnumber", "发件数量"},
                {"ReceviceNumber", "收件人总数"}, {"TotalSize", "总附件大小(MB)"}};
            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (OffMonthReportCountDO entity : reportCountList) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    OffMonthReportCountDO schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = OffMonthReportCountDO.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("工作月报统计", headers, dataset, response);
        }
    }

    public void sortList(List<OffMonthReportCountDO> list, OffMonthReportVO obj) {
        if (null != obj) {
            String unSubmitSort = obj.getUnSubmitSort();
            String submitSort = obj.getSubmitSort();
            // 未提交降序
            if (StringUtils.isNotEmpty(unSubmitSort)) {
                if (unSubmitSort.equals("1")) {
                    Comparator<OffMonthReportCountDO> offMonthReportCountDOComparator =
                        new Comparator<OffMonthReportCountDO>() {
                            @Override
                            public int compare(OffMonthReportCountDO o1, OffMonthReportCountDO o2) {
                                if (o1.getUnSubmitNumber() != o2.getUnSubmitNumber()) {
                                    return o1.getUnSubmitNumber() - o2.getUnSubmitNumber();
                                } else if (o1.getUnSubmitNumber() != o2.getUnSubmitNumber()) {
                                    return o1.getUnSubmitNumber() - o2.getUnSubmitNumber();
                                } else if (o1.getUnSubmitNumber() != o2.getUnSubmitNumber()) {
                                    return o1.getUnSubmitNumber() - o2.getUnSubmitNumber();
                                }
                                return 0;
                            }
                        };
                    Collections.sort(list, offMonthReportCountDOComparator);
                } else {
                    // 未提交升序
                    Comparator<OffMonthReportCountDO> offMonthReportCountDOComparator =
                        new Comparator<OffMonthReportCountDO>() {
                            @Override
                            public int compare(OffMonthReportCountDO o1, OffMonthReportCountDO o2) {
                                if (o1.getUnSubmitNumber() != o2.getUnSubmitNumber()) {
                                    return o2.getUnSubmitNumber() - o1.getUnSubmitNumber();
                                } else if (o1.getUnSubmitNumber() != o2.getUnSubmitNumber()) {
                                    return o2.getUnSubmitNumber() - o1.getUnSubmitNumber();
                                } else if (o1.getUnSubmitNumber() != o2.getUnSubmitNumber()) {
                                    return o2.getUnSubmitNumber() - o1.getUnSubmitNumber();
                                }
                                return 0;
                            }
                        };
                    Collections.sort(list, offMonthReportCountDOComparator);
                }
                // 总附件大小降序
            } else if (StringUtils.isNotEmpty(submitSort)) {
                if ("1".equals(submitSort)) {
                    Comparator<OffMonthReportCountDO> offMonthReportCountDOComparator =
                        new Comparator<OffMonthReportCountDO>() {
                            @Override
                            public int compare(OffMonthReportCountDO o1, OffMonthReportCountDO o2) {
                                if (o1.getSubmitNumber() != o2.getSubmitNumber()) {
                                    return o1.getSubmitNumber() - o2.getSubmitNumber();
                                } else if (o1.getSubmitNumber() != o2.getSubmitNumber()) {
                                    return o1.getSubmitNumber() - o2.getSubmitNumber();
                                } else if (o1.getSubmitNumber() != o2.getSubmitNumber()) {
                                    return o1.getSubmitNumber() - o2.getSubmitNumber();
                                }
                                return 0;
                            }
                        };
                    Collections.sort(list, offMonthReportCountDOComparator);
                } else {
                    // 总附件大小升序
                    Comparator<OffMonthReportCountDO> offMonthReportCountDOComparator =
                        new Comparator<OffMonthReportCountDO>() {
                            @Override
                            public int compare(OffMonthReportCountDO o1, OffMonthReportCountDO o2) {
                                if (o1.getSubmitNumber() != o2.getSubmitNumber()) {
                                    return o2.getSubmitNumber() - o1.getSubmitNumber();
                                } else if (o1.getSubmitNumber() != o2.getSubmitNumber()) {
                                    return o2.getSubmitNumber() - o1.getSubmitNumber();
                                } else if (o1.getSubmitNumber() != o2.getSubmitNumber()) {
                                    return o2.getSubmitNumber() - o1.getSubmitNumber();
                                }
                                return 0;
                            }
                        };
                    Collections.sort(list, offMonthReportCountDOComparator);
                }
            }
        }
    }

    @Override
    @Transactional
    public RetMsg updateMonthReport(OffMonthReportVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (null != obj.getMonthReportDetailList()) { // 日志列表
                List<OffMonthReportDetailVO> monthReportDetailList = obj.getMonthReportDetailList();
                for (OffMonthReportDetailVO offMonthReportDetail : monthReportDetailList) {

                    OffMonthReportDetailVO offMRDetailVo = offMonthReportDetail;
                    OffMonthReportDetail offMRDetail = offMRDetailVo.getOffMonthReportDetail();

                    if (null != offMRDetail) {
                        if (null != offMRDetail.getId()) {// 更新
                            OffMonthReportDetail offMRDetailDB =
                                offMonthReportDetailService.selectById(offMRDetail.getId());
                            if (null != offMRDetailDB) {
                                if (null != offMRDetail.getContent()) {
                                    offMRDetailDB.setContent(offMRDetail.getContent());
                                    offMonthReportDetailService.updateById(offMRDetailDB);
                                    if (null != offMRDetail.getDailylogId()) {
                                        OffDailylog dailylog =
                                            offDailylogService.selectById(offMRDetail.getDailylogId());
                                        if (null != dailylog) {
                                            dailylog.setContent(offMRDetail.getContent());
                                            offDailylogService.updateById(dailylog);
                                        }
                                    }
                                }
                            }
                        } else {// 新增
                            OffDailylog offdailyLog = new OffDailylog();
                            if (offMRDetail.getContent() != null && StringUtils.isNotEmpty(offMRDetail.getContent())) {
                                offdailyLog.setContent(offMRDetail.getContent());
                                offdailyLog.setTitle(offMRDetail.getWorkDate() + "工作日志");
                                String workDateStr =
                                    offMRDetail.getWorkDate().substring(0, offMRDetail.getWorkDate().indexOf(" "));
                                offdailyLog.setWorkDate(DateUtil.str2date(workDateStr, "yyyy-MM-dd"));
                                offdailyLog.setCreateUserId(obj.getCreateUserId());
                                OffDailylogVO offdailyLogvo = new OffDailylogVO();
                                BeanUtils.copyProperties(offdailyLog, offdailyLogvo);
                                offDailylogService.add(offdailyLogvo);
                            }
                        }
                    }

                }
            }
            // 添加附件
            List<ResResource> newResourceList = obj.getResResourceMRList();
            List<Long> newResourceIds = new ArrayList<Long>();
            if (null != newResourceList && newResourceList.size() > 0) {
                for (ResResource resResource : newResourceList) {
                    newResourceIds.add(resResource.getId());
                }
            }
            if (null != newResourceIds && newResourceIds.size() > 0) {
                List<ResResource> addResource = resResourceService.selectBatchIds(newResourceIds);
                for (ResResource resResource : addResource) {
                    resResource.setBizId(obj.getId());
                    resResource.setFileDesc("工作月报附件上传");
                }
                resResourceService.updateBatchById(addResource);
            }

        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    // -----------------------------审批流程使用----------------------------------------
    @Override
    @Transactional
    public RetMsg addComment(String taskId, String message) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        taskService.addComment(taskId, processInstanceId, message);
        // taskService.addComment(taskId, processInstanceId, message);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public void showImg(HttpServletRequest request, HttpServletResponse response, String taskId) throws Exception {
        response.addHeader("Pragma", "No-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addDateHeader("expires", 0);
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        InputStream is = actActivitiService.getRuImageInputStream(processInstanceId, true);
        OutputStream os = response.getOutputStream();
        BufferedImage image = ImageIO.read(is);
        ImageIO.write(image, "PNG", os);
        os.flush();
        os.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public List<FixedFormProcessLog> getAllTaskInfo(String taskId, String processName, String processInstanceId)
        throws Exception {
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

    @Override
    public RetMsg getPreTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<HistoricTaskInstance> currentTask = activitiService.getCurrentNodeInfo(taskId);
        List<TaskDefinition> preList = new ArrayList<TaskDefinition>();;
        List<CustomerTaskDefinition> list = new ArrayList<CustomerTaskDefinition>();
        if (StringUtils.isNotEmpty(processInstanceId) && null != currentTask && !currentTask.isEmpty()) {
            String currentTaskKey = currentTask.get(0).getTaskDefinitionKey();
            if (StringUtils.isNotEmpty(currentTaskKey)) {
                preList = activitiService.getPreTaskInfo(currentTaskKey, processInstanceId);
                if (null != preList && !preList.isEmpty()) {
                    for (TaskDefinition definition : preList) {
                        CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
                        taskDefinition.setKey(StringUtils.isNotEmpty(definition.getKey()) ? definition.getKey() : "");
                        taskDefinition.setAssignee(null != definition.getAssigneeExpression()
                            ? String.valueOf(definition.getAssigneeExpression()) : "");
                        taskDefinition.setNextNodeName(null != definition.getNameExpression()
                            ? String.valueOf(definition.getNameExpression()) : "");
                        list.add(taskDefinition);
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setObject(list);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg isClaim(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        Date claimDate = historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId)
            .singleResult().getClaimTime();
        if (null != claimDate) {
            retMsg.setCode(0);
        } else {
            retMsg.setCode(1);
        }
        return retMsg;
    }

    @Override
    public RetMsg getNextTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
        List<CustomerTaskDefinition> taskDefinitionList = new ArrayList<CustomerTaskDefinition>();
        if (null != list && !list.isEmpty()) {
            for (TaskDefinition definition : list) {
                if (null != definition) {
                    CustomerTaskDefinition taskDefinition = new CustomerTaskDefinition();
                    taskDefinition.setKey(definition.getKey());
                    taskDefinition.setAssignee(String.valueOf(definition.getAssigneeExpression()));
                    taskDefinition.setNextNodeName(String.valueOf(definition.getNameExpression()));
                    taskDefinitionList.add(taskDefinition);
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setObject(taskDefinitionList);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg checkNextTaskInfo(String taskId) throws Exception {
        RetMsg retMsg = new RetMsg();
        String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
        List<TaskDefinition> list = activitiService.getNextTaskInfo(processInstanceId, false);
        boolean isOrgn = false;
        Map<String, Object> map = new HashMap<String, Object>();
        String openType = "";
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
                // 受理人
                if (null != definition.getAssigneeExpression()) {
                    assigneeId = String.valueOf(definition.getAssigneeExpression());
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
                    if (!assineedIdList.isEmpty()) {
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
                    uList.addAll(autUserService.selectList(where));
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

    @Override
    public List<OffMonthReportCountDO> returnReportCountList(OffMonthReportVO obj) throws Exception {
        List<OffMonthReportCountDO> reportList = new ArrayList<OffMonthReportCountDO>();
        if (null != obj) {
            // 部门，上级部门，已交，未交
            // Date startDate = null;
            // Date endDate = null;
            String qDeptName = "";
            String strdate = "";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (obj.getBegTime() != null && !"".equals(obj.getBegTime())) {
                strdate = obj.getBegTime().toString();
            } else {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.MONTH, -1);
                strdate = format.format(cal.getTime());
                String[] tmpDate = strdate.split("-");
                strdate = tmpDate[0] + "-" + tmpDate[1];
            }
            if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                qDeptName = obj.getFullName();
            }
            Where<AutDepartment> w1 = new Where<AutDepartment>();
            Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
            w1.setSqlSelect("id,dept_code,dept_name");
            if (obj.getDeptId() != null && !"".equals(obj.getDeptId().toString())) {
                AutDepartment autdept = autDepartmentService.selectById(obj.getDeptId());
                if (autdept != null) {
                    w1.where("dept_code like {0}", autdept.getDeptCode() + "%");
                    w2.where("dept_code like {0}", autdept.getDeptCode() + "%");
                }
            }
            List<AutDepartment> deptIdList = autDepartmentService.selectList(w1);
            Map<String, String> deptMap = new HashMap<String, String>();
            if (deptIdList != null && deptIdList.size() > 0) {
                for (AutDepartment autDepartment : deptIdList) {
                    deptMap.put(autDepartment.getId().toString(), autDepartment.getDeptName());
                }

            }
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("user_name", "sadmin");
            userWhere.setSqlSelect("id");
            AutUser adminUser = autUserService.selectOne(userWhere);
            List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w2);
            String userIds = "";
            Map<String, List<String>> deptUserMap = new HashMap<String, List<String>>();
            if (deptUserList != null && deptUserList.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : deptUserList) {
                    List<String> tmp = new ArrayList<String>();
                    if (userIds.indexOf(autDepartmentUser.getUserId().toString()) > -1) {
                    } else {
                        userIds += autDepartmentUser.getUserId().toString() + ",";
                    }
                    if (deptUserMap.containsKey(autDepartmentUser.getDeptId().toString())) {
                        tmp = deptUserMap.get(autDepartmentUser.getDeptId().toString());
                    }
                    tmp.add(autDepartmentUser.getUserId().toString());
                    deptUserMap.put(autDepartmentUser.getDeptId().toString(), tmp);
                }
            }
            Map<String, OffMonthReport> offMap = new HashMap<String, OffMonthReport>();
            if (userIds.length() > 0) {
                Where<OffMonthReport> offWhere = new Where<OffMonthReport>();
                offWhere.in("create_user_id", userIds);
                offWhere.eq("month", strdate);
                // offWhere.eq("status", 0);
                offWhere.setSqlSelect("id,submit_id,status,create_user_id");
                List<OffMonthReport> offlist = this.selectList(offWhere);
                if (offlist != null && offlist.size() > 0) {
                    for (OffMonthReport offMonthReport : offlist) {
                        offMap.put(offMonthReport.getCreateUserId().toString(), offMonthReport);
                    }
                }
            }
            if (deptIdList != null && deptIdList.size() > 0) {
                for (AutDepartment autDepartment : deptIdList) {
                    OffMonthReportCountDO off = new OffMonthReportCountDO();
                    List<String> tmp = new ArrayList<String>();
                    int isTrue = 0;
                    int isFalse = 0;
                    if (qDeptName != null && !"".equals(qDeptName)) {
                        String name = autDepartment.getDeptName();
                        if (name.indexOf(qDeptName) == -1) {
                            continue;
                        }
                    }
                    String pCode = autDepartment.getDeptCode();
                    for (AutDepartment autDepartments : deptIdList) {
                        String code = autDepartments.getDeptCode();
                        if (code.indexOf(pCode) == 0) {
                            if (deptUserMap.containsKey(autDepartments.getId().toString())) {
                                List<String> tmps = deptUserMap.get(autDepartments.getId().toString());
                                if (tmps != null && tmps.size() > 0) {
                                    tmp.addAll(tmps);
                                }
                            }
                        }
                    }

                    /*
                     * if (deptUserMap.containsKey(autDepartment.getId().toString() )) { List<String> tmps =
                     * deptUserMap.get(autDepartment.getId().toString()); if (tmps != null && tmps.size() > 0)
                     * { tmp.addAll(tmps); } }
                     */
                    if (tmp != null && tmp.size() > 0) {
                        String ids = "";// 去重复记录
                        if (adminUser != null && adminUser.getId() != null) {
                            ids += adminUser.getId() + ",";
                        }
                        for (String string : tmp) {
                            if (ids.indexOf(string) > -1) {
                                continue;
                            }
                            if (offMap.containsKey(string)) {
                                OffMonthReport of = offMap.get(string);
                                if (of.getStatus() == 0) {
                                    isFalse += 1;
                                } else {
                                    isTrue += 1;
                                }
                            } else {
                                isFalse += 1;
                            }
                        }
                    }

                    off.setDeptName(autDepartment.getDeptName());
                    off.setNo(1);
                    off.setSubmitNumber(isTrue);
                    off.setUnSubmitNumber(isFalse);
                    reportList.add(off);
                }
            }
        }
        return reportList;
    }

    public List<OffMonthReportCountDO> returnReport(String deptCode, Date stime, Date etime, String qDeptName)
        throws Exception {
        // 获取对应的月报LIST
        List<OffMonthReportCountDO> reportList = new ArrayList<OffMonthReportCountDO>();
        Where<AutDepartment> w1 = new Where<AutDepartment>();
        w1.setSqlSelect("id");
        if (!"".equals(qDeptName)) {
            w1.like("dept_name", qDeptName);
        }
        w1.where("dept_code like {0}", deptCode + "%");
        List<AutDepartment> deptIdList = autDepartmentService.selectList(w1);
        List<Long> list = new ArrayList<Long>();
        for (AutDepartment deptId : deptIdList) {
            list.add(deptId.getId());
        }
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                OffMonthReportCountDO off = new OffMonthReportCountDO();
                AutDepartment autdept = autDepartmentService.selectById(list.get(i));
                List<AutUser> userList = autDepartmentService.getChildDeptUserList(autdept);
                String userIds = "";
                if (userList != null && userList.size() > 0) {
                    for (AutUser autUser : userList) {
                        userIds += autUser.getId() + ",";
                    }
                }
                if ("".equals(userIds)) {
                    userIds = "99999999999999999999999";
                }
                off.setDeptName(autdept.getDeptName());
                Where<OffMonthReport> offWhere = new Where<OffMonthReport>();
                offWhere.in("create_user_id", userIds);
                offWhere.between("create_time", stime, etime);
                offWhere.eq("status", 0);
                offWhere.setSqlSelect("id");
                List<OffMonthReport> offlist = this.selectList(offWhere);
                if (offlist != null && offlist.size() > 0) {
                    off.setUnSubmitNumber(offlist.size());
                } else {
                    off.setUnSubmitNumber(0);
                }
                offWhere = new Where<OffMonthReport>();
                offWhere.in("create_user_id", userIds);
                offWhere.between("create_time", stime, etime);
                offWhere.eq("status", 1);
                offWhere.setSqlSelect("id");
                offlist = this.selectList(offWhere);
                if (offlist != null && offlist.size() > 0) {
                    off.setSubmitNumber(offlist.size());
                } else {
                    off.setSubmitNumber(0);
                }
                reportList.add(off);
            }
        }
        return reportList;
    }

    @Override
    public OffMonthReportCountDO returnReportCount() throws Exception {
        OffMonthReportCountDO offDo = new OffMonthReportCountDO();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        String strdate = format.format(cal.getTime());
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        String[] tmpDate = strdate.split("-");
        strdate = tmpDate[0] + "-" + tmpDate[1];
        Where<OffMonthReport> offWhere = new Where<OffMonthReport>();
        offWhere.eq("month", strdate);
        offWhere.setSqlSelect("id,status,create_user_id");
        List<OffMonthReport> offlist = this.selectList(offWhere);
        Map<String, Integer> offMap = new HashMap<String, Integer>();
        if (offlist != null && offlist.size() > 0) {
            for (OffMonthReport offMonthReport : offlist) {
                offMap.put(offMonthReport.getCreateUserId().toString(), offMonthReport.getStatus());
            }
        }
        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.eq("is_active", 1);
        userWhere.where("user_name <> {0}", "sadmin");
        userWhere.setSqlSelect("id");
        List<AutUser> list = autUserService.selectList(userWhere);
        int istrue = 0;
        int isfalse = 0;
        if (list != null && list.size() > 0) {
            for (AutUser autUser : list) {
                if (offMap.containsKey(autUser.getId().toString())) {
                    int tmp = offMap.get(autUser.getId().toString());
                    if (tmp == 0) {
                        isfalse += 1;
                    } else {
                        istrue += 1;
                    }
                } else {
                    isfalse += 1;
                }
            }
        }
        offDo.setShowDate(strdate + "-01至" + format.format(cal.getTime()));
        offDo.setUnSubmitNumber(isfalse);
        offDo.setSubmitNumber(istrue);
        return offDo;

    }

    @Override
    @Transactional
    public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isNotEmpty(taskId)) {
            String processInstanceId =
                taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
            runtimeService.deleteProcessInstance(processInstanceId, null);
            Where<ActAljoinQuery> queryWhere = new Where<ActAljoinQuery>();
            queryWhere.eq("process_instance_id", processInstanceId);
            actAljoinQueryService.delete(queryWhere);
            Where<ActAljoinQueryHis> queryHisWhere = new Where<ActAljoinQueryHis>();
            queryHisWhere.eq("process_instance_id", processInstanceId);
            actAljoinQueryHisService.delete(queryHisWhere);
            // 修改原文件状态
            Date date = new Date();
            if (org.apache.commons.lang.StringUtils.isNotEmpty(bizId)) {
                OffMonthReport offMonthReport = selectById(Long.parseLong(bizId));
                if (null != offMonthReport) {
                    offMonthReport.setStatus(0);// 审核失败-原文件可编辑
                    offMonthReport.setAuditStatus(2);// 审核不通过
                    offMonthReport.setAuditTime(date);
                    offMonthReport.setAuditReason("审核不通过");
                    updateById(offMonthReport);
                }
            }
            // 环节办理人待办数量-1
            AutDataStatistics aut = new AutDataStatistics();
            aut.setObjectKey(WebConstant.AUTDATA_TODOLIST_CODE);
            aut.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
            aut.setBusinessKey(String.valueOf(userId));
            autDataStatisticsService.minus(aut);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg offDetail(AppOffMonthReportVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        Integer claimStatus = 0;
        if (com.baomidou.mybatisplus.toolkit.StringUtils.isNotEmpty(obj.getProcessInstanceId())) {
            Task task = taskService.createTaskQuery().processInstanceId(obj.getProcessInstanceId()).singleResult();
            if (StringUtils.isNotEmpty(task.getAssignee())) {
                claimStatus = 1;
            }
        }
        AppOffMonthReportDO offMonthReportVO = new AppOffMonthReportDO();
        Where<OffMonthReport> reportWhere = new Where<OffMonthReport>();
        // reportWhere.eq("status",1);
        reportWhere.eq("id", obj.getId());
        List<OffMonthReport> reportList = selectList(reportWhere);
        List<Long> reportIdList = new ArrayList<Long>();
        if (null != reportList && !reportList.isEmpty()) {
            for (OffMonthReport report : reportList) {
                if (null != report && null != report.getId()) {
                    reportIdList.add(report.getId());
                    // offMonthReportVO.setTitle(report.getTitle());
                    BeanUtils.copyProperties(report, offMonthReportVO);
                }
            }
        }
        offMonthReportVO.setClaimStatus(claimStatus);
        if (null != reportIdList && !reportIdList.isEmpty()) {
            Where<OffMonthReportDetail> where = new Where<OffMonthReportDetail>();
            where.in("month_report_id", reportIdList);
            where.orderBy("work_date", true);
            List<OffMonthReportDetail> monthReportDetailList = offMonthReportDetailService.selectList(where);
            List<AppOffMonthReportDetailDO> monthReportDetailVOList = new ArrayList<AppOffMonthReportDetailDO>();
            List<Long> dailylogIdList = new ArrayList<Long>();
            if (null != monthReportDetailList && !monthReportDetailList.isEmpty()) {
                for (OffMonthReportDetail monthReportDetail : monthReportDetailList) {
                    if (null != monthReportDetail) {
                        AppOffMonthReportDetail offMonthReportDetail = new AppOffMonthReportDetail();
                        BeanUtils.copyProperties(monthReportDetail, offMonthReportDetail);
                        AppOffMonthReportDetailDO monthReportDetailVO = new AppOffMonthReportDetailDO();
                        monthReportDetailVO.setOffMonthReportDetail(offMonthReportDetail);
                        monthReportDetailVOList.add(monthReportDetailVO);
                        dailylogIdList.add(monthReportDetail.getDailylogId());
                    }
                }
                offMonthReportVO.setMonthReportDetailList(monthReportDetailVOList);
            }

            if (null != dailylogIdList && !dailylogIdList.isEmpty()) {
                Where<ResResource> resourceWhere = new Where<ResResource>();
                resourceWhere.in("biz_id", dailylogIdList);
                List<ResResource> allResourceList = resResourceService.selectList(resourceWhere);
                if (null != allResourceList && !allResourceList.isEmpty()) {
                    List<ResResource> resourceList = new ArrayList<ResResource>();
                    for (AppOffMonthReportDetailDO monthReportDetailVO : monthReportDetailVOList) {
                        resourceList = new ArrayList<ResResource>();
                        for (ResResource resResource : allResourceList) {
                            if (null != monthReportDetailVO && null != resResource) {
                                AppOffMonthReportDetail reportDetail = monthReportDetailVO.getOffMonthReportDetail();
                                if (null != reportDetail) {
                                    if (reportDetail.getDailylogId().equals(resResource.getBizId()) && reportDetail
                                        .getDailylogId().intValue() == resResource.getBizId().intValue()) {
                                        resourceList.add(resResource);
                                        monthReportDetailVO.setResResourceList(resourceList);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // 月报附件
        Where<ResResource> resourceMRwhere = new Where<ResResource>();
        resourceMRwhere.eq("biz_id", obj.getId());
        List<ResResource> resourceMRList = resResourceService.selectList(resourceMRwhere);
        offMonthReportVO.setResResourceMRList(resourceMRList);
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        retMsg.setObject(offMonthReportVO);
        return retMsg;
    }
}
