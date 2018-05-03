package aljoin.web.controller.veh;

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

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.veh.dao.entity.VehMaintain;
import aljoin.veh.dao.object.VehMaintainVO;
import aljoin.veh.iservice.VehMaintainService;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 车船维护信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-08
 */
@Controller
@RequestMapping(value = "/veh/vehMaintain", method = RequestMethod.POST)
@Api(value = "车船维护Controller", description = "车船维护")
public class VehMaintainController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(VehMaintainController.class);

	@Resource
	private VehMaintainService vehMaintainService;
	
	/**
	 * 
	 * 车船维护信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping(value = "/vehMaintainPage", method = RequestMethod.GET)
    @ApiOperation(value = "车船维护页面跳转接口")
	public String vehMaintainPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "veh/vehMaintainPage";
	}
	
	/**
	 * 
	 * 车船维护信息表(分页列表).
	 *
	 * @return：Page<VehMaintain>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<VehMaintain> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, VehMaintainVO obj) {
		Page<VehMaintain> page = null;
		try {
			page = vehMaintainService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * 车船维护信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	@ApiOperation("车船维护新增")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carCode", value = "牌号", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "maintainTime", value = "维护时间", required = false, dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "agentId", value = "经办人ID", required = false, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "agentName", value = "经办人姓名", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "maintainType", value = "维护类型", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "maintainCost", value = "维护费用", required = false, dataType = "BigDecimal", paramType = "query"),
            @ApiImplicitParam(name = "content", value = "备注", required = false, dataType = "String", paramType = "query"),

            @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")

    })
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, VehMaintain obj) {
		RetMsg retMsg = new RetMsg();
		try {
          retMsg = vehMaintainService.add(obj);
        }catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * 车船维护信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, VehMaintain obj) {
		RetMsg retMsg = new RetMsg();

		vehMaintainService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 车船维护信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/update")
	@ResponseBody
    @ApiOperation("车船维护编辑")
	@ApiImplicitParams({
  	     @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query"),
         @ApiImplicitParam(name = "carCode", value = "牌号", required = false, dataType = "string", paramType = "query"),
         @ApiImplicitParam(name = "maintainTime", value = "维护时间", required = false, dataType = "Date", paramType = "query"),
         @ApiImplicitParam(name = "agentId", value = "经办人ID", required = false, dataType = "long", paramType = "query"),
         @ApiImplicitParam(name = "agentName", value = "经办人姓名", required = false, dataType = "string", paramType = "query"),
         @ApiImplicitParam(name = "maintainType", value = "维护类型", required = false, dataType = "string", paramType = "query"),
         @ApiImplicitParam(name = "maintainCost", value = "维护费用", required = false, dataType = "BigDecimal", paramType = "query"),
         @ApiImplicitParam(name = "content", value = "备注", required = false, dataType = "String", paramType = "query"),
      
         @ApiImplicitParam(name = "_csrf", value = "token", required = true, dataType = "string", paramType = "query")

	  })
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, VehMaintain obj) {
		RetMsg retMsg = new RetMsg();

		try {
          retMsg = vehMaintainService.update(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * 车船维护信息表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@ApiOperation("车船维护详情")
    @ApiImplicitParam(name = "id", value = "主键", required = true, dataType = "int", paramType = "query")
	public VehMaintain getById(HttpServletRequest request, HttpServletResponse response, VehMaintain obj) {
		return vehMaintainService.selectById(obj.getId());
	}

}
