package aljoin.aut.service;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentRole;
import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.mapper.AutDepartmentRoleMapper;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.iservice.AutDepartmentRoleService;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutUserRoleService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * (服务实现类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-04
 */
@Service
public class AutDepartmentRoleServiceImpl extends ServiceImpl<AutDepartmentRoleMapper, AutDepartmentRole>
    implements AutDepartmentRoleService {

    @Resource
    AutDepartmentRoleService autDepartmentRoleService;
    @Resource
    AutDepartmentService autDepartmentService;
    @Resource
    AutRoleService autRoleService;
    @Resource
    AutDepartmentUserService autDepartmentUserService;
    @Resource
    AutUserRoleService autUserRoleService;

    @Override
    public Page<AutDepartmentRole> list(PageBean pageBean, AutDepartmentRole obj) throws Exception {
        Where<AutDepartmentRole> where = new Where<AutDepartmentRole>();
        where.orderBy("create_time", false);
        Page<AutDepartmentRole> page =
            selectPage(new Page<AutDepartmentRole>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public RetMsg setAuth(int isActive, long roleId, long deptId) throws Exception {

        RetMsg retMsg = new RetMsg();
        Where<AutDepartmentRole> where = new Where<AutDepartmentRole>();
        where.eq("role_id", roleId);
        where.eq("dept_id", deptId);
        AutDepartment obj = new AutDepartment();
        obj.setId(deptId);
        AutDepartmentRole autDepartmentRole = autDepartmentRoleService.selectOne(where);

        if (autDepartmentRole == null) {
            AutDepartment autDepartment = autDepartmentService.selectById(deptId);
            AutRole autRole = autRoleService.selectById(roleId);
            autDepartmentRole = new AutDepartmentRole();
            autDepartmentRole.setDeptId(deptId);
            autDepartmentRole.setDeptCode(autDepartment.getDeptCode());
            autDepartmentRole.setRoleCode(autRole.getRoleCode());
            autDepartmentRole.setRoleId(roleId);
            autDepartmentRole.setIsActive(isActive);
            autDepartmentRole.setDepartmentRoleRank(10);

            autDepartmentRoleService.insert(autDepartmentRole);

            AutDepartmentUserVO vo = autDepartmentUserService.getUserByDeptId(obj);
            List<AutUserRole> userRoleList = new ArrayList<AutUserRole>();
            for (AutUser autuser : vo.getAutUserList()) {
                AutUserRole autUserRole = new AutUserRole();
                autUserRole.setUserId(autuser.getId());
                autUserRole.setUserName(autuser.getUserName());
                autUserRole.setRoleId(roleId);
                AutRole roleName = autRoleService.selectById(roleId);
                autUserRole.setRoleName(roleName.getRoleName());
                autUserRole.setIsActive(isActive);
                autUserRole.setRoleCode(autRole.getRoleCode());
                autUserRole.setUserRoleRank(1);
                userRoleList.add(autUserRole);
            }
            autUserRoleService.insertBatch(userRoleList);
        } else {
            autDepartmentRole.setIsActive(isActive);
            autDepartmentRoleService.updateById(autDepartmentRole);
            AutDepartmentUserVO vo = autDepartmentUserService.getUserByDeptId(obj);
            // List<AutUserRole> list = new ArrayList<AutUserRole>();
            for (AutUser autuser : vo.getAutUserList()) {
                Long userId = autuser.getId();
                Where<AutUserRole> w1 = new Where<AutUserRole>();
                w1.eq("role_id", roleId);
                w1.eq("user_id", userId);
                AutUserRole autUserRole = autUserRoleService.selectOne(w1);
                if (autUserRole != null) {
                    autUserRole.setIsActive(isActive);
                    autUserRoleService.updateById(autUserRole);
                } else {
                    AutUserRole autUserRole1 = new AutUserRole();
                    autUserRole1.setIsActive(isActive);
                    autUserRole1.setRoleId(roleId);
                    autUserRole1.setUserId(userId);
                    autUserRole1.setUserName(autuser.getUserName());
                    AutRole roleName = autRoleService.selectById(roleId);
                    autUserRole1.setRoleName(roleName.getRoleName());
                    autUserRole1.setRoleCode(roleName.getRoleCode());
                    autUserRole1.setUserRoleRank(1);
                    autUserRoleService.insert(autUserRole1);
                }
                // list.add(autUserRole);
            }
            // autUserRoleService.updateBatchById(list);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<AutDepartmentRole> getDepartmentRoleList(AutDepartmentRole obj) {

        Where<AutDepartmentRole> where = new Where<AutDepartmentRole>();
        where.setSqlSelect("id,role_id,role_code,dept_id,dept_code,is_active");
        where.eq("dept_id", obj.getDeptId());
        where.eq("is_active", 1);
        List<AutDepartmentRole> autDepartmentRoleList = autDepartmentRoleService.selectList(where);

        return autDepartmentRoleList;
    }

}
