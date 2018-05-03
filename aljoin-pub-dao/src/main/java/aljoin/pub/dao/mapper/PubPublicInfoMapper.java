package aljoin.pub.dao.mapper;

import aljoin.pub.dao.entity.PubPublicInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 公共信息表(Mapper接口).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-12
 */
public interface PubPublicInfoMapper extends BaseMapper<PubPublicInfo> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public void copyObject(PubPublicInfo obj) throws Exception;
}