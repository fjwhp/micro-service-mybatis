package aljoin.pub.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 公共信息表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public class PubPublicInfo extends Entity<PubPublicInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    @ApiModelProperty(value = "是否激活", required = false, hidden = true)
    private Integer isActive;
    /**
     * 标题（公告名称）
     */
    @ApiModelProperty(value = "标题（公告名称）", required = false, hidden = true)
    private String title;
    /**
     * 发布人
     */
    @ApiModelProperty(value = "发布人", required = false, hidden = true)
    private String publishName;
    /**
     * 所属分类ID （公共信息表分类ID）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "所属分类ID （公共信息表分类ID）", required = false, hidden = true)
    private Long categoryId;
    /**
     * 有效期(单位为天)
     */
    @ApiModelProperty(value = "有效期(单位为天)", required = false, hidden = true)
    private Integer period;
    /**
     * 有效期开始时间
     */
    @ApiModelProperty(value = "有效期开始时间", required = false, hidden = true)
    private Date periodBeginTime;
    /**
     * 有效期结束时间
     */
    @ApiModelProperty(value = "有效期结束时间", required = false, hidden = true)
    private Date periodEndTime;
    /**
     * 有效期状态（0:有效期限 1:已失效）
     */
    @ApiModelProperty(value = "有效期状态（0:有效期限 1:已失效）", required = false, hidden = true)
    private Integer periodStatus;
    /**
     * 公告对象ID （多个用分号分隔）
     */
    @ApiModelProperty(value = "公告对象ID （多个用分号分隔）", required = false, hidden = true)
    private String noticeObjId;
    /**
     * 公告对象 （多个用分号分隔）
     */
    @ApiModelProperty(value = "公告对象 （多个用分号分隔）", required = false, hidden = true)
    private String noticeObjName;
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容", required = false, hidden = true)
    private String content;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
    @ApiModelProperty(value = "审核状态（1:审核中 2：审核失败 3：审核通过）", required = false, hidden = true)
    private Integer auditStatus;
    /**
     * 审核时间
     */
    private Date auditTime;
    /**
     * 审核理由
     */
    @ApiModelProperty(value = "审核理由", required = false, hidden = true)
    private String auditReason;
    /**
     * 流程ID
     */
    @ApiModelProperty(value = "流程ID", required = false, hidden = true)
    private String processId;

    public Integer getIsActive() {
        return isActive;
    }

    public PubPublicInfo setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PubPublicInfo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getPublishName() {
        return publishName;
    }

    public PubPublicInfo setPublishName(String publishName) {
        this.publishName = publishName;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public PubPublicInfo setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Integer getPeriod() {
        return period;
    }

    public PubPublicInfo setPeriod(Integer period) {
        this.period = period;
        return this;
    }

    public Date getPeriodBeginTime() {
        return periodBeginTime;
    }

    public PubPublicInfo setPeriodBeginTime(Date periodBeginTime) {
        this.periodBeginTime = periodBeginTime;
        return this;
    }

    public Date getPeriodEndTime() {
        return periodEndTime;
    }

    public PubPublicInfo setPeriodEndTime(Date periodEndTime) {
        this.periodEndTime = periodEndTime;
        return this;
    }

    public Integer getPeriodStatus() {
        return periodStatus;
    }

    public PubPublicInfo setPeriodStatus(Integer periodStatus) {
        this.periodStatus = periodStatus;
        return this;
    }

    public String getNoticeObjId() {
        return noticeObjId;
    }

    public PubPublicInfo setNoticeObjId(String noticeObjId) {
        this.noticeObjId = noticeObjId;
        return this;
    }

    public String getNoticeObjName() {
        return noticeObjName;
    }

    public PubPublicInfo setNoticeObjName(String noticeObjName) {
        this.noticeObjName = noticeObjName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public PubPublicInfo setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public PubPublicInfo setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
        return this;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public PubPublicInfo setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
        return this;
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

    public PubPublicInfo setProcessId(String processId) {
        this.processId = processId;
        return this;
    }

}
