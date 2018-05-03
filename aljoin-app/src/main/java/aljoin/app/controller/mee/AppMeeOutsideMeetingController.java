package aljoin.app.controller.mee;

import java.util.HashMap;
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

import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.iservice.MeeOutsideMeetingService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.web.service.mee.MeeOutsideMeetingWebService;
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
@RequestMapping(value = "/app/mee/meeOutsideMeeting", method = RequestMethod.POST)
@Api(value = "外部会议Controller", description = "会议管理->外部会议管理相关接口")
public class AppMeeOutsideMeetingController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(AppMeeOutsideMeetingController.class);
	@Resource
	private MeeOutsideMeetingService meeOutsideMeetingService;
	@Resource
	private MeeOutsideMeetingWebService meeOutsideMeetingWebService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private WebTask webTask;

	private static final String MEET_ADD = "add";
	//private static final String MEET_CANCEL = "cancel";

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
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "variables", value = "流程变量map(Map<String, Object>():此处默认传空map)", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bizId", value = "业务主键ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "message", value = "意见", required = false, dataType = "string", paramType = "query")
	})
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response,
			String taskId, String bizId, String userId, String message) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autUser = getAppUserLogin(request);
			AutUser user = autUserService.selectById(autUser.getUserId());
			Map<String, Object> variables = new HashMap<String, Object>();
			retMsg = meeOutsideMeetingWebService.completeAppTask(variables, taskId, bizId, userId, user, message);
			if (retMsg.getCode() == AppConstant.RET_CODE_SUCCESS) {
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
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

}
