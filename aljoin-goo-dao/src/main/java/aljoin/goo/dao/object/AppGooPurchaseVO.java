package aljoin.goo.dao.object;

import java.io.Serializable;

/**
 * 办公用品申购(实体类).
 * 
 * @author：xuc.
 * 
 * @date： 2018-02-02
 */
public class AppGooPurchaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private String ids;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 流程实例ID
     */
    private String processInstanceId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }
}
