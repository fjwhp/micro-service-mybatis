package aljoin.mai.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiReceiveBoxMapper;
import aljoin.mai.dao.object.MaiReceiveBoxInfo;
import aljoin.mai.dao.object.MaiReceiveBoxListVO;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiScrapBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 收件箱表(服务实现类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
@Service
public class MaiReceiveBoxServiceImpl extends ServiceImpl<MaiReceiveBoxMapper, MaiReceiveBox>
    implements MaiReceiveBoxService {

    @Resource
    private MaiAttachmentService maiAttachmentService;

    @Resource
    private SysParameterService sysParameterService;

    @Resource
    private MaiSendBoxService maiSendBoxService;

    @Resource
    private MaiScrapBoxService maiScrapBoxService;

    @Resource
    private AutMsgOnlineService autMsgOnlineService;

    @Resource
    private AutUserService autUserService;
    
    @Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;

    @Override
    public Page<MaiReceiveBoxListVO> list(PageBean pageBean, MaiReceiveBoxVO obj) throws Exception {
        Page<MaiReceiveBoxListVO> page = new Page<MaiReceiveBoxListVO>();
        Where<MaiReceiveBoxSearch> where = new Where<MaiReceiveBoxSearch>();
        if (null != obj) {
            if (null != obj.getMaiReceiveBox()) {
                Long curUserId = obj.getMaiReceiveBox().getCreateUserId();
                where.eq("is_scrap", 0);
                if (StringUtils.isNotEmpty(obj.getMaiReceiveBoxSearch().getSubjectText())) {
                    where.andNew("subject_text like {0} or send_full_name LIKE {0}", "%" + obj.getMaiReceiveBoxSearch().getSubjectText() + "%");
                }
                if (StringUtils.isNotEmpty(obj.getMaiReceiveBoxSearch().getSendFullName())) {
                    where.like("send_full_name", obj.getMaiReceiveBoxSearch().getSendFullName());
                }
                if (StringUtils.isNotEmpty(obj.getBegSendTime()) && StringUtils.isEmpty(obj.getBegSendTime())) {
                    where.andNew();
                    where.ge("send_time", DateUtil.str2dateOrTime(obj.getBegSendTime()));
                }
                if (StringUtils.isEmpty(obj.getEndSendTime()) && StringUtils.isNotEmpty(obj.getEndSendTime())) {
                    where.andNew();
                    where.le("send_time", DateUtil.str2dateOrTime(obj.getEndSendTime()));
                }
                if (obj.getMaiReceiveBoxSearch().getIsUrgent() != null) {
                    where.andNew();
                    where.eq("is_urgent", obj.getMaiReceiveBoxSearch().getIsUrgent());
                }
                if (obj.getMaiReceiveBoxSearch().getIsImportant() != null) {
                    where.andNew();
                    where.eq("is_important", obj.getMaiReceiveBoxSearch().getIsImportant());
                }
                if (StringUtils.isNotEmpty(obj.getBegSendTime()) && StringUtils.isNotEmpty(obj.getEndSendTime())) {
                    where.andNew();
                    where.between("send_time", DateUtil.str2dateOrTime(obj.getBegSendTime() + " 00:00:00"),
                        DateUtil.str2dateOrTime(obj.getEndSendTime() + " 23:59:59"));
                }
                if (null != obj.getMaiReceiveBoxSearch().getIsRead()) {
                    where.andNew();
                    where.eq("is_read", obj.getMaiReceiveBoxSearch().getIsRead());
                }
                if (StringUtils.isNotEmpty(obj.getSendTimeSort()) && StringUtils.isEmpty(obj.getMaiSizeSort())) {
                    boolean isAsc = false;
                    if ("1".equals(obj.getSendTimeSort())) {
                        isAsc = true;
                    } else {
                        isAsc = false;
                    }
                    where.orderBy("id", isAsc);
                }
                if (StringUtils.isEmpty(obj.getSendTimeSort()) && StringUtils.isNotEmpty(obj.getMaiSizeSort())) {
                    boolean isAsc = false;
                    if ("1".equals(obj.getMaiSizeSort())) {
                        isAsc = true;
                    } else {
                        isAsc = false;
                    }
                    where.orderBy("mail_size", isAsc);
                }
                if (StringUtils.isNotEmpty(obj.getSendTimeSort()) && StringUtils.isNotEmpty(obj.getMaiSizeSort())) {
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
                if (StringUtils.isEmpty(obj.getSendTimeSort()) && StringUtils.isEmpty(obj.getMaiSizeSort())) {
                    where.orderBy("id", false);
                }
                where.andNew();
                where.eq("receive_user_id", curUserId);
            }
        }
        // searchCount为真时，需查询具体内容，否则为首页查询
        where.setSqlSelect("id,subject_text,receive_user_id,is_scrap,send_time,is_read,is_urgent,is_important,attachment_count,send_full_name");
        Boolean searchCount = pageBean.getIsSearchCount().intValue() == 1 ? true : false;
        Page<MaiReceiveBoxSearch> pagination = new Page<MaiReceiveBoxSearch>(pageBean.getPageNum(), pageBean.getPageSize());
        pagination.setSearchCount(searchCount);
        Page<MaiReceiveBoxSearch> indexpage = maiReceiveBoxSearchService.selectPage(pagination, where);
        
        List<MaiReceiveBoxSearch> list = indexpage.getRecords();
        if(list.size()>0){
            List<MaiReceiveBoxListVO> mailList = new ArrayList<MaiReceiveBoxListVO>();
            if(searchCount){
                List<Long> maiIdList = new ArrayList<Long>();
                for (MaiReceiveBoxSearch maisearch : list) {
                    maiIdList.add(maisearch.getId());
                }
                Where<MaiReceiveBox> where2 = new Where<MaiReceiveBox>();
                where2.in("id", maiIdList);
                where2.setSqlSelect("mail_size,id");
                List<MaiReceiveBox> maiReceiveBoxs = selectList(where2);
                for (MaiReceiveBoxSearch maisearch : list) {
                    for (MaiReceiveBox maiReceiveBox : maiReceiveBoxs) {
                        if(maisearch.getId().equals(maiReceiveBox.getId())){
                            MaiReceiveBoxListVO maiListVo = new MaiReceiveBoxListVO();
                            BeanUtils.copyProperties(maisearch, maiListVo);
                            maiListVo.setMailSize(maiReceiveBox.getMailSize());
                            mailList.add(maiListVo);
                        }
                    }
                }
            }else{
                for (MaiReceiveBoxSearch maisearch : list) {
                    MaiReceiveBoxListVO maiListVo = new MaiReceiveBoxListVO();
                    BeanUtils.copyProperties(maisearch, maiListVo);
                    mailList.add(maiListVo);
                }
            }
            
            page.setRecords(mailList);
            page.setCurrent(indexpage.getCurrent());
            page.setSize(indexpage.getSize());
            page.setTotal(indexpage.getTotal());
        }
        return page;
    }

    @Override
    public RetMsg add(MaiReceiveBoxVO obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            MaiReceiveBox receiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch receiveBoxSearch = obj.getMaiReceiveBoxSearch();
            Date date = new Date();
            if (null == receiveBox.getIsRevoke()) {
                receiveBox.setIsRevoke(0);
            } else {
                receiveBox.setRevokeTime(date);
            }
            if (null == receiveBoxSearch.getIsRead()) {
                receiveBoxSearch.setIsRead(0);
            } else {
                obj.getMaiReceiveBox().setReadTime(date);
            }
            if (null == receiveBoxSearch.getIsScrap()) {
                receiveBoxSearch.setIsScrap(0);
            } else {
                receiveBox.setScrapTime(date);
            }
            receiveBoxSearch.setSendTime(date);
            insert(receiveBox);
            maiReceiveBoxSearchService.insertAllColumn(receiveBoxSearch);
            retMsg.setCode(0);
            retMsg.setMessage("操作成功");
        }
        return retMsg;
    }

    @Override
    @Transactional
    public MaiReceiveBoxVO detail(MaiReceiveBox obj, Long userId, String userName, String nickName) throws Exception {
        MaiReceiveBoxVO maiReceiveBoxVO = new MaiReceiveBoxVO();
        if (null != obj && null != obj.getId()) {
            MaiReceiveBox maiReceiveBox = selectById(obj.getId());
            MaiReceiveBoxSearch maiReceiveBoxSearch = maiReceiveBoxSearchService.selectById(obj.getId());
            if (null != maiReceiveBox) {
                if (null != userId) {
                    maiReceiveBoxVO.setUserId(userId);
                    maiReceiveBoxVO.setUserName(userName);
                    maiReceiveBoxVO.setFullName(nickName);
                }
                // 检查相应的在线消息是否已读，未读设置为已读
                if (maiReceiveBoxSearch.getIsRead() == 0) {
                    AutMsgOnline msg = new AutMsgOnline();
                    msg.setBusinessKey(String.valueOf(maiReceiveBox.getId()));
                    msg.setCreateUserId(userId);
                    msg.setMsgType(WebConstant.ONLINE_MSG_MAIL);
                    autMsgOnlineService.updateIsRead(msg);
                }
                // 向发件箱 发一条记录 内容为：已阅读+邮件名称
                if (null != maiReceiveBox.getSendId()) {
                    // 校验是否已经回执
                    Where<MaiSendBox> sendBoxWhere = new Where<MaiSendBox>();
                    sendBoxWhere.eq("id", maiReceiveBox.getSendId());
                    MaiSendBox sendBox1 = maiSendBoxService.selectOne(sendBoxWhere);
                    if (null != sendBox1 && sendBox1.getIsReceipt() == 1 && sendBox1.getIsReceiptMail() == 0
                        && maiReceiveBoxSearch.getIsRead() == 0) {
                        AutUser user = new AutUser();
                        user.setUserName(maiReceiveBox.getReceiveUserName());
                        user.setFullName(maiReceiveBox.getReceiveFullName());
                        user.setId(maiReceiveBoxSearch.getReceiveUserId());
                        MaiSendBox sendBox = autoSend(user, userId, userName, nickName);
                        maiSendBoxService.insert(sendBox);
                        MaiReceiveBoxInfo receiveBoxInfo = autoReceit(sendBox1, userId, userName, nickName);
                        MaiReceiveBox box = receiveBoxInfo.getMaiReceiveBox();
                        box.setSendId(sendBox.getId());
                        insert(box);
                        MaiReceiveBoxSearch searchBox = receiveBoxInfo.getMaiReceiveBoxSearch();
                        searchBox.setId(box.getId());
                        maiReceiveBoxSearchService.insertAllColumn(searchBox);
                    }
                }
                maiReceiveBoxSearch.setIsRead(1);
                maiReceiveBox.setReadTime(new Date());
                updateById(maiReceiveBox);// 修改为已读
                maiReceiveBoxSearchService.updateById(maiReceiveBoxSearch);
                // 人员排序
                List<String> tmpList = new ArrayList<String>();
                String[] ids = maiReceiveBox.getReceiveUserIds().split(";");
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
                maiReceiveBox.setReceiveUserIds(allId);
                maiReceiveBox.setReceiveFullNames(names);
                // 人员排序（抄送人）
                if (maiReceiveBox.getCopyUserIds() != null && maiReceiveBox.getCopyUserIds() != "") {
                    List<String> tmpList1 = new ArrayList<String>();
                    String[] ids1 = maiReceiveBox.getCopyUserIds().split(";");
                    List<Long> receiveIds1 = new ArrayList<Long>();
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
                    maiReceiveBox.setCopyUserIds(allId);
                    maiReceiveBox.setCopyFullNames(names1);
                }
                maiReceiveBoxVO.setMaiReceiveBox(maiReceiveBox);
                maiReceiveBoxVO.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
                Where<MaiAttachment> where = new Where<MaiAttachment>();
                where.eq("receive_id", maiReceiveBox.getId());
                List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                if (null != attachmentList && !attachmentList.isEmpty()) {
                    maiReceiveBoxVO.setMaiAttachmentList(attachmentList);
                }
            }
        }
        return maiReceiveBoxVO;
    }

    @Override
    @Transactional
    public RetMsg delete(MaiReceiveBoxVO obj) throws Exception {
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
                if (!idList.isEmpty()) {
                    Where<MaiReceiveBox> where = new Where<MaiReceiveBox>();
                    where.in("id", idList);
                    List<MaiReceiveBox> maiReceiveBoxList = selectList(where);
                    List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = maiReceiveBoxSearchService.selectBatchIds(idList);
                    List<MaiScrapBox> scrapBoxList = new ArrayList<MaiScrapBox>();
                    if (!maiReceiveBoxList.isEmpty()) {
                        for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
                                for(MaiReceiveBoxSearch reBoxSearch : maiReceiveBoxSearchList){
                                    if(reBoxSearch.getId().equals(receiveBox.getId())){
                                        reBoxSearch.setIsScrap(1);
                                        receiveBox.setScrapTime(new Date());
                                        MaiScrapBox scrapBox = new MaiScrapBox();
                                        scrapBox.setReceiveUserIds(receiveBox.getReceiveUserIds());
                                        scrapBox.setReceiveFullNames(receiveBox.getReceiveFullNames());
                                        scrapBox.setReceiveUserNames(receiveBox.getReceiveUserNames());
                                        scrapBox.setSendUserId(receiveBox.getSendUserId());
                                        scrapBox.setSendFullName(reBoxSearch.getSendFullName());
                                        scrapBox.setSendUserName(receiveBox.getSendUserName());
                                        scrapBox.setMailSize(receiveBox.getMailSize());
                                        scrapBox.setSubjectText(reBoxSearch.getSubjectText());
                                        scrapBox.setOrgnlType(1);
                                        scrapBox.setOrgnlId(receiveBox.getId());
                                        scrapBoxList.add(scrapBox);
                                    }
                                }
                                
                        }
                        updateBatchById(maiReceiveBoxList);
                        maiReceiveBoxSearchService.updateBatchById(maiReceiveBoxSearchList);
                        if (null != scrapBoxList && !scrapBoxList.isEmpty()) {
                            maiScrapBoxService.insertBatch(scrapBoxList);
                        }
                    }
                    // 相关在线信息删除
                    String maiReceiveIds = "";
                    for (Long receiveId : idList) {
                        maiReceiveIds += receiveId + ";";
                    }
                    if (StringUtils.isNotEmpty(maiReceiveIds)) {
                        autMsgOnlineService.deleteMsgList(maiReceiveIds, obj.getUserId());
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
    public RetMsg revokeAndDelete(MaiReceiveBox obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (null != obj.getId()) {
                SysParameter parameter = sysParameterService.selectBykey("allow_revoke_time");
                if (null == parameter) {
                    retMsg.setCode(1);
                    retMsg.setMessage("请到[系统管理]菜单下的[系统参数]设置key为[allow_revoke_time]的参数(不包含[]),该参数为:允许邮件撤回并删除的时间(分钟)");
                    return retMsg;
                } else {
                    Where<MaiReceiveBox> receiveBoxWhere = new Where<MaiReceiveBox>();
                    receiveBoxWhere.setSqlSelect(
                        "id,create_time,version,is_delete,create_user_id,create_user_name,is_revoke,revoke_time,is_scrap,scrap_time,send_id");
                    receiveBoxWhere.eq("id", obj.getId());
                    MaiReceiveBox maiReceiveBox = selectOne(receiveBoxWhere);
                    MaiReceiveBoxSearch maiReceiveBoxSearch = maiReceiveBoxSearchService.selectById(obj.getId());
                    if (null != maiReceiveBox) {
                        Long diffMinutes = DateUtil.get2DateMinutes(maiReceiveBoxSearch.getSendTime());
                        String value = parameter.getParamValue();
                        if (diffMinutes <= Long.parseLong(value)) {
                            Date date = new Date();
                            maiReceiveBoxSearch.setIsScrap(1);
                            maiReceiveBox.setScrapTime(date);
                            maiReceiveBox.setIsRevoke(1);
                            maiReceiveBox.setRevokeTime(date);
                            updateById(maiReceiveBox);
                            maiReceiveBoxSearchService.updateById(maiReceiveBoxSearch);
                            // 修改发件箱的 撤回状态和废件状态
                            if (null != maiReceiveBox.getSendId()) {
                                Where<MaiSendBox> sendBoxWhere = new Where<MaiSendBox>();
                                sendBoxWhere.setSqlSelect(
                                    "id,create_time,version,is_delete,create_user_id,create_user_name,is_revoke,revoke_time,is_scrap,scrap_time");
                                sendBoxWhere.eq("id", maiReceiveBox.getSendId());
                                MaiSendBox maiSendBox = maiSendBoxService.selectOne(sendBoxWhere);
                                if (null != maiSendBox) {
                                    maiSendBox.setIsRevoke(1);
                                    maiSendBox.setRevokeTime(date);
                                    maiSendBox.setIsScrap(1);
                                    maiSendBox.setScrapTime(date);
                                    maiSendBoxService.updateById(maiSendBox);
                                }
                            }
                            // deleteById(obj.getId());
                            retMsg.setCode(0);
                            retMsg.setMessage("操作成功");
                        } else {
                            retMsg.setCode(1);
                            retMsg.setMessage("只允许在" + value + "分钟之内撤回并删除邮件");
                            return retMsg;
                        }
                    }
                }
            }
        }
        return retMsg;
    }

    @Override
    public RetMsg sign(MaiReceiveBoxVO obj, Long userId, String userName, String nickName) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj) {
            if (StringUtils.isNotEmpty(obj.getIds())) {
                String ids = obj.getIds();
                String idArr[] = null;
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
                for (String id : idArr) {
                    if (StringUtils.isNotEmpty(id)) {
                        idList.add(Long.parseLong(id));
                    }
                }
                Where<MaiReceiveBox> where = new Where<MaiReceiveBox>();
                if (!idList.isEmpty()) {
                    where.in("id", idList);
                }
                List<MaiReceiveBox> receiveBoxList = selectList(where);
                List<Long> sendIds = new ArrayList<Long>();
                for (MaiReceiveBox maiReceiveBox : receiveBoxList) {
                    sendIds.add(maiReceiveBox.getSendId());
                }
                Map<Long, MaiReceiveBoxSearch> map = new HashMap<Long, MaiReceiveBoxSearch>();
                List<MaiReceiveBoxSearch> receiveBoxSearchList = maiReceiveBoxSearchService.selectBatchIds(idList);
                for (MaiReceiveBoxSearch maiReceiveBoxSearch : receiveBoxSearchList) {
                    map.put(maiReceiveBoxSearch.getId(), maiReceiveBoxSearch);
                }
                
                List<MaiReceiveBox> maiReceiveBoxList = new ArrayList<MaiReceiveBox>();
                List<MaiReceiveBoxSearch> maiReceiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
                List<MaiSendBox> sendBoxList = new ArrayList<MaiSendBox>();
                if (!receiveBoxList.isEmpty()) {
                    Date date = new Date();
                    for (MaiReceiveBox receiveBox : receiveBoxList) {
                        if (null != receiveBox) {
                            MaiReceiveBoxSearch searchInfo = map.get(receiveBox.getId());
                            if (null != receiveBox.getSendId()) {
                                Where<MaiSendBox> sendBoxWhere = new Where<MaiSendBox>();
                                sendBoxWhere.eq("id", receiveBox.getSendId());
                                MaiSendBox sendBox1 = maiSendBoxService.selectOne(sendBoxWhere);
                                if (null != sendBox1 && sendBox1.getIsReceipt() == 1 && sendBox1.getIsReceiptMail() == 0
                                    && searchInfo.getIsRead() == 0) {
                                    AutUser user = new AutUser();
                                    user.setUserName(receiveBox.getReceiveUserName());
                                    user.setFullName(receiveBox.getReceiveFullName());
                                    user.setId(searchInfo.getReceiveUserId());
                                    sendBoxList.add(autoSend(user, userId, userName, nickName));
                                    MaiReceiveBoxInfo maiReceiveBoxInfo = autoReceit(sendBox1, userId, userName, nickName);
                                    maiReceiveBoxList.add(maiReceiveBoxInfo.getMaiReceiveBox());
                                    maiReceiveBoxSearchList.add(maiReceiveBoxInfo.getMaiReceiveBoxSearch());
                                }
                                searchInfo.setIsRead(1);
                                receiveBox.setReadTime(date); 
                            }
                        }
                    }
                }
                updateBatchById(receiveBoxList);
                maiReceiveBoxSearchService.updateBatchById(receiveBoxSearchList);
                if (null != sendBoxList && !sendBoxList.isEmpty()) {
                    maiSendBoxService.insertBatch(sendBoxList);
                }
                if (null != maiReceiveBoxList && !maiReceiveBoxList.isEmpty()) {
                    if (null != sendBoxList && !sendBoxList.isEmpty()) {
                        int count = 0;
                        for (MaiSendBox sendBox : sendBoxList) {
                            MaiReceiveBox receiveBox = maiReceiveBoxList.get(count);
                            if (null != receiveBox && null != sendBox.getId()) {
                                receiveBox.setSendId(sendBox.getId());
                            }
                            count++;
                        }
                    }
                    insertBatch(maiReceiveBoxList);
                    for (int i = 0; i<maiReceiveBoxList.size(); i++) {
                        maiReceiveBoxSearchList.get(i).setId(maiReceiveBoxList.get(i).getId());
                    }
                    maiReceiveBoxSearchService.insertBatch(maiReceiveBoxSearchList);
                }
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public RetMsg signImport(MaiReceiveBoxSearch obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj && null != obj.getId()) {
            MaiReceiveBoxSearch receiveBoxSearch = maiReceiveBoxSearchService.selectById(obj.getId());
            if (null != receiveBoxSearch && null != obj.getIsImportant()) {
                receiveBoxSearch.setIsImportant(obj.getIsImportant());
                maiReceiveBoxSearchService.updateAllColumnById(receiveBoxSearch);
            }
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    /**
     *
     * 系统自动发送回执邮件
     *
     * @return：MaiSendBox
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public MaiSendBox autoSend(AutUser receiveUser, Long userId, String userName, String nickName) {
        MaiSendBox maiSendBox = new MaiSendBox();
        maiSendBox.setReceiveUserIds(receiveUser.getId() + "");
        maiSendBox.setReceiveUserNames(receiveUser.getUserName());
        maiSendBox.setReceiveFullNames(receiveUser.getFullName());

        maiSendBox.setSendUserId(userId);
        maiSendBox.setSendUserName(userName);
        maiSendBox.setSendFullName(nickName);

        maiSendBox.setCopyUserIds("");
        maiSendBox.setCopyUserNames("");
        maiSendBox.setCopyFullNames("");
        maiSendBox.setIsReceiveSmsRemind(0);
        maiSendBox.setHasReceiveSmsRemind(0);
        maiSendBox.setReceiveUserCount(1);
        maiSendBox.setIsCopySmsRemind(0);
        maiSendBox.setHasCopySmsRemind(0);
        maiSendBox.setSubjectText("系统回执");
        maiSendBox.setMailContent("");
        maiSendBox.setMailSize(0);
        maiSendBox.setHasReceiptReceiveCount(1);
        maiSendBox.setHasReceiptCopyCount(0);
        maiSendBox.setIsReceipt(1);
        maiSendBox.setIsReceiptMail(1);
        maiSendBox.setIsRevoke(0);
        maiSendBox.setIsScrap(0);
        maiSendBox.setAttachmentCount(0);
        maiSendBox.setIsSendSuccess(1);
        maiSendBox.setIsCopyOnlineRemind(0);
        maiSendBox.setHasCopyOnlineRemind(0);
        maiSendBox.setIsReceiveOnlineRemind(0);
        maiSendBox.setHasReceiveOnlineRemind(0);
        Date date = new Date();
        maiSendBox.setSendTime(date);
        maiSendBox.setWeekDay(DateUtil.getWeek(date));
        maiSendBox.setIsUrgent(0);
        return maiSendBox;
    }

    /**
     *
     * 系统自动发送回执邮件
     *
     * @return：MaiReceiveBox
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public MaiReceiveBoxInfo autoReceit(MaiSendBox sendBox, Long userId, String userName, String nickName) {
        MaiReceiveBoxInfo mail = new MaiReceiveBoxInfo();
        MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
        MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
        
        maiReceiveBox.setReceiveUserIds(sendBox.getSendUserId() + "");
        maiReceiveBox.setReceiveUserNames(sendBox.getSendUserName());
        maiReceiveBox.setReceiveFullNames(sendBox.getSendFullName());
        maiReceiveBoxSearch.setReceiveUserId(sendBox.getSendUserId());
        maiReceiveBox.setReceiveUserName(sendBox.getSendUserName());
        maiReceiveBox.setReceiveFullName(sendBox.getSendFullName());

        maiReceiveBox.setSendUserId(userId);
        maiReceiveBox.setSendUserName(userName);
        maiReceiveBoxSearch.setSendFullName(nickName);
        maiReceiveBox.setCopyUserIds("");
        maiReceiveBox.setCopyUserNames("");
        maiReceiveBox.setCopyFullNames("");

        String subjectText = "";
        if (StringUtils.isNotEmpty(sendBox.getSubjectText())) {
            subjectText = sendBox.getSubjectText();
        }
        maiReceiveBoxSearch.setSubjectText("已阅读:" + "\"" + subjectText + "\"");

        StringBuffer sb = new StringBuffer();
        sb.append("<div id=\"temp-1\">邮件收条</div>");
        sb.append("<div id=\"temp-2\">原邮件收件人:" + maiReceiveBox.getReceiveFullNames() + "</div>");
        sb.append("<div id=\"temp-3\">原邮件主题 :" + sendBox.getSubjectText() + "</div>");
        Date date = new Date();
        DateTime dateTime = new DateTime(date);
        sb.append("<div id=\"temp-4\">此收条表明收件人的电脑上曾显示过此邮件，显示时间:" + dateTime.toString("yyyy-MM-HH HH:mm") + "</div>");
        sb.append("<div id=\"temp-5\">");
        sb.append("</div>");
        maiReceiveBox.setMailContent(sb.toString());

        maiReceiveBox.setMailSize(0);
        maiReceiveBox.setIsRevoke(0);
        maiReceiveBox.setReadTime(date);
        maiReceiveBox.setWeekDay(DateUtil.getWeek(date));        
        
        maiReceiveBoxSearch.setAttachmentCount(0);
        maiReceiveBoxSearch.setIsRead(0);
        maiReceiveBoxSearch.setIsScrap(0);
        maiReceiveBoxSearch.setSendTime(date);
        maiReceiveBoxSearch.setIsUrgent(0);
        maiReceiveBoxSearch.setIsImportant(0);
        
        mail.setMaiReceiveBox(maiReceiveBox);
        mail.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
        return mail;
    }
}
