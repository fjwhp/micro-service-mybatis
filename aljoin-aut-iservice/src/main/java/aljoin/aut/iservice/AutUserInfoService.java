package aljoin.aut.iservice;

import aljoin.aut.dao.object.AutUserInfoVO;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.aut.dao.entity.AutUserInfo;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * 
 * 用户信息表(服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-06
 */
public interface AutUserInfoService extends IService<AutUserInfo> {

    /**
     * 
     * 用户信息表(分页列表).
     *
     * @return：Page<AutUserInfo>
     *
     * @author：wangj
     *
     * @date：2017-09-06
     */
    public Page<AutUserInfo> list(PageBean pageBean, AutUserInfo obj) throws Exception;

    /**
     *
     * 根据用户ID获得用户信息列表(不分页).
     *
     * @return：List<AutUserInfo>
     *
     * @author：wangj
     *
     * @date：2017-09-06
     */
    public List<AutUserInfo> list(AutUserInfo obj) throws Exception;

    /**
     *
     * 批量新增或修改.
     *
     * @return：boolean
     *
     * @author：wangj
     *
     * @date：2017-09-07
     */
    public RetMsg addOrUpdateBatch(AutUserInfoVO obj) throws Exception;

    /**
     *
     * 新增或修改时验证key值是否重复
     *
     * @return：boolean
     *
     * @author：wangj
     *
     * @date：2017-09-11
     */
    public RetMsg validate(AutUserInfo obj) throws Exception;
}
