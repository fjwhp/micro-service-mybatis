package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 表单表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
public class ActAljoinForm extends Entity<ActAljoinForm> {

    private static final long serialVersionUID = 1L;

    /**
     * 表单名称(唯一)
     */
    private String formName;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * html编码(base64编码后)
     */
    private String htmlCode;
    /**
     * 表单分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;

    public String getFormName() {
        return formName;
    }

    public ActAljoinForm setFormName(String formName) {
        this.formName = formName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinForm setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public ActAljoinForm setHtmlCode(String htmlCode) {
        this.htmlCode = htmlCode;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public ActAljoinForm setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

}
