package aljoin.veh.dao.object;

import aljoin.veh.dao.entity.VehMaintain;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class VehMaintainVO extends VehMaintain {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    @ApiModelProperty(hidden = true)
    private String begTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(hidden = true)
    private String endTime;

    public String getBegTime() {
        return begTime;
    }

    public void setBegTime(String begTime) {
        this.begTime = begTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
