package aljoin.off.service;

import aljoin.off.dao.entity.OffSchedulingHis;
import aljoin.off.dao.mapper.OffSchedulingHisMapper;
import aljoin.off.iservice.OffSchedulingHisService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 日程安排表(历史表)(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-01
 */
@Service
public class OffSchedulingHisServiceImpl extends ServiceImpl<OffSchedulingHisMapper, OffSchedulingHis> implements OffSchedulingHisService {

  @Resource
  private OffSchedulingHisMapper mapper;

  @Override
  public Page<OffSchedulingHis> list(PageBean pageBean, OffSchedulingHis obj) throws Exception {
	Where<OffSchedulingHis> where = new Where<OffSchedulingHis>();
	where.orderBy("create_time", false);
	Page<OffSchedulingHis> page = selectPage(new Page<OffSchedulingHis>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(OffSchedulingHis obj) throws Exception{
  	mapper.copyObject(obj);
  }
}
