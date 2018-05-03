package aljoin.ioa.iservice;

import aljoin.ioa.dao.object.AppDoTaskVO;
import aljoin.ioa.dao.object.DoTaskShowVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;

/**
 * 
 * 在办工作(服务类).
 * 
 * @author：pengsp
 * 
 * @date： 2017-10-19
 */
public interface IoaDoingWorkService {
	/**
	 *
	 * 在办工作流程(分页列表).
	 *
	 * @return：Page<DoTaskShowVO>
	 *
	 * @author：pengsp
	 *
	 * @date：2017-10-19
	 */
	public Page<DoTaskShowVO> list(PageBean pageBean,String userId,DoTaskShowVO obj) throws Exception;

	/**
	 *
	 * App在办工作流程(分页列表).
	 *
	 * @return：Page<AppDoTaskDO>
	 *
	 * @author：wangj
	 *
	 * @date：2017-12-19
	 */
	public RetMsg doingList(PageBean pageBean, String userId, AppDoTaskVO obj) throws Exception;
	
}
