package aljoin.app.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.druid.util.StringUtils;

import aljoin.app.annotation.ParamObj;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.iservice.AutAppUserLoginService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;
import aljoin.util.EncryptUtil;
import aljoin.util.JsonUtil;
import aljoin.util.SpringContextUtil;
import aljoin.util.StringUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

/**
 * 
 * API拦截器
 *
 * @author：zhongjy
 * 
 * @date：2017年10月19日 上午10:49:37
 */
public class APIHandlerInterceptor implements HandlerInterceptor {
  
  private final static Logger logger = LoggerFactory.getLogger(APIHandlerInterceptor.class);

  /**
   * 请求处理之前进行调用
   */
  @SuppressWarnings("unused")
  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    // 检查不通过返回对象
    RetMsg retMsg = new RetMsg();
    retMsg.setCode(AppConstant.RET_CODE_NO_AUTH);
    // 1.检查否包含：token、sign、timestamp参数,并检查他们的格式是否正确
    String token = request.getParameter("token");
    String sign = request.getParameter("sign");
    String timestamp = request.getParameter("timestamp");
    if (StringUtils.isEmpty(token)) {
      retMsg.setMessage("无权访问:必须包含token参数");
      printJsonMsg(response, retMsg);
      return false;
    }
    if (StringUtils.isEmpty(sign)) {
      retMsg.setMessage("无权访问:必须包含sign参数");
      printJsonMsg(response, retMsg);
      return false;
    }
    if (StringUtils.isEmpty(timestamp)) {
      retMsg.setMessage("无权访问:必须包含timestamp参数");
      printJsonMsg(response, retMsg);
      return false;
    }
    if (token.length() != 32 || !StringUtil.isAllUpperCase(token)) {
      retMsg.setMessage("无权访问:token格式错误");
      printJsonMsg(response, retMsg);
      return false;
    }
    if (sign.length() != 32 || !StringUtil.isAllUpperCase(sign)) {
      retMsg.setMessage("无权访问:sign格式错误");
      printJsonMsg(response, retMsg);
      return false;
    }

    try {
      Long.parseLong(timestamp);
    } catch (Exception e) {
      retMsg.setMessage("无权访问:timestamp格式错误");
      printJsonMsg(response, retMsg);
      return false;
    }
    // 2.签名有效性校验
    AutAppUserLoginService autAppUserLoginService =
        (AutAppUserLoginService) SpringContextUtil.getBean("autAppUserLoginServiceImpl");
    AutAppUserLogin autAppUserLogin = autAppUserLoginService.getByToken(token);
    if (autAppUserLogin == null) {
      retMsg.setMessage("无权访问:token不存在");
      printJsonMsg(response, retMsg);
      return false;
    }
    SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
    Enumeration<String> paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements()) {
      String paramName = paramNames.nextElement();
      //String paramValue = URLEncoder.encode(request.getParameter(paramName), "UTF-8");
      String paramValue = request.getParameter(paramName);
      parameters.put(paramName, paramValue);
    }
    String calculationSign = StringUtil.getSign(parameters, autAppUserLogin.getSecret());
    if (!sign.equals(calculationSign)) {
      retMsg.setMessage("无权访问:签名验证失败");
      printJsonMsg(response, retMsg);
      return false;
    }

    // 验证登录是否超时（最后访问时间和现在进行比较,不超过30分钟）
    long minutePeriod =
        DateUtil.getPeriod(autAppUserLogin.getLastAccessTime(), new Date(), DateUtil.UNIT_MINUTE);
    if (minutePeriod > 30) {
      retMsg.setMessage("无权访问:登录过期");
      printJsonMsg(response, retMsg);
      return false;
    }
    // 验证接口访问是否超次(时间戳校验)
    // 不能超过30分钟(1800000毫秒)
    if (System.currentTimeMillis() - Long.parseLong(timestamp) > 1800000) {
      retMsg.setMessage("无权访问:接口过期");
      printJsonMsg(response, retMsg);
      return false;
    }

    // 获取加密参数
    HandlerMethod handlerMethod = (HandlerMethod) handler;
    ParamObj po = handlerMethod.getMethodAnnotation(ParamObj.class);// 加密字段
    List<String> encryptList = new ArrayList<String>();
    if (po != null) {
      String[] attrArr = po.encryptAttrs().split(",");
      for (int i = 0; i < attrArr.length; i++) {
        encryptList.add(attrArr[i]);
      }
    }
    // 非空，加密校验
    ApiImplicitParams params = handlerMethod.getMethodAnnotation(ApiImplicitParams.class);// 获取api参数
    String myPrivateKey = autAppUserLogin.getAljoinPrivateKey();
    if (params != null) {
      ApiImplicitParam[] paramsArr = params.value();
      for (int i = 0; i < paramsArr.length; i++) {
        ApiImplicitParam ap = paramsArr[i];
        // 如果必填，判断参数中是否有
        if (ap.required()) {         
          if (StringUtils.isEmpty(request.getParameter(ap.name()))) {
            retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
            retMsg.setMessage("参数异常:参数[" + ap.name() + "](" + ap.value() + ")不能为空");
            printJsonMsg(response, retMsg);
            return false;
          }
          String apValue = URLDecoder.decode(request.getParameter(ap.name()), "UTF-8");
        }
        // 判断是否加密
        if (encryptList.contains(ap.name())) {
          String apValue = URLDecoder.decode(request.getParameter(ap.name()), "UTF-8");
          apValue = EncryptUtil.decryptDES(apValue, AppConstant.APP_DES_KEY);
          if (!StringUtils.isEmpty(apValue)) {
            try {
              EncryptUtil.decryptRSA(myPrivateKey, apValue);
            } catch (Exception e) {
              retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
              retMsg.setMessage("参数异常:参数[" + ap.name() + "](" + ap.value() + ")解密异常(加密错误)");
              printJsonMsg(response, retMsg);
              return false;
            }
          }

        }
      }
    }
    // 所有校验通过，放狗,修改接口最后访问时间
    autAppUserLogin.setLastAccessTime(new Date());
    autAppUserLoginService.update(autAppUserLogin);
    return true;
  }

  /**
   * 
   * 返回json
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年10月19日 上午11:10:42
   */
  private void printJsonMsg(HttpServletResponse response, RetMsg retMsg) {
    // 设置编码格式
    response.setContentType("text/plain;charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
    PrintWriter out = null;
    try {
      out = response.getWriter();
      out.write(JsonUtil.obj2str(retMsg));
      out.flush();
    } catch (IOException e) {
      logger.error("",e);
    }
  }

  /**
   * 请求处理之后进行调用
   */
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
      ModelAndView modelAndView) throws Exception {

  }

  /**
   * 渲染了对应的视图之后执行
   */
  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
      Object handler, Exception ex) throws Exception {

  }

}
