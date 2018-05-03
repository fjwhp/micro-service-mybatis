package aljoin.web.controller.aut;

import aljoin.act.iservice.ActActivitiService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.web.controller.BaseController;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import io.swagger.annotations.Api;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
 * @date： 2017-08-15
 */
@Controller
@RequestMapping("/aut/autDepartment")
@Api(value = "部门管理", description = "流程管理->流程设计接口")
public class AutDepartmentController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(AutDepartmentController.class);
  @Resource
  private AutDepartmentService autDepartmentService;
  @Resource
  private IdentityService identityService;
  @Resource
  private ActActivitiService activitiService;
  @Resource
  private AutDepartmentUserService autDepartmentUserService;

	/**
	 * 
	 * (页面).
	 *
	 * @return：String
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-15
	 */
	@RequestMapping("/autDepartmentPage")
	public String autDepartmentPage(HttpServletRequest request, HttpServletResponse response) {

		return "aut/autDepartmentPage";
	}

	/**
	 *
	 * (分页列表).
	 *
	 * @return：Page<AutDepartment>
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-15
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<AutDepartment> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutDepartment obj) {
		Page<AutDepartment> page = null;
		try {
			page = autDepartmentService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;

	}

	/**
	 * 
	 * (新增).
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-15
	 */
	@RequestMapping("/add")
	@ResponseBody
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, AutDepartment obj) {
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autDepartmentService.add(obj);
		} catch (Exception e) {
			retMsg.setCode(1);
			logger.error("", e);
		}
		return retMsg;
	}

	/**
	 * 
	 * (根据ID删除对象).
	 *
	 * @throws Exception 
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017-08-15
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, AutDepartment obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		try {
			autDepartmentService.delete(obj);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}

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
	 * @date：2017-08-15
	 */
	@RequestMapping("/updateById")
	@ResponseBody
	public RetMsg updateById(HttpServletRequest request, HttpServletResponse response, AutDepartment obj) {
		RetMsg retMsg = new RetMsg();
		// 根据ID查询部门信息
		AutDepartment orgnlObj = autDepartmentService.selectById(obj.getId());
		// 更新部门信息
		orgnlObj.setDeptName(obj.getDeptName());
		orgnlObj.setDeptRank(obj.getDeptRank());
		orgnlObj.setIsActive(obj.getIsActive());

		autDepartmentService.updateById(orgnlObj);
		Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
		departmentUserWhere.eq("dept_id",orgnlObj.getId());
		departmentUserWhere.setSqlSelect("id,dept_id,user_id");
		List<AutDepartmentUser> departmentUserList = autDepartmentUserService.selectList(departmentUserWhere);
		//如果是冻结 则删除 act_id_group 和 act_id_membership表记录
		if(orgnlObj.getIsActive() == 0){
			//删除部门在 act_id_group 表中记录
			activitiService.delGroup(orgnlObj.getId());
			if(null != departmentUserList && !departmentUserList.isEmpty()){
				for(AutDepartmentUser departmentUser : departmentUserList){
					if(null != departmentUser && null != departmentUser.getUserId() && null != departmentUser.getDeptId()){
						//删除部门用户 在 act_id_membership 表中记录
						activitiService.delUserGroup(departmentUser.getUserId(),departmentUser.getDeptId());
					}
				}
			}
		}else{//如果是激活 则添加记录到 act_id_group 和 act_id_membership

				activitiService.delGroup(obj.getId());
				//添加部门到 act_id_group 表中
				Group group = identityService.newGroup(obj.getId()+"");//先删除再新增以免主键冲突
				if(null != group){
					group.setType("DEPARTMENT");
					identityService.saveGroup(group);
				}

			if(null != departmentUserList && !departmentUserList.isEmpty()){
				List<Group> groupList = identityService.createGroupQuery().list();
				List<Long> groupIdList = new ArrayList<Long>();
				if(null != groupList && !groupList.isEmpty()){
					for(Group gp : groupList){
						if(StringUtils.isNotEmpty(gp.getId())){
							groupIdList.add(Long.parseLong(gp.getId()));
						}
					}
				}

				List<User> userList = identityService.createUserQuery().list();
				List<Long> userIdList = new ArrayList<Long>();
				if(null != userList && !userList.isEmpty()){
					for(User user : userList){
						if(StringUtils.isNotEmpty(user.getId())){
							userIdList.add(Long.parseLong(user.getId()));
						}
					}
				}
				for(AutDepartmentUser departmentUser : departmentUserList){
					if(null != departmentUser && null != departmentUser.getUserId() && null != departmentUser.getDeptId()){
						if(groupIdList.contains(departmentUser.getDeptId()) && userIdList.contains(departmentUser.getUserId())){
							//添加部门用户关系到act_id_membership
							//activitiService.delUserGroup(departmentUser.getUserId(),departmentUser.getDeptId());
							activitiService.addUserGroup(departmentUser.getUserId(),departmentUser.getDeptId());//先删除再新增以免主键冲突
						}
					}
				}
			}
		}
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
	 * @date：2017-08-15
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public AutDepartment getById(HttpServletRequest request, HttpServletResponse response, AutDepartment obj) {

		AutDepartment autDepartment = autDepartmentService.selectById(obj.getId());

		return autDepartment;
	}

	/**
	 * 
	 * 根据父级部门id查部门信息
	 * 
	 * @返回:List<AutDepartment>
	 * 
	 * @author：laijy
	 * 
	 * @date：2017年8月17日 上午10:11:17
	 */
	@RequestMapping("/selectDeptByParentId")
	@ResponseBody
	public List<AutDepartment> selectDeptByParentId(HttpServletRequest request, HttpServletResponse response,
			AutDepartment obj) throws Exception {
		List<AutDepartment> autDepartmentList = new ArrayList<AutDepartment>();
		Where<AutDepartment> where = new Where<AutDepartment>();
		where.eq("parent_id", obj.getParentId());
		where.orderBy("dept_rank", true);
		where.setSqlSelect("dept_name,dept_rank,dept_level,is_active");
		autDepartmentList = autDepartmentService.selectList(where);
		return autDepartmentList;
	}

	@RequestMapping("/getDepartmentList")
	@ResponseBody
	public List<AutDepartment> getDepartmentList(HttpServletRequest request, HttpServletResponse response) {
		List<AutDepartment> list = autDepartmentService.getDepartmentList();
		return list;
	}
	
	/**
	 * 
	 * 删除部门确认(判断部门是否还有下属部门/用户)
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月19日 上午9:28:00
	 */
	@RequestMapping("/deleteConfirm")
	@ResponseBody
	public RetMsg deleteConfirm(Long id,String deptCode){
		
		RetMsg retMsg = new RetMsg();
		try {
			retMsg = autDepartmentService.deleteConfirm(id,deptCode);
			retMsg.setCode(0);
		} catch (Exception e) {
		  logger.error("", e);
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}
	
	/**
	 * 
	 * 激活的部门(分页列表).
	 *
	 * @return：Page<AutDepartment>
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月9日 上午9:09:00
	 */
	@RequestMapping("/listIsActive")
	@ResponseBody
	public Page<AutDepartment> listIsActive(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutDepartment obj) {
		Page<AutDepartment> page = null;
		try {
			page = autDepartmentService.listIsActive(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;

	}
	
	/**
	 * 
	 * 获得部门及下属部门
	 *
	 * @return：List<AutDepartment>
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月25日 下午10:22:02
	 */
	@RequestMapping("/getChildDeptList")
	@ResponseBody
	public List<AutDepartment> getChildDeptList(AutDepartment obj) throws Exception{
		
		List<AutDepartment> childDeptList =  autDepartmentService.getChildDeptList(obj);
		return childDeptList;
		
	}
	
	/**
	 * 
	 * 获得本部门及下属部门的所有用户
	 *
	 * @return：List<AutUser>
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月25日 下午10:39:54
	 */
	@RequestMapping("/getChildDeptUserList")
	@ResponseBody
	public List<AutUser> getChildDeptUserList(AutDepartment obj) throws Exception{
		
		List<AutUser> autUserList = autDepartmentService.getChildDeptUserList(obj);
		return autUserList;
		
	}
	
	/**
	 * 
	 * 获得本部门及下属部门（分页列表）
	 *
	 * @return：Page<AutDepartment>
	 *
	 * @author：laijy
	 *
	 * @date：2017年10月26日 上午8:28:52
	 */
	@RequestMapping("/getChildDeptPage")
	@ResponseBody
	public Page<AutDepartment> getChildDeptPage(AutDepartment obj,PageBean pageBean) throws Exception{
		
		Page<AutDepartment> page = autDepartmentService.getChildDeptPage(obj,pageBean);
		
		return page;
		
	}
	
}
