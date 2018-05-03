package aljoin.aut.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutWidget;
import aljoin.aut.dao.mapper.AutWidgetMapper;
import aljoin.aut.dao.object.AutWidgetDO;
import aljoin.aut.iservice.AutMenuService;
import aljoin.aut.iservice.AutWidgetService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 控件表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
@Service
public class AutWidgetServiceImpl extends ServiceImpl<AutWidgetMapper, AutWidget> implements AutWidgetService {

    @Resource
    private AutMenuService autMenuService;

    @Override
    public Page<AutWidget> list(PageBean pageBean, AutWidget obj) throws Exception {
        Where<AutWidget> where = new Where<AutWidget>();
        where.orderBy("create_time", false);
        Page<AutWidget> page = selectPage(new Page<AutWidget>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public Page<AutWidgetDO> list(PageBean pageBean, AutWidgetDO obj) throws Exception {
        Where<AutWidget> where = new Where<AutWidget>();
        // 根据归属(父)菜单名称,归属(父)菜单编码，控件名称,控件编码查询
        if (StringUtils.isNotEmpty(obj.getFullMenuName())) {
            // 根据关键字查询菜单
            Where<AutMenu> menuWhere = new Where<AutMenu>();
            menuWhere.like("menu_name", obj.getFullMenuName());
            menuWhere.or("menu_code LIKE {0}", "%" + obj.getFullMenuName() + "%");
            menuWhere.setSqlSelect("id");
            List<AutMenu> menuList = autMenuService.selectList(menuWhere);
            String idInStr = "";
            for (AutMenu autMenu : menuList) {
                idInStr += autMenu.getId() + ",";
            }
            if (!"".equals(idInStr)) {
                idInStr = idInStr.substring(0, idInStr.length() - 1);
            }

            where.like("widget_code", obj.getFullMenuName());
            where.or("widget_name LIKE {0}", "%" + obj.getFullMenuName() + "%");
            if (!"".equals(idInStr)) {
                where.or("menu_id IN({0})", idInStr);
                where.or("parent_menu_id IN({0})", idInStr);
            }
        }
        where.orderBy("widget_code", true);
        Page<AutWidget> page = selectPage(new Page<AutWidget>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        List<AutWidget> widgetList = page.getRecords();

        Page<AutWidgetDO> doPage = new Page<AutWidgetDO>();
        doPage.setCurrent(page.getCurrent());
        doPage.setSize(page.getSize());
        doPage.setTotal(page.getTotal());
        List<AutWidgetDO> doList = new ArrayList<AutWidgetDO>();
        for (AutWidget w : widgetList) {
            AutWidgetDO widgetDO = new AutWidgetDO();
            widgetDO.setAutWidget(w);
            AutMenu menu = autMenuService.selectById(w.getMenuId());
            AutMenu parentMenu = autMenuService.selectById(w.getParentMenuId());
            String fullMenuName = "";
            if (parentMenu != null) {
                fullMenuName = parentMenu.getMenuName() + " -> ";
            }
            if (menu != null) {
                fullMenuName += menu.getMenuName();
            }
            widgetDO.setFullMenuName(fullMenuName);
            doList.add(widgetDO);
        }
        doPage.setRecords(doList);
        return doPage;
    }

}
