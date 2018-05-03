package aljoin.web.controller.act;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.act.iservice.ActActivitiService;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 自定义流程bpmn表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-01
 */
@Controller
@RequestMapping("/act/actActiviti")
public class ActActivitiController extends BaseController {
  @Resource
  private ActActivitiService activitiService;

  /**
   * 
   * 自定义流程bpmn表(页面).
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/actActivitiPage")
  public String actAljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {

    return "act/actActivitiPage";
  }
  
  /**
   * 
   * 启动流程.
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017-09-01
   */
  @RequestMapping("/startProcess")
  @ResponseBody
  public RetMsg startProcess(HttpServletRequest request, HttpServletResponse response) {
    
    return null;
  }



}
