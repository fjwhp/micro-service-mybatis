package aljoin.file.object;

/**
 * 文件上传参数对象
 * 
 * @author zhongjy
 * @date 2018/04/08
 */
public class UploadResult {

    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 原文件名称
     */
    private String orgnlFileName;
    /**
     * 文件大小KB
     */
    private long fileSize;
    /**
     * 文件类型
     */
    private String fileType;

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

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    @Override
    public String toString() {
        return "UploadResult [groupName=" + groupName + ", fileName=" + fileName + ", orgnlFileName=" + orgnlFileName
            + ", fileSize=" + fileSize + ", fileType=" + fileType + "]";
    }

}
