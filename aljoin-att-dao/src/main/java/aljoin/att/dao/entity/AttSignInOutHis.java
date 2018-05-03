package aljoin.att.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;

/**
 * 
 * 签到、退表(历史表)(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-27
 */
public class AttSignInOutHis extends Entity<AttSignInOutHis> {

    private static final long serialVersionUID = 1L;

    /**
     * 签到用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long signUserId;
    /**
     * 签到用户账号
     */
    private String signUserName;
    /**
     * 签到日期
     */
    private Date signDate;
    /**
     * 签到星期
     */
    private String signWeek;
    /**
     * 是否工作日
     */
    private Integer isWorkDay;
    /**
     * 上午上班时间
     */
    private Date amWorkTime;
    /**
     * 上午签到缓冲时间（分钟）
     */
    private Integer amSignInBufferTime;
    /**
     * 上午签到时间
     */
    private Date amSignInTime;
    /**
     * 0-未操作(还在允许打卡的时间范围)，1-准时打卡，2-迟到，3-无打卡（表示过了打卡时间还没有进行签到操作）
     */
    private Integer amSignInStatus;
    /**
     * 上午签到IP
     */
    private String amSignInIp;
    /**
     * 上午签到补签状态：1-未补签，2-补签申请中，3-已经补签通过，4-已经补签不通过
     */
    private Integer amSignInPatchStatus;
    /**
     * 上午签到补签时间
     */
    private Date amSignInPatchTime;
    /**
     * 上午签到补签描述
     */
    private String amSignInPatchDesc;
    /**
     * 上午签到补签审核时间
     */
    private Date amSignInPatchAuditTime;
    /**
     * 上午下班时间
     */
    private Date amOffTime;
    /**
     * 上午签退缓冲时间（分钟）
     */
    private Integer amSignOutBufferTime;
    /**
     * 上午签退时间
     */
    private Date amSignOutTime;
    /**
     * 0-未操作(还在允许打卡的时间范围)，1-准时打卡，2-早退，3-无打卡（表示过了打卡时间还没有进行签到操作）
     */
    private Integer amSignOutStatus;
    /**
     * 上午签退IP
     */
    private String amSignOutIp;
    /**
     * 上午签退补签状态：1-未补签，2-补签申请中，3-已经补签通过，4-已经补签不通过
     */
    private Integer amSignOutPatchStatus;
    /**
     * 上午签退补签时间
     */
    private Date amSignOutPatchTime;
    /**
     * 上午签退补签描述
     */
    private String amSignOutPatchDesc;
    /**
     * 上午签退补签审核时间
     */
    private Date amSignOutPatchAuditTime;
    /**
     * 下午上班时间
     */
    private Date pmWorkTime;
    /**
     * 下午签到缓冲时间（分钟）
     */
    private Integer pmSignInBufferTime;
    /**
     * 下午签到时间
     */
    private Date pmSignInTime;
    /**
     * 0-未操作(还在允许打卡的时间范围)，1-准时打卡，2-迟到，3-无打卡（表示过了打卡时间还没有进行签到操作）
     */
    private Integer pmSignInStatus;
    /**
     * 下午签到IP
     */
    private String pmSignInIp;
    /**
     * 下午签到补签状态：1-未补签，2-补签申请中，3-已经补签通过，4-已经补签不通过
     */
    private Integer pmSignInPatchStatus;
    /**
     * 下午签到补签时间
     */
    private Date pmSignInPatchTime;
    /**
     * 下午签到补签描述
     */
    private String pmSignInPatchDesc;
    /**
     * 下午签到补签审核时间
     */
    private Date pmSignInPatchAuditTime;
    /**
     * 下午下班时间
     */
    private Date pmOffTime;
    /**
     * 下午签退缓冲时间（分钟）
     */
    private Integer pmSignOutBufferTime;
    /**
     * 下午签退时间
     */
    private Date pmSignOutTime;
    /**
     * 0-未操作(还在允许打卡的时间范围)，1-准时打卡，2-早退，3-无打卡（表示过了打卡时间还没有进行签到操作）
     */
    private Integer pmSignOutStatus;
    /**
     * 下午签退IP
     */
    private String pmSignOutIp;
    /**
     * 下午签退补签状态：1-未补签，2-补签申请中，3-已经补签通过，4-已经补签不通过
     */
    private Integer pmSignOutPatchStatus;
    /**
     * 下午签退补签时间
     */
    private Date pmSignOutPatchTime;
    /**
     * 下午签退补签描述
     */
    private String pmSignOutPatchDesc;
    /**
     * 下午签退补签审核时间
     */
    private Date pmSignOutPatchAuditTime;

