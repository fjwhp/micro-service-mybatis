package aljoin.app.controller.mai;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.app.controller.BaseController;
import aljoin.app.object.MaiDraftListVO;
import aljoin.app.object.MaiListVO;
import aljoin.app.object.MaiValListVO;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.object.AppMaiReceiveBoxVO;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mai.iservice.app.AppMaiDraftBoxService;
import aljoin.mai.iservice.app.AppMaiReceiveBoxService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
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
 * 				@date： 2017-09-20
 */
@Controller
@RequestMapping(value = "/app/mai/maiReceiveBox", method = RequestMethod.POST)
@Api(value = "收件箱表", description = "收件箱表接口")
public class MaiReceiveBoxController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(MaiReceiveBoxController.class);

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

	/**
	 * 
	 * 收件箱表(分页列表).
	 *
	 * @return：Page<MaiDraftBox>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@SuppressWarnings("unused")
	@RequestMapping("/list")
	@ResponseBody 
	@ApiOperation(value = "收件箱表(分页列表)") 
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
			@ApiImplicitParam(name = "isRead", value = "是否已读", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "integer", paramType = "query") })
	public RetMsg list(HttpServletRequest request, HttpServletResponse response, MaiListVO ml) {
		Page<MaiValListVO> page = null;
		RetMsg retMsg = new RetMsg();
		try {
			// 获得使用者id,用于过滤，草稿箱仅显示登录用户创建的草稿
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			MaiReceiveBoxSearch obj = new MaiReceiveBoxSearch();
			
			PageBean pageBean = new PageBean();
			pageBean.setPageNum(ml.getPageNum());
			pageBean.setPageSize(ml.getPageSize());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			if (ml.getStaTime() != null && !"".equals(ml.getStaTime())) {
			    obj.setSendTime(format.parse(ml.getStaTime()));// 存储临时查询条件
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
			if (ml.getIsRead() != null && !"".equals(ml.getIsRead())) {
			    obj.setIsRead(Integer.valueOf(ml.getIsRead()));// 存储临时查询条件
			}
			if (ml.getQueryName() != null && !"".equals(ml.getQueryName())) {
			    obj.setSendFullName(ml.getQueryName());
			}
			Page<MaiReceiveBoxSearch> pages = appMaiReceiveBoxService.list(pageBean, obj, userId);
			MaiDraftListVO vo = new MaiDraftListVO();
			List<MaiReceiveBoxSearch> maiList = pages.getRecords();
			List<MaiValListVO> maiValList = new ArrayList<MaiValListVO>();
			for (MaiReceiveBoxSearch maiDraftBox : maiList) {
				MaiValListVO maiVal = new MaiValListVO();
				maiVal.setMaiID(maiDraftBox.getId().toString());
				maiVal.setIsRead(maiDraftBox.getIsRead());
				maiVal.setIsImportant(maiDraftBox.getIsImportant());
				maiVal.setIsUrgent(maiDraftBox.getIsUrgent());
				maiVal.setLastUpdateTime(maiDraftBox.getSendTime());
				maiVal.setReceiveFullNames(maiDraftBox.getSendFullName());
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
	 * 草稿箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/deleteID")
	@ResponseBody
	@ApiOperation(value = "草稿箱表(删除)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "delId", value = "删除邮件ID", required = true, dataType = "String", paramType = "query") })
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			String delId = request.getParameter("delId");
			MaiReceiveBoxVO mb = new MaiReceiveBoxVO();
			mb.setIds(delId);
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			retMsg = appMaiReceiveBoxService.delete(mb, autAppUserLogin.getUserId().toString(),
					autAppUserLogin.getUserName());
			// appMaiReceiveBoxService.deleteById(delId);
			if (retMsg.getCode() == 0) {
				retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
				retMsg.setMessage("操作成功");
			} else {
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 收件箱表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@ApiOperation(value = "收件箱表(获取邮件详情)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "readType", value = "移动设备", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "邮件", required = true, dataType = "String", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response, MaiReceiveBox obj, String readType)
			throws Exception {
		RetMsg retMsg = new RetMsg();
		AppMaiReceiveBoxVO maiReceiveBoxVO = new AppMaiReceiveBoxVO();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			maiReceiveBoxVO = appMaiReceiveBoxService.getById(obj, autAppUserLogin.getUserId().toString(), readType);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(maiReceiveBoxVO);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 收件箱-标记
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017年9月28日 下午4:51:27
	 */
	@RequestMapping("/signImport")
	@ResponseBody
	@ApiOperation(value = "收件箱表(标记)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "important", value = "标注1,取消标注0", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "maiID", value = "标记邮件ID（String）", required = true, dataType = "string", paramType = "query") })
	public RetMsg signImport(HttpServletRequest request, HttpServletResponse response, String maiID, String important) {
		RetMsg retMsg = new RetMsg();
		try {
			// AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
		    MaiReceiveBoxSearch mb = new MaiReceiveBoxSearch();
			mb.setId(Long.valueOf(maiID));
			mb.setIsImportant(Integer.valueOf(important));
			retMsg = maiReceiveBoxService.signImport(mb);
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
	 * 收件箱
	 *
	 * @return：RetMsg
	 *
	 * @author：
	 *
	 * @date：2017年9月28日 下午4:51:27
	 */
	/*
	 * @RequestMapping("/revokeAndDelete")
	 * 
	 * @ResponseBody
	 * 
	 * @ApiOperation(value = "收件箱表(撤回并删除)") // 接口文档对应名称 // 参数说明
	 * 
	 * @ApiImplicitParams({
	 * 
	 * @ApiImplicitParam(name = "timestamp", value = "时间戳", required = false,
	 * dataType = "string", paramType = "query"),
	 * 
	 * @ApiImplicitParam(name = "sign", value = "签名", required = false, dataType
	 * = "string", paramType = "query"),
	 * 
	 * @ApiImplicitParam(name = "token", value = "登录令牌", required = true,
	 * dataType = "string", paramType = "query"),
	 * 
	 * @ApiImplicitParam(name = "maiID", value = "撤回邮件ID（String）", required =
	 * true, dataType = "string", paramType = "query")}) public RetMsg
	 * revokeAndDelete(HttpServletRequest request, HttpServletResponse response,
	 * String maiID) { RetMsg retMsg = new RetMsg(); try { AutAppUserLogin
	 * autAppUserLogin = getAppUserLogin(request); MaiReceiveBox mb=new
	 * MaiReceiveBox(); mb.setId(Long.valueOf(maiID)); retMsg =
	 * maiReceiveBoxService.revokeAndDelete(mb); if(retMsg.getCode()==0){
	 * retMsg.setCode(AppConstant.RET_CODE_SUCCESS); }else{
	 * retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR); } } catch (Exception e) {
	 * logger.error("",e); retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
	 * retMsg.setMessage("操作失败"); } return retMsg; }
	 */

}
