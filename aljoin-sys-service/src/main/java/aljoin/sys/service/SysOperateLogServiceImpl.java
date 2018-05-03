package aljoin.sys.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.sys.dao.entity.SysOperateLog;
import aljoin.sys.dao.mapper.SysOperateLogMapper;
import aljoin.sys.iservice.SysOperateLogService;

/**
 * 
 * 操作日志表(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-06-05
 */
@Service
public class SysOperateLogServiceImpl extends ServiceImpl<SysOperateLogMapper, SysOperateLog>
    implements SysOperateLogService {

	@Override
  public Page<SysOperateLog> list(PageBean pageBean, SysOperateLog obj) throws Exception {
    Where<SysOperateLog> where = new Where<SysOperateLog>();
    if (StringUtils.isNotEmpty(obj.getOperateUserName())) {
      where.like("operate_user_name", obj.getOperateUserName());
      where.or("operate_user_ip LIKE {0}", "%" + obj.getOperateUserName() + "%");
      where.or("operate_log_name LIKE {0}", "%" + obj.getOperateUserName() + "%");
    }

    where.orderBy("create_time", false);
    Page<SysOperateLog> page =
        selectPage(new Page<SysOperateLog>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    return page;
  }
}
