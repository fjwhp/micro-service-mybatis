package aljoin.ioa.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUser;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.object.IoaReceiveFileDO;
import aljoin.ioa.dao.object.IoaReceiveFileVO;
import aljoin.object.FixedFormProcessLog;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.dao.entity.SysDataDict;

/**
 * 
 * 收文阅件表(服务类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
public interface IoaReceiveFileService extends IService<IoaReceiveFile> {

	/**
	 * 
	 * 收文阅件待阅文件(分页列表).
	 *
	 * @return：Page<IoaReceiveFile>
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	public Page<IoaReceiveFileDO> toReadList(PageBean pageBean, IoaReceiveFileVO obj, String userId) throws Exception;

	/**
	 *
	 * 收文阅件在阅文件(分页列表).
	 *
	 * @return：Page<IoaReceiveFile>
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-08
	 */
	public Page<IoaReceiveFileDO> inReadList(PageBean pageBean, IoaReceiveFileVO obj, String userId) throws Exception;

	/**
	 * 
	 * 根据ID删除对象(物理删除)
	 *
	 * 					@return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	public void physicsDeleteById(Long id) throws Exception;

	/**
	 * 
	 * 复制对象(需要完整的对象数据，包括所有的公共字段)
	 *
	 * 								@return：void
	 *
	 * @author：zhongjy
	 *
	 * @date：2017-11-08
	 */
	public void copyObject(IoaReceiveFile obj) throws Exception;

	/**
	 *
	 * 收文阅件编辑
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-13
	 */
	public RetMsg update(IoaReceiveFileVO obj) throws Exception;

	/**
	 *
	 * 收文阅件删除
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-13
	 */
	public RetMsg delete(IoaReceiveFile obj) throws Exception;

	/**
	 *
	 * 收文阅件详情
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-13
	 */
	public Map<String, Object> detail(IoaReceiveFile obj) throws Exception;

	/**
	 *
	 * 收文阅件提交
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-13
	 */
	public RetMsg directorAudit(IoaReceiveFileVO obj) throws Exception;

	/**
	 *
	 * @描述:加载数据字典值
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-14
	 */
	public Map<String, Object> loadDictByCode() throws Exception;

	/**
	 *
	 * @描述:根据流程实例ID 获取节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	public RetMsg checkNextTaskInfo(String processInstanceId, String receiveUserIds, String isCheckAllUser)
	      throws Exception;

	/**
	 *
	 * @描述:根据流程实例ID 获取下个节点信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	public RetMsg getNextTaskInfo(String processInstanceId, boolean withCondition) throws Exception;

	/**
	 *
	 * @描述:根据流程实例ID 完成任务
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
//	public RetMsg completeTask(Map<String, Object> variables, String processInstanceId, String receiveFileId,
//			String readUserIds, String message, String currentUserId, String isEnd, String isOffice) throws Exception;

	/**
	 *
	 * @描述:根据流程实例ID 检查任务是否被签收
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	public RetMsg checkIsClaim(String processInstanceId) throws Exception;

	/**
	 *
	 * @描述:根据流程实例ID 回退到上一节点
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
//	public RetMsg jump2Task2(String processInstanceId, String bizId, String message, Long createUserId)
//			throws Exception;

	/**
	 *
	 * 获取上一个节点的信息
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-15
	 */
	public RetMsg getPreTaskInfo(String processInstanceId) throws Exception;

	/**
	 *
	 * 池主任审批详情
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-13
	 */
	public IoaReceiveFileDO directorDetail(IoaReceiveFile obj, AutUser user) throws Exception;

	/**
	 *
	 * 流转日志
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-16
	 */
	public List<FixedFormProcessLog> getTaskLogInfo(String processInstanceId) throws Exception;

	/**
	 *
	 * 详情并判断是否已经签收
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-18
	 */
	public IoaReceiveFileDO readDetail(IoaReceiveFile obj) throws Exception;

	/**
	 *
	 * 根据数据字典 code 获得人员列表
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-20
	 */
	public List<SysDataDict> memberList(String dictCode) throws Exception;

	/**
	 *
	 * 流程作废
	 *
	 * @return：RetMsg
	 *
	 * @author：wangj
	 *
	 * @date：2017-11-20
	 */
	public RetMsg toVoid(String processInstanceId, String bizId) throws Exception;

	
	/**
	 * 
	 * @描述: 撤回
	 *    
	 * @return： RetMsg  
	 * 
	 * @author：  sunlinan  
	 *
	 * @date 2017年12月13日
	 */
	public RetMsg revoke2Task(String processInstanceId, String bizId, AutUser user) throws Exception;

	/**
	 * 
	 * @描述: 综合查询详情
	 *    
	 * @return：  IoaReceiveFileDO  
	 * 
	 * @author：  huangw  
	 *
	 * @date 2017年12月15日
	 */
	public IoaReceiveFileDO readHisDetail(IoaReceiveFile obj) throws Exception;
	/**
	 *
	 * APP流转日志
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-21
	 */
	public List<FixedFormProcessLog> getAppTaskLogInfo(String processInstanceId) throws Exception;
	/**
	 *
	 * 返回流程名称
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-27
	 */
	public String  getTaskName(String processInstanceId) throws Exception;
	/**
	 *
	 * APP收文阅件详情
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-27
	 */
	public  Map<String, Object> appDetail(IoaReceiveFile obj) throws Exception;

	/**
	 *
	 * APP池主任保存或者提交
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-27
	 */
	public  RetMsg appDirectorAudit(IoaReceiveFileVO obj,Long userID) throws Exception;

	/**
	 *
	 * 提交
	 *
	 * @return：RetMsg
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-27
	 */
//	public RetMsg AppCompleteTask(Map<String, Object> variables, String processInstanceId, String receiveFileId,
//			String readUserIds, String message, String currentUserId, String isEnd, Long userid, String username,String isOffice) throws Exception;

	/**
	 *
	 * APP详情
	 *
	 * @return：IoaReceiveFileDO
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-27
	 */
	public IoaReceiveFileDO directorDetails(IoaReceiveFile obj, AutUser user) throws Exception;

	/**
	 *
	 * APP详情
	 *
	 * @return：IoaReceiveFileDO
	 *
	 * @author：huangw
	 *
	 * @date：2017-12-27
	 */
	public  IoaReceiveFileDO readDetails(IoaReceiveFile obj) throws Exception;

 

  

	

}
