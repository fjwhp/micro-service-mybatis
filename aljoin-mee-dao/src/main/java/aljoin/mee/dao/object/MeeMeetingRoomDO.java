package aljoin.mee.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 会议室表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeMeetingRoomDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 序号
     */
    private Integer no;
    /**
     * 可容纳人数
     */
    private Integer personNumber;
    /**
     * 会议室名称
     */
    private String meetingRoomName;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getPersonNumber() {
        return personNumber;
    }

    public void setPersonNumber(Integer personNumber) {
        this.personNumber = personNumber;
    }

    public String getMeetingRoomName() {
        return meetingRoomName;
    }

    public void setMeetingRoomName(String meetingRoomName) {
        this.meetingRoomName = meetingRoomName;
    }

    public String getMeetingRoomAddress() {
        return meetingRoomAddress;
    }

    public void setMeetingRoomAddress(String meetingRoomAddress) {
        this.meetingRoomAddress = meetingRoomAddress;
    }

    public String getPersonChargeId() {
        return personChargeId;
    }

    public void setPersonChargeId(String personChargeId) {
        this.personChargeId = personChargeId;
    }

    public String getPersonCharge() {
        return personCharge;
    }

    public void setPersonCharge(String personCharge) {
        this.personCharge = personCharge;
    }

    public String getDeviceDescription() {
        return deviceDescription;
    }

    public void setDeviceDescription(String deviceDescription) {
        this.deviceDescription = deviceDescription;
    }
}
