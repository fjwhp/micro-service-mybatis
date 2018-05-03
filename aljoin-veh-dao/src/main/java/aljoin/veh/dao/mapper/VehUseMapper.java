package aljoin.veh.dao.mapper;

import aljoin.veh.dao.entity.VehUse;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 车船使用申请信息表(Mapper接口).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-08
 */
public interface VehUseMapper extends BaseMapper<VehUse> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：xuc
     *
     * @date：2018-01-08
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：xuc
     *
     * @date：2018-01-08
     */
    public void copyObject(VehUse obj) throws Exception;
}