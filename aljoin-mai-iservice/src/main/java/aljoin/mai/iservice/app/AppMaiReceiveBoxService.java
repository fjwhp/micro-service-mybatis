package aljoin.mai.iservice.app;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.object.AppMaiReceiveBoxVO;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 收件箱表(服务类).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-20
 */
public interface AppMaiReceiveBoxService extends IService<MaiReceiveBox> {

  /**
   * 
   * 收件箱表(分页列表).
   *
   * @return：Page<MaiDraftBox>
   *
   * @author：laijy
   *
   * @date：2017-09-20
   */
  public Page<MaiReceiveBoxSearch> list(PageBean pageBean, MaiReceiveBoxSearch obj, Long userId)
      throws Exception;

  /**
   * 
   *  收件箱 -详情
   *
   * @return：MaiDraftBoxVO
   *
   * @author：laijy
   *
   * @date：2017年9月27日 下午8:22:52
   */
  public AppMaiReceiveBoxVO  getById(MaiReceiveBox obj, String userId, String readType)  throws Exception;


  /**
   * 
   * @描述 收件箱 作废
   *
   * @return：RetMsg
   *
   * @author：huangw
   *
   * @date：2017年9月27日 下午8:22:52
   */
  public RetMsg delete(MaiReceiveBoxVO obj, String userid, String userName) throws Exception;


}
