package aljoin.mai.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 发件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiSendBox extends Entity<MaiSendBox> {

    private static final long serialVersionUID = 1L;

    /**
     * 收件人用户ID(分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String receiveUserIds;
    /**
     * 收件人账号(分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String receiveUserNames;
    /**
     * 收件人名称(分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String receiveFullNames;
    /**
     * 是否进行收件人短信提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer isReceiveSmsRemind;
    /**
     * 是否已经进行收件人短信提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer hasReceiveSmsRemind;
    /**
     * 收件人数
     */
    @ApiModelProperty(hidden = true)
    private Integer receiveUserCount;
    /**
     * 发件人用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(hidden = true)
    private Long sendUserId;
    /**
     * 发件人账号
     */
    @ApiModelProperty(hidden = true)
    private String sendUserName;
    /**
     * 发件人名称
     */
    @ApiModelProperty(hidden = true)
    private String sendFullName;
    /**
     * 抄送人用户ID(分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String copyUserIds;
    /**
     * 抄送人账号(分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String copyUserNames;
    /**
     * 抄送人名称(分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String copyFullNames;
    /**
     * 是否进行抄送人短信提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer isCopySmsRemind;
    /**
     * 是否已经进行抄送人短信提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer hasCopySmsRemind;
    /**
     * 主题
     */
    @ApiModelProperty(hidden = true)
    private String subjectText;
    /**
     * 邮件正文
     */
    @ApiModelProperty(hidden = true)
    private String mailContent;
    /**
     * 邮件大小(KB,正文+附件的大小)
     */
    @ApiModelProperty(hidden = true)
    private Integer mailSize;
    /**
     * 对方(含被抄送方)阅读后是否要回执
     */
    @ApiModelProperty(hidden = true)
    private Integer isReceipt;
    /**
     * 本邮件是否回执邮件
     */
    @ApiModelProperty(hidden = true)
    private Integer isReceiptMail;
    /**
     * 已收到回执数(收件人回执数)
     */
    @ApiModelProperty(hidden = true)
    private Integer hasReceiptReceiveCount;
    /**
     * 已收到回执数(抄送人回执数)
     */
    @ApiModelProperty(hidden = true)
    private Integer hasReceiptCopyCount;
    /**
     * 是否撤销(回)
     */
    @ApiModelProperty(hidden = true)
    private Integer isRevoke;
    /**
     * 撤销(回)时间
     */
    @ApiModelProperty(hidden = true)
    private Date revokeTime;
    /**
     * 是否设置为废件（即删除操作）
     */
    @ApiModelProperty(hidden = true)
    private Integer isScrap;
    /**
     * 设置为废件时间
     */
    @ApiModelProperty(hidden = true)
    private Date scrapTime;
    /**
     * 附件个数
     */
    @ApiModelProperty(hidden = true)
    private Integer attachmentCount;
    /**
     * 发送时间
     */
    @ApiModelProperty(hidden = true)
    private Date sendTime;
    /**
     * 是否发送成功
     */
    @ApiModelProperty(hidden = true)
    private Integer isSendSuccess;
    /**
     * 发送日期是星期几(保存字符串)：星期一；星期二；星期三；星期四；星期五；星期六；星期日；
     */
    @ApiModelProperty(hidden = true)
    private String weekDay;

    /**
     * 是否紧急
     */
    @ApiModelProperty(hidden = true)
    private Integer isUrgent;
    /**
     * 是否对抄送人进行在线消息提醒
     */
    private Integer isCopyOnlineRemind;
    /**
     * 是否已经对抄送人进行在线消息提醒
     */
    private Integer hasCopyOnlineRemind;
    /**
     * 是否进行收件人进行在线消息提醒
     */
    private Integer isReceiveOnlineRemind;
    /**
     * 是否已经进行收件人进行在线消息提醒
     */
    private Integer hasReceiveOnlineRemind;

    public Integer getIsCopyOnlineRemind() {
        return isCopyOnlineRemind;
    }

    public void setIsCopyOnlineRemind(Integer isCopyOnlineRemind) {
        this.isCopyOnlineRemind = isCopyOnlineRemind;
    }

    public Integer getHasCopyOnlineRemind() {
        return hasCopyOnlineRemind;
    }

    public void setHasCopyOnlineRemind(Integer hasCopyOnlineRemind) {
        this.hasCopyOnlineRemind = hasCopyOnlineRemind;
    }

    public Integer getIsReceiveOnlineRemind() {
        return isReceiveOnlineRemind;
    }

    public void setIsReceiveOnlineRemind(Integer isReceiveOnlineRemind) {
        this.isReceiveOnlineRemind = isReceiveOnlineRemind;
    }

    public Integer getHasReceiveOnlineRemind() {
        return hasReceiveOnlineRemind;
    }

    public void setHasReceiveOnlineRemind(Integer hasReceiveOnlineRemind) {
        this.hasReceiveOnlineRemind = hasReceiveOnlineRemind;
    }

    public String getReceiveUserIds() {
        return receiveUserIds;
    }

    public MaiSendBox setReceiveUserIds(String receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
        return this;
    }

    public String getReceiveUserNames() {
        return receiveUserNames;
    }

    public MaiSendBox setReceiveUserNames(String receiveUserNames) {
        this.receiveUserNames = receiveUserNames;
        return this;
    }

    public String getReceiveFullNames() {
        return receiveFullNames;
    }

    public MaiSendBox setReceiveFullNames(String receiveFullNames) {
        this.receiveFullNames = receiveFullNames;
        return this;
    }

    public Integer getIsReceiveSmsRemind() {
        return isReceiveSmsRemind;
    }

    public MaiSendBox setIsReceiveSmsRemind(Integer isReceiveSmsRemind) {
        this.isReceiveSmsRemind = isReceiveSmsRemind;
        return this;
    }

    public Integer getHasReceiveSmsRemind() {
        return hasReceiveSmsRemind;
    }

    public MaiSendBox setHasReceiveSmsRemind(Integer hasReceiveSmsRemind) {
        this.hasReceiveSmsRemind = hasReceiveSmsRemind;
        return this;
    }

    public Integer getReceiveUserCount() {
        return receiveUserCount;
    }

    public MaiSendBox setReceiveUserCount(Integer receiveUserCount) {
        this.receiveUserCount = receiveUserCount;
        return this;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public MaiSendBox setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
        return this;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public MaiSendBox setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
        return this;
    }

    public String getSendFullName() {
        return sendFullName;
    }

    public MaiSendBox setSendFullName(String sendFullName) {
        this.sendFullName = sendFullName;
        return this;
    }

    public String getCopyUserIds() {
        return copyUserIds;
    }

    public MaiSendBox setCopyUserIds(String copyUserIds) {
        this.copyUserIds = copyUserIds;
        return this;
    }

    public String getCopyUserNames() {
        return copyUserNames;
    }

    public MaiSendBox setCopyUserNames(String copyUserNames) {
        this.copyUserNames = copyUserNames;
        return this;
    }

    public String getCopyFullNames() {
        return copyFullNames;
    }

    public MaiSendBox setCopyFullNames(String copyFullNames) {
        this.copyFullNames = copyFullNames;
        return this;
    }

    public Integer getIsCopySmsRemind() {
        return isCopySmsRemind;
    }

    public MaiSendBox setIsCopySmsRemind(Integer isCopySmsRemind) {
        this.isCopySmsRemind = isCopySmsRemind;
        return this;
    }

    public Integer getHasCopySmsRemind() {
        return hasCopySmsRemind;
    }

    public MaiSendBox setHasCopySmsRemind(Integer hasCopySmsRemind) {
        this.hasCopySmsRemind = hasCopySmsRemind;
        return this;
    }

    public String getSubjectText() {
        return subjectText;
    }

    public MaiSendBox setSubjectText(String subjectText) {
        this.subjectText = subjectText;
        return this;
    }

    public String getMailContent() {
        return mailContent;
    }

    public MaiSendBox setMailContent(String mailContent) {
        this.mailContent = mailContent;
        return this;
    }

    public Integer getMailSize() {
        return mailSize;
    }

    public MaiSendBox setMailSize(Integer mailSize) {
        this.mailSize = mailSize;
        return this;
    }

    public Integer getIsReceipt() {
        return isReceipt;
    }

    public MaiSendBox setIsReceipt(Integer isReceipt) {
        this.isReceipt = isReceipt;
        return this;
    }

    public Integer getIsReceiptMail() {
        return isReceiptMail;
    }

    public MaiSendBox setIsReceiptMail(Integer isReceiptMail) {
        this.isReceiptMail = isReceiptMail;
        return this;
    }

    public Integer getHasReceiptReceiveCount() {
        return hasReceiptReceiveCount;
    }

    public MaiSendBox setHasReceiptReceiveCount(Integer hasReceiptReceiveCount) {
        this.hasReceiptReceiveCount = hasReceiptReceiveCount;
        return this;
    }

    public Integer getHasReceiptCopyCount() {
        return hasReceiptCopyCount;
    }

    public MaiSendBox setHasReceiptCopyCount(Integer hasReceiptCopyCount) {
        this.hasReceiptCopyCount = hasReceiptCopyCount;
        return this;
    }

    public Integer getIsRevoke() {
        return isRevoke;
    }

    public MaiSendBox setIsRevoke(Integer isRevoke) {
        this.isRevoke = isRevoke;
        return this;
    }

    public Date getRevokeTime() {
        return revokeTime;
    }

    public MaiSendBox setRevokeTime(Date revokeTime) {
        this.revokeTime = revokeTime;
        return this;
    }

    public Integer getIsScrap() {
        return isScrap;
    }

    public MaiSendBox setIsScrap(Integer isScrap) {
        this.isScrap = isScrap;
        return this;
    }

    public Date getScrapTime() {
        return scrapTime;
    }

    public MaiSendBox setScrapTime(Date scrapTime) {
        this.scrapTime = scrapTime;
        return this;
    }

    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public MaiSendBox setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
        return this;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public MaiSendBox setSendTime(Date sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public Integer getIsSendSuccess() {
        return isSendSuccess;
    }

    public MaiSendBox setIsSendSuccess(Integer isSendSuccess) {
        this.isSendSuccess = isSendSuccess;
        return this;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public MaiSendBox setWeekDay(String weekDay) {
        this.weekDay = weekDay;
        return this;
    }

    public Integer getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Integer isUrgent) {
        this.isUrgent = isUrgent;
    }
}
