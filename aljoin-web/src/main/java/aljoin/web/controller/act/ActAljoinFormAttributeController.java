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

import aljoin.act.dao.entity.ActAljoinFormAttribute;
import aljoin.act.iservice.ActAljoinFormAttributeService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 表单控属性件表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-08-31
 */
@Controller
@RequestMapping("/act/actAljoinFormAttribute")
public class ActAljoinFormAttributeController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormAttributeController.class);
	@Resource
	private ActAljoinFormAttributeService actAljoinFormAttributeService;
	
	/**
	 * 
	 * 表单控属性件表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/actAljoinFormAttributePage")
	public String actAljoinFormAttributePage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormAttributePage";
	}
	
	/**
	 * 
	 * 表单控属性件表(分页列表).
	 *
	 * @return：Page<ActAljoinFormAttribute>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormAttribute> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormAttribute obj) {
		Page<ActAljoinFormAttribute> page = null;
		try {
			page = actAljoinFormAttributeService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 表单控属性件表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttribute obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormAttributeService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单控属性件表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttribute obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormAttributeService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单控属性件表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttribute obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFormAttribute orgnlObj = actAljoinFormAttributeService.selectById(obj.getId());
		orgnlObj.setWidgetId(obj.getWidgetId());
		orgnlObj.setAttrName(obj.getAttrName());
		orgnlObj.setAttrValue(obj.getAttrValue());
		orgnlObj.setAttrDesc(obj.getAttrDesc());
		orgnlObj.setIsActive(obj.getIsActive());
		actAljoinFormAttributeService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 表单控属性件表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-08-31
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFormAttribute getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttribute obj) {
		return actAljoinFormAttributeService.selectById(obj.getId());
	}

}
