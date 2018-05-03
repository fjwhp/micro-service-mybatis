package aljoin.object;

import java.util.Date;

/**
 * 
 * 固定表单流程日志(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-11-07
 */
public class FixedFormProcessLog {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    /**
     * 操作人ID
     */
    private String operationId;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 操作人
     */
    private String operationName;

    /**
     * 流转方向
     */
    private String direction;

    /**
     * 接收人ID
     */
    private String recevieUserId;

    /**
     * 接收人
     */
    private String recevieUserName;

    /**
     * 操作时间
     */
    private Date operationTime;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public Date getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(Date operationTime) {
        this.operationTime = operationTime;
    }

    public String getRecevieUserName() {
        return recevieUserName;
    }

    public void setRecevieUserName(String recevieUserName) {
        this.recevieUserName = recevieUserName;
    }

    public String getRecevieUserId() {
        return recevieUserId;
    }

    public void setRecevieUserId(String recevieUserId) {
        this.recevieUserId = recevieUserId;
    }
}
