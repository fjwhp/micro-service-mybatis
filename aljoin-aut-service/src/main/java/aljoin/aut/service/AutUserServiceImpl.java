package aljoin.aut.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.entity.AutSsoData;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.mapper.AutUserMapper;
import aljoin.aut.dao.object.AutUserAndPubVo;
import aljoin.aut.dao.object.AutUserDO;
import aljoin.aut.dao.object.AutUserPubEditVO;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.dao.object.AutUserVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutSsoDataService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomPasswordEncoder;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.SsoData;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.EncryptUtil;
import aljoin.util.StringUtil;

/**
 * 
 * 用户表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-21
 */
@Service
public class AutUserServiceImpl extends ServiceImpl<AutUserMapper, AutUser> implements AutUserService {
    private final static Logger logger = LoggerFactory.getLogger(AutUserServiceImpl.class);

    @Resource
    private AutUserService autUserService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private AutUserMapper autUserMapper;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutRoleService autRoleService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private IdentityService identityService;
    @Resource
    private AutSsoDataService autSsoDataService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private AutUserRankService autUserRankService;
    @Resource
    private CustomPasswordEncoder customPasswordEncoder;

    @Override
    public Page<AutUser> list(PageBean pageBean, AutUser obj) throws Exception {
        Where<AutUser> where = new Where<AutUser>();
        if (StringUtils.isNotEmpty(obj.getUserName())) {
            where.like("user_name", obj.getUserName());
            where.or("full_name LIKE {0}", "%" + obj.getUserName() + "%");
            where.or("user_email LIKE {0}", "%" + obj.getUserName() + "%");
        }
        where.orderBy("create_time", false);
        Page<AutUser> page = selectPage(new Page<AutUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public Page<AutUserVO> voList(PageBean pageBean, AutUser obj) throws Exception {
        Where<AutUser> userWhere = new Where<AutUser>();
        if (StringUtils.isNotEmpty(obj.getUserName())) {
            userWhere.like("user_name", obj.getUserName());
            userWhere.or("full_name LIKE {0}", "%" + obj.getUserName() + "%");
        }
        userWhere.orderBy("create_time", false);
        Page<AutUser> page = selectPage(new Page<AutUser>(pageBean.getPageNum(), pageBean.getPageSize()), userWhere);

        List<AutUser> autUserList = page.getRecords();
        // 获得userIdList，用于查询用户所在部门
        List<Long> userIdList = new ArrayList<Long>();
        for (AutUser autUser : autUserList) {
            userIdList.add(autUser.getId());
        }
        // 用户id来查询部门-用户表获得deptId,以用来查询部门信息
        Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
        deptUserWhere.setSqlSelect("id,user_id,dept_id");
        deptUserWhere.in("user_id", userIdList);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(deptUserWhere);
        Set<Long> deptIdSet = new HashSet<Long>();
        for (AutDepartmentUser deptUser : deptUserList) {
            deptIdSet.add(deptUser.getDeptId());
        }
        // 根据Set里不重复的部门id，查询部门名称
        Where<AutDepartment> deptWhere = new Where<AutDepartment>();
        deptWhere.setSqlSelect("id,dept_name");
        deptWhere.in("id", deptIdSet);
        deptUserWhere.orderBy("id", true);
        List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);
        // 根据userId查询aut_user_position表里的position_id,然后查询aut_position得到position_name
        Where<AutUserPosition> userPositionWhere = new Where<AutUserPosition>();
        userPositionWhere.setSqlSelect("id,user_id,position_id");
        userPositionWhere.in("user_id", userIdList);
        List<AutUserPosition> userPositionList = autUserPositionService.selectList(userPositionWhere);
        List<Long> positionIdList = new ArrayList<Long>();
        for (AutUserPosition userPosition : userPositionList) {
            positionIdList.add(userPosition.getPositionId());
        }
        // 根据positionIdList获得岗位名称
        Where<AutPosition> positionWhere = new Where<AutPosition>();
        positionWhere.setSqlSelect("id,position_name,dept_id");
        positionWhere.in("id", positionIdList);
        deptUserWhere.orderBy("dept_id", true);
        List<AutPosition> positionList = autPositionService.selectList(positionWhere);
        // 查询公共信息
        Where<AutUserPub> w6 = new Where<AutUserPub>();
        w6.setSqlSelect("id,user_id,phone_number");
        w6.in("user_id", userIdList);
        List<AutUserPub> autUserPubList = autUserPubService.selectList(w6);
        // 构造返回值
        Page<AutUserVO> pageList = new Page<AutUserVO>();
        pageList.setCurrent(page.getCurrent());
        pageList.setSize(page.getSize());
        pageList.setTotal(page.getTotal());

        // 拼接返回值
        List<AutUserVO> autUserVoList = new ArrayList<AutUserVO>();
        for (AutUser autUser : autUserList) {
            AutUserVO vo = new AutUserVO();
            vo.setAutUser(autUser);
            autUserVoList.add(vo);
        }
        /*
         * // 1、拼接用户-部门关联表 for (AutUserVO vo : autUserVoList) { for (AutDepartmentUser deptUser :
         * deptUserList) { if (vo.getAutUser().getId().equals(deptUser.getUserId())) {
         * vo.setAutDepartmentUser(deptUser); } } } // 2、拼接部门 for (AutUserVO vo : autUserVoList) { for
         * (AutDepartment dept : deptList) { AutDepartmentUser deptUser = vo.getAutDepartmentUser(); if
         * (null != deptUser) { if (deptUser.getDeptId().equals(dept.getId())) {
         * vo.setAutDepartment(dept); } } }
         */
        /*
         * // 拼接返回值 List<AutUserVO> autUserVoList = new ArrayList<AutUserVO>(); for (AutUser autUser :
         * autUserList) { AutUserVO vo = new AutUserVO(); vo.setAutUser(autUser); autUserVoList.add(vo);
         * }
         */
        // 1、拼接用户-部门关联表
        for (AutUserVO vo : autUserVoList) {
            for (AutDepartmentUser deptUser : deptUserList) {
                if (vo.getAutUser().getId().equals(deptUser.getUserId())) {
                    vo.setAutDepartmentUser(deptUser);
                    String ids = vo.getAutDeptIds();
                    if (ids != null && !"".equals(ids)) {
                        ids += deptUser.getDeptId() + ",";
                    } else {
                        ids = deptUser.getDeptId() + ",";
                    }
                    vo.setAutDeptIds(ids);
                }
            }
        }
        // 2、拼接部门
        for (AutUserVO vo : autUserVoList) {
            for (AutDepartment dept : deptList) {
                AutDepartmentUser deptUser = vo.getAutDepartmentUser();
                if (null != deptUser) {
                    if (deptUser.getDeptId().equals(dept.getId())) {
                        vo.setAutDepartment(dept);
                        String ids = vo.getAutDeptIds();
                        if (ids.indexOf(dept.getId().toString()) > -1) {
                            String names = vo.getAutDeptNames();
                            if (names != null && !"".equals(names)) {
                                names += dept.getDeptName() + ",";
                            } else {
                                names = dept.getDeptName() + ",";
                            }
                            vo.setAutDeptNames(names.substring(0, names.length() - 1));
                        }
                    }
                }
            }
        }
        /*
         * // 3、拼接用户-岗位关联表 for (AutUserVO vo : autUserVoList) { for (AutUserPosition userPosition :
         * userPositionList) { AutUser autUser = vo.getAutUser(); if (null != autUser) { if
         * (vo.getAutUser().getId().equals(userPosition.getUserId())) {
         * vo.setAutUserPosition(userPosition); } } } }
         */
        // 3、拼接用户-岗位关联表
        for (AutUserVO vo : autUserVoList) {
            for (AutUserPosition userPosition : userPositionList) {
                AutUser autUser = vo.getAutUser();
                if (null != autUser) {
                    if (vo.getAutUser().getId().equals(userPosition.getUserId())) {
                        vo.setAutUserPosition(userPosition);
                        String ids = vo.getPositionids();
                        if (ids != null && !"".equals(ids)) {
                            ids += userPosition.getPositionId() + ",";
                        } else {
                            ids = userPosition.getPositionId() + ",";
                        }
                        vo.setPositionids(ids);
                    }
                }
            }
        }
        /*
         * // 4、拼接岗位表 for (AutUserVO vo : autUserVoList) { for (AutPosition position : positionList) {
         * AutUserPosition userPosition = vo.getAutUserPosition(); if(null != userPosition){ if
         * (userPosition.getPositionId().equals(position.getId())) { vo.setAutPosition(position); } } }
         * } pageList.setRecords(autUserVoList); }
         */
        // 4、拼接岗位表
        for (AutUserVO vo : autUserVoList) {
            for (AutPosition position : positionList) {
                AutUserPosition userPosition = vo.getAutUserPosition();
                if (null != userPosition) {
                    if (userPosition.getPositionId().equals(position.getId())) {
                        vo.setAutPosition(position);
                        String ids = vo.getPositionids();
                        if (ids.indexOf(position.getId().toString()) > -1) {
                            String names = vo.getPositionNames();
                            if (names != null && !"".equals(names)) {
                                names += position.getPositionName() + ",";
                            } else {
                                names = position.getPositionName() + ",";
                            }
                            vo.setPositionNames(names.substring(0, names.length() - 1));
                        }
                    }
                }
            }
        }
        // 5、拼接公共信息
        for (AutUserVO vo : autUserVoList) {
            for (AutUserPub pub : autUserPubList) {
                if (vo.getAutUser().getId().equals(pub.getUserId())) {
                    vo.setPhone(pub.getPhoneNumber());
                }
            }
        }
        pageList.setRecords(autUserVoList);

        return pageList;
    }

    @Override
    @Transactional
    public void delete(AutUser obj) throws Exception {
        obj = autUserService.selectById(obj.getId());
        // 删除用户角色关系
        Where<AutUserRole> rolewhere = new Where<AutUserRole>();
        rolewhere.eq("user_id", obj.getId());
        AutUserRole userrole = autUserRoleService.selectOne(rolewhere);
        autUserService.deleteById(obj.getId());
        if (userrole != null) {
            autUserRoleService.deleteById(userrole.getId());
        }
        // 同步activiti用户
        activitiService.delUser(obj.getId());

        Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
        positionWhere.setSqlSelect("id,user_id,position_id,is_active");
        positionWhere.eq("user_id", obj.getId());
        positionWhere.eq("is_active", 1);
        List<AutUserPosition> positionList = autUserPositionService.selectList(positionWhere);

        Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
        departmentUserWhere.setSqlSelect("id,user_id,dept_id,is_active");
        departmentUserWhere.eq("is_active", 1);
        departmentUserWhere.eq("user_id", obj.getId());
        List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);

        Where<AutUserRole> roleWhere = new Where<AutUserRole>();
        roleWhere.setSqlSelect("id,role_id,user_id,is_active");
        roleWhere.eq("is_active", 1);
        roleWhere.eq("user_id", obj.getId());
        List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);

