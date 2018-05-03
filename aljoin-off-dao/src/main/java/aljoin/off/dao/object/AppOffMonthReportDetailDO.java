package aljoin.off.dao.object;

import aljoin.res.dao.entity.ResResource;

import java.io.Serializable;
import java.util.List;

/**
 * 工作月报详情表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class AppOffMonthReportDetailDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志详情
     */
    private AppOffMonthReportDetail offMonthReportDetail;

    /**
     * 附件列表
     */
    private List<ResResource> resResourceList;

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public AppOffMonthReportDetail getOffMonthReportDetail() {
        return offMonthReportDetail;
    }

    public void setOffMonthReportDetail(AppOffMonthReportDetail offMonthReportDetail) {
        this.offMonthReportDetail = offMonthReportDetail;
    }
}
