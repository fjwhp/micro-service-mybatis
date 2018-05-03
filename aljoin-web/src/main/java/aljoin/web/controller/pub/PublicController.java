package aljoin.web.controller.pub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutDepartmentDO;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.aut.dao.object.AutDepartmentVO;
import aljoin.aut.dao.object.AutOrganVO;
import aljoin.aut.dao.object.AutPositionDO;
import aljoin.aut.dao.object.AutPositionVO;
import aljoin.aut.dao.object.AutRoleDO;
import aljoin.aut.dao.object.AutRoleVO;
import aljoin.aut.dao.object.AutUserDO;
import aljoin.aut.dao.object.AutUserVO;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutPositionService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.DB;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Controller
@RequestMapping(value = "/pub/public", method = RequestMethod.POST)
@Api(value = "公共接口", description = "公共接口")
public class PublicController {
  private final static Logger logger = LoggerFactory.getLogger(PublicController.class);

	private DataSource dataSource;
 
	@Resource
	private AutDepartmentService autDepartmentService;

	@Resource
	private AutPositionService autPositionService;

	@Resource
	private AutRoleService autRoleService;

	@Resource
	private AutUserService autUserService;

	@Resource
	AutDepartmentUserService autDepartmentUserService;

	public PublicController(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 
	 * 级联菜单
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月14日 下午5:24:59
	 */
	@RequestMapping(value = "/getListByParentId")
	@ResponseBody
	@ApiOperation(value = "select")
	public RetMsg getListByParentId(HttpServletRequest request, HttpServletResponse response) throws IOException {

		RetMsg retMsg = new RetMsg();
		// 获取标签属性
		Object tableObj = request.getParameter("table");
		Object keyObj = request.getParameter("key");
		Object textObj = request.getParameter("text");
		Object idObj = request.getParameter("id");
		Object nameObj = request.getParameter("name");
		Object defObj = request.getParameter("def");
		Object sclassObj = request.getParameter("sclass");
		Object layVerifyObject = request.getParameter("layVerify");
		Object whereObj = request.getParameter("where");
		Object levelObj = request.getParameter("level");
		Object parentIdObj = request.getParameter("parentId");
		Object levelNameObj = request.getParameter("levelName");

		String table = tableObj == null ? null : tableObj.toString();
		String key = keyObj == null ? null : keyObj.toString();
		String text = textObj == null ? null : textObj.toString();
		String id = idObj == null ? null : idObj.toString();
		String name = nameObj == null ? null : nameObj.toString();
		String def = defObj == null ? null : defObj.toString();
		String sclass = sclassObj == null ? null : sclassObj.toString();
		String layVerify = layVerifyObject == null ? null : layVerifyObject.toString();
		String where = whereObj == null ? null : whereObj.toString();
		String level = levelObj == null ? null : levelObj.toString();
		String parentId = parentIdObj == null ? null : parentIdObj.toString();
		String levelName = levelNameObj == null ? null : levelNameObj.toString();

		// 构造页面需要显示的元素
		StringBuffer select = new StringBuffer();
		select.append("<select aljoin-level='" + level + "' aljoin-table='" + table + "' aljoin-key='" + key
				+ "' aljoin-text='" + text + "' aljoin-id='" + id + "' aljoin-name='" + name + "' aljoin-def='" + def
				+ "' aljoin-sclass='" + sclass + "' aljoin-layVerify='" + layVerify + "' aljoin-where='" + where
				+ "' ");
		if (name != null) {
			select.append("name=\"" + name + "\" ");
		}
		if (name != null) {
			select.append("id=\"" + id + "\" lay-filter=\"" + id + "\" ");
		}
		if (layVerify != null) {
			select.append("lay-verify=\"" + layVerify + "\" ");
		}
		select.append(">");

		select.append("<option value=\"\"></option>");

		// 构造sql获取数据
		where = where.replace("and " + levelName + "=?", "");
		String sql = "SELECT " + key + "," + text + " FROM " + table + " WHERE is_delete = 0 AND parent_id=" + parentId
				+ where;
		
		List<Map<String, String>> list = DB.queryForList(sql, dataSource);
		//如果有查询到数据则返回拼接的下拉框，否则返回""(页面不显示)
		if(list!=null && !list.isEmpty()){
		  //String str = "请选择";
		  //String str1 = "";
		  //select.append("<option value=\""  + "\" selected=\"selected\" style=\"color:#999\">请选择</option>");
			for (Map<String, String> m : list) {
				if (m.get(key).equals(def)) {
					select.append("	 		<option value=\"" + m.get(key) + "\" selected=\"selected\">" + m.get(text)
					+ "</option>");
				} else {
					select.append("	 		<option value=\"" + m.get(key) + "\">" + m.get(text) + "</option>");
				}
			}
			select.append(" 	</select>");
			retMsg.setMessage(select.toString());
		}else{
			retMsg.setMessage("");
		}

		return retMsg;
	}

	/**
	 *
	 * 部门树(流程设计调用).
	 *
	 * @return：List<AutDepartment>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-11
	 */
	@RequestMapping(value = "/deptList")
	@ResponseBody
	@ApiOperation(value = "部门树接口", notes = "部门树接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "isActive", value = "是否激活", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "idList", value = "部门ID", required = false, dataType = "int", paramType = "query"), })
	public List<AutDepartmentDO> deptList(HttpServletRequest request, HttpServletResponse response, AutDepartmentVO obj) {
		List<AutDepartmentDO> departmentList = null;
		try {
			departmentList = autDepartmentService.getDepartmentList(obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return departmentList;

	}

	/**
	 *
	 * 岗位(分页列表).
	 *
	 * @return：Page<AutPosition>
	 *
	 * @author：wangj
	 *
	 * @date：2017-08-17
	 */
	@RequestMapping(value = "/positionList")
	@ApiOperation(value = "岗位分页列表接口", notes = "岗位分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "positionName", value = "岗位名称", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "deptCode", value = "部门编号", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "isActive", value = "是否激活", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "idList", value = "岗位ID", required = false, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "deptId", value = "部门ID", required = false, dataType = "string", paramType = "query")})
	@ResponseBody
	public Page<AutPositionDO> positionList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
											AutPositionVO autPosition) {
		Page<AutPositionDO> page = null;
		try {
			page = autPositionService.positionList(pageBean, autPosition);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 角色表(分页列表).
	 *
	 * @return：Page<AutRole>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-11
	 */
	@RequestMapping(value = "/roleList")
	@ResponseBody
	@ApiOperation(value = "用户分页列表接口", notes = "用户分页列表接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "isActive", value = "是否激活", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "roleName", value = "角色名称(搜索条件)", required = false, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "idList", value = "岗位ID(被添加的记录的ID)", required = false, dataType = "int", paramType = "query") })
	public Page<AutRoleDO> roleList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutRoleVO obj) {
		Page<AutRoleDO> page = null;
		try {
			page = autRoleService.roleList(pageBean, obj);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 *
	 * 用户表(分页列表).
	 *
	 * @return：Page<AutUser>
	 *
	 * @author：wangj
	 *
	 * @date：2017年09月11日 下午5:47:09
	 */
	@RequestMapping(value = "/userList")
	@ResponseBody
	public Page<AutUserDO> userList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
			AutUserVO userVO) {
		Page<AutUserDO> page = null;
		try {
			page = autUserService.getUserList(pageBean, userVO);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}

	/**
	 * 
	 * 通用接口-多部门的多个用户按照先部门(dept_rank)，然后用户(关联表里的department_user_rank)进行排序
	 *
	 * @return：List<AutDepartmentUser>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月14日 下午6:11:12
	 */
	@RequestMapping("/orderUserList")
	@ResponseBody
	public List<AutDepartmentUser> orderUserList(HttpServletRequest request, HttpServletResponse response,
			AutDepartmentUserVO deptUserVo) {

		List<AutDepartmentUser> autDeptUserList = deptUserVo.getAutDepartmentUserList();
		// 一、 获取部门idList，查询对应的部门并排序
		List<Long> deptIdList = new ArrayList<Long>();
		for (AutDepartmentUser deptUser : autDeptUserList) {
			deptIdList.add(deptUser.getDeptId());
		}
		Where<AutDepartment> w1 = new Where<AutDepartment>();
		w1.in("id", deptIdList);
		w1.orderBy("dept_rank");
		List<AutDepartment> deptList = autDepartmentService.selectList(w1);
		// 获得部门排序后的deptIdList
		List<Long> deptIdList2 = new ArrayList<Long>();
		for (AutDepartment dept : deptList) {
			deptIdList2.add(dept.getId());
		}
		//二、 获得用户userIdList，并按照用户在部门的次序department_user_rank排序
		List<Long> deptUserIdList = new ArrayList<Long>();
		for (AutDepartmentUser deptUser : autDeptUserList) {
			deptUserIdList.add(deptUser.getId());
		}
		Where<AutDepartmentUser> w2 = new Where<AutDepartmentUser>();
		w2.in("dept_id", deptIdList2);
		w2.in("id", deptUserIdList);
		w2.orderBy("department_user_rank", true);
		List<AutDepartmentUser> autDeptUserList2 = autDepartmentUserService.selectList(w2);
		// 获得排序后的关联表idList
		List<Long> deptUserIdList2 = new ArrayList<Long>();
		for (AutDepartmentUser deptUser : autDeptUserList2) {
			deptUserIdList2.add(deptUser.getId());
		}

		return autDeptUserList2;

	}

	/**
	 *
	 * 组织机构树.
	 *
	 * @return：AutOrganVO
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-22
	 */
	@RequestMapping(value = "/organList")
	@ResponseBody
	@ApiOperation(value = "组织机构树接口", notes = "组织机构树列表接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userIds", value = "用户ID(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "departmentIds", value = "部门ID(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "fullName", value = "用户姓名(多个分号分隔)", required = false, dataType = "string", paramType = "query"),
		@ApiImplicitParam(name = "deptNames", value = "部门名称(多个分号分隔)", required = false, dataType = "string", paramType = "query")
	})
	public AutOrganVO organList(HttpServletRequest request, HttpServletResponse respons,AutDepartmentUserVO departmentUser) {
		AutOrganVO organVO  = null;
		try {
			organVO  = autDepartmentUserService.getOrganList(departmentUser);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return organVO;
	}

	/**
	 *
	 * 部门用户分页列表.
	 *
	 * @return：Page<AutUser>
	 *
	 * @author：wangj
	 *
	 * @date：2017年09月11日 下午5:47:09
	 */
	@RequestMapping(value = "/departUserList")
	@ResponseBody
	@ApiOperation("撰写邮件->组织机构树->搜索用户接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "pageSize", value = "每页记录数", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "pageNum", value = "当前页码", required = true, dataType = "int", paramType = "query"),
			@ApiImplicitParam(name = "userPwd", value = "密码(此字段作为是否去查询部门用户关联表)", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "userName", value = "账号(搜索条件)", required = false, dataType = "string", paramType = "query")
	})
	public Page<AutUser> departUserList(HttpServletRequest request, HttpServletResponse response, PageBean pageBean,
									AutUser user) {
		Page<AutUser> page = null;
		try {
			page = autUserService.getDeptUserList(pageBean, user);
		} catch (Exception e) {
		  logger.error("", e);
		}
		return page;
	}
}
