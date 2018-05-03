package aljoin.act.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.act.dao.entity.ActAljoinFormCategory;
import aljoin.object.PageBean;

/**
 * 
 * 表单分类表(服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-31
 */
public interface ActAljoinFormCategoryService extends IService<ActAljoinFormCategory> {

    /**
     * 
     * 表单分类表(分页列表).
     *
     * @return：Page<ActAljoinFormCategory>
     *
     * @author：wangj
     *
     * @date：2017-08-31
     */
    public Page<ActAljoinFormCategory> list(PageBean pageBean, ActAljoinFormCategory obj) throws Exception;

    /**
     *
     * 表单分类表(根据父ID查询分页列表).
     *
     * @return：Page<ActAljoinFormCategory>
     *
     * @author：wangj
     *
     * @date：2017-08-31
     */
    public Page<ActAljoinFormCategory> selectListByParentId(PageBean pageBean, ActAljoinFormCategory obj)
        throws Exception;

    /**
     *
     * 表单分类表(表单分类列表).
     *
     * @return：Page<ActAljoinFormCategory>
     *
     * @author：wangj
     *
     * @date：2017-08-31
     */
    public List<ActAljoinFormCategory> selectCategoryList(ActAljoinFormCategory obj) throws Exception;

    /**
     *
     * 表单分类表(校验表单是否已经存在).
     *
     * @return：boolean
     *
     * @author：wangj
     *
     * @date：2017-09-05
     */
    public boolean validCategoryName(ActAljoinFormCategory obj, boolean isAdd) throws Exception;

    /**
     *
     * 表单分类表(校验表单分类下的级别).
     *
     * @return：boolean
     *
     * @author：wangj
     *
     * @date：2017-09-05
     */
    public boolean validCategoryLevel(ActAljoinFormCategory obj) throws Exception;

    /**
     * 
     * 根据父ID获取器所有子分类
     *
     * @return：List<ActAljoinFormCategory>
     *
     * @author：zhongjy
     *
     * @date：2018年1月23日 下午3:37:24
     */
    public List<ActAljoinFormCategory> getAllChildCategoryList(Long parentId) throws Exception;

    public List<ActAljoinFormCategory> getCateGoryList();
}
