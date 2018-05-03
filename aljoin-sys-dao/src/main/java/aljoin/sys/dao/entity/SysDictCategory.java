package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：数据字典分类表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
public class SysDictCategory extends Entity<SysDictCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
	private String categoryName;
    /**
     * 分类排序
     */
	private Integer categoryRank;
    /**
     * 分类级别
     */
	private Integer categoryLevel;
    /**
     * 父级分类ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long parentId;
    /**
     * 是否激活（1：是；0：否）
     */
	private Integer isActive;
    /**
     * 字典类型（1：流水号分类；2：公文文号分类；3常用字典分类）
     */
	private Integer dictType;


	public String getCategoryName() {
		return categoryName;
	}

	public SysDictCategory setCategoryName(String categoryName) {
		this.categoryName = categoryName;
		return this;
	}

	public Integer getCategoryRank() {
		return categoryRank;
	}

	public SysDictCategory setCategoryRank(Integer categoryRank) {
		this.categoryRank = categoryRank;
		return this;
	}

	public Integer getCategoryLevel() {
		return categoryLevel;
	}

	public SysDictCategory setCategoryLevel(Integer categoryLevel) {
		this.categoryLevel = categoryLevel;
		return this;
	}

	public Long getParentId() {
		return parentId;
	}

	public SysDictCategory setParentId(Long parentId) {
		this.parentId = parentId;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public SysDictCategory setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

	public Integer getDictType() {
		return dictType;
	}

	public SysDictCategory setDictType(Integer dictType) {
		this.dictType = dictType;
		return this;
	}

}
