package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 常用意见表(实体类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public class AutUsefulOpinion extends Entity<AutUsefulOpinion> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 意见内容
     */
    private String content;
    /**
     * 意见排序
     */
    private Integer contentRank;
    /**
     * 是否激活
     */
    private Integer isActive;

    public Long getUserId() {
        return userId;
    }

    public AutUsefulOpinion setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public AutUsefulOpinion setContent(String content) {
        this.content = content;
        return this;
    }

    public Integer getContentRank() {
        return contentRank;
    }

    public AutUsefulOpinion setContentRank(Integer contentRank) {
        this.contentRank = contentRank;
        return this;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public AutUsefulOpinion setIsActive(Integer isActive) {
        this.isActive = isActive;
        return this;
    }

}
