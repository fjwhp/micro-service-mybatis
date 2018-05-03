package aljoin.file.object;

import java.util.List;

/**
 * 文件上传参数对象
 * 
 * @author zhongjy
 * @date 2018/04/08
 */
public class UploadParam {

    /**
     * 要上传的文件
     */
    private List<AljoinFile> fileList;
    /**
     * 允许上传的文件类型
     */
    private List<String> allowTypeList;
    /**
     * 单个文件最大限制(KB)
     */
    private long maxSize;

    public List<AljoinFile> getFileList() {
        return fileList;
    }

    public void setFileList(List<AljoinFile> fileList) {
        this.fileList = fileList;
    }

    public List<String> getAllowTypeList() {
        return allowTypeList;
    }

    public void setAllowTypeList(List<String> allowTypeList) {
        this.allowTypeList = allowTypeList;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

}
