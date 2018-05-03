package aljoin.app.controller.act;

import aljoin.app.controller.BaseController;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.web.service.act.ActAljoinBpmnWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 流程表单控制器
 * @author：zhongjy
 * @date：2017年7月25日 下午4:00:56
 */
@Controller
@RequestMapping(value = "/app/act/modeler", method = RequestMethod.POST)
@Api(value = "AppModelerController", description = "协同办公 -> 在办待办工作")
public class AppModelerController extends BaseController{
  private final static Logger logger = LoggerFactory.getLogger(AppModelerController.class);

  @Resource
  private ActAljoinBpmnWebService actAljoinBpmnWebService;

  /**
   * 表单解析
   * @return：String
   * @author：wangj
   * @date：2017年12月20日
   */
  @RequestMapping(value = "/openForm")
  @ApiOperation("在办待办工作详情")
  @ResponseBody
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "businessKey", value = "业务key(自定义流程待办在办、固定流程待办在办详情必传参数)", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "processInstanceId", value = "流程实例ID(自定义流程在办、固定流程在办详情必传参数)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "bpmnId", value = "流程表主键ID(自定义流程待办详情必传参数)", required = false, dataType = "long", paramType = "query"),
    @ApiImplicitParam(name = "taskDefKey", value = "任务key(自定义流程待办详情必传参数)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "taskId", value = "当前任务ID(自定义流程待办、在办详情，固定流程待办详情必传参数)", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "signInTime", value = "签收时间(自定义流程待办详情必传参数)", required = false, dataType = "string", paramType = "query")

  })
  public RetMsg openForm(HttpServletRequest request, HttpServletResponse response) {
    RetMsg retMsg = new RetMsg();
    try {
      AutAppUserLogin appUser = getAppUserLogin(request);
      retMsg = actAljoinBpmnWebService.openAppForm(request,appUser.getUserId(),appUser.getUserName());
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作异常");
    }
    return retMsg;
  }
}
