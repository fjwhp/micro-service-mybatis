package aljoin.ioa.dao.object;

import java.util.Date;

/**
 * 描述：传阅列表及传阅查询参数V
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class CirulaDO {
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 任务创建人
	 */
	private String taskFounder;
	/**
	 * 当前传阅对象ID
	 */
	private String cirUserId;

	/**
	 * 当前办理人
	 */
	private String currentAdmin;
	/**
	 * 传阅记录创建人
	 */
	private String founder;
	/**
	 * 流程名称
	 */
	private String flowName;
	/**
	 * 流程ID
	 */
	private String flowId;

	/**
	 * 流程分类ID/流程分类名称
	 */
	private String flowCategory;
	/**
	 * 缓急
	 */
	private String urgency;
	/**
	 * 查询开始时间
	 */
	private Date startDate;

	/**
	 * 传阅时间/查询结束时间
	 */
	private Date circulaDate;

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
	 * 阅读状态
	 */
	private String readStatus;
	/**
	 * 文号
	 */
	private String documentNumber;
	/**
	 * 任务ID
	 */
	private String taskId;
	/**
	 * 是否按照文号升序
	 */
	private String isdocumentNumberAsc;
	/**
	 * 是否按照传阅时间升序
	 */
	private String isCirculateTimeAsc;

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
	

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
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

	public String getTaskFounder() {
		return taskFounder;
	}

	public void setTaskFounder(String taskFounder) {
		this.taskFounder = taskFounder;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public String getFlowCategory() {
		return flowCategory;
	}

	public void setFlowCategory(String flowCategory) {
		this.flowCategory = flowCategory;
	}

	public Date getCirculaDate() {
		return circulaDate;
	}

	public void setCirculaDate(Date circulaDate) {
		this.circulaDate = circulaDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getCirUserId() {
		return cirUserId;
	}

	public void setCirUserId(String cirUserId) {
		this.cirUserId = cirUserId;
	}

    public String getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }


    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getIsdocumentNumberAsc() {
        return isdocumentNumberAsc;
    }

    public void setIsdocumentNumberAsc(String isdocumentNumberAsc) {
        this.isdocumentNumberAsc = isdocumentNumberAsc;
    }

    public String getIsCirculateTimeAsc() {
        return isCirculateTimeAsc;
    }

    public void setIsCirculateTimeAsc(String isCirculateTimeAsc) {
        this.isCirculateTimeAsc = isCirculateTimeAsc;
    }
}
