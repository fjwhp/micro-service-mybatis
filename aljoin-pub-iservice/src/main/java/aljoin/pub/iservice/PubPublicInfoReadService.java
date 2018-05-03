package aljoin.pub.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.pub.dao.entity.PubPublicInfoRead;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 公共信息阅读表(服务类).
 * 
 * @author：sln
 * 
 * @date： 2017-11-15
 */
public interface PubPublicInfoReadService extends IService<PubPublicInfoRead> {

	/**
	 * 
	 * 公共信息阅读表(分页列表).
	 *
	 * @return：Page<PubPublicInfoRead>
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	public Page<PubPublicInfoRead> list(PageBean pageBean, PubPublicInfoRead obj) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	 *
	 * 					@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	public void physicsDeleteById(Long id) throws Exception;

	/**
	 * 
	 * 复制对象(需要完整的对象数据，包括所有的公共字段)
	 *
	 * 								@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	public void copyObject(PubPublicInfoRead obj) throws Exception;
	
	/**
	 * 
	 * 多数据新增（pubInfo插入时调用）
	 *
	 * 								@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	public void insertList4pub(List<String> userIdList, Long infoId, List<String> userFullNameList) throws Exception;

	/**
	 * 
	 * 更新用户阅读次数
	 *
	 * 								@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	public void update4User(Long userId, Long objId) throws Exception;

	/**
	 * 
	 * 删除公告同时删除该公告的阅读信息+更新用户数据
	 *
	 * 								@return：void
	 *
	 * @author：sln
	 *
	 * @date：2017-11-15
	 */
	public void deletePubRead(Long objId) throws Exception;

}
