package aljoin.ass.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 固定资产信息表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-11
 */
public class AssInfo extends Entity<AssInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
	private Integer isActive;
    /**
     * 物品名
     */
	private String assName;
    /**
     * 所属分类ID （固定资产表分类ID） 
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long categoryId;
    /**
     * 所属分类名称 （固定资产表分类名称） 
     */
	private String categoryName;
    /**
     * 所属部门ID 
     */
	private String departmentId;
    /**
     * 所属部门名称
     */
	private String departmentName;
    /**
     * 负责人ID 
     */
	private String agentId;
    /**
     * 负责人名称
     */
	private String agentName;
    /**
     * 规格型号
     */
	private String assType;
    /**
     * 存放地点
     */
	private String inPlace;
    /**
     * 物品数量
     */
	private Integer assNumber;
    /**
     * 新增时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date addTime;
    /**
     * 编号
     */
	private String assCode;
    /**
     * 单位
     */
	private String unit;
    /**
     * 已用年限
     */
	private String alreadyUseTime;


	public Integer getIsActive() {
		return isActive;
	}

	public AssInfo setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

	public String getAssName() {
		return assName;
	}

	public AssInfo setAssName(String assName) {
		this.assName = assName;
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public AssInfo setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public AssInfo setCategoryName(String categoryName) {
		this.categoryName = categoryName;
		return this;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public AssInfo setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
		return this;
	}

	public String getDepartmentId() {
    return departmentId;
  }

  public void setDepartmentId(String departmentId) {
    this.departmentId = departmentId;
  }

  public String getAgentId() {
    return agentId;
  }

  public void setAgentId(String agentId) {
    this.agentId = agentId;
  }

  public String getAgentName() {
		return agentName;
	}

	public AssInfo setAgentName(String agentName) {
		this.agentName = agentName;
		return this;
	}

	public String getAssType() {
		return assType;
	}

	public AssInfo setAssType(String assType) {
		this.assType = assType;
		return this;
	}

	public String getInPlace() {
		return inPlace;
	}

	public AssInfo setInPlace(String inPlace) {
		this.inPlace = inPlace;
		return this;
	}

	public Integer getAssNumber() {
		return assNumber;
	}

	public AssInfo setAssNumber(Integer assNumber) {
		this.assNumber = assNumber;
		return this;
	}

	public Date getAddTime() {
		return addTime;
	}

	public AssInfo setAddTime(Date addTime) {
		this.addTime = addTime;
		return this;
	}

	public String getAssCode() {
		return assCode;
	}

	public AssInfo setAssCode(String assCode) {
		this.assCode = assCode;
		return this;
	}

	public String getUnit() {
		return unit;
	}

	public AssInfo setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public String getAlreadyUseTime() {
		return alreadyUseTime;
	}

	public AssInfo setAlreadyUseTime(String alreadyUseTime) {
		this.alreadyUseTime = alreadyUseTime;
		return this;
	}

}
