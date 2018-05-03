package aljoin.aut.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutMenu;
import aljoin.aut.dao.entity.AutMenuRole;
import aljoin.aut.dao.entity.AutWidget;
import aljoin.aut.dao.mapper.AutMenuMapper;
import aljoin.aut.iservice.AutDepartmentRoleService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutMenuRoleService;
import aljoin.aut.iservice.AutMenuService;
import aljoin.aut.iservice.AutRoleService;
import aljoin.aut.iservice.AutUserRoleService;
import aljoin.aut.iservice.AutWidgetService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 菜单表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
@Service
public class AutMenuServiceImpl extends ServiceImpl<AutMenuMapper, AutMenu> implements AutMenuService {

    @Resource
    private AutWidgetService autWidgetService;
    @Resource
    private AutUserRoleService autUserRoleService;
    @Resource
    private AutDepartmentUserService autDepartmentUserService;
    @Resource
    private AutDepartmentRoleService autDepartmentRoleService;
    @Resource
    private AutMenuRoleService autMenuRoleService;
    @Resource
    private AutRoleService autRoleService;

    @Override
    public Page<AutMenu> list(PageBean pageBean, AutMenu obj) throws Exception {
        Where<AutMenu> where = new Where<AutMenu>();
        if (StringUtils.isNotEmpty(obj.getMenuName())) {
            where.like("menu_name", obj.getMenuName());
        }
        where.orderBy("menu_code", true);
        Page<AutMenu> page = selectPage(new Page<AutMenu>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public String getNextCode(int menuLevel, String parentCode, boolean isWidget) throws Exception {
        String nextCode = "";
        if (isWidget) {
            // 生成控件编码
            Where<AutWidget> where = new Where<AutWidget>();
            if (menuLevel == 1) {
                where.eq("parent_menu_code", parentCode);
            } else if (menuLevel == 2) {
                where.eq("menu_code", parentCode);
            }
            where.orderBy("widget_code", false);
            where.setSqlSelect("widget_code");
            // where.last("LIMIT 0,1");
            AutWidget autWidget = autWidgetService.selectOne(where);
            String preCode = parentCode.substring(0, 4);
            if (autWidget != null) {
                Integer codeNum = Integer.parseInt(autWidget.getWidgetCode().substring(4, 6));
                codeNum++;
                if (codeNum > 99) {
                    throw new Exception("控件数已经超过99");
                } else {
                    if (codeNum > 9) {
                        nextCode = preCode + codeNum;
                    } else {
                        nextCode = preCode + "0" + codeNum;
                    }
                }
            } else {
                nextCode = preCode + "01";
            }
        } else {
            // 生成菜单编码
            if (menuLevel == 1) {
                // 一级菜单
                Where<AutMenu> where = new Where<AutMenu>();
                where.eq("menu_level", 1);
                where.orderBy("menu_code", false);
                where.setSqlSelect("menu_code");
                // where.last("LIMIT 0,1");
                AutMenu autMenu = selectOne(where);
                if (autMenu != null) {
                    nextCode = autMenu.getMenuCode();
                    String codeStr = nextCode.substring(0, 2);
                    Integer codeNum = Integer.parseInt(codeStr);
                    codeNum++;
                    if (codeNum > 99) {
                        throw new Exception("菜单数已经超过99");
                    } else {
                        if (codeNum > 9) {
                            codeStr = codeNum + "0000";
                        } else {
                            codeStr = "0" + codeNum + "0000";
                        }
                        nextCode = codeStr;
                    }
                } else {
                    nextCode = "000000";
                }
            } else if (menuLevel == 2) {
                // 二级菜单
                Where<AutMenu> where = new Where<AutMenu>();
                where.eq("parent_code", parentCode);
                where.orderBy("menu_code", false);
                // where.last("LIMIT 0,1");
                where.setSqlSelect("menu_code");
                AutMenu autMenu = selectOne(where);
                if (autMenu != null) {
                    nextCode = autMenu.getMenuCode();

                    String codeStr = nextCode.substring(2, 4);
                    Integer codeNum = Integer.parseInt(codeStr);
                    codeNum++;
                    if (codeNum > 99) {
                        throw new Exception("菜单数已经超过99");
                    } else {
                        if (codeNum > 9) {
                            codeStr = parentCode.substring(0, 2) + codeNum + "00";
                        } else {
                            codeStr = parentCode.substring(0, 2) + "0" + codeNum + "00";
                        }
                        nextCode = codeStr;
                    }
                } else {
                    nextCode = parentCode.substring(0, 2) + "0100";
                }
            }
        }
        return nextCode;
    }

    @Override
    public List<AutMenu> list(AutMenu obj) throws Exception {
        Where<AutMenu> where = new Where<AutMenu>();
        if (obj.getMenuLevel() != null) {
            where.eq("menu_level", obj.getMenuLevel());
        }
        if (obj.getParentCode() != null) {
            where.eq("parent_code", obj.getParentCode());
        }
        where.orderBy("menu_code", true);
        List<AutMenu> list = selectList(where);
        return list;
    }

    @Override
    public Page<AutMenu> listByUserId(PageBean pageBean, AutMenu obj, long userId) throws Exception {
        //根据用户id获取其所有角色，包含所属部门的角色id
        //用户-角色
        List<Long> roleIdList = autRoleService.getRoleIdByUserId(userId);
        
        //根据角色获取菜单
        Where<AutMenuRole> w1 = new Where<AutMenuRole>();
        w1.in("role_id", roleIdList).eq("is_active", 1);
        w1.setSqlSelect("menu_id");
        List<AutMenuRole> autMenuRoleList = autMenuRoleService.selectList(w1);
        List<Long> menuIdList = new ArrayList<Long>();
        for (AutMenuRole autMenuRole : autMenuRoleList) {
            menuIdList.add(autMenuRole.getMenuId());
        }
        
        Where<AutMenu> w2 = new Where<AutMenu>();
        w2.in("id", menuIdList);
        w2.orderBy("menu_code", true);
        Page<AutMenu> page = selectPage(new Page<AutMenu>(pageBean.getPageNum(), pageBean.getPageSize()), w2);
        return page;
    }
}
