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

import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import aljoin.act.iservice.ActAljoinDelegateInfoService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Controller
@RequestMapping("/act/actAljoinDelegateInfo")
public class ActAljoinDelegateInfoController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinDelegateInfoController.class);
  @Resource
	private ActAljoinDelegateInfoService actAljoinDelegateInfoService;
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/actAljoinDelegateInfoPage")
	public String actAljoinDelegateInfoPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinDelegateInfoPage";
	}
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）(分页列表).
	 *
	 * @return：Page<ActAljoinDelegateInfo>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinDelegateInfo> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinDelegateInfo obj) {
		Page<ActAljoinDelegateInfo> page = null;
		try {
			page = actAljoinDelegateInfoService.list(pageBean, obj);
		} catch (Exception e) {
			logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfo obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinDelegateInfoService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfo obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinDelegateInfoService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfo obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinDelegateInfo orgnlObj = actAljoinDelegateInfoService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinDelegateInfoService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 任务委托信息表（存放具体的委托任务数据）(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinDelegateInfo getById(HttpServletRequest request, HttpServletResponse response, ActAljoinDelegateInfo obj) {
		return actAljoinDelegateInfoService.selectById(obj.getId());
	}

}
