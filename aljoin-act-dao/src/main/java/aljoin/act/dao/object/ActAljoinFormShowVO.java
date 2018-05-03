package aljoin.act.dao.object;

import java.util.List;

import aljoin.act.dao.entity.ActAljoinForm;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActAljoinFormShowVO {
    /**
     * 存放表单
     */
    ActAljoinForm af;
    /**
     * 表单控件以及属性list
     */
    List<ActAljoinFormWidgetVO> actAljoinFormWidgetVO;

    List<String> categoryId;

    public ActAljoinForm getAf() {
        return af;
    }

    public void setAf(ActAljoinForm af) {
        this.af = af;
    }

    public List<ActAljoinFormWidgetVO> getActAljoinFormWidgetVO() {
        return actAljoinFormWidgetVO;
    }

    public void setActAljoinFormWidgetVO(List<ActAljoinFormWidgetVO> actAljoinFormWidgetVO) {
        this.actAljoinFormWidgetVO = actAljoinFormWidgetVO;
    }

    public List<String> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<String> categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "ActAljoinFormShowVO [af=" + af + ", actAljoinFormWidgetVO=" + actAljoinFormWidgetVO + ", categoryId="
            + categoryId + "]";
    }

}
