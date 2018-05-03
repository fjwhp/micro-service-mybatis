package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.entity.AutUser;

/**
 * 
 * 用户-岗位对象
 *
 * @author：laijy
 * 
 * @date：2017年9月1日 下午1:21:36
 */
public class AutUserPositionVO {
    /**
     * 用户对象
     */
    private AutUser autUser;
    /**
     * 岗位列表
     */
    private List<AutPosition> positionList;

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public List<AutPosition> getPositionList() {
        return positionList;
    }

    public void setPositionList(List<AutPosition> positionList) {
        this.positionList = positionList;
    }

}
