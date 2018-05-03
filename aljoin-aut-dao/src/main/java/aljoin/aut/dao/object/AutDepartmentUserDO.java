package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutDepartmentUserDO {
    /**
     * 部门-用户表对象
     */
    private AutDepartmentUser autDepartmentUser;
    /**
     * 用于存放AutUser的List
     */
    private List<AutUser> autUserList;
    /**
     * 用于存放Autdepartment的List
     */
    private List<AutDepartment> autDepartmentList;

    public List<AutDepartment> getAutDepartmentList() {
        return autDepartmentList;
    }

    public void setAutDepartmentList(List<AutDepartment> autDepartmentList) {
        this.autDepartmentList = autDepartmentList;
    }

    public AutDepartmentUser getAutDepartmentUser() {
        return autDepartmentUser;
    }

    public void setAutDepartmentUser(AutDepartmentUser autDepartmentUser) {
        this.autDepartmentUser = autDepartmentUser;
    }

    public List<AutUser> getAutUserList() {
        return autUserList;
    }

    public void setAutUserList(List<AutUser> autUserList) {
        this.autUserList = autUserList;
    }

}
