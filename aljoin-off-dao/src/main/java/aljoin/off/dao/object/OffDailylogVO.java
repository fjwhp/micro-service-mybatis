package aljoin.off.dao.object;

import aljoin.off.dao.entity.OffDailylog;
import aljoin.res.dao.entity.ResResource;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 * 工作日志(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-14
 */
public class OffDailylogVO extends OffDailylog {

    private static final long serialVersionUID = 1L;

    /**
     * 日志开始日期
     */
    @ApiModelProperty(value = "开始日期", required = false, hidden = true)
    private String workBegDate;

    /**
     * 日志结束日期
     */
    @ApiModelProperty(value = "结束日期", required = false, hidden = true)
    private String workEndDate;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期", required = false, hidden = true)
    private String workDateStr;

    /**
     * 创建日期
     */
    @ApiModelProperty(value = "创建日期", required = false, hidden = true)
    private String createTimeStr;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人", required = false, hidden = true)
    private String createFullName;

    /**
     * 附件列表
     */
    @ApiModelProperty(value = "附件列表", required = false, hidden = true)
    private List<ResResource> resResourceList;

    public String getWorkBegDate() {
        return workBegDate;
    }

    public void setWorkBegDate(String workBegDate) {
        this.workBegDate = workBegDate;
    }

    public String getWorkEndDate() {
        return workEndDate;
    }

    public void setWorkEndDate(String workEndDate) {
        this.workEndDate = workEndDate;
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getCreateFullName() {
        return createFullName;
    }

    public void setCreateFullName(String createFullName) {
        this.createFullName = createFullName;
    }

    public String getWorkDateStr() {
        return workDateStr;
    }

    public void setWorkDateStr(String workDateStr) {
        this.workDateStr = workDateStr;
    }

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

}
