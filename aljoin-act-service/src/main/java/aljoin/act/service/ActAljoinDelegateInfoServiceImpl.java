package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinDelegateInfo;
import aljoin.act.dao.mapper.ActAljoinDelegateInfoMapper;
import aljoin.act.iservice.ActAljoinDelegateInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 任务委托信息表（存放具体的委托任务数据）(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
@Service
public class ActAljoinDelegateInfoServiceImpl extends ServiceImpl<ActAljoinDelegateInfoMapper, ActAljoinDelegateInfo>
    implements ActAljoinDelegateInfoService {

    @Resource
    private ActAljoinDelegateInfoMapper mapper;

    @Override
    public Page<ActAljoinDelegateInfo> list(PageBean pageBean, ActAljoinDelegateInfo obj) throws Exception {
        Where<ActAljoinDelegateInfo> where = new Where<ActAljoinDelegateInfo>();
        where.orderBy("create_time", false);
        Page<ActAljoinDelegateInfo> page =
            selectPage(new Page<ActAljoinDelegateInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinDelegateInfo obj) throws Exception {
        mapper.copyObject(obj);
    }
}
