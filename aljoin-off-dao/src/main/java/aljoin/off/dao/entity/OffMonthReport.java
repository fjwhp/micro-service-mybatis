package aljoin.off.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * 工作月报表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class OffMonthReport extends Entity<OffMonthReport> {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;
    /**
     * 月份
     */
    private String month;
    /**
     * 状态(0:未提交 1:已提交)
     */
    private Integer status;
    /**
     * 提交时间
     */
    private Date submitTime;
    /**
     * 上报人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long submitId;
    /**
     * 上报人
     */
    private String submitterName;
    /**
     * 所属部门
     */
    private String belongDept;
    /**
     * 所属部门
     */
    private String belongDeptId;
    /**
     * 领导点评
     */
    private String comment;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
    private Integer auditStatus;
    /**
     * 审核时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;
    /**
     * 审核理由
     */
    private String auditReason;
    /**
     * 流程ID
     */
    private String processId;

    public String getTitle() {
        return title;
    }

    public OffMonthReport setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMonth() {
        return month;
    }

    public OffMonthReport setMonth(String month) {
        this.month = month;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public OffMonthReport setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public OffMonthReport setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
        return this;
    }

    public Long getSubmitId() {
        return submitId;
    }

    public OffMonthReport setSubmitId(Long submitId) {
        this.submitId = submitId;
        return this;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public OffMonthReport setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
        return this;
    }

    public String getBelongDept() {
        return belongDept;
    }

    public OffMonthReport setBelongDept(String belongDept) {
        this.belongDept = belongDept;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public OffMonthReport setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public String getBelongDeptId() {
        return belongDeptId;
    }

    public void setBelongDeptId(String belongDeptId) {
        this.belongDeptId = belongDeptId;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
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

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
