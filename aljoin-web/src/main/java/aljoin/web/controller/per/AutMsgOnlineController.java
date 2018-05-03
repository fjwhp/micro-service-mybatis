package aljoin.web.controller.per;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.object.AutMsgOnlineVO;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 在线消息表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
@Controller
@RequestMapping("/per/autMsgOnline")
public class AutMsgOnlineController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutMsgOnlineController.class);
    @Resource
    private AutMsgOnlineService autMsgOnlineService;

    /**
     * 
     * 在线消息表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/autMsgOnlinePage")
    public String autMsgOnlinePage(HttpServletRequest request, HttpServletResponse response) {

        return "per/autMsgOnlinePage";
    }

    /**
     * 
     * 在线消息表(分页列表).
     *
     * @return：Page<AutMsgOnline>
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<AutMsgOnline> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean) {
        Page<AutMsgOnline> page = null;
        AutMsgOnline obj = new AutMsgOnline();
        try {

            String msgType = request.getParameter("msgType");
            if (msgType != null && !"".equals(msgType)) {
                obj.setMsgType(msgType);
            }
            String isRead = request.getParameter("isRead");
            if (isRead != null && !"".equals(isRead)) {
                obj.setIsRead(Integer.valueOf(isRead));
            }
            String fromUserFullName = request.getParameter("fullnames");
            if (fromUserFullName != null && !"".equals(fromUserFullName)) {
                obj.setCreateUserName(fromUserFullName);
                obj.setFromUserFullName(fromUserFullName);
                obj.setMsgContent(fromUserFullName);
            }
            CustomUser customUser = getCustomDetail();
            page = autMsgOnlineService.list(pageBean, obj, customUser.getUserId());
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 在线消息表(新增).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutMsgOnline obj) {
        RetMsg retMsg = new RetMsg();
        autMsgOnlineService.insert(obj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 在线消息表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutMsgOnline obj) {
        RetMsg retMsg = new RetMsg();

        autMsgOnlineService.deleteById(obj.getId());

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 在线消息表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutMsgOnline obj) {
        RetMsg retMsg = new RetMsg();

        AutMsgOnline orgnlObj = autMsgOnlineService.selectById(obj.getId());
        // orgnlObj.set...

        autMsgOnlineService.updateById(orgnlObj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 在线消息表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    @RequestMapping("/getById")
    @ResponseBody
    public AutMsgOnline getById(HttpServletRequest request, HttpServletResponse response, AutMsgOnline obj) {
        return autMsgOnlineService.selectById(obj.getId());
    }

    /**
     * 
     * 页面跳转
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017年10月12日 下午2:16:27
     */
    @RequestMapping("/goToPage")
    @ResponseBody
    public void goToPage(HttpServletRequest request, HttpServletResponse response, AutMsgOnline obj) throws Exception {
        try {
            autMsgOnlineService.goToPage(response, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 
     * 在线消息推送
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月23日 下午9:16:02
     */
    @RequestMapping("/pushMessageToUserList")
    @ResponseBody
    public RetMsg pushMessageToUserList(AutMsgOnlineVO vo) throws Exception {
        RetMsg retMsg = null;// autMsgOnlineService.pushMessageToUserList(vo);
        return retMsg;
    }

    /**
     * 
     * 个人中心-在线消息-批量删除 注意：前端调用了封装好的js，参数只能叫ids
     * 
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月25日 下午7:36:34
     */
    @RequestMapping("/deleteMsgList")
    @ResponseBody
    public RetMsg deleteMsgList(String ids) throws Exception {
        RetMsg retMsg = new RetMsg();
        CustomUser user = getCustomDetail();
        retMsg = autMsgOnlineService.deleteMsgList(ids, user.getUserId());
        return retMsg;
    }

}
