package aljoin.web.controller.per;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 用户公共信息表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Controller
@RequestMapping("/per/autUserPub")
public class AutUserPubController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutUserPubController.class);
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private SysParameterService sysParameterService;
   /* @Resource
    private FastdfsService fastdfsService;
*/
    /**
     * 
     * 用户公共信息表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/autUserPubPage")
    public String autUserPubPage(HttpServletRequest request, HttpServletResponse response) {

        return "per/autUserPubPage";
    }

    /**
     * 
     * 个人中心-通讯录页面
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017年10月28日 上午10:32:57
     */
    @RequestMapping("/autUserPubVOPage")
    public String autUserPubVOPage(HttpServletRequest request, HttpServletResponse response) {

        return "per/autUserPubVOPage";
    }

    /**
     * 
     * 用户公共信息表(分页列表).
     *
     * @return：Page<AutUserPub>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<AutUserPub> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUserPubVO obj) {
        Page<AutUserPub> page = null;
        try {
            page = autUserPubService.list(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 个人中心-个人信息-新增
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutUserPubVO obj) throws Exception {

        CustomUser customUser = getCustomDetail();
        RetMsg retMsg = autUserPubService.add(obj, customUser.getUserId());
        return retMsg;
    }

    /**
     * 
     * 用户公共信息表(根据ID删除对象).
     *
     * @return：RetMsg
     * 
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutUserPub obj) {
        RetMsg retMsg = new RetMsg();

        autUserPubService.deleteById(obj.getId());

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 用户公共信息表(根据ID修改对象).
     *
     * @throws Exception
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutUserPubVO obj) throws Exception {

        CustomUser customUser = getCustomDetail();
        RetMsg retMsg = autUserPubService.update(obj, customUser.getUserId());
        return retMsg;
    }

    /**
     * 
     * 用户修改密码
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月10日 下午5:10:47
     */
    @RequestMapping("/updatePwd")
    @ResponseBody
    public RetMsg updatePwd(HttpServletRequest request, HttpServletResponse response, AutUserPubVO obj) {

        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = autUserPubService.updatePwd(obj, customUser.getUserId());
        } catch (Exception e) {
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 个人中心-通讯录
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月13日 上午10:56:32
     */
    @RequestMapping("/getAutUserPubVOList")
    @ResponseBody
    public List<AutUserPubVO> getAutUserPubVOList(AutUserPubVO vo) throws Exception {

        List<AutUserPubVO> autUserPubVOList = autUserPubService.getAutUserPubVOList(vo);

        return autUserPubVOList;

    }

    /**
     * 
     * 个人中心-个人信息-详情（用户、部门、岗位、公共信息）
     *
     * @return：AutUserPubVO
     *
     * @author：laijy
     *
     * @date：2017年10月23日 上午10:46:53
     */
    @RequestMapping("/getById")
    @ResponseBody
    public AutUserPubVO getById(HttpServletRequest request, HttpServletResponse response, AutUserPub obj) {
        AutUserPubVO vo = new AutUserPubVO();
        try {
            CustomUser customUser = getCustomDetail();
            vo = autUserPubService.getById(customUser.getUserId());
            if (vo != null && vo.getAutUserPub() != null) {
                AutUserPub autUserPub = vo.getAutUserPub();
                String imgUrl = vo.getAutUserPub().getUserIcon();
                if (!(imgUrl != null && !"".equals(imgUrl))) {
                    autUserPub.setUserIcon("../../web/images/0.jpg");
                }
                vo.setAutUserPub(autUserPub);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return vo;
    }

    /**
     * 
     * 个人中心-通讯录(本部门)
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月24日 下午1:12:03
     */
    @RequestMapping("/getMyDeptAutUserPubVOList")
    @ResponseBody
    public List<AutUserPubVO> getMyDeptAutUserPubVOList() throws Exception {

        CustomUser customUser = getCustomDetail();
        List<AutUserPubVO> autUserPubVOList = autUserPubService.getMyDeptAutUserPubVOList(customUser.getUserId());

        return autUserPubVOList;
    }

    /**
     * 
     * 个人中心-通讯录(本单位)
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月24日 下午7:46:30
     */
    @RequestMapping("/getMyCompAutUserPubVOList")
    @ResponseBody
    public List<AutUserPubVO> getMyCompAutUserPubVOList() throws Exception {

        CustomUser customUser = getCustomDetail();
        List<AutUserPubVO> myCompAutUserPubVOList = autUserPubService.getMyCompAutUserPubVOList(customUser.getUserId());
        return myCompAutUserPubVOList;

    }

    /**
     * 
     * 个人中心-公共信息-查询
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月26日 上午10:38:16
     */
    @RequestMapping("/searchUserPubInfo")
    @ResponseBody
    public Page<AutUserPubVO> searchUserPubInfo(HttpServletRequest request, HttpServletResponse response,
        AutUserPubVO vo, PageBean pageBean) throws Exception {
        // List<AutUserPubVO> autUserPubInfo = new ArrayList<AutUserPubVO>();
        Page<AutUserPubVO> page = new Page<AutUserPubVO>();
        try {
            CustomUser customUser = getCustomDetail();
            page = autUserPubService.searchValue(vo, customUser.getUserId(), pageBean.getPageNum(),
                pageBean.getPageSize());
            page.setCurrent(pageBean.getPageNum());
            // page.setTotal(total);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e);
            logger.error("通讯录查询失败", e);
        }
        return page;
    }


    @RequestMapping("/searchquery")
    @ResponseBody
    public List<AutUserPubVO> searchquery(HttpServletRequest request, HttpServletResponse response) {
        CustomUser customUser = getCustomDetail();
        List<AutUserPubVO> vo = new ArrayList<AutUserPubVO>();
        try {
            String queryType = request.getParameter("selectType");
            if (queryType == null) {
                queryType = "1";
            }
            String qurySearch = request.getParameter("queryStr");
            if (qurySearch == null) {
                qurySearch = "";
            }
            vo = autUserPubService.searchquery(queryType, qurySearch, customUser.getUserId());

        } catch (Exception e) {
            // TODO Auto-generated catch block
            // logger.error("", e);
            logger.error("查询错误", e.getMessage());
        }
        return vo;
    }
    
    /**
     * 
     * 更换用户头像
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-09-05
     */
    @RequestMapping(value = "/uploadimg", method = RequestMethod.POST)
    @ResponseBody
    public RetMsg uploadImg(HttpServletRequest request, AutUserPubVO autUserPubVO) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = autUserPubService.updateImg(autUserPubVO, customUser.getUserId());
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("用户更换头像失败");
            logger.error("用户更换头像失败", e.getMessage());
        }
        return retMsg;
    }
}
