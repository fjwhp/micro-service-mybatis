package aljoin.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * 临时菜单控制器
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:31:49
 */
@Controller
@RequestMapping("/tempMenu")
public class TempMenuController extends BaseController {

  /**
   * 
   * 临时菜单 页面
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  //已发短信
  @RequestMapping("/smsAlreadySentPage")
  public String msgAljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sms/smsAlreadySentPage";
  }
  //短信草稿
  @RequestMapping("/smsDraftPage")
  public String msg2AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sms/smsDraftPage";
  }
  
  
  
  
  
  //会议室管理
  @RequestMapping("/meeManagePage")
  public String metAljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeManagePage";
  }
  //内部会议室管理
  @RequestMapping("/meeInsideManagePage")
  public String met1AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeInsideManagePage";
  }
  //外部会议室管理
  @RequestMapping("/meeOutsideManagePage")
  public String met2AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeOutsideManagePage";
  }
  //外部会议室管理
  @RequestMapping("/meeMineManagePage")
  public String met3AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeMineManagePage";
  }
  //外部会议室管理
  @RequestMapping("/meeInAppayManagePage")
  public String met4AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeInAppayManagePage";
  }
  //外部会议室管理
  @RequestMapping("/meeOutAppayManagePage")
  public String met5AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeOutAppayManagePage";
  }
  //领导会议
  @RequestMapping("/meeleaderPage")
  public String met6AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "mee/meeleaderPage";
  }
  
  
  
  
  //首页
  @RequestMapping("/indexPage")
  public String indexAljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "ind/indexPage";
  }
  
  
  
  
//公共信息--我的信息
  @RequestMapping("/pubMinePage")
  public String pub1AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "pub/pubMinePage";
  }
//公共信息--最新信息
  @RequestMapping("/pubNewPage")
  public String pub2AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "pub/pubNewPage";
  }
//公共信息--综合信息
  @RequestMapping("/pubMexPage")
  public String pub3AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "pub/pubMexPage";
  } 
  
  
  
  
//个人中心--个人信息
  @RequestMapping("/personalPage")
  public String per1AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "per/personalPage";
  }
//个人中心--常用意见
  @RequestMapping("/perideaPage")
  public String per2AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "per/perideaPage";
  }
//个人中心--名片夹
  @RequestMapping("/perCardPage")
  public String per3AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "per/perCardPage";
  } 
//个人中心--通讯录
  @RequestMapping("/perMaillistPage")
  public String per4AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "per/perMaillistPage";
  } 
//个人中心--在线通知
  @RequestMapping("/perOnlinePage")
  public String per5AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "per/perOnlinePage";
  } 
//个人中心--个人页面定制
  @RequestMapping("/perPage")
  public String per6AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "per/perPage";
  } 
  
  

  
//系统维护--系统设置
  @RequestMapping("/smaSetPage")
  public String sma1AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sma/smaSetPage";
  }
//系统维护--短信管理
  @RequestMapping("/smaSmsdPage")
  public String sma2AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sma/smaSmsdPage";
  } 
//系统维护--公共信息管理
  @RequestMapping("/smaPubsmsPage")
  public String sma3AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sma/smaPubsmsPage";
  } 
//系统维护--领导看板管理
  @RequestMapping("/smaLeaderPage")
  public String sma4AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sma/smaLeaderPage";
  } 
//系统维护--领导会议
  @RequestMapping("/smaLeadmetPage")
  public String sma5AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sma/smaLeadmetPage";
  } 
//系统维护--考勤打卡设置
  @RequestMapping("/smaCheckPage")
  public String sma6AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
    return "sma/smaCheckPage";
  } 
  
//资料管理--综合查询
  @RequestMapping("/dadManagementPage")
  public String dad1ManagementPage(HttpServletRequest request, HttpServletResponse response) {
    return "dad/dadManagementPage";
  }
  
  
//领导看板--领导看图
  @RequestMapping("/dalLeaderPage")
  public String dal1LeaderPage(HttpServletRequest request, HttpServletResponse response) {
    return "dal/dalLeaderPage";
  } 
//短信草稿
  @RequestMapping("/meePage")
  public String meePage(HttpServletRequest request, HttpServletResponse response) {
    return "otherform/mee";
  }
}