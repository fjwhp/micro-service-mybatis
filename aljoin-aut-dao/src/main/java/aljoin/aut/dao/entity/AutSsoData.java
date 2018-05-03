package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 单点登录数据同步表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-11
 */
public class AutSsoData extends Entity<AutSsoData> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 用户名-账号
     */
    private String userName;
    /**
     * 登录密码(MD5)-非本系统的真正密码
     */
    private String userPwd;
    /**
     * 系统名称
     */
    private String sysName;

    public Long getUserId() {
        return userId;
    }

    public AutSsoData setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutSsoData setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public AutSsoData setUserPwd(String userPwd) {
        this.userPwd = userPwd;
        return this;
    }

    public String getSysName() {
        return sysName;
    }

    public AutSsoData setSysName(String sysName) {
        this.sysName = sysName;
        return this;
    }

}
