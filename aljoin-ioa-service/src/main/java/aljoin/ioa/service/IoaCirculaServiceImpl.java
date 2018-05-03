package aljoin.ioa.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.activiti.engine.HistoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmn;
import aljoin.act.dao.entity.ActAljoinQueryHis;
import aljoin.act.iservice.ActAljoinBpmnService;
import aljoin.act.iservice.ActAljoinQueryHisService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaCircula;
import aljoin.ioa.dao.entity.IoaCirculaUser;
import aljoin.ioa.dao.mapper.IoaCirculaMapper;
import aljoin.ioa.dao.object.CirulaDO;
import aljoin.ioa.iservice.IoaCirculaService;
import aljoin.ioa.iservice.IoaCirculaUserService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 收文阅件表(服务实现类).
 * 
 * @author：zhongjy @date： 2017-11-08
 */
@Service
public class IoaCirculaServiceImpl extends ServiceImpl<IoaCirculaMapper, IoaCircula> implements IoaCirculaService {
	@Resource
	private IoaCirculaMapper mapper;
	@Resource
	private ActAljoinQueryHisService actAljoinQueryHisService;
	@Resource
	private ActAljoinBpmnService actAljoinBpmnService;
	@Resource
	private TaskService taskService;
	@Resource
	private HistoryService historyService;
	@Resource
	private IoaCirculaUserService ioaCirculaUserService;

