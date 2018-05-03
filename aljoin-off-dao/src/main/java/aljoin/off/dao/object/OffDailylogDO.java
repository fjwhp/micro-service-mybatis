package aljoin.off.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 工作日志(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-14
 */
public class OffDailylogDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 标题
     */
    private String title;

    /**
     * 创建日期
     */
    private String createDate;

    /**
     * 日志日期
     */
    private String workDate;

    /**
     * 创建人Id
     */
    private Long createUserId;

    /**
     * 创建人
     */
    private String createFullName;

    /**
     * 状态
     */
    private Integer status;
    /**
     * 日志所在月报id
     */
    private Long offMonthReportId;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getWorkDate() {
        return workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getCreateFullName() {
        return createFullName;
    }

    public void setCreateFullName(String createFullName) {
        this.createFullName = createFullName;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOffMonthReportId() {
        return offMonthReportId;
    }

    public void setOffMonthReportId(Long offMonthReportId) {
        this.offMonthReportId = offMonthReportId;
    }
}
