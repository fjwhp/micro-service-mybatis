package aljoin.act.dao.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;

/**
 * 
 * @描述：(实体类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2017-12-12
 */
public class ActRunIdentitylink {


  @TableId("ID_")
  private String id;
  @TableField("REV_")
  private Integer rev;
  @TableField("GROUP_ID_")
  private String groupId;
  @TableField("TYPE_")
  private String type;
  @TableField("USER_ID_")
  private String userId;
  @TableField("TASK_ID_")
  private String taskId;
  @TableField("PROC_INST_ID_")
  private String procInstId;
  @TableField("PROC_DEF_ID_")
  private String procDefId;


  public String getId() {
    return id;
  }

  public ActRunIdentitylink setId(String id) {
    this.id = id;
    return this;
  }

  public Integer getRev() {
    return rev;
  }

  public ActRunIdentitylink setRev(Integer rev) {
    this.rev = rev;
    return this;
  }

  public String getGroupId() {
    return groupId;
  }

  public ActRunIdentitylink setGroupId(String groupId) {
    this.groupId = groupId;
    return this;
  }

  public String getType() {
    return type;
  }

  public ActRunIdentitylink setType(String type) {
    this.type = type;
    return this;
  }

  public String getUserId() {
    return userId;
  }

  public ActRunIdentitylink setUserId(String userId) {
    this.userId = userId;
    return this;
  }

  public String getTaskId() {
    return taskId;
  }

  public ActRunIdentitylink setTaskId(String taskId) {
    this.taskId = taskId;
    return this;
  }

  public String getProcInstId() {
    return procInstId;
  }

  public ActRunIdentitylink setProcInstId(String procInstId) {
    this.procInstId = procInstId;
    return this;
  }

  public String getProcDefId() {
    return procDefId;
  }

  public ActRunIdentitylink setProcDefId(String procDefId) {
    this.procDefId = procDefId;
    return this;
  }

}
