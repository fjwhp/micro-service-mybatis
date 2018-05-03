package aljoin.web.controller.mai;

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

import aljoin.aut.security.CustomUser;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 发件箱表(控制器).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/mai/maiSendBox", method = RequestMethod.POST)
@Api(value = "发件箱Controller", description = "内部邮件->发件箱接口")
public class MaiSendBoxController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(MaiSendBoxController.class);
    @Resource
    private MaiSendBoxService maiSendBoxService;

    /**
     * 
     * 发件箱表(页面).
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiSendBoxPage", method = RequestMethod.GET)
    @ApiOperation(value = "发件箱页面跳转接口")
    public String maiSendBoxPage(HttpServletRequest request, HttpServletResponse response) {

        return "mai/maiSendBoxPage";
    }

    /**
     * 
     * 发件箱表(分页列表).
     *
     * @return：Page<MaiSendBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @ApiOperation(value = "发件箱分页列表接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.subjectText", value = "主题", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.receiveFullNames", value = "收件人", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.begSendTime", value = "开始发送时间", required = false, dataType = "date",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.endSendTime", value = "结束发送时间", required = false, dataType = "date",
            paramType = "query"),
        @ApiImplicitParam(name = "sendTimeSort", value = "发送时间排序(1:升序 其它值是降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSizeSort", value = "邮件大小排序(1:升序 其它值是降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "receviceUserSort", value = "收件人数排序(1:升序 其它值是降序)", required = false,
            dataType = "string", paramType = "query")})
    @FuncObj(desc = "[内部邮件]-[发件箱]-[列表搜索]")
    public Page<MaiSendBox> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        MaiSendBoxVO obj) {
        Page<MaiSendBox> page = null;
        try {
            CustomUser customUser = getCustomDetail();
            if (null == obj.getMaiSendBox()) {
                MaiSendBox maiSendBox = new MaiSendBox();
                obj.setMaiSendBox(maiSendBox);
            }
            obj.getMaiSendBox().setCreateUserId(customUser.getUserId());
            page = maiSendBoxService.list(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 发件箱表(新增).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiSendBox obj) {
        RetMsg retMsg = new RetMsg();

        // obj.set...

        maiSendBoxService.insert(obj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 发件箱表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @ApiOperation(value = "发件箱删除接口")
    @ApiImplicitParam(name = "ids", value = "主键(多个用分号分隔)", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[发件箱]-[删除]")
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiSendBoxVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = maiSendBoxService.delete(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     * 
     * 发件箱表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping("/update")
    @ResponseBody
    @ApiOperation(value = "发件箱修改接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "maiSendBox.id", value = "主键", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.receiveUserIds", value = "收件人用户ID(分号分隔)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.receiveUserNames", value = "收件人账号(分号分隔)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.receiveFullNames", value = "收件人名称(分号分隔)", required = false,
            dataType = "string", paramType = "query"),

        @ApiImplicitParam(name = "maiSendBox.copyUserIds", value = "抄送人用户ID(分号分隔)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.copyUserNames", value = "抄送人账号(分号分隔)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.copyFullNames", value = "抄送人名称(分号分隔)", required = false,
            dataType = "string", paramType = "query"),

        @ApiImplicitParam(name = "maiSendBox.subjectText", value = "主题", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.mailContent", value = "内容", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.mailSize", value = "邮件大小(KB,正文+附件的大小)", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.attachmentCount", value = "附件个数", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.weekDay", value = "阅读日期是星期几", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.isUrgent", value = "是否紧急", required = false, dataType = "int",
            paramType = "query"),

        @ApiImplicitParam(name = "maiSendBox.isReceiveSmsRemind", value = "是否进行收件人短信提醒", required = false,
            dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.receiveUserCount", value = "收件人数", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.isCopySmsRemind", value = "是否进行抄送人短信提醒", required = false,
            dataType = "int", paramType = "query"),

        @ApiImplicitParam(name = "maiSendBox.isReceiptMail", value = "本邮件是否回执邮件", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.receiveUserCount", value = "收件人数", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSendBox.attachmentCount", value = "附件个数", required = false, dataType = "int",
            paramType = "query"),

        @ApiImplicitParam(name = "maiAttachmentList[0].attachName", value = "附件名称(原文件名称)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiAttachmentList[0].attachPath", value = "附件路径(上传后的完整路径)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiAttachmentList[0].attachSize", value = "附件大小KB", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiAttachmentList[1].attachName", value = "附件名称(原文件名称)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiAttachmentList[1].attachPath", value = "附件路径(上传后的完整路径)", required = false,
            dataType = "string", paramType = "query"),
        @ApiImplicitParam(name = "maiAttachmentList[1].attachSize", value = "附件大小KB", required = false,
            dataType = "string", paramType = "query")})
    @FuncObj(desc = "[内部邮件]-[发件箱]-[编辑]")
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiSendBoxVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = maiSendBoxService.update(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     * 
     * 发件箱表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/getById")
    @ResponseBody
    @ApiOperation(value = "发件箱详情接口")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[发件箱]-[详情]")
    public MaiSendBoxVO getById(HttpServletRequest request, HttpServletResponse response, MaiSendBox obj) {
        MaiSendBoxVO maiSendBoxVO = null;
        try {
            CustomUser user = getCustomDetail();
            maiSendBoxVO = maiSendBoxService.detail(obj, user.getUserId(), user.getUsername(), user.getNickName());
        } catch (Exception e) {
            logger.error("", e);
        }
        return maiSendBoxVO;
    }

    /**
     *
     * 发件箱表(撤销并删除).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-21
     */
    @RequestMapping(value = "/revoke")
    @ResponseBody
    @ApiOperation(value = "发件箱撤回并删除接口")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[发件箱]-[撤回并删除]")
    public RetMsg revoke(HttpServletRequest request, HttpServletResponse response, MaiSendBox obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = maiSendBoxService.revokeAndDelete(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     * 
     * 发件箱表(页面).
     *
     * @return：String
     *
     * @author：sln
     *
     * @date：2017-11-08
     */
    @RequestMapping(value = "/mailSendBoxPageEdit", method = RequestMethod.GET)
    @ApiOperation(value = "编辑我的发件箱跳转接口")
    public String mailSendBoxPageEdit(HttpServletRequest request, HttpServletResponse response) {

        String who = request.getParameter("who");

        request.setAttribute("t_who", who);
        return "mai/mailSendBoxPageEdit";
    }

}
