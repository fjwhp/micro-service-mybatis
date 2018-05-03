package aljoin.act.dao.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;

/**
 * 
 * (实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public class ActHiActinst {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    @TableId("ID_")
    private String id;
    @TableField("PROC_DEF_ID_")
    private String procDefId;
    @TableField("PROC_INST_ID_")
    private String procInstId;
    @TableField("EXECUTION_ID_")
    private String exrcutionId;
    @TableField("ACT_ID_")
    private String actId;
    @TableField("TASK_ID_")
    private String taskId;
    @TableField("CALL_PROC_INST_ID_")
    private String callProcInstID;
    @TableField("ACT_NAME_")
    private String actName;
    @TableField("ACT_TYPE_")
    private String actType;
    @TableField("ASSIGNEE_")
    private String assignee;
    @TableField("START_TIME_")
    private Date startTime;
    @TableField("END_TIME_")
    private Date endTime;
    @TableField("DURATION_")
    private Integer duration;
    @TableField("TENANT_ID_")
    private String tenantId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcDefId() {
        return procDefId;
    }

    public void setProcDefId(String procDefId) {
        this.procDefId = procDefId;
    }

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }

    public String getExrcutionId() {
        return exrcutionId;
    }

    public void setExrcutionId(String exrcutionId) {
        this.exrcutionId = exrcutionId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCallProcInstID() {
        return callProcInstID;
    }

    public void setCallProcInstID(String callProcInstID) {
        this.callProcInstID = callProcInstID;
    }

    public String getActName() {
        return actName;
    }

    public void setActName(String actName) {
        this.actName = actName;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

}
