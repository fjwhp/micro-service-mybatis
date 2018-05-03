package aljoin.act.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：(实体类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-13
 */
public class ActAljoinBpmnRun extends Entity<ActAljoinBpmnRun> {

    private static final long serialVersionUID = 1L;

    /**
     * 页面表单备份元素html
     */
	private String htmlCode;
    /**
     * xml代码
     */
	private String xmlCode;
    /**
     * 流程分类ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long categoryId;
    /**
     * 是否激活
     */
	private Integer isActive;
    /**
     * 流程ID
     */
	private String processId;
    /**
     * 流程名称
     */
	private String processName;
    /**
     * 流程描述
     */
	private String processDesc;
    /**
     * 是否已经部署
     */
	private Integer isDeploy;
    /**
     * 流程修改后是否重新部署过
     */
	private Integer isDeployAfterEdit;
    /**
     * 流程表单是否修改过
     */
	private Integer isFormEdit;
    /**
     * 上次部署时间
     */
	private Date lastDeployTime;
    /**
     * 最后部署用户ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long lastDeployUserId;
    /**
     * 最后部署用户账号
     */
	private String lastDeployUserName;
    /**
     * 是否自定义表单
     */
	private Integer hasForm;
    /**
     * 是否自由流程
     */
	private Integer isFree;
    /**
     * 是否固定表单
     */
	private Integer isFixed;
    /**
     * 原流程id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long orgnlId;
    /**
     * 表单授权版本号
     */
	private Integer taskAssigneeVersion;


	public String getHtmlCode() {
		return htmlCode;
	}

	public ActAljoinBpmnRun setHtmlCode(String htmlCode) {
		this.htmlCode = htmlCode;
		return this;
	}

	public String getXmlCode() {
		return xmlCode;
	}

	public ActAljoinBpmnRun setXmlCode(String xmlCode) {
		this.xmlCode = xmlCode;
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public ActAljoinBpmnRun setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public ActAljoinBpmnRun setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

	public String getProcessId() {
		return processId;
	}

	public ActAljoinBpmnRun setProcessId(String processId) {
		this.processId = processId;
		return this;
	}

	public String getProcessName() {
		return processName;
	}

	public ActAljoinBpmnRun setProcessName(String processName) {
		this.processName = processName;
		return this;
	}

	public String getProcessDesc() {
		return processDesc;
	}

	public ActAljoinBpmnRun setProcessDesc(String processDesc) {
		this.processDesc = processDesc;
		return this;
	}

	public Integer getIsDeploy() {
		return isDeploy;
	}

	public ActAljoinBpmnRun setIsDeploy(Integer isDeploy) {
		this.isDeploy = isDeploy;
		return this;
	}

	public Integer getIsDeployAfterEdit() {
		return isDeployAfterEdit;
	}

	public ActAljoinBpmnRun setIsDeployAfterEdit(Integer isDeployAfterEdit) {
		this.isDeployAfterEdit = isDeployAfterEdit;
		return this;
	}

	public Integer getIsFormEdit() {
		return isFormEdit;
	}

	public ActAljoinBpmnRun setIsFormEdit(Integer isFormEdit) {
		this.isFormEdit = isFormEdit;
		return this;
	}

	public Date getLastDeployTime() {
		return lastDeployTime;
	}

	public ActAljoinBpmnRun setLastDeployTime(Date lastDeployTime) {
		this.lastDeployTime = lastDeployTime;
		return this;
	}

	public Long getLastDeployUserId() {
		return lastDeployUserId;
	}

	public ActAljoinBpmnRun setLastDeployUserId(Long lastDeployUserId) {
		this.lastDeployUserId = lastDeployUserId;
		return this;
	}

	public String getLastDeployUserName() {
		return lastDeployUserName;
	}

	public ActAljoinBpmnRun setLastDeployUserName(String lastDeployUserName) {
		this.lastDeployUserName = lastDeployUserName;
		return this;
	}

	public Integer getHasForm() {
		return hasForm;
	}

	public ActAljoinBpmnRun setHasForm(Integer hasForm) {
		this.hasForm = hasForm;
		return this;
	}

	public Integer getIsFree() {
		return isFree;
	}

	public ActAljoinBpmnRun setIsFree(Integer isFree) {
		this.isFree = isFree;
		return this;
	}

	public Integer getIsFixed() {
		return isFixed;
	}

	public ActAljoinBpmnRun setIsFixed(Integer isFixed) {
		this.isFixed = isFixed;
		return this;
	}

	public Long getOrgnlId() {
		return orgnlId;
	}

	public ActAljoinBpmnRun setOrgnlId(Long orgnlId) {
		this.orgnlId = orgnlId;
		return this;
	}

	public Integer getTaskAssigneeVersion() {
		return taskAssigneeVersion;
	}

	public ActAljoinBpmnRun setTaskAssigneeVersion(Integer taskAssigneeVersion) {
		this.taskAssigneeVersion = taskAssigneeVersion;
		return this;
	}

}