    /**
     * 流程ID
     */
    private String processId;

    /**
     * 上午签到补签流程实例ID
     */
    private String amSignInProcInstId;
    /**
     * 上午签退补签流程实例ID
     */
    private String amSignOutProcInstId;
    /**
     * 下午签到补签流程实例ID
     */
    private String pmSignInProcInstId;
    /**
     * 下午签退补签流程实例ID
     */
    private String pmSignOutProcInstId;

    public Long getSignUserId() {
        return signUserId;
    }

    public AttSignInOutHis setSignUserId(Long signUserId) {
        this.signUserId = signUserId;
        return this;
    }

    public String getSignUserName() {
        return signUserName;
    }

    public AttSignInOutHis setSignUserName(String signUserName) {
        this.signUserName = signUserName;
        return this;
    }

    public Date getSignDate() {
        return signDate;
    }

    public AttSignInOutHis setSignDate(Date signDate) {
        this.signDate = signDate;
        return this;
    }

    public String getSignWeek() {
        return signWeek;
    }

    public AttSignInOutHis setSignWeek(String signWeek) {
        this.signWeek = signWeek;
        return this;
    }

    public Integer getIsWorkDay() {
        return isWorkDay;
    }

    public AttSignInOutHis setIsWorkDay(Integer isWorkDay) {
        this.isWorkDay = isWorkDay;
        return this;
    }

    public Date getAmWorkTime() {
        return amWorkTime;
    }

    public AttSignInOutHis setAmWorkTime(Date amWorkTime) {
        this.amWorkTime = amWorkTime;
        return this;
    }

    public Integer getAmSignInBufferTime() {
        return amSignInBufferTime;
    }

    public AttSignInOutHis setAmSignInBufferTime(Integer amSignInBufferTime) {
        this.amSignInBufferTime = amSignInBufferTime;
        return this;
    }

    public Date getAmSignInTime() {
        return amSignInTime;
    }

    public AttSignInOutHis setAmSignInTime(Date amSignInTime) {
        this.amSignInTime = amSignInTime;
        return this;
    }

    public Integer getAmSignInStatus() {
        return amSignInStatus;
    }

    public AttSignInOutHis setAmSignInStatus(Integer amSignInStatus) {
        this.amSignInStatus = amSignInStatus;
        return this;
    }

    public String getAmSignInIp() {
        return amSignInIp;
    }

    public AttSignInOutHis setAmSignInIp(String amSignInIp) {
        this.amSignInIp = amSignInIp;
        return this;
    }

    public Integer getAmSignInPatchStatus() {
        return amSignInPatchStatus;
    }

    public AttSignInOutHis setAmSignInPatchStatus(Integer amSignInPatchStatus) {
        this.amSignInPatchStatus = amSignInPatchStatus;
        return this;
    }

    public Date getAmSignInPatchTime() {
        return amSignInPatchTime;
    }

    public AttSignInOutHis setAmSignInPatchTime(Date amSignInPatchTime) {
        this.amSignInPatchTime = amSignInPatchTime;
        return this;
    }

    public String getAmSignInPatchDesc() {
        return amSignInPatchDesc;
    }

    public AttSignInOutHis setAmSignInPatchDesc(String amSignInPatchDesc) {
        this.amSignInPatchDesc = amSignInPatchDesc;
        return this;
    }

    public Date getAmSignInPatchAuditTime() {
        return amSignInPatchAuditTime;
    }

