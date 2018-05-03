package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutWidget;
import aljoin.aut.dao.object.AutWidgetDO;
import aljoin.object.PageBean;

/**
 * 
 * 控件表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
public interface AutWidgetService extends IService<AutWidget> {

    /**
     * 
     * 控件表(分页列表).
     *
     * @return：Page<AutWidget>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutWidget> list(PageBean pageBean, AutWidget obj) throws Exception;

    /**
     * 
     * 控件表,含菜单信息(分页列表).
     *
     * @return：Page<AutWidget>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutWidgetDO> list(PageBean pageBean, AutWidgetDO obj) throws Exception;
}
