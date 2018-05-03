package aljoin.veh.dao.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 车船维护信息表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-08
 */
public class VehMaintain extends Entity<VehMaintain> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    @ApiModelProperty(hidden = true)
    private Integer isActive;
    /**
     * 经办人ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long agentId;
    /**
     * 经办人姓名
     */
    @ApiModelProperty(hidden = true)
    private String agentName;
    /**
     * 牌号
     */
    @ApiModelProperty(hidden = true)
    private String carCode;
    /**
     * 维护类型
     */
    @ApiModelProperty(hidden = true)
    private String maintainType;
    /**
     * 备注
     */
    @ApiModelProperty(hidden = true)
    private String content;
    /**
     * 维护时间
     */
    @ApiModelProperty(hidden = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date maintainTime;
    /**
     * 维护费用
     */
    @ApiModelProperty(hidden = true)
    private BigDecimal maintainCost;

    public Integer getIsActive() {
        return isActive;
    }

    public VehMaintain setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Long getAgentId() {
        return agentId;
    }

    public VehMaintain setAgentId(Long agentId) {
        this.agentId = agentId;
        return this;
    }

    public String getAgentName() {
        return agentName;
    }

    public VehMaintain setAgentName(String agentName) {
        this.agentName = agentName;
        return this;
    }

    public String getCarCode() {
        return carCode;
    }

    public VehMaintain setCarCode(String carCode) {
        this.carCode = carCode;
        return this;
    }

    public String getMaintainType() {
        return maintainType;
    }

    public VehMaintain setMaintainType(String maintainType) {
        this.maintainType = maintainType;
        return this;
    }

    public String getContent() {
        return content;
    }

    public VehMaintain setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getMaintainTime() {
        return maintainTime;
    }

    public VehMaintain setMaintainTime(Date maintainTime) {
        this.maintainTime = maintainTime;
        return this;
    }

    public BigDecimal getMaintainCost() {
        return maintainCost;
    }

    public VehMaintain setMaintainCost(BigDecimal maintainCost) {
        this.maintainCost = maintainCost;
        return this;
    }

}
