package aljoin.off.dao.object;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.off.dao.entity.OffScheduling;

/**
 * 
 * 日程安排表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-14
 */
public class OffSchedulingDO {

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
     * 主题
     */
    private String theme;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;
    /**
     * 日程类型(1:共享日程 2:个人计划 3:会议通知)
     */
    private String type;

    /**
     * 创建人ID
     */
    private Long createUserId;

    /**
     * 创建人
     */
    private String createFullName;
    /**
     * 首页参数-会议日程月份
     */
    private String month;
    /**
     * 首页参数-会议日程时间
     */
    private String meetDay;

    private String bizType;

    private List<OffScheduling> schedule;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateFullName() {
        return createFullName;
    }

    public void setCreateFullName(String createFullName) {
        this.createFullName = createFullName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OffScheduling> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<OffScheduling> schedule) {
        this.schedule = schedule;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMeetDay() {
        return meetDay;
    }

    public void setMeetDay(String meetDay) {
        this.meetDay = meetDay;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

}
