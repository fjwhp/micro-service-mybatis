package aljoin.web.controller.off;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
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
import aljoin.off.dao.entity.OffDailylog;
import aljoin.off.dao.object.OffDailylogDO;
import aljoin.off.dao.object.OffDailylogVO;
import aljoin.off.iservice.OffDailylogService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 工作日志(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-14
 */
@Controller
@RequestMapping(value = "/off/offDailylog",method = RequestMethod.POST)
@Api(value = "工作日志controller",description = "工作计划->工作日志相关接口")
public class OffDailylogController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(OffDailylogController.class);
	@Resource
	private OffDailylogService offDailylogService;
	
	/**
	 * 
	 * 工作日志(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/offDailylogPage",method = RequestMethod.GET)
	@ApiOperation(value = "工作日志页面接口")
	public String offDailylogPage(HttpServletRequest request,HttpServletResponse response) {
		return "off/offDailylogPage";
	}
	
	/**
	 * 
	 * 工作日志(分页列表).
	 *
	 * @return：Page<OffDailylog>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[工作日志]-[搜索]")
	@ApiOperation(value = "工作日志分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "workBegDate",value = "开始日期",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "workEndDate",value = "结束日期",required = false,dataType = "date",paramType = "query"),

	})
	public Page<OffDailylogDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffDailylogVO obj) {
		Page<OffDailylogDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			page = offDailylogService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 工作日志(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[工作日志]-[新增]")
	@ApiOperation(value = "工作日志新增接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "title",value = "标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "workDate",value = "日志日期",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "content",value = "内容",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, OffDailylogVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			retMsg = offDailylogService.add(obj);
		}catch (Exception e){
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 工作日志(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14ID
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[工作日志]-[删除]")
	@ApiOperation(value = "工作日志删除接口")
	@ApiImplicitParam(name = "id",value = "日志Id",required = true,dataType = "int",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, OffDailylog obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = offDailylogService.delete(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 工作日志(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[工作日志]-[修改]")
	@ApiOperation(value = "工作日志修改接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",value = "ID",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "创建用户ID",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "workDate",value = "地点",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "content",value = "内容",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, OffDailylogVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = offDailylogService.update(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 工作日志(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[工作日志]-[详情]")
	@ApiOperation(value = "工作日志详情接口")
	@ApiImplicitParam(name = "id",value = "ID",required = true,dataType = "int",paramType = "query")
	public OffDailylog getById(HttpServletRequest request, HttpServletResponse response, OffDailylog obj) {
		OffDailylog offDailylog = null;
		try {
			offDailylog =  offDailylogService.detail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return offDailylog;
	}

	/**
	 *
	 * 工作日志(获取当前登录用户信息).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-18
	 */
	@RequestMapping(value = "/getCurrentUser")
	@ResponseBody
	@ApiOperation(value = "工作日志获取当前用户信息接口")
	public RetMsg getCurrentUser(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		Map<String,Object> map = new HashMap<String,Object>();
		try {
			CustomUser currentUser = getCustomDetail();
			if(null != currentUser){
				DateTime dateTime = new DateTime(new Date());
				map.put("currentUser",currentUser);
				map.put("currentDate",dateTime.toString("yyyy-MM-dd"));
				retMsg.setCode(0);
				retMsg.setObject(map);
				retMsg.setMessage("操作成功");
			}else{
				retMsg.setCode(1);
				retMsg.setMessage("操作失败");
			}
		}catch (Exception e){
		  logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 工作日志(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/deleteByIds")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[工作日志]-[批量删除]")
	@ApiOperation(value = "工作日志批量删除接口")
	@ApiImplicitParam(name = "id",value = "ID",required = true,dataType = "int",paramType = "query")
	public RetMsg deleteByIdList(HttpServletRequest request, HttpServletResponse response, String ids) {
		RetMsg retMsg = null;
		try {
			retMsg = offDailylogService.deleteByIds(ids);
		}catch (Exception e){
		  logger.error("", e);
		}
		return retMsg;
	}
}
