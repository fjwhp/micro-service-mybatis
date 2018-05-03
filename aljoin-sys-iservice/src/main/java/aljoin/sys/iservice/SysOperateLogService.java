package aljoin.sys.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.PageBean;
import aljoin.sys.dao.entity.SysOperateLog;

/**
 * 
 * 操作日志表(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-06-05
 */
public interface SysOperateLogService extends IService<SysOperateLog> {

  /**
   * 
   * 操作日志表(分页列表).
   *
   * @return：Page<SysOperateLog>
   *
   * @author：zhongjy
   *
   * @date：2017-06-05
   */
  public Page<SysOperateLog> list(PageBean pageBean, SysOperateLog obj) throws Exception;
}
