package aljoin.ioa.dao.object;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppDoTaskVO {
    /**
     * 表单类型
     */
    @ApiModelProperty(hidden = true)
    private String formTypeId;

    /**
     * 创建人
     */
    @ApiModelProperty(hidden = true)
    private String founder;

    /**
     * 开始创建时间
     */
    @ApiModelProperty(hidden = true)
    private String createBegTime;

    /**
     * 结束创建时间
     */
    @ApiModelProperty(hidden = true)
    private String createEndTime;

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
     * 创建时间是否升序排列 (0:否 1：是)
     */
    @ApiModelProperty(hidden = true)
    private String createDateIsAsc;

    /**
     * 紧急状态是否升序排列 (0:否 1：是)
     */
    @ApiModelProperty(hidden = true)
    private String urgencyIsAsc;

    /**
     * 当前办理人
     */
    private String currentAdmin;

    public String getFormTypeId() {
        return formTypeId;
    }

    public void setFormTypeId(String formTypeId) {
        this.formTypeId = formTypeId;
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
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

    public String getCreateDateIsAsc() {
        return createDateIsAsc;
    }

    public void setCreateDateIsAsc(String createDateIsAsc) {
        this.createDateIsAsc = createDateIsAsc;
    }

    public String getUrgencyIsAsc() {
        return urgencyIsAsc;
    }

    public void setUrgencyIsAsc(String urgencyIsAsc) {
        this.urgencyIsAsc = urgencyIsAsc;
    }

    public String getCurrentAdmin() {
        return currentAdmin;
    }

    public void setCurrentAdmin(String currentAdmin) {
        this.currentAdmin = currentAdmin;
    }
}
