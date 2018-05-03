package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutPosition;

import java.util.List;

/**
 *
 * 岗位值对象
 *
 * @author：wangj
 *
 * @date：2017年8月17日 下午5:12:27
 */
public class AutPositionVO extends AutPosition {
    private static final long serialVersionUID = -2857518822823012041L;

    private String deptName;

    private List<Long> idList;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
