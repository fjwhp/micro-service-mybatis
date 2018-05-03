package aljoin.att.dao.mapper;

import aljoin.att.dao.entity.AttSignInOut;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 
 * 签到、退表(Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-27
 */
public interface AttSignInOutMapper extends BaseMapper<AttSignInOut> {
    /**
     *
     * 删除上个月的考勤记录.
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-09-28
     */
    public void deleteLastSignInOut();
}