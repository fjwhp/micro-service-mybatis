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

import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.object.AutUserPositionVO;
import aljoin.aut.iservice.AutUserPositionService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;

/**
 * 
 * 用户-岗位表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-01
 */
@Controller
@RequestMapping("/aut/autUserPosition")
public class AutUserPositionController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutUserPositionController.class);
	@Resource
	private AutUserPositionService autUserPositionService;

	/**
	 * 
	 * 用户-岗位表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-01
	 */
	@RequestMapping("/autUserPositionPage")
	public String autUserPositionPage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autUserPositionPage";
	}

	/**
	 * 
	 * 用户-岗位表(分页列表).
	 *
	 * @return：Page<AutUserPosition>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-01
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutUserPosition> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutUserPosition obj) {
		Page<AutUserPosition> page = null;
		try {
			page = autUserPositionService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 权限管理-用户管理-新增用户岗位（新增多个岗位）
	 *
	 * 								@return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-01
	 */
	@RequestMapping("/addUserPositionList")
	@ResponseBody
	public RetMsg addUserPositionList(HttpServletRequest request, HttpServletResponse response,
			AutUserPositionVO userPositionVO) {
		RetMsg retMsg = new RetMsg();

		try {
			// 拼接AutUser对象和List<AutPosition>，批量插入
			List<AutUserPosition> userPositionList = autUserPositionService.addUserPositionList(userPositionVO);
			autUserPositionService.insertBatch(userPositionList);
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
		  logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 用户-岗位表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-01
	 */
	@RequestMapping("/deleteUserPosition")
	@ResponseBody
	public RetMsg deleteUserPosition(HttpServletRequest request, HttpServletResponse response, AutUserPosition obj) {

		RetMsg retMsg = new RetMsg();

		try {
			retMsg = autUserPositionService.deleteUserPosition(obj);
			retMsg.setCode(0);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}

		return retMsg;
	}

	/**
	 * 
	 * 权限管理-用户管理-新增用户岗位（新增单个岗位）
	 *
	 * 								@return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月1日 下午3:45:35
	 */
	@RequestMapping("/addUserPosition")
	@ResponseBody
	public RetMsg addUserPosition(HttpServletRequest request, HttpServletResponse response, AutUserPosition obj) {

		RetMsg retMsg = new RetMsg();

		try {
			retMsg = autUserPositionService.addUserPosition(obj);
			retMsg.setCode(0);
		} catch (Exception e) {
			retMsg.setMessage("新增失败");
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}

	@RequestMapping("/getPositoinByUserId")
	@ResponseBody
	public List<AutUserPosition> getPositoinByUserId(HttpServletRequest request, HttpServletResponse response,
			AutUserPosition obj) throws Exception {
		return autUserPositionService.getPositoinByUserId(obj);
	}

}
