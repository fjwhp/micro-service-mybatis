package aljoin.act.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 表单表(运行时)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-28
 */
public class ActAljoinFormRun extends Entity<ActAljoinFormRun> {

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
    /**
     * 源表单分类ID（原来的主键ID）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orgnlId;

    public String getFormName() {
        return formName;
    }

    public ActAljoinFormRun setFormName(String formName) {
        this.formName = formName;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public ActAljoinFormRun setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getHtmlCode() {
        return htmlCode;
    }

    public ActAljoinFormRun setHtmlCode(String htmlCode) {
        this.htmlCode = htmlCode;
        return this;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public ActAljoinFormRun setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public Long getOrgnlId() {
        return orgnlId;
    }

    public ActAljoinFormRun setOrgnlId(Long orgnlId) {
        this.orgnlId = orgnlId;
        return this;
    }

}
