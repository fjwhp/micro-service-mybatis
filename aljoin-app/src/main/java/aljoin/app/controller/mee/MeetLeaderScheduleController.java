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
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.MeetScheduleDO;
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
@RequestMapping(value = "app/mee/meeLearderSchedule", method = RequestMethod.POST)
@Api(value = "移动端会议日程", description = "移动端->移动端领导会议")
public class MeetLeaderScheduleController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(MeetLeaderScheduleController.class);
	@Resource
	private OffSchedulingService offSchedulingService;
	
	/**
	 * 
	 * 领导会议（页面一打开请求）
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/leaderList")
	@ResponseBody // 返回json		
	@ApiOperation(value = "领导会议（页面一打开请求）") // 接口文档对应名称
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "month", value = "月份,格式：2017-10", required = true, dataType = "string",paramType = "query")
	})
	public RetMsg leaderList(HttpServletRequest request, HttpServletResponse response,MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		try{
			retMsg = offSchedulingService.leaderScheduleMap(meet);
		}catch (Exception e) {
		     retMsg.setCode(AppConstant.RET_CODE_ERROR);
		     retMsg.setMessage(e.getMessage());
			 logger.error("App领导会议查询错误",e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 领导会议（点击某位领导或月份请求）
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/leaderMonthList")
	@ResponseBody // 返回json		
	@ApiOperation(value = "领导会议（点击切换月份请求）") // 接口文档对应名称
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "month", value = "月份,格式：2017-10", required = true, dataType = "string",paramType = "query"),
	})
	public RetMsg leaderMonthList(HttpServletRequest request, HttpServletResponse response,MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		List<String> list  = new ArrayList<String>();
		try{
			list = offSchedulingService.leadMonthList(meet, null);
			retMsg.setObject(list);
		}catch (Exception e) {
		     retMsg.setCode(AppConstant.RET_CODE_ERROR);
		     retMsg.setMessage(e.getMessage());
			 logger.error("App领导会议查询错误",e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 领导会议（点击某天请求）
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/leaderDayList")
	@ResponseBody // 返回json		
	@ApiOperation(value = "领导会议（点击某天请求）") // 接口文档对应名称
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "date", value = "时间,格式：2017-10-13", required = true, dataType = "string",paramType = "query"),
	})
	public RetMsg leaderDayList(HttpServletRequest request, HttpServletResponse response,MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		try{
			List<OffScheduling> list = offSchedulingService.leadDayList(meet, null);
			retMsg.setObject(list);
		}catch (Exception e) {
		     retMsg.setCode(AppConstant.RET_CODE_ERROR);
		     retMsg.setMessage(e.getMessage());
			 logger.error("App领导会议查询错误",e);
		}
		retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 领导会议（点击某位领导请求）
	 *
	 * @author：sln
	 * 
	 * @date：2017年11月7日 上午10:13:17
	 */
	@RequestMapping("/leaderMeetingMap")
	@ResponseBody // 返回json		
	@ApiOperation(value = "领导会议（当月+当日会议列表）") // 接口文档对应名称
	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "date", value = "时间,格式：2017-10-13", required = false, dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "bizType", value = "会议类型in/out", required = false, dataType = "string",paramType = "query"),
	})
	public RetMsg oneleaderList(HttpServletRequest request, HttpServletResponse response,MeetScheduleDO meet) {
		RetMsg retMsg = new RetMsg();
		try{
			retMsg = offSchedulingService.leaderMeetingMap(meet);
		}catch (Exception e) {
		     retMsg.setCode(AppConstant.RET_CODE_ERROR);
		     retMsg.setMessage(e.getMessage());
			 logger.error("App领导会议查询错误",e);
		}
		return retMsg;
	}
}
