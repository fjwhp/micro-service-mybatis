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
public final class AutUserDO {

    /**
     * 
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private AutUser autUser;

    /**
     * 标识是否被选中
     */
    private Integer isCheck;

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }
}
