package aljoin.act.dao.object;

import java.util.List;

import aljoin.act.dao.entity.ActAljoinForm;
import aljoin.act.dao.entity.ActAljoinFormAttribute;
import aljoin.act.dao.entity.ActAljoinFormWidget;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActAljoinFormVO {
    /**
     * 存放表单
     */
    ActAljoinForm af;
    /**
     * 存放表单控件list
     */
    List<ActAljoinFormWidget> widgetList;
    /**
     * 存放表单控件属性list
     */
    List<ActAljoinFormAttribute> attributeList;
    /**
     * 多个id
     */
    private String ids;
    /**
     * 临时文件路径
     */
    private String path;
    
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public List<ActAljoinFormWidget> getWidgetList() {
        return widgetList;
    }

    public void setWidgetList(List<ActAljoinFormWidget> widgetList) {
        this.widgetList = widgetList;
    }

    public List<ActAljoinFormAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<ActAljoinFormAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public ActAljoinForm getAf() {
        return af;
    }

    public void setAf(ActAljoinForm af) {
        this.af = af;
    }

    @Override
    public String toString() {
        return "ActAljoinFormVO [af=" + af + ", widgetList=" + widgetList + ", attributeList=" + attributeList + "]";
    }

}
