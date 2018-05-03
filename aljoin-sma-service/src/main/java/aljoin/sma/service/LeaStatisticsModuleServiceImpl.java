package aljoin.sma.service; 

import aljoin.sma.dao.entity.LeaStatisticsModule;
import aljoin.sma.dao.mapper.LeaStatisticsModuleMapper;
import aljoin.sma.iservice.LeaStatisticsModuleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean; 

/**
 * 
 * 领导看板统计模块显示排序(服务实现类).
 * 
 * @author：huangw
 * 
 * @date： 2017-12-25
 */
@Service
public class LeaStatisticsModuleServiceImpl extends ServiceImpl<LeaStatisticsModuleMapper, LeaStatisticsModule> implements LeaStatisticsModuleService {

  @Resource
  private LeaStatisticsModuleMapper mapper;

  @Override
  public Page<LeaStatisticsModule> list(PageBean pageBean, LeaStatisticsModule obj) throws Exception {
	Where<LeaStatisticsModule> where = new Where<LeaStatisticsModule>();
	where.orderBy("create_time", false);
	Page<LeaStatisticsModule> page = selectPage(new Page<LeaStatisticsModule>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

	@Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


	@Override
  public void copyObject(LeaStatisticsModule obj) throws Exception{
  	mapper.copyObject(obj);
  }
}
