package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormWidgetRun;
import aljoin.act.dao.mapper.ActAljoinFormWidgetRunMapper;
import aljoin.act.iservice.ActAljoinFormWidgetRunService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 表单控件表(运行时)(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-09-27
 */
@Service
public class ActAljoinFormWidgetRunServiceImpl extends ServiceImpl<ActAljoinFormWidgetRunMapper, ActAljoinFormWidgetRun>
    implements ActAljoinFormWidgetRunService {

    @Resource
    private ActAljoinFormWidgetRunMapper mapper;

    @Override
    public Page<ActAljoinFormWidgetRun> list(PageBean pageBean, ActAljoinFormWidgetRun obj) throws Exception {
        Where<ActAljoinFormWidgetRun> where = new Where<ActAljoinFormWidgetRun>();
        where.orderBy("create_time", false);
        Page<ActAljoinFormWidgetRun> page =
            selectPage(new Page<ActAljoinFormWidgetRun>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFormWidgetRun obj) throws Exception {
        mapper.copyObject(obj);
    }
}
