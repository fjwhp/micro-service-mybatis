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

import aljoin.act.dao.entity.ActAljoinFormAttributeRun;
import aljoin.act.iservice.ActAljoinFormAttributeRunService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 表单控属性件表(运行时)(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-27
 */
@Controller
@RequestMapping("/act/actAljoinFormAttributeRun")
public class ActAljoinFormAttributeRunController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormAttributeRunController.class);
	@Resource
	private ActAljoinFormAttributeRunService actAljoinFormAttributeRunService;
	
	/**
	 * 
	 * 表单控属性件表(运行时)(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/actAljoinFormAttributeRunPage")
	public String actAljoinFormAttributeRunPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormAttributeRunPage";
	}
	
	/**
	 * 
	 * 表单控属性件表(运行时)(分页列表).
	 *
	 * @return：Page<ActAljoinFormAttributeRun>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormAttributeRun> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormAttributeRun obj) {
		Page<ActAljoinFormAttributeRun> page = null;
		try {
			page = actAljoinFormAttributeRunService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 表单控属性件表(运行时)(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttributeRun obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinFormAttributeRunService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单控属性件表(运行时)(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttributeRun obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormAttributeRunService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单控属性件表(运行时)(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttributeRun obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFormAttributeRun orgnlObj = actAljoinFormAttributeRunService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinFormAttributeRunService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 表单控属性件表(运行时)(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-27
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFormAttributeRun getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormAttributeRun obj) {
		return actAljoinFormAttributeRunService.selectById(obj.getId());
	}

}
