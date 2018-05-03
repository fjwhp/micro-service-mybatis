package aljoin.act.dao.object;

import java.util.Date;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActAljoinBpmnVO {
    private String id;
    /**
     * 流程id
     */
    private String processId;
    /**
     * 是否激活
     */
    private Integer isActive;
    /**
     * 流程名称
     */
    private String processName;
    /**
     * 流程分类名称
     */
    private String categoryName;
    /**
     * 流程分类id
     */
    private String categoryId;
    /**
     * 流程状态
     */
    private Integer isDeployAfterEdit;
    /**
     * 是否部署
     */
    private Integer isDeploy;
    /**
     * 表单状态
     */
    private Integer isFormEdit;
    /**
     * 上次部署时间
     */
    private Date lastDeployTime;

    /**
     * 是否固定表单
     */
    private Integer isFixed;

    /**
     * 是否自由流程
     */
    private Integer isFree;
    
    /**
     * 多个id
     */
    private String ids;
    
    /**
     * 临时文件路径
     */
    private String path;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getIsDeployAfterEdit() {
        return isDeployAfterEdit;
    }

    public void setIsDeployAfterEdit(Integer isDeployAfterEdit) {
        this.isDeployAfterEdit = isDeployAfterEdit;
    }

    public Integer getIsDeploy() {
        return isDeploy;
    }

    public void setIsDeploy(Integer isDeploy) {
        this.isDeploy = isDeploy;
    }

    public Integer getIsFormEdit() {
        return isFormEdit;
    }

    public void setIsFormEdit(Integer isFormEdit) {
        this.isFormEdit = isFormEdit;
    }

    public Date getLastDeployTime() {
        return lastDeployTime;
    }

    public void setLastDeployTime(Date lastDeployTime) {
        this.lastDeployTime = lastDeployTime;
    }

    public Integer getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(Integer isFixed) {
        this.isFixed = isFixed;
    }

    public Integer getIsFree() {
        return isFree;
    }

    public void setIsFree(Integer isFree) {
        this.isFree = isFree;
    }
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ActAljoinBpmnVO [id=" + id + ", processId=" + processId + ", isActive=" + isActive + ", processName="
            + processName + ", categoryName=" + categoryName + ", categoryId=" + categoryId + ", isDeployAfterEdit="
            + isDeployAfterEdit + ", isDeploy=" + isDeploy + ", isFormEdit=" + isFormEdit + ", lastDeployTime="
            + lastDeployTime + ", isFixed=" + isFixed + ", isFree=" + isFree + ", ids=" + ids + ", path=" + path + "]";
    }

}
