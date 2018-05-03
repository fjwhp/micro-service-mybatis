package aljoin.web.controller.veh;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.veh.dao.entity.VehUse;
import aljoin.veh.dao.object.VehUseVO;
import aljoin.veh.iservice.VehUseService;
import aljoin.web.controller.BaseController;
import aljoin.web.service.veh.VehUseWebService;
import aljoin.web.task.WebTask;

/**
 * 
 * 车船使用申请信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
@Controller
@RequestMapping("/veh/vehUse")
public class VehUseController extends BaseController {

  private final static Logger logger = LoggerFactory.getLogger(VehUseController.class);
	@Resource
	private VehUseService vehUseService;
	@Resource
	private VehUseWebService vehUseWebService;
	@Resource
	private WebTask webTask;
	
	/**
	 * 
	 * 车船使用申请信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/vehUsePage")
	public String vehUsePage(HttpServletRequest request,HttpServletResponse response) {
		
		return "veh/vehUsePage";
	}
	
	/**
	 * 
	 * 车船使用申请信息表(分页列表).
	 *
	 * @return：Page<VehUse>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<VehUse> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, VehUse obj) {
		Page<VehUse> page = null;
		try {
			page = vehUseService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 车船使用申请信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, VehUse obj) {
		RetMsg retMsg = new RetMsg();
	      try {
	        CustomUser user = getCustomDetail();
	        AutUser autuser = null;
	        if (null != user && null != user.getUserId()) {
	            obj.setCreateUserId(user.getUserId());
	            autuser = new AutUser();
	            autuser.setId(user.getUserId());
	            autuser.setFullName(user.getNickName());
	        }
	        retMsg = vehUseWebService.add(obj,autuser);
	    } catch (Exception e) {
	        logger.error("", e);
	        retMsg.setCode(1);
	        retMsg.setMessage("操作失败");
	    }
		return retMsg;
	}
	
	/**
	 * 
	 * 车船使用申请信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, VehUse obj) {
		RetMsg retMsg = new RetMsg();

		vehUseService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 车船使用申请信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, VehUse obj) {
		RetMsg retMsg = new RetMsg();

		VehUse orgnlObj = vehUseService.selectById(obj.getId());
		// orgnlObj.set...

		vehUseService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 车船使用申请信息表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public VehUseVO getById(HttpServletRequest request, HttpServletResponse response, VehUse obj) {
		return vehUseService.getById(obj.getId());
	}
	
	/**
	 * 
	 * 完成车船使用申请任务
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月9日 下午4:03:01
	 */
	@RequestMapping("/completeTask")
    @ResponseBody
    public RetMsg completeTask(HttpServletRequest request, HttpServletResponse response, Map<String, Object> variables,
            String taskId, String bizId, String userId, String message) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser user = getCustomDetail();
            AutUser autUser = new AutUser();
            autUser.setId(user.getUserId());
            autUser.setFullName(user.getNickName());
            autUser.setUserName(user.getUsername());
            retMsg = vehUseWebService.completeTask(variables, taskId, bizId, userId, message, autUser);
            if (retMsg.getObject() != null) {
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) retMsg.getObject();
                String handle = map.get("handle");
                if (handle != null && StringUtils.isNotEmpty(handle)) {// 当下一级办理人不为空时发送待办消息
                    String processInstanceId = map.get("processInstanceId");
                    webTask.sendOnlineMsg(processInstanceId, handle, autUser);
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
	 * 用车申请流程作废
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月24日 上午10:17:41
	 */
	@RequestMapping(value = "/void")
    @ResponseBody
    public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId,String bizId) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = vehUseService.toVoid(taskId,bizId,customUser.getUserId());
        }catch (Exception e){
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

}
