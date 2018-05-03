package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

/**
 * 流程分类表(实体类).
 *
 * @author：zhongjy.
 *
 * @date： 2017-08-31
 */
public class ActAljoinCategory extends Entity<ActAljoinCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程分类名称(唯一)
     */
    @ApiModelProperty(hidden = false)
    private String categoryName;
    /**
     * 是否激活
     */
    @ApiModelProperty(hidden = false)
    private Integer isActive;
    /**
     * (同级)分类排序
     */
    @ApiModelProperty(hidden = false)
    private Integer categoryRank;
    /**
     * 父级分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(hidden = false)
    private Long parentId;
    /**
     * 分类级别
     */
    @ApiModelProperty(hidden = false)
    private Integer categoryLevel;

    public String getCategoryName() {
        return categoryName;
    }

    public ActAljoinCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinCategory setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public ActAljoinCategory setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public ActAljoinCategory setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getCategoryLevel() {
        return categoryLevel;
    }

    public ActAljoinCategory setCategoryLevel(Integer categoryLevel) {
        this.categoryLevel = categoryLevel;
        return this;
    }

}
