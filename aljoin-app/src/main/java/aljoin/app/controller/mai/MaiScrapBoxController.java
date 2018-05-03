package aljoin.app.controller.mai;

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
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiScrapBoxVO;
import aljoin.mai.dao.object.MaiScrapValBoxVO;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mai.iservice.app.AppMaiScrapBoxService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 废件箱表(控制器).
 * 
 * @author：wangj
 * 
 * 				@date： 2017-09-20
 */
@Controller
@RequestMapping(value = "app/mai/maiScrapBox", method = RequestMethod.POST)
@Api(value = "废件箱表", description = "废件箱表接口")
public class MaiScrapBoxController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(MaiScrapBoxController.class);
	@Resource
	private AppMaiScrapBoxService appMaiScrapBoxService;
	@Resource
	private MaiReceiveBoxService maiReceiveBoxService;
	@Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;
	@Resource
	private MaiSendBoxService maiSendBoxService;

	/**
	 * 
	 * 废件箱表(分页列表).
	 *
	 * @return：Page<MaiScrapBox>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/list")
	@ResponseBody 
	@ApiOperation(value = "废件箱表(分页列表)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isUrgent", value = "查询是否紧急", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "staTime", value = "查询开始时间", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "查询结束时间", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isCcessory", value = "查询是否有附件", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "title", value = "查询标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "queryName", value = "查询收件人", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "页面条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "integer", paramType = "query") })
	public RetMsg list(HttpServletRequest request, HttpServletResponse response, MaiListVO ml) {
		Page<MaiScrapBox> page = null;
		RetMsg retMsg = new RetMsg();
		try {
			// 获得使用者id,用于过滤，废件箱仅显示登录用户创建的废件
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			PageBean pageBean = new PageBean();
			pageBean.setPageNum(ml.getPageNum());
			pageBean.setPageSize(ml.getPageSize());
			MaiScrapBox obj = new MaiScrapBox();
			obj.setReceiveFullNames(ml.getQueryName());
			obj.setSubjectText(ml.getTitle());
			obj.setIsDelete(ml.getIsUrgent());
			obj.setMailSize(ml.getIsccessory());
			page = appMaiScrapBoxService.list(pageBean, obj, userId, ml.getStaTime(), ml.getEndTime());
			MaiDraftListVO vo = new MaiDraftListVO();
			List<MaiScrapBox> maiList = page.getRecords();
			List<MaiValListVO> maiValList = new ArrayList<MaiValListVO>();
			for (MaiScrapBox maiScrapBox : maiList) {
				MaiValListVO maiVal = new MaiValListVO();
				maiVal.setMaiID(maiScrapBox.getId().toString());
				if (maiScrapBox.getOrgnlType() == 1) {
				    MaiReceiveBoxSearch maiSearch = maiReceiveBoxSearchService.selectById(maiScrapBox.getOrgnlId());
				    if(null != maiSearch){
				        maiVal.setIsUrgent(maiSearch.getIsUrgent());
				    }
				} else {
					MaiSendBox msb = maiSendBoxService.selectById(maiScrapBox.getOrgnlId());
					if (msb != null) {
						maiVal.setIsUrgent(msb.getIsUrgent());
					}
				}
				maiVal.setLastUpdateTime(maiScrapBox.getLastUpdateTime());
				maiVal.setReceiveFullNames(maiScrapBox.getSendFullName());
				maiVal.setSubjectText(maiScrapBox.getSubjectText());
				maiValList.add(maiVal);
			}
			// MaiValListVO
			vo.setMaiDraftBoxList(maiValList);
			vo.setPages(page.getPages());
			vo.setSize(page.getSize());
			vo.setTotal(page.getTotal());
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
	 * 废件箱表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/add")
	@ResponseBody
	@ApiOperation(value = "添加为废件")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "orgnlType", value = "收件箱 : 0 发件箱: 1", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "orgnlId", value = "邮件对应ID", required = true, dataType = "long", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MaiScrapBox obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			obj.setCreateUserId(userId);
			obj.setCreateUserName(autAppUserLogin.getUserName());
			retMsg = appMaiScrapBoxService.scrapAdd(obj);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 * 
	 * 废件箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MaiScrapBoxVO obj) {

		RetMsg retMsg = new RetMsg();
		try {
			retMsg = new RetMsg();
			List<MaiScrapBox> maiScrapBoxList = obj.getMaiScrapBoxList();
			List<Long> idList = new ArrayList<Long>();
			if (null != maiScrapBoxList) {
				for (MaiScrapBox box : maiScrapBoxList) {
					idList.add(box.getId());
				}
			}
			appMaiScrapBoxService.deleteBatchIds(idList);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 废件箱表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/deleteID")
	@ResponseBody
	@ApiOperation(value = "废件箱表(删除ID)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "delId", value = "删除邮件", required = true, dataType = "String", paramType = "query") })
	public RetMsg deleteId(HttpServletRequest request, HttpServletResponse response, String delId) {

		RetMsg retMsg = new RetMsg();
		try {
			retMsg = new RetMsg();
			appMaiScrapBoxService.deleteById(Long.valueOf(delId));
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 废件箱表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MaiScrapBox obj) {
		RetMsg retMsg = new RetMsg();

		MaiScrapBox orgnlObj = appMaiScrapBoxService.selectById(obj.getId());

		appMaiScrapBoxService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 废件箱表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@ApiOperation(value = "废件箱表(获取邮件详情)") 
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "邮件", required = true, dataType = "String", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response, MaiScrapBox obj) {
		RetMsg retMsg = new RetMsg();
		MaiScrapValBoxVO svb = new MaiScrapValBoxVO();
		try {
			svb = appMaiScrapBoxService.getById(obj);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(svb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("", e.getMessage());
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("操作失败" + e.getMessage());
		}
		return retMsg;
	}

	/**
	 * 
	 * 内部邮件-废件箱-恢复
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月22日 下午5:19:03
	 */
	@RequestMapping("/recover")
	@ResponseBody
	@ApiOperation(value = "废件箱表(恢复)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "maiID", value = "废件ID(String)", required = true, dataType = "String", paramType = "query") })
	public RetMsg recover(HttpServletRequest request, HttpServletResponse response, String maiID) {

		RetMsg regMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			// regMsg = appMaiScrapBoxService.recover(obj);MaiScrapBoxVOobj
			regMsg = appMaiScrapBoxService.recoverMai(maiID, autAppUserLogin);
			// maiAttachmentList[0]
		} catch (Exception e) {
			logger.error("", e);
			regMsg.setCode(AppConstant.RET_CODE_ERROR);
			regMsg.setMessage(e.getMessage());
		}
		return regMsg;
	}

}
