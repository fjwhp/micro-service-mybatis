package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutUserInfo;
import aljoin.dao.entity.Entity;

/**
 * 
 * 用户信息表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-06
 */
public class AutUserInfoVO extends Entity<AutUserInfo> {

    private static final long serialVersionUID = 1L;

    private List<AutUserInfo> userInfoList;

    public List<AutUserInfo> getUserInfoList() {
        return userInfoList;
    }

    public void setUserInfoList(List<AutUserInfo> userInfoList) {
        this.userInfoList = userInfoList;
    }
}
