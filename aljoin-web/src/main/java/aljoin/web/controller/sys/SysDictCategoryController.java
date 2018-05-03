package aljoin.web.controller.sys;

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

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDictCategory;
import aljoin.sys.iservice.SysDictCategoryService;
import aljoin.web.controller.BaseController;

/**
 * 
 * @描述：数据字典分类表(控制器).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-18
 */
@Controller
@RequestMapping("/sys/sysDictCategory")
public class SysDictCategoryController extends BaseController {
    private final static Logger logger = LoggerFactory.getLogger(SysDictCategoryController.class);
	@Resource
	private SysDictCategoryService sysDictCategoryService;
	
	/**
	 * 
	 * @描述：数据字典分类表(页面).
	 *
	 * @返回：String
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/sysDictCategoryPage")
	public String sysDictCategoryPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "sys/sysDictCategoryPage";
	}
	
	/**
	 * 
	 * @描述：数据字典分类表(分页列表).
	 *
	 * @返回：Page<SysDictCategory>
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<SysDictCategory> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, SysDictCategory obj) {
		Page<SysDictCategory> page = null;
		try {
			page = sysDictCategoryService.list(pageBean, obj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return page;
	}
	

	/**
	 * 
	 * @描述：数据字典分类表(新增分类).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, SysDictCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysDictCategoryService.add(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：数据字典分类表(根据ID删除对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, SysDictCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysDictCategoryService.delete(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
	
	/**
	 * 
	 * @描述：数据字典分类表(根据ID修改对象).
	 *
	 * @返回：RetMsg
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, SysDictCategory obj) {
		RetMsg retMsg = new RetMsg();
		try {
            retMsg = sysDictCategoryService.update(obj);
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(1);
            retMsg.setMessage("操作失败");
        }
		return retMsg;
	}
    
	/**
	 * 
	 * @描述：数据字典分类表(根据ID获取对象).
	 *
	 * @返回：SysDictCategory
	 *
	 * @作者：caizx
	 *
	 * @时间：2018-03-18
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public SysDictCategory getById(HttpServletRequest request, HttpServletResponse response, SysDictCategory obj) {
		return sysDictCategoryService.selectById(obj.getId());
	}
	
	/**
     * 
     * @描述：获取数据字典分类列表.
     *
     * @返回：List<SysDictCategory>
     *
     * @作者：caizx
     *
     * @时间：2018-03-18
     */
    @RequestMapping("/getCategoryList")
    @ResponseBody
    public List<SysDictCategory> getCategoryList(HttpServletRequest request, HttpServletResponse response, SysDictCategory obj) {
        return sysDictCategoryService.getCategoryList(obj);
    }

}
