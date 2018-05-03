package aljoin.web.controller.mee;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.security.CustomUser;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.object.MeeInsideMeetingDO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeLeaderMeetingDO;
import aljoin.mee.dao.object.MeeLeaderMeetingRoomCountDO;
import aljoin.mee.iservice.MeeInsideMeetingService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.web.controller.BaseController;
import aljoin.web.service.mee.MeeInsideMeetingWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 内部会议表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/mee/meeInsideMeeting", method = RequestMethod.POST)
@Api(value = "内部会议Controller", description = "会议管理->内部会议相关接口")
public class MeeInsideMeetingController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(MeeInsideMeetingController.class);
	@Resource
	private MeeInsideMeetingService meeInsideMeetingService;
	@Resource
	private MeeInsideMeetingWebService meeInsideMeetingWebService;
	@Resource
	private WebTask webTask;
	private static final String MEET_ADD = "add";
	// private static final String MEET_UPDATE = "update";
	private static final String MEET_CANCEL = "cancel";

	/**
	 *
	 * 内部会议表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("内部会议室新增接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "contacts", value = "联系人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId", value = "会议室名称", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomName", value = "会议室名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "partyMemebersId", value = "参会人员ID(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "partyMemeberNames", value = "参会人员(多个分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "beginTime", value = "会议开始时间(格式为:2017-10-10 10:10)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "会议结束时间(格式为:2017-10-10 10:10)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "meetingContent", value = "会议内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMsg", value = "是否短信提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail", value = "是否邮件提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline", value = "是否在线提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "attendances", value = "出席人员 (多个分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	    public RetMsg add(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			if (null != customUser) {
				obj.setCreateUserId(customUser.getUserId());
			}
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			// webTask.meeInsideTask(obj,user);
			retMsg = meeInsideMeetingWebService.add(obj, user);
			if (retMsg.getCode() == 0) {
				MeeInsideMeeting meeting = (MeeInsideMeeting) retMsg.getObject();
				if (0 != obj.getIsWarnMail()) {
					MaiWriteVO mai = meeInsideMeetingService.mailWarn(meeting, user, MEET_ADD);
					webTask.maiTask(mai, user);
				}
				if (0 != obj.getIsWarnMsg()) {
					SmsShortMessage msg = meeInsideMeetingService.smaWarn(meeting, user, MEET_ADD);
					webTask.shortMsgAdd(msg,user);
				}
				if (0 != obj.getIsWarnOnline()) {
					AutMsgOnlineRequestVO msgRequest = meeInsideMeetingService.onlineWarn(meeting, user, MEET_ADD);
					webTask.pushMessageToUserList(msgRequest);
				}
			}
			/*
			 * retMsg.setCode(0); retMsg.setMessage("操作成功");
			 */
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 内部会议表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeInsideManagePage", method = RequestMethod.GET)
	@ApiOperation("内部会议页面")
	public String meeInsideMeetingPage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeInsideManagePage";
	}

	/**
	 *
	 * 内部会议表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation(value = "内部会议列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId", value = "会议室ID（会议室下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingSituation", value = "使用情况（下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query")

	})
	public Page<MeeInsideMeetingDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			MeeInsideMeetingVO obj) {
		Page<MeeInsideMeetingDO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			page = meeInsideMeetingService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 内部会议表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MeeInsideMeeting obj) {
		RetMsg retMsg = new RetMsg();

		meeInsideMeetingService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 内部会议变更.
	 *
	 * 			@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("内部会议变更")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "contacts", value = "联系人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId", value = "会议室名称", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomName", value = "会议室名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "partyMemebersId", value = "参会人员ID(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "partyMemeberNames", value = "参会人员(多个分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "beginTime", value = "会议开始时间(格式为:2017-10-10 10:10)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "会议结束时间(格式为:2017-10-10 10:10)", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "meetingContent", value = "会议内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMsg", value = "是否短信提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail", value = "是否邮件提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline", value = "是否在线提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "attendances", value = "出席人员 (多个分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[0].id", value = "资源id)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = meeInsideMeetingWebService.update(obj, user);
			if (retMsg.getCode() == 0) {
				MeeInsideMeeting meeInsideMeeting = (MeeInsideMeeting) retMsg.getObject();
				if (StringUtils.isNotEmpty(obj.getNewMember())) {
					meeInsideMeeting.setPartyMemebersId(obj.getNewMember());
					meeInsideMeeting.setPartyMemeberNames(obj.getNewMemberName());
					if (0 != obj.getIsWarnMail()) {
						MaiWriteVO mai = meeInsideMeetingService.mailWarn(meeInsideMeeting, user, MEET_ADD);
						webTask.maiTask(mai, user);
					}
					if (0 != obj.getIsWarnMsg()) {
						SmsShortMessage msg = meeInsideMeetingService.smaWarn(meeInsideMeeting, user, MEET_ADD);
						webTask.shortMsgAdd(msg,user);
					}
					if (0 != obj.getIsWarnOnline()) {
						AutMsgOnlineRequestVO msgRequest = meeInsideMeetingService.onlineWarn(meeInsideMeeting, user,
								MEET_ADD);
						webTask.pushMessageToUserList(msgRequest);
					}
				}
				if (StringUtils.isNotEmpty(obj.getDelMember())) {
					meeInsideMeeting.setPartyMemebersId(obj.getDelMember());
					meeInsideMeeting.setPartyMemeberNames(obj.getDelMemberName());
					if (0 != obj.getIsWarnMail()) {
						MaiWriteVO mai = meeInsideMeetingService.mailWarn(meeInsideMeeting, user, MEET_CANCEL);
						webTask.maiTask(mai, user);
					}
					if (0 != obj.getIsWarnMsg()) {
						SmsShortMessage msg = meeInsideMeetingService.smaWarn(meeInsideMeeting, user, MEET_CANCEL);
						webTask.shortMsgAdd(msg,user);
					}
					if (0 != obj.getIsWarnOnline()) {
						AutMsgOnlineRequestVO msgRequest = meeInsideMeetingService.onlineWarn(meeInsideMeeting, user,
								MEET_CANCEL);
						webTask.pushMessageToUserList(msgRequest);
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 内部会议表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("内部会议详情")
	@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "long", paramType = "query")
	public MeeInsideMeetingVO getById(HttpServletRequest request, HttpServletResponse response, MeeInsideMeeting obj) {
		MeeInsideMeetingVO meeInsideMeetingVO = null;
		try {
			meeInsideMeetingVO = meeInsideMeetingService.detail(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return meeInsideMeetingVO;
	}

	/**
	 *
	 * 我的内部会议表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/personList")
	@ResponseBody
	@ApiOperation(value = "我的内部会议列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId", value = "会议室ID（会议室下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingSituation", value = "使用情况（下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query")

	})
	public Page<MeeInsideMeetingDO> personList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, MeeInsideMeetingVO obj) {
		Page<MeeInsideMeetingDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user && null != user.getUserId()) {
				obj.setCreateUserId(user.getUserId());
			}
			page = meeInsideMeetingService.personList(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 内部会议取消.
	 *
	 * 			@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/cancel")
	@ResponseBody
	@ApiOperation("内部会议取消")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "isWarnMsg", value = "是否短信提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail", value = "是否邮件提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline", value = "是否在线提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "long", paramType = "query") })
	public RetMsg cancel(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = meeInsideMeetingWebService.cancel(obj, user);
			if (retMsg.getCode() == 0) {
				MeeInsideMeeting meeInsideMeeting = (MeeInsideMeeting) retMsg.getObject();
				if (0 != obj.getIsWarnMail()) {
					MaiWriteVO mai = meeInsideMeetingService.mailWarn(meeInsideMeeting, user, MEET_CANCEL);
					webTask.maiTask(mai, user);
				}
				if (0 != obj.getIsWarnMsg()) {
					SmsShortMessage msg = meeInsideMeetingService.smaWarn(meeInsideMeeting, user, MEET_CANCEL);
					webTask.shortMsgAdd(msg,user);
				}
				if (0 != obj.getIsWarnOnline()) {
					AutMsgOnlineRequestVO msgRequest = meeInsideMeetingService.onlineWarn(meeInsideMeeting, user,
							MEET_CANCEL);
					webTask.pushMessageToUserList(msgRequest);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 领导内部会议.
	 *
	 * 			@return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/leaderList")
	@ResponseBody
	@ApiOperation(value = "内部会议列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "leader", value = "领导人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "thisMonth", value = "本月", required = false, dataType = "date", paramType = "query") })
	public MeeLeaderMeetingRoomCountDO leaderList(HttpServletRequest request, HttpServletResponse response,
			MeeInsideMeetingVO obj) {
		MeeLeaderMeetingRoomCountDO meetingRoomCountDO = null;
		try {
			meetingRoomCountDO = meeInsideMeetingService.leaderList(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return meetingRoomCountDO;
	}

	/**
	 *
	 * 我的内部会议表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeMineManagePage", method = RequestMethod.GET)
	@ApiOperation("我的内部会议页面")
	public String metMineManagePage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeMineManagePage";
	}

	/**
	 *
	 * 内部会详情(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeInsideManagePageDetail", method = RequestMethod.GET)
	@ApiOperation("内部会议详情页面")
	public String meeInsideManagePageDetail(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String isDraft = request.getParameter("isDraft");

		request.setAttribute("t_id", id);
		request.setAttribute("t_isDraft", isDraft);

		return "mee/meeInsideManagePageDetail";
	}

	/**
	 *
	 * 内部会议申请管理(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeInAppayManagePage", method = RequestMethod.GET)
	@ApiOperation("内部会议申请管理页面")
	public String metInAppayManagePage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeInAppayManagePage";
	}

	/**
	 *
	 * 领导会议(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeleaderPage", method = RequestMethod.GET)
	@ApiOperation("领导会议页面")
	public String meeleaderPage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeleaderPage";
	}

	/**
	 *
	 * 领导内部会议.
	 *
	 * 			@return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/leaderCount")
	@ResponseBody
	@ApiOperation(value = "内部会议列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "leader", value = "领导人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "thisWeek", value = "本周", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "thisMonth", value = "本月", required = false, dataType = "date", paramType = "query") })
	public List<MeeLeaderMeetingDO> leaderCount(HttpServletRequest request, HttpServletResponse response,
			MeeInsideMeetingVO obj) {
		List<MeeLeaderMeetingDO> list = null;
		try {
			list = meeInsideMeetingService.leaderCount(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 领导会议分页列表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/leaderPersonList")
	@ResponseBody
	@ApiOperation(value = "领导会议分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),

	})
	public Page<MeeInsideMeetingDO> leaderPersonList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, MeeLeaderMeetingDO obj) {
		Page<MeeInsideMeetingDO> page = null;
		try {
			page = meeInsideMeetingService.leaderPersonList(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 内部会议表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/insideMeetApplylist")
	@ResponseBody
	@ApiOperation(value = "内部会议申请列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId", value = "会议室ID（会议室下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingSituation", value = "使用情况（下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query")

	})
	public Page<MeeInsideMeetingDO> insideMeetApplylist(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, MeeInsideMeetingVO obj) {
		Page<MeeInsideMeetingDO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			page = meeInsideMeetingService.managerlist(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 领导会议分页列表(分页列表).
	 *
	 * @return：Page<MeeInsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@SuppressWarnings("unused")
	@RequestMapping(value = "/pushMessge", method = RequestMethod.GET)
	@ApiOperation(value = "内部会议申请列表")
	public void pushMessge(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			MeeLeaderMeetingDO obj) {
		Page<MeeInsideMeetingDO> page = null;
		try {
			logger.info("开始调用");
			meeInsideMeetingService.pushInsideMeeting();
			logger.info("结束调用");
		} catch (Exception e) {
			logger.error("", e);
		}
	}
}
