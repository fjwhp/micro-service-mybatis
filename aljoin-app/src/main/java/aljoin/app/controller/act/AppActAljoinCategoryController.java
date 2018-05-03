package aljoin.app.controller.act;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.app.controller.BaseController;
import aljoin.object.AppConstant;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 流程分类表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-08-31
 */
@Controller
@RequestMapping(value = "/app/act/actAljoinCategory",method = RequestMethod.POST)
@Api(value = "App流程分类操作Controller", description = "协同办公 -> 流程分类")
public class AppActAljoinCategoryController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AppActAljoinCategoryController.class);
	@Resource
	private ActAljoinCategoryService actAljoinCategoryService;

	/**
	 * 
	 * 获取所有顶级流程分类
	 *
	 * @return：List<ActAljoinCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017年12月19日
	 */
	@RequestMapping(value = "/getAllCategoryList")
	@ResponseBody
	@ApiOperation("获取所有顶级流程分类")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "timestamp", value = "时间戳", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "sign", value = "签名", required = true, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "token", value = "登录令牌", required = true, dataType = "string", paramType = "query")

	})
	public RetMsg getAllCategoryList(HttpServletRequest request, HttpServletResponse response, ActAljoinCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = actAljoinCategoryService.getAllParentCategoryList(obj);
		} catch (Exception e) {
		  logger.error("", e);
		  retMsg.setCode(AppConstant.RET_CODE_ERROR);
		  retMsg.setMessage("操作异常");
		}
		return retMsg;
	}

}
