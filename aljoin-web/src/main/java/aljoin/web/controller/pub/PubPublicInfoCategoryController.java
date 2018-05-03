package aljoin.web.controller.pub;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.object.PubPublicInfoCategoryDO;
import aljoin.pub.dao.object.PubPublicInfoCategoryVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 公共信息分类表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/pub/pubPublicInfoCategory",method = RequestMethod.POST)
@Api(value = "公共信息分类Controller",description = "系统维护->公共信息管理->公共信息分类接口")
public class PubPublicInfoCategoryController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(PubPublicInfoCategoryController.class);
	@Resource
	private PubPublicInfoCategoryService pubPublicInfoCategoryService;
	
	/**
	 * 
	 * 公共信息分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/pubPublicInfoCategoryPage",method = RequestMethod.GET)
	@ApiOperation("公共信息分类页面跳转")
	public String pubPublicInfoCategoryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sma/pubPublicInfoCategoryPage";
	}

	/**
	 *
	 * 公共信息分类表(分页列表).
	 *
	 * @return：Page<PubPublicInfoCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation("公共信息分类分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "name",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "状态",required = false,dataType = "int",paramType = "query")
	})
	public Page<PubPublicInfoCategoryDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, PubPublicInfoCategory obj) {
		Page<PubPublicInfoCategoryDO> page = null;
		try {
			page = pubPublicInfoCategoryService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 公共信息分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("公共信息分类新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categryRank",value = "排序",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isUse",value = "是否使用流程",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "processId",value = "使用流程",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "processName",value = "流程名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "useGroupId",value = "使用群体ID （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "useGroupName",value = "使用群体 （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "状态",required = false,dataType = "int",paramType = "query")

	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, PubPublicInfoCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoCategoryService.add(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 公共信息分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@ApiOperation("公共信息分类删除")
	@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "int",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, PubPublicInfoCategory obj) {
		RetMsg retMsg = new RetMsg();

		pubPublicInfoCategoryService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 公共信息分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("公共信息分类新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "name",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categryRank",value = "排序",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isUse",value = "是否使用流程",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "bpmnId",value = "使用流程",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "bpmnName",value = "流程名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "useGroupId",value = "使用群体ID （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "useGroupName",value = "使用群体 （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "状态（0：无效 1：有效）",required = false,dataType = "int",paramType = "query")

	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, PubPublicInfoCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoCategoryService.update(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 公共信息分类表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("公共信息分类详情")
	@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "int",paramType = "query")
	public PubPublicInfoCategoryVO getById(HttpServletRequest request, HttpServletResponse response, PubPublicInfoCategory obj) {
//		return pubPublicInfoCategoryService.selectById(obj.getId());
		PubPublicInfoCategoryVO category = null;
		try {
			category = pubPublicInfoCategoryService.detail(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return category;
	}

	/**
	 *
	 * 公共信息分类表.
	 *
	 * @return：Page<PubPublicInfoCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	@RequestMapping(value = "/validList")
	@ResponseBody
	@ApiOperation("公共信息分类列表")
	public List<PubPublicInfoCategory> validList(HttpServletRequest request, HttpServletResponse response,PubPublicInfoCategory obj) {
		List<PubPublicInfoCategory> categoryList = null;
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			categoryList = pubPublicInfoCategoryService.validList(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return categoryList;
	}
	/**
	 * 
	 * 首页公共信息分类
	 *
	 * @return：List<PubPublicInfoCategory>
	 *
	 * @author：xuc
	 *
	 * @date：2017年11月24日 上午10:19:31
	 */
   @RequestMapping(value = "/allList")
   @ResponseBody
   @ApiOperation("公共信息分类列表")
   public List<PubPublicInfoCategory> allList(HttpServletRequest request, HttpServletResponse response,PubPublicInfoCategory obj) {
       List<PubPublicInfoCategory> categoryList = null;
       try {
           CustomUser user = getCustomDetail();
           if(null != user){
               obj.setCreateUserId(user.getUserId());
           }
           categoryList = pubPublicInfoCategoryService.allList(obj);
       } catch (Exception e) { 
         logger.error("", e);
       }
       return categoryList;
   }
}
