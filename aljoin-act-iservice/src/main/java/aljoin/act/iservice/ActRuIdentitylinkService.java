package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActRuIdentitylink;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * (服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-12
 */
public interface ActRuIdentitylinkService extends IService<ActRuIdentitylink> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<ActRuIdentitylink>
     *
     * @author：zhongjy
     *
     * @date：2017-12-12
     */
    public Page<ActRuIdentitylink> list(PageBean pageBean, ActRuIdentitylink obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-12
     */
    public void physicsDeleteById(String id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-12-12
     */
    public void copyObject(ActRuIdentitylink obj) throws Exception;
}
