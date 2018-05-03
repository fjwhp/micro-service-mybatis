package aljoin.app.controller.act;

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
import aljoin.ioa.dao.object.AppDoTaskVO;
import aljoin.ioa.iservice.IoaDoingWorkService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 在办工作
 * @author：zhongjy
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/app/ioa/ioaDoingWork", method = RequestMethod.POST)
@Api(value = "App在办工作流程操作Controller", description = "协同办公 -> 在办工作")
public class AppIoaDoingWorkController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AppIoaDoingWorkController.class);
  @Resource
  private IoaDoingWorkService ioaDoingWorkService;


  /**
   * 根据当前用户获取在办工作流程
   * @return：List
   * @author：pengsp
   * @date：2017年10月16日
   */
  @RequestMapping(value = "/selectDoTask")
  @ResponseBody
  @ApiOperation(value = "根据当前用户获取在办工作分页列表")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
    @ApiImplicitParam(name = "formTypeId", value = "表单类型ID", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "founder", value = "创建人", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "urgency", value = "缓急", required = false, dataType = "string", paramType = "query"),
    @ApiImplicitParam(name = "createBegTime", value = "开始收件时间", required = false, dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "createEndTime", value = "结束收件时间", required = false, dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "urgencyIsAsc", value = "缓急排序(0:倒序 1:升序)", required = false, dataType = "date", paramType = "query"),
    @ApiImplicitParam(name = "createDateIsAsc", value = "创建时间排序(0:倒序 1:升序)", required = false, dataType = "date", paramType = "query")

  })
  public RetMsg selectDoTask(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AppDoTaskVO obj) {
    RetMsg retMsg = new RetMsg();
    //Page<AppDoTaskDO> page = new Page<AppDoTaskDO>();
    try {
      AutAppUserLogin customUser = getAppUserLogin(request);
      String userId = customUser.getUserId().toString();
      retMsg = ioaDoingWorkService.doingList(pageBean, userId, obj);
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("操作异常");
    }
    return retMsg;
  }
}
