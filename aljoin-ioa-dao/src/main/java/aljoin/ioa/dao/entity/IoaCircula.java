package aljoin.ioa.dao.entity;

import aljoin.dao.entity.Entity;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 传阅件表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaCircula extends Entity<IoaCircula> {

    private static final long serialVersionUID = 1L;
   
    /**
     * 流程实例ID
     */
    @ApiModelProperty(hidden = true)
    private String processInstanceId;
   
    /**
     * 所有传阅对象的用户名字
     */
    @ApiModelProperty(hidden = true)
    private String cirNames;
    
    /**
     * 所有传阅对象的用户名字
     */
    @ApiModelProperty(hidden = true)
    private String cirIds;
    /**
     * 所有传阅对象的用户名字
     */
    @ApiModelProperty(hidden = true)
    private String createUserfullName;


	

	public String getCreateUserfullName() {
		return createUserfullName;
	}

	public void setCreateUserfullName(String createUserfullName) {
		this.createUserfullName = createUserfullName;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getCirNames() {
		return cirNames;
	}

	public void setCirNames(String cirNames) {
		this.cirNames = cirNames;
	}

	public String getCirIds() {
		return cirIds;
	}

	public void setCirIds(String cirIds) {
		this.cirIds = cirIds;
	}
    
 
}
