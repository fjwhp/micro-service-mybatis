package aljoin.lea.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.object.ActHolidayListVO;
import aljoin.act.dao.object.ActHolidayVO;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.object.ActApprovalVO;
import aljoin.ioa.dao.object.ActRegulationListVO;
import aljoin.ioa.dao.object.ActRegulationVO;
import aljoin.ioa.dao.object.ActWorkingListVO;
import aljoin.ioa.dao.object.ActWorkingVO;
import aljoin.ioa.iservice.IoaMonitorWorkService;
import aljoin.lea.iservice.LeaLeaderService;
import aljoin.mai.dao.object.MaiSendBoxCountDO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mee.dao.object.MeeLeaderMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingCountDO;
import aljoin.mee.dao.object.MeeMeetingCountVO;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.object.OffMonthReportCountDO;
import aljoin.off.dao.object.OffMonthReportVO;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.sma.dao.entity.LeaStatisticsModule;
import aljoin.sma.iservice.LeaStatisticsModuleService;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.util.ExcelUtil;

/**
 *
 * 领导看板(服务实现类).
 *
 * @author：wangj
 *
 *               @date： 2017-10-17
 */
@Service
public class LeaLeaderServiceImpl implements LeaLeaderService {
    @Resource
    private MaiSendBoxService maiSendBoxService;
    @Resource
    private AttSignInOutService attSignInOutService;
    @Resource
    private MeeInsideMeetingService meeInsideMeetingService;
    @Resource
    private OffMonthReportService offMonthReportService;
    @Resource
    private IoaMonitorWorkService ioaMonitorWorkService;
    @Resource
    private LeaStatisticsModuleService leaStatisticsModuleService;
    @Resource
    private SysDataDictService sysDataDictService;

    @Override
    public Page<MaiSendBoxCountDO> mailCountList(MaiSendBoxVO obj, PageBean pageBean) throws Exception {
        Page<MaiSendBoxCountDO> page = new Page<MaiSendBoxCountDO>();
        List<MaiSendBoxCountDO> list = maiSendBoxService.mailCountList(obj);
        if (list != null && list.size() > 0) {
            list = maiSort(list, obj);
            for (int i = 0; i < list.size(); i++) {
                MaiSendBoxCountDO off = list.get(i);
                off.setNo(i + 1);
                list.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            }
            List<MaiSendBoxCountDO> returnList = new ArrayList<MaiSendBoxCountDO>();
            if (sum < list.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < list.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(list);
            }
            page.setTotal(list.size());
        } else {
            page.setTotal(0);
            page.setRecords(list);
        }
        page.setSize(pageBean.getPageSize());
        return page;
    }

