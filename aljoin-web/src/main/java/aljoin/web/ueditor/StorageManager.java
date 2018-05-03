package aljoin.web.ueditor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;

import aljoin.file.object.AljoinFile;
import aljoin.file.object.UploadParam;
import aljoin.file.object.UploadResult;
import aljoin.file.service.AljoinFileService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;
import aljoin.util.SpringContextUtil;
import aljoin.util.StringUtil;
import lombok.Synchronized;

public class StorageManager {
  
  private final static Logger logger = LoggerFactory.getLogger(StorageManager.class);
  
  
  public static final int BUFFER_SIZE = 8192;

  public static State saveBinaryFile(byte[] data, String path) {
    File file = new File(path);

    State state = valid(file);
    if (!state.isSuccess()) {
      return state;
    }
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
      bos.write(data);
      bos.flush();
      bos.close();
    } catch (IOException ioe) {
      return new BaseState(false, 4);
    }
    state = new BaseState(true, file.getAbsolutePath());
    state.putInfo("size", data.length);
    state.putInfo("title", file.getName());
    return state;
  }

  public static State saveFileByInputStream(InputStream is, String path, long maxSize) {
    State state = null;

    File tmpFile = getTmpFile();

    byte[] dataBuf = new byte['?'];
    BufferedInputStream bis = new BufferedInputStream(is, 8192);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);

      int count = 0;
      while ((count = bis.read(dataBuf)) != -1) {
        bos.write(dataBuf, 0, count);
      }
      bos.flush();
      bos.close();
      if (tmpFile.length() > maxSize) {
        tmpFile.delete();
        return new BaseState(false, 1);
      }
      state = saveTmpFile(tmpFile, path);
      if (!state.isSuccess()) {
        tmpFile.delete();
      }
      return state;
    } catch (IOException localIOException) {
    }
    return new BaseState(false, 4);
  }

  public static State saveFileByInputStream(InputStream is, String path) {
    State state = null;

    File tmpFile = getTmpFile();

    byte[] dataBuf = new byte['?'];
    BufferedInputStream bis = new BufferedInputStream(is, 8192);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);

      int count = 0;
      while ((count = bis.read(dataBuf)) != -1) {
        bos.write(dataBuf, 0, count);
      }
      bos.flush();
      bos.close();

      state = saveTmpFile(tmpFile, path);
      if (!state.isSuccess()) {
        tmpFile.delete();
      }
      return state;
    } catch (IOException localIOException) {
    }
    return new BaseState(false, 4);
  }

  private static File getTmpFile() {
    File tmpDir = FileUtils.getTempDirectory();
    double d = Math.random() * 10000.0D;
    String tmpFileName = String.valueOf(d).replace(".", "");
    return new File(tmpDir, tmpFileName);
  }

  private static State saveTmpFile(File tmpFile, String path) {
    State state = null;
    File targetFile = new File(path);
    if (targetFile.canWrite()) {
      return new BaseState(false, 2);
    }
    try {
      FileUtils.moveFile(tmpFile, targetFile);
    } catch (IOException e) {
      return new BaseState(false, 4);
    }
    state = new BaseState(true);
    state.putInfo("size", targetFile.length());
    state.putInfo("title", targetFile.getName());

    return state;
  }

  private static State valid(File file) {
    File parentPath = file.getParentFile();
    if ((!parentPath.exists()) && (!parentPath.mkdirs())) {
      return new BaseState(false, 3);
    }
    if (!parentPath.canWrite()) {
      return new BaseState(false, 2);
    }
    return new BaseState(true);
  }

  /**
   * 上传FTP文件
   * 
   * @param is
   * @param path
   * @param maxSize
   * @return
   */
  public static State saveFtpFileByInputStream(InputStream is, String remoteDir, String path,
      long maxSize, boolean keepLocalFile, String remoteDir3, String path3) {
    State state = null;

    File tmpFile = getTmpFile();

    byte[] dataBuf = new byte[2048];
    BufferedInputStream bis = new BufferedInputStream(is, 8192);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);

      int count = 0;
      while ((count = bis.read(dataBuf)) != -1) {
        bos.write(dataBuf, 0, count);
      }
      bos.flush();
      bos.close();

      if (tmpFile.length() > maxSize) {
        tmpFile.delete();
        return new BaseState(false, 1);
      }

      state = saveFtpTmpFile(tmpFile, remoteDir, path, keepLocalFile, remoteDir3, path3);

      if (!state.isSuccess()) {
        tmpFile.delete();
      }

      return state;
    } catch (IOException localIOException) {
    }
    return new BaseState(false, 4);
  }

  private static State saveFtpTmpFile(File tmpFile, String remoteDir, String path,
      boolean keepLocalFile, String remoteDir3, String path3) {
    State state = null;
    File targetFile = new File(path);
    String fn = null;
    if (targetFile.canWrite()){
    	return new BaseState(false, 2);
    }
    try {
      
      tmpFile.delete();
    } catch (Exception e) {
      File targetFile3 = new File(path3);
      if (targetFile3.canWrite()){
    	  return new BaseState(false, 2);
      }
      try {
        FileUtils.moveFile(tmpFile, targetFile3);
      } catch (IOException e1) {
        return new BaseState(false, 4);
      }
    }

    try {
      fn = createFullName(targetFile, WebConstant.FILE_FOLDER_UEDITOR_PIC);
      if (fn == null) {
        return new BaseState(false, 4);
      }
    } catch (Exception e) {
      return new BaseState(false, 4);
    }

    try {
      if (!keepLocalFile) {
        targetFile.delete();
      }
    } catch (Exception e) {

    }

    state = new BaseState(true);
    state.putInfo("size", targetFile.length());
    state.putInfo("title", path.substring(path.lastIndexOf("/") + 1));
    return state;
  }

  private static String createFullName(File file, String moduleName) {
    String fullName = null;
    // 构造文件夹名称(年月日时分秒)
    String ymdsfm = DateUtil.datetime2numstr(null);
    String ymd = ymdsfm.substring(0, 8);
    String sfm = ymdsfm.substring(8, ymdsfm.length());
    String fileOrgnlName = file.getName();
    String dotName =
        fileOrgnlName.substring(fileOrgnlName.lastIndexOf("."), fileOrgnlName.length());
    String newFileName = sfm + StringUtil.getUUID() + dotName;
    // 获取文件服务器(http)地址和端口(读取数据库相关配置)@ TODO
    String fileServerAddress = "";
    String fileServerPort = "";
    fullName = "http://" + fileServerAddress + ":" + fileServerPort + "/" + moduleName + "/" + ymd
        + "/" + newFileName;
    return fullName;
  }

  @SuppressWarnings("unused")
  private static String createFullName2(File file, RetMsg resp) {
    String fileFastDfsIpPortRead = "";
    String fullName = fileFastDfsIpPortRead + "/" + resp.getObject();
    return fullName;
  }


  /**
   * fastdfs上传文件
   * 
   * @param is
   * @param path
   * @param maxSize
   * @return
   */
  public static State saveHttpFileByInputStream(InputStream is, String remoteDir, String path,
    long maxSize, boolean keepLocalFile, String remoteDir3, String path3) {
    State state = null;

    String dotName = path.substring(path.lastIndexOf(".") + 1);
    File tmpFile = getTmpFile2(dotName);

    byte[] dataBuf = new byte[2048];
    BufferedInputStream bis = new BufferedInputStream(is, 8192);
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), 8192);

      int count = 0;
      while ((count = bis.read(dataBuf)) != -1) {
        bos.write(dataBuf, 0, count);
      }
      bos.flush();
      bos.close();

      if (tmpFile.length() > maxSize) {
        tmpFile.delete();
        return new BaseState(false, 1);
      }

      state = saveHttpTmpFile(tmpFile, remoteDir, path, keepLocalFile, remoteDir3, path3);

      if (!state.isSuccess()) {
        tmpFile.delete();
      }

      return state;
    } catch (IOException localIOException) {
    }
    return new BaseState(false, 4);
  }

