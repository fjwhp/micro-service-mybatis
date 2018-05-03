package aljoin.web.controller.att;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.att.dao.entity.AttSignInOutHis;
import aljoin.att.iservice.AttSignInOutHisService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 签到、退表(历史表)(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping("/att/attSignInOutHis")
public class AttSignInOutHisController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AttSignInOutHisController.class);
	@Resource
	private AttSignInOutHisService attSignInOutHisService;
	
	/**
	 * 
	 * 签到、退表(历史表)(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/attSignInOutHisPage")
	public String attSignInOutHisPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "att/attSignInOutHisPage";
	}
	
	/**
	 * 
	 * 签到、退表(历史表)(分页列表).
	 *
	 * @return：Page<AttSignInOutHis>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AttSignInOutHis> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AttSignInOutHis obj) {
		Page<AttSignInOutHis> page = null;
		try {
			page = attSignInOutHisService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 签到、退表(历史表)(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AttSignInOutHis obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		attSignInOutHisService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 签到、退表(历史表)(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AttSignInOutHis obj) {
		RetMsg retMsg = new RetMsg();

		attSignInOutHisService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 签到、退表(历史表)(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AttSignInOutHis obj) {
		RetMsg retMsg = new RetMsg();

		AttSignInOutHis orgnlObj = attSignInOutHisService.selectById(obj.getId());
		// orgnlObj.set...

		attSignInOutHisService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 签到、退表(历史表)(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AttSignInOutHis getById(HttpServletRequest request, HttpServletResponse response, AttSignInOutHis obj) {
		return attSignInOutHisService.selectById(obj.getId());
	}

}
