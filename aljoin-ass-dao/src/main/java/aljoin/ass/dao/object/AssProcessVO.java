package aljoin.ass.dao.object;

import aljoin.ass.dao.entity.AssProcess;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AssProcessVO extends AssProcess {

    private static final long serialVersionUID = 1L;
    private Integer assStatus;

    public Integer getAssStatus() {
        return assStatus;
    }

    public void setAssStatus(Integer assStatus) {
        this.assStatus = assStatus;
    }

}
