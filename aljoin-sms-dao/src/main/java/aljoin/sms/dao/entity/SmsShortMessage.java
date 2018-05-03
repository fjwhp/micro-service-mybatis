package aljoin.sms.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;

/**
 * 
 * 短信表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public class SmsShortMessage extends Entity<SmsShortMessage> {

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
     * 发送人数
     */
    private Integer sendNumber;
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

    public SmsShortMessage setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public SmsShortMessage setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SmsShortMessage setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public SmsShortMessage setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public SmsShortMessage setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public Integer getSendNumber() {
        return sendNumber;
    }

    public SmsShortMessage setSendNumber(Integer sendNumber) {
        this.sendNumber = sendNumber;
        return this;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public SmsShortMessage setSendTime(Date sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public SmsShortMessage setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public SmsShortMessage setRemark(String remark) {
        this.remark = remark;
        return this;
    }

}
