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

import aljoin.aut.dao.entity.AutMenuRole;
import aljoin.aut.iservice.AutMenuRoleService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 菜单-角色表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Controller
@RequestMapping("/aut/autMenuRole")
public class AutMenuRoleController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutMenuRoleController.class);
	@Resource
	private AutMenuRoleService autMenuRoleService;

	/**
	 * 
	 * 菜单-角色表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/autMenuRolePage")
	public String autMenuRolePage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autMenuRolePage";
	}

	/**
	 * 
	 * 菜单-角色表(分页列表).
	 *
	 * @return：Page<AutMenuRole>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单角色管理]-[搜索]")
	public Page<AutMenuRole> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutMenuRole obj) {
		Page<AutMenuRole> page = null;
		try {
			page = autMenuRoleService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 菜单-角色表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单角色管理]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutMenuRole obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		autMenuRoleService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 菜单-角色表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单角色管理]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutMenuRole obj) {
		RetMsg retMsg = new RetMsg();

		autMenuRoleService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 菜单-角色表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单角色管理]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutMenuRole obj) {
		RetMsg retMsg = new RetMsg();

		AutMenuRole orgnlObj = autMenuRoleService.selectById(obj.getId());
		// orgnlObj.set...

		autMenuRoleService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 菜单-角色表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[菜单角色管理]-[详情]")
	public AutMenuRole getById(HttpServletRequest request, HttpServletResponse response, AutMenuRole obj) {
		return autMenuRoleService.selectById(obj.getId());
	}

}
