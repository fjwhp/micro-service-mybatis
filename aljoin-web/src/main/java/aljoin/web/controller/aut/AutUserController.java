package aljoin.web.controller.aut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.IdentityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.iservice.ActActivitiService;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutRoleDO;
import aljoin.aut.dao.object.AutUserAndPubVo;
import aljoin.aut.dao.object.AutUserPubEditVO;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.dao.object.AutUserVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomPasswordEncoder;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.SsoData;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import aljoin.web.service.aut.AutUserWebService;

/**
 * 
 * 用户表(控制器).
 * 
 * @author：zhongjy
 * 
 *                 @date： 2017-05-21
 */
@Controller
@RequestMapping(value = "/aut/autUser")
public class AutUserController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutUserController.class);
    @Resource
    private AutUserService autUserService;
    @Resource
    private CustomPasswordEncoder customPasswordEncoder;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutRoleService autRoleService;
    @Resource
    private ActActivitiService activitiService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private IdentityService identityService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private AttSignInOutService attSignInOutService;
    @Resource
    private AutUserWebService autUserWebService;
    @Resource
    private AutUserRankService autUserRankService;

    /**
     * 
     * 用户表(页面).
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:29:41
     */
    @RequestMapping("/autUserPage")
    public String index(HttpServletRequest request) {
        return "aut/autUserPage";
    }

    /**
     * 
     * 用户表(分页列表).
     *
     * @return：Page<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:30:09
     */
    @RequestMapping("/voList")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[分页列表]")
    public Page<AutUserVO> voList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUser obj) {
        Page<AutUserVO> page = null;
        try {
            page = autUserService.voList(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 用户表关联用户排序表排序(分页列表).
     *
     * @return：Page<AutUser>
     *
     * @author：huanghz
     *
     * @date：2017年12月14日 下午14:30:09
     */
    @RequestMapping("/rankList")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[排序管理]-[搜索分页]")
    public Page<AutUserVO> rankList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUser obj) {
        Page<AutUserVO> page = null;
        try {
            page = autUserService.rankList(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    @RequestMapping("/list")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[搜索]")
    public Page<AutUser> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUser obj) {
        Page<AutUser> page = null;
        try {
            page = autUserService.list(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 用户表(新增).
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:30:49
     */
    @RequestMapping("/add")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[新增]")
    public RetMsg add(HttpServletRequest requesta, HttpServletResponse responsea, AutUserAndPubVo obj) {
        RetMsg retMsg = new RetMsg();
        CustomUser customUser = getCustomDetail();
        try {
            retMsg = autUserWebService.addUser(obj, customUser.getUserId());
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 用户表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:44:27
     */
    @RequestMapping("/delete")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[删除]")
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutUser autUser) {
        RetMsg retMsg = new RetMsg();
        try {
            autUserService.delete(autUser);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 用户表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:42:36
     */
    @RequestMapping("/update")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[修改]")
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutUserPubEditVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = autUserService.updateUser(obj);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("[权限管理]-[用户管理]-[修改]", e);
        }
        return retMsg;
    }

    /**
     * 
     * 用户表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:34:54
     */
    /*
     * @RequestMapping("/getById")
     * 
     * @ResponseBody
     * 
     * @FuncObj(desc = "[权限管理]-[用户管理]-[详情]") public AutUser getById(HttpServletRequest request,
     * HttpServletResponse response, AutUser obj) { AutUser autUser =
     * autUserService.selectById(obj.getId()); // 密码MD5后传递到前台
     * autUser.setUserPwd(EncryptUtil.encryptMD5(autUser.getUserPwd())); return autUser; }
     */
    @RequestMapping("/getById")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[详情]")
    public AutUserPubVO getById(HttpServletRequest request, HttpServletResponse response, AutUser obj) {
        AutUserPubVO vo = new AutUserPubVO();
        try {
            vo = autUserService.getById(obj.getId());
            if (vo != null && vo.getAutUserPub() != null) {
                AutUserPub autUserPub = vo.getAutUserPub();
                vo.setAutUserPub(autUserPub);
            } else {
                AutUserPub autUserPub = new AutUserPub();
                autUserPub.setPhoneNumber("");
                autUserPub.setTelNumber("");
                vo.setAutUserPub(autUserPub);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        /*
         * // 密码MD5后传递到前台 autUser.setUserPwd(EncryptUtil.encryptMD5(autUser.getUserPwd()));
         */
        return vo;
    }

    /**
     * 
     * 角色表.
     *
     * @return：Page<AutMenu>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    @RequestMapping("/roleList")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[分配角色(查看)]")
    public Page<AutRoleDO> roleList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean) {
        Page<AutRoleDO> page = new Page<AutRoleDO>();
        long userId = Long.parseLong(request.getParameter("userId"));
        try {
            Where<AutUserRole> w1 = new Where<AutUserRole>();
            w1.eq("user_id", userId).eq("is_active", 1);
            w1.setSqlSelect("role_id");
            List<AutUserRole> autUserRoleList = autUserRoleService.selectList(w1);
            Set<Long> roleIdSet = new HashSet<Long>();
            for (AutUserRole autUserRole : autUserRoleList) {
                roleIdSet.add(autUserRole.getRoleId());
            }

            Page<AutRole> rolePage = autRoleService.list(pageBean, new AutRole());
            List<AutRole> roleList = rolePage.getRecords();

            page.setCurrent(rolePage.getCurrent());
            page.setSize(rolePage.getSize());
            page.setTotal(rolePage.getTotal());
            List<AutRoleDO> voList = new ArrayList<AutRoleDO>();
            for (AutRole autRole : roleList) {
                AutRoleDO roleDO = new AutRoleDO();
                if (roleIdSet.contains(autRole.getId())) {
                    roleDO.setIsCheck(1);
                } else {
                    roleDO.setIsCheck(0);
                }
                roleDO.setAutRole(autRole);
                voList.add(roleDO);
            }
            page.setRecords(voList);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 用户分配角色
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    @RequestMapping("/setAuth")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[分配角色(操作)]")
    public RetMsg setAuth(HttpServletRequest request, HttpServletResponse response) {
        RetMsg retMsg = new RetMsg();

        int isActive = Integer.parseInt(request.getParameter("isCheck"));
        long id = Long.parseLong(request.getParameter("id"));
        long userId = Long.parseLong(request.getParameter("userId"));
        // 菜单权限
        try {
            retMsg = autUserService.setAuth(isActive, id, userId);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 获取用户列表（id和昵称）.
     *
     * @return：AutUser
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:34:54
     */
    @RequestMapping("/getUserList")
    @ResponseBody
    public List<AutUser> getUserList(HttpServletRequest request, HttpServletResponse response) {
        List<AutUser> list = autUserService.getUserList();
        return list;
    }

    /**
     * 
     * 点击某部门，查询未分配进该部门的用户 查询所有用户，过滤掉根据部门id查询“部门-用户表”到的用户
     *
     * @return：Page<AutUser>
     *
     * @author：laijy
     *
     * @date：2017年8月28日 上午8:43:24
     */
    @RequestMapping("/listNoDepartmentUser")
    @ResponseBody
    public Page<AutUser> listNoDepartmentUser(HttpServletRequest request, HttpServletResponse response,
        PageBean pageBean, AutUser obj, Long deptId) {
        Page<AutUser> page = null;
        try {

            List<AutDepartmentUser> userIdList = autDepartmentUserService.getUserIdByDepartmentId(deptId);

            List<Long> list = new ArrayList<Long>();
            for (int i = 0; i < userIdList.size(); i++) {
                list.add(userIdList.get(i).getUserId());
            }

            page = autUserService.listNoDepartmentUser(pageBean, list);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 只显示isActive=1的用户
     *
     * @return：Page<AutUser>
     *
     * @author：laijy
     *
     * @date：2017年9月30日 下午2:17:37
     */
    @RequestMapping("/listIsAvtive")
    @ResponseBody
    @FuncObj(desc = "[权限管理]-[用户管理]-[搜索]")
    public Page<AutUser> listIsAvtive(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUser obj) {
        Page<AutUser> page = null;
        try {
            page = autUserService.listIsAvtive(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    @RequestMapping("/testThirdSsoDataSynInvoke")
    @ResponseBody
    @FuncObj(desc = "[第三方提供的单点登录数据同步接口，供oa调用]")
    public RetMsg testThirdSsoDataSynInvoke(HttpServletRequest request, HttpServletResponse response, SsoData ssoData) {
        RetMsg retMsg = new RetMsg();
        try {
            if (ssoData != null) {
                System.out.println("第三方接口获取OA的操作类型：" + ssoData.getOperationType());
                System.out.println("第三方接口获取OA的帐户：" + ssoData.getLoginAccount());
                System.out.println("第三方接口获取OA的密码：" + ssoData.getLoginPwd());
                System.out.println("第三方接口获取OA的时间戳：" + ssoData.getTimeStamp());
                System.out.println("第三方接口获取OA的签名：" + ssoData.getSign());
                retMsg.setCode(200);
                retMsg.setMessage("第三方提供接口成功返回");
            } else {
                retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
                retMsg.setMessage("第三方提供接口未能成功获取OA的参数");
            }
        } catch (Exception e) {
            retMsg.setCode(500);
            retMsg.setMessage(e.getMessage());
            logger.error("单点登录数据同步异常：", e);
        }
        return retMsg;
    }
    
}
