package aljoin.web.controller.act;

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

import aljoin.act.dao.entity.ActAljoinBpmnUser;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutUsersVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.web.controller.BaseController;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 
 * 流程-用户关系表(控制器).
 * 
 * @author：pengsp
 * 
 * @date： 2017-10-12
 */
@Controller
@RequestMapping("/act/actAljoinBpmnUser")
public class ActAljoinBpmnUserController extends BaseController {
  private final static Logger logger = LoggerFactory.getLogger(ActAljoinBpmnUserController.class);
	@Resource
	private ActAljoinBpmnUserService actAljoinBpmnUserService;
	@Resource
	private AutDepartmentService autDepartmentService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private AutUserService autUserService;
	/**
	 * 
	 * 流程-用户关系表(页面).
	 *
	 * @return：String
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/actAljoinBpmnUserPage")
	public String actAljoinBpmnUserPage(HttpServletRequest request,HttpServletResponse response) {
		
		return "act/actAljoinBpmnUserPage";
	}
	
	/**
	 * 
	 * 流程-用户关系表(分页列表).
	 *
	 * @return：Page<ActAljoinBpmnUser>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Page<ActAljoinBpmnUser> list(HttpServletRequest request, HttpServletResponse response, PageBean pageBean, ActAljoinBpmnUser obj) {
		Page<ActAljoinBpmnUser> page = null;
		try {
			page = actAljoinBpmnUserService.list(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
	
	/**
	 * 
	 * 流程-用户关系表(根据ID删除对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public RetMsg delete(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnUser obj) {
		RetMsg retMsg = new RetMsg();

		actAljoinBpmnUserService.deleteById(obj.getId());

		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;  
	}
	
	/**
	 * 
	 * 流程-用户关系表(根据ID修改对象).
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/update")
	@ResponseBody
	public RetMsg update(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnUser obj) {
		RetMsg retMsg = new RetMsg();

		ActAljoinBpmnUser orgnlObj = actAljoinBpmnUserService.selectById(obj.getId());
		actAljoinBpmnUserService.updateById(orgnlObj);
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}
    
	/**
	 * 
	 * 流程-用户关系表(根据ID获取对象).
	 *
	 * @return：AutUser
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/getById")
	@ResponseBody
	public ActAljoinBpmnUser getById(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnUser obj) {
		return actAljoinBpmnUserService.selectById(obj.getId());
	}
	
	/**
	 * 
	 * 获取部门列表
	 *
	 * @return：List<AutDepartment>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/getDepartmentList")
	@ResponseBody
	@ApiOperation(value = "获取部门列表接口")
	public List<AutDepartment> getDepartmentList(HttpServletRequest request, HttpServletResponse response) {
		List<AutDepartment> list = autDepartmentService.getDepartmentList();
		return list;
	}
	
	/**
	 * 
	 * 根据部门id获取用户
	 *
	 * @return：List<AutUser>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12 
	 */
	@RequestMapping("/getAutDepartmentUserList")
	@ResponseBody
	@ApiOperation(value = "根据部门id获取用户接口") 
	@ApiImplicitParam(name = "deptId", value = "部门ID", required = true, dataType = "string",
		paramType = "query")
	public Page<AutUsersVO>  getAutDepartmentUserList(HttpServletRequest request, HttpServletResponse response,PageBean pageBean,String deptId,String userids,String manids) {
		Page<AutUsersVO> page=new Page<AutUsersVO>();
		//Page<AutUsersVO> vopage=new Page<AutUsersVO>();   
		Page<AutDepartmentUser> deptpage=new Page<AutDepartmentUser>();
		List<AutDepartmentUser> autDepartmentUserList = new ArrayList<AutDepartmentUser>();
		List<AutUsersVO> autUserList = new ArrayList<AutUsersVO>();
		Where<AutDepartmentUser> where = new Where<AutDepartmentUser>();
		where.eq("dept_id", deptId);
		where.eq("is_active", 1);
		where.eq("is_delete", 0);
		deptpage = autDepartmentUserService.selectPage(new Page<AutDepartmentUser>(pageBean.getPageNum(), pageBean.getPageSize()),where);
		autDepartmentUserList=deptpage.getRecords();		
		
		for(AutDepartmentUser autDepartmentUser:autDepartmentUserList){
			AutUser user = autUserService.selectById(autDepartmentUser.getUserId());
			if(user==null){
				continue;
			}
			AutUsersVO userV=new AutUsersVO();
			userV.setAutUser(user);			
			if(userids!=null && userids.length()>0 && userids.indexOf(user.getId().toString())>-1){
				userV.setUserids("1");
			}else{
				userV.setUserids("0");
			}
			if(manids!=null && manids.length()>0 && manids.indexOf(user.getId().toString())>-1){
				userV.setManids("1");
			}else{
				userV.setManids("0");
			}
			autUserList.add(userV);
		}
		page.setRecords(autUserList);
		page.setSize(deptpage.getSize());
		page.setTotal(deptpage.getTotal());
		return page;
	}
	
