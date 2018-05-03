package aljoin.res.dao.object;

/**
 * 下载文件接收参数对象
 * 
 * @author caizx
 * 
 * @date 2018/04/08
 */
public class DownloadParam {

    /**
     * 分组名称
     */
    private String groupName;
    /**
     * 远程文件名称
     */
    private String fileName;
    
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

}
