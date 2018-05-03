package aljoin.app.controller;

import java.net.URLDecoder;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.iservice.AutAppUserLoginService;
import aljoin.object.AppConstant;
import aljoin.util.EncryptUtil;

/**
 * 
 * 基础控制器类
 *
 * @author：zhongjy
 *
 * @date：2017年5月25日 下午8:30:31
 */
public class BaseController {
  public static Logger log =LoggerFactory.getLogger(BaseController.class);
  @Resource
  private AutAppUserLoginService autAppUserLoginService;

  /**
   * 
   * 获取当前用户信息
   *
   * @return：CustomUser
   *
   * @author：zhongjy
   *
   * @date：2017年5月25日 下午8:49:45
   */
  public AutAppUserLogin getAppUserLogin(HttpServletRequest request) throws Exception {
    AutAppUserLogin autAppUserLogin =
        autAppUserLoginService.getByToken(request.getParameter("token"));
    return autAppUserLogin;

  }


  /**
   * 
   * 非空判断
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年10月31日 下午9:49:14
   */
  public void checkParamIsNull(Map<String, Object> param) throws Exception {
    for (Map.Entry<String, Object> entry : param.entrySet()) {
      if (StringUtils.isEmpty(entry.getValue())) {
        throw new Exception("参数[" + entry.getKey() + "]不能为空");
      }
    }
  }

  /**
   * 
   * 参数解密
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月31日 下午9:48:41
   */
  public String dencryptParam(String key, AutAppUserLogin autAppUserLogin) throws Exception {
    key = URLDecoder.decode(key, "UTF-8");
    key = EncryptUtil.decryptDES(key, AppConstant.APP_DES_KEY);
    key = EncryptUtil.decryptRSA(autAppUserLogin.getAljoinPrivateKey(), key);
    return key;

  }
  
  /**
   * 
   * 参数加密
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月31日 下午9:48:41
   */
  public String encryptParam(String paramValue, AutAppUserLogin autAppUserLogin) throws Exception {
    paramValue = EncryptUtil.encryptRSA(autAppUserLogin.getOtherPublicKey(), paramValue);
    paramValue = EncryptUtil.encryptDES(paramValue, AppConstant.APP_DES_KEY);
    paramValue = URLDecoder.decode(paramValue, "UTF-8");
    return paramValue;

  }


}
