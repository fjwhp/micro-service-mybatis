package aljoin.ioa.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 收文阅件-用户评论(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaReceiveReadUser extends Entity<IoaReceiveReadUser> {

    private static final long serialVersionUID = 1L;

    /**
     * 收文阅件-传阅对象表(分类)主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiveReadObjectId;
    /**
     * 收文阅件表主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiveFileId;
    /**
     * 阅读用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long readUserId;
    /**
     * 阅读用户姓名
     */
    private String readUserFullName;
    /**
     * 阅读用户部门ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long readDeptId;
    /**
     * 阅读用户部门名称
     */
    private String readDeptName;
    /**
     * 是否已经阅读
     */
    private Integer isRead;
    /**
     * 阅读时间
     */
    private Date readTime;
    /**
     * 阅读意见
     */
    private String readOpinion;

    public Long getReceiveReadObjectId() {
        return receiveReadObjectId;
    }

    public IoaReceiveReadUser setReceiveReadObjectId(Long receiveReadObjectId) {
        this.receiveReadObjectId = receiveReadObjectId;
        return this;
    }

    public Long getReceiveFileId() {
        return receiveFileId;
    }

    public IoaReceiveReadUser setReceiveFileId(Long receiveFileId) {
        this.receiveFileId = receiveFileId;
        return this;
    }

    public Long getReadUserId() {
        return readUserId;
    }

    public IoaReceiveReadUser setReadUserId(Long readUserId) {
        this.readUserId = readUserId;
        return this;
    }

    public String getReadUserFullName() {
        return readUserFullName;
    }

    public IoaReceiveReadUser setReadUserFullName(String readUserFullName) {
        this.readUserFullName = readUserFullName;
        return this;
    }

    public Long getReadDeptId() {
        return readDeptId;
    }

    public IoaReceiveReadUser setReadDeptId(Long readDeptId) {
        this.readDeptId = readDeptId;
        return this;
    }

    public String getReadDeptName() {
        return readDeptName;
    }

    public IoaReceiveReadUser setReadDeptName(String readDeptName) {
        this.readDeptName = readDeptName;
        return this;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public IoaReceiveReadUser setIsRead(Integer isRead) {
        this.isRead = isRead;
        return this;
    }

    public Date getReadTime() {
        return readTime;
    }

    public IoaReceiveReadUser setReadTime(Date readTime) {
        this.readTime = readTime;
        return this;
    }

    public String getReadOpinion() {
        return readOpinion;
    }

    public IoaReceiveReadUser setReadOpinion(String readOpinion) {
        this.readOpinion = readOpinion;
        return this;
    }

}
