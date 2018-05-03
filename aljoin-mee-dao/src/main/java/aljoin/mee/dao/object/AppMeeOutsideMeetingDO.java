package aljoin.mee.dao.object;

import aljoin.res.dao.entity.ResResource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.List;

/**
 * 外部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class AppMeeOutsideMeetingDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 标题
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
     * 会议地址
     */
    private String address;

    /**
     * 参会人员
     */
    private String partyMemeberNames;

    /**
     * 外部出席单位&nbsp;/&nbsp;人员
     */
    private String attendances;

    /**
     * 会议开始时间
     */
    private String beginTime;

    /**
     * 会议结束时间
     */
    private String endTime;

    /**
     * 会议内容
     */
    private String meetingContent;

    /**
     * 签收状态
     */
    private Integer claimStatus;

    /**
     * 附件
     */
    private List<ResResource> resResourceList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getMeetingHost() {
        return meetingHost;
    }

    public void setMeetingHost(String meetingHost) {
        this.meetingHost = meetingHost;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPartyMemeberNames() {
        return partyMemeberNames;
    }

    public void setPartyMemeberNames(String partyMemeberNames) {
        this.partyMemeberNames = partyMemeberNames;
    }

    public String getAttendances() {
        return attendances;
    }

    public void setAttendances(String attendances) {
        this.attendances = attendances;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getMeetingContent() {
        return meetingContent;
    }

    public void setMeetingContent(String meetingContent) {
        this.meetingContent = meetingContent;
    }

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }
}
