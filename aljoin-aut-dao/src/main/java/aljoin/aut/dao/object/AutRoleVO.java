package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutRole;

import java.util.List;

/**
 * 
 * 角色值对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:09:27
 */
public class AutRoleVO extends AutRole {

    /**
     * TODO
     */
    private static final long serialVersionUID = 1L;

    private List<Long> idList;

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
