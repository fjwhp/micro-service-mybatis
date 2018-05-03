package aljoin.ioa.dao.object;

import java.util.Date;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppWaitTaskDO {
    /**
     * 标题
     */
    private String title;

    /**
     * 前办理人
     */
    private String formerManager;

    /**
     * 创建人
     */
    private String founder;

    /**
     * 表单类型
     */
    private String formType;
    /**
     * 缓急
     */
    private String urgency;

    /**
     * 签收时间
     */
    private Date signInTime;

    /**
     * 收件时间
     */
    private Date receiveTime;

    /**
     * 环节
     */
    private String link;

    /*
     * 任务id
     */
    private String taskId;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * bpmnId
     */
    private String bpmnId;

    /**
     * taskDefKey
     */
    private String taskDefKey;
    /**
     * businessKey
     */
    private String businessKey;

    private Integer urgencyStatus;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormerManager() {
        return formerManager;
    }

    public void setFormerManager(String formerManager) {
        this.formerManager = formerManager;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getBpmnId() {
        return bpmnId;
    }

    public void setBpmnId(String bpmnId) {
        this.bpmnId = bpmnId;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Integer getUrgencyStatus() {
        return urgencyStatus;
    }

    public void setUrgencyStatus(Integer urgencyStatus) {
        this.urgencyStatus = urgencyStatus;
    }
}
