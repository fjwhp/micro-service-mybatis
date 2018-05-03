package aljoin.off.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;

/**
 * 
 * 日程安排表(历史表)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-01
 */
public class OffSchedulingHis extends Entity<OffSchedulingHis> {

    private static final long serialVersionUID = 1L;

    /**
     * 主题
     */
    private String theme;
    /**
     * 地点
     */
    private String place;
    /**
     * 开始日期
     */
    private Date startDate;
    /**
     * 开始小时和分钟
     */
    private String startHourMin;
    /**
     * 结束日期
     */
    private Date endDate;
    /**
     * 结束小时和分钟
     */
    private String endHourMin;
    /**
     * 内容
     */
    private String content;
    /**
     * 日程类型(1:共享日程 2:个人计划 3:会议通知)
     */
    private Integer type;
    /**
     * 共享给ID（被共享人ID,多个用分号分隔）
     */
    private String sharedPersonId;
    /**
     * 共享给姓名（被共享人姓名,多个用分号分隔）
     */
    private String sharedPersonName;
    /**
     * 是否短信提醒
     */
    private Integer isWarnMsg;
    /**
     * 是否邮件提醒
     */
    private Integer isWarnMail;
    /**
     * 是否在线提醒
     */
    private Integer isWarnOnline;
    /**
     * 业务主键ID
     */
    private Long bizId;
    /**
     * 业务类型
     */
    private String bizType;

    public String getTheme() {
        return theme;
    }

    public OffSchedulingHis setTheme(String theme) {
        this.theme = theme;
        return this;
    }

    public String getPlace() {
        return place;
    }

    public OffSchedulingHis setPlace(String place) {
        this.place = place;
        return this;
    }

    public Date getStartDate() {
        return startDate;
    }

    public OffSchedulingHis setStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getStartHourMin() {
        return startHourMin;
    }

    public OffSchedulingHis setStartHourMin(String startHourMin) {
        this.startHourMin = startHourMin;
        return this;
    }

    public Date getEndDate() {
        return endDate;
    }

    public OffSchedulingHis setEndDate(Date endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getEndHourMin() {
        return endHourMin;
    }

    public OffSchedulingHis setEndHourMin(String endHourMin) {
        this.endHourMin = endHourMin;
        return this;
    }

    public String getContent() {
        return content;
    }

    public OffSchedulingHis setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getType() {
        return type;
    }

    public OffSchedulingHis setType(Integer type) {
        this.type = type;
        return this;
    }

    public String getSharedPersonId() {
        return sharedPersonId;
    }

    public OffSchedulingHis setSharedPersonId(String sharedPersonId) {
        this.sharedPersonId = sharedPersonId;
        return this;
    }

    public String getSharedPersonName() {
        return sharedPersonName;
    }

    public OffSchedulingHis setSharedPersonName(String sharedPersonName) {
        this.sharedPersonName = sharedPersonName;
        return this;
    }

    public Integer getIsWarnMsg() {
        return isWarnMsg;
    }

    public OffSchedulingHis setIsWarnMsg(Integer isWarnMsg) {
        this.isWarnMsg = isWarnMsg;
        return this;
    }

    public Integer getIsWarnMail() {
        return isWarnMail;
    }

    public OffSchedulingHis setIsWarnMail(Integer isWarnMail) {
        this.isWarnMail = isWarnMail;
        return this;
    }

    public Integer getIsWarnOnline() {
        return isWarnOnline;
    }

    public OffSchedulingHis setIsWarnOnline(Integer isWarnOnline) {
        this.isWarnOnline = isWarnOnline;
        return this;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }
}
