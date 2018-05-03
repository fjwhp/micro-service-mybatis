package aljoin.web.controller.per;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutCard;
import aljoin.aut.dao.object.AutCardVO;
import aljoin.aut.iservice.AutCardService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 名片表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Controller
@RequestMapping("/per/autCard")
public class AutCardController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutCardController.class);
	@Resource
	private AutCardService autCardService;
	
	/**
	 * 
	 * 名片表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/autCardPage")
	public String autCardPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "per/autCardPage";
	}
	
	/**
	 * 
	 * 名片表(分页列表).
	 *
	 * @return：Page<autCard>
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutCardVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutCard obj) {
		Page<AutCardVO> pageVo = null;
		try {
			CustomUser customUser = getCustomDetail();			
			pageVo = autCardService.list(pageBean, obj,customUser.getUserId().toString());
		} catch (Exception e) {
		  logger.error("", e);
		}
		return pageVo;
	}
	 

	/**
	 * 
	 * 名片表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutCard obj) {
		RetMsg retMsg = new RetMsg();
		obj.setIsActive(1);
		autCardService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 名片表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutCard obj) {
		RetMsg retMsg = new RetMsg();

		autCardService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 名片表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutCard obj) {
		RetMsg retMsg = new RetMsg();
		try {
			AutCard orgnlObj = autCardService.selectById(obj.getId());
			orgnlObj.setCompanyAddress(obj.getCompanyAddress());
			orgnlObj.setCategoryId(obj.getCategoryId());
			orgnlObj.setCompanyFax(obj.getCompanyFax());
			orgnlObj.setCompanyName(obj.getCompanyName());
			orgnlObj.setCompanyTel(obj.getCompanyTel());
			orgnlObj.setGender(obj.getGender());
			orgnlObj.setIsActive(obj.getIsActive());
			orgnlObj.setMsnNumber(obj.getMsnNumber());
			orgnlObj.setPhoneNumber(obj.getPhoneNumber());
			orgnlObj.setPositionName(obj.getPositionName());
			orgnlObj.setQqNumber(obj.getQqNumber());
			orgnlObj.setRemark(obj.getRemark());
			orgnlObj.setUserMail(obj.getUserMail());
			orgnlObj.setUserName(obj.getUserName());
			orgnlObj.setWechatNumber(obj.getWechatNumber());

			autCardService.updateById(orgnlObj);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}
    
	/**
	 * 
	 * 名片表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AutCardVO getById(HttpServletRequest request, HttpServletResponse response, AutCard obj) {
		
		AutCardVO autCardVO = new AutCardVO();
		try {
			autCardVO = autCardService.getById(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return autCardVO;
		
	}

}
