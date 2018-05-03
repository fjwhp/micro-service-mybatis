package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutDepartment;

/**
 * 
 * @author zhongjy
 * 
 * @date 2018年2月9日
 */
public class AutDepartmentDO extends AutDepartment {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private AutDepartment autDepartment;

    private Integer isCheck;

    public AutDepartment getAutDepartment() {
        return autDepartment;
    }

    public void setAutDepartment(AutDepartment autDepartment) {
        this.autDepartment = autDepartment;
    }

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }
}
