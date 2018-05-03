package aljoin.file.service;

import aljoin.file.object.UploadParam;
import aljoin.object.RetMsg;

/**
 * 文件操作服务接口
 * 
 * @author zhongjy
 * @date 2018/04/03
 */
public interface AljoinFileService {

    /**
     * 上传文件
     * 
     * @param uploadParam
     * @return
     * @throws Exception
     */
    public RetMsg upload(UploadParam uploadParam) throws Exception;

    /**
     * 下载文件
     * 
     * @param groupName
     * @param fileName
     * @return
     * @throws Exception
     */
    public RetMsg download(String groupName, String fileName) throws Exception;

    /**
     * 删除文件
     * 
     * @param groupName
     * @param fileName
     * @return
     * @throws Exception
     */
    public RetMsg delete(String groupName, String fileName) throws Exception;
    
    /**
     * 保存文件正文文件
     * 
     * @param groupName
     * @param fileName
     * @return
     * @throws Exception
     */
    public RetMsg saveOffice(String orgnlName, byte[] fileByte) throws Exception;


}
