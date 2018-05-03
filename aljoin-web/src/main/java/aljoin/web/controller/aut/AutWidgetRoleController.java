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

import aljoin.aut.dao.entity.AutWidgetRole;
import aljoin.aut.iservice.AutWidgetRoleService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 控件-角色表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Controller
@RequestMapping("/aut/autWidgetRole")
public class AutWidgetRoleController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutWidgetRoleController.class);
	@Resource
	private AutWidgetRoleService autWidgetRoleService;
	
	/**
	 * 
	 * 控件-角色表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/autWidgetRolePage")
	public String autWidgetRolePage(HttpServletRequest request,HttpServletResponse response) {
		
		return "aut/autWidgetRolePage";
	}
	
	/**
	 * 
	 * 控件-角色表(分页列表).
	 *
	 * @return：Page<AutWidgetRole>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件角色管理]-[搜索]")
	public Page<AutWidgetRole> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutWidgetRole obj) {
		Page<AutWidgetRole> page = null;
		try {
			page = autWidgetRoleService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 控件-角色表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件角色管理]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutWidgetRole obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		autWidgetRoleService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 控件-角色表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件角色管理]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutWidgetRole obj) {
		RetMsg retMsg = new RetMsg();

		autWidgetRoleService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 控件-角色表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件角色管理]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutWidgetRole obj) {
		RetMsg retMsg = new RetMsg();

		AutWidgetRole orgnlObj = autWidgetRoleService.selectById(obj.getId());
		// orgnlObj.set...

		autWidgetRoleService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 控件-角色表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[控件角色管理]-[详情]")
	public AutWidgetRole getById(HttpServletRequest request, HttpServletResponse response, AutWidgetRole obj) {
		return autWidgetRoleService.selectById(obj.getId());
	}

}
