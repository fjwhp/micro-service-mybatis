package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinBpmnRun;
import aljoin.act.dao.mapper.ActAljoinBpmnRunMapper;
import aljoin.act.iservice.ActAljoinBpmnRunService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * @描述：(服务实现类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2018-03-13
 */
@Service
public class ActAljoinBpmnRunServiceImpl extends ServiceImpl<ActAljoinBpmnRunMapper, ActAljoinBpmnRun> implements ActAljoinBpmnRunService {

  @Resource
  private ActAljoinBpmnRunMapper mapper;

  @Override
  public Page<ActAljoinBpmnRun> list(PageBean pageBean, ActAljoinBpmnRun obj) throws Exception {
	Where<ActAljoinBpmnRun> where = new Where<ActAljoinBpmnRun>();
	where.orderBy("create_time", false);
	Page<ActAljoinBpmnRun> page = selectPage(new Page<ActAljoinBpmnRun>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	
	
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  public void copyObject(ActAljoinBpmnRun obj) throws Exception{
  	mapper.copyObject(obj);
  }
}
