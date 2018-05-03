package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutMenu;
import aljoin.object.PageBean;

/**
 * 
 * 菜单表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
public interface AutMenuService extends IService<AutMenu> {

    /**
     * 
     * 菜单表(分页列表).
     *
     * @return：Page<AutMenu>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutMenu> list(PageBean pageBean, AutMenu obj) throws Exception;

    /**
     * 
     * 根据菜单级别和父级菜ID码获取下一个菜单编码或者控件编码,如果isWidget=true, 表示生产一菜单或者二级菜单( 根据menuLevel参数)的控件编码
     *
     * @返回 ： String
     *
     * @author：zhongjy
     *
     * @date：2017年6月3日 上午8:23:48
     */
    public String getNextCode(int menuLevel, String parentCode, boolean isWidget) throws Exception;

    /**
     * 
     * 获取一二级菜单列表
     *
     * @return：List<AutMenu>
     *
     * @author：zhongjy
     *
     * @date：2017年6月1日 下午8:52:53
     */
    List<AutMenu> list(AutMenu obj) throws Exception;
    
    /**
     * 
     * 根据用户id获取菜单.
     *
     * @return：Page<AutMenu>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutMenu> listByUserId(PageBean pageBean, AutMenu obj, long userId) throws Exception;
}
