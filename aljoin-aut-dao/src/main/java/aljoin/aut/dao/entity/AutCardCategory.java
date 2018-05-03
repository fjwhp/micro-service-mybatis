package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 名片分类表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public class AutCardCategory extends Entity<AutCardCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 名片分类名称(唯一)
     */
    private String categoryName;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * (同级)分类排序
     */
    private Integer categoryRank;
    /**
     * 分类归属用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    public String getCategoryName() {
        return categoryName;
    }

    public AutCardCategory setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutCardCategory setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public AutCardCategory setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AutCardCategory setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

}
