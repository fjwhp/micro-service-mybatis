package aljoin.web.controller.act;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.object.ActAljoinCategoryBpmnVO;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 流程分类表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-08-31
 */
@Controller
@RequestMapping("/act/actAljoinCategory")
public class ActAljoinCategoryController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinCategoryController.class);
	@Resource
	private ActAljoinCategoryService actAljoinCategoryService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;

	/**
	 * 
	 * 流程分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/actAljoinCategoryPage")
	public String actAljoinCategoryPage(HttpServletRequest request, HttpServletResponse response) {

		return "act/actAljoinCategoryPage";
	}

	/**
	 * 
	 * 流程分类表(分页列表).
	 *
	 * @return：Page<ActAljoinCategory>
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinCategory> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ActAljoinCategory obj) {

		Page<ActAljoinCategory> page = null;
		try {
			page = actAljoinCategoryService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 流程分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinCategory obj) {

		RetMsg retMsg = new RetMsg();
		try {
			// 判断同级分类数量不超过999才新增
			if (!actAljoinCategoryService.outNumber(obj)) {
				if (obj.getParentId() == null) {
					obj.setParentId(0L);
					obj.setCategoryLevel(1);
				}
				// 验证分类名称唯一才新增
				if (actAljoinCategoryService.compareCategoryName(obj)) {
					actAljoinCategoryService.insert(obj);
					retMsg.setMessage("操作成功");
				} else {
					retMsg.setMessage("错误！分类名称已存在，请重新命名");
				}
			} else {
				retMsg.setMessage("错误！同级流程分类数量已超过999个");
			}
			retMsg.setCode(0);
		} catch (Exception e) {
			retMsg.setCode(1);
			logger.error("", e);
		}

		return retMsg;
	}

	/**
	 * 
	 * 流程分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinCategory obj) {

		RetMsg retMsg = new RetMsg();
		try {
		  // 判断分类（含器子分类）是否含有流程，如果有流程，不允许删除
		  Set<Long> categoryIdSet = new HashSet<Long>();
		  List<ActAljoinCategory> list = actAljoinCategoryService.getAllChildList(obj.getId());
		  for (ActAljoinCategory actAljoinCategory : list) {
		    categoryIdSet.add(actAljoinCategory.getId());
          }
		  categoryIdSet.add(obj.getId());
		  Where<ActAljoinBpmn> actAljoinBpmnWhere = new Where<ActAljoinBpmn>();
		  actAljoinBpmnWhere.in("category_id", categoryIdSet);
		  int count  = actAljoinBpmnService.selectCount(actAljoinBpmnWhere);
		  if(count > 0){
		      // 分类下有流程，不能删除
		      retMsg.setCode(1);
	          retMsg.setMessage("该分类下有流程，不能删除");
		  }else{
		      actAljoinCategoryService.deleteById(obj.getId());
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
	 * 流程分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinCategory obj) {

		RetMsg retMsg = new RetMsg();

		try {
			ActAljoinCategory orgnlObj = actAljoinCategoryService.selectById(obj.getId());
			orgnlObj.setCategoryRank(obj.getCategoryRank());
			orgnlObj.setIsActive(obj.getIsActive());

			// 判断是否有对分类名称修改，有则要判断修改后的名称是否已存在
			if (!orgnlObj.getCategoryName().equals(obj.getCategoryName())) {
				// 判断修改的名字不跟已有的冲突，才进行修改
				if (actAljoinCategoryService.compareCategoryName(obj)) {
					orgnlObj.setCategoryName(obj.getCategoryName());
					actAljoinCategoryService.updateById(orgnlObj);
					retMsg.setMessage("修改成功");
				} else {
					retMsg.setMessage("错误！分类名称已存在，请重命名！");
				}
			} else {// 如果分类名称没有修改，直接插入
				actAljoinCategoryService.updateById(orgnlObj);
				retMsg.setMessage("修改成功");
			}
			retMsg.setCode(0);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 流程分类表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinCategory getById(HttpServletRequest request, HttpServletResponse response, ActAljoinCategory obj) {
		return actAljoinCategoryService.getById(obj);
	}
	/**
	 * 
	 * 获取所有的流程分类
	 *
	 * @return：List<ActAljoinCategory>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月4日 上午10:21:24
	 */
	@RequestMapping("/getAllCategoryList")
	@ResponseBody
	public List<ActAljoinCategory> getAllCategoryList(HttpServletRequest request, HttpServletResponse response) {

		List<ActAljoinCategory> categoryList = new ArrayList<ActAljoinCategory>();
		try {
			categoryList = actAljoinCategoryService.getAllCategoryList();
		} catch (Exception e) {
		  logger.error("", e);
		}

		return categoryList;
	}
	
	@RequestMapping("/getByParentId")
	@ResponseBody
	public List<ActAljoinCategory> getByParentId(HttpServletRequest request, HttpServletResponse response,ActAljoinCategory obj){
		List<ActAljoinCategory> categoryList = new ArrayList<ActAljoinCategory>();
		
		try {
			categoryList = actAljoinCategoryService.getByParentId(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return categoryList;
		
	}
	/**
	 * 
	 * @描述：获取所有的流程分类和流程
	 *
	 * @返回：List<ActAljoinCategory>
	 *
	 * @作者：laijy
	 *
	 * @时间：2017年9月4日 上午10:21:24
	 */
	@RequestMapping("/getAllCategoryBpmnList")
	@ResponseBody
	public List<ActAljoinCategoryBpmnVO> getAllCategoryBpmnList(HttpServletRequest request, HttpServletResponse response) {

	List<ActAljoinCategoryBpmnVO> categoryList = new ArrayList<ActAljoinCategoryBpmnVO>();
	try {
	categoryList = actAljoinCategoryService.getAllCategoryBpmnList();
	} catch (Exception e) {
	  logger.error("", e);
	}

	return categoryList;
	}

	
}
