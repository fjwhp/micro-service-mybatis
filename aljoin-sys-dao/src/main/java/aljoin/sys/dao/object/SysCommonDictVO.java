 package aljoin.sys.dao.object;

import aljoin.aut.dao.entity.AutUser;
import aljoin.sys.dao.entity.SysCommonDict;

/**
 * @作者：caizx
 * 
 * @时间: 2018-03-21
 */
public class SysCommonDictVO extends SysCommonDict {

    /**
     *TODO
     */
    private static final long serialVersionUID = -6656293472008887847L;
    
    /**
     * 用户类
     */
    private AutUser autUser;
    
    /**
     * 常用字典类
     */
    private SysCommonDict sysCommonDict;

    public AutUser getAutUser() {
        return autUser;
    }

    public void setAutUser(AutUser autUser) {
        this.autUser = autUser;
    }

    public SysCommonDict getSysCommonDict() {
        return sysCommonDict;
    }

    public void setSysCommonDict(SysCommonDict sysCommonDict) {
        this.sysCommonDict = sysCommonDict;
    }
    
}
