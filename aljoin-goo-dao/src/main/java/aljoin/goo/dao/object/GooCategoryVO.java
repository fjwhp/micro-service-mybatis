package aljoin.goo.dao.object;

import java.util.List;

import aljoin.goo.dao.entity.GooCategory;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class GooCategoryVO extends GooCategory {

    private static final long serialVersionUID = 1L;

    private List<String> gooCategory;

    public List<String> getGooCategory() {
        return gooCategory;
    }

    public void setGooCategory(List<String> gooCategory) {
        this.gooCategory = gooCategory;
    }
}
