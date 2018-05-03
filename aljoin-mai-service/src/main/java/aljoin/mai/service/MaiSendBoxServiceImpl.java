package aljoin.mai.service;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.object.AutMsgOnlineRequestVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiSendBoxMapper;
import aljoin.mai.dao.object.MaiSendBoxCountDO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.MaiDraftBoxService;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiScrapBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.iservice.ResResourceService;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.ExcelUtil;

/**
 * 
 * 发件箱表(服务实现类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
@Service
public class MaiSendBoxServiceImpl extends ServiceImpl<MaiSendBoxMapper, MaiSendBox> implements MaiSendBoxService {
    @Resource
    private MaiReceiveBoxService maiReceiveBoxService;
    @Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;
    @Resource
    private MaiAttachmentService maiAttachmentService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private MaiScrapBoxService maiScrapBoxService;
    @Resource
    private AutMsgOnlineService autMsgOnlineService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private AutDepartmentService autDepartmentService;
    @Resource
    private AutUserPositionService autUserPositionService;
    @Resource
    private AutPositionService autPositionService;
    @Resource
    private MaiDraftBoxService maiDraftBoxService;
    @Resource
    private SysMsgModuleInfoService sysMsgModuleInfoService;
    @Resource
    private ResResourceService resResourceService;
    /*@Resource
    private SmsShortMessageService smsShortMessageService;*/

    @Override
    public Page<MaiSendBox> list(PageBean pageBean, MaiSendBoxVO obj) throws Exception {
        Where<MaiSendBox> where = new Where<MaiSendBox>();
        if (null != obj) {
            if (null != obj.getMaiSendBox()) {
                where.eq("send_user_id", obj.getMaiSendBox().getCreateUserId());
                where.and();
                where.eq("is_scrap", 0);
                where.and();
                where.eq("is_receipt_mail", 0);
                if (StringUtils.isNotEmpty(obj.getMaiSendBox().getSubjectText())) {
                    where.andNew();
                    where.like("subject_text", obj.getMaiSendBox().getSubjectText());
                    where.or("receive_full_names LIKE {0}", "%" + obj.getMaiSendBox().getSubjectText() + "%");
                }
                if (StringUtils.isNotEmpty(obj.getMaiSendBox().getReceiveFullNames())) {
                    where.like("receive_full_names", obj.getMaiSendBox().getReceiveFullNames());
                }
                if (StringUtils.isNotEmpty(obj.getBegSendTime()) && StringUtils.isEmpty(obj.getEndSendTime())) {
                    where.andNew();
                    where.ge("send_time", DateUtil.str2dateOrTime(obj.getBegSendTime() + " 00:00:00"));
                }
                if (StringUtils.isEmpty(obj.getBegSendTime()) && StringUtils.isNotEmpty(obj.getEndSendTime())) {
                    where.andNew();
                    where.le("send_time", DateUtil.str2dateOrTime(obj.getEndSendTime() + " 23:59:59"));
                }
                if (StringUtils.isNotEmpty(obj.getBegSendTime()) && StringUtils.isNotEmpty(obj.getEndSendTime())) {
                    where.andNew();
                    where.between("send_time", DateUtil.str2dateOrTime(obj.getBegSendTime() + " 00:00:00"),
                        DateUtil.str2dateOrTime(obj.getEndSendTime() + " 23:59:59"));
                }
                if (StringUtils.isNotEmpty(obj.getSendTimeSort()) && StringUtils.isEmpty(obj.getMaiSizeSort())
                    && StringUtils.isEmpty(obj.getReceviceUserSort())) {
                    boolean isAsc = false;
                    if ("1".equals(obj.getSendTimeSort())) {
                        isAsc = true;
                    } else {
                        isAsc = false;
                    }
                    where.orderBy("send_time", isAsc);
                }
                if (StringUtils.isEmpty(obj.getSendTimeSort()) && StringUtils.isNotEmpty(obj.getMaiSizeSort())
                    && StringUtils.isEmpty(obj.getReceviceUserSort())) {
                    boolean isAsc = false;
                    if ("1".equals(obj.getMaiSizeSort())) {
                        isAsc = true;
                    } else {
                        isAsc = false;
                    }
                    where.orderBy("mail_size", isAsc);
                }
                if (StringUtils.isEmpty(obj.getSendTimeSort()) && StringUtils.isEmpty(obj.getMaiSizeSort())
                    && StringUtils.isNotEmpty(obj.getReceviceUserSort())) {
                    boolean isAsc = false;
                    if ("1".equals(obj.getReceviceUserSort())) {
                        isAsc = true;
                    } else {
                        isAsc = false;
                    }
                    where.orderBy("receive_user_count", isAsc);
                }
                if (StringUtils.isNotEmpty(obj.getSendTimeSort()) && StringUtils.isNotEmpty(obj.getMaiSizeSort())
                    && StringUtils.isEmpty(obj.getReceviceUserSort())) {
                    if ("1".equals(obj.getSendTimeSort()) && "1".equals(obj.getMaiSizeSort())) {
                        where.orderBy("send_time,mail_size", true);
                    } else if ("1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getMaiSizeSort())) {
                        where.orderBy("send_time", true);
                        where.orderBy("mail_size", false);
                    } else if (!"1".equals(obj.getSendTimeSort()) && "1".equals(obj.getMaiSizeSort())) {
                        where.orderBy("send_time", false);
                        where.orderBy("mail_size", true);
                    } else if (!"1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getMaiSizeSort())) {
                        where.orderBy("send_time", false);
                        where.orderBy("mail_size", false);
                    }
                }
                if (StringUtils.isNotEmpty(obj.getSendTimeSort()) && StringUtils.isEmpty(obj.getMaiSizeSort())
                    && StringUtils.isNotEmpty(obj.getReceviceUserSort())) {
                    if ("1".equals(obj.getSendTimeSort()) && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time,receive_user_count", true);
                    } else if ("1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time", true);
                        where.orderBy("receive_user_count", false);
                    } else if (!"1".equals(obj.getSendTimeSort()) && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time", false);
                        where.orderBy("receive_user_count", true);
                    } else if (!"1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time", false);
                        where.orderBy("receive_user_count", false);
                    }
                }
                if (StringUtils.isEmpty(obj.getSendTimeSort()) && StringUtils.isNotEmpty(obj.getMaiSizeSort())
                    && StringUtils.isNotEmpty(obj.getReceviceUserSort())) {
                    if ("1".equals(obj.getMaiSizeSort()) && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("mail_size,receive_user_count", true);
                    } else if ("1".equals(obj.getMaiSizeSort()) && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("mail_size", true);
                        where.orderBy("receive_user_count", false);
                    } else if (!"1".equals(obj.getMaiSizeSort()) && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("mail_size", false);
                        where.orderBy("receive_user_count", true);
                    } else if (!"1".equals(obj.getMaiSizeSort()) && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("mail_size", false);
                        where.orderBy("receive_user_count", false);
                    }
                }
                if (StringUtils.isNotEmpty(obj.getSendTimeSort()) && StringUtils.isNotEmpty(obj.getMaiSizeSort())
                    && StringUtils.isNotEmpty(obj.getReceviceUserSort())) {
                    if ("1".equals(obj.getSendTimeSort()) && "1".equals(obj.getMaiSizeSort())
                        && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time,mail_size,receive_user_count", true);
                    } else if ("1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getMaiSizeSort())
                        && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time,receive_user_count", true);
                        where.orderBy("mail_size", false);
                    } else if ("1".equals(obj.getSendTimeSort()) && "1".equals(obj.getMaiSizeSort())
                        && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time,mail_size", true);
                        where.orderBy("receive_user_count", false);
                    } else if (!"1".equals(obj.getSendTimeSort()) && "1".equals(obj.getMaiSizeSort())
                        && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("receive_user_count,mail_size", true);
                        where.orderBy("send_time", false);
                    } else if (!"1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getMaiSizeSort())
                        && "1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("receive_user_count", true);
                        where.orderBy("send_time,mail_size", false);
                    } else if ("1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getMaiSizeSort())
                        && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("send_time", true);
                        where.orderBy("receive_user_count,mail_size", false);
                    } else if (!"1".equals(obj.getSendTimeSort()) && "1".equals(obj.getMaiSizeSort())
                        && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("mail_size", true);
                        where.orderBy("send_time,receive_user_count", false);
                    } else if (!"1".equals(obj.getSendTimeSort()) && !"1".equals(obj.getMaiSizeSort())
                        && !"1".equals(obj.getReceviceUserSort())) {
                        where.orderBy("mail_size,send_time,receive_user_count", false);
                    }
                }

                if (StringUtils.isEmpty(obj.getSendTimeSort()) && StringUtils.isEmpty(obj.getMaiSizeSort())
                    && StringUtils.isEmpty(obj.getReceviceUserSort())) {
                    where.orderBy("send_time", false);
                }
            }
        }
        where.setSqlSelect(
            "id,receive_full_names,subject_text,send_time,is_urgent,mail_size,receive_user_count,create_time");
        Page<MaiSendBox> page = selectPage(new Page<MaiSendBox>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    @Transactional
    public RetMsg add(MaiWriteVO obj, AutUser customUser) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            MaiSendBox maiSendBox = obj.getMaiSendBox();
            MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            List<MaiReceiveBox> maiReceiveBoxList = new ArrayList<MaiReceiveBox>();
            List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
            if (null != maiReceiveBox) {
                if (null == maiSendBox) {
                    maiSendBox = new MaiSendBox();
                }

                if (null == maiReceiveBox.getIsRevoke()) {
                    maiReceiveBox.setIsRevoke(0);
                }
                if (null == maiReceiveBoxSearch.getIsScrap()) {
                    maiReceiveBoxSearch.setIsScrap(0);
                }

                if (null == maiReceiveBoxSearch.getIsRead()) {
                    maiReceiveBoxSearch.setIsRead(0);
                }
                if (null == maiReceiveBoxSearch.getIsUrgent()) {
                    maiReceiveBoxSearch.setIsUrgent(0);
                }
                if (null == maiReceiveBoxSearch.getIsImportant()) {
                    maiReceiveBoxSearch.setIsImportant(0);
                }

                Date date = new Date();
                maiReceiveBoxSearch.setSendTime(date);
                maiReceiveBox.setWeekDay(DateUtil.getWeek(date));
                BeanUtils.copyProperties(maiReceiveBox, maiSendBox);
                BeanUtils.copyProperties(maiReceiveBoxSearch, maiSendBox);
                maiSendBox.setIsSendSuccess(1);
                if (null != customUser) {
                    maiSendBox.setSendUserId(customUser.getId());
                    maiSendBox.setSendUserName(customUser.getUserName());
                    maiSendBox.setSendFullName(customUser.getFullName());
                }
                if (null == maiSendBox.getIsReceiveSmsRemind()) {
                    maiSendBox.setIsReceiveSmsRemind(0);
                }
                if (null == maiSendBox.getIsCopySmsRemind()) {
                    maiSendBox.setIsCopySmsRemind(0);
                }
                if (null == maiSendBox.getHasReceiveSmsRemind()) {
                    maiSendBox.setHasReceiveSmsRemind(0);
                }
                if (null == maiSendBox.getHasCopySmsRemind()) {
                    maiSendBox.setHasCopySmsRemind(0);
                }
                if (null == maiSendBox.getIsReceipt()) {
                    maiSendBox.setIsReceipt(0);
                } else {
                    maiSendBox.setIsReceipt(1);
                }
                if (null == maiSendBox.getIsReceiptMail()) {
                    maiSendBox.setIsReceiptMail(0);
                }
                if (null == maiSendBox.getIsCopyOnlineRemind()) {
                    maiSendBox.setIsCopyOnlineRemind(0);
                }
                if (null == maiSendBox.getHasCopyOnlineRemind()) {
                    maiSendBox.setHasCopyOnlineRemind(0);
                }
                if (null == maiSendBox.getHasReceiveOnlineRemind()) {
                    maiSendBox.setHasReceiveOnlineRemind(0);
                }
                if (null == maiSendBox.getIsReceiveOnlineRemind()) {
                    maiSendBox.setIsReceiveOnlineRemind(0);
                }
                if (null == maiSendBox.getHasReceiptReceiveCount()) {
                    maiSendBox.setHasReceiptReceiveCount(0);
                }
                if (null == maiSendBox.getHasReceiptCopyCount()) {
                    maiSendBox.setHasReceiptCopyCount(0);
                }
                String[] receiveUserIds = null;
                String[] receiveUserNames = null;
                String[] receiveFullNames = null;

                if (StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserIds())
                    && StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserNames())
                    && StringUtils.isNotEmpty(maiReceiveBox.getReceiveFullNames())) {
                    if (maiReceiveBox.getReceiveUserIds().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveUserIds().endsWith(";")) {
                            receiveUserIds = maiReceiveBox.getReceiveUserIds()
                                .substring(0, maiReceiveBox.getReceiveUserIds().lastIndexOf(";")).split(";");
                        } else {
                            receiveUserIds = maiReceiveBox.getReceiveUserIds().split(";");
                        }
                    } else {
                        receiveUserIds = new String[1];
                        receiveUserIds[0] = maiReceiveBox.getReceiveUserIds();
                    }
                    if (maiReceiveBox.getReceiveUserNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveUserNames().endsWith(";")) {
                            receiveUserNames = maiReceiveBox.getReceiveUserNames()
                                .substring(0, maiReceiveBox.getReceiveUserNames().lastIndexOf(";")).split(";");
                        } else {
                            receiveUserNames = maiReceiveBox.getReceiveUserNames().split(";");
                        }
                    } else {
                        receiveUserNames = new String[1];
                        receiveUserNames[0] = maiReceiveBox.getReceiveUserNames();
                    }
                    if (maiReceiveBox.getReceiveFullNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveFullNames().endsWith(";")) {
                            receiveFullNames = maiReceiveBox.getReceiveFullNames()
                                .substring(0, maiReceiveBox.getReceiveFullNames().lastIndexOf(";")).split(";");
                        } else {
                            receiveFullNames = maiReceiveBox.getReceiveFullNames().split(";");
                        }
                    } else {
                        receiveFullNames = new String[1];
                        receiveFullNames[0] = maiReceiveBox.getReceiveFullNames();
                    }
                }
                String[] copyUserIds = null;
                String[] copyUserNames = null;
                String[] copyFullNames = null;
                if (StringUtils.isNotEmpty(maiReceiveBox.getCopyUserIds())
                    && StringUtils.isNotEmpty(maiReceiveBox.getCopyUserNames())
                    && StringUtils.isNotEmpty(maiReceiveBox.getCopyFullNames())) {
                    if (maiReceiveBox.getCopyUserIds().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyUserIds().endsWith(";")) {
                            copyUserIds = maiReceiveBox.getCopyUserIds()
                                .substring(0, maiReceiveBox.getCopyUserIds().lastIndexOf(";")).split(";");
                        } else {
                            copyUserIds = maiReceiveBox.getCopyUserIds().split(";");
                        }
                    } else {
                        copyUserIds = new String[1];
                        copyUserIds[0] = maiReceiveBox.getCopyUserIds();
                    }
                    if (maiReceiveBox.getCopyUserNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyUserNames().endsWith(";")) {
                            copyUserNames = maiReceiveBox.getCopyUserNames()
                                .substring(0, maiReceiveBox.getCopyUserNames().lastIndexOf(";")).split(";");
                        } else {
                            copyUserNames = maiReceiveBox.getCopyUserNames().split(";");
                        }
                    } else {
                        copyUserNames = new String[1];
                        copyUserNames[0] = maiReceiveBox.getCopyUserNames();
                    }
                    if (maiReceiveBox.getCopyFullNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyFullNames().endsWith(";")) {
                            copyFullNames = maiReceiveBox.getCopyFullNames()
                                .substring(0, maiReceiveBox.getCopyFullNames().lastIndexOf(";")).split(";");
                        } else {
                            copyFullNames = maiReceiveBox.getCopyFullNames().split(";");
                        }
                    } else {
                        copyFullNames = new String[1];
                        copyFullNames[0] = maiReceiveBox.getCopyFullNames();
                    }
                }
                if (null != receiveUserIds) {
                    maiSendBox.setReceiveUserCount(receiveUserIds.length);
                    insert(maiSendBox);
                    for (int i = 0; i <= (receiveUserIds.length - 1); i++) {
                        MaiReceiveBox receiveBox = new MaiReceiveBox();
                        BeanUtils.copyProperties(maiReceiveBox, receiveBox);
                        MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        
                        receiveBox.setReceiveUserName(receiveUserNames[i]);
                        receiveBox.setSendId(maiSendBox.getId());
                        receiveBox.setReceiveFullName(receiveFullNames[i]);
                        if (null != customUser) {
                            receiveBox.setSendUserId(customUser.getId());
                            receiveBox.setSendUserName(customUser.getUserName());
                            receiveBoxSearch.setSendFullName(customUser.getFullName());
                        }
                        maiReceiveBoxList.add(receiveBox);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(receiveUserIds[i]));
                        receiveBoxSearch.setSendTime(date);
                        maiReceiveBoxSearchList.add(receiveBoxSearch);
                    }
                }
                if (null != copyUserIds) {
                    for (int i = 0; i <= (copyUserIds.length - 1); i++) {
                        MaiReceiveBox receiveBox = new MaiReceiveBox();
                        BeanUtils.copyProperties(maiReceiveBox, receiveBox);
                        MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        
                        receiveBox.setReceiveFullName(copyFullNames[i]);
                        receiveBox.setReceiveUserName(copyUserNames[i]);
                        receiveBox.setSendId(maiSendBox.getId());
                        if (null != customUser) {
                            receiveBox.setSendUserId(customUser.getId());
                            receiveBox.setSendUserName(customUser.getUserName());
                            receiveBoxSearch.setSendFullName(customUser.getFullName());
                        }
                        maiReceiveBoxList.add(receiveBox);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(copyUserIds[i]));
                        receiveBoxSearch.setSendTime(date);
                        maiReceiveBoxSearchList.add(receiveBoxSearch);
                    }
                }

                // 去重
                for (int i = 0; i <= maiReceiveBoxList.size() - 1; i++) {
                    for (int j = maiReceiveBoxList.size() - 1; j > i; j--) {
                        MaiReceiveBoxSearch receiveBox1 = maiReceiveBoxSearchList.get(i);
                        MaiReceiveBoxSearch receiveBox2 = maiReceiveBoxSearchList.get(j);
                        if (null != receiveBox1 && null != receiveBox2) {
                            if ((receiveBox1.getReceiveUserId().equals(receiveBox2.getReceiveUserId())) && (receiveBox1
                                .getReceiveUserId().longValue() == receiveBox1.getReceiveUserId().longValue())) {
                                maiReceiveBoxList.remove(j);
                                maiReceiveBoxSearchList.remove(j);
                            }
                        }
                    }
                }

                if (null != maiReceiveBoxList && !maiReceiveBoxList.isEmpty()) {
                    maiReceiveBoxService.insertBatch(maiReceiveBoxList);
                    for (int i = 0; i < maiReceiveBoxList.size(); i++) {
                        maiReceiveBoxSearchList.get(i).setId(maiReceiveBoxList.get(i).getId());
                    }
                    maiReceiveBoxSearchService.insertBatch(maiReceiveBoxSearchList);
                }

                List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
                if (null != maiAttachmentList && null != maiReceiveBoxList) {
                    if (!maiAttachmentList.isEmpty() && !maiReceiveBoxList.isEmpty()) {
                        for (MaiAttachment attachment : maiAttachmentList) {
                            MaiAttachment maiAttachment = new MaiAttachment();
                            BeanUtils.copyProperties(attachment, maiAttachment);
                            maiAttachment.setSendId(maiSendBox.getId());
                            maiAttachment.setReceiveId(0L);
                            maiAttachment.setDraftId(0L);
                            newMaiAttachmentList.add(maiAttachment);
                        }

                        for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
                            for (MaiAttachment attachment : maiAttachmentList) {
                                MaiAttachment maiAttachment = new MaiAttachment();
                                BeanUtils.copyProperties(attachment, maiAttachment);
                                maiAttachment.setSendId(0L);
                                maiAttachment.setReceiveId(receiveBox.getId());
                                maiAttachment.setDraftId(0L);
                                newMaiAttachmentList.add(maiAttachment);
                            }
                        }
                    }
                }
                if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                    maiAttachmentService.insertBatch(newMaiAttachmentList);
                }
                
            }
            // 删除草稿箱中的该记录
            MaiDraftBox maiDraftBox = obj.getMaiDraftBox();
            if(null != maiDraftBox && null != maiDraftBox.getId()){
                maiDraftBoxService.deleteById(maiDraftBox.getId());
            }
            // ---------------------在线消息调用（start）------------------------------
            List<String> templateList = new ArrayList<String>();
            Map<String, String> list = new HashMap<String, String>();
            if (null != obj.getMaiSendBox()) {
                list.put("recipient", maiSendBox.getReceiveFullNames());
                list.put("refer", maiSendBox.getCopyFullNames());
                list.put("sender", customUser.getFullName());
                list.put("theme", obj.getMaiSendBox().getSubjectText());
                String isUrgent = "";
                if (null != obj.getMaiSendBox().getIsUrgent() && obj.getMaiSendBox().getIsUrgent() != 0) {
                    isUrgent = WebConstant.URGENT;
                }
                list.put("emergency", isUrgent);
                list.put("sendTime", DateUtil.datetime2str(obj.getMaiSendBox().getSendTime()));
                templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
                    WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
            } else if (null != obj.getMaiDraftBox()) {

            } else if (null != obj.getMaiReceiveBox()) {
                list.put("recipient", maiSendBox.getReceiveFullNames());
                list.put("refer", maiSendBox.getCopyFullNames());
                list.put("sender", customUser.getFullName());
                list.put("theme", obj.getMaiReceiveBoxSearch().getSubjectText());
                String isUrgent = "";
                if (null != obj.getMaiReceiveBoxSearch().getIsUrgent() && obj.getMaiReceiveBoxSearch().getIsUrgent() != 0) {
                    isUrgent = WebConstant.URGENT;
                }
                list.put("emergency", isUrgent);
                list.put("sendTime", DateUtil.datetime2str(obj.getMaiReceiveBoxSearch().getSendTime()));
                templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
                    WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
            }

            AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
            // 前端传来的1个或多个userId（以';'号隔开）
            if (null != templateList && !templateList.isEmpty()) {
                requestVO.setMsgContent(templateList.get(0));
            } else {
                throw new Exception("邮件模板消息未设置");
            }

            // 封装推送信息请求信息
            requestVO.setFromUserId(customUser.getId());
            requestVO.setFromUserFullName(customUser.getFullName());
            requestVO.setFromUserName(customUser.getUserName());
            requestVO.setMsgType(WebConstant.ONLINE_MSG_MAIL);
            for (MaiReceiveBoxSearch maiSearch : maiReceiveBoxSearchList) {
                if (maiSearch != null) {
                    if (maiSearch.getReceiveUserId() != null) {
                        requestVO.setBusinessKey(String.valueOf(maiSearch.getId()));
                        requestVO.setGoUrl("mai/maiReceiveBox/maiReceiveBoxPageDetailPage.html?id=" + maiSearch.getId()
                            + "&isImportant=" + maiSearch.getIsImportant() + "&isRead=" + maiSearch.getIsRead());
                        List<String> recId = new ArrayList<String>();
                        recId.add(String.valueOf(maiSearch.getReceiveUserId()));
                        requestVO.setToUserId(recId);
                        autMsgOnlineService.pushMessageToUserList(requestVO);
                        recId.clear();
                    }
                }
            }
            // ---------------------在线消息调用（end）------------------------------
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public MaiSendBoxVO detail(MaiSendBox obj, Long userId, String userName, String nickName) throws Exception {
        MaiSendBoxVO maiSendBoxVO = new MaiSendBoxVO();
        if (null != obj) {
            if (null != obj.getId()) {
                MaiSendBox maiSendBox = selectById(obj.getId());
                if (null != maiSendBox) {
                    // 人员排序（收件人）
                    List<String> tmpList = new ArrayList<String>();
                    String[] ids = maiSendBox.getReceiveUserIds().split(";");
                    List<Long> receiveIds = new ArrayList<Long>();
                    String allId = "";
                    for (int i = 0; i < ids.length; i++) {
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
                    maiSendBox.setReceiveUserIds(allId);
                    maiSendBox.setReceiveFullNames(names);
                    // 人员排序（抄送人）
                    if (maiSendBox.getCopyUserIds() != null && maiSendBox.getCopyUserIds() != "") {
                        List<String> tmpList1 = new ArrayList<String>();
                        String[] ids1 = maiSendBox.getCopyUserIds().split(";");
                        List<Long> receiveIds1 = new ArrayList<Long>();
                        String allId1 = "";
                        for (int i = 0; i < ids1.length; i++) {
                            receiveIds1.add(Long.valueOf(ids1[i]));
                        }
                        Where<AutUser> where2 = new Where<AutUser>();
                        where2.in("id", ids1);
                        where2.setSqlSelect("id", "user_name", "full_name");
                        List<AutUser> userList1 = autUserService.selectList(where2);
                        Map<Long, Integer> rankList1 = autUserService.getUserRankList(receiveIds1, null);
                        if (rankList1.size() > 0) {
                            for (Long entry : rankList1.keySet()) {
                                String usrid = entry.toString();
                                allId1 += usrid + ";";
                                for (AutUser autUser : userList1) {
                                    String tmp = autUser.getId().toString();
                                    if (tmp.equals(usrid)) {
                                        tmpList1.add(autUser.getFullName());
                                    }
                                }
                            }
                        }
                        String names1 = "";
                        for (String name : tmpList1) {
                            names1 += name + ";";
                        }
                        maiSendBox.setCopyUserIds(allId1);
                        maiSendBox.setCopyFullNames(names1);
                    }

                    maiSendBoxVO.setMaiSendBox(maiSendBox);
                    if (null != userId) {
                        maiSendBoxVO.setUserId(userId);
                        maiSendBoxVO.setUserName(userName);
                        maiSendBoxVO.setFullName(nickName);
                    }
                    Where<MaiAttachment> where = new Where<MaiAttachment>();
                    where.eq("send_id", maiSendBox.getId());
                    List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                    if (null != attachmentList && !attachmentList.isEmpty()) {
                        maiSendBoxVO.setMaiAttachmentList(attachmentList);
                    }
                }
            }
        }
        return maiSendBoxVO;
    }

    @Override
    public MaiSendBoxVO appdetail(MaiSendBox obj, AutUser user) throws Exception {
        MaiSendBoxVO maiSendBoxVO = new MaiSendBoxVO();
        if (null != obj) {
            if (null != obj.getId()) {
                MaiSendBox maiSendBox = selectById(obj.getId());
                if (null != maiSendBox) {
                    maiSendBoxVO.setMaiSendBox(maiSendBox);
                    if (null != user) {
                        maiSendBoxVO.setUserId(user.getId());
                        maiSendBoxVO.setUserName(user.getUserName());
                        maiSendBoxVO.setFullName(user.getFullName());
                    }
                    Where<MaiAttachment> where = new Where<MaiAttachment>();
                    where.eq("send_id", maiSendBox.getId());
                    List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                    if (null != attachmentList && !attachmentList.isEmpty()) {
                        maiSendBoxVO.setMaiAttachmentList(attachmentList);
                    }
                }
            }
        }
        return maiSendBoxVO;
    }

    @Override
    @Transactional
    public RetMsg delete(MaiSendBoxVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (null != obj.getIds()) {
                String ids = obj.getIds();
                String[] idArr = null;
                if (ids.indexOf(";") > -1) {
                    if (ids.endsWith(";")) {
                        idArr = ids.substring(0, ids.lastIndexOf(";")).split(";");
                    } else {
                        idArr = ids.split(";");
                    }
                } else {
                    idArr = new String[1];
                    idArr[0] = ids;
                }
                List<Long> idList = new ArrayList<Long>();
                if (null != idArr) {
                    for (String id : idArr) {
                        if (StringUtils.isNotEmpty(id)) {
                            idList.add(Long.parseLong(id));
                        }
                    }
                }
                if (null != idList && !idList.isEmpty()) {
                    Where<MaiSendBox> where = new Where<MaiSendBox>();
                    where.in("id", idList);
                    List<MaiSendBox> maiSendBoxList = selectList(where);
                    List<MaiScrapBox> maiDraftBoxList = new ArrayList<MaiScrapBox>();
                    if (!maiSendBoxList.isEmpty()) {
                        Date date = new Date();
                        for (MaiSendBox sendBox : maiSendBoxList) {
                            if (null != sendBox) {
                                sendBox.setIsScrap(1);
                                sendBox.setScrapTime(date);
                                MaiScrapBox maiScrapBox = new MaiScrapBox();
                                maiScrapBox.setOrgnlId(sendBox.getId());
                                maiScrapBox.setOrgnlType(2);
                                maiScrapBox.setReceiveUserIds(sendBox.getReceiveUserIds());
                                maiScrapBox.setReceiveFullNames(sendBox.getReceiveFullNames());
                                maiScrapBox.setReceiveUserNames(sendBox.getReceiveUserNames());
                                maiScrapBox.setSendUserId(sendBox.getSendUserId());
                                maiScrapBox.setSendFullName(sendBox.getSendFullName());
                                maiScrapBox.setSendUserName(sendBox.getSendUserName());
                                maiScrapBox.setMailSize(sendBox.getMailSize());
                                maiScrapBox.setSubjectText(sendBox.getSubjectText());
                                maiDraftBoxList.add(maiScrapBox);
                            }
                        }
                        maiScrapBoxService.insertBatch(maiDraftBoxList);
                        updateBatchById(maiSendBoxList);
                        // deleteBatchIds(idList);
                    }
                }
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            }
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg revokeAndDelete(MaiSendBox obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (null != obj.getId()) {
                SysParameter parameter = sysParameterService.selectBykey("allow_revoke_time");
                if (null == parameter) {
                    retMsg.setCode(1);
                    retMsg.setMessage("请到[系统管理]菜单下的[系统参数]设置key为[allow_revoke_time]的参数(不包含[]),该参数为:允许邮件撤回并删除的时间(分钟)");
                    return retMsg;
                } else {
                    Where<MaiSendBox> sendBoxWhere = new Where<MaiSendBox>();
                    sendBoxWhere.setSqlSelect(
                        "id,create_time,version,is_delete,send_time,create_user_id,create_user_name,is_revoke,revoke_time,is_scrap,scrap_time");
                    sendBoxWhere.eq("id", obj.getId());
                    MaiSendBox maiSendBox = selectOne(sendBoxWhere);
                    if (null != maiSendBox) {
                        Long diffMinutes = DateUtil.get2DateMinutes(maiSendBox.getSendTime());
                        String value = parameter.getParamValue();
                        if (diffMinutes <= Long.parseLong(value)) {
                            Date date = new Date();
                            maiSendBox.setIsScrap(1);
                            maiSendBox.setScrapTime(date);
                            maiSendBox.setIsRevoke(1);
                            maiSendBox.setRevokeTime(date);
                            updateById(maiSendBox);
                            Where<MaiReceiveBox> where = new Where<MaiReceiveBox>();
                            where.eq("send_id", obj.getId());
                            where.setSqlSelect(
                                "id,create_time,version,is_delete,create_user_id,create_user_name,is_revoke,revoke_time,is_scrap,scrap_time,send_id");
                            List<MaiReceiveBox> receiveBoxList = maiReceiveBoxService.selectList(where);
                            List<Long> receiveBoxIds = new ArrayList<Long>();
                            if (null != receiveBoxList && !receiveBoxList.isEmpty()) {
                                for (MaiReceiveBox receiveBox : receiveBoxList) {
                                    if (null != receiveBox) {
                                        receiveBox.setIsRevoke(1);
                                        receiveBox.setRevokeTime(date);
                                        receiveBox.setScrapTime(date);
                                        receiveBoxIds.add(receiveBox.getId());
                                    }
                                }
                                maiReceiveBoxService.updateBatchById(receiveBoxList);
                                if(receiveBoxIds.size()>0){
                                    List<MaiReceiveBoxSearch> maiReceiveBoxSearchs = maiReceiveBoxSearchService.selectBatchIds(receiveBoxIds);
                                    for (MaiReceiveBoxSearch maiReceiveBoxSearch : maiReceiveBoxSearchs) {
                                        maiReceiveBoxSearch.setIsScrap(1);
                                    }
                                    maiReceiveBoxSearchService.updateBatchById(maiReceiveBoxSearchs);
                                }
                                List<Long> idList = new ArrayList<Long>();
                                List<Long> idList1 = new ArrayList<Long>();
                                for (MaiReceiveBox receiveBox : receiveBoxList) {
                                    if (null != receiveBox && null != receiveBox.getId()) {
                                        idList.add(receiveBox.getId());
                                    }
                                }
                                Where<AutMsgOnline> msgWhere = new Where<AutMsgOnline>();
                                msgWhere.in("business_key", idList);
                                msgWhere.setSqlSelect(
                                    "id,create_time,version,is_delete,create_user_id,create_user_name,msg_type");
                                List<AutMsgOnline> msgList = autMsgOnlineService.selectList(msgWhere);
                                for (AutMsgOnline msg : msgList) {
                                    if (null != msg && null != msg.getId()) {
                                        idList1.add(msg.getId());
                                    }
                                }
                                if (null != idList && !idList.isEmpty()) {
                                    maiReceiveBoxService.deleteBatchIds(idList);
                                }
                                if (null != idList1 && !idList1.isEmpty()) {
                                    autMsgOnlineService.deleteBatchIds(idList1);
                                }
                            }
                            // deleteById(obj.getId());
                            retMsg.setCode(0);
                            retMsg.setMessage("操作成功");
                        } else {
                            retMsg.setCode(1);
                            retMsg.setMessage("只允许在" + parameter.getParamValue() + "分钟之内撤回并删除邮件");
                            return retMsg;
                        }
                    }
                }
            }
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg update(MaiSendBoxVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        MaiSendBox maiSendBox = obj.getMaiSendBox();
        if (null != obj && null != maiSendBox) {
            List<MaiAttachment> newList = new ArrayList<MaiAttachment>();
            if (null != maiSendBox.getId()) {
                List<MaiAttachment> attachmentList = obj.getMaiAttachmentList();
                int size = 0;
                if (null != attachmentList && !attachmentList.isEmpty()) {
                    for (MaiAttachment attachment : attachmentList) {
                        if (null != attachment && null == attachment.getId()) {
                            MaiAttachment maiAttachment = new MaiAttachment();
                            BeanUtils.copyProperties(attachment, maiAttachment);
                            newList.add(maiAttachment);
                            size += attachment.getAttachSize();
                        }
                    }
                }
                MaiSendBox orgnlObj = selectById(maiSendBox.getId());
                if (null != orgnlObj) {
                    MaiSendBox sendBox = new MaiSendBox();
                    BeanUtils.copyProperties(orgnlObj, sendBox);
                    if (null == maiSendBox.getIsRevoke()) {
                        sendBox.setIsRevoke(0);
                    } else {
                        sendBox.setIsRevoke(maiSendBox.getIsRevoke());
                    }
                    if (null == maiSendBox.getIsScrap()) {
                        sendBox.setIsScrap(0);
                    } else {
                        sendBox.setIsScrap(maiSendBox.getIsScrap());
                    }
                    if (null == maiSendBox.getIsUrgent()) {
                        sendBox.setIsUrgent(0);
                    } else {
                        sendBox.setIsUrgent(maiSendBox.getIsUrgent());
                    }
                    if (null == maiSendBox.getIsReceiveSmsRemind()) {
                        sendBox.setIsReceiveSmsRemind(0);
                    } else {
                        sendBox.setIsReceiveSmsRemind(maiSendBox.getIsReceiveSmsRemind());
                    }
                    if (null == maiSendBox.getIsCopySmsRemind()) {
                        sendBox.setIsCopySmsRemind(0);
                    } else {
                        sendBox.setIsCopySmsRemind(maiSendBox.getIsCopySmsRemind());
                    }
                    if (null == maiSendBox.getHasReceiveSmsRemind()) {
                        sendBox.setHasReceiveSmsRemind(0);
                    } else {
                        sendBox.setHasReceiveSmsRemind(maiSendBox.getHasReceiveSmsRemind());
                    }
                    if (null == maiSendBox.getHasCopySmsRemind()) {
                        sendBox.setHasCopySmsRemind(0);
                    } else {
                        sendBox.setHasCopySmsRemind(maiSendBox.getHasCopySmsRemind());
                    }
                    if (null == maiSendBox.getIsReceipt()) {
                        sendBox.setIsReceipt(0);
                    } else {
                        sendBox.setIsReceipt(maiSendBox.getIsReceipt());
                    }
                    if (null == maiSendBox.getIsReceiptMail()) {
                        sendBox.setIsReceiptMail(0);
                    }
                    if (null == maiSendBox.getIsReceiptMail()) {
                        sendBox.setIsReceiptMail(0);
                    } else {
                        sendBox.setIsReceiptMail(maiSendBox.getIsReceiptMail());
                    }
                    if (null == maiSendBox.getHasReceiptReceiveCount()) {
                        sendBox.setHasReceiptReceiveCount(0);
                    } else {
                        sendBox.setHasReceiptReceiveCount(maiSendBox.getHasReceiptReceiveCount());
                    }
                    if (null == maiSendBox.getHasReceiptCopyCount()) {
                        sendBox.setHasReceiptCopyCount(0);
                    } else {
                        sendBox.setHasReceiptCopyCount(maiSendBox.getHasReceiptCopyCount());
                    }

                    sendBox.setSendTime(new Date());

                    if (null != sendBox.getAttachmentCount()) {
                        sendBox.setAttachmentCount(maiSendBox.getAttachmentCount() + orgnlObj.getAttachmentCount());
                    }

                    if (StringUtils.isNotEmpty(maiSendBox.getMailContent())) {
                        sendBox.setMailContent(maiSendBox.getMailContent());
                        int len = maiSendBox.getMailContent().getBytes().length / 1024;
                        sendBox.setMailSize((size + len));
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getReceiveUserIds())) {
                        sendBox.setReceiveUserIds(maiSendBox.getReceiveUserIds());
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getReceiveUserNames())) {
                        sendBox.setReceiveUserNames(maiSendBox.getReceiveUserNames());
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getReceiveFullNames())) {
                        sendBox.setReceiveFullNames(maiSendBox.getReceiveFullNames());
                    }
                    if (null != maiSendBox.getIsReceiveSmsRemind()) {
                        sendBox.setIsReceiveSmsRemind(maiSendBox.getIsReceiveSmsRemind());
                    }
                    if (null != maiSendBox.getIsUrgent()) {
                        sendBox.setIsUrgent(maiSendBox.getIsUrgent());
                    }
                    if (null != maiSendBox.getIsCopySmsRemind()) {
                        sendBox.setIsCopySmsRemind(maiSendBox.getIsCopySmsRemind());
                    }
                    if (null != maiSendBox.getReceiveUserCount()) {
                        sendBox.setReceiveUserCount(maiSendBox.getReceiveUserCount());
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getCopyUserIds())) {
                        sendBox.setCopyUserIds(maiSendBox.getCopyUserIds());
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getCopyUserNames())) {
                        sendBox.setCopyUserNames(maiSendBox.getCopyUserNames());
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getCopyFullNames())) {
                        sendBox.setCopyFullNames(maiSendBox.getCopyFullNames());
                    }
                    if (StringUtils.isNotEmpty(maiSendBox.getSubjectText())) {
                        sendBox.setSubjectText(maiSendBox.getSubjectText());
                    }
                    updateById(sendBox);

                    List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
                    if (null != attachmentList) {
                        if (!attachmentList.isEmpty()) {
                            for (MaiAttachment attachment : attachmentList) {
                                MaiAttachment maiAttachment = new MaiAttachment();
                                BeanUtils.copyProperties(attachment, maiAttachment);
                                maiAttachment.setSendId(sendBox.getId());
                                maiAttachment.setReceiveId(0L);
                                maiAttachment.setDraftId(0L);
                                newMaiAttachmentList.add(maiAttachment);
                            }
                        }
                    }
                    if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                        maiAttachmentService.insertOrUpdateBatch(newMaiAttachmentList);
                    }
                    
                    retMsg.setCode(0);
                    retMsg.setMessage("操作成功");
                }
            }
        }
        return retMsg;
    }

    /*@Override
    public RetMsg upload(MultipartHttpServletRequest request, boolean async, String fileModuleName) throws Exception {
        if (!async) {
            async = true;
        }
        if (null == fileModuleName) {
            fileModuleName = "mai";
        }
        List<MultipartFile> files = request.getFiles("file");

        // 调通用接口，获取参数
        Map<String, String> map = sysParameterService.allowFileType();
        int limitSize = Integer.parseInt(map.get("limitSize"));
        String allowType = map.get("allowType");
        // upload()方法参数要List，所以此处add进List
        String[] typeArr = allowType.split("\\|");
        List<String> allowFileType = new ArrayList<String>();
        for (String s : typeArr) {
            allowFileType.add(s);
        }
        return fastdfsService.upload(files, allowFileType, limitSize, async, fileModuleName);
    }*/

    @Override
    public List<MaiSendBoxCountDO> mailCountList(MaiSendBoxVO obj) throws Exception {
        List<MaiSendBoxCountDO> boxList = new ArrayList<MaiSendBoxCountDO>();
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getThisWeek();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        if ((obj.getBegTime() != "" && "".equals(obj.getBegTime()))
            || (obj.getEndTime() != null && !"".equals(obj.getEndTime()))) {
            startTime = df.parse(obj.getBegTime());
            endTime = df.parse(obj.getEndTime());
            /* cal.setTime(endTime);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            endTime = cal.getTime();*/
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }

        // String showDate=df.format(startTime)+"至"+df.format(endTime);
        List<AutUser> userList = new ArrayList<AutUser>();
        List<AutPosition> positionList = new ArrayList<AutPosition>();
        List<AutUserPosition> positionUserList = new ArrayList<AutUserPosition>();
        Map<String, String> upMap = new HashMap<String, String>();
        Map<String, String> pnMap = new HashMap<String, String>();
        String userids = "";
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            AutDepartment autDepartment = autDepartmentService.selectById(Long.valueOf(obj.getDeptId()));
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            dUserWhere.setSqlSelect("user_id");
            List<AutDepartmentUser> duserlist = autDepartmentUserService.selectList(dUserWhere);
            if (duserlist != null && duserlist.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : duserlist) {
                    userids += autDepartmentUser.getUserId() + ",";
                }
                if (userids.length() > 0) {
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", userids);
                    userWhere.eq("is_active", 1);
                    userWhere.eq("is_account_expired", 0);
                    if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                        userWhere.like("full_name", obj.getFullName());
                    }
                    userWhere.eq("is_account_locked", 0);
                    userWhere.eq("is_credentials_expired", 0);
                    userWhere.setSqlSelect("full_name,id");
                    userList = autUserService.selectList(userWhere);
                    Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                    userPWhere.in("user_id", userids);
                    userPWhere.eq("is_active", 1);
                    userPWhere.setSqlSelect("position_id,user_id");
                    positionUserList = autUserPositionService.selectList(userPWhere);
                }
            } else {
                return boxList;
            }
        } else {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.eq("is_account_expired", 0);
            userWhere.eq("is_account_locked", 0);
            if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                userWhere.like("full_name", obj.getFullName());
            }
            userWhere.eq("is_credentials_expired", 0);
            userWhere.setSqlSelect("full_name,id");
            userList = autUserService.selectList(userWhere);

            if (userList != null && userList.size() > 0) {
                for (AutUser autUser : userList) {
                    userids += autUser.getId() + ",";
                }
                Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                userPWhere.in("user_id", userids);
                userPWhere.eq("is_active", 1);
                userPWhere.setSqlSelect("position_id,user_id");
                positionUserList = autUserPositionService.selectList(userPWhere);
            } else {
                return boxList;
            }
        }
        if (positionUserList != null) {
            String positionIds = "";
            for (AutUserPosition autUserPosition : positionUserList) {
                positionIds += autUserPosition.getPositionId() + ",";
            }
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.in("id", positionIds);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            positionList = autPositionService.selectList(pWhere);
        }
        if (positionList != null && positionList.size() > 0) {
            for (AutPosition autPosition : positionList) {
                pnMap.put(autPosition.getId().toString(), autPosition.getPositionName());
            }
        }
        positionList.clear();
        if (positionUserList != null && positionUserList.size() > 0) {
            for (AutUserPosition autUserPosition : positionUserList) {
                String tmpPosName = "";
                if (upMap.containsKey(autUserPosition.getUserId().toString())) {
                    tmpPosName = upMap.get(autUserPosition.getUserId().toString());

                }
                if (pnMap.containsKey(autUserPosition.getPositionId().toString())) {
                    tmpPosName += pnMap.get(autUserPosition.getPositionId().toString()) + ";";
                }
                upMap.put(autUserPosition.getUserId().toString(), tmpPosName);
            }
        }
        pnMap.clear();
        positionUserList.clear();
        Map<String, MaiSendBoxCountDO> returnMap = new HashMap<String, MaiSendBoxCountDO>();
        // if (upMap != null && upMap.size() > 0) {
        for (AutUser autUser : userList) {
            int no = 1;
            MaiSendBoxCountDO vo = new MaiSendBoxCountDO();
            if (upMap != null && upMap.containsKey(autUser.getId().toString())) {
                vo.setPositionName(upMap.get(autUser.getId().toString()));
            }
            vo.setFullName(autUser.getFullName());
            vo.setReceviceNumber(0);
            vo.setSendNnumber(0);
            vo.setTotalSize(0);
            vo.setNo(no);
            returnMap.put(autUser.getId().toString(), vo);
        }
        // }
        upMap.clear();
        if (userids.length() > 0) {
            Where<MaiSendBox> where = new Where<MaiSendBox>();
            where.in("send_user_id", userids);
            where.eq("is_receipt_mail", 0);
            where.between("send_time", dfs.parse(df.format(startTime) + " 00:00:00"),
                dfs.parse(df.format(endTime) + " 23:59:59"));
            where.setSqlSelect(
                "id,receive_full_names,subject_text,send_user_id,send_time,is_urgent,mail_size,receive_user_ids,receive_user_count,create_time");
            List<MaiSendBox> sendBoxList = selectList(where);

            Where<MaiAttachment> attWhere = new Where<MaiAttachment>();
            // attWhere.in("create_user_id", userids);
            List<Long> sendId = new ArrayList<Long>();
            String sendIds = "";
            if (sendBoxList != null && sendBoxList.size() > 0) {

                for (MaiSendBox maiSendBox : sendBoxList) {
                    if (sendIds.indexOf(maiSendBox.getId().toString()) > -1) {
                    } else {
                        sendIds += maiSendBox.getId() + ",";
                        sendId.add(maiSendBox.getId());
                    }
                }
                attWhere.in("send_id", sendId);
            } else {
                attWhere.eq("send_id", 1231231231232333L);
            }
            // attWhere.isNotNull("send_id");
            attWhere.setSqlSelect("send_id,attach_size");
            List<MaiAttachment> attlist = maiAttachmentService.selectList(attWhere);
            Map<String, Integer> attMap = new HashMap<String, Integer>();
            if (null != attlist && attlist.size() > 0) {
                for (MaiAttachment maiAttachment : attlist) {
                    int tmp = 0;
                    if (attMap.containsKey(maiAttachment.getSendId().toString())) {
                        tmp = attMap.get(maiAttachment.getSendId().toString());
                    }
                    tmp += maiAttachment.getAttachSize();
                    attMap.put(maiAttachment.getSendId().toString(), tmp);
                }
            }
            if (sendBoxList != null && sendBoxList.size() > 0) {
                for (MaiSendBox maiSendBox : sendBoxList) {
                    if (returnMap.containsKey(maiSendBox.getSendUserId().toString())) {
                        MaiSendBoxCountDO contdo = returnMap.get(maiSendBox.getSendUserId().toString());
                        int rno = contdo.getReceviceNumber();
                        int tno = contdo.getTotalSize();
                        int sno = contdo.getSendNnumber() + 1;
                        String rids = maiSendBox.getReceiveUserIds();
                        if (rids != null && rids.length() > 0) {
                            if (rids.indexOf(";") > -1) {
                                rids = rids.substring(0, rids.length() - 1);
                                if (rids.indexOf(";") > -1) {
                                    String sNO[] = rids.split(";");
                                    rno += sNO.length;
                                } else {
                                    rno += 1;
                                }
                            } else {
                                rno += 1;
                            }

                        }
                        if (attMap.containsKey(maiSendBox.getId().toString())) {
                            tno += attMap.get(maiSendBox.getId().toString());
                        }
                        contdo.setReceviceNumber(rno);
                        contdo.setTotalSize(tno);
                        contdo.setSendNnumber(sno);
                        returnMap.put(maiSendBox.getSendUserId().toString(), contdo);
                    }
                }
            }
        }
        if (returnMap != null && returnMap.size() > 0) {
            for (MaiSendBoxCountDO v : returnMap.values()) {
                /// v.setShowDate(showDate);
                boxList.add(v);
            }
        }
        return boxList;
    }

    public void sortList(List<MaiSendBoxCountDO> list, MaiSendBoxVO obj) {
        if (null != obj) {
            String recevieSort = obj.getRecevieSort();
            String attachmentSizeSort = obj.getAttachmentSort();
            String sendSort = obj.getSendSort();
            // 收件总人数降序
            if (StringUtils.isNotEmpty(recevieSort)) {
                if (recevieSort.equals("1")) {
                    Comparator<MaiSendBoxCountDO> sendBoxCountDOComparator = new Comparator<MaiSendBoxCountDO>() {
                        @Override
                        public int compare(MaiSendBoxCountDO o1, MaiSendBoxCountDO o2) {
                            if (o1.getReceviceNumber() != o2.getReceviceNumber()) {
                                return o1.getReceviceNumber() - o2.getReceviceNumber();
                            } else if (o1.getReceviceNumber() != o2.getReceviceNumber()) {
                                return o1.getReceviceNumber() - o2.getReceviceNumber();
                            } else if (o1.getReceviceNumber() != o2.getReceviceNumber()) {
                                return o1.getReceviceNumber() - o2.getReceviceNumber();
                            }
                            return 0;
                        }
                    };
                    Collections.sort(list, sendBoxCountDOComparator);
                } else {
                    // 收件总人数升序
                    Comparator<MaiSendBoxCountDO> sendBoxCountDOComparator = new Comparator<MaiSendBoxCountDO>() {
                        @Override
                        public int compare(MaiSendBoxCountDO o1, MaiSendBoxCountDO o2) {
                            if (o1.getReceviceNumber() != o2.getReceviceNumber()) {
                                return o2.getReceviceNumber() - o1.getReceviceNumber();
                            } else if (o1.getReceviceNumber() != o2.getReceviceNumber()) {
                                return o2.getReceviceNumber() - o1.getReceviceNumber();
                            } else if (o1.getReceviceNumber() != o2.getReceviceNumber()) {
                                return o2.getReceviceNumber() - o1.getReceviceNumber();
                            }
                            return 0;
                        }
                    };
                    Collections.sort(list, sendBoxCountDOComparator);
                }
                // 总附件大小降序
            } else if (StringUtils.isNotEmpty(attachmentSizeSort)) {
                if ("1".equals(attachmentSizeSort)) {
                    Comparator<MaiSendBoxCountDO> sendBoxCountDOComparator = new Comparator<MaiSendBoxCountDO>() {
                        @Override
                        public int compare(MaiSendBoxCountDO o1, MaiSendBoxCountDO o2) {
                            if (o1.getTotalSize() != o2.getTotalSize()) {
                                return o1.getTotalSize() - o2.getTotalSize();
                            } else if (o1.getTotalSize() != o2.getTotalSize()) {
                                return o1.getTotalSize() - o2.getTotalSize();
                            } else if (o1.getTotalSize() != o2.getTotalSize()) {
                                return o1.getTotalSize() - o2.getTotalSize();
                            }
                            return 0;
                        }
                    };
                    Collections.sort(list, sendBoxCountDOComparator);
                } else {
                    // 总附件大小升序
                    Comparator<MaiSendBoxCountDO> sendBoxCountDOComparator = new Comparator<MaiSendBoxCountDO>() {
                        @Override
                        public int compare(MaiSendBoxCountDO o1, MaiSendBoxCountDO o2) {
                            if (o1.getTotalSize() != o2.getTotalSize()) {
                                return o2.getTotalSize() - o1.getTotalSize();
                            } else if (o1.getTotalSize() != o2.getTotalSize()) {
                                return o2.getTotalSize() - o1.getTotalSize();
                            } else if (o1.getTotalSize() != o2.getTotalSize()) {
                                return o2.getTotalSize() - o1.getTotalSize();
                            }
                            return 0;
                        }
                    };
                    Collections.sort(list, sendBoxCountDOComparator);
                }
            } else if (StringUtils.isNotEmpty(sendSort)) {
                if ("1".equals(sendSort)) {
                    Comparator<MaiSendBoxCountDO> sendBoxCountDOComparator = new Comparator<MaiSendBoxCountDO>() {
                        @Override
                        public int compare(MaiSendBoxCountDO o1, MaiSendBoxCountDO o2) {
                            if (o1.getSendNnumber() != o2.getSendNnumber()) {
                                return o1.getSendNnumber() - o2.getSendNnumber();
                            } else if (o1.getSendNnumber() != o2.getSendNnumber()) {
                                return o1.getSendNnumber() - o2.getSendNnumber();
                            } else if (o1.getSendNnumber() != o2.getSendNnumber()) {
                                return o1.getSendNnumber() - o2.getSendNnumber();
                            }
                            return 0;
                        }
                    };
                    Collections.sort(list, sendBoxCountDOComparator);
                } else {
                    // 总附件大小升序
                    Comparator<MaiSendBoxCountDO> sendBoxCountDOComparator = new Comparator<MaiSendBoxCountDO>() {
                        @Override
                        public int compare(MaiSendBoxCountDO o1, MaiSendBoxCountDO o2) {
                            if (o1.getSendNnumber() != o2.getSendNnumber()) {
                                return o2.getSendNnumber() - o1.getSendNnumber();
                            } else if (o1.getSendNnumber() != o2.getSendNnumber()) {
                                return o2.getSendNnumber() - o1.getSendNnumber();
                            } else if (o1.getSendNnumber() != o2.getSendNnumber()) {
                                return o2.getSendNnumber() - o1.getSendNnumber();
                            }
                            return 0;
                        }
                    };
                    Collections.sort(list, sendBoxCountDOComparator);
                }
            }
        }
    }

    @Override
    public void export(HttpServletResponse response, MaiSendBoxVO obj) throws Exception {
        List<MaiSendBoxCountDO> mailCountList = mailCountList(obj);
        if (mailCountList != null && mailCountList.size() > 0) {
            int a = Integer.valueOf(obj.getAttachmentSort());
            int b = Integer.valueOf(obj.getSendSort());
            int c = Integer.valueOf(obj.getRecevieSort());
            boolean flag = true;
            while (flag) {
                MaiSendBoxCountDO tmpDo = mailCountList.get(0);
                for (int i = 0; i < mailCountList.size() - 1; i++) {// 冒泡趟数，n-1趟
                    for (int j = 0; j < mailCountList.size() - i - 1; j++) {
                        if (a > 0) {
                            if (a == 2) {
                                if (mailCountList.get(j + 1).getTotalSize() < mailCountList.get(j).getTotalSize()) {
                                    tmpDo = mailCountList.get(j);
                                    mailCountList.set(j, mailCountList.get(j + 1));
                                    mailCountList.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (mailCountList.get(j + 1).getTotalSize() > mailCountList.get(j).getTotalSize()) {
                                    tmpDo = mailCountList.get(j);
                                    mailCountList.set(j, mailCountList.get(j + 1));
                                    mailCountList.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                        if (b > 0) {
                            if (b == 2) {
                                if (mailCountList.get(j + 1).getSendNnumber() < mailCountList.get(j).getSendNnumber()) {
                                    tmpDo = mailCountList.get(j);
                                    mailCountList.set(j, mailCountList.get(j + 1));
                                    mailCountList.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (mailCountList.get(j + 1).getSendNnumber() > mailCountList.get(j).getSendNnumber()) {
                                    tmpDo = mailCountList.get(j);
                                    mailCountList.set(j, mailCountList.get(j + 1));
                                    mailCountList.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                        if (c > 0) {
                            if (c == 2) {
                                if (mailCountList.get(j + 1).getReceviceNumber() < mailCountList.get(j)
                                    .getReceviceNumber()) {
                                    tmpDo = mailCountList.get(j);
                                    mailCountList.set(j, mailCountList.get(j + 1));
                                    mailCountList.set(j + 1, tmpDo);
                                    flag = true;
                                }
                            } else {
                                if (mailCountList.get(j + 1).getReceviceNumber() > mailCountList.get(j)
                                    .getReceviceNumber()) {
                                    tmpDo = mailCountList.get(j);
                                    mailCountList.set(j, mailCountList.get(j + 1));
                                    mailCountList.set(j + 1, tmpDo);
                                    flag = true;
                                }

                            }
                        }
                    }
                    if (!flag) {
                        break;// 若果没有发生交换，则退出循环
                    }
                }
                flag = false;
            }
        }
        if (null != mailCountList && !mailCountList.isEmpty()) {
            for (int i = 0; i < mailCountList.size(); i++) {
                MaiSendBoxCountDO mai = mailCountList.get(i);
                mai.setNo(i + 1);
                mailCountList.set(i, mai);
            }
            String[][] headers = {{"No", "序号"}, {"FullName", "用户名"}, {"PositionName", "所属岗位"}, {"SendNnumber", "发件数量"},
                {"ReceviceNumber", "收件人总数"}, {"MtotalSize", "总附件大小(MB)"}};
            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
            for (MaiSendBoxCountDO entity : mailCountList) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < headers.length; i++) {
                    MaiSendBoxCountDO schedulingVO = entity;
                    String methodName = "get" + headers[i][0];
                    Class<?> clazz = MaiSendBoxCountDO.class;
                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
                    if (null != method) {
                        map.put(headers[i][0], method.invoke(schedulingVO));
                    }
                }
                dataset.add(map);
            }
            ExcelUtil.exportExcel("邮件统计", headers, dataset, response);
        }
    }

    @Override
    public List<MaiSendBoxCountDO> mailChartList(MaiSendBoxVO obj) throws Exception {
        List<MaiSendBoxCountDO> boxList = new ArrayList<MaiSendBoxCountDO>();
        Date startTime = null;
        Date endTime = null;
        String weets = obj.getThisWeek();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if ((obj.getBegTime() != "" && "".equals(obj.getBegTime()))
            || (obj.getEndTime() != null && !"".equals(obj.getEndTime()))) {
            startTime = df.parse(obj.getBegTime());
            endTime = df.parse(obj.getEndTime());
            /*cal.setTime(endTime);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            endTime = cal.getTime();*/
        } else {
            if ("0".equals(weets)) {
                // 30天
                Date newDate = new Date();
                cal.setTime(df.parse(df.format(newDate)));
                endTime = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startTime = cal.getTime();

            }
            if ("1".equals(weets)) {
                // 本周
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
                startTime = cal.getTime();
                cal.setTime(df.parse(df.format(startTime)));
                startTime = cal.getTime();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                cal.add(Calendar.WEEK_OF_YEAR, 1);
                endTime = cal.getTime();
            }
            if ("2".equals(weets)) {
                // 本月
                String newDate = df.format(new Date());
                String[] newTime = newDate.split("-");
                newDate = newTime[0] + "-" + newTime[1] + "-01";
                cal.setTime(df.parse(newDate));
                // cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.add(Calendar.SECOND, 0);
                startTime = cal.getTime();
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                endTime = cal.getTime();
            }
        }
        /* cal.setTime(endTime);
        cal.add(Calendar.DAY_OF_MONTH, -1);*/
        String showDate = df.format(startTime) + "至" + df.format(endTime.getTime());
        List<AutUser> userList = new ArrayList<AutUser>();
        List<AutPosition> positionList = new ArrayList<AutPosition>();

        List<AutUserPosition> positionUserList = new ArrayList<AutUserPosition>();
        Map<String, String> upMap = new HashMap<String, String>();
        Map<String, String> pnMap = new HashMap<String, String>();
        String userids = "";
        if (obj.getDeptId() != null && !"".equals(obj.getDeptId())) {
            AutDepartment autDepartment = autDepartmentService.selectById(Long.valueOf(obj.getDeptId()));
            Where<AutDepartmentUser> dUserWhere = new Where<AutDepartmentUser>();
            dUserWhere.where("dept_code like {0}", autDepartment.getDeptCode() + "%");
            dUserWhere.setSqlSelect("user_id");
            List<AutDepartmentUser> duserlist = autDepartmentUserService.selectList(dUserWhere);
            if (duserlist != null && duserlist.size() > 0) {
                for (AutDepartmentUser autDepartmentUser : duserlist) {
                    userids += autDepartmentUser.getUserId() + ",";
                }
                if (userids.length() > 0) {
                    Where<AutUser> userWhere = new Where<AutUser>();
                    userWhere.in("id", userids);
                    userWhere.eq("is_active", 1);
                    userWhere.eq("is_account_expired", 0);
                    if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                        userWhere.like("full_name", obj.getFullName());
                    }
                    userWhere.eq("is_account_locked", 0);
                    userWhere.eq("is_credentials_expired", 0);
                    userWhere.setSqlSelect("full_name,id");
                    userList = autUserService.selectList(userWhere);
                    Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                    userPWhere.in("user_id", userids);
                    userPWhere.eq("is_active", 1);
                    userPWhere.setSqlSelect("position_id,user_id");
                    positionUserList = autUserPositionService.selectList(userPWhere);
                }
            } else {
                return boxList;
            }
        } else {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.eq("is_active", 1);
            userWhere.eq("is_account_expired", 0);
            userWhere.eq("is_account_locked", 0);
            if (obj.getFullName() != null && !"".equals(obj.getFullName())) {
                userWhere.like("full_name", obj.getFullName());
            }
            userWhere.eq("is_credentials_expired", 0);
            userWhere.setSqlSelect("full_name,id");
            userList = autUserService.selectList(userWhere);

            if (userList != null && userList.size() > 0) {
                for (AutUser autUser : userList) {
                    userids += autUser.getId() + ",";
                }
                Where<AutUserPosition> userPWhere = new Where<AutUserPosition>();
                userPWhere.in("user_id", userids);
                userPWhere.eq("is_active", 1);
                userPWhere.setSqlSelect("position_id,user_id");
                positionUserList = autUserPositionService.selectList(userPWhere);
            } else {
                return boxList;
            }
        }
        if (positionUserList != null) {
            String positionIds = "";
            for (AutUserPosition autUserPosition : positionUserList) {
                positionIds += autUserPosition.getPositionId() + ",";
            }
            Where<AutPosition> pWhere = new Where<AutPosition>();
            pWhere.in("id", positionIds);
            pWhere.eq("is_active", 1);
            pWhere.setSqlSelect("id,position_name");
            positionList = autPositionService.selectList(pWhere);
        }
        if (positionList != null && positionList.size() > 0) {
            for (AutPosition autPosition : positionList) {
                pnMap.put(autPosition.getId().toString(), autPosition.getPositionName());
            }
        }
        positionList.clear();
        if (positionUserList != null && positionUserList.size() > 0) {
            for (AutUserPosition autUserPosition : positionUserList) {
                String tmpPosName = "";
                if (upMap.containsKey(autUserPosition.getUserId().toString())) {
                    tmpPosName = upMap.get(autUserPosition.getUserId().toString());

                }
                if (pnMap.containsKey(autUserPosition.getPositionId().toString())) {
                    tmpPosName += pnMap.get(autUserPosition.getPositionId().toString()) + ";";
                }
                upMap.put(autUserPosition.getUserId().toString(), tmpPosName);
            }
        }
        pnMap.clear();
        positionUserList.clear();
        Map<String, MaiSendBoxCountDO> returnMap = new HashMap<String, MaiSendBoxCountDO>();
        if (upMap != null && upMap.size() > 0) {
            for (AutUser autUser : userList) {
                int no = 1;
                MaiSendBoxCountDO vo = new MaiSendBoxCountDO();
                vo.setPositionName(upMap.get(autUser.getId().toString()));
                vo.setFullName(autUser.getFullName());
                vo.setReceviceNumber(0);
                vo.setSendNnumber(0);
                vo.setTotalSize(0);
                vo.setNo(no);
                returnMap.put(autUser.getId().toString(), vo);
            }
        }
        upMap.clear();
        if (userids.length() > 0) {
            Where<MaiSendBox> where = new Where<MaiSendBox>();
            where.in("send_user_id", userids);
            where.eq("is_receipt_mail", 0);
            where.between("send_time", startTime, DateUtil.str2datetime(df.format(endTime) + " 23:59:59"));
            where.setSqlSelect(
                "id,receive_full_names,subject_text,send_user_id,send_time,is_urgent,mail_size,receive_user_ids,receive_user_count,create_time");
            List<MaiSendBox> sendBoxList = selectList(where);
            Where<MaiAttachment> attWhere = new Where<MaiAttachment>();
            // attWhere.in("create_user_id", userids);
            attWhere.isNotNull("send_id");
            attWhere.setSqlSelect("send_id,attach_size");
            List<MaiAttachment> attlist = maiAttachmentService.selectList(attWhere);
            Map<String, Integer> attMap = new HashMap<String, Integer>();
            if (null != attlist && attlist.size() > 0) {
                for (MaiAttachment maiAttachment : attlist) {
                    int tmp = 0;
                    if (attMap.containsKey(maiAttachment.getSendId().toString())) {
                        tmp = attMap.get(maiAttachment.getSendId().toString());
                    }
                    tmp += maiAttachment.getAttachSize();
                    attMap.put(maiAttachment.getSendId().toString(), tmp);
                }
            }
            if (sendBoxList != null && sendBoxList.size() > 0) {
                for (MaiSendBox maiSendBox : sendBoxList) {
                    if (returnMap.containsKey(maiSendBox.getSendUserId().toString())) {
                        MaiSendBoxCountDO contdo = returnMap.get(maiSendBox.getSendUserId().toString());
                        int rno = contdo.getReceviceNumber();
                        int tno = contdo.getTotalSize();
                        int sno = contdo.getSendNnumber() + 1;
                        String rids = maiSendBox.getReceiveUserIds();
                        if (rids != null && rids.length() > 0) {
                            if (rids.indexOf(";") > -1) {
                                rids = rids.substring(0, rids.length() - 1);
                                if (rids.indexOf(";") > -1) {
                                    String sNO[] = rids.split(";");
                                    rno += sNO.length;
                                } else {
                                    rno += 1;
                                }
                            } else {
                                rno += 1;
                            }

                        }
                        if (attMap.containsKey(maiSendBox.getId().toString())) {
                            tno += attMap.get(maiSendBox.getId().toString());
                        }
                        contdo.setReceviceNumber(rno);
                        contdo.setTotalSize(tno);
                        contdo.setSendNnumber(sno);

                        returnMap.put(maiSendBox.getSendUserId().toString(), contdo);
                    }
                }
            }
        }
        Map<String, MaiSendBoxCountDO> deptMap = new HashMap<String, MaiSendBoxCountDO>();
        Where<AutDepartment> dWhere = new Where<AutDepartment>();
        dWhere.eq("is_active", 1);
        List<AutDepartment> deptList = autDepartmentService.selectList(dWhere);
        Where<AutDepartmentUser> audWhere = new Where<AutDepartmentUser>();
        audWhere.eq("is_active", 1);
        List<AutDepartmentUser> duserlist = autDepartmentUserService.selectList(audWhere);
        for (AutDepartment autDepartment : deptList) {
            int no = 1;
            MaiSendBoxCountDO vo = new MaiSendBoxCountDO();
            vo.setDeptName(autDepartment.getDeptName());
            vo.setReceviceNumber(0);
            vo.setSendNnumber(0);
            vo.setTotalSize(0);
            vo.setNo(no);
            deptMap.put(autDepartment.getId().toString(), vo);
        }
        deptList.clear();
        if (duserlist != null && duserlist.size() > 0) {
            // String userIds="";
            for (AutDepartmentUser deptUser : duserlist) {
                /*if(userIds.indexOf(deptUser.getUserId().toString())>-1){
                    continue;
                }*/
                // userIds+=deptUser.getUserId().toString()+",";
                if (returnMap.containsKey(deptUser.getUserId().toString())) {
                    if (deptMap.containsKey(deptUser.getDeptId().toString())) {
                        MaiSendBoxCountDO tmpVo = deptMap.get(deptUser.getDeptId().toString());
                        int r = tmpVo.getReceviceNumber();
                        int s = tmpVo.getSendNnumber();
                        int t = tmpVo.getTotalSize();
                        MaiSendBoxCountDO tmpdo = returnMap.get(deptUser.getUserId().toString());
                        r += tmpdo.getReceviceNumber();
                        s += tmpdo.getSendNnumber();
                        t += tmpdo.getTotalSize();
                        tmpVo.setReceviceNumber(r);
                        tmpVo.setSendNnumber(s);
                        tmpVo.setTotalSize(t);
                        deptMap.put(deptUser.getDeptId().toString(), tmpVo);
                    }
                }
            }
            for (MaiSendBoxCountDO v : deptMap.values()) {
                v.setShowDate(showDate);
                boxList.add(v);
            }
        }
        return boxList;
    }

    @Override
    public Page<MaiSendBox> applist(PageBean pageBean, MaiSendBox obj, String id) throws Exception {
        Where<MaiSendBox> where = new Where<MaiSendBox>();
        where.eq("send_user_id", id);
        where.eq("is_scrap", 0);
        where.eq("is_delete", 0);
        where.eq("is_receipt_mail", 0);
        SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (obj.getCreateTime() != null) {
            where.ge("last_update_time", formats.parse(format.format(obj.getCreateTime()) + " 00:00:00"));
        }
        if (obj.getLastUpdateTime() != null) {
            // Calendar c = Calendar.getInstance();
            // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // c.setTime(obj.getLastUpdateTime());
            // c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            where.le("last_update_time", formats.parse(format.format(obj.getLastUpdateTime()) + " 23:59:59"));
        }
        if (obj.getIsUrgent() != null) {
            where.eq("is_urgent", obj.getIsUrgent());
        }
        if (obj.getIsDelete() != null) {
            if (obj.getIsDelete() == 1) {
                where.where("attachment_count > {0}", 0);
            }
        }
        // 搜索条件:主题、收件人、操作时间
        if (StringUtils.isNotEmpty(obj.getSubjectText())) {
            where.and();
            where.like("subject_text", obj.getSubjectText());
        }
        if (StringUtils.isNotEmpty(obj.getSendFullName())) {
            where.like("receive_full_names", obj.getSendFullName());
        }
        where.orderBy("create_time", false);
        Page<MaiSendBox> page = selectPage(new Page<MaiSendBox>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;

    }

    @Override
    @Transactional
    public RetMsg appAddMai(MaiWriteVO obj, AutUser customUser) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            MaiSendBox maiSendBox = obj.getMaiSendBox();
            MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            List<MaiReceiveBox> maiReceiveBoxList = new ArrayList<MaiReceiveBox>();
            List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
            int size = 0;
            if (null != maiReceiveBox) {
                int allowMailSpace = 0;
                int myMailSpace = 0;
                if (StringUtils.isNotEmpty(maiReceiveBoxSearch.getSubjectText())) {
                    if (maiReceiveBoxSearch.getSubjectText().length() > 200) {
                        retMsg.setCode(1);
                        retMsg.setMessage("主题长度不能超过200个字符");
                        return retMsg;
                    }
                } else {
                    retMsg.setCode(1);
                    retMsg.setMessage("标题不能为空");
                    return retMsg;
                }
                if (StringUtils.isEmpty(maiReceiveBox.getReceiveUserIds())) {
                    retMsg.setCode(1);
                    retMsg.setMessage("收件人不能为空");
                    return retMsg;
                }
                // 获得个人或者领导的空间限制大小
                if (null != customUser && null != customUser.getId()) {
                    Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
                    departmentUserWhere.eq("user_id", customUser.getId());
                    departmentUserWhere.setSqlSelect("id,is_leader");
                    List<AutDepartmentUser> departmentUserList
                        = autDepartmentUserService.selectList(departmentUserWhere);
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
                    maiSendBoxWhere.eq("send_user_id", customUser.getId());
                    maiSendBoxWhere.setSqlSelect("id,send_user_id,mail_size");
                    List<MaiSendBox> maiSendBoxList = selectList(maiSendBoxWhere);
                    if (null != maiSendBoxList && !maiSendBoxList.isEmpty()) {
                        for (MaiSendBox sendBox : maiSendBoxList) {
                            if (null != sendBox && null != sendBox.getMailSize()) {
                                myMailSpace += sendBox.getMailSize();
                            }
                        }
                    }
                }
                if (null == maiSendBox) {
                    maiSendBox = new MaiSendBox();
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
                if (null == maiReceiveBox.getIsRevoke()) {
                    maiReceiveBox.setIsRevoke(0);
                }
                if (null == maiReceiveBoxSearch.getIsScrap()) {
                    maiReceiveBoxSearch.setIsScrap(0);
                }

                if (null == maiReceiveBoxSearch.getIsRead()) {
                    maiReceiveBoxSearch.setIsRead(0);
                }
                if (null == maiReceiveBoxSearch.getIsUrgent()) {
                    maiReceiveBoxSearch.setIsUrgent(0);
                }
                if (null == maiReceiveBoxSearch.getIsImportant()) {
                    maiReceiveBoxSearch.setIsImportant(0);
                }
                if (StringUtils.isNotEmpty(maiReceiveBox.getMailContent())) {
                    int len = (((maiReceiveBox.getMailContent().getBytes("utf-8").length) / 1024));
                    // 邮件内容空间校验
                    if (((len + size + myMailSpace) / 1024) > allowMailSpace) {
                        retMsg.setCode(1);
                        retMsg.setMessage("邮件空间超限： 最大限度为(" + allowMailSpace + "MB)");
                        return retMsg;
                    }
                    maiReceiveBox.setMailSize((size + len));
                } else {
                    maiReceiveBox.setMailSize(size);
                }
                Date date = new Date();
                maiReceiveBoxSearch.setSendTime(date);
                maiReceiveBox.setWeekDay(DateUtil.getWeek(date));
                maiReceiveBox.setCreateUserId(customUser.getId());
                maiReceiveBox.setCreateUserName(customUser.getUserName());
                BeanUtils.copyProperties(maiReceiveBox, maiSendBox);
                maiSendBox.setIsSendSuccess(1);
                if (null != customUser) {
                    maiSendBox.setSendUserId(customUser.getId());
                    maiSendBox.setSendUserName(customUser.getUserName());
                    maiSendBox.setSendFullName(customUser.getFullName());
                    maiSendBox.setCreateUserId(customUser.getId());
                    maiSendBox.setCreateUserName(customUser.getUserName());
                    maiSendBox.setLastUpdateUserId(customUser.getId());
                    maiSendBox.setLastUpdateUserName(customUser.getUserName());
                }
                if (null == maiSendBox.getIsReceiveSmsRemind()) {
                    maiSendBox.setIsReceiveSmsRemind(0);
                }
                if (null == maiSendBox.getIsCopySmsRemind()) {
                    maiSendBox.setIsCopySmsRemind(0);
                }
                if (null == maiSendBox.getHasReceiveSmsRemind()) {
                    maiSendBox.setHasReceiveSmsRemind(0);
                }
                if (null == maiSendBox.getHasCopySmsRemind()) {
                    maiSendBox.setHasCopySmsRemind(0);
                }
                if (null == maiSendBox.getIsReceipt()) {
                    maiSendBox.setIsReceipt(0);
                } else {
                    maiSendBox.setIsReceipt(1);
                }
                if (null == maiSendBox.getIsReceiptMail()) {
                    maiSendBox.setIsReceiptMail(0);
                }
                if (null == maiSendBox.getHasReceiptReceiveCount()) {
                    maiSendBox.setHasReceiptReceiveCount(0);
                }
                if (null == maiSendBox.getHasReceiptCopyCount()) {
                    maiSendBox.setHasReceiptCopyCount(0);
                }
                if (null == maiSendBox.getHasReceiptCopyCount()) {
                    maiSendBox.setHasReceiptCopyCount(0);
                }
                maiSendBox.setAttachmentCount(0);
                if (null != maiAttachmentList && maiAttachmentList.size() > 0) {
                    maiSendBox.setAttachmentCount(maiAttachmentList.size());
                } else {
                    maiSendBox.setAttachmentCount(0);
                }
                String[] receiveUserIds = null;
                String[] receiveUserNames = null;
                String[] receiveFullNames = null;

                if (StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserIds())
                    && StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserNames())
                    && StringUtils.isNotEmpty(maiReceiveBox.getReceiveFullNames())) {
                    if (maiReceiveBox.getReceiveUserIds().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveUserIds().endsWith(";")) {
                            receiveUserIds = maiReceiveBox.getReceiveUserIds()
                                .substring(0, maiReceiveBox.getReceiveUserIds().lastIndexOf(";")).split(";");
                        } else {
                            receiveUserIds = maiReceiveBox.getReceiveUserIds().split(";");
                        }
                    } else {
                        receiveUserIds = new String[1];
                        receiveUserIds[0] = maiReceiveBox.getReceiveUserIds();
                    }
                    if (maiReceiveBox.getReceiveUserNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveUserNames().endsWith(";")) {
                            receiveUserNames = maiReceiveBox.getReceiveUserNames()
                                .substring(0, maiReceiveBox.getReceiveUserNames().lastIndexOf(";")).split(";");
                        } else {
                            receiveUserNames = maiReceiveBox.getReceiveUserNames().split(";");
                        }
                    } else {
                        receiveUserNames = new String[1];
                        receiveUserNames[0] = maiReceiveBox.getReceiveUserNames();
                    }
                    if (maiReceiveBox.getReceiveFullNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveFullNames().endsWith(";")) {
                            receiveFullNames = maiReceiveBox.getReceiveFullNames()
                                .substring(0, maiReceiveBox.getReceiveFullNames().lastIndexOf(";")).split(";");
                        } else {
                            receiveFullNames = maiReceiveBox.getReceiveFullNames().split(";");
                        }
                    } else {
                        receiveFullNames = new String[1];
                        receiveFullNames[0] = maiReceiveBox.getReceiveFullNames();
                    }
                }
                String[] copyUserIds = null;
                String[] copyUserNames = null;
                String[] copyFullNames = null;
                if (StringUtils.isNotEmpty(maiReceiveBox.getCopyUserIds())
                    && StringUtils.isNotEmpty(maiReceiveBox.getCopyUserNames())
                    && StringUtils.isNotEmpty(maiReceiveBox.getCopyFullNames())) {
                    if (maiReceiveBox.getCopyUserIds().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyUserIds().endsWith(";")) {
                            copyUserIds = maiReceiveBox.getCopyUserIds()
                                .substring(0, maiReceiveBox.getCopyUserIds().lastIndexOf(";")).split(";");
                        } else {
                            copyUserIds = maiReceiveBox.getCopyUserIds().split(";");
                        }
                    } else {
                        copyUserIds = new String[1];
                        copyUserIds[0] = maiReceiveBox.getCopyUserIds();
                    }
                    if (maiReceiveBox.getCopyUserNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyUserNames().endsWith(";")) {
                            copyUserNames = maiReceiveBox.getCopyUserNames()
                                .substring(0, maiReceiveBox.getCopyUserNames().lastIndexOf(";")).split(";");
                        } else {
                            copyUserNames = maiReceiveBox.getCopyUserNames().split(";");
                        }
                    } else {
                        copyUserNames = new String[1];
                        copyUserNames[0] = maiReceiveBox.getCopyUserNames();
                    }
                    if (maiReceiveBox.getCopyFullNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyFullNames().endsWith(";")) {
                            copyFullNames = maiReceiveBox.getCopyFullNames()
                                .substring(0, maiReceiveBox.getCopyFullNames().lastIndexOf(";")).split(";");
                        } else {
                            copyFullNames = maiReceiveBox.getCopyFullNames().split(";");
                        }
                    } else {
                        copyFullNames = new String[1];
                        copyFullNames[0] = maiReceiveBox.getCopyFullNames();
                    }
                }
                if (null != receiveUserIds) {
                    maiSendBox.setReceiveUserCount(receiveUserIds.length);
                    maiSendBox.setIsCopyOnlineRemind(1);
                    maiSendBox.setHasCopyOnlineRemind(0);
                    maiSendBox.setIsReceiveOnlineRemind(1);
                    maiSendBox.setHasReceiveOnlineRemind(0);
                    maiSendBox.setIsCopySmsRemind(0);
                    maiSendBox.setHasCopySmsRemind(0);
                    maiSendBox.setHasReceiveSmsRemind(0);
                    maiSendBox.setHasReceiveSmsRemind(0);
                    insert(maiSendBox);
                    for (int i = 0; i <= (receiveUserIds.length - 1); i++) {
                        MaiReceiveBox receiveBox = new MaiReceiveBox();
                        BeanUtils.copyProperties(maiReceiveBox, receiveBox);
                        MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(receiveUserIds[i]));

                        if (receiveUserNames[i] == null || "".equals(receiveUserNames[i])) {
                            if (receiveUserIds[i] != null && !"".equals(receiveUserIds[i])) {
                                AutUser user = autUserService.selectById(receiveUserIds[i]);
                                if (user.getUserName() == "" || user.getUserName() == "") {
                                    receiveBox.setReceiveUserName(user.getFullName());
                                } else {
                                    receiveBox.setReceiveUserName(user.getUserName());
                                }
                                receiveBox.setReceiveFullName(user.getFullName() + ";");
                                receiveBox.setReceiveFullNames(user.getFullName() + ";");
                            }
                        } else {
                            receiveBox.setReceiveUserName(receiveUserNames[i]);
                            receiveBox.setReceiveFullName(receiveFullNames[i]);
                        }
                        receiveBox.setSendId(maiSendBox.getId());
                        receiveBoxSearch.setSendTime(date);
                        receiveBoxSearch.setAttachmentCount(maiSendBox.getAttachmentCount());
                        if (null != customUser) {
                            receiveBox.setSendUserId(customUser.getId());
                            receiveBox.setSendUserName(customUser.getUserName());
                            receiveBoxSearch.setSendFullName(customUser.getFullName());
                            receiveBox.setCreateUserId(customUser.getId());
                            receiveBox.setCreateUserName(customUser.getUserName());
                            receiveBox.setLastUpdateUserId(customUser.getId());
                            receiveBox.setLastUpdateUserName(customUser.getUserName());
                        }
                        maiReceiveBoxList.add(receiveBox);
                        maiReceiveBoxSearchList.add(receiveBoxSearch);
                    }
                }
                if (null != copyUserIds) {
                    for (int i = 0; i <= (copyUserIds.length - 1); i++) {
                        MaiReceiveBox receiveBox = new MaiReceiveBox();
                        BeanUtils.copyProperties(maiReceiveBox, receiveBox);
                        MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(copyUserIds[i]));
                        receiveBox.setReceiveUserName(copyUserNames[i]);
                        receiveBox.setReceiveFullName(copyFullNames[i]);
                        receiveBox.setSendId(maiSendBox.getId());
                        receiveBoxSearch.setSendTime(date);
                        if (null != customUser) {
                            receiveBox.setSendUserId(customUser.getId());
                            receiveBox.setSendUserName(customUser.getUserName());
                            receiveBoxSearch.setSendFullName(customUser.getFullName());
                            receiveBox.setCreateUserId(customUser.getId());
                            receiveBox.setCreateUserName(customUser.getUserName());
                            receiveBox.setLastUpdateUserId(customUser.getId());
                            receiveBox.setLastUpdateUserName(customUser.getUserName());
                        }
                        receiveBoxSearch.setAttachmentCount(maiSendBox.getAttachmentCount());
                        maiReceiveBoxList.add(receiveBox);
                        maiReceiveBoxSearchList.add(receiveBoxSearch);
                    }
                }

                // 去重
                for (int i = 0; i <= maiReceiveBoxSearchList.size() - 1; i++) {
                    for (int j = maiReceiveBoxSearchList.size() - 1; j > i; j--) {
                        MaiReceiveBoxSearch receiveBox1 = maiReceiveBoxSearchList.get(i);
                        MaiReceiveBoxSearch receiveBox2 = maiReceiveBoxSearchList.get(j);
                        if (null != receiveBox1 && null != receiveBox2) {
                            if ((receiveBox1.getReceiveUserId().equals(receiveBox2.getReceiveUserId())) && (receiveBox1
                                .getReceiveUserId().longValue() == receiveBox1.getReceiveUserId().longValue())) {
                                maiReceiveBoxList.remove(j);
                                maiReceiveBoxSearchList.remove(j);
                            }
                        }
                    }
                }

                if (null != maiReceiveBoxList && !maiReceiveBoxList.isEmpty()) {
                    maiReceiveBoxService.insertBatch(maiReceiveBoxList);
                }

                List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
                if (null != maiAttachmentList && null != maiReceiveBoxList) {
                    if (!maiAttachmentList.isEmpty() && !maiReceiveBoxList.isEmpty()) {
                        for (MaiAttachment attachment : maiAttachmentList) {
                            MaiAttachment maiAttachment = new MaiAttachment();
                            BeanUtils.copyProperties(attachment, maiAttachment);
                            maiAttachment.setSendId(maiSendBox.getId());
                            maiAttachment.setReceiveId(0L);
                            maiAttachment.setDraftId(0L);
                            maiAttachment.setCreateUserId(customUser.getId());
                            maiAttachment.setCreateUserName(customUser.getUserName());
                            newMaiAttachmentList.add(maiAttachment);
                        }

                        for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
                            for (MaiAttachment attachment : maiAttachmentList) {
                                MaiAttachment maiAttachment = new MaiAttachment();
                                BeanUtils.copyProperties(attachment, maiAttachment);
                                maiAttachment.setSendId(0L);
                                maiAttachment.setReceiveId(receiveBox.getId());
                                maiAttachment.setDraftId(0L);
                                maiAttachment.setCreateUserId(customUser.getId());
                                maiAttachment.setCreateUserName(customUser.getUserName());
                                newMaiAttachmentList.add(maiAttachment);
                            }
                        }
                    }
                }
                if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                    maiAttachmentService.insertBatch(newMaiAttachmentList);
                }
            }

            // ########################################################

            AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();

            // 前端传来的1个或多个userId（以';'号隔开）
            if (null != obj.getMaiSendBox()) {
                requestVO.setMsgContent(WebConstant.ONLINE_MSG_MAIL_TITLE_PRE + obj.getMaiSendBox().getSubjectText());
            } else if (null != obj.getMaiDraftBox()) {
                requestVO.setMsgContent(WebConstant.ONLINE_MSG_MAIL_TITLE_PRE + obj.getMaiDraftBox().getSubjectText());
            } else if (null != obj.getMaiReceiveBox()) {
                requestVO
                    .setMsgContent(WebConstant.ONLINE_MSG_MAIL_TITLE_PRE + obj.getMaiReceiveBoxSearch().getSubjectText());
            }

            // 封装推送信息请求信息
            requestVO.setFromUserId(customUser.getId());
            requestVO.setFromUserFullName(customUser.getFullName());
            requestVO.setFromUserName(customUser.getUserName());
            requestVO.setMsgType(WebConstant.ONLINE_MSG_MAIL);
            for (MaiReceiveBoxSearch mai : maiReceiveBoxSearchList) {
                if (mai != null) {
                    if (mai.getReceiveUserId() != null) {
                        requestVO.setBusinessKey(String.valueOf(mai.getId()));
                        requestVO.setGoUrl("mai/maiReceiveBox/maiReceiveBoxPageDetailPage.html?id=" + mai.getId()
                            + "&isImportant=" + mai.getIsImportant() + "&isRead=" + mai.getIsRead());
                        List<String> recId = new ArrayList<String>();
                        recId.add(String.valueOf(mai.getReceiveUserId()));
                        requestVO.setToUserId(recId);
                        autMsgOnlineService.pushMessageToUserList(requestVO);
                        recId.clear();
                    }
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg aPPrevokeAndDelete(MaiSendBox obj, String usrId, String userName) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (null != obj.getId()) {
                SysParameter parameter = sysParameterService.selectBykey("allow_revoke_time");
                if (null == parameter) {
                    retMsg.setCode(1);
                    retMsg.setMessage("请到[系统管理]菜单下的[系统参数]设置key为[allow_revoke_time]的参数(不包含[]),该参数为:允许邮件撤回并删除的时间(分钟)");
                    return retMsg;
                } else {
                    Where<MaiSendBox> sendBoxWhere = new Where<MaiSendBox>();
                    // sendBoxWhere.setSqlSelect("id,create_time,version,is_delete,create_user_id,create_user_name,is_revoke,revoke_time,is_scrap,scrap_time");
                    sendBoxWhere.eq("id", obj.getId());
                    MaiSendBox maiSendBox = selectOne(sendBoxWhere);
                    if (null != maiSendBox) {
                        Long diffMinutes = DateUtil.get2DateMinutes(maiSendBox.getSendTime());
                        String value = parameter.getParamValue();
                        if (diffMinutes <= Long.valueOf(value)) {
                            Date date = new Date();
                            maiSendBox.setIsScrap(1);
                            maiSendBox.setScrapTime(date);
                            maiSendBox.setIsRevoke(1);
                            maiSendBox.setRevokeTime(date);
                            maiSendBox.setLastUpdateUserName(userName);
                            maiSendBox.setLastUpdateUserId(Long.valueOf(usrId));
                            updateById(maiSendBox);
                            Where<MaiReceiveBox> where = new Where<MaiReceiveBox>();
                            where.eq("send_id", obj.getId());
                            where.setSqlSelect(
                                "id,create_time,version,is_delete,create_user_id,create_user_name,is_revoke,revoke_time,is_scrap,scrap_time,send_id");
                            List<MaiReceiveBox> receiveBoxList = maiReceiveBoxService.selectList(where);
                            List<Long> receiveBoxIds = new ArrayList<Long>();
                            if (null != receiveBoxList && !receiveBoxList.isEmpty()) {
                                for (MaiReceiveBox receiveBox : receiveBoxList) {
                                    if (null != receiveBox) {
                                        receiveBox.setIsRevoke(1);
                                        receiveBox.setRevokeTime(date);
                                        receiveBox.setScrapTime(date);
                                        receiveBox.setLastUpdateUserId(Long.valueOf(usrId));
                                        receiveBox.setLastUpdateUserName(userName);
                                        receiveBoxIds.add(receiveBox.getId());
                                    }
                                }
                                maiReceiveBoxService.updateBatchById(receiveBoxList);
                                if(receiveBoxIds.size()>0){
                                    List<MaiReceiveBoxSearch> maiReceiveBoxSearchs = maiReceiveBoxSearchService.selectBatchIds(receiveBoxIds);
                                    for (MaiReceiveBoxSearch maiReceiveBoxSearch : maiReceiveBoxSearchs) {
                                        maiReceiveBoxSearch.setIsScrap(1);
                                    }
                                    maiReceiveBoxSearchService.updateBatchById(maiReceiveBoxSearchs);
                                }
                                List<Long> idList = new ArrayList<Long>();
                                for (MaiReceiveBox receiveBox : receiveBoxList) {
                                    if (null != receiveBox && null != receiveBox.getId()) {
                                        idList.add(receiveBox.getId());
                                    }
                                }
                                Where<AutMsgOnline> msgWhere = new Where<AutMsgOnline>();
                                msgWhere.in("business_key", idList);
                                msgWhere.setSqlSelect(
                                    "id,create_time,version,is_delete,create_user_id,create_user_name,msg_type");
                                List<AutMsgOnline> msgList = autMsgOnlineService.selectList(msgWhere);
                                List<Long> idList1 = new ArrayList<Long>();
                                for (AutMsgOnline msg : msgList) {
                                    if (null != msg && null != msg.getId()) {
                                        idList1.add(msg.getId());
                                    }
                                }
                                if (null != idList && !idList.isEmpty()) {
                                    maiReceiveBoxService.deleteBatchIds(idList);
                                }
                                if (null != idList1 && !idList1.isEmpty()) {
                                    autMsgOnlineService.deleteBatchIds(idList1);
                                }
                            }

                            // deleteById(obj.getId());
                            retMsg.setCode(0);
                            retMsg.setMessage("操作成功");
                        } else {
                            retMsg.setCode(1);
                            retMsg.setMessage("只允许在" + parameter.getParamValue() + "分钟之内撤回并删除邮件");
                            return retMsg;
                        }
                    }
                }
            }
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg appdelete(MaiSendBoxVO obj, Long userid, String userName) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (null != obj.getIds()) {
                String ids = obj.getIds();
                String[] idArr = null;
                if (ids.indexOf(";") > -1) {
                    if (ids.endsWith(";")) {
                        idArr = ids.substring(0, ids.lastIndexOf(";")).split(";");
                    } else {
                        idArr = ids.split(";");
                    }
                } else {
                    idArr = new String[1];
                    idArr[0] = ids;
                }
                List<Long> idList = new ArrayList<Long>();
                if (null != idArr) {
                    for (String id : idArr) {
                        if (StringUtils.isNotEmpty(id)) {
                            idList.add(Long.parseLong(id));
                        }
                    }
                }
                if (null != idList && !idList.isEmpty()) {
                    Where<MaiSendBox> where = new Where<MaiSendBox>();
                    where.in("id", idList);
                    List<MaiSendBox> maiSendBoxList = selectList(where);
                    List<MaiScrapBox> maiDraftBoxList = new ArrayList<MaiScrapBox>();
                    if (!maiSendBoxList.isEmpty()) {
                        Date date = new Date();
                        for (MaiSendBox sendBox : maiSendBoxList) {
                            if (null != sendBox) {
                                sendBox.setIsScrap(1);
                                sendBox.setScrapTime(date);
                                MaiScrapBox maiScrapBox = new MaiScrapBox();
                                maiScrapBox.setOrgnlId(sendBox.getId());
                                maiScrapBox.setOrgnlType(2);
                                maiScrapBox.setReceiveUserIds(sendBox.getReceiveUserIds());
                                maiScrapBox.setReceiveFullNames(sendBox.getReceiveFullNames());
                                maiScrapBox.setReceiveUserNames(sendBox.getReceiveUserNames());
                                maiScrapBox.setSendUserId(sendBox.getSendUserId());
                                maiScrapBox.setSendFullName(sendBox.getSendFullName());
                                maiScrapBox.setSendUserName(sendBox.getSendUserName());
                                maiScrapBox.setMailSize(sendBox.getMailSize());
                                maiScrapBox.setSubjectText(sendBox.getSubjectText());
                                maiScrapBox.setCreateUserId(userid);
                                maiScrapBox.setCreateUserName(userName);
                                maiDraftBoxList.add(maiScrapBox);
                            }
                        }
                        maiScrapBoxService.insertBatch(maiDraftBoxList);
                        updateBatchById(maiSendBoxList);
                        // deleteBatchIds(idList);
                    }
                }
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            }
        }
        return retMsg;
    }

    @Transactional
    @Override
    public RetMsg add4Push(MaiWriteVO obj, AutUser customUser, List<AutDepartmentUser> departmentUserList,
        List<MaiSendBox> maiSendBoxList) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            MaiSendBox maiSendBox = obj.getMaiSendBox();
            MaiReceiveBox maiReceiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch maiReceiveBoxSearch = obj.getMaiReceiveBoxSearch();
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            List<MaiReceiveBox> maiReceiveBoxList = new ArrayList<MaiReceiveBox>();
            List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
            int size = 0;
            if (null != maiReceiveBox) {
                int allowMailSpace = 0;
                int myMailSpace = 0;
                if (StringUtils.isNotEmpty(maiReceiveBoxSearch.getSubjectText())) {
                    if (maiReceiveBoxSearch.getSubjectText().length() > 200) {
                        retMsg.setCode(1);
                        retMsg.setMessage("主题长度不能超过200个字符");
                        return retMsg;
                    }
                } else {
                    retMsg.setCode(1);
                    retMsg.setMessage("标题不能为空");
                    return retMsg;
                }
                if (StringUtils.isEmpty(maiReceiveBox.getReceiveUserIds())) {
                    retMsg.setCode(1);
                    retMsg.setMessage("收件人不能为空");
                    return retMsg;
                }
                
                // 获得个人或者领导的空间限制大小
                if (null != customUser && null != customUser.getId()) {
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

                    if (null != maiSendBoxList && !maiSendBoxList.isEmpty()) {
                        for (MaiSendBox sendBox : maiSendBoxList) {
                            if (null != sendBox && null != sendBox.getMailSize()) {
                                myMailSpace += sendBox.getMailSize();
                            }
                        }
                    }
                }
                if (null == maiSendBox) {
                    maiSendBox = new MaiSendBox();
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
                if (null == maiReceiveBox.getIsRevoke()) {
                    maiReceiveBox.setIsRevoke(0);
                }
                if (null == maiReceiveBoxSearch.getIsScrap()) {
                    maiReceiveBoxSearch.setIsScrap(0);
                }

                if (null == maiReceiveBoxSearch.getIsRead()) {
                    maiReceiveBoxSearch.setIsRead(0);
                }
                if (null == maiReceiveBoxSearch.getIsUrgent()) {
                    maiReceiveBoxSearch.setIsUrgent(0);
                }
                if (null == maiReceiveBoxSearch.getIsImportant()) {
                    maiReceiveBoxSearch.setIsImportant(0);
                }
                if (StringUtils.isNotEmpty(maiReceiveBox.getMailContent())) {
                    int len = (((maiReceiveBox.getMailContent().getBytes("utf-8").length) / 1024));
                    // 邮件内容空间校验
                    if (((len + size + myMailSpace) / 1024 / 1024) > allowMailSpace) {
                        retMsg.setCode(1);
                        retMsg.setMessage("您的邮件空间超限： 最大限度为(" + allowMailSpace + "MB)");
                        return retMsg;
                    }
                    maiReceiveBox.setMailSize((size + len));
                } else {
                    maiReceiveBox.setMailSize(size);
                }
                Date date = new Date();
                maiReceiveBoxSearch.setSendTime(date);
                maiReceiveBox.setWeekDay(DateUtil.getWeek(date));
                BeanUtils.copyProperties(maiReceiveBox, maiSendBox);
                maiSendBox.setIsSendSuccess(1);
                if (null != customUser) {
                    maiSendBox.setSendUserId(customUser.getId());
                    maiSendBox.setSendUserName(customUser.getUserName());
                    maiSendBox.setSendFullName(customUser.getFullName());
                }
                if (null == maiSendBox.getIsReceiveSmsRemind()) {
                    maiSendBox.setIsReceiveSmsRemind(0);
                }
                if (null == maiSendBox.getIsCopySmsRemind()) {
                    maiSendBox.setIsCopySmsRemind(0);
                }
                if (null == maiSendBox.getHasReceiveSmsRemind()) {
                    maiSendBox.setHasReceiveSmsRemind(0);
                }
                if (null == maiSendBox.getHasCopySmsRemind()) {
                    maiSendBox.setHasCopySmsRemind(0);
                }
                if (null == maiSendBox.getIsReceipt()) {
                    maiSendBox.setIsReceipt(0);
                } else {
                    maiSendBox.setIsReceipt(1);
                }
                if (null == maiSendBox.getIsReceiptMail()) {
                    maiSendBox.setIsReceiptMail(0);
                }
                if (null == maiSendBox.getHasReceiptReceiveCount()) {
                    maiSendBox.setHasReceiptReceiveCount(0);
                }
                if (null == maiSendBox.getHasReceiptCopyCount()) {
                    maiSendBox.setHasReceiptCopyCount(0);
                }
                String[] receiveUserIds = null;
                String[] receiveUserNames = null;
                String[] receiveFullNames = null;

                if (StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserIds())
                    && StringUtils.isNotEmpty(maiReceiveBox.getReceiveUserNames())
                    && StringUtils.isNotEmpty(maiReceiveBox.getReceiveFullNames())) {
                    if (maiReceiveBox.getReceiveUserIds().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveUserIds().endsWith(";")) {
                            receiveUserIds = maiReceiveBox.getReceiveUserIds()
                                .substring(0, maiReceiveBox.getReceiveUserIds().lastIndexOf(";")).split(";");
                        } else {
                            receiveUserIds = maiReceiveBox.getReceiveUserIds().split(";");
                        }
                    } else {
                        receiveUserIds = new String[1];
                        receiveUserIds[0] = maiReceiveBox.getReceiveUserIds();
                    }
                    if (maiReceiveBox.getReceiveUserNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveUserNames().endsWith(";")) {
                            receiveUserNames = maiReceiveBox.getReceiveUserNames()
                                .substring(0, maiReceiveBox.getReceiveUserNames().lastIndexOf(";")).split(";");
                        } else {
                            receiveUserNames = maiReceiveBox.getReceiveUserNames().split(";");
                        }
                    } else {
                        receiveUserNames = new String[1];
                        receiveUserNames[0] = maiReceiveBox.getReceiveUserNames();
                    }
                    if (maiReceiveBox.getReceiveFullNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getReceiveFullNames().endsWith(";")) {
                            receiveFullNames = maiReceiveBox.getReceiveFullNames()
                                .substring(0, maiReceiveBox.getReceiveFullNames().lastIndexOf(";")).split(";");
                        } else {
                            receiveFullNames = maiReceiveBox.getReceiveFullNames().split(";");
                        }
                    } else {
                        receiveFullNames = new String[1];
                        receiveFullNames[0] = maiReceiveBox.getReceiveFullNames();
                    }
                }
                String[] copyUserIds = null;
                String[] copyUserNames = null;
                String[] copyFullNames = null;
                if (StringUtils.isNotEmpty(maiReceiveBox.getCopyUserIds())
                    && StringUtils.isNotEmpty(maiReceiveBox.getCopyUserNames())
                    && StringUtils.isNotEmpty(maiReceiveBox.getCopyFullNames())) {
                    if (maiReceiveBox.getCopyUserIds().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyUserIds().endsWith(";")) {
                            copyUserIds = maiReceiveBox.getCopyUserIds()
                                .substring(0, maiReceiveBox.getCopyUserIds().lastIndexOf(";")).split(";");
                        } else {
                            copyUserIds = maiReceiveBox.getCopyUserIds().split(";");
                        }
                    } else {
                        copyUserIds = new String[1];
                        copyUserIds[0] = maiReceiveBox.getCopyUserIds();
                    }
                    if (maiReceiveBox.getCopyUserNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyUserNames().endsWith(";")) {
                            copyUserNames = maiReceiveBox.getCopyUserNames()
                                .substring(0, maiReceiveBox.getCopyUserNames().lastIndexOf(";")).split(";");
                        } else {
                            copyUserNames = maiReceiveBox.getCopyUserNames().split(";");
                        }
                    } else {
                        copyUserNames = new String[1];
                        copyUserNames[0] = maiReceiveBox.getCopyUserNames();
                    }
                    if (maiReceiveBox.getCopyFullNames().indexOf(";") > -1) {
                        if (maiReceiveBox.getCopyFullNames().endsWith(";")) {
                            copyFullNames = maiReceiveBox.getCopyFullNames()
                                .substring(0, maiReceiveBox.getCopyFullNames().lastIndexOf(";")).split(";");
                        } else {
                            copyFullNames = maiReceiveBox.getCopyFullNames().split(";");
                        }
                    } else {
                        copyFullNames = new String[1];
                        copyFullNames[0] = maiReceiveBox.getCopyFullNames();
                    }
                }
                if (null != receiveUserIds) {
                    maiSendBox.setReceiveUserCount(receiveUserIds.length);
                    for (int i = 0; i <= (receiveUserIds.length - 1); i++) {
                        MaiReceiveBox receiveBox = new MaiReceiveBox();
                        BeanUtils.copyProperties(maiReceiveBox, receiveBox);
                        MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(receiveUserIds[i]));
                        receiveBox.setReceiveUserName(receiveUserNames[i]);
                        receiveBox.setReceiveFullName(receiveFullNames[i]);
                        receiveBox.setSendId((long)-1);
                        receiveBoxSearch.setSendTime(date);
                        if (null != customUser) {
                            receiveBox.setSendUserId(customUser.getId());
                            receiveBox.setSendUserName(customUser.getUserName());
                            receiveBoxSearch.setSendFullName(customUser.getFullName());
                        }
                        maiReceiveBoxList.add(receiveBox);
                        maiReceiveBoxSearchList.add(receiveBoxSearch);
                    }
                }
                if (null != copyUserIds) {
                    for (int i = 0; i <= (copyUserIds.length - 1); i++) {
                        MaiReceiveBox receiveBox = new MaiReceiveBox();
                        BeanUtils.copyProperties(maiReceiveBox, receiveBox);
                        MaiReceiveBoxSearch receiveBoxSearch = new MaiReceiveBoxSearch();
                        BeanUtils.copyProperties(maiReceiveBoxSearch, receiveBoxSearch);
                        receiveBoxSearch.setReceiveUserId(Long.parseLong(copyUserIds[i]));
                        receiveBox.setReceiveUserName(copyUserNames[i]);
                        receiveBox.setReceiveFullName(copyFullNames[i]);
                        receiveBox.setSendId(maiSendBox.getId());
                        receiveBoxSearch.setSendTime(date);
                        if (null != customUser) {
                            receiveBox.setSendUserId(customUser.getId());
                            receiveBox.setSendUserName(customUser.getUserName());
                            receiveBoxSearch.setSendFullName(customUser.getFullName());
                        }
                        maiReceiveBoxList.add(receiveBox);
                        maiReceiveBoxSearchList.add(receiveBoxSearch);
                    }
                }

                // 去重
                for (int i = 0; i <= maiReceiveBoxList.size() - 1; i++) {
                    for (int j = maiReceiveBoxList.size() - 1; j > i; j--) {
                        MaiReceiveBoxSearch receiveBox1 = maiReceiveBoxSearchList.get(i);
                        MaiReceiveBoxSearch receiveBox2 = maiReceiveBoxSearchList.get(j);
                        if (null != receiveBox1 && null != receiveBox2) {
                            if ((receiveBox1.getReceiveUserId().equals(receiveBox2.getReceiveUserId())) && (receiveBox1
                                .getReceiveUserId().longValue() == receiveBox1.getReceiveUserId().longValue())) {
                                maiReceiveBoxList.remove(j);
                                maiReceiveBoxSearchList.remove(j);
                            }
                        }
                    }
                }

                if (null != maiReceiveBoxList && !maiReceiveBoxList.isEmpty()) {
                    maiReceiveBoxService.insertBatch(maiReceiveBoxList);
                }

                List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
                if (null != maiAttachmentList && null != maiReceiveBoxList) {
                    if (!maiAttachmentList.isEmpty() && !maiReceiveBoxList.isEmpty()) {
                        for (MaiAttachment attachment : maiAttachmentList) {
                            MaiAttachment maiAttachment = new MaiAttachment();
                            BeanUtils.copyProperties(attachment, maiAttachment);
                            maiAttachment.setSendId(maiSendBox.getId());
                            maiAttachment.setReceiveId(0L);
                            maiAttachment.setDraftId(0L);
                            newMaiAttachmentList.add(maiAttachment);
                        }

                        for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
                            for (MaiAttachment attachment : maiAttachmentList) {
                                MaiAttachment maiAttachment = new MaiAttachment();
                                BeanUtils.copyProperties(attachment, maiAttachment);
                                maiAttachment.setSendId(0L);
                                maiAttachment.setReceiveId(receiveBox.getId());
                                maiAttachment.setDraftId(0L);
                                newMaiAttachmentList.add(maiAttachment);
                            }
                        }
                    }
                }
                if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                    maiAttachmentService.insertBatch(newMaiAttachmentList);
                }
            }

            // ---------------------在线消息调用（start）------------------------------
            List<String> templateList = new ArrayList<String>();
            Map<String, String> list = new HashMap<String, String>();
            if (null != obj.getMaiSendBox()) {
                list.put("recipient", maiSendBox.getReceiveFullNames());
                list.put("refer", maiSendBox.getCopyFullNames());
                list.put("sender", customUser.getFullName());
                list.put("theme", obj.getMaiSendBox().getSubjectText());
                String isUrgent = "";
                if (null != obj.getMaiSendBox().getIsUrgent() && obj.getMaiSendBox().getIsUrgent() != 0) {
                    isUrgent = WebConstant.URGENT;
                }
                list.put("emergency", isUrgent);
                list.put("sendTime", DateUtil.datetime2str(obj.getMaiSendBox().getSendTime()));
                templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
                    WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
            } else if (null != obj.getMaiDraftBox()) {

            } else if (null != obj.getMaiReceiveBox()) {
                list.put("recipient", maiSendBox.getReceiveFullNames());
                list.put("refer", maiSendBox.getCopyFullNames());
                list.put("sender", customUser.getFullName());
                list.put("theme", obj.getMaiReceiveBoxSearch().getSubjectText());
                String isUrgent = "";
                if (null != obj.getMaiReceiveBoxSearch().getIsUrgent() && obj.getMaiReceiveBoxSearch().getIsUrgent() != 0) {
                    isUrgent = WebConstant.URGENT;
                }
                list.put("emergency", isUrgent);
                list.put("sendTime", DateUtil.datetime2str(obj.getMaiReceiveBoxSearch().getSendTime()));
                templateList = sysMsgModuleInfoService.contextTemplate(list, WebConstant.TEMPLATE_TYPE_MSG,
                    WebConstant.TEMPLATE_MAIL_CODE, WebConstant.TEMPLATE_BEHAVIOR_ZXYJ);
            }
            AutMsgOnlineRequestVO requestVO = new AutMsgOnlineRequestVO();
            // 前端传来的1个或多个userId（以';'号隔开）
            if (null != templateList && !templateList.isEmpty()) {
                requestVO.setMsgContent(templateList.get(0));
            }
            // 封装推送信息请求信息
            requestVO.setFromUserId(customUser.getId());
            requestVO.setFromUserFullName(customUser.getFullName());
            requestVO.setFromUserName(customUser.getUserName());
            requestVO.setMsgType(WebConstant.ONLINE_MSG_MAIL);
            for (MaiReceiveBoxSearch mai : maiReceiveBoxSearchList) {
                if (mai != null) {
                    if (mai.getReceiveUserId() != null) {
                        requestVO.setBusinessKey(String.valueOf(mai.getId()));
                        requestVO.setGoUrl("mai/maiReceiveBox/maiReceiveBoxPageDetailPage.html?id=" + mai.getId()
                            + "&isImportant=" + mai.getIsImportant() + "&isRead=" + mai.getIsRead());
                        List<String> recId = new ArrayList<String>();
                        recId.add(String.valueOf(mai.getReceiveUserId()));
                        requestVO.setToUserId(recId);
                        autMsgOnlineService.pushMessageToUserList(requestVO);
                        recId.clear();
                    }
                }
            }
            // ---------------------在线消息调用（end）------------------------------
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

}