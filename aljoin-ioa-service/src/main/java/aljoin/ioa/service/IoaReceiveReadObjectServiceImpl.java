package aljoin.ioa.service;

import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import aljoin.ioa.dao.mapper.IoaReceiveReadObjectMapper;
import aljoin.ioa.iservice.IoaReceiveReadObjectService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 收文阅件-传阅对象表(分类)(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Service
public class IoaReceiveReadObjectServiceImpl extends ServiceImpl<IoaReceiveReadObjectMapper, IoaReceiveReadObject> implements IoaReceiveReadObjectService {

  @Resource
  private IoaReceiveReadObjectMapper mapper;

  @Override
  public Page<IoaReceiveReadObject> list(PageBean pageBean, IoaReceiveReadObject obj) throws Exception {
	Where<IoaReceiveReadObject> where = new Where<IoaReceiveReadObject>();
	where.orderBy("create_time", false);
	Page<IoaReceiveReadObject> page = selectPage(new Page<IoaReceiveReadObject>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  @Override
  public void copyObject(IoaReceiveReadObject obj) throws Exception{
  	mapper.copyObject(obj);
  }
}
