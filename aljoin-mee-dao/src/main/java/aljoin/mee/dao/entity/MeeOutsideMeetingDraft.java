package aljoin.mee.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * 外部会议草稿表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeOutsideMeetingDraft extends Entity<MeeOutsideMeetingDraft> {

    private static final long serialVersionUID = 1L;

    /**
     * 会议标题
     */
    private String meetingTitle;
    /**
     * 主持人
     */
    private String meetingHost;
    /**
     * 联系人
     */
    private String contacts;
    /**
     * 参会人员ID (多个分号分隔)
     */
    private String partyMemebersId;
    /**
     * 参会人员名称 (多个分号分隔)
     */
    private String partyMemeberNames;
    /**
     * 出席人员 (多个分号分隔)
     */
    private String attendances;
    /**
     * 会议开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date beginTime;
    /**
     * 会议结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    /**
     * 会议内容
     */
    private String meetingContent;
    /**
     * 是否短信提醒
     */
    private Integer isWarnMsg;
    /**
     * 是否邮件提醒
     */
    private Integer isWarnMail;
    /**
     * 是否在线提醒
     */
    private Integer isWarnOnline;
    /**
     * 会议情况（1:未完成 2：已完成 3：已取消）
     */
    private Integer meetingSituation;

    /**
     * 填写地址
     */
    private String address;

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public MeeOutsideMeetingDraft setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
        return this;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public MeeOutsideMeetingDraft setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
        return this;
    }

    public String getContacts() {
        return contacts;
    }

    public MeeOutsideMeetingDraft setContacts(String contacts) {
        this.contacts = contacts;
        return this;
    }

    public String getPartyMemebersId() {
        return partyMemebersId;
    }

    public MeeOutsideMeetingDraft setPartyMemebersId(String partyMemebersId) {
        this.partyMemebersId = partyMemebersId;
        return this;
    }

    public String getPartyMemeberNames() {
        return partyMemeberNames;
    }

    public MeeOutsideMeetingDraft setPartyMemeberNames(String partyMemeberNames) {
        this.partyMemeberNames = partyMemeberNames;
        return this;
    }

    public String getAttendances() {
        return attendances;
    }

    public MeeOutsideMeetingDraft setAttendances(String attendances) {
        this.attendances = attendances;
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public MeeOutsideMeetingDraft setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public MeeOutsideMeetingDraft setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public MeeOutsideMeetingDraft setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
        return this;
    }

    public Integer getMeetingSituation() {
        return meetingSituation;
    }

    public MeeOutsideMeetingDraft setMeetingSituation(Integer meetingSituation) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
