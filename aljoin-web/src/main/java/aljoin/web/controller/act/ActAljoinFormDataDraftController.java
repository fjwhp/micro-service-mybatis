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

import aljoin.act.dao.entity.ActAljoinFormDataDraft;
import aljoin.act.iservice.ActAljoinFormDataDraftService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 表单数据表(草稿)(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-09-30
 */
@Controller
@RequestMapping("/act/actAljoinFormDataDraft")
public class ActAljoinFormDataDraftController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinFormDataDraftController.class);
	@Resource
	private ActAljoinFormDataDraftService actAljoinFormDataDraftService;
	
	/**
	 * 
	 * 表单数据表(草稿)(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/actAljoinFormDataDraftPage")
	public String actAljoinFormDataDraftPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinFormDataDraftPage";
	}
	
	/**
	 * 
	 * 表单数据表(草稿)(分页列表).
	 *
	 * @return：Page<ActAljoinFormDataDraft>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinFormDataDraft> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinFormDataDraft obj) {
		Page<ActAljoinFormDataDraft> page = null;
		try {
			page = actAljoinFormDataDraftService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	

	/**
	 * 
	 * 表单数据表(草稿)(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataDraft obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinFormDataDraftService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单数据表(草稿)(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataDraft obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinFormDataDraftService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 表单数据表(草稿)(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataDraft obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinFormDataDraft orgnlObj = actAljoinFormDataDraftService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinFormDataDraftService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 表单数据表(草稿)(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinFormDataDraft getById(HttpServletRequest request, HttpServletResponse response, ActAljoinFormDataDraft obj) {
		return actAljoinFormDataDraftService.selectById(obj.getId());
	}
	
	/**
	 * 
	 * 关闭流程
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-09-30
	 */
	@RequestMapping("/closeTask")
	@ResponseBody
	public RetMsg closeTask(HttpServletRequest request, HttpServletResponse response, String activityId) {
		RetMsg retMsg = new RetMsg();
		try {
			actAljoinFormDataDraftService.closeTask(activityId);
			retMsg.setCode(0);
			retMsg.setMessage("关闭成功");
		} catch (Exception e) {
			retMsg.setCode(0);
			retMsg.setMessage("关闭失败");
		}
		
		return retMsg;
	}
}
