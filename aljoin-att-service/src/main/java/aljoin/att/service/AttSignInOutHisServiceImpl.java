package aljoin.att.service;

import aljoin.att.dao.entity.AttSignInOutHis;
import aljoin.att.dao.mapper.AttSignInOutHisMapper;
import aljoin.att.iservice.AttSignInOutHisService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 签到、退表(历史表)(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-27
 */
@Service
public class AttSignInOutHisServiceImpl extends ServiceImpl<AttSignInOutHisMapper, AttSignInOutHis> implements AttSignInOutHisService {

	@Override
	public Page<AttSignInOutHis> list(PageBean pageBean, AttSignInOutHis obj) throws Exception {
		Where<AttSignInOutHis> where = new Where<AttSignInOutHis>();
		where.orderBy("create_time", false);
		Page<AttSignInOutHis> page = selectPage(new Page<AttSignInOutHis>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}	
}
