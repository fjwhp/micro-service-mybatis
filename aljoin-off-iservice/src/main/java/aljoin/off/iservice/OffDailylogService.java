package aljoin.off.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffDailylog;
import aljoin.off.dao.object.OffDailylogDO;
import aljoin.off.dao.object.OffDailylogVO;

/**
 * 
 * 工作日志(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-14
 */
public interface OffDailylogService extends IService<OffDailylog> {

	/**
	 * 
	 * 工作日志(分页列表).
	 *
	 * @return：Page<OffDailylog>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	public Page<OffDailylog> list(PageBean pageBean, OffDailylog obj) throws Exception;

	/**
	 *
	 * 工作日志(分页列表).
	 *
	 * @return：Page<OffDailylogDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-14
	 */
	public Page<OffDailylogDO> list(PageBean pageBean, OffDailylogVO obj) throws Exception;

	/**
	 *
	 * 工作日志新增.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public RetMsg add(OffDailylogVO obj) throws Exception;

	/**
	 *
	 * 工作日志编辑.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public RetMsg update(OffDailylogVO obj) throws Exception;

	/**
	 *
	 * 工作日志删除.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public RetMsg delete(OffDailylog obj) throws Exception;

	/**
	 *
	 * 工作日志详情.
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-10-11
	 */
	public OffDailylogVO detail(OffDailylog obj) throws Exception;
	
	/**
	 *
	 * 工作日志批量删除.
	 *
	 * @return：RetMsg
	 *
	 * @author：sun
	 *
	 * @date：2017-10-26
	 */
	public RetMsg deleteByIds(String ids) throws Exception;

}
