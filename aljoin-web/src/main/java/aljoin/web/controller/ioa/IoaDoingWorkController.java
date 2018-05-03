package aljoin.web.controller.ioa;

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

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.security.CustomUser;
import aljoin.ioa.dao.object.DoTaskShowVO;
import aljoin.ioa.iservice.IoaDoingWorkService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 在办工作
 *
 * @author：zhongjy
 * 
 * @date：2017年10月12日 上午8:43:11
 */
@Controller
@RequestMapping(value = "/ioa/ioaDoingWork", method = RequestMethod.POST)
@Api(value = "待办工作流程操作Controller", description = "流程->待办工作")
public class IoaDoingWorkController extends BaseController{
  private final static Logger logger = LoggerFactory.getLogger(IoaDoingWorkController.class);
	@Resource
	private IoaDoingWorkService ioaDoingWorkService;
	
  /**
   * 
   * 在办工作页面
   *
   * @return：String
   *
   * @author：zhongjy
   *
   * @date：2017年10月12日 上午8:42:57
   */
  @RequestMapping(value = "/ioaDoingWorkPage", method = RequestMethod.GET)
  public String ioaDoingWorkPage(HttpServletRequest request) {
    return "ioa/ioaDoingWorkPage";
  }

  /**
   * 
   * 在办列表(分页列表).
   *
   * @return：Page<AutUser>
   *
   * @author：zhongjy
   *
   * @date：2017年5月27日 下午4:30:09
   */
  @RequestMapping("/list")
  @ResponseBody
  @FuncObj(desc = "[协同办公]-[在办工作]-[搜索]")
  public Page<AutUser> list(HttpServletRequest request, HttpServletResponse response,
      PageBean pageBean, AutUser obj) {
    Page<AutUser> page = null;
    try {
      //page = autUserService.list(pageBean, obj);
    } catch (Exception e) {
      logger.error("", e);
    }
    return page;
  }

  /**
   * 
   * 催办
   *
   * @return：RetMsg
   *
   * @author：zhongjy
   *
   * @date：2017年5月27日 下午4:44:27
   */
  @RequestMapping("/doUrge")
  @ResponseBody
  @FuncObj(desc = "[协同办公]-[在办工作]-[催办]")
  public RetMsg doUrge(HttpServletRequest request, HttpServletResponse response, AutUser autUser) {
    RetMsg retMsg = new RetMsg();
    try {
      //autUserService.delete(autUser);
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
    } catch (Exception e) {
      retMsg.setCode(1);
      retMsg.setMessage(e.getMessage());
      logger.error("", e);
    }
    return retMsg;
  }
  	
  /**
   * 
   * 根据当前用户获取在办工作流程
   *
   * @return：List
   *
   * @author：pengsp
   *
   * @date：2017年10月16日
   */
  	@RequestMapping(value="/selectDoTask",method = RequestMethod.GET)
  	@ResponseBody
  	@ApiOperation(value = "根据当前用户获取在办工作流程")
  	public Page<DoTaskShowVO> selectDoTask(HttpServletRequest request, HttpServletResponse response,PageBean pageBean,DoTaskShowVO obj){
  		Page<DoTaskShowVO> page=new Page<DoTaskShowVO>();
		try {
			CustomUser customUser=getCustomDetail();
			String userId=customUser.getUserId().toString();
			page=ioaDoingWorkService.list(pageBean, userId,obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
  		return page;
  	}
}
