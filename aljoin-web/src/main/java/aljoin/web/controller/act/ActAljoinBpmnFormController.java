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

import aljoin.act.dao.entity.ActAljoinBpmnForm;
import aljoin.act.iservice.ActAljoinBpmnFormService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 流程元素-表单关系(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-15
 */
@Controller
@RequestMapping("/act/actAljoinBpmnForm")
public class ActAljoinBpmnFormController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinBpmnFormController.class);
	@Resource
	private ActAljoinBpmnFormService actAljoinBpmnFormService;
	
	/**
	 * 
	 * 流程元素-表单关系(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/actAljoinBpmnFormPage")
	public String actAljoinBpmnFormPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinBpmnFormPage";
	}
	
	/**
	 * 
	 * 流程元素-表单关系(分页列表).
	 *
	 * @return：Page<ActAljoinBpmnForm>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinBpmnForm> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinBpmnForm obj) {
		Page<ActAljoinBpmnForm> page = null;
		try {
			page = actAljoinBpmnFormService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 流程元素-表单关系(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnForm obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinBpmnFormService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 流程元素-表单关系(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnForm obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinBpmnFormService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 流程元素-表单关系(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnForm obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinBpmnForm orgnlObj = actAljoinBpmnFormService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinBpmnFormService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 流程元素-表单关系(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-15
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinBpmnForm getById(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnForm obj) {
		return actAljoinBpmnFormService.selectById(obj.getId());
	}

}
