package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 流程任务-授权表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-02
 */
public class ActAljoinTaskAssignee extends Entity<ActAljoinTaskAssignee> {

    private static final long serialVersionUID = 1L;

    /**
     * bpmn流程表ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bpmnId;
    /**
     * 流程ID
     */
    private String processId;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 候选部门ID
     */
    private String assigneeDepartmentIds;
    /**
     * 候选岗位ID
     */
    private String assigneePositionIds;
    /**
     * 受理人ID
     */
    private String assigneeUserIds;
    /**
     * 候选用户ID
     */
    private String assigneeCandidateIds;
    /**
     * 候选组(角色)ID
     */
    private String assigneeGroupIds;
    /**
     * 显示控件ID(逗号分隔给任务节点下可视的控件ID)
     */
    private String showWidgetIds;
    /**
     * 编辑控件ID(逗号分隔给任务节点下可编辑的控件ID)
     */
    private String editWidgetIds;
    /**
     * 意见域ID（控制只能设置textarea）
     */
    private String commentWidgetIds;
    /**
     * 不可为空（必填）控件ID
     */
    private String notNullWidgetIds;
    /**
     * 办理权限ID
     */
    private String operateAuthIds;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 红头控件ID
     */
    private String redHeadWidgetIds;
    /**
     * 痕迹保留控件ID
     */
    private String saveMarkWidgetIds;
    /**
     * 是否返回创建人
     */
    private Integer isReturnCreater;

    /**
     * 加签意见域ID（控制只能设置textarea）
     */
    private String signCommentWidgetIds;

    /**
     * 创建人所在部门人员
     */
    private Integer staffMembersDepartment;

    /**
     * 上一个环节办理人人所在部门人员
     */
    private Integer lastlinkDepartment;

    /**
     * 创建人所在岗位人员办理
     */
    private Integer createPersonsJob;

    public Long getBpmnId() {
        return bpmnId;
    }

    public ActAljoinTaskAssignee setBpmnId(Long bpmnId) {
        this.bpmnId = bpmnId;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public ActAljoinTaskAssignee setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public String getTaskId() {
        return taskId;
    }

    public ActAljoinTaskAssignee setTaskId(String taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getAssigneeDepartmentIds() {
        return assigneeDepartmentIds;
    }

    public ActAljoinTaskAssignee setAssigneeDepartmentIds(String assigneeDepartmentIds) {
        this.assigneeDepartmentIds = assigneeDepartmentIds;
        return this;
    }

    public String getAssigneePositionIds() {
        return assigneePositionIds;
    }

    public ActAljoinTaskAssignee setAssigneePositionIds(String assigneePositionIds) {
        this.assigneePositionIds = assigneePositionIds;
        return this;
    }

    public String getAssigneeUserIds() {
        return assigneeUserIds;
    }

    public ActAljoinTaskAssignee setAssigneeUserIds(String assigneeUserIds) {
        this.assigneeUserIds = assigneeUserIds;
        return this;
    }

    public String getAssigneeCandidateIds() {
        return assigneeCandidateIds;
    }

    public ActAljoinTaskAssignee setAssigneeCandidateIds(String assigneeCandidateIds) {
        this.assigneeCandidateIds = assigneeCandidateIds;
        return this;
    }

    public String getAssigneeGroupIds() {
        return assigneeGroupIds;
    }

    public ActAljoinTaskAssignee setAssigneeGroupIds(String assigneeGroupIds) {
        this.assigneeGroupIds = assigneeGroupIds;
        return this;
    }

    public String getShowWidgetIds() {
        return showWidgetIds;
    }

    public ActAljoinTaskAssignee setShowWidgetIds(String showWidgetIds) {
        this.showWidgetIds = showWidgetIds;
        return this;
    }

    public String getEditWidgetIds() {
        return editWidgetIds;
    }

    public ActAljoinTaskAssignee setEditWidgetIds(String editWidgetIds) {
        this.editWidgetIds = editWidgetIds;
        return this;
    }

    public String getCommentWidgetIds() {
        return commentWidgetIds;
    }

    public ActAljoinTaskAssignee setCommentWidgetIds(String commentWidgetIds) {
        this.commentWidgetIds = commentWidgetIds;
        return this;
    }

    public String getNotNullWidgetIds() {
        return notNullWidgetIds;
    }

    public ActAljoinTaskAssignee setNotNullWidgetIds(String notNullWidgetIds) {
        this.notNullWidgetIds = notNullWidgetIds;
        return this;
    }

    public String getOperateAuthIds() {
        return operateAuthIds;
    }

    public ActAljoinTaskAssignee setOperateAuthIds(String operateAuthIds) {
        this.operateAuthIds = operateAuthIds;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinTaskAssignee setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getRedHeadWidgetIds() {
        return redHeadWidgetIds;
    }

    public ActAljoinTaskAssignee setRedHeadWidgetIds(String redHeadWidgetIds) {
        this.redHeadWidgetIds = redHeadWidgetIds;
        return this;
    }

    public String getSaveMarkWidgetIds() {
        return saveMarkWidgetIds;
    }

    public ActAljoinTaskAssignee setSaveMarkWidgetIds(String saveMarkWidgetIds) {
        this.saveMarkWidgetIds = saveMarkWidgetIds;
        return this;
    }

    public Integer getIsReturnCreater() {
        return isReturnCreater;
    }

    public ActAljoinTaskAssignee setIsReturnCreater(Integer isReturnCreater) {
        this.isReturnCreater = isReturnCreater;
        return this;
    }

    public String getSignCommentWidgetIds() {
        return signCommentWidgetIds;
    }

    public void setSignCommentWidgetIds(String signCommentWidgetIds) {
        this.signCommentWidgetIds = signCommentWidgetIds;
    }

    public Integer getStaffMembersDepartment() {
        return staffMembersDepartment;
    }

    public void setStaffMembersDepartment(Integer staffMembersDepartment) {
        this.staffMembersDepartment = staffMembersDepartment;
    }

    public Integer getLastlinkDepartment() {
        return lastlinkDepartment;
    }

    public void setLastlinkDepartment(Integer lastlinkDepartment) {
        this.lastlinkDepartment = lastlinkDepartment;
    }

    public Integer getCreatePersonsJob() {
        return createPersonsJob;
    }

    public void setCreatePersonsJob(Integer createPersonsJob) {
        this.createPersonsJob = createPersonsJob;
    }
}
