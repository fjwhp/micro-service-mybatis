package aljoin.ioa.iservice;

import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.ioa.dao.entity.IoaRegClosed;
import aljoin.ioa.dao.object.IoaRegClosedVO;
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
public interface IoaRegClosedService extends IService<IoaRegClosed> {

	
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
		public Page<IoaRegClosed> list(PageBean pageBean, IoaRegClosedVO obj, Long userId) throws Exception; 			
		
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
	  public void copyObject(IoaRegClosed obj) throws Exception;
	  /**
	   * 
	   * @描述：添加对象(需要完整的对象数据，包括所有的公共字段)
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public RetMsg addRegClosed(IoaRegClosed obj) throws Exception;
	  /**
	   * 
	   * @描述：导出对象(需要完整的对象数据，包括所有的公共字段)
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public  void export(HttpServletResponse response, IoaRegClosed obj) throws Exception;

	
	  
	

}
