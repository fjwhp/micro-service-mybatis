package aljoin.act.dao.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;
import java.util.List;

/**
 * 
 * (实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public class ActHiTaskinst {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    @TableId("ID_")
    private String id;
    @TableField("PROC_DEF_ID_")
    private String procDefId;
    @TableField("TASK_DEF_KEY_")
    private String taskDefKey;
    @TableField("PROC_INST_ID_")
    private String procInstId;
    @TableField("EXECUTION_ID_")
    private String executionId;
    @TableField("NAME_")
    private String name;
    @TableField("PARENT_TASK_ID_")
    private String parentTaskId;
    @TableField("DESCRIPTION_")
    private String description;
    @TableField("OWNER_")
    private String owner;
    @TableField("ASSIGNEE_")
    private String assignee;
    @TableField("START_TIME_")
    private Date startTime;
    @TableField("CLAIM_TIME_")
    private Date claimTime;
    @TableField("END_TIME_")
    private Date endTime;
    @TableField("DURATION_")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long duration;
    @TableField("DELETE_REASON_")
    private String deleteReason;
    @TableField("PRIORITY_")
    private Integer priority;
    @TableField("DUE_DATE_")
    private Date dueDate;
    @TableField("FORM_KEY_")
    private String formKey;
    @TableField("CATEGORY_")
    private String category;
    @TableField("TENANT_ID_")
    private String tenantId;

    private List<String> processInstanceIds;

    private String dbType;

    public String getId() {
        return id;
    }

    public ActHiTaskinst setId(String id) {
        this.id = id;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActHiTaskinst setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public ActHiTaskinst setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
        return this;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActHiTaskinst setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getExecutionId() {
        return executionId;
    }

    public ActHiTaskinst setExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActHiTaskinst setName(String name) {
        this.name = name;
        return this;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public ActHiTaskinst setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ActHiTaskinst setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public ActHiTaskinst setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getAssignee() {
        return assignee;
    }

    public ActHiTaskinst setAssignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ActHiTaskinst setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getClaimTime() {
        return claimTime;
    }

    public ActHiTaskinst setClaimTime(Date claimTime) {
        this.claimTime = claimTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ActHiTaskinst setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Long getDuration() {
        return duration;
    }

    public ActHiTaskinst setDuration(Long duration) {
        this.duration = duration;
        return this;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public ActHiTaskinst setDeleteReason(String deleteReason) {
        this.deleteReason = deleteReason;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public ActHiTaskinst setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public ActHiTaskinst setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getFormKey() {
        return formKey;
    }

    public ActHiTaskinst setFormKey(String formKey) {
        this.formKey = formKey;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public ActHiTaskinst setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public ActHiTaskinst setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public List<String> getProcessInstanceIds() {
        return processInstanceIds;
    }

    public void setProcessInstanceIds(List<String> processInstanceIds) {
        this.processInstanceIds = processInstanceIds;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
