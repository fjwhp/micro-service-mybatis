package aljoin.web.controller.act;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 流程查询表(历史表)(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-02
 */
@Controller
@RequestMapping("/act/actAljoinQueryHis")
public class ActAljoinQueryHisController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinQueryHisController.class);
  @Resource
  private ActAljoinQueryHisService actAljoinQueryHisService;

  /**
   * 
   * 流程查询表(历史表)(页面).
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017-11-02
   */
  @RequestMapping("/actAljoinQueryHisPage")
  public String actAljoinQueryHisPage(HttpServletRequest request, HttpServletResponse response) {

    return "act/actAljoinQueryHisPage";
  }

  /**
   * 
   * 流程查询表(历史表)(分页列表).
   *
   * @return：Page<ActAljoinQueryHis>
   *
   * @author：zhongjy
   *
   * @date：2017-11-02
   */
  @RequestMapping("/list")
  @ResponseBody
  public Page<ActAljoinQueryHis> list(HttpServletRequest request, HttpServletResponse response,
      PageBean pageBean, ActAljoinQueryHis obj) {
    Page<ActAljoinQueryHis> page = null;
    try {
      page = actAljoinQueryHisService.list(pageBean, obj);
    } catch (Exception e) {
      logger.error("",e);
    }
    return page;
  }


  /**
   * 
   * 流程查询表(历史表)(新增).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-11-02
   */
  @RequestMapping("/add")
  @ResponseBody
  public RetMsg add(HttpServletRequest request, HttpServletResponse response,
      ActAljoinQueryHis obj) {
    RetMsg retMsg = new RetMsg();

    // obj.set...

    actAljoinQueryHisService.insert(obj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 流程查询表(历史表)(根据ID删除对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-11-02
   */
  @RequestMapping("/delete")
  @ResponseBody
  public RetMsg delete(HttpServletRequest request, HttpServletResponse response,
      ActAljoinQueryHis obj) {
    RetMsg retMsg = new RetMsg();

    actAljoinQueryHisService.deleteById(obj.getId());

    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 流程查询表(历史表)(根据ID修改对象).
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017-11-02
   */
  @RequestMapping("/update")
  @ResponseBody
  public RetMsg update(HttpServletRequest request, HttpServletResponse response,
      ActAljoinQueryHis obj) {
    RetMsg retMsg = new RetMsg();

    ActAljoinQueryHis orgnlObj = actAljoinQueryHisService.selectById(obj.getId());
    // orgnlObj.set...

    actAljoinQueryHisService.updateById(orgnlObj);
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  /**
   * 
   * 流程查询表(历史表)(根据ID获取对象).
   *
   * @return：AutUser
   *
   * @author：zhongjy
   *
   * @date：2017-11-02
   */
  @RequestMapping("/getById")
  @ResponseBody
  public ActAljoinQueryHis getById(HttpServletRequest request, HttpServletResponse response,
      ActAljoinQueryHis obj) {
    return actAljoinQueryHisService.selectById(obj.getId());
  }

}
