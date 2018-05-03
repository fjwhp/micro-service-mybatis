package aljoin.ass.dao.object;

import java.util.List;

import aljoin.ass.dao.entity.AssCategory;
import aljoin.ass.dao.entity.AssInfo;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AssInfoVO extends AssInfo {

    /**
     * TODO
     */
    private static final long serialVersionUID = 1L;
    /**
     * 序号
     */
    private Integer no;
    /**
     * 多个id
     */
    private String ids;
    /**
     * 分类ids
     */
    private List<AssCategory> categoryIds;

    public List<AssCategory> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<AssCategory> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }
}
