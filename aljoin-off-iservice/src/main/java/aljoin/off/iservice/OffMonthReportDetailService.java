package aljoin.off.iservice;

import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffMonthReport;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.off.dao.entity.OffMonthReportDetail;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 工作月报详情表(服务类).
 * 
 * @author：wangj
 * 
 * @date： 2017-10-11
 */
public interface OffMonthReportDetailService extends IService<OffMonthReportDetail> {

  /**
   * 
   * 工作月报详情表(分页列表).
   *
   * @return：Page<OffMonthReportDetail>
   *
   * @author：wangj
   *
   * @date：2017-10-11
   */
	public Page<OffMonthReportDetail> list(PageBean pageBean, OffMonthReportDetail obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-11
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：wangj
   *
   * @date：2017-10-11
   */
  public void copyObject(OffMonthReportDetail obj) throws Exception;

  /**
   *
   * 月报编辑
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-11
   */
  public RetMsg update(OffMonthReportDetail obj) throws Exception;

  /**
   *
   * 月报删除
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-11
   */
  public RetMsg delete(OffMonthReportDetail obj) throws Exception;

  /**
   *
   * 保存领导点评
   *
   * @return：RetMsg
   *
   * @author：wangj
   *
   * @date：2017-10-11
   */
  public RetMsg saveComment(OffMonthReport obj) throws Exception;
}
