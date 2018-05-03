package aljoin.sms.dao.mapper;

import aljoin.sms.dao.entity.SmsShortMessage;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 短信表(Mapper接口).
 * 
 * @author：laijy.
 * 
 * @date： 2017-10-10
 */
public interface SmsShortMessageMapper extends BaseMapper<SmsShortMessage> {
    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-10
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
     * @date：2017-10-10
     */
    public void copyObject(SmsShortMessage obj) throws Exception;
}