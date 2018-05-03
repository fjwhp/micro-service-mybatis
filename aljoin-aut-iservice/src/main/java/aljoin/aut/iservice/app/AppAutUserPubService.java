package aljoin.aut.iservice.app;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 用户公共信息表(服务类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
public interface AppAutUserPubService extends IService<AutUserPub> {

  /**
   * 
   * 用户公共信息表(分页列表).
   *
   * @return：Page<AutUserPub>
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
	public Page<AutUserPub> list(PageBean pageBean, AutUserPubVO obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
  public void copyObject(AutUserPub obj) throws Exception;
  
  /**
   * 
   * @描述:用户修改密码
   *
   * @return：RetMsg
   *
   * @author：laijy
   *
   * @date：2017年10月10日 下午4:36:21
   */
  public RetMsg updatePwd(AutUserPubVO obj,AutAppUserLogin autAppUserLogin) throws Exception;
  
  /**
   * 
   * 个人中心-通讯录
   *
   * @return：List<AutUserPubVO>
   *
   * @author：laijy
   *
   * @date：2017年10月13日 上午9:54:36
   */
  public List<AutUserPubVO> getAutUserPubVOList(AutDepartment autDepartment) throws Exception;
  
  /**
   * 
   * 个人中心-个人信息-新增
   *
   * @return：RetMsg
   *
   * @author：laijy
   *
   * @date：2017年10月18日 上午9:48:47
   */
  public RetMsg add(AutUserPubVO obj) throws Exception;
  
  /**
   * 
   * 个人中心-个人信息-修改
   *
   * @return：RetMsg
   *
   * @author：laijy
   *
   * @date：2017年10月18日 上午11:11:09
   */
  public RetMsg update(AutUserPubVO obj) throws Exception;
}
