package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormAttribute;
import aljoin.act.dao.mapper.ActAljoinFormAttributeMapper;
import aljoin.act.iservice.ActAljoinFormAttributeService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 表单控属性件表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
@Service
public class ActAljoinFormAttributeServiceImpl extends ServiceImpl<ActAljoinFormAttributeMapper, ActAljoinFormAttribute>
    implements ActAljoinFormAttributeService {

    @Override
    public Page<ActAljoinFormAttribute> list(PageBean pageBean, ActAljoinFormAttribute obj) throws Exception {
        Where<ActAljoinFormAttribute> where = new Where<ActAljoinFormAttribute>();
        if (StringUtils.isNotEmpty(obj.getAttrName())) {
            where.like("attr_name", obj.getAttrName());
        }
        where.orderBy("create_time", false);
        Page<ActAljoinFormAttribute> page =
            selectPage(new Page<ActAljoinFormAttribute>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }
}
