package aljoin.web.controller.pag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.druid.util.StringUtils;
import com.zhuozhengsoft.pageoffice.DocumentVersion;
import com.zhuozhengsoft.pageoffice.FileSaver;
import com.zhuozhengsoft.pageoffice.OfficeVendorType;
import com.zhuozhengsoft.pageoffice.OpenModeType;
import com.zhuozhengsoft.pageoffice.PageOfficeCtrl;
import com.zhuozhengsoft.pageoffice.wordwriter.DataRegion;
import com.zhuozhengsoft.pageoffice.wordwriter.WordDocument;

import aljoin.aut.security.CustomUser;
import aljoin.file.service.AljoinFileService;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.StringUtil;
import aljoin.web.controller.BaseController;
import aljoin.web.javaBean.PageOfficePermissionBean;
import aljoin.web.javaBean.PageOfficeRedTitle;

/**
 * 
 * 卓正pageoffice封装调用
 *
 * @author：wuhp
 * 
 * @date：2017年11月3日 下午3:21:31
 */
@Controller
@RequestMapping("/pag/pageOffice")
public class PageOfficeController extends BaseController {
	//private static final RetMsg UploadResult = null;
    private static Logger log = LoggerFactory.getLogger(PageOfficeController.class);
	@Resource
	private SysParameterService sysParameterService;
	@Resource
	private AljoinFileService aljoinFileService;
	@Resource
	private ResResourceService resResourceService;

	/**
	 * 打开资源文件
	 * 
	 * @return
	 * 
	 *
	 * @return：void
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月3日 下午3:21:58
	 */
	@RequestMapping("/openWord")
	public void openWord(PageOfficePermissionBean permissionBean, HttpServletRequest request,
			HttpServletResponse response) {
		try {

			PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
			// 设置服务器页面
			poCtrl.setServerPage(request.getContextPath() + "/poserver.zz");
			if (permissionBean == null || permissionBean.getOpenModeType() == null
					|| permissionBean.getOpenModeType().isEmpty()) {
				throw new Exception("必要参数RelativePath，OpenModeType不能为空");
			}
			String attachId = permissionBean.getAttachId(); // 附件相对地址

			// 打开模式 1 只读模式 docReadOnly 2 word编辑模式
			// docNormalEdit 3 痕迹保留模式 docRevisionOnly
			OpenModeType omt = null;
			// 经过权限设置处理，来控制返回给前端需要显示什么控件
			poCtrl = ctlButton(poCtrl, omt, permissionBean);
			CustomUser user = getCustomDetail();
			if (!StringUtils.isEmpty(permissionBean.getFileNameDesc()) && permissionBean.getIsNewWord().equals("2")) {
				// 家里空白页面，需要传入文件名
				poCtrl.setServerPage(request.getContextPath() + "/poserver.zz"); // 此行必须
				// 隐藏菜单栏
				poCtrl.setMenubar(false);
				// 隐藏工具栏
				// 设置保存页面
				// 新建Word文件，webCreateNew方法中的两个参数分别指代“操作人”和“新建Word文档的版本号”
				poCtrl.webCreateNew(user.getNickName(), DocumentVersion.Word2003);
				poCtrl.setTagId("PageOfficeCtrl1"); // 此行必须
			} else {
				// poCtrl.setSaveFilePage("/pag/pageOffice/saveFile?relativePath="
				// +
				// relativePath); // 保存指定
				poCtrl.addCustomToolButton("全屏", "SetFullScreen()", 4);
				poCtrl.addCustomToolButton("关闭", "Close()", 21); // controller

				String fileOpenPath = packagePushFullPathById(attachId);
				//String fileOpenPath = "http://localhost:8081/res/file/downloadOffice?groupName=group1&fileName=M00/00/03/wKgA31ri4OeAeDbDAABBMa59ZjE88.docx";
				poCtrl.setTitlebar(false);
				poCtrl.setTimeSlice(20);
				poCtrl.setOfficeVendor(OfficeVendorType.WPSOffice); // 客户端启动wps打开文件
				poCtrl.webOpen(fileOpenPath, setModeType(permissionBean.getOpenModeType()), user.getNickName());
				// 设置response以什么码表向浏览器写
			}

			response.setCharacterEncoding("utf-8");
			// 指定浏览器以什么码表打开
			response.setHeader("content-type", "text/html;charset=utf-8");
			PrintWriter out;
			out = response.getWriter();
			out.write(poCtrl.getHtmlCode("PageOfficeCtrl1"));
		} catch (Exception e) {
			log.error("", e);
		}

	}

