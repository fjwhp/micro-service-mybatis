package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormAttributeRun;
import aljoin.act.dao.mapper.ActAljoinFormAttributeRunMapper;
import aljoin.act.iservice.ActAljoinFormAttributeRunService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 表单控属性件表(运行时)(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
@Service
public class ActAljoinFormAttributeRunServiceImpl
    extends ServiceImpl<ActAljoinFormAttributeRunMapper, ActAljoinFormAttributeRun>
    implements ActAljoinFormAttributeRunService {

    @Resource
    private ActAljoinFormAttributeRunMapper mapper;

    @Override
    public Page<ActAljoinFormAttributeRun> list(PageBean pageBean, ActAljoinFormAttributeRun obj) throws Exception {
        Where<ActAljoinFormAttributeRun> where = new Where<ActAljoinFormAttributeRun>();
        where.orderBy("create_time", false);
        Page<ActAljoinFormAttributeRun> page =
            selectPage(new Page<ActAljoinFormAttributeRun>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFormAttributeRun obj) throws Exception {
        mapper.copyObject(obj);
    }
}
