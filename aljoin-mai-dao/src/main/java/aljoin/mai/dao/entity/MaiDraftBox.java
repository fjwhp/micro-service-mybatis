package aljoin.mai.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 草稿箱表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-20
 */
public class MaiDraftBox extends Entity<MaiDraftBox> {

    private static final long serialVersionUID = 1L;

    /**
     * 收件人用户ID(分号分隔)
     */
    private String receiveUserIds;
    /**
     * 收件人账号(分号分隔)
     */
    private String receiveUserNames;
    /**
     * 收件人名称(分号分隔)
     */
    private String receiveFullNames;
    /**
     * 是否进行收件人短信提醒
     */
    private Integer isReceiveSmsRemind;
    /**
     * 发件人用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sendUserId;
    /**
     * 发件人账号
     */
    private String sendUserName;
    /**
     * 发件人名称
     */
    private String sendFullName;
    /**
     * 抄送人用户ID(分号分隔)
     */
    private String copyUserIds;
    /**
     * 抄送人账号(分号分隔)
     */
    private String copyUserNames;
    /**
     * 抄送人名称(分号分隔)
     */
    private String copyFullNames;
    /**
     * 是否进行抄送人短信提醒
     */
    private Integer isCopySmsRemind;
    /**
     * 是否设置为废件（即删除操作）
     */
    private Integer isScrap;
    /**
     * 设置为废件时间
     */
    private Date scrapTime;
    /**
     * 附件个数
     */
    private Integer attachmentCount;
    /**
     * 主题
     */
    private String subjectText;
    /**
     * 邮件正文
     */
    private String mailContent;
    /**
     * 对方收到后是否要回执
     */
    private Integer isReceipt;
    /**
     * 邮件大小(正文+附件的大小)
     */
    private Integer mailSize;

    /**
     * 是否紧急
     */
    private Integer isUrgent;

    public String getReceiveUserIds() {
        return receiveUserIds;
    }

    public MaiDraftBox setReceiveUserIds(String receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
        return this;
    }

    public String getReceiveUserNames() {
        return receiveUserNames;
    }

    public MaiDraftBox setReceiveUserNames(String receiveUserNames) {
        this.receiveUserNames = receiveUserNames;
        return this;
    }

    public String getReceiveFullNames() {
        return receiveFullNames;
    }

    public MaiDraftBox setReceiveFullNames(String receiveFullNames) {
        this.receiveFullNames = receiveFullNames;
        return this;
    }

    public Integer getIsReceiveSmsRemind() {
        return isReceiveSmsRemind;
    }

    public MaiDraftBox setIsReceiveSmsRemind(Integer isReceiveSmsRemind) {
        this.isReceiveSmsRemind = isReceiveSmsRemind;
        return this;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public MaiDraftBox setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
        return this;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public MaiDraftBox setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
        return this;
    }

    public String getSendFullName() {
        return sendFullName;
    }

    public MaiDraftBox setSendFullName(String sendFullName) {
        this.sendFullName = sendFullName;
        return this;
    }

    public String getCopyUserIds() {
        return copyUserIds;
    }

    public MaiDraftBox setCopyUserIds(String copyUserIds) {
        this.copyUserIds = copyUserIds;
        return this;
    }

    public String getCopyUserNames() {
        return copyUserNames;
    }

    public MaiDraftBox setCopyUserNames(String copyUserNames) {
        this.copyUserNames = copyUserNames;
        return this;
    }

    public String getCopyFullNames() {
        return copyFullNames;
    }

    public MaiDraftBox setCopyFullNames(String copyFullNames) {
        this.copyFullNames = copyFullNames;
        return this;
    }

    public Integer getIsCopySmsRemind() {
        return isCopySmsRemind;
    }

    public MaiDraftBox setIsCopySmsRemind(Integer isCopySmsRemind) {
        this.isCopySmsRemind = isCopySmsRemind;
        return this;
    }

    public Integer getIsScrap() {
        return isScrap;
    }

    public MaiDraftBox setIsScrap(Integer isScrap) {
        this.isScrap = isScrap;
        return this;
    }

    public Date getScrapTime() {
        return scrapTime;
    }

    public MaiDraftBox setScrapTime(Date scrapTime) {
        this.scrapTime = scrapTime;
        return this;
    }

    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public MaiDraftBox setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
        return this;
    }

    public String getSubjectText() {
        return subjectText;
    }

    public MaiDraftBox setSubjectText(String subjectText) {
        this.subjectText = subjectText;
        return this;
    }

    public String getMailContent() {
        return mailContent;
    }

    public MaiDraftBox setMailContent(String mailContent) {
        this.mailContent = mailContent;
        return this;
    }

    public Integer getIsReceipt() {
        return isReceipt;
    }

    public MaiDraftBox setIsReceipt(Integer isReceipt) {
        this.isReceipt = isReceipt;
        return this;
    }

    public Integer getMailSize() {
        return mailSize;
    }

    public MaiDraftBox setMailSize(Integer mailSize) {
        this.mailSize = mailSize;
        return this;
    }

    public Integer getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Integer isUrgent) {
        this.isUrgent = isUrgent;
    }

}
