package aljoin.goo.dao.object;

import java.util.List;

import aljoin.goo.dao.entity.GooPurchase;

/**
 * 
 * 办公用品申购视图类
 *
 * @author：xuc
 * 
 * @date：2018年1月4日 下午4:34:49
 */
public class GooPurchaseVO extends GooPurchase {

    /**
     * TODO
     */
    private static final long serialVersionUID = 6096402411346185280L;

    /**
     * 申购物品列表
     */
    private List<GooPurchase> gooInfoList;
    /**
     * 多个id
     */
    private String ids;

    public List<GooPurchase> getGooInfoList() {
        return gooInfoList;
    }

    public void setGooInfoList(List<GooPurchase> gooInfoList) {
        this.gooInfoList = gooInfoList;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

}
