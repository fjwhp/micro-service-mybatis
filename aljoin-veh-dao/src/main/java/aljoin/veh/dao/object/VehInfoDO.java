package aljoin.veh.dao.object;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class VehInfoDO {

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    public Long id;

    /**
     * 序号
     */
    private Integer no;
    /**
     * 品牌型号
     */
    private String carModle;
    /**
     * 类型
     */
    private String carShip;
    /**
     * 牌号
     */
    private String carCode;
    /**
     * 车船情况
     */
    private Integer carCondition;
    /**
     * 使用部门id
     */
    private String deptId;

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * 使用部门
     */
    private String dept;
    /**
     * 购置时间
     */
    private Date purchaseTime;
    /**
     * 状态
     */
    private Integer status;

    /**
     * 在用人Id
     */
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 在用人
     */
    private String userName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getCarModle() {
        return carModle;
    }

    public void setCarModle(String carModle) {
        this.carModle = carModle;
    }

    public String getCarShip() {
        return carShip;
    }

    public void setCarShip(String carShip) {
        this.carShip = carShip;
    }

    public String getCarCode() {
        return carCode;
    }

    public void setCarCode(String carCode) {
        this.carCode = carCode;
    }

    public Integer getCarCondition() {
        return carCondition;
    }

    public void setCarCondition(Integer carCondition) {
        this.carCondition = carCondition;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Date getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(Date purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
