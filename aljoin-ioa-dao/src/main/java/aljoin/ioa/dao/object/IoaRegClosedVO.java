 package aljoin.ioa.dao.object;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

public class IoaRegClosedVO{
    
    /**
     * 收文标题
     */
    @ApiModelProperty(hidden = true)
    private String title;
    /**
     * 登记人名称
     */
    @ApiModelProperty(hidden = true)
    private String registrationName;
    /**
     * 登记日期(开始)
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    /**
     * 登记日期(结束)
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    /**
     * 收文文号
     */
    @ApiModelProperty(hidden = true)
    private String closedNo;
    /**
     * 来文文号
     */
    @ApiModelProperty(hidden = true)
    private String toNo;
    /**
     * 是否根据收文文号正序
     */
    @ApiModelProperty(hidden = true)
    private String isClosedNoAsc;
    /**
     * 是否根据来文文号正序
     */
    @ApiModelProperty(hidden = true)
    private String istoNoAsc;
    /**
     * 是否根据登记时间正序
     */
    @ApiModelProperty(hidden = true)
    private String isRegistrationTimeAsc;
    /**
     * 来文单位
     */
    @ApiModelProperty(hidden = true)
    private String toUnit;
    /**
     * 所属分类
     */
    @ApiModelProperty(hidden = true)
    private String category;
    
    
    public String getIsClosedNoAsc() {
        return isClosedNoAsc;
    }

    public void setIsClosedNoAsc(String isClosedNoAsc) {
        this.isClosedNoAsc = isClosedNoAsc;
    }

    public String getIstoNoAsc() {
        return istoNoAsc;
    }

    public void setIstoNoAsc(String istoNoAsc) {
        this.istoNoAsc = istoNoAsc;
    }

    public String getIsRegistrationTimeAsc() {
        return isRegistrationTimeAsc;
    }

    public void setIsRegistrationTimeAsc(String isRegistrationTimeAsc) {
        this.isRegistrationTimeAsc = isRegistrationTimeAsc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRegistrationName() {
        return registrationName;
    }

    public void setRegistrationName(String registrationName) {
        this.registrationName = registrationName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getClosedNo() {
        return closedNo;
    }

    public void setClosedNo(String closedNo) {
        this.closedNo = closedNo;
    }

    public String getToNo() {
        return toNo;
    }

    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    public String getToUnit() {
        return toUnit;
    }

    public void setToUnit(String toUnit) {
        this.toUnit = toUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
