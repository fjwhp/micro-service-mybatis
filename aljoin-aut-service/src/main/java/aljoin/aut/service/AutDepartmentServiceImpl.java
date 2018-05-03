package aljoin.aut.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import aljoin.act.iservice.ActActivitiService;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.mapper.AutDepartmentMapper;
import aljoin.aut.dao.object.AutDepartmentDO;
import aljoin.aut.dao.object.AutDepartmentVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * (服务实现类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-15
 */
@Service
public class AutDepartmentServiceImpl extends ServiceImpl<AutDepartmentMapper, AutDepartment>
    implements AutDepartmentService {

    @Resource
    private AutDepartmentMapper autDepartmentMapper;
    @Resource
    private AutDepartmentService autDepartmentService;

    @Resource
    private AutDepartmentUserService autDepartmentUserService;

    @Resource
    private ActActivitiService activitiService;

    @Resource
    private IdentityService identityService;

    @Resource
    AutUserService autUserService;

    @Override
    public Page<AutDepartment> list(PageBean pageBean, AutDepartment obj) throws Exception {

        Where<AutDepartment> where = new Where<AutDepartment>();
        if (StringUtils.isNotEmpty(obj.getDeptName())) {
            where.like("dept_name", obj.getDeptName());
        }
        // 原来按照部门编号排序，改成按照dept_rank部门(同级)排序
        where.orderBy("dept_rank", true);
        Page<AutDepartment> page =
            selectPage(new Page<AutDepartment>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public String getNextCode(int deptLevel, String parentCode, boolean isWidget) throws Exception {
        String nextCode = "";
        // 生成部门编码
        if (deptLevel == 1) {
            // 一级部门
            Where<AutDepartment> where = new Where<AutDepartment>();
            where.eq("dept_level", 1);
            where.orderBy("dept_code", false);
            where.setSqlSelect("dept_code");
            // where.last("LIMIT 0,1");
            AutDepartment autDepartment = selectOne(where);
            if (autDepartment != null) {
                nextCode = autDepartment.getDeptCode();
                String codeStr = nextCode.substring(nextCode.length() - 3);
                Integer codeNum = Integer.parseInt(codeStr);
                codeNum++;
                if (codeNum > 999) {
                    throw new Exception("部门数已经超过999");
                } else {
                    if (codeNum > 99) {
                        codeStr = String.valueOf(codeNum);
                    } else if (codeNum > 9) {
                        codeStr = "0" + codeNum;
                    } else {
                        codeStr = "00" + codeNum;
                    }
                    nextCode = codeStr;
                }
            } else {
                // 没有查到1级部门，直接设置一级部门的deptCode为001
                nextCode = "001";
            }
        } else if (deptLevel >= 2) {
            // 二级及以下的部门
            Where<AutDepartment> where = new Where<AutDepartment>();
            where.eq("parent_code", parentCode);
            where.orderBy("dept_code", false);
            // where.last("LIMIT 0,1");
            where.setSqlSelect("dept_code");
            AutDepartment autDepartment = selectOne(where);
            if (autDepartment != null) {
                // 得到最大的部门编码
                nextCode = autDepartment.getDeptCode();
                // 截取部门编码的最后3位
                String codeStr = nextCode.substring(nextCode.length() - 3);
                // 最后三位+1，如查到的部门编码最后3位是085，接下来就是086
                Integer codeNum = Integer.parseInt(codeStr);
                codeNum++;
                if (codeNum > 999) {
                    throw new Exception("部门数已经超过999");
                } else {
                    if (codeNum > 99) {
                        codeStr = parentCode.substring(0, 3 * (deptLevel - 1)) + codeNum;
                    } else if (codeNum > 9) {
                        codeStr = parentCode.substring(0, 3 * (deptLevel - 1)) + "0" + codeNum;
                    } else {
                        codeStr = parentCode.substring(0, 3 * (deptLevel - 1)) + "00" + codeNum;
                    }
                    nextCode = codeStr;
                }
            } else {
                nextCode = parentCode.substring(0, 3 * (deptLevel - 1)) + "001";
            }
        }
        return nextCode;
    }

    @Override
    public List<AutDepartment> selectByIds(List<Long> ids) throws Exception {
        return autDepartmentMapper.selectByIds(ids);
    }

    @Override
    public List<AutDepartment> getDepartmentList() {

        Where<AutDepartment> where = new Where<AutDepartment>();
        where.setSqlSelect("id,dept_code,dept_level,dept_name,dept_rank,parent_id,is_active");
        where.orderBy("dept_rank", true);
        List<AutDepartment> list = selectList(where);

        return list;
    }

    @Override
    public List<AutDepartmentDO> getDepartmentList(AutDepartmentVO departmentVO) {
        Where<AutDepartment> where = new Where<AutDepartment>();
        if (null != departmentVO) {
            if (null != departmentVO.getIsActive()) {
                where.eq("is_active", departmentVO.getIsActive());
            }
        }
        where.setSqlSelect("id,dept_code,dept_level,dept_name,dept_rank,parent_id,is_active");
        where.orderBy("dept_rank", true);

        List<Long> idList = new ArrayList<Long>();
        if (null != departmentVO.getIdList()) {
            if (!departmentVO.getIdList().isEmpty()) {
                idList = departmentVO.getIdList();
            }
        }

        List<AutDepartment> list = selectList(where);

        List<AutDepartmentDO> departmentDOList = new ArrayList<AutDepartmentDO>();
        if (!list.isEmpty()) {
            for (AutDepartment department : list) {
                AutDepartmentDO autDepartmentDO = new AutDepartmentDO();
                autDepartmentDO.setAutDepartment(department);
                autDepartmentDO.setIsCheck(0);
                if (idList.contains(department.getId())) {
                    autDepartmentDO.setIsCheck(1);
                } else {
                    autDepartmentDO.setIsCheck(0);
                }
                departmentDOList.add(autDepartmentDO);
            }
        }
        return departmentDOList;
    }

    @Override
    public List<Long> selectChildIdListByParent(Long pid, List<Long> allDeptIdList) throws Exception {
        if (allDeptIdList == null) {
            allDeptIdList = new ArrayList<Long>();
            allDeptIdList.add(pid);
        }
        Where<AutDepartment> w1 = new Where<AutDepartment>();
        w1.setSqlSelect("id,dept_level");
        w1.eq("parent_id", pid);
        List<AutDepartment> autDepartmentList = autDepartmentService.selectList(w1);
        if (autDepartmentList.size() > 0) {
            for (AutDepartment autDepartment : autDepartmentList) {
                allDeptIdList.add(autDepartment.getId());
                selectChildIdListByParent(autDepartment.getId(), allDeptIdList);
            }
        }
        return allDeptIdList;
    }

    @Override
    public List<Long> selectChildIdListByDeptCode(String deptCode) throws Exception {
        Where<AutDepartment> w1 = new Where<AutDepartment>();
        w1.setSqlSelect("id");
        w1.like("dept_code", deptCode);
        List<AutDepartment> deptIdList = autDepartmentService.selectList(w1);
        List<Long> childIdList = new ArrayList<Long>();
        for (AutDepartment deptId : deptIdList) {
            childIdList.add(deptId.getId());
        }
        return childIdList;
    }

    @Override
    public RetMsg deleteConfirm(Long id, String deptCode) throws Exception {

        RetMsg retMsg = new RetMsg();

        List<Long> deptIdList = autDepartmentService.selectChildIdListByDeptCode(deptCode);
        // 查询部门下是否还有用户
        Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
        w2.setSqlSelect("dept_id,user_id");
        w2.in("dept_id", deptIdList);
        List<AutDepartmentUser> userList = autDepartmentUserService.selectList(w2);
        // 查询有无下属部门(除去自身)
        Where<AutDepartment> w1 = new Where<AutDepartment>();
        w1.setSqlSelect("id");
        w1.in("id", deptIdList);
        List<AutDepartment> deptList = autDepartmentService.selectList(w1);
        // 删除掉本部门（查询有无下属部门的时候排除掉自身）
        Iterator<AutDepartment> it = deptList.iterator();
        while (it.hasNext()) {
            AutDepartment autDepartment = it.next();
            if (autDepartment.getId().equals(id)) {
                it.remove();
            }
        }

        if (deptList.size() != 0 || userList.size() != 0) {
            retMsg.setMessage("该部门还有下属部门或用户，是否确认删除？");
        } else {
            retMsg.setMessage("是否确认删除");
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg add(AutDepartment obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (obj.getParentCode() == null) {
                obj.setParentCode("0");
                obj.setDeptLevel(1);
                obj.setParentId(0L);
            }
            obj.setDeptCode(getNextCode(obj.getDeptLevel(), obj.getParentCode(), false));
            insert(obj);
            if (obj.getIsActive() == 1) {
                // 与activity流程表(act_id_group) 进行关联
                Group group = identityService.newGroup(obj.getId() + "");
                if (null != group) {
                    group.setType("DEPARTMENT");
                    identityService.saveGroup(group);
                }
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(AutDepartment obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj && StringUtils.isNotEmpty(obj.getDeptCode())) {
            List<Long> deptIdList = selectChildIdListByDeptCode(obj.getDeptCode());
            if (null != deptIdList && deptIdList.size() > 0) {
                Where<AutDepartment> w1 = new Where<AutDepartment>();
                w1.in("id", deptIdList);
                autDepartmentService.delete(w1);

                activitiService.delGroup(obj.getId());
                Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
                departmentUserWhere.eq("dept_id", obj.getId());
                departmentUserWhere.setSqlSelect("id,dept_id,user_id");
                List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);
                if (null != departmentUserList && !departmentUserList.isEmpty()) {
                    for (AutDepartmentUser departmentUser : departmentUserList) {
                        if (null != departmentUser && null != departmentUser.getUserId()
                            && null != departmentUser.getDeptId()) {
                            activitiService.delUserGroup(departmentUser.getUserId(), departmentUser.getDeptId());
                        }
                    }
                }
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            }
        }
        return retMsg;
    }

    @Override
    public Page<AutDepartment> listIsActive(PageBean pageBean, AutDepartment obj) throws Exception {

        Where<AutDepartment> where = new Where<AutDepartment>();
        if (StringUtils.isNotEmpty(obj.getDeptName())) {
            where.like("dept_name", obj.getDeptName());
        }
        where.eq("is_active", 1);
        where.orderBy("dept_rank", true);
        Page<AutDepartment> page =
            selectPage(new Page<AutDepartment>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public List<AutDepartment> getChildDeptList(AutDepartment obj) throws Exception {

        Long deptId = obj.getId();
        AutDepartment autDepartment = selectById(deptId);

        String deptCode = autDepartment.getDeptCode();
        Where<AutDepartment> w1 = new Where<AutDepartment>();
        // w1.like("dept_code", deptCode);
        w1.where("dept_code like {0}", deptCode + "%");
        w1.eq("is_active", 1);
        w1.eq("is_delete", 0);
        List<AutDepartment> autDepartmentList = this.selectList(w1);

        return autDepartmentList;
    }

    @Override
    public List<AutUser> getChildDeptUserList(AutDepartment obj) throws Exception {
        Long deptId = obj.getId();
        AutDepartment autDepartment = selectById(deptId);
        List<AutDepartment> autDepartmentList = new ArrayList<AutDepartment>();
        if (autDepartment.getId() != null) {
            autDepartmentList = getChildDeptList(obj);
        }
        List<Long> deptIdList = new ArrayList<Long>();
        for (AutDepartment dept : autDepartmentList) {
            deptIdList.add(dept.getId());
        }
        Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
        w2.in("dept_id", deptIdList);
        w2.setSqlSelect("user_id");
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w2);
        List<Long> userIdList = new ArrayList<Long>();
        for (AutDepartmentUser deptUser : deptUserList) {
            userIdList.add(deptUser.getUserId());
        }
        Where<AutUser> w3 = new Where<AutUser>();
        List<AutUser> autUserList = new ArrayList<AutUser>();
        if (null != userIdList && !userIdList.isEmpty()) {
            w3.in("id", userIdList);
            w3.setSqlSelect("id,full_name,user_name");
            autUserList = autUserService.selectList(w3);
        } else {
            autUserList = null;
        }
        return autUserList;
    }

    @Override
    public List<AutUser> getDeptAndChildForUserListByDeptId(Long deptId) throws Exception {
        List<AutDepartment> autDepartmentList = new ArrayList<AutDepartment>();
        List<Long> deptIdList = new ArrayList<Long>();
        List<Long> userIdList = new ArrayList<Long>();
        List<AutUser> autUserList = new ArrayList<AutUser>();

        AutDepartment autDepartment = new AutDepartment();
        autDepartment.setId(deptId);
        autDepartmentList = getChildDeptList(autDepartment);
        for (AutDepartment dept : autDepartmentList) {
            deptIdList.add(dept.getId());
        }

        Where<AutDepartmentUser> whereAutDepartMentUser = new Where<AutDepartmentUser>();
        whereAutDepartMentUser.setSqlSelect("user_id");
        whereAutDepartMentUser.in("dept_id", deptIdList);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(whereAutDepartMentUser);

        for (AutDepartmentUser deptUser : deptUserList) {
            userIdList.add(deptUser.getUserId());
        }
        Where<AutUser> whereAutUser = new Where<AutUser>();
        if (null != userIdList && !userIdList.isEmpty()) {
            whereAutUser.setSqlSelect("id,full_name,user_name");
            whereAutUser.in("id", userIdList);
            autUserList = autUserService.selectList(whereAutUser);
        } else {
            autUserList = null;
        }
        return autUserList;
    }

    @Override
    public Page<AutDepartment> getChildDeptPage(AutDepartment obj, PageBean pageBean) throws Exception {

        List<AutDepartment> deptList = getChildDeptList(obj);

        List<Long> deptIdList = new ArrayList<Long>();
        for (AutDepartment dept : deptList) {
            deptIdList.add(dept.getId());
        }
        Where<AutDepartment> w1 = new Where<AutDepartment>();
        w1.in("id", deptIdList);
        w1.setSqlSelect("id,dept_code,dept_name");
        Page<AutDepartment> page =
            selectPage(new Page<AutDepartment>(pageBean.getPageNum(), pageBean.getPageSize()), w1);

        return page;

    }

}