@SuppressWarnings({"unused", "unchecked"})
private static State saveHttpTmpFile(File tmpFile, String remoteDir, String path,
      boolean keepLocalFile, String remoteDir3, String path3) {
    State state = null;
    File targetFile = new File(path);
    List<ResResource> resultList = null;
    SysParameter sysParameter = null;
    if (targetFile.canWrite()){
    	return new BaseState(false, 2);
    }
    try {
      String fileModuleName = "UEDITOR_IMAGE_ADD";
      SysParameterService sysParameterService = (SysParameterService) SpringContextUtil.getBean("sysParameterServiceImpl");
      AljoinFileService aljoinFileService = (AljoinFileService)SpringContextUtil.getBean("aljoinFileServiceImpl");
      ResResourceService resResourceService = (ResResourceService)SpringContextUtil.getBean("resResourceServiceImpl");
      // 构造参数对象
      UploadParam uploadParam = new UploadParam();
      // 调通用接口，获取参数
      Map<String, String> map = sysParameterService.allowFileType();
      // 最大允许上传的文件大小（单位是kb 1024byte=1KB）
      Long limitSize = Long.parseLong(map.get("limitSize"));
      uploadParam.setMaxSize(limitSize);
      //允许上传的类型
      String allowType = map.get("allowType");
      String[] typeArr = allowType.split("\\|");
      uploadParam.setAllowTypeList(Arrays.asList(typeArr));
      // 设置文件
      List<AljoinFile> fileList = new ArrayList<AljoinFile>();
      fileList.add(new AljoinFile(Files.readAllBytes(tmpFile.toPath()), tmpFile.getName(), tmpFile.length()));
      uploadParam.setFileList(fileList);
      RetMsg retMsg = resResourceService.upload(uploadParam,fileModuleName);
      resultList = (ArrayList<ResResource>)retMsg.getObject();
      if (resultList == null) {
        return new BaseState(false, 4);
      }
    } catch (Exception e) {
      logger.error("saveHttpTmpFile文件上传失败",e);
      return new BaseState(false, 4);
    }
    try {
      if (!keepLocalFile) {
        targetFile.delete();
      }
    } catch (Exception e) {

    }
    state = new BaseState(true);
    state.putInfo("size", targetFile.length());
    state.putInfo("title", resultList.get(0).getOrgnlFileName());
    state.putInfo("httpFullFileName", "/res/file/download?groupName="+resultList.get(0).getGroupName()+"&fileName="+resultList.get(0).getFileName());
    return state;
  }

  private static File getTmpFile2(String fileExentdName) {
    File tmpDir = FileUtils.getTempDirectory();
    double d = Math.random() * 10000.0D;
    String tmpFileName = String.valueOf(d).replace(".", "") + "." + fileExentdName;
    return new File(tmpDir, tmpFileName);
  }

 
}
