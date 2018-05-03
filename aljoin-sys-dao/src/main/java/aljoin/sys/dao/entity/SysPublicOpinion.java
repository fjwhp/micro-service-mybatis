package aljoin.sys.dao.entity;

import aljoin.dao.entity.Entity;

/**
 * 
 * @描述：公共意见表(实体类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-14
 */
public class SysPublicOpinion extends Entity<SysPublicOpinion> {

    private static final long serialVersionUID = 1L;

    /**
     * 公共意见内容
     */
	private String content;
    /**
     * 公共意见排序
     */
	private Integer contentRank;
    /**
     * 是否激活(1:是;0:否)
     */
	private Integer isActive;


	public String getContent() {
		return content;
	}

	public SysPublicOpinion setContent(String content) {
		this.content = content;
		return this;
	}

	public Integer getContentRank() {
		return contentRank;
	}

	public SysPublicOpinion setContentRank(Integer contentRank) {
		this.contentRank = contentRank;
		return this;
	}

	public Integer getIsActive() {
		return isActive;
	}

	public SysPublicOpinion setIsActive(Integer isActive) {
		this.isActive = isActive;
		return this;
	}

}
