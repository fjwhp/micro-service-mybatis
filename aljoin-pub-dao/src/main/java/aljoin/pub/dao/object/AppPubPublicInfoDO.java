package aljoin.pub.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 
 * 公共信息表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public class AppPubPublicInfoDO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "主键", required = false, hidden = false)
    public Long id;

    /**
     * 标题（公告名称）
     */
    @ApiModelProperty(value = "标题", required = false, hidden = false)
    private String title;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "发布时间", required = false, hidden = false)
    private String createDate;

    /**
     * 创建人ID
     */
    @ApiModelProperty(value = "创建人ID", required = false, hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    /**
     * 发布人
     */
    @ApiModelProperty(value = "发布人", required = false, hidden = false)
    private String publishName;
    /**
     * 分类ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "分类ID", required = false, hidden = true)
    private Long categoryId;
    /**
     * 已读未读
     */
    private Integer isRead;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
