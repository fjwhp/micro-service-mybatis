package aljoin.ass.iservice;

import java.text.ParseException;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.ass.dao.entity.AssProcess;
import aljoin.ass.dao.object.AllTaskShowVO;
import aljoin.ass.dao.object.AssProcessVO;

import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 固定财产流程表(服务类).
 * 
 * @author：xuc
 * 
 * @date： 2018-01-12
 */
public interface AssProcessService extends IService<AssProcess> {

  /**
   * 
   * 固定财产流程表(分页列表).
   *
   * @return：Page<AssProcess>
   *
   * @author：xuc
   *
   * @date：2018-01-12
   */
	public Page<AssProcess> list(PageBean pageBean, AssProcess obj) throws Exception; 	
	
  /**
   * 
   * 根据ID删除对象(物理删除)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-12
   */
  public void physicsDeleteById(Long id) throws Exception;

  /**
   * 
   * 复制对象(需要完整的对象数据，包括所有的公共字段)
   *
   * @return：void
   *
   * @author：xuc
   *
   * @date：2018-01-12
   */
  public void copyObject(AssProcess obj) throws Exception;

  /**
   * 
   * @throws Exception 
   * 固定资产流程作废
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月24日 上午10:23:55
   */
  public RetMsg toVoid(String taskId, String bizId, Long userId) throws Exception;

  /**
   * 
   * 获取流程id
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月25日 上午9:40:20
   */
  public RetMsg getBpmnId(AssProcessVO obj);

  /**
   * 
   * 固定资产综合查询分页
   *
   * @return：Page<ActAljoinQueryHis>
   *
   * @author：xuc
   *
   * @date：2018年1月25日 上午9:40:34
   */
  public Page<ActAljoinQueryHis> getAllTask(PageBean pageBean, AllTaskShowVO obj,
      Map<String, String> map) throws ParseException, Exception;

  /**
   * 
   * 获取businessKey
   *
   * @return：RetMsg
   *
   * @author：xuc
   *
   * @date：2018年1月25日 上午9:40:53
   */
  public RetMsg getBusinessKey(String processInstanceId);
}
