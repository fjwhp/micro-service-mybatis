package aljoin.sms.dao.mapper;

import aljoin.sms.dao.entity.SmsShortMessageSummary;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 短信明细表(Mapper接口).
 * 
 * @author：huangw.
 * 
 * @date： 2018-01-15
 */
public interface SmsShortMessageSummaryMapper extends BaseMapper<SmsShortMessageSummary> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2018-01-15
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
     * @date：2018-01-15
     */
    public void copyObject(SmsShortMessageSummary obj) throws Exception;
}