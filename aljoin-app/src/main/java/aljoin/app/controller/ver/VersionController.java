package aljoin.app.controller.ver;

import java.io.File;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.app.controller.BaseController;
import aljoin.dao.config.Where;
import aljoin.file.object.DownloadResult;
import aljoin.file.service.AljoinFileService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import io.swagger.annotations.Api;

/**
 * 
 * 更新版本
 *
 * @author：xuc
 * 
 * @date：2018年1月3日 上午10:18:54
 */
@Controller
@RequestMapping(value = "/app/ver/version",method = RequestMethod.POST)
@Api(value = "版本更新Controller",description = "版本更新相关接口")
public class VersionController extends BaseController {

  private final static Logger logger = LoggerFactory.getLogger(VersionController.class);
  @Resource
  private SysParameterService sysParameterService;
  @Resource
  private AljoinFileService aljoinFileService;
  @Resource
  private ResResourceService resResourceService;
  
  /**
   * 
   * @throws Exception 
   * app更新检测
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月3日 上午10:53:02
   */
  @RequestMapping("/update")
  @ResponseBody
  public RetMsg update(HttpServletRequest request, HttpServletResponse response, String versionName) throws Exception {
    RetMsg retMsg = new RetMsg();
    String version = "";
    try {
    String key = "app_version";
    SysParameter sysParameter = sysParameterService.selectBykey(key);
    SysParameter param1 = sysParameterService.selectBykey("ImgServer");
    version = sysParameter.getParamValue();
    String name = param1.getParamValue() + "android" + File.separator + version + ".apk";
    /*  if(param != null) {
        if(Integer.parseInt(param.getParamValue().substring(1)) > Integer.parseInt(versionName.substring(1))) {
          retMsg.setCode(0);
          retMsg.setMessage("检测到新版本,是否更新？");
        }
      }*/
    retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
    retMsg.setObject(name);
    retMsg.setMessage("操作成功");
    } catch (Exception e) {
      logger.error("", e);
      retMsg.setCode(AppConstant.RET_CODE_ERROR);
      retMsg.setMessage("请求异常");
  }
    return retMsg;
 }
  
  /**
   * 
   * app更新
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月3日 上午11:08:27
   */

  @RequestMapping("/doUpdate")
  public ResponseEntity<byte[]> doUpdate(HttpServletRequest request, HttpServletResponse response) {
    ResponseEntity<byte[]> retEntity = null;
    try {
      SysParameter param = sysParameterService.selectBykey("app_version");
      String name = "android" + File.separator + param.getParamValue() + ".apk";
      Where<ResResource> where = new Where<ResResource>();
      where.setSqlSelect("id,group_name,file_name");
      where.eq("orgnl_file_name", name);
      List<ResResource> resources = resResourceService.selectList(where);
      if (null != resources && resources.size() > 0) {
          HttpHeaders headers = new HttpHeaders();
          RetMsg retMsg = aljoinFileService.download(resources.get(0).getGroupName(), resources.get(0).getFileName());
          DownloadResult result = (DownloadResult)retMsg.getObject();
          byte[] content = result.getByteFile();
          headers.setContentDispositionFormData("attachment",
              new String("数字海洋办公系统".getBytes("UTF-8"), "iso-8859-1"));
          headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
          retEntity = new ResponseEntity<byte[]>(content, headers, HttpStatus.CREATED);
      }
    } catch (Exception e) {
      logger.error("", e);
    }
    return retEntity;
  }
}
