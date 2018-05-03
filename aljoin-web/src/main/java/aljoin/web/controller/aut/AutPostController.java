package aljoin.web.controller.aut;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutPost;
import aljoin.aut.iservice.AutPostService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * @描述：岗位表(控制器).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
@Controller
@RequestMapping("/aut/autPost")
public class AutPostController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(AutPostController.class);
	@Resource
	private AutPostService autPostService;
	
	/**
	 * 
	 * @描述：岗位表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/autPostPage")
	public String autPostPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "aut/autPostPage";
	}
	
	/**
	 * 
	 * @描述：岗位表(分页列表).
	 *
	 * @返回：Page<AutPost>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutPost> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, AutPost obj) {
		Page<AutPost> page = null;
		try {
			page = autPostService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * @描述：岗位表(新增).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutPost obj) {
		RetMsg retMsg = new RetMsg();
		obj.setIsActive(1);
		autPostService.insert(obj);
		retMsg.setCode(0);
		retMsg.setObject(obj.getId());
		retMsg.setMessage("操作成功");
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：岗位表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutPost obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = autPostService.delete(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：岗位表(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutPost obj) {
		RetMsg retMsg = new RetMsg();

		AutPost orgnlObj = autPostService.selectById(obj.getId());
		// orgnlObj.set...

		autPostService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * @描述：岗位表(根据ID获取对象).
	 *
	 * @返回：AutUser
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-04-09
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AutPost getById(HttpServletRequest request, HttpServletResponse response, AutPost obj) {
		return autPostService.selectById(obj.getId());
	}
	
	/**
     * 
     * @描述：岗位表(获得岗位列表).
     *
     * @返回：List<AutPost>
     *
     * @作者：caizx
     *
     * @时间：2018-04-09
     */
    @RequestMapping("/getPostList")
    @ResponseBody
    public List<AutPost> getPostList(HttpServletRequest request, HttpServletResponse response) {
        Where<AutPost> where = new Where<AutPost>();
        where.orderBy("post_rank,id",true);
        return autPostService.selectList(where);
    }
    

}
