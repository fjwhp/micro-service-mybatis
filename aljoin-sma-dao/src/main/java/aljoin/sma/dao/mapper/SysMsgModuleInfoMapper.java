package aljoin.sma.dao.mapper;

import aljoin.sma.dao.entity.SysMsgModuleInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 消息模板信息表(Mapper接口).
 * 
 * @author：huangw.
 * 
 * @date： 2017-11-14
 */
public interface SysMsgModuleInfoMapper extends BaseMapper<SysMsgModuleInfo> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-14
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-14
     */
    public void copyObject(SysMsgModuleInfo obj) throws Exception;
}