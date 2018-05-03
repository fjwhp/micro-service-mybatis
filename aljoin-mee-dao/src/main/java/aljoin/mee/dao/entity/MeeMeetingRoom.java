package aljoin.mee.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 会议室表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeMeetingRoom extends Entity<MeeMeetingRoom> {

    private static final long serialVersionUID = 1L;

    /**
     * 会议室名称
     */
    private String meetingRoomName;
    /**
     * 可容纳人数
     */
    private Integer personNumber;
    /**
     * 会议室地址
     */
    private String meetingRoomAddress;
    /**
     * 会议室负责人ID(多个分号分隔)
     */
    private String personChargeId;
    /**
     * 会议室负责人(多个分号分隔)
     */
    private String personCharge;
    /**
     * 设备描述
     */
    private String deviceDescription;

    /**
     * 使用次数
     */
    private Integer useCount;

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public MeeMeetingRoom setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
        return this;
    }

    public Integer getPersonNumber() {
        return personNumber;
    }

    public MeeMeetingRoom setPersonNumber(Integer personNumber) {
        this.personNumber = personNumber;
        return this;
    }

    public String getMeetingRoomAddress() {
        return meetingRoomAddress;
    }

    public MeeMeetingRoom setMeetingRoomAddress(String meetingRoomAddress) {
        this.meetingRoomAddress = meetingRoomAddress;
        return this;
    }

    public String getPersonChargeId() {
        return personChargeId;
    }

    public MeeMeetingRoom setPersonChargeId(String personChargeId) {
        this.personChargeId = personChargeId;
        return this;
    }

    public String getPersonCharge() {
        return personCharge;
    }

    public MeeMeetingRoom setPersonCharge(String personCharge) {
        this.personCharge = personCharge;
        return this;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public MeeMeetingRoom setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
        return this;
    }

    public Integer getUseCount() {
        return useCount;
    }

    public void setUseCount(Integer useCount) {
        this.useCount = useCount;
    }
}
