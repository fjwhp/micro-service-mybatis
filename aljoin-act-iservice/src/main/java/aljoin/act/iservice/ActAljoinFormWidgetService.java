package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFormWidget;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 表单控件表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
public interface ActAljoinFormWidgetService extends IService<ActAljoinFormWidget> {

    /**
     * 
     * 表单控件表(分页列表).
     *
     * @return：Page<ActAljoinFormWidget>
     *
     * @author：zhongjy
     *
     * @date：2017-08-31
     */
    public Page<ActAljoinFormWidget> list(PageBean pageBean, ActAljoinFormWidget obj) throws Exception;
}
