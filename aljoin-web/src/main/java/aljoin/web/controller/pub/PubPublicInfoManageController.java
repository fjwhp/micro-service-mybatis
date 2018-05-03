package aljoin.web.controller.pub;

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

import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoVO;
import aljoin.pub.iservice.PubPublicInfoService;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 公共信息管理(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping(value = "/pub/pubPublicInfoManage",method = RequestMethod.POST)
@Api(value = "公共信息管理Controller",description = "公共信息->公共信息管理相关接口")
public class PubPublicInfoManageController extends BaseController{

	private final static Logger logger = LoggerFactory.getLogger(PubPublicInfoManageController.class);
	@Resource
	private PubPublicInfoService pubPublicInfoService;
	
	/**
	 *
	 * 公共信息管理(页面).
	 *
	 * @return：String
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	@RequestMapping(value = "/pubPublicInfoManagePage",method = RequestMethod.GET)
	@ApiOperation("公共信息管理页面")
	public String pubPublicInfoPage(HttpServletRequest request,HttpServletResponse response) {
		return "pub/pubPublicInfoManagePage";
	}
	
	/**
	 *
	 * 我的信息表(分页列表).
	 *
	 * @return：Page<PubPublicInfo>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping(value = "/manageList")
	@ResponseBody
	@ApiOperation("公共信息监管分页列表")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize",value = "每页记录数",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "pageNum",value = "当前页码",required = true,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "categoryId",value = "类型ID（下拉）",required = false,dataType = "long",paramType = "query"),
			@ApiImplicitParam(name = "title",value = "名称",required = false,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "auditStatus",value = "审核情况",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "periodStatus",value = "有效期",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "publishName",value = "发布人",required = false,dataType = "int",paramType = "query"),
			@ApiImplicitParam(name = "createTime",value = "日期",required = false,dataType = "date",paramType = "query")
	})
	public Page<PubPublicInfoDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, PubPublicInfoVO obj) {
		Page<PubPublicInfoDO> page = null;
		try {
			CustomUser user = getCustomDetail();
			if(null != user){
				obj.setCreateUserId(user.getUserId());
			}
			page = pubPublicInfoService.manageList(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
}
