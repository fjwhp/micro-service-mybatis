 package aljoin.sys.dao.object;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @作者：caizx
 * 
 * @时间: 2018-03-25
 */
public class SysSerialNumberDO {
    
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
     * 流水号名称
     */
    private String serialNumName;
    
    /**
     * 流水号
     */
    private String serialNum;
    
    /**
     * 流水号分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    
    /**
     * 流水号分类名称
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

    public String getSerialNumName() {
        return serialNumName;
    }

    public void setSerialNumName(String serialNumName) {
        this.serialNumName = serialNumName;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
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
    
    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
