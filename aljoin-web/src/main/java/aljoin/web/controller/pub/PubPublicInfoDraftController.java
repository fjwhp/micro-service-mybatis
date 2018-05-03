package aljoin.web.controller.pub;

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

import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfoDraft;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoDraftVO;
import aljoin.pub.iservice.PubPublicInfoDraftService;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 公共信息草稿表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/pub/pubPublicInfoDraft",method = RequestMethod.POST)
@Api(value = "公共信息草稿Controller",description = "公共信息->我的信息->草稿箱相关接口")
public class PubPublicInfoDraftController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(PubPublicInfoDraftController.class);
	@Resource
	private PubPublicInfoDraftService pubPublicInfoDraftService;
	
	/**
	 * 
	 * 公共信息草稿表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/pubPublicInfoDraftPage",method = RequestMethod.GET)
	@ApiOperation("公共信息草稿页面跳转")
	public String pubPublicInfoDraftPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "pub/pubPublicInfoDraftPage";
	}
	
	/**
	 * 
	 * 公共信息草稿表(分页列表).
	 *
	 * @return：Page<PubPublicInfoDraft>
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation("公共信息草稿分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "categoryId",value = "类型ID（下拉）",required = false,dataType = "long",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "auditStatus",value = "审核情况",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "periodStatus",value = "有效期",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "createTime",value = "日期",required = false,dataType = "date",paramType = "query")
	})
	public Page<PubPublicInfoDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, PubPublicInfoDraftVO obj) {
		Page<PubPublicInfoDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoDraftService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 公共信息草稿表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("公共信息草稿新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "title",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "publishName",value = "发布人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categoryId",value = "类型ID（下拉）",required = false,dataType = "long",paramType = "query"),
			@ApiImplicitParam(name = "period",value = "有效期",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "noticeObjId",value = "公告对象ID （多个用分号分隔",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "noticeObjName",value = "公告对象 （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "content",value = "公告内容",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[0].attachName",value = "附件名称(原文件名)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachPath",value = "上传后的完整路径",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachSize",value = "附件大小(KB)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[1].attachName",value = "附件名称(原文件名)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachPath",value = "上传后的完整路径",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachSize",value = "附件大小(KB)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")

	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, PubPublicInfoDraftVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoDraftService.add(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 公共信息草稿表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@ApiOperation("公共信息草稿删除")
	@ApiImplicitParam(name = "ids",value = "主键(支持批量删除，删除多个用分号分隔)",required = true,dataType = "int",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, PubPublicInfoDraftVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoDraftService.delete(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	/**
	 * 
	 * 公共信息草稿表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("公共信息草稿编辑")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "long",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "publishName",value = "发布人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categoryId",value = "类型ID（下拉）",required = false,dataType = "long",paramType = "query"),
			@ApiImplicitParam(name = "period",value = "有效期",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "noticeObjId",value = "公告对象ID （多个用分号分隔",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "noticeObjName",value = "公告对象 （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "content",value = "公告内容",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[0].attachName",value = "附件名称(原文件名)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachPath",value = "上传后的完整路径",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachSize",value = "附件大小(KB)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[1].attachName",value = "附件名称(原文件名)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachPath",value = "上传后的完整路径",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachSize",value = "附件大小(KB)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")

	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, PubPublicInfoDraftVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = pubPublicInfoDraftService.update(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 公共信息草稿表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("公共信息草稿详情")
	@ApiImplicitParam(name = "id",value = "主键",required = true,dataType = "long",paramType = "query")
	public PubPublicInfoDraftVO getById(HttpServletRequest request, HttpServletResponse response, PubPublicInfoDraft obj) {
		PubPublicInfoDraftVO pubPublicInfoDraftVO = null;
		try {
			pubPublicInfoDraftVO = pubPublicInfoDraftService.detail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return pubPublicInfoDraftVO;
	}

	/**
	 *
	 * 公共信息草稿(提交).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/addProcess")
	@ResponseBody
	@ApiOperation("公共信息草稿提交")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "title",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "publishName",value = "发布人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "categoryId",value = "类型ID（下拉）",required = false,dataType = "long",paramType = "query"),
			@ApiImplicitParam(name = "period",value = "有效期",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "noticeObjId",value = "公告对象ID （多个用分号分隔",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "noticeObjName",value = "公告对象 （多个用分号分隔）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "content",value = "公告内容",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[0].attachName",value = "附件名称(原文件名)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachPath",value = "上传后的完整路径",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[0].attachSize",value = "附件大小(KB)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "attachmentList[1].attachName",value = "附件名称(原文件名)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachPath",value = "上传后的完整路径",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "attachmentList[1].attachSize",value = "附件大小(KB)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")

	})
	public RetMsg addProcess(HttpServletRequest request, HttpServletResponse response, PubPublicInfoDraftVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			if(null != user && null != user.getUserId()){
				obj.setCreateUserId(user.getUserId());
			}
			retMsg = pubPublicInfoDraftService.addProcess(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

}
