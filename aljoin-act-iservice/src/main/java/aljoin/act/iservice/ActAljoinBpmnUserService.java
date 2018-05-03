package aljoin.act.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.act.dao.entity.ActAljoinBpmnUser;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程-用户关系表(服务类).
 * 
 * @author：pengsp.
 * 
 * @date： 2017-10-12
 */
public interface ActAljoinBpmnUserService extends IService<ActAljoinBpmnUser> {

    /**
     * 
     * 流程-用户关系表(分页列表).
     *
     * @return：Page<ActAljoinBpmnUser>
     *
     * @author：pengsp
     *
     * @date：2017-10-12
     */
    public Page<ActAljoinBpmnUser> list(PageBean pageBean, ActAljoinBpmnUser obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：pengsp
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
     * @author：pengsp
     *
     * @date：2017-10-12
     */
    public void copyObject(ActAljoinBpmnUser obj) throws Exception;

    /**
     * 
     * 添加授权
     *
     * @return：void
     *
     * @author：huangw
     *
     * @date：2017-11-11
     */
    public RetMsg addUserAndMan(String userId, String manId, String bpmnId, String isCheck) throws Exception;

}
