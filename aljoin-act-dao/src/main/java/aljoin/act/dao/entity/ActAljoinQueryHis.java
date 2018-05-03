package aljoin.act.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;

/**
 * 
 * 流程查询表(历史表)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-02
 */
public class ActAljoinQueryHis extends Entity<ActAljoinQueryHis> {

    private static final long serialVersionUID = 1L;

    /**
     * 主流程实例ID
     */
    private String processInstanceId;
    /**
     * 流程名称
     */
    private String processName;
    /**
     * 流程标题
     */
    private String processTitle;
    /**
     * 流程创建人姓名
     */
    private String createFullUserName;
    /**
     * 流程当前办理人
     */
    private String currentHandleFullUserName;
    /**
     * 流程ID,从一级到末级
     */
    private String processCategoryIds;
    /**
     * 紧急状态：一般，紧急，加急
     */
    private String urgentStatus;
    /**
     * 限办时间
     */
    private Date limitFinishTime;
    /**
     * 发起时间
     */
    private Date startTime;
    /**
     * 开始任务ID
     */
    private String startTask;
    
    /**
	 * 编号
	 */
	private String serialNumber;
	/**
	 * 文号
	 */
	private String referenceNumber;
	
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public ActAljoinQueryHis setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
        return this;
    }

    public String getProcessName() {
        return processName;
    }

    public ActAljoinQueryHis setProcessName(String processName) {
        this.processName = processName;
        return this;
    }

    public String getProcessTitle() {
        return processTitle;
    }

    public ActAljoinQueryHis setProcessTitle(String processTitle) {
        this.processTitle = processTitle;
        return this;
    }

    public String getCreateFullUserName() {
        return createFullUserName;
    }

    public ActAljoinQueryHis setCreateFullUserName(String createFullUserName) {
        this.createFullUserName = createFullUserName;
        return this;
    }

    public String getCurrentHandleFullUserName() {
        return currentHandleFullUserName;
    }

    public ActAljoinQueryHis setCurrentHandleFullUserName(String currentHandleFullUserName) {
        this.currentHandleFullUserName = currentHandleFullUserName;
        return this;
    }

    public String getProcessCategoryIds() {
        return processCategoryIds;
    }

    public ActAljoinQueryHis setProcessCategoryIds(String processCategoryIds) {
        this.processCategoryIds = processCategoryIds;
        return this;
    }

    public String getUrgentStatus() {
        return urgentStatus;
    }

    public ActAljoinQueryHis setUrgentStatus(String urgentStatus) {
        this.urgentStatus = urgentStatus;
        return this;
    }

    public Date getLimitFinishTime() {
        return limitFinishTime;
    }

    public ActAljoinQueryHis setLimitFinishTime(Date limitFinishTime) {
        this.limitFinishTime = limitFinishTime;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public ActAljoinQueryHis setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public String getStartTask() {
        return startTask;
    }

    public ActAljoinQueryHis setStartTask(String startTask) {
        this.startTask = startTask;
        return this;
    }

}
