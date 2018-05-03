package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutUser;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppAutUserPubVO {

    /**
     * 用户表
     */
    private AutUser autUser;

    /**
     * 修改密码时的新密码
     */
    private String newUserPwd;

    /**
     * 修改密码时再次输入的新密码
     */
    private String newUserPwdConfirm;
    /**
     * 用户-多部门
     */
    private String autDeptNames;

    /**
     * 用户-多岗位
     */
    private String positionNames;

    /**
     * 手机号码
     */
    private String phoneNumber;

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

    public String getAutDeptNames() {
        return autDeptNames;
    }

    public void setAutDeptNames(String autDeptNames) {
        this.autDeptNames = autDeptNames;
    }

    public String getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(String positionNames) {
        this.positionNames = positionNames;
    }

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public String getNewUserPwd() {
        return newUserPwd;
    }

    public void setNewUserPwd(String newUserPwd) {
        this.newUserPwd = newUserPwd;
    }

    public String getNewUserPwdConfirm() {
        return newUserPwdConfirm;
    }

    public void setNewUserPwdConfirm(String newUserPwdConfirm) {
        this.newUserPwdConfirm = newUserPwdConfirm;
    }

}
