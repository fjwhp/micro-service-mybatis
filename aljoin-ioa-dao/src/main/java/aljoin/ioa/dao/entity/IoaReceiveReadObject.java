package aljoin.ioa.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 收文阅件-传阅对象表(分类)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaReceiveReadObject extends Entity<IoaReceiveReadObject> {

    private static final long serialVersionUID = 1L;

    /**
     * 传阅对象ID（数据字典DICT_READ_OBJECT_LEADER+DICT_READ_OBJECT_OFFICE+DICT_READ_OBJECT_OTHER）
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long objectId;
    /**
     * 传阅名称（数据字典DICT_READ_OBJECT_LEADER+DICT_READ_OBJECT_OFFICE+DICT_READ_OBJECT_OTHER）
     */
    private String objectName;
    /**
     * 收文阅件主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiveFileId;
    /**
     * 该对象对应的用户ids
     */
    private String readUserIds;
    /**
     * 该对象对应的用户names
     */
    private String readUserNames;

    public Long getObjectId() {
        return objectId;
    }

    public IoaReceiveReadObject setObjectId(Long objectId) {
        this.objectId = objectId;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public IoaReceiveReadObject setObjectName(String objectName) {
        this.objectName = objectName;
        return this;
    }

    public Long getReceiveFileId() {
        return receiveFileId;
    }

    public IoaReceiveReadObject setReceiveFileId(Long receiveFileId) {
        this.receiveFileId = receiveFileId;
        return this;
    }

    public String getReadUserIds() {
        return readUserIds;
    }

    public IoaReceiveReadObject setReadUserIds(String readUserIds) {
        this.readUserIds = readUserIds;
        return this;
    }

    public String getReadUserNames() {
        return readUserNames;
    }

    public IoaReceiveReadObject setReadUserNames(String readUserNames) {
        this.readUserNames = readUserNames;
        return this;
    }

}
