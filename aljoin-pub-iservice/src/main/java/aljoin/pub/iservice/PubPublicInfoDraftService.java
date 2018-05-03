package aljoin.pub.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfoDraft;
import aljoin.pub.dao.object.PubPublicInfoDO;
import aljoin.pub.dao.object.PubPublicInfoDraftVO;

/**
 * 
 * 公共信息草稿表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
public interface PubPublicInfoDraftService extends IService<PubPublicInfoDraft> {

    /**
     * 
     * 公共信息草稿表(分页列表).
     *
     * @return：Page<PubPublicInfoDraft>
     *
     * @author：laijy
     *
     * @date：2017-10-12
     */
    public Page<PubPublicInfoDO> list(PageBean pageBean, PubPublicInfoDraftVO obj) throws Exception;

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
    public void copyObject(PubPublicInfoDraft obj) throws Exception;

    /**
     *
     * 公共信息新建
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public RetMsg add(PubPublicInfoDraftVO obj) throws Exception;

    /**
     *
     * 公共信息详情
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public PubPublicInfoDraftVO detail(PubPublicInfoDraft obj) throws Exception;

    /**
     *
     * 公共信息编辑
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-16
     */
    public RetMsg update(PubPublicInfoDraftVO obj) throws Exception;

    /**
     *
     * 公共信息删除
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-12
     */
    public RetMsg delete(PubPublicInfoDraftVO obj) throws Exception;

    /**
     *
     * 公共信息草稿提交流程
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017-10-25
     */
    public RetMsg addProcess(PubPublicInfoDraftVO obj) throws Exception;
}
