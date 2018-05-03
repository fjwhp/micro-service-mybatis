package aljoin.mee.iservice;

import aljoin.mee.dao.entity.MeeMeetingRoom;
import aljoin.mee.dao.object.MeeInsideMeetingVO;
import aljoin.mee.dao.object.MeeMeetingRoomCountDO;
import aljoin.mee.dao.object.MeeMeetingRoomDO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * 
 * 会议室表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-12
 */
public interface MeeMeetingRoomService extends IService<MeeMeetingRoom> {

	/**
	 * 
	 * 会议室表(分页列表).
	 *
	 * @return：Page<MeeMeetingRoom>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public Page<MeeMeetingRoomDO> list(PageBean pageBean, MeeMeetingRoom obj) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	 *
	 * 					@return：void
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
	 * 								@return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public void copyObject(MeeMeetingRoom obj) throws Exception;

	/**
	 *
	 * 会议室本周或者本月列表.
	 *
	 * 					@return：Page<MeeMeetingRoom>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public MeeMeetingRoomCountDO countlist(MeeInsideMeetingVO obj) throws Exception;

	/**
	 *
	 * 新增会议室
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public RetMsg add(MeeMeetingRoom obj) throws Exception;

	/**
	 *
	 * 会议室列表(下拉加载数据).
	 *
	 * @return：List<MeeMeetingRoom>
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-12
	 */
	public List<MeeMeetingRoom> roomList(MeeMeetingRoom obj) throws Exception;

	/**
	 *
	 * 编辑会议室
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-27
	 */
	public RetMsg update(MeeMeetingRoom obj) throws Exception;

	/**
	 *
	 * 负责人会议室列表
	 *
	 * @return：void
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-27
	 */
	public List<MeeMeetingRoom> chargeList(MeeMeetingRoom obj) throws Exception;

	/**
	 * 
	 * 会议室详情
	 *
	 * @return：MeeMeetingRoom
	 *
	 * @author：xuc
	 *
	 * @date：2017年12月20日 上午11:00:03
	 */
    public MeeMeetingRoom detail(Long id) throws Exception;
}
