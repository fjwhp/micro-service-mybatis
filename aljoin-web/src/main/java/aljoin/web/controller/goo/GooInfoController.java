package aljoin.web.controller.goo;

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
import aljoin.goo.dao.entity.GooInfo;
import aljoin.goo.dao.object.GooInfoDO;
import aljoin.goo.dao.object.GooInfoVO;
import aljoin.goo.iservice.GooInfoService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 办公用品信息表(控制器).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-04
 */
@Controller
@RequestMapping("/goo/gooInfo")
public class GooInfoController extends BaseController {
    
    private final static Logger logger = LoggerFactory.getLogger(GooInfoController.class);

	@Resource
	private GooInfoService gooInfoService;
	
	/**
	 * 
	 * 办公用品信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/gooInfoPage")
	public String gooInfoPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "goo/gooInfoPage";
	}
	
	/**
	 * 
	 * 办公用品信息表(分页列表).
	 *
	 * @return：Page<GooInfo>
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<GooInfoDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, GooInfoVO obj) {
	  Page<GooInfoDO> page = null;
      try {
          CustomUser user = getCustomDetail();
          if (null != user) {
              obj.setCreateUserId(user.getUserId());
          }
          page = gooInfoService.list(pageBean, obj);
      } catch (Exception e) {
          logger.error("", e);
      }
		return page;
	}
	

	/**
	 * 
	 * 办公用品信息表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, GooInfo obj) {
		RetMsg retMsg = new RetMsg();

		try {
          retMsg = gooInfoService.add(obj);
        }catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品信息表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, GooInfo obj) {
		RetMsg retMsg = new RetMsg();

		gooInfoService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 办公用品信息表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, GooInfo obj) {
		RetMsg retMsg = new RetMsg();

		try {
          retMsg = gooInfoService.update(obj);
        } catch (Exception e) {
          logger.error("", e);
          retMsg.setCode(1);
          retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * 办公用品信息表(根据ID获取对象).
	 *
	 * @throws Exception 
	 * @return：AutUser
	 *
	 * @author：xuc
	 *
	 * @date：2018-01-04
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public GooInfoVO getById(HttpServletRequest request, HttpServletResponse response, GooInfo obj) throws Exception {
		return gooInfoService.getById(obj.getId());
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
    @ApiOperation(value = "导出接口")
    @FuncObj(desc = "[办公用品]-[盘点]")
	public void export(HttpServletRequest request, HttpServletResponse response, GooInfoVO obj) {
      try {
          CustomUser user = getCustomDetail();
          if (null != user) {
              obj.setCreateUserId(user.getUserId());
          }
          gooInfoService.export(response, obj);
      } catch (Exception e) {
          logger.error("", e);
      }
  }
}
