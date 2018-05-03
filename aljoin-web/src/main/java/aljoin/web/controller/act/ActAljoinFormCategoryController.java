package aljoin.web.controller.act;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

import aljoin.act.dao.entity.ActAljoinForm;
import aljoin.act.dao.entity.ActAljoinFormCategory;
import aljoin.act.iservice.ActAljoinFormCategoryService;
import aljoin.act.iservice.ActAljoinFormService;
import aljoin.dao.config.Where;
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
 * 表单分类表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-08-31
 */
@Controller
@RequestMapping(value = "/act/actAljoinFormCategory",method = RequestMethod.POST)
@Api(value = "表单分类controller",description = "流程管理->表单分类接口")
public class ActAljoinFormCategoryController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormCategoryController.class);
	@Resource
	private ActAljoinFormCategoryService actAljoinFormCategoryService;
	@Resource
	private ActAljoinFormService actAljoinFormService;
	
	/**
	 * 
	 * 表单分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/actAljoinFormCategoryPage",method = RequestMethod.GET)
	@ApiOperation(value = "表单分类页面跳转",notes = "表单分类页面跳转接口")
	public String actAljoinFormCategoryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormCategoryPage";
	}
	
	/**
	 * 
	 * 表单分类表(分页列表).
	 *
	 * @return：Page<ActAljoinFormCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation(value = "表单分类分页列表",notes = "表单分类分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "categoryName",value = "表单分类名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categoryRank",value = "同级分类排序",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "是否激活",required = false,dataType = "string",paramType = "query")
	})
	public Page<ActAljoinFormCategory> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormCategory obj) {
		Page<ActAljoinFormCategory> page = null;
		try {
			page = actAljoinFormCategoryService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 表单分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation(value = "表单分类新增",notes = "表单分类新增一级和二级分类接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryName",value = "表单分类名称",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categoryRank",value = "同级分类排序",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "是否激活",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "parentId",value = "父级分类ID",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "categoryLevel",value = "分类级别",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			if(null != obj){
				boolean flag = actAljoinFormCategoryService.validCategoryName(obj,true);//验证表单分类名称 true:表单已经存在 false:表单不存在可以新增
				if(!flag){
					flag = actAljoinFormCategoryService.validCategoryLevel(obj);//验证表单级别 true:子分类没有超过级别 false:子分类超过999
					if(flag){
						if(null == obj.getParentId()){
							obj.setParentId(0L);
							obj.setCategoryLevel(1);
						}
						actAljoinFormCategoryService.insert(obj);
						retMsg.setCode(0);
						retMsg.setMessage("操作成功");
					}else{
						retMsg.setCode(1);
						retMsg.setMessage("分类数目已达到最大，不能再添加了");
					}

				}else{
					retMsg.setCode(1);
					retMsg.setMessage("该分类已经存在");
				}

			}

		}catch (Exception e){
			new AljoinException("请检查入参");
			logger.error("", e);
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 表单分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/delete")  
	@ResponseBody
	@ApiOperation(value = "表单分类删除",notes = "表单分类删除接口")
	@ApiImplicitParam(name = "id",value = "分类ID",required = true,dataType = "int",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormCategory obj) {
		/*RetMsg retMsg = new RetMsg();

		actAljoinFormCategoryService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;*/
		
	  RetMsg retMsg = new RetMsg();
      try {
        // 判断分类（含器子分类）是否含有流程，如果有流程，不允许删除
        Set<Long> categoryIdSet = new HashSet<Long>();
        List<ActAljoinFormCategory> list = actAljoinFormCategoryService.getAllChildCategoryList(obj.getId());
        for (ActAljoinFormCategory actAljoinCategory : list) {
          categoryIdSet.add(actAljoinCategory.getId());
        }
        categoryIdSet.add(obj.getId());
        Where<ActAljoinForm> actAljoinFormWhere = new Where<ActAljoinForm>();
        actAljoinFormWhere.in("category_id", categoryIdSet);
        int count  = actAljoinFormService.selectCount(actAljoinFormWhere);
        if(count > 0){
            // 分类下有表单，不能删除
            retMsg.setCode(1);
            retMsg.setMessage("该分类下有表单，不能删除");
        }else{
            actAljoinFormCategoryService.deleteById(obj.getId());
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
      } catch (Exception e) {
        retMsg.setCode(1);
        retMsg.setMessage(e.getMessage());
        logger.error("", e);
      }
      return retMsg;
		
		
	}
	
	/**
	 * 
	 * 表单分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation(value = "表单分类编辑",notes = "表单分类编辑接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "分类ID",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "categoryName",value = "表单分类名称",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categoryRank",value = "同级分类排序",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "是否激活",required = true,dataType = "string",paramType = "query"),
			/*@ApiImplicitParam(name = "parentId",value = "父级分类ID",required = true,dataType = "int",paramType = "query"),*/
			@ApiImplicitParam(name = "categoryLevel",value = "分类级别",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			boolean flag = actAljoinFormCategoryService.validCategoryName(obj,false);
			if(!flag){
				ActAljoinFormCategory orgnlObj = actAljoinFormCategoryService.selectById(obj.getId());

				if(null != obj.getCategoryName()){
					orgnlObj.setCategoryName(obj.getCategoryName());
				}
				if(null != obj.getIsActive()){
					orgnlObj.setIsActive(obj.getIsActive());
				}
				if(null != obj.getCategoryRank()){
					orgnlObj.setCategoryRank(obj.getCategoryRank());
				}
				actAljoinFormCategoryService.updateById(orgnlObj);
				retMsg.setCode(0);
				retMsg.setMessage("操作成功");
			}else {
				retMsg.setCode(1);
				retMsg.setMessage("该分类已经存在");
			}

		}catch (Exception e){
			new AljoinException("");
			logger.error("", e);
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 表单分类表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation(value = "表单分类详情",notes = "表单分类详情接口")
	@ApiImplicitParam(name = "id",value = "分类ID",required = true,dataType = "int",paramType = "query")
	public ActAljoinFormCategory getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormCategory obj) {
		return actAljoinFormCategoryService.selectById(obj.getId());
	}

	/**
	 *
	 * 表单分类表(根据父ID查询分页列表).
	 *
	 * @return：Page<ActAljoinFormCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/getListByParentId")
	@ResponseBody
	@ApiOperation(value = "表单分类根据父ID查询分页列表",notes = "表单分类根据父ID查询分页列表接口")
	@ApiImplicitParam(name = "parentId",value = "父级分类ID",required = true,dataType = "int",paramType = "query")
	public Page<ActAljoinFormCategory> getListByParentId(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormCategory obj) {
		Page<ActAljoinFormCategory> page = null;
		try {
			page = actAljoinFormCategoryService.selectListByParentId(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 表单分类表(表单分类列表).
	 *
	 * @return：Page<ActAljoinFormCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/getList")
	@ResponseBody
	@ApiOperation(value = "表单分类根据父ID或查询所有分类列表(不分页)",notes = "表单分类根据父ID查询列表或者直接查询所有列表(不分页)接口")
	@ApiImplicitParam(name = "parentId",value = "父级分类ID",required = false,dataType = "int",paramType = "query")
	public List<ActAljoinFormCategory> getList(HttpServletRequest request, HttpServletResponse response,ActAljoinFormCategory obj) {
		List<ActAljoinFormCategory> categoryList = null;
		try {
			categoryList = actAljoinFormCategoryService.selectCategoryList(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return categoryList;
	}
	/**
	 *
	 * 表单分类表(表单分类列表).
	 *
	 * @return：Page<ActAljoinFormCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/getCateGoryList")
	@ResponseBody
	@ApiOperation(value = "表单分类根据父ID或查询所有分类列表(不分页)",notes = "表单分类根据父ID查询列表或者直接查询所有列表(不分页)接口")
	@ApiImplicitParam(name = "parentId",value = "父级分类ID",required = false,dataType = "int",paramType = "query")
	public List<ActAljoinFormCategory> getCateGoryList(HttpServletRequest request, HttpServletResponse response) {
		List<ActAljoinFormCategory> categoryList = null;
		try {
			categoryList = actAljoinFormCategoryService.getCateGoryList();
		} catch (Exception e) {
		  logger.error("", e);
		}
		return categoryList;
	}
	
}
