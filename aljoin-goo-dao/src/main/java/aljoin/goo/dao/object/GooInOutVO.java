package aljoin.goo.dao.object;

import java.util.List;

import aljoin.goo.dao.entity.GooInOut;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class GooInOutVO extends GooInOut {

    /**
     * TODO
     */
    private static final long serialVersionUID = 1L;
    /**
     * 实体类list
     */
    private List<GooInOut> gooInfoList;
    /**
     * 多个id
     */
    private String ids;

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<GooInOut> getGooInfoList() {
        return gooInfoList;
    }

    public void setGooInfoList(List<GooInOut> gooInfoList) {
        this.gooInfoList = gooInfoList;
    }
}