	@Override
	public Page<CirulaDO> list(PageBean pageBean, CirulaDO obj) throws Exception {
		String startDate = null;
		String endDate = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		// 默认列表查询3个月数据
		if (obj.getStartDate() != null && obj.getCirculaDate() != null) {
			startDate = format.format(obj.getStartDate()) + " 00:00:00";
			endDate = format.format(obj.getCirculaDate()) + " 23:59:59";
		} else {
			startDate = getMonthDate(3, 2)+" 00:00:00";
			endDate = getMonthDate(3, 4) + " 23:59:59";
		}
		// 查询传阅表
		Where<IoaCircula> where = new Where<IoaCircula>();
		where.ge("create_time", startDate);
		where.lt("create_time", endDate);
		where.like("cir_ids", obj.getCirUserId() + ";");
		String circulaUser = obj.getFounder();// 传阅人名查询
		if (circulaUser != null && !"".equals(circulaUser)) {
			where.like("create_userfull_name", circulaUser);
		}
		if(obj.getIsCirculateTimeAsc() != null && StringUtils.isNotEmpty(obj.getIsCirculateTimeAsc())){
		    where.orderBy("id", true);
		}else{
		    where.orderBy("id", false);
		}
		List<IoaCircula> list = selectList(where);
		Page<CirulaDO> pageDo = new Page<CirulaDO>();
		if(list.size() == 0){
		    return pageDo;
		}
		Set<String> procSetId = new HashSet<String>();
		// id排序列表
		List<String> procIdList = new ArrayList<String>();
		HashMap<String, IoaCircula> cirMap = new HashMap<String, IoaCircula>();
        for (IoaCircula ioaCircula : list) {
            cirMap.put(ioaCircula.getProcessInstanceId(), ioaCircula);
            procSetId.add(ioaCircula.getProcessInstanceId());
            procIdList.add(ioaCircula.getProcessInstanceId());
        }
        // 阅读情况
        Where<IoaCirculaUser> userwhere = new Where<IoaCirculaUser>();
        userwhere.setSqlSelect("id,process_instance_id");
        userwhere.in("process_instance_id", procSetId);
        userwhere.eq("create_user_id", obj.getCirUserId());
        List<IoaCirculaUser> readList = ioaCirculaUserService.selectList(userwhere);
        if(obj.getReadStatus() != null){
            if(obj.getReadStatus().equals("1")){
                procSetId.clear();
                for (IoaCirculaUser ioaCirculaUser : readList) {
                    procSetId.add(ioaCirculaUser.getProcessInstanceId());
                }
            }else if(obj.getReadStatus().equals("0")){
                for (IoaCirculaUser ioaCirculaUser : readList) {
                    procSetId.remove(ioaCirculaUser.getProcessInstanceId());
                }
            }
        }
        if(procSetId.size() == 0){
            return pageDo;
        }
        Where<ActAljoinQueryHis> hiswhere = new Where<ActAljoinQueryHis>();
        hiswhere.in("process_instance_id", procSetId);
        String flowId = obj.getFlowName();// 流程ID
        String flowcateGroy = obj.getFlowCategory();// 流程分类ID
        // 流程 查询
        if (flowId != null && !"".equals(flowId)) {
            flowcateGroy = null;
            String flowName = "";
            ActAljoinBpmn aljoinBpmn = actAljoinBpmnService.selectById(new Long(flowId));
            if (aljoinBpmn != null) {
                flowName = aljoinBpmn.getProcessName();
                hiswhere.eq("process_name", flowName);
                hiswhere.where("process_category_ids like {0}", "%" + aljoinBpmn.getCategoryId().toString());
            }
        }
        // 流程分类ID查询
        if (flowcateGroy != null && !"".equals(flowcateGroy)) {
            hiswhere.like("process_category_ids", flowcateGroy);
        }
        // 标题查询
        String tmpTitle = obj.getTitle();
        if (tmpTitle != null && !"".equals(tmpTitle)) {
            hiswhere.like("process_title", tmpTitle);
        }
        // 创建人
        String tmpCreateUser = obj.getTaskFounder();
        if (tmpCreateUser != null && !"".equals(tmpCreateUser)) {
            hiswhere.like("create_userfull_name", tmpCreateUser);
        }
        // 文号查询
        String documentNumber = obj.getDocumentNumber();
        if(null != documentNumber && StringUtils.isNotEmpty(documentNumber)){
            hiswhere.like("reference_number", documentNumber);
        }
        
        if(obj.getIsdocumentNumberAsc() != null && obj.getIsdocumentNumberAsc().equals("0")){
            hiswhere.orderBy("reference_number",false);
        }else if(obj.getIsdocumentNumberAsc() != null && obj.getIsdocumentNumberAsc().equals("1")){
            hiswhere.orderBy("reference_number",true);
        }else if(obj.getIsCirculateTimeAsc() != null && obj.getIsCirculateTimeAsc().equals("0")){
            hiswhere.orderBy("id",false);
        }else{
            hiswhere.orderBy("id",true);
        }
        
        Page<ActAljoinQueryHis> hisPage = actAljoinQueryHisService
                .selectPage(new Page<ActAljoinQueryHis>(pageBean.getPageNum(), pageBean.getPageSize()), hiswhere);
        if (hisPage != null) {
            List<ActAljoinQueryHis> hisList = hisPage.getRecords();
            List<CirulaDO> CirulaDOList = new ArrayList<CirulaDO>();

            if (hisList != null && hisList.size() > 0) {
                // 获取所有未完成的流程实例所在任务环节
                List<Task> tasksList = taskService.createTaskQuery().processInstanceIdIn(new ArrayList<String>(procSetId))
                        .list();
                Map<String, Task> taskMap = new HashMap<String, Task>();
                if (tasksList != null && tasksList.size() > 0) {
                    for (Task task : tasksList) {
                        taskMap.put(task.getProcessInstanceId(), task);
                    }
                }
                // 所有完成任务的任务
                List<HistoricProcessInstance> historicProcessInstanceList = historyService
                        .createHistoricProcessInstanceQuery().processInstanceIds(procSetId)
                        .orderByProcessInstanceEndTime().asc().list();
                Map<String, HistoricProcessInstance> hisTaskMap = new HashMap<String, HistoricProcessInstance>();
                for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
                    hisTaskMap.put(historicProcessInstance.getId().toString(), historicProcessInstance);
                }
                for (ActAljoinQueryHis actAljoinQueryHis : hisList) {
                    CirulaDO cirulaDo = new CirulaDO();
                    cirulaDo.setFlowName(actAljoinQueryHis.getProcessName());// 流程名称
                    cirulaDo.setTitle(actAljoinQueryHis.getProcessTitle());// 流程标题
                    String tmpProId = actAljoinQueryHis.getProcessInstanceId();
                    cirulaDo.setProcessInstanceId(tmpProId);// 流程实例ID
                    cirulaDo.setTaskFounder(actAljoinQueryHis.getCreateFullUserName());// 任务创建人
                    cirulaDo.setCurrentAdmin(actAljoinQueryHis.getCurrentHandleFullUserName());
                    cirulaDo.setDocumentNumber(actAljoinQueryHis.getReferenceNumber());//文号
                    if (hisTaskMap.containsKey(tmpProId)) {
                        cirulaDo.setBusinessKey(hisTaskMap.get(tmpProId).getBusinessKey());
                    }

                    if (cirMap.containsKey(actAljoinQueryHis.getProcessInstanceId())) {
                        IoaCircula tmpCircula = cirMap.get(actAljoinQueryHis.getProcessInstanceId());
                        cirulaDo.setCirculaDate(tmpCircula.getCreateTime());// 传阅时间
                        cirulaDo.setFounder(tmpCircula.getCreateUserfullName());// 传阅人
                    }
                    for(IoaCirculaUser readUser : readList){
                        if(readUser.getProcessInstanceId().equals(cirulaDo.getProcessInstanceId())){
                            cirulaDo.setReadStatus("已阅");
                        }
                    }
                    if(StringUtils.isEmpty(cirulaDo.getReadStatus())){
                        cirulaDo.setReadStatus("未阅"); 
                    }
                    CirulaDOList.add(cirulaDo);
                }
            }
            List<CirulaDO> rankList = new ArrayList<CirulaDO>();
            if(CirulaDOList.size()>0 &&( obj.getIsdocumentNumberAsc() == null || StringUtils.isEmpty(obj.getIsdocumentNumberAsc()))){
                for(String id : procIdList){
                    for (CirulaDO cirulaDO : CirulaDOList) {
                        if(cirulaDO.getProcessInstanceId().equals(id)){
                            rankList.add(cirulaDO);
                        }
                    } 
                }
                pageDo.setRecords(rankList);
            }else{
                pageDo.setRecords(CirulaDOList);
            }
            pageDo.setSize(hisPage.getSize());
            pageDo.setTotal(hisPage.getTotal());
        }
    
