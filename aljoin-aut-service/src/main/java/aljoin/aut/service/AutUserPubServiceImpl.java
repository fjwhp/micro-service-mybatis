package aljoin.aut.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.mapper.AutUserPubMapper;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomPasswordEncoder;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 用户公共信息表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Service
public class AutUserPubServiceImpl extends ServiceImpl<AutUserPubMapper, AutUserPub> implements AutUserPubService {

    private final static Logger logger = LoggerFactory.getLogger(AutUserPubServiceImpl.class);

    @Resource
    private AutUserPubMapper mapper;
    @Resource
    private AutUserService autUserService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private CustomPasswordEncoder customPasswordEncoder;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private ResResourceService resResourceService;

    @Override
    public Page<AutUserPub> list(PageBean pageBean, AutUserPubVO obj) throws Exception {

        Where<AutUserPub> where = new Where<AutUserPub>();
        where.orderBy("create_time", false);
        Page<AutUserPub> page = selectPage(new Page<AutUserPub>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutUserPub obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public RetMsg updatePwd(AutUserPubVO obj, Long customUserId) throws Exception {

        // 用户输入的老密码
        String oldPwd = obj.getAutUser().getUserPwd();
        // 新密码
        String newPwd = obj.getNewUserPwd();
        // 新密码确认
        String newPwdConfirm = obj.getNewUserPwdConfirm();
        AutUser autUser = autUserService.selectById(customUserId);
        RetMsg retMsg = new RetMsg();
        // 校验原密码是否正确
        if (customPasswordEncoder.matches(oldPwd, autUser.getUserPwd())) {
            // 比对前后输入的2次新密码是否相同
            if (newPwd == newPwdConfirm || newPwd.equals(newPwdConfirm)) {
                autUser.setUserPwd(customPasswordEncoder.encode(newPwd));
                autUserService.updateById(autUser);
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            } else {
                retMsg.setCode(1);
                retMsg.setMessage("前后两次新密码不一致");
            }
        } else {
            retMsg.setCode(1);
            retMsg.setMessage("原密码不正确");
        }
        return retMsg;
    }

    @Override
    public List<AutUserPubVO> getAutUserPubVOList(AutUserPubVO autUserPubVO) throws Exception {

        AutDepartment autDepartment = autUserPubVO.getAutDepartment();
        AutPosition position = autUserPubVO.getAutPosition();
        AutUser user = autUserPubVO.getAutUser();

        Long deptId = autDepartment.getId();
        AutDepartment dept = autDepartmentService.selectById(deptId);
        // 根据dept_code查出所有下属部门
        Where<AutDepartmentUser> aduWhere = new Where<AutDepartmentUser>();
        aduWhere.setSqlSelect("user_id,dept_code,department_user_rank");
        if (dept != null && dept.getDeptCode() != null) {
            aduWhere.like("dept_code", dept.getDeptCode());
        }
        aduWhere.orderBy("department_user_rank", false);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(aduWhere);

        // 获得userIdList
        List<Long> userIdList = new ArrayList<Long>();
        for (AutDepartmentUser deptUser : deptUserList) {
            userIdList.add(deptUser.getUserId());
        }

        List<AutUser> autUserList = new ArrayList<AutUser>();
        List<AutUserPosition> autUserPositionList = new ArrayList<AutUserPosition>();
        List<AutPosition> autPositionList = new ArrayList<AutPosition>();
        List<AutUserPub> autUserPubList = new ArrayList<AutUserPub>();

        if (null != userIdList && !userIdList.isEmpty()) {
            // 根据用户id找aut_user获获得所有full_name、user_email
            Where<AutUser> auWhere = new Where<AutUser>();
            auWhere.setSqlSelect("id,full_name,user_email");
            auWhere.orderBy("id");
            auWhere.in("id", userIdList);
            if (StringUtils.checkValNotNull(user)) {
                auWhere.like("full_name", user.getFullName());
            }
            autUserList = autUserService.selectList(auWhere);

            // 根据userId找aut_user_position，获得用户所有的所在岗位（1用户可能多岗）
            Where<AutUserPosition> aupWhere = new Where<AutUserPosition>();
            aupWhere.setSqlSelect("user_id,position_id");
            aupWhere.in("user_id", userIdList);
            autUserPositionList = autUserPositionService.selectList(aupWhere);

            // 根据部门id，找aut_position获得该部门下的所有岗位id(有deptId,positionId)
            Where<AutPosition> apWhere = new Where<AutPosition>();
            apWhere.setSqlSelect("id,position_name,dept_id");
            if (dept != null && dept.getDeptCode() != null) {
                apWhere.like("dept_code", dept.getDeptCode());
            }

            if (StringUtils.checkValNotNull(position)) {
                apWhere.like("position_name", position.getPositionName());
            }
            autPositionList = autPositionService.selectList(apWhere);

            // 根据userIdList查询相关所有用户公共信息
            Where<AutUserPub> auPubWhere = new Where<AutUserPub>();
            auPubWhere
                .setSqlSelect("user_id,phone_number,tel_number,fax_number,law_number,chest_card_number,user_icon");
            auPubWhere.in("user_id", userIdList);
            autUserPubList = autUserPubService.selectList(auPubWhere);
        }
        // 拼接返回值VOList
        List<AutUserPubVO> autUserPubVOList = new ArrayList<AutUserPubVO>();

        // 1、拼接用户进voList
        for (AutUser autUser : autUserList) {
            AutUserPubVO vo = new AutUserPubVO();
            vo.setAutUser(autUser);
            autUserPubVOList.add(vo);
        }
        // 2、拼接用户-岗位关联表
        for (AutUserPubVO vo : autUserPubVOList) {
            for (AutUserPosition autUserPosition : autUserPositionList) {
                if (vo.getAutUser().getId().equals(autUserPosition.getUserId())) {
                    vo.setAutUserPosition(autUserPosition);
                }
            }
        }

        // 3、岗位表
        for (AutUserPubVO vo : autUserPubVOList) {
            AutUserPosition autUserPosition = vo.getAutUserPosition();
            for (AutPosition autPosition : autPositionList) {
                if (StringUtils.checkValNotNull(autUserPosition)) {
                    if (autUserPosition.getPositionId() == autPosition.getId()
                        || vo.getAutUserPosition().getPositionId().equals(autPosition.getId())) {
                        vo.setAutPosition(autPosition);
                    }
                }
            }
        }

        // 4、拼接公共信息
        for (AutUserPubVO vo : autUserPubVOList) {
            for (AutUserPub autUserPub : autUserPubList) {
                if (vo.getAutUser().getId().equals(autUserPub.getUserId())) {
                    vo.setAutUserPub(autUserPub);
                }
            }
        }

        // 剔除掉不符合岗位查询条件的User
        List<AutUserPubVO> autUserPubVOList2 = new ArrayList<AutUserPubVO>();
        for (AutUserPubVO vo : autUserPubVOList) {
            for (AutPosition autPosition : autPositionList) {
                AutUserPubVO userPubVo = new AutUserPubVO();
                AutPosition autPosition2 = vo.getAutPosition();
                if (StringUtils.checkValNotNull(autPosition2)) {
                    if (autPosition2.getId() == autPosition.getId()
                        || autPosition2.getId().equals(autPosition.getId())) {
                        userPubVo.setAutUser(vo.getAutUser());
                        userPubVo.setAutDepartment(vo.getAutDepartment());
                        userPubVo.setAutDepartmentUser(vo.getAutDepartmentUser());
                        userPubVo.setAutUserPub(vo.getAutUserPub());
                        userPubVo.setAutUserPosition(vo.getAutUserPosition());
                        userPubVo.setAutPosition(vo.getAutPosition());
                        autUserPubVOList2.add(userPubVo);
                    }
                }
            }
        }

        if (StringUtils.checkValNotNull(position)) {
            return autUserPubVOList2;
        } else {
            return autUserPubVOList;
        }
    }

    @Override
    @Transactional
    public RetMsg add(AutUserPubVO obj, Long customUserId) throws Exception {
        RetMsg retMsg = new RetMsg();
        try {
            // Long userId = customUser.getUserId();
            AutUser autUser = obj.getAutUser();
            AutUserPub autUserPub = obj.getAutUserPub();
            if (StringUtils.checkValNotNull(autUser.getUserEmail())) {
                AutUser orgnlObj = autUserService.selectById(customUserId);
                orgnlObj.setUserEmail(autUser.getUserEmail());
                autUserService.updateById(orgnlObj);
            }
            if (StringUtils.checkValNotNull(autUserPub)) {
                if (autUserPub.getUserId() == null) {
                    autUserPub.setUserId(customUserId);
                }
                if (StringUtils.checkValNull(autUserPub.getMaxMailSize())) {
                    // 设置默认邮件大小1G
                    autUserPub.setMaxMailSize(1073741824);
                }
                autUserPubService.insert(autUserPub);
            }
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg update(AutUserPubVO obj, Long customUserId) throws Exception {

        RetMsg retMsg = new RetMsg();
        try {
            AutUser autUser = obj.getAutUser();
            AutUserPub autUserPub = obj.getAutUserPub();

            if (StringUtils.checkValNotNull(autUser.getUserEmail())) {
                AutUser orgnlObj = autUserService.selectById(customUserId);
                orgnlObj.setUserEmail(autUser.getUserEmail());
                autUserService.updateById(orgnlObj);
            } else {
                AutUser orgnlObj = autUserService.selectById(customUserId);
                orgnlObj.setUserEmail("");
                autUserService.updateById(orgnlObj);
            }
            // 前端传来的AutUserPub如果非空则插入，如果为空则不处理
            if (StringUtils.checkValNotNull(autUserPub)) {
                AutUserPub autUserPub2 = autUserPubService.selectById(autUserPub.getId());
                // 1、有查到用户信息，则更新
                if (StringUtils.checkValNotNull(autUserPub2)) {
                    autUserPub2.setUserId(customUserId);
                    autUserPub2.setPhoneNumber(autUserPub.getPhoneNumber());
                    autUserPub2.setTelNumber(autUserPub.getTelNumber());
                    autUserPub2.setFaxNumber(autUserPub.getFaxNumber());
                    autUserPub2.setLawNumber(autUserPub.getLawNumber());
                    autUserPub2.setChestCardNumber(autUserPub.getChestCardNumber());
                    autUserPub2.setUserIcon(autUserPub.getUserIcon());
                    autUserPubService.updateById(autUserPub2);
                    // 2、没查到用户信息，则新增
                } else {
                    add(obj, customUserId);
                }
            }
            retMsg.setCode(0);
            retMsg.setMessage("修改成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @Override
    public AutUserPubVO getById(Long customUserId) throws Exception {

        // 用户信息
        Where<AutUser> w1 = new Where<AutUser>();
        w1.setSqlSelect("id,user_name,full_name,user_email");
        w1.eq("id", customUserId);
        AutUser autUser = autUserService.selectOne(w1);
        // 部门信息
        Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
        w2.setSqlSelect("id,dept_id,user_id");
        w2.eq("user_id", customUserId);
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
        w4.eq("user_id", customUserId);
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
        w6.eq("user_id", customUserId);
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
        }
        if (StringUtils.checkValNotNull(autUserPub)) {
            vo.setAutUserPub(autUserPub);
        }

        return vo;
    }

    @Override
    public AutUserPubVO appGetById(String userid) throws Exception {

        // 用户信息
        Where<AutUser> w1 = new Where<AutUser>();
        w1.setSqlSelect("id,user_name,full_name,user_email");
        w1.eq("id", userid);
        AutUser autUser = autUserService.selectOne(w1);
        // 部门信息
        Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
        w2.setSqlSelect("id,dept_id,user_id");
        w2.eq("user_id", userid);
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
        w4.eq("user_id", userid);
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
        w6.eq("user_id", userid);
        AutUserPub autUserPub = autUserPubService.selectOne(w6);

        // 拼接信息
        AutUserPubVO vo = new AutUserPubVO();
        vo.setAutUser(autUser);
        if (null != deptList && !deptList.isEmpty()) {
            // 如果用户有多个部门
            AutDepartment department = new AutDepartment();
            String name = "";
            for (int i = 0; i < deptList.size(); i++) {
                name += deptList.get(i).getDeptName() + ";";
            }
            department.setDeptName(name.substring(0, name.length() - 1));
            vo.setAutDepartment(department);
            vo.setAutDeptNames(name.substring(0, name.length() - 1));
        }
        if (null != positionList && !positionList.isEmpty()) {
            // 如果用户有多个岗位
            AutPosition position = new AutPosition();
            String name = "";
            for (int i = 0; i < positionList.size(); i++) {
                name += positionList.get(i).getPositionName() + ";";
            }
            position.setPositionName(name.substring(0, name.length() - 1));
            vo.setPositionNames(name.substring(0, name.length() - 1));
            vo.setAutPosition(position);
        }
        if (StringUtils.checkValNotNull(autUserPub)) {
            vo.setAutUserPub(autUserPub);
        }

        return vo;
    }

    @SuppressWarnings("unused")
    @Override
    public List<AutUserPubVO> getMyDeptAutUserPubVOList(Long customUserId) throws Exception {
        // 获得登录用户id，查询用户-部门表
        List<AutUserPubVO> myDeptAutUserPubVOList = new ArrayList<AutUserPubVO>();
        Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
        where.setSqlSelect("id,user_id,dept_id");
        where.eq("user_id", customUserId);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(where);
        // 获得所在部门
        List<AutDepartment> deptList = new ArrayList<AutDepartment>();
        String deptIds = "";
        if (null != deptUserList && !deptUserList.isEmpty()) {
            for (AutDepartmentUser deptUser : deptUserList) {
                deptIds += deptUser.getDeptId() + ",";
            }
        }
        if (deptIds.length() > 0) {
            Where<AutDepartmentUser> udwhere = new Where<AutDepartmentUser>();
            udwhere.setSqlSelect("id,user_id,dept_id");
            udwhere.in("dept_id", deptIds);
            List<AutDepartmentUser> deptUserLists = autDepartmentUserService.selectList(udwhere);
            if (deptUserLists != null && deptUserLists.size() > 0) {
                deptIds = "";
                for (AutDepartmentUser autDepartmentUser : deptUserLists) {
                    deptIds += autDepartmentUser.getUserId() + ",";
                }
                Where<AutUser> uwhere = new Where<AutUser>();
                uwhere.setSqlSelect("id,user_id,dept_id");
                uwhere.in("id", deptIds);
                uwhere.setSqlSelect("id,full_name");
                List<AutUser> userLists = autUserService.selectList(uwhere);
                Where<AutUserPub> upwhere = new Where<AutUserPub>();
                upwhere.in("user_id", deptIds);
                upwhere.setSqlSelect("user_id,phone_number");
                List<AutUserPub> userPubLists = autUserPubService.selectList(upwhere);
                Map<String, AutUserPub> userpMap = new HashMap<String, AutUserPub>();
                if (userPubLists != null && userPubLists.size() > 0) {
                    for (AutUserPub autUserPub : userPubLists) {
                        userpMap.put(autUserPub.getUserId().toString(), autUserPub);
                    }
                }
                if (userLists != null && userLists.size() > 0) {
                    List<Long> useridlist = new ArrayList<Long>();
                    for (AutUser autUser : userLists) {
                        AutUserPubVO vo = new AutUserPubVO();
                        useridlist.add(autUser.getId());
                        vo.setAutUser(autUser);
                        if (userpMap.containsKey(autUser.getId().toString())) {
                            vo.setAutUserPub(userpMap.get(autUser.getId().toString()));
                        }
                        myDeptAutUserPubVOList.add(vo);
                    }
                    Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                    List<AutUserPubVO> tmpList = myDeptAutUserPubVOList;
                    if (rankList.size() > 0) {
                        for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                            String usrid = entry.getKey().toString();
                            int no = entry.getValue();
                            no = no - 1;
                            for (AutUserPubVO autUserPubVO : tmpList) {
                                String tmp = autUserPubVO.getAutUser().getId().toString();
                                if (tmp.equals(usrid)) {
                                    myDeptAutUserPubVOList.set(no, autUserPubVO);
                                }
                            }
                        }
                    }
                }
            }
        }
        return myDeptAutUserPubVOList;
    }

    @SuppressWarnings("unused")
    @Override
    public List<AutUserPubVO> getMyCompAutUserPubVOList(Long customUserId) throws Exception {

        List<AutUserPubVO> myCompAutUserPubVOList = new ArrayList<AutUserPubVO>();
        Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
        w1.setSqlSelect("id,user_id,dept_id,dept_code");
        w1.eq("user_id", customUserId);
        List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w1);
        List<AutDepartment> deptList = new ArrayList<AutDepartment>();
        if (null != deptUserList && !deptUserList.isEmpty()) {
            String deptCodes = "";
            List<AutDepartment> dlists = new ArrayList<AutDepartment>();
            for (AutDepartmentUser deptUser : deptUserList) {
                String deptCode = deptUser.getDeptCode();
                if (deptCode.length() > 6) {
                    deptCode = deptCode.substring(0, 6);
                }
                Where<AutDepartment> w5 = new Where<AutDepartment>();
                w5.where("dept_code like {0}", deptCode + "%");
                List<AutDepartment> dlist = autDepartmentService.selectList(w5);
                if (dlist != null && dlist.size() > 0) {
                    dlists.addAll(dlist);
                }
            }
            String deptIds = "";
            if (dlists != null && dlists.size() > 0) {
                for (AutDepartment autDepartment : dlists) {
                    deptIds += autDepartment.getId() + ",";
                }
            }

            if (deptIds.length() > 0) {
                Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
                w2.setSqlSelect("id,user_id,dept_id");
                w2.in("dept_id", deptIds);
                List<AutDepartmentUser> deptUList = autDepartmentUserService.selectList(w2);

                String ids = "";
                if (deptUList != null && deptUList.size() > 0) {
                    for (AutDepartmentUser autDepartmentUser : deptUList) {
                        ids += autDepartmentUser.getUserId() + ",";
                    }
                    if (ids.length() > 0) {
                        Where<AutUser> uwhere = new Where<AutUser>();
                        uwhere.setSqlSelect("id,user_id,dept_id");
                        uwhere.in("id", ids);
                        uwhere.setSqlSelect("id,full_name");
                        List<AutUser> userLists = autUserService.selectList(uwhere);
                        Where<AutUserPub> upwhere = new Where<AutUserPub>();
                        upwhere.in("user_id", ids);
                        upwhere.setSqlSelect("user_id,phone_number");
                        List<AutUserPub> userPubLists = autUserPubService.selectList(upwhere);
                        Map<String, AutUserPub> userpMap = new HashMap<String, AutUserPub>();
                        if (userPubLists != null && userPubLists.size() > 0) {
                            for (AutUserPub autUserPub : userPubLists) {
                                userpMap.put(autUserPub.getUserId().toString(), autUserPub);
                            }
                        }
                        if (userLists != null && userLists.size() > 0) {
                            List<Long> useridlist = new ArrayList<Long>();
                            for (AutUser autUser : userLists) {
                                AutUserPubVO vo = new AutUserPubVO();
                                useridlist.add(autUser.getId());
                                vo.setAutUser(autUser);
                                if (userpMap.containsKey(autUser.getId().toString())) {
                                    vo.setAutUserPub(userpMap.get(autUser.getId().toString()));
                                }
                                myCompAutUserPubVOList.add(vo);
                            }
                            Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                            List<AutUserPubVO> tmpList = myCompAutUserPubVOList;
                            if (rankList.size() > 0) {
                                for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                                    String usrid = entry.getKey().toString();
                                    int no = entry.getValue();
                                    no = no - 1;
                                    for (AutUserPubVO autUserPubVO : tmpList) {
                                        String tmp = autUserPubVO.getAutUser().getId().toString();
                                        if (tmp.equals(usrid)) {
                                            myCompAutUserPubVOList.set(no, autUserPubVO);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }
        }

        return myCompAutUserPubVOList;
    }

    @Override
    @Transactional
    public List<AutUserPubVO> searchUserPubInfo(AutUserPubVO vo, Long customUserId) throws Exception {

        AutUserPubVO autUserPubVO = new AutUserPubVO();
        List<AutUserPubVO> autUserPubVOList = new ArrayList<AutUserPubVO>();
        // 查询情形1：有或无部门id，可能有其他查询条件
        if (StringUtils.checkValNotNull(vo) || null != vo) {
            AutDepartment autDepartment = vo.getAutDepartment();
            AutUser autUser = vo.getAutUser();
            AutPosition autPosition = vo.getAutPosition();
            // 查询用户
            if (StringUtils.checkValNotNull(autUser) && null != autUser.getFullName()) {
                autUserPubVO.setAutUser(autUser);
            }
            // 查询岗位
            if (StringUtils.checkValNotNull(autPosition) && null != autPosition.getPositionName()) {
                autUserPubVO.setAutPosition(autPosition);
            }
            // 有传部门id
            if (null != autDepartment && null != autDepartment.getId()) {
                Long deptId = autDepartment.getId();
                AutDepartment dept = autDepartmentService.selectById(deptId);
                autUserPubVO.setAutDepartment(dept);
                autUserPubVOList.add(autUserPubVO);
            } else {
                // 无部门id，查询登录用户所在部门
                Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
                w1.in("user_id", String.valueOf(customUserId));
                w1.orderBy("department_user_rank", true);
                List<AutDepartmentUser> autDeptUserList = autDepartmentUserService.selectList(w1);
                List<Long> deptIdList = new ArrayList<Long>();
                for (AutDepartmentUser autDepartmentUser : autDeptUserList) {
                    deptIdList.add(autDepartmentUser.getDeptId());
                }
                List<AutDepartment> deptList = autDepartmentService.selectBatchIds(deptIdList);
                for (AutDepartment dept : deptList) {
                    autUserPubVO.setAutDepartment(dept);
                    autUserPubVOList.add(autUserPubVO);
                }
            }
        } else {
            // 查询情形1：无任何查询条件，默认查询用户所在部门通讯录
            Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
            w1.in("user_id", String.valueOf(customUserId));
            w1.orderBy("department_user_rank", true);
            List<AutDepartmentUser> autDeptUserList = autDepartmentUserService.selectList(w1);
            List<Long> deptIdList = new ArrayList<Long>();
            for (AutDepartmentUser autDepartmentUser : autDeptUserList) {
                deptIdList.add(autDepartmentUser.getDeptId());
            }
            List<AutDepartment> deptList = autDepartmentService.selectBatchIds(deptIdList);
            for (AutDepartment dept : deptList) {
                autUserPubVO.setAutDepartment(dept);
                autUserPubVOList.add(autUserPubVO);
            }
        }

        List<AutUserPubVO> autUserPubVoList = new ArrayList<AutUserPubVO>();
        for (AutUserPubVO userPubVO : autUserPubVOList) {
            List<AutUserPubVO> autUserPubVoList2 = new ArrayList<AutUserPubVO>();
            autUserPubVoList2 = getAutUserPubVOList(userPubVO);
            autUserPubVoList.addAll(autUserPubVoList2);
        }
        // 调用查询接口
        return autUserPubVoList;
    }

    @SuppressWarnings("unused")
    @Override
    @Transactional
    public Page<AutUserPubVO> searchValue(AutUserPubVO vo, Long customUserId, Integer num, Integer size)
        throws Exception {
        Page<AutUserPubVO> page = new Page<AutUserPubVO>();
        List<AutUserPubVO> autUserPubVoList = new ArrayList<AutUserPubVO>();
        // 查询条件
        String search = "";
        if (vo.getAutUser() != null && vo.getAutUser().getFullName() != null) {
            search = vo.getAutUser().getFullName();
        }
        // 查询部门
        String dept = "";
        if (vo.getAutDepartment() != null && vo.getAutDepartment().getId() != null) {
            dept = vo.getAutDepartment().getId().toString();
        }

        if ("".equals(search) && "".equals(dept)) {
            // 查询该所有人
            Page<AutUser> pageUser = new Page<AutUser>();
            Where<AutUser> autUserWhere = new Where<AutUser>();
            autUserWhere.eq("is_delete", 0);
            autUserWhere.eq("is_active", 1);
            autUserWhere.eq("is_account_expired", 0);
            autUserWhere.eq("is_account_locked", 0);
            List<AutUser> autUserList = autUserService.selectList(autUserWhere);
            // -----查询所有岗位
            Where<AutUserPosition> uPosWhere = new Where<AutUserPosition>();
            // uPosWhere.eq("user_id", id);
            uPosWhere.eq("is_delete", 0);
            uPosWhere.eq("is_active", 1);
            uPosWhere.setSqlSelect("user_id,position_id");
            List<AutUserPosition> autUserPosList = autUserPositionService.selectList(uPosWhere);
            // 查询所有岗位名称
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.eq("is_delete", 0);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            List<AutPosition> pList = autPositionService.selectList(pWhere);
            Map<String, String> upMap = new HashMap<String, String>();
            if (autUserPosList != null && autUserPosList.size() > 0 && pList != null && pList.size() > 0) {
                Map<String, String> pMap = new HashMap<String, String>();
                for (AutPosition autPosition : pList) {
                    pMap.put(autPosition.getId().toString(), autPosition.getPositionName());
                }
                for (AutUserPosition autPosition : autUserPosList) {
                    String pname = "";
                    if (upMap.containsKey(autPosition.getUserId().toString())) {
                        pname = upMap.get(autPosition.getUserId().toString());
                        if (pMap.containsKey(autPosition.getPositionId().toString())) {
                            pname += pMap.get(autPosition.getPositionId().toString());
                        }

                    } else {
                        if (pMap.containsKey(autPosition.getPositionId().toString())) {
                            pname = pMap.get(autPosition.getPositionId().toString()) + ";";
                        }
                    }
                    upMap.put(autPosition.getUserId().toString(), pname);
                }
                autUserPosList.clear();
                pList.clear();
            }

            Where<AutUserPub> userPubWhere = new Where<AutUserPub>();
            userPubWhere.eq("is_delete", 0);
            List<AutUserPub> pubLis = autUserPubService.selectList(userPubWhere);
            Map<String, AutUserPub> pubMap = new HashMap<String, AutUserPub>();
            if (pubLis != null && pubLis.size() > 0) {
                for (AutUserPub autUserPub : pubLis) {
                    pubMap.put(autUserPub.getUserId().toString(), autUserPub);
                }
            }
            pubLis.clear();
            List<Long> useridlist = new ArrayList<Long>();
            if (autUserList != null) {
                for (AutUser autUser : autUserList) {
                    AutUserPubVO autUserpubVo = new AutUserPubVO();
                    String id = autUser.getId().toString();
                    useridlist.add(autUser.getId());
                    autUserpubVo.setAutUser(autUser);
                    if (pubMap.containsKey(autUser.getId().toString())) {
                        autUserpubVo.setAutUserPub(pubMap.get(autUser.getId().toString()));
                    } else {
                        AutUserPub tmppub = new AutUserPub();
                        autUserpubVo.setAutUserPub(tmppub);
                    }

                    AutPosition ap = new AutPosition();
                    // Boolean isContinue = false;
                    if (upMap.containsKey(autUser.getId().toString())) {
                        ap.setPositionName(upMap.get(autUser.getId().toString()));
                    }
                    autUserpubVo.setAutPosition(ap);
                    autUserPubVoList.add(autUserpubVo);
                }
                Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                tmpList.addAll(autUserPubVoList);
                if (rankList.size() > 0) {
                    for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                        String usrid = entry.getKey().toString();
                        int no = entry.getValue();
                        no = no - 1;
                        for (AutUserPubVO autUserPubVO : tmpList) {
                            String tmp = autUserPubVO.getAutUser().getId().toString();
                            if (tmp.equals(usrid)) {
                                autUserPubVoList.set(no, autUserPubVO);
                            }
                        }
                    }
                }
            }

            int tmpsize = size;
            Integer sum = tmpsize * (num - 1);
            if (autUserPubVoList != null) {
                if (sum < autUserPubVoList.size()) {
                    int strSum = 0;
                    if (num > 1) {
                        strSum = tmpsize * (num - 1);
                    }
                    sum = sum + tmpsize - 1;
                    List<AutUserPubVO> autUserPubVoLists = new ArrayList<AutUserPubVO>();
                    for (int i = strSum; i < autUserPubVoList.size(); i++) {
                        if (sum < i) {
                            break;
                        }
                        autUserPubVoLists.add(autUserPubVoList.get(i));
                    }
                    page.setRecords(autUserPubVoLists);
                    page.setSize(tmpsize);
                    page.setTotal(autUserPubVoList.size());
                    return page;
                } else {
                    page.setRecords(autUserPubVoList);
                    page.setSize(tmpsize);
                    page.setTotal(autUserPubVoList.size());
                }
            } else {
                page.setRecords(autUserPubVoList);
                page.setSize(Integer.valueOf(size));
                page.setTotal(0);
            }
            return page;
        }
        if ("".equals(search)) {
            AutDepartment autDepartment = autDepartmentService.selectById(dept);
            Where<AutDepartmentUser> autWhere = new Where<AutDepartmentUser>();
            autWhere.eq("is_active", 1);
            autWhere.eq("is_delete", 0);
            autWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            autWhere.orderBy("department_user_rank", true);
            autWhere.setSqlSelect("user_id");
            List<AutDepartmentUser> pUserList = autDepartmentUserService.selectList(autWhere);
            if (pUserList != null && pUserList.size() > 0) {
                String userId = "";
                for (AutDepartmentUser autDepartmentUser : pUserList) {
                    if (userId.indexOf(autDepartmentUser.getUserId().toString()) > -1) {
                        continue;
                    } else {
                        userId += autDepartmentUser.getUserId() + ",";
                    }
                }
                // String[] userIds = userId.split(",");
                Where<AutUser> autUserWhere = new Where<AutUser>();
                autUserWhere.eq("is_delete", 0);
                autUserWhere.eq("is_active", 1);
                autUserWhere.in("id", userId);
                autUserWhere.eq("is_account_expired", 0);
                autUserWhere.eq("is_account_locked", 0);
                List<AutUser> autUserList = autUserService.selectList(autUserWhere);
                Where<AutUserPosition> uPosWhere = new Where<AutUserPosition>();
                // uPosWhere.eq("user_id", id);
                uPosWhere.eq("is_delete", 0);
                uPosWhere.in("user_id", userId);
                uPosWhere.eq("is_active", 1);
                uPosWhere.setSqlSelect("user_id,position_id");
                List<AutUserPosition> autUserPosList = autUserPositionService.selectList(uPosWhere);
                // 查询所有岗位名称
                Where<AutPosition> pWhere = new Where<AutPosition>();
                pWhere.eq("is_delete", 0);
                pWhere.eq("is_active", 1);
                pWhere.setSqlSelect("id,position_name");
                List<AutPosition> pList = autPositionService.selectList(pWhere);
                Map<String, String> upMap = new HashMap<String, String>();
                if (autUserPosList != null && autUserPosList.size() > 0 && pList != null && pList.size() > 0) {
                    Map<String, String> pMap = new HashMap<String, String>();
                    for (AutPosition autPosition : pList) {
                        pMap.put(autPosition.getId().toString(), autPosition.getPositionName());
                    }
                    for (AutUserPosition autPosition : autUserPosList) {
                        String pname = "";
                        if (upMap.containsKey(autPosition.getUserId().toString())) {
                            pname = upMap.get(autPosition.getUserId().toString());
                            if (pMap.containsKey(autPosition.getPositionId().toString())) {
                                pname += pMap.get(autPosition.getPositionId().toString());
                            }

                        } else {
                            if (pMap.containsKey(autPosition.getPositionId().toString())) {
                                pname = pMap.get(autPosition.getPositionId().toString()) + ";";
                            }
                        }
                        upMap.put(autPosition.getUserId().toString(), pname);
                    }
                    autUserPosList.clear();
                    pList.clear();
                }

                Where<AutUserPub> userPubWhere = new Where<AutUserPub>();
                userPubWhere.in("user_id", userId);
                userPubWhere.eq("is_delete", 0);
                List<AutUserPub> pubLis = autUserPubService.selectList(userPubWhere);
                Map<String, AutUserPub> pubMap = new HashMap<String, AutUserPub>();
                if (pubLis != null && pubLis.size() > 0) {
                    for (AutUserPub autUserPub : pubLis) {
                        pubMap.put(autUserPub.getUserId().toString(), autUserPub);
                    }
                }
                pubLis.clear();
                if (autUserList != null) {
                    List<Long> useridlist = new ArrayList<Long>();
                    /* for(int i = 0; i < userIds.length; i++) { */
                    for (AutUser autUser : autUserList) {
                        // if(userIds[i].equals(autUser.getId().toString())) {
                        AutUserPubVO autUserpubVo = new AutUserPubVO();
                        String id = autUser.getId().toString();
                        useridlist.add(autUser.getId());
                        autUserpubVo.setAutUser(autUser);
                        if (pubMap.containsKey(autUser.getId().toString())) {
                            autUserpubVo.setAutUserPub(pubMap.get(autUser.getId().toString()));
                        } else {
                            AutUserPub tmppub = new AutUserPub();
                            autUserpubVo.setAutUserPub(tmppub);
                        }

                        AutPosition ap = new AutPosition();
                        // Boolean isContinue = false;
                        if (upMap.containsKey(autUser.getId().toString())) {
                            ap.setPositionName(upMap.get(autUser.getId().toString()));
                        }
                        autUserpubVo.setAutPosition(ap);
                        autUserPubVoList.add(autUserpubVo);
                        // }
                        // }
                    }
                    Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                    List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                    tmpList.addAll(autUserPubVoList);
                    if (rankList.size() > 0) {
                        for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                            String usrid = entry.getKey().toString();
                            int no = entry.getValue();
                            no = no - 1;
                            for (AutUserPubVO autUserPubVO : tmpList) {
                                String tmp = autUserPubVO.getAutUser().getId().toString();
                                if (tmp.equals(usrid)) {
                                    autUserPubVoList.set(no, autUserPubVO);
                                }
                            }
                        }
                    }
                }
                int tmpsize = size;
                Integer sum = tmpsize * (num - 1);
                if (autUserPubVoList != null) {
                    if (sum < autUserPubVoList.size()) {
                        int strSum = 0;
                        if (num > 1) {
                            strSum = tmpsize * (num - 1);
                        }
                        sum = sum + tmpsize - 1;
                        List<AutUserPubVO> autUserPubVoLists = new ArrayList<AutUserPubVO>();
                        for (int i = strSum; i < autUserPubVoList.size(); i++) {
                            if (sum < i) {
                                break;
                            }
                            autUserPubVoLists.add(autUserPubVoList.get(i));
                        }
                        page.setRecords(autUserPubVoLists);
                        page.setSize(tmpsize);
                        page.setTotal(autUserPubVoList.size());
                        return page;
                    } else {
                        page.setRecords(autUserPubVoList);
                        page.setSize(tmpsize);
                        page.setTotal(autUserPubVoList.size());
                    }
                } else {
                    page.setRecords(autUserPubVoList);
                    page.setSize(Integer.valueOf(size));
                    page.setTotal(0);
                }
                return page;
            }

        }

        String depts = "";
        if (!"".equals(dept)) {
            AutDepartment autDepartment = autDepartmentService.selectById(dept);
            Where<AutDepartmentUser> autWhere = new Where<AutDepartmentUser>();
            autWhere.eq("is_active", 1);
            autWhere.eq("is_delete", 0);
            autWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            autWhere.orderBy("department_user_rank", true);
            autWhere.setSqlSelect("user_id");
            List<AutDepartmentUser> pUserList = autDepartmentUserService.selectList(autWhere);
            if (pUserList != null && pUserList.size() > 0) {
                String userId = "";
                for (AutDepartmentUser autDepartmentUser : pUserList) {
                    userId += autDepartmentUser.getUserId() + ",";
                }
                String[] userIds = userId.split(",");
                Where<AutPosition> pWhere = new Where<AutPosition>();
                pWhere.eq("is_delete", 0);
                // pWhere.like("position_name", search);
                pWhere.eq("is_active", 1);
                pWhere.setSqlSelect("id,position_name");
                List<AutPosition> pList = autPositionService.selectList(pWhere);
                Where<AutUser> autUserWhere = new Where<AutUser>();
                autUserWhere.eq("is_delete", 0);
                autUserWhere.eq("is_active", 1);
                autUserWhere.in("id", userId);
                autUserWhere.eq("is_account_expired", 0);
                autUserWhere.eq("is_account_locked", 0);
                List<AutUser> autUserList = autUserService.selectList(autUserWhere);
                Where<AutUserPosition> uPosWhere = new Where<AutUserPosition>();
                // uPosWhere.eq("user_id", id);
                uPosWhere.eq("is_delete", 0);
                uPosWhere.in("user_id", userId);
                uPosWhere.eq("is_active", 1);
                uPosWhere.setSqlSelect("user_id,position_id");
                List<AutUserPosition> autUserPosList = autUserPositionService.selectList(uPosWhere);
                // 查询所有岗位名称

                Map<String, String> upMap = new HashMap<String, String>();
                if (autUserPosList != null && autUserPosList.size() > 0 && pList != null && pList.size() > 0) {
                    Map<String, String> pMap = new HashMap<String, String>();
                    for (AutPosition autPosition : pList) {
                        pMap.put(autPosition.getId().toString(), autPosition.getPositionName());
                    }
                    for (AutUserPosition autPosition : autUserPosList) {
                        String pname = "";
                        if (upMap.containsKey(autPosition.getUserId().toString())) {
                            pname = upMap.get(autPosition.getUserId().toString());
                            if (pMap.containsKey(autPosition.getPositionId().toString())) {
                                pname += pMap.get(autPosition.getPositionId().toString());
                            }

                        } else {
                            if (pMap.containsKey(autPosition.getPositionId().toString())) {
                                pname = pMap.get(autPosition.getPositionId().toString()) + ";";
                            }
                        }
                        upMap.put(autPosition.getUserId().toString(), pname);
                    }
                    autUserPosList.clear();
                    pList.clear();
                }

                Where<AutUserPub> userPubWhere = new Where<AutUserPub>();
                userPubWhere.in("user_id", userId);
                userPubWhere.eq("is_delete", 0);
                List<AutUserPub> pubLis = autUserPubService.selectList(userPubWhere);
                Map<String, AutUserPub> pubMap = new HashMap<String, AutUserPub>();
                if (pubLis != null && pubLis.size() > 0) {
                    for (AutUserPub autUserPub : pubLis) {
                        pubMap.put(autUserPub.getUserId().toString(), autUserPub);
                    }
                }
                pubLis.clear();
                if (autUserList != null) {
                    for (int i = 0; i < userIds.length; i++) {
                        for (AutUser autUser : autUserList) {
                            if (userIds[i].equals(autUser.getId().toString())) {
                                AutUserPubVO autUserpubVo = new AutUserPubVO();
                                String id = autUser.getId().toString();
                                autUserpubVo.setAutUser(autUser);
                                if (pubMap.containsKey(autUser.getId().toString())) {
                                    autUserpubVo.setAutUserPub(pubMap.get(autUser.getId().toString()));
                                } else {
                                    AutUserPub tmppub = new AutUserPub();
                                    autUserpubVo.setAutUserPub(tmppub);
                                }
                                AutPosition ap = new AutPosition();
                                if (upMap.containsKey(autUser.getId().toString())) {
                                    ap.setPositionName(upMap.get(autUser.getId().toString()));
                                }
                                autUserpubVo.setAutPosition(ap);
                                autUserPubVoList.add(autUserpubVo);
                            }
                        }
                    }
                }
                if (autUserPubVoList != null && autUserPubVoList.size() > 0) {
                    List<Long> useridlist = new ArrayList<Long>();
                    for (int i = 0; i < autUserPubVoList.size(); i++) {
                        AutUserPubVO tmpvo = autUserPubVoList.get(i);
                        boolean istrue = false;
                        String name = tmpvo.getAutUser().getFullName();
                        AutPosition pos = tmpvo.getAutPosition();
                        if (name.indexOf(search) > -1) {
                            useridlist.add(tmpvo.getAutUser().getId());
                            continue;
                        }
                        if (pos.getPositionName() != null) {
                            String pname = pos.getPositionName();
                            if (pname.indexOf(search) > -1) {
                                useridlist.add(tmpvo.getAutUser().getId());
                                istrue = true;
                                continue;
                            }
                        }
                        autUserPubVoList.remove(i);
                        i--;
                    }
                    Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                    List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                    tmpList.addAll(autUserPubVoList);
                    if (rankList.size() > 0) {
                        for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                            String usrid = entry.getKey().toString();
                            int no = entry.getValue();
                            no = no - 1;
                            for (AutUserPubVO autUserPubVO : tmpList) {
                                String tmp = autUserPubVO.getAutUser().getId().toString();
                                if (tmp.equals(usrid)) {
                                    autUserPubVoList.set(no, autUserPubVO);
                                }
                            }
                        }
                    }
                }
                int tmpsize = size;
                Integer sum = tmpsize * (num - 1);
                if (autUserPubVoList != null) {
                    if (sum < autUserPubVoList.size()) {
                        int strSum = 0;
                        if (num > 1) {
                            strSum = tmpsize * (num - 1);
                        }
                        sum = sum + tmpsize - 1;
                        List<AutUserPubVO> autUserPubVoLists = new ArrayList<AutUserPubVO>();
                        for (int i = strSum; i < autUserPubVoList.size(); i++) {
                            if (sum < i) {
                                break;
                            }
                            autUserPubVoLists.add(autUserPubVoList.get(i));
                        }
                        page.setRecords(autUserPubVoLists);
                        page.setSize(tmpsize);
                        page.setTotal(autUserPubVoList.size());
                        return page;
                    } else {
                        page.setRecords(autUserPubVoList);
                        page.setSize(tmpsize);
                        page.setTotal(autUserPubVoList.size());
                    }
                } else {
                    page.setRecords(autUserPubVoList);
                    page.setSize(Integer.valueOf(size));
                    page.setTotal(0);
                }
            }
        } else {
            Page<AutUser> pageUser = new Page<AutUser>();
            Where<AutUser> autUserWhere = new Where<AutUser>();
            autUserWhere.eq("is_delete", 0);
            autUserWhere.eq("is_active", 1);
            autUserWhere.eq("is_account_expired", 0);
            autUserWhere.eq("is_account_locked", 0);
            List<AutUser> autUserList = autUserService.selectList(autUserWhere);
            // -----查询所有岗位
            Where<AutUserPosition> uPosWhere = new Where<AutUserPosition>();
            // uPosWhere.eq("user_id", id);
            uPosWhere.eq("is_delete", 0);
            uPosWhere.eq("is_active", 1);
            uPosWhere.setSqlSelect("user_id,position_id");
            List<AutUserPosition> autUserPosList = autUserPositionService.selectList(uPosWhere);
            // 查询所有岗位名称
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.eq("is_delete", 0);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            List<AutPosition> pList = autPositionService.selectList(pWhere);
            Map<String, String> upMap = new HashMap<String, String>();
            if (autUserPosList != null && autUserPosList.size() > 0 && pList != null && pList.size() > 0) {
                Map<String, String> pMap = new HashMap<String, String>();
                for (AutPosition autPosition : pList) {
                    pMap.put(autPosition.getId().toString(), autPosition.getPositionName());
                }
                for (AutUserPosition autPosition : autUserPosList) {
                    String pname = "";
                    if (upMap.containsKey(autPosition.getUserId().toString())) {
                        pname = upMap.get(autPosition.getUserId().toString());
                        if (pMap.containsKey(autPosition.getPositionId().toString())) {
                            pname += pMap.get(autPosition.getPositionId().toString());
                        }

                    } else {
                        if (pMap.containsKey(autPosition.getPositionId().toString())) {
                            pname = pMap.get(autPosition.getPositionId().toString()) + ";";
                        }
                    }
                    upMap.put(autPosition.getUserId().toString(), pname);
                }
                autUserPosList.clear();
                pList.clear();
            }

            Where<AutUserPub> userPubWhere = new Where<AutUserPub>();
            userPubWhere.eq("is_delete", 0);
            List<AutUserPub> pubLis = autUserPubService.selectList(userPubWhere);
            Map<String, AutUserPub> pubMap = new HashMap<String, AutUserPub>();
            if (pubLis != null && pubLis.size() > 0) {
                for (AutUserPub autUserPub : pubLis) {
                    pubMap.put(autUserPub.getUserId().toString(), autUserPub);
                }
            }
            pubLis.clear();
            if (autUserList != null) {
                for (AutUser autUser : autUserList) {
                    AutUserPubVO autUserpubVo = new AutUserPubVO();
                    String id = autUser.getId().toString();
                    autUserpubVo.setAutUser(autUser);
                    if (pubMap.containsKey(autUser.getId().toString())) {
                        autUserpubVo.setAutUserPub(pubMap.get(autUser.getId().toString()));
                    } else {
                        AutUserPub tmppub = new AutUserPub();
                        autUserpubVo.setAutUserPub(tmppub);
                    }

                    AutPosition ap = new AutPosition();
                    // Boolean isContinue = false;
                    if (upMap.containsKey(autUser.getId().toString())) {
                        ap.setPositionName(upMap.get(autUser.getId().toString()));
                    }
                    autUserpubVo.setAutPosition(ap);
                    autUserPubVoList.add(autUserpubVo);
                }
            }
            if (autUserPubVoList != null && autUserPubVoList.size() > 0) {
                List<Long> useridlist = new ArrayList<Long>();
                for (int i = 0; i < autUserPubVoList.size(); i++) {
                    AutUserPubVO tmpvo = autUserPubVoList.get(i);
                    boolean istrue = false;
                    String name = tmpvo.getAutUser().getFullName();
                    AutPosition pos = tmpvo.getAutPosition();
                    if (name.indexOf(search) > -1) {
                        useridlist.add(tmpvo.getAutUser().getId());
                        continue;
                    }
                    if (pos.getPositionName() != null) {
                        String pname = pos.getPositionName();
                        if (pname.indexOf(search) > -1) {
                            istrue = true;
                            continue;
                        }
                    }
                    autUserPubVoList.remove(i);
                    i--;
                }
                Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                tmpList.addAll(autUserPubVoList);
                if (rankList.size() > 0) {
                    for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                        String usrid = entry.getKey().toString();
                        int no = entry.getValue();
                        no = no - 1;
                        for (AutUserPubVO autUserPubVO : tmpList) {
                            String tmp = autUserPubVO.getAutUser().getId().toString();
                            if (tmp.equals(usrid)) {
                                autUserPubVoList.set(no, autUserPubVO);
                            }
                        }
                    }
                }
            }
            int tmpsize = size;
            Integer sum = tmpsize * (num - 1);
            if (autUserPubVoList != null) {
                if (sum < autUserPubVoList.size()) {
                    int strSum = 0;
                    if (num > 1) {
                        strSum = tmpsize * (num - 1);
                    }
                    sum = sum + tmpsize - 1;
                    List<AutUserPubVO> autUserPubVoLists = new ArrayList<AutUserPubVO>();
                    for (int i = strSum; i < autUserPubVoList.size(); i++) {
                        if (sum < i) {
                            break;
                        }
                        autUserPubVoLists.add(autUserPubVoList.get(i));
                    }
                    page.setRecords(autUserPubVoLists);
                    page.setSize(tmpsize);
                    page.setTotal(autUserPubVoList.size());
                    return page;
                } else {
                    page.setRecords(autUserPubVoList);
                    page.setSize(tmpsize);
                    page.setTotal(autUserPubVoList.size());
                }
            } else {
                page.setRecords(autUserPubVoList);
                page.setSize(Integer.valueOf(size));
                page.setTotal(0);
            }
        }

        return page;
    }

    public int setPageInfo(int pageSize, int rowCount) {

        int iPageSize = pageSize;
        int iRowCount = rowCount;
        int iPageCount = 0;
        iPageCount = (iRowCount + iPageSize - 1) / iPageSize;
        return iPageCount;
    }

    @Override
    public RetMsg updateImg(AutUserPubVO autUserPubVO, Long customUserId) throws Exception {

        RetMsg retMsg = new RetMsg();
        Where<AutUserPub> pubWhere = new Where<AutUserPub>();
        pubWhere.where("user_id={0}", customUserId);
        AutUserPub autUserPub = this.selectOne(pubWhere);
        //删除资源表原文件记录
        /*if (autUserPubVO != null) {
            Where<ResResource> where = new Where<ResResource>();
            where.eq("biz_id",autUserPub.getId());
            ResResource resResource = resResourceService.selectOne(where);
            if (null != resResource) {
                resResourceService.delete(resResource);
            }
        }*/
        if (autUserPub != null && autUserPubVO != null) {
            autUserPub.setUserIcon(autUserPubVO.getPath());
            Boolean isUp = this.updateById(autUserPub);
            //更新资源表
            ResResource resource = resResourceService.selectById(autUserPubVO.getResourceId());
            if (null != resource) {
                resource.setBizId(autUserPub.getId());
                resource.setFileDesc("个人信息头像上传");
                resResourceService.updateById(resource);
            }
            if (isUp) {
                retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
                retMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
                return retMsg;
            } else {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage(WebConstant.RETMSG_OPERATION_FAIL);
                return retMsg;
            }
        } else {
            AutUserPub userPub = new AutUserPub();
            userPub.setUserId(customUserId);
          //更新资源表
            ResResource resource = resResourceService.selectById(autUserPubVO.getResourceId());
            if (null != resource) {
                resource.setBizId(autUserPub.getId());
                resource.setFileDesc("个人信息头像上传");
                resResourceService.updateById(resource);
            }
            if (autUserPubVO != null && autUserPubVO.getPath() != null) {
                userPub.setUserIcon(autUserPubVO.getPath());
            }
            if (StringUtils.checkValNull(userPub.getMaxMailSize())) {
                // 设置默认邮件大小1G
                userPub.setMaxMailSize(1073741824);
            }
            Boolean isUp = autUserPubService.insert(userPub);
            if (isUp) {
                retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
                retMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
                return retMsg;
            } else {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage(WebConstant.RETMSG_OPERATION_FAIL);
                return retMsg;
            }
        }

    }

    

    @SuppressWarnings("unused")
    @Override
    @Transactional
    public List<AutUserPubVO> searchquery(String queryType, String search, Long customUserId) throws Exception { // 本部门,岗位上一级
        List<AutUserPubVO> autUserPubVoList = new ArrayList<AutUserPubVO>();
        if ("1".equals(queryType)) {
            // 获得登录用户id，查询用户-部门表
            Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
            where.setSqlSelect("id,user_id,dept_id");
            where.eq("user_id", customUserId);
            where.orderBy("department_user_rank", true);
            List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(where);
            // 获得所在部门
            List<AutDepartment> deptList = new ArrayList<AutDepartment>();
            String deptIds = "";
            if (null != deptUserList && !deptUserList.isEmpty()) {
                for (AutDepartmentUser deptUser : deptUserList) {
                    deptIds += deptUser.getDeptId() + ",";
                }
            }
            if (deptIds.length() > 0) {
                Where<AutDepartmentUser> udwhere = new Where<AutDepartmentUser>();
                udwhere.setSqlSelect("id,user_id,dept_id");
                udwhere.in("dept_id", deptIds);
                udwhere.orderBy("department_user_rank", true);
                List<AutDepartmentUser> deptUserLists = autDepartmentUserService.selectList(udwhere);
                if (deptUserLists != null && deptUserLists.size() > 0) {
                    deptIds = "";
                    for (AutDepartmentUser autDepartmentUser : deptUserLists) {
                        deptIds += autDepartmentUser.getUserId() + ",";
                    }
                    Where<AutUser> uwhere = new Where<AutUser>();
                    uwhere.setSqlSelect("id,user_id,dept_id");
                    uwhere.in("id", deptIds);
                    uwhere.setSqlSelect("id,full_name");
                    List<AutUser> userLists = autUserService.selectList(uwhere);
                    Where<AutUserPub> upwhere = new Where<AutUserPub>();
                    upwhere.in("user_id", deptIds);
                    upwhere.setSqlSelect("user_id,phone_number");
                    List<AutUserPub> userPubLists = autUserPubService.selectList(upwhere);
                    Map<String, AutUserPub> userpMap = new HashMap<String, AutUserPub>();
                    if (userPubLists != null && userPubLists.size() > 0) {
                        for (AutUserPub autUserPub : userPubLists) {
                            userpMap.put(autUserPub.getUserId().toString(), autUserPub);
                        }
                    }
                    if (userLists != null && userLists.size() > 0) {
                        List<Long> useridlist = new ArrayList<Long>();
                        for (AutUser autUser : userLists) {
                            AutUserPubVO vo = new AutUserPubVO();

                            vo.setAutUser(autUser);
                            if (userpMap.containsKey(autUser.getId().toString())) {
                                vo.setAutUserPub(userpMap.get(autUser.getId().toString()));
                            }
                            if (search != null && !"".equals(search)) {
                                if (autUser.getFullName().indexOf(search) > -1) {
                                    useridlist.add(autUser.getId());
                                } else {
                                    continue;
                                }
                            } else {
                                useridlist.add(autUser.getId());
                            }
                            autUserPubVoList.add(vo);
                        }
                        Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                        List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                        tmpList.addAll(autUserPubVoList);
                        if (rankList.size() > 0) {
                            for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                                String usrid = entry.getKey().toString();
                                int no = entry.getValue();
                                no = no - 1;
                                for (AutUserPubVO autUserPubVO : tmpList) {
                                    String tmp = autUserPubVO.getAutUser().getId().toString();
                                    if (tmp.equals(usrid)) {
                                        autUserPubVoList.set(no, autUserPubVO);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        if ("2".equals(queryType)) {
            Where<AutDepartmentUser> w1 = new Where<AutDepartmentUser>();
            w1.setSqlSelect("id,user_id,dept_id,dept_code");
            w1.eq("user_id", customUserId);
            w1.orderBy("department_user_rank", true);
            List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(w1);
            List<AutDepartment> deptList = new ArrayList<AutDepartment>();
            if (null != deptUserList && !deptUserList.isEmpty()) {
                String deptCodes = "";
                List<AutDepartment> dlists = new ArrayList<AutDepartment>();
                for (AutDepartmentUser deptUser : deptUserList) {
                    String deptCode = deptUser.getDeptCode();
                    if (deptCode.length() > 6) {
                        deptCode = deptCode.substring(0, 6);
                    }
                    Where<AutDepartment> w5 = new Where<AutDepartment>();
                    w5.where("dept_code like {0}", deptCode + "%");
                    List<AutDepartment> dlist = autDepartmentService.selectList(w5);
                    if (dlist != null && dlist.size() > 0) {
                        dlists.addAll(dlist);
                    }
                }
                String deptIds = "";
                if (dlists != null && dlists.size() > 0) {
                    for (AutDepartment autDepartment : dlists) {
                        deptIds += autDepartment.getId() + ",";
                    }
                }

                if (deptIds.length() > 0) {
                    Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
                    w2.setSqlSelect("id,user_id,dept_id");
                    w2.in("dept_id", deptIds);
                    w2.orderBy("department_user_rank", true);
                    List<AutDepartmentUser> deptUList = autDepartmentUserService.selectList(w2);

                    String ids = "";
                    if (deptUList != null && deptUList.size() > 0) {
                        for (AutDepartmentUser autDepartmentUser : deptUList) {
                            ids += autDepartmentUser.getUserId() + ",";
                        }
                        if (ids.length() > 0) {
                            Where<AutUser> uwhere = new Where<AutUser>();
                            uwhere.setSqlSelect("id,user_id,dept_id");
                            uwhere.in("id", ids);
                            uwhere.setSqlSelect("id,full_name");
                            List<AutUser> userLists = autUserService.selectList(uwhere);
                            Where<AutUserPub> upwhere = new Where<AutUserPub>();
                            upwhere.in("user_id", ids);
                            upwhere.setSqlSelect("user_id,phone_number");
                            List<AutUserPub> userPubLists = autUserPubService.selectList(upwhere);
                            Map<String, AutUserPub> userpMap = new HashMap<String, AutUserPub>();
                            if (userPubLists != null && userPubLists.size() > 0) {
                                for (AutUserPub autUserPub : userPubLists) {
                                    userpMap.put(autUserPub.getUserId().toString(), autUserPub);
                                }
                            }
                            if (userLists != null && userLists.size() > 0) {
                                List<Long> useridlist = new ArrayList<Long>();
                                for (AutUser autUser : userLists) {
                                    AutUserPubVO vo = new AutUserPubVO();

                                    vo.setAutUser(autUser);
                                    if (userpMap.containsKey(autUser.getId().toString())) {
                                        vo.setAutUserPub(userpMap.get(autUser.getId().toString()));
                                    }
                                    if (search != null && !"".equals(search)) {
                                        if (autUser.getFullName().indexOf(search) > -1) {
                                            useridlist.add(autUser.getId());
                                        } else {
                                            continue;
                                        }
                                    } else {
                                        useridlist.add(autUser.getId());
                                    }
                                    autUserPubVoList.add(vo);
                                }
                                Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                                List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                                tmpList.addAll(autUserPubVoList);
                                if (rankList.size() > 0) {
                                    for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                                        String usrid = entry.getKey().toString();
                                        int no = entry.getValue();
                                        no = no - 1;
                                        for (AutUserPubVO autUserPubVO : tmpList) {
                                            String tmp = autUserPubVO.getAutUser().getId().toString();
                                            if (tmp.equals(usrid)) {
                                                autUserPubVoList.set(no, autUserPubVO);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                }
            }

        }
        if ("3".equals(queryType)) {

            Where<AutUser> userwhere = new Where<AutUser>();
            if (search != null && !"".equals(search)) {
                userwhere.like("full_name", search);
            }
            userwhere.setSqlSelect("id,full_name");
            List<AutUser> userList = autUserService.selectList(userwhere);
            Where<AutUserPub> userpwhere = new Where<AutUserPub>();
            userpwhere.setSqlSelect("user_id,phone_number");
            List<AutUserPub> userpList = autUserPubService.selectList(userpwhere);
            Map<String, AutUserPub> userpMap = new HashMap<String, AutUserPub>();
            if (userpList != null && userpList.size() > 0) {
                for (AutUserPub autUserPub : userpList) {
                    userpMap.put(autUserPub.getUserId().toString(), autUserPub);
                }
            }
            if (userpList != null && userpList.size() > 0) {
                List<Long> useridlist = new ArrayList<Long>();
                for (AutUser autUser : userList) {
                    AutUserPubVO vo = new AutUserPubVO();
                    useridlist.add(autUser.getId());
                    vo.setAutUser(autUser);
                    if (userpMap.containsKey(autUser.getId().toString())) {
                        vo.setAutUserPub(userpMap.get(autUser.getId().toString()));
                    }
                    autUserPubVoList.add(vo);
                }
                Map<Long, Integer> rankList = autUserService.getUserRankList(useridlist, "dept");
                List<AutUserPubVO> tmpList = new ArrayList<AutUserPubVO>();
                tmpList.addAll(autUserPubVoList);
                if (rankList.size() > 0) {
                    for (Map.Entry<Long, Integer> entry : rankList.entrySet()) {
                        String usrid = entry.getKey().toString();
                        int no = entry.getValue();
                        no = no - 1;
                        for (AutUserPubVO autUserPubVO : tmpList) {
                            String tmp = autUserPubVO.getAutUser().getId().toString();
                            if (tmp.equals(usrid)) {
                                autUserPubVoList.set(no, autUserPubVO);
                            }
                        }
                    }
                }
            }

        }
        return autUserPubVoList;

    }

    @Override
    public RetMsg appUpdatePwd(String oldPwd, String newPwd, String newPwds, String userId) throws Exception {

        AutUser autUser = autUserService.selectById(Long.valueOf(userId));
        RetMsg retMsg = new RetMsg();
        // 校验原密码是否正确
        if (customPasswordEncoder.matches(oldPwd, autUser.getUserPwd())) {
            // 比对前后输入的2次新密码是否相同
            if (newPwd == newPwds || newPwd.equals(newPwds)) {
                autUser.setUserPwd(customPasswordEncoder.encode(newPwd));
                autUser.setLastUpdateUserId(autUser.getId());
                autUser.setLastUpdateUserName(autUser.getUserName());;
                autUserService.updateById(autUser);
                retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
                retMsg.setMessage("操作成功");
            } else {
                retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
                retMsg.setMessage("前后两次新密码不一致");
            }
        } else {
            retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
            retMsg.setMessage("原密码不正确");
        }
        return retMsg;
    }

    @Override
    public RetMsg appUpdatePub(AutUserPubVO vo, String userId) throws Exception {
        // TODO Auto-generated method stub
        AutUser autUser = autUserService.selectById(Long.valueOf(userId));
        RetMsg retMsg = new RetMsg();
        AutUser tmpuser = vo.getAutUser();
        AutUserPub tmpPub = vo.getAutUserPub();
        if (tmpuser != null) {
            if (tmpuser.getUserEmail() != null && !"".equals(tmpuser.getUserEmail())) {
                autUser.setUserEmail(tmpuser.getUserEmail());
                autUser.setLastUpdateUserId(autUser.getId());
                autUser.setLastUpdateUserName(autUser.getUserName());
                autUserService.updateById(autUser);
            }
        }
        if (tmpPub != null) {
            Where<AutUserPub> pubWhere = new Where<AutUserPub>();
            pubWhere.eq("user_id", userId);
            AutUserPub pub = autUserPubService.selectOne(pubWhere);
            if (pub != null) {
                boolean isChange = false;
                if (tmpPub.getFaxNumber() != null && !"".equals(tmpPub.getFaxNumber())) {
                    pub.setFaxNumber(tmpPub.getFaxNumber());
                    isChange = true;
                }
                if (tmpPub.getPhoneNumber() != null && !"".equals(tmpPub.getPhoneNumber())) {
                    pub.setPhoneNumber(tmpPub.getPhoneNumber());
                    isChange = true;
                }
                if (tmpPub.getLawNumber() != null && !"".equals(tmpPub.getLawNumber())) {
                    pub.setLawNumber(tmpPub.getLawNumber());
                    isChange = true;
                }
                if (tmpPub.getTelNumber() != null && !"".equals(tmpPub.getTelNumber())) {
                    pub.setTelNumber(tmpPub.getTelNumber());
                    isChange = true;
                }
                if (tmpPub.getChestCardNumber() != null && !"".equals(tmpPub.getChestCardNumber())) {
                    pub.setChestCardNumber(tmpPub.getChestCardNumber());
                    isChange = true;
                }
                if (isChange) {
                    pub.setLastUpdateUserId(autUser.getId());
                    pub.setLastUpdateUserName(autUser.getUserName());
                    this.updateById(pub);
                }
            } else {
                pub = new AutUserPub();
                boolean isChange = false;
                if (tmpPub.getFaxNumber() != null && !"".equals(tmpPub.getFaxNumber())) {
                    pub.setFaxNumber(tmpPub.getFaxNumber());
                    isChange = true;
                }
                if (tmpPub.getPhoneNumber() != null && !"".equals(tmpPub.getPhoneNumber())) {
                    pub.setPhoneNumber(tmpPub.getPhoneNumber());
                    isChange = true;
                }
                if (tmpPub.getLawNumber() != null && !"".equals(tmpPub.getLawNumber())) {
                    pub.setLawNumber(tmpPub.getLawNumber());
                    isChange = true;
                }
                if (tmpPub.getTelNumber() != null && !"".equals(tmpPub.getTelNumber())) {
                    pub.setTelNumber(tmpPub.getTelNumber());
                    isChange = true;
                }
                if (tmpPub.getChestCardNumber() != null && !"".equals(tmpPub.getChestCardNumber())) {
                    pub.setChestCardNumber(tmpPub.getChestCardNumber());
                    isChange = true;
                }
                if (isChange) {
                    pub.setLastUpdateUserId(autUser.getId());
                    pub.setLastUpdateUserName(autUser.getUserName());
                    pub.setCreateUserId(autUser.getId());
                    pub.setCreateUserName(autUser.getUserName());
                    pub.setUserId(autUser.getId());
                    Where<AutDepartmentUser> dUWhere = new Where<AutDepartmentUser>();
                    dUWhere.eq("is_leader", 1);
                    dUWhere.eq("is_active", 1);
                    List<AutDepartmentUser> list = autDepartmentUserService.selectList(dUWhere);
                    if (list != null && list.size() > 0) {
                        SysParameter sysParameter = sysParameterService.selectBykey("leader_mail_space");
                        pub.setMaxMailSize(Integer.parseInt(sysParameter.getParamValue()));
                    } else {
                        SysParameter sysParameter = sysParameterService.selectBykey("personal_mail_space");
                        pub.setMaxMailSize(Integer.parseInt(sysParameter.getParamValue()));
                    }
                    // autUserPub.setMaxMailSize(Integer.parseInt(sysParameter.getParamValue()));

                    this.insert(pub);
                }
            }
        }

        return retMsg;
    }

}
