package aljoin.act.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class AllTaskDataShowVO {
    /**
     * 表单类型
     */
    private String formType;
    /**
     * 标题
     */
    private String title;
    /**
     * 操作人
     */
    private String operator;
    /**
     * 创建人
     */
    private String founder;
    private String htime;

    public String getHtime() {
        return htime;
    }

    public void setHtime(String htime) {
        this.htime = htime;
    }

    private String businessKey;
    private String processInstanceId;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    /**
     * 归档时间
     */
    private String endTime;
    private String urgentStatus;

    public String getUrgentStatus() {
        return urgentStatus;
    }

    public void setUrgentStatus(String urgentStatus) {
        this.urgentStatus = urgentStatus;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * 1 新系统数据 0旧系统数据
     */
    private String dataType;

    public String getFormType() {
        return formType;
    }

    public void setFormType(String formType) {
        this.formType = formType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "AllTaskDataShowVO [formType=" + formType + ", title=" + title + ", operator=" + operator + ", founder="
            + founder + ", htime=" + htime + ", businessKey=" + businessKey + ", processInstanceId=" + processInstanceId
            + ", endTime=" + endTime + ", urgentStatus=" + urgentStatus + ", dataType=" + dataType + "]";
    }

}
