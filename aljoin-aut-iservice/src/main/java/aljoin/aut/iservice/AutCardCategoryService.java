package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutCardCategory;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 名片分类表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
public interface AutCardCategoryService extends IService<AutCardCategory> {

    /**
     * 
     * 名片分类表(分页列表).
     *
     * @return：Page<ActCardCategory>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    public Page<AutCardCategory> list(PageBean pageBean, AutCardCategory obj, Long customUserId) throws Exception;

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
    public void copyObject(AutCardCategory obj) throws Exception;

    /**
     * 
     * 新增名片分类
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月11日 上午8:54:43
     */
    public RetMsg add(AutCardCategory obj, Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-名片夹分类-列表
     *
     * @return：List<AutCardCategory>
     *
     * @author：laijy
     *
     * @date：2017年10月27日 上午10:34:22
     */
    public List<AutCardCategory> getCardCategoryList(Long customUserId) throws Exception;

}
