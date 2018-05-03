package aljoin.web.controller.aut;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutUserPost;
import aljoin.aut.dao.object.AutUserPostVO;
import aljoin.aut.iservice.AutUserPostService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * @描述：用户-岗位表(控制器).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
@Controller
@RequestMapping("/aut/autUserPost")
public class AutUserPostController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutUserPostController.class);
	@Resource
	private AutUserPostService autUserPostService;
	
	/**
	 * 
	 * @描述：用户-岗位表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/autUserPostPage")
	public String autUserPostPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "aut/autUserPostPage";
	}
	
	/**
	 * 
	 * @描述：用户-岗位表(分页列表).
	 *
	 * @返回：Page<AutUserPost>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutUserPost> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutUserPost obj) {
		Page<AutUserPost> page = null;
		try {
			page = autUserPostService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * @描述：用户-岗位表(新增).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutUserPostVO obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = autUserPostService.add(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
            
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：用户-岗位表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutUserPost obj) {
		RetMsg retMsg = new RetMsg();

		autUserPostService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：用户-岗位表(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutUserPost obj) {
		RetMsg retMsg = new RetMsg();

		AutUserPost orgnlObj = autUserPostService.selectById(obj.getId());
		// orgnlObj.set...

		autUserPostService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * @描述：用户-岗位表(根据ID获取对象).
	 *
	 * @返回：AutUser
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AutUserPost getById(HttpServletRequest request, HttpServletResponse response, AutUserPost obj) {
		return autUserPostService.selectById(obj.getId());
	}
	
}
