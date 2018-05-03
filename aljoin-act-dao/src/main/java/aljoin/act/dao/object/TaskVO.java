package aljoin.act.dao.object;

import java.util.List;
import java.util.Set;

/**
 * 
 * 任务节点对象
 *
 * @author：zhongjy
 * 
 * @date：2017年11月27日 下午9:33:34
 */
public class TaskVO {

    /**
     * 打开方式： 1-弹出组织机构树：受理人和候选对象都没有选择（包含选择后找不到对应的办理或者候选用户---这个优先级最高）
     * 2-不弹出：仅选择了受理人（并且该受理人是合法状态，否则会被1覆盖而变成无人状态而继续弹出整个组织机构树） 3-当受理人和候选对象都有选择并且有选择组织机构时，弹出机构树
     * 4-当受理人和候选对象都有选择并且没有选择组织机构时，弹出用户列表
     */
    private String openType;

    /**
     * 任务类型：1-普通的用户任务，2-并行会签任务，3-串行会签任务
     */
    private String taskType;

    /**
     * 节点key
     */
    private String taskKey;
    /**
     * （默认）受理人
     */
    private SimpleUserVO defaultAssigneeUser;
    /**
     * 节点名称
     */
    private String taskName;
    /**
     * 候选用户姓名列表（所有用户：含受理和选择其他候选对象得出来的所有用户）
     */
    private List<SimpleUserVO> userList;

    /**
     * 当前任务所选择的部门列表
     */
    private List<SimpleDeptVO> deptList;

    /**
     * 非部门产生的用户id
     */
    private Set<String> unDeptUserSet;

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public List<SimpleUserVO> getUserList() {
        return userList;
    }

    public void setUserList(List<SimpleUserVO> userList) {
        this.userList = userList;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public SimpleUserVO getDefaultAssigneeUser() {
        return defaultAssigneeUser;
    }

    public void setDefaultAssigneeUser(SimpleUserVO defaultAssigneeUser) {
        this.defaultAssigneeUser = defaultAssigneeUser;
    }

    public List<SimpleDeptVO> getDeptList() {
        return deptList;
    }

    public void setDeptList(List<SimpleDeptVO> deptList) {
        this.deptList = deptList;
    }

    public Set<String> getUnDeptUserSet() {
        return unDeptUserSet;
    }

    public void setUnDeptUserSet(Set<String> unDeptUserSet) {
        this.unDeptUserSet = unDeptUserSet;
    }

    @Override
    public String toString() {
        return "TaskVO [openType=" + openType + ", taskType=" + taskType + ", taskKey=" + taskKey
            + ", defaultAssigneeUser=" + defaultAssigneeUser + ", taskName=" + taskName + ", userList=" + userList
            + ", deptList=" + deptList + ", unDeptUserSet=" + unDeptUserSet + "]";
    }

}
