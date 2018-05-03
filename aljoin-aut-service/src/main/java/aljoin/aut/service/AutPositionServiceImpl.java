package aljoin.aut.service;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.mapper.AutPositionMapper;
import aljoin.aut.dao.object.AutPositionDO;
import aljoin.aut.dao.object.AutPositionVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 岗位(服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-17
 */
@Service
public class AutPositionServiceImpl extends ServiceImpl<AutPositionMapper, AutPosition> implements AutPositionService {
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private IdentityService identityService;
    @Resource
    private AutUserPositionService autUserPositionService;

    @Override
    public Page<AutPositionDO> list(PageBean pageBean, AutPosition obj) throws Exception {
        Where<AutPosition> where = null;
        Page<AutPositionDO> page = new Page<AutPositionDO>();
        if (null != obj) {
            where = new Where<AutPosition>();
            where.setSqlSelect("id,position_name,dept_id,dept_code,is_active,position_rank,create_time");

            if (null != obj.getIsActive()) {
                where.eq("is_active", obj.getIsActive());
            }

            if (StringUtils.isNotEmpty(obj.getPositionName())) {
                where.like("position_name", obj.getPositionName());
                where.or(" dept_code LIKE {0} ", "%" + obj.getPositionName() + "%");
            }

            if (null != obj.getDeptId()) {
                where.eq("dept_id", obj.getDeptId());
            }

            where.orderBy("position_rank", true);

            Page<AutPosition> page2 =
                selectPage(new Page<AutPosition>(pageBean.getPageNum(), pageBean.getPageSize()), where);
            List<AutPosition> positionList = page2.getRecords();
            List<AutPositionDO> positionDOList = new ArrayList<AutPositionDO>();
            if (null != positionList && !positionList.isEmpty()) {
                List<Long> deptIdList = new ArrayList<Long>();
                for (AutPosition position : positionList) {
                    if (null != position && null != position.getDeptId()) {
                        AutPositionDO positionDO = new AutPositionDO();
                        positionDO.setAutPosition(position);
                        positionDOList.add(positionDO);
                        deptIdList.add(position.getDeptId());
                    }
                }
                if (null != deptIdList && !deptIdList.isEmpty()) {
                    Where<AutDepartment> where1 = new Where<AutDepartment>();
                    where1.in("id", deptIdList);
                    List<AutDepartment> departmentList = autDepartmentService.selectList(where1);
                    if (null != departmentList && !departmentList.isEmpty() && departmentList.size() == 1) {
                        if (null != positionDOList && !positionDOList.isEmpty()) {
                            for (int i = 0; i < positionDOList.size(); i++) {
                                AutDepartment department = departmentList.get(0);
                                AutPositionDO positionDO = positionDOList.get(i);
                                if (null != positionDO && null != department
                                    && StringUtils.isNotEmpty(department.getDeptName())) {
                                    positionDO.setDeptName(department.getDeptName());
                                }
                            }
                        }
                    }
                }
            }
            page.setRecords(positionDOList);
            page.setTotal(page2.getTotal());
            page.setSize(page2.getSize());
        }
        return page;
    }

    @Override
    public AutPositionVO selectPositionById(AutPosition obj) throws Exception {
        AutPositionVO positionVO = null;
        if (null != obj.getId()) {
            AutPosition position = this.selectById(obj.getId());
            if (null != position && null != position.getDeptId()) {
                positionVO = new AutPositionVO();
                AutDepartment department = autDepartmentService.selectById(position.getDeptId());
                BeanUtils.copyProperties(position, positionVO);
                positionVO.setDeptName(department.getDeptName());
            }
        }
        return positionVO;
    }

    @Override
    public List<AutPosition> getPositionListByDeptId(AutPosition obj) throws Exception {
        Where<AutPosition> where = new Where<AutPosition>();
        where.setSqlSelect("id,position_name,dept_id,dept_code,position_rank");
        where.eq("dept_id", obj.getDeptId());
        where.orderBy("position_rank");
        List<AutPosition> autPositionList = autPositionService.selectList(where);
        return autPositionList;
    }

    @Override
    public Page<AutPositionDO> positionList(PageBean pageBean, AutPositionVO obj) throws Exception {
        Where<AutPosition> where = null;
        Page<AutPositionDO> page = new Page<AutPositionDO>();
        if (null != obj) {
            where = new Where<AutPosition>();
            where.setSqlSelect("id,position_name,dept_id,dept_code,is_active,position_rank,create_time");

            if (null != obj.getIsActive()) {
                where.eq("is_active", obj.getIsActive());
            } else {
                where.eq("is_active", 1);
            }

            if (StringUtils.isNotEmpty(obj.getPositionName())) {
                where.like("position_name", obj.getPositionName());
                where.or(" dept_code LIKE {0} ", "%" + obj.getPositionName() + "%");
            }
            // 增加部门查询条件
            if (null != obj.getDeptId()) {
                where.eq("dept_id", obj.getDeptId());
            }

            if (null != obj.getDeptId()) {
                where.eq("dept_id", obj.getDeptId());
            }

            where.orderBy("create_time", false);
            List<Long> idList = new ArrayList<Long>();
            if (null != obj.getIdList()) {
                if (!obj.getIdList().isEmpty()) {
                    idList = obj.getIdList();
                }
            }

            Page<AutPosition> page2 =
                selectPage(new Page<AutPosition>(pageBean.getPageNum(), pageBean.getPageSize()), where);
            List<AutPosition> positionList = page2.getRecords();
            List<AutPositionDO> positionDOList = new ArrayList<AutPositionDO>();
            if (null != positionList && !positionList.isEmpty()) {
                List<Long> deptIdList = new ArrayList<Long>();
                for (AutPosition position : positionList) {
                    if (null != position && null != position.getDeptId()) {
                        AutPositionDO positionDO = new AutPositionDO();
                        positionDO.setAutPosition(position);
                        positionDO.setIsCheck(0);
                        if (idList.contains(position.getId())) {
                            positionDO.setIsCheck(1);
                        } else {
                            positionDO.setIsCheck(0);
                        }
                        positionDOList.add(positionDO);
                        deptIdList.add(position.getDeptId());
                    }
                }
                if (null != deptIdList && !deptIdList.isEmpty()) {
                    Where<AutDepartment> where1 = new Where<AutDepartment>();
                    where1.in("id", deptIdList);
                    List<AutDepartment> departmentList = autDepartmentService.selectList(where1);
                    if (null != departmentList && !departmentList.isEmpty() && departmentList.size() == 1) {
                        if (null != positionDOList && !positionDOList.isEmpty()) {
                            for (int i = 0; i < positionDOList.size(); i++) {
                                AutDepartment department = departmentList.get(0);
                                AutPositionDO positionDO = positionDOList.get(i);
                                if (null != positionDO && null != department
                                    && StringUtils.isNotEmpty(department.getDeptName())) {
                                    positionDO.setDeptName(department.getDeptName());
                                }
                            }
                        }
                    }
                }
            }
            page.setRecords(positionDOList);
            page.setTotal(page2.getTotal());
            page.setSize(page2.getSize());
        }
        return page;
    }

    @Override
    @Transactional
    public RetMsg add(AutPosition obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            insert(obj);
            if (obj.getIsActive() == 1) {
                // 添加 岗位到 act_id_group
                Group group = identityService.newGroup(obj.getId() + "");
                if (null != group) {
                    group.setType("POSITION");
                    identityService.saveGroup(group);
                    retMsg.setCode(0);
                    retMsg.setMessage("操作成功");
                }
            }
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg delete(AutPosition obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            deleteById(obj.getId());
            // act_id_group 删除岗位信息
            activitiService.delGroup(obj.getId());
            Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
            positionWhere.eq("position_id", obj.getId());
            positionWhere.eq("is_active", 1);
            positionWhere.setSqlSelect("id,user_id,position_id");
            List<AutUserPosition> userPositionList = autUserPositionService.selectList(positionWhere);
            if (null != userPositionList && !userPositionList.isEmpty()) {
                for (AutUserPosition departmentUser : userPositionList) {
                    if (null != departmentUser && null != departmentUser.getUserId()
                        && null != departmentUser.getPositionId()) {
                        // act_id_membership 删除岗位用户信息
                        activitiService.delUserGroup(departmentUser.getUserId(), departmentUser.getPositionId());
                    }
                }
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    public RetMsg validate(AutPosition obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (StringUtils.isNotEmpty(obj.getPositionName()) && null != obj.getDeptId()) {
                Where<AutPosition> where = new Where<AutPosition>();
                where.eq("dept_id", obj.getDeptId());
                where.eq("position_name", obj.getPositionName());
                if (selectCount(where) > 1) {
                    retMsg.setCode(1);
                    retMsg.setMessage("该部门已存在此岗位！");
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public Page<AutPositionDO> listIsActive(PageBean pageBean, AutPosition obj) throws Exception {
        Where<AutPosition> where = null;
        Page<AutPositionDO> page = new Page<AutPositionDO>();
        if (null != obj) {
            where = new Where<AutPosition>();
            where.setSqlSelect("id,position_name,dept_id,dept_code,is_active,position_rank,create_time");

            if (StringUtils.isNotEmpty(obj.getPositionName())) {
                where.like("position_name", obj.getPositionName());
                where.or(" dept_code LIKE {0} ", "%" + obj.getPositionName() + "%");
            }

            if (null != obj.getDeptId()) {
                where.eq("dept_id", obj.getDeptId());
            }

            where.eq("is_active", 1);
            where.orderBy("create_time", false);

            Page<AutPosition> page2 =
                selectPage(new Page<AutPosition>(pageBean.getPageNum(), pageBean.getPageSize()), where);
            List<AutPosition> positionList = page2.getRecords();
            List<AutPositionDO> positionDOList = new ArrayList<AutPositionDO>();
            if (null != positionList && !positionList.isEmpty()) {
                List<Long> deptIdList = new ArrayList<Long>();
                for (AutPosition position : positionList) {
                    if (null != position && null != position.getDeptId()) {
                        AutPositionDO positionDO = new AutPositionDO();
                        positionDO.setAutPosition(position);
                        positionDOList.add(positionDO);
                        deptIdList.add(position.getDeptId());
                    }
                }
                if (null != deptIdList && !deptIdList.isEmpty()) {
                    Where<AutDepartment> where1 = new Where<AutDepartment>();
                    where1.in("id", deptIdList);
                    List<AutDepartment> departmentList = autDepartmentService.selectList(where1);
                    if (null != departmentList && !departmentList.isEmpty() && departmentList.size() == 1) {
                        if (null != positionDOList && !positionDOList.isEmpty()) {
                            for (int i = 0; i < positionDOList.size(); i++) {
                                AutDepartment department = departmentList.get(0);
                                AutPositionDO positionDO = positionDOList.get(i);
                                if (null != positionDO && null != department
                                    && StringUtils.isNotEmpty(department.getDeptName())) {
                                    positionDO.setDeptName(department.getDeptName());
                                }
                            }
                        }
                    }
                }
            }
            page.setRecords(positionDOList);
            page.setTotal(page2.getTotal());
            page.setSize(page2.getSize());
        }
        return page;
    }

    @Override
    @Transactional
    public RetMsg update(AutPosition orgnlObj, AutPosition autPosition) {
        RetMsg retMsg = new RetMsg();
        if (null != autPosition.getPositionName()) {
            orgnlObj.setPositionName(autPosition.getPositionName());
        }
        if (null != autPosition.getPositionRank()) {
            orgnlObj.setPositionRank(autPosition.getPositionRank());
        }
        if (null != autPosition.getIsActive()) {
            orgnlObj.setIsActive(autPosition.getIsActive());
        }
        if (null != autPosition.getDeptId()) {
            orgnlObj.setDeptId(autPosition.getDeptId());
        }
        if (null != autPosition.getDeptCode()) {
            orgnlObj.setDeptCode(autPosition.getDeptCode());
        }
        updateById(orgnlObj);

        Where<AutUserPosition> positionWhere = new Where<AutUserPosition>();
        positionWhere.eq("position_id", orgnlObj.getId());
        positionWhere.eq("is_active", 1);
        positionWhere.setSqlSelect("id,user_id,position_id");
        List<AutUserPosition> userPositionList = autUserPositionService.selectList(positionWhere);
        if (autPosition.getIsActive() == 0) {
            activitiService.delGroup(orgnlObj.getId());
            // 岗位冻结解除关系act_id_membership
            if (null != userPositionList && !userPositionList.isEmpty()) {
                for (AutUserPosition departmentUser : userPositionList) {
                    if (null != departmentUser && null != departmentUser.getUserId()
                        && null != departmentUser.getPositionId()) {
                        activitiService.delUserGroup(departmentUser.getUserId(), departmentUser.getPositionId());
                    }
                }
            }
        } else {
            List<Group> groupList = identityService.createGroupQuery().list();
            List<Long> groupIdList = new ArrayList<Long>();
            if (null != groupList && !groupList.isEmpty()) {
                for (Group gp : groupList) {
                    if (null != gp && StringUtils.isNotEmpty(gp.getId())) {
                        groupIdList.add(Long.parseLong(gp.getId()));
                    }
                }
            }
            if (!groupIdList.contains(orgnlObj.getId())) {
                Group group = identityService.newGroup(orgnlObj.getId() + "");
                if (null != group) {
                    group.setType("POSITION");
                    identityService.saveGroup(group);
                }
            }

            if (null != userPositionList && !userPositionList.isEmpty()) {
                List<Group> groupList2 = identityService.createGroupQuery().list();
                List<Long> groupIdList2 = new ArrayList<Long>();
                if (null != groupList2 && !groupList2.isEmpty()) {
                    for (Group gp : groupList2) {
                        if (null != gp && StringUtils.isNotEmpty(gp.getId())) {
                            groupIdList2.add(Long.parseLong(gp.getId()));
                        }
                    }
                }
                for (AutUserPosition departmentUser : userPositionList) {
                    if (null != departmentUser && null != departmentUser.getUserId()
                        && null != departmentUser.getPositionId()) {
                        if (groupIdList2.contains(departmentUser.getPositionId())) {
                            activitiService.delUserGroup(departmentUser.getUserId(), departmentUser.getPositionId());
                            activitiService.addUserGroup(departmentUser.getUserId(), departmentUser.getPositionId());// 先删除
                                                                                                                     // 再新增以防主键冲突
                        }
                    }
                }
            }
        }

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");

        return retMsg;
    }
}
