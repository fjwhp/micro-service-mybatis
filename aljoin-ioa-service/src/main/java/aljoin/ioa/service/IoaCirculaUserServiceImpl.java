package aljoin.ioa.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import aljoin.act.iservice.ActAljoinFormWidgetRunService;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaCircula;
import aljoin.ioa.dao.entity.IoaCirculaUser;
import aljoin.ioa.dao.mapper.IoaCirculaUserMapper;
import aljoin.ioa.iservice.IoaCirculaService;
import aljoin.ioa.iservice.IoaCirculaUserService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;

/**
 * 收文阅件表(服务实现类).
 * 
 * @author：zhongjy @date： 2017-11-08
 */
@Service
public class IoaCirculaUserServiceImpl extends ServiceImpl<IoaCirculaUserMapper, IoaCirculaUser>
		implements IoaCirculaUserService {
	@Resource
	private IoaCirculaUserMapper mapper;
	@Resource
	private AutDepartmentService autDepartmentService;
	@Resource
	private AutDepartmentUserService autDepartmentUserService;
	@Resource
	private  AutUserService autUserService;
	@Resource
	private  RuntimeService runtimeService;
	@Resource
	private IoaCirculaService ioaCirculaService;
	@Resource
	private ActAljoinFormDataRunService actAljoinFormDataRunService;
	@Resource
	private ActAljoinFormWidgetRunService actAljoinFormWidgetRunService;
	
	@Override
	public Page<IoaCirculaUser> list(PageBean pageBean, String proId) throws Exception {
		Where<IoaCirculaUser> where = new Where<IoaCirculaUser>();
		Page<IoaCirculaUser> page = new Page<IoaCirculaUser>();
		where.eq("process_instance_id", proId);
		where.orderBy("id", true);
		page = selectPage(new Page<IoaCirculaUser>(pageBean.getPageNum(), pageBean.getPageSize()),where);
		return page;
	}
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	public void copyObject(IoaCirculaUser obj) throws Exception {
		mapper.copyObject(obj);
	}

	@Override
	public RetMsg add(IoaCirculaUser obj) throws Exception {
		RetMsg retMsg = new RetMsg();		
		retMsg.setCode(0);
		HashMap<String,Integer> resultMap = new HashMap<String,Integer>();
		String processInstanceId = obj.getProcessInstanceId();
		
		Where<IoaCircula>ioaWhere=new Where<IoaCircula>();
        ioaWhere.eq("process_instance_id", processInstanceId);
        ioaWhere.like("cir_ids", obj.getCreateUserFullName() + ";");
        List<IoaCircula> ioaList=ioaCirculaService.selectList(ioaWhere);
        
        //判断是否是传阅件
        Integer isCirculate = 1;
        if(ioaList.size() == 0){
            isCirculate = 0;
            resultMap.put("isCirculate", isCirculate);
            retMsg.setObject(resultMap);
            return retMsg;
        }
        resultMap.put("isCirculate", isCirculate);
		// 判断流程是否结束
		ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		Integer isOver = 0;
		if(pi == null){
		    isOver = 1;
		}
		resultMap.put("isOver", isOver);
		
		String userId = obj.getCreateUserId().toString();
        Where<IoaCirculaUser> circulaUserWhere = new Where<IoaCirculaUser>();
        circulaUserWhere.eq("process_instance_id", processInstanceId);
        circulaUserWhere.eq("create_user_id", userId);
        circulaUserWhere.setSqlSelect("id,create_time,create_user_id,process_instance_id,dept_name,opinon,opinon_time,create_user_full_name");
        IoaCirculaUser circulaUser = this.selectOne(circulaUserWhere);
        Integer isread = 0;
        if (circulaUser != null) {
            if (circulaUser.getOpinonTime() != null) {
                // 1表示不显示填写传阅意见按钮
                isread = 1;
            } 
            resultMap.put("isread", isread);
            retMsg.setObject(resultMap);
            return retMsg;
        }
        resultMap.put("isread", isread);
		AutUser user=autUserService.selectById(new Long(userId));
		IoaCirculaUser ioaCirculaUser = new IoaCirculaUser();
		ioaCirculaUser.setCreateUserFullName(user.getFullName());
		ioaCirculaUser.setProcessInstanceId(processInstanceId);
		Where<AutDepartmentUser> deptUserWhere = new Where<AutDepartmentUser>();
		deptUserWhere.eq("user_id", userId);
		List<AutDepartmentUser> deptUserList = autDepartmentUserService.selectList(deptUserWhere);
		if (deptUserList != null && deptUserList.size() > 0) {
			String deptIds = "";
			for (AutDepartmentUser autDepartmentUser : deptUserList) {
				deptIds += autDepartmentUser.getDeptId() + ",";
			}
			Where<AutDepartment> deptWhere = new Where<AutDepartment>();
			deptWhere.in("id", deptIds);
			deptWhere.orderBy("dept_rank", false);
			List<AutDepartment> deptList = autDepartmentService.selectList(deptWhere);
			if (deptList != null && deptList.size() > 0) {
				String deptNames = "";
				for (AutDepartment autDepartment : deptList) {
					deptNames += autDepartment.getDeptName() + ";";
				}
				ioaCirculaUser.setDeptName(deptNames);
			}
		}
		Boolean isTrue = this.insert(ioaCirculaUser);
		if (isTrue) {
			retMsg.setCode(0);
			// 0标识显示填写传阅意见按钮
			retMsg.setObject(resultMap);
		} else {
			retMsg.setCode(1);
			retMsg.setMessage("添加失败，数据库插入新纪录失败");
		}
		return retMsg;
	}

	@Override
	public RetMsg addCirculaOpinon(IoaCirculaUser obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		retMsg.setCode(0);
		String processInstanceId = obj.getProcessInstanceId();
		ProcessInstance instance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
		if(instance == null){
		    retMsg.setCode(1);
            retMsg.setMessage("流程已流转结束，不可填写传阅意见！");
            return retMsg;
		}
		Where<IoaCirculaUser> circulaUserWhere = new Where<IoaCirculaUser>();
		circulaUserWhere.eq("process_instance_id", processInstanceId);
		circulaUserWhere.eq("create_user_id", obj.getCreateUserId());
		IoaCirculaUser circulaUser = selectOne(circulaUserWhere);
		if (circulaUser != null) {
			if (circulaUser.getOpinonTime() == null) {
				circulaUser.setOpinonTime(new Date());
				circulaUser.setOpinon(obj.getOpinon());
				this.insertOrUpdateAllColumn(circulaUser);
				String topButtonComment = obj.getOpinon() + "(" + circulaUser.getCreateUserFullName() + DateUtil.datetime2str(circulaUser.getOpinonTime()) + ")";
				//===传阅意见域保留===
				//1.找到表单域
				Where<ActAljoinFormDataRun> formDataRunWhere = new Where<ActAljoinFormDataRun>();
				formDataRunWhere.eq("proc_inst_id", processInstanceId);
				formDataRunWhere.like("form_widget_id", "aljoin_form_circulate_text", SqlLike.RIGHT);
				// 一个表单只允许配置一个传阅意见域
				ActAljoinFormDataRun formData = actAljoinFormDataRunService.selectOne(formDataRunWhere);
				if(formData != null){
				    String formWidgetVal = formData.getFormWidgetValue();
	                if (formWidgetVal.lastIndexOf("\n") != -1) {
	                    formWidgetVal = formWidgetVal.substring(0, formWidgetVal.lastIndexOf("\n"));
	                    formWidgetVal = formWidgetVal + "\n" + topButtonComment;
	                } else {
	                    formWidgetVal = topButtonComment;
	                }
	                formData.setFormWidgetValue(formWidgetVal);
	                actAljoinFormDataRunService.updateById(formData);
				}
			} else {
				retMsg.setCode(1);
				retMsg.setMessage("已经填写传阅意见，不可重复填写！");
				return retMsg;
			}
		} else {
			retMsg.setCode(1);
			retMsg.setMessage("添加传阅意见失败，个人明细未生成！");
			return retMsg;
		}
		return retMsg;
	}
	@Override
	public String isOver(IoaCirculaUser obj) throws Exception {
		 ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(obj.getProcessInstanceId()).singleResult();
        if(pi==null){  
        	return "3"; //已经完成
        }  
        else{  
        	return "4"; //未完成
        }
		
	}
}
