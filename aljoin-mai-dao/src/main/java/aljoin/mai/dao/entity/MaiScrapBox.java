package aljoin.mai.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 废件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiScrapBox extends Entity<MaiScrapBox> {

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
     * 邮件大小(正文+附件的大小)
     */
    private Integer mailSize;
    /**
     * 主题
     */
    private String subjectText;
    /**
     * 源类型：1-收件箱，2-发件箱
     */
    private Integer orgnlType;
    /**
     * 源ID，针对orgnl_type,对应相应的表：mai_receive_box,mai_send_box
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgnlId;

    public String getReceiveUserIds() {
        return receiveUserIds;
    }

    public MaiScrapBox setReceiveUserIds(String receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
        return this;
    }

    public String getReceiveUserNames() {
        return receiveUserNames;
    }

    public MaiScrapBox setReceiveUserNames(String receiveUserNames) {
        this.receiveUserNames = receiveUserNames;
        return this;
    }

    public String getReceiveFullNames() {
        return receiveFullNames;
    }

    public MaiScrapBox setReceiveFullNames(String receiveFullNames) {
        this.receiveFullNames = receiveFullNames;
        return this;
    }

    public Long getSendUserId() {
        return sendUserId;
    }

    public MaiScrapBox setSendUserId(Long sendUserId) {
        this.sendUserId = sendUserId;
        return this;
    }

    public String getSendUserName() {
        return sendUserName;
    }

    public MaiScrapBox setSendUserName(String sendUserName) {
        this.sendUserName = sendUserName;
        return this;
    }

    public String getSendFullName() {
        return sendFullName;
    }

    public MaiScrapBox setSendFullName(String sendFullName) {
        this.sendFullName = sendFullName;
        return this;
    }

    public Integer getMailSize() {
        return mailSize;
    }

    public MaiScrapBox setMailSize(Integer mailSize) {
        this.mailSize = mailSize;
        return this;
    }

    public String getSubjectText() {
        return subjectText;
    }

    public MaiScrapBox setSubjectText(String subjectText) {
        this.subjectText = subjectText;
        return this;
    }

    public Integer getOrgnlType() {
        return orgnlType;
    }

    public MaiScrapBox setOrgnlType(Integer orgnlType) {
        this.orgnlType = orgnlType;
        return this;
    }

    public Long getOrgnlId() {
        return orgnlId;
    }

    public MaiScrapBox setOrgnlId(Long orgnlId) {
        this.orgnlId = orgnlId;
        return this;
    }

}
