package aljoin.off.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.iservice.AutDepartmentService;
import aljoin.aut.iservice.AutDepartmentUserService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.off.dao.entity.OffDailylog;
import aljoin.off.dao.entity.OffMonthReport;
import aljoin.off.dao.entity.OffMonthReportDetail;
import aljoin.off.dao.mapper.OffDailylogMapper;
import aljoin.off.dao.object.OffDailylogDO;
import aljoin.off.dao.object.OffDailylogVO;
import aljoin.off.iservice.OffDailylogService;
import aljoin.off.iservice.OffMonthReportDetailService;
import aljoin.off.iservice.OffMonthReportService;
import aljoin.res.dao.entity.ResResource;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 工作日志(服务实现类) .
 * 
 * @author：wangj
 * 
 * @date： 2017-09-14
 */
@Service
public class OffDailylogServiceImpl extends ServiceImpl<OffDailylogMapper, OffDailylog> implements OffDailylogService {
	@Resource
	private OffMonthReportService offMonthReportService;

	@Resource
	private AutUserService autUserService;

	@Resource
	private AutDepartmentUserService autDepartmentUserService;

	@Resource
	private AutDepartmentService autDepartmentService;

	@Resource
	private ResResourceService resResourceService;

	@Resource
	private OffMonthReportDetailService offMonthReportDetailService;
	
	@Resource
	private SysParameterService sysParameterService;

