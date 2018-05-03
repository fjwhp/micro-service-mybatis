package aljoin.web.controller.act;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinFormWidget;
import aljoin.act.iservice.ActAljoinFormWidgetService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 表单控件表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-08-31
 */
@Controller
@RequestMapping("/act/actAljoinFormWidget")
public class ActAljoinFormWidgetController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormWidgetController.class);
  @Resource
  private ActAljoinFormWidgetService actAljoinFormWidgetService;

  /**
   * 
   * 表单控件表(页面).
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/actAljoinFormWidgetPage")
  public String actAljoinFormWidgetPage(HttpServletRequest request, HttpServletResponse response) {

    return "act/actAljoinFormWidgetPage";
  }

  /**
   * 
   * 表单控件表(分页列表).
   *
   * @return：Page<ActAljoinFormWidget>
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/list")
  @ResponseBody
  public Page<ActAljoinFormWidget> list(HttpServletRequest request, HttpServletResponse response,
      PageBean pageBean, ActAljoinFormWidget obj) {
    Page<ActAljoinFormWidget> page = null;
    try {
      page = actAljoinFormWidgetService.list(pageBean, obj);
    } catch (Exception e) {
      logger.error("", e);
    }
    return page;
  }


  /**
   * 
   * 表单控件表(新增).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/add")
  @ResponseBody
  public RetMsg add(HttpServletRequest request, HttpServletResponse response,
      ActAljoinFormWidget obj) {
    RetMsg retMsg = new RetMsg();

    actAljoinFormWidgetService.insert(obj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 表单控件表(根据ID删除对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/delete")
  @ResponseBody
  public RetMsg delete(HttpServletRequest request, HttpServletResponse response,
      ActAljoinFormWidget obj) {
    RetMsg retMsg = new RetMsg();

    actAljoinFormWidgetService.deleteById(obj.getId());

    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 表单控件表(根据ID修改对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/update")
  @ResponseBody
  public RetMsg update(HttpServletRequest request, HttpServletResponse response,
      ActAljoinFormWidget obj) {
    RetMsg retMsg = new RetMsg();

    ActAljoinFormWidget orgnlObj = actAljoinFormWidgetService.selectById(obj.getId());
    orgnlObj.setWidgetType(obj.getWidgetType());
    orgnlObj.setWidgetId(obj.getWidgetId());
    orgnlObj.setWidgetName(obj.getWidgetName());
    orgnlObj.setFormId(obj.getFormId());
    orgnlObj.setIsActive(obj.getIsActive());
    actAljoinFormWidgetService.updateById(orgnlObj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 表单控件表(根据ID获取对象).
   *
   * @return：AutUser
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/getById")
  @ResponseBody
  public ActAljoinFormWidget getById(HttpServletRequest request, HttpServletResponse response,
      ActAljoinFormWidget obj) {
    return actAljoinFormWidgetService.selectById(obj.getId());
  }

  /**
   * 
   * 表单控件表(根据ID获取对象).
   *
   * @return：AutUser
   *
   * @author：zhongjy
   *
   * @date：2017-08-31
   */
  @RequestMapping("/getWidgetByFormId")
  @ResponseBody
  public List<ActAljoinFormWidget> getWidgetByFormId(HttpServletRequest request,
      HttpServletResponse response, ActAljoinFormWidget obj) {
    Where<ActAljoinFormWidget> actAljoinFormWidgetWhere = new Where<ActAljoinFormWidget>();
    actAljoinFormWidgetWhere.eq("form_id", obj.getId());
    List<ActAljoinFormWidget> list =
        actAljoinFormWidgetService.selectList(actAljoinFormWidgetWhere);
    return list;
  }

}
