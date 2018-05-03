package aljoin.web.controller.ioa;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import aljoin.ioa.iservice.IoaReceiveReadObjectService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 收文阅件-传阅对象表(分类)(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Controller
@RequestMapping("/ioa/ioaReceiveReadObject")
public class IoaReceiveReadObjectController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(IoaReceiveReadObjectController.class);
	@Resource
	private IoaReceiveReadObjectService ioaReceiveReadObjectService;
	
	/**
	 * 
	 * 收文阅件-传阅对象表(分类)(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/ioaReceiveReadObjectPage")
	public String ioaReceiveReadObjectPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "ioa/ioaReceiveReadObjectPage";
	}
	
	/**
	 * 
	 * 收文阅件-传阅对象表(分类)(分页列表).
	 *
	 * @return：Page<IoaReceiveReadObject>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<IoaReceiveReadObject> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, IoaReceiveReadObject obj) {
		Page<IoaReceiveReadObject> page = null;
		try {
			page = ioaReceiveReadObjectService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 收文阅件-传阅对象表(分类)(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadObject obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		ioaReceiveReadObjectService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 收文阅件-传阅对象表(分类)(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadObject obj) {
		RetMsg retMsg = new RetMsg();

		ioaReceiveReadObjectService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 收文阅件-传阅对象表(分类)(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadObject obj) {
		RetMsg retMsg = new RetMsg();

		IoaReceiveReadObject orgnlObj = ioaReceiveReadObjectService.selectById(obj.getId());
		// orgnlObj.set...

		ioaReceiveReadObjectService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 收文阅件-传阅对象表(分类)(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public IoaReceiveReadObject getById(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadObject obj) {
		return ioaReceiveReadObjectService.selectById(obj.getId());
	}

}
