package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * 岗位(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-17
 */
public class AutPosition extends Entity<AutPosition> {

    private static final long serialVersionUID = 1L;

    /**
     * 岗位名称
     */
    private String positionName;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 部门编号
     */
    private String deptCode;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 分类排序
     */
    private Integer positionRank;

    public String getPositionName() {
        return positionName;
    }

    public AutPosition setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public Long getDeptId() {
        return deptId;
    }

    public AutPosition setDeptId(Long deptId) {
        this.deptId = deptId;
        return this;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public AutPosition setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutPosition setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public Integer getPositionRank() {
        return positionRank;
    }

    public AutPosition setPositionRank(Integer positionRank) {
        this.positionRank = positionRank;
        return this;
    }

}
