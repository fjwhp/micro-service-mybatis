package aljoin.act.iservice;

import java.util.List;

import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.act.dao.entity.ActAljoinCategory;
import aljoin.act.dao.object.ActAljoinCategoryBpmnVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 流程分类表(服务类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:00:22
 */
public interface ActAljoinCategoryService extends IService<ActAljoinCategory> {

    /**
     * 
     * 流程分类表(分页列表).
     *
     * @return：Page<ActAljoinCategory>
     *
     * @author：zhongjy
     *
     * @date：2017-07-06
     */
    public Page<ActAljoinCategory> list(PageBean pageBean, ActAljoinCategory obj) throws Exception;

    /**
     * 
     * 获得所有流程分类
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：laijy
     *
     * @date：2017年8月31日 下午1:56:50
     */
    public List<ActAljoinCategory> getAllCategoryList() throws Exception;

    /**
     * 
     * 比较页面新增的用户名是否唯一
     *
     * @return：String
     *
     * @author：laijy
     *
     * @date：2017年8月31日 上午10:11:41
     */
    public Boolean compareCategoryName(ActAljoinCategory obj) throws Exception;

    /**
     * 
     * 根据流程分类id查看详情
     *
     * @return：ActAljoinCategory
     *
     * @author：laijy
     *
     * @date：2017年8月31日 下午4:19:33
     */
    public ActAljoinCategory getById(ActAljoinCategory obj);

    /**
     * 
     * 判断新增流程分类的时候是否超过数量(同级子流程最多999)
     *
     * @return：Boolean
     *
     * @author：laijy
     *
     * @date：2017年8月31日 下午4:34:30
     */
    public Boolean outNumber(ActAljoinCategory obj);

    /**
     * 
     * 根据父ID获取子分类
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：laijy
     *
     * @date：2017年9月4日 上午10:11:27
     */
    public List<ActAljoinCategory> getByParentId(ActAljoinCategory obj) throws Exception;

    /**
     * 
     * 根据父ID获取子分类
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：laijy
     *
     * @date：2017年9月4日 上午10:11:27
     */
    public List<ActAljoinCategory> getAllParentCategoryList(Long categoryid) throws Exception;

    /**
     * 
     * 根据ID获取所有子分类
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：huangw
     *
     * @date：2017年11月10日
     */
    public List<ActAljoinCategory> getAllChildList(Long categoryid) throws Exception;

    /**
     * 
     * 获取用户分类列表
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：huangw
     *
     * @date：2017年11月10日
     */
    public List<ActAljoinCategory> getUserBpmnList(String userID) throws Exception;

    /**
     *
     * 获取所有顶级父分类.
     *
     * @return：List<ActAljoinCategory>
     *
     * @author：wangj
     *
     * @date：2017-12-19
     */
    public RetMsg getAllParentCategoryList(ActAljoinCategory obj) throws Exception;

	public List<ActAljoinCategoryBpmnVO> getAllCategoryBpmnList() throws Exception;


}
