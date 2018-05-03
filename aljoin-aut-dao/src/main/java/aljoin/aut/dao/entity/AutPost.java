package aljoin.aut.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * @描述：岗位表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
public class AutPost extends Entity<AutPost> {

    private static final long serialVersionUID = 1L;

    /**
     * 岗位名称
     */
	private String postName;
    /**
     * 岗位排序
     */
	private Integer postRank;
    /**
     * 是否激活（1：是；0：否）
     */
	private Integer isActive;


	public String getPostName() {
		return postName;
	}

	public AutPost setPostName(String postName) {
		this.postName = postName;
		return this;
	}

	public Integer getPostRank() {
		return postRank;
	}

	public AutPost setPostRank(Integer postRank) {
		this.postRank = postRank;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public AutPost setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

}
