package aljoin.web.controller.ioa;

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

import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaRegCategory;
import aljoin.ioa.dao.entity.IoaRegClosed;
import aljoin.ioa.dao.entity.IoaRegHair;
import aljoin.ioa.iservice.IoaRegCategoryService;
import aljoin.ioa.iservice.IoaRegClosedService;
import aljoin.ioa.iservice.IoaRegHairService;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.exception.AljoinException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 收发文登记分类
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaRegCategory", method = RequestMethod.POST)
@Api(value = "收文登记操作Controller", description = "流程->收文登记")
public class IoaRegCategoryController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(IoaRegCategoryController.class);
	@Resource
	private IoaRegCategoryService ioaRegCategoryService;
	@Resource
	private IoaRegClosedService ioaRegClosedService;
	@Resource
	private IoaRegHairService ioaRegHairService;

	/**
	 *
	 *返回收发文分类表
	 *
	 * @return：Page<ActAljoinFormCategory>
	 *
	 * @author：
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/getCateGoryList")
	@ResponseBody
	@ApiOperation(value = "表单分类根据父ID或查询所有分类列表(不分页)", notes = "表单分类根据父ID查询列表或者直接查询所有列表(不分页)接口")
	@ApiImplicitParam(name = "regType", value = "收发文类型标识0收文，1发文", required = false, dataType = "int", paramType = "query")
	public List<IoaRegCategory> getCateGoryList(HttpServletRequest request, HttpServletResponse response,
			String regType) {
		List<IoaRegCategory> categoryList = null;
		try {
			categoryList = ioaRegCategoryService.getCateGoryList(regType);
		} catch (Exception e) {
			logger.error("", e);
		}
		return categoryList;
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
	@ApiOperation(value = "表单分类新增", notes = "表单分类新增一级和二级分类接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "categoryName", value = "表单分类名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "categoryRank", value = "同级分类排序", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isActive", value = "是否激活", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "parentId", value = "父级分类ID", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryLevel", value = "分类级别", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "regType", value = "分类类型", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, IoaRegCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			if (null != obj) {
			    //验证分类名称: true:表单已经存在 false:表单不存在可以新增
				boolean flag = ioaRegCategoryService.validCategoryName(obj, true);
				if (!flag) {
				    //验证表单级别 true:子分类没有超过级别 false:子分类超过999
					flag = ioaRegCategoryService.validCategoryLevel(obj);
					if (flag) {
						if (null == obj.getParentId()) {
							obj.setParentId(0L);
							obj.setCategoryLevel(1);
						}
						retMsg = ioaRegCategoryService.addIoaRegCategory(obj);
					} else {
						retMsg.setCode(1);
						retMsg.setMessage("分类数目已达到最大，不能再添加了");
					}

				} else {
					retMsg.setCode(1);
					retMsg.setMessage("该分类已经存在");
				}

			}

		} catch (Exception e) {
			new AljoinException("请检查入参");
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation(value = "表单分类编辑", notes = "表单分类编辑接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "分类ID", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "categoryName", value = "表单分类名称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "categoryRank", value = "同级分类排序", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isActive", value = "是否激活", required = true, dataType = "string", paramType = "query"),
			/*
			 * @ApiImplicitParam(name = "parentId",value = "父级分类ID",required =
			 * true,dataType = "int",paramType = "query"),
			 */
			@ApiImplicitParam(name = "categoryLevel", value = "分类级别", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "regType", value = "分类类型", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query") })
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, IoaRegCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			boolean flag = ioaRegCategoryService.validCategoryName(obj, false);
			if (!flag) {
				IoaRegCategory orgnlObj = ioaRegCategoryService.selectById(obj.getId());

				if (null != obj.getCategoryName()) {
					orgnlObj.setCategoryName(obj.getCategoryName());
				}
				if (null != obj.getIsActive()) {
					orgnlObj.setIsActive(obj.getIsActive());
				}
				if (null != obj.getCategoryRank()) {
					orgnlObj.setCategoryRank(obj.getCategoryRank());
				}
				ioaRegCategoryService.updateById(orgnlObj);
				retMsg.setCode(0);
				retMsg.setMessage("操作成功");
			} else {
				retMsg.setCode(1);
				retMsg.setMessage("该分类已经存在");
			}

		} catch (Exception e) {
			new AljoinException("");
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
	@ApiOperation(value = "表单分类删除", notes = "表单分类删除接口")
	@ApiImplicitParam(name = "id", value = "分类ID", required = true, dataType = "int", paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, IoaRegCategory obj) {

		RetMsg retMsg = new RetMsg();
		try {
			// 判断分类（含器子分类）是否含有流程，
			Set<Long> categoryIdSet = new HashSet<Long>();
			List<IoaRegCategory> list = ioaRegCategoryService.getAllChildCategoryList(obj.getId());
			for (IoaRegCategory ioaRegCategory : list) {
				categoryIdSet.add(ioaRegCategory.getId());
			}
			categoryIdSet.add(obj.getId());
			// 如果有内容，不允许删除（收发文查询）
			Where<IoaRegClosed> closedWhere = new Where<IoaRegClosed>();
			closedWhere.in("category", categoryIdSet);
			List<IoaRegClosed> closedList = ioaRegClosedService.selectList(closedWhere);
			int count = 0;
			if (closedList != null) {
				count = closedList.size();
			}
			if (count > 0) {
				// 分类下有表单，不能删除
				retMsg.setCode(1);
				retMsg.setMessage("该分类下有收文登记记录，不能删除");
			} else {
				Where<IoaRegHair> hairWhere = new Where<IoaRegHair>();
				hairWhere.in("category", categoryIdSet);
				List<IoaRegHair> hairList = ioaRegHairService.selectList(hairWhere);
				if (hairList != null && hairList.size() > 0) {
					retMsg.setCode(1);
					retMsg.setMessage("该分类下有发文登记记录，不能删除");
				} else {
					ioaRegCategoryService.deleteById(obj.getId());
					retMsg.setCode(0);
					retMsg.setMessage("操作成功");
				}
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
	 * 表单分类表(根据ID获取对象).
	 *
	 * @return：
	 *
	 * @author：
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation(value = "分类详情", notes = "分类详情接口")
	@ApiImplicitParam(name = "id", value = "分类ID", required = true, dataType = "int", paramType = "query")
	public IoaRegCategory getById(HttpServletRequest request, HttpServletResponse response, IoaRegCategory obj) {
		IoaRegCategory ioa = ioaRegCategoryService.selectById(obj.getId());
		return ioa;
	}
}
