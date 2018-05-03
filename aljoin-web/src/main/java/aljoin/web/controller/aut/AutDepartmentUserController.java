package aljoin.web.controller.aut;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import com.baomidou.mybatisplus.plugins.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * (控制器).
 * 
 * @author：laijy
 * 
 * @date： 2017-08-21
 */
@Controller
@RequestMapping(value = "/aut/autDepartmentUser", method = RequestMethod.POST)
@Api(value = "岗位管理", description = "权限管理->部门岗位接口")
public class AutDepartmentUserController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutDepartmentUserController.class);

	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private AutDepartmentService autDepartmentService;
	@Resource
	private AutUserService autUserService;
	@Resource
	private ActActivitiService activitiService;

	/**
	 * 
	 * (页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-21
	 */
	@RequestMapping("/autDepartmentUserPage")
	public String autDepartmentUserPage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autDepartmentUserPage";
	}

	/**
	 * 
	 * (分页列表).
	 *
	 * @return：Page<AutDepartmentUser>
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-21
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutDepartmentUser> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutDepartmentUser obj) {
		Page<AutDepartmentUser> page = null;
		try {
			page = autDepartmentUserService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * (新增)
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-21
	 */
	@RequestMapping("/addDepartmentUser")
	@ResponseBody
	public RetMsg addDepartmentUser(HttpServletRequest request, HttpServletResponse response,
			AutDepartmentUser departmentUser) {
		RetMsg retMsg = autDepartmentUserService.addDepartmentUser(departmentUser);
		return retMsg;
	}

	/**
	 * 
	 * (根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-21
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutDepartmentUser obj) {

		RetMsg retMsg = new RetMsg();
		autDepartmentUserService.deleteById(obj.getId());
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * (根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-21
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, AutDepartmentUser obj) {
		RetMsg retMsg = new RetMsg();
		AutDepartmentUser orgnlObj = autDepartmentUserService.selectById(obj.getId());
		orgnlObj.setDepartmentUserRank(obj.getDepartmentUserRank());
		orgnlObj.setIsLeader(obj.getIsLeader());
		autDepartmentUserService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	/**
	 * 
	 * (根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-21
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AutDepartmentUser getById(HttpServletRequest request, HttpServletResponse response, AutDepartmentUser obj) {
		return autDepartmentUserService.selectById(obj.getId());
	}

	/**
	 * 
	 * 给用户分配部门
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月12日 下午5:19:45
	 */
	@RequestMapping("/addUserDepartment")
	@ResponseBody
	public RetMsg addUserDepartment(HttpServletRequest request, HttpServletResponse response,
			AutDepartmentUserVO departmentUserVO) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autDepartmentUserService.userAddDepartment(departmentUserVO);
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage("操作失败");
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * 根据用户id查询该用户所在部门
	 *
	 * @return：List<String>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月12日 下午5:20:17
	 */
	@RequestMapping("/getDeptByUserId")
	@ResponseBody
	public List<String> getDeptByUserId(AutUser autUser) throws Exception {

		List<AutDepartmentUser> deptIdList = autDepartmentUserService.getDeptByUserId(autUser.getId());
		// 前台接收到的Long类型会被四舍五入，这里要拼成String
		List<String> deptIdStrlist = new ArrayList<String>();
		for (int i = 0; i < deptIdList.size(); i++) {
			deptIdStrlist.add(deptIdList.get(i).getDeptId() + "");
		}
		return deptIdStrlist;
	}

	/**
	 * 
	 * @throws Exception 
	 * 删除部门用户(须同时删除该用户-岗位表里的记录)
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月25日 上午9:03:29
	 */
	@RequestMapping(value = "/deleteDeptUser")
	@ResponseBody
	public RetMsg deleteDeptUser(HttpServletRequest request, HttpServletResponse response, AutDepartmentUser obj) throws Exception{
	
		RetMsg retMsg = new RetMsg();
		retMsg = autDepartmentUserService.deleteDeptUser(obj);
		return retMsg;
	}
	
	/**
	 * 
	 * 根据部门id查询部门下的用户
	 *
	 * @return：AutDepartmentUserVO
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月25日 下午2:15:12
	 */
	@RequestMapping(value = "/getUserByDeptId")
	@ApiOperation(value = "获取部门下的用户")
	@ApiImplicitParam(name = "AutDepartment.id", value = "部门id", required = true, dataType = "int", paramType = "query")
	@ResponseBody
	public AutDepartmentUserVO getUserByDeptId(AutDepartment obj) throws Exception {
		AutDepartmentUserVO vo = autDepartmentUserService.getUserByDeptId(obj);
		return vo;
	}
}
