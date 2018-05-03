package aljoin.ioa.dao.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 收文阅件表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaCirculaUser extends Entity<IoaCirculaUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 流程实例ID
     */
    @ApiModelProperty(hidden = true)
    private String processInstanceId;

    /**
     * 部门名称
     */
    @ApiModelProperty(hidden = true)
    private String deptName;
    

    /**
     * 传阅意见
     */
    @ApiModelProperty(hidden = true)
    private String opinon;
    
    
    /**
     * 传阅意见 填写时间
     */
    @ApiModelProperty(hidden = true)
    private Date  opinonTime;
    
    /**
     * 传阅日志传阅意见时间 返回格式：意见(2018-03-21 00:02:31)
     */
    @ApiModelProperty(hidden = true)
    private String  returnOpinonTime;
    /**
     * 传阅传阅域 意见      返回格式：意见(张三   2018-03-21 00:02:31)
     */
    @ApiModelProperty(hidden = true)
    private String  returnOpinon;
    

	public void setReturnOpinonTime(String returnOpinonTime) {
		this.returnOpinonTime = returnOpinonTime;
	}


	public void setReturnOpinon(String returnOpinon) {
		this.returnOpinon = returnOpinon;
	}


	/**
     * 传阅明细对应用户
     */
    @ApiModelProperty(hidden = true)
    private String  createUserFullName;
    
	public String getCreateUserFullName() {
		return createUserFullName;
	}


	public void setCreateUserFullName(String createUserFullName) {
		this.createUserFullName = createUserFullName;
	}


	public String getProcessInstanceId() {
		return processInstanceId;
	}


	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}


	public String getDeptName() {
		return deptName;
	}


	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public String getOpinon() {		
		return opinon;
	}


	public void setOpinon(String opinon) {
		this.opinon = opinon;
	}


	public Date getOpinonTime() {
		return opinonTime;
	}


	public void setOpinonTime(Date opinonTime) {
		this.opinonTime = opinonTime;
	}
    
	public String getReturnOpinonTime() {
		if(this.opinon!=null && !"".equals(this.opinon)){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return opinon+"("+format.format(this.opinonTime)+")";
		}else{
			return "";
		}		
	}


	public String getReturnOpinon() {
		if(this.opinon!=null && !"".equals(this.opinon)){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return opinon+"("+this.createUserFullName+" "+format.format(this.opinonTime)+")";
		}else{
			return "";
		}		
	}

}
