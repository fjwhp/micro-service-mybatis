package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFormAttributeRun;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 表单控属性件表(运行时)(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
public interface ActAljoinFormAttributeRunService extends IService<ActAljoinFormAttributeRun> {

    /**
     * 
     * 表单控属性件表(运行时)(分页列表).
     *
     * @return：Page<ActAljoinFormAttributeRun>
     *
     * @author：zhongjy
     *
     * @date：2017-09-27
     */
    public Page<ActAljoinFormAttributeRun> list(PageBean pageBean, ActAljoinFormAttributeRun obj) throws Exception;

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
    public void copyObject(ActAljoinFormAttributeRun obj) throws Exception;
}
