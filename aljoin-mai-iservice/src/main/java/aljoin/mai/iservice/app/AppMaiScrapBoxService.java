package aljoin.mai.iservice.app;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.object.MaiScrapValBoxVO;
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
public interface AppMaiScrapBoxService extends IService<MaiScrapBox> {

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
	public Page<MaiScrapBox> list(PageBean pageBean, MaiScrapBox obj, Long userId,String time1,String time2) throws Exception;
	
	/**
	 * 
	 * 作废
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月22日 下午5:23:08
	 */
	public RetMsg scrapAdd(MaiScrapBox obj) throws Exception;
	/**
	 * 
	 * 恢复单个
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月22日 下午5:23:08
	 */
	public  RetMsg recoverMai(String obj,AutAppUserLogin autAppUserLogin) throws Exception;
	/**
	 * 
	 * 获取对应的废件箱数据
	 *
	 * @return：RetMsg
	 *
	 * @author：laijy
	 *
	 * @date：2017年9月22日 下午5:23:08
	 */
	public MaiScrapValBoxVO getById(MaiScrapBox objs) throws Exception;
	
}