	@Override
	public Page<OffDailylog> list(PageBean pageBean, OffDailylog obj) throws Exception {
		Where<OffDailylog> where = new Where<OffDailylog>();
		where.orderBy("create_time", false);
		Page<OffDailylog> page = selectPage(new Page<OffDailylog>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public Page<OffDailylogDO> list(PageBean pageBean, OffDailylogVO obj) throws Exception {
		Where<OffDailylog> where = new Where<OffDailylog>();
		where.setSqlSelect("create_time,create_user_id,id,title,work_date");
		if(null != obj){
			if(StringUtils.isNotEmpty(obj.getWorkBegDate()) && StringUtils.isEmpty(obj.getWorkEndDate())){
				where.ge("work_date",DateUtil.str2dateOrTime(obj.getWorkBegDate()));
			}
			if(StringUtils.isEmpty(obj.getWorkBegDate()) && StringUtils.isNotEmpty(obj.getWorkEndDate())){
				where.le("work_date",DateUtil.str2dateOrTime(obj.getWorkEndDate()));
			}
			if((StringUtils.isNotEmpty(obj.getWorkBegDate()) && StringUtils.isNotEmpty(obj.getWorkEndDate()))){
				where.between("work_date",DateUtil.str2dateOrTime(obj.getWorkBegDate()),DateUtil.str2dateOrTime(obj.getWorkEndDate()));
			}

			if(StringUtils.isNotEmpty(obj.getTitle())){
				where.like("title",obj.getTitle());
			}
			where.eq("create_user_id",obj.getCreateUserId());
			where.eq("is_delete",0);
		}
		where.orderBy("create_time", false);
		Page<OffDailylog> oldPage = selectPage(new Page<OffDailylog>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		Page<OffDailylogDO> page = new Page<OffDailylogDO>();
		List<OffDailylog> dailylogList = oldPage.getRecords();
		List<Long> userIdList = new ArrayList<Long>();
		List<OffDailylogDO> dailylogDOList = new ArrayList<OffDailylogDO>();
		List<Long> dailyLogIds = new ArrayList<Long>();
		if(null != dailylogList && !dailylogList.isEmpty()){
			for(OffDailylog dailylog : dailylogList){
				if(null != dailylog){
					dailyLogIds.add(dailylog.getId());
					if(!userIdList.contains(dailylog.getId())){
						userIdList.add(dailylog.getCreateUserId());
					}
					OffDailylogDO offDailylogDO = new OffDailylogDO();
					offDailylogDO.setId(dailylog.getId());
					offDailylogDO.setTitle(dailylog.getTitle());
					DateTime dateTime = new DateTime(dailylog.getCreateTime());
					offDailylogDO.setCreateDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDDHHMMSS));
					dateTime = new DateTime(dailylog.getWorkDate());
					offDailylogDO.setWorkDate(dateTime.toString(WebConstant.PATTERN_BAR_YYYYMMDD));
					offDailylogDO.setCreateUserId(dailylog.getCreateUserId());
					dailylogDOList.add(offDailylogDO);
				}
			}
		}
		
		if(dailyLogIds.size()>0){
			Where<AutUser> userWhere = new Where<AutUser>();
			userWhere.setSqlSelect("id,full_name");
			userWhere.eq("is_active",1);
			if(userIdList.size()>1){
				userWhere.in("id",userIdList);
			}else if(!userIdList.isEmpty()){
				userWhere.eq("id", userIdList.get(0));
			}
			List<AutUser> userList = autUserService.selectList(userWhere);
			
			Set<Long> offMRids = new HashSet<Long>();
			Where<OffMonthReportDetail> whereMR1 = new Where<OffMonthReportDetail>();
			whereMR1.in("dailylog_id", dailyLogIds);
			where.setSqlSelect("id,dailylog_id,month_report_id");
			List<OffMonthReportDetail> offMRdetails = offMonthReportDetailService.selectList(whereMR1);
			for (OffMonthReportDetail offMonthReportDetail : offMRdetails) {
				offMRids.add(offMonthReportDetail.getMonthReportId());
				for(OffDailylogDO dailylogDO : dailylogDOList){
					if(dailylogDO.getId().equals(offMonthReportDetail.getDailylogId())){
						dailylogDO.setOffMonthReportId(offMonthReportDetail.getMonthReportId());
					}
				}
			}
			Where<OffMonthReport> whereMR = new Where<OffMonthReport>();
			whereMR.setSqlSelect("id,status");
			whereMR.in("id", offMRids);
			List<OffMonthReport> monthReportList = offMonthReportService.selectList(whereMR);
			for(OffDailylogDO dailylogDO : dailylogDOList){
				if(userList.size()>0){
					for(AutUser user : userList){
						if(StringUtils.isNotEmpty(user.getFullName())){
							if((null != user.getId() && null != dailylogDO.getCreateUserId())){
								if(user.getId().equals(dailylogDO.getCreateUserId()) && user.getId().intValue() == dailylogDO.getCreateUserId().intValue()){
									dailylogDO.setCreateFullName(user.getFullName());
								}
							}
						}
					}
				}
				if(monthReportList.size()>0){
					for (OffMonthReport offMonthReport : monthReportList) {
						if(dailylogDO.getOffMonthReportId().equals(offMonthReport.getId())){
							dailylogDO.setStatus(offMonthReport.getStatus());
						}
					}
				}
			}
		}
		page.setRecords(dailylogDOList);
		page.setTotal(oldPage.getTotal());
		page.setSize(oldPage.getSize());
		page.setCurrent(oldPage.getCurrent());
		return page;
	}

	@Override
	@Transactional
	public RetMsg add(OffDailylogVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if(null != obj){
			if(StringUtils.isEmpty(obj.getTitle())){
				obj.setTitle("");
				retMsg.setCode(1);
				retMsg.setMessage("标题不能为空");
				return retMsg;
			}
			if(StringUtils.isEmpty(obj.getContent())){
				obj.setContent("");
				retMsg.setCode(1);
				retMsg.setMessage("内容不能为空");
				return retMsg;
			}
			if(null == obj.getWorkDate()){
				obj.setWorkDate(new Date());
			}
			Date date = obj.getWorkDate();
			DateTime curDate = new DateTime(date);
			Where<OffDailylog> offDailylogWhere = new Where<OffDailylog>();
			offDailylogWhere.eq("work_date",date);
			offDailylogWhere.eq("create_user_id",obj.getCreateUserId());
			int count = selectCount(offDailylogWhere);
			if(count >= 1){
				retMsg.setCode(1);
				retMsg.setMessage("日志已经存在！");
				return retMsg;
			}
			String month = DateUtil.getFirstDateOfMonth(curDate.toString(WebConstant.PATTERN_BAR_YYYYMM));
			Where<OffMonthReport> whereMR = new Where<OffMonthReport>();
			whereMR.eq("create_user_id",obj.getCreateUserId());
			whereMR.eq("month",month);
			whereMR.eq("status", 1);
			int submitedMR = offMonthReportService.selectCount(whereMR);
			if(submitedMR > 0){
				retMsg.setCode(1);
				retMsg.setMessage(month + "月月报已经提交，不能再新增该月日志！");
				return retMsg;
			}
			OffDailylog offDailylog = new OffDailylog();
			BeanUtils.copyProperties(obj,offDailylog);
			if(null != offDailylog){
				insert(offDailylog);
			}
			
			if(null != obj.getCreateUserId()){
				Where<OffMonthReport> where = new Where<OffMonthReport>();
				where.setSqlSelect("id");
				where.eq("create_user_id",obj.getCreateUserId());
				where.eq("month",month);
				List<OffMonthReport> offMonthReportList = offMonthReportService.selectList(where);
				Long monthReportId = null;
				if(null != offMonthReportList && offMonthReportList.isEmpty()){
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.eq("id",obj.getCreateUserId());
					userWhere.setSqlSelect("id,user_name,full_name");
					AutUser user = autUserService.selectOne(userWhere);
					OffMonthReport monthReport = new OffMonthReport();
					if(null != user){
						monthReport.setTitle(user.getFullName() + " " + month +  "月份月报");
						monthReport.setSubmitId(obj.getCreateUserId());
						monthReport.setSubmitterName(user.getFullName());
					}
					monthReport.setMonth(month);
					monthReport.setStatus(0);
					Where<AutDepartmentUser> departmentUserWhere = new Where<AutDepartmentUser>();
					departmentUserWhere.eq("user_id",obj.getCreateUserId());
					departmentUserWhere.setSqlSelect("id,user_id,dept_id");
					List<AutDepartmentUser> autDepartmentUserList = autDepartmentUserService.selectList(departmentUserWhere);

					if(null != autDepartmentUserList && !autDepartmentUserList.isEmpty()){
						List<Long> deptIdList = new ArrayList<Long>();
						for(AutDepartmentUser departmentUser : autDepartmentUserList){
							if(null != departmentUser){
								deptIdList.add(departmentUser.getDeptId());
							}
						}
						List<AutDepartment> departmentList = null;
						if(null != deptIdList && !deptIdList.isEmpty()){
							Where<AutDepartment> departmentWhere = new Where<AutDepartment>();
							departmentWhere.in("id",deptIdList);
							departmentWhere.setSqlSelect("id,dept_name");
							departmentList = autDepartmentService.selectList(departmentWhere);
						}
						String belongDeptId = "";
						String belongDeptName = "";
						if(null != departmentList && !departmentList.isEmpty()){
							for (AutDepartment department : departmentList){
								belongDeptId += department.getId()+";";
								belongDeptName += department.getDeptName()+";";
							}
						}
						monthReport.setBelongDeptId(belongDeptId);
						monthReport.setBelongDept(belongDeptName);
						monthReport.setAuditStatus(0);
						offMonthReportService.insert(monthReport);
						monthReportId = monthReport.getId(); 
					}else{
						monthReport.setBelongDeptId("");
						monthReport.setBelongDept("");
					}
				}else{
					if(offMonthReportList.size()>1){
						retMsg.setCode(1);
						retMsg.setMessage("当月存在多条月报表信息");
						return retMsg;
					}
					monthReportId = offMonthReportList.get(0).getId();
				}
				OffMonthReportDetail monthReportDetail = new OffMonthReportDetail();
				monthReportDetail.setDailylogId(offDailylog.getId());
				monthReportDetail.setContent(offDailylog.getContent());
				String week = DateUtil.getWeek(date);
				String curDateStr = curDate.toString("yyyy-MM-dd");
				monthReportDetail.setWorkDate(curDateStr+" "+week);
				monthReportDetail.setMonthReportId(monthReportId);
				
				offMonthReportDetailService.insert(monthReportDetail);
				// 保存附件
	            List<ResResource> resResourceList = obj.getResResourceList();
	            List<Long> ids = new ArrayList<Long>();
	            if(null != resResourceList && !resResourceList.isEmpty()){
	                for (ResResource resResource : resResourceList) {
	                    ids.add(resResource.getId());
	                }
	                Where<ResResource> resResourceWhere = new Where<ResResource>();
	                resResourceWhere.in("id", ids);
	                resResourceList = resResourceService.selectList(resResourceWhere);
	                for (ResResource resResource : resResourceList) {
	                    resResource.setBizId(offDailylog.getId());
	                    resResource.setFileDesc("新增工作日志附件上传");
	                }
	                resResourceService.updateBatchById(resResourceList);
	            }
			}
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	@Transactional
	public RetMsg update(OffDailylogVO obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if(null != obj && null != obj.getId()){
			Where<OffMonthReportDetail> whereMR = new Where<OffMonthReportDetail>();
			whereMR.eq("dailylog_id", obj.getId());
			whereMR.eq("is_delete", 0);
			OffMonthReportDetail offMRdetail = offMonthReportDetailService.selectOne(whereMR);
			if(null != offMRdetail){
				OffMonthReport offMR = offMonthReportService.selectById(offMRdetail.getMonthReportId());
				if(offMR != null){
					if(offMR.getStatus() == 1){
						retMsg.setCode(1);
						retMsg.setMessage("月报已提交，不可再修改");
						return retMsg;
					}
				}
			}
			OffDailylog offDailylog = selectById(obj.getId());
			if(null != offDailylog){
				if(StringUtils.isNotEmpty(obj.getTitle())){
					offDailylog.setTitle(obj.getTitle());
				}
				if(StringUtils.isNotEmpty(obj.getContent())){
					offDailylog.setContent(obj.getContent());
				}
				if(null != obj.getWorkDate()){
					offDailylog.setWorkDate(obj.getWorkDate());
				}
				updateById(offDailylog);
				Where<OffMonthReportDetail> monthReportDetailWhere = new Where<OffMonthReportDetail>();
				monthReportDetailWhere.eq("dailylog_id",obj.getId());
				OffMonthReportDetail offMonthReportDetail = offMonthReportDetailService.selectOne(monthReportDetailWhere);
				if(null != offMonthReportDetail){
					if(StringUtils.isNotEmpty(obj.getContent())){
						offMonthReportDetail.setContent(obj.getContent());
					}
					offMonthReportDetailService.updateById(offMonthReportDetail);
				}
			}
			//附件
			List<ResResource> newResourceList = obj.getResResourceList();
            List<Long> newResourceIds = new ArrayList<Long>();
            if (null != newResourceList && newResourceList.size() > 0) {
                for (ResResource resResource : newResourceList) {
                    newResourceIds.add(resResource.getId());
                }
            }
            if (null != newResourceIds && newResourceIds.size() > 0) {
                List<ResResource> addResource = resResourceService.selectBatchIds(newResourceIds);
                for (ResResource resResource : addResource) {
                    resResource.setBizId(offDailylog.getId());
                    resResource.setFileDesc("编辑工作日志附件上传");
                }
                resResourceService.updateBatchById(addResource);
            }
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	@Transactional
	public RetMsg delete(OffDailylog obj) throws Exception {
		RetMsg retMsg = new RetMsg();
		if(null != obj && null != obj.getId()){
			Where<OffMonthReportDetail> whereMR1 = new Where<OffMonthReportDetail>();
			whereMR1.eq("dailylog_id", obj.getId());
			OffMonthReportDetail offMRdetail = offMonthReportDetailService.selectOne(whereMR1);
			if(null != offMRdetail){
				OffMonthReport offMR = offMonthReportService.selectById(offMRdetail.getMonthReportId());
				if(offMR != null){
					if(offMR.getStatus() == 1){
						retMsg.setCode(1);
						retMsg.setMessage("月报已提交，不可删除");
						return retMsg;
					}
				}
			}
			deleteById(obj.getId());
			//删除附件(暂时不删除附件信息)
			/*Where<ResResource> resourceWhere = new Where<ResResource>();
			resourceWhere.eq("biz_id",obj.getId());
            List<ResResource> resourceList = resResourceService.selectList(resourceWhere);
            List<Long> resourcesIds = new ArrayList<Long>();
            if (null != resourceList && resourceList.size() > 0) {
                for (ResResource resResource : resourceList) {
                    resourcesIds.add(resResource.getId());
                }
                resResourceService.deleteBatchById(resourcesIds);
            }*/
			Where<OffMonthReportDetail> where = new Where<OffMonthReportDetail>();
			where.eq("dailylog_id",obj.getId());
			OffMonthReportDetail  offMonthReportDetail = offMonthReportDetailService.selectOne(where);
			Long idMR = new Long(0);
			if(null != offMonthReportDetail && null != offMonthReportDetail.getId()){
				idMR = offMonthReportDetail.getId();
				offMonthReportDetailService.deleteById(offMonthReportDetail.getId());
			}
			//当月报中没有日志时直接删除月报
			if(0 != idMR){
				Where<OffMonthReportDetail> whereMR = new Where<OffMonthReportDetail>();
				whereMR.eq("month_report_id",offMonthReportDetail.getMonthReportId());
				int count = offMonthReportDetailService.selectCount(whereMR);
				if(count<=0){
					OffMonthReport offMR = offMonthReportService.selectById(offMonthReportDetail.getMonthReportId());
					offMonthReportService.deleteById(offMR.getId());
					//删除附件(暂时不删除附件信息)
		            /*Where<ResResource> monthResource = new Where<ResResource>();
		            monthResource.eq("biz_id",offMR.getId());
		            List<ResResource> monthResourceList = resResourceService.selectList(monthResource);
		            List<Long> monthResourceIds = new ArrayList<Long>();
		            if (null != monthResourceList && monthResourceList.size() > 0) {
		                for (ResResource resResource : monthResourceList) {
		                    monthResourceIds.add(resResource.getId());
		                }
		                resResourceService.deleteBatchById(monthResourceIds);
		            }*/
				}
			}
			
		}
		retMsg.setCode(0);
		retMsg.setMessage("操作成功");
		return retMsg;
	}

	@Override
	public OffDailylogVO detail(OffDailylog obj) throws Exception {
		OffDailylogVO offDailylogVO = null;
		if(null != obj && null != obj.getId()){
			offDailylogVO = new OffDailylogVO();
			OffDailylog offDailylog = selectById(obj.getId());
			if(null != offDailylog){
				BeanUtils.copyProperties(offDailylog,offDailylogVO);
				if(null != offDailylogVO.getCreateTime()){
					DateTime dateTime = new DateTime(offDailylogVO.getCreateTime());
					offDailylogVO.setCreateTimeStr(dateTime.toString("yyyy-MM-dd HH:mm"));
				}
				if(null != offDailylogVO.getWorkDate()){
					DateTime dateTime = new DateTime(offDailylogVO.getWorkDate());
					offDailylogVO.setWorkDateStr(dateTime.toString("yyyy-MM-dd"));
				}
				if(null != offDailylogVO.getCreateUserId()){
					Where<AutUser> userWhere = new Where<AutUser>();
					userWhere.eq("id",offDailylogVO.getCreateUserId());
					AutUser user = autUserService.selectOne(userWhere);
					if(null != user && StringUtils.isNotEmpty(user.getFullName())){
						offDailylogVO.setCreateFullName(user.getFullName());
					}
				}
				
				Where<ResResource> where = new Where<ResResource>();
		        where.eq("biz_id", offDailylog.getId());
		        List<ResResource> resourceList = resResourceService.selectList(where);
		        if (null != resourceList && !resourceList.isEmpty()) {
		            offDailylogVO.setResResourceList(resourceList);
		        }
			}

		}
		return offDailylogVO;
	}
	
	@Override
	@Transactional
	public RetMsg deleteByIds(String ids) throws Exception {
		RetMsg retMsg = new RetMsg();
		Boolean isSubmit = false;
		if(null!=ids && !ids.isEmpty()){
			if(";".equals(ids.indexOf(ids.length()-1))){
				ids = ids.substring(0, ids.length()-1);
			}
			String[] idArr = ids.split(";");
			for(int i = 0; i<idArr.length; i++){
				long id = Long.valueOf(idArr[i]);
				OffDailylog log = new OffDailylog();
				log.setId(id);
				retMsg = delete(log);
				if(retMsg.getCode() == 1){
					isSubmit = true;
				}
				//删除附件(暂时不删除)
				/*Where<ResResource> where = new Where<ResResource>();
	            where.in("biz_id",idArr);
	            List<ResResource> resourcesList = resResourceService.selectList(where);
	            List<Long> resourcesIds = new ArrayList<Long>();
                for (ResResource resResource : resourcesList) {
                    resourcesIds.add(resResource.getId());
                }
                if(null != resourcesList && !resourcesList.isEmpty()){
                    resResourceService.deleteBatchById(resourcesIds);
                }*/
			}
		}
		if(isSubmit){
			retMsg.setMessage("已提交月报中的日志不可操作，</br>其他日志操作已完成！");
		}else{
			retMsg.setMessage("操作成功");
		}
		retMsg.setCode(0);
		return retMsg;
	}
}
