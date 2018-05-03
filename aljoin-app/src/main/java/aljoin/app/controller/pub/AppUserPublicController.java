package aljoin.app.controller.pub;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.app.controller.BaseController;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.object.AppAutUserPubVO;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/app/pub/autUserPub", method = RequestMethod.POST)
@Api(value = "个人信息", description = "个人信息接口")
public class AppUserPublicController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(AppUserPublicController.class);

	@Resource
	private AutUserPubService autUserPubService;

	/**
	 *
	 * 个人信息返回
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-16
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	@ApiOperation(value = "个人信息接口", notes = "个人信息接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg getById(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			AutUserPubVO vo = new AutUserPubVO();
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();		
			vo = autUserPubService.appGetById(userId.toString());
			AppAutUserPubVO appVO=new AppAutUserPubVO();
			appVO.setAutUser(vo.getAutUser());
			if(vo.getAutUserPub()!=null){
				AutUserPub apub=vo.getAutUserPub();
				if(apub.getLawNumber()!=null){
				    appVO.setLawNumber(apub.getLawNumber());
				}else{
					appVO.setLawNumber("");
				}
				if(apub.getChestCardNumber()!=null){
				    appVO.setChestCardNumber(apub.getChestCardNumber());
				}else{
					appVO.setChestCardNumber("");
				}
				
				if(apub.getFaxNumber()!=null){
				    appVO.setFaxNumber(apub.getFaxNumber());
				}else{
					appVO.setFaxNumber("");
				}
				if(apub.getPhoneNumber()!=null){
				    appVO.setPhoneNumber(apub.getPhoneNumber());
				}else{
					appVO.setPhoneNumber("");
				}
				if(apub.getTelNumber()!=null){
				    appVO.setTelNumber(apub.getTelNumber());
				}else{
					appVO.setTelNumber("");
				}
			}
			appVO.setAutDeptNames(vo.getAutDeptNames());
			appVO.setPositionNames(vo.getPositionNames());
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("操作成功");
			retMsg.setObject(appVO);
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}
	/**
	 *
	 * 个人信息返回
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-16
	 */
	@RequestMapping(value = "/upDateUser")
	@ResponseBody
	@ApiOperation(value = "个人信息修改", notes = "个人信息接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "autUserPub.phoneNumber", value = "手机", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "autUserPub.telNumber", value = "电话", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "autUserPub.faxNumber", value = "传真", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "autUserPub.lawNumber", value = "执法号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "autUserPub.chestCardNumber", value = "胸牌号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "autUser.userEmail", value = "邮件", required = false, dataType = "string", paramType = "query")			
			})
	public RetMsg upDateUser(HttpServletRequest request, HttpServletResponse response,AutUserPubVO vo) {
		RetMsg retMsg = new RetMsg();
		try {
	
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();		
			retMsg=autUserPubService.appUpdatePub(vo, userId.toString());
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

	/**
	 *
	 * 修改密码
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-11-16
	 */
	@RequestMapping(value = "/updatePwd")
	@ResponseBody
	@ApiOperation(value = "修改密码", notes = "修改密码")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "oldPass", value = "旧密码", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "newPass", value = "新密码", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "repeatPass", value = "确认新密码", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query") })
	public RetMsg updatePwd(HttpServletRequest request, HttpServletResponse response, String oldPass, String newPass,String repeatPass) {
		RetMsg retMsg = new RetMsg();
		try {		
			AutAppUserLogin autAppUserLogin = getAppUserLogin(request);
			Long userId = autAppUserLogin.getUserId();	
			if(oldPass!=null && !"".equals(oldPass)){
				oldPass=dencryptParam(oldPass, autAppUserLogin);
			}else{
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
				retMsg.setMessage("旧密码为空");
				return retMsg;
			}
			if(newPass!=null && !"".equals(newPass)){
				newPass=dencryptParam(newPass, autAppUserLogin);				
			}else{
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
				retMsg.setMessage("新密码为空");
				return retMsg;
			}
			if(repeatPass!=null &&  !"".equals(newPass)){
				repeatPass=dencryptParam(repeatPass, autAppUserLogin); 
				
			}else{
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
				retMsg.setMessage("二次新密码为空");
				return retMsg;
			}
			if(oldPass.equals(newPass)){
				retMsg.setCode(AppConstant.RET_CODE_ERROR);
				retMsg.setMessage("新旧密码一致");
				return retMsg;
			}
			retMsg=autUserPubService.appUpdatePwd(oldPass, newPass, repeatPass, userId.toString());				
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}
}
