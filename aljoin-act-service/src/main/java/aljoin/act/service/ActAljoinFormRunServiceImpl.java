package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormRun;
import aljoin.act.dao.mapper.ActAljoinFormRunMapper;
import aljoin.act.iservice.ActAljoinFormRunService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 表单表(运行时)(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
@Service
public class ActAljoinFormRunServiceImpl extends ServiceImpl<ActAljoinFormRunMapper, ActAljoinFormRun>
    implements ActAljoinFormRunService {

    @Resource
    private ActAljoinFormRunMapper mapper;

    @Override
    public Page<ActAljoinFormRun> list(PageBean pageBean, ActAljoinFormRun obj) throws Exception {
        Where<ActAljoinFormRun> where = new Where<ActAljoinFormRun>();
        where.orderBy("create_time", false);
        Page<ActAljoinFormRun> page =
            selectPage(new Page<ActAljoinFormRun>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFormRun obj) throws Exception {
        mapper.copyObject(obj);
    }
}
