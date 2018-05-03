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

import aljoin.act.dao.entity.ActAljoinQuery;
import aljoin.act.iservice.ActAljoinQueryService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 流程查询表(控制器).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-02
 */
@Controller
@RequestMapping("/act/actAljoinQuery")
public class ActAljoinQueryController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(ActAljoinQueryController.class);
    
	@Resource
	private ActAljoinQueryService actAljoinQueryService;
	
	/**
	 * 
	 * 流程查询表(页面).
	 *
	 * @return：String
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-02
	 */
	@RequestMapping("/actAljoinQueryPage")
	public String actAljoinQueryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinQueryPage";
	}
	
	/**
	 * 
	 * 流程查询表(分页列表).
	 *
	 * @return：Page<ActAljoinQuery>
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-02
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinQuery> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinQuery obj) {
		Page<ActAljoinQuery> page = null;
		try {
			page = actAljoinQueryService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("",e);
		}
		return page;
	}
	

	/**
	 * 
	 * 流程查询表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-02
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ActAljoinQuery obj) {
		RetMsg retMsg = new RetMsg();

		// obj.set...

		actAljoinQueryService.insert(obj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 流程查询表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-02
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinQuery obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinQueryService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * 流程查询表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-02
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinQuery obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinQuery orgnlObj = actAljoinQueryService.selectById(obj.getId());
		// orgnlObj.set...

		actAljoinQueryService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 流程查询表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-02
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinQuery getById(HttpServletRequest request, HttpServletResponse response, ActAljoinQuery obj) {
		return actAljoinQueryService.selectById(obj.getId());
	}

}
