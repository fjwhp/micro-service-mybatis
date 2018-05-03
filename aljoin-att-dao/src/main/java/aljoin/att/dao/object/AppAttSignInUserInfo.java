package aljoin.att.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 签到统计(实体类).
 * 
 * @author zhongjy.
 * 
 * @date 2018年2月9日
 */
public class AppAttSignInUserInfo {
    /**
     * 处室
     */
    private String deptName;
    /**
     * 用户名
     */
    private String signUserName;
    /**
     * 用户id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long signUserId;

    private String signPathTime;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSignUserName() {
        return signUserName;
    }

    public void setSignUserName(String signUserName) {
        this.signUserName = signUserName;
    }

    public Long getSignUserId() {
        return signUserId;
    }

    public void setSignUserId(Long signUserId) {
        this.signUserId = signUserId;
    }

    public String getSignPathTime() {
        return signPathTime;
    }

    public void setSignPathTime(String signPathTime) {
        this.signPathTime = signPathTime;
    }
}