        // 同步activiti用户
        if (null != positionList && !positionList.isEmpty()) {
            for (AutUserPosition userPosition : positionList) {
                activitiService.delUserGroup(obj.getId(), userPosition.getPositionId());
            }
        }
        if (null != departmentUserList && !departmentUserList.isEmpty()) {
            for (AutDepartmentUser departmentUser : departmentUserList) {
                activitiService.delUserGroup(obj.getId(), departmentUser.getDeptId());
            }
        }
        if (null != roleList && !roleList.isEmpty()) {
            for (AutUserRole role : roleList) {
                activitiService.delUserGroup(obj.getId(), role.getRoleId());
            }
        }
        // 删除用户表的同时也要删除用户排序表的记录； modify by huanghz at 2017-12-19
        autUserRankService.deleteById(obj.getId());

    }

    @Override
    @Transactional
    public RetMsg add(AutUser obj, String charPassword) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 检查用户名是否存在
        Where<AutUser> where = new Where<AutUser>();
        where.eq("user_name", obj.getUserName());
        if (obj.getUserEmail() != null && StringUtils.isNotEmpty(obj.getUserEmail())) {
            where.or("user_email = {0}", obj.getUserEmail());
        }
        if (autUserService.selectCount(where) > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("账号或邮箱已被注册");
        } else {
            obj.setIsCredentialsExpired(0);
            insertAllColumn(obj);
            // autUserService.insert(obj);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
            obj = autUserService.selectOne(where);
            retMsg.setObject(obj);
            // 同步activiti用户
            activitiService.addUser(obj.getId(), obj.getFullName());

        }

        return retMsg;
    }

    @Override
    public List<AutUser> getUserList() {
        Where<AutUser> where = new Where<AutUser>();
        where.eq("is_active", 1);
        where.setSqlSelect("id,user_name,full_name");
        List<AutUser> list = selectList(where);
        return list;
    }

    @Override
    public List<AutUser> getMyUserList() throws Exception {
        return autUserMapper.getMyUserList();
    }

    @Override
    public Page<AutUser> listNoDepartmentUser(PageBean pageBean, List<Long> userIdList) throws Exception {
        Where<AutUser> where = new Where<AutUser>();
        where.notIn("id", userIdList);
        where.orderBy("create_time", false);

        Page<AutUser> page = selectPage(new Page<AutUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    @Transactional
    public RetMsg setAuth(int isActive, long id, long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 菜单权限
        Where<AutUserRole> where = new Where<AutUserRole>();
        where.eq("user_id", userId);
        where.eq("role_id", id);
        AutUserRole autUserRole = autUserRoleService.selectOne(where);
        if (autUserRole == null) {
            AutUser autUser = autUserService.selectById(userId);
            AutRole autRole = autRoleService.selectById(id);
            autUserRole = new AutUserRole();
            autUserRole.setUserId(userId);
            autUserRole.setRoleId(autRole.getId());
            autUserRole.setRoleCode(autRole.getRoleCode());
            autUserRole.setUserName(autUser.getUserName());
            autUserRole.setRoleName(autRole.getRoleName());
            autUserRole.setIsActive(isActive);
            // 给用户分配角色的时候，用户角色排序默认值为0
            autUserRole.setUserRoleRank(0);
            autUserRoleService.insert(autUserRole);
            // 用户分配岗位与流程表关联
            activitiService.addUserGroup(autUserRole.getUserId(), autUserRole.getRoleId());
        } else {
            autUserRole.setIsActive(isActive);
            autUserRoleService.updateById(autUserRole);
            if (isActive == 0) {
                // 用户分配岗位与流程表关联
                activitiService.delUserGroup(autUserRole.getUserId(), autUserRole.getRoleId());
            } else {
                List<Group> groupList = identityService.createGroupQuery().list();
                List<Long> groupIdList = new ArrayList<Long>();
                if (null != groupList && !groupList.isEmpty()) {
                    for (Group gp : groupList) {
                        if (StringUtils.isNotEmpty(gp.getId())) {
                            groupIdList.add(Long.parseLong(gp.getId()));
                        }
                    }
                }

                if (groupIdList.contains(autUserRole.getRoleId())) {
                    // 用户分配岗位与流程表关联
                    activitiService.delUserGroup(autUserRole.getUserId(), autUserRole.getRoleId());
                    activitiService.addUserGroup(autUserRole.getUserId(), autUserRole.getRoleId());// 先删除
                                                                                                   // 再添加以免主键冲突
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public Page<AutUserDO> getUserList(PageBean pageBean, AutUserVO obj) throws Exception {
        Where<AutUser> where = new Where<AutUser>();
        Page<AutUserDO> page = new Page<AutUserDO>();
        if (null != obj) {
            if (StringUtils.isNotEmpty(obj.getUserName())) {
                where.like("user_name", obj.getUserName());
                where.or("full_name LIKE {0}", "%" + obj.getUserName() + "%");
                where.or("user_email LIKE {0}", "%" + obj.getUserName() + "%");
            }
            if (null != obj.getDeptId()) {
                // 查询部门下的用户
                Where<AutDepartmentUser> aduWhere = new Where<AutDepartmentUser>();
                aduWhere.eq("dept_id", obj.getDeptId());
                aduWhere.eq("is_active", 1);
                aduWhere.setSqlSelect("user_id");
                List<AutDepartmentUser> aduList = autDepartmentUserService.selectList(aduWhere);
                List<Long> userIdList = new ArrayList<Long>();
                for (AutDepartmentUser adu : aduList) {
                    userIdList.add(adu.getUserId());
                }
                if (userIdList.size() > 0) {
                    where.in("id", userIdList);
                } else {
                    // 该部门下没有用户
                    where.eq("id", 0);
                }
            }
            if (null != obj.getIsActive()) {
                where.eq("is_active", obj.getIsActive());
            } else {
                where.eq("is_active", 1);
            }
        }

        if (null != obj.getUserPwd()) {
            // Where<AutDepartmentUser> departmentUserwhere = new
            // Where<AutDepartmentUser>();
            // List<AutDepartmentUser> a =
            // autDepartmentUserService.selectList(departmentUserwhere);
        }
        where.orderBy("create_time", false);
        where.setSqlSelect("id,user_name,full_name,is_active,create_time");

        List<Long> idList = new ArrayList<Long>();
        if (null != obj.getIdList()) {
            if (!obj.getIdList().isEmpty()) {
                idList = obj.getIdList();
            }
        }

        Page<AutUser> page2 = selectPage(new Page<AutUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<AutUser> userList = page2.getRecords();
        List<AutUserDO> userDOList = new ArrayList<AutUserDO>();
        if (!userList.isEmpty()) {
            for (AutUser user : userList) {
                AutUserDO userDO = new AutUserDO();
                userDO.setAutUser(user);
                userDO.setIsCheck(0);
                if (null != obj.getIdList()) {
                    if (idList.contains(user.getId())) {
                        userDO.setIsCheck(1);
                    } else {
                        userDO.setIsCheck(0);
                    }
                }
                userDOList.add(userDO);
            }
        }

        page.setRecords(userDOList);
        page.setTotal(page2.getTotal());
        page.setSize(page2.getSize());
        return page;
    }

    @Override
    public Page<AutUser> getDeptUserList(PageBean pageBean, AutUser obj) throws Exception {
        Where<AutUser> where = new Where<AutUser>();
        if (StringUtils.isNotEmpty(obj.getUserName())) {
            where.like("user_name", obj.getUserName());
            where.or("full_name LIKE {0}", "%" + obj.getUserName() + "%");
            where.or("user_email LIKE {0}", "%" + obj.getUserName() + "%");
        }
        if (StringUtils.isNotEmpty(obj.getUserPwd())) {
            Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
            List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);
            if (null != departmentUserList && !departmentUserList.isEmpty()) {
                List<Long> idList = new ArrayList<Long>();
                for (AutDepartmentUser departmentUser : departmentUserList) {
                    if (null != departmentUser) {
                        idList.add(departmentUser.getUserId());
                    }
                }
                if (null != idList && !idList.isEmpty()) {
                    where.in("id", idList);
                }
            }

        }
        if (null == obj.getIsActive()) {
            where.eq("is_active", 1);
        } else {
            where.eq("is_active", obj.getIsActive());
        }
        where.orderBy("create_time", false);
        Page<AutUser> page = selectPage(new Page<AutUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public Page<AutUser> listIsAvtive(PageBean pageBean, AutUser obj) throws Exception {
        Where<AutUser> where = new Where<AutUser>();
        if (StringUtils.isNotEmpty(obj.getUserName())) {
            where.like("user_name", obj.getUserName());
            where.or("full_name LIKE {0}", "%" + obj.getUserName() + "%");
            where.or("user_email LIKE {0}", "%" + obj.getUserName() + "%");
        }
        where.eq("is_active", 1);
        where.orderBy("create_time", false);
        Page<AutUser> page = selectPage(new Page<AutUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public Map<Long, Integer> getUserRankList(List<Long> userIdList, String rankType) throws Exception {
        /*
         * Map<Long, Integer> retMap = new HashMap<Long, Integer>(); for (Long id : userIdList) {
         * retMap.put(id, 0); } if ("dept".equals(rankType)) { // 部门 // Where<AutDepartmentUser> where =
         * new Where<AutDepartmentUser>(); // where.setSqlSelect("dept_id,department_user_rank"); //
         * where.in("user_id", userIdList);
         * 
         * } else if ("position".equals(rankType)) { // 岗位
         * 
         * } else if ("role".equals(rankType)) { // 角色
         * 
         * }
         */

        Map<Long, Integer> retMap = new LinkedHashMap<Long, Integer>();
        if (userIdList != null && userIdList.size() > 0) {
            Integer irank = 1;
            Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
            autUserRankWhere.setSqlSelect("id,user_rank");
            autUserRankWhere.in("id", userIdList);
            autUserRankWhere.orderBy("user_rank", true);
            // List<Map<String, Object>> autUserRankListMap =
            // autUserRankService.selectMaps(autUserRankWhere);
            List<AutUserRank> list = autUserRankService.selectList(autUserRankWhere);
            if (list != null && list.size() > 0) {
                for (AutUserRank autUserRank : list) {
                    retMap.put(autUserRank.getId(), irank++);
                }
            }
            /*
             * for (Map<String, Object> mapObj : autUserRankListMap) { retMap.put((Long) mapObj.get("id"),
             * irank++); }
             */
        }
        return retMap;
    }

    @Override
    public Page<AutUserRank> getUserRankPage(PageBean pageBean, List<Long> userIdList) throws Exception {
        Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
        autUserRankWhere.setSqlSelect("id,user_rank");
        autUserRankWhere.in("id", userIdList);
        autUserRankWhere.orderBy("user_rank", true);
        Page<AutUserRank> page = autUserRankService
            .selectPage(new Page<AutUserRank>(pageBean.getPageNum(), pageBean.getPageSize()), autUserRankWhere);
        return page;
    }

    @Override
    public AutUserPubVO getById(Long id) {
        // 用户信息
        // w1.setSqlSelect("id,user_name,full_name,user_pwd,user_email,is_active,create_time,create_user_name,last_update_time,last_update_user_name");
        // w1.eq("id", id);
        AutUser autUser = autUserService.selectById(id);
        // AutUser autUser = autUserService.selectOne(w1);
        autUser.setUserPwd(EncryptUtil.encryptMD5(autUser.getUserPwd()));
        // 部门信息
        Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
        w2.setSqlSelect("id,dept_id,user_id");
        w2.eq("user_id", id);
        List<AutDepartmentUser> autDepartmentUser = autDepartmentUserService.selectList(w2);
        List<Long> deptIdList = new ArrayList<Long>();
        for (AutDepartmentUser deptUser : autDepartmentUser) {
            deptIdList.add(deptUser.getDeptId());
        }

        List<AutDepartment> deptList = new ArrayList<AutDepartment>();
        if (null != deptIdList && !deptIdList.isEmpty()) {
            Where<AutDepartment> w3 = new Where<AutDepartment>();
            w3.setSqlSelect("id,dept_name");
            w3.in("id", deptIdList);
            deptList = autDepartmentService.selectList(w3);
        }
        // 岗位信息
        Where<AutUserPosition> w4 = new Where<AutUserPosition>();
        w4.setSqlSelect("id,user_id,position_id");
        w4.eq("user_id", id);
        List<AutUserPosition> userPositionList = autUserPositionService.selectList(w4);

        List<Long> positionIdList = new ArrayList<Long>();
        for (AutUserPosition position : userPositionList) {
            positionIdList.add(position.getPositionId());
        }

        List<AutPosition> positionList = new ArrayList<AutPosition>();
        if (null != userPositionList && !userPositionList.isEmpty()) {
            Where<AutPosition> w5 = new Where<AutPosition>();
            w5.setSqlSelect("id,position_name,dept_id");
            w5.in("id", positionIdList);
            positionList = autPositionService.selectList(w5);
        }

        // 公共信息
        Where<AutUserPub> w6 = new Where<AutUserPub>();
        w6.setSqlSelect(
            "id,user_id,phone_number,tel_number,fax_number,law_number,chest_card_number,user_icon,max_mail_size");
        w6.eq("user_id", id);
        AutUserPub autUserPub = autUserPubService.selectOne(w6);

        // 拼接信息
        AutUserPubVO vo = new AutUserPubVO();
        vo.setAutUser(autUser);
        if (null != deptList && !deptList.isEmpty()) {
            // 如果用户有多个部门
            AutDepartment department = new AutDepartment();
            String name = "";
            for (int i = 0; i < deptList.size(); i++) {
                name += deptList.get(i).getDeptName() + ",";
            }
            department.setDeptName(name.substring(0, name.length() - 1));
            vo.setAutDepartment(department);
        } else {
            AutDepartment department = new AutDepartment();
            department.setDeptName("无部门");
            vo.setAutDepartment(department);
        }
        if (null != positionList && !positionList.isEmpty()) {
            // 如果用户有多个岗位
            AutPosition position = new AutPosition();
            String name = "";
            for (int i = 0; i < positionList.size(); i++) {
                name += positionList.get(i).getPositionName() + ",";
            }
            position.setPositionName(name.substring(0, name.length() - 1));
            vo.setAutPosition(position);
        } else {
            AutPosition position = new AutPosition();
            position.setPositionName("无岗位");
            vo.setAutPosition(position);
        }
        if (StringUtils.checkValNotNull(autUserPub)) {
            vo.setAutUserPub(autUserPub);
        }

        return vo;
    }

    private RetMsg checkSsoDataSynch(SsoData ssoData) {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(200);
        if (ssoData == null) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：参数对象(ssoData)不能为空");
            return retMsg;
        }
        // 参数非空校验
        if (StringUtils.isEmpty(ssoData.getOperationType())) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：操作类型(operationType)不能为空");
            return retMsg;
        }
        // 参数非空校验
        if (StringUtils.isEmpty(ssoData.getLoginAccount())) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：登录账号(loginAccount)不能为空");
            return retMsg;
        }
        if ("1".equals(ssoData.getOperationType())) {
            // 新增时密码不能为空
            if (StringUtils.isEmpty(ssoData.getLoginPwd())) {
                retMsg.setCode(400);
                retMsg.setMessage("参数异常：新增时帐户密码(loginPwd)不能为空");
                return retMsg;
            } else {
                // 格式校验
                if (ssoData.getLoginPwd().length() != 32 || !StringUtil.isAllUpperCase(ssoData.getLoginPwd())) {
                    retMsg.setCode(400);
                    retMsg.setMessage("参数异常：新增时帐户密码(loginPwd)格式错误");
                    return retMsg;
                }
            }

        } else if ("2".equals(ssoData.getOperationType())) {
            // 修改时密码不能为空
            if (StringUtils.isEmpty(ssoData.getLoginPwd())) {
                retMsg.setCode(400);
                retMsg.setMessage("参数异常：修改时帐户密码(loginPwd)不能为空");
                return retMsg;
            } else {
                // 格式校验
                if (ssoData.getLoginPwd().length() != 32 || !StringUtil.isAllUpperCase(ssoData.getLoginPwd())) {
                    retMsg.setCode(400);
                    retMsg.setMessage("参数异常：修改时帐户密码(loginPwd)格式错误");
                    return retMsg;
                }
            }
        } else if ("3".equals(ssoData.getOperationType())) {
            // 查询时密码不能为空
            if (StringUtils.isEmpty(ssoData.getLoginPwd())) {
                retMsg.setCode(400);
                retMsg.setMessage("参数异常：查询时帐户密码(loginPwd)不能为空");
                return retMsg;
            } else {
                // 格式校验
                if (ssoData.getLoginPwd().length() != 32 || !StringUtil.isAllUpperCase(ssoData.getLoginPwd())) {
                    retMsg.setCode(400);
                    retMsg.setMessage("参数异常：查询时帐户密码(loginPwd)格式错误");
                    return retMsg;
                }
            }

        } else {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：操作类型(operationType)非法");
            return retMsg;
        }
        if (StringUtils.isEmpty(ssoData.getTimeStamp())) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：时间戳(timeStamp)不能为空");
            return retMsg;
        } else {
            try {
                Long.parseLong(ssoData.getTimeStamp());
            } catch (Exception e) {
                retMsg.setCode(400);
                retMsg.setMessage("参数异常：时间戳(timeStamp)格式错误");
                return retMsg;
            }
        }

        if (StringUtils.isEmpty(ssoData.getSign())) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：签名(sign)不能为空");
            return retMsg;
        } else {// 格式校验
            if (ssoData.getSign().length() != 32 || !StringUtil.isAllUpperCase(ssoData.getSign())) {
                retMsg.setCode(400);
                retMsg.setMessage("参数异常：签名(sign)格式错误");
                return retMsg;
            }
        }

        // 所有参数检查通过后进行签名校验
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("operationType", ssoData.getOperationType());
        parameters.put("loginAccount", ssoData.getLoginAccount());
        parameters.put("loginPwd", ssoData.getLoginPwd());
        parameters.put("timeStamp", ssoData.getTimeStamp());
        String loginSceret = "";
        String sign = StringUtil.getSign(parameters, loginSceret);
        // 第三方传递的签名与oa提供的签名算法一致的情况下，才允许对方操作oa的aut_sso_data表。
        if (!sign.equals(ssoData.getSign())) {
            retMsg.setCode(403);
            retMsg.setMessage("无权访问：签名(sign)非法");
            return retMsg;
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg ssoDataSynch(SsoData ssoData) throws Exception {
        // 进行简单的参数非空已经格式判断
        RetMsg retMsg = checkSsoDataSynch(ssoData);
        if (retMsg.getCode() != 200) {
            return retMsg;
        }
        Integer liCode = AppConstant.RET_CODE_SUCCESS;
        String lsMsg = "";
        // 校验通过进行真正操作
        if ("1".equals(ssoData.getOperationType())) {
            // 新增，判断账号在我们自己的用户表是否存在
            Where<AutUser> autUserWhere = new Where<AutUser>();
            autUserWhere.eq("user_name", ssoData.getLoginAccount()); // 第三方的用户id？
            AutUser autUser = selectOne(autUserWhere);
            if (autUser == null) {
                // 不存在，新增用户表,用户公共信息表和单点登录数据表
                autUser = new AutUser();
                autUser.setUserPwd(ssoData.getLoginPwd());
                autUser.setUserName(ssoData.getLoginAccount());
                autUser.setIsActive(1);
                autUser.setIsAccountExpired(0);
                autUser.setIsAccountLocked(0);
                autUser.setIsCredentialsExpired(0);
                autUser.setFullName(ssoData.getLoginAccount());
                autUser.setUserEmail(ssoData.getLoginAccount() + "@youwebsite.com");
                insert(autUser);

                AutUserPub autUserPub = new AutUserPub();
                autUserPub.setUserId(autUser.getId());
                SysParameter sysParameter = sysParameterService.selectBykey("personal_mail_space");
                autUserPub.setMaxMailSize(Integer.parseInt(sysParameter.getParamValue()));
                autUserPubService.insert(autUserPub);

                AutSsoData autSsoData = new AutSsoData();
                autSsoData.setUserId(autUser.getId());
                autSsoData.setUserName(ssoData.getLoginAccount());
                autSsoData.setUserPwd(ssoData.getLoginPwd());
                autSsoData.setSysName("xmsoa");
                autSsoDataService.insert(autSsoData);
                lsMsg = "新增用户成功";
            } else {
                // 存在，返回提示
                liCode = AppConstant.RET_CODE_BIZ_ERR;
                lsMsg = "业务操作异常：用户已存在";
            }
        } else if ("2".equals(ssoData.getOperationType())) {
            // 只修改aut_sso_data表中密码，其它oa的相关表及字段是不会影响到的。
            String lsUserName = ssoData.getLoginAccount();
            Where<AutSsoData> where = new Where<AutSsoData>();
            // create_time,last_update_time,version,is_delete,last_update_user_id,last_update_user_name,create_user_id,create_user_name,
            // where.setSqlSelect("id,user_id,user_name,user_pwd,sys_name"); //更新字段默认查询出该条记录的所有字段属性；
            // 否则更新nullpoint异常；
            where.eq("user_name", lsUserName.trim());
            AutSsoData autSsoData = autSsoDataService.selectOne(where);
            if (autSsoData != null) {
                autSsoData.setUserPwd(ssoData.getLoginPwd());
                Boolean lbRtn = autSsoDataService.updateById(autSsoData);
                if (!lbRtn) {
                    liCode = AppConstant.RET_CODE_BIZ_ERR;
                    lsMsg = "业务操作异常：oa接口更新不成功";
                } else {
                    lsMsg = "oa更新用户密码成功";
                }
            } else {
                liCode = AppConstant.RET_CODE_BIZ_ERR;
                lsMsg = "业务操作异常：用户不存在";
            }
        } else if ("3".equals(ssoData.getOperationType())) {
            // 根据接口调用方提供的用户名查询，业务上暂时不需要默认查询全表的用户名及密码。
            SsoData sd = new SsoData();
            Where<AutSsoData> where = new Where<AutSsoData>();
            where.setSqlSelect("user_name,user_pwd");
            // 目前的查询情况是接口调用方一定会指定用户查询，看接口提供方的数据是否存在；不会查询全部or1=1，这样没有业务意义,若存在则要修改上面的校验判断，再是修改下面的查询函数返回List；
            where.eq("user_name", ssoData.getLoginAccount().trim());// 理论上user_name不能重复；
            AutSsoData autSsoData = autSsoDataService.selectOne(where);
            if (autSsoData != null) {
                sd.setLoginAccount(autSsoData.getUserName());
                sd.setLoginPwd(autSsoData.getUserPwd());
                lsMsg = "oa存在此用户信息";
                retMsg.setObject(sd);
            } else {
                liCode = AppConstant.RET_CODE_BIZ_ERR;
                lsMsg = "请确认是否存在指定的用户帐号，并联系OA系统管理员";
            }
        }
        retMsg.setCode(liCode);
        retMsg.setMessage(lsMsg);
        return retMsg;
    }

    private RetMsg checkSsoDataSynInvoke(SsoData ssoData) {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(200);
        if (ssoData == null) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：参数对象(ssoData)不能为空");
            return retMsg;
        }
        if (StringUtils.isEmpty(ssoData.getOperationType())) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：操作类型(operationType)不能为空");
            return retMsg;
        }
        if (StringUtils.isEmpty(ssoData.getLoginAccount())) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：登录账号(loginAccount)不能为空");
            return retMsg;
        }
        // 新增 要对密码明文进行非空校验及MD5加密且转换为大写，而修改、查询出来的密码都是OA已经加密过的，所以不用再MD5再加密；
        String loginPwd = ssoData.getLoginPwd();
        if (StringUtils.isEmpty(loginPwd)) {
            retMsg.setCode(400);
            retMsg.setMessage("参数异常：密码(loginPwd)不能为空");
            return retMsg;
        } else {
            if (ssoData.getLoginPwd().length() != 32 || !StringUtil.isAllUpperCase(ssoData.getLoginPwd())) {
                loginPwd = EncryptUtil.encryptMD5(loginPwd).toUpperCase();
                ssoData.setLoginPwd(loginPwd);
            }
        }
        // TimeStamp Sign oa调用第三方提供的接口时，时间戳数据类型格式和签名标识的算法要由对方提供； 【注：到时需要第三方提供的加密算法进行转化。】
        /*
         * if (StringUtils.isEmpty(ssoData.getTimeStamp())) { retMsg.setCode(400);
         * retMsg.setMessage("参数异常：时间戳(timeStamp)不能为空"); return retMsg; } else { try {
         * Long.parseLong(ssoData.getTimeStamp()); } catch (Exception e) { retMsg.setCode(400);
         * retMsg.setMessage("参数异常：时间戳(timeStamp)格式错误"); return retMsg; } }
         */
        Long timeStamp = System.currentTimeMillis();
        ssoData.setTimeStamp(timeStamp.toString());

        // 所有参数检查通过后进行签名校验, 签名校验要由接口提供方来决定，oa跟第三方的算法保持一致即可；
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("operationType", ssoData.getOperationType());
        parameters.put("loginAccount", ssoData.getLoginAccount());
        parameters.put("loginPwd", ssoData.getLoginPwd());
        parameters.put("timeStamp", ssoData.getTimeStamp());
        String loginSceret = "";
        String sign = StringUtil.getSign(parameters, loginSceret);
        ssoData.setSign(sign);

        return retMsg;
    }

    // 调用第三方提供的接口，oa把根据不同的操作类型，把参数对象传递给对方，根据对方的返回值状态判断是否提交本地事务。[用户管理模块的新增、修改、查询操作将调用此接口--结合设计完善；
    // 再是controler添加测试类的调用入口。]
    @Override
    public RetMsg ssoDataSynInvoke(SsoData ssoData) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 对密码需要加密后才可以调用第三方接口进行传递；再是需要根据第三方提供的签名逄法计算 ； 校验后期需要沟通确认。
        retMsg = checkSsoDataSynInvoke(ssoData);
        if (retMsg.getCode() != 200) {
            return retMsg;
        }
        // 返回值代码需要与第三方的约定；
        /*
         * Integer liCode = AppConstant.RET_CODE_SUCCESS ; String lsMsg = "";
         */
        // http协议向服务端请求办法及传参，返回值对象转换；
        retMsg = invokeByParam(ssoData);
        return retMsg;
    }

    /**
     * 
     * OA调用第三方接口，通过http协议把参数对象传递给第三方.
     *
     * @return：RetMsg
     *
     * @author：huanghz
     *
     * @date：2017年12月18日 下午8:47:18
     */
    private RetMsg invokeByParam(SsoData ssoData) throws Exception {
        RetMsg retMsg = new RetMsg();
        CloseableHttpClient httpClient = null;
        HttpPost httpPost = null;
        CloseableHttpResponse response = null;
        try {
            SysParameter sysParameter = sysParameterService.selectBykey("sso_datasyn_invoke_url");
            if (sysParameter == null) {
                retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
                retMsg.setMessage("oa系统表未配置第三方接口的访问地址 ");
                return retMsg;
            }
            String lsURI = sysParameter.getParamValue();
            httpClient = HttpClients.createDefault();
            httpPost = new HttpPost(lsURI);
            // 创建参数队列
            List<NameValuePair> kvList = new ArrayList<NameValuePair>();
            kvList.add(new BasicNameValuePair("operationType", ssoData.getOperationType()));
            kvList.add(new BasicNameValuePair("loginAccount", ssoData.getLoginAccount()));
            kvList.add(new BasicNameValuePair("loginPwd", ssoData.getLoginPwd()));
            // 时间值格式及签名需要与第三方确认；符名算法要由对方提供。
            // kvList.add(new BasicNameValuePair("timeStamp", ssoData.getTimeStamp()));
            // kvList.add(new BasicNameValuePair("sign", ssoData.getSign()));
            UrlEncodedFormEntity reqEntity = new UrlEncodedFormEntity(kvList, "UTF-8");
            httpPost.setEntity(reqEntity);
            response = httpClient.execute(httpPost); // 把请求uri跟相关参数对象传递给第三方的controler,第三方执行完程序将返回状态信息跟查询的返回值对象；

            /*
             * MultipartEntityBuilder builder = MultipartEntityBuilder.create();
             * builder.addTextBody("ssoData", ssoData.toString());
             * builder.setCharset(Charset.forName(HTTP.UTF_8)); HttpEntity reqEntity = builder.build();
             * httpPost.setEntity(reqEntity); response = httpclient.execute(httpPost);
             */
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                HttpEntity respEntity = response.getEntity();
                String retStr = EntityUtils.toString(respEntity);
                // 需要引入gson jar包；
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(retStr);
                JsonObject jo = je.getAsJsonObject();
                retMsg.setCode(jo.get("code").getAsInt());
                retMsg.setMessage(jo.get("message").getAsString());
                retMsg.setObject(jo.get("object").isJsonNull() ? null : jo.get("object").getAsString());// 只有查询操作才有返回ssoData对象
            } else {
                retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
                retMsg.setMessage("调用第三方接口进行业务操作失败");
            }
        } catch (Exception e) {
            retMsg.setCode(AppConstant.RET_CODE_ERROR);
            retMsg.setMessage("系统异常");
            System.out.print(e.getMessage());
            logger.error("", e);
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection();
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    retMsg.setCode(AppConstant.RET_CODE_ERROR);
                    retMsg.setMessage("httpclient关闭失败");
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    retMsg.setCode(AppConstant.RET_CODE_ERROR);
                    retMsg.setMessage("CloseableHttpResponse关闭失败");
                }
            }

        }
        return retMsg;
    }

    @Override
    public Page<AutUserVO> rankList(PageBean pageBean, AutUser obj) throws Exception {

        Page<AutUserVO> retPage = new Page<AutUserVO>();
        // 用户查询条件
        List<AutUser> autUserList = new ArrayList<AutUser>();
        Set<Long> userIdSet = new HashSet<Long>();
        Map<Long, AutUser> autUserMap = new HashMap<Long, AutUser>();
        Boolean isNameFind = false; // 是否输入人员查找条件；
        if (StringUtils.isNotEmpty(obj.getUserName())) {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.like("user_name", obj.getUserName());
            userWhere.or("full_name LIKE {0}", "%" + obj.getUserName() + "%");
            autUserList = selectList(userWhere);
            for (AutUser autUser : autUserList) {
                userIdSet.add(autUser.getId());
                autUserMap.put(autUser.getId(), autUser);
            }
            isNameFind = true;
        }
        // 如果用户表存在搜索条件，结合用户表的查询条件从排序表按user_rank字段排序查询出数据；
        Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
        autUserRankWhere.setSqlSelect("id,user_rank");
        if (isNameFind) {
            if (userIdSet.size() > 0) {
                autUserRankWhere.in("id", userIdSet);
            } else {
                autUserRankWhere.where("1!=1", "");
            }

        }

        autUserRankWhere.orderBy("user_rank", true);
        Page<AutUserRank> autUserRankPage = autUserRankService
            .selectPage(new Page<AutUserRank>(pageBean.getPageNum(), pageBean.getPageSize()), autUserRankWhere);
        List<AutUserRank> autUserRankList = autUserRankPage.getRecords();
        List<AutUserVO> autUserVoList = new ArrayList<AutUserVO>();
        List<Long> userIdList = new ArrayList<Long>();
        for (AutUserRank autUserRank : autUserRankList) {
            AutUserVO vo = new AutUserVO();
            vo.setId(autUserRank.getId());
            vo.setUserRank(autUserRank.getUserRank());
            autUserVoList.add(vo);
            userIdList.add(autUserRank.getId());
        }

        // 未指定查询条件，则根据排序表的顺序查询； 获得userIdList，用于查询用户所在部门
        if (userIdSet.size() <= 0) {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,user_name,full_name");
            userWhere.in("id", userIdList); // 比如用户排序表中一次10条记录查询出来： userIdList
            autUserList = selectList(userWhere);// 用户表可能删除掉个别用户；
            for (AutUser autUser : autUserList) {
                autUserMap.put(autUser.getId(), autUser);
            }
        }

        // 用户id来查询部门-用户表获得deptId,以用来查询部门信息
        Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
        deptUserWhere.setSqlSelect("id,user_id,dept_id");
        deptUserWhere.in("user_id", userIdList);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(deptUserWhere);
        Set<Long> deptIdSet = new HashSet<Long>();
        for (AutDepartmentUser deptUser : deptUserList) {
            deptIdSet.add(deptUser.getDeptId());
        }
        // 根据Set里不重复的部门id，查询部门名称
        Where<AutDepartment> deptWhere = new Where<AutDepartment>();
        deptWhere.setSqlSelect("id,dept_name");
        deptWhere.in("id", deptIdSet);
        // deptUserWhere.orderBy("id", true);
        List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);

        // 根据userId查询aut_user_position表里的position_id,然后查询aut_position得到position_name
        Where<AutUserPosition> userPositionWhere = new Where<AutUserPosition>();
        userPositionWhere.setSqlSelect("id,user_id,position_id");
        userPositionWhere.in("user_id", userIdList);
        List<AutUserPosition> userPositionList = autUserPositionService.selectList(userPositionWhere);
        List<Long> positionIdList = new ArrayList<Long>();
        for (AutUserPosition userPosition : userPositionList) {
            positionIdList.add(userPosition.getPositionId());
        }
        // 根据positionIdList获得岗位名称
        Where<AutPosition> positionWhere = new Where<AutPosition>();
        positionWhere.setSqlSelect("id,position_name,dept_id");
        positionWhere.in("id", positionIdList);
        // deptUserWhere.orderBy("dept_id", true);
        List<AutPosition> positionList = autPositionService.selectList(positionWhere);

        // 拼接返回值
        /*
         * for (AutUser autUser : autUserList) { AutUserVO vo = new AutUserVO(); vo.setAutUser(autUser);
         * autUserVoList.add(vo); //与908特重复添加vo对象； }
         */
        for (AutUserVO autUserVo : autUserVoList) {
            Long userId = autUserVo.getId();
            AutUser autUser = autUserMap.get(userId);
            autUserVo.setAutUser(autUser); // 1142 1143行的描述问题；
        }

        // 1、拼接用户-部门关联表
        for (AutUserVO vo : autUserVoList) {
            for (AutDepartmentUser deptUser : deptUserList) {
                if (vo.getAutUser() != null && (vo.getAutUser().getId().equals(deptUser.getUserId()))) {
                    vo.setAutDepartmentUser(deptUser);
                    String ids = vo.getAutDeptIds();
                    if (ids != null && !"".equals(ids)) {
                        ids += deptUser.getDeptId() + ",";
                    } else {
                        ids = deptUser.getDeptId() + ",";
                    }
                    vo.setAutDeptIds(ids);
                }
            }
        }
        // 2、拼接部门
        for (AutUserVO vo : autUserVoList) {
            for (AutDepartment dept : deptList) {
                AutDepartmentUser deptUser = vo.getAutDepartmentUser();
                if (null != deptUser) {
                    if (deptUser.getDeptId().equals(dept.getId())) {
                        vo.setAutDepartment(dept);
                        String ids = vo.getAutDeptIds();
                        if (ids.indexOf(dept.getId().toString()) > -1) {
                            String names = vo.getAutDeptNames();
                            if (names != null && !"".equals(names)) {
                                names += dept.getDeptName() + ",";
                            } else {
                                names = dept.getDeptName() + ",";
                            }
                            vo.setAutDeptNames(names.substring(0, names.length() - 1));
                        }
                    }
                }
            }
        }
        // 3、拼接用户-岗位关联表
        for (AutUserVO vo : autUserVoList) {
            for (AutUserPosition userPosition : userPositionList) {
                AutUser autUser = vo.getAutUser();
                if (null != autUser) {
                    if (vo.getAutUser().getId().equals(userPosition.getUserId())) {
                        vo.setAutUserPosition(userPosition);
                        String ids = vo.getPositionids();
                        if (ids != null && !"".equals(ids)) {
                            ids += userPosition.getPositionId() + ",";
                        } else {
                            ids = userPosition.getPositionId() + ",";
                        }
                        vo.setPositionids(ids);
                    }
                }
            }
        }
        // 4、拼接岗位表
        for (AutUserVO vo : autUserVoList) {
            for (AutPosition position : positionList) {
                AutUserPosition userPosition = vo.getAutUserPosition();
                if (null != userPosition) {
                    if (userPosition.getPositionId().equals(position.getId())) {
                        vo.setAutPosition(position);
                        String ids = vo.getPositionids();
                        if (ids.indexOf(position.getId().toString()) > -1) {
                            String names = vo.getPositionNames();
                            if (names != null && !"".equals(names)) {
                                names += position.getPositionName() + ",";
                            } else {
                                names = position.getPositionName() + ",";
                            }
                            vo.setPositionNames(names.substring(0, names.length() - 1));
                        }
                    }
                }
            }
        }

        retPage.setCurrent(autUserRankPage.getCurrent());
        retPage.setSize(pageBean.getPageSize());
        retPage.setTotal(autUserRankPage.getTotal());
        retPage.setRecords(autUserVoList);

        return retPage;
    }

    @Override
    @Transactional
    public RetMsg updateUser(AutUserPubEditVO obj) throws Exception {

        RetMsg retMsg = new RetMsg();
        // 检查邮箱是否在其他用户中存在(排除自己)
        Where<AutUser> where = new Where<AutUser>();
        where.ne("id", obj.getId());
        where.eq("user_email", obj.getUserEmail());
        if (StringUtils.isNotEmpty(obj.getUserEmail()) && selectCount(where) > 1) {
            retMsg.setCode(1);
            retMsg.setMessage("邮箱已被占用");
        } else {
            // 原来的用户信息
            AutUser orgnlObj = selectById(obj.getId());
            orgnlObj.setFullName(obj.getFullName());
            // 如果MD5值相等，则没有修改密码,否则修改了
            if (!obj.getUserPwd().equals(EncryptUtil.encryptMD5(orgnlObj.getUserPwd())) && obj.getUserPwd() != null
                && obj.getUserPwd() != "") {
                orgnlObj.setUserPwd(customPasswordEncoder.encode(obj.getUserPwd()));
            }
            orgnlObj.setIsActive(obj.getIsActive());
            orgnlObj.setUserEmail(obj.getUserEmail());

            // 公共信息(用户的其他信息)，改表的信息也要修改（没有插入，有则修改）
            Where<AutUserPub> pubwhere = new Where<AutUserPub>();
            pubwhere.eq("user_id", obj.getId());
            AutUserPub pub = autUserPubService.selectOne(pubwhere);
            if (null != obj.getPhoneNumber()) {
                if (null != pub) {
                    pub.setPhoneNumber(obj.getPhoneNumber());
                } else {
                    AutUserPub pubuser = new AutUserPub();
                    pubuser.setPhoneNumber(obj.getPhoneNumber());
                    pubuser.setUserId(obj.getId());
                    pubuser.setMaxMailSize(1);
                    autUserPubService.insert(pubuser);
                }
            }
            if (null != obj.getTelNumber()) {
                if(pub != null){
                    pub.setTelNumber(obj.getTelNumber());
                    autUserPubService.updateById(pub);
                }
            }
            // 修改用户信息以及用户对应的公共信息
            updateById(orgnlObj);
            

            // 取出activiti的所有的用户信息
            List<User> userList = identityService.createUserQuery().list();
            List<Long> userIdList = new ArrayList<Long>();
            if (null != userList && !userList.isEmpty()) {
                for (User user : userList) {
                    if (StringUtils.isNotEmpty(user.getId())) {
                        userIdList.add(Long.parseLong(user.getId()));
                    }
                }
            }
            // 获取用户的岗位信息
            Where<AutUserPosition> userPositionWhere = new Where<AutUserPosition>();
            userPositionWhere.setSqlSelect("id,user_id,position_id,is_active");
            userPositionWhere.eq("user_id", obj.getId());
            userPositionWhere.eq("is_active", 1);
            List<AutUserPosition> userPositionList = autUserPositionService.selectList(userPositionWhere);
            List<Long> positionIdList = new ArrayList<Long>();
            if (null != userPositionList && !userPositionList.isEmpty()) {
                for (AutUserPosition position : userPositionList) {
                    positionIdList.add(position.getPositionId());
                }
            }
            List<AutPosition> positionList = new ArrayList<AutPosition>();
            if (null != positionIdList && !positionIdList.isEmpty()) {
                Where<AutPosition> positionWhere = new Where<AutPosition>();
                positionWhere.setSqlSelect("id");
                positionWhere.in("id", positionIdList);
                positionList = autPositionService.selectList(positionWhere);
            }

            // 获取用户的所有部门信息
            Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
            departmentUserWhere.setSqlSelect("id,user_id,dept_id,is_active");
            departmentUserWhere.eq("is_active", 1);
            departmentUserWhere.eq("user_id", obj.getId());
            List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);
            List<Long> deptIdList = new ArrayList<Long>();
            if (null != departmentUserList && !departmentUserList.isEmpty()) {
                for (AutDepartmentUser departmentUser : departmentUserList) {
                    deptIdList.add(departmentUser.getDeptId());
                }
            }
            List<AutDepartment> departmentList = new ArrayList<AutDepartment>();
            if (null != deptIdList && !deptIdList.isEmpty()) {
                Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
                departmentWhere.setSqlSelect("id");
                departmentWhere.in("id", deptIdList);
                departmentList = autDepartmentService.selectList(departmentWhere);
            }
            // 获取用户的所有角色信息
            Where<AutUserRole> userRoleWhere = new Where<AutUserRole>();
            userRoleWhere.setSqlSelect("id,role_id,user_id,is_active");
            userRoleWhere.eq("is_active", 1);
            userRoleWhere.eq("user_id", obj.getId());
            List<AutUserRole> userRoleList = autUserRoleService.selectList(userRoleWhere);
            List<Long> roleIdList = new ArrayList<Long>();
            if (null != userRoleList && !userRoleList.isEmpty()) {
                for (AutUserRole userRole : userRoleList) {
                    roleIdList.add(userRole.getRoleId());
                }
            }
            List<AutRole> roleList = new ArrayList<AutRole>();
            if (null != roleIdList && !roleIdList.isEmpty()) {
                Where<AutRole> roleWhere = new Where<AutRole>();
                roleWhere.setSqlSelect("id");
                roleWhere.in("id", roleIdList);
                roleList = autRoleService.selectList(roleWhere);
            }

            // 如果有修改激活，并且是改成激活状态
            if (obj.getIsActive() == 1) {
                if (!userIdList.contains(obj.getId())) {
                    // 如果activiti没有当前这个用户，新增

                    activitiService.delUser(obj.getId());
                    activitiService.addUser(obj.getId(), obj.getFullName());// 先删除再新增以免主键冲突

                    List<Group> groupList = identityService.createGroupQuery().list();
                    List<Long> groupIdList = new ArrayList<Long>();
                    if (null != groupList && !groupList.isEmpty()) {
                        for (Group gp : groupList) {
                            if (StringUtils.isNotEmpty(gp.getId())) {
                                groupIdList.add(Long.parseLong(gp.getId()));
                            }
                        }
                    }
                    if (null != positionList && !positionList.isEmpty()) {
                        for (AutPosition userPosition : positionList) {
                            if (groupIdList.contains(userPosition.getId())) {
                                if (groupIdList.contains(userPosition.getId())) {
                                    activitiService.delUserGroup(obj.getId(), userPosition.getId());
                                    activitiService.addUserGroup(obj.getId(), userPosition.getId());// 先删除再新增以免主键冲突
                                }
                            }
                        }
                    }
                    if (null != departmentList && !departmentList.isEmpty()) {
                        for (AutDepartment department : departmentList) {
                            if (groupIdList.contains(department.getId())) {
                                activitiService.delUserGroup(obj.getId(), department.getId());
                                activitiService.addUserGroup(obj.getId(), department.getId());// 先删除再新增以免主键冲突
                            }
                        }
                    }
                    if (null != roleList && !roleList.isEmpty()) {
                        for (AutRole role : roleList) {
                            if (groupIdList.contains(role.getId())) {
                                activitiService.delUserGroup(obj.getId(), role.getId());
                                activitiService.addUserGroup(obj.getId(), role.getId());// 先删除再新增以免主键冲突
                            }
                        }
                    }
                } else {
                    // 已经有这个用户，修改（姓名）
                    User u = identityService.createUserQuery().userId(obj.getId().toString()).singleResult();
                    u.setFirstName(obj.getFullName());
                    identityService.saveUser(u);
                }
            } else {
                // 同步activiti用户
                if (null != userIdList && !userIdList.isEmpty()) {
                    if (userIdList.contains(obj.getId())) {
                        activitiService.delUser(obj.getId());
                        if (null != positionList && !positionList.isEmpty()) {
                            for (AutPosition userPosition : positionList) {
                                activitiService.delUserGroup(obj.getId(), userPosition.getId());
                            }
                        }
                        if (null != departmentList && !departmentList.isEmpty()) {
                            for (AutDepartment department : departmentList) {
                                activitiService.delUserGroup(obj.getId(), department.getId());
                            }
                        }
                        if (null != roleList && !roleList.isEmpty()) {
                            for (AutRole role : roleList) {
                                activitiService.delUserGroup(obj.getId(), role.getId());
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

    @Override
    @Transactional
    public RetMsg addUser(AutUserAndPubVo obj, Long customUserid) throws Exception {
        RetMsg retMsg = new RetMsg();

        String charPassword = obj.getUserPwd();
        obj.setUserPwd(customPasswordEncoder.encode(obj.getUserPwd()));
        AutUser autUser = new AutUser();
        autUser.setFullName(obj.getFullName());
        autUser.setUserName(obj.getUserName());
        autUser.setUserPwd(obj.getUserPwd());
        autUser.setUserEmail(obj.getUserEmail());
        autUser.setIsDelete(0);
        autUser.setIsActive(obj.getIsActive());
        autUser.setIsAccountExpired(0);
        autUser.setIsAccountLocked(0);
        retMsg = add(autUser, charPassword);
        if (retMsg.getCode() == 0) {
            autUser = (AutUser)retMsg.getObject();
            AutUserPub autUserPut = new AutUserPub();
            autUserPut.setUserId(autUser.getId());
            autUserPut.setFaxNumber(obj.getFaxNumber());
            autUserPut.setIsDelete(0);
            autUserPut.setPhoneNumber(obj.getPhoneNumber());
            autUserPut.setMaxMailSize(1000);
            autUserPut.setTelNumber(obj.getTelNumber());
            autUserPut.setLawNumber(obj.getLawNumber());
            autUserPut.setChestCardNumber(obj.getChestCardNumber());
            AutUserPubVO pubVo = new AutUserPubVO();
            pubVo.setAutUser(autUser);
            pubVo.setAutUserPub(autUserPut);
            retMsg = autUserPubService.add(pubVo, customUserid);
        }
        retMsg.setObject(autUser);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<Map<Long, Object>> getUserDeptList() {
        Where<AutUser> where = new Where<AutUser>();
        where.eq("is_active", 1);
        where.setSqlSelect("id,full_name");
        List<AutUser> userList = selectList(where);
        List<Long> userIdList = new ArrayList<Long>();
        for (AutUser autUser : userList) {
            userIdList.add(autUser.getId());
        }
        // 用户id来查询部门-用户表获得deptId,以用来查询部门信息
        Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
        deptUserWhere.setSqlSelect("id,user_id,dept_id");
        deptUserWhere.in("user_id", userIdList);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(deptUserWhere);
        Set<Long> deptIdSet = new HashSet<Long>();
        for (AutDepartmentUser deptUser : deptUserList) {
            deptIdSet.add(deptUser.getDeptId());
        }
        // 根据Set里不重复的部门id，查询部门名称
        Where<AutDepartment> deptWhere = new Where<AutDepartment>();
        deptWhere.setSqlSelect("id,dept_name");
        deptWhere.in("id", deptIdSet);
        deptUserWhere.orderBy("id", true);
        List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);
        //拼接用户和部门信息
        ArrayList<Map<Long, Object>> userDeptList = new ArrayList<Map<Long, Object>>();
        for (AutUser autUser : userList) {
            Map<Long, Object> map = new HashMap<>();
            StringBuilder userDept = new StringBuilder();
            userDept.append(autUser.getFullName()).append("     ");
            for (AutDepartmentUser departmentUser : deptUserList) {
                if (autUser.getId().equals(departmentUser.getUserId())) {
                    String deptName = null;
                    for (AutDepartment autDepartment : deptList) {
                        if (departmentUser.getDeptId().equals(autDepartment.getId())) {
                            deptName = autDepartment.getDeptName();
                        }
                    }
                    userDept.append("(").append(deptName).append(")");
                }
            }
            map.put(autUser.getId(), userDept);
            userDeptList.add(map);
        }
        return userDeptList;
    }

}
