package aljoin.web.controller.act;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import aljoin.act.iservice.ActAljoinTaskAssigneeService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 流程任务-授权表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-15
 */
@Controller
@RequestMapping("/act/actAljoinTaskAssignee")
public class ActAljoinTaskAssigneeController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinTaskAssigneeController.class);
	@Resource
	private ActAljoinTaskAssigneeService actAljoinTaskAssigneeService;
	
	/**
	 * 
	 * 流程任务-授权表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/actAljoinTaskAssigneePage")
	public String actAljoinTaskAssigneePage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinTaskAssigneePage";
	}
	
	/**
	 * 
	 * 流程任务-授权表(分页列表).
	 *
	 * @return：Page<ActAljoinTaskAssignee>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinTaskAssignee> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinTaskAssignee obj) {
		Page<ActAljoinTaskAssignee> page = null;
		try {
			page = actAljoinTaskAssigneeService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 流程任务-授权表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskAssignee obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinTaskAssigneeService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 流程任务-授权表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskAssignee obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinTaskAssigneeService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 流程任务-授权表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskAssignee obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinTaskAssignee orgnlObj = actAljoinTaskAssigneeService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinTaskAssigneeService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 流程任务-授权表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinTaskAssignee getById(HttpServletRequest request, HttpServletResponse response, ActAljoinTaskAssignee obj) {
		return actAljoinTaskAssigneeService.selectById(obj.getId());
	}

}
