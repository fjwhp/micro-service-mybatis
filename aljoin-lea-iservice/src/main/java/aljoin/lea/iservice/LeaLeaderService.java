package aljoin.lea.iservice;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.object.ActHolidayListVO;
import aljoin.act.dao.object.ActHolidayVO;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.ioa.dao.object.ActApprovalVO;
import aljoin.ioa.dao.object.ActRegulationListVO;
import aljoin.ioa.dao.object.ActRegulationVO;
import aljoin.ioa.dao.object.ActWorkingListVO;
import aljoin.ioa.dao.object.ActWorkingVO;
import aljoin.mai.dao.object.MaiSendBoxCountDO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mee.dao.object.MeeLeaderMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingCountDO;
import aljoin.mee.dao.object.MeeMeetingCountVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.off.dao.object.OffMonthReportCountDO;
import aljoin.off.dao.object.OffMonthReportVO;
import aljoin.sma.dao.entity.LeaStatisticsModule;

public interface LeaLeaderService {
    /**
     *
     * 邮件统计
     *
     * @return：List<MaiSendBoxCountDO>
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public Page<MaiSendBoxCountDO> mailCountList(MaiSendBoxVO obj, PageBean pageBean) throws Exception;

    /**
     *
     * 邮件统计导出
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public void export(HttpServletResponse response, MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 考勤统计
     *
     * @return：List<AttSignInCount>
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public Page<AttSignInCount> getAttSignInCountList(AttSignInCount obj, PageBean pageBean) throws Exception;

    /**
     *
     * 考勤统计
     *
     * @return：List<AttSignInCount>
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public List<AttSignInCount> getAttSignInCount(AttSignInCount obj) throws Exception;

    /**
     *
     * 考勤统计导出
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public void export(HttpServletResponse response, AttSignInCount obj) throws Exception;

    /**
     *
     * 会议统计
     *
     * @return：List<MeeMeetingCountDO>
     *
     * @author：huangw
     *
     * @date：2017年11月21日
     */
    public Page<MeeMeetingCountDO> meetingCountList(MeeMeetingCountVO obj, String weets, PageBean pageBean)
        throws Exception;

    /**
     *
     * 会议统计导出
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public void export(HttpServletResponse response, MeeMeetingCountVO obj, String weets) throws Exception;

    /**
     *
     * 工作月报统计
     *
     * @return：List<OffMonthReportCountDO>
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public Page<OffMonthReportCountDO> reportCountList(OffMonthReportVO obj, PageBean pageBean) throws Exception;

    /**
     *
     * 工作月报统计
     *
     * @return：List<OffMonthReportCountDO>
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public OffMonthReportCountDO reportCount() throws Exception;

    /**
     *
     * 工作月报统计导出
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public void export(HttpServletResponse response, OffMonthReportVO obj) throws Exception;

    /**
     *
     * 邮件图表统计
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public List<MaiSendBoxCountDO> mailChartList(MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 会议图表统计
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public MeeMeetingCountDO meetingCount(MeeMeetingCountVO obj, String weets) throws Exception;

    /**
     *
     * 工作流统计首页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public List<ActWorkingVO> getWorking(ActWorkingListVO obj) throws Exception;

    /**
     *
     * 工作流统计分页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public List<ActWorkingVO> getWorkingList(ActWorkingListVO obj) throws Exception;

    /**
     *
     * 工作流导出
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public void export(HttpServletResponse response, ActWorkingListVO obj) throws Exception;

    /**
     *
     * 审批流导出
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月24日
     */
    public void exportApproval(HttpServletResponse response, ActWorkingListVO obj) throws Exception;

    /**
     *
     * 工作流统计首页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public List<ActApprovalVO> getApproval(ActWorkingListVO obj) throws Exception;

    /**
     *
     * 工作流统计分页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public Page<ActApprovalVO> getApprovalList(ActWorkingListVO obj, PageBean pageBean) throws Exception;

    /**
     *
     * 请假导出
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月24日
     */
    public void exportHoli(HttpServletResponse response, ActHolidayVO obj) throws Exception;

    /**
     *
     * 请假统计首页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public ActHolidayListVO getHoli(ActHolidayVO obj) throws Exception;

    /**
     *
     * 请假统计分页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public Page<ActHolidayListVO> getHoliList(ActHolidayVO obj, PageBean pageBean) throws Exception;

    /**
     *
     * 工作监管
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月24日
     */
    public void exportRegulation(HttpServletResponse response, ActRegulationListVO obj) throws Exception;

    /**
     *
     * 工作监管首页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public List<ActRegulationVO> getRegulation(ActRegulationListVO obj) throws Exception;

    /**
     *
     * 工作监管分页
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月23日
     */
    public Page<ActRegulationVO> getRegulationList(ActRegulationListVO obj, PageBean pageBean) throws Exception;

    /**
     *
     * 会议室使用情况
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017年11月31日
     */
    public List<MeeLeaderMeetingRoomCountDO> getMeetingRoom() throws Exception;

    /**
     *
     * 领导看板初始化
     *
     * @return：RetMsg
     *
     * @author：huangw
     *
     * @date：2017年12月25日
     */
    public RetMsg init(Long userId) throws Exception;

    /**
     *
     * 领导看板初始化List
     *
     * @return：RetMsg
     *
     * @author：huangw
     *
     * @date：2017年12月25日
     */
    public List<LeaStatisticsModule> selectListUser(String userid) throws Exception;

    /**
     *
     * 领导看板移动重置
     *
     * @return：RetMsg
     *
     * @author：huangw
     *
     * @date：2017年12月25日
     */
    public RetMsg removeUpdate(String codes, Long userId) throws Exception;

}
