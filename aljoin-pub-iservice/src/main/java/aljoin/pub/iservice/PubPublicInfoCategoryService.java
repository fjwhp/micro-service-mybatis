package aljoin.pub.iservice;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.object.PubPublicInfoCategoryDO;
import aljoin.pub.dao.object.PubPublicInfoCategoryVO;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * 
 * 公共信息分类表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-16
 */
public interface PubPublicInfoCategoryService extends IService<PubPublicInfoCategory> {

	/**
	 * 
	 * 公共信息分类表(分页列表).
	 *
	 * @return：Page<PubPublicInfoCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	public Page<PubPublicInfoCategoryDO> list(PageBean pageBean, PubPublicInfoCategory obj) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	 *
	 * 					@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	public void physicsDeleteById(Long id) throws Exception;

	/**
	 * 
	 * 复制对象(需要完整的对象数据，包括所有的公共字段)
	 *
	 * 								@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	public void copyObject(PubPublicInfoCategory obj) throws Exception;

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
	public RetMsg update(PubPublicInfoCategory obj) throws Exception;

	/**
	 *
	 * 公共信息新增
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	public RetMsg add(PubPublicInfoCategory obj) throws Exception;

	/**
	 *
	 * 公共信息有效分类列表.
	 *
	 * 				@return：List<PubPublicInfoCategory>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	public List<PubPublicInfoCategory> validList(PubPublicInfoCategory obj) throws Exception;

	/**
	 *
	 * 公共信息有效分类列表(App).
	 *
	 * @return：List<AppPubPublicInfoCategoryDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-16
	 */
	public RetMsg categoryList(PubPublicInfoCategory obj) throws Exception;

	/**
	 * 
	 * 公共信息所有分类
	 *
	 * @return：List<PubPublicInfoCategory>
	 *
	 * @author：xuc
	 *
	 * @date：2017年11月24日 上午10:31:16
	 */
	public List<PubPublicInfoCategory> allList(PubPublicInfoCategory obj) throws Exception;

	/**
	 * 
	 * 公共信息分类详情（带分类流程分类）
	 *
	 * @return：List<PubPublicInfoCategory>
	 *
	 * @author：xuc
	 *
	 * @date：2017年11月24日 上午10:31:16
	 */
	public PubPublicInfoCategoryVO detail(PubPublicInfoCategory obj) throws Exception;

}
