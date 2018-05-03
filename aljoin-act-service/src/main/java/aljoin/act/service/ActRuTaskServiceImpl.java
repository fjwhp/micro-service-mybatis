package aljoin.act.service;

import aljoin.act.dao.entity.ActRuTask;
import aljoin.act.dao.mapper.ActRuTaskMapper;
import aljoin.act.iservice.ActRuTaskService;
import aljoin.object.PageBean;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * (服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
@Service
public class ActRuTaskServiceImpl extends ServiceImpl<ActRuTaskMapper, ActRuTask> implements ActRuTaskService {

    @Resource
    private ActRuTaskMapper mapper;

    @Override
    public Page<ActRuTask> list(PageBean pageBean, ActRuTask obj) throws Exception {
        Page<ActRuTask> page = selectWaitPage(new Page<ActRuTask>(pageBean.getPageNum(), pageBean.getPageSize()), obj);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActRuTask obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public Page<ActRuTask> selectWaitPage(Page<ActRuTask> page, ActRuTask wrapper) throws Exception {
        page.setRecords(this.mapper.selectWaitPage(page, wrapper));
        return page;
    }
    @Override
    public Page<ActRuTask> selectWaitingPage(Page<ActRuTask> page, ActRuTask wrapper) throws Exception {
    	page.setRecords(this.mapper.selectWaitingPage(page, wrapper));    
        return page;
    }

	@Override
	public Page<ActRuTask> waitingList(PageBean pageBean, ActRuTask obj) throws Exception {
		 Page<ActRuTask> page = selectWaitingPage(new Page<ActRuTask>(pageBean.getPageNum(), pageBean.getPageSize()), obj);
	       return page;
	}
}
