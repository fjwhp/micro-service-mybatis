package aljoin.web.controller.act;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.act.dao.entity.ActAljoinFormDataHis;
import aljoin.act.iservice.ActAljoinFormDataHisService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 历史表单数据表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping("/act/actAljoinFormDataHis")
public class ActAljoinFormDataHisController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormDataHisController.class);
	@Resource
	private ActAljoinFormDataHisService actAljoinFormDataHisService;
	
	/**
	 * 
	 * 历史表单数据表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/actAljoinFormDataHisPage")
	public String actAljoinFormDataHisPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormDataHisPage";
	}
	
	/**
	 * 
	 * 历史表单数据表(分页列表).
	 *
	 * @return：Page<ActAljoinFormDataHis>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormDataHis> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormDataHis obj) {
		Page<ActAljoinFormDataHis> page = null;
		try {
			page = actAljoinFormDataHisService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 历史表单数据表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataHis obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinFormDataHisService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 历史表单数据表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataHis obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormDataHisService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 历史表单数据表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataHis obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFormDataHis orgnlObj = actAljoinFormDataHisService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinFormDataHisService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 历史表单数据表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFormDataHis getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataHis obj) {
		return actAljoinFormDataHisService.selectById(obj.getId());
	}

}