	@RequestMapping("/openBlankWord")
	public void openBlankWord(PageOfficePermissionBean permissionBean, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			PageOfficeCtrl poCtrl1 = new PageOfficeCtrl(request);
			OpenModeType omt = null;
			// 经过权限设置处理，来控制返回给前端需要显示什么控件
			poCtrl1 = ctlButton(poCtrl1, omt, permissionBean);
			CustomUser user = getCustomDetail();
			poCtrl1.setServerPage(request.getContextPath() + "/poserver.zz"); // 此行必须

			if (permissionBean.getAttachId().isEmpty()) {
				// 新建Word文件，webCreateNew方法中的两个参数分别指代“操作人”和“新建Word文档的版本号”
				poCtrl1.webCreateNew(user.getNickName(), DocumentVersion.Word2003);
				poCtrl1.setTagId("PageOfficeCtrl1"); // 此行必须
				poCtrl1.addCustomToolButton("全屏", "SetFullScreen()", 4);
				poCtrl1.addCustomToolButton("关闭", "Close()", 21); // controller
			} else {
				// 打开就文件编辑
				String attachId = permissionBean.getAttachId(); // 附件相对地址
				poCtrl1.addCustomToolButton("全屏", "SetFullScreen()", 4);
				poCtrl1.addCustomToolButton("关闭", "Close()", 21); // controller
				// poCtrl1.setJsFunction_BeforeDocumentClosed("BeforeDocumentClosed");
				String fileOpenPath = packagePushFullPathById(attachId);
				poCtrl1.setTitlebar(false);
				poCtrl1.setTimeSlice(20);
				poCtrl1.setOfficeVendor(OfficeVendorType.WPSOffice); // 客户端启动wps打开文件
				poCtrl1.webOpen(fileOpenPath, setModeType(permissionBean.getOpenModeType()), user.getNickName());
			}

			response.setCharacterEncoding("utf-8");
			// 指定浏览器以什么码表打开
			response.setHeader("content-type", "text/html;charset=utf-8");
			PrintWriter out;
			out = response.getWriter();
			out.write(poCtrl1.getHtmlCode("PageOfficeCtrl1"));
		} catch (IOException e) {
			log.error("", e);
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * 
	 * pageOffice 按钮是否显示控制器
	 *
	 * @return：PageOfficeCtrl
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月16日 上午11:06:21
	 */
	public PageOfficeCtrl ctlButton(PageOfficeCtrl poCtrl, OpenModeType omt, PageOfficePermissionBean permissionBean) {

		if ("1".equals(permissionBean.getOpenModeType())) {
			// 隐藏Office工具条
			poCtrl.setOfficeToolbars(false);
			// 隐藏自定义工具栏
			poCtrl.setCustomToolbar(false);
			omt = OpenModeType.docReadOnly;
			poCtrl.setCaption("当前文档只读模式打开");

		} else if ("2".equals(permissionBean.getOpenModeType())) {
			poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
			poCtrl.addCustomToolButton("保存", "Save", 1);
			poCtrl.addCustomToolButton("印章签字", "InsertSeal()", 2);
			poCtrl.setOfficeToolbars(true);
			poCtrl.setCustomToolbar(true);
			omt = OpenModeType.docNormalEdit;
		} else if ("3".equals(permissionBean.getOpenModeType())) {
			poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
			poCtrl.addCustomToolButton("保存", "Save", 1);
			// poCtrl.addCustomToolButton("隐藏痕迹", "showRevisions()", 5);
			poCtrl.setOfficeToolbars(true);
			poCtrl.setCustomToolbar(true);
			omt = OpenModeType.docRevisionOnly;
		}
		if (permissionBean.getIsImportButton() != null && permissionBean.getIsImportButton().equals("1")) {
			poCtrl.addCustomToolButton("导入文件", "importData()", 15);
		}
		if (permissionBean.getIsSubmitButton() != null && permissionBean.getIsSubmitButton().equals("1")) {
			poCtrl.addCustomToolButton("提交", "submitData()", 1);
		}
		if ("1".equals(permissionBean.getRedTitle())) {
			poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
			poCtrl.addCustomToolButton("套红", "redTitleSetting()", 10);
		}
		if (permissionBean.getIsNewWord() != null && permissionBean.getIsNewWord().equals("2")) {
		}
		if (permissionBean.getPagePrint() != null && permissionBean.getPagePrint().equals("1")) {
			poCtrl.addCustomToolButton("套打", "pagePrint()", 1);
		}

		return poCtrl;
	}

	@RequestMapping("/saveFile")
	public void saveFile(HttpServletRequest request, HttpServletResponse response) {
		FileSaver fs = new FileSaver(request, response);
		String attachId = request.getParameter("attachId"); // 相对路径
		//String fileSavePath;
		try {
		    //aljoinFileService.update("group1", "M00/00/03/wKgA31ri4OeAeDbDAABBMa59ZjE88.docx", fs.getFileBytes());
		    resResourceService.saveFile(attachId, fs.getFileBytes());
			//fileSavePath = packageFullPathById(attachId);
			//log.debug("文件保存目标路径是===" + fileSavePath);
			//fs.saveToFile(fileSavePath);
			fs.close();
		} catch (Exception e) {
			log.error("", e);
		}

	}

	/**
	 * 新建文件保存
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("/saveBlankFile")
	public void saveBlankFile(HttpServletRequest request, HttpServletResponse response) {
		try {
			// 定义保存对象
			FileSaver fs = new FileSaver(request, response);
			String type = request.getParameter("pageType");
			String formId = request.getParameter("formId");
			if (type.isEmpty()) {
				throw new Exception("page_type模板参数类型不能为空");
			}
			
			String fileNameDesc = (String) request.getParameter("fileNameDesc");
			if (fileNameDesc != null && fileNameDesc.length() > 0) {
				String FileSubject = new String(fileNameDesc.getBytes("utf-8"));
				// 模块名称+日期
				//String ymdsfm = new DateTime().toString("yyyyMMddHHmmss");
				//String ymd = ymdsfm.substring(0, 8);
				// 创建文件夹
				//String relativePath = WebConstant.MODULE_PAGE_OFFICE + File.separator + ymd;
				//File f;
				//f = new File(getUploadPath() + File.separator + relativePath);

				/*if (!f.exists()) {
					f.mkdirs();
				}*/
				//String fileName = StringUtil.getUUID() + ".doc";
				RetMsg result = resResourceService.saveBlankFile(fileNameDesc,fs.getFileBytes(),fs.getFileSize());
				ResResource resource = (ResResource)result.getObject();
				// 保存文件
				//fs.saveToFile(getUploadPath() + File.separator + relativePath + File.separator + fileName);
				// 设置保存结果
				// fs.showPage(300,300);

				//String filePath = relativePath + File.separator + fileName;
				//resAttachment.setAttachName(fileNameDesc);
				//resAttachment.setAttachPath(filePath);
				//resAttachment.setDescription(fileNameDesc);
				//resAttachment.setAttachSize(fs.getFileSize());
				//resAttachment.setResourceId(Long.valueOf(formId));
				//resAttachment.setType(type);
				//resAttachmentService.insert(resAttachment);
				resource.setBizId(Long.valueOf(formId));
                resource.setType(type);
                resResourceService.updateById(resource);
				fs.setCustomSaveResult("ok|" + resource.getId());
				fs.close();
				PrintWriter out = response.getWriter();
				out.print(FileSubject);
			}

		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * 
	 * 相对路径，转换为磁盘全路径
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月14日 下午4:44:48
	 */
	/*public String getFullFilePath(String relativePath) throws Exception {

		String fileSavePath = getUploadPath() + File.separator + relativePath; // 构造保存的，磁盘全路径
		return fileSavePath;
	}*/

