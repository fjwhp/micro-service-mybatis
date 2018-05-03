package aljoin.aut.dao.mapper;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import aljoin.aut.dao.entity.AutUser;

/**
 * 
 * 用户表(Mapper接口).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-06-10
 */
public interface AutUserMapper extends BaseMapper<AutUser> {

    /**
     * 
     * 自定义mapper方法例子
     *
     * @return：List<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年8月17日 上午11:41:37
     */
    public List<AutUser> getMyUserList() throws Exception;

}
