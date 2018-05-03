package aljoin.mee.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 内部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeInsideMeeting extends Entity<MeeInsideMeeting> {

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
     * 会议室Id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(hidden = true)
    private Long meetingRoomId;
    /**
     * 地址
     */
    @ApiModelProperty(hidden = true)
    private String address;
    /**
     * 会议室名称
     */
    @ApiModelProperty(hidden = true)
    private String meetingRoomName;
    /**
     * 参会人员ID (多个分号分隔)
     */
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

    public MeeInsideMeeting setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
        return this;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public MeeInsideMeeting setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
        return this;
    }

    public String getContacts() {
        return contacts;
    }

    public MeeInsideMeeting setContacts(String contacts) {
        this.contacts = contacts;
        return this;
    }

    public Long getMeetingRoomId() {
        return meetingRoomId;
    }

    public MeeInsideMeeting setMeetingRoomId(Long meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MeeInsideMeeting setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public MeeInsideMeeting setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
        return this;
    }

    public String getPartyMemebersId() {
        return partyMemebersId;
    }

    public MeeInsideMeeting setPartyMemebersId(String partyMemebersId) {
        this.partyMemebersId = partyMemebersId;
        return this;
    }

    public String getPartyMemeberNames() {
        return partyMemeberNames;
    }

    public MeeInsideMeeting setPartyMemeberNames(String partyMemeberNames) {
        this.partyMemeberNames = partyMemeberNames;
        return this;
    }

    public String getAttendances() {
        return attendances;
    }

    public MeeInsideMeeting setAttendances(String attendances) {
        this.attendances = attendances;
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public MeeInsideMeeting setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MeeInsideMeeting setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public MeeInsideMeeting setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
        return this;
    }

    public Integer getMeetingSituation() {
        return meetingSituation;
    }

    public MeeInsideMeeting setMeetingSituation(Integer meetingSituation) {
        this.meetingSituation = meetingSituation;
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
