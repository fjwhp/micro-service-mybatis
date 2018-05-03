package aljoin.ioa.dao.object;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AllTaskShowVO {
    /**
     * 表单类型
     */
    private String formType;
    /**
     * 缓急
     */
    private String isUrgency;
    /**
     * 标题
     */
    private String title;
    /**
     * 办里人
     */
    private String operator;
    /**
     * 创建人
     */
    private String founder;

    /**
     * 创建人
     */
    private String operatorTime;
    /**
     * 创建时间
     */
    private String creationTime;

    private String businessKey;
    private String processInstanceId;

    public String getIsUrgency() {
        return isUrgency;
    }

    public void setIsUrgency(String isUrgency) {
        this.isUrgency = isUrgency;
    }

    public String getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(String operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

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

    /*
       * 归档时间
       */
    private String endTime;

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

}
