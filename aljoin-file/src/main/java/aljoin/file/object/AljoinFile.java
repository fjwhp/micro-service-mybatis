package aljoin.file.object;

/**
 * 自定义文件类
 * 
 * @author zhongjy
 * @date 2018/04/12
 */
public class AljoinFile {

    /**
     * 文件二进制
     */
    private byte[] fileContent;
    /**
     * 文件名称
     */
    private String orgnlFileName;
    /**
     * 文件长度
     */
    private long fileSize;

    public AljoinFile(byte[] fileContent, String orgnlFileName, long fileSize) {
        this.fileContent = fileContent;
        this.orgnlFileName = orgnlFileName;
        this.fileSize = fileSize;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
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
