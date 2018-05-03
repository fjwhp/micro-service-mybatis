package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 名片表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public class AutCard extends Entity<AutCard> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 名片分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    /**
     * 职务名称
     */
    private String positionName;
    /**
     * 姓名
     */
    private String userName;
    /**
     * 1-男，2-女
     */
    private Integer gender;
    /**
     * 单位名称
     */
    private String companyName;
    /**
     * 单位电话
     */
    private String companyTel;
    /**
     * 单位传真
     */
    private String companyFax;
    /**
     * 单位地址
     */
    private String companyAddress;
    /**
     * 手机号码
     */
    private String phoneNumber;
    /**
     * 微信
     */
    private String wechatNumber;
    /**
     * qq
     */
    private String qqNumber;
    /**
     * msn
     */
    private String msnNumber;
    /**
     * 电子邮件
     */
    private String userMail;
    /**
     * 备注
     */
    private String remark;

    public Integer getIsActive() {
        return isActive;
    }

    public AutCard setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public AutCard setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public String getPositionName() {
        return positionName;
    }

    public AutCard setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutCard setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public AutCard setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public String getCompanyName() {
        return companyName;
    }

    public AutCard setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public String getCompanyTel() {
        return companyTel;
    }

    public AutCard setCompanyTel(String companyTel) {
        this.companyTel = companyTel;
        return this;
    }

    public String getCompanyFax() {
        return companyFax;
    }

    public AutCard setCompanyFax(String companyFax) {
        this.companyFax = companyFax;
        return this;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public AutCard setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public AutCard setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getWechatNumber() {
        return wechatNumber;
    }

    public AutCard setWechatNumber(String wechatNumber) {
        this.wechatNumber = wechatNumber;
        return this;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public AutCard setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
        return this;
    }

    public String getMsnNumber() {
        return msnNumber;
    }

    public AutCard setMsnNumber(String msnNumber) {
        this.msnNumber = msnNumber;
        return this;
    }

    public String getUserMail() {
        return userMail;
    }

    public AutCard setUserMail(String userMail) {
        this.userMail = userMail;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public AutCard setRemark(String remark) {
        this.remark = remark;
        return this;
    }

}