	/*public String getUploadPath() throws Exception {
		SysParameter param = sysParameterService.selectBykey("pageoffice_upload_path"); // 切记
																						// 是磁盘路径，不是http路径
		if (param == null) {
			throw new Exception("syss_parameter系统参数upload_path不能为空,请联系管理员");
		}
		return param.getParamValue();
	}*/

	public String getImgServerPath() throws Exception {
		SysParameter param = sysParameterService.selectBykey("pageoffice_open_path");
		if (param == null) {
			throw new Exception("syss_parameter系统参数ImgServer不能为空,请联系管理员");
		}
		return param.getParamValue();
	}

	/**
	 * 
	 * @throws Exception
	 *             通过附件id attachId获取全路径
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月18日 下午3:46:48
	 */
	/*public String packageFullPathById(String attachId) throws Exception {
		ResResource resResource = resResourceService.selectById(attachId);
		if (resResource == null) {
			throw new Exception(attachId + "附件不存在");
		}
		log.debug("物理地址路径===" + getUploadPath() + File.separator + resResource.getFileName());
		return getUploadPath() + File.separator + resResource.getFileName();
	}*/

	/**
	 * 
	 * 打开附件路径封装
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月20日 下午4:54:57
	 */
	public String packagePushFullPathById(String attachId) throws Exception {
	    ResResource resResource = resResourceService.selectById(attachId);
		if (resResource == null) {
			throw new Exception(attachId + "附件不存在");
		}
		//log.debug("http参数路径==" + getImgServerPath() + File.separator + resResource.getFileName());
        //String fileOpenPath = "http://localhost:8081/res/file/downloadOffice?groupName=group1&fileName=M00/00/03/wKgA31ri4OeAeDbDAABBMa59ZjE88.docx";
		//return getImgServerPath() + File.separator + "res" + File.separator + "file"+ File.separator +"downloadOffice?groupName="+resResource.getGroupName()+"&fileName="+resResource.getFileName()+"";
		return getImgServerPath() + "?groupName="+resResource.getGroupName()+"&fileName="+resResource.getFileName()+"";
		//return getImgServerPath() + File.separator + resResource.getFileName();
	}

