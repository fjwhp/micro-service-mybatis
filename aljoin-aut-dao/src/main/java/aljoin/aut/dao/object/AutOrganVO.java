package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutDepartment;

import java.util.List;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutOrganVO {

    /**
     * serialVersionUID
     */
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 部门列表
     */
    private List<AutDepartment> departmentList;

    /**
     * 部门用户列表
     */
    private List<AutDepartmentUserVO> departmentUserList;

    public List<AutDepartment> getDepartmentList() {
        return departmentList;
    }

    public void setDepartmentList(List<AutDepartment> departmentList) {
        this.departmentList = departmentList;
    }

    public List<AutDepartmentUserVO> getDepartmentUserList() {
        return departmentUserList;
    }

    public void setDepartmentUserList(List<AutDepartmentUserVO> departmentUserList) {
        this.departmentUserList = departmentUserList;
    }
}
