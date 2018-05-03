package aljoin.goo.dao.object;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 办公用品分类表(实体类).
 *
 * @author：xuc
 * 
 * @date：2018年1月2日 下午5:26:04
 */
public class GooCategoryDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private String id;
    /**
     * 序号
     */
    private Integer no;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 排序
     */
    private Integer rank;
    /**
     * 状态 (0：无效 1：有效)
     */
    private String status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建者
     */
    private String createUserName;
    /**
     * 创建者id
     */
    private long createUserId;
    /**
     * 创建者姓名
     */
    private String createUserFullName;

    public String getCreateUserFullName() {
        return createUserFullName;
    }

    public void setCreateUserFullName(String createUserFullName) {
        this.createUserFullName = createUserFullName;
    }

    public long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(long createUserId) {
        this.createUserId = createUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
