package aljoin.web.controller.goo;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.goo.dao.entity.GooCategory;
import aljoin.goo.iservice.GooCategoryService;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 办公用品分类表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-03
 */
@Controller
@RequestMapping("/goo/gooCategory")
public class GooCategoryController extends BaseController {
    
    private final static Logger logger = LoggerFactory.getLogger(GooCategoryController.class);
	@Resource
	private GooCategoryService gooCategoryService;
	
	/**
	 * 
	 * 办公用品分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-03
	 */
	@RequestMapping("/gooCategoryPage")
	public String gooCategoryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "goo/gooCategoryPage";
	}
	
	/**
	 * 
	 * 办公用品分类表(分页列表).
	 *
	 * @return：List<GooCategory>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-03
	 */
	@RequestMapping("/list")
	@ResponseBody
	public List<GooCategory> list(HttpServletRequest request, HttpServletResponse response) {
		List<GooCategory> page = null;
		try {
			page = gooCategoryService.list();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 办公用品分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-03
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, GooCategory obj) {
		RetMsg retMsg = new RetMsg();

		try {
		  retMsg = gooCategoryService.add(obj);
		}catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-03
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, GooCategory obj) {
		RetMsg retMsg = new RetMsg();

		gooCategoryService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-03
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, GooCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
		  retMsg = gooCategoryService.update(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * 办公用品分类表(根据ID获取对象).
	 *
	 * @return：GooCategory
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-03
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public GooCategory getById(HttpServletRequest request, HttpServletResponse response, GooCategory obj) {
		return gooCategoryService.getById(obj.getId());
	}

}
