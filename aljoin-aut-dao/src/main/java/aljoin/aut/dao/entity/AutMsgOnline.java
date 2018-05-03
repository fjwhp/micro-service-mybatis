package aljoin.aut.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 在线消息表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-28
 */
public class AutMsgOnline extends Entity<AutMsgOnline> {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private String msgType;
    /**
     * 消息内容
     */
    private String msgContent;
    /**
     * 消息来源用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fromUserId;
    /**
     * 消息来源用户账号
     */
    private String fromUserName;
    /**
     * 消息来源用户名称
     */
    private String fromUserFullName;
    /**
     * 接收人账号
     */
    private String receiveUserName;
    /**
     * 接收消息用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiveUserId;
    /**
     * 是否已经阅读
     */
    private Integer isRead;
    /**
     * 阅读时间
     */
    private Date readTime;
    /**
     * 业务主键，通过本字段解析点击消息需要进行的操作
     */
    private String businessKey;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 跳转链接
     */
    private String goUrl;

    public String getMsgType() {
        return msgType;
    }

    public AutMsgOnline setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public AutMsgOnline setMsgContent(String msgContent) {
        this.msgContent = msgContent;
        return this;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public AutMsgOnline setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
        return this;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public AutMsgOnline setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
        return this;
    }

    public String getFromUserFullName() {
        return fromUserFullName;
    }

    public AutMsgOnline setFromUserFullName(String fromUserFullName) {
        this.fromUserFullName = fromUserFullName;
        return this;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public AutMsgOnline setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
        return this;
    }

    public Long getReceiveUserId() {
        return receiveUserId;
    }

    public AutMsgOnline setReceiveUserId(Long receiveUserId) {
        this.receiveUserId = receiveUserId;
        return this;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public AutMsgOnline setIsRead(Integer isRead) {
        this.isRead = isRead;
        return this;
    }

    public Date getReadTime() {
        return readTime;
    }

    public AutMsgOnline setReadTime(Date readTime) {
        this.readTime = readTime;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public AutMsgOnline setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutMsgOnline setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getGoUrl() {
        return goUrl;
    }

    public AutMsgOnline setGoUrl(String goUrl) {
        this.goUrl = goUrl;
        return this;
    }

}
