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

import aljoin.act.dao.entity.ActAljoinFormWidgetRun;
import aljoin.act.iservice.ActAljoinFormWidgetRunService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 表单控件表(运行时)(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping("/act/actAljoinFormWidgetRun")
public class ActAljoinFormWidgetRunController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormWidgetRunController.class);
	@Resource
	private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
	
	/**
	 * 
	 * 表单控件表(运行时)(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/actAljoinFormWidgetRunPage")
	public String actAljoinFormWidgetRunPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormWidgetRunPage";
	}
	
	/**
	 * 
	 * 表单控件表(运行时)(分页列表).
	 *
	 * @return：Page<ActAljoinFormWidgetRun>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormWidgetRun> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormWidgetRun obj) {
		Page<ActAljoinFormWidgetRun> page = null;
		try {
			page = actAljoinFormWidgetRunService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 表单控件表(运行时)(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormWidgetRun obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinFormWidgetRunService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单控件表(运行时)(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormWidgetRun obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormWidgetRunService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单控件表(运行时)(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormWidgetRun obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFormWidgetRun orgnlObj = actAljoinFormWidgetRunService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinFormWidgetRunService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 表单控件表(运行时)(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFormWidgetRun getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormWidgetRun obj) {
		return actAljoinFormWidgetRunService.selectById(obj.getId());
	}

}
