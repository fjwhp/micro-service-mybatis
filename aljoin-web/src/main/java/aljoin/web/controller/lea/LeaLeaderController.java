package aljoin.web.controller.lea;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.object.ActHolidayListVO;
import aljoin.act.dao.object.ActHolidayVO;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.aut.security.CustomUser;
import aljoin.ioa.dao.object.ActApprovalVO;
import aljoin.ioa.dao.object.ActRegulationListVO;
import aljoin.ioa.dao.object.ActRegulationVO;
import aljoin.ioa.dao.object.ActWorkingListVO;
import aljoin.ioa.dao.object.ActWorkingVO;
import aljoin.lea.iservice.LeaLeaderService;
import aljoin.mai.dao.object.MaiSendBoxCountDO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mee.dao.object.MeeMeetingCountDO;
import aljoin.mee.dao.object.MeeMeetingCountVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.object.OffMonthReportCountDO;
import aljoin.off.dao.object.OffMonthReportVO;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 *
 * 领导看板(控制器).
 *
 * @author：wangj
 *
 *               @date： 2017-10-17
 */
@Controller
@RequestMapping(value = "/lea/leader", method = RequestMethod.POST)
@Api(value = "领导看板Controller", description = "领导看板->领导看图相关接口")
public class LeaLeaderController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(LeaLeaderController.class);
    @Resource
    private LeaLeaderService leaLeaderService;

    /**
     * 
     * 模块定制表(初始化首页及个性定制页数据).
     *
     * @return：RetMsg
     *
     * @author：
     *
     * @date：2017-11-07
     */
    @RequestMapping("/init")
    @ResponseBody
    public RetMsg init(HttpServletRequest request, HttpServletResponse response) {
        CustomUser user = getCustomDetail();
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = leaLeaderService.init(user.getUserId());
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 模块定制表(移动模块重新排序).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/removeUpdate")
    @ResponseBody
    public RetMsg removeUpdate(HttpServletRequest request, HttpServletResponse response) {
        RetMsg retMsg = new RetMsg();
        CustomUser user = getCustomDetail();
        try {
            String codes = request.getParameter("module_code");
            if (codes == null && "".equals(codes)) {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("参数为空");
            }
            retMsg = leaLeaderService.removeUpdate(codes, user.getUserId());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("更新失败" + e.getMessage());
        }
        return retMsg;
    }

    /**
     *
     * 邮件统计列表.
     *
     * @return：Page<MaiSendBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiCountList")
    @ResponseBody
    @ApiOperation(value = "邮件统计列表接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "sendSort", value = "发件数排序", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "recevieSort", value = "收件人总数排序(1:降序 2：升序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "attachmentSort", value = "总附件大小排序(1:降序 2：升序)", required = false, dataType = "string",
            paramType = "query")})
    public Page<MaiSendBoxCountDO> maiCountList(HttpServletRequest request, HttpServletResponse response,
        MaiSendBoxVO obj, PageBean pageBean) {
        Page<MaiSendBoxCountDO> maiCountList = new Page<MaiSendBoxCountDO>();
        try {
            maiCountList = leaLeaderService.mailCountList(obj, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return maiCountList;
    }

    /**
     *
     * 邮件统计列表.
     *
     * @return：Page<MaiSendBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiCountExport", method = RequestMethod.GET)
    // @ResponseBody
    @ApiOperation(value = "邮件统计导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "thisMonth", value = "本月", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "recevieSort", value = "收件人总数排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "sendSort", value = "发件数排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "attachmentSort", value = "总附件大小排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query")})
    public void maiCountExport(HttpServletRequest request, HttpServletResponse response, MaiSendBoxVO obj) {
        try {
            leaLeaderService.export(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     *
     * 工作流统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/workingCountList")
    @ResponseBody
    @ApiOperation(value = "工作流统计列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "bpmnName", value = "流程名称", required = false, dataType = "string",
            paramType = "query")})
    public List<ActWorkingVO> workingCountList(HttpServletRequest request, HttpServletResponse response,
        ActWorkingListVO obj) {
        List<ActWorkingVO> actWorkingVOList = new ArrayList<ActWorkingVO>();
        try {
            actWorkingVOList = leaLeaderService.getWorkingList(obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return actWorkingVOList;
    }

    /**
     *
     * 工作流统计首页.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/workingCount")
    @ResponseBody
    @ApiOperation(value = "工作流统计首页")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "bpmnName", value = "流程名称", required = false, dataType = "string",
            paramType = "query")})
    public List<ActWorkingVO> workingCount(HttpServletRequest request, HttpServletResponse response,
        ActWorkingListVO obj) {
        List<ActWorkingVO> actWorkingVOList = new ArrayList<ActWorkingVO>();
        try {
            actWorkingVOList = leaLeaderService.getWorking(obj);

        } catch (Exception e) {
            logger.error("", e);
        }
        return actWorkingVOList;
    }

    /**
     *
     * 工作流统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/workingCountExport", method = RequestMethod.GET)
    @ApiOperation(value = "工作流统计导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "thisMonth", value = "本月", required = false, dataType = "string",
            paramType = "query"),})
    public void workingCountExport(HttpServletRequest request, HttpServletResponse response, ActWorkingListVO obj) {
        try {
            leaLeaderService.export(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     *
     * 请假统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    /*
    @RequestMapping(value = "/holidayCountList")
    @ResponseBody
    @ApiOperation(value = "请假统计列表")
    @ApiImplicitParams({
    		@ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
    		@ApiImplicitParam(name = "startTimeholi", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
    		@ApiImplicitParam(name = "endTimeholi", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
    		@ApiImplicitParam(name = "weeks", value = "本周", required = false, dataType = "string", paramType = "query"),
    		@ApiImplicitParam(name = "orderByNoneSignIn", value = "根据未签到次数降序(1:升序 其它：降序)", required = false, dataType = "string", paramType = "query"),
    		@ApiImplicitParam(name = "orderByNoneSignOut", value = "根据未签退次数降序(1:升序 其它：降序)", required = false, dataType = "string", paramType = "query"),
    		@ApiImplicitParam(name = "orderByLate", value = "根据迟到次数降序(1:升序 其它：降序)", required = false, dataType = "string", paramType = "query"),
    		@ApiImplicitParam(name = "orderByLeaveEarly", value = "根据早退次数降序(1:升序 其它：降序)", required = false, dataType = "string", paramType = "query") })
    public Page<ActHolidayListVO> holidayCountList(HttpServletRequest request, HttpServletResponse response,
    		ActHolidayVO obj, PageBean pageBean) {
    	Page<ActHolidayListVO> attActHolidayList = new Page<ActHolidayListVO>();
    	try {
    		
    	} catch (Exception e) {
    		logger.error("", e);
    	}
    	return attActHolidayList;
    }*/

    /**
     *
     * 考勤统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/attSignInCountList")
    @ResponseBody
    @ApiOperation(value = "考勤统计列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "deptName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderByNoneSignIn", value = "根据未签到次数降序(1:升序 其它：降序)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderByNoneSignOut", value = "根据未签退次数降序(1:升序 其它：降序)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderByLate", value = "根据迟到次数降序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByLeaveEarly", value = "根据早退次数降序(1:升序 其它：降序)", required = false,
            dataType = "string", paramType = "query")})
    public Page<AttSignInCount> attSignInCountList(HttpServletRequest request, HttpServletResponse response,
        AttSignInCount obj, PageBean pageBean) {
        Page<AttSignInCount> attSignInCountList = new Page<AttSignInCount>();
        try {
            attSignInCountList = leaLeaderService.getAttSignInCountList(obj, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return attSignInCountList;
    }

    /**
     *
     * 考勤统计
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/attSignInCount")
    @ResponseBody
    @ApiOperation(value = "考勤统计")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "deptName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "string", paramType = "query")})
    public List<AttSignInCount> attSignInCount(HttpServletRequest request, HttpServletResponse response,
        AttSignInCount obj) {
        List<AttSignInCount> attSignInCount = new ArrayList<AttSignInCount>();
        try {
            obj.setThisWeek("0");
            attSignInCount = leaLeaderService.getAttSignInCount(obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return attSignInCount;
    }

    /**
     *
     * 考勤统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/attSignInCountExport", method = RequestMethod.GET)
    @ApiOperation(value = "考勤统计导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "thisMonth", value = "本月", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderByNoneSignIn", value = "根据未签到次数降序(1:升序 其它：降序)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderByNoneSignOut", value = "根据未签退次数降序(1:升序 其它：降序)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "orderByLate", value = "根据迟到次数降序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByLeaveEarly", value = "根据早退次数降序(1:升序 其它：降序)", required = false,
            dataType = "string", paramType = "query")})
    public void attSignInCountExport(HttpServletRequest request, HttpServletResponse response, AttSignInCount obj) {
        try {
            leaLeaderService.export(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     *
     * 首页视图会议数据.
     *
     * @return：List<AttSignInCount>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/meetingCount")
    @ResponseBody
    @ApiOperation(value = "看板会议统计")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date",
            paramType = "query")})
    public MeeMeetingCountDO meetingCount(HttpServletRequest request, HttpServletResponse response) {
        MeeMeetingCountDO countDo = new MeeMeetingCountDO();
        try {
            MeeMeetingCountVO obj = new MeeMeetingCountVO();
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            String weeks = "0";
            obj.setInSort("1");
            obj.setOutSort("0");
            countDo = leaLeaderService.meetingCount(obj, weeks);
            if (countDo.getInSide() == null) {
                countDo.setInSide(0);
            }
            if (countDo.getOutSide() == null) {
                countDo.setOutSide(0);
            }
            BigDecimal unSubmit = new BigDecimal(countDo.getInSide());
            BigDecimal submit = new BigDecimal(countDo.getOutSide());
            // 已提交/（已提交+未提交）*100%
            BigDecimal sum = submit.add(unSubmit);
            BigDecimal returnSubmit = new BigDecimal("0");
            if (sum.compareTo(new BigDecimal("0")) != 0) {
                returnSubmit = submit.divide(sum, 2, BigDecimal.ROUND_FLOOR);
                returnSubmit = returnSubmit.multiply(new BigDecimal(100));
            }
            String returnStr = df.format(returnSubmit);
            countDo.setFullName(returnStr);
        } catch (Exception e) {
            logger.error("", e);
        }
        return countDo;
    }

    /**
     *
     * 会议统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/meetingCountList")
    @ResponseBody
    @ApiOperation(value = "会议统计列表接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "30天 0。本周1，本月2", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "inSort", value = "内部会议排序(1:升序 其它：降序,0不排序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "outSort", value = "外部会议排序(1:升序 其它：降序,0不排序)", required = false, dataType = "string",
            paramType = "query")})
    public Page<MeeMeetingCountDO> meetingCountList(HttpServletRequest request, HttpServletResponse response,
        MeeMeetingCountVO obj, String weeks, PageBean pageBean) {
        Page<MeeMeetingCountDO> page = new Page<MeeMeetingCountDO>();
        try {
            page = leaLeaderService.meetingCountList(obj, weeks, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     *
     * 会议统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/meetingCountListExport", method = RequestMethod.GET)
    @ApiOperation(value = "会议统计导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weets", value = "本周本月30天内", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "inSort", value = "内部会议排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "outSort", value = "外部会议排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query")})
    public void meetingCountListExport(HttpServletRequest request, HttpServletResponse response, MeeMeetingCountVO obj,
        String weets) {
        try {
            leaLeaderService.export(response, obj, weets);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     *
     * 工作月报统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/reportCountList")
    @ResponseBody
    @ApiOperation(value = "工作月报统计列表接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "统计月", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "unSubmitSort", value = "未提交排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "submitSort", value = " 已提交排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query")})
    public Page<OffMonthReportCountDO> reportCountList(HttpServletRequest request, HttpServletResponse response,
        OffMonthReportVO obj, PageBean pageBean) {
        // List<OffMonthReportCountDO> offMonthReportCountDOList = null;
        Page<OffMonthReportCountDO> page = new Page<OffMonthReportCountDO>();
        try {
            page = leaLeaderService.reportCountList(obj, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     *
     * 工作月报统计首页.
     *
     * @return：List<AttSignInCount>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/reportCount")
    @ResponseBody
    @ApiOperation(value = "工作月报图表统计接口")
    public OffMonthReportCountDO reportCount(HttpServletRequest request, HttpServletResponse response) {
        // List<OffMonthReportCountDO> offMonthReportCountDOList = null;
        OffMonthReportCountDO offdo = new OffMonthReportCountDO();
        try {
            java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
            offdo = leaLeaderService.reportCount();
            BigDecimal unSubmit = new BigDecimal(offdo.getUnSubmitNumber());
            BigDecimal submit = new BigDecimal(offdo.getSubmitNumber());
            // 已提交/（已提交+未提交）*100%
            BigDecimal sum = submit.add(unSubmit);
            BigDecimal returnSubmit = new BigDecimal("0");
            if (sum.compareTo(new BigDecimal("0")) != 0) {
                returnSubmit = submit.divide(sum, 3, BigDecimal.ROUND_FLOOR);
                returnSubmit = returnSubmit.multiply(new BigDecimal(100));
            }
            String returnStr = df.format(returnSubmit);
            offdo.setFullName(returnStr);
        } catch (Exception e) {
            logger.error("", e);
        }
        return offdo;
    }

    /**
     *
     * 工作月报统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/reportCountExport", method = RequestMethod.GET)
    @ApiOperation(value = "工作月报统计导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "fullName", value = "主题", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "begTime", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "endTime", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "thisMonth", value = "本月", required = false, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "unSubmitSort", value = "未提交排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "submitSort", value = " 已提交排序(1:升序 其它：降序)", required = false, dataType = "string",
            paramType = "query")})
    public void reportCountExport(HttpServletRequest request, HttpServletResponse response, OffMonthReportVO obj) {
        try {
            leaLeaderService.export(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     *
     * 邮件图表统计列表.
     *
     * @return：Page<MaiSendBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiChartList")
    @ResponseBody
    @ApiOperation(value = "邮件图表统计列表接口")
    public List<MaiSendBoxCountDO> maiChartList(HttpServletRequest request, HttpServletResponse response,
        MaiSendBoxVO obj) {
        List<MaiSendBoxCountDO> maiCountList = new ArrayList<MaiSendBoxCountDO>();
        try {
            obj.setThisWeek("0");
            maiCountList = leaLeaderService.mailChartList(obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return maiCountList;
    }

    /**
     *
     * 审计流程统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/approvalCountList")
    @ResponseBody
    @ApiOperation(value = "工作流统计列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query")})
    public Page<ActApprovalVO> approvalCountList(HttpServletRequest request, HttpServletResponse response,
        ActWorkingListVO obj, PageBean pageBean) {
        Page<ActApprovalVO> page = new Page<ActApprovalVO>();
        try {
            page = leaLeaderService.getApprovalList(obj, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     *
     * 审计流程统计首页.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/approvalCount")
    @ResponseBody
    @ApiOperation(value = "审计流程统计首页")

    public List<ActApprovalVO> approvalCount(HttpServletRequest request, HttpServletResponse response,
        ActWorkingListVO obj) {
        List<ActApprovalVO> actWorkingVOList = new ArrayList<ActApprovalVO>();
        try {
            obj.setWeeks("0");
            actWorkingVOList = leaLeaderService.getApproval(obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return actWorkingVOList;
    }

    /**
     *
     * 审计流程统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/approvalCountExport", method = RequestMethod.GET)
    @ApiOperation(value = "审计流程统计导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query")})
    public void approvalCountExport(HttpServletRequest request, HttpServletResponse response, ActWorkingListVO obj) {
        try {
            leaLeaderService.exportApproval(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    // !-------------------------------
    /**
     *
     * 请假流程统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/holiCountList")
    @ResponseBody
    @ApiOperation(value = "请假流统计列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "startTimeholi", value = "开始统计时间", required = false, dataType = "date",
            paramType = "query"),
        @ApiImplicitParam(name = "endTimeholi", value = "结束统计时间", required = false, dataType = "date",
            paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "oderByThings", value = "事假", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "oderByDisease", value = "病假", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "oderByYear", value = "年假", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "oderByMarriage", value = "婚假", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "oderByMaternity", value = "产假", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "oderByAllocated", value = "公假", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByDie", value = "喪假", required = true, dataType = "string",
            paramType = "query")})
    public Page<ActHolidayListVO> holiCountList(HttpServletRequest request, HttpServletResponse response,
        ActHolidayVO obj, PageBean pageBean) {
        Page<ActHolidayListVO> page = new Page<ActHolidayListVO>();
        try {
            page = leaLeaderService.getHoliList(obj, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     *
     * 请假流程统计首页.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/holiCount")
    @ResponseBody
    @ApiOperation(value = "请假流程统计首页")

    public List<ActHolidayListVO> holilCount(HttpServletRequest request, HttpServletResponse response,
        ActHolidayVO obj) {
        ActHolidayListVO actWorkingVOList = new ActHolidayListVO();
        List<ActHolidayListVO> list = new ArrayList<ActHolidayListVO>();
        try {
            obj.setWeeks("0");
            actWorkingVOList = leaLeaderService.getHoli(obj);
            if (actWorkingVOList != null) {
                ActHolidayListVO vo = new ActHolidayListVO();
                vo.setUserName("其他假");
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setOtherLeave(actWorkingVOList.getOtherLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("公休假");
                vo.setOtherLeave(actWorkingVOList.getAllocatedLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("事假");
                vo.setOtherLeave(actWorkingVOList.getThingsLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("病假");
                vo.setOtherLeave(actWorkingVOList.getDiseaseLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("婚假");
                vo.setOtherLeave(actWorkingVOList.getMarriageLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("探亲假");
                vo.setOtherLeave(actWorkingVOList.getAnnualLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("生育假");
                vo.setOtherLeave(actWorkingVOList.getMaternityLeave());
                list.add(vo);
                vo = new ActHolidayListVO();
                vo.setShowDate(actWorkingVOList.getShowDate());
                vo.setUserName("丧假");
                vo.setOtherLeave(actWorkingVOList.getDieLeave());
                list.add(vo);
                boolean flag = true;
                while (flag) {
                    ActHolidayListVO tmpDo = list.get(0);
                    for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
                        for (int j = 0; j < list.size() - i - 1; j++) {
                            if (list.get(j + 1).getOtherLeave() > list.get(j).getOtherLeave()) {
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
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return list;
    }

    /**
     *
     * 审计流程统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/holiCountExport", method = RequestMethod.GET)
    @ApiOperation(value = "请假导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query")})
    public void holiCountExport(HttpServletRequest request, HttpServletResponse response, ActHolidayVO obj) {
        try {
            leaLeaderService.exportHoli(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     *
     * 工作监控统计列表.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/regulationCountList")
    @ResponseBody
    @ApiOperation(value = "工作监控统计列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "fullnameRegulation", value = "查询", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByNo", value = "数量排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderBySTH", value = "签收时间排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByPSTH", value = "平均签收时间排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByTotalTime", value = "办理时间排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByPTotalTime", value = "平均办理时间排序", required = true, dataType = "string",
            paramType = "query")})
    public Page<ActRegulationVO> regulationCountList(HttpServletRequest request, HttpServletResponse response,
        ActRegulationListVO obj, PageBean pageBean) {
        Page<ActRegulationVO> page = new Page<ActRegulationVO>();
        try {
            page = leaLeaderService.getRegulationList(obj, pageBean);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     *
     * 工作监控统计首页.
     *
     * @return：List<AttSignInCount>
     *
     * @author：HUANGW
     *
     * @date：2017-11-22
     */
    @RequestMapping(value = "/regulationCount")
    @ResponseBody
    @ApiOperation(value = "工作监控统计首页")

    public List<ActRegulationVO> regulationCount(HttpServletRequest request, HttpServletResponse response,
        ActRegulationListVO obj) {
        List<ActRegulationVO> actWorkingVOList = new ArrayList<ActRegulationVO>();
        try {
            obj.setWeeks("0");
            actWorkingVOList = leaLeaderService.getRegulation(obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return actWorkingVOList;
    }

    /**
     *
     * 工作监控统计导出.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/regulationCountExport", method = RequestMethod.GET)
    @ApiOperation(value = "工作监控导出接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "long", paramType = "query"),
        @ApiImplicitParam(name = "time1", value = "开始统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "time2", value = "结束统计时间", required = false, dataType = "date", paramType = "query"),
        @ApiImplicitParam(name = "weeks", value = "本周", required = true, dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "fullnameRegulation", value = "查询", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByNo", value = "数量排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderBySTH", value = "签收时间排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByPSTH", value = "平均签收时间排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByTotalTime", value = "办理时间排序", required = true, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "orderByPTotalTime", value = "平均办理时间排序", required = true, dataType = "string",
            paramType = "query")})
    public void regulationCountExport(HttpServletRequest request, HttpServletResponse response,
        ActRegulationListVO obj) {
        try {
            leaLeaderService.exportRegulation(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
