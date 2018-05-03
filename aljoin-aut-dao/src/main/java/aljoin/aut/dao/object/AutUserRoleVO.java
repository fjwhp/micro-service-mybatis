package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserRole;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutUserRoleVO {

    private List<AutUserRole> autUserRoleList;

    private List<AutUser> autUserList;

    public List<AutUserRole> getAutUserRoleList() {
        return autUserRoleList;
    }

    public void setAutUserRoleList(List<AutUserRole> autUserRoleList) {
        this.autUserRoleList = autUserRoleList;
    }

    public List<AutUser> getAutUserList() {
        return autUserList;
    }

    public void setAutUserList(List<AutUser> autUserList) {
        this.autUserList = autUserList;
    }

}
