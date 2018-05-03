package aljoin.ioa.dao.object;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class DoTaskShowVO {
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
     * 当前办理人
     */
    private String currentAdmin;
    /**
     * 处理时间
     */
    private Date processingDate;
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
     * 填报时间是否升序排列 (0:否 1：是)
     */
    private String fillingDateIsAsc;

    /**
     * 紧急状态是否升序排列 (0:否 1：是)
     */
    private String urgencyIsAsc;

    /**
     * 流程名称
     */
    private String processName;

    /**
     * 开始创建时间
     */
    private String createBegTime;

    /**
     * 结束创建时间
     */
    private String createEndTime;
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

    public String getCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(String currentAdmin) {
        this.currentAdmin = currentAdmin;
    }

    public Date getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
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

    public String getFillingDateIsAsc() {
        return fillingDateIsAsc;
    }

    public void setFillingDateIsAsc(String fillingDateIsAsc) {
        this.fillingDateIsAsc = fillingDateIsAsc;
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

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getCreateBegTime() {
        return createBegTime;
    }

    public void setCreateBegTime(String createBegTime) {
        this.createBegTime = createBegTime;
    }

    public String getCreateEndTime() {
        return createEndTime;
    }

    public void setCreateEndTime(String createEndTime) {
        this.createEndTime = createEndTime;
    }
}
