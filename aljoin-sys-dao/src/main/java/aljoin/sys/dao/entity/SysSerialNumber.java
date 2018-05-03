package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：流水号表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-23
 */
public class SysSerialNumber extends Entity<SysSerialNumber> {

    private static final long serialVersionUID = 1L;

    /**
     * 流水号名称
     */
	private String serialNumName;
    /**
     * 前缀
     */
	private String prefix;
    /**
     * 年号规则（1:无 2：年 3：年月4：年月日）
     */
	private Integer reignTitleRule;
    /**
     * 符号
     */
	private String sign;
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
     * 后缀
     */
	private String postfix;
    /**
     * 重置规则（1:不重置 2：按年重置 3：按月重置）
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
     * 流水号分类ID
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long categoryId;
	/**
     * 流程名称(多个用;分开)
     */
    private String bpmnIds;


	public String getSerialNumName() {
		return serialNumName;
	}

	public SysSerialNumber setSerialNumName(String serialNumName) {
		this.serialNumName = serialNumName;
		return this;
	}

	public String getPrefix() {
		return prefix;
	}

	public SysSerialNumber setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	public Integer getReignTitleRule() {
		return reignTitleRule;
	}

	public SysSerialNumber setReignTitleRule(Integer reignTitleRule) {
		this.reignTitleRule = reignTitleRule;
		return this;
	}

	public String getSign() {
		return sign;
	}

	public SysSerialNumber setSign(String sign) {
		this.sign = sign;
		return this;
	}

	public Integer getIsFixLength() {
		return isFixLength;
	}

	public SysSerialNumber setIsFixLength(Integer isFixLength) {
		this.isFixLength = isFixLength;
		return this;
	}

	public Long getStartValue() {
		return startValue;
	}

	public SysSerialNumber setStartValue(Long startValue) {
		this.startValue = startValue;
		return this;
	}

	public Integer getDigit() {
		return digit;
	}

	public SysSerialNumber setDigit(Integer digit) {
		this.digit = digit;
		return this;
	}

	public String getPostfix() {
		return postfix;
	}

	public SysSerialNumber setPostfix(String postfix) {
		this.postfix = postfix;
		return this;
	}

	public Integer getResettingRule() {
		return resettingRule;
	}

	public SysSerialNumber setResettingRule(Integer resettingRule) {
		this.resettingRule = resettingRule;
		return this;
	}

	public Long getCurrentValue() {
		return currentValue;
	}

	public SysSerialNumber setCurrentValue(Long currentValue) {
		this.currentValue = currentValue;
		return this;
	}

	public Integer getStatus() {
		return status;
	}

	public SysSerialNumber setStatus(Integer status) {
		this.status = status;
		return this;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public SysSerialNumber setCategoryId(Long categoryId) {
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
