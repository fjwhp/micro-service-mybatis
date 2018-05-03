package aljoin.act.dao.object;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class DraftTaskShowVO {
    /**
     * 类型
     */
    private String formType;
    /**
     * 缓急
     */
    private String urgency;
    /**
     * 名称
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
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private String outTime;

    private String draftId;

    private String businessKey;

    private String bid;

    private String taskDefKey;

    private String activityId;

    private Date signInTime;

    public Date getSignInTime() {
        return signInTime;
    }

    public void setSignInTime(Date signInTime) {
        this.signInTime = signInTime;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getTaskDefKey() {
        return taskDefKey;
    }

    public void setTaskDefKey(String taskDefKey) {
        this.taskDefKey = taskDefKey;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getDraftId() {
        return draftId;
    }

    public void setDraftId(String draftId) {
        this.draftId = draftId;
    }

    public String getOutTime() {
        if (this.createTime != null) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            outTime = formatter.format(createTime);
        } else {
            outTime = "";
        }
        return outTime;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DraftTaskShowVO [formType=" + formType + ", urgency=" + urgency + ", title=" + title + ", link=" + link
            + ", formerManager=" + formerManager + ", createTime=" + createTime + ", outTime=" + outTime + ", draftId="
            + draftId + ", businessKey=" + businessKey + ", bid=" + bid + ", taskDefKey=" + taskDefKey + ", activityId="
            + activityId + ", signInTime=" + signInTime + ", founder=" + founder + ", taskId=" + taskId
            + ", processInstanceId=" + processInstanceId + "]";
    }

}
