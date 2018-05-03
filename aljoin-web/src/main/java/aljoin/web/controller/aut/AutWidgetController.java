package aljoin.web.controller.aut;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutWidget;
import aljoin.aut.dao.object.AutWidgetDO;
import aljoin.aut.iservice.AutMenuService;
import aljoin.aut.iservice.AutWidgetService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 控件表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Controller
@RequestMapping("/aut/autWidget")
public class AutWidgetController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutWidgetController.class);
	@Resource
	private AutWidgetService autWidgetService;
	@Resource
	private AutMenuService autMenuService;

	/**
	 * 
	 * 控件表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/autWidgetPage")
	public String autWidgetPage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autWidgetPage";
	}

	/**
	 * 
	 * 控件表(分页列表).
	 *
	 * @return：Page<AutWidget>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件管理]-[搜索]")
	public Page<AutWidgetDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutWidgetDO obj) {
		Page<AutWidgetDO> page = null;
		try {
			page = autWidgetService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 控件表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件管理]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutWidget obj) {
		RetMsg retMsg = new RetMsg();

		try {
			if (StringUtils.isNotEmpty(obj.getMenuCode())) {
				// 二级
				obj.setWidgetCode(autMenuService.getNextCode(2, obj.getMenuCode(), true));
				// 查询二级菜单
				Where<AutMenu> where2 = new Where<AutMenu>();
				where2.eq("menu_code", obj.getMenuCode());
				AutMenu menu = autMenuService.selectOne(where2);
				obj.setMenuId(menu.getId());
			} else {
				// 一级
				obj.setWidgetCode(autMenuService.getNextCode(1, obj.getParentMenuCode(), true));
			}
			// 查询一级菜单
			Where<AutMenu> where = new Where<AutMenu>();
			where.eq("menu_code", obj.getParentMenuCode());
			AutMenu parentMenu = autMenuService.selectOne(where);
			obj.setParentMenuId(parentMenu.getId());
			autWidgetService.insert(obj);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 控件表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件管理]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutWidget obj) {
		RetMsg retMsg = new RetMsg();

		autWidgetService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 控件表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件管理]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutWidget obj) {
		RetMsg retMsg = new RetMsg();

		AutWidget orgnlObj = autWidgetService.selectById(obj.getId());
		orgnlObj.setIsActive(obj.getIsActive());
		orgnlObj.setWidgetName(obj.getWidgetName());

		autWidgetService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 控件表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件管理]-[详情]")
	public AutWidget getById(HttpServletRequest request, HttpServletResponse response, AutWidget obj) {
		return autWidgetService.selectById(obj.getId());
	}

}
