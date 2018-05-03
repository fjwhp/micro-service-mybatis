package aljoin.pub.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 公共信息分类表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-16
 */
public class PubPublicInfoCategory extends Entity<PubPublicInfoCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 状态 (0：无效 1：有效)
     */
    @ApiModelProperty(value = "状态 (0：无效 1：有效)", hidden = true)
    private Integer isActive;
    /**
     * 分类名称
     */
    @ApiModelProperty(value = " 分类名称")
    private String name;
    /**
     * 排序
     */
    @ApiModelProperty(value = "排序", hidden = true)
    private Integer categoryRank;
    /**
     * 使用流程ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = "流程ID", hidden = true)
    private String processId;
    /**
     * 使用流程名称
     */
    @ApiModelProperty(value = "流程名称", hidden = true)
    private String processName;
    /**
     * 使用群体ID （多个用分号分隔）
     */
    @ApiModelProperty(value = "使用群体ID （多个用分号分隔）", hidden = true)
    private String useGroupId;
    /**
     * 使用群体 （多个用分号分隔）
     */
    @ApiModelProperty(value = "使用群体 （多个用分号分隔)", hidden = true)
    private String useGroupName;
    /**
     * 是否使用流程(0:否 1:是)
     */
    @ApiModelProperty(value = "是否使用流程(0:否 1:是)", hidden = true)
    private Integer isUse;

    public Integer getIsActive() {
        return isActive;
    }

    public PubPublicInfoCategory setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getName() {
        return name;
    }

    public PubPublicInfoCategory setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getCategoryRank() {
        return categoryRank;
    }

    public void setCategoryRank(Integer categoryRank) {
        this.categoryRank = categoryRank;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getUseGroupId() {
        return useGroupId;
    }

    public PubPublicInfoCategory setUseGroupId(String useGroupId) {
        this.useGroupId = useGroupId;
        return this;
    }

    public String getUseGroupName() {
        return useGroupName;
    }

    public PubPublicInfoCategory setUseGroupName(String useGroupName) {
        this.useGroupName = useGroupName;
        return this;
    }

    public Integer getIsUse() {
        return isUse;
    }

    public void setIsUse(Integer isUse) {
        this.isUse = isUse;
    }
}
