package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 表单分类表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-31
 */
public class ActAljoinFormCategory extends Entity<ActAljoinFormCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 表单分类名称(唯一)
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

    public String getCategoryName() {
        return categoryName;
    }

    public ActAljoinFormCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinFormCategory setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public ActAljoinFormCategory setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public ActAljoinFormCategory setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public ActAljoinFormCategory setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
        return this;
    }

}
