package aljoin.ioa.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.ioa.dao.entity.IoaCircula;
import aljoin.ioa.dao.object.CirulaDO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 收文阅件表(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
public interface IoaCirculaService extends IService<IoaCircula> {

	
	/**
	   * 
	   * @描述：人员排序表(分页列表).
	   *
	   * @返回：Page<AutUserRank>
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
		public Page<CirulaDO> list(PageBean pageBean, CirulaDO obj) throws Exception; 			
		
	  /**
	   * 
	   * @描述：根据ID删除对象(物理删除)
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public void physicsDeleteById(Long id) throws Exception;

	  /**
	   * 
	   * @描述：复制对象(需要完整的对象数据，包括所有的公共字段)
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public void copyObject(IoaCircula obj) throws Exception;
	  /**
		 * 
		 * @描述：已阅未阅
		 *
		 * @返回：void
		 *
		 * @作者：zhongjy
		 *
		 * @时间：2017-12-13
		 */
	  public RetMsg openCirculaLog(String proId) throws Exception;
	

}
