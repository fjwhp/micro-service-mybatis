package aljoin.act.dao.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;

import java.io.Serializable;
import java.util.Date;

/**
 * 执行流对象（实体类）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class ActRunTimeExecution implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID_")
    private String id;
    @TableField("REV_")
    private Integer rev;
    @TableField("PROC_INST_ID_")
    private String procInstId;
    @TableField("BUSINESS_KEY_")
    private String businessKey;
    @TableField("PARENT_ID_")
    private String parentId;
    @TableField("PROC_DEF_ID_")
    private String procDefId;
    @TableField("SUPER_EXEC_")
    private String superExec;
    @TableField("ACT_ID_")
    private String actId;
    @TableField("IS_ACTIVE_")
    private Integer isActive;
    @TableField("IS_CONCURRENT_")
    private Integer isConcurrent;
    @TableField("IS_SCOPE_")
    private Integer isScope;
    @TableField("IS_EVENT_SCOPE_")
    private Integer isEventScope;
    @TableField("SUSPENSION_STATE_")
    private Integer suspensionState;
    @TableField("CACHED_ENT_STATE_")
    private Integer cachedEntState;
    @TableField("TENANT_ID_")
    private String tenantId;
    @TableField("NAME_")
    private String name;
    @TableField("LOCK_TIME_")
    private Date lockTime;

    public String getId() {
        return id;
    }

    public ActRunTimeExecution setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getRev() {
        return rev;
    }

    public ActRunTimeExecution setRev(Integer rev) {
        this.rev = rev;
        return this;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActRunTimeExecution setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public ActRunTimeExecution setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public ActRunTimeExecution setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActRunTimeExecution setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public String getSuperExec() {
        return superExec;
    }

    public ActRunTimeExecution setSuperExec(String superExec) {
        this.superExec = superExec;
        return this;
    }

    public String getActId() {
        return actId;
    }

    public ActRunTimeExecution setActId(String actId) {
        this.actId = actId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActRunTimeExecution setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getIsConcurrent() {
        return isConcurrent;
    }

    public ActRunTimeExecution setIsConcurrent(Integer isConcurrent) {
        this.isConcurrent = isConcurrent;
        return this;
    }

    public Integer getIsScope() {
        return isScope;
    }

    public ActRunTimeExecution setIsScope(Integer isScope) {
        this.isScope = isScope;
        return this;
    }

    public Integer getIsEventScope() {
        return isEventScope;
    }

    public ActRunTimeExecution setIsEventScope(Integer isEventScope) {
        this.isEventScope = isEventScope;
        return this;
    }

    public Integer getSuspensionState() {
        return suspensionState;
    }

    public ActRunTimeExecution setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
        return this;
    }

    public Integer getCachedEntState() {
        return cachedEntState;
    }

    public ActRunTimeExecution setCachedEntState(Integer cachedEntState) {
        this.cachedEntState = cachedEntState;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public ActRunTimeExecution setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActRunTimeExecution setName(String name) {
        this.name = name;
        return this;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public ActRunTimeExecution setLockTime(Date lockTime) {
        this.lockTime = lockTime;
        return this;
    }

}
