package aljoin.ioa.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 发文登记表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaRegHair extends Entity<IoaRegHair> {

    private static final long serialVersionUID = 1L;   

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
     * 密级
     */
    @ApiModelProperty(hidden = true)
    private int secretLevel;
    /**
     * 密级
     */
    @ApiModelProperty(hidden = true)
    private String level;
    /**
     * 所属分类
     */
    @ApiModelProperty(hidden = true)
    private String category;
    /**
     * 签发时间
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date hairTime;
    /**
     *  文件类型
     */
    @ApiModelProperty(hidden = true)
    private String hairType;
    /**
     *  是否可以修改，删除
     */
    @ApiModelProperty(hidden = true)
    private int isChange;
    
	public int getIsChange() {
		return isChange;
	}
	public void setIsChange(int isChange) {
		this.isChange = isChange;
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
	public Date getRegistrationTime() {
		return registrationTime;
	}
	public void setRegistrationTime(Date registration_time) {
		this.registrationTime = registration_time;
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
	public int getSecretLevel() {
		return secretLevel;
	}
	public void setSecretLevel(int secretLevel) {
		this.secretLevel = secretLevel;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
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
    

  
   
    

   
  
  
 
}