	/**
	 * 
	 * 以poBrowser方式打开 word
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月14日 下午4:24:45
	 */
	@RequestMapping("/poBrowser")
	public String poBrowser(PageOfficePermissionBean pageOfficePermissionBean, Model model,
			HttpServletRequest request) {
		model.addAttribute("pageOfficePermissionBean", pageOfficePermissionBean);

		if (pageOfficePermissionBean.getAttachId() == null) {
			pageOfficePermissionBean.setAttachId("");
		}
		if (pageOfficePermissionBean.getRedTitle() == null) {
			pageOfficePermissionBean.setRedTitle("");
		}
		if (pageOfficePermissionBean.getHandSign() == null) {
			pageOfficePermissionBean.setHandSign("");
		}
		if (pageOfficePermissionBean.getFileNameDesc() == null) {
			pageOfficePermissionBean.setFileNameDesc("");
		}

		return "pag/poBrowser";
	}

	/**
	 * 
	 * 以poBrowser方式打开 word
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月14日 下午4:24:45
	 */
	@RequestMapping("/poBlankBrowser")
	public String poBlankBrowser(PageOfficePermissionBean pageOfficePermissionBean, Model model) {
		model.addAttribute("pageOfficePermissionBean", pageOfficePermissionBean);

		if (pageOfficePermissionBean.getAttachId() == null) {
			pageOfficePermissionBean.setAttachId("");
		}
		if (pageOfficePermissionBean.getRedTitle() == null) {
			pageOfficePermissionBean.setRedTitle("");
		}
		if (pageOfficePermissionBean.getHandSign() == null) {
			pageOfficePermissionBean.setHandSign("");
		}
		if (pageOfficePermissionBean.getFileNameDesc() == null) {
			pageOfficePermissionBean.setFileNameDesc("");
		}
		if (pageOfficePermissionBean.getPageType() == null) {
			pageOfficePermissionBean.setPageType("");
		}
		if (pageOfficePermissionBean.getFormId() == null) {
			pageOfficePermissionBean.setFormId("");
		}
		if (pageOfficePermissionBean.getPagePrint() == null) {
			pageOfficePermissionBean.setPagePrint("");
		}
		if (pageOfficePermissionBean.getFieldValues() == null) {
			pageOfficePermissionBean.setFieldValues("");
		}
		if (pageOfficePermissionBean.getFields() == null) {
			pageOfficePermissionBean.setFields("");
		}
		return "pag/poBlankBrowser";
	}

