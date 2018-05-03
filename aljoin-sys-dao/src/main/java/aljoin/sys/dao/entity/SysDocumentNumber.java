package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：公文文号表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-26
 */
public class SysDocumentNumber extends Entity<SysDocumentNumber> {

    private static final long serialVersionUID = 1L;

    /**
     * 文号名称
     */
	private String documentNumName;
    /**
     * 机构代字
     */
	private String agencyCode;
    /**
     * 年号规则（1:无 2：年）
     */
	private Integer reignTitleRule;
    /**
     * 文号格式(用&拼接)
     */
	private String documentNumPattern;
    /**
     * 是否固定长度（1：是；0：否）
     */
	private Integer isFixLength;
    /**
     * 起始值
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long startValue;
    /**
     * 位数
     */
	private Integer digit;
    /**
     * 重置规则（1:不重置 2：按年重置）
     */
	private Integer resettingRule;
    /**
     * 当前值
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long currentValue;
    /**
     * 状态（1：启用；0：停用）
     */
	private Integer status;
    /**
     * 文号分类ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long categoryId;
    /**
     * 流程名称(多个用;分开)
     */
	private String bpmnIds;


	public String getDocumentNumName() {
		return documentNumName;
	}

	public SysDocumentNumber setDocumentNumName(String documentNumName) {
		this.documentNumName = documentNumName;
		return this;
	}

	public String getAgencyCode() {
		return agencyCode;
	}

	public SysDocumentNumber setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
		return this;
	}

	public Integer getReignTitleRule() {
		return reignTitleRule;
	}

	public SysDocumentNumber setReignTitleRule(Integer reignTitleRule) {
		this.reignTitleRule = reignTitleRule;
		return this;
	}

	public String getDocumentNumPattern() {
		return documentNumPattern;
	}

	public SysDocumentNumber setDocumentNumPattern(String documentNumPattern) {
		this.documentNumPattern = documentNumPattern;
		return this;
	}

	public Integer getIsFixLength() {
		return isFixLength;
	}

	public SysDocumentNumber setIsFixLength(Integer isFixLength) {
		this.isFixLength = isFixLength;
		return this;
	}

	public Long getStartValue() {
		return startValue;
	}

	public SysDocumentNumber setStartValue(Long startValue) {
		this.startValue = startValue;
		return this;
	}

	public Integer getDigit() {
		return digit;
	}

	public SysDocumentNumber setDigit(Integer digit) {
		this.digit = digit;
		return this;
	}

	public Integer getResettingRule() {
		return resettingRule;
	}

	public SysDocumentNumber setResettingRule(Integer resettingRule) {
		this.resettingRule = resettingRule;
		return this;
	}

	public Long getCurrentValue() {
		return currentValue;
	}

	public SysDocumentNumber setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public SysDocumentNumber setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public SysDocumentNumber setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
		return this;
	}

    public String getBpmnIds() {
        return bpmnIds;
    }

    public void setBpmnIds(String bpmnIds) {
        this.bpmnIds = bpmnIds;
    }

}
