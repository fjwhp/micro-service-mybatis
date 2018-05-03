package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinTaskAssignee;
import aljoin.act.dao.mapper.ActAljoinTaskAssigneeMapper;
import aljoin.act.iservice.ActAljoinTaskAssigneeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 流程任务-授权表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2018-01-02
 */
@Service
public class ActAljoinTaskAssigneeServiceImpl extends ServiceImpl<ActAljoinTaskAssigneeMapper, ActAljoinTaskAssignee>
    implements ActAljoinTaskAssigneeService {

    @Resource
    private ActAljoinTaskAssigneeMapper mapper;

    @Override
    public Page<ActAljoinTaskAssignee> list(PageBean pageBean, ActAljoinTaskAssignee obj) throws Exception {
        Where<ActAljoinTaskAssignee> where = new Where<ActAljoinTaskAssignee>();
        where.orderBy("create_time", false);
        Page<ActAljoinTaskAssignee> page =
            selectPage(new Page<ActAljoinTaskAssignee>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinTaskAssignee obj) throws Exception {
        mapper.copyObject(obj);
    }
}
