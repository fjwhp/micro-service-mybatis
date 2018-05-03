package aljoin.aut.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutCardCategory;
import aljoin.aut.dao.mapper.AutCardCategoryMapper;
import aljoin.aut.iservice.AutCardCategoryService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 名片分类表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Service
public class AutCardCategoryServiceImpl extends ServiceImpl<AutCardCategoryMapper, AutCardCategory>
    implements AutCardCategoryService {

    private final static Logger logger = LoggerFactory.getLogger(AutCardCategoryServiceImpl.class);

    @Resource
    private AutCardCategoryMapper mapper;

    @Override
    public Page<AutCardCategory> list(PageBean pageBean, AutCardCategory obj, Long customUserId) throws Exception {

        Where<AutCardCategory> where = new Where<AutCardCategory>();
        // 只能查看到自己的名片分类
        where.eq("user_id", customUserId);
        where.orderBy("category_rank", true);
        if (StringUtils.checkValNotNull(obj.getCategoryName())) {
            where.like("category_name", obj.getCategoryName());
        }
        Page<AutCardCategory> page
            = selectPage(new Page<AutCardCategory>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutCardCategory obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public RetMsg add(AutCardCategory obj, Long customUserId) throws Exception {

        RetMsg retMsg = new RetMsg();

        obj.setUserId(customUserId);
        obj.setIsActive(1);

        try {
            insert(obj);
            retMsg.setCode(0);
            retMsg.setMessage("新增成功");
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(1);
            retMsg.setMessage("新增失败");
        }

        return retMsg;
    }

    @Override
    public List<AutCardCategory> getCardCategoryList(Long customUserId) throws Exception {

        Where<AutCardCategory> where = new Where<AutCardCategory>();
        where.eq("user_id", customUserId);
        where.orderBy("category_rank", true);
        List<AutCardCategory> cardCategoryList = selectList(where);
        return cardCategoryList;
    }
}
