package aljoin.aut.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutCard;
import aljoin.aut.dao.object.AutCardVO;
import aljoin.object.PageBean;

/**
 * 
 * 名片表(服务类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
public interface AutCardService extends IService<AutCard> {

  /**
   * 
   * 名片表(分页列表).
   *
   * @return：Page<ActCard>
   *
   * @author：laijy
   *
   * @date：2017-10-10
   */
	public Page<AutCardVO> list(PageBean pageBean, AutCard obj,String useId) throws Exception; 	
	
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
  public void copyObject(AutCard obj) throws Exception;
  
  /**
   * 
   * 个人中心-名片-详情
   *
   * @return：AutCardVO
   *
   * @author：laijy
   *
   * @date：2017年10月27日 下午4:17:27
   */
  public AutCardVO getById(AutCard obj) throws Exception;
}