    public List<MaiSendBoxCountDO> maiSort(List<MaiSendBoxCountDO> list, MaiSendBoxVO obj) throws Exception {
        boolean flag = true;
        int rSort = Integer.valueOf(obj.getRecevieSort());
        int tSort = Integer.valueOf(obj.getAttachmentSort());
        int sSort = Integer.valueOf(obj.getSendSort());
        while (flag) {
            MaiSendBoxCountDO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (rSort > 0) {
                        if (rSort == 2) {
                            if (list.get(j + 1).getReceviceNumber() < list.get(j).getReceviceNumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getReceviceNumber() > list.get(j).getReceviceNumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (tSort > 0) {
                        if (tSort == 2) {
                            if (list.get(j + 1).getTotalSize() < list.get(j).getTotalSize()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getTotalSize() > list.get(j).getTotalSize()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (sSort > 0) {
                        if (sSort == 2) {
                            if (list.get(j + 1).getSendNnumber() < list.get(j).getSendNnumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getSendNnumber() > list.get(j).getSendNnumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public void export(HttpServletResponse response, MaiSendBoxVO obj) throws Exception {
        maiSendBoxService.export(response, obj);
    }

    @Override
    public List<AttSignInCount> getAttSignInCount(AttSignInCount obj) throws Exception {
        List<AttSignInCount> list = attSignInOutService.getLAttSignInCount(obj);
        List<AttSignInCount> returnlist = new ArrayList<AttSignInCount>();
        if (list != null && list.size() > 0) {
            boolean flag = true;
            while (flag) {
                AttSignInCount tmpDo = list.get(0);
                for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                    for (int j = 0; j < list.size() - i - 1; j++) {
                        AttSignInCount tmpa = list.get(j + 1);
                        AttSignInCount tmpb = list.get(j);
                        int suma = tmpa.getLateNums() + tmpa.getLeaveEarlyNums() + tmpa.getNoneSignInNums()
                            + tmpa.getNoneSignOutNums() + tmpa.getSignPatchNums();

                        int sumb = tmpb.getLateNums() + tmpb.getLeaveEarlyNums() + tmpb.getNoneSignInNums()
                            + tmpb.getNoneSignOutNums() + tmpb.getSignPatchNums();

                        if (suma > sumb) {
                            tmpDo = list.get(j);
                            list.set(j, list.get(j + 1));
                            list.set(j + 1, tmpDo);
                            flag = true;
                        }
                    }
                    if (!flag) {
                        break;// 若果没有发生交换，则退出循环
                    }
                }
                flag = false;
            }
            for (int i = 0; i < list.size(); i++) {
                if (i == 5) {
                    break;
                }
                returnlist.add(list.get(i));
            }
        }
        return returnlist;
    }

    @Override
    public Page<AttSignInCount> getAttSignInCountList(AttSignInCount obj, PageBean pageBean) throws Exception {
        // 排序reportCountList
        Page<AttSignInCount> page = new Page<AttSignInCount>();
        List<AttSignInCount> list = attSignInOutService.getLAttSignInCountList(obj);
        List<AttSignInCount> returnList = new ArrayList<AttSignInCount>();
        if (list != null && list.size() > 0) {
            list = signSort(list, obj);
            for (int i = 0; i < list.size(); i++) {
                AttSignInCount off = list.get(i);
                off.setNo(String.valueOf(i + 1));
                list.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            }
            if (sum < list.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < list.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(list);
            }
            page.setTotal(list.size());
        } else {
            page.setTotal(0);
            page.setRecords(list);
        }
        page.setSize(pageBean.getPageSize());
        return page;
    }

    public List<AttSignInCount> signSort(List<AttSignInCount> list, AttSignInCount obj) throws Exception {
        // List<AttSignInCount> coumtList=new ArrayList<AttSignInCount>();
        boolean flag = true;
        int noSign = Integer.valueOf(obj.getOrderByNoneSignIn()),
            noSignOut = Integer.valueOf(obj.getOrderByNoneSignOut()), late = Integer.valueOf(obj.getOrderByLate()),
            leaveEarly = Integer.valueOf(obj.getOrderByLeaveEarly()),
            retroactive = Integer.valueOf(obj.getOrderByPatch());
        while (flag) {
            AttSignInCount tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (noSign > 0) {
                        if (noSign == 2) {
                            if (list.get(j + 1).getNoneSignInNums() < list.get(j).getNoneSignInNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getNoneSignInNums() > list.get(j).getNoneSignInNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (noSignOut > 0) {
                        if (noSignOut == 2) {
                            if (list.get(j + 1).getNoneSignOutNums() < list.get(j).getNoneSignOutNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getNoneSignOutNums() > list.get(j).getNoneSignOutNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (leaveEarly > 0) {
                        if (leaveEarly == 2) {
                            if (list.get(j + 1).getLeaveEarlyNums() < list.get(j).getLeaveEarlyNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getLeaveEarlyNums() > list.get(j).getLeaveEarlyNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (retroactive > 0) {
                        if (retroactive == 2) {
                            if (list.get(j + 1).getSignPatchNums() < list.get(j).getSignPatchNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getSignPatchNums() > list.get(j).getSignPatchNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (late > 0) {
                        if (late == 2) {
                            if (list.get(j + 1).getLateNums() < list.get(j).getLateNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getLateNums() > list.get(j).getLateNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }

                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;

    }

    @Override
    public void export(HttpServletResponse response, AttSignInCount obj) throws Exception {
        List<AttSignInCount> list = attSignInOutService.getLAttSignInCountList(obj);
        if (list != null && list.size() > 0) {
            list = signSort(list, obj);
        }
        if (null != list && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                AttSignInCount off = list.get(i);
                off.setNo(String.valueOf((i + 1)));
                list.set(i, off);
            }
            String[][] headers = {{"No", "序号"}, {"DeptName", "部门"}, {"NoneSignInNums", "未签到"},
                {"NoneSignOutNums", "未签退"}, {"LateNums", "迟到"}, {"LeaveEarlyNums", "早退"}, {"SignPatchNums", "补签"}};
            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (AttSignInCount entity : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    AttSignInCount schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = AttSignInCount.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("考勤统计", headers, dataset, response);
        }
        attSignInOutService.export(response, obj);
    }

    @Override
    public MeeMeetingCountDO meetingCount(MeeMeetingCountVO obj, String weets) throws Exception {
        MeeMeetingCountDO meeDO = new MeeMeetingCountDO();
        // int inSort = Integer.valueOf(obj.getInSort());
        // int outSort = Integer.valueOf(obj.getOutSort());
        meeDO = meeInsideMeetingService.meetingCount(obj, weets);
        return meeDO;

    }

    @Override
    public List<MeeLeaderMeetingRoomCountDO> getMeetingRoom() throws Exception {
        List<MeeLeaderMeetingRoomCountDO> meeDO = new ArrayList<MeeLeaderMeetingRoomCountDO>();
        meeDO = meeInsideMeetingService.getAppRoom();
        return meeDO;

    }

    @Override
    public Page<MeeMeetingCountDO> meetingCountList(MeeMeetingCountVO obj, String weets, PageBean pageBean)
        throws Exception {
        Page<MeeMeetingCountDO> page = new Page<MeeMeetingCountDO>();
        int inSort = Integer.valueOf(obj.getInSort());
        int outSort = Integer.valueOf(obj.getOutSort());
        List<MeeMeetingCountDO> meeList = meeInsideMeetingService.meetingCountList(obj, weets);
        meeList = this.meeSort(meeList, inSort, outSort);
        List<MeeMeetingCountDO> returnList = new ArrayList<MeeMeetingCountDO>();
        if (meeList != null && meeList.size() > 0) {
            for (int i = 0; i < meeList.size(); i++) {
                MeeMeetingCountDO off = meeList.get(i);
                off.setNo(i + 1);
                meeList.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            }
            if (sum < meeList.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < meeList.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(meeList.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(meeList);
            }
            page.setTotal(meeList.size());
        } else {
            page.setTotal(0);
            page.setRecords(meeList);
        }
        page.setSize(pageBean.getPageSize());

        return page;
    }

    public List<MeeMeetingCountDO> meeSort(List<MeeMeetingCountDO> list, int unSub, int sub) {
        // 提交排序
        boolean flag = true;
        while (flag) {
            MeeMeetingCountDO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (unSub > 0) {
                        if (unSub == 2) {
                            if (list.get(j + 1).getInSide() < list.get(j).getInSide()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getInSide() > list.get(j).getInSide()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    } else {
                        if (sub == 2) {
                            if (list.get(j + 1).getOutSide() < list.get(j).getOutSide()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getOutSide() > list.get(j).getOutSide()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public void export(HttpServletResponse response, MeeMeetingCountVO obj, String weets) throws Exception {
        List<MeeMeetingCountDO> meeList = meeInsideMeetingService.meetingCountList(obj, weets);
        int unSub = Integer.valueOf(obj.getInSort());// 内部会议
        int sub = Integer.valueOf(obj.getOutSort());// 外部会议
        if (meeList != null && meeList.size() > 0) {
            meeList = meeSort(meeList, unSub, sub);
        }
        if (null != meeList && !meeList.isEmpty()) {
            for (int i = 0; i < meeList.size(); i++) {
                MeeMeetingCountDO off = meeList.get(i);
                off.setNo(i + 1);
                meeList.set(i, off);
            }
            String[][] headers
                = {{"No", "序号"}, {"FullName", "用户名"}, {"PositionNames", "岗位"}, {"InSide", "内部邮件"}, {"OutSide", "外部邮件"}};
            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (MeeMeetingCountDO entity : meeList) {
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

    @Override
    public Page<OffMonthReportCountDO> reportCountList(OffMonthReportVO obj, PageBean pageBean) throws Exception {
        // 查询月报统计 未提交,提交(0代表不排序，1代表倒序，2代表升序)
        Page<OffMonthReportCountDO> page = new Page<OffMonthReportCountDO>();
        int unSub = Integer.valueOf(obj.getUnSubmitSort());// 未提交
        int sub = Integer.valueOf(obj.getSubmitSort());// 未提交
        List<OffMonthReportCountDO> list = offMonthReportService.returnReportCountList(obj);
        List<OffMonthReportCountDO> returnList = new ArrayList<OffMonthReportCountDO>();
        if (list != null && list.size() > 0) {
            list = submitSort(list, unSub, sub);
            for (int i = 0; i < list.size(); i++) {
                OffMonthReportCountDO off = list.get(i);
                off.setNo(i + 1);
                list.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            }
            if (sum < list.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < list.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(list);
            }
            page.setTotal(list.size());
        } else {
            page.setTotal(0);
            page.setRecords(list);
        }
        page.setSize(pageBean.getPageSize());
        return page;
    }

    public List<OffMonthReportCountDO> submitSort(List<OffMonthReportCountDO> list, int unSub, int sub) {
        // 提交排序
        boolean flag = true;
        while (flag) {
            OffMonthReportCountDO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (unSub > 0) {
                        if (unSub == 2) {
                            if (list.get(j + 1).getUnSubmitNumber() < list.get(j).getUnSubmitNumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getUnSubmitNumber() > list.get(j).getUnSubmitNumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    } else {
                        if (sub == 2) {
                            if (list.get(j + 1).getSubmitNumber() < list.get(j).getSubmitNumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getSubmitNumber() > list.get(j).getSubmitNumber()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public void export(HttpServletResponse response, OffMonthReportVO obj) throws Exception {
        List<OffMonthReportCountDO> reportCountList = offMonthReportService.returnReportCountList(obj);
        int unSub = Integer.valueOf(obj.getUnSubmitSort());// 未提交
        int sub = Integer.valueOf(obj.getSubmitSort());// 未提交
        if (reportCountList != null && reportCountList.size() > 0) {
            reportCountList = submitSort(reportCountList, unSub, sub);
        }
        if (null != reportCountList && !reportCountList.isEmpty()) {
            for (int i = 0; i < reportCountList.size(); i++) {
                OffMonthReportCountDO off = reportCountList.get(i);
                off.setNo(i + 1);
                reportCountList.set(i, off);
            }
            String[][] headers = {{"No", "序号"}, {"DeptName", "部门"}, {"SubmitNumber", "已交"}, {"UnSubmitNumber", "未交"}};
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

    @Override
    public List<MaiSendBoxCountDO> mailChartList(MaiSendBoxVO obj) throws Exception {
        List<MaiSendBoxCountDO> list = maiSendBoxService.mailChartList(obj);
        List<MaiSendBoxCountDO> retrunlist = new ArrayList<MaiSendBoxCountDO>();
        if (list != null && list.size() > 0) {
            boolean flag = true;
            while (flag) {
                MaiSendBoxCountDO tmpDo = list.get(0);
                for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                    for (int j = 0; j < list.size() - i - 1; j++) {
                        if (list.get(j + 1).getSendNnumber() > list.get(j).getSendNnumber()) {
                            tmpDo = list.get(j);
                            list.set(j, list.get(j + 1));
                            list.set(j + 1, tmpDo);
                            flag = true;
                        }

                    }

                    if (!flag) {
                        break;// 若果没有发生交换，则退出循环
                    }
                }
                flag = false;
            }
            if (list.size() > 5) {
                for (int i = 0; i < 5; i++) {
                    MaiSendBoxCountDO tmpdo = list.get(i);
                    // int psum=tmpdo.getTotalSize()/1024;
                    // tmpdo.setTotalSize(psum);
                    retrunlist.add(tmpdo);
                }
            } else {
                retrunlist = list;
            }
        }

        return retrunlist;
    }

    @Override
    public OffMonthReportCountDO reportCount() throws Exception {
        // TODO Auto-generated method stub
        return offMonthReportService.returnReportCount();
    }

    @Override
    public List<ActWorkingVO> getWorkingList(ActWorkingListVO obj) throws Exception {
        List<ActWorkingVO> list = ioaMonitorWorkService.getWorkingList(obj);
        List<ActWorkingVO> returnList = new ArrayList<ActWorkingVO>();
        if (list != null && list.size() > 0) {
            returnList = submitSort(list);
            /*if (list.size() > 20 && "1".equals(obj.getIsOne())) {
            	for (int i = 0; i < list.size(); i++) {
            		if (i == 20) {
            			break;
            		}
            		returnList.add(list.get(i));
            	}
            } else {
            	returnList = list;
            }*/
        }
        return returnList;
    }

    public List<ActWorkingVO> submitSort(List<ActWorkingVO> list) {
        // 提交排序
        boolean flag = true;
        while (flag) {
            ActWorkingVO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (list.get(j + 1).getBpmnNums() > list.get(j).getBpmnNums()) {
                        tmpDo = list.get(j);
                        list.set(j, list.get(j + 1));
                        list.set(j + 1, tmpDo);
                        flag = true;
                    }

                }

                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public List<ActWorkingVO> getWorking(ActWorkingListVO obj) throws Exception {
        List<ActWorkingVO> list = ioaMonitorWorkService.getWorkingList(obj);
        List<ActWorkingVO> returnList = new ArrayList<ActWorkingVO>();
        if (list != null && list.size() > 0) {
            list = submitSort(list);
            if (list.size() > 5) {
                for (int i = 0; i < list.size(); i++) {
                    if (i == 5) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
            } else {
                returnList = list;
            }
        }
        return returnList;
    }

    @Override
    public void export(HttpServletResponse response, ActWorkingListVO obj) throws Exception {
        List<ActWorkingVO> list = ioaMonitorWorkService.getWorkingList(obj);
        List<ActWorkingVO> returnList = new ArrayList<ActWorkingVO>();
        if (list != null && list.size() > 0) {
            list = submitSort(list);
            /*if (list.size() > 20 && "1".equals(obj.getIsOne())) {
            	for (int i = 0; i < list.size(); i++) {
            		if (i == 20) {
            			break;
            		}
            		returnList.add(list.get(i));
            	}
            } else {*/
            returnList = list;
            /*}*/
        }
        if (null != returnList && !returnList.isEmpty()) {
            for (int i = 0; i < returnList.size(); i++) {
                ActWorkingVO off = returnList.get(i);
                off.setNo(i + 1);
                list.set(i, off);
            }
            String[][] headers = {{"No", "序号"}, {"BpmnName", "流程名称"}, {"BpmnNums", "使用次数"}};
            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (ActWorkingVO entity : returnList) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    ActWorkingVO schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = ActWorkingVO.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("工作流统计", headers, dataset, response);
        }
    }

    @Override
    public void exportApproval(HttpServletResponse response, ActWorkingListVO obj) throws Exception {
        List<ActApprovalVO> list = ioaMonitorWorkService.getApprovalList(obj);
        if (null != list && !list.isEmpty()) {
            list = appSort(list, obj);
            for (int i = 0; i < list.size(); i++) {
                ActApprovalVO off = list.get(i);
                off.setNo(i + 1);
                list.set(i, off);
            }
            String[][] headers = {{"No", "序号"}, {"UserName", "用户名"}, {"JobsName", "所属岗位"}, {"NewNums", "新建"},
                {"StayNums", "收到待办"}, {"HandleNums", "办理待办"}};
            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (ActApprovalVO entity : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    ActApprovalVO schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = ActApprovalVO.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("审批流程统计", headers, dataset, response);
        }
    }

    @Override
    public List<ActApprovalVO> getApproval(ActWorkingListVO obj) throws Exception {
        // TODO Auto-generated method stub
        List<ActApprovalVO> appVo = ioaMonitorWorkService.getApproval(obj);
        List<ActApprovalVO> appsVo = new ArrayList<ActApprovalVO>();
        if (appVo != null) {
            appVo = approvalSort(appVo);
            if (appVo.size() > 5) {
                appsVo.add(appVo.get(0));
                appsVo.add(appVo.get(1));
                appsVo.add(appVo.get(2));
                appsVo.add(appVo.get(3));
                appsVo.add(appVo.get(4));
            } else {
                appsVo = appVo;
            }
        }
        return appsVo;
    }

    public List<ActApprovalVO> approvalSort(List<ActApprovalVO> list) {
        // 提交排序
        boolean flag = true;

        while (flag) {
            ActApprovalVO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    ActApprovalVO tmpA = list.get(j + 1);
                    ActApprovalVO tmpB = list.get(j);
                    int sumA = tmpA.getNewNums() + tmpA.getHandleNums() + tmpA.getStayNums();
                    int sumB = tmpB.getNewNums() + tmpB.getHandleNums() + tmpB.getStayNums();
                    if (sumA > sumB) {
                        tmpDo = tmpB;
                        list.set(j, tmpA);
                        list.set(j + 1, tmpDo);
                        flag = true;
                    }
                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public Page<ActApprovalVO> getApprovalList(ActWorkingListVO obj, PageBean pageBean) throws Exception {
        // TODO Auto-generated method stub
        Page<ActApprovalVO> page = new Page<ActApprovalVO>();
        List<ActApprovalVO> list = ioaMonitorWorkService.getApprovalList(obj);
        List<ActApprovalVO> returnList = new ArrayList<ActApprovalVO>();
        if (list != null && list.size() > 0) {
            list = appSort(list, obj);
            for (int i = 0; i < list.size(); i++) {
                ActApprovalVO off = list.get(i);
                off.setNo(i + 1);
                list.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            }
            if (sum < list.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < list.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(list);
            }
            page.setTotal(list.size());

        } else {
            page.setTotal(0);
            page.setRecords(list);
        }
        page.setSize(pageBean.getPageSize());

        return page;
    }

    public List<ActApprovalVO> appSort(List<ActApprovalVO> list, ActWorkingListVO obj) {
        // 提交排序
        boolean flag = true;
        int byHandle = Integer.valueOf(obj.getOderByHandle());
        int byNewe = Integer.valueOf(obj.getOderByNew());
        int byStay = Integer.valueOf(obj.getOderByStay());
        while (flag) {
            ActApprovalVO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (byHandle > 0) {
                        if (byHandle == 2) {
                            if (list.get(j + 1).getHandleNums() < list.get(j).getHandleNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getHandleNums() > list.get(j).getHandleNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (byNewe > 0) {
                        if (byNewe == 2) {
                            if (list.get(j + 1).getNewNums() < list.get(j).getNewNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getNewNums() > list.get(j).getNewNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (byStay > 0) {
                        if (byStay == 2) {
                            if (list.get(j + 1).getStayNums() < list.get(j).getStayNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getStayNums() > list.get(j).getStayNums()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }

                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public void exportHoli(HttpServletResponse response, ActHolidayVO obj) throws Exception {
        PageBean pageBean = new PageBean();
        List<ActHolidayListVO> list = ioaMonitorWorkService.getHolidayList(obj, pageBean);
        if (null != list && !list.isEmpty()) {
            list = holiSort(list, obj);
            for (int i = 0; i < list.size(); i++) {
                ActHolidayListVO off = list.get(i);
                off.setNo(String.valueOf(i + 1));
                list.set(i, off);
            }
            String[][] headers = {{"No", "序号"}, {"UserName", "用户名"}, {"JobsName", "所属岗位"}, {"ThingsLeave", "事假"},
                {"DiseaseLeave", "病假"}, {"AnnualLeave", "探亲假"}, {"MarriageLeave", "婚假"}, {"MaternityLeave", "生育假"},
                {"AllocatedLeave", "公休假"}, {"DieLeave", "丧假"}, {"OtherLeave", "其他假"}};

            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (ActHolidayListVO entity : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    ActHolidayListVO schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = ActHolidayListVO.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("请假统计", headers, dataset, response);
        }
    }

    @Override
    public ActHolidayListVO getHoli(ActHolidayVO obj) throws Exception {
        // TODO Auto-generated method stub
        return ioaMonitorWorkService.getHoliday(obj);
    }

    @Override
    public Page<ActHolidayListVO> getHoliList(ActHolidayVO obj, PageBean pageBean) throws Exception {
        // TODO Auto-generated method stub
        Page<ActHolidayListVO> page = new Page<ActHolidayListVO>();
        List<ActHolidayListVO> list = ioaMonitorWorkService.getHolidayList(obj, pageBean);
        if (list != null && list.size() > 0) {
            list = this.holiSort(list, obj);
        }
        List<ActHolidayListVO> returnList = new ArrayList<ActHolidayListVO>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ActHolidayListVO off = list.get(i);
                int nos = i + 1;
                off.setNo(String.valueOf(nos));
                list.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            } else {
                pageBean.setPageNum(1);
            }
            if (sum < list.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < list.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(list);
            }
            page.setTotal(list.size());
        } else {
            page.setTotal(0);
            page.setRecords(list);
        }
        page.setSize(pageBean.getPageSize());

        return page;
    }

    public List<ActHolidayListVO> holiSort(List<ActHolidayListVO> list, ActHolidayVO obj) {
        // 提交排序
        boolean flag = true;
        int oderByAllocated = Integer.valueOf(obj.getOderByAllocated());
        int orderByDie = Integer.valueOf(obj.getOrderByDie());
        int oderByDisease = Integer.valueOf(obj.getOderByDisease());
        int oderByMarriage = Integer.valueOf(obj.getOderByMarriage());
        int oderByMaternity = Integer.valueOf(obj.getOderByMaternity());
        int oderByThings = Integer.valueOf(obj.getOderByThings());
        int oderByYear = Integer.valueOf(obj.getOderByYear());
        int oderByOther = Integer.valueOf(obj.getOrderByOther());

        while (flag) {
            ActHolidayListVO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (oderByAllocated > 0) {
                        if (oderByAllocated == 2) {
                            if (list.get(j + 1).getAllocatedLeave() < list.get(j).getAllocatedLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getAllocatedLeave() > list.get(j).getAllocatedLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                    }
                    if (orderByDie > 0) {
                        if (orderByDie == 2) {
                            if (list.get(j + 1).getDieLeave() < list.get(j).getDieLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getDieLeave() > list.get(j).getDieLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (oderByDisease > 0) {
                        if (oderByDisease == 2) {
                            if (list.get(j + 1).getDiseaseLeave() < list.get(j).getDiseaseLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getDiseaseLeave() > list.get(j).getDiseaseLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (oderByMarriage > 0) {
                        if (oderByMarriage == 2) {
                            if (list.get(j + 1).getMarriageLeave() < list.get(j).getMarriageLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getMarriageLeave() > list.get(j).getMarriageLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (oderByMaternity > 0) {
                        if (oderByMaternity == 2) {
                            if (list.get(j + 1).getMaternityLeave() < list.get(j).getMaternityLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getMaternityLeave() > list.get(j).getMaternityLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (oderByThings > 0) {
                        if (oderByThings == 2) {
                            if (list.get(j + 1).getThingsLeave() < list.get(j).getThingsLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getThingsLeave() > list.get(j).getThingsLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (oderByYear > 0) {
                        if (oderByYear == 2) {
                            if (list.get(j + 1).getAnnualLeave() < list.get(j).getAnnualLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getAnnualLeave() > list.get(j).getAnnualLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }
                    if (oderByOther > 0) {
                        if (oderByOther == 2) {
                            if (list.get(j + 1).getOtherLeave() < list.get(j).getOtherLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getOtherLeave() > list.get(j).getOtherLeave()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        }
                    }

                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public void exportRegulation(HttpServletResponse response, ActRegulationListVO obj) throws Exception {
        PageBean pageBean = new PageBean();
        List<ActRegulationVO> list = ioaMonitorWorkService.getRegulationList(obj, pageBean);
        if (null != list && !list.isEmpty()) {
            list = regulationSort(list, obj);
            for (int i = 0; i < list.size(); i++) {
                ActRegulationVO off = list.get(i);
                off.setNo(i + 1);
                list.set(i, off);
            }
            String[][] headers = {{"No", "序号"}, {"FullName", "用户名"}, {"JobsName", "所属岗位"}, {"SumNo", "文件数量"},
                {"Returnsth", "签收总时间"}, {"Psth", "平均签收时间"}, {"ReturntotalTime", "处理总时间"}, {"pTotalTime", "平均处理时间"}};

            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (ActRegulationVO entity : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    ActRegulationVO schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = ActRegulationVO.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("工作监管", headers, dataset, response);
        }
    }

    @Override
    public List<ActRegulationVO> getRegulation(ActRegulationListVO obj) throws Exception {
        List<ActRegulationVO> list = ioaMonitorWorkService.getRegulation(obj);
        List<ActRegulationVO> returnlist = new ArrayList<ActRegulationVO>();
        if (list != null && list.size() > 0) {
            boolean flag = true;
            while (flag) {
                ActRegulationVO tmpDo = list.get(0);
                for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                    for (int j = 0; j < list.size() - i - 1; j++) {
                        BigDecimal suma = list.get(j + 1).getPsth().add(list.get(j + 1).getpTotalTime());
                        BigDecimal sumb = list.get(j).getPsth().add(list.get(j).getpTotalTime());
                        if (suma.compareTo(sumb) == 1) {
                            tmpDo = list.get(j);
                            list.set(j, list.get(j + 1));
                            list.set(j + 1, tmpDo);
                            flag = true;
                        }
                    }
                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
                flag = false;
            }
            if (list.size() > 5) {
                for (int i = 0; i < 5; i++) {
                    returnlist.add(list.get(i));
                }
            } else {
                returnlist = list;
            }
        } else {
            returnlist = list;
        }

        return returnlist;

    }

    public List<ActRegulationVO> regulationSort(List<ActRegulationVO> list, ActRegulationListVO obj) {
        // 提交排序
        boolean flag = true;
        int orderByNo = obj.getOrderByNo();
        int orderBySTH = obj.getOrderBySTH();
        int orderByPSTH = obj.getOrderByPSTH();
        int orderByTotalTime = obj.getOrderByTotalTime();
        int orderByPTotalTime = obj.getOrderByPTotalTime();

        while (flag) {
            ActRegulationVO tmpDo = list.get(0);
            for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                for (int j = 0; j < list.size() - i - 1; j++) {
                    if (orderByNo > 0) {
                        if (orderByNo == 2) {
                            if (list.get(j + 1).getSumNo() < list.get(j).getSumNo()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }
                        } else {
                            if (list.get(j + 1).getSumNo() > list.get(j).getSumNo()) {
                                tmpDo = list.get(j);
                                list.set(j, list.get(j + 1));
                                list.set(j + 1, tmpDo);
                                flag = true;
                            }

                        }
                        if (orderByPTotalTime > 0) {
                            if (orderByPTotalTime == 2) {
                                if (list.get(j + 1).getpTotalTime().compareTo(list.get(j).getpTotalTime()) == -1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (list.get(j + 1).getpTotalTime().compareTo(list.get(j).getpTotalTime()) == 1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                        if (orderByTotalTime > 0) {
                            if (orderByTotalTime == 2) {
                                if (list.get(j + 1).getTotalTime().compareTo(list.get(j).getTotalTime()) == -1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (list.get(j + 1).getTotalTime().compareTo(list.get(j).getTotalTime()) == 1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                        if (orderByPSTH > 0) {
                            if (orderByPSTH == 2) {
                                if (list.get(j + 1).getPsth().compareTo(list.get(j).getPsth()) == -1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (list.get(j + 1).getPsth().compareTo(list.get(j).getPsth()) == 1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                        if (orderBySTH > 0) {
                            if (orderBySTH == 2) {
                                if (list.get(j + 1).getSth().compareTo(list.get(j).getSth()) == -1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (list.get(j + 1).getSth().compareTo(list.get(j).getSth()) == 1) {
                                    tmpDo = list.get(j);
                                    list.set(j, list.get(j + 1));
                                    list.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                    }
                }
                if (!flag) {
                    break;// 若果没有发生交换，则退出循环
                }
            }
            flag = false;
        }
        return list;
    }

    @Override
    public Page<ActRegulationVO> getRegulationList(ActRegulationListVO obj, PageBean pageBean) throws Exception {
        Page<ActRegulationVO> page = new Page<ActRegulationVO>();
        List<ActRegulationVO> list = ioaMonitorWorkService.getRegulationList(obj, pageBean);
        if (list != null && list.size() > 0) {
            list = this.regulationSort(list, obj);
        }
        List<ActRegulationVO> returnList = new ArrayList<ActRegulationVO>();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ActRegulationVO off = list.get(i);
                int nos = i + 1;
                off.setNo(nos);
                list.set(i, off);
            }
            int tmpsize = pageBean.getPageSize();
            Integer sum = 0;
            if (pageBean.getPageNum() != null) {
                sum = tmpsize * (pageBean.getPageNum() - 1);
            } else {
                pageBean.setPageNum(1);
            }
            if (sum < list.size()) {
                int strSum = 0;
                if (pageBean.getPageNum() > 1) {
                    strSum = tmpsize * (pageBean.getPageNum() - 1);
                }
                sum = sum + tmpsize - 1;
                for (int i = strSum; i < list.size(); i++) {
                    if (sum < i) {
                        break;
                    }
                    returnList.add(list.get(i));
                }
                page.setRecords(returnList);
            } else {
                page.setRecords(list);
            }
            page.setTotal(list.size());
        } else {
            page.setTotal(0);
            page.setRecords(list);
        }
        page.setSize(pageBean.getPageSize());
        return page;
    }

    @Override
    @Transactional
    public RetMsg init(Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 默认isActive=1
        // 生成个人默认配置
        List<LeaStatisticsModule> adpmList = new ArrayList<LeaStatisticsModule>();
        adpmList = selectListUser(userId.toString());
        if (adpmList != null && adpmList.size() > 0) {

        } else {
            List<SysDataDict> lDataList = sysDataDictService.getByCode("LeaPage");
            if (lDataList != null && lDataList.size() > 0) {

            } else {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("请设置数据字典领导看板模块排序,配置编码为：LeaPage");
                return retMsg;
            }
            for (SysDataDict sysDataDict : lDataList) {
                LeaStatisticsModule obj = new LeaStatisticsModule();
                obj.setIsDelete(0);
                obj.setIsActive(1);
                obj.setIsHide(0);
                obj.setModuleRank(Integer.valueOf(sysDataDict.getDictValue()));
                obj.setModuleName(sysDataDict.getDictKey());
                obj.setModuleCode(sysDataDict.getDictValue());
                leaStatisticsModuleService.insert(obj);
            }
            adpmList = selectListUser(userId.toString());
        }
        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
        retMsg.setObject(adpmList);
        return retMsg;
    }

    @Override
    public List<LeaStatisticsModule> selectListUser(String userid) throws Exception {
        List<LeaStatisticsModule> ipm = new ArrayList<LeaStatisticsModule>();
        Where<LeaStatisticsModule> where = new Where<LeaStatisticsModule>();
        where.where("create_user_id={0}", userid);
        where.eq("is_delete", 0);
        where.eq("is_active", 1);
        where.orderBy("module_rank", true);
        ipm = leaStatisticsModuleService.selectList(where);
        return ipm;
    }

    @Override
    public RetMsg removeUpdate(String codes, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        Where<LeaStatisticsModule> ipWhere = new Where<LeaStatisticsModule>();
        ipWhere.eq("create_user_id", userId);
        ipWhere.eq("is_hide", 0);
        ipWhere.eq("is_active", 1);
        ipWhere.orderBy("module_rank");
        List<LeaStatisticsModule> autPageModuleList = leaStatisticsModuleService.selectList(ipWhere);
        List<LeaStatisticsModule> tmpList = new ArrayList<LeaStatisticsModule>();
        // List<Integer> tmpRank = new ArrayList<Integer>();
        String[] code = codes.split(",");
        for (int i = 0; i < code.length; i++) {
            for (LeaStatisticsModule autIndexPageModule : autPageModuleList) {
                if ((code[i]).equals(autIndexPageModule.getModuleCode())) {
                    tmpList.add(autIndexPageModule);
                    break;
                }
            }
        }
        List<Integer> intList = new ArrayList<Integer>();
        for (int i = 0; i < autPageModuleList.size(); i++) {
            intList.add(autPageModuleList.get(i).getModuleRank());
        }
        for (int i = 0; i < tmpList.size(); i++) {
            LeaStatisticsModule autIndexPageModule = tmpList.get(i);
            autIndexPageModule.setModuleRank(intList.get(i));
            leaStatisticsModuleService.updateById(autIndexPageModule);
        }
        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage("操作成功");
        return retMsg;
    }
}
