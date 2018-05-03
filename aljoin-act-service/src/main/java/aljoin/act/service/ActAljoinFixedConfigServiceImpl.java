package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFixedConfig;
import aljoin.act.dao.mapper.ActAljoinFixedConfigMapper;
import aljoin.act.iservice.ActAljoinFixedConfigService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 固定流程配置表(服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-11-07
 */
@Service
public class ActAljoinFixedConfigServiceImpl extends ServiceImpl<ActAljoinFixedConfigMapper, ActAljoinFixedConfig>
    implements ActAljoinFixedConfigService {

    @Resource
    private ActAljoinFixedConfigMapper mapper;

    @Override
    public Page<ActAljoinFixedConfig> list(PageBean pageBean, ActAljoinFixedConfig obj) throws Exception {
        Where<ActAljoinFixedConfig> where = new Where<ActAljoinFixedConfig>();
        where.orderBy("create_time", false);
        Page<ActAljoinFixedConfig> page =
            selectPage(new Page<ActAljoinFixedConfig>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinFixedConfig obj) throws Exception {
        mapper.copyObject(obj);
    }
}
