package aljoin.aut.dao.object;

import java.io.Serializable;

/**
 * 
 * 用户值对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:08:24
 */
public class AutUserAndPubVo implements Serializable {

    /**
     * 
     */
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

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getIsAccountExpired() {
        return isAccountExpired;
    }

    public void setIsAccountExpired(Integer isAccountExpired) {
        this.isAccountExpired = isAccountExpired;
    }

    public Integer getIsAccountLocked() {
        return isAccountLocked;
    }

    public void setIsAccountLocked(Integer isAccountLocked) {
        this.isAccountLocked = isAccountLocked;
    }

    public Integer getIsCredentialsExpired() {
        return isCredentialsExpired;
    }

    public void setIsCredentialsExpired(Integer isCredentialsExpired) {
        this.isCredentialsExpired = isCredentialsExpired;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getLawNumber() {
        return lawNumber;
    }

    public void setLawNumber(String lawNumber) {
        this.lawNumber = lawNumber;
    }

    public String getChestCardNumber() {
        return chestCardNumber;
    }

    public void setChestCardNumber(String chestCardNumber) {
        this.chestCardNumber = chestCardNumber;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

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
}
