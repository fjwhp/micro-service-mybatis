package aljoin.pub.dao.object;

import aljoin.pub.dao.entity.PubPublicInfoDraft;
import aljoin.res.dao.entity.ResResource;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 * 公共信息草稿表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public class PubPublicInfoDraftVO extends PubPublicInfoDraft {

    /**
     * TODO
     */
    private static final long serialVersionUID = -6430207183534089410L;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 用于批量删除 ids
     */
    @ApiModelProperty(value = "ids", required = false, hidden = true)
    private String ids;
    /**
     * 附件列表
     */
    @ApiModelProperty(value = "附件列表", required = false, hidden = true)
    private List<ResResource> resResourceList;

    /**
     * 搜索条件
     */
    @ApiModelProperty(hidden = true)
    private String searchKey;
    @ApiModelProperty(hidden = true)
    private String begDate;
    @ApiModelProperty(hidden = true)
    private String endDate;
    @ApiModelProperty(hidden = true)
    private String categoryName;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
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
}
