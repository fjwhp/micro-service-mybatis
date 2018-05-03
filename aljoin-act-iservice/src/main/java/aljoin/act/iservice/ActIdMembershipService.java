package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActIdMembership;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * (服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-12
 */
public interface ActIdMembershipService extends IService<ActIdMembership> {

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
    public Page<ActIdMembership> list(PageBean pageBean, ActIdMembership obj) throws Exception;

}
