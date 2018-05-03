package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormDataRun;
import aljoin.act.dao.mapper.ActAljoinFormDataRunMapper;
import aljoin.act.iservice.ActAljoinFormDataRunService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 运行时表单数据表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
@Service
public class ActAljoinFormDataRunServiceImpl extends ServiceImpl<ActAljoinFormDataRunMapper, ActAljoinFormDataRun>
    implements ActAljoinFormDataRunService {

    @Resource
    private ActAljoinFormDataRunMapper mapper;

    @Override
    public Page<ActAljoinFormDataRun> list(PageBean pageBean, ActAljoinFormDataRun obj) throws Exception {
        Where<ActAljoinFormDataRun> where = new Where<ActAljoinFormDataRun>();
        where.orderBy("create_time", false);
        Page<ActAljoinFormDataRun> page =
            selectPage(new Page<ActAljoinFormDataRun>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFormDataRun obj) throws Exception {
        mapper.copyObject(obj);
    }
}
