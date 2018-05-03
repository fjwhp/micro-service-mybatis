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
import aljoin.goo.dao.entity.GooInOut;
import aljoin.goo.dao.object.GooInOutDO;
import aljoin.goo.dao.object.GooInOutVO;
import aljoin.goo.iservice.GooInOutService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import aljoin.web.service.goo.GooInOutWebService;
import aljoin.web.task.WebTask;

/**
 * 
 * 办公用品出入库信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
@Controller
@RequestMapping("/goo/gooInOut")
public class GooInOutController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(GooInOutController.class);
	@Resource
	private GooInOutService gooInOutService;
	@Resource
	private GooInOutWebService gooInOutWebService;
	@Resource
	private WebTask webTask;
	
	/**
	 * 
	 * 办公用品出入库信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/gooInOutPage")
	public String gooInOutPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "goo/gooInOutPage";
	}
	
	/**
	 * 
	 * 办公用品出入库信息表(分页列表).
	 *
	 * @return：Page<GooInOut>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<GooInOut> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, GooInOut obj) {
		Page<GooInOut> page = null;
		try {
			page = gooInOutService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 办公用品出入库信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, GooInOutVO obj) {
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
        retMsg = gooInOutWebService.add(obj,autuser);
    } catch (Exception e) {
        logger.error("", e);
        retMsg.setCode(1);
        retMsg.setMessage("操作失败");
    }
      return retMsg;
	}
	
	/**
	 * 
	 * 办公用品出入库信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, GooInOut obj) {
		RetMsg retMsg = new RetMsg();

		gooInOutService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品出入库信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, GooInOut obj) {
		RetMsg retMsg = new RetMsg();

		GooInOut orgnlObj = gooInOutService.selectById(obj.getId());
		// orgnlObj.set...

		gooInOutService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 办公用品出入库信息表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public List<GooInOut> getById(HttpServletRequest request, HttpServletResponse response, GooInOutVO obj) {
	  List<GooInOut> gooInOut = null;

      try {
        gooInOut = gooInOutService.getById(obj);
      } catch (Exception e) {
        logger.error("", e);
      }
      return gooInOut;
	}

	/**
	 * 
	 * 出入库完成任务
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月5日 上午10:00:36
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
            retMsg = gooInOutWebService.completeTask(variables, taskId, bizId, userId, message, autUser);
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
	 * 获取当前登录信息（时间 姓名 部门）
	 *
	 * @return：GooInOut
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月11日 上午10:28:03
	 */
	@RequestMapping("/getCurrent")
    @ResponseBody
    public GooInOutDO getCurrent(HttpServletRequest request, HttpServletResponse response) {
      AutUser autUser = null;
	  try {
	      CustomUser user = getCustomDetail();
	      autUser = new AutUser();
	      autUser.setFullName(user.getNickName());
	      autUser.setId(user.getUserId());
      } catch (Exception e) {
        e.printStackTrace();
      }
	  return gooInOutService.getCurrent(autUser);
    }
	
	/**
	 * 
	 * 出入库作废
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018年1月24日 上午9:50:55
	 */
	@RequestMapping(value = "/void")
    @ResponseBody
    public RetMsg toVoid(HttpServletRequest request, HttpServletResponse response, String taskId,String bizId) {
        RetMsg retMsg = new RetMsg();
        try {
            CustomUser customUser = getCustomDetail();
            retMsg = gooInOutService.toVoid(taskId,bizId,customUser.getUserId());
        }catch (Exception e){
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }
}
