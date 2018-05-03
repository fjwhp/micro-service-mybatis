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
import aljoin.mee.dao.object.MeeOutsideMeetingDraftDO;
import aljoin.mee.dao.object.MeeOutsideMeetingDraftVO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.mee.iservice.MeeOutsideMeetingDraftService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 外部会议草稿表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/mee/meeOutsideMeetingDraft",method = RequestMethod.POST)
@Api(value = "外部会议草稿Controller",description = "会议管理->外部会议草稿相关接口")
public class MeeOutsideMeetingDraftController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(MeeOutsideMeetingDraftController.class);
	@Resource
	private MeeOutsideMeetingDraftService meeOutsideMeetingDraftService;
	
	/**
	 * 
	 * 外部会议草稿表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeOutsideMeetingDraftPage",method = RequestMethod.GET)
	public String meeOutsideMeetingDraftPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "mee/meeOutsideMeetingDraftPage";
	}
	
	/**
	 * 
	 * 外部会议草稿表(分页列表).
	 *
	 * @return：Page<MeeOutsideMeetingDraft>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "address",value = "地址",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "startDate",value = "开始日期",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "endDate",value = "结束日期",required = false,dataType = "date",paramType = "query")

	})
	public Page<MeeOutsideMeetingDraftDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MeeOutsideMeetingDraftVO obj) {
		Page<MeeOutsideMeetingDraftDO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			page = meeOutsideMeetingDraftService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 外部会议草稿表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation("外部会议草稿新增接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingHost",value = "主持人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "contacts",value = "联系人",required = false,dataType = "string",paramType = "query"),
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

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

            @ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeOutsideMeetingDraftService.add(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 外部会议草稿表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@ApiOperation("外部会议草稿删除接口")
	@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeOutsideMeetingDraftService.delete(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 外部会议草稿表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation("外部会议草稿编辑接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingHost",value = "主持人",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "contacts",value = "联系人",required = false,dataType = "string",paramType = "query"),
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

			@ApiImplicitParam(name = "resResourceList[0].id", value = "资源id", required = false, dataType = "string", paramType = "query"),

            @ApiImplicitParam(name = "resResourceList[1].id", value = "资源id", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		RetMsg retMsg = new RetMsg();
        try {
			retMsg = meeOutsideMeetingDraftService.update(obj);
        }catch (Exception e){
          logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * 外部会议草稿表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("外部会议草稿详情接口")
	@ApiImplicitParam(name = "meetingTitle",value = "会议标题",required = false,dataType = "string",paramType = "query")
	public MeeOutsideMeetingVO getById(HttpServletRequest request, HttpServletResponse response, MeeOutsideMeetingVO obj) {
		MeeOutsideMeetingVO meeOutsideMeetingVO = null;
		try {
			meeOutsideMeetingVO = meeOutsideMeetingDraftService.detail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return meeOutsideMeetingVO;
	}

}
