package aljoin.ioa.iservice;

import aljoin.ioa.dao.object.IoaReceiveReadUserDO;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.ioa.dao.entity.IoaReceiveReadUser;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 收文阅件-用户评论(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
public interface IoaReceiveReadUserService extends IService<IoaReceiveReadUser> {

  /**
   * 
   * 收文阅件-用户评论(分页列表).
   *
   * @return：Page<IoaReceiveReadUser>
   *
   * @author：zhongjy
   *
   * @date：2017-11-08
   */
	public Page<IoaReceiveReadUserDO> list(PageBean pageBean, IoaReceiveReadUser obj) throws Exception;
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017-11-08
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017-11-08
   */
  public void copyObject(IoaReceiveReadUser obj) throws Exception;

  /**
   *
   * 传阅情况(已读未读人员)
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-11-13
   */
  public IoaReceiveReadUserDO circulation(String bizId) throws Exception;


}
