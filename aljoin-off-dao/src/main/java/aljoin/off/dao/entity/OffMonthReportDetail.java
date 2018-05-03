package aljoin.off.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 工作月报详情表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class OffMonthReportDetail extends Entity<OffMonthReportDetail> {

    private static final long serialVersionUID = 1L;

    /**
     * 月报ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long monthReportId;
    /**
     * 日志ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dailylogId;
    /**
     * 内容
     */
    private String content;
    /**
     * 日期（日志日期加星期）
     */
    private String workDate;

    public Long getMonthReportId() {
        return monthReportId;
    }

    public OffMonthReportDetail setMonthReportId(Long monthReportId) {
        this.monthReportId = monthReportId;
        return this;
    }

    public Long getDailylogId() {
        return dailylogId;
    }

    public OffMonthReportDetail setDailylogId(Long dailylogId) {
        this.dailylogId = dailylogId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public OffMonthReportDetail setContent(String content) {
        this.content = content;
        return this;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }
}
