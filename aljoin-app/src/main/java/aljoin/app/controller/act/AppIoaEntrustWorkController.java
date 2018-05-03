package aljoin.app.controller.act;

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

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.dao.object.ActAljoinDelegateVO;
import aljoin.act.dao.object.AllTaskDataShowVO;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.app.controller.BaseController;
import aljoin.app.task.WebTask;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.service.act.ActAljoinDelegateWebService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 委托工作
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "app/ioa/ioaEntruestWork", method = RequestMethod.POST)
@Api(value = "委托工作Controller", description = "流程->委托工作")
public class AppIoaEntrustWorkController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(AppIoaEntrustWorkController.class);
	@Resource
	private ActAljoinDelegateService actAljoinDelegateService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActAljoinDelegateWebService actAljoinDelegateWebService;
	@Resource
	private WebTask webTask;

	/**
	 * 
	 * 委托任务列表(分页列表).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/list")
	@ResponseBody // 返回json
	@ApiOperation(value = "委托任务列表(分页列表)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "delegateStatus", value = "1-未开始（当前时间少于开发时间），2-代理中（时间内），3-已结束（时间结束），4-已终止（人为终止）", required = false, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "begTime", value = "查询开始时间  yyyy-MM-yy hh:mm:ss", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "查询开始时间  yyyy-MM-yy hh:mm:ss", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "assigneeUserFullnames", value = "查询受理人", required = false, dataType = "string", paramType = "query") })
	public RetMsg list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ActAljoinDelegateVO obj) {
		RetMsg retMsg = new RetMsg();
		Page<ActAljoinDelegateVO> page = new Page<ActAljoinDelegateVO>();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			page = actAljoinDelegateService.getAllEntrustWork(pageBean, obj, userId);
			retMsg.setObject(page);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 委托任务列表(添加委托任务).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/add")
	@ResponseBody
	@ApiOperation(value = "委托任务列表(添加委托任务)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "assigneeUserIds", value = "选择1个或多个用户id", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "assigneeUserNames", value = "选择1个或多个用户名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "assigneeUserFullnames", value = "选择1个或多个用户昵称", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "begTime", value = "委托开始时间   yyyy-MM-yy hh:mm:ss", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "委托结束时间   yyyy-MM-yy hh:mm:ss", required = true, dataType = "date", paramType = "query"),
			@ApiImplicitParam(name = "delegateDesc", value = "委托说明(150)", required = false, dataType = "String", paramType = "query") })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateVO obj) {

		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			obj.setOwnerUserId(userId.toString());
			AutUser autUser = autUserService.selectById(userId);
			obj.setOwnerUserName(autUser.getUserName());
			obj.setOwnerUserFullname(autUser.getFullName());
			// actAljoinDelegateWebService.add(obj);
			// 由于执行新增委托时，有可能时间很长，所以用异步线程处理
			// 插入业务表数据
			ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.add(obj);
			if (null != actAljoinDelegate) {
				// 进行真正的委托处理
				webTask.addDelegateBiz(actAljoinDelegate);
			}
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 委托任务列表(中止委托).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/stopTask")
	@ResponseBody
	@ApiOperation(value = "委托任务列表(中止委托)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "委托任务ID", required = true, dataType = "long", paramType = "query") })
	public RetMsg stopTask(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateVO obj) {

		RetMsg retMsg = new RetMsg();
		try {
			// AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			// Long userId = autAppUserLogin.getUserId();
			// AutUser user = autUserService.selectById(userId);
			// actAljoinDelegateWebService.appStopDelegateBiz(actAljoinDelegate,
			// user, false);
			ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(obj.getId());
			// 删除委托数据
			actAljoinDelegateWebService.stopDelegateBiz(actAljoinDelegate, true);
			// 在异步线程中终止委托
			webTask.stopDelegateBiz(actAljoinDelegate, 2);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 委托工作列表(分页).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-09-20
	 */
	@RequestMapping("/workList")
	@ResponseBody
	@ApiOperation(value = "委托工作列表(分页列表)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query"),
			@ApiImplicitParam(name = "begTime", value = "查询开始时间  yyyy-MM-yy hh:mm:ss", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "endTime", value = "查询开始时间  yyyy-MM-yy hh:mm:ss", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "assigneeUserFullnames", value = "标题", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "委托任务ID", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "delegateDesc", value = "表单类型ID", required = false, dataType = "string", paramType = "query")

	})
	public RetMsg workList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ActAljoinDelegateVO obj) {
		RetMsg retMsg = new RetMsg();
		Page<AllTaskDataShowVO> page = new Page<AllTaskDataShowVO>();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();
			page = actAljoinDelegateService.getAllEntrustWorkData(pageBean, obj, userId);
			retMsg.setObject(page);
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(AppConstant.RET_CODE_BIZ_ERR);
			retMsg.setMessage("操作失败：" + e.getMessage());
			e.printStackTrace();
			logger.error("", e);
		}
		return retMsg;

	}

	/**
	 * 
	 * 任务委托表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-10-24
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@ApiOperation(value = "委托工作列表(删除)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "id", value = "委托记录ID", required = true, dataType = "string", paramType = "query") })
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, String id) {
		RetMsg retMsg = new RetMsg();

		try {

			Long delegateId = Long.parseLong(id);
			ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(delegateId);
			// 对于3-已结束（时间结束），4-已终止（人为终止）'的数据,只需要删除本表数据即可
			if (actAljoinDelegate.getDelegateStatus().intValue() == 3
					|| actAljoinDelegate.getDelegateStatus().intValue() == 4) {
				actAljoinDelegateService.deleteById(actAljoinDelegate.getId());
			} else {
				actAljoinDelegateWebService.stopDelegateBiz(actAljoinDelegate, true);
			}
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("删除成功");
		} catch (Exception e) {
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage("删除失败");
			logger.error("", e);
		}
		return retMsg;
	}
}
