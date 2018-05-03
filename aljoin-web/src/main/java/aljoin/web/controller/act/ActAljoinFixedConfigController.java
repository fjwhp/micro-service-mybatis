package aljoin.web.controller.act;

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

import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 固定流程配置表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-11-07
 */
@Controller
@RequestMapping(value = "/act/actAljoinFixedConfig",method = RequestMethod.POST)
@Api(value = "固定流程配置相关接口",description = "固定流程配置相关接口")
public class ActAljoinFixedConfigController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ActAljoinFixedConfigController.class);
	@Resource
	private ActAljoinFixedConfigService actAljoinFixedConfigService;
	
	/**
	 * 
	 * 固定流程配置表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping("/actAljoinFixedConfigPage")
	public String actAljoinFixedConfigPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFixedConfigPage";
	}
	
	/**
	 * 
	 * 固定流程配置表(分页列表).
	 *
	 * @return：Page<ActAljoinFixedConfig>
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFixedConfig> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFixedConfig obj) {
		Page<ActAljoinFixedConfig> page = null;
		try {
			page = actAljoinFixedConfigService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 固定流程配置表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping(value = "/add",method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation("固定流程配置新增")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "processCode",value = "流程Code",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "processId",value = "流程ID",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "processName",value = "流程名称",required = true,dataType = "string",paramType = "query"),
			@ApiImplicitParam(name = "isActive",value = "是否激活",required = true,dataType = "string",paramType = "query")
	})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFixedConfig obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinFixedConfigService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 固定流程配置表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFixedConfig obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFixedConfigService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 固定流程配置表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFixedConfig obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFixedConfig orgnlObj = actAljoinFixedConfigService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinFixedConfigService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 固定流程配置表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-07
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFixedConfig getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFixedConfig obj) {
		return actAljoinFixedConfigService.selectById(obj.getId());
	}

}
