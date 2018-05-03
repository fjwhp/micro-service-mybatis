package aljoin.aut.security;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentRole;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutMenuRole;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutMenuVO;
import aljoin.aut.dao.object.AutRoleVO;
import aljoin.aut.dao.object.AutUserVO;
import aljoin.aut.iservice.AutDepartmentRoleService;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMenuRoleService;
import aljoin.aut.iservice.AutMenuService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;

/**
 * 
 * 用户服务
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:15:01
 */
@Service
public class UserService {

    @Resource
    private AutUserService autUserService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutMenuRoleService autMenuRoleService;
    @Resource
    private AutMenuService autMenuService;
    @Resource
    private AutDepartmentRoleService autDepartmentRoleService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutUserPubService autUserPubService;

    /**
     * 
     * 根据登录名获取用户
     *
     * @return：AutUserVO
     *
     * @author：zhongjy
     *
     * @date：2017年5月7日 下午3:51:32
     */
    public AutUserVO getUserByUserName(String userName) {
        AutUserVO user = null;
        Where<AutUser> autUserWhere = new Where<AutUser>();
        autUserWhere.eq("user_name", userName);
        autUserWhere.setSqlSelect(
            "id,user_name,user_pwd,is_active,is_account_expired,is_credentials_expired,is_account_locked,full_name");
        AutUser autUser = autUserService.selectOne(autUserWhere);
        if (autUser != null) {
            user = new AutUserVO();
            BeanUtils.copyProperties(autUser, user);
            List<AutRoleVO> roleList = new ArrayList<AutRoleVO>();
            Where<AutUserRole> where = new Where<AutUserRole>();
            where.eq("is_active", 1);
            where.eq("user_id", autUser.getId());
            where.setSqlSelect("role_code,role_name");
            // 用户角色
            List<AutUserRole> autUserRoleList = autUserRoleService.selectList(where);
            List<String> roleCodeList = new ArrayList<String>();
            for (AutUserRole autUserRole : autUserRoleList) {
                // 获取角色列表
                AutRoleVO autRoleVO = new AutRoleVO();
                autRoleVO.setRoleCode(autUserRole.getRoleCode());
                autRoleVO.setRoleName(autUserRole.getRoleName());
                roleList.add(autRoleVO);
                roleCodeList.add(autUserRole.getRoleCode());
            }
            // 部门用户
            Where<AutDepartmentUser> whereDeptUser = new Where<AutDepartmentUser>();
            whereDeptUser.eq("user_id", autUser.getId());
            whereDeptUser.eq("is_active", 1);
            whereDeptUser.setSqlSelect("dept_id");
            List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(whereDeptUser);
            if (deptUserList.size() > 0) {
                List<Long> deptIdList = new ArrayList<Long>();
                String autDeptIds = "";
                String autDeptNames = "";
                for (AutDepartmentUser du : deptUserList) {
                    deptIdList.add(du.getDeptId());
                    autDeptIds += du.getDeptId() + ",";
                }
                Where<AutDepartmentRole> whereDeptRole = new Where<AutDepartmentRole>();
                whereDeptRole.eq("is_active", 1);
                whereDeptRole.in("dept_id", deptIdList);
                whereDeptRole.setSqlSelect("role_code");
                // 部门角色
                List<AutDepartmentRole> autDepartmentRoleList = autDepartmentRoleService.selectList(whereDeptRole);
                if (!autDepartmentRoleList.isEmpty()) {
                    for (AutDepartmentRole autDepartmentRole : autDepartmentRoleList) {
                        // 获取角色列表
                        AutRoleVO autRoleVO = new AutRoleVO();
                        autRoleVO.setRoleCode(autDepartmentRole.getRoleCode());
                        roleList.add(autRoleVO);
                        roleCodeList.add(autDepartmentRole.getRoleCode());
                    }
                }
                // 获取部门信息
                Where<AutDepartment> whereDept = new Where<AutDepartment>();
                whereDept.in("id", deptIdList);
                whereDept.setSqlSelect("dept_name");
                List<AutDepartment> autDepartmentList = autDepartmentService.selectList(whereDept);
                for (AutDepartment autDepartment : autDepartmentList) {
                    autDeptNames += autDepartment.getDeptName() + "/";
                }
                if(StringUtils.isNotEmpty(autDeptIds) && StringUtils.isNotEmpty(autDeptNames)) {
                    user.setAutDeptIds(autDeptIds.substring(0, autDeptIds.length() - 1));
                    user.setAutDeptNames(autDeptNames.substring(0, autDeptNames.length() - 1));
                }
            }

            user.setRoleList(roleList);

            // 菜单列表
            List<AutMenuVO> autMenuVOList = new ArrayList<AutMenuVO>();
            if (roleCodeList.size() > 0) {
                // 根据角色列表获取菜单数据:角色->菜单ID->菜单
                Where<AutMenuRole> where2 = new Where<AutMenuRole>();
                where2.eq("is_active", 1);
                where2.in("role_code", roleCodeList);
                where2.setSqlSelect("menu_id");
                List<AutMenuRole> autMenuRoleList = autMenuRoleService.selectList(where2);
                List<Long> menuIdList = new ArrayList<Long>();
                for (AutMenuRole autMenuRole : autMenuRoleList) {
                    menuIdList.add(autMenuRole.getMenuId());
                }
                Where<AutMenu> where3 = new Where<AutMenu>();
                where3.eq("is_active", 1);
                where3.in("id", menuIdList);
                where3.orderBy("menu_rank", true);
                where3.setSqlSelect("id,menu_code,menu_level,menu_name,parent_code,menu_icon,menu_href,menu_rank");
                List<AutMenu> autMenuList = autMenuService.selectList(where3);
                List<AutMenu> autMenuChildList = new ArrayList<AutMenu>();

                for (AutMenu autMenu : autMenuList) {
                    if (autMenu.getMenuLevel().intValue() == 1) {
                        // 一级菜单
                        AutMenuVO autMenuVO = new AutMenuVO();
                        BeanUtils.copyProperties(autMenu, autMenuVO);
                        autMenuVOList.add(autMenuVO);
                    } else {
                        // 二级菜单
                        autMenuChildList.add(autMenu);
                    }
                }
                // 把二级菜单放到以及菜单中
                for (AutMenuVO autMenuVO : autMenuVOList) {
                    List<AutMenuVO> children = new ArrayList<AutMenuVO>();
                    for (AutMenu autMenu : autMenuChildList) {
                        if (autMenuVO.getMenuCode().equals(autMenu.getParentCode())) {
                            AutMenuVO child = new AutMenuVO();
                            BeanUtils.copyProperties(autMenu, child);
                            children.add(child);
                        }
                    }
                    autMenuVO.setChildren(children);
                }
            }
            user.setMenuList(autMenuVOList);
            // 获取用户头像
            Where<AutUserPub> userPubWhere = new Where<AutUserPub>();
            userPubWhere.eq("user_id", user.getId());
            userPubWhere.setSqlSelect("user_icon");
            AutUserPub userPub = autUserPubService.selectOne(userPubWhere);
            if(userPub != null) {
                user.setUserIcon(userPub.getUserIcon());
            }
        }
        return user;
    }

    public void ser1() {
        System.out.println("这是ser1...");
    }

    public void ser2() {
        System.out.println("这是ser2...");
    }
}
