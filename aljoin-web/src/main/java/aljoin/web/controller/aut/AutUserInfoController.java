package aljoin.web.controller.aut;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUserInfo;
import aljoin.aut.dao.object.AutUserInfoVO;
import aljoin.aut.iservice.AutUserInfoService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 用户信息表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-06
 */
@Controller
@RequestMapping(value = "/aut/autUserInfo",method = RequestMethod.POST)
@Api(value = "用户信息controller",description = "权限管理->用户管理->用户信息接口")  
public class AutUserInfoController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutUserInfoController.class);
	@Resource
	private AutUserInfoService autUserInfoService;
	
	/**
	 * 
	 * 用户信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/autUserInfoPage",method = RequestMethod.GET)
	public String autUserInfoPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "aut/autUserInfoPage";
	}
	
	/**
	 * 
	 * 用户信息表(分页列表).
	 *
	 * @return：Page<AutUserInfo>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/list")
	@ResponseBody
	public Page<AutUserInfo> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutUserInfo obj) {
		Page<AutUserInfo> page = null;
		try {
			page = autUserInfoService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 用户信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutUserInfo obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		autUserInfoService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 用户信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	@ApiOperation(value = "用户信息删除",notes = "用户信息删除接口")
	@ApiImplicitParam(name = "id",value = "用户信息ID",required = true,dataType = "int",paramType = "query")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutUserInfo obj) {
		RetMsg retMsg = new RetMsg();

		autUserInfoService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 用户信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutUserInfo obj) {
		RetMsg retMsg = new RetMsg();

		AutUserInfo orgnlObj = autUserInfoService.selectById(obj.getId());
		// orgnlObj.set...
		if(null != obj.getUserId()){
			orgnlObj.setUserId(obj.getUserId());
		}
		if(null != obj.getUserKey()){
			orgnlObj.setUserKey(obj.getUserKey());
		}
		if(null != obj.getDescription()){
			orgnlObj.setDescription(obj.getDescription());
		}
		autUserInfoService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 用户信息表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/getById")
	@ResponseBody
	public AutUserInfo getById(HttpServletRequest request, HttpServletResponse response, AutUserInfo obj) {
		return autUserInfoService.selectById(obj.getId());
	}

	/**
	 *
	 * 根据用户ID获取用户信息列表(不分页).
	 *
	 * @return：Page<AutUserInfo>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/getListByUserId")
	@ResponseBody
	@ApiOperation(value = "根据用户ID获取用户信息列表(不分页)",notes = "根据用户ID获取用户信息列表(不分页)接口")
	@ApiImplicitParam(name = "userId",value = "用户ID",required = true,dataType = "string",paramType = "query")
	public List<AutUserInfo> getListByUserId(HttpServletRequest request, HttpServletResponse response, AutUserInfo obj) {
		List<AutUserInfo> autUserInfoList = null;
		try {
			autUserInfoList = autUserInfoService.list(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return autUserInfoList;
	}


	/**
	 *
	 * 用户信息表(批量新增或修改).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/addOrUpdateBatch")
	@ResponseBody
	@ApiOperation(value = "用户信息批量新增或修改",notes = "用户信息批量新增或修改接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userInfoList[0].id",value = "用户信息ID",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[0].userId",value = "用户ID",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[0].key",value = "键",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[0].value",value = "值",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[0].description",value = "描述",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[0].isActive",value = "是否激活",required = true,dataType = "int",paramType = "query"),

			@ApiImplicitParam(name = "userInfoList[1].id",value = "用户信息ID",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[1].userId",value = "用户ID",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[1].key",value = "键",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[1].value",value = "值",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[1].description",value = "描述",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "userInfoList[1].isActive",value = "是否激活",required = true,dataType = "int",paramType = "query"),

			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg addOrUpdateBatch(HttpServletRequest request, HttpServletResponse response, AutUserInfoVO userInfoVO) {
		RetMsg retMsg = new RetMsg();
		try{
			retMsg = autUserInfoService.addOrUpdateBatch(userInfoVO);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(0);
			retMsg.setMessage("操作失败");
		}
		return retMsg;
	}

	/**
	 *
	 * 新增或修改校验同一个用户是否存在相同的key值.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-06
	 */
	@RequestMapping(value = "/validate",method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "校验同一个用户是否存在相同的Key值接口",notes = "校验同一个用户是否存在相同的Key值接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId",value = "用户ID",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "key",value = "属性键值",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "_csrf",value = "token",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg validate(HttpServletRequest request, HttpServletResponse response, AutUserInfo obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autUserInfoService.validate(obj);
		}catch (Exception e){
		  logger.error("", e);
			retMsg.setCode(0);
			retMsg.setMessage("校验属性键值异常");
		}
		return retMsg;
	}

}
