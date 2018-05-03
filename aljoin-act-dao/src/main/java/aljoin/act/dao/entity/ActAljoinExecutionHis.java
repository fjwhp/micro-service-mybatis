package aljoin.act.dao.entity;

import java.util.Date;

/**
 * 
 * 流程实例(执行流)表(历史表)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-19
 */
public class ActAljoinExecutionHis {

    /**
     * 主键ID
     */
    private String execId;
    /**
     * 数据版本
     */
    private Integer rev;
    /**
     * 流程实例ID(一个实例可能有多个执行流，该字段表示执行流所属的流程实例)
     */
    private String procInstId;
    /**
     * 启动流程时指定的业务主键
     */
    private String businessKey;
    /**
     * 归属流程实例ID
     */
    private String parentId;
    /**
     * 流程定义ID
     */
    private String procDefId;
    /**
     * 父执行流ID
     */
    private String superExec;
    /**
     * 当前执行流行为ID，ID在流程文件中定义
     */
    private String actId;
    /**
     * 是否活跃
     */
    private Integer isActive;
    /**
     * 执行流是否在并行
     */
    private Integer isConcurrent;
    /**
     * 是否在执行流范围内
     */
    private Integer isScope;
    /**
     * 是否在事件范围内
     */
    private Integer isEventScope;
    /**
     * 表示流程的中断状态
     */
    private Integer suspensionState;
    /**
     * 流程实体缓存，0-7
     */
    private Integer cachedEntState;
    private String tenantId;
    /**
     * 流程实例、执行流ID
     */
    private String name;
    /**
     * 锁定时间
     */
    private Date lockTime;

    public Integer getRev() {
        return rev;
    }

    public ActAljoinExecutionHis setRev(Integer rev) {
        this.rev = rev;
        return this;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActAljoinExecutionHis setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public ActAljoinExecutionHis setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
        return this;
    }

    public String getParentId() {
        return parentId;
    }

    public ActAljoinExecutionHis setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActAljoinExecutionHis setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public String getSuperExec() {
        return superExec;
    }

    public ActAljoinExecutionHis setSuperExec(String superExec) {
        this.superExec = superExec;
        return this;
    }

    public String getActId() {
        return actId;
    }

    public ActAljoinExecutionHis setActId(String actId) {
        this.actId = actId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinExecutionHis setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getIsConcurrent() {
        return isConcurrent;
    }

    public ActAljoinExecutionHis setIsConcurrent(Integer isConcurrent) {
        this.isConcurrent = isConcurrent;
        return this;
    }

    public Integer getIsScope() {
        return isScope;
    }

    public ActAljoinExecutionHis setIsScope(Integer isScope) {
        this.isScope = isScope;
        return this;
    }

    public Integer getIsEventScope() {
        return isEventScope;
    }

    public ActAljoinExecutionHis setIsEventScope(Integer isEventScope) {
        this.isEventScope = isEventScope;
        return this;
    }

    public Integer getSuspensionState() {
        return suspensionState;
    }

    public ActAljoinExecutionHis setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
        return this;
    }

    public Integer getCachedEntState() {
        return cachedEntState;
    }

    public ActAljoinExecutionHis setCachedEntState(Integer cachedEntState) {
        this.cachedEntState = cachedEntState;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public ActAljoinExecutionHis setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActAljoinExecutionHis setName(String name) {
        this.name = name;
        return this;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public ActAljoinExecutionHis setLockTime(Date lockTime) {
        this.lockTime = lockTime;
        return this;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

}
