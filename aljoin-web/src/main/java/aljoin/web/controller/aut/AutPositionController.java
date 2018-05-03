package aljoin.web.controller.aut;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.object.AutPositionDO;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.exception.AljoinException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 岗位(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-08-17
 */
@Controller
@RequestMapping(value = "/aut/autPosition", method = RequestMethod.POST)
@Api(value = "岗位管理", description = "权限管理->部门岗位->岗位管理接口")
public class AutPositionController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutPositionController.class);
  @Resource
  private AutPositionService autPositionService;

  @Resource
  private ActActivitiService activitiService;

  @Resource
  private IdentityService identityService;

  @Resource
  private AutUserPositionService autUserPositionService;

  /**
   * 
   * 岗位(页面).
   *
   * @return：String
   *
   * @author：wangj
   *
   * @date：2017-08-17
   */
  @RequestMapping(value = "/autPositionPage", method = RequestMethod.GET)
  @ApiOperation(value = "岗位页面跳转接口")
  public String autPositionPage(HttpServletRequest request, HttpServletResponse response) {
    return "aut/autPositionPage";
  }

  /**
   * 
   * 岗位(分页列表).
   *
   * @return：Page<AutPosition>
   *
   * @author：wangj
   *
   * @date：2017-08-17
   */
  @RequestMapping(value = "/list")
  @ApiOperation(value = "岗位分页列表接口")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "positionName", value = "岗位名称", required = false,
          dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "deptCode", value = "部门编号", required = false, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "isActive", value = "是否激活", required = false, dataType = "string",
          paramType = "query")})
  @ResponseBody
  public Page<AutPositionDO> list(HttpServletRequest request, HttpServletResponse response,
      PageBean pageBean, AutPosition autPosition) {
    Page<AutPositionDO> page = null;
    try {
      page = autPositionService.list(pageBean, autPosition);
    } catch (Exception e) {
      logger.error("", e);
    }
    return page;
  }


  /**
   * 
   * 岗位(新增).
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-08-17
   */
  @RequestMapping(value = "/add")
  @ApiOperation(value = "岗位新增接口")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "positionName", value = "岗位名称", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "deptCode", value = "部门编号", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "isActive", value = "是否激活", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "positionRank", value = "分类排序", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string",
          paramType = "query")})
  @ResponseBody
  public RetMsg add(HttpServletRequest request, HttpServletResponse response,
      AutPosition autPosition) {
    RetMsg retMsg = new RetMsg();
    try {
      retMsg = autPositionService.add(autPosition);
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage("操作异常,请检查入参！");
      logger.error("", e);
    }


    return retMsg;
  }

  /**
   * 
   * 岗位(根据ID删除对象).
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-08-17
   */
  @RequestMapping(value = "/delete")
  @ApiOperation(value = "岗位删除接口")
  @ApiImplicitParam(name = "id", value = "岗位ID", required = true, dataType = "int",
      paramType = "query")
  @ResponseBody
  public RetMsg delete(HttpServletRequest request, HttpServletResponse response,
      AutPosition autPosition) {
    RetMsg retMsg = new RetMsg();
    try {
      retMsg = autPositionService.delete(autPosition);
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage("操作异常,请检查入参！");
    }
    return retMsg;
  }

  /**
   * 
   * 岗位(根据ID修改对象).
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-08-17
   */
  @RequestMapping(value = "/update")
  @ApiOperation(value = "岗位编辑接口")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "id", value = "岗位ID", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "positionName", value = "岗位名称", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "deptCode", value = "部门编号", required = false, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "isActive", value = "是否激活", required = true, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "positionRank", value = "分类排序", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string",
          paramType = "query")})
  @ResponseBody
  public RetMsg update(HttpServletRequest request, HttpServletResponse response,
      AutPosition autPosition) {
    RetMsg retMsg = new RetMsg();

    AutPosition orgnlObj = autPositionService.selectById(autPosition.getId());
    retMsg = autPositionService.update(orgnlObj,autPosition);
    // orgnlObj.set...
    
    return retMsg;
  }

  /**
   * 
   * 岗位(根据ID获取对象).
   *
   * @return：AutUser
   *
   * @author：wangj
   *
   * @date：2017-08-17
   */
  @RequestMapping(value = "/getById")
  @ApiOperation("岗位详情接口")
  @ApiImplicitParam(name = "id", value = "岗位ID", required = true, dataType = "int",
      paramType = "query")
  @ResponseBody
  public AutPosition getById(HttpServletRequest request, HttpServletResponse response,
      AutPosition autPosition) {
    try {
      return autPositionService.selectById(autPosition);
    } catch (Exception e) {
      new AljoinException("请检查传入参数是否正确！");
    }
    return null;
  }

  /**
   * 
   * 岗位列表接口(根据部门id查询)
   *
   *                      @return：List<AutPosition>
   *
   * @author：laijy
   *
   * @date：2017年9月11日 上午10:48:58
   */
  @RequestMapping("/getPositionListByDeptId")
  @ResponseBody
  public List<AutPosition> getPositionListByDeptId(AutPosition obj) throws Exception {
    List<AutPosition> autPositionList = autPositionService.getPositionListByDeptId(obj);
    return autPositionList;

  }

  /**
   *
   * 岗位列表接口(根据部门id查询)
   *
   *                      @return：List<AutPosition>
   *
   * @author：laijy
   *
   * @date：2017年9月11日 上午10:48:58
   */
  @RequestMapping("/validate")
  @ResponseBody
  @ApiOperation("岗位名称校验接口")
  @ApiImplicitParam(name = "positionName", value = "岗位名称", required = true, dataType = "string",
      paramType = "query")
  public RetMsg validate(AutPosition obj) {
    RetMsg retMsg = new RetMsg();
    try {
      retMsg = autPositionService.validate(obj);
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage("操作失败");
      logger.error("", e);
    }
    return retMsg;
  }

  /**
   * 
   * 激活的岗位(分页列表)
   *
   *                 @return：Page<AutPositionDO>
   *
   * @author：laijy
   *
   * @date：2017年10月9日 上午9:01:32
   */
  @RequestMapping(value = "/listIsActive")
  @ApiOperation(value = "激活的岗位分页列表接口")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int",
          paramType = "query"),
      @ApiImplicitParam(name = "positionName", value = "岗位名称", required = false,
          dataType = "string", paramType = "query"),
      @ApiImplicitParam(name = "deptCode", value = "部门编号", required = false, dataType = "string",
          paramType = "query"),
      @ApiImplicitParam(name = "isActive", value = "是否激活", required = false, dataType = "string",
          paramType = "query")})
  @ResponseBody
  public Page<AutPositionDO> listIsActive(HttpServletRequest request, HttpServletResponse response,
      PageBean pageBean, AutPosition autPosition) {
    Page<AutPositionDO> page = null;
    try {
      page = autPositionService.listIsActive(pageBean, autPosition);
    } catch (Exception e) {
      logger.error("", e);
    }
    return page;
  }
}
