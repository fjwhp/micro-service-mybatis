package aljoin.app.controller.lea;

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
import aljoin.app.controller.BaseController;
import aljoin.app.object.MaiValListVO;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.ioa.dao.object.ActApprovalVO;
import aljoin.ioa.dao.object.ActRegulationListVO;
import aljoin.ioa.dao.object.ActRegulationVO;
import aljoin.ioa.dao.object.ActWorkingListVO;
import aljoin.ioa.dao.object.ActWorkingVO;
import aljoin.lea.iservice.LeaLeaderService;
import aljoin.mai.dao.object.MaiSendBoxCountDO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mee.dao.object.AppMeeMeetingCountDO;
import aljoin.mee.dao.object.MeeLeaderMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingCountDO;
import aljoin.mee.dao.object.MeeMeetingCountVO;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.off.dao.object.OffMonthReportCountDO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 领导看板(控制器).
 * 
 * @author：laijy
 * 
 * 				@date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/app/lea/leaShow", method = RequestMethod.POST)
@Api(value = "领导看板", description = "领导看板接口")
public class AppLeaController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(AppLeaController.class);
	@Resource
	private AutUserService autUserService;
	@Resource
	private AutUserPubService autUserPubService;
	@Resource
	private LeaLeaderService leaLeaderService;

	/**
	 * 
	 * 手机领导看板 工作月报
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/offLea")
	@ResponseBody
	@ApiOperation(value = "手机领导看板 工作月报")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg offLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			OffMonthReportCountDO offdo = new OffMonthReportCountDO();
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
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(offdo);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板请假统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/holiLea")
	@ResponseBody 
	@ApiOperation(value = "手机领导看板请假统计") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg holiLea(HttpServletRequest request, HttpServletResponse response) {
		Page<MaiValListVO> page = null;
		RetMsg retMsg = new RetMsg();
		ActHolidayVO obj = new ActHolidayVO();
		try {
			ActHolidayListVO actWorkingVOList = new ActHolidayListVO();
			obj.setWeeks("0");
			actWorkingVOList = leaLeaderService.getHoli(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(actWorkingVOList);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板工作流统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/workingLea")
	@ResponseBody 
	@ApiOperation(value = "手机领导看板工作流统计")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg workingLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		ActWorkingListVO obj = new ActWorkingListVO();
		obj.setWeeks("0");
		try {
			List<ActWorkingVO> actWorkingVOList = new ArrayList<ActWorkingVO>();
			actWorkingVOList = leaLeaderService.getWorking(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(actWorkingVOList);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板会议室统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/meeLea")
	@ResponseBody 
	@ApiOperation(value = "手机领导看板会议室统计")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg meeLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		AppMeeMeetingCountDO appMeeMeetingCountDO = new AppMeeMeetingCountDO();
		MeeMeetingCountDO countDo = new MeeMeetingCountDO();
		List<MeeLeaderMeetingRoomCountDO> roomList = new ArrayList<MeeLeaderMeetingRoomCountDO>();
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
			appMeeMeetingCountDO.setMeeMeetingCountDO(countDo);
			roomList = leaLeaderService.getMeetingRoom();
			appMeeMeetingCountDO.setMeeLeaderMeetingRoomCountDO(roomList);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(appMeeMeetingCountDO);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板邮件统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/mailLea")
	@ResponseBody
	@ApiOperation(value = "手机领导看板邮件统计") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg mailLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			List<MaiSendBoxCountDO> actWorkingVOList = new ArrayList<MaiSendBoxCountDO>();
			MaiSendBoxVO vo = new MaiSendBoxVO();
			vo.setThisWeek("0");
			actWorkingVOList = leaLeaderService.mailChartList(vo);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(actWorkingVOList);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板工作统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/regulationlLea")
	@ResponseBody
	@ApiOperation(value = "手机领导看板工作统计")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg regulationlLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			List<ActRegulationVO> actWorkingVOList = new ArrayList<ActRegulationVO>();
			ActRegulationListVO obj = new ActRegulationListVO();
			obj.setWeeks("0");
			actWorkingVOList = leaLeaderService.getRegulation(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(actWorkingVOList);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板流程统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/approvalLea")
	@ResponseBody
	@ApiOperation(value = "手机领导流程统计")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg approvalLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			List<ActApprovalVO> actWorkingVOList = new ArrayList<ActApprovalVO>();
			ActWorkingListVO obj = new ActWorkingListVO();
			obj.setWeeks("0");
			actWorkingVOList = leaLeaderService.getApproval(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(actWorkingVOList);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 手机领导看板考勤统计
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-11-29
	 */
	@RequestMapping("/attSignInLea")
	@ResponseBody 
	@ApiOperation(value = "手机考勤统计") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg attSignInLea(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {

			List<AttSignInCount> attSignInCount = new ArrayList<AttSignInCount>();
			AttSignInCount obj = new AttSignInCount();
			obj.setThisWeek("0");
			attSignInCount = leaLeaderService.getAttSignInCount(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(attSignInCount);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);

		}
		return retMsg;
	}

}
