package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutRole;

/**
 * 
 * 角色对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:09:27
 */
public class AutRoleDO {

    private AutRole autRole;
    private Integer isCheck;

    public AutRole getAutRole() {
        return autRole;
    }

    public void setAutRole(AutRole autRole) {
        this.autRole = autRole;
    }

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

}
