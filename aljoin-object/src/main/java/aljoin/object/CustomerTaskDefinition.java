package aljoin.object;

import java.io.Serializable;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class CustomerTaskDefinition implements Serializable {
    /**
     * TODO
     */
    private static final long serialVersionUID = -6556555880795759478L;
    /**
     * 流程key
     */
    private String key;
    /**
     * 节点名称
     */
    private String nextNodeName;
    /**
     * 节点办理人Id
     */
    private String assignee;

    /**
     * 节点办理人
     */
    private String assigneeName;

    /**
     * 部门Id
     */
    private String deptId;

    /**
     * 部门
     */
    private String deptName;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNextNodeName() {
        return nextNodeName;
    }

    public void setNextNodeName(String nextNodeName) {
        this.nextNodeName = nextNodeName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }
}
