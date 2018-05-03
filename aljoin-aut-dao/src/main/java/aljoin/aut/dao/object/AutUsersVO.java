package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutUser;

/**
 * 
 * 用户值对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:08:24
 */
public final class AutUsersVO extends AutUser {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private AutUser autUser;

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    private String userids;

    public String getUserids() {
        return userids;
    }

    public void setUserids(String userids) {
        this.userids = userids;
    }

    public String getManids() {
        return manids;
    }

    public void setManids(String manids) {
        this.manids = manids;
    }

    private String manids;

}
