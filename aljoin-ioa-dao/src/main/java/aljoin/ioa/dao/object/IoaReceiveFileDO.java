package aljoin.ioa.dao.object;

import aljoin.ioa.dao.entity.IoaReceiveFile;
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
public class IoaReceiveFileDO extends IoaReceiveFile {

    private static final long serialVersionUID = 1L;

    /**
     * 附件列表
     */
    @ApiModelProperty(hidden = true)
    private List<ResResource> resResourceList;;
    /**
     * 填报时间
     */
    @ApiModelProperty(hidden = true)
    private String fillingTime;

    /**
     * 环节名称
     */
    @ApiModelProperty(hidden = true)
    private String linkName;

    /**
     * 传阅对象列表
     */
    @ApiModelProperty(hidden = true)
    private List<IoaReceiveReadObjectDO> objectDOList;

    /**
     * 是否被签收（fals : 否 true : 是）
     */
    @ApiModelProperty(hidden = true)
    private boolean isClaim;

    /**
     * 流程名称
     */
    @ApiModelProperty(hidden = true)
    private String taskName;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public String getFillingTime() {
        return fillingTime;
    }

    public void setFillingTime(String fillingTime) {
        this.fillingTime = fillingTime;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public List<IoaReceiveReadObjectDO> getObjectDOList() {
        return objectDOList;
    }

    public void setObjectDOList(List<IoaReceiveReadObjectDO> objectDOList) {
        this.objectDOList = objectDOList;
    }

    public boolean isClaim() {
        return isClaim;
    }

    public void setClaim(boolean claim) {
        isClaim = claim;
    }
}