	/**
	 * 
	 * 调用pageOffice demo页面
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月16日 上午11:38:00
	 */
	@RequestMapping("/index")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		// "pag/index1"
		return "pag/index";
	}

	/**
	 * 
	 * 新建的文档保存提交
	 *
	 * @return：void
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月16日 下午12:15:03
	 */
	@RequestMapping("/submitData")
	public void submitData(HttpServletRequest request, HttpServletResponse response) {
		// 定义保存对象

		try {
			FileSaver fs = new FileSaver(request, response);
			String resourceId = request.getParameter("resourceId");
			if (resourceId == null) {
				resourceId = StringUtil.getUUID();
				// throw new Exception("resourceId参数不能为空");
			}
			if ((".doc".equals(fs.getFileExtName())) || (".docx".equals(fs.getFileExtName()))) { // 只保存Word格式的文档。
				long resId = Long.valueOf(resourceId);
				// 文件导入后，关闭pageOffice窗口，再次打卡，或者不到文件名称，和文件后缀名，此时随机给名称，并给扩展名doc，因为再次获取不到文件名和扩展名
				String fileName = StringUtils.isEmpty(fs.getLocalFileName()) ? StringUtil.getUUID() + ".doc"
						: fs.getLocalFileName(); // 文件名称

				String fileExtName = StringUtils.isEmpty(fs.getLocalFileExtName()) ? ".doc" : fs.getLocalFileExtName(); // 本地文件扩展名称

				// 附件上传，数据库记录相关的操作 ，待完成 TODO

				// 模块名称+日期
				//String ymdsfm = new DateTime().toString("yyyyMMddHHmmss");
				//String ymd = ymdsfm.substring(0, 8);
				//String sfm = ymdsfm.substring(8, ymdsfm.length());
				// 创建文件夹
				//String relativePath = WebConstant.MODULE_PAGE_OFFICE + File.separator + ymd;
				//File f = new File(getUploadPath() + File.separator + relativePath);
				/*if (!f.exists()) {
					f.mkdirs();
				}*/
				// 保存文件 路径为磁盘完整路径
				// 构建文件名称
				//String fileNameStr = sfm + StringUtil.getUUID() + fileExtName;
				//fs.saveToFile(getUploadPath() + File.separator + relativePath + File.separator + fileNameStr);
				RetMsg result = resResourceService.saveBlankFile(fileName, fs.getFileBytes(), fs.getFileSize());
				ResResource resource = (ResResource)result.getObject();
				resource.setBizId(resId);
				resource.setType(WebConstant.MODULE_PAGE_OFFICE);
				resource.setFileType(fileExtName);
				resResourceService.updateById(resource);
				// 插入资源表
				//ResAttachment resAttachment = new ResAttachment();
				//resAttachment.setAttachName(fileName);
				//resAttachment.setAttachPath(relativePath + File.separator + fileNameStr);
				//resAttachment.setAttachSize(fileSize);
				//resAttachment.setResourceId(resId);
				//resAttachment.setDescription("表单浏览正文附件上传");
				//resAttachment.setType(WebConstant.MODULE_PAGE_OFFICE);
				//resAttachmentService.insert(resAttachment);

				// 设置保存结果
				fs.setCustomSaveResult(resource.getId() + "|" + resource.getOrgnlFileName());
				response.setCharacterEncoding("utf-8");
				// 指定浏览器以什么码表打开
				response.setHeader("content-type", "text/html;charset=utf-8");
			} else {
				PrintWriter out;
				out = response.getWriter();
				out.println("保存失败！当前文档不是Word文档。"); // 简单输出错误信息，您可以设计友好的提示信息。
				fs.showPage(380, 200); // 如果当前文档不是Word文档，PageOfficeCtrl
										// 控件就弹出对话框显示自定义错误页面。
			}

			// fs.showPage(300,300);
			fs.close();
		} catch (Exception e) {
			log.error("", e);
		}
	}

	/**
	 * 
	 * @throws Exception
	 *             新建文件
	 *
	 * @return：void
	 *
	 * @author：xuc
	 *
	 * @date：2017年11月17日 下午2:42:10
	 */
	@RequestMapping("/openNewWord")
	public void openNewWord(PageOfficePermissionBean pageOfficePermissionBean, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
		poCtrl.addCustomToolButton("导入文件", "importData()", 15);
		poCtrl.addCustomToolButton("提交数据", "submitData()", 1);
		WordDocument doc = new WordDocument();
		poCtrl.setWriter(doc);
		poCtrl.setTitlebar(false);
		poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
		poCtrl.setServerPage(request.getContextPath() + "/poserver.zz");
		poCtrl.setOfficeVendor(OfficeVendorType.WPSOffice); // 客户端启动wps打开文件
		String attachId = pageOfficePermissionBean.getAttachId();
		if (!StringUtils.isEmpty(attachId)) {
			String filePath = packagePushFullPathById(attachId);
			CustomUser user = getCustomDetail();
			// if(new File(filePath).exists()){
			// //当文件找不到，pageoffice会抛异常给客户端，提示文件路径找不到
			// poCtrl.setTimeSlice(10);
			poCtrl.webOpen(filePath, setModeType(pageOfficePermissionBean.getOpenModeType()), user.getNickName());
			// 设置response以什么码表向浏览器写
			response.setCharacterEncoding("utf-8");
			// 指定浏览器以什么码表打开
			response.setHeader("content-type", "text/html;charset=utf-8");
			// }

		}
		// poCtrl.setSaveDataPage("/pag/pageOffice/submitData");

		PrintWriter out;

		out = response.getWriter();
		out.write(poCtrl.getHtmlCode("PageOfficeCtrl1"));
	}

	/**
	 * 套红
	 * 
	 * @author wuhp
	 * @since 2018-04-16 10:13
	 * @param permissionBean
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/redTitleSetting")
	public void redTitleSetting(PageOfficePermissionBean permissionBean, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
		poCtrl.setServerPage(request.getContextPath() + "/poserver.zz"); // 此行必须
		poCtrl.setCustomToolbar(false);
		poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
		// 获取模板文件
		String templatePath = getModelPath(request);
		String filePath = packagePushFullPathById(permissionBean.getAttachId());
		// copyFile(templatePath, filePath);
		CustomUser user = getCustomDetail();
		// 填充数据和正文内容到红头文件 TODO 根据实际规则赋值
		PageOfficeRedTitle redTitle = new PageOfficeRedTitle();
		// redTitle.setCopies("6");
		// redTitle.setDocNum("海洋局发文[2017]001");
		// redTitle.setIssueDate("20171122");
		// redTitle.setIssueDept("办公室");
		redTitle.setMainContent(filePath);
		// redTitle.setTitle("海洋局标题发文");
		// redTitle.setTopicWords("主题词");
		// redTitle.setUnitTitle("单位标题");
		WordDocument doc = packageRetTitleFile(redTitle);
		poCtrl.setWriter(doc);
		poCtrl.webOpen(templatePath, OpenModeType.docNormalEdit, user.getNickName());
		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(poCtrl.getHtmlCode("PageOfficeCtrl1"));
		} catch (IOException e) {
			log.error("", e);
		}

	}

	/**
	 * 套打
	 * 
	 * @author wuhp
	 * @since 2018-04-16 10:13
	 * @param permissionBean
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/pagePrintContrl")
	public void pagePrint(PageOfficePermissionBean permissionBean, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		PageOfficeCtrl poCtrl = new PageOfficeCtrl(request);
		// 封装需要填充的值

		Map<String, String> map = packageFieldValues(request);

		poCtrl.setServerPage(request.getContextPath() + "/poserver.zz"); // 此行必须
		poCtrl.setCustomToolbar(false);
		poCtrl.setJsFunction_AfterDocumentOpened("AfterDocumentOpened");
		// 获取模板文件
		//String templatePath = packageFullPathById(permissionBean.getAttachId());
		String templatePath = packagePushFullPathById(permissionBean.getAttachId());
		// copyFile(templatePath, filePath);
		CustomUser user = getCustomDetail();
		// 填充数据
		WordDocument doc = packageDataRegion(map);
		poCtrl.setWriter(doc);
		poCtrl.webOpen(templatePath, OpenModeType.docNormalEdit, user.getNickName());
		PrintWriter out;
		try {
			out = response.getWriter();
			out.write(poCtrl.getHtmlCode("PageOfficeCtrl1"));
		} catch (IOException e) {
			log.error("", e);
		}

	}

	/**
	 * 封装需要填充的数据区域
	 * 
	 * @author wuhp
	 * @param map
	 * @return
	 */
	public WordDocument packageDataRegion(Map<String, String> resultMap) {
		WordDocument doc = new WordDocument();
		try {
			for (String key : resultMap.keySet()) {
				log.debug("key=" + key + " value=" + resultMap.get(key));
				DataRegion copies = doc.openDataRegion("PO_" + key);
				copies.setValue(resultMap.get(key));
			}
		} catch (Exception e) {
			log.error("需要填充的数据区域不存在，不影响正常逻辑", e);
		}

		return doc;

	}

	/**
	 * 封装需要填充的值域
	 * 
	 * @author wuhp
	 * @since 2018-04-16 11:28
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> packageFieldValues(HttpServletRequest request) throws Exception {
		Map<String, String> result = new HashMap<String, String>();
		String fieldValues = request.getParameter("fieldValues");
		// 格式 域名称1|域值1,域名称2|域值2,域名称3|域值3
		try {
			String[] fields = fieldValues.split(",");
			for (int i = 0; i < fields.length; i++) {
				String field = fields[i];
				if (!field.isEmpty()) {
					String key = field.split("-")[0];
					String value = field.split("-")[1];
					result.put(key, value);
				}
			}

		} catch (Exception e) {
			throw new Exception("fieldValues参数格式错误：" + fieldValues);
		}
		return result;
	}

	/**
	 * 
	 * @throws Exception
	 *             获取模板文件路径
	 *
	 * @return：String
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月23日 下午4:40:32
	 */
	public String getModelPath(HttpServletRequest request) throws Exception {
		String modelId = request.getParameter("modelId");
		String modelPath = "/pageoffice/defaultRedTitleModel.docx";
		if (!StringUtils.isEmpty(modelId)) {
			modelPath = packagePushFullPathById(modelId);
		}
		return modelPath;
	}

	/**
	 * 
	 * 配置 套红文件需要的信息
	 *
	 * @return：WordDocument
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月22日 下午7:12:26
	 */
	public WordDocument packageRetTitleFile(PageOfficeRedTitle redTitle) {
		WordDocument doc = new WordDocument();
		DataRegion copies = doc.openDataRegion("PO_copies");
		if (!StringUtils.isEmpty(redTitle.getCopies())) {
			copies.setValue(redTitle.getCopies());
		}
		DataRegion docNum = doc.openDataRegion("PO_docNum");
		if (!StringUtils.isEmpty(redTitle.getDocNum())) {
			docNum.setValue(redTitle.getDocNum());
		}
		DataRegion issueDate = doc.openDataRegion("PO_issueDate");
		if (!StringUtils.isEmpty(redTitle.getIssueDate())) {
			issueDate.setValue(redTitle.getIssueDate());
		}
		DataRegion issueDept = doc.openDataRegion("PO_issueDept");
		if (!StringUtils.isEmpty(redTitle.getIssueDept())) {
			issueDept.setValue(redTitle.getIssueDept());
		}
		DataRegion sTextS = doc.openDataRegion("PO_mainContent");
		sTextS.setValue("[word]" + redTitle.getMainContent() + "[/word]");
		DataRegion sTitle = doc.openDataRegion("PO_title");
		if (!StringUtils.isEmpty(redTitle.getTitle())) {
			sTitle.setValue(redTitle.getTitle());
		}
		DataRegion topicWords = doc.openDataRegion("PO_topicWords");
		if (!StringUtils.isEmpty(redTitle.getTopicWords())) {
			topicWords.setValue(redTitle.getTopicWords());
		}
		DataRegion unitTitle = doc.openDataRegion("PO_unitTitle");
		if (!StringUtils.isEmpty(redTitle.getUnitTitle())) {
			unitTitle.setValue(redTitle.getUnitTitle());
		}

		return doc;
	}

	/**
	 * 
	 * 打开模式的转换器，如果后续有需要增加，在map 添加类型即可
	 *
	 * @return：Map
	 *
	 * @author：wuhp
	 *
	 * @date：2017年11月15日 下午1:37:39
	 */
	public Map<String, OpenModeType> packageMode() {
		// 目前仅针对word开发
		Map<String, OpenModeType> map = new HashMap<String, OpenModeType>();
		map.put("1", OpenModeType.docReadOnly); // 阅读模式，用户只能阅读文档，不能对文档做任何修改，也不能拖曳文档等。
		map.put("2", OpenModeType.docNormalEdit); // 修改无痕迹模式，普通文档编辑方法，不留痕迹，没有限制。
		map.put("3", OpenModeType.docRevisionOnly);// 修改有痕迹模式，强制痕迹保留。
		// 截止11.20以下未开发
		map.put("4", OpenModeType.docHandwritingOnly); // 手写模式，文档中只能进行手写编辑操作。
		map.put("5", OpenModeType.docAdmin);// 核稿模式，清稿或定稿操作
		map.put("6", OpenModeType.docSubmitForm);// 提交模式，可通过DataRegion向其它页面提交数据。
		return map;
	}

	public OpenModeType setModeType(String modeType) {

		Map<String, OpenModeType> map = packageMode();
		return map.get(modeType);
	}

	@SuppressWarnings({ "unused", "resource" })
	public void copyFile(String oldPath, String newPath) {

		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("");
			log.error("复制单个文件操作出错", e);
			e.printStackTrace();
		}
	}

	public void printByModel() {

	}

}
