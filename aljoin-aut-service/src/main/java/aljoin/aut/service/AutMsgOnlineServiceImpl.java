package aljoin.aut.service;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.mapper.AutMsgOnlineMapper;
import aljoin.aut.dao.object.AutMsgOnlineRequest;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.websocket.WebSocketMsg;
import aljoin.websocket.service.WebSocketService;

/**
 * 
 * 在线消息表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
@Service
public class AutMsgOnlineServiceImpl extends ServiceImpl<AutMsgOnlineMapper, AutMsgOnline>
    implements AutMsgOnlineService {
    private static Logger log = LoggerFactory.getLogger(AutMsgOnlineServiceImpl.class);
    @Resource
    private AutMsgOnlineMapper mapper;

    @Resource
    private AutMsgOnlineService autMsgOnlineService;

    @Resource
    private WebSocketService webSocketService;

    @Resource
    private AutUserService autUserService;

    @Resource
    private AutDataStatisticsService autDataStatisticsService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;

    @Override
    public Page<AutMsgOnline> list(PageBean pageBean, AutMsgOnline obj, Long customUserId) throws Exception {
        Where<AutMsgOnline> where = new Where<AutMsgOnline>();
        where.like("receive_user_id", customUserId.toString());

        if (StringUtils.checkValNotNull(obj.getMsgType())) {
            where.andNew();
            where.eq("msg_type", obj.getMsgType());

        }
        if (StringUtils.checkValNotNull(obj.getIsRead())) {
            where.andNew();
            where.eq("is_read", obj.getIsRead());
        }

        if (StringUtils.checkValNotNull(obj.getMsgContent())) {
            where.andNew();
            where.like("msg_content", obj.getMsgContent())
                .or(" create_user_name like {0}", "%" + obj.getCreateUserName() + "%")
                .or("from_user_full_name like {0}", "%" + obj.getCreateUserName() + "%");

        }
        where.orderBy("create_time", false);
        Page<AutMsgOnline> page =
            selectPage(new Page<AutMsgOnline>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutMsgOnline obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public void goToPage(HttpServletResponse response, AutMsgOnline obj) throws Exception {
        response.sendRedirect("http://www.ioaforce.com");

    }

    @Override
    @Transactional
    public RetMsg pushMessageToUserList(AutMsgOnlineRequestVO msgRequest) throws Exception {
        RetMsg retMsg = new RetMsg();
        try {
            String errMsg = "";
            if (null == msgRequest) {
                errMsg = "msgRequest 参数不能为空";
            } else if (msgRequest.getMsgType() == null) {
                errMsg = "MsgType参数不能为空";
            } else if (StringUtils.isEmpty(msgRequest.getMsgContent())) {
                errMsg = "MsgContent参数不能为空";
            } else if (StringUtils.isEmpty(msgRequest.getFromUserName())) {
                errMsg = "FromUserName参数不能为空";
            } else if (msgRequest.getToUserIds() == null || msgRequest.getToUserIds().size() == 0) {
                errMsg = "ToUser参数不能为空";
            }
            if (!"".equals(errMsg)) {
                retMsg = RetMsg.getFailRetMsg();
                retMsg.setMessage(errMsg);
                return retMsg;
            }
            // 入库对象
            List<AutMsgOnline> listOnline = new ArrayList<AutMsgOnline>();
            // 封装发送对象
            List<String> userNames = new ArrayList<String>();
            String idString = org.apache.commons.lang.StringUtils.join(msgRequest.getToUserIds(), ",");
            Where<AutUser> where = new Where<AutUser>();
            where.setSqlSelect("id,user_name");
            where.in("id", idString);
            List<AutUser> users = autUserService.selectList(where);

            for (AutUser user : users) {
                AutMsgOnline msgLine = new AutMsgOnline();
                msgLine.setFromUserFullName(msgRequest.getFromUserFullName());
                msgLine.setFromUserId(msgRequest.getFromUserId());
                msgLine.setFromUserName(msgRequest.getFromUserName());
                msgLine.setReceiveUserId(user.getId());
                msgLine.setReceiveUserName(user.getUserName());
                msgLine.setCreateUserId(msgRequest.getFromUserId());
                msgLine.setCreateUserName(msgRequest.getFromUserName());
                msgLine.setIsActive(1);
                msgLine.setIsRead(0);
                msgLine.setMsgType(msgRequest.getMsgType());
                msgLine.setMsgContent(msgRequest.getMsgContent());
                if (StringUtils.isNotEmpty(msgRequest.getGoUrl())) {
                    msgLine.setGoUrl(msgRequest.getGoUrl());
                }
                if (StringUtils.isNotEmpty(msgRequest.getBusinessKey())) {
                    msgLine.setBusinessKey(msgRequest.getBusinessKey());
                }
                listOnline.add(msgLine);
                userNames.add(user.getUserName());
            }

            // 往在线消息表里新增记录
            if(listOnline.size() >= 1){
                if (listOnline.size() == 1) {
                    autMsgOnlineService.insert(listOnline.get(0));
                } else {
                    autMsgOnlineService.insertBatch(listOnline);
                }
            }
            // 推送消息
            for (String username : userNames) {
                for (AutMsgOnline msg : listOnline) {
                    if (msg.getReceiveUserName().equals(username)) {
                        WebSocketMsg webMsg = new WebSocketMsg();
                        webMsg.setCode(msgRequest.getMsgType());
                        webMsg.setObject(msg);
                        webMsg.setMessage(msgRequest.getMsgContent());
                        webSocketService.pushMessageToUser(webMsg, username);
                    }
                }
            }

            // ######################################################################
            // 数据统计表数据更新
            retMsg = RetMsg.getSuccessRetMsg();
        } catch (Exception e) {
            log.error("消息推送失败", e);
            retMsg = RetMsg.getFailRetMsg();
        }

        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg deleteMsgList(String autMsgOnlineId, Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        try {
            // 把多个userId分割，放进receiveUserIdList
            List<String> receiveUserIdList = Arrays.asList(autMsgOnlineId.split(";"));
            List<AutMsgOnline> msgList = selectBatchIds(receiveUserIdList);
            Integer count = 0;
            if (msgList != null && !msgList.isEmpty()) {
                for (AutMsgOnline msg : msgList) {
                    if (msg.getIsRead() == 0) {
                        count += 1;
                    }
                }
            }
            if (count > 0) {
                AutDataStatistics autData = new AutDataStatistics();
                autData.setObjectCount(count);
                autData.setBusinessKey(String.valueOf(userId));
                autData.setObjectKey(WebConstant.AUTDATA_ONLINE_CODE);
                autDataStatisticsService.minus(autData);
            }
            // 批量删除
            deleteBatchIds(receiveUserIdList);
            retMsg = RetMsg.getSuccessRetMsg();
        } catch (Exception e) {
            log.error("删除错误", e);
            retMsg = RetMsg.getFailRetMsg("操作失败");
        }
        return retMsg;
    }

    @Override
    public void updateIsRead(AutMsgOnline obj) throws Exception {
        if (obj != null && obj.getBusinessKey() != null) {
            AutMsgOnline autMsgOnline = new AutMsgOnline();
            Where<AutMsgOnline> where = new Where<AutMsgOnline>();
            where.eq("business_key", obj.getBusinessKey());
            where.eq("msg_type", WebConstant.ONLINE_MSG_MAIL);
            where.eq("is_read", 0);
            autMsgOnline = selectOne(where);
            if (autMsgOnline != null) {
                autMsgOnline.setIsRead(1);
                updateById(autMsgOnline);
                AutDataStatistics autData = new AutDataStatistics();
                autData.setObjectCount(1);
                autData.setBusinessKey(String.valueOf(obj.getCreateUserId()));
                autData.setObjectKey(WebConstant.AUTDATA_ONLINE_CODE);
                autDataStatisticsService.minus(autData);
            }
        }
    }

    @Override
    @Transactional
    public RetMsg minus(AutDataStatistics obj, String msgId, Long userID) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != msgId) {
            AutMsgOnline msg = new AutMsgOnline();
            msg = autMsgOnlineService.selectById(msgId);
            if (null != msg) {// 未读消息处理
                String url = msg.getGoUrl();
                String signInTime = "";
                Boolean isMe = true;// 签收人是否是自己
                // 查询任务是否签收，签收者是否是当前
                if (url.indexOf("signInTime=&") > -1 && url.indexOf("ioaReceiveFile,") == -1) {
                    url = url.substring(url.indexOf("activityId=") + 11, url.length());
                    url = url.substring(0, url.indexOf("&"));
                    HistoricTaskInstance taskInstance = null;
                    if (url != null && !"".equals(url)) {
                        taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(url).singleResult();
                        if (taskInstance != null) {
                            Date date = taskInstance.getClaimTime();
                            if (date != null && taskInstance.getAssignee() != null) {
                                if (taskInstance.getAssignee().equals(userID.toString())) {
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    signInTime = formatter.format(date);
                                } else {
                                    isMe = false;
                                }
                            }
                        }
                    }
                    url = msg.getGoUrl();
                }
                if (!"".equals(signInTime) && isMe) {
                    url = url.replace("signInTime=&", "signInTime=" + signInTime + "&");
                    msg.setGoUrl(url);
                }
                if (msg.getIsRead() == 1) {
                    autMsgOnlineService.updateById(msg);
                }
                if (msg.getIsRead() == 0) {
                    msg.setIsRead(1);
                    autMsgOnlineService.updateById(msg);
                    // if (null != obj && null != obj.getBusinessKey()) {
                    // obj.setObjectKey(WebConstant.AUTDATA_ONLINE_CODE);
                    // autDataStatisticsService.minus(obj);
                    // }
                }
                Map<String, String> retMap = new HashMap<String, String>();
                if (url.indexOf("openForm") > -1) {
                    // '/'+'mai/maiReceiveBox/maiReceiveBoxPageDetailPage.html?id=944139390194688001&isImportant=0&isRead=0
                    retMap.put("toUrl", msg.getGoUrl());
                } else {
                    // 收文阅件模块===用processInstantce判断当前任务及当前任务办理人
                    if (url.indexOf("ioaReceiveWorkPageDetail") > -1) {
                        String[] goUrls = url.split("&");
                        String taskName = goUrls[1].substring(goUrls[1].indexOf("=") + 1, goUrls[1].length());
                        String linkName = URLDecoder.decode(taskName, "utf-8");
                        String processInstanceId = goUrls[2].substring(goUrls[2].indexOf("=") + 1, goUrls[2].length());
                        List<Task> tskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
                        // Task task = taskService.createTaskQuery().taskId(url).singleResult();
                        if (tskList.size() > 0) {
                            for (Task task : tskList) {
                                if (task.getName().equals(linkName) || task.getName().indexOf(linkName) > -1) {
                                    if (task.getAssignee() == null) {
                                        if (task.getName() != null) {
                                            retMap.put("taskName", task.getName());
                                            retMap.put("toUrl", "/" + msg.getGoUrl());
                                            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
                                            retMsg.setObject(retMap);
                                            return retMsg;
                                        }
                                    }
                                    if (task.getAssignee().equals(String.valueOf(userID))) {
                                        retMsg.setMessage("操作成功");
                                        if (task.getName() != null) {
                                            retMap.put("taskName", task.getName());
                                            retMap.put("toUrl", "/" + msg.getGoUrl());
                                            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
                                            retMsg.setObject(retMap);
                                            return retMsg;
                                        }
                                    }
                                }
                            }
                            retMsg.setObject(retMap);
                            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                            retMsg.setMessage("该工作已被完成或者撤回！");
                            return retMsg;
                        } else {
                            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                            retMsg.setMessage("该工作已被完成或者撤回！");
                            return retMsg;
                        }
                    } else {
                        retMap.put("toUrl", "/" + msg.getGoUrl());
                    }
                }

                // 判断是否是协同办公消息。
                if (url.indexOf("openForm") > -1) {
                    url = url.substring(url.indexOf("activityId=") + 11, url.length());
                    url = url.substring(0, url.indexOf("&"));
                    Task task = taskService.createTaskQuery().taskId(url).singleResult();

                    if (task != null) {
                        if (isMe) {
                            retMsg.setMessage("操作成功");
                            if (task.getName() != null) {
                                retMap.put("taskName", task.getName());
                                retMap.put("toUrl", msg.getGoUrl());

                            }
                            retMsg.setCode(0);
                        } else {
                            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                            retMsg.setMessage("该工作已被其他办理人签收！");
                        }
                    } else {
                        retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                        retMsg.setMessage("该工作已被完成或者撤回！");
                    }
                    // taskService.
                } else {
                    retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
                    retMsg.setMessage("");
                }
                retMsg.setObject(retMap);
            } else {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("该工作已被撤回或者结束！");
            }
        } else {
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("在线消息记录为空（id）");
        }

        return retMsg;
    }

    public String checkMessageValid(AutMsgOnlineRequestVO msgRequest) throws Exception {
        String msgBack = "";
        if (null == msgRequest) {
            msgBack = "msgRequest 参数不能为空";
        } else if (msgRequest.getMsgType() == null) {
            msgBack = "MsgType参数不能为空";
        } else if (StringUtils.isEmpty(msgRequest.getMsgContent())) {
            msgBack = "MsgContent参数不能为空";
        } else if (StringUtils.isEmpty(msgRequest.getFromUserName())) {
            msgBack = "FromUserName参数不能为空";
        } else if (msgRequest.getToUserIds() == null || msgRequest.getToUserIds().size() == 0) {
            msgBack = "ToUser参数不能为空";
        }
        return msgBack;
    }

    /**
     * 用与一条消息多方发送或者单独发送（不论发送给单人或多人，这条消息是都一样的）
     * 
     * @param msgRequestList
     * @param userId
     * @throws Exception
     */
    public void pushMessageToUserList(AutMsgOnlineRequest msgRequest, List<Long> userIds) throws Exception {
        if (userIds == null || userIds.size() <= 1) {
            Long userId = 0L;
            if (null != userIds && userIds.size() > 0) {
                userId = userIds.get(0);
            } else {
                userId = msgRequest.getToUserId();
            }
            if (0L != userId) {
                AutUser user = autUserService.selectById(userId);
                AutMsgOnline msgLine = new AutMsgOnline();
                msgLine.setFromUserFullName(msgRequest.getFromUserFullName());
                msgLine.setFromUserId(msgRequest.getFromUserId());
                msgLine.setFromUserName(msgRequest.getFromUserName());
                msgLine.setReceiveUserId(msgRequest.getToUserId());
                msgLine.setReceiveUserName(user.getUserName());
                msgLine.setIsActive(1);
                msgLine.setIsRead(0);
                msgLine.setMsgType(msgRequest.getMsgType());
                msgLine.setMsgContent(msgRequest.getMsgContent());
                if (StringUtils.isNotEmpty(msgRequest.getGoUrl())) {
                    msgLine.setGoUrl(msgRequest.getGoUrl());
                }
                if (StringUtils.isNotEmpty(msgRequest.getBusinessKey())) {
                    msgLine.setBusinessKey(msgRequest.getBusinessKey());
                }
                autMsgOnlineService.insert(msgLine);

                WebSocketMsg webMsg = new WebSocketMsg();
                webMsg.setCode(msgRequest.getMsgType());
                webMsg.setObject(msgLine);
                webMsg.setMessage(msgRequest.getMsgContent());
                webSocketService.pushMessageToUser(webMsg, user.getUserName());
            }
        } else {
            // 封装发送对象
            List<String> userNames = new ArrayList<String>();
            Where<AutUser> where = new Where<AutUser>();
            where.setSqlSelect("id,user_name");
            where.in("id", userIds);
            List<AutUser> users = autUserService.selectList(where);
            // 入库对象
            List<AutMsgOnline> listOnline = new ArrayList<AutMsgOnline>();
            for (Long userId : userIds) {
                for(AutUser user : users){
                    if(userId.equals(user.getId())){
                        AutMsgOnline msgLine = new AutMsgOnline();
                        msgLine.setFromUserFullName(msgRequest.getFromUserFullName());
                        msgLine.setFromUserId(msgRequest.getFromUserId());
                        msgLine.setFromUserName(msgRequest.getFromUserName());
                        msgLine.setReceiveUserId(msgRequest.getToUserId());
                        msgLine.setReceiveUserName(user.getUserName());
                        msgLine.setCreateUserId(msgRequest.getFromUserId());
                        msgLine.setCreateUserName(msgRequest.getFromUserName());
                        msgLine.setIsActive(1);
                        msgLine.setIsRead(0);
                        msgLine.setMsgType(msgRequest.getMsgType());
                        msgLine.setMsgContent(msgRequest.getMsgContent());
                        if (StringUtils.isNotEmpty(msgRequest.getGoUrl())) {
                            msgLine.setGoUrl(msgRequest.getGoUrl());
                        }
                        if (StringUtils.isNotEmpty(msgRequest.getBusinessKey())) {
                            msgLine.setBusinessKey(msgRequest.getBusinessKey());
                        }
                        listOnline.add(msgLine);
                        userNames.add(user.getUserName()); 
                    }
                }
            }

            // 往在线消息表里新增记录
            autMsgOnlineService.insertBatch(listOnline);
            // 推送消息
            WebSocketMsg webMsg = new WebSocketMsg();
            webMsg.setCode(msgRequest.getMsgType());
            webMsg.setMessage(msgRequest.getMsgContent());
            webSocketService.pushMessageToUserList(webMsg,userNames);
        }
    }
}
