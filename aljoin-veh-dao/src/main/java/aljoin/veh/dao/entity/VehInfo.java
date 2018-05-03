package aljoin.veh.dao.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 车船信息表(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-12
 */
public class VehInfo extends Entity<VehInfo> {

    private static final long serialVersionUID = 1L;

    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 使用部门名称（多个分号隔开）
     */
    private String useDepartmentName;
    /**
     * 使用部门ID
     */
    private String useDepartmentId;
    /**
     * 在用人ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long useUserId;
    /**
     * 司机姓名
     */
    private String driverName;
    /**
     * 备注
     */
    private String content;
    /**
     * 牌号
     */
    private String carCode;
    /**
     * 车船类型（座位）
     */
    private String carType;
    /**
     * 车船类型
     */
    private String carShip;
    /**
     * 品牌型号
     */
    private String carModle;
    /**
     * 车船状态
     */
    private Integer carStatus;
    /**
     * 车船情况
     */
    private Integer carCondition;
    /**
     * 购买时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date purchaseTime;
    /**
     * 保险卡号
     */
    private String cardCode;
    /**
     * 标准耗油
     */
    private String consume;
    /**
     * 已用年限
     */
    private String alreadyUseTime;
    /**
     * 已行驶里程
     */
    private String alreadyRun;

    public Integer getIsActive() {
        return isActive;
    }

    public VehInfo setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

    public String getUseDepartmentName() {
        return useDepartmentName;
    }

    public VehInfo setUseDepartmentName(String useDepartmentName) {
        this.useDepartmentName = useDepartmentName;
        return this;
    }

    public String getUseDepartmentId() {
        return useDepartmentId;
    }

    public VehInfo setUseDepartmentId(String useDepartmentId) {
        this.useDepartmentId = useDepartmentId;
        return this;
    }

    public Long getUseUserId() {
        return useUserId;
    }

    public VehInfo setUseUserId(Long useUserId) {
        this.useUserId = useUserId;
        return this;
    }

    public String getDriverName() {
        return driverName;
    }

    public VehInfo setDriverName(String driverName) {
        this.driverName = driverName;
        return this;
    }

    public String getContent() {
        return content;
    }

    public VehInfo setContent(String content) {
        this.content = content;
        return this;
    }

    public String getCarCode() {
        return carCode;
    }

    public VehInfo setCarCode(String carCode) {
        this.carCode = carCode;
        return this;
    }

    public String getCarType() {
        return carType;
    }

    public VehInfo setCarType(String carType) {
        this.carType = carType;
        return this;
    }

    public String getCarShip() {
        return carShip;
    }

    public VehInfo setCarShip(String carShip) {
        this.carShip = carShip;
        return this;
    }

    public String getCarModle() {
        return carModle;
    }

    public VehInfo setCarModle(String carModle) {
        this.carModle = carModle;
        return this;
    }

    public Integer getCarStatus() {
        return carStatus;
    }

    public VehInfo setCarStatus(Integer carStatus) {
        this.carStatus = carStatus;
        return this;
    }

    public Integer getCarCondition() {
        return carCondition;
    }

    public VehInfo setCarCondition(Integer carCondition) {
        this.carCondition = carCondition;
        return this;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public VehInfo setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
        return this;
    }

    public String getCardCode() {
        return cardCode;
    }

    public VehInfo setCardCode(String cardCode) {
        this.cardCode = cardCode;
        return this;
    }

    public String getConsume() {
        return consume;
    }

    public VehInfo setConsume(String consume) {
        this.consume = consume;
        return this;
    }

    public String getAlreadyUseTime() {
        return alreadyUseTime;
    }

    public VehInfo setAlreadyUseTime(String alreadyUseTime) {
        this.alreadyUseTime = alreadyUseTime;
        return this;
    }

    public String getAlreadyRun() {
        return alreadyRun;
    }

    public VehInfo setAlreadyRun(String alreadyRun) {
        this.alreadyRun = alreadyRun;
        return this;
    }

}
