package aljoin.app.controller.veh;

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
import aljoin.aut.iservice.AutUserService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.veh.iservice.VehUseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/app/veh/vehUse", method = RequestMethod.POST)
@Api(value = "车船申请Controller", description = "车船申请相关接口")
public class AppVehUseController extends BaseController {

  private final static Logger logger = LoggerFactory.getLogger(AppVehUseController.class);
  
  @Resource
  private AutUserService autUserService;
  @Resource
  private WebTask webTask;
  @Resource
  private VehUseService vehUseService;
  
  @RequestMapping("/completeTask")
  @ResponseBody
  @ApiOperation("完成任务")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "bizId", value = "业务主键ID", required = true, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "userId", value = "用户ID(单个或多个均要加分号分隔)", required = false, dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "message", value = "意见", required = false, dataType = "string", paramType = "query")
  })
  public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response,
                                                       String taskId, String bizId, String userId, String message) {
      RetMsg retMsg = new RetMsg();
      try {
          AutAppUserLogin user = getAppUserLogin(request);
          AutUser autUser = autUserService.selectById(user.getUserId());
          Map<String, Object> variables  = new  HashMap<String,Object>();
          retMsg = vehUseService.completeAppTask(variables, taskId, bizId, userId, message, autUser);
          if (retMsg.getObject() != null) {
              @SuppressWarnings("unchecked")
              HashMap<String, String> map = (HashMap<String, String>) retMsg.getObject();
              String handle = map.get("handle");
              if (handle != null && StringUtils.isNotEmpty(handle)) {// 当下一级办理人不为空时发送待办消息
                  String processInstanceId = map.get("processInstanceId");
                  webTask.sendOnlineMsg(processInstanceId, handle, autUser);
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
