package aljoin.object;

import java.io.Serializable;

/**
 * 
 * 前后端分页参数类
 *
 * @author：zhongjy
 *
 * @date：2017年5月16日 下午10:14:31
 */
public class PageBean implements Serializable {
    /**
     * TODO
     */
    private static final long serialVersionUID = 1725204143822523471L;
    /**
     * 每页记录数
     */
    private Integer pageSize;
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 是否计算总页数，默认计算
     */
    private Integer isSearchCount = 1;

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getIsSearchCount() {
        return isSearchCount;
    }

    public void setIsSearchCount(Integer isSearchCount) {
        this.isSearchCount = isSearchCount;
    }
}
