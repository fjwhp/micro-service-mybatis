package aljoin.web.controller.sys;

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
import aljoin.sys.dao.entity.SysOperateLog;
import aljoin.sys.iservice.SysOperateLogService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 操作日志表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-06-05
 */
@Controller
@RequestMapping("/sys/sysOperateLog")
public class SysOperateLogController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(SysOperateLogController.class);
	@Resource
	private SysOperateLogService sysOperateLogService;
	
	/**
	 * 
	 * 操作日志表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-05
	 */
	@RequestMapping("/sysOperateLogPage")
	public String sysOperateLogPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sys/sysOperateLogPage";
	}
	
	/**
	 * 
	 * 操作日志表(分页列表).
	 *
	 * @return：Page<SysOperateLog>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-05
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysOperateLog> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysOperateLog obj) {
		Page<SysOperateLog> page = null;
		try {
			page = sysOperateLogService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 操作日志表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-05
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysOperateLog obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		sysOperateLogService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 操作日志表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-05
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[系统管理]-[日志管理]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysOperateLog obj) {
		RetMsg retMsg = new RetMsg();

		sysOperateLogService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 操作日志表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-05
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysOperateLog obj) {
		RetMsg retMsg = new RetMsg();

		SysOperateLog orgnlObj = sysOperateLogService.selectById(obj.getId());
		// orgnlObj.set...

		sysOperateLogService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 操作日志表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-06-05
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysOperateLog getById(HttpServletRequest request, HttpServletResponse response, SysOperateLog obj) {
		return sysOperateLogService.selectById(obj.getId());
	}

}
