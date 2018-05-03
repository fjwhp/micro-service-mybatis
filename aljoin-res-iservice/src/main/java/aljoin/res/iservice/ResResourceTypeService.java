package aljoin.res.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.dao.entity.ResResourceType;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 资源分类表(服务类).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-05
 */
public interface ResResourceTypeService extends IService<ResResourceType> {

	/**
	 * 
	 * 资源分类表(分页列表).
	 *
	 * @return：Page<ResResourceType>
	 *
	 * @author：laijy
	 *
	 * @date：2017-09-05
	 */
	public Page<ResResourceType> list(PageBean pageBean, ResResourceType obj) throws Exception;
	
	/**
	 * 
	 * 判断新增资源分类的时候是否超过数量(同级分类最多999个)
	 *
	 * @return：Boolean
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午3:41:19
	 */
	public Boolean outNumber(ResResourceType obj);
	
	/**
	 * 
	 * 比较页面新增的分类名是否唯一
	 *
	 * @return：Boolean
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午3:48:06
	 */
	public Boolean compareTypeName(ResResourceType obj) throws Exception;
	
	/**
	 * 
	 * 获得所有资源分类
	 *
	 * @return：List<ResResourceType>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午3:51:48
	 */
	public List<ResResourceType> getAllResourceTypeList() throws Exception;
	
	/**
	 * 
	 * 根据id查看详情
	 *
	 * @return：ResResourceType
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月5日 下午4:41:29
	 */
	public ResResourceType getById(ResResourceType obj);
	
	/**
	 * 
	 * 根据Pid查询子分类(要查询子分类的分类id作为Pid传过来)
	 *
	 * @return：List<ResResourceType>
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月6日 上午11:33:50
	 */
	public List<ResResourceType> getResourceListByPid(ResResourceType obj);
	
	/**
     * 
     * 删除分类
     *
     * @return：RetMsg
     *
     * @author：caizx
     *
     * @date：2018年4月20日 
     */
    public RetMsg delete(ResResourceType obj);
	}
