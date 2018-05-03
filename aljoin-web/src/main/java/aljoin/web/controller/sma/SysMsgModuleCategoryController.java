package aljoin.web.controller.sma;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;


import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sma.dao.entity.SysMsgModuleCategory;
import aljoin.sma.iservice.SysMsgModuleCategoryService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 消息模板分类表(控制器).
 * 
 * @author：huangw
 * 
 * @date： 2017-11-14
 */
@Controller
@RequestMapping("/sma/sysMsgModuleCategory")
public class SysMsgModuleCategoryController extends BaseController {
	  private final static Logger logger = LoggerFactory.getLogger(SysMsgModuleCategoryController.class);
	@Resource
	private SysMsgModuleCategoryService sysMsgModuleCategoryService;
	
	
	/**
	 * 
	 * 消息模板分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/sysMsgModuleCategoryPage")
	public String sysMsgModuleCategoryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sma/sysMsgModuleCategoryPage";
	}
	
	/**
	 * 
	 * 消息模板分类表(分页列表).
	 *
	 * @return：Page<SysMsgModuleCategory>
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysMsgModuleCategory> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysMsgModuleCategory obj) {
		Page<SysMsgModuleCategory> page = null;
		try {
			page = sysMsgModuleCategoryService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	/**
	 * 
	 * 消息模板分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/classification")
	@ResponseBody
	public RetMsg classification(HttpServletRequest request, HttpServletResponse response, String groupName) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg=sysMsgModuleCategoryService.classification(groupName);			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 消息模板分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysMsgModuleCategory obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		sysMsgModuleCategoryService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 消息模板分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysMsgModuleCategory obj) {
		RetMsg retMsg = new RetMsg();

		sysMsgModuleCategoryService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 消息模板分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysMsgModuleCategory obj) {
		RetMsg retMsg = new RetMsg();

		SysMsgModuleCategory orgnlObj = sysMsgModuleCategoryService.selectById(obj.getId());
		// orgnlObj.set...

		sysMsgModuleCategoryService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 消息模板分类表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysMsgModuleCategory getById(HttpServletRequest request, HttpServletResponse response, SysMsgModuleCategory obj) {
		return sysMsgModuleCategoryService.selectById(obj.getId());
	}

}
