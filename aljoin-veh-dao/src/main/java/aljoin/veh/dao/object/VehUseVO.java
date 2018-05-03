package aljoin.veh.dao.object;

import aljoin.veh.dao.entity.VehInfo;
import aljoin.veh.dao.entity.VehUse;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class VehUseVO extends VehUse {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 车船
     */
    private VehInfo info;

    public VehInfo getInfo() {
        return info;
    }

    public void setInfo(VehInfo info) {
        this.info = info;
    }
}
