package aljoin.web.controller.ioa;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import aljoin.ioa.dao.object.IoaReceiveReadUserDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.ioa.dao.entity.IoaReceiveReadUser;
import aljoin.ioa.iservice.IoaReceiveReadUserService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 收文阅件-用户评论(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Controller
@RequestMapping("/ioa/ioaReceiveReadUser")
public class IoaReceiveReadUserController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(IoaReceiveReadUserController.class);
	@Resource
	private IoaReceiveReadUserService ioaReceiveReadUserService;
	
	/**
	 * 
	 * 收文阅件-用户评论(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/ioaReceiveReadUserPage")
	public String ioaReceiveReadUserPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "ioa/ioaReceiveReadUserPage";
	}
	
	/**
	 * 
	 * 收文阅件-用户评论(分页列表).
	 *
	 * @return：Page<IoaReceiveReadUser>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<IoaReceiveReadUserDO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, IoaReceiveReadUser obj) {
		Page<IoaReceiveReadUserDO> page = null;
		try {
			page = ioaReceiveReadUserService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 收文阅件-用户评论(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadUser obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		ioaReceiveReadUserService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 收文阅件-用户评论(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadUser obj) {
		RetMsg retMsg = new RetMsg();

		ioaReceiveReadUserService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 收文阅件-用户评论(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadUser obj) {
		RetMsg retMsg = new RetMsg();

		IoaReceiveReadUser orgnlObj = ioaReceiveReadUserService.selectById(obj.getId());
		// orgnlObj.set...

		ioaReceiveReadUserService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 收文阅件-用户评论(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public IoaReceiveReadUser getById(HttpServletRequest request, HttpServletResponse response, IoaReceiveReadUser obj) {
		return ioaReceiveReadUserService.selectById(obj.getId());
	}

	/**
	 *
	 * 传阅情况.
	 *
	 * @return：IoaReceiveReadUserDO
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-16
	 */
	@RequestMapping("/circulation")
	@ResponseBody
	public IoaReceiveReadUserDO circulation(HttpServletRequest request, HttpServletResponse response, String processInstanceId) {
		IoaReceiveReadUserDO readUserDO = null;
		try {
			readUserDO = ioaReceiveReadUserService.circulation(processInstanceId);
		}catch (Exception e){
			logger.error("",e);
		}
		return readUserDO;
	}

}
