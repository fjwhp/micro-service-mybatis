package aljoin.sms.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sms.dao.entity.SmsShortMessage;
import aljoin.sms.dao.entity.SmsShortMessageSummary;
import aljoin.sms.dao.mapper.SmsShortMessageMapper;
import aljoin.sms.iservice.SmsShortMessageService;
import aljoin.sms.iservice.SmsShortMessageSummaryService;
import aljoin.sms.pubservice.SmsServiceUntils;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 短信表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Service
public class SmsShortMessageServiceImpl extends ServiceImpl<SmsShortMessageMapper, SmsShortMessage>
    implements SmsShortMessageService {

    private final static Logger logger = LoggerFactory.getLogger(SmsShortMessageServiceImpl.class);

    @Resource
    private SmsShortMessageMapper mapper;

    @Resource
    private AutUserService autUserService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private SmsShortMessageService smsShortMessageService;
    @Resource
    private SmsShortMessageSummaryService smsShortMessageSummaryService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private SysParameterService sysParameterService;

    @Override
    public Page<SmsShortMessage> list(PageBean pageBean, SmsShortMessage obj, Long customUserId, String time1,
        String time2) throws Exception {
        Where<SmsShortMessage> where = new Where<SmsShortMessage>();

        if (StringUtils.isNotEmpty(obj.getTheme())) {
            where.like("theme", obj.getTheme());
            where.or("receiver_name LIKE {0}", "%" + obj.getReceiverName() + "%");
        }
        if (StringUtils.isNotEmpty(time1) && StringUtils.isNotEmpty(time2)) {
            where.andNew();
            where.between("send_time", DateUtil.str2datetime(time1 + " 00:00:00"),
                DateUtil.str2datetime(time2 + " 23:59:59"));
        } else if (StringUtils.isNotEmpty(time1)) {
            where.andNew();
            where.ge("send_time", DateUtil.str2datetime(time1 + " 00:00:00"));
        } else if (StringUtils.isNotEmpty(time2)) {
            where.andNew();
            where.le("send_time", DateUtil.str2datetime(time2 + " 23:59:59"));
        }

        // 只能看到自己的短信
        where.andNew();
        where.eq("create_user_id", customUserId);
        where.orderBy("create_time", false);
        Page<SmsShortMessage> page
            = selectPage(new Page<SmsShortMessage>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(SmsShortMessage obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    @Transactional
    public RetMsg add(SmsShortMessage obj, AutUser user) throws Exception {
        RetMsg retMsg = new RetMsg();
        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage("发送成功");
        try {
            String sendSmsUser = obj.getReceiverId();
            if (";".equals(sendSmsUser.substring(sendSmsUser.length(), sendSmsUser.length()))) {
                sendSmsUser = sendSmsUser.substring(0, sendSmsUser.length() - 1);
            }
            String[] ids = sendSmsUser.split(";");

            obj.setSendNumber(ids.length);
            obj.setSendTime(new Date());
            obj.setIsActive(1);
            if (user != null) {
                obj.setCreateUserId(user.getId());
            }
            SysParameter parameter = sysParameterService.selectBykey("is_send_sms");
            String isTrue = "";
            Map<String, String> pubMap = new HashMap<String, String>();
            if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
                isTrue = parameter.getParamValue();
                if ("1".equals(isTrue)) {
                    obj.setSendStatus(1);
                    Where<AutUserPub> pubWhere = new Where<AutUserPub>();
                    pubWhere.in("user_id", ids);
                    pubWhere.setSqlSelect("user_id,phone_number");
                    List<AutUserPub> pubList = autUserPubService.selectList(pubWhere);
                    if (pubList != null && pubList.size() > 0) {
                        for (AutUserPub autUserPub : pubList) {
                            pubMap.put(autUserPub.getUserId().toString(), autUserPub.getPhoneNumber());
                        }
                        pubList.clear();
                    }
                    String sendNo = "";
                    for (String string : ids) {
                        // sendIds+=string
                        if (pubMap.containsKey(string)) {
                            sendNo += pubMap.get(string) + ",";
                        }
                    }
                    if (sendNo.length() > 0) {
                        sendNo = sendNo.substring(0, sendNo.length() - 1);
                        retMsg = SmsServiceUntils.sendSmMT(sendNo.split(","), obj.getContent());
                    }
                } else {
                    obj.setSendStatus(0);
                }
            }

            if (retMsg != null && retMsg.getCode() != null && retMsg.getCode() == 0) {
                obj.setSendStatus(1);
            }
            if (insert(obj) && "1".equals(isTrue)) {
                Where<AutUser> autWhere = new Where<AutUser>();
                autWhere.in("id", ids);
                autWhere.setSqlSelect("id,full_name");
                List<AutUser> autList = autUserService.selectList(autWhere);
                Map<Long, String> autMap = new HashMap<Long, String>();
                if (autList != null && autList.size() > 0) {
                    for (AutUser autUser : autList) {
                        autMap.put(autUser.getId(), autUser.getFullName());
                    }
                    autList.clear();
                }
                List<SmsShortMessageSummary> smsList = new ArrayList<SmsShortMessageSummary>();
                for (String string : ids) {
                    SmsShortMessageSummary sumUser = new SmsShortMessageSummary();
                    sumUser.setContent(obj.getContent());
                    sumUser.setTheme(obj.getTheme());
                    // sumUser.setSendNumber(1);
                    sumUser.setSendTime(new Date());
                    sumUser.setIsActive(1);
                    sumUser.setIsDelete(0);
                    sumUser.setReceiverId(string);
                    String name = autMap.get(Long.valueOf(string));
                    sumUser.setReceiverName(name);
                    sumUser.setRemark(obj.getId().toString());
                    if (pubMap.containsKey(string)) {
                        if (pubMap.get(string) != null && !"".equals(pubMap.get(string))) {
                            sumUser.setSendNumber(pubMap.get(string));
                            sumUser.setSendStatus(1);
                        } else {
                            sumUser.setSendStatus(0);
                        }
                    } else {
                        sumUser.setSendStatus(0);
                    }
                    smsList.add(sumUser);
                }
                if (smsList.size() > 0) {
                    smsShortMessageSummaryService.insertBatch(smsList);
                }
            }

        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg sendSms(SmsShortMessage obj, String userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (obj != null) {
            obj.setId(null);
            if (obj.getContent() == null && "".equals(obj.getContent())) {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("短信内容为空!");
                return retMsg;
            }
            if (obj.getReceiverId() == null && "".equals(obj.getReceiverId())) {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("短信收件人为空!");
                return retMsg;
            }
            if (obj.getTheme() == null && "".equals(obj.getTheme())) {
                retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                retMsg.setMessage("短信主题不能为空!");
                return retMsg;
            }
            String reIds = obj.getReceiverId();
            if (";".equals(reIds.substring(reIds.length(), reIds.length()))) {
                reIds = reIds.substring(0, reIds.length() - 1);
            }
            String[] resid = reIds.split(";");
            obj.setSendNumber(resid.length);
            obj.setIsActive(1);
            obj.setSendTime(new Date());
            obj.setSendStatus(1);
            SysParameter parameter = sysParameterService.selectBykey("is_send_sms");
            String isTrue = "";
            if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
                isTrue = parameter.getParamValue();
                if ("1".equals(isTrue)) {
                    obj.setSendStatus(1);
                } else {
                    obj.setSendStatus(0);
                }
            }

            if (smsShortMessageService.insert(obj) && "1".equals(isTrue)) {
                // 获取用户电话号码
                Where<AutUser> autWhere = new Where<AutUser>();
                autWhere.in("id", resid);
                autWhere.setSqlSelect("id,full_name");
                List<AutUser> autList = autUserService.selectList(autWhere);
                Map<Long, String> autMap = new HashMap<Long, String>();
                if (autList != null && autList.size() > 0) {
                    for (AutUser autUser : autList) {
                        autMap.put(autUser.getId(), autUser.getFullName());
                    }
                    autList.clear();
                }
                Where<AutUserPub> pubWhere = new Where<AutUserPub>();
                pubWhere.in("user_id", resid);
                pubWhere.setSqlSelect("user_id,phone_number");
                List<AutUserPub> pubList = autUserPubService.selectList(pubWhere);
                Map<Long, String> pubMap = new HashMap<Long, String>();
                if (pubList != null && pubList.size() > 0) {
                    for (AutUserPub autUserPub : pubList) {
                        pubMap.put(autUserPub.getUserId(), autUserPub.getPhoneNumber());
                    }
                    pubList.clear();
                }
                // 获取用户角色
                Where<AutUserRole> roleWhere = new Where<AutUserRole>();
                roleWhere.in("user_id", resid);
                roleWhere.setSqlSelect("user_id,role_name");
                roleWhere.eq("is_active", 1);
                List<AutUserRole> roleList = autUserRoleService.selectList(roleWhere);
                Map<Long, String> roleMap = new HashMap<Long, String>();
                if (roleList != null && roleList.size() > 0) {
                    for (AutUserRole autUserRole : roleList) {
                        roleMap.put(autUserRole.getUserId(), autUserRole.getRoleName());
                    }
                    roleList.clear();
                }
                // 获取用户岗位
                Where<AutUserPosition> posWhere = new Where<AutUserPosition>();
                posWhere.in("user_id", resid);
                posWhere.setSqlSelect("user_id,position_id");
                List<AutUserPosition> posList = autUserPositionService.selectList(posWhere);
                Map<Long, String> posMap = new HashMap<Long, String>();
                if (posList != null && posList.size() > 0) {
                    String posNameIds = "";
                    for (AutUserPosition autUserPos : posList) {
                        posNameIds += autUserPos.getPositionId() + ",";
                    }
                    Map<Long, String> postionMap = new HashMap<Long, String>();
                    if (posNameIds.length() > 0) {
                        Where<AutPosition> positionWhere = new Where<AutPosition>();
                        posNameIds.substring(0, posNameIds.length() - 1);
                        positionWhere.in("id", posNameIds);
                        positionWhere.eq("is_active", 1);
                        positionWhere.setSqlSelect("id,position_name");
                        List<AutPosition> postionList = autPositionService.selectList(positionWhere);
                        if (postionList != null && postionList.size() > 0) {
                            for (AutPosition autPosition : postionList) {
                                postionMap.put(autPosition.getId(), autPosition.getPositionName());
                            }
                        }
                    }
                    for (AutUserPosition autUserPos : posList) {
                        String posids = "";
                        if (posMap.containsKey(autUserPos.getUserId())) {
                            posids = posMap.get(autUserPos.getUserId());
                        }
                        if (postionMap.containsKey(autUserPos.getPositionId())) {
                            posids += postionMap.get(autUserPos.getPositionId()) + ";";
                        }
                        posMap.put(autUserPos.getUserId(), posids);
                    }
                }
                List<SmsShortMessageSummary> smsList = new ArrayList<SmsShortMessageSummary>();

                for (String string : resid) {
                    if (string != null && !"".equals(string)) {
                        String content = obj.getContent();
                        Long sendId = Long.valueOf(string);
                        SmsShortMessageSummary sumVo = new SmsShortMessageSummary();
                        sumVo.setIsDelete(0);
                        sumVo.setIsActive(1);
                        sumVo.setReceiverId(string);
                        sumVo.setReceiverName(autMap.get(sendId));
                        sumVo.setTheme(obj.getTheme());
                        sumVo.setSendTime(new Date());
                        if (content.indexOf("$1") > -1) {
                            if (autMap.containsKey(sendId)) {
                                content = content.replaceAll("\\$1", autMap.get(sendId));
                            } else {
                                content = content.replaceAll("\\$1", "");
                            }
                        }
                        if (content.indexOf("$2") > -1) {
                            if (roleMap.containsKey(sendId)) {
                                content = content.replaceAll("\\$2", roleMap.get(sendId));
                            } else {
                                content = content.replaceAll("\\$2", "");
                            }
                        }
                        if (content.indexOf("$3") > -1) {
                            if (roleMap.containsKey(sendId)) {
                                content = content.replaceAll("\\$3", posMap.get(sendId));
                            } else {
                                content = content.replaceAll("\\$3", "");
                            }
                        }
                        sumVo.setContent(content);
                        Boolean isSend = false;
                        if (pubMap.containsKey(sendId)) {
                            String send = pubMap.get(sendId);
                            if (send != null && !"".equals(send)) {
                                sumVo.setSendNumber(send);
                                isSend = true;
                            }
                        }
                        isSend = false;
                        if (isSend) {
                            RetMsg ret = smsShortMessageSummaryService.addOne(sumVo,
                                autUserService.selectById(Long.valueOf(userId)));
                            if (ret.getCode() != 0) {
                                sumVo.setSendStatus(0);
                            } else {
                                sumVo.setSendStatus(1);
                            }
                        } else {
                            sumVo.setSendStatus(0);
                        }
                        smsList.add(sumVo);
                    }
                }
                if (smsList.size() > 0) {
                    smsShortMessageSummaryService.insertBatch(smsList);
                }
            }
            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
            retMsg.setMessage("操作成功");
        } else {
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("无发送数据内容");
        }
        return retMsg;
    }

    @Override
    public RetMsg deleteShortMessageList(String ids) throws Exception {

        RetMsg retMsg = new RetMsg();
        try {
            // 把多个shortMessageId分割，放进shortMessageIdList
            List<String> shortMessageIdList = Arrays.asList(ids.split(";"));
            // 批量删除
            deleteBatchIds(shortMessageIdList);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }

        return retMsg;
    }

    @Override
    @Transactional
    public List<Map<String, String>> sendSms() throws Exception {
        // TODO Auto-generated method stub

        Where<AutUser> userWhere = new Where<AutUser>();
        userWhere.eq("is_active", 1);
        userWhere.setSqlSelect("id,full_name");
        List<AutUser> userList = autUserService.selectList(userWhere);
        Map<String, String> userMap = new HashMap<String, String>();

        for (AutUser autUser : userList) {
            userMap.put(autUser.getId().toString(), autUser.getFullName());
        }
        userList.clear();
        Where<AutUserPub> userPubWhere = new Where<AutUserPub>();
        userPubWhere.eq("is_active", 1);
        userPubWhere.setSqlSelect("user_id,phone_number");
        List<AutUserPub> userPutList = autUserPubService.selectList(userPubWhere);
        Map<String, String> userputMap = new HashMap<String, String>();
        if (userPutList != null && userPutList.size() > 0) {
            for (AutUserPub autUserPub : userPutList) {
                if (autUserPub != null && autUserPub.getPhoneNumber() != null
                    && !"".equals(autUserPub.getPhoneNumber())) {
                    userputMap.put(autUserPub.getUserId().toString(), autUserPub.getPhoneNumber());
                }
            }
        }
        Where<SmsShortMessage> where = new Where<SmsShortMessage>();
        where.eq("send_status", "0");
        List<SmsShortMessage> smsList = selectList(where);
        List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
        if (smsList != null && smsList.size() > 0) {
            for (SmsShortMessage smsShortMessage : smsList) {
                // {$1}{$2}
                String content = smsShortMessage.getContent();
                String receiverids = smsShortMessage.getReceiverId();
                String sendId = smsShortMessage.getCreateUserId().toString();
                if (receiverids.indexOf(";") > -1) {
                    // Map<String, String> sms = new HashMap();
                    String ids[] = receiverids.split(";");
                    for (String string : ids) {
                        if (string != null && !"".equals(string)) {
                            Map<String, String> sms = new HashMap<String, String>();
                            if (userMap.containsKey(receiverids) && userputMap.containsKey(receiverids)
                                && userMap.containsKey(sendId)) {
                                if (content != null && !"".equals(content)) {
                                    if (content.indexOf("{$1}") > -1) {
                                        content = content.replaceAll("{$1}", userMap.get(sendId));
                                    }
                                    if (content.indexOf("{$2}") > -1) {
                                        content = content.replaceAll("{$2}", userMap.get(receiverids));
                                    }
                                }
                                sms.put("phone", userputMap.get(receiverids));
                                sms.put("content", content);
                                returnList.add(sms);
                            }
                        }
                    }

                } else {
                    Map<String, String> sms = new HashMap<String, String>();
                    if (userMap.containsKey(receiverids) && userputMap.containsKey(receiverids)
                        && userMap.containsKey(sendId)) {
                        if (content != null && !"".equals(content)) {
                            if (content.indexOf("{$1}") > -1) {
                                content = content.replaceAll("{$1}", userMap.get(sendId));
                            }
                            if (content.indexOf("{$2}") > -1) {
                                content = content.replaceAll("{$2}", userMap.get(receiverids));
                            }
                        }
                        sms.put("phone", userputMap.get(receiverids));
                        sms.put("content", content);
                        returnList.add(sms);
                    }
                }
            }
        }
        return returnList;
    }

    @SuppressWarnings("unused")
    @Override
    public SmsShortMessage detail(Long id) throws Exception {
        SmsShortMessage sms = selectById(id);
        // 人员排序（收件人）
        List<String> tmpList = new ArrayList<String>();
        String[] ids = sms.getReceiverId().split(";");
        List<Long> receiveIds = new ArrayList<Long>();
        String allId = "";
        for (int i = 0; i < ids.length; i++) {
            String tmp = ids[i].trim();
            receiveIds.add(Long.valueOf(ids[i]));
        }
        Where<AutUser> where1 = new Where<AutUser>();
        where1.in("id", ids);
        where1.setSqlSelect("id", "user_name", "full_name");
        List<AutUser> userList = autUserService.selectList(where1);
        Map<Long, Integer> rankList = autUserService.getUserRankList(receiveIds, null);
        if (rankList.size() > 0) {
            for (Long entry : rankList.keySet()) {
                String usrid = entry.toString();
                allId += usrid + ";";
                for (AutUser autUser : userList) {
                    String tmp = autUser.getId().toString();
                    if (tmp.equals(usrid)) {
                        tmpList.add(autUser.getFullName());
                    }
                }
            }
        }
        String names = "";
        for (String name : tmpList) {
            names += name + ";";
        }
        sms.setReceiverName(names.substring(0, names.length() - 1));
        sms.setReceiverId(allId);
        return sms;
    }
}
