 package aljoin.sys.dao.object;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @作者：caizx
 * 
 * @时间: 2018-03-27
 */
public class SysDocumentNumberDO {
    
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    
    /**
     * 创建人
     */
    private String fullName;
    
    /**
     * 文号名称
     */
    private String documentNumName;
    
    /**
     * 文号
     */
    private String documentNum;
    
    /**
     * 文号分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    
    /**
     * 文号分类名称
     */
    private String categoryName;
    
    /**
     * 状态
     */
    private String statusName;
    
    /**
     * 状态（1：启用；0：停用）
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDocumentNumName() {
        return documentNumName;
    }

    public void setDocumentNumName(String documentNumName) {
        this.documentNumName = documentNumName;
    }

    public String getDocumentNum() {
        return documentNum;
    }

    public void setDocumentNum(String documentNum) {
        this.documentNum = documentNum;
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

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
