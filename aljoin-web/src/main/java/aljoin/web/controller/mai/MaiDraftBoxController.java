package aljoin.web.controller.mai;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiDraftBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiDraftBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import aljoin.web.task.WebTask;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 草稿箱表(控制器).
 * 
 * @author：laijy
 * 
 *               @date： 2017-09-20
 */
@Controller
@RequestMapping("/mai/maiDraftBox")
public class MaiDraftBoxController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(MaiDraftBoxController.class);
    @Resource
    private MaiDraftBoxService maiDraftBoxService;
    @Resource
    private MaiSendBoxService maiSendBoxService;
    @Resource
    private WebTask webTask;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private SysParameterService sysParameterService;

    /**
     * 
     * 草稿箱表(页面).
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    @RequestMapping("/maiDraftBoxPage")
    public String maiDraftBoxPage(HttpServletRequest request, HttpServletResponse response) {

        return "mai/maiDraftBoxPage";
    }

    /**
     * 
     * 草稿箱表(分页列表).
     *
     * @return：Page<MaiDraftBox>
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    @RequestMapping("/list")
    @ResponseBody
    @FuncObj(desc = "[内部邮件]-[草稿箱]-[列表搜索]")
    public Page<MaiDraftBox> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        MaiDraftBox obj, String time1, String time2, String orderByTime) {
        Page<MaiDraftBox> page = null;
        try {
            // 获得使用者id,用于过滤，草稿箱仅显示登录用户创建的草稿
            CustomUser user = getCustomDetail();
            Long userId = user.getUserId();
            page = maiDraftBoxService.list(pageBean, obj, userId, time1, time2, orderByTime);
        } catch (Exception e) {
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 草稿箱表(新增).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    @RequestMapping("/add")
    @ResponseBody
    @FuncObj(desc = "[内部邮件]-[草稿箱]-[新增]")
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {

        RetMsg retMsg = new RetMsg();
        CustomUser customUser = getCustomDetail();
        try {
            retMsg = maiDraftBoxService.add(obj, customUser.getUserId(), customUser.getUsername(),
                customUser.getNickName());
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     * 
     * 草稿箱表(根据ID删除对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    @RequestMapping("/delete")
    @ResponseBody
    @FuncObj(desc = "[内部邮件]-[草稿箱]-[删除]")
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiDraftBoxVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            List<MaiDraftBox> draftBoxList = obj.getDraftBoxList();
            List<Long> idList = new ArrayList<Long>();
            for (MaiDraftBox box : draftBoxList) {
                idList.add(box.getId());
            }
            maiDraftBoxService.deleteBatchIds(idList);
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
     * 草稿箱表(根据ID修改对象).
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    @RequestMapping("/update")
    @ResponseBody
    @FuncObj(desc = "[内部邮件]-[草稿箱]-[编辑]")
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {
        RetMsg retMsg = new RetMsg();
        CustomUser customUser = getCustomDetail();
        try {
            retMsg = maiDraftBoxService.update(obj, customUser.getUserId(), customUser.getUsername(),
                customUser.getNickName());
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }

        return retMsg;
    }

    /**
     * 
     * 草稿箱表(根据ID获取对象).
     *
     * @return：AutUser
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    @RequestMapping("/getById")
    @ResponseBody
    @FuncObj(desc = "[内部邮件]-[草稿箱]-[详情]")
    public MaiDraftBoxVO getById(HttpServletRequest request, HttpServletResponse response, MaiDraftBox obj) {
        MaiDraftBoxVO maiDraftBoxVO = new MaiDraftBoxVO();
        try {
            maiDraftBoxVO = maiDraftBoxService.getById(obj);
        } catch (Exception e) {
            logger.error("", e);
        }
        return maiDraftBoxVO;
    }

    /**
     * 
     * 草稿箱-发送（发送后删掉草稿箱中的该记录）
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月28日 下午4:51:27
     */
    @RequestMapping("/sendDraft")
    @ResponseBody
    @FuncObj(desc = "[内部邮件]-[草稿箱]-[发送]")
    public RetMsg sendDraft(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            AutUser user = new AutUser();
            user.setFullName(customUser.getNickName());
            user.setId(customUser.getUserId());
            user.setUserName(customUser.getUsername());
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
            int allowMailSpace = 0;
            int myMailSpace = 0;
            int size = 0;
            // 获得个人或者领导的空间限制大小
            Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
            departmentUserWhere.eq("user_id", customUser.getUserId());
            departmentUserWhere.setSqlSelect("id,is_leader");
            List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);
            if (null != departmentUserList && !departmentUserList.isEmpty()) {
                String isLeader = "";
                for (AutDepartmentUser departmentUser : departmentUserList) {
                    if (null != departmentUser && null != departmentUser.getIsLeader()) {
                        isLeader += departmentUser.getIsLeader();
                    }
                }
                if (StringUtils.isNotEmpty(isLeader)) {
                    String key = "";
                    if (isLeader.indexOf("1") > -1) {
                        key = "leader_mail_space";
                    } else {
                        key = "personal_mail_space";
                    }
                    SysParameter parameter = sysParameterService.selectBykey(key);
                    if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
                        allowMailSpace = Integer.parseInt(parameter.getParamValue());
                    }
                }
            }

            Where<MaiSendBox> maiSendBoxWhere = new Where<MaiSendBox>();
            maiSendBoxWhere.eq("send_user_id", customUser.getUserId());
            maiSendBoxWhere.setSqlSelect("id,send_user_id,mail_size");
            List<MaiSendBox> maiSendBoxList = maiSendBoxService.selectList(maiSendBoxWhere);
            if (null != maiSendBoxList && !maiSendBoxList.isEmpty()) {
                for (MaiSendBox sendBox : maiSendBoxList) {
                    if (null != sendBox && null != sendBox.getMailSize()) {
                        myMailSpace += sendBox.getMailSize();
                    }
                }
            }
            if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                    maiReceiveBoxSearch.setAttachmentCount(maiAttachmentList.size());
                    for (MaiAttachment attachment : maiAttachmentList) {
                        if (null != attachment && null == attachment.getId()) {
                            size += attachment.getAttachSize();
                        }
                    }
                }
            }
            if (StringUtils.isNotEmpty(maiReceiveBox.getMailContent())) {
                int len = (((maiReceiveBox.getMailContent().getBytes("utf-8").length) / 1024));
                // 邮件内容空间校验
                if (((len + size + myMailSpace) / 1024) >= allowMailSpace) {
                    retMsg.setCode(1);
                    retMsg.setMessage("您的邮件空间超限： 最大限度为(" + allowMailSpace + "MB)");
                    return retMsg;
                }
                maiReceiveBox.setMailSize((size + len));
            } else {
                maiReceiveBox.setMailSize(size);
            }
            obj.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
            obj.setMaiReceiveBox(maiReceiveBox);
            webTask.maiDraftSendTask(obj, user);
            retMsg.setCode(0);
            retMsg.setMessage("发送成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    /**
     *
     * 草稿编辑(页面).
     *
     * @return：String
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    @RequestMapping(value = "/maiDraftBoxPageEdit", method = RequestMethod.GET)
    @ApiOperation("草稿箱编辑(页面)")
    public String maiDraftBoxPageEdit(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String sendUserId = request.getParameter("sendUserId");
        String sendUserName = request.getParameter("sendUserName");
        String sendFullName = request.getParameter("sendFullName");

        request.setAttribute("t_id", id);
        request.setAttribute("t_sendUserId", sendUserId);
        request.setAttribute("t_sendUserName", sendUserName);
        request.setAttribute("t_sendFullName", sendFullName);
        return "mai/maiDraftBoxPageEdit";
    }

}
