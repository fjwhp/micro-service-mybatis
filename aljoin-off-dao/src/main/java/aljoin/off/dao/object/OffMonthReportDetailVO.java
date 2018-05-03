package aljoin.off.dao.object;

import java.util.List;

import aljoin.off.dao.entity.OffMonthReportDetail;
import aljoin.res.dao.entity.ResResource;

/**
 * 
 * 工作月报详情表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-11
 */
public class OffMonthReportDetailVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 日志详情
     */
    private OffMonthReportDetail offMonthReportDetail;

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

    public OffMonthReportDetail getOffMonthReportDetail() {
        return offMonthReportDetail;
    }

    public void setOffMonthReportDetail(OffMonthReportDetail offMonthReportDetail) {
        this.offMonthReportDetail = offMonthReportDetail;
    }

}
