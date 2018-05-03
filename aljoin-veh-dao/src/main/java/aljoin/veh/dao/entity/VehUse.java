package aljoin.veh.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 车船使用申请信息表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-15
 */
public class VehUse extends Entity<VehUse> {

    private static final long serialVersionUID = 1L;
    /**
     * 申请人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long applicationId;
    /**
     * 用车人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long useUserId;
    /**
     * 车船ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long carId;

    /**
     * 用车人
     */
    private String useUserName;

    /**
     * 申请人
     */
    private String applicationName;

    public String getUseUserName() {
        return useUserName;
    }

    public void setUseUserName(String useUserName) {
        this.useUserName = useUserName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * 单证编号编号
     */
    private String listCode;
    /**
     * 目的地
     */
    private String destination;
    /**
     * 标题（申购单标题）
     */
    private String title;
    /**
     * 流程名称
     */
    private String processName;
    /**
     * 事由
     */
    private String content;
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
     * 流程ID
     */
    private String processId;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date beginTime;
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
    /**
     * 单证日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date submitTime;

    public Long getApplicationId() {
        return applicationId;
    }

    public VehUse setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public Long getUseUserId() {
        return useUserId;
    }

    public VehUse setUseUserId(Long useUserId) {
        this.useUserId = useUserId;
        return this;
    }

    public Long getCarId() {
        return carId;
    }

    public VehUse setCarId(Long carId) {
        this.carId = carId;
        return this;
    }

    public String getListCode() {
        return listCode;
    }

    public void setListCode(String listCode) {
        this.listCode = listCode;
    }

    public String getDestination() {
        return destination;
    }

    public VehUse setDestination(String destination) {
        this.destination = destination;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VehUse setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getProcessName() {
        return processName;
    }

    public VehUse setProcessName(String processName) {
        this.processName = processName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public VehUse setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public VehUse setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
        return this;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public VehUse setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
        return this;
    }

    public String getProcessId() {
        return processId;
    }

    public VehUse setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public VehUse setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public VehUse setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public VehUse setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
        return this;
    }

}
