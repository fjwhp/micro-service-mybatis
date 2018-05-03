package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 加签信息（实体类）
 *
 * @author：wangj
 *
 * @date：2018年03月19日
 */
public class ActAljoinTaskSignInfo extends Entity<ActAljoinTaskSignInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程表ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bpmnId;
    /**
     * 流程实例ID
     */
    private String processInstanceId;
    /**
     * 流程定义ID
     */
    private String processDefId;
    /**
     * 执行流ID
     */
    private String executionId;
    /**
     * 任务ID（原来任务ID）
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
     * 加签产生的任务ID
     */
    private String signTaskId;
    /**
     * 加签任务ID路径（从开始到结束）
     */
    private String taskIds;
    /**
     * 任务拥有者ID（最开始的任务拥有者，第一次开始加签人）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long taskOwnerId;
    /**
     * 任务拥有者姓名（最开始的任务拥有者，第一次开始加签人）
     */
    private String taskOwnerName;
    /**
     * 加签者用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long taskSignUserId;
    /**
     * 加签者用户姓名
     */
    private String taskSignUserName;
    /**
     * 被加签者用户ID，task_sign_user_ids分割后的结果
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long taskSignedUserId;
    /**
     * 被加签者用户姓名，task_sign_user_names分割后的结果
     */
    private String taskSignedUserName;
    /**
     * 是否返回流程的拥有者,0-提交进入下一环节，1-返回流程拥有者
     */
    private Integer isBackOwner;
    /**
     * 加签用户ID路径（从开始到结束，逗号分隔）
     */
    private String taskSignUserIds;
    /**
     * 加签用户姓名路径（从开始到结束，逗号分隔）
     */
    private String taskSignUserNames;
    /**
     * 完成类型：0-没做，1-加签完成，2-提交完成
     */
    private Integer finishType;

    /**
     * 所有加签任务ID路径（从开始到结束）
     */
    private String allTaskIds;

    public Long getBpmnId() {
        return bpmnId;
    }

    public ActAljoinTaskSignInfo setBpmnId(Long bpmnId) {
        this.bpmnId = bpmnId;
        return this;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public ActAljoinTaskSignInfo setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public String getProcessDefId() {
        return processDefId;
    }

    public ActAljoinTaskSignInfo setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
        return this;
    }

    public String getExecutionId() {
        return executionId;
    }

    public ActAljoinTaskSignInfo setExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public ActAljoinTaskSignInfo setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public ActAljoinTaskSignInfo setTaskKey(String taskKey) {
        this.taskKey = taskKey;
        return this;
    }

    public String getTaskName() {
        return taskName;
    }

    public ActAljoinTaskSignInfo setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public String getSignTaskId() {
        return signTaskId;
    }

    public ActAljoinTaskSignInfo setSignTaskId(String signTaskId) {
        this.signTaskId = signTaskId;
        return this;
    }

    public String getTaskIds() {
        return taskIds;
    }

    public ActAljoinTaskSignInfo setTaskIds(String taskIds) {
        this.taskIds = taskIds;
        return this;
    }

    public Long getTaskOwnerId() {
        return taskOwnerId;
    }

    public ActAljoinTaskSignInfo setTaskOwnerId(Long taskOwnerId) {
        this.taskOwnerId = taskOwnerId;
        return this;
    }

    public String getTaskOwnerName() {
        return taskOwnerName;
    }

    public ActAljoinTaskSignInfo setTaskOwnerName(String taskOwnerName) {
        this.taskOwnerName = taskOwnerName;
        return this;
    }

    public Long getTaskSignUserId() {
        return taskSignUserId;
    }

    public ActAljoinTaskSignInfo setTaskSignUserId(Long taskSignUserId) {
        this.taskSignUserId = taskSignUserId;
        return this;
    }

    public String getTaskSignUserName() {
        return taskSignUserName;
    }

    public ActAljoinTaskSignInfo setTaskSignUserName(String taskSignUserName) {
        this.taskSignUserName = taskSignUserName;
        return this;
    }

    public Long getTaskSignedUserId() {
        return taskSignedUserId;
    }

    public ActAljoinTaskSignInfo setTaskSignedUserId(Long taskSignedUserId) {
        this.taskSignedUserId = taskSignedUserId;
        return this;
    }

    public String getTaskSignedUserName() {
        return taskSignedUserName;
    }

    public ActAljoinTaskSignInfo setTaskSignedUserName(String taskSignedUserName) {
        this.taskSignedUserName = taskSignedUserName;
        return this;
    }

    public Integer getIsBackOwner() {
        return isBackOwner;
    }

    public ActAljoinTaskSignInfo setIsBackOwner(Integer isBackOwner) {
        this.isBackOwner = isBackOwner;
        return this;
    }

    public String getTaskSignUserIds() {
        return taskSignUserIds;
    }

    public ActAljoinTaskSignInfo setTaskSignUserIds(String taskSignUserIds) {
        this.taskSignUserIds = taskSignUserIds;
        return this;
    }

    public String getTaskSignUserNames() {
        return taskSignUserNames;
    }

    public ActAljoinTaskSignInfo setTaskSignUserNames(String taskSignUserNames) {
        this.taskSignUserNames = taskSignUserNames;
        return this;
    }

    public Integer getFinishType() {
        return finishType;
    }

    public ActAljoinTaskSignInfo setFinishType(Integer finishType) {
        this.finishType = finishType;
        return this;
    }

    public String getAllTaskIds() {
        return allTaskIds;
    }

    public void setAllTaskIds(String allTaskIds) {
        this.allTaskIds = allTaskIds;
    }
}
