package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutWidget;

/**
 * 
 * 菜单对象(tree).
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:09:27
 */
public class AutMenuVO extends AutMenu {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 二级子菜单
     */
    private List<AutMenuVO> children;
    /**
     * 菜单控件
     */
    private List<AutWidget> widgetList;

    public List<AutMenuVO> getChildren() {
        return children;
    }

    public void setChildren(List<AutMenuVO> children) {
        this.children = children;
    }

    public List<AutWidget> getWidgetList() {
        return widgetList;
    }

    public void setWidgetList(List<AutWidget> widgetList) {
        this.widgetList = widgetList;
    }

}
