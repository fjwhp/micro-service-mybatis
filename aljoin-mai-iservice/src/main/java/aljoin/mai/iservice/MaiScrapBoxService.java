package aljoin.mai.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.object.MaiScrapBoxVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 废件箱表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
public interface MaiScrapBoxService extends IService<MaiScrapBox> {

	/**
	 * 
	 * 废件箱表(分页列表).
	 *
	 * @return：Page<MaiScrapBox>
	 *
	 * @author：wangj
	 *
	 * @date：2017-09-20
	 */
	public Page<MaiScrapBox> list(PageBean pageBean, MaiScrapBox obj, Long userId,String time1,String time2,String orderByTime) throws Exception;
	
	/**
	 * 
	 * 恢复
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月22日 下午5:23:08
	 */
	public RetMsg recover(MaiScrapBoxVO obj) throws Exception;
}
