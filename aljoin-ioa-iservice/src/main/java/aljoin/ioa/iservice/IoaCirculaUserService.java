package aljoin.ioa.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.ioa.dao.entity.IoaCirculaUser;
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
public interface IoaCirculaUserService extends IService<IoaCirculaUser> {

	
	/**
	   * 
	   * @描述：传阅意见(分页列表).
	   *
	   * @返回：Page<AutUserRank>
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
		public Page<IoaCirculaUser> list(PageBean pageBean, String porId) throws Exception; 	
		
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
	  public void copyObject(IoaCirculaUser obj) throws Exception;
  

	  /**
	   * 
	   * @描述：添加传阅明细
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public RetMsg add(IoaCirculaUser obj) throws Exception;
	  /**
	   * 
	   * @描述：判断是否显示传阅按钮
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public String isOver(IoaCirculaUser obj) throws Exception;
	  /**
	   * 
	   * @描述：添加传阅意见
	   *
	   * @返回：void
	   *
	   * @作者：zhongjy
	   *
	   * @时间：2017-12-13
	   */
	  public RetMsg  addCirculaOpinon(IoaCirculaUser obj) throws Exception;

}
