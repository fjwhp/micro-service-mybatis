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

import aljoin.act.dao.entity.ActAljoinDelegateInfoHis;
import aljoin.act.iservice.ActAljoinDelegateInfoHisService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）,历史表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Controller
@RequestMapping("/act/actAljoinDelegateInfoHis")
public class ActAljoinDelegateInfoHisController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinDelegateInfoHisController.class);
	@Resource
	private ActAljoinDelegateInfoHisService actAljoinDelegateInfoHisService;
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）,历史表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/actAljoinDelegateInfoHisPage")
	public String actAljoinDelegateInfoHisPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinDelegateInfoHisPage";
	}
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）,历史表(分页列表).
	 *
	 * @return：Page<ActAljoinDelegateInfoHis>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinDelegateInfoHis> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinDelegateInfoHis obj) {
		Page<ActAljoinDelegateInfoHis> page = null;
		try {
			page = actAljoinDelegateInfoHisService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）,历史表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfoHis obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinDelegateInfoHisService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）,历史表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfoHis obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinDelegateInfoHisService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）,历史表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfoHis obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinDelegateInfoHis orgnlObj = actAljoinDelegateInfoHisService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinDelegateInfoHisService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）,历史表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinDelegateInfoHis getById(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfoHis obj) {
		return actAljoinDelegateInfoHisService.selectById(obj.getId());
	}

}
