package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinActivityLog;
import aljoin.act.dao.mapper.ActAljoinActivityLogMapper;
import aljoin.act.iservice.ActAljoinActivityLogService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 流程操作日志表(服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-25
 */
@Service
public class ActAljoinActivityLogServiceImpl extends ServiceImpl<ActAljoinActivityLogMapper, ActAljoinActivityLog>
    implements ActAljoinActivityLogService {

    @Resource
    private ActAljoinActivityLogMapper mapper;

    @Override
    public Page<ActAljoinActivityLog> list(PageBean pageBean, ActAljoinActivityLog obj) throws Exception {
        Where<ActAljoinActivityLog> where = new Where<ActAljoinActivityLog>();
        where.orderBy("create_time", false);
        Page<ActAljoinActivityLog> page =
            selectPage(new Page<ActAljoinActivityLog>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinActivityLog obj) throws Exception {
        mapper.copyObject(obj);
    }
}
