package aljoin.web.controller.mee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeLeaderMeetingDO;
import aljoin.mee.dao.object.MeeMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeOutsideMeetingDO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.web.controller.BaseController;
import aljoin.web.service.mee.MeeOutsideMeetingWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 外部会议表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/mee/meeOutsideMeeting", method = RequestMethod.POST)
@Api(value = "外部会议Controller", description = "会议管理->外部会议管理相关接口")
public class MeeOutsideMeetingController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(MeeMeetingRoomController.class);
	@Resource
	private MeeOutsideMeetingService meeOutsideMeetingService;
	@Resource
	private MeeOutsideMeetingWebService meeOutsideMeetingWebService;
	@Resource
	private WebTask webTask;
	@Resource
	private AutUserService autUserService;

	private static final String MEET_ADD = "add";
	private static final String MEET_CANCEL = "cancel";

	/**
	 * 
	 * 外部会议表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeOutsideManagePage", method = RequestMethod.GET)
	@ApiOperation("外部会议页面")
	public String meeOutsideMeetingPage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeOutsideManagePage";
	}

	/**
	 *
	 * 外部会详情(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeOutsideManagePageDetail", method = RequestMethod.GET)
	@ApiOperation("外部会议详情页面")
	public String meeOutsideManagePageDetail(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String isDraft = request.getParameter("isDraft");

		request.setAttribute("t_id", id);
		request.setAttribute("t_isDraft", isDraft);

		return "mee/meeOutsideManagePageDetail";
	}

	/**
	 * 
	 * 外部会议表(分页列表).
	 *
	 * @return：Page<MeeOutsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation("外部会议分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "auditStatus", value = "审核状态", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingSituation", value = "使用情况（下拉框）", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query")

	})
	public Page<MeeOutsideMeetingDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			MeeOutsideMeetingVO obj) {
		Page<MeeOutsideMeetingDO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			page = meeOutsideMeetingService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 外部会议表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("外部会议新增接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "contacts", value = "联系人", required = false, dataType = "string", paramType = "query"),
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
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			if (null != user && null != user.getUserId()) {
				obj.setCreateUserId(user.getUserId());
			}
			retMsg = meeOutsideMeetingWebService.add(obj,user);
//			webTask.meeOutsideTask(obj, user);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 外部会议表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeeting obj) {
		RetMsg retMsg = new RetMsg();

		meeOutsideMeetingService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 外部会议变更
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("外部会议变更接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "主键", required = false, dataType = "long", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "meetingHost", value = "主持人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "contacts", value = "联系人", required = false, dataType = "string", paramType = "query"),
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
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setId(customUser.getUserId());
			user.setFullName(customUser.getNickName());
			user.setUserName(customUser.getUsername());
			retMsg = meeOutsideMeetingWebService.update(obj, user);
			if (retMsg.getCode() == 0) {
				MeeOutsideMeeting meeOutsideMeeting = (MeeOutsideMeeting) retMsg.getObject();
				if (StringUtils.isNotEmpty(obj.getNewMember())) {
					meeOutsideMeeting.setPartyMemebersId(obj.getNewMember());
					meeOutsideMeeting.setPartyMemeberNames(obj.getNewMemberName());
					// 提醒
					if (0 != meeOutsideMeeting.getIsWarnMail()) {
						MaiWriteVO mai = meeOutsideMeetingService.mailWarn(meeOutsideMeeting, user, MEET_ADD);
						webTask.maiTask(mai, user);
					}
					if (0 != meeOutsideMeeting.getIsWarnMsg()) {
						SmsShortMessage msg = meeOutsideMeetingService.smaWarn(meeOutsideMeeting, user, MEET_ADD);
						webTask.shortMsgAdd(msg,user);
					}
					if (0 != meeOutsideMeeting.getIsWarnOnline()) {
						AutMsgOnlineRequestVO msgRequest = meeOutsideMeetingService.onlineWarn(meeOutsideMeeting, user,
								MEET_ADD);
						webTask.pushMessageToUserList(msgRequest);
					}
				}
				if (StringUtils.isNotEmpty(obj.getDelMember())) {
					meeOutsideMeeting.setPartyMemebersId(obj.getDelMember());
					meeOutsideMeeting.setPartyMemeberNames(obj.getDelMember());
					// 提醒
					if (0 != meeOutsideMeeting.getIsWarnMail()) {
						MaiWriteVO mai = meeOutsideMeetingService.mailWarn(meeOutsideMeeting, user, MEET_CANCEL);
						webTask.maiTask(mai, user);
					}
					if (0 != meeOutsideMeeting.getIsWarnMsg()) {
						SmsShortMessage msg = meeOutsideMeetingService.smaWarn(meeOutsideMeeting, user, MEET_CANCEL);
						webTask.shortMsgAdd(msg,user);
					}
					if (0 != meeOutsideMeeting.getIsWarnOnline()) {
						AutMsgOnlineRequestVO msgRequest = meeOutsideMeetingService.onlineWarn(meeOutsideMeeting, user,
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
	 * 外部会议表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("外部会议详情接口")
	@ApiImplicitParam(name = "id", value = "主键", required = false, dataType = "long", paramType = "query")
	public MeeOutsideMeetingVO getById(HttpServletRequest request, HttpServletResponse response,
			MeeOutsideMeeting obj) {
		MeeOutsideMeetingVO meeOutsideMeetingVO = null;
		try {
			meeOutsideMeetingVO = meeOutsideMeetingService.detail(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return meeOutsideMeetingVO;
	}

	/**
	 *
	 * 我的外部会议表(分页列表).
	 *
	 * @return：Page<MeeOutsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/personList")
	@ResponseBody
	@ApiOperation("我的外部会议分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle", value = "会议标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "address", value = "地址", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query")

	})
	public Page<MeeOutsideMeetingDO> personList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, MeeOutsideMeetingVO obj) {
		Page<MeeOutsideMeetingDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user && null != user.getUserId()) {
				obj.setCreateUserId(user.getUserId());
			}
			page = meeOutsideMeetingService.personList(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 外部会议取消
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/cancel")
	@ResponseBody
	@ApiOperation("外部会议取消接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "isWarnMsg", value = "是否短信提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail", value = "是否邮件提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline", value = "是否在线提醒（0：否 1：是）", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "long", paramType = "query") })
	public RetMsg cancel(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = meeOutsideMeetingWebService.cancel(obj, user);
			if (retMsg.getCode() == 0) {
				MeeOutsideMeeting meeOutsideMeeting = (MeeOutsideMeeting) retMsg.getObject();
				if (0 != obj.getIsWarnMail()) {
					MaiWriteVO mai = meeOutsideMeetingService.mailWarn(meeOutsideMeeting, user, MEET_CANCEL);
					webTask.maiTask(mai, user);
				}
				if (0 != obj.getIsWarnMsg()) {
					SmsShortMessage msg = meeOutsideMeetingService.smaWarn(meeOutsideMeeting, user, MEET_CANCEL);
					webTask.shortMsgAdd(msg,user);
				}
				if (0 != obj.getIsWarnOnline()) {
					AutMsgOnlineRequestVO msgRequest = meeOutsideMeetingService.onlineWarn(meeOutsideMeeting, user,
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
	 * 外部领导会议.
	 *
	 * 			@return：Page<MeeOutsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/leaderList")
	@ResponseBody
	@ApiOperation("外部领导会议接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "leader", value = "领导人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query")

	})
	public MeeMeetingRoomCountDO leaderList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			MeeInsideMeetingVO obj) {
		MeeMeetingRoomCountDO meeMeetingRoomDO = null;
		try {
			meeMeetingRoomDO = meeOutsideMeetingService.leaderList(obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return meeMeetingRoomDO;
	}

	/**
	 *
	 * 我的外部会议表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeOutMineManagePage", method = RequestMethod.GET)
	@ApiOperation("我的外部会议")
	public String metOutMineManagePage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeOutMineManagePage";
	}

	/**
	 *
	 * 我的外部会议表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeOutAppayManagePage", method = RequestMethod.GET)
	@ApiOperation("外部会议申请页面")
	public String metOutAppayManagePage(HttpServletRequest request, HttpServletResponse response) {

		return "mee/meeOutAppayManagePage";
	}

	/**
	 *
	 * 我的外部会议审核分页列表.
	 *
	 * 					@return：Page<MeeOutsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/auditList")
	@ResponseBody
	@ApiOperation("我的外部会议审核分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "startDate", value = "开始日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endDate", value = "结束日期", required = false, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "auditStatus", value = "审核状态(1:审核中 2：审核失败 3：审核通过)", required = true, dataType = "int", paramType = "query"),

	})
	public Page<MeeOutsideMeetingDO> auditList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, MeeOutsideMeetingVO obj) {
		Page<MeeOutsideMeetingDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if (null != user && null != user.getUserId()) {
				obj.setCreateUserId(user.getUserId());
			}
			page = meeOutsideMeetingService.auditlist(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 审批
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月7日 下午2:04:43
	 */
	@RequestMapping("/completeTask")
	@ResponseBody
	@ApiOperation("完成任务")
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables,
			String taskId, String bizId, String userId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = meeOutsideMeetingWebService.completeTask(variables, taskId, bizId, userId, user, message);
			if (retMsg.getCode() == 0) {
				//跨环节，发送在线消息给下一环节办理人
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, user);
				}
				//流程结束，消息提醒
				if(map.get("isEnd") != null){
					MeeOutsideMeeting outsideMeeting = meeOutsideMeetingService.selectById(Long.parseLong(bizId));
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.setSqlSelect("id","full_name","user_name");
					userWhere.eq("id", outsideMeeting.getCreateUserId());
					AutUser user1 = autUserService.selectOne(userWhere);
					
					if (0 != outsideMeeting.getIsWarnMsg()) {
						SmsShortMessage msg = meeOutsideMeetingService.smaWarn(outsideMeeting, user1, MEET_ADD);
						webTask.shortMsgAdd(msg,user1);
					}
					if (0 != outsideMeeting.getIsWarnOnline()) {
						AutMsgOnlineRequestVO msgRequest = meeOutsideMeetingService.onlineWarn(outsideMeeting, user1,
								MEET_ADD);
						webTask.pushMessageToUserList(msgRequest);
					}
					if (0 != outsideMeeting.getIsWarnMail()) {
						MaiWriteVO mai = meeOutsideMeetingService.mailWarn(outsideMeeting, user1, MEET_ADD);
						webTask.maiTask(mai, user1);
					}
				}
				retMsg.setObject(null);
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
	 * 检查当前节点下一个节点是显示组织机构还是人员
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/checkNextTaskInfo")
	@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")
	@ResponseBody
	@ApiOperation("检查当前节点下一个节点是显示组织机构还是人员")
	public RetMsg checkNextTaskInfo(String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeOutsideMeetingService.checkNextTaskInfo(taskId);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value = "/getAllTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllTaskInfo2(String taskId, String processName, String processInstanceId) {
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = meeOutsideMeetingService.getAllTaskInfo(taskId, processInstanceId);
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 回退
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/jump2Task2")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "业务主键ID(多个用分分隔)", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("回退")
	public RetMsg jump2Task2(String taskId, String bizId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = meeOutsideMeetingWebService.jump2Task2(taskId, bizId, message, user.getUserId());
			if (retMsg.getObject() != null) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if (handle != null && StringUtils.isNotEmpty(handle)) {// 当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					AutUser autUser = new AutUser();
					autUser.setId(user.getUserId());
					autUser.setFullName(user.getNickName());
					autUser.setUserName(user.getUsername());
					webTask.sendOnlineMsg(processInstanceId, handle, autUser);
				}
				retMsg.setObject(null);
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
	 * 我的外部会议表(分页列表).
	 *
	 * @return：Page<MeeOutsideMeeting>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/leaderPersonList")
	@ResponseBody
	@ApiOperation("我的外部会议分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),

	})
	public Page<MeeOutsideMeetingDO> leaderPersonList(HttpServletRequest request, HttpServletResponse response,
			PageBean pageBean, MeeLeaderMeetingDO obj) {
		Page<MeeOutsideMeetingDO> page = null;
		try {
			page = meeOutsideMeetingService.leaderPersonList(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 外部会议流程作废.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/void")
	@ResponseBody
	@ApiOperation("外部会议流程作废")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId, String bizId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeOutsideMeetingService.toVoid(taskId, bizId, getCustomDetail().getUserId());
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：HUANGW
	 *
	 * @date：2017年12月07日 下午5:34:17
	 */
	@RequestMapping(value = "/getAllHisTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processInstanceId", value = "实例ID", required = true, dataType = "string", paramType = "query") })
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllHisTaskInfo(String processInstanceId) {
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			// list =
			// meeOutsideMeetingService.getAllHisTaskInfo(processInstanceId);
			list = meeOutsideMeetingService.getAllTaskInfo(null, processInstanceId);
			if (list != null && list.size() > 0) {
				boolean flag = true;
				while (flag) {
					FixedFormProcessLog tmpDo = list.get(0);
					for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
						for (int j = 0; j < list.size() - i - 1; j++) {
							if (list.get(j + 1).getOperationTime().getTime() < list.get(j).getOperationTime()
									.getTime()) {
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
			
				FixedFormProcessLog tmplog=list.get(list.size()-1);
				tmplog.setDirection(tmplog.getDirection()+" ----> 归档");
				list.set(list.size()-1, tmplog);	
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return list;
	}
	/**
	 * 
	 * 外部会议推送
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月21日 下午4:56:34
	 */
	@SuppressWarnings("unused")
    @RequestMapping(value = "/pushMessge", method = RequestMethod.GET)
    @ApiOperation(value = "外部会议申请列表")
    public void pushMessge(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
            MeeLeaderMeetingDO obj) {
        Page<MeeOutsideMeetingDO> page = null;
        try {
            logger.info("开始调用");
            meeOutsideMeetingService.pushOutsideMeeting();
            logger.info("结束调用");
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
