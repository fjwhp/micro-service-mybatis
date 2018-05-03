package aljoin.off.dao.object;

import aljoin.res.dao.entity.ResResource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 工作月报表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class AppOffMonthReportDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 主管领导意见
     */
    private String comment;

    /**
     * 标题
     */
    private String title;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 部门
     */
    private String belongDept;

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
     * 签收状态
     */
    private Integer claimStatus;

    /**
     * 日志列表
     */
    @ApiModelProperty(value = " 日志列表", required = false, hidden = true)
    private List<AppOffMonthReportDetailDO> monthReportDetailList;

    /**
     * 月报附件
     */
    private List<ResResource> resResourceMRList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public String getBelongDept() {
        return belongDept;
    }

    public void setBelongDept(String belongDept) {
        this.belongDept = belongDept;
    }

    public List<AppOffMonthReportDetailDO> getMonthReportDetailList() {
        return monthReportDetailList;
    }

    public void setMonthReportDetailList(List<AppOffMonthReportDetailDO> monthReportDetailList) {
        this.monthReportDetailList = monthReportDetailList;
    }

    public List<ResResource> getResResourceMRList() {
        return resResourceMRList;
    }

    public void setResResourceMRList(List<ResResource> resResourceMRList) {
        this.resResourceMRList = resResourceMRList;
    }

    public Long getSubmitId() {
        return submitId;
    }

    public void setSubmitId(Long submitId) {
        this.submitId = submitId;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }
}
