package aljoin.mai.service.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutMsgOnline;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.iservice.AutMsgOnlineService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiReceiveBoxMapper;
import aljoin.mai.dao.object.AppMaiReceiveBoxVO;
import aljoin.mai.dao.object.MaiReceiveBoxInfo;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiScrapBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mai.iservice.app.AppMaiDraftBoxService;
import aljoin.mai.iservice.app.AppMaiReceiveBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 草稿箱表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-09-20
 */
@Service
public class AppMaiReceiveBoxServiceImpl extends ServiceImpl<MaiReceiveBoxMapper, MaiReceiveBox>
    implements AppMaiReceiveBoxService {

    // private final static Logger logger =
    // LoggerFactory.getLogger(AppMaiReceiveBoxServiceImpl.class);

    @Resource
    private AppMaiDraftBoxService appMaiDraftBoxService;
    @Resource
    private MaiAttachmentService maiAttachmentService;
    @Resource
    private AppMaiReceiveBoxService appMaiReceiveBoxService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private MaiScrapBoxService maiScrapBoxService;
    @Resource
    private AutMsgOnlineService autMsgOnlineService;
    @Resource
    private MaiSendBoxService maiSendBoxService;
    @Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;

    
    @Override
    public Page<MaiReceiveBoxSearch> list(PageBean pageBean, MaiReceiveBoxSearch obj, Long userId) throws Exception {
        Where<MaiReceiveBoxSearch> where = new Where<MaiReceiveBoxSearch>();
        where.eq("receive_user_id", userId.toString());
        where.eq("is_scrap", 0);

        SimpleDateFormat formatS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        if (obj.getSendTime() != null) {
            where.ge("send_time", formatS.parse(format.format(obj.getSendTime()) + " 00:00:00"));
        }

        if (obj.getIsUrgent() != null) {
            where.eq("is_urgent", obj.getIsUrgent());
        }
        if (obj.getIsRead() != null) {
            where.eq("is_read", obj.getIsRead());
        }
        if (obj.getIsDelete() != null) {
            if (obj.getIsDelete() == 1) {
                where.where("attachment_count > {0}", 0);
            } else {
                where.eq("attachment_count", 0);
            }
        }

        // 搜索条件:主题、收件人、操作时间
        if (StringUtils.isNotEmpty(obj.getSubjectText())) {
            where.like("subject_text", obj.getSubjectText());
        }
        if (StringUtils.isNotEmpty(obj.getSendFullName())) {
            where.like("send_full_name", obj.getSendFullName());
        }
        where.orderBy("send_time", false);
        Page<MaiReceiveBoxSearch> page =
            maiReceiveBoxSearchService.selectPage(new Page<MaiReceiveBoxSearch>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    @Transactional
    public AppMaiReceiveBoxVO getById(MaiReceiveBox obj, String userId, String readType) throws Exception {

        AppMaiReceiveBoxVO maiDraftBoxVO = new AppMaiReceiveBoxVO();
        if (null != obj) {
            if (null != obj.getId()) {
                // Where<MaiReceiveBox> w1 = new Where<MaiReceiveBox>();
                // w1.eq("id", obj.getId());

                MaiReceiveBox maiReceiveBox = selectById(obj.getId());
                MaiReceiveBoxSearch maiSearch = maiReceiveBoxSearchService.selectById(obj.getId());

                if (maiReceiveBox != null) {
                    maiDraftBoxVO.setMaiReceiveBox(maiReceiveBox);
                    /*SysParameter parameter = sysParameterService.selectBykey("ImgServer");
                    String rootPath = "";
                    if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
                        rootPath = parameter.getParamValue();
                    }*/
                    Where<MaiAttachment> where = new Where<MaiAttachment>();
                    where.setSqlSelect("attach_name,attach_path,attach_size,send_id,receive_id,draft_id");
                    where.eq("receive_id", maiReceiveBox.getId());
                    List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                    //List<MaiAttachment> returnList = new ArrayList<MaiAttachment>();
                    if (!attachmentList.isEmpty()) {
                        /*for (MaiAttachment maiAttachment : attachmentList) {
                            String tmp = maiAttachment.getAttachPath();
                            // tmp = tmp.substring(2, tmp.length());
                            tmp = tmp.replaceAll("//", "/");
                            String path = rootPath + tmp;
                            returnList.add(maiAttachment.setAttachPath(path));
                        }*/
                        maiDraftBoxVO.setMaiAttachmentList(attachmentList);
                    }

                    // 向发件箱 发一条记录 内容为：已阅读+邮件名称
                    if (null != maiReceiveBox.getSendId()) {
                        // 校验是否已经回执
                        Where<MaiSendBox> sendBoxWhere = new Where<MaiSendBox>();
                        sendBoxWhere.eq("id", maiReceiveBox.getSendId());
                        MaiSendBox sendBox1 = maiSendBoxService.selectOne(sendBoxWhere);
                        if (null != sendBox1 && sendBox1.getIsReceipt() == 1 && sendBox1.getIsReceiptMail() == 0
                            && maiSearch.getIsRead() == 0) {
                            AutUser user = autUserService.selectById(Long.valueOf(userId));
                            MaiSendBox sendBox = autoSend(maiReceiveBox, user);
                            sendBox.setIsCopyOnlineRemind(0);
                            sendBox.setIsCopySmsRemind(0);
                            sendBox.setHasCopyOnlineRemind(0);
                            sendBox.setHasCopySmsRemind(0);
                            sendBox.setIsReceiveOnlineRemind(1);
                            sendBox.setIsReceiveSmsRemind(0);
                            sendBox.setHasReceiveOnlineRemind(0);
                            sendBox.setHasReceiveSmsRemind(0);
                            maiSendBoxService.insert(sendBox);
                            MaiReceiveBoxInfo mailInfo = autoReceit(sendBox1, user, readType);
                            MaiReceiveBox receiveBox = mailInfo.getMaiReceiveBox();
                            receiveBox.setSendId(sendBox.getId());
                            insert(receiveBox);
                            MaiReceiveBoxSearch receiveBoxSearch = mailInfo.getMaiReceiveBoxSearch();
                            receiveBoxSearch.setId(receiveBox.getId());
                            maiReceiveBoxSearchService.insert(receiveBoxSearch);
                        }
                        if (maiSearch.getIsRead() == 0) {
                            AutMsgOnline msg = new AutMsgOnline();
                            msg.setBusinessKey(String.valueOf(maiReceiveBox.getId()));
                            msg.setCreateUserId(maiReceiveBox.getSendId());
                            msg.setCreateUserName(maiReceiveBox.getSendUserName());
                            msg.setMsgType(WebConstant.ONLINE_MSG_MAIL);
                            autMsgOnlineService.updateIsRead(msg);
                            maiSearch.setIsRead(1);
                            maiReceiveBoxSearchService.updateById(maiSearch);
                        }
                    }
                } else {
                    throw new Exception("找不到该邮件");
                }

            }

        }
        return maiDraftBoxVO;
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
    public MaiReceiveBoxInfo autoReceit(MaiSendBox sendBox, AutUser user, String readType) {
        MaiReceiveBoxInfo mail = new MaiReceiveBoxInfo();
        MaiReceiveBox maiReceiveBox = new MaiReceiveBox();
        MaiReceiveBoxSearch maiReceiveBoxSearch = new MaiReceiveBoxSearch();
        maiReceiveBox.setReceiveUserIds(sendBox.getReceiveUserIds());
        maiReceiveBox.setReceiveUserNames(sendBox.getReceiveUserNames());
        maiReceiveBox.setReceiveFullNames(sendBox.getReceiveFullNames());

        maiReceiveBoxSearch.setReceiveUserId(sendBox.getSendUserId());
        maiReceiveBox.setReceiveUserName(sendBox.getSendUserName());
        maiReceiveBox.setReceiveFullName(sendBox.getSendFullName());
        maiReceiveBox.setCreateUserId(user.getId());
        maiReceiveBox.setCreateUserName(user.getUserName());
        maiReceiveBox.setSendUserId(user.getId());
        maiReceiveBox.setSendUserName(user.getUserName());
        maiReceiveBoxSearch.setSendFullName(user.getFullName());

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
        sb.append("<div id=\"temp-4\">此收条表明收件人的" + readType + "上曾显示过此邮件，显示时间:" + dateTime.toString("yyyy-MM-HH HH:mm")
            + "</div>");
        sb.append("<div id=\"temp-5\">");
        sb.append("</div>");
        maiReceiveBox.setMailContent(sb.toString());
        maiReceiveBox.setMailSize(0);
        maiReceiveBox.setIsRevoke(0);
        maiReceiveBox.setWeekDay(DateUtil.getWeek(date));
        maiReceiveBox.setReadTime(date);
        maiReceiveBoxSearch.setAttachmentCount(0);
        maiReceiveBoxSearch.setSendTime(date);
        maiReceiveBoxSearch.setIsScrap(0);
        maiReceiveBoxSearch.setIsRead(0);
        maiReceiveBoxSearch.setIsUrgent(0);
        maiReceiveBoxSearch.setIsImportant(0);
        mail.setMaiReceiveBox(maiReceiveBox);
        mail.setMaiReceiveBoxSearch(maiReceiveBoxSearch);
        return mail;
    }

    public String getImg(String rootPath, String id) throws Exception {
        String path = "";

        if (id.length() > 0) {
            String[] ids = id.split(";");
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != null && !"".equals(ids[i])) {
                    if (!"".equals(rootPath)) {
                        AutUserPub autUserPub = autUserPubService.selectById(ids[i]);
                        if (autUserPub != null && autUserPub.getUserIcon() != null
                            && !"".equals(autUserPub.getUserIcon())) {
                            path = path + rootPath + autUserPub.getUserIcon() + ";";
                        } else {
                            // 默认地址
                            path = path + "web/images/0.jpg" + ";";
                        }
                    } else {
                        path = path + "web/images/0.jpg" + ";";
                    }
                }
            }
        }
        return path;

    }

    /**
     *
     * 系统自动发送回执邮件
     *
     * @return：MaiSendBox
     *
     * @author：huangw
     *
     * @date：2017年12月12日
     */
    public MaiSendBox autoSend(MaiReceiveBox maiReceiveBox, AutUser user) {
        MaiSendBox maiSendBox = new MaiSendBox();
        maiSendBox.setReceiveUserIds(maiReceiveBox.getSendUserId() + "");
        maiSendBox.setReceiveUserNames(maiReceiveBox.getSendUserName());
        maiSendBox.setReceiveFullNames(maiReceiveBox.getReceiveFullNames());
        maiSendBox.setCreateUserId(user.getId());
        maiSendBox.setCreateUserName(user.getUserName());
        maiSendBox.setSendUserId(user.getId());
        maiSendBox.setSendUserName(user.getUserName());
        maiSendBox.setSendFullName(user.getFullName());
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
        Date date = new Date();
        maiSendBox.setSendTime(date);
        maiSendBox.setWeekDay(DateUtil.getWeek(date));
        maiSendBox.setIsUrgent(0);
        return maiSendBox;
    }

    @Override
    @Transactional
    public RetMsg delete(MaiReceiveBoxVO obj, String userId, String nameName) throws Exception {
        RetMsg retMsg = new RetMsg();
        if (null != obj && null != obj.getIds()) {
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
                List<MaiReceiveBoxSearch> maiReceiveBoxSearchs = maiReceiveBoxSearchService.selectBatchIds(idList);
                List<MaiScrapBox> scrapBoxList = new ArrayList<MaiScrapBox>();
                if (!maiReceiveBoxList.isEmpty()) {
                    for (MaiReceiveBox receiveBox : maiReceiveBoxList) {
                        for (MaiReceiveBoxSearch maiReceiveBoxSearch : maiReceiveBoxSearchs) {
                            if (receiveBox.getId().equals(maiReceiveBoxSearch.getId())) {
                                maiReceiveBoxSearch.setIsScrap(1);
                                receiveBox.setScrapTime(new Date());
                                MaiScrapBox scrapBox = new MaiScrapBox();
                                scrapBox.setReceiveUserIds(receiveBox.getReceiveUserIds());
                                scrapBox.setReceiveFullNames(receiveBox.getReceiveFullNames());
                                scrapBox.setReceiveUserNames(receiveBox.getReceiveUserNames());
                                scrapBox.setSendUserId(receiveBox.getSendUserId());
                                scrapBox.setSendFullName(maiReceiveBoxSearch.getSendFullName());
                                scrapBox.setSendUserName(receiveBox.getSendUserName());
                                scrapBox.setMailSize(receiveBox.getMailSize());
                                scrapBox.setSubjectText(maiReceiveBoxSearch.getSubjectText());
                                scrapBox.setOrgnlType(1);
                                scrapBox.setOrgnlId(receiveBox.getId());
                                scrapBox.setCreateUserId(Long.valueOf(userId));
                                scrapBox.setCreateUserName(nameName);
                                scrapBoxList.add(scrapBox);
                            }
                        }
                    }
                    updateBatchById(maiReceiveBoxList);
                    maiReceiveBoxSearchService.updateBatchById(maiReceiveBoxSearchs);
                    if (null != scrapBoxList && !scrapBoxList.isEmpty()) {
                        maiScrapBoxService.insertBatch(scrapBoxList);
                    }
                }
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            }
        }
        return retMsg;
    }
}
