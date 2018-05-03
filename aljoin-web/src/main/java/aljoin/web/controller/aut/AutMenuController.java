package aljoin.web.controller.aut;

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

import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.object.AutMenuDO;
import aljoin.aut.iservice.AutMenuService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 菜单表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Controller
@RequestMapping("/aut/autMenu")
public class AutMenuController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutMenuController.class);
	@Resource
	private AutMenuService autMenuService;

	/**
	 * 
	 * 菜单表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/autMenuPage")
	public String autMenuPage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autMenuPage";
	}

	/**
	 * 
	 * 菜单表(分页列表).
	 *
	 * @return：Page<AutMenu>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单管理]-[搜索]")
	public Page<AutMenu> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutMenu obj) {
		Page<AutMenu> page = null;
		try {
			page = autMenuService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 菜单表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单管理]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutMenu obj) {
		RetMsg retMsg = new RetMsg();
		// 判断菜单名称是否存在
		try {
			Where<AutMenu> where = new Where<AutMenu>();
			where.eq("menu_name", obj.getMenuName());
			if (autMenuService.selectCount(where) > 0) {
				retMsg.setCode(1);
				retMsg.setMessage("菜单名称已被占用");
			} else {
				obj.setMenuCode(autMenuService.getNextCode(obj.getMenuLevel(), obj.getParentCode(), false));
				autMenuService.insert(obj);
				retMsg.setCode(0);
				retMsg.setMessage("操作成功");
			}
		} catch (Exception e) {
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 菜单表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单管理]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutMenu obj) {
		RetMsg retMsg = new RetMsg();

		// 如果是父节点，则子节则其下面的所有子节点也会被删除
		AutMenu autMenu = autMenuService.selectById(obj.getId());
		if (autMenu.getMenuLevel().intValue() == 1) {
			Where<AutMenu> where = new Where<AutMenu>();
			where.eq("parent_code", autMenu.getMenuCode());
			autMenuService.delete(where);
		}
		autMenuService.deleteById(obj.getId());
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 菜单表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单管理]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutMenu obj) {
		RetMsg retMsg = new RetMsg();

		// 检查菜单名称是否已被占用
		Where<AutMenu> where = new Where<AutMenu>();
		where.eq("menu_name", obj.getMenuName());
		where.ne("id", obj.getId());
		if (autMenuService.selectCount(where) > 0) {
			retMsg.setCode(1);
			retMsg.setMessage("菜单名称已被占用");
		} else {
			AutMenu orgnlObj = autMenuService.selectById(obj.getId());
			orgnlObj.setMenuName(obj.getMenuName());
			orgnlObj.setMenuHref(obj.getMenuHref());
			orgnlObj.setMenuIcon(obj.getMenuIcon());
			orgnlObj.setMenuRank(obj.getMenuRank());
			orgnlObj.setIsActive(obj.getIsActive());

			autMenuService.updateById(orgnlObj);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		}
		return retMsg;
	}

	/**
	 * 
	 * 菜单表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单管理]-[详情]")
	public AutMenuDO getById(HttpServletRequest request, HttpServletResponse response, AutMenu obj) {
		AutMenuDO autMenuDO = new AutMenuDO();
		AutMenu autMenu = autMenuService.selectById(obj.getId());
		autMenuDO.setAutMenu(autMenu);
		if (autMenu.getMenuLevel().intValue() == 2) {
			Where<AutMenu> where = new Where<AutMenu>();
			where.eq("menu_code", autMenu.getParentCode());
			autMenuDO.setParentMenu(autMenuService.selectOne(where));
		}
		return autMenuDO;
	}

	/**
	 * 
	 * 获取菜单以及其归属的控件
	 *
	 * @return：List<AutMenuVO>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年5月31日 下午7:32:51
	 */
	@RequestMapping("/getMenuList")
	@ResponseBody
	public List<AutMenu> getMenuList(HttpServletRequest request, HttpServletResponse response, AutMenu obj) {
		List<AutMenu> list = null;
		try {
			list = autMenuService.list(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return list;
	}

}
