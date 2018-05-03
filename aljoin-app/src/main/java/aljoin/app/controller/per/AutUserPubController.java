package aljoin.app.controller.per;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.app.controller.BaseController;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.iservice.app.AppAutUserPubService;
import aljoin.object.RetMsg;

/**
 * 
 * 用户公共信息表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Controller
@RequestMapping("/app/per/autUserPub") 
public class AutUserPubController extends BaseController {
  
  private final static Logger logger = LoggerFactory.getLogger(AutUserPubController.class);
  
	@Resource
	private AppAutUserPubService appAutUserPubService;

	@Resource
	AutUserService autUserService;

	/**
	 * 
	 * 用户修改密码
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月10日 下午5:10:47
	 */
	@RequestMapping("/updatePwd")
	@ResponseBody
	public RetMsg updatePwd(HttpServletRequest request, HttpServletResponse response, AutUserPubVO obj) {

		RetMsg retMsg = new RetMsg();
		try {
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			retMsg = appAutUserPubService.updatePwd(obj,autAppUserLogin);
		} catch (Exception e) {
			logger.error("",e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 用户公共信息-新增
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月18日 上午10:32:12
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(AutUserPubVO obj) throws Exception {

		RetMsg retMsg = appAutUserPubService.add(obj);
		return retMsg;
	}

	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(AutUserPubVO obj) throws Exception {

		RetMsg retMsg = appAutUserPubService.update(obj);
		return retMsg;
	}
}