    public AttSignInOutHis setAmSignInPatchAuditTime(Date amSignInPatchAuditTime) {
        this.amSignInPatchAuditTime = amSignInPatchAuditTime;
        return this;
    }

    public Date getAmOffTime() {
        return amOffTime;
    }

    public AttSignInOutHis setAmOffTime(Date amOffTime) {
        this.amOffTime = amOffTime;
        return this;
    }

    public Integer getAmSignOutBufferTime() {
        return amSignOutBufferTime;
    }

    public AttSignInOutHis setAmSignOutBufferTime(Integer amSignOutBufferTime) {
        this.amSignOutBufferTime = amSignOutBufferTime;
        return this;
    }

    public Date getAmSignOutTime() {
        return amSignOutTime;
    }

    public AttSignInOutHis setAmSignOutTime(Date amSignOutTime) {
        this.amSignOutTime = amSignOutTime;
        return this;
    }

    public Integer getAmSignOutStatus() {
        return amSignOutStatus;
    }

    public AttSignInOutHis setAmSignOutStatus(Integer amSignOutStatus) {
        this.amSignOutStatus = amSignOutStatus;
        return this;
    }

    public String getAmSignOutIp() {
        return amSignOutIp;
    }

    public AttSignInOutHis setAmSignOutIp(String amSignOutIp) {
        this.amSignOutIp = amSignOutIp;
        return this;
    }

    public Integer getAmSignOutPatchStatus() {
        return amSignOutPatchStatus;
    }

    public AttSignInOutHis setAmSignOutPatchStatus(Integer amSignOutPatchStatus) {
        this.amSignOutPatchStatus = amSignOutPatchStatus;
        return this;
    }

    public Date getAmSignOutPatchTime() {
        return amSignOutPatchTime;
    }

    public AttSignInOutHis setAmSignOutPatchTime(Date amSignOutPatchTime) {
        this.amSignOutPatchTime = amSignOutPatchTime;
        return this;
    }

    public String getAmSignOutPatchDesc() {
        return amSignOutPatchDesc;
    }

    public AttSignInOutHis setAmSignOutPatchDesc(String amSignOutPatchDesc) {
        this.amSignOutPatchDesc = amSignOutPatchDesc;
        return this;
    }

    public Date getAmSignOutPatchAuditTime() {
        return amSignOutPatchAuditTime;
    }

    public AttSignInOutHis setAmSignOutPatchAuditTime(Date amSignOutPatchAuditTime) {
        this.amSignOutPatchAuditTime = amSignOutPatchAuditTime;
        return this;
    }

    public Date getPmWorkTime() {
        return pmWorkTime;
    }

    public AttSignInOutHis setPmWorkTime(Date pmWorkTime) {
        this.pmWorkTime = pmWorkTime;
        return this;
    }

    public Integer getPmSignInBufferTime() {
        return pmSignInBufferTime;
    }

    public AttSignInOutHis setPmSignInBufferTime(Integer pmSignInBufferTime) {
        this.pmSignInBufferTime = pmSignInBufferTime;
        return this;
    }

    public Date getPmSignInTime() {
        return pmSignInTime;
    }

    public AttSignInOutHis setPmSignInTime(Date pmSignInTime) {
        this.pmSignInTime = pmSignInTime;
        return this;
    }

    public Integer getPmSignInStatus() {
        return pmSignInStatus;
    }

    public AttSignInOutHis setPmSignInStatus(Integer pmSignInStatus) {
        this.pmSignInStatus = pmSignInStatus;
        return this;
    }

    public String getPmSignInIp() {
        return pmSignInIp;
    }

    public AttSignInOutHis setPmSignInIp(String pmSignInIp) {
        this.pmSignInIp = pmSignInIp;
        return this;
    }

    public Integer getPmSignInPatchStatus() {
        return pmSignInPatchStatus;
    }

    public AttSignInOutHis setPmSignInPatchStatus(Integer pmSignInPatchStatus) {
        this.pmSignInPatchStatus = pmSignInPatchStatus;
        return this;
    }

    public Date getPmSignInPatchTime() {
        return pmSignInPatchTime;
    }

