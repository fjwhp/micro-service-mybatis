package aljoin.act.dao.object;

import java.util.List;

import aljoin.act.dao.entity.ActAljoinFormAttribute;
import aljoin.act.dao.entity.ActAljoinFormWidget;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActAljoinFormWidgetVO {
    /**
     * 存放表单控件
     */
    ActAljoinFormWidget actAljoinFormWidget;
    /**
     * 存放表单控件属性list
     */
    List<ActAljoinFormAttribute> attributeList;

    public List<ActAljoinFormAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<ActAljoinFormAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public ActAljoinFormWidget getActAljoinFormWidget() {
        return actAljoinFormWidget;
    }

    public void setActAljoinFormWidget(ActAljoinFormWidget actAljoinFormWidget) {
        this.actAljoinFormWidget = actAljoinFormWidget;
    }

    @Override
    public String toString() {
        return "ActAljoinFormWidgetVO [actAljoinFormWidget=" + actAljoinFormWidget + ", attributeList=" + attributeList
            + "]";
    }

}
