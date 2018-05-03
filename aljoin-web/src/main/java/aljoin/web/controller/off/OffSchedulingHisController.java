package aljoin.web.controller.off;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffSchedulingHis;
import aljoin.off.iservice.OffSchedulingHisService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 日程安排表(历史表)(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-01
 */
@Controller
@RequestMapping("/off/offSchedulingHis")
public class OffSchedulingHisController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(OffSchedulingHisController.class);
	@Resource
	private OffSchedulingHisService offSchedulingHisService;
	
	/**
	 * 
	 * 日程安排表(历史表)(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/offSchedulingHisPage")
	public String offSchedulingHisPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "off/offSchedulingHisPage";
	}
	
	/**
	 * 
	 * 日程安排表(历史表)(分页列表).
	 *
	 * @return：Page<OffSchedulingHis>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<OffSchedulingHis> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffSchedulingHis obj) {
		Page<OffSchedulingHis> page = null;
		try {
			page = offSchedulingHisService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 日程安排表(历史表)(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, OffSchedulingHis obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		offSchedulingHisService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 日程安排表(历史表)(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, OffSchedulingHis obj) {
		RetMsg retMsg = new RetMsg();

		offSchedulingHisService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 日程安排表(历史表)(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, OffSchedulingHis obj) {
		RetMsg retMsg = new RetMsg();

		OffSchedulingHis orgnlObj = offSchedulingHisService.selectById(obj.getId());
		// orgnlObj.set...

		offSchedulingHisService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 日程安排表(历史表)(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-01
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public OffSchedulingHis getById(HttpServletRequest request, HttpServletResponse response, OffSchedulingHis obj) {
		return offSchedulingHisService.selectById(obj.getId());
	}

}
