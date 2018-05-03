package aljoin.pub.dao.entity;

import java.util.Date;
import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 公共信息阅读表(实体类).
 * 
 * @author：sln.
 * 
 * @date： 2017-11-15
 */
public class PubPublicInfoRead extends Entity<PubPublicInfoRead> {

    private static final long serialVersionUID = 1L;

    /**
     * 公共信息ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long infoId;
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
     * 是否已经阅读
     */
    private Integer isRead;
    /**
     * （最后）阅读时间
     */
    private Date readTime;
    /**
     * 阅读次数
     */
    private Integer readCount;

    public Long getInfoId() {
        return infoId;
    }

    public PubPublicInfoRead setInfoId(Long infoId) {
        this.infoId = infoId;
        return this;
    }

    public Long getReadUserId() {
        return readUserId;
    }

    public PubPublicInfoRead setReadUserId(Long readUserId) {
        this.readUserId = readUserId;
        return this;
    }

    public String getReadUserFullName() {
        return readUserFullName;
    }

    public PubPublicInfoRead setReadUserFullName(String readUserFullName) {
        this.readUserFullName = readUserFullName;
        return this;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public PubPublicInfoRead setIsRead(Integer isRead) {
        this.isRead = isRead;
        return this;
    }

    public Date getReadTime() {
        return readTime;
    }

    public PubPublicInfoRead setReadTime(Date readTime) {
        this.readTime = readTime;
        return this;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public PubPublicInfoRead setReadCount(Integer readCount) {
        this.readCount = readCount;
        return this;
    }

}
