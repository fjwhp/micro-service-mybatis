package aljoin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 
 * 数据源配置
 *
 * @author：zhongjyy
 *
 * @date：2017年6月5日 下午6:38:51
 */
@ConfigurationProperties(prefix = "aljoin.setting")
public class AljoinSetting {

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 是否使用密码登录
     */
    private String isUsePwdLogin;
    /**
     * 是否使用验证码登录
     */
    private String isUseValidateCode;
    /**
     * 是否使用rememberMe
     */
    private String isUseRememberMe;

    /**
     * 是否使用单点登录
     */
    private String isUseSSO;

    /**
     * CAS服务地址
     */
    private String casUrl;
    /**
     * CAS服务登录地址
     */
    private String casLoginUrl;
    /**
     * CAS服务登出地址
     */
    private String casLogoutUrl;
    /**
     * 应用访问地址
     */
    private String appUrl;
    /**
     * 应用登录地址
     */
    private String appLoginUrl;
    /**
     * 应用登出地址
     */
    private String appLogoutUrl;

    /**
     * mas机ip地址
     */
    private String masIP;
    /**
     * mas机数据库名称
     */
    private String masDbName;
    /**
     * mas机登录用户名
     */
    private String masLoginUserName;
    /**
     * mas机登录密码
     */
    private String masLoginPwd;
    /**
     * mas机接口编码
     */
    private String masApiCode;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getIsUseSSO() {
        return isUseSSO;
    }

    public void setIsUseSSO(String isUseSSO) {
        this.isUseSSO = isUseSSO;
    }

    public String getCasUrl() {
        return casUrl;
    }

    public void setCasUrl(String casUrl) {
        this.casUrl = casUrl;
    }

    public String getCasLoginUrl() {
        return casLoginUrl;
    }

    public void setCasLoginUrl(String casLoginUrl) {
        this.casLoginUrl = casLoginUrl;
    }

    public String getCasLogoutUrl() {
        return casLogoutUrl;
    }

    public void setCasLogoutUrl(String casLogoutUrl) {
        this.casLogoutUrl = casLogoutUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppLoginUrl() {
        return appLoginUrl;
    }

    public void setAppLoginUrl(String appLoginUrl) {
        this.appLoginUrl = appLoginUrl;
    }

    public String getAppLogoutUrl() {
        return appLogoutUrl;
    }

    public void setAppLogoutUrl(String appLogoutUrl) {
        this.appLogoutUrl = appLogoutUrl;
    }

    public String getIsUsePwdLogin() {
        return isUsePwdLogin;
    }

    public void setIsUsePwdLogin(String isUsePwdLogin) {
        this.isUsePwdLogin = isUsePwdLogin;
    }

    public String getIsUseValidateCode() {
        return isUseValidateCode;
    }

    public void setIsUseValidateCode(String isUseValidateCode) {
        this.isUseValidateCode = isUseValidateCode;
    }

    public String getIsUseRememberMe() {
        return isUseRememberMe;
    }

    public void setIsUseRememberMe(String isUseRememberMe) {
        this.isUseRememberMe = isUseRememberMe;
    }

    public String getMasIP() {
        return masIP;
    }

    public void setMasIP(String masIP) {
        this.masIP = masIP;
    }

    public String getMasDbName() {
        return masDbName;
    }

    public void setMasDbName(String masDbName) {
        this.masDbName = masDbName;
    }

    public String getMasLoginUserName() {
        return masLoginUserName;
    }

    public void setMasLoginUserName(String masLoginUserName) {
        this.masLoginUserName = masLoginUserName;
    }

    public String getMasLoginPwd() {
        return masLoginPwd;
    }

    public void setMasLoginPwd(String masLoginPwd) {
        this.masLoginPwd = masLoginPwd;
    }

    public String getMasApiCode() {
        return masApiCode;
    }

    public void setMasApiCode(String masApiCode) {
        this.masApiCode = masApiCode;
    }

}
