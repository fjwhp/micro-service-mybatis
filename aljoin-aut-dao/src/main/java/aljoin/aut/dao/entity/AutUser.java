package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 用户表(实体类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:02:31
 */
public class AutUser extends Entity<AutUser> {

    private static final long serialVersionUID = 1L;
    /**
     * 登录密码
     */
    private String userPwd;
    /**
     * 账号(唯一)
     */
    private String userName;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 账号是否过期
     */
    private Integer isAccountExpired;
    /**
     * 账号是否被锁
     */
    private Integer isAccountLocked;
    /**
     * 证书是否过期
     */
    private Integer isCredentialsExpired;
    /**
     * 昵称(显示名称)
     */
    private String fullName;
    /**
     * 用户邮箱
     */
    private String userEmail;

    public String getUserPwd() {
        return userPwd;
    }

    public AutUser setUserPwd(String userPwd) {
        this.userPwd = userPwd;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutUser setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutUser setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getIsAccountExpired() {
        return isAccountExpired;
    }

    public AutUser setIsAccountExpired(Integer isAccountExpired) {
        this.isAccountExpired = isAccountExpired;
        return this;
    }

    public Integer getIsAccountLocked() {
        return isAccountLocked;
    }

    public AutUser setIsAccountLocked(Integer isAccountLocked) {
        this.isAccountLocked = isAccountLocked;
        return this;
    }

    public Integer getIsCredentialsExpired() {
        return isCredentialsExpired;
    }

    public AutUser setIsCredentialsExpired(Integer isCredentialsExpired) {
        this.isCredentialsExpired = isCredentialsExpired;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public AutUser setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
