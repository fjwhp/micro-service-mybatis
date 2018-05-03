package aljoin.app.controller.mai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.app.controller.BaseController;
import aljoin.app.object.MaiDraftListVO;
import aljoin.app.object.MaiListVO;
import aljoin.app.object.MaiValListVO;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.file.object.AljoinFile;
import aljoin.file.object.UploadParam;
import aljoin.file.service.AljoinFileService;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.AppMaiSendBoxVO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mai.iservice.app.AppMaiDraftBoxService;
import aljoin.mai.iservice.app.AppMaiReceiveBoxService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.iservice.SysParameterService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 草稿箱表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/app/mai/maiWrite", method = RequestMethod.POST)
@Api(value = "发件箱表", description = "发件箱表接口")
public class MaiWriteController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(MaiWriteController.class);

	@Resource
	private AppMaiDraftBoxService appMaiDraftBoxService;
	@Resource
	private AppMaiReceiveBoxService appMaiReceiveBoxService;
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
	private SysParameterService sysParameterService;
	@Resource
	private MaiReceiveBoxService maiReceiveBoxService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private AutUserPubService autUserPubService;
	@Resource
	private AljoinFileService aljoinFileService;
	@Resource
	private ResResourceService resResourceService;

	/**
	 * 
	 * 发件箱箱表(分页列表).
	 *
	 * @return：Page<MaiDraftBox>
	 *
	 * @author：
	 *
	 * @date：2017-11-07
	 */
	@SuppressWarnings("unused")
  @RequestMapping("/list")
	@ResponseBody 
	@ApiOperation(value = "发件箱表(分页列表)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isUrgent", value = "查询是否紧急", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "staTime", value = "查询开始时间", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "查询结束时间", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isccessory", value = "查询是否有附件", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "queryName", value = "查询收件人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "页面条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "integer", paramType = "query") })
	public RetMsg list(HttpServletRequest request, HttpServletResponse response, MaiListVO ml) {
		Page<MaiValListVO> page = null;
		RetMsg retMsg = new RetMsg();
		try {
			// 获得使用者id,用于过滤，草稿箱仅显示登录用户创建的草稿
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			String userId = autAppUserLogin.getUserId().toString();
			MaiSendBox obj = new MaiSendBox();
			PageBean pageBean = new PageBean();
			pageBean.setPageNum(ml.getPageNum());
			pageBean.setPageSize(ml.getPageSize());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			if (ml.getStaTime() != null && !"".equals(ml.getStaTime())) {
				obj.setCreateTime(format.parse(ml.getStaTime()));// 存储临时查询条件
			}
			if (ml.getEndTime() != null && !"".equals(ml.getEndTime())) {
				obj.setLastUpdateTime(format.parse(ml.getEndTime()));// 存储临时查询条件
			}
			if (ml.getIsUrgent() != null) {
				obj.setIsUrgent(ml.getIsUrgent());// 存储临时查询条件
			}
			if (ml.getIsccessory() != null) {
				obj.setIsDelete(ml.getIsccessory());// 存储临时查询条件
			}
			if (ml.getTitle() != null && !"".equals(ml.getTitle())) {
				obj.setSubjectText(ml.getTitle());// 存储临时查询条件
			}
			if (ml.getQueryName() != null && !"".equals(ml.getQueryName())) {
				obj.setSendFullName(ml.getQueryName());// 存储临时查询条件
			}
			Page<MaiSendBox> pages = maiSendBoxService.applist(pageBean, obj, userId);
			MaiDraftListVO vo = new MaiDraftListVO();
			List<MaiSendBox> maiList = pages.getRecords();
			List<MaiValListVO> maiValList = new ArrayList<MaiValListVO>();
			for (MaiSendBox maiDraftBox : maiList) {
				MaiValListVO maiVal = new MaiValListVO();
				maiVal.setMaiID(maiDraftBox.getId().toString());
				maiVal.setIsUrgent(maiDraftBox.getIsUrgent());
				maiVal.setLastUpdateTime(maiDraftBox.getLastUpdateTime());
				maiVal.setReceiveFullNames(maiDraftBox.getReceiveFullNames());
				maiVal.setSubjectText(maiDraftBox.getSubjectText());
				maiValList.add(maiVal);
			}
			// MaiValListVO
			vo.setMaiDraftBoxList(maiValList);
			vo.setPages(pages.getPages());
			vo.setSize(pages.getSize());
			vo.setTotal(pages.getTotal());
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(vo);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("App邮件列表查询错误", e);

		}
		return retMsg;
	}

	/**
	 * 
	 * 发件箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/deleteID")
	@ResponseBody
	@ApiOperation(value = "发件箱表(删除)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "delId", value = "删除邮件ID", required = true, dataType = "String", paramType = "query") })
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			String delId = request.getParameter("delId");
			MaiSendBoxVO vo = new MaiSendBoxVO();
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			vo.setIds(delId);
			// maiSendBoxService.deleteById(Long.valueOf(delId));
			retMsg = maiSendBoxService.appdelete(vo, autAppUserLogin.getUserId(), autAppUserLogin.getUserName());
			if (retMsg.getCode() == 0) {
				retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
				retMsg.setMessage("操作成功");
			} else {
				retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 发件箱箱表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@ApiOperation(value = "发件箱表(获取邮件详情)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "邮件", required = true, dataType = "String", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response, MaiSendBox obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		//MaiSendBoxVO maiSendBoxVO = new MaiSendBoxVO();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			AutUser user = autUserService.selectById(autAppUserLogin.getUserId());
			MaiSendBoxVO msb = maiSendBoxService.appdetail(obj, user);
			AppMaiSendBoxVO appMaiSendBoxVO = new AppMaiSendBoxVO();
			appMaiSendBoxVO.setMaiSendBox(msb.getMaiSendBox());
			//appMaiSendBoxVO.setMaiAttachmentList(msb.getMaiAttachmentList());
			SysParameter parameter = sysParameterService.selectBykey("ImgServer");
			String rootPath = "";
			if (null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())) {
				rootPath = parameter.getParamValue();
			}
           List<MaiAttachment> attachmentList=msb.getMaiAttachmentList();
		   List<MaiAttachment> returnList =new ArrayList<MaiAttachment>();					
				if (attachmentList!=null && attachmentList.size()>0) {
					for (MaiAttachment maiAttachment : attachmentList) {
						String tmp=maiAttachment.getAttachPath();
						//tmp=tmp.substring(2, tmp.length());
						tmp=tmp.replaceAll("//","/");
						String path=rootPath+tmp;
						returnList.add(maiAttachment.setAttachPath(path));
					}
					appMaiSendBoxVO.setMaiAttachmentList(returnList);
			}					
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(msb);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败" + e.getMessage());
		}
		return retMsg;
	}

	public String getImg(String rootPath, String id) throws Exception {
		String path = "";
		if (id.length() > 0) {
			String[] ids = id.split(";");
			for (int i = 0; i < ids.length; i++) {
				if (ids[i] != null && !"".equals(ids[i])) {
					if (!"".equals(rootPath)) {
						AutUserPub autUserPub = autUserPubService.selectById(ids[i]);
						if (autUserPub != null && autUserPub.getUserIcon() != null
								&& !"".equals(autUserPub.getUserIcon())) {
							path = path + rootPath + autUserPub.getUserIcon() + ";";
						} else {
							// 默认地址
							path = path + "web/images/0.jpg" + ";";
						}
					} else {
						path = path + "web/images/0.jpg" + ";";
					}
				}
			}
		}
		return path;

	}

	/**
	 * 
	 * 发件箱表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation(value = "邮件发送接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.receiveUserIds", value = "收件人用户ID(分号分隔)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.receiveUserNames", value = "收件人账号(分号分隔)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.receiveFullNames", value = "收件人名称(分号分隔)", required = true, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiReceiveBox.copyUserIds", value = "抄送人用户ID(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.copyUserNames", value = "抄送人账号(分号分隔)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.copyFullNames", value = "抄送人名称(分号分隔)", required = false, dataType = "string", paramType = "query"),

			@ApiImplicitParam(name = "maiReceiveBox.subjectText", value = "主题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.mailContent", value = "内容", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.isUrgent", value = "是否紧急", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiReceiveBox.isImportant", value = "是否重要", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.isReceiveSmsRemind", value = "是否进行收件人短信提醒", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.receiveUserCount", value = "收件人数", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiSendBox.isCopySmsRemind", value = "是否进行抄送人短信提醒", required = false, dataType = "int", paramType = "query"),

			@ApiImplicitParam(name = "maiSendBox.isReceipt", value = "本邮件是否回执邮件", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[0].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachName", value = "附件名称(原文件名)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachPath", value = "上传后的完整路径", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiAttachmentList[1].attachSize", value = "附件大小(KB)", required = false, dataType = "string", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiWriteVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			AutUser user = autUserService.selectById(autAppUserLogin.getUserId());
			retMsg = maiSendBoxService.appAddMai(obj, user);
			if(retMsg.getCode()==0 || retMsg.getCode()==200){
				retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			}else{
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 发件箱表(撤回并删除)
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017年9月28日 下午4:51:27
	 */
	@RequestMapping("/revokeAndDelete")
	@ResponseBody
	@ApiOperation(value = "发件箱表(撤回并删除)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiID", value = "撤回邮件ID（String）", required = true, dataType = "string", paramType = "query") })
	public RetMsg revokeAndDelete(HttpServletRequest request, HttpServletResponse response, String maiID) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			MaiSendBox mb = new MaiSendBox();
			mb.setId(Long.valueOf(maiID));
			retMsg = maiSendBoxService.aPPrevokeAndDelete(mb, autAppUserLogin.getUserId().toString(),
					autAppUserLogin.getUserName());
			if (retMsg.getCode() == 0) {
				retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			} else {
				retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 撰写邮件上传附件.
	 *
	 * 				@return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-25
	 */
	@RequestMapping(value = "/upload")
	@ResponseBody
	@ApiOperation(value = "邮件上传接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "file", value = "文件流", required = false, dataType = "string", paramType = "query") })
	public RetMsg upload(MultipartHttpServletRequest request) {
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
            uploadParam.setMaxSize(limitSize*1024);
            //允许上传的类型
            String allowType = map.get("allowType");
            String[] typeArr = allowType.split("\\|");
            uploadParam.setAllowTypeList(Arrays.asList(typeArr));
            for (MultipartFile file : files) {
                fileList.add(new AljoinFile(file.getBytes(), file.getOriginalFilename(), file.getSize()));
            }
            uploadParam.setFileList(fileList);
			retMsg = resResourceService.upload(uploadParam,"撰写新邮件");
			if (retMsg.getCode() == 0) {
				retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			} else {
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("上传失败");
		}
		return retMsg;
	}
}
