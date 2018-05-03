package aljoin.off.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 工作月报表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class OffMonthReportCountDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 序号
     */
    private Integer no;

    /**
     * 机关
     */
    private String office;
    /**
     * 处室
     */
    private String deptName;
    /**
     * 处室
     */
    private String fullName;
    /**
     * 状态(未提交)
     */
    private Integer unSubmitNumber;

    /**
     * 状态(0:未提交 1:已提交)
     */
    private Integer submitNumber;
    private String showDate;

    public String getShowDate() {
        return showDate;
    }

    public void setShowDate(String showDate) {
        this.showDate = showDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getUnSubmitNumber() {
        return unSubmitNumber;
    }

    public void setUnSubmitNumber(Integer unSubmitNumber) {
        this.unSubmitNumber = unSubmitNumber;
    }

    public Integer getSubmitNumber() {
        return submitNumber;
    }

    public void setSubmitNumber(Integer submitNumber) {
        this.submitNumber = submitNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
