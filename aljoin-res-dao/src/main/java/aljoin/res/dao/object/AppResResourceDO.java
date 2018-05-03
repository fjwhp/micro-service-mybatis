package aljoin.res.dao.object;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 附件表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class AppResResourceDO implements Serializable {

    private static final long serialVersionUID = 1L;
    
    /**
     * 附件id
     */
    @ApiModelProperty(value = "附件名称")
    private Long id;
    
    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String groupName;
    /**
     * 附件系统文件名称
     */
    @ApiModelProperty(value = "附件系统文件名称")
    private String fileName;
    /**
     * 原文件名称
     */
    @ApiModelProperty(value = "附件原文件名称")
    private String orgnlFileName;
    /**
     * 文件大小KB
     */
    @ApiModelProperty(value = " 附件大小KB")
    private long fileSize;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getOrgnlFileName() {
        return orgnlFileName;
    }
    public void setOrgnlFileName(String orgnlFileName) {
        this.orgnlFileName = orgnlFileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
}
