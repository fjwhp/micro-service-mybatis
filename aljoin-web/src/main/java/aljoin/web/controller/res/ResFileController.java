package aljoin.web.controller.res;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import aljoin.res.dao.object.DownloadParam;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 文件系统.
 * 
 * @author：caizx
 * 
 *               @date： 2018-04-19
 */
@Controller
@RequestMapping(value = "/res/file")
public class ResFileController {

    private final static Logger logger = LoggerFactory.getLogger(ResFileController.class);

    @Resource
    private AljoinFileService aljoinFileService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private ResResourceService resResourceService;

    /**
     * 
     * 文件上传(MultipartFile)
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     *               @date： 2018-04-19
     */
    @RequestMapping("/upload")
    @ResponseBody
    public RetMsg upload(MultipartHttpServletRequest request, HttpServletResponse response, String fileModuleName) {
        RetMsg retMsg = new RetMsg();
        try {
            List<MultipartFile> files = request.getFiles("file");
            // 设置文件
            List<AljoinFile> fileList = new ArrayList<AljoinFile>();
            // 构造参数对象
            UploadParam uploadParam = new UploadParam();
            // 调通用接口，获取参数
            Map<String, String> map = sysParameterService.allowFileType();
            // 最大允许上传的文件大小（单位是kb 1024byte=1KB）
            Long limitSize = Long.parseLong(map.get("limitSize"));
            uploadParam.setMaxSize(limitSize * 1024);
            // 允许上传的类型
            String allowType = map.get("allowType");
            String[] typeArr = allowType.split("\\|");
            uploadParam.setAllowTypeList(Arrays.asList(typeArr));
            for (MultipartFile file : files) {
                fileList.add(new AljoinFile(file.getBytes(), file.getOriginalFilename(), file.getSize()));
            }
            uploadParam.setFileList(fileList);
            retMsg = resResourceService.upload(uploadParam, fileModuleName);
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
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request, HttpServletResponse response,
        DownloadParam downloadParam) {
        // 分组名称
        String groupName = downloadParam.getGroupName();
        // 远程文件名称
        String fileName = downloadParam.getFileName();
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
    public RetMsg deleteFile(HttpServletRequest request, HttpServletResponse response, ResResource obj) {
        RetMsg retMsg = new RetMsg();
        try {
            if (null != obj && null != obj.getId()) {
                ResResource original = resResourceService.selectById(obj);
                if (null != original) {
                    // 分组名称
                    String groupName = obj.getGroupName();
                    // 远程文件名称
                    String fileName = obj.getFileName();
                    retMsg = aljoinFileService.delete(groupName, fileName);
                }
            }
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }
    
    /**
     * 文件下载(正文)
     * 
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/downloadOffice")
    public  void downloadOffice(HttpServletRequest request, HttpServletResponse response,
        DownloadParam downloadParam) {
        // 分组名称
        String groupName = downloadParam.getGroupName();
        // 远程文件名称
        String fileName = downloadParam.getFileName();
        try {
            RetMsg retMsg = aljoinFileService.download(groupName, fileName);
            DownloadResult result = (DownloadResult)retMsg.getObject();
            byte[] content = result.getByteFile();
            response.reset();
            response.setContentType("application/msword"); // application/x-excel, application/ms-powerpoint, application/pdf
            response.setHeader("Content-Disposition",
                            "attachment; filename="+new String(result.getOrgnlFileName().getBytes("UTF-8"), "iso-8859-1")); //fileN应该是编码后的(utf-8)
            response.setContentLength(content.length);

            OutputStream outputStream = response.getOutputStream();
            outputStream.write(content);

            outputStream.flush();
            outputStream.close();
            outputStream = null;
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
