package aljoin.web.controller.aut;

import java.util.ArrayList;
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

import aljoin.aut.dao.entity.AutDepartmentRole;
import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.object.AutRoleDO;
import aljoin.aut.iservice.AutDepartmentRoleService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * (控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-04
 */
@Controller
@RequestMapping("/aut/autDepartmentRole")
public class AutDepartmentRoleController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutDepartmentRoleController.class);
	@Resource
	private AutDepartmentRoleService autDepartmentRoleService;
	@Resource
	AutRoleService autRoleService;

	/**
	 * 
	 * (页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-04
	 */
	@RequestMapping("/autDepartmentRolePage")
	public String autDepartmentRolePage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autDepartmentRolePage";
	}

	/**
	 * 
	 * (分页列表).
	 *
	 * @return：Page<AutDepartmentRole>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-04
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutDepartmentRole> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutDepartmentRole obj) {
		Page<AutDepartmentRole> page = null;
		try {
			page = autDepartmentRoleService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * [权限管理]-[部门管理]-[分配角色(操作)]
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-04
	 */
	@RequestMapping("/setAuth")
	@ResponseBody
	public RetMsg setAuth(HttpServletRequest request, HttpServletResponse response,AutDepartmentRole obj) {

		RetMsg retMsg = new RetMsg();

		int isActive = obj.getIsActive();
		long roleId = obj.getRoleId();
		long deptId = obj.getDeptId();
		try {
			retMsg = autDepartmentRoleService.setAuth(isActive, roleId, deptId);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	@RequestMapping("/getDepartmentRoleList")
	@ResponseBody
	public List<AutDepartmentRole> getDepartmentRoleList(AutDepartmentRole obj) throws Exception {
		List<AutDepartmentRole> departmentRoleList = autDepartmentRoleService.getDepartmentRoleList(obj);
		return departmentRoleList;

	}

	/**
	 * 
	 * [权限管理]-[部门管理]-[分配角色(查看)]
	 *
	 * @return：List<AutRoleDO>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月4日 下午3:42:32
	 */
	@RequestMapping("/roleList")
	@ResponseBody
	public List<AutRoleDO> roleList(HttpServletRequest request, HttpServletResponse response,AutDepartmentRole obj) {

		List<AutRoleDO> roleDOList = new ArrayList<AutRoleDO>();
		try {
			// 查询已分配到AutDepartmentRole表的记录
			AutDepartmentRole autDeptmentRole = new AutDepartmentRole();
			autDeptmentRole.setDeptId(obj.getDeptId());
			
			List<AutDepartmentRole> autDeptRoleList = autDepartmentRoleService.getDepartmentRoleList(autDeptmentRole);
			List<Long> deptRoleIdList = new ArrayList<Long>();
			for (AutDepartmentRole departmentRole : autDeptRoleList) {
				deptRoleIdList.add(departmentRole.getRoleId());
			}
			// 查询AutRole中的所有记录
			Where<AutRole> where = new Where<AutRole>();
			where.setSqlSelect("id,role_name,role_code,is_active");
			List<AutRole> autRoleList = autRoleService.selectList(where);
			// 关联表里有的role则setIsCheck(1)，否则setIsCheck(0)
			for (AutRole autRole : autRoleList) {
				AutRoleDO autRoleDO = new AutRoleDO();
				if (deptRoleIdList.contains(autRole.getId())) {
					autRoleDO.setIsCheck(1);
				} else {
					autRoleDO.setIsCheck(0);
				}
				autRoleDO.setAutRole(autRole);
				roleDOList.add(autRoleDO);
			}

		} catch (Exception e) {
		  logger.error("", e);
		}
		return roleDOList;

	}

}
