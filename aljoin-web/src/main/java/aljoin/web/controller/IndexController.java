package aljoin.web.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserInfo;
import aljoin.aut.dao.object.AutMenuVO;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.dao.object.NavigationVO;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutIndexPageModuleService;
import aljoin.aut.iservice.AutMenuRoleService;
import aljoin.aut.iservice.AutUserInfoService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomPasswordEncoder;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.object.DoTaskShowVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.ioa.iservice.IoaDoingWorkService;
import aljoin.ioa.iservice.IoaWaitingWorkService;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.object.MaiReceiveBoxListVO;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.SsoData;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.off.dao.object.OffSchedulingDO;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoCategoryService;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.util.StringUtil;
import aljoin.web.annotation.FuncObj;
import aljoin.web.javaBean.IndexDataBean;

/**
 * 
 * 首页控制器
 *
 * @author：zhongjy
 *
 * @date：2017年4月24日 下午12:31:49
 */
@Controller
public class IndexController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Resource
    private AutMenuRoleService autMenuRoleService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private CustomPasswordEncoder customPasswordEncoder;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private AttSignInOutService attSignInOutService;
    @Resource
    private AutIndexPageModuleService autIndexPageModuleService;
    @Resource
    private AutDataStatisticsService autDataStatisticsService;
    @Resource
    private OffSchedulingService offSchedulingService;
    @Resource
    private MaiReceiveBoxService maiReceiveBoxService;
    @Resource
    private PubPublicInfoCategoryService pubPublicInfoCategoryService;
    @Resource
    private PubPublicInfoService pubPublicInfoService;
    @Resource
    private IoaWaitingWorkService ioaWaitingWorkService;
    @Resource
    private IoaDoingWorkService ioaDoingWorkService;
    @Resource
    private AutUserInfoService autUserInfoService;

    /**
     * 
     * 首页
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月22日 下午11:05:27
     */
    @RequestMapping("/")
    public String index(HttpServletRequest request) {
        CustomUser cu = getCustomDetail();
        request.setAttribute("CURRENT_NICK_NAME", cu.getNickName());
        request.setAttribute("IMGURL", StringUtils.isEmpty(cu.getUserIcon())?"":cu.getUserIcon());
        request.setAttribute("CURRENT_DEPT_NAMES",
            StringUtils.isEmpty(cu.getDeptNames()) ? "无归属部门" : cu.getDeptNames());
        // 主体颜色
        // request.setAttribute("ALJOIN_CURRENT_MAIN_COLOR", "#039adf");
        // 副颜色
        // request.setAttribute("ALJOIN_CURRENT_SUB_COLOR", "#e5f5fc");
        String mainColor = (String)request.getSession().getAttribute("ALJOIN_CURRENT_MAIN_COLOR");
        if (mainColor == null) {
            Where<AutUserInfo> userInfoWhere = new Where<AutUserInfo>();
            userInfoWhere.eq("user_id", cu.getUserId());
            userInfoWhere.eq("is_active", 1);
            userInfoWhere.eq("is_delete", 0);
            userInfoWhere.eq("user_key", "THEME-COLOR-CODE");
            AutUserInfo userInfo = autUserInfoService.selectOne(userInfoWhere);
            if (userInfo == null) {
                String defaultTheme = StringUtil.hex2rgba("#039adf", "1");
                String defaultSubTheme = StringUtil.hex2rgba("#039adf", "0.2");
                // 插入
                userInfo = new AutUserInfo();
                userInfo.setIsActive(1);
                userInfo.setUserId(cu.getUserId());
                userInfo.setUserKey("THEME-COLOR-CODE");
                userInfo.setUserValue("#039adf");// 默认
                userInfo.setDescription("用户皮肤主题颜色");
                autUserInfoService.insert(userInfo);
                request.getSession().setAttribute("ALJOIN_CURRENT_MAIN_COLOR", defaultTheme);
                request.getSession().setAttribute("ALJOIN_CURRENT_SUB_COLOR", defaultSubTheme);
            } else {
                request.getSession().setAttribute("ALJOIN_CURRENT_MAIN_COLOR",
                    StringUtil.hex2rgba(userInfo.getUserValue(), "1"));
                request.getSession().setAttribute("ALJOIN_CURRENT_SUB_COLOR",
                    StringUtil.hex2rgba(userInfo.getUserValue(), "0.2"));
            }
        }

        logger.info("Xmx=" + Runtime.getRuntime().maxMemory() / 1024.0 / 1024 + "M");
        logger.info("free mem=" + Runtime.getRuntime().freeMemory() / 1024.0 / 1024 + "M");
        logger.info("total mem=" + Runtime.getRuntime().totalMemory() / 1024.0 / 1024 + "M");
        return "index/index";
    }

    /**
     * 动态皮肤样式
     * 
     * @param request
     * @return
     */
    @RequestMapping("/getCssStyle")
    public void getCssStyle(HttpServletRequest request, HttpServletResponse response) {
        //
        String css = request.getParameter("css");
        try {
            response.setContentType("text/css;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            // File file = ResourceUtils.getFile("classpath:static/web/plugins/layui2/css/layui.css");
            // File file = ResourceUtils.getFile("classpath:" + css);
            // byte[] fileStr = Files.readAllBytes(file.toPath());
            InputStream is = this.getClass().getResourceAsStream("/" + css);
            byte[] fileStr = new byte[is.available()];
            is.read(fileStr);
            String str = new String(fileStr);
            String mainColor = (String)request.getSession().getAttribute("ALJOIN_CURRENT_MAIN_COLOR");
            String subColor = (String)request.getSession().getAttribute("ALJOIN_CURRENT_SUB_COLOR");
            str = str.replaceAll("ALJOIN_CURRENT_MAIN_COLOR", mainColor);
            str = str.replaceAll("ALJOIN_CURRENT_SUB_COLOR", subColor);
            System.out.println(str);
            PrintWriter out = response.getWriter();
            out.write(str);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/index/changeCssStyle")
    @ResponseBody
    public RetMsg changeCssStyle(HttpServletRequest request, HttpServletResponse response) {
        String colorCode = request.getParameter("colorCode");
        CustomUser cu = getCustomDetail();
        Where<AutUserInfo> userInfoWhere = new Where<AutUserInfo>();
        userInfoWhere.eq("user_id", cu.getUserId());
        userInfoWhere.eq("is_active", 1);
        userInfoWhere.eq("is_delete", 0);
        userInfoWhere.eq("user_key", "THEME-COLOR-CODE");
        AutUserInfo userInfo = autUserInfoService.selectOne(userInfoWhere);
        if (userInfo == null) {
            // 插入
            userInfo = new AutUserInfo();
            userInfo.setIsActive(1);
            userInfo.setUserId(cu.getUserId());
            userInfo.setUserKey("THEME-COLOR-CODE");
            userInfo.setUserValue(colorCode);
            userInfo.setDescription("用户皮肤主题颜色");
            autUserInfoService.insert(userInfo);
        }
        {
            // 修改
            userInfo.setUserValue(colorCode);
            autUserInfoService.updateById(userInfo);
        }
        // 把颜色放到session
        request.getSession().setAttribute("ALJOIN_CURRENT_MAIN_COLOR", StringUtil.hex2rgba(colorCode, "1"));
        request.getSession().setAttribute("ALJOIN_CURRENT_SUB_COLOR", StringUtil.hex2rgba(colorCode, "0.2"));
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 主页
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月22日 下午11:05:27
     */
    @RequestMapping("/index/main")
    public String main(HttpServletRequest request) {
        request.setAttribute("CURRENT_NICK_NAME", getCustomDetail().getNickName());
        return "index/main";
    }

    /**
     * 
     * 个人信息
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年5月22日 下午11:05:27
     */
    @RequestMapping("/index/userInfoPage")
    @FuncObj(desc = "[首页]-[个人信息]")
    public String userInfoPage(HttpServletRequest request) {
        AutUser autUser = autUserService.selectById(getCustomDetail().getUserId());
        request.setAttribute("fullName", autUser.getFullName());
        return "index/userInfoPage";
    }

    /**
     * 
     * 返回左侧菜单数据
     *
     * @return：List<NavigationVO>
     *
     * @author：zhongjy
     *
     * @date：2017年5月22日 下午11:05:39
     */
    @RequestMapping("/index/menuList")
    @ResponseBody
    public List<NavigationVO> menuList(HttpServletRequest request) {
        List<AutMenuVO> autMenuVOList = getCustomDetail().getMenuList();
        List<NavigationVO> navigatioList = new ArrayList<NavigationVO>();
        for (AutMenuVO autMenuVO : autMenuVOList) {
            NavigationVO nav = new NavigationVO();
            nav.setTitle(autMenuVO.getMenuName());
            nav.setSpread(false);
            nav.setIcon(autMenuVO.getMenuIcon());
            nav.setHref(autMenuVO.getMenuHref());
            nav.setId(autMenuVO.getId().toString());
            if (autMenuVO.getChildren() != null && autMenuVO.getChildren().size() > 0) {
                List<NavigationVO> children = new ArrayList<NavigationVO>();
                for (AutMenuVO autMenuChildVO : autMenuVO.getChildren()) {
                    NavigationVO child = new NavigationVO();
                    child.setTitle(autMenuChildVO.getMenuName());
                    child.setSpread(false);
                    child.setIcon(autMenuChildVO.getMenuIcon());
                    child.setId(autMenuChildVO.getId().toString());
                    child.setHref(autMenuChildVO.getMenuHref());
                    children.add(child);
                }
                nav.setChildren(children);
            }
            navigatioList.add(nav);
        }
        return navigatioList;
    }

    @RequestMapping("/index/doChangePwd")
    @ResponseBody
    @FuncObj(desc = "[首页]-[修改密码]-[确定修改]")
    public RetMsg doChangePwd(HttpServletRequest request, HttpServletResponse response) {
        String oldPwd = request.getParameter("oldPwd");
        String newPwd = request.getParameter("newPwd");
        AutUser autUser = autUserService.selectById(getCustomDetail().getUserId());
        RetMsg retMsg = new RetMsg();
        if (customPasswordEncoder.matches(oldPwd, autUser.getUserPwd())) {
            autUser.setUserPwd(customPasswordEncoder.encode(newPwd));
            autUserService.updateById(autUser);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } else {
            retMsg.setCode(1);
            retMsg.setMessage("原密码错误");
        }
        return retMsg;
    }

    @RequestMapping("/index/saveUserInfo")
    @ResponseBody
    @FuncObj(desc = "[首页]-[个人信息]-[保存]")
    public RetMsg saveUserInfo(HttpServletRequest request, HttpServletResponse response) {
        String fullName = request.getParameter("fullName");
        AutUser autUser = autUserService.selectById(getCustomDetail().getUserId());
        autUser.setFullName(fullName);
        autUserService.updateById(autUser);
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 单点登录数据同步接口
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年11月21日 上午8:52:56
     */
    @RequestMapping("/sso/dataSynch")
    @ResponseBody
    @FuncObj(desc = "[单点登录数据同步接口]")
    public RetMsg ssoDataSynch(HttpServletRequest request, HttpServletResponse response, SsoData ssoData) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = autUserService.ssoDataSynch(ssoData);
        } catch (Exception e) {
            retMsg.setCode(500);
            retMsg.setMessage(e.getMessage());
            logger.error("单点登录数据同步异常：", e);
        }
        return retMsg;
    }

    /**
     * 
     * 首页数据汇总接口
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年12月5日 下午3:18:46
     */
    @RequestMapping("/index/indexAllData")
    @ResponseBody
    @FuncObj(desc = "[首页]-[首页数据汇总接口]")
    public RetMsg indexAllData(HttpServletRequest request, HttpServletResponse response, OffSchedulingDO obj,
        PageBean pageBean, MaiReceiveBoxVO obj2, PubPublicInfoCategory obj3, PubPublicInfoVO obj4, WaitTaskShowVO obj5,
        DoTaskShowVO obj6) {
        RetMsg backMsg = new RetMsg();
        CustomUser user = getCustomDetail();

        IndexDataBean indexDataBean = new IndexDataBean();

        RetMsg getSignInOutStr = null;
        RetMsg init = null;
        RetMsg indexCount = null;
        List<OffScheduling> dateMeet = null;
        RetMsg checkMsg = null;
        List<String> monthMeet = null;
        Page<MaiReceiveBoxListVO> list = null;
        List<PubPublicInfoCategory> allList = null;
        Page<PubPublicInfoDO> lastList = null;
        Page<WaitTaskShowVO> selectWaitTask = null;
        Page<DoTaskShowVO> selectDoTask = null;
        List<AutUserPubVO> searchquery = null;

        // 1.首页签到签退显示文本att/attSignInOut/getSignInOutStr
        try {
            getSignInOutStr = attSignInOutService.getSignInOutStr(user.getUserId(), request);
        } catch (Exception e) {
            getSignInOutStr.setCode(1);
            getSignInOutStr.setMessage(e.getMessage());
            logger.error("首页签到签退显示文本异常", e);
        }

        // 2.首页模块定制表(初始化首页及个性定制页数据).per/autIndexPageModule/init
        try {
            init = autIndexPageModuleService.init(user.getUserId());
        } catch (Exception e) {
            init.setCode(1);
            init.setMessage(e.getMessage());
            logger.error("首页模块定制表(初始化首页及个性定制页数据)异常", e);
        }
        // 3.在线通知 待办 公告 文件 统计aut/autDataStatistics/indexCount
        try {
            AutDataStatistics autData = new AutDataStatistics();
            List<AutDataStatistics> statisticsList = new ArrayList<AutDataStatistics>();
            if (user != null) {
                autData.setBusinessKey(String.valueOf(user.getUserId()));
            }
            statisticsList = autDataStatisticsService.getUserCount(autData);
            indexCount = new RetMsg();
            indexCount.setCode(0);
            indexCount.setObject(statisticsList);
        } catch (Exception e) {
            indexCount.setCode(1);
            indexCount.setMessage(e.getMessage());
            logger.error("在线通知 待办 公告 文件 统计异常", e);
        }

        // 4.首页会议列表接口off/offScheduling/dateMeet
        try {
            obj.setCreateUserId(user.getUserId());
            List<OffScheduling> scheduleList = offSchedulingService.indexList4day(obj);
            dateMeet = scheduleList;
        } catch (Exception e) {
            logger.error("首页会议列表接口", e);
        }

        // 5.检查未读消息数量aut/autDataStatistics/checkMsg
        try {
            checkMsg = new RetMsg();
            AutDataStatistics autData = new AutDataStatistics();
            if (user != null) {
                autData.setBusinessKey(String.valueOf(user.getUserId()));
            }
            Integer msgCount = autDataStatisticsService.getUserMsgCount(autData);
            checkMsg.setObject(msgCount);
            checkMsg.setMessage("操作成功");
            checkMsg.setCode(0);
        } catch (Exception e) {
            checkMsg.setCode(1);
            checkMsg.setMessage("操作失败");
            logger.error("检查未读消息数量", e);
        }

        // 6.首页月日程接口off/offScheduling/monthMeet
        try {
            obj.setCreateUserId(user.getUserId());
            List<String> strList = offSchedulingService.indexList4Month(obj);
            monthMeet = strList;
        } catch (Exception e) {
            logger.error("首页月日程接口", e);
        }
        // 7.收件箱分页列表接口mai/maiReceiveBox/list
        try {
            CustomUser customUser = getCustomDetail();
            if (null == obj2.getMaiReceiveBox()) {
                MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
                obj2.setMaiReceiveBox(maiReceiveBox);
            }
            obj2.getMaiReceiveBox().setCreateUserId(customUser.getUserId());
            Page<MaiReceiveBoxListVO> page1 = maiReceiveBoxService.list(pageBean, obj2);
            list = page1;
        } catch (Exception e) {
            logger.error("收件箱分页列表接口", e);
        }
        // 8.公共信息分类列表pub/pubPublicInfoCategory/allList
        try {
            obj.setCreateUserId(user.getUserId());
            List<PubPublicInfoCategory> categoryList = pubPublicInfoCategoryService.allList(obj3);
            allList = categoryList;
        } catch (Exception e) {
            logger.error("公共信息分类列表", e);
        }
        // 9.最新信息分页列表pub/pubPublicInfo/lastList
        try {
            obj.setCreateUserId(user.getUserId());
            Page<PubPublicInfoDO> page2 = pubPublicInfoService.lastList(pageBean, obj4);
            lastList = page2;
        } catch (Exception e) {
            logger.error("最新信息分页列表", e);
        }
        // 10.待办列表ioa/ioaWaitWork/selectWaitTask
        try {
            String userId = user.getUserId().toString();
            Page<WaitTaskShowVO> page3 = ioaWaitingWorkService.list(pageBean, userId, obj5);
            selectWaitTask = page3;
        } catch (Exception e) {
            logger.error("待办列表", e);
        }
        // 11.在办列表ioa/ioaDoingWork/selectDoTask
        try {
            String userId = user.getUserId().toString();
            Page<DoTaskShowVO> page4 = ioaDoingWorkService.list(pageBean, userId, obj6);
            selectDoTask = page4;
        } catch (Exception e) {
            logger.error("在办列表", e);
        }
        // 12.通讯录查询per/autUserPub/searchquery
        try {
            List<AutUserPubVO> vo = new ArrayList<AutUserPubVO>();
            String queryType = request.getParameter("selectType");
            if (queryType == null) {
                queryType = "1";
            }
            String qurySearch = request.getParameter("queryStr");
            if (qurySearch == null) {
                qurySearch = "";
            }
            vo = autUserPubService.searchquery(queryType, qurySearch, user.getUserId());
            searchquery = vo;
        } catch (Exception e) {
            logger.error("查询错误", e.getMessage());
        }
        indexDataBean.setGetSignInOutStr(getSignInOutStr);
        indexDataBean.setInit(init);
        indexDataBean.setIndexCount(indexCount);
        indexDataBean.setDateMeet(dateMeet);
        indexDataBean.setCheckMsg(checkMsg);
        indexDataBean.setMonthMeet(monthMeet);
        indexDataBean.setList(list);
        indexDataBean.setAllList(allList);
        indexDataBean.setLastList(lastList);
        indexDataBean.setSelectWaitTask(selectWaitTask);
        indexDataBean.setSelectDoTask(selectDoTask);
        indexDataBean.setSearchquery(searchquery);

        backMsg.setObject(indexDataBean);
        backMsg.setCode(0);
        return backMsg;
    }

}
