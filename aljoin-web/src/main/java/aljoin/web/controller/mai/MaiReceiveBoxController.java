package aljoin.web.controller.mai;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.security.CustomUser;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.object.MaiReceiveBoxListVO;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.mai.iservice.MaiReceiveBoxService;
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
 * 收件箱表(控制器).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/mai/maiReceiveBox", method = RequestMethod.POST)
@Api(value = "收件箱Controller", description = "内部邮件->收件箱接口")
public class MaiReceiveBoxController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(MaiReceiveBoxController.class);
    @Resource
    private MaiReceiveBoxService maiReceiveBoxService;

    /**
     * 
     * 收件箱表(页面).
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiReceiveBoxPage", method = RequestMethod.GET)
    public String maiReceiveBoxPage(HttpServletRequest request, HttpServletResponse response) {

        return "mai/maiReceiveBoxPage";
    }

    /**
     * 
     * 收件箱表(分页列表).
     *
     * @return：Page<MaiReceiveBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    @ApiOperation(value = "收件箱分页列表接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.subjectText", value = "主题", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.sendFullName", value = "发件人", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.begSendTime", value = "开始发送时间", required = false, dataType = "date",
            paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.endSendTime", value = "结束发送时间", required = false, dataType = "date",
            paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.isRead", value = "状态", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.isImportant", value = "是否重要", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "maiReceiveBox.isUrgent", value = "是否紧急", required = false, dataType = "int",
            paramType = "query"),
        @ApiImplicitParam(name = "sendTimeSort", value = "发送时间排序(1:升序 其它值是降序)", required = false, dataType = "string",
            paramType = "query"),
        @ApiImplicitParam(name = "maiSizeSort", value = "邮件大小排序(1:升序 其它值是降序)", required = false, dataType = "string",
            paramType = "query")})
    @FuncObj(desc = "[内部邮件]-[收件箱]-[搜索]")
    public Page<MaiReceiveBoxListVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        MaiReceiveBoxVO obj) {
        Page<MaiReceiveBoxListVO> page = null;
        try {
            CustomUser customUser = getCustomDetail();
            if (null == obj.getMaiReceiveBox()) {
                MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
                obj.setMaiReceiveBox(maiReceiveBox);
            }
            if (null == obj.getMaiReceiveBoxSearch()){
                MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
                obj.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
            }
            obj.getMaiReceiveBox().setCreateUserId(customUser.getUserId());
            page = maiReceiveBoxService.list(pageBean, obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 收件箱表(新增).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiReceiveBoxVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = maiReceiveBoxService.add(obj);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
            logger.error("", e);
        }
        return retMsg;
    }

    /**
     * 
     * 收件箱表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    @ApiOperation(value = "收件箱删除接口")
    @ApiImplicitParam(name = "ids", value = "主键(多个用分号隔开)", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[收件箱]-[删除]")
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiReceiveBoxVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            obj.setUserId(customUser.getUserId());
            retMsg = maiReceiveBoxService.delete(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     * 
     * 收件箱表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiReceiveBox obj) {
        RetMsg retMsg = new RetMsg();

        MaiReceiveBox orgnlObj = maiReceiveBoxService.selectById(obj.getId());
        BeanUtils.copyProperties(obj, orgnlObj);
        maiReceiveBoxService.updateById(orgnlObj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * 收件箱表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/getById")
    @ResponseBody
    @ApiOperation(value = "收件箱详情接口")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[收件箱]-[详情]")
    public MaiReceiveBoxVO getById(HttpServletRequest request, HttpServletResponse response, MaiReceiveBox obj) {
        MaiReceiveBoxVO maiReceiveBoxVO = null;
        try {
            CustomUser user = getCustomDetail();
            maiReceiveBoxVO
                = maiReceiveBoxService.detail(obj, user.getUserId(), user.getUsername(), user.getNickName());
        } catch (Exception e) {
            logger.error("", e);
        }
        return maiReceiveBoxVO;
    }

    /**
     *
     * 收件箱表(撤销并删除).
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-21
     */
    @RequestMapping(value = "/revoke")
    @ResponseBody
    @ApiOperation(value = "收件箱撤回并删除接口")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[收件箱]-[撤回并删除]")
    public RetMsg revoke(HttpServletRequest request, HttpServletResponse response, MaiReceiveBox obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = maiReceiveBoxService.revokeAndDelete(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     *
     * 收件箱标记为已读未读().
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-22
     */
    @RequestMapping(value = "/signRead")
    @ResponseBody
    @ApiOperation(value = "收件箱标记为已读接口")
    @ApiImplicitParam(name = "ids", value = "主键(多个用逗号分隔)", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[收件箱]-[标记为已读]")
    public RetMsg signRead(HttpServletRequest request, HttpServletResponse response, MaiReceiveBoxVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser user = getCustomDetail();
            retMsg = maiReceiveBoxService.sign(obj, user.getUserId(), user.getUsername(), user.getNickName());
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     *
     * 收件箱标记为已读().
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017-09-22
     */
    @RequestMapping(value = "/signImport")
    @ResponseBody
    @ApiOperation(value = "收件箱标记为是否重要")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
    @FuncObj(desc = "[内部邮件]-[收件箱]-[标记为是否重要]")
    public RetMsg signImport(HttpServletRequest request, HttpServletResponse response, MaiReceiveBoxSearch obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = maiReceiveBoxService.signImport(obj);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作异常");
        }
        return retMsg;
    }

    /**
     *
     * 收件箱详情页面(页面).
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017-10-31
     */
    @RequestMapping(value = "/maiReceiveBoxPageDetailPage", method = RequestMethod.GET)
    @ApiOperation("收件箱详情页面接口")
    public String maiReceiveBoxPageDetailPage(HttpServletRequest request, HttpServletResponse response) {

        String id = request.getParameter("id");
        String isImportant = request.getParameter("isImportant");
        String isRead = request.getParameter("isRead");

        request.setAttribute("t_id", id);
        request.setAttribute("t_isImportant", isImportant);
        request.setAttribute("t_isRead", isRead);
        return "mai/maiReceiveBoxPageDetailPage";
    }

    /**
     *
     * 发件箱编辑(页面).
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiReceiveBoxPageEdit", method = RequestMethod.GET)
    @ApiOperation("发件箱编辑(页面)")
    public String maiReceiveBoxPageEdit(HttpServletRequest request, HttpServletResponse response) {
        String who = request.getParameter("who");
        String title = request.getParameter("title");

        request.setAttribute("t_who", who);
        request.setAttribute("t_title", title);

        return "mai/maiReceiveBoxPageEdit";
    }
}
