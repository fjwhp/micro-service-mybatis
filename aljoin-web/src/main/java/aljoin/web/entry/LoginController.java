package aljoin.web.entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import aljoin.aut.security.CustomUser;
import aljoin.config.AljoinSetting;
import aljoin.web.controller.BaseController;

/**
 * 
 * 登录控制器
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:31:49
 */
@Controller
public class LoginController extends BaseController {
  
  @Resource
  private AljoinSetting aljoinSetting;

  @RequestMapping("login.html")
  public String loginPage(HttpServletRequest request, HttpServletResponse response) {
    // 判断是否有登录，如果已经登录，直接跳转到首页，不要跳到登录页面
    Object loginObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (loginObj instanceof CustomUser) {
      // 已经登录
      return "redirect:/";
    } else {
      // 匿名，未登录
      request.setAttribute("isUsePwdLogin", aljoinSetting.getIsUsePwdLogin());
      request.setAttribute("isUseValidateCode", aljoinSetting.getIsUseValidateCode());
      request.setAttribute("isUseRememberMe", aljoinSetting.getIsUseRememberMe());
      return "entry/login";
    }
  }
  
  @RequestMapping("login.do")
  public String loginPage2(HttpServletRequest request, HttpServletResponse response) {
    // 判断是否有登录，如果已经登录，直接跳转到首页，不要跳到登录页面
    Object loginObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (loginObj instanceof CustomUser) {
      // 已经登录
      return "redirect:/";
    } else {
      // 匿名，未登录
      request.setAttribute("isUsePwdLogin", aljoinSetting.getIsUsePwdLogin());
      request.setAttribute("isUseValidateCode", aljoinSetting.getIsUseValidateCode());
      request.setAttribute("isUseRememberMe", aljoinSetting.getIsUseRememberMe());
      return "entry/login";
    }
  }
  
  @RequestMapping("logout.do")
  public String loginPage3(HttpServletRequest request, HttpServletResponse response) {
    // 判断是否有登录，如果已经登录，直接跳转到首页，不要跳到登录页面
    Object loginObj = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (loginObj instanceof CustomUser) {
      // 已经登录
      return "redirect:/";
    } else {
      // 匿名，未登录
      request.setAttribute("isUsePwdLogin", aljoinSetting.getIsUsePwdLogin());
      request.setAttribute("isUseValidateCode", aljoinSetting.getIsUseValidateCode());
      request.setAttribute("isUseRememberMe", aljoinSetting.getIsUseRememberMe());
      return "entry/login";
    }
  }
}
