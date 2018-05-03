package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.entity.AutUserPub;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutUserPubVO {

    private AutUserPub autUserPub;

    /**
     * 用户表
     */
    private AutUser autUser;

    /**
     * 修改密码时的新密码
     */
    private String newUserPwd;

    /**
     * 修改密码时再次输入的新密码
     */
    private String newUserPwdConfirm;

    /**
     * 部门表
     */
    private AutDepartment autDepartment;

    /**
     * 部门-岗位表
     */
    private AutPosition autPosition;

    /**
     * 用户-岗位表
     */
    private AutUserPosition autUserPosition;

    /**
     * 用户-部门表
     */
    private AutDepartmentUser autDepartmentUser;

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
     * 图片在资源表的业务id
     */
    private Long bizId;
    
    /**
     * 文件描述
     */
    private String fileDesc;
    
    /**
     * 文件路径
     */
    private String path;
    
    /**
     * 资源id
     */
    private Long resourceId;

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

    public AutUserPub getAutUserPub() {
        return autUserPub;
    }

    public void setAutUserPub(AutUserPub autUserPub) {
        this.autUserPub = autUserPub;
    }

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public String getNewUserPwd() {
        return newUserPwd;
    }

    public void setNewUserPwd(String newUserPwd) {
        this.newUserPwd = newUserPwd;
    }

    public String getNewUserPwdConfirm() {
        return newUserPwdConfirm;
    }

    public void setNewUserPwdConfirm(String newUserPwdConfirm) {
        this.newUserPwdConfirm = newUserPwdConfirm;
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

    public AutUserPosition getAutUserPosition() {
        return autUserPosition;
    }

    public void setAutUserPosition(AutUserPosition autUserPosition) {
        this.autUserPosition = autUserPosition;
    }

    public AutDepartmentUser getAutDepartmentUser() {
        return autDepartmentUser;
    }

    public void setAutDepartmentUser(AutDepartmentUser autDepartmentUser) {
        this.autDepartmentUser = autDepartmentUser;
    }

    public Long getBizId() {
        return bizId;
    }

    public void setBizId(Long bizId) {
        this.bizId = bizId;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }
    
}
