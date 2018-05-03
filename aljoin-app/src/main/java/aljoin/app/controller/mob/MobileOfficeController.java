package aljoin.app.controller.mob;

import java.io.File;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.zhuozhengsoft.moboffice.FileSaver;
import com.zhuozhengsoft.moboffice.MobOfficeCtrl;
import com.zhuozhengsoft.moboffice.OpenModeType;

import aljoin.app.controller.BaseController;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;

/**
 * 
 * 查看和编辑文档
 *
 * @author：wuhp
 * 
 * @date：2017年11月4日 上午10:13:17
 */
@Controller
@RequestMapping("/app/mob/mobileOffice")
public class MobileOfficeController extends BaseController {

  @Resource
  private AutUserService autUserService;

  /**
   * 
   * 操作文档的方法----查看
   *
   * @return：void
   *
   * @author：wuhp
   *
   * @date：2017年11月4日 上午10:17:20
   */
  @RequestMapping("viewOffice")
  public RetMsg viewOffice(HttpServletRequest request, HttpServletResponse response,
      String fullPath) {

    if (StringUtils.isEmpty(fullPath)) {
      return RetMsg.getFailRetMsg("fullPath参数不能为空！");
    } else if (!new File(fullPath).exists()) {
      return RetMsg.getFailRetMsg("fullPath参数,文件不存在");
    }
    try {
      String fileName = new File(fullPath).getName();
      RetMsg validateMsg = validateSupportType(fileName);
      if (validateMsg.getCode().equals(WebConstant.RETMSG_FAIL_CODE)) {
        return validateMsg;
      } // 校验完毕、
      OpenModeType openModeType = validateFile(fileName, "true");

      MobOfficeCtrl mobCtrl = new MobOfficeCtrl(request, response);
      mobCtrl.setServerPage("mobserver.zz");

      // mobCtrl.setSaveFilePage("savedoc.jsp");
      mobCtrl.webOpen(fullPath, openModeType, getFullName(request)); // 根据登陆人 的姓名
      return RetMsg.getSuccessRetMsg();
    } catch (Exception e) {
      log.error("", e);
      return RetMsg.getFailRetMsg(e.getMessage());
    }
  }

  /**
   * 
   * 编辑
   *
   * @return：RetMsg
   *
   * @author：wuhp
   *
   * @date：2017年11月4日 上午11:22:51
   */
  @RequestMapping("editOffice")
  public RetMsg editOffice(HttpServletRequest request, HttpServletResponse response,
      String fullPath) {

    try {
      String fileName = new File(fullPath).getName();
      RetMsg validateMsg = validateSupportType(fileName);
      if (validateMsg.getCode().equals(WebConstant.RETMSG_FAIL_CODE)) {
        return validateMsg;
      } // 校验完毕、
      OpenModeType openModeType = validateFile(fileName, "false");

      MobOfficeCtrl mobCtrl = new MobOfficeCtrl(request, response);
      mobCtrl.setServerPage("mobserver.zz");
    //  mobCtrl.setSaveFilePage("savedoc.jsp");

      mobCtrl.webOpen(fullPath, openModeType, getFullName(request)); // 根据登陆人 的姓名
      return RetMsg.getSuccessRetMsg();
    } catch (Exception e) {
      log.error("", e);
      return RetMsg.getFailRetMsg(e.getMessage());
    }
  }

  /**
   * 
   * @throws Exception
   * @描述:获取登陆人姓名
   *
   * @return：String
   *
   * @author：wuhp
   *
   * @date：2017年11月22日 下午7:47:23
   */
  public String getFullName(HttpServletRequest request) throws Exception {
    AutAppUserLogin user = getAppUserLogin(request);
    AutUser autUser = autUserService.selectById(user.getUserId());
    return autUser.getFullName();
  }

  /**
   * 
   * 保存
   *
   * @return：RetMsg
   *
   * @author：wuhp
   *
   * @date：2017年11月4日 上午11:24:55
   */
  @RequestMapping("saveOffice")
  public RetMsg saveOffice(HttpServletRequest request, HttpServletResponse response,
      String fullPath) {
    try {
      FileSaver fs = new FileSaver(request, response);
      // fs.saveToFile(request.getSession().getServletContext().getRealPath("doc/")+"/"+
      // fs.getFileName());
      fs.saveToFile(fs.getFileName());
      fs.close();
      return RetMsg.getSuccessRetMsg();
    } catch (Exception e) {
      log.error("", e);
      return RetMsg.getFailRetMsg(e.getMessage());
    }

  }

  public RetMsg validateSupportType(String fileName) {
    if (!(fileName.endsWith(".doc") || fileName.endsWith(".xls") || fileName.endsWith(".ppt")
        || fileName.endsWith(".pdf") || fileName.endsWith(".docx") || fileName.endsWith(".xlsx")
        || fileName.endsWith(".pptx"))) {
      return RetMsg.getFailRetMsg("文件格式不支持，仅支持doc,docx,xls,xlsx,ppt,pptx,pdf");
    }
    return RetMsg.getSuccessRetMsg();
  }

  public OpenModeType validateFile(String fileName, String readOnly) {
    OpenModeType openModeType = OpenModeType.docNormalEdit;
    if (fileName.endsWith(".doc")) {
      if (readOnly.equals("true")) {
        openModeType = OpenModeType.docReadOnly;
      } else {
        openModeType = OpenModeType.docNormalEdit;
      }
    } else if (fileName.endsWith(".xls")) {
      if (readOnly.equals("true")){
    	  openModeType = OpenModeType.xlsReadOnly;  
      }else{
    	  openModeType = OpenModeType.xlsNormalEdit;
      }
    } else if (fileName.endsWith(".ppt")) {
      if (readOnly.equals("true")) {
        openModeType = OpenModeType.pptReadOnly;
      } else {
        openModeType = OpenModeType.pptNormalEdit;
      }
    } else if (fileName.endsWith(".pdf")) {
      // pdf 仅仅支持只读
      openModeType = OpenModeType.pdfReadOnly;
    }
    return openModeType;
  }

}
