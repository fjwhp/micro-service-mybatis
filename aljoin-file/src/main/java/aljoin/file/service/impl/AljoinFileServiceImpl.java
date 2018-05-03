package aljoin.file.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient;
import org.springframework.stereotype.Service;

import aljoin.file.factory.AljoinFileFactory;
import aljoin.file.object.AljoinFile;
import aljoin.file.object.DownloadResult;
import aljoin.file.object.UploadParam;
import aljoin.file.object.UploadResult;
import aljoin.file.service.AljoinFileService;
import aljoin.object.RetMsg;

/**
 * 文件操作服务实现
 * 
 * @author zhongjy
 * @date 2018/04/03
 */
@Service
public class AljoinFileServiceImpl implements AljoinFileService {

    @Resource
    private AljoinFileFactory aljoinFileFactory;

    /**
     * 参数非空判断
     * 
     * @param uploadParam
     */
    private void checkParamNull(UploadParam uploadParam) throws Exception {

        if (uploadParam.getFileList() == null || uploadParam.getFileList().size() == 0) {
            throw new Exception("文件不能为空");
        } else {
            for (AljoinFile file : uploadParam.getFileList()) {
                if (file == null || file.getFileContent() == null) {
                    throw new Exception("文件不能为空");
                }
            }
        }
        if (uploadParam.getAllowTypeList() == null || uploadParam.getAllowTypeList().size() == 0) {
            throw new Exception("允许上传文件类型不能为空");
        }
    }

    /**
     * 文件类型判断
     * 
     * @param fileObject
     */
    private void checkFileType(UploadParam fileObject) throws Exception {
        List<String> newAllowTypeList = new ArrayList<String>();
        for (String ft : fileObject.getAllowTypeList()) {
            newAllowTypeList.add(ft.toLowerCase());
        }
        List<AljoinFile> fileList = fileObject.getFileList();
        String tempFileName = null;
        String[] tempFileNameArr = null;
        String fileSuffixName = null;
        for (AljoinFile file : fileList) {
            tempFileName = file.getOrgnlFileName();
            tempFileNameArr = tempFileName.split("\\.");
            fileSuffixName = tempFileNameArr[(tempFileNameArr.length - 1)];
            if (!newAllowTypeList.contains(fileSuffixName.toLowerCase())) {
                String allowTypes = String.join(",", newAllowTypeList);
                throw new Exception("文件类型不合法，只能上传[" + allowTypes + "]文件类型");
            }
        }
    }

    /**
     * 文件大小判断
     * 
     * @param fileObject
     */
    private void checkFileSize(UploadParam fileObject) throws Exception {
        for (AljoinFile file : fileObject.getFileList()) {
            if (file.getFileSize() > fileObject.getMaxSize() * 1024) {
                throw new Exception("文件大小不合法，最大只能上传[" + fileObject.getMaxSize() + "KB]");
            }
        }
    }

    @Override
    public RetMsg upload(UploadParam fileObject) throws Exception {
        RetMsg retMsg = new RetMsg();
        /**
         * 非空判断
         */
        checkParamNull(fileObject);
        /**
         * 允许文件类型判断
         */
        checkFileType(fileObject);
        /**
         * 文件大小判断
         */
        checkFileSize(fileObject);
        /**
         * 上传文件
         */
        StorageClient client = aljoinFileFactory.getStorageClient();
        String tempFileName = null;
        String[] tempFileNameArr = null;
        String fileSuffixName = null;
        List<UploadResult> resultList = new ArrayList<UploadResult>();
        for (AljoinFile file : fileObject.getFileList()) {
            NameValuePair[] metaList = new NameValuePair[1];
            metaList[0] = new NameValuePair("orgnlName", file.getOrgnlFileName());
            tempFileName = file.getOrgnlFileName();
            tempFileNameArr = tempFileName.split("\\.");
            fileSuffixName = tempFileNameArr[(tempFileNameArr.length - 1)];
            String[] resultArr = client.upload_file(file.getFileContent(), fileSuffixName, metaList);
            UploadResult result = new UploadResult();
            result.setGroupName(resultArr[0]);
            result.setFileName(resultArr[1]);
            result.setFileSize(file.getFileSize() / 1024);
            result.setOrgnlFileName(tempFileName);
            result.setFileType(fileSuffixName);
            resultList.add(result);
        }
        retMsg.setCode(0);
        retMsg.setMessage("上传成功");
        retMsg.setObject(resultList);
        return retMsg;
    }

    @Override
    public RetMsg download(String groupName, String fileName) throws Exception {
        RetMsg regMsg = new RetMsg();
        DownloadResult result = new DownloadResult();
        StorageClient client = aljoinFileFactory.getStorageClient();
        byte[] bytes = client.download_file(groupName, fileName);
        NameValuePair[] pair = client.get_metadata(groupName, fileName);
        result.setByteFile(bytes);
        result.setOrgnlFileName(pair[0].getValue());
        regMsg.setCode(0);
        regMsg.setMessage("下载成功");
        regMsg.setObject(result);
        return regMsg;
    }

    @Override
    public RetMsg delete(String groupName, String fileName) throws Exception {
        RetMsg regMsg = new RetMsg();
        StorageClient client = aljoinFileFactory.getStorageClient();
        client.delete_file(groupName, fileName);
        regMsg.setCode(0);
        regMsg.setMessage("删除成功");
        return regMsg;
    }

    @Override
    public RetMsg saveOffice(String orgnlName,byte[] fileByte) throws Exception {
        RetMsg regMsg = new RetMsg();
        StorageClient client = aljoinFileFactory.getStorageClient();
        NameValuePair[] metaList = new NameValuePair[1];
        metaList[0] = new NameValuePair("orgnlName", orgnlName);
        String[] fileNameArr = orgnlName.split("\\.");
        String fileSuffixName = fileNameArr[(fileNameArr.length - 1)];
        String[] retArr = client.upload_file(fileByte, fileSuffixName, metaList);
        UploadResult result = new UploadResult();
        result.setGroupName(retArr[0]);
        result.setFileName(retArr[1]);
        result.setOrgnlFileName(orgnlName);
        result.setFileType(fileSuffixName);
        regMsg.setCode(0);
        regMsg.setObject(result);
        regMsg.setMessage("保存文件正文成功");
        return regMsg;
    }

}
