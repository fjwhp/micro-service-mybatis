package aljoin.aut.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AppAutDepartment;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.mapper.AutDepartmentUserMapper;
import aljoin.aut.dao.object.AppAutDepartmentUserVO;
import aljoin.aut.dao.object.AppAutOrganVO;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * (服务实现类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-21
 */
@Service
public class AutDepartmentUserServiceImpl extends ServiceImpl<AutDepartmentUserMapper, AutDepartmentUser>
    implements AutDepartmentUserService {

    private final static Logger logger = LoggerFactory.getLogger(AutDepartmentUserServiceImpl.class);
    @Resource
    private AutUserService autUserService;

    @Resource
    private AutDepartmentUserService autDepartmentUserService;

    @Resource
    private ActActivitiService activitiService;

    @Resource
    private IdentityService identityService;

    @Resource
    private AutDepartmentService autDepartmentService;

    @Resource
    private AutPositionService autPositionService;

    @Resource
    private AutUserPositionService autUserPositionService;

    @Resource
    private AutUserPubService autUserPubService;

    @Resource
    private SysParameterService sysParameterService;

    @Resource
    private AutUserRankService autUserRankService;

    @Override
    public Page<AutDepartmentUser> list(PageBean pageBean, AutDepartmentUser obj) throws Exception {
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.orderBy("department_user_rank", false);
        Page<AutDepartmentUser> page =
            selectPage(new Page<AutDepartmentUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public RetMsg addDepartmentUser(AutDepartmentUser departmentUser) {
        RetMsg retMsg = new RetMsg();
        try {
            // 根据部门id查用户id，并获得userIdList
            AutDepartment autDepartment = new AutDepartment();
            autDepartment.setId(departmentUser.getDeptId());
            List<AutDepartmentUser> deptUserList =
                autDepartmentUserService.getUserByDeptId(autDepartment).getAutDepartmentUserList();
            List<Long> deptUserIdList = new ArrayList<Long>();
            for (int i = 0; i < deptUserList.size(); i++) {
                deptUserIdList.add(deptUserList.get(i).getUserId());
                // 判断部门是否在act_id_group表中存在
            }
            // 比对传过来的id，如果有相同的不执行插入
            if (!deptUserIdList.contains(departmentUser.getUserId())) {
                autDepartmentUserService.insertOrUpdate(departmentUser);
                if (null != departmentUser.getDeptId() && null != departmentUser.getUserId()) {
                    List<Group> groupList = identityService.createGroupQuery().list();
                    List<Long> groupIdList = new ArrayList<Long>();
                    if (null != groupList && !groupList.isEmpty()) {
                        for (Group gp : groupList) {
                            if (StringUtils.isNotEmpty(gp.getId())) {
                                groupIdList.add(Long.parseLong(gp.getId()));
                            }
                        }
                    }
                    if (groupIdList.contains(departmentUser.getDeptId())) {
                        // 部门添加用户与流程表关联
                        activitiService.delUserGroup(departmentUser.getUserId(), departmentUser.getDeptId());
                        activitiService.addUserGroup(departmentUser.getUserId(), departmentUser.getDeptId());// 先删除再添加以免主键冲突
                    }
                }
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            } else {
                retMsg.setCode(1);
                retMsg.setMessage("用户已在该部门");
            }
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    @Override
    @Transactional
    public List<AutDepartmentUser> addUserDepartment(AutDepartmentUserVO departmentUserVO) {

        // 拼接被选中的“用户-部门”，插入表AutDepartmentUser
        List<AutDepartmentUser> departmentUserList = new ArrayList<AutDepartmentUser>();
        try {

            // 上次的部门-用户记录
            Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
            Long userId = departmentUserVO.getUserId();
            w1.eq("user_id", userId);
            w1.eq("is_delete", 0);
            List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w1);
            List<Long> oldDeptIdList = new ArrayList<Long>();
            for (AutDepartmentUser deptUser : deptUserList) {
                oldDeptIdList.add(deptUser.getDeptId());
            }
            // 这次选中的所有部门,以获得部门id用于筛选出前后两次不一样的部门
            List<AutDepartment> departmentList = departmentUserVO.getAutDepartmentList();
            List<Long> newDeptIdList = new ArrayList<Long>();
            if (null != departmentList && !departmentList.isEmpty()) {
                for (AutDepartment dept : departmentList) {
                    newDeptIdList.add(dept.getId());
                }
            }
            // 筛选出相比之前没有的部门(要删掉的部门)
            List<Long> diffDeptIdList = new ArrayList<Long>();
            for (Long oldDeptId : oldDeptIdList) {
                if (!newDeptIdList.contains(oldDeptId)) {
                    diffDeptIdList.add(oldDeptId);
                }
            }

            List<Long> positionIdList = new ArrayList<Long>();
            if (!diffDeptIdList.isEmpty() && null != diffDeptIdList) {
                // 找到要删掉的部门的所有岗位
                Where<AutPosition> positionWhere = new Where<AutPosition>();
                positionWhere.in("dept_id", diffDeptIdList);
                List<AutPosition> positionList = autPositionService.selectList(positionWhere);
                for (AutPosition position : positionList) {
                    positionIdList.add(position.getId());
                }
            }
            List<Group> groupList = identityService.createGroupQuery().list();
            List<Long> groupIdList = new ArrayList<Long>();
            if (null != groupList && !groupList.isEmpty()) {
                for (Group gp : groupList) {
                    if (StringUtils.isNotEmpty(gp.getId())) {
                        groupIdList.add(Long.parseLong(gp.getId()));
                    }
                }
            }
            if (!positionIdList.isEmpty() && null != positionIdList) {
                // 根据user_id,position_id删除这次用户没在部门的岗位
                Where<AutUserPosition> userPositionWhere = new Where<AutUserPosition>();
                userPositionWhere.eq("user_id", userId);
                userPositionWhere.in("position_id", positionIdList);
                List<AutUserPosition> userPositionList = autUserPositionService.selectList(userPositionWhere);
                if (null != userPositionList && !userPositionList.isEmpty()) {
                    List<Long> userPositionIdList = new ArrayList<Long>();
                    for (AutUserPosition autUserPosition : userPositionList) {
                        userPositionIdList.add(autUserPosition.getId());
                        activitiService.delUserGroup(userId, autUserPosition.getPositionId());
                    }
                    autUserPositionService.deleteBatchIds(userPositionIdList);
                }
            }
            // 删掉该用户所在的所有部门
            autDepartmentUserService.deleteDeptByUserId(departmentUserVO);
            if (groupIdList.contains(departmentUserVO.getDeptId())) {

                activitiService.delUserGroup(userId, departmentUserVO.getDeptId());
            }
            // 重新新增部门
            if (departmentList != null) {
                for (int i = 0; i < departmentList.size(); i++) {
                    // 将前端传来的信息set进autDeptUser
                    AutDepartmentUser autDeptUser = new AutDepartmentUser();
                    autDeptUser.setDeptCode(departmentList.get(i).getDeptCode());
                    autDeptUser.setDeptId(departmentList.get(i).getId());
                    autDeptUser.setUserId(departmentUserVO.getUserId());
                    if (departmentUserVO.getIsLeader() == null) {
                        autDeptUser.setIsLeader(0);
                    }
                    autDeptUser.setUserName(departmentUserVO.getUserName());
                    autDeptUser.setIsActive(departmentList.get(i).getIsActive());
                    // 用户新增部门的时候，设置该用户的默认排序为0
                    autDeptUser.setDepartmentUserRank(0);
                    // 拼好的autDeptUser，add进departmentUserList
                    departmentUserList.add(autDeptUser);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return departmentUserList;
    }

    @Override
    public List<AutDepartmentUser> getUserIdByDepartmentId(Long deptId) {

        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.setSqlSelect("user_id");
        where.eq("dept_id", deptId);
        List<AutDepartmentUser> list = selectList(where);

        return list;
    }

    @Override
    public List<AutDepartmentUser> getDeptByUserId(Long userId) throws Exception {
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.setSqlSelect("dept_id");
        where.eq("user_id", userId);
        List<AutDepartmentUser> deptUserList = selectList(where);
        return deptUserList;
    }

    @Override
    public AutDepartmentUserVO getUserByDeptId(AutDepartment obj) throws Exception {
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        Long deptId = obj.getId();
        where.setSqlSelect("id,dept_id,dept_code,user_id,user_name,department_user_rank,is_leader");
        where.eq("dept_id", deptId);
        where.orderBy("department_user_rank", true);
        // 用于存放根据部门id查询到的记录
        List<AutDepartmentUser> autDeptUserList = selectList(where);

        List<Long> userIdList = new ArrayList<Long>();
        for (AutDepartmentUser deptUser : autDeptUserList) {
            userIdList.add(deptUser.getUserId());
        }
        // 查aut_user表，获得在该部门的用户信息
        Where<AutUser> w2 = new Where<AutUser>();
        w2.in("id", userIdList);
        List<AutUser> autUserList = autUserService.selectList(w2);

        AutDepartmentUserVO vo = new AutDepartmentUserVO();
        List<AutUser> autUserList2 = new ArrayList<AutUser>();
        List<AutDepartmentUser> autDeptUserList2 = new ArrayList<AutDepartmentUser>();
        // 判断为同1个用户，按顺序add进List，用户表的fullName才会和关联表的数据对应
        for (int i = 0; i < autDeptUserList.size(); i++) {
            for (int j = 0; j < autUserList.size(); j++) {
                if (autDeptUserList.get(i).getUserId().equals(autUserList.get(j).getId())) {
                    autDeptUserList2.add(autDeptUserList.get(i));
                    autUserList2.add(autUserList.get(j));
                }
            }
        }
        vo.setAutDepartmentUserList(autDeptUserList2);
        vo.setAutUserList(autUserList2);
        return vo;
    }

    @Override
    public RetMsg deleteDeptByUserId(AutDepartmentUserVO departmentUserVO) throws Exception {

        RetMsg retMsg = new RetMsg();
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.eq("user_id", departmentUserVO.getUserId());
        autDepartmentUserService.delete(where);
        // 查询act_id_group表
        List<Group> groupList = identityService.createGroupQuery().list();
        if (null != groupList && !groupList.isEmpty()) {
            // 判断部门是否在act_id_group表中存在
            if (groupList.contains(departmentUserVO.getDeptId())) {
                // 部门添加用户与流程表关联
                activitiService.delUserGroup(departmentUserVO.getUserId(), departmentUserVO.getDeptId());
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("删除成功");
        return retMsg;
    }

    @Override
    public RetMsg deleteUserByDeptId(AutDepartmentUserVO departmentUserVO) throws Exception {
        RetMsg retMsg = new RetMsg();
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.eq("dept_id", departmentUserVO.getDeptId());
        autDepartmentUserService.delete(where);
        // 查询act_id_group表
        List<Group> groupList = identityService.createGroupQuery().list();
        if (null != groupList && !groupList.isEmpty()) {
            // 判断部门是否在act_id_group表中存在
            if (groupList.contains(departmentUserVO.getDeptId())) {
                // 部门添加用户与流程表关联
                activitiService.delUserGroup(departmentUserVO.getUserId(), departmentUserVO.getDeptId());
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("删除成功");
        return retMsg;
    }

    @Override
    public RetMsg userAddDepartment(AutDepartmentUserVO departmentUserVO) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<AutDepartmentUser> orgnlDeptUserList =
            autDepartmentUserService.getDeptByUserId(departmentUserVO.getUserId());
        List<AutDepartment> departList = departmentUserVO.getAutDepartmentList();
        // 查询出原有的 部门用户关系 进行删除（act_id_membership）
        if (null != orgnlDeptUserList && !orgnlDeptUserList.isEmpty()) {
            for (AutDepartmentUser departmentUser : orgnlDeptUserList) {
                activitiService.delUserGroup(departmentUserVO.getUserId(), departmentUser.getDeptId());
            }
        }
        // 获取到传入的 部门用户关系列表 进行添加（act_id_membership）
        if (null != departList && !departList.isEmpty()) {
            for (AutDepartment department : departList) {
                activitiService.delUserGroup(departmentUserVO.getUserId(), department.getId());
                activitiService.addUserGroup(departmentUserVO.getUserId(), department.getId());
            }
        }

        // 上面过滤后，拼装进deptId和deptName的List，用于插入数据库
        List<AutDepartmentUser> departmentUserList = addUserDepartment(departmentUserVO);
        if (departmentUserList != null && !departmentUserList.isEmpty()) {
            insertOrUpdateBatch(departmentUserList);
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public AutOrganVO getOrganList(AutDepartmentUserVO deptUser) throws Exception {
        AutOrganVO organVO = new AutOrganVO();
        List<AutUser> autUserList = new ArrayList<AutUser>();
        if (StringUtils.isNotEmpty(deptUser.getUserIds()) && deptUser.getUserIds().indexOf("null") == -1) {
            Where<AutUser> autUserWhere = new Where<AutUser>();
            autUserWhere.setSqlSelect("id,user_name,full_name");
            if (deptUser.getUserIds().indexOf(";") > -1) {
                List<String> userIdList = Arrays.asList(deptUser.getUserIds().split(";"));
                autUserWhere.in("id", userIdList);
            } else {
                autUserWhere.eq("id", deptUser.getUserIds());
            }
            autUserList = autUserService.selectList(autUserWhere);
        }

        List<Long> uIdList = new ArrayList<Long>();
        for (AutUser user : autUserList) {
            uIdList.add(user.getId());
        }

        List<AutDepartmentUser> departUserList = new ArrayList<AutDepartmentUser>();
        if (uIdList.size() > 0) {
            Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
            departmentUserWhere.setSqlSelect("id,dept_id,user_id");
            departmentUserWhere.in("user_id", uIdList);
            departUserList = autDepartmentUserService.selectList(departmentUserWhere);
        }

        List<String> deptIdList = new ArrayList<String>();
        for (AutDepartmentUser autDepartmentUser : departUserList) {
            deptIdList.add(String.valueOf(autDepartmentUser.getDeptId()));
        }

        List<String> departmtIdList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(deptUser.getDepartmentIds()) && deptUser.getDepartmentIds().indexOf("null") == -1) {
            if (deptUser.getDepartmentIds().indexOf(";") > -1) {
                departmtIdList = Arrays.asList(deptUser.getDepartmentIds().split(";"));
            } else {
                departmtIdList.add(deptUser.getDepartmentIds());
            }
        }
        if (departmtIdList.size() > 0) {
            Where<AutDepartmentUser> autDepartmentUserWhere = new Where<AutDepartmentUser>();
            autDepartmentUserWhere.setSqlSelect("id,dept_id,user_id");
            autDepartmentUserWhere.in("dept_id", departmtIdList);
            List<AutDepartmentUser> autDepartmentUsers = autDepartmentUserService.selectList(autDepartmentUserWhere);
            for (AutDepartmentUser autDepartmentUser : autDepartmentUsers) {
                uIdList.add(autDepartmentUser.getUserId());
            }
        }

        List<String> departIdList = new ArrayList<String>();
        Where<AutDepartment> deptWhere = new Where<AutDepartment>();
        deptWhere.setSqlSelect("id,dept_code,dept_level,dept_name,dept_rank,parent_id,is_active");
        deptWhere.orderBy("dept_rank", true);
        if (StringUtils.isNotEmpty(deptUser.getDepartmentIds())) {
            Set<Long> set = new HashSet<Long>();
            if (StringUtils.isNotEmpty(deptUser.getDepartmentIds())) {
                if (deptUser.getDepartmentIds().indexOf(";") > -1) {
                    String[] deptIdArr = deptUser.getDepartmentIds().split(";");
                    List<String> departmentIdList = new ArrayList<String>();
                    if (null != deptIdList && !deptIdList.isEmpty()) {
                        for (String deptId : deptIdList) {
                            departmentIdList.add(deptId);
                        }
                    }
                    for (String arr : deptIdArr) {
                        departIdList.add(arr);
                        departIdList.add(arr);
                        departmentIdList.add(arr);
                    }
                    for (String deptIds : departmentIdList) {
                        getParentAndChildDeptIds(set, Long.valueOf(deptIds));
                    }
                } else {
                    departIdList.add(deptUser.getDepartmentIds());
                    if (null != deptIdList && !deptIdList.isEmpty()) {
                        deptIdList.add(deptUser.getDepartmentIds());
                        for (String deptIds : deptIdList) {
                            getParentAndChildDeptIds(set, Long.valueOf(deptIds));
                        }
                    } else {
                        getParentAndChildDeptIds(set, Long.valueOf(deptUser.getDepartmentIds()));
                    }
                }
                deptWhere.in("id", set);
            }
        }
        List<AutDepartment> departmentList = autDepartmentService.selectList(deptWhere);

        List<String> departmentIdList = new ArrayList<String>();
        if (StringUtils.isNotEmpty(deptUser.getDepartmentIds())) {
            if (deptUser.getDepartmentIds().indexOf(";") > -1) {
                departmentIdList = Arrays.asList(deptUser.getDepartmentIds().split(";"));
            } else {
                departmentIdList.add(deptUser.getDepartmentIds());
            }
        }

        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.eq("is_active", 1);
        where.setSqlSelect("id,dept_id,dept_code,user_id,user_name,is_active");
        where.orderBy("department_user_rank", true);

        if (uIdList.size() > 0 && departIdList.size() > 0) {
            where.in("user_id", uIdList);
            where.or();
            where.in("dept_id", departIdList);
        }
        if (uIdList.size() > 0 && departIdList.size() == 0) {
            where.in(" user_id", uIdList);
        }
        if (uIdList.size() == 0 && departIdList.size() > 0) {
            where.in(" dept_id ", departIdList);
        }

        List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(where);
        if (null != departmentUserList && !departmentUserList.isEmpty()) {
            List<Long> userIdList = new ArrayList<Long>();
            for (AutDepartmentUser departmentUser : departmentUserList) {
                if (null != departmentUser && null != departmentUser.getUserId()) {
                    userIdList.add(departmentUser.getUserId());
                }
            }
            // Map<Long, Integer> rankList = autUserService.getUserRankList(userIdList,null);
            Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
            autUserRankWhere.setSqlSelect("id,user_rank");
            autUserRankWhere.in("id", userIdList);
            autUserRankWhere.orderBy("user_rank", true);
            List<AutUserRank> autUserRankList = autUserRankService.selectList(autUserRankWhere);
            List<Long> userRankIdList = new ArrayList<Long>();
            for (AutUserRank autUserRank : autUserRankList) {
                userRankIdList.add(autUserRank.getId());
            }
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.in("id", userRankIdList);
            userWhere.setSqlSelect("id,user_name,full_name");
            List<AutUser> userList = autUserService.selectList(userWhere);

            List<AutDepartmentUserVO> departmentUserList1 = new ArrayList<AutDepartmentUserVO>();
            if ((null != userList && !userList.isEmpty())) {
                for (Long id : userRankIdList) {
                    for (AutDepartmentUser departmentUser : departmentUserList) {
                        if (!id.equals(departmentUser.getUserId())) {
                            continue;
                        }
                        for (AutUser user : userList) {
                            if (null != user && StringUtils.isNotEmpty(user.getFullName())) {
                                if (null != user.getId() && null != departmentUser.getUserId()) {
                                    if (String.valueOf(user.getId())
                                        .equals(String.valueOf(departmentUser.getUserId()))) {
                                        AutDepartmentUserVO autDepartmentUser = new AutDepartmentUserVO();
                                        BeanUtils.copyProperties(departmentUser, autDepartmentUser);
                                        autDepartmentUser.setFullName(user.getFullName());
                                        departmentUserList1.add(autDepartmentUser);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            organVO.setDepartmentUserList(departmentUserList1);
        }
        if (null != departmentList) {
            organVO.setDepartmentList(departmentList);
        }

        return organVO;
    }

    @Override
    public List<AutDepartmentUser> getDeptUserList(AutDepartmentUser autDepartmentUser) throws Exception {
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        if (null != autDepartmentUser && StringUtils.isNotEmpty(autDepartmentUser.getUserName())) {
            where.like("user_name", autDepartmentUser.getUserName());
        }
        return autDepartmentUserService.selectList(where);
    }

    @Override
    public RetMsg deleteDeptUser(AutDepartmentUser obj) {

        RetMsg retMsg = new RetMsg();
        try {
            // 1、部门用户与流程相关表解除关系
            AutDepartmentUser autDepartmentUser = selectById(obj.getId());
            if (null != obj.getUserId() && null != obj.getDeptId()) {
                activitiService.delUserGroup(autDepartmentUser.getUserId(), autDepartmentUser.getDeptId());
            }
            // 2、删除部门下的用户
            autDepartmentUserService.deleteById(obj.getId());
            // 根据deptId查询该部门下所有的position_id
            Where<AutPosition> w1 = new Where<AutPosition>();
            w1.setSqlSelect("id,position_name,dept_id,dept_code,position_rank");
            w1.eq("dept_id", obj.getDeptId());
            List<AutPosition> autPositionList = autPositionService.selectList(w1);
            List<Long> positionIdList = new ArrayList<Long>();
            for (AutPosition autPosition : autPositionList) {
                positionIdList.add(autPosition.getId());
            }
            // 3、删除在用户在该部门的所有岗位(需要position_id、user_id)
            Where<AutUserPosition> w2 = new Where<AutUserPosition>();
            w2.eq("user_id", obj.getUserId());
            w2.in("position_id", positionIdList);
            autUserPositionService.delete(w2);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }

        return retMsg;
    }

    @Override
    public RetMsg getAppOrganList() throws Exception {
        RetMsg retMsg = new RetMsg();
        AppAutOrganVO organVO = new AppAutOrganVO();
        Where<AutDepartment> deptWhere = new Where<AutDepartment>();
        deptWhere.setSqlSelect("id,dept_code,dept_level,dept_name,dept_rank,parent_id,parent_code,is_active");
        deptWhere.orderBy("dept_rank", true);
        List<AutDepartment> departmentList = autDepartmentService.selectList(deptWhere);
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.eq("is_active", 1);
        where.setSqlSelect("id,dept_id,dept_code,user_id,user_name,is_active,department_user_rank,is_leader");
        List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(where);
        if (null != departmentUserList && !departmentUserList.isEmpty()) {
            List<Long> userIdList = new ArrayList<Long>();
            for (AutDepartmentUser departmentUser : departmentUserList) {
                if (null != departmentUser && null != departmentUser.getUserId()) {
                    userIdList.add(departmentUser.getUserId());
                }
            }

            Where<AutUserPub> pubWhere = new Where<AutUserPub>();
            pubWhere.in("user_id", userIdList);
            pubWhere.setSqlSelect("id,user_id,user_icon");
            List<AutUserPub> pubList = autUserPubService.selectList(pubWhere);

            Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
            autUserRankWhere.setSqlSelect("id,user_rank");
            autUserRankWhere.in("id", userIdList);
            autUserRankWhere.orderBy("user_rank", true);
            List<AutUserRank> autUserRankList = autUserRankService.selectList(autUserRankWhere);
            List<Long> userRankIdList = new ArrayList<Long>();

            for (AutUserRank autUserRank : autUserRankList) {
                userRankIdList.add(autUserRank.getId());
            }

            Where<AutUser> autUserWhere = new Where<AutUser>();
            autUserWhere.eq("is_active", 1);
            autUserWhere.in("id", userRankIdList);
            autUserWhere.setSqlSelect("id,user_name,full_name");
            List<AutUser> userList = autUserService.selectList(autUserWhere);

            List<AppAutDepartmentUserVO> departmentUserList1 = new ArrayList<AppAutDepartmentUserVO>();
            if ((null != userList && !userList.isEmpty())) {
                for (AutUserRank autUserRank : autUserRankList) {
                    for (AutDepartmentUser departmentUser : departmentUserList) {
                        if (!autUserRank.getId().equals(departmentUser.getUserId())) {
                            continue;
                        }
                        for (AutUser user : userList) {
                            if (null != user && StringUtils.isNotEmpty(user.getFullName())) {
                                if (null != user.getId() && null != departmentUser.getUserId()) {
                                    if (String.valueOf(user.getId())
                                        .equals(String.valueOf(departmentUser.getUserId()))) {
                                        AppAutDepartmentUserVO autDepartmentUser = new AppAutDepartmentUserVO();
                                        autDepartmentUser.setUserRank(autUserRank.getUserRank());
                                        BeanUtils.copyProperties(departmentUser, autDepartmentUser);
                                        autDepartmentUser.setFullName(user.getFullName());
                                        departmentUserList1.add(autDepartmentUser);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (null != pubList && !pubList.isEmpty()) {
                SysParameter parameter = sysParameterService.selectBykey("ImgServer");
                String rootPath = "";
                if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
                    rootPath = parameter.getParamValue();
                }
                for (AppAutDepartmentUserVO departmentUser : departmentUserList1) {
                    for (AutUserPub pub : pubList) {
                        if (null != pub && StringUtils.isNotEmpty(pub.getUserIcon())) {
                            if (null != pub.getUserId() && null != departmentUser.getUserId()) {
                                if (String.valueOf(pub.getUserId())
                                    .equals(String.valueOf(departmentUser.getUserId()))) {
                                    departmentUser.setHeadImg(
                                        StringUtils.isNotEmpty(pub.getUserIcon()) ? rootPath + pub.getUserIcon() : "");
                                }
                            }
                        }
                    }
                }
            }
            organVO.setDepartmentUserList(departmentUserList1);
        }

        List<AppAutDepartment> autDepartmentList = new ArrayList<AppAutDepartment>();
        if (null != departmentList && !departmentList.isEmpty()) {
            for (AutDepartment department : departmentList) {
                AppAutDepartment autDepartment = new AppAutDepartment();
                autDepartment.setDeptId(department.getId());
                BeanUtils.copyProperties(department, autDepartment);
                autDepartmentList.add(autDepartment);
            }
            organVO.setDepartmentList(autDepartmentList);
        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        retMsg.setObject(organVO);
        return retMsg;
    }

    /**
     *
     * 根据部门ID获得父部门和对应子部门
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017-12-19
     */
    public Set<Long> getParentAndChildDeptIds(Set<Long> deptSet, Long deptId) {
        Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
        departmentWhere.setSqlSelect("id,parent_id");
        departmentWhere.eq("id", deptId);
        AutDepartment department = autDepartmentService.selectOne(departmentWhere);

        List<Long> deptIdList = new ArrayList<Long>();
        if (null != department) {
            deptIdList.add(department.getId());

            // 获取原来的值和现在的值
            deptSet.addAll(deptIdList);

            // 获取子分类
            Where<AutDepartment> departmentWhere2 = new Where<AutDepartment>();
            departmentWhere2.setSqlSelect("id,parent_id");
            departmentWhere2.eq("id", department.getParentId());
            List<AutDepartment> departmentList = autDepartmentService.selectList(departmentWhere2);
            if (null != departmentList && !departmentList.isEmpty()) {
                for (AutDepartment dept : departmentList) {
                    getParentAndChildDeptIds(deptSet, dept.getId());
                }
            }
        }
        return deptSet;
    }
}
