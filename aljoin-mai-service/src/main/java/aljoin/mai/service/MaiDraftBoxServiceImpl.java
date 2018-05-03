package aljoin.mai.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiDraftBoxMapper;
import aljoin.mai.dao.object.MaiDraftBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.MaiDraftBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.iservice.ResResourceService;
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
public class MaiDraftBoxServiceImpl extends ServiceImpl<MaiDraftBoxMapper, MaiDraftBox> implements MaiDraftBoxService {

    private final static Logger logger = LoggerFactory.getLogger(MaiDraftBoxServiceImpl.class);

    @Resource
    private MaiDraftBoxService maiDraftBoxService;
    @Resource
    private MaiAttachmentService maiAttachmentService;
    @Resource
    private ResResourceService resResourceService;

    @Override
    public Page<MaiDraftBox> list(PageBean pageBean, MaiDraftBox obj, Long userId, String time1, String time2,
        String orderByTime) throws Exception {
        Where<MaiDraftBox> where = new Where<MaiDraftBox>();
        where.eq("create_user_id", userId);

        // 搜索条件:主题、收件人
        if (StringUtils.isNotEmpty(obj.getSubjectText())) {
            where.andNew();
            where.like("subject_text", obj.getSubjectText());
            where.or("receive_full_names LIKE {0}", "%" + obj.getSubjectText() + "%");
        }
        if (StringUtils.isNotEmpty(time1) && StringUtils.isNotEmpty(time2)) {
            where.andNew();
            where.between("create_time", DateUtil.str2dateOrTime(time1 + " 00:00:00"),
                DateUtil.str2dateOrTime(time2 + " 23:59:59"));
        } else if (StringUtils.isNotEmpty(time1)) {
            where.andNew();
            where.ge("create_time", DateUtil.str2dateOrTime(time1 + " 00:00:00"));
        } else if (StringUtils.isNotEmpty(time2)) {
            where.andNew();
            where.le("create_time", DateUtil.str2dateOrTime(time2 + " 23:59:59"));
        }
        // 按照邮件大小升序/降序或按收件时间排序，如果都默认按收件时间降序
        if (StringUtils.checkValNotNull(obj.getMailSize())) {
            if ("1".equals(obj.getMailSize()) || obj.getMailSize() == 1) {
                where.orderBy("mail_size", true);
            } else {
                where.orderBy("mail_size", false);
            }
        } else if (StringUtils.checkValNotNull(orderByTime)) {
            if ("1".equals(orderByTime)) {
                where.orderBy("last_update_time", true);
            } else {
                where.orderBy("last_update_time", false);
            }
        } else {
            where.orderBy("last_update_time", false);
        }
        Page<MaiDraftBox> page
            = selectPage(new Page<MaiDraftBox>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public RetMsg add(MaiWriteVO obj, Long  userId,String userName,String nickName) throws Exception {

        RetMsg retMsg = new RetMsg();

        try {
            MaiDraftBox draftBox = new MaiDraftBox();
            MaiReceiveBox receiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch receiveBoxSearch = obj.getMaiReceiveBoxSearch();
            MaiSendBox sendBox = obj.getMaiSendBox();

            if (null != userId) {
                draftBox.setSendUserId(userId);
                draftBox.setSendUserName(userName);
                draftBox.setSendFullName(nickName);
            }
            // 附件表
            int size = 0;
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        if (null != attachment && null == attachment.getId()) {
                            size += attachment.getAttachSize();
                        }
                    }
                }
            }
            if (null != receiveBoxSearch){
                if (null != receiveBoxSearch.getAttachmentCount()) {
                    draftBox.setAttachmentCount(receiveBoxSearch.getAttachmentCount());
                } else {
                    draftBox.setAttachmentCount(0);
                } 
                if (null != receiveBoxSearch.getIsUrgent()) {
                    draftBox.setIsUrgent(receiveBoxSearch.getIsUrgent());
                } else {
                    draftBox.setIsUrgent(0);
                }
                if (StringUtils.isNotEmpty(receiveBoxSearch.getSubjectText())) {
                    draftBox.setSubjectText(receiveBoxSearch.getSubjectText());
                }
            }
            if (null != receiveBox) {
                if (StringUtils.isNotEmpty(receiveBox.getReceiveFullName())) {
                    draftBox.setReceiveFullNames(receiveBox.getReceiveFullName());
                }
                if (StringUtils.isNotEmpty(receiveBox.getReceiveUserNames())) {
                    draftBox.setReceiveUserNames(receiveBox.getReceiveUserNames());
                }
                if (StringUtils.isNotEmpty(receiveBox.getReceiveUserIds())) {
                    draftBox.setReceiveUserIds(receiveBox.getReceiveUserIds());
                }
                if (StringUtils.isNotEmpty(receiveBox.getCopyFullNames())) {
                    draftBox.setCopyFullNames(receiveBox.getCopyFullNames());
                }
                draftBox.setReceiveFullNames(receiveBox.getReceiveFullNames());
                if (StringUtils.isNotEmpty(receiveBox.getCopyUserIds())) {
                    draftBox.setCopyUserIds(receiveBox.getCopyUserIds());
                }
                if (StringUtils.isNotEmpty(receiveBox.getCopyUserNames())) {
                    draftBox.setCopyUserNames(receiveBox.getCopyUserNames());
                }
                if (StringUtils.isNotEmpty(receiveBox.getMailContent())) {
                    draftBox.setMailContent(receiveBox.getMailContent());
                }
                // 邮件size = 附件size+正文size
                if (null != receiveBox.getMailContent()) {
                    int len = receiveBox.getMailContent().getBytes().length / 1024;
                    draftBox.setMailSize((size + len));
                } else {
                    draftBox.setMailSize(size);
                }
            }
            if (null != sendBox) {
                if (null != sendBox.getIsReceiveSmsRemind()) {
                    draftBox.setIsReceiveSmsRemind(sendBox.getIsReceiveSmsRemind());
                } else {
                    draftBox.setIsReceiveSmsRemind(0);
                }

                if (null != sendBox.getScrapTime()) {
                    draftBox.setScrapTime(sendBox.getScrapTime());
                }
                if (null != sendBox.getIsCopySmsRemind()) {
                    draftBox.setIsCopySmsRemind(sendBox.getIsCopySmsRemind());
                } else {
                    draftBox.setIsCopySmsRemind(0);
                }
                if (null != sendBox.getIsReceipt()) {
                    draftBox.setIsReceipt(sendBox.getIsReceipt());
                } else {
                    draftBox.setIsReceipt(0);
                }
            } else {
                draftBox.setIsReceiveSmsRemind(0);
                draftBox.setIsReceipt(0);
                draftBox.setIsCopySmsRemind(0);
            }

            draftBox.setIsScrap(0);
            draftBox.setScrapTime(new Date());
            // 数据插入草稿箱表
            maiDraftBoxService.insert(draftBox);

            // 保存为草稿，有附件的时候，往附件表MaiAttachment新增记录
            List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
            if (null != maiAttachmentList) {
                if (!maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        MaiAttachment maiAttachment = new MaiAttachment();
                        BeanUtils.copyProperties(attachment, maiAttachment);
                        maiAttachment.setSendId(0L);
                        maiAttachment.setReceiveId(0L);
                        maiAttachment.setDraftId(draftBox.getId());
                        newMaiAttachmentList.add(maiAttachment);
                    }
                }
            }
            if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                maiAttachmentService.insertBatch(newMaiAttachmentList);
            }
            
            retMsg.setCode(0);
            retMsg.setMessage("保存草稿成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @Override
    public RetMsg update(MaiWriteVO obj, Long customUserId, String userName, String nickName) throws Exception {

        RetMsg retMsg = new RetMsg();
        try {
            // 根据前台传过来的草稿主键ID查出原草稿数据
            Where<MaiDraftBox> w1 = new Where<MaiDraftBox>();
            w1.eq("id", obj.getMaiDraftBox().getId());
            MaiDraftBox draftBox = maiDraftBoxService.selectOne(w1);

            MaiReceiveBox receiveBox = obj.getMaiReceiveBox();
            MaiReceiveBoxSearch receiveBoxSearch = obj.getMaiReceiveBoxSearch();
            MaiSendBox sendBox = obj.getMaiSendBox();

            if (null != customUserId) {
                draftBox.setSendUserId(customUserId);
                draftBox.setSendUserName(userName);
                draftBox.setSendFullName(nickName);
            }

            if (StringUtils.checkValNotNull(receiveBoxSearch.getAttachmentCount())) {
                draftBox.setAttachmentCount(receiveBoxSearch.getAttachmentCount());
            } else {
                draftBox.setAttachmentCount(0);
            }

            if (StringUtils.checkValNotNull(receiveBox.getReceiveFullName())) {
                draftBox.setReceiveFullNames(receiveBox.getReceiveFullName());
            }
            if (StringUtils.checkValNotNull(receiveBox.getReceiveUserNames())) {
                draftBox.setReceiveUserNames(receiveBox.getReceiveUserNames());
            }
            if (StringUtils.checkValNotNull(receiveBox.getReceiveUserIds())) {
                draftBox.setReceiveUserIds(receiveBox.getReceiveUserIds());
            }
            if (StringUtils.checkValNotNull(receiveBox.getCopyFullNames())) {
                draftBox.setCopyFullNames(receiveBox.getCopyFullNames());
            }
            draftBox.setReceiveFullNames(receiveBox.getReceiveFullNames());
            if (null == sendBox || StringUtils.checkValNull(sendBox)) {
                draftBox.setIsReceiveSmsRemind(0);
                draftBox.setIsCopySmsRemind(0);
                draftBox.setIsReceipt(0);
                draftBox.setIsUrgent(0);
                draftBox.setScrapTime(new Date());
            } else {
                if (StringUtils.checkValNotNull(sendBox.getIsReceiveSmsRemind())) {
                    draftBox.setIsReceiveSmsRemind(sendBox.getIsReceiveSmsRemind());
                } else {
                    draftBox.setIsReceiveSmsRemind(0);
                }
                if (StringUtils.checkValNotNull(sendBox.getScrapTime())) {
                    draftBox.setScrapTime(sendBox.getScrapTime());
                }
                if (StringUtils.checkValNotNull(sendBox.getIsCopySmsRemind())) {
                    draftBox.setIsCopySmsRemind(sendBox.getIsCopySmsRemind());
                } else {
                    draftBox.setIsCopySmsRemind(0);
                }
                if (StringUtils.checkValNotNull(sendBox.getIsReceipt())) {
                    draftBox.setIsReceipt(sendBox.getIsReceipt());
                } else {
                    draftBox.setIsReceipt(0);
                }
                if (StringUtils.checkValNotNull(sendBox.getIsUrgent())) {
                    draftBox.setIsUrgent(sendBox.getIsUrgent());
                } else {
                    draftBox.setIsUrgent(0);
                }
            }
            if (StringUtils.checkValNotNull(receiveBox.getCopyUserIds())) {
                draftBox.setCopyUserIds(receiveBox.getCopyUserIds());
            }
            if (StringUtils.checkValNotNull(receiveBox.getCopyUserNames())) {
                draftBox.setCopyUserNames(receiveBox.getCopyUserNames());
            }
            if (StringUtils.checkValNotNull(receiveBoxSearch.getSubjectText())) {
                draftBox.setSubjectText(receiveBoxSearch.getSubjectText());
            }
            if (StringUtils.checkValNotNull(receiveBox.getMailContent())) {
                draftBox.setMailContent(receiveBox.getMailContent());
            }
            draftBox.setIsScrap(0);

            // 附件表
            int size = 0;
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        if (null != attachment && null == attachment.getId()) {
                            size += attachment.getAttachSize();
                        }
                    }
                }
            }
            if (null != receiveBox.getMailContent() || null != maiAttachmentList) {
                int len = receiveBox.getMailContent().getBytes().length / 1024;
                draftBox.setMailSize((size + len));
            } else {
                draftBox.setMailSize(0);
            }

            maiDraftBoxService.updateById(draftBox);

            // 保存为草稿，有附件的时候，往附件表MaiAttachment新增记录
            List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
            if (null != maiAttachmentList) {
                if (!maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        MaiAttachment maiAttachment = new MaiAttachment();
                        BeanUtils.copyProperties(attachment, maiAttachment);
                        maiAttachment.setSendId(0L);
                        maiAttachment.setReceiveId(0L);
                        maiAttachment.setDraftId(draftBox.getId());
                        newMaiAttachmentList.add(maiAttachment);
                    }
                }
            }
            // 删除原先的附件，然后新增这次的附件
            Where<MaiAttachment> where = new Where<MaiAttachment>();
            where.eq("draft_id", draftBox.getId());
            maiAttachmentService.delete(where);

            // 新增这次的附件
            if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                maiAttachmentService.insertBatch(newMaiAttachmentList);
            }
            
            retMsg.setCode(0);
            retMsg.setMessage("修改成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @Override
    public MaiDraftBoxVO getById(MaiDraftBox obj) throws Exception {

        MaiDraftBoxVO maiDraftBoxVO = new MaiDraftBoxVO();
        if (null != obj) {
            if (null != obj.getId()) {
                MaiDraftBox maiDraftBox = selectById(obj.getId());
                if (null != maiDraftBox) {
                    maiDraftBoxVO.setMaiDraftBox(maiDraftBox);
                }
                Where<MaiAttachment> where = new Where<MaiAttachment>();
                where.eq("draft_id", maiDraftBox.getId());
                List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                if (!attachmentList.isEmpty()) {
                    maiDraftBoxVO.setMaiAttachmentList(attachmentList);
                }

            }

        }
        return maiDraftBoxVO;
    }
}
