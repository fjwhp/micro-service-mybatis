package aljoin.pub.dao.object;

import java.util.List;

import aljoin.pub.dao.entity.PubPublicInfo;
import aljoin.pub.dao.entity.PubPublicInfoRead;
import aljoin.res.dao.entity.ResResource;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class PubPublicInfoVO extends PubPublicInfo {
    /**
     * TODO
     */
    private static final long serialVersionUID = 1763051337290199855L;
    /**
     * 用于批量删除
     */
    @ApiModelProperty(value = "ids", required = false, hidden = true)
    private String ids;
    /**
     * 附件列表
     */
    @ApiModelProperty(value = "附件列表", required = false, hidden = true)
    private List<ResResource> resResourceList;
    @ApiModelProperty(hidden = true)
    private PubPublicInfoRead pubPublicInfoRead;

    @ApiModelProperty(hidden = true)
    private String searchKey;
    @ApiModelProperty(hidden = true)
    private String begDate;
    @ApiModelProperty(hidden = true)
    private String endDate;
    @ApiModelProperty(hidden = true)
    private String categoryName;
    @ApiModelProperty(hidden = true)
    private Integer isRead;

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public PubPublicInfoRead getPubPublicInfoRead() {
        return pubPublicInfoRead;
    }

    public void setPubPublicInfoRead(PubPublicInfoRead pubPublicInfoRead) {
        this.pubPublicInfoRead = pubPublicInfoRead;
    }

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getBegDate() {
        return begDate;
    }

    public void setBegDate(String begDate) {
        this.begDate = begDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
