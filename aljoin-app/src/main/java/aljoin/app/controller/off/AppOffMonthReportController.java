package aljoin.app.controller.off;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import aljoin.aut.iservice.AutUserService;
import aljoin.object.RetMsg;
import aljoin.web.service.off.OffMonthReportWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 工作月报表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-11
 */
@Controller
@RequestMapping(value = "/app/off/offMonthReport",method = RequestMethod.POST)
@Api(value = "工作月报Controller",description = "工作计划->工作报表->工作月报相关接口")
public class AppOffMonthReportController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AppOffMonthReportController.class);
	@Resource
    private OffMonthReportWebService offMonthReportWebService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private WebTask webTask;

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
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bizId", value = "业务主键ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "userId", value = "用户ID", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "message", value = "意见", required = false, dataType = "string", paramType = "query")
	})
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, String taskId, String bizId,String userId,String message) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUser = getAppUserLogin(request);
			AutUser user = autUserService.selectById(autAppUser.getUserId());
			Map<String, Object> variables = new HashMap<String, Object>();
			retMsg = offMonthReportWebService.completeAppTask(variables,taskId,bizId,userId,user,message);
			if(retMsg.getObject()!=null){
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, user);
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

}
