package aljoin.ioa.dao.object;

import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import aljoin.res.dao.entity.ResResource;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 * 收文阅件表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaReceiveFileVO extends IoaReceiveFile {

    private static final long serialVersionUID = 1L;

    /**
     * 附件列表
     */
    @ApiModelProperty(hidden = true)
    private List<ResResource> resResourceList;

    /**
     * 填报开始时间
     */
    @ApiModelProperty(hidden = true)
    private String begDate;

    /**
     * 填报结束时间
     */
    @ApiModelProperty(hidden = true)
    private String endDate;

    /**
     * 传阅对象
     */
    @ApiModelProperty(hidden = true)
    private List<IoaReceiveReadObject> readObjectList;

    /**
     * 搜索关键字
     */
    @ApiModelProperty(hidden = true)
    private String searchKey;

    /**
     * 是否选择全局人员
     */
    @ApiModelProperty(hidden = true)
    private String isCheckAllUser;

    /**
     * 填报时间排序（0：降序 1：升序）
     */
    @ApiModelProperty(hidden = true)
    private String fillingTimeIsAsc;

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
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

    public List<IoaReceiveReadObject> getReadObjectList() {
        return readObjectList;
    }

    public void setReadObjectList(List<IoaReceiveReadObject> readObjectList) {
        this.readObjectList = readObjectList;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getIsCheckAllUser() {
        return isCheckAllUser;
    }

    public void setIsCheckAllUser(String isCheckAllUser) {
        this.isCheckAllUser = isCheckAllUser;
    }

    public String getFillingTimeIsAsc() {
        return fillingTimeIsAsc;
    }

    public void setFillingTimeIsAsc(String fillingTimeIsAsc) {
        this.fillingTimeIsAsc = fillingTimeIsAsc;
    }
}
