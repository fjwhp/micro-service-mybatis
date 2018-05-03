package aljoin.ioa.dao.object;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class WaitTaskShowVO {
    /**
     * 表单类型
     */
    private String formType;
    /**
     * 缓急
     */
    private String urgency;
    /**
     * 标题
     */
    private String title;
    /**
     * 环节
     */
    private String link;
    /**
     * 前办理人
     */
    private String formerManager;
    /**
     * 签收时间
     */
    private Date signInTime;
    /**
     * 填报时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fillingDate;
    /**
     * 创建人
     */
    private String founder;

    /**
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

    /**
     * 填报时间是否升序排列 (0:否 1：是)
     */
    private String fillingDateIsAsc;

    /**
     * 缓急状态（1：一般 2：紧急 3：加急）
     */
    private Integer urgencyStatus;

    /**
     * 紧急状态是否升序排列 (0:否 1：是)
     */
    private String urgencyIsAsc;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 收件开始时间
     */
    private String receiveBegTime;

    /**
     * 收件结束时间
     */
    private String receiveEndTime;

    /**
     * 文号
     */
    private String referenceNumber;
    /**
     * 编号
     */
    private String serialNumber;

    public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getBpmnId() {
        return bpmnId;
    }

    public void setBpmnId(String bpmnId) {
        this.bpmnId = bpmnId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFormerManager() {
        return formerManager;
    }

    public void setFormerManager(String formerManager) {
        this.formerManager = formerManager;
    }

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public Date getFillingDate() {
        return fillingDate;
    }

    public void setFillingDate(Date fillingDate) {
        this.fillingDate = fillingDate;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getUrgencyIsAsc() {
        return urgencyIsAsc;
    }

    public void setUrgencyIsAsc(String urgencyIsAsc) {
        this.urgencyIsAsc = urgencyIsAsc;
    }

    public Integer getUrgencyStatus() {
        return urgencyStatus;
    }

    public void setUrgencyStatus(Integer urgencyStatus) {
        this.urgencyStatus = urgencyStatus;
    }

    public String getFillingDateIsAsc() {
        return fillingDateIsAsc;
    }

    public void setFillingDateIsAsc(String fillingDateIsAsc) {
        this.fillingDateIsAsc = fillingDateIsAsc;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getReceiveBegTime() {
        return receiveBegTime;
    }

    public void setReceiveBegTime(String receiveBegTime) {
        this.receiveBegTime = receiveBegTime;
    }

    public String getReceiveEndTime() {
        return receiveEndTime;
    }

    public void setReceiveEndTime(String receiveEndTime) {
        this.receiveEndTime = receiveEndTime;
    }
}
