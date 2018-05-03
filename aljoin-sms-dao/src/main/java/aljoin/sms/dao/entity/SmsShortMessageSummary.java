package aljoin.sms.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;

/**
 * 
 * 短信明细表(实体类).
 * 
 * @author：huangw.
 * 
 * @date： 2018-01-15
 */
public class SmsShortMessageSummary extends Entity<SmsShortMessageSummary> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 主题
     */
    private String theme;
    /**
     * 内容
     */
    private String content;
    /**
     * 收信人ID(多个用分号分隔)
     */
    private String receiverId;
    /**
     * 收信人(多个用分号分隔)
     */
    private String receiverName;
    /**
     * 电话号码
     */
    private String sendNumber;
    /**
     * 发送时间
     */
    private Date sendTime;
    /**
     * 发送状态(0:发送失败 1:发送成功)
     */
    private Integer sendStatus;
    /**
     * 备注
     */
    private String remark;

    public Integer getIsActive() {
        return isActive;
    }

    public SmsShortMessageSummary setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public SmsShortMessageSummary setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SmsShortMessageSummary setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public SmsShortMessageSummary setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public SmsShortMessageSummary setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public String getSendNumber() {
        return sendNumber;
    }

    public SmsShortMessageSummary setSendNumber(String sendNumber) {
        this.sendNumber = sendNumber;
        return this;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public SmsShortMessageSummary setSendTime(Date sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public SmsShortMessageSummary setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public SmsShortMessageSummary setRemark(String remark) {
        this.remark = remark;
        return this;
    }

}
