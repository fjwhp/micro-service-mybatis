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
import aljoin.sma.dao.entity.SysMsgModuleInfo;
import aljoin.sma.iservice.SysMsgModuleInfoService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 消息模板信息表(控制器).
 * 
 * @author：huangw
 * 
 * @date： 2017-11-14
 */
@Controller
@RequestMapping("/sma/sysMsgModuleInfo")
public class SysMsgModuleInfoController extends BaseController {
	  private final static Logger logger = LoggerFactory.getLogger(SysMsgModuleInfoController.class);
	@Resource
	private SysMsgModuleInfoService sysMsgModuleInfoService;
	
	/**
	 * 
	 * 消息模板信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/sysMsgModuleInfoPage")
	public String sysMsgModuleInfoPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sma/sysMsgModuleInfoPage";
	}
	
	/**
	 * 
	 * 消息模板信息表(分页列表).
	 *
	 * @return：Page<SysMsgModuleInfo>
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysMsgModuleInfo> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysMsgModuleInfo obj) {
		Page<SysMsgModuleInfo> page = null;
		try {
			page = sysMsgModuleInfoService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 消息模板信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysMsgModuleInfo obj,String groupName) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg=sysMsgModuleInfoService.addOrUpdata(obj,groupName);
		} catch (Exception e) {
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		
		return retMsg;
	}
	
	/**
	 * 
	 * 消息模板信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysMsgModuleInfo obj) {
		RetMsg retMsg = new RetMsg();
		sysMsgModuleInfoService.deleteById(obj.getId());
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 消息模板信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysMsgModuleInfo obj) {
		RetMsg retMsg = new RetMsg();
		SysMsgModuleInfo orgnlObj = sysMsgModuleInfoService.selectById(obj.getId());
		// orgnlObj.set...
		sysMsgModuleInfoService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 消息模板信息表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-14
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysMsgModuleInfo getById(HttpServletRequest request, HttpServletResponse response, SysMsgModuleInfo obj) {
		return sysMsgModuleInfoService.selectById(obj.getId());
	}

}
