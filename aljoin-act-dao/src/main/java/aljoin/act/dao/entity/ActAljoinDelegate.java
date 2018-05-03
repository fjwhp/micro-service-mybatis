package aljoin.act.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 任务委托表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class ActAljoinDelegate extends Entity<ActAljoinDelegate> {

    private static final long serialVersionUID = 1L;

    /**
     * 委托人用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long ownerUserId;
    /**
     * 委托人账号
     */
    private String ownerUserName;
    /**
     * 委托人名称
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
    private Date begTime;
    /**
     * 委托结束时间
     */
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
    /**
     * 是否全盘委托
     */
    private Integer isDelegateAll;

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public ActAljoinDelegate setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
        return this;
    }

    public String getOwnerUserName() {
        return ownerUserName;
    }

    public ActAljoinDelegate setOwnerUserName(String ownerUserName) {
        this.ownerUserName = ownerUserName;
        return this;
    }

    public String getOwnerUserFullname() {
        return ownerUserFullname;
    }

    public ActAljoinDelegate setOwnerUserFullname(String ownerUserFullname) {
        this.ownerUserFullname = ownerUserFullname;
        return this;
    }

    public String getAssigneeUserIds() {
        return assigneeUserIds;
    }

    public ActAljoinDelegate setAssigneeUserIds(String assigneeUserIds) {
        this.assigneeUserIds = assigneeUserIds;
        return this;
    }

    public String getAssigneeUserNames() {
        return assigneeUserNames;
    }

    public ActAljoinDelegate setAssigneeUserNames(String assigneeUserNames) {
        this.assigneeUserNames = assigneeUserNames;
        return this;
    }

    public String getAssigneeUserFullnames() {
        return assigneeUserFullnames;
    }

    public ActAljoinDelegate setAssigneeUserFullnames(String assigneeUserFullnames) {
        this.assigneeUserFullnames = assigneeUserFullnames;
        return this;
    }

    public Date getBegTime() {
        return begTime;
    }

    public ActAljoinDelegate setBegTime(Date begTime) {
        this.begTime = begTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public ActAljoinDelegate setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getDelegateDesc() {
        return delegateDesc;
    }

    public ActAljoinDelegate setDelegateDesc(String delegateDesc) {
        this.delegateDesc = delegateDesc;
        return this;
    }

    public String getDelegateBpmnIds() {
        return delegateBpmnIds;
    }

    public ActAljoinDelegate setDelegateBpmnIds(String delegateBpmnIds) {
        this.delegateBpmnIds = delegateBpmnIds;
        return this;
    }

    public Integer getIsWaitDelegate() {
        return isWaitDelegate;
    }

    public ActAljoinDelegate setIsWaitDelegate(Integer isWaitDelegate) {
        this.isWaitDelegate = isWaitDelegate;
        return this;
    }

    public Integer getIsAssigneeDelegate() {
        return isAssigneeDelegate;
    }

    public ActAljoinDelegate setIsAssigneeDelegate(Integer isAssigneeDelegate) {
        this.isAssigneeDelegate = isAssigneeDelegate;
        return this;
    }

    public Integer getDelegateStatus() {
        return delegateStatus;
    }

    public ActAljoinDelegate setDelegateStatus(Integer delegateStatus) {
        this.delegateStatus = delegateStatus;
        return this;
    }

    public Integer getIsDelegateAll() {
        return isDelegateAll;
    }

    public ActAljoinDelegate setIsDelegateAll(Integer isDelegateAll) {
        this.isDelegateAll = isDelegateAll;
        return this;
    }

}
