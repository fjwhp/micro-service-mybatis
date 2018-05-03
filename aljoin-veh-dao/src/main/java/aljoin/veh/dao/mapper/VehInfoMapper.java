package aljoin.veh.dao.mapper;

import aljoin.veh.dao.entity.VehInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 车船信息表(Mapper接口).
 * 
 * @author：xuc.
 * 
 * @date： 2018-01-08
 */
public interface VehInfoMapper extends BaseMapper<VehInfo> {
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
    public void copyObject(VehInfo obj) throws Exception;
}