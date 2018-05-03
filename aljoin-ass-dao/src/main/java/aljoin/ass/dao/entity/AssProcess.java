package aljoin.ass.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 固定财产流程表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-12
 */
public class AssProcess extends Entity<AssProcess> {

    private static final long serialVersionUID = 1L;

    /**
     * 经办人ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long applicationId;
    /**
     * 经办人姓名
     */
	private String applicationName;
    /**
     * 部门ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long deptId;
    /**
     * 部门
     */
	private String deptName;
    /**
     * 单证编号编号
     */
	private String listCode;
    /**
     * 标题（申购单标题）
     */
	private String title;
    /**
     * 流程名称
     */
	private String processName;
    /**
     * 内容
     */
	private String content;
    /**
     * 单证日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date submitTime;
    /**
     * 审核状态（1:审核中 2：审核失败 3：审核通过）
     */
	private Integer auditStatus;
    /**
     * 审核时间
     */
	private Date auditTime;
    /**
     * 流程ID
     */
	private String processId;


	public Long getApplicationId() {
		return applicationId;
	}

	public AssProcess setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
		return this;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public AssProcess setApplicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}

	public Long getDeptId() {
		return deptId;
	}

	public AssProcess setDeptId(Long deptId) {
		this.deptId = deptId;
		return this;
	}

	public String getDeptName() {
		return deptName;
	}

	public AssProcess setDeptName(String deptName) {
		this.deptName = deptName;
		return this;
	}

  public String getListCode() {
    return listCode;
  }

  public void setListCode(String listCode) {
    this.listCode = listCode;
  }

  public String getTitle() {
		return title;
	}

	public AssProcess setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getProcessName() {
		return processName;
	}

	public AssProcess setProcessName(String processName) {
		this.processName = processName;
		return this;
	}

	public String getContent() {
		return content;
	}

	public AssProcess setContent(String content) {
		this.content = content;
		return this;
	}

	public Date getSubmitTime() {
		return submitTime;
	}

	public AssProcess setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
		return this;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public AssProcess setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
		return this;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public AssProcess setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
		return this;
	}

	public String getProcessId() {
		return processId;
	}

	public AssProcess setProcessId(String processId) {
		this.processId = processId;
		return this;
	}

}
