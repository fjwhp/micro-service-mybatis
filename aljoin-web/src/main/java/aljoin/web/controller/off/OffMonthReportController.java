package aljoin.web.controller.off;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.dao.object.OffMonthReportDO;
import aljoin.off.dao.object.OffMonthReportVO;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.web.controller.BaseController;
import aljoin.web.service.off.OffMonthReportWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 工作月报表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-11
 */
@Controller
@RequestMapping(value = "/off/offMonthReport",method = RequestMethod.POST)
@Api(value = "工作月报Controller",description = "工作计划->工作报表->工作月报相关接口")
public class OffMonthReportController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(OffMonthReportController.class);
	@Resource
	private OffMonthReportService offMonthReportService;
	@Resource
	private OffMonthReportWebService offMonthReportWebService;
	@Resource
	private WebTask webTask;
	
	/**
	 * 
	 * 工作月报表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/offMonthReportPage",method = RequestMethod.GET)
	@ApiOperation("工作月报页面")
	public String offMonthReportPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "off/offMonthReportPage";
	}
	
	/**
	 * 
	 * 工作月报表(分页列表).
	 *
	 * @return：Page<OffMonthReport>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation("工作月报分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "status",value = "状态",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "month",value = "月份",required = false,dataType = "string",paramType = "query")

	})
	public Page<OffMonthReportDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffMonthReport obj) {
		Page<OffMonthReportDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			page = offMonthReportService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 工作月报表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, OffMonthReport obj) {
		RetMsg retMsg = new RetMsg();
		
		// obj.set...

		offMonthReportService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 工作月报表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, OffMonthReport obj) {
		RetMsg retMsg = new RetMsg();

		offMonthReportService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 工作月报表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, OffMonthReport obj) {
		RetMsg retMsg = new RetMsg();

		OffMonthReport orgnlObj = offMonthReportService.selectById(obj.getId());
		// orgnlObj.set...

		offMonthReportService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 工作月报表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation("工作月报详情")
	@ApiImplicitParam(name = "id",value = "主键ID",required = true,dataType = "long",paramType = "query")
	public OffMonthReportVO getById(HttpServletRequest request, HttpServletResponse response, OffMonthReport obj) {
		OffMonthReportVO monthReportVO = null;
		try {
			obj.setComment("月报可编辑");//这种情况不查附件
			monthReportVO = offMonthReportService.detail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return monthReportVO;
	}

	/**
	 *
	 * 已提交月报详情(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/getSubmitById")
	@ResponseBody
	@ApiOperation("已提交月报详情")
	@ApiImplicitParam(name = "id",value = "主键ID",required = true,dataType = "long",paramType = "query")
	public OffMonthReportVO getSubmitById(HttpServletRequest request, HttpServletResponse response, OffMonthReport obj) {
		OffMonthReportVO monthReportVO = null;
		try {
			monthReportVO = offMonthReportService.submitDetail(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return monthReportVO;
	}

	/**
	 *
	 * 工作月报提交.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/submit")
	@ResponseBody
	@ApiOperation("工作月报提交")
	public RetMsg submit(HttpServletRequest request, HttpServletResponse response, OffMonthReport obj) {
		RetMsg retMsg = new RetMsg();
		try {
		    obj.setCreateUserId(getCustomDetail().getUserId());
			retMsg = offMonthReportWebService.submit(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 已收月报(分页列表).
	 *
	 * @return：Page<OffMonthReport>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/recevieList")
	@ResponseBody
	@ApiOperation("已收月报分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "month",value = "月份",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "begTime",value = "开始提交时间",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "endTime",value = "结束提交时间",required = false,dataType = "date",paramType = "query")

	})
	public Page<OffMonthReportDO> recevieList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffMonthReportVO obj) {
		Page<OffMonthReportDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			page = offMonthReportService.recevieList(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 月报管理(分页列表).
	 *
	 * @return：Page<OffMonthReport>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/managerList")
	@ResponseBody
	@ApiOperation("月报管理分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "标题",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "month",value = "月份",required = false,dataType = "string",paramType = "query")

	})
	public Page<OffMonthReportDO> managerList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffMonthReport obj) {
		Page<OffMonthReportDO> page = null;
		try {
			page = offMonthReportService.managerList(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	
	/**
	 *
	 * 工作月报保存.
	 *
	 * @return：RetMsg
	 *
	 * @author：sln
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping(value = "/save")
	@ResponseBody
	@ApiOperation("工作月报提交")
	public RetMsg save(HttpServletRequest request, HttpServletResponse response, OffMonthReportVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			retMsg = offMonthReportService.updateMonthReport(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 *
	 * 审批
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月7日 下午2:04:43
	 */
	@RequestMapping("/completeTask")
	@ResponseBody
	@ApiOperation("完成任务")
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables, String taskId, String bizId,String userId,String message) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = offMonthReportWebService.completeTask(variables,taskId,bizId,userId,user,message);
			if(retMsg.getObject()!=null){
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, user);
				}
				retMsg.setObject(null);
			}
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 根据任务ID查询流程是否被签收
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/isClaim")
	@ResponseBody
	@ApiOperation("根据任务ID查询流程是否被签收")
	public RetMsg isClaim(HttpServletRequest request, HttpServletResponse response, String taskId) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = offMonthReportService.isClaim(taskId);
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 根据任务ID查询下一个流程节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/getNextTaskInfo",method = RequestMethod.GET)
	@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query")
	@ResponseBody
	@ApiOperation("根据任务ID查询下一个流程节点信息")
	public RetMsg getNextTaskInfo(String taskId){
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = offMonthReportService.getNextTaskInfo(taskId);
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

   /**
    *
    * 检查当前节点下一个节点是显示组织机构还是人员
    *
    * @return：RetMsg
    *
    * @author：wangj
    *
    * @date：2017-11-06
    */
   @RequestMapping(value = "/checkNextTaskInfo")
   @ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query")
   @ResponseBody
   @ApiOperation("检查当前节点下一个节点是显示组织机构还是人员")
   public RetMsg checkNextTaskInfo(String taskId){
       RetMsg retMsg = new RetMsg();
       try {
           retMsg = offMonthReportService.checkNextTaskInfo(taskId);
       }catch (Exception e){
           logger.error("", e);
           retMsg.setCode(1);
           retMsg.setMessage("操作失败");
       }
       return retMsg;
   }

	/**
	 *
	 * 填写意见
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-06
	 */
	@RequestMapping(value = "/addComment")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "message",value = "意见",required = true,dataType = "int",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("填写意见")
	public RetMsg addComment(String taskId,String message){
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = offMonthReportService.addComment(taskId,message);
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 输出流程图
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value="/showImg",method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query")
	})
	@ApiOperation("流程图")
	public void showImg(HttpServletRequest request, HttpServletResponse response, String taskId) {
		try {
			offMonthReportService.showImg(request,response,taskId);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value="/getAllTaskInfo",method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllTaskInfo2(String taskId,String processName,String processInstanceId){
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = offMonthReportService.getAllTaskInfo(taskId,processName,processInstanceId);
		}catch (Exception e){
			logger.error("月报流程日志出错", e);
		}
		return list;
	}

	/**
	 *
	 * 获得上一节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/getPreTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("获得上一节点信息")
	public RetMsg getPreTaskInfo(String taskId){
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = offMonthReportService.getPreTaskInfo(taskId);
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 回退
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/jump2Task2")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "bizId",value = "业务主键ID(多个用分分隔)",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("回退")
	public RetMsg jump2Task2(String taskId,String bizId,String message){
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			AutUser user = new AutUser();
			user.setFullName(customUser.getNickName());
			user.setId(customUser.getUserId());
			user.setUserName(customUser.getUsername());
			retMsg = offMonthReportWebService.jump2Task2(taskId,bizId,message,user);
			if(retMsg.getObject()!=null){
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//其实这里可以不要判断，退回肯定有下级办理人，不然退给谁
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, user);
				}
				retMsg.setObject(null);
			}
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 公共信息流程作废.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/void")
	@ResponseBody
	@ApiOperation("公共信息流程作废")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query")
	})
	public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId,String bizId) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			retMsg = offMonthReportService.toVoid(taskId,bizId,customUser.getUserId());
		}catch (Exception e){
			logger.error("",e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	/**
	 *
	 * 流程日志(流程实例)
	 *
	 * @return：void
	 *
	 * @author：huangw
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value="/getAllPinsInfo",method = RequestMethod.GET)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "流程实例ID",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllPinsInfo(String taskId,String processName){
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = offMonthReportService.getAllTaskInfo(null,processName,taskId);
			if (list != null && list.size() > 0) {
				boolean flag = true;
				while (flag) {
					FixedFormProcessLog tmpDo = list.get(0);
					for (int i = 0; i < list.size() - 1; i++) {// 冒泡趟数，n-1趟
						for (int j = 0; j < list.size() - i - 1; j++) {
							if (list.get(j + 1).getOperationTime().getTime() < list.get(j).getOperationTime()
									.getTime()) {
								tmpDo = list.get(j);
								list.set(j, list.get(j + 1));
								list.set(j + 1, tmpDo);
								flag = true;
							}
						}

					}
					if (!flag) {
						break;// 若果没有发生交换，则退出循环
					}
					flag = false;
				}
			
				FixedFormProcessLog tmplog=list.get(list.size()-1);
				tmplog.setDirection(tmplog.getDirection()+" ----> 归档");
				list.set(list.size()-1, tmplog);	
			}
			//list = offMonthReportService.getAllPinsInfo(taskId,processName);
		}catch (Exception e){
			logger.error("", e);
		}
		return list;
	}
}
