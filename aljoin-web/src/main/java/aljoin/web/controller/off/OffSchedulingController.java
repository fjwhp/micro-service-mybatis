package aljoin.web.controller.off;

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

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.security.CustomUser;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.OffSchedulingDO;
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import aljoin.web.service.off.OffSchedulingWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 日程安排表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-01
 */
@Controller
@RequestMapping(value = "/off/offScheduling", method = RequestMethod.POST)
@Api(value = "日程安排controller", description = "工作计划->日程安排相关接口")
public class OffSchedulingController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(OffSchedulingController.class);
	@Resource
	private OffSchedulingService offSchedulingService;
	@Resource
	private OffSchedulingWebService offSchedulingWebService;
	@Resource
	private WebTask webTask;

	/**
	 * 
	 * 日程安排表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping(value = "/offSchedulingPage", method = RequestMethod.GET)
	@ApiOperation(value = "日程安排页面接口")
	public String offSchedulingPage(HttpServletRequest request, HttpServletResponse response) {
		return "off/offSchedulingPage";
	}

	/**
	 * 
	 * 日程安排表(分页列表).
	 *
	 * @return：Page<OffScheduling>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@FuncObj(desc = "[工作计划]-[日程安排]-[搜索]")
	@ApiOperation(value = "日程安排分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "scheduleStartDate", value = "开始日期", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "scheduleEtartDate", value = "结束日期", required = false, dataType = "string", paramType = "query") })
	public List<OffScheduling> list(HttpServletRequest request, HttpServletResponse response, OffSchedulingVO obj) {
		List<OffScheduling> offScheduling = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			offScheduling = offSchedulingService.list(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return offScheduling;
	}

	/**
	 * 
	 * 日程安排表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[工作计划]-[日程安排]-[新增]")
	@ApiOperation(value = "日程安排新增接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "theme", value = "主题", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "place", value = "地点", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDateStr", value = "开始时间", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDateStr", value = "结束时间", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "content", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sharedPersonId", value = "被共享者ID(多个用分号分隔)", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "sharedPersonName", value = "被共享者(多个用分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMsg", value = "在线短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail", value = "邮件提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline", value = "在线提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, OffSchedulingVO obj) {
		RetMsg retMsg = new RetMsg();
		CustomUser customUser = getCustomDetail();
		try {
			if (null != customUser) {
				obj.setCreateUserId(customUser.getUserId());
			}
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			
			retMsg = offSchedulingWebService.add(obj, user);
			if (retMsg.getCode() == 0) {
				OffScheduling offScheduling = (OffScheduling) retMsg.getObject();
				if(offScheduling.getType() != 2){
					if (obj.getIsWarnMail() == 1 && obj.getType() != 3) {// 邮件提醒
						MaiWriteVO mai = offSchedulingWebService.maiWarn(offScheduling, user);
						webTask.maiTask(mai, user);
					}
					if (obj.getIsWarnMsg() == 1 && obj.getType() != 3) {// 短信提醒
						SmsShortMessage msg = offSchedulingWebService.smaWarn(offScheduling, user);
						webTask.shortMsgAdd(msg,user);
					}
					if (obj.getIsWarnOnline() == 1 && obj.getType() != 3) {// 在线短信提醒
						AutMsgOnlineRequestVO msgRequest = offSchedulingWebService.onlineWarn(offScheduling, user);
						webTask.pushMessageToUserList(msgRequest);
					}
				}
			}
			// retMsg.setCode(0);
			// retMsg.setMessage("操作成功");

		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 日程安排表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[工作计划]-[日程安排]-[删除]")
	@ApiOperation(value = "日程安排删除接口")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, OffScheduling obj) {
		RetMsg retMsg = new RetMsg();

		try {
			retMsg = offSchedulingService.delete(obj);
		} catch (Exception e) {
			retMsg.setCode(0);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}

		return retMsg;
	}

	/**
	 * 
	 * 日程安排表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[工作计划]-[日程安排]-[修改]")
	@ApiOperation(value = "日程安排修改接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "theme", value = "主题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "place", value = "地点", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDateStr", value = "开始时间", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "startHourMinStr", value = "开始小时", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "content", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sharedPersonId", value = "被共享者ID(多个用分号分隔)", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "sharedPersonName", value = "被共享者(多个用分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMsg", value = "在线短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail", value = "邮件提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline", value = "在线提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, OffSchedulingVO obj) {
		RetMsg retMsg = new RetMsg();
		CustomUser customUser = getCustomDetail();
		try {
			if (null != customUser) {
				obj.setCreateUserId(customUser.getUserId());
			}
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = offSchedulingWebService.update(obj, user);
			if (retMsg.getCode() == 0) {
              OffScheduling offScheduling = (OffScheduling) retMsg.getObject();
              if(offScheduling.getType() != 2){
                  if (obj.getIsWarnMail() != null && obj.getIsWarnMail() == 1 && obj.getType() != 3) {// 邮件提醒
                      MaiWriteVO mai = offSchedulingWebService.maiWarn(offScheduling, user);
                      webTask.maiTask(mai, user);
                  }
                  if (obj.getIsWarnMsg() != null && obj.getIsWarnMsg() == 1 && obj.getType() != 3) {// 短信提醒
                      SmsShortMessage msg = offSchedulingWebService.smaWarn(offScheduling, user);
                      webTask.shortMsgAdd(msg,user);
                  }
                  if (obj.getIsWarnOnline() != null && obj.getIsWarnOnline() == 1 && obj.getType() != 3) {// 在线短信提醒
                      AutMsgOnlineRequestVO msgRequest = offSchedulingWebService.onlineWarn(offScheduling, user);
                      webTask.pushMessageToUserList(msgRequest);
                  }
              }
          }
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 日程安排表(根据ID获取对象).
	 *
	 * @return：OffScheduling
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[工作计划]-[日程安排]-[详情]")
	@ApiOperation(value = "日程安排详情接口")
	@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "int", paramType = "query")
	public OffScheduling getById(HttpServletRequest request, HttpServletResponse response, OffSchedulingVO obj) {
		OffSchedulingVO offscheduling = null;
		CustomUser user = getCustomDetail();
		try {
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			offscheduling = offSchedulingService.detail(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return offscheduling;
	}

	/**
	 * 
	 * 日程安排表(首页-月).
	 *
	 * 					@return：List<String>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/monthMeet")
	@ResponseBody
	@FuncObj(desc = "[首页]-[日程]-[月日程]")
	@ApiOperation(value = "首页月日程接口")
	@ApiImplicitParam(name = "month", value = "月份", required = true, dataType = "string", paramType = "query")
	public List<String> monthMeet(HttpServletRequest request, HttpServletResponse response, OffSchedulingDO obj) {
		List<String> list = null;
		CustomUser user = getCustomDetail();
		try {
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			list = offSchedulingService.indexList4Month(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 * 
	 * 日程安排表(首页-日).
	 *
	 * 					@return：List<OffScheduling>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/dateMeet")
	@ResponseBody
	@FuncObj(desc = "[首页]-[日程]-[当日会议列表]")
	@ApiOperation(value = "首页会议列表接口")
	@ApiImplicitParam(name = "meetDay", value = "日期", required = true, dataType = "string", paramType = "query")
	public List<OffScheduling> dateMeet(HttpServletRequest request, HttpServletResponse response, OffSchedulingDO obj) {
		List<OffScheduling> list = null;
		CustomUser user = getCustomDetail();
		try {
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			list = offSchedulingService.indexList4day(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 * 
	 * 日程安排表(导出).
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "导出接口")
	@FuncObj(desc = "[个人办公]-[日程安排]-[导出]")
	public void export(HttpServletRequest request, HttpServletResponse response, OffSchedulingVO obj) {
		try {
			CustomUser user = getCustomDetail();
			if (null != user) {
				obj.setCreateUserId(user.getUserId());
			}
			offSchedulingService.export(response, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 * 
	 * 日程安排表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping(value = "/offSchedulingDetailPage", method = RequestMethod.GET)
	@ApiOperation(value = "日程安排详情页面接口")
	public String offSchedulingDetailPage(HttpServletRequest request, HttpServletResponse response) {
		return "off/offSchedulingDetailPage";
	}
}
