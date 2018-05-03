package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 用户-岗位表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-01
 */
public class AutUserPosition extends Entity<AutUserPosition> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 岗位ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long positionId;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 排序
     */
    private Integer userPositionRank;

    public Integer getUserPositionRank() {
        return userPositionRank;
    }

    public void setUserPositionRank(Integer userPositionRank) {
        this.userPositionRank = userPositionRank;
    }

    public Long getUserId() {
        return userId;
    }

    public AutUserPosition setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public AutUserPosition setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public Long getPositionId() {
        return positionId;
    }

    public AutUserPosition setPositionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutUserPosition setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