		return pageDo;
	}

	/**
	 * 获取前N年、N月、N日时间
	 * 
	 * @param addNo
	 *            添加天数、月份。年
	 * @param type
	 *            1日，2月，3年
	 * @return
	 */
	public String getMonthDate(int addNo, int type) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		addNo = 0 - addNo;
		if (type == 1) {
			c.add(Calendar.DATE, addNo);
		}
		if (type == 2) {
			c.add(Calendar.MONTH, addNo);
		}
		if (type == 3) {
			c.add(Calendar.YEAR, addNo);
		}
		Date m = c.getTime();
		String mon = format.format(m);
		return mon;

	}

	public RetMsg openCirculaLog(String proId) throws Exception{
		RetMsg retMsg=new RetMsg();
		Where<IoaCircula>circulaWhere=new Where<IoaCircula>();
		circulaWhere.eq("process_instance_id", proId);
		List<IoaCircula> circulaList= this.selectList(circulaWhere);
		if(circulaList!=null && circulaList.size()>0){
			String readUserName="";//已阅名称
			String userName="";//未阅人员名称
			for (IoaCircula ioaCircula : circulaList) {
				userName+=ioaCircula.getCirNames();
			}
			Where<IoaCirculaUser>circulaUserWhere=new Where<IoaCirculaUser>();
			circulaUserWhere.eq("process_instance_id", proId);
			List<IoaCirculaUser> circulaUserList=ioaCirculaUserService.selectList(circulaUserWhere);
			if(circulaUserList!=null && circulaUserList.size()>0){
				for (IoaCirculaUser ioaCirculaUser : circulaUserList) {
					String repName=ioaCirculaUser.getCreateUserFullName();
					userName=userName.replaceAll(repName+";", "");
					readUserName+=repName+";";
				}
			}
			Map<String,String> userMap=new HashMap<String,String>();
			readUserName=readUserName.replaceAll(";",",");
			if(readUserName.length()>0){
				readUserName=readUserName.substring(0, readUserName.length()-1);
			}
			userName=userName.replaceAll(";",",");
			if(userName.length()>0){
				userName=userName.substring(0, userName.length()-1);
			}
			userMap.put("readUser", readUserName);
			userMap.put("noRead", userName);
			retMsg.setCode(0);
			retMsg.setObject(userMap);
			retMsg.setMessage("日志查询成功！");
		}else{
			retMsg.setCode(1);
			retMsg.setMessage("数据记录异常！查询数据为空");
		}
		return retMsg;		
	};

	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	public void copyObject(IoaCircula obj) throws Exception {
		mapper.copyObject(obj);
	}
}
