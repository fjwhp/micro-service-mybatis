package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUsefulOpinion;
import aljoin.aut.dao.object.AutUsefulOpinionVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 常用意见表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
public interface AutUsefulOpinionService extends IService<AutUsefulOpinion> {

    /**
     * 
     * 常用意见表(分页列表).
     *
     * @return：Page<AutUsefulOpinion>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    public Page<AutUsefulOpinionVO> list(PageBean pageBean, AutUsefulOpinion obj, Long customUserId) throws Exception;

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
    public void copyObject(AutUsefulOpinion obj) throws Exception;

    /**
     *
     * 常用意见表.
     *
     * @return：List<AutUsefulOpinion>
     *
     * @author：wangj
     *
     * @date：2017-11-20
     */
    public List<AutUsefulOpinion> list(AutUsefulOpinion obj) throws Exception;

    /**
     *
     * APP添加常用意见表.
     *
     * @return：RetMsg
     *
     * @author：huangw
     *
     * @date：2017-12-20
     */
    public RetMsg appAdd(AutUsefulOpinion obj) throws Exception;

    /**
     *
     * APP常用意见表.
     *
     * @return：List<String>
     *
     * @author：huangw
     *
     * @date：2017-12-20
     */
    public List<AutUsefulOpinion> appList(Long userID) throws Exception;
}
