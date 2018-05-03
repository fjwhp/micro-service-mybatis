package aljoin.app.controller;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.app.annotation.ParamObj;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * Demo.
 * 
 *           @author：laijy
 * 
 * @date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/app/demo", method = RequestMethod.POST)
@Api(value = "接口demo", description = "接口demo")
public class DemoController extends BaseController {
  
  private final static Logger logger = LoggerFactory.getLogger(DemoController.class);

  @RequestMapping("/test")
  @ResponseBody
  @ParamObj(encryptAttrs = "aa,bb", encryptType = AppConstant.PARAM_ENCRYPT_RSA)
  @ApiOperation(value = "接口访问demo")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "cc", value = "参数-cc", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "bb", value = "参数-bb(加密)", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "aa", value = "参数-aa(加密)", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string",
          paramType = "query")})
  public RetMsg test(HttpServletRequest request, HttpServletResponse response) {
    RetMsg retMsg = new RetMsg();
    try {
      AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
      Enumeration<String> paramNames = request.getParameterNames();
      while (paramNames.hasMoreElements()) {
        String paramName = paramNames.nextElement();
        String paramValue = request.getParameter(paramName);
        if ("aa".equals(paramName) || "bb".equals(paramName)) {
          paramValue = dencryptParam(paramValue, autAppUserLogin);
        }
        System.out.println(paramName + ": " + paramValue);
      }
      System.out.println("访问成功了。。。");
      retMsg.setCode(0);
      String msg = "你好，访问成功";
      retMsg.setMessage(encryptParam(msg, autAppUserLogin));
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage(e.getMessage());
      logger.error("",e);
    }
    return retMsg;
  }
}
