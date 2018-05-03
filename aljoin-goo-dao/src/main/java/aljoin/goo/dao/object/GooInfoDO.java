package aljoin.goo.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 办公用品信息实体类
 *
 * @author：xuc
 * 
 * @date：2018年1月4日 下午3:11:34
 */
public class GooInfoDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public Long id;

    /**
     * 序号
     */
    private Integer no;
    /**
     * 所属分类ID （办公用品表分类ID）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    /**
     * 所属分类名称
     */
    private String categoryName;
    /**
     * 物品名称
     */
    private String gooName;
    /**
     * 单位
     */
    private String unit;

    public String getGooName() {
        return gooName;
    }

    public void setGooName(String gooName) {
        this.gooName = gooName;
    }

    /**
     * 物品编号
     */
    private String gooCode;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 预警值
     */
    private Integer emerNum;
    /**
     * 剩余数量
     */
    private Integer number;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getGooCode() {
        return gooCode;
    }

    public void setGooCode(String gooCode) {
        this.gooCode = gooCode;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getEmerNum() {
        return emerNum;
    }

    public void setEmerNum(Integer emerNum) {
        this.emerNum = emerNum;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
