package aljoin.aut.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * 部门用户App(实体类).
 *
 * @author：wangj.
 *
 * @date： 2017-11-02
 */

public class AppAutDepartmentUserVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long deptId;
    /**
     * 部门编号
     */
    private String deptCode;

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
     * 部门用户的排序
     */
    private Integer departmentUserRank;

    /**
     * 是否本部门领导
     */
    private Integer isLeader;

    /**
     * 用户名
     */
    private String fullName;

    /**
     * 用户头像
     */
    private String headImg;

    /**
     * 人员排序
     */
    private BigDecimal userRank;

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(Integer isLeader) {
        this.isLeader = isLeader;
    }

    public Integer getDepartmentUserRank() {
        return departmentUserRank;
    }

    public void setDepartmentUserRank(Integer departmentUserRank) {
        this.departmentUserRank = departmentUserRank;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public BigDecimal getUserRank() {
        return userRank;
    }

    public void setUserRank(BigDecimal userRank) {
        this.userRank = userRank;
    }
}
