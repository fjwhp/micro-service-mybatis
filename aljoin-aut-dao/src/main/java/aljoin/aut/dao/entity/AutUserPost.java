package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * @描述：用户-岗位表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
public class AutUserPost extends Entity<AutUserPost> {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long userId;
    /**
     * 岗位id
     */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long postId;
    /**
     * 是否激活（1：是；0：否）
     */
	private Integer isActive;


	public Long getUserId() {
		return userId;
	}

	public AutUserPost setUserId(Long userId) {
		this.userId = userId;
		return this;
	}

	public Long getPostId() {
		return postId;
	}

	public AutUserPost setPostId(Long postId) {
		this.postId = postId;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public AutUserPost setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

}
