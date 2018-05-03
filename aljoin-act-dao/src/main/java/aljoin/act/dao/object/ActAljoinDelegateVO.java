package aljoin.act.dao.object;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActAljoinDelegateVO {
    /**
     * ID
     */
    private String id;
    /**
     * 委托人用户ID
     */
    private String ownerUserId;
    /**
     * 委托人用户账号
     */
    private String ownerUserName;
    /**
     * 委托人用户名称
     */
    private String ownerUserFullname;
    /**
     * 被委托人用户ID，逗号分隔
     */
    private String assigneeUserIds;
    /**
     * 被委托人账号，逗号分隔
     */
    private String assigneeUserNames;
    /**
     * 被委托人名称，逗号分隔
     */
    private String assigneeUserFullnames;
    /**
     * 委托开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date begTime;
    /**
     * 委托结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 委托说明
     */
    private String delegateDesc;
    /**
     * 委托流程ID
     */
    private String delegateBpmnIds;
    /**
     * 是否全盘委托
     */
    private Integer isDelegateAll;
    /**
     * 待办是否委托（未签收任务）
     */
    private Integer isWaitDelegate;
    /**
     * 已签收任务是否委托
     */
    private Integer isAssigneeDelegate;
    /**
     * 委托状态：1-未开始（当前时间少于开发时间），2-代理中（时间内），3-已结束（时间结束），4-已终止（人为终止）
     */
    private Integer delegateStatus;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public void setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
    }

    public String getOwnerUserFullname() {
        return ownerUserFullname;
    }

    public void setOwnerUserFullname(String ownerUserFullname) {
        this.ownerUserFullname = ownerUserFullname;
    }

    public String getAssigneeUserIds() {
        return assigneeUserIds;
    }

    public void setAssigneeUserIds(String assigneeUserIds) {
        this.assigneeUserIds = assigneeUserIds;
    }

    public String getAssigneeUserNames() {
        return assigneeUserNames;
    }

    public void setAssigneeUserNames(String assigneeUserNames) {
        this.assigneeUserNames = assigneeUserNames;
    }

    public String getAssigneeUserFullnames() {
        return assigneeUserFullnames;
    }

    public void setAssigneeUserFullnames(String assigneeUserFullnames) {
        this.assigneeUserFullnames = assigneeUserFullnames;
    }

    public Date getBegTime() {
        return begTime;
    }

    public void setBegTime(Date begTime) {
        this.begTime = begTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDelegateDesc() {
        return delegateDesc;
    }

    public void setDelegateDesc(String delegateDesc) {
        this.delegateDesc = delegateDesc;
    }

    public String getDelegateBpmnIds() {
        return delegateBpmnIds;
    }

    public void setDelegateBpmnIds(String delegateBpmnIds) {
        this.delegateBpmnIds = delegateBpmnIds;
    }

    public Integer getIsDelegateAll() {
        return isDelegateAll;
    }

    public void setIsDelegateAll(Integer isDelegateAll) {
        this.isDelegateAll = isDelegateAll;
    }

    public Integer getIsWaitDelegate() {
        return isWaitDelegate;
    }

    public void setIsWaitDelegate(Integer isWaitDelegate) {
        this.isWaitDelegate = isWaitDelegate;
    }

    public Integer getIsAssigneeDelegate() {
        return isAssigneeDelegate;
    }

    public void setIsAssigneeDelegate(Integer isAssigneeDelegate) {
        this.isAssigneeDelegate = isAssigneeDelegate;
    }

    public Integer getDelegateStatus() {
        return delegateStatus;
    }

    public void setDelegateStatus(Integer delegateStatus) {
        this.delegateStatus = delegateStatus;
    }

    @Override
    public String toString() {
        return "ActAljoinDelegateVO [id=" + id + ", ownerUserId=" + ownerUserId + ", ownerUserName=" + ownerUserName
            + ", ownerUserFullname=" + ownerUserFullname + ", assigneeUserIds=" + assigneeUserIds
            + ", assigneeUserNames=" + assigneeUserNames + ", assigneeUserFullnames=" + assigneeUserFullnames
            + ", begTime=" + begTime + ", endTime=" + endTime + ", delegateDesc=" + delegateDesc + ", delegateBpmnIds="
            + delegateBpmnIds + ", isDelegateAll=" + isDelegateAll + ", isWaitDelegate=" + isWaitDelegate
            + ", isAssigneeDelegate=" + isAssigneeDelegate + ", delegateStatus=" + delegateStatus + "]";
    }

}
