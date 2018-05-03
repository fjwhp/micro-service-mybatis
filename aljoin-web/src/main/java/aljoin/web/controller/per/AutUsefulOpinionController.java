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
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUsefulOpinion;
import aljoin.aut.dao.object.AutUsefulOpinionVO;
import aljoin.aut.iservice.AutUsefulOpinionService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.controller.BaseController;

/**
 * 
 * 常用意见表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Controller
@RequestMapping("/per/autUsefulOpinion")
public class AutUsefulOpinionController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutUsefulOpinionController.class);
    @Resource
    private AutUsefulOpinionService autUsefulOpinionService;

    /**
     * 
     * 常用意见表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/autUsefulOpinionPage")
    public String autUsefulOpinionPage(HttpServletRequest request, HttpServletResponse response) {

        return "per/autUsefulOpinionPage";
    }

    /**
     * 
     * 常用意见表(分页列表).
     *
     * @return：Page<AutUsefulOpinion>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<AutUsefulOpinionVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        AutUsefulOpinion obj) {
        Page<AutUsefulOpinionVO> page = null;
        try {
            CustomUser customUser = getCustomDetail();
            page = autUsefulOpinionService.list(pageBean, obj, customUser.getUserId());
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 常用意见表(新增).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutUsefulOpinion obj) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            // 默认激活
            obj.setIsActive(1);
            obj.setUserId(customUser.getUserId());
            if (obj.getContent() != null) {
                if (obj.getContent().toString().length() >= 100) {
                    retMsg.setCode(1);
                    retMsg.setMessage("常用意见长度不能超过100个");
                    return retMsg;
                }
            }
            if (null == obj.getContentRank()) {
                obj.setContentRank(WebConstant.FULOPINION_DEF_RANK);
            }
            autUsefulOpinionService.insert(obj);
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
     * 常用意见表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutUsefulOpinion obj) {
        RetMsg retMsg = new RetMsg();

        autUsefulOpinionService.deleteById(obj.getId());

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 常用意见表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutUsefulOpinion obj) {
        RetMsg retMsg = new RetMsg();

        AutUsefulOpinion orgnlObj = autUsefulOpinionService.selectById(obj.getId());

        orgnlObj.setContent(obj.getContent());
        orgnlObj.setContentRank(obj.getContentRank());

        autUsefulOpinionService.updateById(orgnlObj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 常用意见表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/getById")
    @ResponseBody
    public AutUsefulOpinion getById(HttpServletRequest request, HttpServletResponse response, AutUsefulOpinion obj) {
        return autUsefulOpinionService.selectById(obj.getId());
    }

    /**
     *
     * 常用意见表.
     *
     * @return：List<AutUsefulOpinion>
     *
     * @author：wangj
     *
     * @date：2017-11-20
     */
    @RequestMapping("/opinionList")
    @ResponseBody
    public List<AutUsefulOpinion> opinionList(HttpServletRequest request, HttpServletResponse response,
        AutUsefulOpinion obj) {
        List<AutUsefulOpinion> list = new ArrayList<AutUsefulOpinion>();
        try {
            CustomUser user = getCustomDetail();
            obj.setCreateUserId(user.getUserId());
            list = autUsefulOpinionService.list(obj);
        } catch (Exception e) {
            logger.error("", e);
        }

        return list;
    }
}
