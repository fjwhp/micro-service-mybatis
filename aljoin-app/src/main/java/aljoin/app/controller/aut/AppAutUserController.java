package aljoin.app.controller.aut;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.app.annotation.FuncObj;
import aljoin.app.annotation.ParamObj;
import aljoin.app.controller.BaseController;
import aljoin.app.object.LoginDO;
import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.iservice.AutAppUserLoginService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomPasswordEncoder;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import aljoin.util.EncryptUtil;
import aljoin.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 用户表(控制器).
 * 
 * @author：zhongjy
 * 
 * 				@date： 2017-05-21
 */
@Controller
@RequestMapping(value = "app/aut/autUser", method = RequestMethod.POST)
@Api(value = "移动端用户权限", description = "移动端->移动端用户权限")
public class AppAutUserController extends BaseController {

	private final static Logger logger = LoggerFactory.getLogger(AppAutUserController.class);

	@Resource
	private AutUserService autUserService;
	@Resource
	private AutAppUserLoginService autAppUserLoginService;
	@Resource
	private AutUserPubService autUserPubService;

	/**
	 * 
	 * 用户登录
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017年10月23日 上午8:55:58
	 */
	@RequestMapping("/appLogin")
	@ResponseBody
	@FuncObj(desc = "[移动端]-[app用户登录]")
	@ParamObj(encryptAttrs = "userName,userPwd", encryptType = AppConstant.PARAM_ENCRYPT_DES)
	@ApiOperation(value = "app用户登录接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userName", value = "用户名(DES加密)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "userPwd", value = "密码(DES加密)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "rsaPubKey", value = "RSA公钥", required = true, dataType = "string", paramType = "query") })
	public RetMsg appLogin(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = null;
		// 获取参数
		String userName = request.getParameter("userName");
		String userPwd = request.getParameter("userPwd");
		String rsaPubKey = request.getParameter("rsaPubKey");

		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userName", userName);
		param.put("userPwd", userPwd);
		param.put("rsaPubKey", rsaPubKey);
		try {
			// 非空判断
			checkParamIsNull(param);
			// 解密
			try {
				userName = EncryptUtil.decryptDES(userName, AppConstant.APP_DES_KEY);
				userPwd = EncryptUtil.decryptDES(userPwd, AppConstant.APP_DES_KEY);
			} catch (Exception e) {
				throw new Exception("解密异常(加密错误)");
			}
			// 获取用户
			Where<AutUser> where = new Where<AutUser>();
			where.eq("user_name", userName);
			where.setSqlSelect("id,user_pwd,user_name,full_name");
			AutUser autUser = autUserService.selectOne(where);
			if (autUser == null) {
				throw new Exception("用户[" + userName + "]不存在");
			}
			// 判断密码是否正确
			CustomPasswordEncoder cpe = new CustomPasswordEncoder();
			if (!cpe.matches(userPwd, autUser.getUserPwd())) {
				throw new Exception("密码错误");
			}

			// 检查登录信息表是否有数据，如果没有，插入，如果有则修改
			AutAppUserLogin orgnlAutAppUserLogin = autAppUserLoginService.getByUserId(autUser.getId());
			Where<AutUserPub> autUserPubWhere = new Where<AutUserPub>();
			autUserPubWhere.eq("user_id", autUser.getId());
			autUserPubWhere.setSqlSelect("user_icon");
			AutUserPub autUserPub = autUserPubService.selectOne(autUserPubWhere);
			// 获取当前时间
			Date currentDate = new Date();
			// 获取ras密钥对
			Map<String, String> keyPair = EncryptUtil.getRsaKeyPair();
			// 登录成功时返回对象
			LoginDO loginDO = new LoginDO();
			if (orgnlAutAppUserLogin == null) {
				// 插入
				AutAppUserLogin autAppUserLogin = new AutAppUserLogin();
				autAppUserLogin.setToken(StringUtil.getUUID().toUpperCase());
				autAppUserLogin.setUserId(autUser.getId());
				autAppUserLogin.setSecret(StringUtil.getNRandom(5) + StringUtil.getNRandom(5));
				autAppUserLogin.setUserName(autUser.getUserName());
				autAppUserLogin.setLastAccessTime(currentDate);
				autAppUserLogin.setLastLoginTime(currentDate);
				autAppUserLogin.setAljoinPrivateKey(keyPair.get("private"));// 自己的私钥
				autAppUserLogin.setOtherPublicKey(rsaPubKey);// 对方的公钥
				autAppUserLogin.setLoginCount(1);

				loginDO.setAljoinPubKey(keyPair.get("public"));// 返回对方和强公钥
				loginDO.setSecret(autAppUserLogin.getSecret());
				loginDO.setToken(autAppUserLogin.getToken());
				loginDO.setUserName(autUser.getUserName());
				if (autUserPub != null) {
					loginDO.setUserIcon(autUserPub.getUserIcon());
				}
				loginDO.setUserFullName(autUser.getFullName());

				autAppUserLoginService.add(autAppUserLogin);
			} else {
				// 更新
				orgnlAutAppUserLogin.setToken(StringUtil.getUUID().toUpperCase());
				orgnlAutAppUserLogin.setSecret(StringUtil.getNRandom(5) + StringUtil.getNRandom(5));
				orgnlAutAppUserLogin.setLastAccessTime(currentDate);
				orgnlAutAppUserLogin.setLastLoginTime(currentDate);
				orgnlAutAppUserLogin.setAljoinPrivateKey(keyPair.get("private"));
				orgnlAutAppUserLogin.setOtherPublicKey(rsaPubKey);
				orgnlAutAppUserLogin.setLoginCount(orgnlAutAppUserLogin.getLoginCount() + 1);

				loginDO.setAljoinPubKey(keyPair.get("public"));// 返回对方和强公钥
				loginDO.setSecret(orgnlAutAppUserLogin.getSecret());
				loginDO.setToken(orgnlAutAppUserLogin.getToken());
				loginDO.setUserName(autUser.getUserName());
				if (autUserPub != null) {
					loginDO.setUserIcon(autUserPub.getUserIcon());
				}
				loginDO.setUserFullName(autUser.getFullName());

				autAppUserLoginService.update(orgnlAutAppUserLogin);
			}

			// 校验通过，构造返回数据
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
			retMsg.setMessage("登录成功");
			retMsg.setObject(loginDO);
		} catch (Exception e) {
			retMsg = new RetMsg();
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
			retMsg.setMessage(e.getMessage());
			logger.error("", e);
		}
		return retMsg;
	}

}
