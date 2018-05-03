package aljoin.sms.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 短信草稿表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public class SmsShortMessageDraft extends Entity<SmsShortMessageDraft> {

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

    public Integer getIsActive() {
        return isActive;
    }

    public SmsShortMessageDraft setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getTheme() {
        return theme;
    }

    public SmsShortMessageDraft setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getContent() {
        return content;
    }

    public SmsShortMessageDraft setContent(String content) {
        this.content = content;
        return this;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public SmsShortMessageDraft setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public SmsShortMessageDraft setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public Integer getSendNumber() {
        return sendNumber;
    }

    public SmsShortMessageDraft setSendNumber(Integer sendNumber) {
        this.sendNumber = sendNumber;
        return this;
    }

}
