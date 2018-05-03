package aljoin.web.controller.act;

import aljoin.act.dao.entity.ActAljoinTaskSignInfo;
import aljoin.act.iservice.ActAljoinTaskSignInfoService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.act.ActAljoinTaskSignInfoWebService;
import com.baomidou.mybatisplus.plugins.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @描述：加签信息表(控制器).
 * 
 * @作者：wangj
 * 
 * @时间: 2018-02-05
 */

/**
 *
 */
@Controller
@RequestMapping("/act/actAljoinTaskSignInfo")
public class ActAljoinTaskSignInfoController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ActAljoinTaskSignInfoController.class);
    @Resource
    private ActAljoinTaskSignInfoService actAljoinTaskSignInfoService;
    @Resource
    private ActAljoinTaskSignInfoWebService actAljoinTaskSignInfoWebService;

    /**
     * 
     * @描述：加签信息表(页面).
     *
     * @返回：String
     *
     * @作者：wangj
     *
     * @时间：2018-02-05
     */
    @RequestMapping("/actAljoinTaskSignInfoPage")
    public String actAljoinTaskSignInfoPage(HttpServletRequest request, HttpServletResponse response) {

        return "act/actAljoinTaskSignInfoPage";
    }

    /**
     * 
     * @描述：加签信息表(分页列表).
     *
     * @返回：Page<ActAljoinTaskSignInfo>
     *
     * @作者：wangj
     *
     * @时间：2018-02-05
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<ActAljoinTaskSignInfo> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
        ActAljoinTaskSignInfo obj) {
        Page<ActAljoinTaskSignInfo> page = null;
        try {
            page = actAljoinTaskSignInfoService.list(pageBean, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return page;
    }

    /**
     * 
     * @描述：加签信息表(新增).
     *
     * @返回：RetMsg
     *
     * @作者：wangj
     *
     * @时间：2018-02-05
     */
    @RequestMapping("/add")
    @ResponseBody
    public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskSignInfo obj) {
        RetMsg retMsg = new RetMsg();

        // obj.set...

        actAljoinTaskSignInfoService.insert(obj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * @描述：加签信息表(根据ID删除对象).
     *
     * @返回：RetMsg
     *
     * @作者：wangj
     *
     * @时间：2018-02-05
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskSignInfo obj) {
        RetMsg retMsg = new RetMsg();

        actAljoinTaskSignInfoService.deleteById(obj.getId());

        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * @描述：加签信息表(根据ID修改对象).
     *
     * @返回：RetMsg
     *
     * @作者：wangj
     *
     * @时间：2018-02-05
     */
    @RequestMapping("/update")
    @ResponseBody
    public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskSignInfo obj) {
        RetMsg retMsg = new RetMsg();

        ActAljoinTaskSignInfo orgnlObj = actAljoinTaskSignInfoService.selectById(obj.getId());
        // orgnlObj.set...

        actAljoinTaskSignInfoService.updateById(orgnlObj);
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     * 
     * @描述：加签信息表(根据ID获取对象).
     *
     * @返回：AutUser
     *
     * @作者：wangj
     *
     * @时间：2018-02-05
     */
    @RequestMapping("/getById")
    @ResponseBody
    public ActAljoinTaskSignInfo getById(HttpServletRequest request, HttpServletResponse response,
        ActAljoinTaskSignInfo obj) {
        return actAljoinTaskSignInfoService.selectById(obj.getId());
    }

    /**
     *
     * @描述：查询当前任务节点已经被加签过的人员（包括自己）.
     *
     * @返回：RetMsg
     *
     * @作者：wangj
     *
     * @时间：2018-03-06
     */
    @RequestMapping("/getSignedUserIds")
    @ResponseBody
    public RetMsg getSignedUserIds(HttpServletRequest request, HttpServletResponse response, String taskId,
        String bpmnId) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser user = getCustomDetail();
            retMsg = actAljoinTaskSignInfoWebService.getSignedUserIds(user.getUserId(),taskId);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setMessage("操作失败");
            retMsg.setCode(1);
        }
        return retMsg;
    }
}
