package aljoin.web.controller.tim;

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
import aljoin.tim.dao.entity.TimSchedule;
import aljoin.tim.iservice.TimScheduleService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 任务调度表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-10-26
 */
@Controller
@RequestMapping("/tim/timSchedule")
public class TimScheduleController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(TimScheduleController.class);
	@Resource
	private TimScheduleService timScheduleService;
	
	/**
	 * 
	 * 任务调度表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-10-26
	 */
	@RequestMapping("/timSchedulePage")
	public String timSchedulePage(HttpServletRequest request,HttpServletResponse response) {
		
		return "tim/timSchedulePage";
	}
	
	/**
	 * 
	 * 任务调度表(分页列表).
	 *
	 * @return：Page<TimSchedule>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-10-26
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<TimSchedule> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, TimSchedule obj) {
		Page<TimSchedule> page = null;
		try {
			page = timScheduleService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 任务调度表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-10-26
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, TimSchedule obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		timScheduleService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 任务调度表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-10-26
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, TimSchedule obj) {
		RetMsg retMsg = new RetMsg();

		timScheduleService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 任务调度表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-10-26
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, TimSchedule obj) {
		RetMsg retMsg = new RetMsg();

		TimSchedule orgnlObj = timScheduleService.selectById(obj.getId());
		// orgnlObj.set...

		timScheduleService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 任务调度表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-10-26
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public TimSchedule getById(HttpServletRequest request, HttpServletResponse response, TimSchedule obj) {
		return timScheduleService.selectById(obj.getId());
	}

}
