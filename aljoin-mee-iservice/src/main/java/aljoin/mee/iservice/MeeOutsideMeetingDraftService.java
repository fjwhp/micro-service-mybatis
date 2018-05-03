package aljoin.mee.iservice;

import aljoin.mee.dao.entity.MeeOutsideMeeting;
import aljoin.mee.dao.object.MeeOutsideMeetingDraftDO;
import aljoin.mee.dao.object.MeeOutsideMeetingDraftVO;
import aljoin.mee.dao.object.MeeOutsideMeetingVO;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.mee.dao.entity.MeeOutsideMeetingDraft;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 外部会议草稿表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
public interface MeeOutsideMeetingDraftService extends IService<MeeOutsideMeetingDraft> {

  /**
   * 
   * 外部会议草稿表(分页列表).
   *
   * @return：Page<MeeOutsideMeetingDraft>
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
	public Page<MeeOutsideMeetingDraftDO> list(PageBean pageBean, MeeOutsideMeetingDraftVO obj) throws Exception;
	
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
  public void copyObject(MeeOutsideMeetingDraft obj) throws Exception;

  /**
   *
   * 外部会议新增草稿
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  public RetMsg add(MeeOutsideMeetingVO obj) throws Exception;

  /**
   *
   *  外部会议详情
   *
   * @return：  RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-13
   */
  public MeeOutsideMeetingVO detail(MeeOutsideMeeting obj) throws Exception;

  /**
   *
   * 外部会议编辑草稿
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  public RetMsg update(MeeOutsideMeetingVO obj) throws Exception;

  /**
   *
   * 外部会议删除草稿
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  public RetMsg delete(MeeOutsideMeetingVO obj) throws Exception;

  /**
   *
   * 外部会议草稿详情
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-12
   */
  public MeeOutsideMeetingVO detail(MeeOutsideMeetingVO obj) throws Exception;
}
