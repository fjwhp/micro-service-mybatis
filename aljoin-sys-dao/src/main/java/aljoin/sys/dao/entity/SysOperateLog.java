package aljoin.sys.dao.entity;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;

/**
 * 
 * 操作日志表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-06-05
 */
public class SysOperateLog extends Entity<SysOperateLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 操作用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long operateUserId;
    /**
     * 操作用户账号
     */
    private String operateUserName;
    /**
     * 操作用户IP
     */
    private String operateUserIp;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 请求地址
     */
    private String requestAddress;
    /**
     * 操作日志名称
     */
    private String operateLogName;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 操作明细说明
     */
    private String operateDetailDesc;

    public Long getOperateUserId() {
        return operateUserId;
    }

    public SysOperateLog setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
        return this;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public SysOperateLog setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
        return this;
    }

    public String getOperateUserIp() {
        return operateUserIp;
    }

    public SysOperateLog setOperateUserIp(String operateUserIp) {
        this.operateUserIp = operateUserIp;
        return this;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public SysOperateLog setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    public String getRequestAddress() {
        return requestAddress;
    }

    public SysOperateLog setRequestAddress(String requestAddress) {
        this.requestAddress = requestAddress;
        return this;
    }

    public String getOperateLogName() {
        return operateLogName;
    }

    public SysOperateLog setOperateLogName(String operateLogName) {
        this.operateLogName = operateLogName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public SysOperateLog setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getOperateDetailDesc() {
        return operateDetailDesc;
    }

    public SysOperateLog setOperateDetailDesc(String operateDetailDesc) {
        this.operateDetailDesc = operateDetailDesc;
        return this;
    }

}
