package aljoin.web.controller.ass;

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

import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.ass.dao.entity.AssProcess;
import aljoin.ass.dao.object.AllTaskShowVO;
import aljoin.ass.dao.object.AssProcessVO;
import aljoin.ass.iservice.AssProcessService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.ass.AssProcessWebService;
import aljoin.web.task.WebTask;

/**
 * 
 * 固定财产流程表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-12
 */
@Controller
@RequestMapping("/ass/assProcess")
public class AssProcessController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AssProcessController.class);
	@Resource
	private AssProcessService assProcessService;
	@Resource
	private AssProcessWebService assProcessWebService;
	@Resource
	private WebTask webTask;
	
	/**
	 * 
	 * 固定财产流程表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-12
	 */
	@RequestMapping("/assProcessPage")
	public String assProcessPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "ass/assProcessPage";
	}
	
	/**
	 * 
	 * 固定财产流程表(分页列表).
	 *
	 * @return：Page<AssProcess>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-12
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AssProcess> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AssProcess obj) {
		Page<AssProcess> page = null;
		try {
			page = assProcessService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 固定财产流程表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-12
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AssProcessVO obj) {
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
	        retMsg = assProcessWebService.add(obj,autuser);
	    } catch (Exception e) {
	        logger.error("", e);
	        retMsg.setCode(1);
	        retMsg.setMessage("操作失败");
	    }
		return retMsg;
	}
	
	/**
	 * 
	 * 固定财产流程表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-12
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AssProcess obj) {
		RetMsg retMsg = new RetMsg();

		assProcessService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 固定财产流程表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-12
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AssProcess obj) {
		RetMsg retMsg = new RetMsg();

		AssProcess orgnlObj = assProcessService.selectById(obj.getId());
		// orgnlObj.set...

		assProcessService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 固定财产流程表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-12
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AssProcess getById(HttpServletRequest request, HttpServletResponse response, AssProcess obj) {
		return assProcessService.selectById(obj.getId());
	}

	/**
	 * 
	 * 固定资产流程完成任务
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月12日 下午3:34:52
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
            retMsg = assProcessWebService.completeTask(variables, taskId, bizId, userId, message, autUser);
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
	 * 固定流程作废
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月24日 上午10:23:21
	 */
	@RequestMapping(value = "/void")
    @ResponseBody
    public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId,String bizId) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = assProcessService.toVoid(taskId,bizId,customUser.getUserId());
        }catch (Exception e){
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
	
	/**
	 * 
	 * 获取流程id
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月24日 下午4:49:27
	 */
	@RequestMapping(value = "/getBpmnId")
    @ResponseBody
    public RetMsg getBpmnId(HttpServletRequest request, HttpServletResponse response,AssProcessVO obj) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = assProcessService.getBpmnId(obj);
        }catch (Exception e){
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
	
	/**
	 * 
	 * 固定资产流程列表
	 *
	 * @return：Page<AllTaskShowVO>
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月24日 下午5:09:26
	 */
	@RequestMapping("/getAllTask")
	  @ResponseBody
	  public Page<ActAljoinQueryHis> getAllTask(HttpServletRequest request, HttpServletResponse response,
	      PageBean pageBean, AllTaskShowVO obj, String startTime, String endTime, String name, String title) {
	    Page<ActAljoinQueryHis> page = new Page<ActAljoinQueryHis>();
	    try {
	      Map<String, String> map = new HashMap<String, String>();
	      CustomUser customUser = getCustomDetail();
	      if (customUser == null) {
	        return page;
	      }
	      map.put("userId", customUser.getUserId().toString());
	      map.put("startTime", startTime);
	      map.put("endTime", endTime);
          map.put("name", name);
          map.put("title", title);
	      page = assProcessService.getAllTask(pageBean, obj, map);
	    } catch (Exception e) {
	      logger.error("", e);
	    }
	    return page;
	  }
	
	/**
	 * 
	 * 获取businessKey 
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月25日 上午9:38:51
	 */
	@RequestMapping(value = "/getBusinessKey")
    @ResponseBody
    public RetMsg getBusinessKey(HttpServletRequest request, HttpServletResponse response,String processInstanceId) {
        RetMsg retMsg = new RetMsg();
        try {
            retMsg = assProcessService.getBusinessKey(processInstanceId);
        }catch (Exception e){
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
}
