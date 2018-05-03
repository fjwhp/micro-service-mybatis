package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutUsefulOpinion;
import aljoin.aut.dao.entity.AutUser;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutUsefulOpinionVO {

    private AutUsefulOpinion autUsefulOpinion;

    private AutUser autUser;

    public AutUsefulOpinion getAutUsefulOpinion() {
        return autUsefulOpinion;
    }

    public void setAutUsefulOpinion(AutUsefulOpinion autUsefulOpinion) {
        this.autUsefulOpinion = autUsefulOpinion;
    }

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

}