	/**
	 * 
	 * 用户授权
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/add")
	@ResponseBody
	@ApiOperation(value = "用户授权")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "授权用户id", required = true, dataType = "long",
				paramType = "query"),
		@ApiImplicitParam(name = "manId", value = "授权用户id", required = true, dataType = "long",
		paramType = "query"),
		@ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "long",
          	paramType = "query")})
	public RetMsg add(HttpServletRequest request, HttpServletResponse response, String userId,String manId,String bpmnId,String isCheck) {
		RetMsg retMsg = new RetMsg();
		try {			
			if(isCheck!=null && !"".equals(isCheck)){
				Where<AutUser>userwhere=new Where<AutUser>();
				userwhere.eq("is_delete", 0);
				userwhere.eq("is_active", 1);
				userwhere.eq("is_account_locked", 0);
				userwhere.eq("is_account_expired", 0);	
				userwhere.setSqlSelect("id");
				List<AutUser> autUserList=autUserService.selectList(userwhere);
				userId="";
				for (int i = 0; i < autUserList.size(); i++) {
					userId+=autUserList.get(i).getId()+",";
				}
			}
			retMsg=actAljoinBpmnUserService.addUserAndMan(userId, manId, bpmnId, isCheck);			
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage("授权失败"+e.getMessage());
		}
		
		return retMsg;
	}
	
	/**
	 * 
	 * 根据流程id查询所有用户权限
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/getAllUserByBpmnId")
	@ResponseBody
	@ApiOperation(value = "根据流程id查询所有用户权限")
	@ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "long",
          	paramType = "query")
	public List<AutUser> getAllUserByBpmnId(HttpServletRequest request, HttpServletResponse response, String bpmnId) {
		List<AutUser> autUserList=new ArrayList<AutUser>();
		try {
			Where<ActAljoinBpmnUser> where =new Where<ActAljoinBpmnUser>();
			where.eq("bpmn_id", bpmnId);
			where.eq("auth_type",0);
			where.eq("is_delete",0);
			where.eq("is_active",1);
			List<ActAljoinBpmnUser> list=actAljoinBpmnUserService.selectList(where);
			for(ActAljoinBpmnUser user:list){
				AutUser autUser=autUserService.selectById(user.getUserId());
				if(autUser!=null){
				autUserList.add(autUser);
				} 
			}
		} catch (Exception e) {
		  logger.error("", e);
		}
		
		return autUserList;
	}
	/**
	 * 
	 * 根据流程id查询所有用户权限
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/getAllManByBpmnId")
	@ResponseBody
	@ApiOperation(value = "根据流程id查询所有用户管理权限")
	@ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "long",
          	paramType = "query")
	public List<AutUser> getAllManByBpmnId(HttpServletRequest request, HttpServletResponse response, String bpmnId) {
		List<AutUser> autUserList=new ArrayList<AutUser>();
		try {
			Where<ActAljoinBpmnUser> where =new Where<ActAljoinBpmnUser>();			
			where.eq("bpmn_id", bpmnId);
			where.eq("auth_type",1);
			where.eq("is_delete",0);
			where.eq("is_active",1);
			List<ActAljoinBpmnUser> list=actAljoinBpmnUserService.selectList(where);
			
			for(ActAljoinBpmnUser user:list){
				AutUser autUser=autUserService.selectById(user.getUserId());
				if(autUser==null){
					continue;
				}
				autUserList.add(autUser);
			}
		} catch (Exception e) {
		  logger.error("", e);
		}
		
		return autUserList;
	}
	
	/**
	 * 
	 * 用户授权
	 *
	 * @return：RetMsg
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-12
	 */
	@RequestMapping("/delUser")
	@ResponseBody
	@ApiOperation(value = "用户删除")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "long",
				paramType = "query"),
		@ApiImplicitParam(name = "bpmnId", value = "流程ID", required = true, dataType = "long",
          	paramType = "query")})
	public RetMsg delUser(HttpServletRequest request, HttpServletResponse response, ActAljoinBpmnUser obj) {
		RetMsg retMsg = new RetMsg();
		try {
			Where<ActAljoinBpmnUser> where =new Where<ActAljoinBpmnUser>();
			where.eq("bpmn_id", obj.getBpmnId());
			where.eq("user_id", obj.getUserId());
			ActAljoinBpmnUser user=actAljoinBpmnUserService.selectOne(where);
			if(user != null){
				actAljoinBpmnUserService.physicsDeleteById(user.getId());
			}
			retMsg.setCode(0);
			retMsg.setMessage("删除成功");
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage("删除失败");
		}
		
		return retMsg;
	}
}
