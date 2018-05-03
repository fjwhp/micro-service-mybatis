package aljoin.off.dao.entity;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 
 * 工作日志(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-14
 */
public class OffDailylog extends Entity<OffDailylog> {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题", required = false, hidden = true)
    private String title;

    /**
     * 日志日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "日志日期", required = false, hidden = true)
    private Date workDate;
    /**
     * 工作日志内容
     */
    @ApiModelProperty(value = "日志内容", required = false, hidden = true)
    private String content;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", required = false, hidden = true)
    private String remark;

    public Date getWorkDate() {
        return workDate;
    }

    public OffDailylog setWorkDate(Date workDate) {
        this.workDate = workDate;
        return this;
    }

    public String getContent() {
        return content;
    }

    public OffDailylog setContent(String content) {
        this.content = content;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public OffDailylog setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
