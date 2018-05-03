package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 用户公共信息表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public class AutUserPub extends Entity<AutUserPub> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 手机号码
     */
    private String phoneNumber;
    /**
     * 电话号码
     */
    private String telNumber;
    /**
     * 传真号码
     */
    private String faxNumber;
    /**
     * 执法证号
     */
    private String lawNumber;
    /**
     * 胸牌号
     */
    private String chestCardNumber;
    /**
     * 头像
     */
    private String userIcon;
    /**
     * 最大邮箱容量(KB)
     */
    private Integer maxMailSize;

    public Long getUserId() {
        return userId;
    }

    public AutUserPub setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AutUserPub setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public AutUserPub setTelNumber(String telNumber) {
        this.telNumber = telNumber;
        return this;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public AutUserPub setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
        return this;
    }

    public String getLawNumber() {
        return lawNumber;
    }

    public AutUserPub setLawNumber(String lawNumber) {
        this.lawNumber = lawNumber;
        return this;
    }

    public String getChestCardNumber() {
        return chestCardNumber;
    }

    public AutUserPub setChestCardNumber(String chestCardNumber) {
        this.chestCardNumber = chestCardNumber;
        return this;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public AutUserPub setUserIcon(String userIcon) {
        this.userIcon = userIcon;
        return this;
    }

    public Integer getMaxMailSize() {
        return maxMailSize;
    }

    public AutUserPub setMaxMailSize(Integer maxMailSize) {
        this.maxMailSize = maxMailSize;
        return this;
    }

}
