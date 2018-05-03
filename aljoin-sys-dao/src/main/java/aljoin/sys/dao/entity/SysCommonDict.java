package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：常用字典表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-21
 */
public class SysCommonDict extends Entity<SysCommonDict> {

    private static final long serialVersionUID = 1L;

    /**
     * 常用字典名称
     */
	private String dictName;
    /**
     * 常用字典排序
     */
	private Integer dictRank;
    /**
     * 字典内容
     */
	private String dictContent;
    /**
     * 字典内容排序
     */
	private Integer dictContentRank;
    /**
     * 常用字典分类ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long categoryId;
    /**
     * 常用字典唯一标识符
     */
	private String dictCode;
    /**
     * 是否激活(1:是;0:否)
     */
	private Integer isActive;


	public String getDictName() {
		return dictName;
	}

	public SysCommonDict setDictName(String dictName) {
		this.dictName = dictName;
		return this;
	}

	public Integer getDictRank() {
		return dictRank;
	}

	public SysCommonDict setDictRank(Integer dictRank) {
		this.dictRank = dictRank;
		return this;
	}

	public String getDictContent() {
		return dictContent;
	}

	public SysCommonDict setDictContent(String dictContent) {
		this.dictContent = dictContent;
		return this;
	}

	public Integer getDictContentRank() {
		return dictContentRank;
	}

	public SysCommonDict setDictContentRank(Integer dictContentRank) {
		this.dictContentRank = dictContentRank;
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public SysCommonDict setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getDictCode() {
		return dictCode;
	}

	public SysCommonDict setDictCode(String dictCode) {
		this.dictCode = dictCode;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public SysCommonDict setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

}
