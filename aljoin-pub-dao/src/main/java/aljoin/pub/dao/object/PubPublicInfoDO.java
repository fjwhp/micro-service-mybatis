package aljoin.pub.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 公共信息表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public class PubPublicInfoDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public Long id;

    /**
     * 是否激活
     */
    private Integer no;
    /**
     * 所属分类ID （公共信息表分类ID）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    /**
     * 所属分类名称
     */
    private String categoryName;
    /**
     * 标题（公告名称）
     */
    private String title;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
    private String auditStatus;
    /**
     * 创建日期
     */
    private String createDate;
    /**
     * 有效期
     */
    private String validate;
    /**
     * 创建人ID
     */
    private Long createUserId;
    /**
     * 发布人
     */
    private String publishName;
    /**
     * 已读未读
     */
    private Integer isRead;
    /**
     * 姓名
     */
    private String createFullName;

    public String getCreateFullName() {
        return createFullName;
    }

    public void setCreateFullName(String createFullName) {
        this.createFullName = createFullName;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getValidate() {
        return validate;
    }

    public void setValidate(String validate) {
        this.validate = validate;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
