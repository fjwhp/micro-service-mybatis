package aljoin.web.controller.att;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.service.ActFixedFormServiceImpl;
import aljoin.att.dao.entity.AttSignInOut;
import aljoin.att.dao.object.AttSignInCount;
import aljoin.att.dao.object.AttSignInOutHisVO;
import aljoin.att.dao.object.AttSignInOutVO;
import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;
import aljoin.web.controller.BaseController;
import aljoin.web.service.att.AttSignInOutWebService;
import aljoin.web.task.WebTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 签到、退表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping(value = "/att/attSignInOut",method = RequestMethod.POST)
@Api(value = "签到Controller",description = "考勤管理->签到签退相关接口")
public class AttSignInOutController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AttSignInOutController.class);
	@Resource
	private AttSignInOutService attSignInOutService;
	@Resource
	private AttSignInOutWebService attSignInOutWebService;
	@Resource
	private SysDataDictService sysDataDictService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private WebTask webTask;
	@Resource
	private ActFixedFormServiceImpl actFixedFormService;
	@Resource
	private AutDepartmentService autDepartmentService;
	/**
	 * 
	 * 签到、退表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping(value = "/attSignInOutPage",method = RequestMethod.GET)
	@ApiOperation(value = "个人签到详细页面跳转接口")
	public String attSignInOutPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "att/attSignInOutPage";
	}
	
	/**
	 * 
	 * 签到、退表(分页列表).
	 *
	 * @return：Page<AttSignInOut>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	@ApiOperation(value = "个人签到分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "thisWeek",value = "本周(搜索条件：默认传该值)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "thisMonth",value = "本月(搜索条件)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "lastWeek",value = "上周(搜索条件)",required = false,dataType = "date",paramType = "query"),
			@ApiImplicitParam(name = "nextWeek",value = "下周(搜索条件)",required = false,dataType = "date",paramType = "query")
	})
	public Page<AttSignInOut> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AttSignInOutVO obj) {
		Page<AttSignInOut> page = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			page = attSignInOutService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 签到、退表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("定时生成当月考勤数据接口")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			attSignInOutService.deleteLastSignInOut();
			retMsg = attSignInOutService.add();
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 签到、退表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AttSignInOut obj) {
		RetMsg retMsg = new RetMsg();

		attSignInOutService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 签到、退表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AttSignInOut obj) {
		RetMsg retMsg = new RetMsg();

		AttSignInOut orgnlObj = attSignInOutService.selectById(obj.getId());
		// orgnlObj.set...

		attSignInOutService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 签到、退表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AttSignInOut getById(HttpServletRequest request, HttpServletResponse response, AttSignInOut obj) {
		return attSignInOutService.selectById(obj.getId());
	}

	/**
	 *
	 * @throws Exception 
	 * 考勤补签(管理员-上午补签、下午补签)
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月27日 下午3:18:38
	 */
	@RequestMapping("/signInPatch")
	@ResponseBody
	public RetMsg adminSignInPatch(AttSignInOutVO obj) throws Exception{

		RetMsg retMsg = attSignInOutWebService.signInPatch(obj);
		return retMsg;
	}
	

	/**
	 *
	 * 签到、退表(分页列表).
	 *
	 * @return：Page<AttSignInOut>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping(value = "/signInDetailList")
	@ResponseBody
	@ApiOperation(value = "签到详细分页列表接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "thisMonth",value = "本月(搜索条件—默认传该值(当前月):2017-10)",required = false,dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "lastMonth",value = "上月",required = false,dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "nextMonth",value = "下月(任意不为空的值)",required = false,dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "deptId",value = "部门ID",required = false,dataType = "string",paramType = "query"),
    @ApiImplicitParam(name = "monthBeg",value = "自选月份(如：2017-10)",required = false,dataType = "string",paramType = "query")
	})
	public AttSignInOutVO signInDetailList(HttpServletRequest request, HttpServletResponse response,AttSignInOutVO obj) {
		AttSignInOutVO attSignInOutVO = null;
		try {
			attSignInOutVO = attSignInOutService.signInDetailList(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return attSignInOutVO;
	}

	/**
    *
    * 签到、退表(分页列表).
    *
    * @return：Page<AttSignInOutVO>
    *
    * @author：huanghz
    *
    * @date：2018-01-11
    */
   @RequestMapping(value = "/signInDetailListPage")
   @ResponseBody
   @ApiOperation(value = "签到详细分页列表接口")
   @ApiImplicitParams({
       @ApiImplicitParam(name = "thisMonth",value = "本月(搜索条件—默认传该值(当前月):2017-10)",required = false,dataType = "string",paramType = "query"),
       @ApiImplicitParam(name = "lastMonth",value = "上月",required = false,dataType = "string",paramType = "query"),
       @ApiImplicitParam(name = "nextMonth",value = "下月(任意不为空的值)",required = false,dataType = "string",paramType = "query"),
       @ApiImplicitParam(name = "deptId",value = "部门ID",required = false,dataType = "string",paramType = "query"),
       @ApiImplicitParam(name = "monthBeg",value = "自选月份(如：2017-10)",required = false,dataType = "string",paramType = "query"),
       @ApiImplicitParam(name = "queryYM",value = "查询年月(如：2018-01)",required = false,dataType = "string",paramType = "query")
   })
   public Page<AttSignInOutVO> signInDetailListPage(HttpServletRequest request, HttpServletResponse response,PageBean pageBean,AttSignInOutVO obj) {
     Page<AttSignInOutVO> page = null;
       try {
//         long starttime1 = System.currentTimeMillis();
//         System.out.println(starttime1);
           page = attSignInOutService.signInDetailListPage(pageBean,obj);
//         System.out.println("结束时间耗时："+ (System.currentTimeMillis() - starttime1));
       } catch (Exception e) {
         logger.error("", e);
       }
       
       return page;
   }
   
   
	/**
	 *
	 * 登录成功 给当前登录用户推送打卡消息.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-29
	 */
	@RequestMapping("/pushMassage")
	@ResponseBody
	public RetMsg pushMassage(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = attSignInOutService.pushMessageToUserList(user.getUserId());
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 签到统计
	 *
	 * @return：List<AttSignInCount>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月28日 上午8:43:08
	 */
	@RequestMapping("/getAttSignInCountList")
	@ResponseBody
	public List<AttSignInCount> getAttSignInCountList(AttSignInCount obj) {

		List<AttSignInCount> list = new ArrayList<AttSignInCount>();
		try {
			list = attSignInOutService.getAttSignInCountList(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return list;
	}
	/**
     * 
     * 签到统计
     *
     * @return：List<AttSignInCount>
     *
     * @author：laijy
     *
     * @date：2017年9月28日 上午8:43:08
     */
    @RequestMapping("/getAttSignInHisCountList")
    @ResponseBody
    public List<AttSignInCount> getAttSignInHisCountList(AttSignInCount obj) {

        List<AttSignInCount> list = new ArrayList<AttSignInCount>();
        try {
            list = attSignInOutService.getAttSignInHisCountList(obj);
        } catch (Exception e) {
          logger.error("", e);
        }
        return list;
    }
	
	/**
	 * 
	 * 签到统计页面跳转接口
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月29日 下午2:36:44
	 */
	@RequestMapping(value = "/attSignInOutCountPage",method = RequestMethod.GET )
	public String attSignInOutCountPage(HttpServletRequest request,HttpServletResponse response) {
		return "att/attSignInOutCountPage";
	}

	/**
	 *
	 * 签到、退表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping(value = "/personalSignInList")
	@ResponseBody
	@ApiOperation("个人签到详细列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "thisMonth",value = "本月(任意不为空的值)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "lastMonth",value = "上月(任意不为空的值)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "nextMonth",value = "下月(任意不为空的值)",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "monthBeg",value = "自选月份(如：2017-10)",required = false,dataType = "string",paramType = "query")
	})
	public AttSignInOutVO personalSignInList(HttpServletRequest request, HttpServletResponse response, AttSignInOutVO obj) {
		AttSignInOutVO signInOutVO = null;
		try {
			CustomUser user = getCustomDetail();
			obj.setCreateUserId(user.getUserId());
			signInOutVO = attSignInOutService.personalSignInList(obj);
		}catch (Exception e){
		  logger.error("", e);
		}
		return signInOutVO;
	}


	/**
	 *
	 * 签到、退表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping(value = "/attSignInOutDetailPage",method = RequestMethod.GET)
	@ApiOperation(value = "签到详细页面跳转接口")
	public String attSignInOutDetailPage(HttpServletRequest request,HttpServletResponse response) {

		return "att/attSignInOutDetailPage";
	}
	
	/**
	 * 
	 * 导出Excel
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月12日 上午8:53:00
	 */
	@RequestMapping(value = "/export",method = RequestMethod.GET)
	public void export(HttpServletRequest request, HttpServletResponse response,AttSignInCount obj){
		try {
			attSignInOutService.export(response,obj);
		}catch (Exception e){
		  logger.error("", e);
		}
	}

	/**
	 *
	 * 签到签退
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月24日
	 */
	@RequestMapping(value = "/confirmSign")
	@ApiOperation("签到签退")
	@ResponseBody
	public RetMsg confirmSign(HttpServletRequest request){
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = attSignInOutService.confirmSign(user.getUserId(),request);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	@RequestMapping("/getUserInfo")
	@ResponseBody
	public AttSignInCount getUserInfo() throws Exception{
		
		CustomUser customUser = getCustomDetail();
		AttSignInCount signInCount = attSignInOutService.getUserInfo(customUser.getUserId());
		return signInCount;
		
	}

	/**
	 *
	 * 首页签到签退显示文本
	 *
	 * @return：void
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月24日
	 */
	@RequestMapping(value = "/getSignInOutStr")
	@ApiOperation("首页签到签退显示文本")
	@ResponseBody
	public RetMsg getSignInOutStr(HttpServletRequest request){
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			retMsg = attSignInOutService.getSignInOutStr(user.getUserId(),request);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 获得补签申请人信息(姓名、岗位、申请时间)
	 *
	 * @return：AttSignInCount
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月27日 下午2:17:31
	 */
	@RequestMapping(value = "/getSignPathUserInfo")
	@ResponseBody
	public AttSignInCount getSignPathUserInfo() throws Exception{
		
		CustomUser customUser = getCustomDetail();
		
		AttSignInCount sinPathUserInfo = attSignInOutService.getSignPathUserInfo(customUser.getUserId());
		
		return sinPathUserInfo;
		
	}
	/**
	 *
	 * 获得补签申请信息
	 *
	 * @return：List<AttSignInOutVO>
	 *
	 * @author：wangj
	 *
	 * @date：2017年11月09日
	 */
	@RequestMapping(value = "/getSignInOutPatchList")
	@ResponseBody
	@ApiOperation("获得补签申请信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids",value = "id(多个用分号隔开)",required = false,dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "taskId",value = "任务ID",required = false,dataType = "string",paramType = "query"),
		@ApiImplicitParam(name = "processId",value = "流程实例ID",required = false,dataType = "string",paramType = "query")
	})
	public RetMsg getSignInOutPatchList(AttSignInOutHisVO obj){
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = attSignInOutService.getSignInOutHisPatchList(obj);
		}catch (Exception e){
			logger.error(e+"");
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return  retMsg;
	}
	/**
	 *
	 * 综合查询获取补签申请信息
	 *
	 * @return：List<AttSignInOutVO>
	 *
	 * @author：huangw
	 *
	 * @date：2017年12月09日
	 */
	@RequestMapping(value = "/getSignInOutPatchDataList")
	@ResponseBody
	@ApiOperation("获得补签申请信息")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "ids",value = "id(多个用分号隔开)",required = false,dataType = "string",paramType = "query")
	})
	public List<AttSignInOutHisVO> getSignInOutPatchDataList(AttSignInOutHisVO obj){
		List<AttSignInOutHisVO> list = null;
		try {
			list = attSignInOutService.getSignInOutHisDatePatchList(obj);
		}catch (Exception e){
			logger.error(e+"");
		}
		return  list;
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
	public RetMsg checkNextTaskInfo(String taskId,String nextSteps){
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = attSignInOutService.checkNextTaskInfo(taskId,nextSteps);
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 流程日志
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月11日 下午5:34:17
	 */
	@RequestMapping(value="/getAllTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "任务ID",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllTaskInfo2(String taskId, String processName,String processInstanceId){
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = attSignInOutService.getAllTaskInfo(taskId,processInstanceId);
		}catch (Exception e){
			logger.error("", e);
		}
		return list;
	}
	/**
	 *
	 * 流程日志(实例ID)
	 *
	 * @return：void
	 *
	 * @author：huangw
	 *
	 * @date：2017年12月22日  10:53
	 */
	@RequestMapping(value="/getAllHisTaskInfo")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "taskId",value = "实例ID",required = true,dataType = "string",paramType = "query")
	})
	@ResponseBody
	@ApiOperation("流程日志")
	public List<FixedFormProcessLog> getAllHisTaskInfo(String taskId, String processName){
		List<FixedFormProcessLog> list = new ArrayList<FixedFormProcessLog>();
		try {
			list = attSignInOutService.getAllTaskInfo(null,taskId);
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
		}catch (Exception e){
			logger.error("", e);
		}
		return list;
	}

	/**
	 *
	 * 审批
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017年11月09日
	 */
	@RequestMapping("/completeTask")
	@ResponseBody
	@ApiOperation("完成任务")
	public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables, String taskId, String bizId, String userId,String message,String nextStept) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser user = getCustomDetail();
			if(nextStept == null){
				retMsg.setCode(1);
				retMsg.setMessage("初始化信息失败，请检查信息是否完整");
				return retMsg;
			}
			AutUser autUser = new AutUser();
			autUser.setId(user.getUserId());
			autUser.setFullName(user.getNickName());
			autUser.setUserName(user.getUsername());
			
			variables.put("nextStept", Integer.valueOf(nextStept));
			retMsg = attSignInOutWebService.completeTask(variables,taskId,bizId,userId,message,autUser);
			if(retMsg.getObject()!=null){
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					webTask.sendOnlineMsg(processInstanceId, handle, autUser);
				}
				retMsg.setObject(null);
			}
			retMsg.setObject(null);
		} catch (Exception e) {
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
			CustomUser user = getCustomDetail();
			Long creatUserId = user.getUserId();
			retMsg = attSignInOutWebService.jump2Task2(taskId,bizId,message,creatUserId);
			if(retMsg.getObject()!=null){
				@SuppressWarnings("unchecked")
				HashMap<String,String> map = (HashMap<String, String>) retMsg.getObject();
				String handle = map.get("handle");
				if(handle != null && StringUtils.isNotEmpty(handle)){//当下一级办理人不为空时发送待办消息
					String processInstanceId = map.get("processInstanceId");
					AutUser autUser = new AutUser();
					autUser.setId(user.getUserId());
					autUser.setFullName(user.getNickName());
					autUser.setUserName(user.getUsername());
					webTask.sendOnlineMsg(processInstanceId, handle, autUser);
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
	 * 考勤补签流程作废.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/void")
	@ResponseBody
	@ApiOperation("考勤补签流程作废")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "taskId", value = "任务ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "bizId", value = "主键ID", required = true, dataType = "string", paramType = "query")
	})
	public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId,String bizId) {
		RetMsg retMsg = new RetMsg();
		try {
			CustomUser customUser = getCustomDetail();
			retMsg = attSignInOutService.toVoid(taskId,bizId,customUser.getUserId());
		}catch (Exception e){
			logger.error("",e);
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
			CustomUser user = getCustomDetail();
			Long createUserId = user.getUserId();
			//查询当前用户所在的部门信息
			Where<AutDepartmentUser> userdeptwhere = new Where<AutDepartmentUser>();
            userdeptwhere.setSqlSelect("dept_id,dept_code");
            userdeptwhere.eq("user_id", createUserId);
            List<AutDepartmentUser> autDepartmentUsers = autDepartmentUserService.selectList(userdeptwhere);
            Integer nextStept = 3;// 默认是其他部门分支
            if(autDepartmentUsers.size()>0){
              AutDepartmentUser userDept = autDepartmentUsers.get(0);
              //研究所数据字典
              List<SysDataDict> labDictList = sysDataDictService.getByCode(WebConstant.DICT_ATT_DEPARTMENT_LAB);
              //南方中心-办公室-其他数据字典
              List<SysDataDict> dictList = sysDataDictService.getByCode(WebConstant.DICT_ATT_DEPARTMENT);
              Map<String,SysDataDict> map = new HashMap<String,SysDataDict>();
              Set<Long> deptIdList = new HashSet<Long>(); 
              for (SysDataDict sysDataDict : labDictList) {
                deptIdList.add(Long.valueOf(sysDataDict.getDictKey()));
                map.put(sysDataDict.getDictKey(), sysDataDict);
              }
              for (SysDataDict sysDataDict : dictList) {
                deptIdList.add(Long.valueOf(sysDataDict.getDictKey()));
                map.put(sysDataDict.getDictKey(), sysDataDict);
              }
              if (!deptIdList.isEmpty()) {
                Where<AutDepartment> deptWhere = new Where<AutDepartment>();
                deptWhere.setSqlSelect("id,dept_code");
                deptWhere.in("id", deptIdList);
                List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);
                List<AutDepartment> labDeptList = new ArrayList<AutDepartment>();
                // 先从里面识别出最特殊的：研究所-其他|研究所-办公室
                for (AutDepartment dept : deptList) {
                  for (SysDataDict sysDataDict : labDictList) {
                    if(sysDataDict.getDictKey().equals(dept.getId().toString())){
                      labDeptList.add(dept);
                    }
                  }
                }
                // 筛选出研究所下的子部门
                List<AutDepartment> subOffice = new ArrayList<AutDepartment>();
                if(labDeptList.size()>0){
                  for (AutDepartment autDept : labDeptList) {
                    for (AutDepartment subDept : labDeptList) {
                      if(subDept.getDeptCode().length()<autDept.getDeptCode().length()){
                        if(!subOffice.contains(subDept)){
                          subOffice.add(autDept);
                        }
                      }
                    }
                  }
                }
                // 如果研究所的数据字典有设置
                if(subOffice.size()>0){
                  for (AutDepartment dept : deptList) {
                    for(AutDepartment labOffice : subOffice){
                      if(userDept.getDeptId().equals(labOffice.getId())){
                        nextStept = map.get(labOffice.getId().toString()).getDictRank();
                        break;
                      }else if(userDept.getDeptCode().startsWith(dept.getDeptCode()) || userDept.getDeptCode().equals(dept.getDeptCode())){
                        nextStept = map.get(dept.getId().toString()).getDictRank();
                        break;
                      }
                    }
                  }
                }else{
                  // 如果研究所的数据字典没有设置-不影响基础功能
                  for (AutDepartment dept : deptList) {
                    if(userDept.getDeptCode().startsWith(dept.getDeptCode()) || userDept.getDeptCode().equals(dept.getDeptCode())){
                      nextStept = map.get(dept.getId().toString()).getDictRank();
                      break;
                    }
                  }
                }
              }
            }
			retMsg = attSignInOutService.getNextTaskInfo2(taskId,String.valueOf(nextStept));
		}catch (Exception e){
			logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}
}
