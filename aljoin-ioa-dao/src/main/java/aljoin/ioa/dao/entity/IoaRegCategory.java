package aljoin.ioa.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 收发文分类表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-31
 */
public class IoaRegCategory extends Entity<IoaRegCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 收发文分类名称(唯一)
     */
    private String categoryName;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 同级分类排序
     */
    private Integer categoryRank;
    /**
     * 父级分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long parentId;
    /**
     * 分类级别
     */
    private Integer categoryLevel;
    
    /**
     * 分类级别  0收，1发
     */
    private String regType;

    public String getRegType() {
		return regType;
	}

	public void setRegType(String regType) {
		this.regType = regType;
	}

	public String getCategoryName() {
        return categoryName;
    }

    public IoaRegCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public IoaRegCategory setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public IoaRegCategory setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public IoaRegCategory setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public IoaRegCategory setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
        return this;
    }

}
