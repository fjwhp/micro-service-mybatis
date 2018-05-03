package aljoin.web.controller.sma;

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
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sma.dao.entity.LeaStatisticsModule;
import aljoin.sma.iservice.SystemMaintainService;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.dao.object.SysParameterVO;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 用户公共信息表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Controller
@RequestMapping(value = "/sma/systemMaintain",method = RequestMethod.POST)
@Api(value = "系统维护controller",description = "系统维护相关接口")
public class SystemMaintainController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(SystemMaintainController.class);
	@Resource
	private SystemMaintainService systemMaintainService;
	

	/**
	 *
	 * 公共信息管理(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping(value = "/smaPubsmsPage",method = RequestMethod.GET)
	@ApiOperation("公共信息页面接口")
	public String autUserPubPage(HttpServletRequest request, HttpServletResponse response) {

		return "sma/smaPubsmsPage";
	}
	
	/**
	 *
	 * 系统设置.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/leaListPage")
	@ResponseBody
	@ApiOperation(value = "领导页面")	
	public Page<LeaStatisticsModule> leaListPage(HttpServletRequest request, HttpServletResponse response, PageBean pb) {
		Page<LeaStatisticsModule> page=new Page<LeaStatisticsModule>();
		try {
			 CustomUser user = getCustomDetail();
			page = systemMaintainService.selectUser(user.getUserId().toString(),pb);
		} catch (Exception e) {
		    logger.error("", e);
		}
		return  page;
	}
	/**
	 *
	 * 系统设置.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/addOrUpdataLea")
	@ResponseBody
	@ApiOperation(value = "领导页面")	
	public RetMsg addOrUpdataLea(HttpServletRequest request, HttpServletResponse response,  LeaStatisticsModule obj) {
		RetMsg retMsg = new RetMsg();
		CustomUser user = getCustomDetail();			
		try {
			retMsg=systemMaintainService.updateModule(obj, user.getUserId());			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("",e);
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage("更新失败"+e.getMessage());
		}		
		return retMsg;
	}
	/**
	 *
	 * 系统设置.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/sysSet")
	@ResponseBody
	@ApiOperation(value = "系统设置接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params['is_mail_sms']",value = "邮件短信(0:关闭 1:打开)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['allow_revoke_time']",value = "撤回并删除有效时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['personal_mail_space']",value = "个人邮件空间限制(kb)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['leader_mail_space']",value = "领导邮件空间限制(kb)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['sys_attachment_size']",value = "系统附件大小(kb)",required = false,dataType = "string",paramType = "query")
	})
	public RetMsg sysSet(HttpServletRequest request, HttpServletResponse response, SysParameterVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = systemMaintainService.sysSet(obj);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return  retMsg;
	}

	/**
	 *
	 * 系统设置详情.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/sysSetDetail",method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "系统设置详情接口")
	public List<SysParameter> sysSetDetail(HttpServletRequest request, HttpServletResponse response, SysParameterVO obj) {
		List<SysParameter> sysParameterList = null;
		try {
			sysParameterList = systemMaintainService.sysSetDetail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return sysParameterList;
	}

	/**
	 *
	 * 领导会议设置.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/leaderMetSet")
	@ResponseBody
	@ApiOperation(value = "领导会议设置接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "paramKey",value = "领导会议参数Key(默认传值:leader_meeting_member)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "paramValue",value = "领导会议参数value(保存用户ID多个用户分号分隔)",required = false,dataType = "string",paramType = "query")
	})
	public RetMsg leaderMetSet(HttpServletRequest request, HttpServletResponse response, SysParameter obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = systemMaintainService.leaderMetSet(obj);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return  retMsg;
	}

	/**
	 *
	 * 领导会议设置详情.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/leaderMetSetDetail")
	@ResponseBody
	@ApiOperation(value = "领导会议设置详情接口")
	public SysParameterVO leaderMetSetDetail(HttpServletRequest request, HttpServletResponse response, SysParameter obj) {
		SysParameterVO sysParameter= null;
		try {
			sysParameter = systemMaintainService.leaderMetSetDetail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return sysParameter;
	}

	/**
	 *
	 * 系统设置(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping(value = "/smaSetPage",method = RequestMethod.GET)
	@ApiOperation("系统设置页面接口")
	public String sma1AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
		return "sma/smaSetPage";
	}

	/**
	 *
	 * 短信管理(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping(value = "/smaSmsdPage",method = RequestMethod.GET)
	@ApiOperation("短信管理页面接口")
	public String sma2AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
		return "sma/smaSmsdPage";
	}

	/**
	 *
	 * 领导看板管理(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping(value = "/smaLeaderPage",method = RequestMethod.GET)
	@ApiOperation("领导看板管理页面接口")
	public String sma4AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
		return "sma/smaLeaderPage";
	}

	/**
	 *
	 * 领导会议(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping(value = "/smaLeadmetPage",method = RequestMethod.GET)
	@ApiOperation("领导会议页面接口")
	public String sma5AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
		return "sma/smaLeadmetPage";
	}

	/**
	 *
	 * 考勤打卡设置(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping(value = "/smaCheckPage",method = RequestMethod.GET)
	@ApiOperation("考勤打卡设置页面接口")
	public String sma6AljoinBpmnPage(HttpServletRequest request, HttpServletResponse response) {
		return "sma/smaCheckPage";
	}

	/**
	 *
	 * 系统设置.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping(value = "/attCardSet",method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "考勤打卡设置接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params['am_allow_late']",value = "上午允许迟到时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_leave_early']",value = "上午允许早退时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_allow_late']",value = "下午允许迟到时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_leave_early']",value = "下午允许早退时间(分钟)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['am_signin_popup_time']",value = "上午签到弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['am_signin_popup_endtime']",value = "上午签到结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_signout_popup_time']",value = "上午签退弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['am_signout_popup_endtime']",value = "上午签退结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['pm_signin_popup_time']",value = "下午签到开始弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['pm_signin_popup_endtime']",value = "下午签到结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_signout_popup_time']",value = "下午签退开始弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['pm_signout_popup_endtime']",value = "下午签退结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['am_work_punch_time']",value = "上午上班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_work_punch_begtime']",value = "上午上班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_work_punch_endtime']",value = "上午上班结束打卡时间",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['am_offwork_punch_time']",value = "上午下班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_offwork_punch_begtime']",value = "上午下班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_offwork_punch_endtime']",value = "上午下班结束打卡时间",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['pm_work_punch_time']",value = "下午上班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_work_punch_begtime']",value = "下午上班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_work_punch_endtime']",value = "下午上班结束打卡时间",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['pm_offwork_punch_time']",value = "下午下班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_offwork_punch_begtime']",value = "下午下班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_offwork_punch_endtime']",value = "下午下班结束打卡时间",required = false,dataType = "string",paramType = "query")

	})
	public RetMsg attCardSet(HttpServletRequest request, HttpServletResponse response, SysParameterVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = systemMaintainService.attCardSet(obj);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return  retMsg;
	}

	/**
	 *
	 * 考勤打卡设置详情.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/attCardSetDetail")
	@ResponseBody
	@ApiOperation(value = "考勤打卡设置详情接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "params['am_allow_late']",value = "上午允许迟到时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_leave_early']",value = "上午允许早退时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_allow_late']",value = "下午允许迟到时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_leave_early']",value = "下午允许早退时间(分钟)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['am_signin_popup_time']",value = "上午签到弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['am_signin_popup_endtime']",value = "上午签到结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_signout_popup_time']",value = "上午签退弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['am_signout_popup_endtime']",value = "上午签退结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['pm_signin_popup_time']",value = "下午签到弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['pm_signin_popup_endtime']",value = "下午签到结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_signout_popup_time']",value = "下午签退弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),
			//@ApiImplicitParam(name = "params['pm_signout_popup_endtime']",value = "下午签退结束弹窗时间(分钟)",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['am_work_punch_time']",value = "上午上班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_work_punch_begtime']",value = "上午上班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_work_punch_endtime']",value = "上午上班结束打卡时间",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['am_offwork_punch_time']",value = "上午下班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_offwork_punch_begtime']",value = "上午下班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['am_offwork_punch_endtime']",value = "上午下班结束打卡时间",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['pm_work_punch_time']",value = "下午上班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_work_punch_begtime']",value = "下午上班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_work_punch_endtime']",value = "下午上班结束打卡时间",required = false,dataType = "string",paramType = "query"),

			@ApiImplicitParam(name = "params['pm_offwork_punch_time']",value = "下午下班打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_offwork_punch_begtime']",value = "下午下班开始打卡时间",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "params['pm_offwork_punch_endtime']",value = "下午下班结束打卡时间",required = false,dataType = "string",paramType = "query")

	})
	public List<SysParameter> attCardSetDetail(HttpServletRequest request, HttpServletResponse response, SysParameterVO obj) {
		List<SysParameter> sysParameterList = null;
		try {
			sysParameterList = systemMaintainService.attCardSetDetail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return sysParameterList;
	}
	
	/**
	 *
	 * 不参与考勤统计人员设置.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/attCountPersonSet")
	@ResponseBody
	@ApiOperation(value = "不参与考勤统计人员设置接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "paramValue",value = "不参与考勤统计人员id（分号分割）", required = true,dataType = "string",paramType = "query")
	})
	public RetMsg attCountPersonSet(HttpServletRequest request, HttpServletResponse response, SysParameterVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = systemMaintainService.addOrUpdateAttcountPerson(obj);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return  retMsg;
	}
	
	/**
	 *
	 * 不参与考勤统计人员详情.
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-18
	 */
	@RequestMapping(value = "/attCountPersonDetail")
	@ResponseBody
	@ApiOperation(value = "不参与考勤统计人员详情接口")
	public RetMsg attCountPersonDetail(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			SysParameterVO value = systemMaintainService.attCountPersonDetail(null);
			retMsg.setCode(1);
			retMsg.setObject(value);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return  retMsg;
	}
}
