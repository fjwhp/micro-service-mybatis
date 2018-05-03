package aljoin.veh.dao.object;

import aljoin.veh.dao.entity.VehInfo;
import aljoin.veh.dao.entity.VehUse;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppVehUseVO extends VehUse {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 车船
     */
    private VehInfo info;

    /**
     * 签收状态
     */
    private Integer claimStatus;

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }

    public VehInfo getInfo() {
        return info;
    }

    public void setInfo(VehInfo info) {
        this.info = info;
    }
}
