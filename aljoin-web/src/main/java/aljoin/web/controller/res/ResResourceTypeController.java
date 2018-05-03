package aljoin.web.controller.res;

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
import aljoin.res.dao.entity.ResResourceType;
import aljoin.res.iservice.ResResourceTypeService;
import aljoin.web.annotation.FuncObj;
import aljoin.web.controller.BaseController;

/**
 * 
 * 资源分类表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-05
 */
@Controller
@RequestMapping("/res/resResourceType")
public class ResResourceTypeController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ResResourceTypeController.class);
	@Resource
	private ResResourceTypeService resResourceTypeService;

	/**
	 * 
	 * 资源分类表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/resResourceTypePage")
	public String resResourceTypePage(HttpServletRequest request, HttpServletResponse response) {

		return "res/resResourceTypePage";
	}

	/**
	 * 
	 * 资源分类表(分页列表).
	 *
	 * @return：Page<ResResourceType>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/list")
	@ResponseBody
	@FuncObj(desc = "[资源管理]-[资源管理]-[搜索]")
	public Page<ResResourceType> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			ResResourceType obj) {
		Page<ResResourceType> page = null;
		try {
			page = resResourceTypeService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 资源分类表(新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/add")
	@ResponseBody
	@FuncObj(desc = "[资源管理]-[资源分类]-[新增]")
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, ResResourceType obj) {

		RetMsg retMsg = new RetMsg();
		try {
			// 判断同级分类数量不超过999才新增
			if (!resResourceTypeService.outNumber(obj)) {
				if (obj.getParentId() == null) {
					obj.setParentId(0L);
					obj.setTypeLevel(1);
				}
				// 验证分类名称唯一才新增
				if (resResourceTypeService.compareTypeName(obj)) {
					resResourceTypeService.insert(obj);
					retMsg.setMessage("操作成功");
					retMsg.setCode(0);
				} else {
					retMsg.setMessage("错误！分类名称已存在，请重新命名");
					retMsg.setCode(1);
				}
			}
		} catch (Exception e) {
			retMsg.setMessage(e.getMessage());
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 资源分类表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/delete")
	@ResponseBody
	@FuncObj(desc = "[资源管理]-[资源分类]-[删除]")
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ResResourceType obj) {
		RetMsg retMsg = new RetMsg();

		resResourceTypeService.delete(obj);

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * 资源分类表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/update")
	@ResponseBody
	@FuncObj(desc = "[资源管理]-[资源分类]-[更新]")
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ResResourceType obj) {
		RetMsg retMsg = new RetMsg();
		try {
			ResResourceType orgnlObj = resResourceTypeService.selectById(obj.getId());
			orgnlObj.setTypeRank(obj.getTypeRank());
			orgnlObj.setIsActive(obj.getIsActive());
			// 判断是否有对分类名称修改，有则要判断修改后的名称是否已存在
			if (!orgnlObj.getTypeName().equals(obj.getTypeName())) {
				// 判断修改的名字不跟已有的冲突，才进行修改
				if (resResourceTypeService.compareTypeName(obj)) {
					orgnlObj.setTypeName(obj.getTypeName());
					resResourceTypeService.updateById(orgnlObj);
					retMsg.setMessage("修改成功");
					retMsg.setCode(0);
				} else {
					retMsg.setMessage("错误！分类名称已存在，请重命名！");
					retMsg.setCode(1);
				}
			} else {// 如果分类名称没有修改，直接插入
				resResourceTypeService.updateById(orgnlObj);
				retMsg.setCode(0);
				retMsg.setMessage("修改成功");
			}
		} catch (Exception e) {
			retMsg.setMessage(e.getMessage());
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 资源分类详情(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	@RequestMapping("/getById")
	@ResponseBody
	@FuncObj(desc = "[资源管理]-[资源分类]-[详情]")
	public ResResourceType getById(HttpServletRequest request, HttpServletResponse response, ResResourceType obj) {
		return resResourceTypeService.getById(obj);
	}

}
