package aljoin.ioa.dao.object;

import java.util.Date;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppDoTaskDO {
    /**
     * 标题
     */
    private String title;

    /**
     * 当前办理人
     */
    private String currentAdmin;

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
     * 处理时间
     */
    private Date processingDate;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 环节
     */
    private String link;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * businessKey
     */
    private String businessKey;

    /**
     * 缓急状态（1：一般 2：紧急 3：加急）
     */
    private Integer urgencyStatus;
    /**
     * 任务ID
     */
    private String taskId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(String currentAdmin) {
        this.currentAdmin = currentAdmin;
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

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
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

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

}
