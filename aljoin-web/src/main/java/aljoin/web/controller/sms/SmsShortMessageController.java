package aljoin.web.controller.sms;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 短信表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Controller
@RequestMapping("/sms/smsShortMessage")
public class SmsShortMessageController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(SmsShortMessageController.class);
    @Resource
    private SmsShortMessageService smsShortMessageService;

    /**
     * 
     * 短信表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/smsalreadysentPage")
    public String smsShortMessagePage(HttpServletRequest request, HttpServletResponse response) {

        return "sms/smsalreadysentPage";
    }

    /**
     * 
     * 短信表(分页列表).
     *
     * @return：Page<SmsShortMessage>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<SmsShortMessage> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        SmsShortMessage obj, String time1, String time2) {
        Page<SmsShortMessage> page = null;
        try {
            CustomUser customUser = getCustomDetail();
            page = smsShortMessageService.list(pageBean, obj, customUser.getUserId(), time1, time2);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 短信表(新增).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, SmsShortMessage obj) {
        RetMsg retMsg = new RetMsg();

        try {
            retMsg = smsShortMessageService.add(obj, null);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    /**
     * 
     * 短信表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SmsShortMessage obj) {
        RetMsg retMsg = new RetMsg();

        try {
            smsShortMessageService.deleteById(obj.getId());

            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    /**
     * 
     * 短信表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, SmsShortMessage obj) {
        RetMsg retMsg = new RetMsg();

        try {
            SmsShortMessage orgnlObj = smsShortMessageService.selectById(obj.getId());
            smsShortMessageService.updateById(orgnlObj);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    /**
     * 
     * 短信表(根据ID获取对象).
     *
     * @throws Exception
     * @return：AutUser
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    @RequestMapping("/getById")
    @ResponseBody
    public SmsShortMessage getById(HttpServletRequest request, HttpServletResponse response, SmsShortMessage obj)
        throws Exception {
        SmsShortMessage sms = null;
        sms = smsShortMessageService.detail(obj.getId());
        return sms;
    }

    /**
     * 
     * 手机短信-已发短信-删除（批量）
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月26日 下午3:49:43
     */
    @RequestMapping("/deleteShortMessageList")
    @ResponseBody
    public RetMsg deleteShortMessageList(String ids) throws Exception {

        RetMsg retMsg = new RetMsg();
        retMsg = smsShortMessageService.deleteShortMessageList(ids);
        return retMsg;

    }

}
