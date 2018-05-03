package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinFormAttribute;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 表单控属性件表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
public interface ActAljoinFormAttributeService extends IService<ActAljoinFormAttribute> {

    /**
     * 
     * 表单控属性件表(分页列表).
     *
     * @return：Page<ActAljoinFormAttribute>
     *
     * @author：zhongjy
     *
     * @date：2017-08-31
     */
    public Page<ActAljoinFormAttribute> list(PageBean pageBean, ActAljoinFormAttribute obj) throws Exception;
}
