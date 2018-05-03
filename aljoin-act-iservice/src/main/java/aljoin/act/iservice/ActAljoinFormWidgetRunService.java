package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFormWidgetRun;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 表单控件表(运行时)(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
public interface ActAljoinFormWidgetRunService extends IService<ActAljoinFormWidgetRun> {

    /**
     * 
     * 表单控件表(运行时)(分页列表).
     *
     * @return：Page<ActAljoinFormWidgetRun>
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
     */
    public Page<ActAljoinFormWidgetRun> list(PageBean pageBean, ActAljoinFormWidgetRun obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
     */
    public void copyObject(ActAljoinFormWidgetRun obj) throws Exception;
}
