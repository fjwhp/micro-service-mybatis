package aljoin.app.controller.pub;

import aljoin.app.controller.BaseController;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/app/pub/public", method = RequestMethod.POST)
@Api(value = "公共功能接口", description = "公共功能接口")
public class AppPublicController extends BaseController{
  
    private final static Logger logger = LoggerFactory.getLogger(AppPublicController.class);

	@Resource
	private AutDepartmentUserService autDepartmentUserService;

	/**
	 *
	 * 组织机构树.
	 *
	 * @return：AutOrganVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-31
	 */
	@RequestMapping(value = "/organList")
	@ResponseBody
	@ApiOperation(value = "组织机构树接口", notes = "组织机构树列表接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = false, dataType = "string",
			paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = false, dataType = "string",
			paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string",
			paramType = "query")})
	public RetMsg organList(HttpServletRequest request, HttpServletResponse response) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg  = autDepartmentUserService.getAppOrganList();
		} catch (Exception e) {
			logger.error("",e);
			retMsg.setCode(AppConstant.RET_CODE_ERROR);
		}
		return retMsg;
	}
}
