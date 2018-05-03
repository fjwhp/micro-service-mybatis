package aljoin.off.dao.object;

import java.util.List;

import aljoin.off.dao.entity.OffMonthReport;
import aljoin.res.dao.entity.ResResource;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 工作月报表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class OffMonthReportVO extends OffMonthReport {

    private static final long serialVersionUID = 1L;

    /**
     * 开始提交时间
     */
    @ApiModelProperty(value = "开始提交时间", required = false, hidden = true)
    private String begTime;

    /**
     * 结束提交时间
     */
    @ApiModelProperty(value = " 结束提交时间", required = false, hidden = true)
    private String endTime;
    /**
     * 日志列表
     */
    @ApiModelProperty(value = " 日志列表", required = false, hidden = true)
    private List<OffMonthReportDetailVO> monthReportDetailList;

    /**
     * 用户名
     */
    @ApiModelProperty(hidden = true)
    private String fullName;
    /**
     * 部门ID
     */
    @ApiModelProperty(hidden = true)
    private Long deptId;
    /**
     * 本周
     */
    @ApiModelProperty(hidden = true)
    private String thisWeek;
    /**
     * 本月
     */
    @ApiModelProperty(hidden = true)
    private String thisMonth;
    /**
     * 未提交排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String unSubmitSort;
    /**
     * 已提交排序(1:降序 其它：升序)
     */
    private String submitSort;

    private String dayOfMonth;

    /**
     * 月报附件
     */
    private List<ResResource> resResourceMRList;

    public List<OffMonthReportDetailVO> getMonthReportDetailList() {
        return monthReportDetailList;
    }

    public void setMonthReportDetailList(List<OffMonthReportDetailVO> monthReportDetailList) {
        this.monthReportDetailList = monthReportDetailList;
    }

    public String getBegTime() {
        return begTime;
    }

    public void setBegTime(String begTime) {
        this.begTime = begTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(String thisWeek) {
        this.thisWeek = thisWeek;
    }

    public String getThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(String thisMonth) {
        this.thisMonth = thisMonth;
    }

    public String getUnSubmitSort() {
        return unSubmitSort;
    }

    public void setUnSubmitSort(String unSubmitSort) {
        this.unSubmitSort = unSubmitSort;
    }

    public String getSubmitSort() {
        return submitSort;
    }

    public void setSubmitSort(String submitSort) {
        this.submitSort = submitSort;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public List<ResResource> getResResourceMRList() {
        return resResourceMRList;
    }

    public void setResResourceMRList(List<ResResource> resResourceMRList) {
        this.resResourceMRList = resResourceMRList;
    }

    
}
