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
import aljoin.off.dao.entity.OffMonthReportDetail;
import aljoin.off.iservice.OffMonthReportDetailService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 工作月报详情表(控制器).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-11
 */
@Controller
@RequestMapping("/off/offMonthReportDetail")
public class OffMonthReportDetailController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(OffMonthReportDetailController.class);
	@Resource
	private OffMonthReportDetailService offMonthReportDetailService;
	
	/**
	 * 
	 * 工作月报详情表(页面).
	 *
	 * @return：String
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/offMonthReportDetailPage")
	public String offMonthReportDetailPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "off/offMonthReportDetailPage";
	}
	
	/**
	 * 
	 * 工作月报详情表(分页列表).
	 *
	 * @return：Page<OffMonthReportDetail>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<OffMonthReportDetail> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, OffMonthReportDetail obj) {
		Page<OffMonthReportDetail> page = null;
		try {
			page = offMonthReportDetailService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 工作月报详情表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, OffMonthReportDetail obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		offMonthReportDetailService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 工作月报详情表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, OffMonthReportDetail obj) {
		RetMsg retMsg = new RetMsg();

		offMonthReportDetailService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 工作月报详情表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, OffMonthReportDetail obj) {
		RetMsg retMsg = new RetMsg();

		OffMonthReportDetail orgnlObj = offMonthReportDetailService.selectById(obj.getId());
		// orgnlObj.set...

		offMonthReportDetailService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 工作月报详情表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public OffMonthReportDetail getById(HttpServletRequest request, HttpServletResponse response, OffMonthReportDetail obj) {
		return offMonthReportDetailService.selectById(obj.getId());
	}

}
