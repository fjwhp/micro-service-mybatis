package aljoin.web.controller.ioa;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.ioa.iservice.IoaMonitorWorkService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.controller.BaseController;
import aljoin.web.service.ioa.IoaMonitorWorkWebService;
import aljoin.web.task.WebTask;

/**
 * 
 * 流转监控
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaMonitor")
public class IoaMonitorController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(IoaMonitorController.class);
    @Resource
    private IoaMonitorWorkService ioaMonitorWorkService;
    @Resource
    private WebTask webTask;
    @Resource
    private IoaMonitorWorkWebService ioaMonitorWorkWebService;
    /**
     * 
     * 流转监控页面
     *
     * @return：String
     *
     * @author：zhongjy
     *
     * @date：2017年10月12日 上午8:42:57
     */
    @RequestMapping("/ioaMonitorPage")
    public String ioaMonitorPage(HttpServletRequest request) {
        return "ioa/ioaMonitorPage";
    }

    /**
     * 
     * 根据当前用户获取流转流程
     *
     * @return：List
     *
     * @author：pengsp
     *
     * @date：2017年10月16日
     */
    @RequestMapping("/list")
    @ResponseBody
    public Page<WaitTaskShowVO> selectWaitTask(HttpServletRequest request, HttpServletResponse response,
        PageBean pageBean) {
        Page<WaitTaskShowVO> page = new Page<WaitTaskShowVO>();
        try {
            Map<String, Object> queryMap = new HashMap<String, Object>();
            String queryValue = request.getParameter("formType");
            
            queryValue = request.getParameter("qBpmn");
            //流程id
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("qBpmn", queryValue);
            // 流程分类
            }else if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("formType", queryValue);
            }
            queryValue = request.getParameter("searchKey");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("searchKey", queryValue);
            }
            queryValue = request.getParameter("startTime");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("StatrTime", queryValue);
            }
            queryValue = request.getParameter("endTime");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("EndTime", queryValue);
            }
            queryValue = request.getParameter("isUrgent");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("isUrgent", queryValue);
            }
            queryValue = request.getParameter("serialNumber");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("serialNumber", queryValue);
            }
            queryValue = request.getParameter("referenceNumber");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("referenceNumber", queryValue);
            }
            queryValue = request.getParameter("sorting");
            if (queryValue != null && !"".equals(queryValue)) {
                queryMap.put("sorting", queryValue);
            } else {
                queryMap.put("sorting", "1");
            }
            
            CustomUser customUser = getCustomDetail();
            queryMap.put("UserID", customUser.getUserId());
            queryMap.put("UserName", customUser.getNickName());
            page = ioaMonitorWorkService.list(pageBean, queryMap);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
        }
        return page;
    }

    /**
     * 
     * 根据当前用户获取流转流程
     *
     * @return：List
     *
     * @author：pengsp
     *
     * @date：2017年10月16日
     */
    @RequestMapping("/selectUrgent")
    @ResponseBody
    public RetMsg selectUrgent(HttpServletRequest request, HttpServletResponse response, String urgentId,
        String urgentMsg) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = ioaMonitorWorkService.selectUrgent(customUser.getUserId(), customUser.getUsername(),
                customUser.getNickName(), urgentId, urgentMsg);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    /**
     * 
     * 根据任务ID，单个或者批量作废流程
     *
     * @return：RetMsg
     *
     * @author：huangw
     *
     * @date：2017年12月14日
     */
    @RequestMapping("/invalidTask")
    @ResponseBody
    public RetMsg invalidTask(HttpServletRequest request, HttpServletResponse response, String invalid) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = ioaMonitorWorkService.selectInvalid(customUser.getUserId(), invalid);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    /**
     *特送人员
     * @param request               请求对象
     * @param response              响应对象
     * @param taskIds               任务Id（多个用分号分隔）
     * @param assignees             特送人员（多个用分号分隔）
     * @return RetMsg               响应对象
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/deliveryPersonnel")
    @ResponseBody
    public RetMsg deliveryPersonnel(HttpServletRequest request, HttpServletResponse response,String taskIds,String processInstanceIds,String message,String assignees) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser user = getCustomDetail();
            AutUser autUser = new AutUser();
            autUser.setId(user.getUserId());
            autUser.setFullName(user.getNickName());
            autUser.setUserName(user.getUsername());

            retMsg = ioaMonitorWorkWebService.deliveryPersonnel(taskIds,processInstanceIds,message,assignees,user.getUserId());
            Map<String,Object> retMap = (Map<String,Object>)retMsg.getObject();
            Map<String,String> procInstIdMap = (Map<String,String>)retMap.get("processInstanceIds");
            Map<String,String> businessKeyMap = (Map<String,String>)retMap.get("businessKeys");
            Map<String,String> taskAuthMap = (Map<String,String>)retMap.get("taskAuths");

            //被特送的人员需要在线消息通知提醒
            webTask.sendMutilOnlineMsg4Delivery(processInstanceIds,procInstIdMap,autUser);

            Map<String,String> map = new HashMap<>();
            for(String key : procInstIdMap.keySet()){
                map.put("processInstanceId",key);
                map.put("businessKey",businessKeyMap.get(key));
                map.put("taskAuth",taskAuthMap.get(key));
                webTask.sendOnlineMsg4MulTask(map, autUser);
            }
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

}
