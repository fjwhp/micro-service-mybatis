package aljoin.mai.service;

import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.mapper.MaiReceiveBoxSearchMapper;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * @描述：收件箱表(服务实现类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-30
 */
@Service
public class MaiReceiveBoxSearchServiceImpl extends ServiceImpl<MaiReceiveBoxSearchMapper, MaiReceiveBoxSearch> implements MaiReceiveBoxSearchService {

  @Resource
  private MaiReceiveBoxSearchMapper mapper;

  @Override
  public Page<MaiReceiveBoxSearch> list(PageBean pageBean, MaiReceiveBoxSearch obj) throws Exception {
	Where<MaiReceiveBoxSearch> where = new Where<MaiReceiveBoxSearch>();
	where.orderBy("create_time", false);
	Page<MaiReceiveBoxSearch> page = selectPage(new Page<MaiReceiveBoxSearch>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	
	
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  public void copyObject(MaiReceiveBoxSearch obj) throws Exception{
  	mapper.copyObject(obj);
  }
}
