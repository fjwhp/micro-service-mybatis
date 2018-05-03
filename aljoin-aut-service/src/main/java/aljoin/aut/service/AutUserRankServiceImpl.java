package aljoin.aut.service;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.mapper.AutUserRankMapper;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

/**
 * 
 * 人员排序表(服务实现类).
 * 
 * @author：huanghz.
 * 
 * @date： 2017-12-13
 */
@Service
public class AutUserRankServiceImpl extends ServiceImpl<AutUserRankMapper, AutUserRank> implements AutUserRankService {

    @Resource
    private AutUserRankMapper mapper;

    @Override
    public Page<AutUserRank> list(PageBean pageBean, AutUserRank obj) throws Exception {
        Where<AutUserRank> where = new Where<AutUserRank>();
        where.orderBy("create_time", false);
        Page<AutUserRank> page =
            selectPage(new Page<AutUserRank>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutUserRank obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public RetMsg add(Long id) throws Exception {
        RetMsg retMsg = new RetMsg();

        Where<AutUserRank> where = new Where<AutUserRank>();
        where.setSqlSelect("user_rank");
        where.orderBy("user_rank", false);
        AutUserRank autUserRankMax = selectOne(where);
        BigDecimal userRankMax = autUserRankMax.getUserRank();
        userRankMax = userRankMax.add(new BigDecimal(1.00)); // 取整数存储；

        AutUserRank autUserRank = new AutUserRank();
        autUserRank.setId(id);
        autUserRank.setUserRank(new BigDecimal(255.00));
        mapper.insert(autUserRank);

        retMsg.setCode(0);
        retMsg.setMessage("人员排序新增成功。");
        return retMsg;
    }
}
