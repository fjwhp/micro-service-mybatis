package aljoin.web.controller.goo;

import java.util.HashMap;
import java.util.List;
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
import aljoin.goo.dao.entity.GooPurchase;
import aljoin.goo.dao.object.GooPurchaseVO;
import aljoin.goo.iservice.GooPurchaseService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.goo.GooPurchaseWebService;
import aljoin.web.task.WebTask;

/**
 * 
 * 办公用品申购信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
@Controller
@RequestMapping("/goo/gooPurchase")
public class GooPurchaseController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(GooPurchaseController.class);
	@Resource
	private GooPurchaseService gooPurchaseService;
	@Resource
	private GooPurchaseWebService gooPurchaseWebService;
	@Resource
	private WebTask webTask;
	
	/**
	 * 
	 * 办公用品申购信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/gooPurchasePage")
	public String gooPurchasePage(HttpServletRequest request,HttpServletResponse response) {
		
		return "goo/gooPurchasePage";
	}
	
	/**
	 * 
	 * 办公用品申购信息表(分页列表).
	 *
	 * @return：Page<GooPurchase>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<GooPurchase> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, GooPurchase obj) {
		Page<GooPurchase> page = null;
		try {
			page = gooPurchaseService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 办公用品申购信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, GooPurchaseVO obj) {
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
          retMsg = gooPurchaseWebService.add(obj,autuser);
      } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
      }
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品申购信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, GooPurchase obj) {
		RetMsg retMsg = new RetMsg();

		gooPurchaseService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品申购信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, GooPurchase obj) {
		RetMsg retMsg = new RetMsg();

		GooPurchase orgnlObj = gooPurchaseService.selectById(obj.getId());
		// orgnlObj.set...

		gooPurchaseService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 办公用品申购信息表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public List<GooPurchase> getById(HttpServletRequest request, HttpServletResponse response, GooPurchaseVO obj) {
	  List<GooPurchase> gooPurchase = null;

      try {
        gooPurchase = gooPurchaseService.getById(obj);
      } catch (Exception e) {
        logger.error("", e);
      }
      return gooPurchase;
	}

	/**
	 * 
	 * 申购完成任务
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月5日 上午9:01:48
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
            retMsg = gooPurchaseWebService.completeTask(variables, taskId, bizId, userId, message, autUser);
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
	 * 办公用品申购流程作废
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月24日 上午10:12:10
	 */
	@RequestMapping(value = "/void")
    @ResponseBody
    public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId,String bizId) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = gooPurchaseService.toVoid(taskId,bizId,customUser.getUserId());
        }catch (Exception e){
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
}
