package aljoin.web.controller.mee;

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

import aljoin.aut.security.CustomUser;
import aljoin.mee.dao.entity.MeeMeetingRoom;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingRoomDO;
import aljoin.mee.iservice.MeeMeetingRoomService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 会议室表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/mee/meeMeetingRoom",method = RequestMethod.POST)
@Api(value = "会议室controller",description = "会议管理->会议室相关接口")
public class MeeMeetingRoomController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(MeeMeetingRoomController.class);
	@Resource
	private MeeMeetingRoomService meeMeetingRoomService;
	
	/**
	 * 
	 * 会议室表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/meeManagePage",method = RequestMethod.GET)
	@ApiOperation("会议室页面跳转")
	public String meeMeetingRoomPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "mee/meeManagePage";
	}
	
	/**
	 * 
	 * 会议室表(分页列表).
	 *
	 * @return：Page<MeeMeetingRoom>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation(value = "会议室分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomName",value = "会议室",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomAddress",value = "地址",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "personCharge",value = "负责人",required = false,dataType = "string",paramType = "query")
	})
	public Page<MeeMeetingRoomDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MeeMeetingRoom obj) {
		Page<MeeMeetingRoomDO> page = null;
		try {
			page = meeMeetingRoomService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 会议室表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation(value = "新增会议室接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "meetingRoomName",value = "会议室名称",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "personNumber",value = "可容纳人数",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomAddress",value = "会议室地址",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "personChargeId",value = "会议室负责人ID(多个分号分隔)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "personCharge",value = "会议室负责人(多个分号分隔)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "deviceDescription",value = "设备描述",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, MeeMeetingRoom obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeMeetingRoomService.add(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 会议室表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, MeeMeetingRoom obj) {
		RetMsg retMsg = new RetMsg();

		meeMeetingRoomService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 会议室表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, MeeMeetingRoom obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = meeMeetingRoomService.update(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 会议室表(根据ID获取对象).
	 *
	 *@throws Exception
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public MeeMeetingRoom getById(HttpServletRequest request, HttpServletResponse response, MeeMeetingRoom obj) throws Exception {
	  MeeMeetingRoom room = null;
	  room = meeMeetingRoomService.detail(obj.getId());
	  return room;
	}

	/**
	 *
	 * 会议室周分页列表.
	 *
	 * @return：MeeMeetingRoomCountDO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/countlist")
	@ResponseBody
	@ApiOperation(value = "会议室分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "thisWeek",value = "本周(查询条件)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "thisMonth",value = "本月(查询条件)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomName",value = "会议室名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "meetingRoomId",value = "会议室Id(用于查看会议室情况)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "beginTime",value = "开始日期",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "endTime",value = "结束日期",required = false,dataType = "date",paramType = "query")
	})
	public MeeMeetingRoomCountDO countlist(HttpServletRequest request, HttpServletResponse response, MeeInsideMeetingVO obj) {
		MeeMeetingRoomCountDO meetingRoomCountDO = null;
		try {
			meetingRoomCountDO = meeMeetingRoomService.countlist(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return meetingRoomCountDO;
	}

	/**
	 *
	 * 会议室列表(列表下拉加载数据).
	 *
	 * @return：List<MeeMeetingRoom>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/roomList")
	@ResponseBody
	@ApiOperation(value = "会议室列表(列表下拉加载数据)")
	public List<MeeMeetingRoom> roomList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MeeMeetingRoom obj) {
		List<MeeMeetingRoom> list = null;
		try {
			list = meeMeetingRoomService.roomList( obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return list;
	}
	
	/**
	 *
	 * 负责人会议室列表(列表下拉加载数据).
	 *
	 * @return：List<MeeMeetingRoom>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/chargeList")
	@ResponseBody
	@ApiOperation(value = "user所负责的会议室列表")
	public List<MeeMeetingRoom> chargeList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, MeeMeetingRoom obj) {
		List<MeeMeetingRoom> list = null;
		try {
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			list = meeMeetingRoomService.chargeList( obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return list;
	}
}
