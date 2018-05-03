package aljoin.app.controller.att;

import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.RetMsg;
import aljoin.web.service.att.AttSignInOutWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 签到、退表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping(value = "/app/att/attSignInOut",method = RequestMethod.POST)
@Api(value = "签到Controller",description = "考勤管理->签到签退相关接口")
public class AppAttSignInOutController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AppAttSignInOutController.class);
	@Resource
	private AttSignInOutService attSignInOutService;
	@Resource
    private AttSignInOutWebService attSignInOutWebService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private WebTask webTask;

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
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query")})
	@ResponseBody
	@ApiOperation("检查当前节点下一个节点是显示组织机构还是人员")
	public RetMsg checkNextTaskInfo(HttpServletRequest request,String taskId){
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			retMsg = attSignInOutService.checkAppNextTaskInfo(taskId,user.getUserId());
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * app审批
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年11月09日
	 */
	@RequestMapping("/completeTask")
	@ResponseBody
	@ApiOperation("完成任务")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bizId", value = "业务主键ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "message", value = "意见", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "nextStept", value = "流程变量(1:南方中心 2：办公室 3：其他)", required = true, dataType = "string", paramType = "query")
	})
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response,  String taskId, String bizId, String userId, String message, String nextStept) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin user = getAppUserLogin(request);
			AutUser autUser = autUserService.selectById(user.getUserId());
			Map<String, Object> variables = new HashMap<String,Object>();
			variables.put("nextStept", Integer.valueOf(nextStept));
			retMsg = attSignInOutWebService.completeAppTask(variables,taskId,bizId,userId,message,autUser);
			if(retMsg.getObject()!=null){
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, autUser);
				}
				retMsg.setObject(null);
			}
			retMsg.setObject(null);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}


}
