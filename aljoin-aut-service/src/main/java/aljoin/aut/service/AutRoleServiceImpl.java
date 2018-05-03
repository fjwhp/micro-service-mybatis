package aljoin.aut.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutDepartmentRole;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.mapper.AutRoleMapper;
import aljoin.aut.dao.object.AutRoleDO;
import aljoin.aut.dao.object.AutRoleVO;
import aljoin.aut.dao.object.AutUserRoleVO;
import aljoin.aut.iservice.AutDepartmentRoleService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 角色表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
@Service
public class AutRoleServiceImpl extends ServiceImpl<AutRoleMapper, AutRole> implements AutRoleService {

    private final static Logger logger = LoggerFactory.getLogger(AutRoleServiceImpl.class);

    @Resource
    private ActActivitiService activitiService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private IdentityService identityService;
    @Resource
    private AutDepartmentRoleService autDepartmentRoleService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;

    @Override
    public Page<AutRole> list(PageBean pageBean, AutRole obj) throws Exception {
        Where<AutRole> where = new Where<AutRole>();
        if (StringUtils.isNotEmpty(obj.getRoleName())) {
            where.like("role_name", obj.getRoleName());
            where.or("role_code LIKE {0}", "%" + obj.getRoleName() + "%");
        }
        where.orderBy("role_rank", true);
        Page<AutRole> page = selectPage(new Page<AutRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    @Transactional
    public RetMsg add(AutRole obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 检查角色名称和角色代码是否已经存在
        Where<AutRole> where = new Where<AutRole>();
        where.eq("role_name", obj.getRoleName());
        where.or("role_code = {0}", obj.getRoleCode());
        if (selectCount(where) > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("角色名称或代码已存在");
        } else {
            insert(obj);
            if (obj.getIsActive() == 1) {
                // 同步activiti分组
                activitiService.addGroup(obj.getId());
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(AutRole obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        deleteById(obj.getId());
        // 删除act_id_group 角色信息
        activitiService.delGroup(obj.getId());
        Where<AutUserRole> userRoleWhere = new Where<AutUserRole>();
        userRoleWhere.eq("role_id", obj.getId());
        userRoleWhere.eq("is_active", 1);
        userRoleWhere.setSqlSelect("id,role_id,user_id");
        List<AutUserRole> autUserRoleList = autUserRoleService.selectList(userRoleWhere);

        if (null != autUserRoleList && !autUserRoleList.isEmpty()) {
            for (AutUserRole userRole : autUserRoleList) {
                if (null != userRole && null != userRole.getUserId() && null != userRole.getRoleId()) {
                    // 删除act_id_membership 角色和用户信息
                    activitiService.delUserGroup(userRole.getUserId(), userRole.getRoleId());
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public Page<AutRoleDO> roleList(PageBean pageBean, AutRoleVO obj) throws Exception {
        Where<AutRole> where = new Where<AutRole>();
        Page<AutRoleDO> page = new Page<AutRoleDO>();
        if (StringUtils.isNotEmpty(obj.getRoleName())) {
            where.like("role_name", obj.getRoleName());
            where.or("role_code LIKE {0}", "%" + obj.getRoleName() + "%");
        }
        if (null != obj.getIsActive()) {
            where.eq("is_active", obj.getIsActive());
        }

        where.orderBy("create_time", false);
        where.setSqlSelect("id,role_name,role_code,is_active,create_time");
        List<Long> idList = new ArrayList<Long>();
        if (null != obj.getIdList()) {
            if (!obj.getIdList().isEmpty()) {
                idList = obj.getIdList();
            }
        }
        Page<AutRole> page2 = selectPage(new Page<AutRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        // List<AutRole> roleList = selectList(where);
        List<AutRole> roleList = page2.getRecords();
        List<AutRoleDO> roleDOList = new ArrayList<AutRoleDO>();
        if (!roleList.isEmpty()) {
            for (AutRole role : roleList) {
                AutRoleDO roleDO = new AutRoleDO();
                roleDO.setAutRole(role);
                roleDO.setIsCheck(0);
                if (idList.contains(role.getId())) {
                    roleDO.setIsCheck(1);
                } else {
                    roleDO.setIsCheck(0);
                }
                roleDOList.add(roleDO);
            }
        }
        page.setRecords(roleDOList);
        page.setTotal(page2.getTotal());
        page.setSize(page2.getSize());
        return page;
    }

    @Override
    public Page<AutUserRole> getUserByRoleId(PageBean pageBean, AutRole autRole) throws Exception {

        Where<AutUserRole> where = new Where<AutUserRole>();
        List<Long> userIds = new ArrayList<Long>();
        where.setSqlSelect("id,user_id,user_name,user_role_rank");
        where.eq("role_id", autRole.getId());
        where.eq("is_active", 1);
        where.orderBy("user_role_rank", true);
        List<AutUserRole> autUserRoles = autUserRoleService.selectList(where);
        for (AutUserRole autUserRole : autUserRoles) {
            userIds.add(autUserRole.getUserId());
        }
        Where<AutUser> userwhere = new Where<AutUser>();
        userwhere.in("id", userIds);
        List<AutUser> users = autUserService.selectList(userwhere);
        Page<AutUserRole> page =
            autUserRoleService.selectPage(new Page<AutUserRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        for (AutUser autUser : users) {
            for (int i = 0; i < page.getRecords().size(); i++) {
                if (autUser.getId().equals(page.getRecords().get(i).getUserId())) {
                    page.getRecords().get(i).setUserName(autUser.getFullName());
                }
            }
        }
        List<AutUserRole> userRoles = page.getRecords();
        List<AutUser> userList = new ArrayList<AutUser>();
        for (AutUserRole autUserRole : userRoles) {
            userIds.add(autUserRole.getUserId());
        }
        if (null != userIds && !userIds.isEmpty()) {
            Where<AutUser> autUserwhere = new Where<AutUser>();
            autUserwhere.setSqlSelect("id,user_name,full_name");
            autUserwhere.in("id", userIds);
            userList = autUserService.selectList(autUserwhere);
        }
        for (AutUserRole autUserRole : userRoles) {
            for (AutUser autUser : userList) {
                if (autUserRole.getUserId().equals(autUser.getId())) {
                    autUserRole.setUserName(autUser.getFullName());
                }
            }
        }
        return page;
    }

    @Override
    public RetMsg updateRoleUserRankList(AutUserRoleVO autUserRoleVO) {
        RetMsg retMsg = new RetMsg();
        try {
            // 收到前端List
            List<AutUserRole> autUserRoleList = autUserRoleVO.getAutUserRoleList();
            // 根据前端传来的List，获得idList，用于查询关联表的所有数据
            List<Long> idList = new ArrayList<Long>();
            for (AutUserRole autUserRole : autUserRoleList) {
                idList.add(autUserRole.getId());
            }
            // 根据idList查询出关联表的所有数据orgnlObjList
            Where<AutUserRole> where = new Where<AutUserRole>();
            where.in("id", idList);
            // 数据表查询到的数据(按该数据的新增时间排序)
            List<AutUserRole> orgnlObjList = autUserRoleService.selectList(where);
            // 比对传过来的List和表里查到的List，如果id相同(是同一条数据)，则更新排序字段
            if (autUserRoleList != null && !autUserRoleList.isEmpty() && orgnlObjList != null
                && !orgnlObjList.isEmpty()) {
                for (int i = 0; i < autUserRoleList.size(); i++) {
                    for (int j = 0; j < orgnlObjList.size(); j++) {
                        if (autUserRoleList.get(i).getId() == orgnlObjList.get(j).getId()
                            || autUserRoleList.get(i).getId().equals(orgnlObjList.get(j).getId())) {
                            orgnlObjList.get(j).setUserRoleRank(autUserRoleList.get(i).getUserRoleRank());
                        }
                    }
                }
            }
            // 批量更新关联表数据的排序
            autUserRoleService.updateBatchById(orgnlObjList);
            retMsg.setCode(0);
            retMsg.setMessage("更新成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("更新失败");
        }
        return retMsg;
    }

    @Override
    public AutUserRoleVO getAutUserRoloVo(AutRole autRole) throws Exception {

        Where<AutUserRole> where = new Where<AutUserRole>();
        where.setSqlSelect("id,user_id,user_name,user_role_rank");
        where.eq("role_id", autRole.getId());
        where.orderBy("user_role_rank", true);
        // 根据角色id查询到该角色下的用户List
        List<AutUserRole> autUserRoleList = autUserRoleService.selectList(where);

        // 获得用户id，根据这个idList查找aut_user表，以获得full_name
        List<Long> userIdList = new ArrayList<Long>();
        for (AutUserRole autUserRole : autUserRoleList) {
            userIdList.add(autUserRole.getUserId());
        }
        // 根据用户idList查询到的用户(包含full_name)
        Where<AutUser> w2 = new Where<AutUser>();
        w2.setSqlSelect("id,user_name,full_name,user_email,is_delete,is_active");
        w2.in("id", userIdList);
        List<AutUser> autUserList = autUserService.selectList(w2);

        List<AutUserRole> autUserRoleList2 = new ArrayList<AutUserRole>();
        List<AutUser> autUserList2 = new ArrayList<AutUser>();
        AutUserRoleVO autUserRoleVo = new AutUserRoleVO();

        for (int i = 0; i < autUserRoleList.size(); i++) {
            for (int j = 0; j < autUserList.size(); j++) {
                if (autUserRoleList.get(i).getUserId() == autUserList.get(j).getId()
                    || autUserRoleList.get(i).getUserId().equals(autUserList.get(j).getId())) {
                    autUserRoleList2.add(autUserRoleList.get(i));
                    autUserList2.add(autUserList.get(j));
                }
            }
        }
        autUserRoleVo.setAutUserList(autUserList2);
        autUserRoleVo.setAutUserRoleList(autUserRoleList2);

        return autUserRoleVo;
    }

    @Override
    public List<Long> getRoleIdByUserId(Long userId) throws Exception {
        List<Long> roleIdList = new ArrayList<Long>();
        
        Where<AutUserRole> where = new Where<AutUserRole>();
        where.eq("is_active", 1);
        where.eq("user_id", userId);
        where.setSqlSelect("role_id");
        // 用户角色
        List<AutUserRole> autUserRoleList = autUserRoleService.selectList(where);
        for (AutUserRole autUserRole : autUserRoleList) {
            roleIdList.add(autUserRole.getRoleId());
        }
        //用户-部门-角色
        Where<AutDepartmentUser> whereDeptUser = new Where<AutDepartmentUser>();
        whereDeptUser.eq("user_id", userId);
        whereDeptUser.eq("is_active", 1);
        whereDeptUser.setSqlSelect("dept_id");
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(whereDeptUser);
        if (deptUserList.size() > 0) {
            List<Long> deptIdList = new ArrayList<Long>();
            for (AutDepartmentUser du : deptUserList) {
                deptIdList.add(du.getDeptId());
            }
            Where<AutDepartmentRole> whereDeptRole = new Where<AutDepartmentRole>();
            whereDeptRole.eq("is_active", 1);
            whereDeptRole.in("dept_id", deptIdList);
            whereDeptRole.setSqlSelect("role_id");
            // 部门角色
            List<AutDepartmentRole> autDepartmentRoleList = autDepartmentRoleService.selectList(whereDeptRole);
            for (AutDepartmentRole autDepartmentRole : autDepartmentRoleList) {
                roleIdList.add(autDepartmentRole.getRoleId());
            }
        }
        return roleIdList;
    }
}
