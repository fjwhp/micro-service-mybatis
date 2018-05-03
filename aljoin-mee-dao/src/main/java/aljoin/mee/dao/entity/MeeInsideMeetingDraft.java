package aljoin.mee.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 内部会议草稿表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeInsideMeetingDraft extends Entity<MeeInsideMeetingDraft> {

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
    @ApiModelProperty(hidden = true)
    private Date beginTime;
    /**
     * 会议结束时间
     */
    @ApiModelProperty(hidden = true)
    private Date endTime;
    /**
     * 会议内容
     */
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

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public MeeInsideMeetingDraft setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
        return this;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public MeeInsideMeetingDraft setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
        return this;
    }

    public String getContacts() {
        return contacts;
    }

    public MeeInsideMeetingDraft setContacts(String contacts) {
        this.contacts = contacts;
        return this;
    }

    public Long getMeetingRoomId() {
        return meetingRoomId;
    }

    public MeeInsideMeetingDraft setMeetingRoomId(Long meetingRoomId) {
        this.meetingRoomId = meetingRoomId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public MeeInsideMeetingDraft setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public MeeInsideMeetingDraft setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
        return this;
    }

    public String getPartyMemebersId() {
        return partyMemebersId;
    }

    public MeeInsideMeetingDraft setPartyMemebersId(String partyMemebersId) {
        this.partyMemebersId = partyMemebersId;
        return this;
    }

    public String getPartyMemeberNames() {
        return partyMemeberNames;
    }

    public MeeInsideMeetingDraft setPartyMemeberNames(String partyMemeberNames) {
        this.partyMemeberNames = partyMemeberNames;
        return this;
    }

    public String getAttendances() {
        return attendances;
    }

    public MeeInsideMeetingDraft setAttendances(String attendances) {
        this.attendances = attendances;
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public MeeInsideMeetingDraft setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MeeInsideMeetingDraft setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public MeeInsideMeetingDraft setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
        return this;
    }

    public Integer getMeetingSituation() {
        return meetingSituation;
    }

    public MeeInsideMeetingDraft setMeetingSituation(Integer meetingSituation) {
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
}
