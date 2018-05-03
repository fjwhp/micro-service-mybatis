package aljoin.tim.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 任务调度表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-15
 */
public class TimSchedule extends Entity<TimSchedule> {

    private static final long serialVersionUID = 1L;

    /**
     * 任务运行时间表达式
     */
    private String cronExpression;
    /**
     * 任务描述
     */
    private String jobDesc;
    /**
     * 任务分组
     */
    private String jobGroup;
    /**
     * 任务名称
     */
    private String jobName;
    /**
     * 任务状态：NONE-已经完成，且不会再执行，或者找不到该触发器，或者Trigger已经被删除;NORMAL-正常状态;PAUSED-暂停状态;COMPLETE-触发器完成，但是任务可能还正在执行中;BLOCKED-线程阻塞状态
     * ;ERROR-出现错误
     */
    private String jobStatus;
    /**
     * 任务执行类名
     */
    private String exeClassName;
    /**
     * 开始时间
     */
    private Date begTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 下一次执行时间
     */
    private Date nextTime;
    /**
     * 上一次执行时间
     */
    private Date previousTime;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 业务类型（标记业务模块）
     */
    private String bizType;
    /**
     * 业务主键（关联业务主表）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long bizId;
    /**
     * 是否自动启动
     */
    private Integer isAuto;

    public String getCronExpression() {
        return cronExpression;
    }

    public TimSchedule setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
        return this;
    }

    public String getJobDesc() {
        return jobDesc;
    }

    public TimSchedule setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
        return this;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public TimSchedule setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
        return this;
    }

    public String getJobName() {
        return jobName;
    }

    public TimSchedule setJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public TimSchedule setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
        return this;
    }

    public String getExeClassName() {
        return exeClassName;
    }

    public TimSchedule setExeClassName(String exeClassName) {
        this.exeClassName = exeClassName;
        return this;
    }

    public Date getBegTime() {
        return begTime;
    }

    public TimSchedule setBegTime(Date begTime) {
        this.begTime = begTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public TimSchedule setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public Date getNextTime() {
        return nextTime;
    }

    public TimSchedule setNextTime(Date nextTime) {
        this.nextTime = nextTime;
        return this;
    }

    public Date getPreviousTime() {
        return previousTime;
    }

    public TimSchedule setPreviousTime(Date previousTime) {
        this.previousTime = previousTime;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public TimSchedule setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getBizType() {
        return bizType;
    }

    public TimSchedule setBizType(String bizType) {
        this.bizType = bizType;
        return this;
    }

    public Long getBizId() {
        return bizId;
    }

    public TimSchedule setBizId(Long bizId) {
        this.bizId = bizId;
        return this;
    }

    public Integer getIsAuto() {
        return isAuto;
    }

    public TimSchedule setIsAuto(Integer isAuto) {
        this.isAuto = isAuto;
        return this;
    }

}
