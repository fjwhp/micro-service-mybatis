package aljoin.ioa.iservice;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;

import aljoin.ioa.dao.entity.IoaRegCategory;
import aljoin.object.RetMsg;

/**
 * 
 * 流程分类表(服务类).
 *
 * @author：zhongjy
 * 
 * @date：2017年7月25日 下午4:00:22
 */
public interface IoaRegCategoryService extends IService<IoaRegCategory> {

	 /**
    *
    * 收发文分类表
    *
    * @return：List<IoaRegCategory>
    *
    * @author：wangj
    *
    * @date：2017-09-05
    */
	
	public List<IoaRegCategory> getCateGoryList(String type);
	
	 /**
    *
    * 分类表(校验分类下的级别).
    *
    * @return：boolean
    *
    * @author：wangj
    *
    * @date：2017-09-05
    */
   public boolean validCategoryLevel(IoaRegCategory obj) throws Exception;
   
   /**
   *
   * 分类表(分类列表).
   *
   * @return：Page<IoaRegCategory>
   *
   * @author：
   *
   * @date：2017-08-31
   */
  public List<IoaRegCategory> selectCategoryList(IoaRegCategory obj) throws Exception;
  /**
  *
  * 分类表(校验是否已经存在).
  *
  * @return：boolean
  *
  * @author：
  *
  * @date：2017-09-05
  */
 public boolean validCategoryName(IoaRegCategory obj, boolean isAdd) throws Exception;
 /**
 *
 * 添加分类
 *
 * @return：boolean
 *
 * @author：
 *
 * @date：2017-09-05
 */
public RetMsg addIoaRegCategory(IoaRegCategory obj) throws Exception;
/**
 * 
 * 根据父ID获取器所有子分类
 *
 * @return：List<IoaRegCategory>
 *
 * @author：
 *
 * @date：2018年1月23日 下午3:37:24
 */
public List<IoaRegCategory> getAllChildCategoryList(Long parentId) throws Exception;


}
