package aljoin.ioa.dao.object;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;

public class IoaRegHairVO {
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
     * 登记日期
     */
    @ApiModelProperty(hidden = true)
    private Date registrationTime;
    /**
     * 发文文号
     */
    @ApiModelProperty(hidden = true)
    private String hairNo;
    /**
     * 发文单位
     */
    @ApiModelProperty(hidden = true)
    private String hairUnit;
    /**
     * 所属分类
     */
    @ApiModelProperty(hidden = true)
    private String category;
    /**
     * 签发时间
     */
    @ApiModelProperty(hidden = true)
    private Date hairTime;
    /**
     * 文件类型
     */
    @ApiModelProperty(hidden = true)
    private String hairType;
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
     * 是否按照登记日期正序
     */
    @ApiModelProperty(hidden = true)
    private String isRegistrationTimeAsc;
    /**
     * 是否按照发文日期正序
     */
    @ApiModelProperty(hidden = true)
    private String isHairTimeAsc;
    /**
     * 是否按照发文文号正序
     */
    @ApiModelProperty(hidden = true)
    private String ishairNoAsc;

    @ApiModelProperty(hidden = true)
    private Long createUserId;
    
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

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getHairNo() {
        return hairNo;
    }

    public void setHairNo(String hairNo) {
        this.hairNo = hairNo;
    }

    public String getHairUnit() {
        return hairUnit;
    }

    public void setHairUnit(String hairUnit) {
        this.hairUnit = hairUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getHairTime() {
        return hairTime;
    }

    public void setHairTime(Date hairTime) {
        this.hairTime = hairTime;
    }

    public String getHairType() {
        return hairType;
    }

    public void setHairType(String hairType) {
        this.hairType = hairType;
    }

    public String getIsRegistrationTimeAsc() {
        return isRegistrationTimeAsc;
    }

    public void setIsRegistrationTimeAsc(String isRegistrationTimeAsc) {
        this.isRegistrationTimeAsc = isRegistrationTimeAsc;
    }

    public String getIsHairTimeAsc() {
        return isHairTimeAsc;
    }

    public void setIsHairTimeAsc(String isHairTimeAsc) {
        this.isHairTimeAsc = isHairTimeAsc;
    }

    public String getIshairNoAsc() {
        return ishairNoAsc;
    }

    public void setIshairNoAsc(String ishairNoAsc) {
        this.ishairNoAsc = ishairNoAsc;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
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


}
