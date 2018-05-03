package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AppAutDepartment;

import java.io.Serializable;
import java.util.List;

/**
 *
 * 组织机构App(实体类).
 *
 * @author：wangj.
 *
 * @date： 2017-11-02
 */

public class AppAutOrganVO implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 部门列表
     */
    private List<AppAutDepartment> departmentList;

    /**
     * 部门用户列表
     */
    private List<AppAutDepartmentUserVO> departmentUserList;

    public List<AppAutDepartment> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<AppAutDepartment> departmentList) {
        this.departmentList = departmentList;
    }

    public List<AppAutDepartmentUserVO> getDepartmentUserList() {
        return departmentUserList;
    }

    public void setDepartmentUserList(List<AppAutDepartmentUserVO> departmentUserList) {
        this.departmentUserList = departmentUserList;
    }
}
