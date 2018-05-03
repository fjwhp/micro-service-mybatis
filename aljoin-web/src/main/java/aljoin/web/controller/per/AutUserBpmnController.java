package aljoin.web.controller.per;

import java.util.ArrayList;
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

import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.object.ActAljoinBpmnVO;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinCategoryService;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.aut.security.CustomUser;
import aljoin.object.PageBean;
import aljoin.sys.iservice.SysParameterService;
import aljoin.web.controller.BaseController;

/**
 * 
 * 用户公共信息表(控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Controller
@RequestMapping("/per/autUserBpmn")
public class AutUserBpmnController extends BaseController {
	  private final static Logger logger = LoggerFactory.getLogger(AutUserBpmnController.class);
	@Resource
	private AutUserPubService autUserPubService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private SysParameterService sysParameterService;
	@Resource
	 private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	 private ActAljoinCategoryService actAljoinCategoryService;
	/**
	 * 
	 * 用户个人管理流程信息表(页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/autUserBpmnPage")
	public String autUserPubPage(HttpServletRequest request, HttpServletResponse response) {

		return "per/autmyManage";
	}

	/**
	 * 
	 * 用户个人管理流程(分页列表).
	 *
	 * @return：Page<AutUserPub>
	 *
	 * @author：laijy
	 *
	 * @date：2017-10-10
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinBpmnVO> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			String bpmnid) {
		Page<ActAljoinBpmnVO> page = null;
		try {
			CustomUser customUser = getCustomDetail();
			page = actAljoinBpmnService.userBpmnlist(pageBean, bpmnid ,customUser.getUserId().toString());
		} catch (Exception e) {			
		  logger.error("", e);
		}
		return page;
	}
	/**
	 * 
	 * 获取用户个人的流程分类
	 *
	 * @return：List<ActAljoinCategory>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月4日 上午10:21:24
	 */
	@RequestMapping("/getAllCategoryList")
	@ResponseBody
	public List<ActAljoinCategory> getAllCategoryList(HttpServletRequest request, HttpServletResponse response) {

		List<ActAljoinCategory> categoryList = new ArrayList<ActAljoinCategory>();
		try {
			CustomUser customUser = getCustomDetail();
			categoryList = actAljoinCategoryService.getUserBpmnList(customUser.getUserId().toString());
		} catch (Exception e) {
		  logger.error("", e);
		}

		return categoryList;
	}

}
