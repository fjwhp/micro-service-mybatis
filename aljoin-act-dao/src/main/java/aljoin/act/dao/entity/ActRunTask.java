package aljoin.act.dao.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;

import java.io.Serializable;
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
public class ActRunTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID_")
    private String id;
    @TableField("REV_")
    private Integer rev;
    @TableField("EXECUTION_ID_")
    private String executionId;
    @TableField("PROC_INST_ID_")
    private String procInstId;
    @TableField("PROC_DEF_ID_")
    private String procDefId;
    @TableField("NAME_")
    private String name;
    @TableField("PARENT_TASK_ID_")
    private String parentTaskId;
    @TableField("DESCRIPTION_")
    private String description;
    @TableField("TASK_DEF_KEY_")
    private String taskDefKey;
    @TableField("OWNER_")
    private String owner;
    @TableField("ASSIGNEE_")
    private String assignee;
    @TableField("DELEGATION_")
    private String delegation;
    @TableField("PRIORITY_")
    private Integer priority;
    @TableField("CREATE_TIME_")
    private Date createTime;
    @TableField("DUE_DATE_")
    private Date dueDate;
    @TableField("CATEGORY_")
    private String category;
    @TableField("SUSPENSION_STATE_")
    private Integer suspensionState;
    @TableField("TENANT_ID_")
    private String tenantId;
    @TableField("FORM_KEY_")
    private String formKey;

    private List<String> processInstanceIds;

    private String createBegTime;

    private String createEndTime;

    private String dbType;

    public String getId() {
        return id;
    }

    public ActRunTask setId(String id) {
        this.id = id;
        return this;
    }

    public Integer getRev() {
        return rev;
    }

    public ActRunTask setRev(Integer rev) {
        this.rev = rev;
        return this;
    }

    public String getExecutionId() {
        return executionId;
    }

    public ActRunTask setExecutionId(String executionId) {
        this.executionId = executionId;
        return this;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public ActRunTask setProcInstId(String procInstId) {
        this.procInstId = procInstId;
        return this;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public ActRunTask setProcDefId(String procDefId) {
        this.procDefId = procDefId;
        return this;
    }

    public String getName() {
        return name;
    }

    public ActRunTask setName(String name) {
        this.name = name;
        return this;
    }

    public String getParentTaskId() {
        return parentTaskId;
    }

    public ActRunTask setParentTaskId(String parentTaskId) {
        this.parentTaskId = parentTaskId;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ActRunTask setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public ActRunTask setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public ActRunTask setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getAssignee() {
        return assignee;
    }

    public ActRunTask setAssignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public String getDelegation() {
        return delegation;
    }

    public ActRunTask setDelegation(String delegation) {
        this.delegation = delegation;
        return this;
    }

    public Integer getPriority() {
        return priority;
    }

    public ActRunTask setPriority(Integer priority) {
        this.priority = priority;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public ActRunTask setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public ActRunTask setDueDate(Date dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public ActRunTask setCategory(String category) {
        this.category = category;
        return this;
    }

    public Integer getSuspensionState() {
        return suspensionState;
    }

    public ActRunTask setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
        return this;
    }

    public String getTenantId() {
        return tenantId;
    }

    public ActRunTask setTenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getFormKey() {
        return formKey;
    }

    public ActRunTask setFormKey(String formKey) {
        this.formKey = formKey;
        return this;
    }

    public List<String> getProcessInstanceIds() {
        return processInstanceIds;
    }

    public void setProcessInstanceIds(List<String> processInstanceIds) {
        this.processInstanceIds = processInstanceIds;
    }

    public String getCreateBegTime() {
        return createBegTime;
    }

    public void setCreateBegTime(String createBegTime) {
        this.createBegTime = createBegTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
