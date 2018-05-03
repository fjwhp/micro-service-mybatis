package aljoin.app.controller.mee;

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

import aljoin.app.controller.BaseController;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.MeetScheduleDO;
import aljoin.off.dao.object.OffSchedulingDO;
import aljoin.off.iservice.OffSchedulingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 会议日程控制器
 *
 * @author：sln
 * 
 * @date：2017年11月7日 上午10:13:17
 */
@Controller
@RequestMapping(value = "app/mee/meeSchedule", method = RequestMethod.POST)
@Api(value = "移动端会议日程", description = "移动端->移动端会议日程")
public class MeetScheduleController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(MeetScheduleController.class);
	@Resource
	private OffSchedulingService offSchedulingService;
	@Resource
	private MeeInsideMeetingService meeInsideMeetingService;
	@Resource
	private MeeOutsideMeetingService meeOutsideMeetingService;

	/**
	 * 
	 * 月-有会议日程的日期
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/monthMeet")
	@ResponseBody // 返回json
	@ApiOperation(value = "会议日程(月会议)") // 接口文档对应名称

	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "month", value = "月份,格式：2017-10", required = true, dataType = "string", paramType = "query") })
	public RetMsg getMonthMeet(HttpServletRequest request, HttpServletResponse response, MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		List<String> scheduleList = new ArrayList<String>();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			OffSchedulingDO obj = new OffSchedulingDO();
			obj.setCreateUserId(userId);
			obj.setMonth(meet.getMonth());
			scheduleList = offSchedulingService.indexList4Month(obj);
			retMsg.setObject(scheduleList);
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App会议日程月日程查询错误", e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 当日会议列表
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/dayMeetList")
	@ResponseBody
	@ApiOperation(value = "会议日程(当日日程列表)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "date", value = "会议日期,格式：2017-10-13", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "biztype", value = "会议类型:in/out", required = false, dataType = "string", paramType = "query") })
	public RetMsg getDayMeetList(HttpServletRequest request, HttpServletResponse response, MeetScheduleDO obj) {
		RetMsg retMsg = new RetMsg();
		List<OffScheduling> scheduleList = new ArrayList<OffScheduling>();
		try {
			if (null != obj) {
				AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
				Long userId = autAppUserLogin.getUserId();
				OffSchedulingDO schedule = new OffSchedulingDO();
				schedule.setCreateUserId(userId);
				schedule.setBizType(obj.getBizType());
				schedule.setMonth(obj.getMonth());
				schedule.setMeetDay(obj.getDate());
				scheduleList = offSchedulingService.indexList4day(schedule);
				retMsg.setObject(scheduleList);
			}
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App会议日程查询错误", e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 内部会详情
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/insideMeetDetail")
	@ResponseBody
	@ApiOperation(value = "会议日程(内部会议详情)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键", required = true, dataType = "String", paramType = "query") })
	public RetMsg insideMeetDetail(HttpServletRequest request, HttpServletResponse response, MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		MeeInsideMeeting meeting = new MeeInsideMeeting();
		try {
			MeeInsideMeeting insideMeeting = new MeeInsideMeeting();
			insideMeeting.setId(meet.getBizId());
			meeting = meeInsideMeetingService.getDetail4App(insideMeeting);
			if (null != meeting) {
				retMsg.setObject(meeting);
			}
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App会议日程查询错误", e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 外部会详情
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/outsideMeetDetail")
	@ResponseBody
	@ApiOperation(value = "会议日程(外部会详情)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键", required = true, dataType = "String", paramType = "query") })
	public RetMsg outsideMeetDetail(HttpServletRequest request, HttpServletResponse response, MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		MeeOutsideMeeting meeting = new MeeOutsideMeeting();
		try {
			MeeOutsideMeeting outsideMeeting = new MeeOutsideMeeting();
			outsideMeeting.setId(meet.getBizId());
			meeting = meeOutsideMeetingService.getDetail4App(outsideMeeting);
			if (null != meeting) {
				retMsg.setObject(meeting);
			}
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App会议日程查询错误", e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 个人会议
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/personMeetingMap")
	@ResponseBody // 返回json
	@ApiOperation(value = "个人会议") // 接口文档对应名称

	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "date", value = "时间,格式：2017-10-13", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizType", value = "会议类型in/out", required = false, dataType = "string", paramType = "query"), })
	public RetMsg oneleaderList(HttpServletRequest request, HttpServletResponse response, MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		try {

			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			meet.setLeaderid(userId);
			retMsg = offSchedulingService.leaderMeetingMap(meet);
			// retMsg = offSchedulingService.leaderMeetingMap(meet);
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App个人会议查询错误", e);
		}
		return retMsg;
	}
}
