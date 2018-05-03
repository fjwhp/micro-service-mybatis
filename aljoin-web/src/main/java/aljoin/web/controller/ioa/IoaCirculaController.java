package aljoin.web.controller.ioa;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.HistoryService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaCircula;
import aljoin.ioa.dao.entity.IoaCirculaUser;
import aljoin.ioa.dao.object.CirulaDO;
import aljoin.ioa.iservice.IoaCirculaService;
import aljoin.ioa.iservice.IoaCirculaUserService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

/**
 * 
 * 传阅工作
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaCircula", method = RequestMethod.POST)
@Api(value = "传阅工作流程操作Controller", description = "流程->传阅工作")
public class IoaCirculaController extends BaseController {
	private final static Logger logger = LoggerFactory.getLogger(IoaCirculaController.class);
	@Resource
	private IoaCirculaService ioaCirculaService;
	@Resource
	private IoaCirculaUserService ioaCirculaUserService;
	@Resource
	private HistoryService historyService;
	@Resource
	private AutUserService autUserService;
	
	
	/**
	 * 
	 * @描述：(页面).
	 *
	 * @返回：String
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping(value ="/ioaCirculaPage", method = RequestMethod.GET)
	public String ioaCirculaPage(HttpServletRequest request,HttpServletResponse response) {		
		return "ioa/ioaCirculaPage";
	}
	
	/**
	 * 
	 * @描述：传阅(分页列表).
	 *
	 * @返回：Page<AutUserRank>
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/list")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "title", value = "查询的标题", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "founder", value = "查询传阅者姓名", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "flowId", value = "查询的流程ID", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "pageSize", value = "每页数据条数", required = true, dataType = "integer", paramType = "query"),
		@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "integer", paramType = "query"),
		@ApiImplicitParam(name = "flowCategory", value = "查询的流程分类ID", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "urgency", value = "查询的缓急，1：一般 2：紧急 3：加急", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "createTime", value = "查询的开始时间", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "taskFounder", value = "任务创建者姓名", required = false, dataType = "string", paramType = "query") })
	@ResponseBody
	public Page<CirulaDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, CirulaDO obj,String startTime,String endTime) {
		Page<CirulaDO> page = null;
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			if(startTime!=null && !"".equals(startTime)){
				obj.setStartDate(format.parse(startTime));
			}
			if(endTime!=null &&  !"".equals(endTime)){
				obj.setCirculaDate(format.parse(endTime));
			}
			CustomUser customUser = getCustomDetail();
			obj.setCirUserId(customUser.getUserId().toString());
			page = ioaCirculaService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("", e);
		}
		return page;
	}	

	
	/**
	 * 
	 * @描述：传阅明细(根据ID删除对象).用户打开传阅工作后判断是否显示填写传阅意见按钮以及记录第一次打开时间
	 *
	 * @返回：RetMsg
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/openCirculaWork")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query")
		 })
	@ResponseBody
	public RetMsg openCirculaWork(HttpServletRequest request, HttpServletResponse response, IoaCirculaUser obj) {
		RetMsg retMsg = new RetMsg();
		try {
			if(obj.getProcessInstanceId()==null || "".equals(obj.getProcessInstanceId())){
				retMsg.setCode(1);
				retMsg.setMessage("传阅明细记录失败，流程实例ID为空！");
				return retMsg;
			}
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			retMsg=ioaCirculaUserService.add(obj);	
			retMsg.setCode(0);
			retMsg.setMessage("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(1);
			retMsg.setMessage(e.toString());
			logger.error("", e);
		}	
		return retMsg;
	}
	/**
	 * 
	 * @描述：添加传阅意见(已阅，未阅读)
	 *
	 * @返回：RetMsg
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/addCirculaOpinon")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "opinon", value = "传阅意见", required = true, dataType = "string", paramType = "query")
		 })
	@ResponseBody
	public RetMsg addCirculaOption(HttpServletRequest request, HttpServletResponse response, IoaCirculaUser obj) {
		RetMsg retMsg = new RetMsg();
		try {			
			if(obj.getProcessInstanceId()==null || "".equals(obj.getProcessInstanceId())){
				retMsg.setCode(1);
				retMsg.setMessage("添加传阅意见失败，流程实例ID为空！");
				return retMsg;
			}
			CustomUser customUser = getCustomDetail();
			obj.setCreateUserId(customUser.getUserId());
			retMsg=ioaCirculaUserService.addCirculaOpinon(obj);		
		} catch (Exception e) {
			// TODO: handle exception
			retMsg.setCode(1);
			retMsg.setMessage(e.toString());
			logger.error("", e);
		}
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：传阅日志(已阅，未阅读)
	 *
	 * @返回：RetMsg
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/openCirculaLog")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query")
		 })
	@ResponseBody
	public RetMsg openCirculaLog(HttpServletRequest request, HttpServletResponse response, String processInstanceId, String activityId) {
		RetMsg retMsg = new RetMsg();
		try {
		    if(StringUtils.isEmpty(processInstanceId)){
		        HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(activityId).singleResult();
		        if(task != null){
		            processInstanceId = task.getProcessInstanceId();
		        }
		    }
		    if(StringUtils.isEmpty(processInstanceId)){
	            retMsg.setCode(1);
	            retMsg.setMessage("流程参数为空！");
	            return retMsg;
	        }
			retMsg=	ioaCirculaService.openCirculaLog(processInstanceId);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.toString());
			logger.error("", e);
		}		
		return retMsg;
	}
	/**
	 * 
	 * @描述：传阅日志(意见列表)
	 *
	 * @返回：RetMsg
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/openCirculaLogOpinon")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "processInstanceId", value = "流程实例ID", required = true, dataType = "string", paramType = "query")
		 })
	@ResponseBody
	public Page<IoaCirculaUser>  openCirculaLogOpinon(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, String processInstanceId,String activityId) {
		Page<IoaCirculaUser> page = null;
		try {
		    if(StringUtils.isEmpty(processInstanceId)){
                HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(activityId).singleResult();
                if(task != null){
                    processInstanceId = task.getProcessInstanceId();
                }
            }
            if(StringUtils.isEmpty(processInstanceId)){
                throw new Exception("流程参数为空！");
            }
			page = ioaCirculaUserService.list(pageBean, processInstanceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * @描述：传阅(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/delete")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "传阅ID", required = true, dataType = "Long", paramType = "query")
		 })
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, IoaCircula obj) {
		RetMsg retMsg = new RetMsg();
		ioaCirculaService.deleteById(obj.getId());
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：传阅(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/update")	
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "传阅ID", required = true, dataType = "Long", paramType = "query")
		 })
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, IoaCircula obj) {
		RetMsg retMsg = new RetMsg();

		IoaCircula orgnlObj = ioaCirculaService.selectById(obj.getId());
		// orgnlObj.set...

		ioaCirculaService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * @描述：传阅(根据ID获取对象).
	 *
	 * @返回：AutUser
	 *
	 * @作者：zhongjy
	 *
	 * @时间：2017-12-13
	 */
	@RequestMapping("/getById")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "id", value = "传阅ID", required = true, dataType = "Long", paramType = "query")
		 })
	@ResponseBody
	public IoaCircula getById(HttpServletRequest request, HttpServletResponse response, IoaCircula obj) {
		return ioaCirculaService.selectById(obj.getId());
	}
	
	/**
     * 
     * @描述：传阅(根据ID获取对象).
     *
     * @返回：AutUser
     *
     * @作者：zhongjy
     *
     * @时间：2017-12-13
     */
    @RequestMapping("/getCiculatedUserId")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "processInstanceId", value = "流程实例id", required = true, dataType = "Long", paramType = "query")
         })
    @ResponseBody
    public RetMsg getCiculatedUserId(HttpServletRequest request, HttpServletResponse response, String processInstanceId, String taskId) {
        RetMsg retMsg = new RetMsg();
        try {
            if(StringUtils.isEmpty(processInstanceId)){
                if(StringUtils.isEmpty(taskId)){
                    throw new Exception("流程参数为空！");
                }else{
                    HistoricTaskInstance task = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
                    processInstanceId = task.getProcessInstanceId();
                }
            }
            Where<IoaCircula> where = new Where<IoaCircula>();
            where.eq("process_instance_id", processInstanceId);
            where.setSqlSelect("cir_ids");
            List<IoaCircula> ioaCirculayeList = ioaCirculaService.selectList(where);
            HashSet<String> userIdSet = new HashSet<String>();
            for (IoaCircula ioaCircula : ioaCirculayeList) {
                userIdSet.addAll(Arrays.asList(ioaCircula.getCirIds().split(";")));
            }
            List<HistoricTaskInstance> historicTaskInstances =
                historyService.createHistoricTaskInstanceQuery().processInstanceId(processInstanceId).list();
            for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
                if(null != historicTaskInstance.getAssignee()){
                    userIdSet.add(historicTaskInstance.getAssignee()); 
                }
            }
            String userIds  = "";
            if(userIdSet.size()>0){
                Where<AutUser> userWhere = new Where<AutUser>();
                userWhere.setSqlSelect("id,user_name,full_name");
                userWhere.notIn("id",userIdSet);
                List<AutUser> userList = autUserService.selectList(userWhere);
                for(AutUser user : userList){
                    userIds += user.getId() +";";
                }
            }
            retMsg.setObject(userIds);
            retMsg.setCode(1);
        } catch (Exception e) {
            retMsg.setCode(1);
            retMsg.setMessage(e.toString());
            logger.error("", e);
        }
        return retMsg;
    }
	
}
