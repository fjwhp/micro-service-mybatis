package aljoin.mee.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 外部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class MeeOutsideMeetingDO {

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
     * 会议标题
     */
    private String meetingTitle;

    /**
     * 填写地址
     */
    private String address;

    /**
     * 会议开始时间
     */
    private String beginDate;
    /**
     * 会议结束时间
     */
    private String endDate;
    /**
     * 会议情况（1:未完成 2：已完成 3：已取消）
     */
    private String meetingSituation;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
    private String auditStatus;
    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建人
     */
    private String createFullName;

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

    public String getMeetingTitle() {
        return meetingTitle;
    }

    public void setMeetingTitle(String meetingTitle) {
        this.meetingTitle = meetingTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMeetingSituation() {
        return meetingSituation;
    }

    public void setMeetingSituation(String meetingSituation) {
        this.meetingSituation = meetingSituation;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateFullName() {
        return createFullName;
    }

    public void setCreateFullName(String createFullName) {
        this.createFullName = createFullName;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
