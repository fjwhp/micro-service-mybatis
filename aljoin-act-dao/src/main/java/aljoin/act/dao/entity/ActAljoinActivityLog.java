package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;

import java.util.Date;

/**
 *
 * 流程操作日志表(实体类) .
 *
 * @author：wangj.
 *
 * @date： 2017-12-25
 */
public class ActAljoinActivityLog extends Entity<ActAljoinActivityLog> {

    private static final long serialVersionUID = 1L;

    /**
     * 操作用户ID
     */
    private String operateUserId;
    /**
     * 操作用户
     */
    private String operateFullName;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 操作(1：提交 2：回退 3：撤回)
     */
    private Integer operateStatus;
    /**
     * 接收人用户ID(多个用分号分隔)
     */
    private String receiveUserIds;
    /**
     * 接收人用户姓名(多个用分号分隔)
     */
    private String receiveFullNames;
    /**
     * 审批意见
     */
    private String comment;
    /**
     * 上一个任务节点
     */
    private String lastTaskName;
    /**
     * 当前任务节点
     */
    private String currentTaskName;
    /**
     * 下一个任务节点
     */
    private String nextTaskName;
    /**
     * 上一个任务ID
     */
    private String lastTaskId;
    /**
     * 当前任务ID
     */
    private String currentTaskId;
    /**
     * 下一个任务ID
     */
    private String nextTaskId;
    /**
     * 流程定义ID
     */
    private String procDefId;
    /**
     * 上一个任务key
     */
    private String lastTaskDefKey;
    /**
     * 当前一个任务key
     */
    private String currentTaskDefKey;
    /**
     * 下一个任务key
     */
    private String nextTaskDefKey;
    /**
     * 流程实例ID
     */
    private String procInstId;
    /**
     * 执行流ID
     */
    private String executionId;

    public String getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(String operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateFullName() {
        return operateFullName;
    }

    public void setOperateFullName(String operateFullName) {
        this.operateFullName = operateFullName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public ActAljoinActivityLog setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    public Integer getOperateStatus() {
        return operateStatus;
    }

    public ActAljoinActivityLog setOperateStatus(Integer operateStatus) {
        this.operateStatus = operateStatus;
        return this;
    }

    public String getReceiveUserIds() {
        return receiveUserIds;
    }

    public void setReceiveUserIds(String receiveUserIds) {
        this.receiveUserIds = receiveUserIds;
    }

    public String getReceiveFullNames() {
        return receiveFullNames;
    }

    public void setReceiveFullNames(String receiveFullNames) {
        this.receiveFullNames = receiveFullNames;
    }

    public String getComment() {
        return comment;
    }

    public ActAljoinActivityLog setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getLastTaskName() {
        return lastTaskName;
    }

    public ActAljoinActivityLog setLastTaskName(String lastTaskName) {
        this.lastTaskName = lastTaskName;
        return this;
    }

    public String getCurrentTaskName() {
        return currentTaskName;
    }

    public ActAljoinActivityLog setCurrentTaskName(String currentTaskName) {
        this.currentTaskName = currentTaskName;
        return this;
    }

    public String getNextTaskName() {
        return nextTaskName;
    }

    public ActAljoinActivityLog setNextTaskName(String nextTaskName) {
        this.nextTaskName = nextTaskName;
        return this;
    }

    public String getLastTaskId() {
        return lastTaskId;
    }

    public ActAljoinActivityLog setLastTaskId(String lastTaskId) {
        this.lastTaskId = lastTaskId;
        return this;
    }

    public String getCurrentTaskId() {
        return currentTaskId;
    }

    public ActAljoinActivityLog setCurrentTaskId(String currentTaskId) {
        this.currentTaskId = currentTaskId;
        return this;
    }

    public String getNextTaskId() {
        return nextTaskId;
    }

    public ActAljoinActivityLog setNextTaskId(String nextTaskId) {
        this.nextTaskId = nextTaskId;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActAljoinActivityLog setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public String getLastTaskDefKey() {
        return lastTaskDefKey;
    }

    public void setLastTaskDefKey(String lastTaskDefKey) {
        this.lastTaskDefKey = lastTaskDefKey;
    }

    public String getCurrentTaskDefKey() {
        return currentTaskDefKey;
    }

    public void setCurrentTaskDefKey(String currentTaskDefKey) {
        this.currentTaskDefKey = currentTaskDefKey;
    }

    public String getNextTaskDefKey() {
        return nextTaskDefKey;
    }

    public void setNextTaskDefKey(String nextTaskDefKey) {
        this.nextTaskDefKey = nextTaskDefKey;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActAljoinActivityLog setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getExecutionId() {
        return executionId;
    }

    public ActAljoinActivityLog setExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

}
