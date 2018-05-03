package aljoin.veh.dao.object;

import java.util.List;

import aljoin.res.dao.entity.ResResource;
import aljoin.veh.dao.entity.VehInfo;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class VehInfoVO extends VehInfo {

    /**
     * TODO
     */
    private static final long serialVersionUID = 6630527656085012533L;
    private String deptName;
    /**
     * 多个车船id
     */
    private String ids;
    /**
     * 多个牌号
     */
    private String carCodes;
    /**
     * 附件列表
     */
    private List<ResResource> resResourceList;

    public String getCarCodes() {
        return carCodes;
    }

    public void setCarCodes(String carCodes) {
        this.carCodes = carCodes;
    }

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
