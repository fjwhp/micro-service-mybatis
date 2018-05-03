package aljoin.aut.dao.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 移动端用户登录权限表（特殊表）(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-19
 */
public class AutAppUserLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 登录令牌,大写(每次登录成功后修改并返回)
     */
    private String token;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 加密键,10位随机数(每次登录成功后修改并返回)
     */
    private String secret;
    /**
     * 最后访问时间
     */
    private Date lastAccessTime;
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    /**
     * 合强RSA私钥（用来解密）
     */
    private String aljoinPrivateKey;
    /**
     * 对方RSA公钥（用来加密）
     */
    private String otherPublicKey;
    /**
     * 登录次数
     */
    private Integer loginCount;

    public String getToken() {
        return token;
    }

    public AutAppUserLogin setToken(String token) {
        this.token = token;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AutAppUserLogin setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutAppUserLogin setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getSecret() {
        return secret;
    }

    public AutAppUserLogin setSecret(String secret) {
        this.secret = secret;
        return this;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public AutAppUserLogin setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
        return this;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public AutAppUserLogin setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return this;
    }

    public String getAljoinPrivateKey() {
        return aljoinPrivateKey;
    }

    public AutAppUserLogin setAljoinPrivateKey(String aljoinPrivateKey) {
        this.aljoinPrivateKey = aljoinPrivateKey;
        return this;
    }

    public String getOtherPublicKey() {
        return otherPublicKey;
    }

    public AutAppUserLogin setOtherPublicKey(String otherPublicKey) {
        this.otherPublicKey = otherPublicKey;
        return this;
    }

    public Integer getLoginCount() {
        return loginCount;
    }

    public AutAppUserLogin setLoginCount(Integer loginCount) {
        this.loginCount = loginCount;
        return this;
    }

}
