package aljoin.pub.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppPubPublicInfoVO implements Serializable {
    /**
     * TODO
     */
    private static final long serialVersionUID = 8045554905052602091L;

    /**
     * 组合搜索 关键字
     */
    @ApiModelProperty(hidden = true)
    private String searchKey;

    /**
     * 填报开始时间
     */
    @ApiModelProperty(hidden = true)
    private String begDate;

    /**
     * 填报结束时间
     */
    @ApiModelProperty(hidden = true)
    private String endDate;

    /**
     * 分类ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private String categoryId;

    /**
     * 标题
     */
    @ApiModelProperty(hidden = true)
    private String title;

    /**
     * 发布人
     */
    @ApiModelProperty(hidden = true)
    private String publishName;

    /**
     * 发布人ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUserId;

    public String getBegDate() {
        return begDate;
    }

    public void setBegDate(String begDate) {
        this.begDate = begDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublishName() {
        return publishName;
    }

    public void setPublishName(String publishName) {
        this.publishName = publishName;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }
}
