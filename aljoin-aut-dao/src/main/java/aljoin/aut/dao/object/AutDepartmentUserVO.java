package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutDepartmentUserVO extends AutDepartmentUser {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 前端传来的用户信息
     */
    @ApiModelProperty(hidden = true)
    private AutUser autUser;
    /**
     * 用于存放AutUser的List
     */
    @ApiModelProperty(hidden = true)
    private List<AutUser> autUserList;
    /**
     * 用于存放Autdepartment的List
     */
    @ApiModelProperty(hidden = true)
    private List<AutDepartment> autDepartmentList;
    /**
     * 用户存放“部门-用户”表List
     */
    @ApiModelProperty(hidden = true)
    private List<AutDepartmentUser> autDepartmentUserList;

    /**
     * 用户姓名
     */
    @ApiModelProperty(hidden = true)
    private String fullName;

    /**
     * 部门名称
     */
    @ApiModelProperty(hidden = true)
    private String deptNames;

    /**
     * 部门ID
     */
    @ApiModelProperty(hidden = true)
    private String departmentIds;

    /**
     * 用户ID
     */
    @ApiModelProperty(hidden = true)
    private String userIds;

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public List<AutDepartmentUser> getAutDepartmentUserList() {
        return autDepartmentUserList;
    }

    public void setAutDepartmentUserList(List<AutDepartmentUser> autDepartmentUserList) {
        this.autDepartmentUserList = autDepartmentUserList;
    }

    public List<AutDepartment> getAutDepartmentList() {
        return autDepartmentList;
    }

    public void setAutDepartmentList(List<AutDepartment> autDepartmentList) {
        this.autDepartmentList = autDepartmentList;
    }

    public List<AutUser> getAutUserList() {
        return autUserList;
    }

    public void setAutUserList(List<AutUser> autUserList) {
        this.autUserList = autUserList;
    }

    public String getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(String deptNames) {
        this.deptNames = deptNames;
    }

    public String getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(String departmentIds) {
        this.departmentIds = departmentIds;
    }

    public String getUserIds() {
        return userIds;
    }

    public void setUserIds(String userIds) {
        this.userIds = userIds;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
