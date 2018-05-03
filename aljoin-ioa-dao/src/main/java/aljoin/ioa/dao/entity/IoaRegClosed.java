package aljoin.ioa.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 收文登记表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaRegClosed extends Entity<IoaRegClosed> {

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
	 * 来文类型
	 */
	@ApiModelProperty(hidden = true)
	private String toType;
	/**
	 * 来文单位
	 */
	@ApiModelProperty(hidden = true)
	private String toUnit;
	/**
	 * 密级
	 */
	@ApiModelProperty(hidden = true)
	private Integer secretLevel;
	/**
	 * 密级
	 */
	@ApiModelProperty(hidden = true)
	private String level;
	/**
	 * 收文日期
	 */
	@ApiModelProperty(hidden = true)
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date closedDate;
	/**
	 * 缓急程度
	 */
	@ApiModelProperty(hidden = true)
	private Integer prioritiesLevel;
	/**
	 * 页面显示缓急程度
	 */
	@ApiModelProperty(hidden = true)
	private String priorities;
	/**
	 * 份数
	 */
	@ApiModelProperty(hidden = true)
	private Integer closedNumber;
	/**
	 * 所属分类
	 */
	@ApiModelProperty(hidden = true)
	private String category;

	/**
	 * 是否可以修改，删除
	 */
	@ApiModelProperty(hidden = true)
	private Integer isChange;

	public Integer getIsChange() {
		return isChange;
	}

	public void setIsChange(Integer isChange) {
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

	public String getToType() {
		return toType;
	}

	public void setToType(String toType) {
		this.toType = toType;
	}
	public Date getRegistrationTime() {
		return registrationTime;
	}

	public void setRegistrationTime(Date registrationTime) {
		this.registrationTime = registrationTime;
	}
	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public Integer getSecretLevel() {
		return secretLevel;
	}

	public void setSecretLevel(Integer secretLevel) {
		this.secretLevel = secretLevel;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public Integer getPrioritiesLevel() {
		return prioritiesLevel;
	}

	public void setPrioritiesLevel(Integer prioritiesLevel) {
		this.prioritiesLevel = prioritiesLevel;
	}

	public String getPriorities() {		
		return priorities;
	}

	public void setPriorities(String priorities) {
		this.priorities = priorities;
	}

	public Integer getClosedNumber() {
		return closedNumber;
	}

	public void setClosedNumber(Integer closedNumber) {
		this.closedNumber = closedNumber;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

}
