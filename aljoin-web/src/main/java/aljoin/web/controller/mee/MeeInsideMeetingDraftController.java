package aljoin.web.controller.mee;

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
import aljoin.mee.dao.object.MeeInsideMeetingDraftDO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.iservice.MeeInsideMeetingDraftService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 内部会议草稿表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/mee/meeInsideMeetingDraft",method = RequestMethod.POST)
@Api(value = "内部会议草稿Controller",description="会议管理->内部会议草稿相关接口")
public class MeeInsideMeetingDraftController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(MeeInsideMeetingDraftController.class);
	@Resource
	private MeeInsideMeetingDraftService meeInsideMeetingDraftService;
	
	/**
	 * 
	 * 内部会议草稿表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeInsideMeetingDraftPage",method = RequestMethod.GET)
	@ApiOperation("内部会议草稿页面")
	public String meeInsideMeetingDraftPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "mee/meeInsideMeetingDraftPage";
	}
	
	/**
	 * 
	 * 内部会议草稿表(分页列表).
	 *
	 * @return：Page<MeeInsideMeetingDraft>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation("内部会议草稿分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingHost",value = "主持人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId",value = "会议室ID（会议室下拉框）",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingSituation",value = "使用情况（下拉框）",required = false,dataType = "string",paramType = "query")
	})
	public Page<MeeInsideMeetingDraftDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MeeInsideMeetingVO obj) {
		Page<MeeInsideMeetingDraftDO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			page = meeInsideMeetingDraftService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 内部会议草稿表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("内部会议草稿新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingHost",value = "主持人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "contacts",value = "联系人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId",value = "会议室名称",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomName",value = "会议室名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "address",value = "地址",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "partyMemebersId",value = "参会人员ID(多个分号分隔)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "partyMemeberNames",value = "参会人员(多个分号分隔)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "beginTime",value = "会议开始时间(格式为:2017-10-10 10:10)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "endTime",value = "会议结束时间(格式为:2017-10-10 10:10)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "meetingContent",value = "会议内容",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "isWarnMsg",value = "是否短信提醒（0：否 1：是）",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail",value = "是否邮件提醒（0：否 1：是）",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline",value = "是否在线提醒（0：否 1：是）",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "attendances",value = "出席人员 (多个分号分隔)",required = false,dataType = "string",paramType = "query"),
			
			@ApiImplicitParam(name = "resResourceList[0].id",value = "资源id",required = false,dataType = "string",paramType = "query"),
			
			@ApiImplicitParam(name = "resResourceList[1].id",value = "资源id",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeInsideMeetingDraftService.add(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 内部会议草稿表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@ApiOperation("内部会议草稿删除")
	@ApiImplicitParam(name = "id",value = "主键ID",required = false,dataType = "long",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeInsideMeetingDraftService.delete(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 内部会议草稿表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("内部会议草稿编辑")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "主键ID",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingHost",value = "主持人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "contacts",value = "联系人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId",value = "会议室名称",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomName",value = "会议室名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "address",value = "地址",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "partyMemebersId",value = "参会人员ID(多个分号分隔)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "partyMemeberNames",value = "参会人员(多个分号分隔)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "beginTime",value = "会议开始时间(格式为:2017-10-10 10:10)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "endTime",value = "会议结束时间(格式为:2017-10-10 10:10)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "meetingContent",value = "会议内容",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "isWarnMsg",value = "是否短信提醒（0：否 1：是）",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isWarnMail",value = "是否邮件提醒（0：否 1：是）",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "isWarnOnline",value = "是否在线提醒（0：否 1：是）",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "attendances",value = "出席人员 (多个分号分隔)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "resResourceList[0].id",value = "资源id",required = false,dataType = "string",paramType = "query"),
            
            @ApiImplicitParam(name = "resResourceList[1].id",value = "资源id",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeInsideMeetingDraftService.update(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 内部会议草稿表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("内部会议草稿详情")
	@ApiImplicitParam(name = "id",value = "主键ID",required = true,dataType = "long",paramType = "query")
	public MeeInsideMeetingVO getById(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		MeeInsideMeetingVO meeInsideMeetingVO = null;
		try {
			meeInsideMeetingVO = meeInsideMeetingDraftService.detail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return meeInsideMeetingVO;
	}

}
