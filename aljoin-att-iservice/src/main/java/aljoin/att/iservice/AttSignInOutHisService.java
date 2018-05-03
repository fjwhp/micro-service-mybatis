package aljoin.att.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.att.dao.entity.AttSignInOutHis;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 签到、退表(历史表)(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-27
 */
public interface AttSignInOutHisService extends IService<AttSignInOutHis> {

	/**
	 * 
	 * 签到、退表(历史表)(分页列表).
	 *
	 * @return：Page<AttSignInOutHis>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-27
	 */
	public Page<AttSignInOutHis> list(PageBean pageBean, AttSignInOutHis obj) throws Exception;
	}
