package aljoin.mee.dao.entity;

import java.util.Date;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * 外部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeOutsideMeeting extends Entity<MeeOutsideMeeting> {

    private static final long serialVersionUID = 1L;

    /**
     * 会议标题
     */
    @ApiModelProperty(hidden = true)
    private String meetingTitle;
    /**
     * 主持人
     */
    @ApiModelProperty(hidden = true)
    private String meetingHost;
    /**
     * 联系人
     */
    @ApiModelProperty(hidden = true)
    private String contacts;
    /**
     * 参会人员ID (多个分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String partyMemebersId;
    /**
     * 参会人员名称 (多个分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String partyMemeberNames;
    /**
     * 出席人员 (多个分号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String attendances;
    /**
     * 会议开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(hidden = true)
    private Date beginTime;
    /**
     * 会议结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    @ApiModelProperty(hidden = true)
    private Date endTime;
    /**
     * 会议内容
     */
    @ApiModelProperty(hidden = true)
    private String meetingContent;
    /**
     * 是否短信提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer isWarnMsg;
    /**
     * 是否邮件提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer isWarnMail;
    /**
     * 是否在线提醒
     */
    @ApiModelProperty(hidden = true)
    private Integer isWarnOnline;
    /**
     * 会议情况（1:未完成 2：已完成 3：已取消）
     */
    @ApiModelProperty(hidden = true)
    private Integer meetingSituation;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
    @ApiModelProperty(hidden = true)
    private Integer auditStatus;
    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(hidden = true)
    private Date auditTime;
    /**
     * 审核理由
     */
    @ApiModelProperty(hidden = true)
    private String auditReason;
    /**
     * 流程ID
     */
    @ApiModelProperty(hidden = true)
    private String processId;
    /**
     * 填写地址
     */
    @ApiModelProperty(hidden = true)
    private String address;

    /**
     * 新增成员
     */
    @ApiModelProperty(hidden = true)
    private String newMember;

    /**
     * 删除成员
     */
    @ApiModelProperty(hidden = true)
    private String delMember;

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public MeeOutsideMeeting setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
        return this;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public MeeOutsideMeeting setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
        return this;
    }

    public String getContacts() {
        return contacts;
    }

    public MeeOutsideMeeting setContacts(String contacts) {
        this.contacts = contacts;
        return this;
    }

    public String getPartyMemebersId() {
        return partyMemebersId;
    }

    public MeeOutsideMeeting setPartyMemebersId(String partyMemebersId) {
        this.partyMemebersId = partyMemebersId;
        return this;
    }

    public String getPartyMemeberNames() {
        return partyMemeberNames;
    }

    public MeeOutsideMeeting setPartyMemeberNames(String partyMemeberNames) {
        this.partyMemeberNames = partyMemeberNames;
        return this;
    }

    public String getAttendances() {
        return attendances;
    }

    public MeeOutsideMeeting setAttendances(String attendances) {
        this.attendances = attendances;
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public MeeOutsideMeeting setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MeeOutsideMeeting setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public MeeOutsideMeeting setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
        return this;
    }

    public Integer getMeetingSituation() {
        return meetingSituation;
    }

    public MeeOutsideMeeting setMeetingSituation(Integer meetingSituation) {
        this.meetingSituation = meetingSituation;
        return this;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public MeeOutsideMeeting setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
        return this;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public MeeOutsideMeeting setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
        return this;
    }

    public String getAuditReason() {
        return auditReason;
    }

    public void setAuditReason(String auditReason) {
        this.auditReason = auditReason;
    }

    public String getProcessId() {
        return processId;
    }

    public MeeOutsideMeeting setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public Integer getIsWarnMsg() {
        return isWarnMsg;
    }

    public void setIsWarnMsg(Integer isWarnMsg) {
        this.isWarnMsg = isWarnMsg;
    }

    public Integer getIsWarnMail() {
        return isWarnMail;
    }

    public void setIsWarnMail(Integer isWarnMail) {
        this.isWarnMail = isWarnMail;
    }

    public Integer getIsWarnOnline() {
        return isWarnOnline;
    }

    public void setIsWarnOnline(Integer isWarnOnline) {
        this.isWarnOnline = isWarnOnline;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNewMember() {
        return newMember;
    }

    public void setNewMember(String newMember) {
        this.newMember = newMember;
    }

    public String getDelMember() {
        return delMember;
    }

    public void setDelMember(String delMember) {
        this.delMember = delMember;
    }

}
