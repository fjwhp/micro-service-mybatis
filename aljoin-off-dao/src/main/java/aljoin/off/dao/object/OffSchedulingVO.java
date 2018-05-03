package aljoin.off.dao.object;

import aljoin.off.dao.entity.OffScheduling;

/**
 * 
 * 日程安排表(实体类).
 * 
 * @author：sunln.
 * 
 * @date： 2017-11-02
 */
public class OffSchedulingVO extends OffScheduling {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 日程安排（日程开始时间）
     */
    private String scheduleStartDate;

    /**
     * 日程安排（日程结束时间）
     */
    private String scheduleEndDate;

    /**
     * 序号
     */
    private Integer no;

    /**
     * 开始时间
     */
    private String startDateStr;

    /**
     * 结束时间
     */
    private String endDateStr;
    /**
     * 日程类型(1:共享日程 2:个人计划 3:会议通知)
     */
    private String typeStr;

    /**
     * 创建人
     */
    private String createFullName;

    /**
     * 创建时间
     */
    private String createTimeStr;
    /**
     * 是否可编辑
     */
    private Integer isEdit;

    public String getScheduleStartDate() {
        return scheduleStartDate;
    }

    public void setScheduleStartDate(String scheduleStartDate) {
        this.scheduleStartDate = scheduleStartDate;
    }

    public String getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(String scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getStartDateStr() {
        return startDateStr;
    }

    public void setStartDateStr(String startDateStr) {
        this.startDateStr = startDateStr;
    }

    public String getEndDateStr() {
        return endDateStr;
    }

    public void setEndDateStr(String endDateStr) {
        this.endDateStr = endDateStr;
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public String getCreateFullName() {
        return createFullName;
    }

    public void setCreateFullName(String createFullName) {
        this.createFullName = createFullName;
    }

    public Integer getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Integer isEdit) {
        this.isEdit = isEdit;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

}
