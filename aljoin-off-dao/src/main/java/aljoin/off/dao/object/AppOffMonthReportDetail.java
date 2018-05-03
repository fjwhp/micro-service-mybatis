package aljoin.off.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 工作月报详情表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class AppOffMonthReportDetail implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

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

    public AppOffMonthReportDetail setMonthReportId(Long monthReportId) {
        this.monthReportId = monthReportId;
        return this;
    }

    public Long getDailylogId() {
        return dailylogId;
    }

    public AppOffMonthReportDetail setDailylogId(Long dailylogId) {
        this.dailylogId = dailylogId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AppOffMonthReportDetail setContent(String content) {
        this.content = content;
        return this;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
