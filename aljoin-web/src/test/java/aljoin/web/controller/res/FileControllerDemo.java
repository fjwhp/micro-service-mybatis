package aljoin.web.controller.res;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import aljoin.file.object.AljoinFile;
import aljoin.file.object.DownloadResult;
import aljoin.file.object.UploadParam;
import aljoin.file.service.AljoinFileService;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.web.controller.BaseController;

/**
 * 文件操作Controller例子
 * 
 * @author zhongjy
 * @date 2018/04/12
 */
@Controller
@RequestMapping(value = "/res/fileSystem")
public class FileControllerDemo extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(FileControllerDemo.class);

    @Resource
    private AljoinFileService aljoinFileService;

    /**
     * 文件上传(MultipartFile)
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/upload")
    @ResponseBody
    public RetMsg upload(MultipartHttpServletRequest request, HttpServletResponse response) {
        RetMsg retMsg = null;
        try {
            List<MultipartFile> files = request.getFiles("file");

            MultipartFile file = files.get(0);
            // 构造参数对象
            UploadParam uploadParam = new UploadParam();
            // 允许上传的类型，注意，这个是后台设定，不是前台参数，是进行后台的类型判断的
            uploadParam.setAllowTypeList(Arrays.asList("txt", "jpg", "png", "rar"));
            // 最大允许上传的文件大小，注意，这个是后台设定，不是前台参数，单位是byte 1024byte=1KB
            uploadParam.setMaxSize(999);
            // 设置文件
            List<AljoinFile> fileList = new ArrayList<AljoinFile>();
            fileList.add(new AljoinFile(file.getBytes(), file.getOriginalFilename(), file.getSize()));
            uploadParam.setFileList(fileList);
            retMsg = aljoinFileService.upload(uploadParam);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }

        return retMsg;
    }

    /**
     * 文件上传(File)
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/upload2")
    @ResponseBody
    public RetMsg upload2(MultipartHttpServletRequest request, HttpServletResponse response) {
        RetMsg retMsg = null;
        try {
            File f1 = new File("C:\\Users\\zhongjy\\Desktop\\表单图片文件.png");
            // 构造参数对象
            UploadParam uploadParam = new UploadParam();
            // 允许上传的类型，注意，这个是后台设定，不是前台参数，是进行后台的类型判断的
            uploadParam.setAllowTypeList(Arrays.asList("txt", "jpg", "png", "rar"));
            // 最大允许上传的文件大小，注意，这个是后台设定，不是前台参数，单位是byte 1024byte=1KB
            uploadParam.setMaxSize(999);
            // 设置文件
            List<AljoinFile> fileList = new ArrayList<AljoinFile>();
            fileList.add(new AljoinFile(Files.readAllBytes(f1.toPath()), f1.getName(), f1.length()));
            uploadParam.setFileList(fileList);
            retMsg = aljoinFileService.upload(uploadParam);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }

        return retMsg;
    }

    /**
     * 文件下载
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/download")
    public ResponseEntity<byte[]> resResourcePage(HttpServletRequest request, HttpServletResponse response) {
        // 分组名称
        String groupName = "";
        // 远程文件名称
        String fileName = "";
        ResponseEntity<byte[]> retEntity = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            RetMsg retMsg = aljoinFileService.download(groupName, fileName);
            DownloadResult result = (DownloadResult)retMsg.getObject();
            byte[] content = result.getByteFile();
            headers.setContentDispositionFormData("attachment",
                new String(result.getOrgnlFileName().getBytes("UTF-8"), "iso-8859-1"));
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            retEntity = new ResponseEntity<byte[]>(content, headers, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("", e);
        }
        return retEntity;
    }

    /**
     * 删除文件
     * 
     * @param request
     * @param response
     * @param obj
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public RetMsg getById(HttpServletRequest request, HttpServletResponse response, ResResource obj) {
        RetMsg retMsg = null;
        // 分组名称
        String groupName = "";
        // 远程文件名称
        String fileName = "";
        try {
            retMsg = aljoinFileService.delete(groupName, fileName);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

}
