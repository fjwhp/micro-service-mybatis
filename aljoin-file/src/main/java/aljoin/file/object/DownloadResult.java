package aljoin.file.object;

/**
 * 文件下载结果对象
 * 
 * @author zhongjy
 * @date 2018/04/08
 */
public class DownloadResult {

    /**
     * 文件名称
     */
    private byte[] byteFile;
    /**
     * 原文件名称
     */
    private String orgnlFileName;

    public byte[] getByteFile() {
        return byteFile;
    }

    public void setByteFile(byte[] byteFile) {
        this.byteFile = byteFile;
    }

    public String getOrgnlFileName() {
        return orgnlFileName;
    }

    public void setOrgnlFileName(String orgnlFileName) {
        this.orgnlFileName = orgnlFileName;
    }

}
