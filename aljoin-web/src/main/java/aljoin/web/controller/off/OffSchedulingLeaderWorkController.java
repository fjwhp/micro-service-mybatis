package aljoin.web.controller.off;

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
import aljoin.off.dao.object.OffSchedulingVO;
import aljoin.off.iservice.OffSchedulingService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 日程安排表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-14
 */
@Controller
@RequestMapping(value = "/off/offSchedulingLeader",method = RequestMethod.POST)
@Api(value = "领导看板controller",description = "工作计划->领导查看相关接口")
public class OffSchedulingLeaderWorkController extends BaseController {
	
	private final static Logger logger = LoggerFactory.getLogger(OffSchedulingLeaderWorkController.class);
	@Resource
	private OffSchedulingService offSchedulingService;
	
	/**
	 *
	 * 领导看板页面.
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/offSchedulingLeaderPage",method = RequestMethod.GET)
	@ApiOperation(value = "领导查看页面接口")
	public String offSchedulingLeaderPage(HttpServletRequest request,HttpServletResponse response) {

		return "off/offSchedulingLeaderPage";
	}
	
	/**
	 *
	 * 领导看板(分页列表).
	 *
	 * @return：Page<OffScheduling>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	@RequestMapping(value = "/lederList")
	@ResponseBody
	@FuncObj(desc = "[个人办公]-[领导看板]-[搜索]")
	@ApiOperation(value = "领导看板分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "theme",value = "主题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "type",value = "类别",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "startBegDate",value = "开始日期(起)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "startEndDate",value = "结束日期(至)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "createFullName",value = "发布人",required = false,dataType = "string",paramType = "query"),
	})
	public Page<OffSchedulingVO> lederList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffSchedulingVO obj) {
		Page<OffSchedulingVO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			page = offSchedulingService.leaderList(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	
	/**
	 * 
	 * 日程安排表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public OffSchedulingVO getById(HttpServletRequest request, HttpServletResponse response, OffSchedulingVO obj) {
		OffSchedulingVO offScheduling = null;
		CustomUser user = getCustomDetail();
		if(null != user){
			obj.setCreateUserId(user.getUserId());
		}
		try {
			offScheduling = offSchedulingService.detail(obj);
		} catch (Exception e) {
			logger.error("",e);
		}
		return offScheduling;
	}

}
