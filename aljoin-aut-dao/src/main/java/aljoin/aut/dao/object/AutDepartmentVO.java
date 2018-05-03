package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutDepartment;

import java.util.List;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutDepartmentVO extends AutDepartment {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用于存放Autdepartment的List
     */
    private List<Long> idList;

    private List<Long> pIdList;

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public List<Long> getpIdList() {
        return pIdList;
    }

    public void setpIdList(List<Long> pIdList) {
        this.pIdList = pIdList;
    }

}
