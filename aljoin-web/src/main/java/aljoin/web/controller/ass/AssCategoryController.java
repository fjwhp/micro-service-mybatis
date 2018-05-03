package aljoin.web.controller.ass;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.ass.dao.entity.AssCategory;
import aljoin.ass.iservice.AssCategoryService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 固定资产分类表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-09
 */
@Controller
@RequestMapping("/ass/assCategory")
public class AssCategoryController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AssCategoryController.class);
	@Resource
	private AssCategoryService assCategoryService;
	
	/**
	 * 
	 * 固定资产分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-09
	 */
	@RequestMapping("/assCategoryPage")
	public String assCategoryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "ass/assCategoryPage";
	}
	
	/**
	 * 
	 * 固定资产分类表(分页列表).
	 *
	 * @return：Page<AssCategory>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-09
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<AssCategory> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AssCategory obj) {
	  List<AssCategory> page = null;
		try {
			page = assCategoryService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 固定资产分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-09
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AssCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
          retMsg = assCategoryService.add(obj);
        }catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * 固定资产分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-09
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AssCategory obj) {
		RetMsg retMsg = new RetMsg();

		assCategoryService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 固定资产分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-09
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AssCategory obj) {
		RetMsg retMsg = new RetMsg();

		try {
          retMsg = assCategoryService.update(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * 固定资产分类表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-09
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AssCategory getById(HttpServletRequest request, HttpServletResponse response, AssCategory obj) {
		return assCategoryService.getById(obj.getId());
	}

}
