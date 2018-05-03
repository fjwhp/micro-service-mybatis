package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutPosition;

/**
 *
 * 岗位对象
 *
 * @author：wangj
 *
 * @date：2017年8月17日 下午5:12:27
 */
public class AutPositionDO {

    private AutPosition autPosition;

    private Integer isCheck;

    private String deptName;

    public AutPosition getAutPosition() {
        return autPosition;
    }

    public void setAutPosition(AutPosition autPosition) {
        this.autPosition = autPosition;
    }

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
}