    public AttSignInOutHis setPmSignInPatchTime(Date pmSignInPatchTime) {
        this.pmSignInPatchTime = pmSignInPatchTime;
        return this;
    }

    public String getPmSignInPatchDesc() {
        return pmSignInPatchDesc;
    }

    public AttSignInOutHis setPmSignInPatchDesc(String pmSignInPatchDesc) {
        this.pmSignInPatchDesc = pmSignInPatchDesc;
        return this;
    }

    public Date getPmSignInPatchAuditTime() {
        return pmSignInPatchAuditTime;
    }

    public AttSignInOutHis setPmSignInPatchAuditTime(Date pmSignInPatchAuditTime) {
        this.pmSignInPatchAuditTime = pmSignInPatchAuditTime;
        return this;
    }

    public Date getPmOffTime() {
        return pmOffTime;
    }

    public AttSignInOutHis setPmOffTime(Date pmOffTime) {
        this.pmOffTime = pmOffTime;
        return this;
    }

    public Integer getPmSignOutBufferTime() {
        return pmSignOutBufferTime;
    }

    public AttSignInOutHis setPmSignOutBufferTime(Integer pmSignOutBufferTime) {
        this.pmSignOutBufferTime = pmSignOutBufferTime;
        return this;
    }

    public Date getPmSignOutTime() {
        return pmSignOutTime;
    }

    public AttSignInOutHis setPmSignOutTime(Date pmSignOutTime) {
        this.pmSignOutTime = pmSignOutTime;
        return this;
    }

    public Integer getPmSignOutStatus() {
        return pmSignOutStatus;
    }

    public AttSignInOutHis setPmSignOutStatus(Integer pmSignOutStatus) {
        this.pmSignOutStatus = pmSignOutStatus;
        return this;
    }

    public String getPmSignOutIp() {
        return pmSignOutIp;
    }

    public AttSignInOutHis setPmSignOutIp(String pmSignOutIp) {
        this.pmSignOutIp = pmSignOutIp;
        return this;
    }

    public Integer getPmSignOutPatchStatus() {
        return pmSignOutPatchStatus;
    }

    public AttSignInOutHis setPmSignOutPatchStatus(Integer pmSignOutPatchStatus) {
        this.pmSignOutPatchStatus = pmSignOutPatchStatus;
        return this;
    }

    public Date getPmSignOutPatchTime() {
        return pmSignOutPatchTime;
    }

    public AttSignInOutHis setPmSignOutPatchTime(Date pmSignOutPatchTime) {
        this.pmSignOutPatchTime = pmSignOutPatchTime;
        return this;
    }

    public String getPmSignOutPatchDesc() {
        return pmSignOutPatchDesc;
    }

    public AttSignInOutHis setPmSignOutPatchDesc(String pmSignOutPatchDesc) {
        this.pmSignOutPatchDesc = pmSignOutPatchDesc;
        return this;
    }

    public Date getPmSignOutPatchAuditTime() {
        return pmSignOutPatchAuditTime;
    }

    public AttSignInOutHis setPmSignOutPatchAuditTime(Date pmSignOutPatchAuditTime) {
        this.pmSignOutPatchAuditTime = pmSignOutPatchAuditTime;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getAmSignInProcInstId() {
        return amSignInProcInstId;
    }

    public void setAmSignInProcInstId(String amSignInProcInstId) {
        this.amSignInProcInstId = amSignInProcInstId;
    }

    public String getAmSignOutProcInstId() {
        return amSignOutProcInstId;
    }

    public void setAmSignOutProcInstId(String amSignOutProcInstId) {
        this.amSignOutProcInstId = amSignOutProcInstId;
    }

    public String getPmSignInProcInstId() {
        return pmSignInProcInstId;
    }

    public void setPmSignInProcInstId(String pmSignInProcInstId) {
        this.pmSignInProcInstId = pmSignInProcInstId;
    }

    public String getPmSignOutProcInstId() {
        return pmSignOutProcInstId;
    }

    public void setPmSignOutProcInstId(String pmSignOutProcInstId) {
        this.pmSignOutProcInstId = pmSignOutProcInstId;
    }
}
