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

import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 运行时表单数据表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping("/act/actAljoinFormDataRun")
public class ActAljoinFormDataRunController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormDataRunController.class);
	@Resource
	private ActAljoinFormDataRunService actAljoinFormDataRunService;
	
	/**
	 * 
	 * 运行时表单数据表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/actAljoinFormDataRunPage")
	public String actAljoinFormDataRunPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormDataRunPage";
	}
	
	/**
	 * 
	 * 运行时表单数据表(分页列表).
	 *
	 * @return：Page<ActAljoinFormDataRun>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormDataRun> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormDataRun obj) {
		Page<ActAljoinFormDataRun> page = null;
		try {
			page = actAljoinFormDataRunService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 运行时表单数据表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinFormDataRunService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 运行时表单数据表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormDataRunService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 运行时表单数据表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFormDataRun orgnlObj = actAljoinFormDataRunService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinFormDataRunService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 运行时表单数据表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFormDataRun getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataRun obj) {
		return actAljoinFormDataRunService.selectById(obj.getId());
	}

}
