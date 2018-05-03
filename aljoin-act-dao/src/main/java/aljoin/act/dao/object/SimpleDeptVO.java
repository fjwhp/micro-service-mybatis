package aljoin.act.dao.object;

import java.util.List;

/**
 * 
 * 简单部门对象
 *
 * @author：zhongjy
 * 
 * @date：2017年11月27日 下午9:33:34
 */
public class SimpleDeptVO {

    /**
     * 部门ID
     */
    private String deptId;
    /**
     * 部门对应的用户列表
     */
    private List<SimpleUserVO> userList;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public List<SimpleUserVO> getUserList() {
        return userList;
    }

    public void setUserList(List<SimpleUserVO> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "SimpleDeptVO [deptId=" + deptId + ", userList=" + userList + "]";
    }

}
