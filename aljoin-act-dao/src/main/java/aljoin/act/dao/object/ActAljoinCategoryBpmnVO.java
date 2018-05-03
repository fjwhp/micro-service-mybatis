package aljoin.act.dao.object;

import java.util.Date;

public class ActAljoinCategoryBpmnVO {

	private String id;
	/**
	 * 树节点对应PID
	 */
	private String pId;
	/**
	 * 树节点对应名称
	 */
	private String noteName;
	/**
	 * 是否是分类 0:流程，1:分类
	 */
	private String isCategory;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getpId() {
		return pId;
	}

	public void setpId(String pId) {
		this.pId = pId;
	}

	public String getNoteName() {
		return noteName;
	}

	public void setNoteName(String noteName) {
		this.noteName = noteName;
	}

	public String getIsCategory() {
		return isCategory;
	}

	public void setIsCategory(String isCategory) {
		this.isCategory = isCategory;
	}

}
