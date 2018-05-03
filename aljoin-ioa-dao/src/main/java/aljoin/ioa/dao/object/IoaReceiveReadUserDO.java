package aljoin.ioa.dao.object;

import java.io.Serializable;

/**
 * 
 * 收文阅件-用户评论(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaReceiveReadUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 未阅人员ID
     */
    private String inReadIds;

    /**
     * 已阅人员ID
     */
    private String toReadIds;

    /**
     * 未阅人员
     */
    private String inReadNames;

    /**
     * 已阅人员
     */
    private String toReadNames;

    /**
     * 操作人
     */
    private String operationName;

    /**
     * 部门
     */
    private String deptName;

    /**
     * 时间
     */
    private String readTimeStr;

    /**
     * 意见
     */
    private String readOpinion;

    public String getInReadIds() {
        return inReadIds;
    }

    public void setInReadIds(String inReadIds) {
        this.inReadIds = inReadIds;
    }

    public String getToReadIds() {
        return toReadIds;
    }

    public void setToReadIds(String toReadIds) {
        this.toReadIds = toReadIds;
    }

    public String getInReadNames() {
        return inReadNames;
    }

    public void setInReadNames(String inReadNames) {
        this.inReadNames = inReadNames;
    }

    public String getToReadNames() {
        return toReadNames;
    }

    public void setToReadNames(String toReadNames) {
        this.toReadNames = toReadNames;
    }

    public String getReadTimeStr() {
        return readTimeStr;
    }

    public void setReadTimeStr(String readTimeStr) {
        this.readTimeStr = readTimeStr;
    }

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getReadOpinion() {
        return readOpinion;
    }

    public void setReadOpinion(String readOpinion) {
        this.readOpinion = readOpinion;
    }
}
