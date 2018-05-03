package aljoin.app.object;

import java.util.List;

/**
 * 
 * 邮件列表返回数据对象
 *
 * @author：zhongjy
 * 
 * @date：2017年10月23日 下午2:54:19
 */
public class MaiDraftListVO {
	/**
	 * 当前页
	 */
	private Integer total;
	/**
	 * 页数条数
	 */
	private Integer size;
	/**
	 * 总页
	 */
	private Integer pages;
	/**
	 * 草稿箱列表
	 */
	private List<MaiValListVO> maiDraftBoxList;

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getPages() {
		return pages;
	}

	public void setPages(Integer pages) {
		this.pages = pages;
	}

	public List<MaiValListVO> getMaiDraftBoxList() {
		return maiDraftBoxList;
	}

	public void setMaiDraftBoxList(List<MaiValListVO> maiDraftBoxList) {
		this.maiDraftBoxList = maiDraftBoxList;
	}

}
