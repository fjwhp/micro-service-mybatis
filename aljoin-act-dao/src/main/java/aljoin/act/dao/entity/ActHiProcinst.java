package aljoin.act.dao.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * 
 * (实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public class ActHiProcinst {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    @TableId("ID_")
    private String id;
    @TableField("PROC_INST_ID_")
    private String procInstId;
    @TableField("BUSINESS_KEY_")
    private String businessKey;
    @TableField("PROC_DEF_ID_")
    private String procDefId;
    @TableField("START_TIME_")
    private Date startTime;
    @TableField("END_TIME_")
    private Date endTime;
    @TableField("DURATION_")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long duration;
    @TableField("START_USER_ID_")
    private String startUserId;
    @TableField("START_ACT_ID_")
    private String startActId;
    @TableField("END_ACT_ID_")
    private String endActId;
    @TableField("SUPER_PROCESS_INSTANCE_ID_")
    private String superProcessInstanceId;
    @TableField("DELETE_REASON_")
    private String deleteReason;
    @TableField("TENANT_ID_")
    private String tenantId;
    @TableField("NAME_")
    private String name;

    public String getId() {
        return id;
    }

    public ActHiProcinst setId(String id) {
        this.id = id;
        return this;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActHiProcinst setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public ActHiProcinst setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActHiProcinst setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ActHiProcinst setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ActHiProcinst setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Long getDuration() {
        return duration;
    }

    public ActHiProcinst setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public String getStartUserId() {
        return startUserId;
    }

    public ActHiProcinst setStartUserId(String startUserId) {
        this.startUserId = startUserId;
        return this;
    }

    public String getStartActId() {
        return startActId;
    }

    public ActHiProcinst setStartActId(String startActId) {
        this.startActId = startActId;
        return this;
    }

    public String getEndActId() {
        return endActId;
    }

    public ActHiProcinst setEndActId(String endActId) {
        this.endActId = endActId;
        return this;
    }

    public String getSuperProcessInstanceId() {
        return superProcessInstanceId;
    }

    public ActHiProcinst setSuperProcessInstanceId(String superProcessInstanceId) {
        this.superProcessInstanceId = superProcessInstanceId;
        return this;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public ActHiProcinst setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public ActHiProcinst setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActHiProcinst setName(String name) {
        this.name = name;
        return this;
    }

}
