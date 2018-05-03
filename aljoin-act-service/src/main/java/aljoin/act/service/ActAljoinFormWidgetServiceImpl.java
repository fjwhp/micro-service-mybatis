package aljoin.act.service;

import aljoin.act.dao.entity.ActAljoinFormWidget;
import aljoin.act.dao.mapper.ActAljoinFormWidgetMapper;
import aljoin.act.iservice.ActAljoinFormWidgetService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 表单控件表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-08-31
 */
@Service
public class ActAljoinFormWidgetServiceImpl extends ServiceImpl<ActAljoinFormWidgetMapper, ActAljoinFormWidget>
    implements ActAljoinFormWidgetService {

    @Override
    public Page<ActAljoinFormWidget> list(PageBean pageBean, ActAljoinFormWidget obj) throws Exception {
        Where<ActAljoinFormWidget> where = new Where<ActAljoinFormWidget>();
        if (StringUtils.isNotEmpty(obj.getWidgetName())) {
            where.like("widget_name", obj.getWidgetName());
        }
        where.orderBy("create_time", false);
        Page<ActAljoinFormWidget> page =
            selectPage(new Page<ActAljoinFormWidget>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }
}
