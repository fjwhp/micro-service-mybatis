package aljoin.web.ueditor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import aljoin.object.WebConstant;
import aljoin.util.DateUtil;
import aljoin.util.StringUtil;

public class CustomUploader {

    public static final State save(HttpServletRequest request, Map<String, Object> conf) {

        if (!ServletFileUpload.isMultipartContent(request)) {
            return new BaseState(false, 5);
        }

        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
            MultipartFile multipartFile = multipartRequest.getFile(conf.get("fieldName").toString());  
            if(multipartFile==null){  
                return new BaseState(false, 7);  
            }

            String savePath = (String) conf.get("savePath");
            String originFileName = multipartFile.getOriginalFilename();
            Map<String, String> ns = createNames(originFileName, WebConstant.FILE_FOLDER_UEDITOR_PIC);
            
            String suffix = FileType.getSuffixByFilename(originFileName);

            originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
            savePath = savePath + suffix;

            long maxSize = ((Long) conf.get("maxSize")).longValue();

            if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
                return new BaseState(false, 8);
            }

            // 直接去绝对路径
            boolean keepLocalFile = "false".equals(conf.get("keepLocalFile")) ? false : true;
            //InputStream is = fileStream.openStream();
            InputStream is = multipartFile.getInputStream(); 
            // 正常上传路径@ TODO
            String uploadPath = "";
            String remoteDir2 = uploadPath + "/" + WebConstant.FILE_FOLDER_UEDITOR_PIC + "/" + ns.get("ymd") + "/";
            String physicalPath2 = remoteDir2 + ns.get("fileName");
            // 正常上传失败，尝试上传到临时文件夹
            String remoteDir3 = uploadPath + "/" + WebConstant.FILE_FOLDER_UEDITOR_PIC + "/" + ns.get("ymd") + "/";
            String physicalPath3 = remoteDir3 + ns.get("fileName");
            //原来的文件本地文件上传--begin
            //State storageState = StorageManager.saveFtpFileByInputStream(is, remoteDir2, physicalPath2, maxSize, keepLocalFile, remoteDir3, physicalPath3);
            //原来的文件本地文件上传--end
            //http文件上传--begin
            State storageState = StorageManager.saveHttpFileByInputStream(is, remoteDir2, physicalPath2, maxSize, keepLocalFile, remoteDir3, physicalPath3);
            //http文件上传--end
            // JsonObject jo = Util.str2Jsonobject(storageState.toJSONString());
            is.close();

            if (storageState.isSuccess()) {
                /*原来的文件本地文件上传--begin*/
                //  String ftpFullFileName = ns.get("fullName");
                //  storageState.putInfo("url", ftpFullFileName.substring(ftpFullFileName.indexOf(FileConstant.FILE_FOLDER_UEDITOR_PIC)));
                /*原来的文件本地文件上传--end*/
                //http文件上传--begin
                String storageStateJson = storageState.toJSONString();
                JsonParser jpJsonParser = new JsonParser();
                JsonElement je = jpJsonParser.parse(storageStateJson);
                JsonObject jo = je.getAsJsonObject();
                
                String ftpFullFileName = jo.get("httpFullFileName").getAsString();
                storageState.putInfo("url", ftpFullFileName);
                //http文件上传--end
                storageState.putInfo("type", suffix);
                storageState.putInfo("original", originFileName + suffix);
            }

            return storageState;
        }  catch (IOException localIOException) {}
        return new BaseState(false, 4);
    }

    @SuppressWarnings("rawtypes")
    private static boolean validType(String type, String[] allowTypes) {
        List list = Arrays.asList(allowTypes);

        return list.contains(type);
    }
    
    /**
     * 
     * 构造文件的相关属性
     *
     * @return：Map<String,String>
     *
     * @author：zhongjy
     *
     * @date：2017年9月20日 下午1:38:15
     */
    private static Map<String, String> createNames(String fileOrgnlName, String moduleName) {
        Map<String, String> retList = new HashMap<String, String>();
        String fullName = null;
        // 构造文件夹名称(年月日时分秒)
        String ymdsfm = DateUtil.datetime2numstr(null);
        String ymd = ymdsfm.substring(0, 8);
        retList.put("ymd", ymd);
        String sfm = ymdsfm.substring(8, ymdsfm.length());
        String dotName = fileOrgnlName.substring(fileOrgnlName.lastIndexOf("."), fileOrgnlName.length());
        String newFileName = sfm + StringUtil.getUUID() + dotName;
        retList.put("fileName", newFileName);
        //获取文件服务器(http)地址和端口(读取数据库相关配置)@ TODO
        String fileServerAddress = "";
        String fileServerPort = "";
        fullName = "http://" + fileServerAddress + ":" + fileServerPort + "/" + moduleName + "/" + ymd + "/" + newFileName;
        retList.put("fullName", fullName);
        return retList;
    }
}
