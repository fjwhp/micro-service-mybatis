package aljoin.aut.dao.object;

import java.util.List;

import aljoin.aut.dao.entity.AutMenu;

/**
 * 
 * 菜单对象
 *
 * @author：zhongjy
 *
 * @date：2017年5月3日 下午5:09:27
 */
public class AutMenuDO {

    private AutMenu autMenu;
    private AutMenu parentMenu;
    private List<AutWidgetDO> widgetList;
    private Integer isCheck;

    public AutMenu getAutMenu() {
        return autMenu;
    }

    public void setAutMenu(AutMenu autMenu) {
        this.autMenu = autMenu;
    }

    public AutMenu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(AutMenu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public List<AutWidgetDO> getWidgetList() {
        return widgetList;
    }

    public void setWidgetList(List<AutWidgetDO> widgetList) {
        this.widgetList = widgetList;
    }

    public Integer getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(Integer isCheck) {
        this.isCheck = isCheck;
    }

}
