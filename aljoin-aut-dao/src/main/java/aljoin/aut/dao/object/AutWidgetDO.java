package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutWidget;

/**
 * 
 * 控件数据对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:09:27
 */
public class AutWidgetDO {

    /**
     * 控件对象
     */
    private AutWidget autWidget;
    /**
     * 控件归属菜单
     */
    private AutMenu menu;
    /**
     * 控件归属菜单的父菜单
     */
    private AutMenu parentMenu;
    /**
     * 完整归属菜单名：一级+二级菜单名称
     */
    private String fullMenuName;
    /**
     * 是否选中
     */
    private Integer isCheck;

    public AutWidget getAutWidget() {
        return autWidget;
    }

    public void setAutWidget(AutWidget autWidget) {
        this.autWidget = autWidget;
    }

    public AutMenu getMenu() {
        return menu;
    }

    public void setMenu(AutMenu menu) {
        this.menu = menu;
    }

    public AutMenu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(AutMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public String getFullMenuName() {
        return fullMenuName;
    }

    public void setFullMenuName(String fullMenuName) {
        this.fullMenuName = fullMenuName;
    }

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

}
