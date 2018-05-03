package aljoin.aut.service;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.*;
import aljoin.aut.dao.mapper.AutUserPositionMapper;
import aljoin.aut.dao.object.AutUserPositionVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.activiti.engine.IdentityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 用户-岗位表(服务实现类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-01
 */
@Service
public class AutUserPositionServiceImpl extends ServiceImpl<AutUserPositionMapper, AutUserPosition>
    implements AutUserPositionService {

    @Resource
    AutUserPositionService autUserPositionService;

    @Resource
    private ActActivitiService activitiService;

    @Resource
    AutDepartmentUserService autDepartmentUserService;

    @Resource
    private IdentityService identityService;

    @Resource
    private AutDepartmentService autDepartmentService;

    @Resource
    private AutPositionService autPositionService;

    @Override
    public Page<AutUserPosition> list(PageBean pageBean, AutUserPosition obj) throws Exception {
        Where<AutUserPosition> where = new Where<AutUserPosition>();
        where.orderBy("create_time", false);
        Page<AutUserPosition> page =
            selectPage(new Page<AutUserPosition>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    @Transactional
    public List<AutUserPosition> addUserPositionList(AutUserPositionVO userPositionVO) throws Exception {

        List<AutUserPosition> userPositionList = new ArrayList<AutUserPosition>();

        // 拼接前先删除该用户下的岗位信息，然后再用户新增
        autUserPositionService.deleteById(userPositionVO.getAutUser().getId());
        // 拼接AutUser对象和positionList
        AutUser autUser = userPositionVO.getAutUser();
        List<AutPosition> positionList = userPositionVO.getPositionList();
        for (int i = 0; i < positionList.size(); i++) {
            AutUserPosition userPosition = new AutUserPosition();
            userPosition.setIsActive(positionList.get(i).getIsActive());
            userPosition.setUserId(autUser.getId());
            userPosition.setUserName(autUser.getUserName());
            // userPosition.setPositionId(positionList.get(i).getId());
            userPositionList.add(userPosition);
        }

        return userPositionList;
    }

    @Override
    public List<AutUserPosition> getPositoinByUserId(AutUserPosition obj) throws Exception {

        Where<AutUserPosition> where = new Where<AutUserPosition>();
        where.setSqlSelect("id,user_id,position_id,is_active");
        where.eq("user_id", obj.getUserId());

        List<AutUserPosition> positionList = selectList(where);
        return positionList;
    }

    @Override
    public Boolean compareUserPosition(AutUserPosition obj) throws Exception {

        Boolean judge = false;

        List<AutUserPosition> userPositionList = autUserPositionService.getPositoinByUserId(obj);
        // 根据userId有查询到岗位再对比
        if (userPositionList != null && !userPositionList.isEmpty()) {
            for (int i = 0; i < userPositionList.size(); i++) {
                if (userPositionList.get(i).getPositionId().equals(obj.getPositionId())) {
                    return false;
                } else {
                    judge = true;
                }
            }
        } else {
            judge = true;
        }
        return judge;

    }

    @Override
    public RetMsg addUserPosition(AutUserPosition obj) throws Exception {
        RetMsg retMsg = new RetMsg();

        AutUserPosition autUserPosition = new AutUserPosition();

        // 比较新增的岗位是否分配过，未分配再新增
        if (autUserPositionService.compareUserPosition(obj)) {
            autUserPosition.setPositionId(obj.getPositionId());
            autUserPosition.setUserId(obj.getUserId());
            autUserPosition.setUserName(obj.getUserName());
            autUserPosition.setIsActive(obj.getIsActive());
            if (obj.getUserPositionRank() == null) {
                autUserPosition.setUserPositionRank(0);
            }

            // obj里有岗位id，根据岗位id查deptId、userId
            Long userId = obj.getUserId();
            Long positionId = obj.getPositionId();
            Where<AutPosition> w1 = new Where<AutPosition>();
            w1.setSqlSelect("id,dept_id");
            w1.eq("id", positionId);
            AutPosition autPosition = autPositionService.selectOne(w1);
            AutDepartment autDepartment = autDepartmentService.selectById(autPosition.getDeptId());

            Long deptId = autPosition.getDeptId();
            Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
            w2.setSqlSelect("id");
            w2.eq("dept_id", deptId);
            w2.eq("user_id", userId);
            AutDepartmentUser deptUser = autDepartmentUserService.selectOne(w2);
            // 判断对象空，即用户没在这个部门才把用户新增进部门,然后再新增用户岗位
            if (!StringUtils.checkValNotNull(deptUser)) {
                AutDepartmentUser deptUser2 = new AutDepartmentUser();
                deptUser2.setUserId(userId);
                deptUser2.setDeptId(deptId);
                deptUser2.setDeptCode(autDepartment.getDeptCode());
                deptUser2.setUserName(obj.getUserName());
                deptUser2.setIsActive(1);
                // 默认排序0
                deptUser2.setDepartmentUserRank(0);
                deptUser2.setIsLeader(0);
                autDepartmentUserService.addDepartmentUser(deptUser2);
            }

            autUserPositionService.insert(autUserPosition);

            if (null != autUserPosition.getUserId() && null != autUserPosition.getPositionId()) {
                // 用户分配岗位与流程表关联
                activitiService.addUserGroup(autUserPosition.getUserId(), autUserPosition.getPositionId());
            }

            retMsg.setMessage("新增成功");
        } else {
            retMsg.setMessage("已分配该岗位");
        }
        return retMsg;
    }

    @Override
    public RetMsg deleteUserPosition(AutUserPosition obj) throws Exception {

        RetMsg retMsg = new RetMsg();

        Where<AutUserPosition> where = new Where<AutUserPosition>();
        where.eq("user_id", obj.getUserId());
        where.eq("position_id", obj.getPositionId());
        delete(where);
        // 判断岗位是否在act_id_group表中存在
        if (null != obj.getUserId() && null != obj.getPositionId()) {
            // 用户分配岗位与流程表解除关联
            activitiService.delUserGroup(obj.getUserId(), obj.getPositionId());
        }
        retMsg.setMessage("删除成功");
        return retMsg;
    }

}
