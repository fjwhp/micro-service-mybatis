package aljoin.sys.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.object.PageBean;
import aljoin.sys.dao.entity.SysDataDict;

import java.util.List;

/**
 * 
 * 数据字典表(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-05-27
 */
public interface SysDataDictService extends IService<SysDataDict> {

  /**
   * 
   * 数据字典表(分页列表).
   *
   * @return：Page<SysDataDict>
   *
   * @author：zhongjy
   *
   * @date：2017-05-27
   */
  public Page<SysDataDict> list(PageBean pageBean, SysDataDict obj) throws Exception;

  /**
   * 
   * 修改数据字典
   *
   * @return：void
   *
   * @author：zhongjy
   *
   * @date：2017年5月30日 下午9:27:34
   */
  public void update(SysDataDict obj, String[] dictKey1, String[] dictValue1, String[] isActive1,
      String[] id1,Integer[] dictRank) throws Exception;

  /**
   *
   * 根据字典code获取字典列表信息
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017年10月13日
   */
  public List<SysDataDict> getByCode(String code) throws Exception;
}
