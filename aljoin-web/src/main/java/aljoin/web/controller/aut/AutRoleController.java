package aljoin.web.controller.aut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutMenuRole;
import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.entity.AutWidget;
import aljoin.aut.dao.entity.AutWidgetRole;
import aljoin.aut.dao.object.AutMenuDO;
import aljoin.aut.dao.object.AutUserRoleVO;
import aljoin.aut.dao.object.AutWidgetDO;
import aljoin.aut.iservice.AutMenuRoleService;
import aljoin.aut.iservice.AutMenuService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutWidgetRoleService;
import aljoin.aut.iservice.AutWidgetService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 角色表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
@Controller
@RequestMapping("/aut/autRole")
public class AutRoleController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutRoleController.class);
	@Resource
	private AutRoleService autRoleService;
	@Resource
	private AutMenuService autMenuService;
	@Resource
	private AutWidgetService autWidgetService;
	@Resource
	private AutMenuRoleService autMenuRoleService;
	@Resource
	private AutWidgetRoleService autWidgetRoleService;
	@Resource
	private ActActivitiService activitiService;
	@Resource
	private AutUserRoleService autUserRoleService;

	@Resource
	private IdentityService identityService;
	/**
	 * 
	 * 角色表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/autRolePage")
	public String autRolePage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autRolePage";
	}

	/**
	 * 
	 * 角色表(分页列表).
	 *
	 * @return：Page<AutRole>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[搜索]")
	public Page<AutRole> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutRole obj) {
		Page<AutRole> page = null;
		try {
			page = autRoleService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 角色表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutRole obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autRoleService.add(obj);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 角色表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutRole obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autRoleService.delete(obj);
			Where<AutMenuRole> where = new Where<AutMenuRole>();
			where.eq("role_id", obj.getId());
			where.eq("is_active", 1);
			Where<AutUserRole> where1 = new Where<AutUserRole>();
            where1.eq("role_id", obj.getId());
            where1.eq("is_active", 1);
			List<AutUserRole> userList = autUserRoleService.selectList(where1);
			List<AutMenuRole> list = autMenuRoleService.selectList(where);
			for (AutMenuRole autMenuRole : list) {
			  autMenuRole.setIsActive(0);
            }
			for (AutUserRole autUserRole : userList) {
			  autUserRole.setIsActive(0);
            }
			if(list.size() != 0) {
			  autMenuRoleService.updateBatchById(list);
			}
			if(userList.size() != 0) {
              autUserRoleService.updateBatchById(userList);
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
	 * 角色表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[修改]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutRole obj) {
		RetMsg retMsg = new RetMsg();

		// 查询是否有其他相同的角色名称和代码
		Where<AutRole> where = new Where<AutRole>();
		where.eq("role_name", obj.getRoleName());
		where.or("role_code = {0}", obj.getRoleCode());
		where.and().ne("id", obj.getId());
		if (autRoleService.selectCount(where) > 1) {
			retMsg.setCode(1);
			retMsg.setMessage("角色名称或代码已存在");
		} else {
			AutRole orgnlObj = autRoleService.selectById(obj.getId());
			orgnlObj.setRoleName(obj.getRoleName());
			orgnlObj.setRoleCode(obj.getRoleCode());
			orgnlObj.setIsActive(obj.getIsActive());
			orgnlObj.setRoleRank(obj.getRoleRank());
			autRoleService.updateById(orgnlObj);

			Where<AutUserRole> userRoleWhere = new Where<AutUserRole>();
			userRoleWhere.eq("role_id",orgnlObj.getId());
			userRoleWhere.setSqlSelect("id,role_id,user_id");
			List<AutUserRole> autUserRoleList = autUserRoleService.selectList(userRoleWhere);
			if(orgnlObj.getIsActive() == 0 ){
				//删除act_id_group 角色信息
				activitiService.delGroup(orgnlObj.getId());
				if(null != autUserRoleList && !autUserRoleList.isEmpty()){
					for(AutUserRole userRole : autUserRoleList){
						if(null != userRole && null != userRole.getUserId() && null != userRole.getRoleId()){
							//删除act_id_membership 角色和用户信息
							activitiService.delUserGroup(userRole.getUserId(),userRole.getRoleId());
						}
					}
				}
			}else{
				activitiService.delGroup(orgnlObj.getId());
				//act_id_group 新增角色信息
				activitiService.addGroup(orgnlObj.getId());//先删除再新增以免主键冲突

				if(null != autUserRoleList && !autUserRoleList.isEmpty()){
					List<Group> groupList = identityService.createGroupQuery().list();
					List<Long> groupIdList = new ArrayList<Long>();
					if(null != groupList && !groupList.isEmpty()){
						for(Group gp : groupList){
							if(null != gp && StringUtils.isNotEmpty(gp.getId())){
								groupIdList.add(Long.parseLong(gp.getId()));
							}
						}
					}
					for(AutUserRole userRole : autUserRoleList){
						if(null != userRole && null != userRole.getUserId() && null != userRole.getRoleId()){
							if(!groupIdList.contains(userRole.getRoleId())){
								//act_id_membership 新增角色和用户信息
								activitiService.delUserGroup(userRole.getUserId(),userRole.getRoleId());
								activitiService.addUserGroup(userRole.getUserId(),userRole.getRoleId());//先删除再新增以免主键冲突
							}
						}
					}
				}
			}

			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		}
		return retMsg;
	}

	/**
	 * 
	 * 角色表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[详情]")
	public AutRole getById(HttpServletRequest request, HttpServletResponse response, AutRole obj) {
		return autRoleService.selectById(obj.getId());
	}

	/**
	 * 
	 * 菜单表,含菜单下的控件(分页列表).
	 *
	 * @return：Page<AutMenu>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/menuWidgetList")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[授权(查看)]")
	public Page<AutMenuDO> menuWidgetList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutMenu obj) {
	    CustomUser cu = getCustomDetail();
		Page<AutMenuDO> page = new Page<AutMenuDO>();
		long roleId = Long.parseLong(request.getParameter("roleId"));
		try {
		    //根据角色获取菜单
			Where<AutMenuRole> w1 = new Where<AutMenuRole>();
			w1.eq("role_id", roleId).eq("is_active", 1);
			w1.setSqlSelect("menu_id");
			List<AutMenuRole> autMenuRoleList = autMenuRoleService.selectList(w1);
			Set<Long> menuIdSet = new HashSet<Long>();
			for (AutMenuRole autMenuRole : autMenuRoleList) {
				menuIdSet.add(autMenuRole.getMenuId());
			}
			//根据角色获取看控件
			Where<AutWidgetRole> w2 = new Where<AutWidgetRole>();
			w2.eq("role_id", roleId).eq("is_active", 1);
			w2.setSqlSelect("widget_id");
			List<AutWidgetRole> autWidgetRoleList = autWidgetRoleService.selectList(w2);
			Set<Long> widgetIdSet = new HashSet<Long>();
			for (AutWidgetRole autWidgetRole : autWidgetRoleList) {
				widgetIdSet.add(autWidgetRole.getWidgetId());
			}

			//获取菜单分页对象，注意：只能获取自己具有权限的菜单(sadmin用户除外-超级管理员具有一切权限)
			Page<AutMenu> menuPage = null;
			if(cu.getUsername().equals("sadmin")) {
			    menuPage = autMenuService.list(pageBean, obj);
			}else {
			     menuPage = autMenuService.listByUserId(pageBean, obj, cu.getUserId());
			}
			
			List<AutMenu> menuList = menuPage.getRecords();
			
			//获取自己有权限的控件（非超级管理员蔡去查找）
			Set<Long> myWidgetIdSet = null;
			if(!cu.getUsername().equals("sadmin")) {
			    List<Long> myRoleIdList = autRoleService.getRoleIdByUserId(cu.getUserId());
	            Where<AutWidgetRole> w3 = new Where<AutWidgetRole>();
	            w3.in("role_id", myRoleIdList).eq("is_active", 1);
	            w3.setSqlSelect("widget_id");
	            List<AutWidgetRole> autWidgetRoleList2 = autWidgetRoleService.selectList(w3);
	            myWidgetIdSet = new HashSet<Long>();
	            for (AutWidgetRole autWidgetRole : autWidgetRoleList2) {
	                myWidgetIdSet.add(autWidgetRole.getWidgetId());
	            }
			}
			

			page.setCurrent(menuPage.getCurrent());
			page.setSize(menuPage.getSize());
			page.setTotal(menuPage.getTotal());
			//定义返回结果了列表
			List<AutMenuDO> voList = new ArrayList<AutMenuDO>();
			for (AutMenu autMenu : menuList) {
				AutMenuDO menuDO = new AutMenuDO();
				if (menuIdSet.contains(autMenu.getId())) {
					menuDO.setIsCheck(1);
				} else {
					menuDO.setIsCheck(0);
				}
				menuDO.setAutMenu(autMenu);
				// 根据菜单查询其控件
				Where<AutWidget> where = new Where<AutWidget>();
				where.eq("menu_id", autMenu.getId());
				List<AutWidget> widgetList = autWidgetService.selectList(where);
				List<AutWidgetDO> widgetDOList = new ArrayList<AutWidgetDO>();
				for (AutWidget autWidget : widgetList) {
				    //必须是自己有权限的控件才显示出来（超级管理员除外）
				    if(cu.getUsername().equals("sadmin")) {
				        //超级管理员，显示所有控件
				        AutWidgetDO autWidgetDO = new AutWidgetDO();
                        autWidgetDO.setAutWidget(autWidget);
                        if (widgetIdSet.contains(autWidget.getId())) {
                            autWidgetDO.setIsCheck(1);
                        } else {
                            autWidgetDO.setIsCheck(0);
                        }
                        widgetDOList.add(autWidgetDO);
				    }else {
				        //非超级管理员，只显示有权限的
				        if(myWidgetIdSet.contains(autWidget.getId())) {
				            AutWidgetDO autWidgetDO = new AutWidgetDO();
	                        autWidgetDO.setAutWidget(autWidget);
	                        if (widgetIdSet.contains(autWidget.getId())) {
	                            autWidgetDO.setIsCheck(1);
	                        } else {
	                            autWidgetDO.setIsCheck(0);
	                        }
	                        widgetDOList.add(autWidgetDO);
	                    }
				    }
				}
				menuDO.setWidgetList(widgetDOList);
				voList.add(menuDO);
			}
			page.setRecords(voList);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 菜单及控件授权
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-05-27
	 */
	@RequestMapping("/setAuth")
	@ResponseBody
	@FuncObj(desc = "[权限管理]-[角色管理]-[授权(操作)]")
	public RetMsg setAuth(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		/*CustomUser custom = getCustomDetail();
		AutUser user = new AutUser();
		user.setId(custom.getUserId());
		user.setFullName(custom.getNickName());
		Where<AutUserRole> where1 = new Where<AutUserRole>();
		where1.eq("user_id", user.getId());
		where1.setSqlSelect("role_code");
		AutUserRole autUser = autUserRoleService.selectOne(where1);*/
		int isActive = Integer.parseInt(request.getParameter("isCheck"));
		Long id = Long.parseLong(request.getParameter("id"));
		String code = request.getParameter("id");
		String type = request.getParameter("type");
		Long roleId = Long.parseLong(request.getParameter("roleId"));
		AutRole role = autRoleService.selectById(roleId);
		if(!"SUPER_ADMIN".equals(role.getRoleCode()) || (!("940039106940477441").equals(code) && "SUPER_ADMIN".equals(role.getRoleCode()))){
		  if ("1".equals(type)) {
		    // 菜单权限
		    Where<AutMenuRole> where = new Where<AutMenuRole>();
		    where.eq("menu_id", id);
		    where.eq("role_id", roleId);
		    AutMenuRole autMenuRole = autMenuRoleService.selectOne(where);
		    if (autMenuRole == null) {
		      AutMenu autMenu = autMenuService.selectById(id);
		      AutRole autRole = autRoleService.selectById(roleId);
		      autMenuRole = new AutMenuRole();
		      autMenuRole.setMenuCode(autMenu.getMenuCode());
		      autMenuRole.setMenuId(autMenu.getId());
		      autMenuRole.setIsActive(isActive);
		      autMenuRole.setRoleName(autRole.getRoleName());
		      autMenuRole.setRoleCode(autRole.getRoleCode());
		      autMenuRole.setRoleId(autRole.getId());
		      autMenuRoleService.insert(autMenuRole);
		    } else {
		      autMenuRole.setIsActive(isActive);
		      autMenuRoleService.updateById(autMenuRole);
		    }
		  } else {
		    // 控件权限
		    Where<AutWidgetRole> where = new Where<AutWidgetRole>();
		    where.eq("widget_id", id);
		    where.eq("role_id", roleId);
		    AutWidgetRole autWidgetRole = autWidgetRoleService.selectOne(where);
		    if (autWidgetRole == null) {
		      AutWidget autWidget = autWidgetService.selectById(id);
		      AutRole autRole = autRoleService.selectById(roleId);
		      autWidgetRole = new AutWidgetRole();
		      autWidgetRole.setWidgetCode(autWidget.getWidgetCode());
		      autWidgetRole.setWidgetId(autWidget.getId());
		      autWidgetRole.setRoleId(autRole.getId());
		      autWidgetRole.setRoleCode(autRole.getRoleCode());
		      autWidgetRole.setRoleName(autRole.getRoleName());
		      autWidgetRole.setIsActive(isActive);
		      autWidgetRoleService.insert(autWidgetRole);
		    } else {
		      autWidgetRole.setIsActive(isActive);
		      autWidgetRoleService.updateById(autWidgetRole);
		    }
		}
		  retMsg.setCode(0);
		  retMsg.setMessage("操作成功");
		}else {
		  retMsg.setCode(1);
          retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 角色列表
	 *
	 * @return：List<AutRole>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月4日 下午4:07:26
	 */
	@RequestMapping("/selectRoleList")
	@ResponseBody
	public List<AutRole> selectRoleList(){
		Where<AutRole> where = new Where<AutRole>();
		where.setSqlSelect("id,role_name,role_code,is_active，role_id");
		where.orderBy("role_rank");
		List<AutRole> roleList = autRoleService.selectList(where);
		return roleList;
		
	}
	
	/**
	 * 
	 * @throws Exception 
	 * [权限管理]-[角色管理]-[查看角色下的用户(分页)]
	 *
	 * @return：List<AutUserRole>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月13日 下午1:59:29
	 */
	@RequestMapping("/getUserByRoleId")
	@ResponseBody
	public Page <AutUserRole> getUserByRoleId(PageBean pageBean,AutRole autRole) throws Exception{
		Page<AutUserRole> page = null;
		
		page = autRoleService.getUserByRoleId(pageBean,autRole);
		
		return page;
		
	}
	
	/**
	 * 
	 * [权限管理]-[角色管理]-[查看角色下的用户]
	 * 包含aut_user表里的fullName和关联表里的用户角色排序userRoleRank
	 *
	 * @return：AutUserRoleVO
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月14日 下午4:22:21
	 */
	@RequestMapping("/getAutUserRoloVo")
	@ResponseBody
	public AutUserRoleVO getAutUserRoloVo(AutRole autRole) throws Exception{
		
		AutUserRoleVO autUserVo = autRoleService.getAutUserRoloVo(autRole);
		
		return autUserVo;
		
	}
	
	/**
	 * 
	 * [权限管理]-[角色管理]-[更新角色下部门排序]
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月13日 下午4:42:09
	 */
	@RequestMapping("/updateRoleUserRankList")
	@ResponseBody
	public RetMsg updateRoleUserRank(AutUserRoleVO autUserRoleVO){
		
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autRoleService.updateRoleUserRankList(autUserRoleVO);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return retMsg;
		
	}
	
	/**
	 * 
	 * [权限管理]-[角色管理]-[更新角色下部门排序]
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月18日 上午9:21:21
	 */
	@RequestMapping("/updateRoleUserRank")
	@ResponseBody
	public RetMsg updateRoleUserRank(AutUserRole obj){
		
		RetMsg retMsg = new RetMsg();
		try {
			AutUserRole orgnlObj = autUserRoleService.selectById(obj.getId());
			orgnlObj.setUserRoleRank(obj.getUserRoleRank());
			autUserRoleService.updateById(orgnlObj);
			retMsg.setCode(0);
			retMsg.setMessage("更新成功");
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

}
