package aljoin.mee.iservice;

import aljoin.mee.dao.entity.MeeInsideMeeting;
import aljoin.mee.dao.entity.MeeInsideMeetingDraft;
import aljoin.mee.dao.object.MeeInsideMeetingDraftDO;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 内部会议草稿表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
public interface MeeInsideMeetingDraftService extends IService<MeeInsideMeetingDraft> {

  /**
   * 
   * 内部会议草稿表(分页列表).
   *
   * @return：Page<MeeInsideMeetingDraft>
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
	public Page<MeeInsideMeetingDraftDO> list(PageBean pageBean, MeeInsideMeetingVO obj) throws Exception;
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  public void copyObject(MeeInsideMeetingDraft obj) throws Exception;

  /**
   *
   *  内部会议新增
   *
   * @return：  RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-13
   */
  public RetMsg add(MeeInsideMeetingVO obj) throws Exception;

  /**
   *
   *  内部会议详情
   *
   * @return：  RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-13
   */
  public MeeInsideMeetingVO detail(MeeInsideMeeting obj) throws Exception;

  /**
   *
   *  内部会议编辑
   *
   * @return：  RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-13
   */
  public RetMsg update(MeeInsideMeetingVO obj) throws Exception;

  /**
   *
   *  内部会议删除
   *
   * @return：  RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-13
   */
  public RetMsg delete(MeeInsideMeetingVO obj) throws Exception;
}
