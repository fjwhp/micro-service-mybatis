package aljoin.aut.dao.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;

/**
 * 
 * 用户信息表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-06
 */
public class AutUserInfo extends Entity<AutUserInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 键
     */
    private String userKey;
    /**
     * 值
     */
    private String userValue;
    /**
     * 描述
     */
    private String description;

    public Integer getIsActive() {
        return isActive;
    }

    public AutUserInfo setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AutUserInfo setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserValue() {
        return userValue;
    }

    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
