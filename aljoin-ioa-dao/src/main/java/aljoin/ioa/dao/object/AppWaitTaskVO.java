package aljoin.ioa.dao.object;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppWaitTaskVO {
    /**
     * 收件开始时间
     */
    @ApiModelProperty(hidden = true)
    private String receiveBegTime;

    /**
     * 收件结束时间
     */
    @ApiModelProperty(hidden = true)
    private String receiveEndTime;

    /**
     * 创建人
     */
    @ApiModelProperty(hidden = true)
    private String founder;

    /**
     * 前办理人
     */
    @ApiModelProperty(hidden = true)
    private String formerManager;

    /**
     * 表单类型ID
     */
    @ApiModelProperty(hidden = true)
    private String formTypeId;

    /**
     * 缓急
     */
    @ApiModelProperty(hidden = true)
    private String urgency;

    /**
     * 标题
     */
    @ApiModelProperty(hidden = true)
    private String title;

    /**
     * 填报时间是否升序排列 (0:否 1：是)
     */
    @ApiModelProperty(hidden = true)
    private String receiveTimeIsAsc;

    /**
     * 缓急状态（1：一般 2：紧急 3：加急）
     */
    @ApiModelProperty(hidden = true)
    private Integer urgencyStatus;

    /**
     * 紧急状态是否升序排列 (0:否 1：是)
     */
    @ApiModelProperty(hidden = true)
    private String urgencyIsAsc;

    @ApiModelProperty(hidden = true)
    private List<Long> categoryIdList;

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

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    public String getFormerManager() {
        return formerManager;
    }

    public void setFormerManager(String formerManager) {
        this.formerManager = formerManager;
    }

    public String getFormTypeId() {
        return formTypeId;
    }

    public void setFormTypeId(String formTypeId) {
        this.formTypeId = formTypeId;
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

    public String getReceiveTimeIsAsc() {
        return receiveTimeIsAsc;
    }

    public void setReceiveTimeIsAsc(String receiveTimeIsAsc) {
        this.receiveTimeIsAsc = receiveTimeIsAsc;
    }

    public Integer getUrgencyStatus() {
        return urgencyStatus;
    }

    public void setUrgencyStatus(Integer urgencyStatus) {
        this.urgencyStatus = urgencyStatus;
    }

    public String getUrgencyIsAsc() {
        return urgencyIsAsc;
    }

    public void setUrgencyIsAsc(String urgencyIsAsc) {
        this.urgencyIsAsc = urgencyIsAsc;
    }

    public List<Long> getCategoryIdList() {
        return categoryIdList;
    }

    public void setCategoryIdList(List<Long> categoryIdList) {
        this.categoryIdList = categoryIdList;
    }
}
