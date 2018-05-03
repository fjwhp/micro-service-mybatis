package aljoin.goo.dao.object;

import java.util.List;

import aljoin.goo.dao.entity.GooCategory;
import aljoin.goo.dao.entity.GooInOut;
import aljoin.goo.dao.entity.GooInfo;

/**
 * 
 * 办公用品详情（视图类）
 *
 * @author：xuc
 * 
 * @date：2018年1月4日 上午11:31:25
 */
public class GooInfoVO extends GooInfo {

    /**
     * TODO
     */
    private static final long serialVersionUID = 7053949847375569032L;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public List<GooInOut> getGooInOut() {
        return gooInOut;
    }

    public void setGooInOut(List<GooInOut> gooInOut) {
        this.gooInOut = gooInOut;
    }

    /**
     * 序号
     */
    private Integer no;
    /**
     * 出入库记录
     */
    private List<GooInOut> gooInOut;
    /**
     * 多个id逗号分隔
     */
    private String ids;
    /**
     * 分类ids
     */
    private List<GooCategory> categoryIds;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<GooCategory> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<GooCategory> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
