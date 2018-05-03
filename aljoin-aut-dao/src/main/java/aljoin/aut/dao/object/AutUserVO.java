package aljoin.aut.dao.object;

import java.math.BigDecimal;
import java.util.List;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;

/**
 * 
 * 用户值对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:08:24
 */
public final class AutUserVO extends AutUser {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 角色列表
     */
    private List<AutRoleVO> roleList;
    /**
     * 菜单列表
     */
    private List<AutMenuVO> menuList;
    /**
     * 用户IDlist
     */
    private List<Long> idList;
    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门表
     */
    private AutDepartment autDepartment;

    /**
     * 岗位表
     */
    private AutPosition autPosition;
    /**
     * 用户表
     */
    private AutUser autUser;
    /**
     * 用户-部门关联表
     */
    private AutDepartmentUser autDepartmentUser;
    /**
     * 用户-岗位关联表
     */
    private AutUserPosition autUserPosition;

    /**
     * 用户-多部门
     */
    private String autDeptIds;

    /**
     * 用户-多部门
     */
    private String autDeptNames;

    /**
     * 用户-多岗位
     */
    private String positionids;
    /**
     * 用户-多岗位
     */
    private String positionNames;
    /**
     * 用户公共信息
     */
    private String phone;
    /**
     * 用户头像
     */
    private String userIcon;

    private BigDecimal userRank;

    public String getAutDeptIds() {
        return autDeptIds;
    }

    public void setAutDeptIds(String autDeptIds) {
        this.autDeptIds = autDeptIds;
    }

    public String getAutDeptNames() {
        return autDeptNames;
    }

    public void setAutDeptNames(String autDeptNames) {
        this.autDeptNames = autDeptNames;
    }

    public String getPositionids() {
        return positionids;
    }

    public void setPositionids(String positionids) {
        this.positionids = positionids;
    }

    public String getPositionNames() {
        return positionNames;
    }

    public void setPositionNames(String positionNames) {
        this.positionNames = positionNames;
    }

    public List<AutRoleVO> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<AutRoleVO> roleList) {
        this.roleList = roleList;
    }

    public List<AutMenuVO> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<AutMenuVO> menuList) {
        this.menuList = menuList;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public AutDepartment getAutDepartment() {
        return autDepartment;
    }

    public void setAutDepartment(AutDepartment autDepartment) {
        this.autDepartment = autDepartment;
    }

    public AutPosition getAutPosition() {
        return autPosition;
    }

    public void setAutPosition(AutPosition autPosition) {
        this.autPosition = autPosition;
    }

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public AutDepartmentUser getAutDepartmentUser() {
        return autDepartmentUser;
    }

    public void setAutDepartmentUser(AutDepartmentUser autDepartmentUser) {
        this.autDepartmentUser = autDepartmentUser;
    }

    public AutUserPosition getAutUserPosition() {
        return autUserPosition;
    }

    public void setAutUserPosition(AutUserPosition autUserPosition) {
        this.autUserPosition = autUserPosition;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BigDecimal getUserRank() {
        return userRank;
    }

    public void setUserRank(BigDecimal userRank) {
        this.userRank = userRank;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

}
