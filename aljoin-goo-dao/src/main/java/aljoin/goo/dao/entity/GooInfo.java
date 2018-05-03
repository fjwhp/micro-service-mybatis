package aljoin.goo.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 办公用品信息表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-04
 */
public class GooInfo extends Entity<GooInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 物品名
     */
    private String name;
    /**
     * 所属分类ID （办公用品表分类ID）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    /**
     * 状态（0:正常 1:预警）
     */
    private Integer status;
    /**
     * 简介
     */
    private String content;
    /**
     * 物品排序
     */
    private Integer gooRank;
    /**
     * 物品数量
     */
    private Integer number;
    /**
     * 预警值
     */
    private Integer emerNum;
    /**
     * 编号
     */
    private String gooCode;
    /**
     * 单位
     */
    private String unit;

    public Integer getIsActive() {
        return isActive;
    }

    public GooInfo setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getName() {
        return name;
    }

    public GooInfo setName(String name) {
        this.name = name;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public GooInfo setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Integer getStatus() {
        return status;
    }

    public GooInfo setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getContent() {
        return content;
    }

    public GooInfo setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getGooRank() {
        return gooRank;
    }

    public GooInfo setGooRank(Integer gooRank) {
        this.gooRank = gooRank;
        return this;
    }

    public Integer getNumber() {
        return number;
    }

    public GooInfo setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public Integer getEmerNum() {
        return emerNum;
    }

    public GooInfo setEmerNum(Integer emerNum) {
        this.emerNum = emerNum;
        return this;
    }

    public String getGooCode() {
        return gooCode;
    }

    public GooInfo setGooCode(String gooCode) {
        this.gooCode = gooCode;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public GooInfo setUnit(String unit) {
        this.unit = unit;
        return this;
    }

}
