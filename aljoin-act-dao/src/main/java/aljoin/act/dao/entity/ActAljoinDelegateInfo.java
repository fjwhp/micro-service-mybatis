package aljoin.act.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-14
 */
public class ActAljoinDelegateInfo extends Entity<ActAljoinDelegateInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 委托人用户ID(顶级)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;
    /**
     * 委托人名称(顶级)
     */
    private String ownerUserFullname;
    /**
     * 委托人用户ID(直接)
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastUserId;
    /**
     * 委托人名称(直接)
     */
    private String lastUserFullname;
    /**
     * 被委托人用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long assigneeUserId;
    /**
     * 被委托人名称
     */
    private String assigneeUserFullname;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 任务key
     */
    private String taskKey;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 流程ID
     */
    private String processId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 任务是否已完成
     */
    private Integer hasDo;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 委派(源头)链如：张三->李四->王五
     */
    private String delegateUserNames;
    /**
     * 委派(源头)链如：张三id,李四id,王五id(逗号分隔)
     */
    private String delegateUserIds;
    /**
     * 引起委托的任务委托表的主键ID链(逗号分隔)
     */
    private String delegateIds;
    /**
     * 直接任务委托表的主键ID链
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long firstDelegateId;
    /**
     * 顶级任务委托表的主键ID链
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long lastDelegateId;
    /**
     * 流程分类名称
     */
    private String processCategoryName;
    /**
     * 流程分类id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processCategoryId;
    /**
     * 紧急状态名称：一般，紧急，加急
     */
    private String urgentStatus;
    /**
     * 流程标题
     */
    private String processTitle;
    /**
     * 处理时间
     */
    private Date handleTime;
    /**
     * 填报时间
     */
    private Date startTime;
    /**
     * 流程发起者id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long processStarterId;
    /**
     * 流程发起者姓名
     */
    private String processStarterFullName;
    /**
     * 任务委托前被委托人是否是办理人
     */
    private Integer isSelfTask;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public ActAljoinDelegateInfo setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public String getOwnerUserFullname() {
        return ownerUserFullname;
    }

    public ActAljoinDelegateInfo setOwnerUserFullname(String ownerUserFullname) {
        this.ownerUserFullname = ownerUserFullname;
        return this;
    }

    public Long getLastUserId() {
        return lastUserId;
    }

    public ActAljoinDelegateInfo setLastUserId(Long lastUserId) {
        this.lastUserId = lastUserId;
        return this;
    }

    public String getLastUserFullname() {
        return lastUserFullname;
    }

    public ActAljoinDelegateInfo setLastUserFullname(String lastUserFullname) {
        this.lastUserFullname = lastUserFullname;
        return this;
    }

    public Long getAssigneeUserId() {
        return assigneeUserId;
    }

    public ActAljoinDelegateInfo setAssigneeUserId(Long assigneeUserId) {
        this.assigneeUserId = assigneeUserId;
        return this;
    }

    public String getAssigneeUserFullname() {
        return assigneeUserFullname;
    }

    public ActAljoinDelegateInfo setAssigneeUserFullname(String assigneeUserFullname) {
        this.assigneeUserFullname = assigneeUserFullname;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public ActAljoinDelegateInfo setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public ActAljoinDelegateInfo setTaskKey(String taskKey) {
        this.taskKey = taskKey;
        return this;
    }

    public String getTaskName() {
        return taskName;
    }

    public ActAljoinDelegateInfo setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public ActAljoinDelegateInfo setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public ActAljoinDelegateInfo setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public Integer getHasDo() {
        return hasDo;
    }

    public ActAljoinDelegateInfo setHasDo(Integer hasDo) {
        this.hasDo = hasDo;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinDelegateInfo setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getDelegateUserNames() {
        return delegateUserNames;
    }

    public ActAljoinDelegateInfo setDelegateUserNames(String delegateUserNames) {
        this.delegateUserNames = delegateUserNames;
        return this;
    }

    public String getDelegateUserIds() {
        return delegateUserIds;
    }

    public ActAljoinDelegateInfo setDelegateUserIds(String delegateUserIds) {
        this.delegateUserIds = delegateUserIds;
        return this;
    }

    public String getDelegateIds() {
        return delegateIds;
    }

    public ActAljoinDelegateInfo setDelegateIds(String delegateIds) {
        this.delegateIds = delegateIds;
        return this;
    }

    public Long getFirstDelegateId() {
        return firstDelegateId;
    }

    public ActAljoinDelegateInfo setFirstDelegateId(Long firstDelegateId) {
        this.firstDelegateId = firstDelegateId;
        return this;
    }

    public Long getLastDelegateId() {
        return lastDelegateId;
    }

    public ActAljoinDelegateInfo setLastDelegateId(Long lastDelegateId) {
        this.lastDelegateId = lastDelegateId;
        return this;
    }

    public String getProcessCategoryName() {
        return processCategoryName;
    }

    public ActAljoinDelegateInfo setProcessCategoryName(String processCategoryName) {
        this.processCategoryName = processCategoryName;
        return this;
    }

    public Long getProcessCategoryId() {
        return processCategoryId;
    }

    public ActAljoinDelegateInfo setProcessCategoryId(Long processCategoryId) {
        this.processCategoryId = processCategoryId;
        return this;
    }

    public String getUrgentStatus() {
        return urgentStatus;
    }

    public ActAljoinDelegateInfo setUrgentStatus(String urgentStatus) {
        this.urgentStatus = urgentStatus;
        return this;
    }

    public String getProcessTitle() {
        return processTitle;
    }

    public ActAljoinDelegateInfo setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
        return this;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public ActAljoinDelegateInfo setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ActAljoinDelegateInfo setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getProcessStarterId() {
        return processStarterId;
    }

    public ActAljoinDelegateInfo setProcessStarterId(Long processStarterId) {
        this.processStarterId = processStarterId;
        return this;
    }

    public String getProcessStarterFullName() {
        return processStarterFullName;
    }

    public ActAljoinDelegateInfo setProcessStarterFullName(String processStarterFullName) {
        this.processStarterFullName = processStarterFullName;
        return this;
    }

    public Integer getIsSelfTask() {
        return isSelfTask;
    }

    public void setIsSelfTask(Integer isSelfTask) {
        this.isSelfTask = isSelfTask;
    }

}
