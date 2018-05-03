 package aljoin.sys.dao.object;

import aljoin.aut.dao.entity.AutUser;
import aljoin.sys.dao.entity.SysPublicOpinion;

/**
 * @作者：caizx
 * 
 * @时间: 2018-03-15
 */
public class SysPublicOpinionVO extends SysPublicOpinion {

    /**
     *TODO
     */
    private static final long serialVersionUID = 1L;
    
    
    /**
     * 多个公共信息id
     */
    private String ids;
    
    /**
     * 用户类
     */
    private AutUser autUser;
    
    /**
     * 公共意见类
     */
    private SysPublicOpinion sysPublicOpinion;
    
    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public SysPublicOpinion getSysPublicOpinion() {
        return sysPublicOpinion;
    }

    public void setSysPublicOpinion(SysPublicOpinion sysPublicOpinion) {
        this.sysPublicOpinion = sysPublicOpinion;
    }
    

}
