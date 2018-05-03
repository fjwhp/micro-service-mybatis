package aljoin.sys.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.PageBean;
import aljoin.sys.dao.entity.SysParameter;

import java.util.Map;

/**
 * 
 * 系统参数表(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-06-04
 */
public interface SysParameterService extends IService<SysParameter> {

  /**
   * 
   * 系统参数表(分页列表).
   *
   * @return：Page<SysParameter>
   *
   * @author：zhongjy
   *
   * @date：2017-06-04
   */
  public Page<SysParameter> list(PageBean pageBean, SysParameter obj) throws Exception;

  /**
   *
   * 系统参数表(根据key查询参数信息).
   *
   * @return：Page<SysParameter>
   *
   * @author：wangj
   *
   * @date：2017-09-01
   */
  public SysParameter selectBykey(String key) throws Exception;


  /**
   *
   * 获得允许上传文件的大小、文件类型
   *
   * @return：Map<String,String>
   *
   * @author：laijy
   *
   * @date：2017年9月21日 上午8:52:44
   */
  public Map<String, String> allowFileType() throws Exception;
}
