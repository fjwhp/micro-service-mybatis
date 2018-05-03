package aljoin.ioa.service;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaRegCategory;
import aljoin.ioa.dao.entity.IoaRegClosed;
import aljoin.ioa.dao.mapper.IoaCirculaMapper;
import aljoin.ioa.dao.mapper.IoaRegClosedMapper;
import aljoin.ioa.dao.object.IoaRegClosedVO;
import aljoin.ioa.iservice.IoaRegCategoryService;
import aljoin.ioa.iservice.IoaRegClosedService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;
import aljoin.util.ExcelUtil;

/**
 * 收文阅件表(服务实现类).
 * 
 * @author：zhongjy @date： 2017-11-08
 */
@Service
public class IoaRegClosedServiceImpl extends ServiceImpl<IoaRegClosedMapper, IoaRegClosed>
		implements IoaRegClosedService {
	@Resource
	private IoaCirculaMapper mapper;
	@Resource
	private IoaRegCategoryService ioaRegCategoryService;

	@Override
	public Page<IoaRegClosed> list(PageBean pageBean, IoaRegClosedVO obj, Long userId) throws Exception {
		String startDate = null;
		String endDate = null;
		// 登记日期区间、文号、文件标题、来文单位、发往单位
		Where<IoaRegClosed> where = new Where<IoaRegClosed>();
		if (obj.getStartTime() != null && obj.getEndTime() != null) {
            startDate = DateUtil.date2str(6, obj.getStartTime()) + " 00:00:00";
            endDate = DateUtil.date2str(6, obj.getEndTime()) + " 00:00:00";
            where.ge("create_time", startDate);
            where.lt("create_time", endDate);
        }
        if (obj.getClosedNo() != null && !"".equals(obj.getClosedNo())) {
            where.like("closed_no", obj.getClosedNo());//收文文号
        }
        if (obj.getToNo() != null && !"".equals(obj.getToNo())) {
            where.like("to_no", obj.getToNo());//来文文号
        }
        if (obj.getTitle() != null && !"".equals(obj.getTitle())) {
            where.like("title", obj.getTitle());//标题
        }
        if (obj.getToUnit() != null && StringUtils.isNotEmpty(obj.getToUnit())){
            where.like("to_unit", obj.getToUnit());//来文单位
        }
        String circulaUser = obj.getRegistrationName();// 人名查询
		if (circulaUser != null && !"".equals(circulaUser)) {
			where.like("registration_name", circulaUser);
		}
		if (obj.getCategory()!= null && StringUtils.isNotEmpty(obj.getCategory())){
		    where.eq("category", obj.getCategory());
		}
		if(obj.getIsClosedNoAsc() != null && "1".equals(obj.getIsClosedNoAsc())){
		    where.orderBy("closed_no");
		}else if(obj.getIsClosedNoAsc() != null && "2".equals(obj.getIsClosedNoAsc())){
		    where.orderBy("closed_no",false);
		}else if(obj.getIsRegistrationTimeAsc() != null && "1".equals(obj.getIsRegistrationTimeAsc())){
            where.orderBy("registration_time");
        }else if(obj.getIsRegistrationTimeAsc() != null && "2".equals(obj.getIsRegistrationTimeAsc())){
            where.orderBy("registration_time",false);
        }else if(obj.getIstoNoAsc() != null && "1".equals(obj.getIstoNoAsc())){
            where.orderBy("to_no",false);
        }else if(obj.getIstoNoAsc() != null && "2".equals(obj.getIstoNoAsc())){
            where.orderBy("to_no",false);
        }else{
            where.orderBy("id", false);
        }
		Page<IoaRegClosed> list = this.selectPage(new Page<IoaRegClosed>(pageBean.getPageNum(), pageBean.getPageSize()),
				where);

		return list;
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyObject(IoaRegClosed obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public RetMsg addRegClosed(IoaRegClosed obj) throws Exception {
		// TODO Auto-generated method stub
		RetMsg retMsg=new RetMsg();
		this.insert(obj);		
		retMsg.setCode(0);
		retMsg.setMessage("登记成功！");
		return retMsg;
	}

	@Override
	public void export(HttpServletResponse response,IoaRegClosed obj) throws Exception {
		// TODO Auto-generated method stub
		String startDate = null;
		String endDate = null;
		// 登记日期区间、文号、文件标题、来文单位、发往单位
		Where<IoaRegClosed> where = new Where<IoaRegClosed>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (obj.getCreateTime() != null && obj.getClosedDate() != null) {
			startDate = format.format(obj.getCreateTime()) + " 00:00:00";
			endDate = format.format(obj.getClosedDate()) + " 23:59:59";
			where.ge("create_time", startDate);
			where.lt("create_time", endDate);
		}
		if (obj.getClosedNo() != null && !"".equals(obj.getClosedNo())) {
			where.like("closed_no", obj.getClosedNo());//收文文号
		}
		if (obj.getTitle() != null && !"".equals(obj.getTitle())) {
			where.like("to_no", obj.getToNo());//来文文号
		}
		if (obj.getToType() != null && !"".equals(obj.getToType())) {
			where.like("to_type", obj.getToType());//来文类型
		}
		if (obj.getToUnit() != null && !"".equals(obj.getToUnit())) {
			where.like("to_unit", obj.getToUnit());//来文单位
		}
		
		if (obj.getPrioritiesLevel()!=null ) {
			where.eq("priorities_level", obj.getPrioritiesLevel());//缓急程度
		}
		if (obj.getCategory() != null && !"".equals(obj.getCategory()) ) {
			String cateGoryId=obj.getCategory().toString()+",";		
	        String tmpCateGoryId="";
	            do {
	                tmpCateGoryId+=cateGoryId;              
	                Where<IoaRegCategory>cateGoryWhere=new Where<IoaRegCategory>();
	                cateGoryWhere.in("parent_id", cateGoryId);
	                cateGoryWhere.eq("reg_type", "0");
	                cateGoryWhere.setSqlSelect("id");
	                List<IoaRegCategory> cateGoryList=ioaRegCategoryService.selectList(cateGoryWhere);
	                cateGoryId="";
	                if(cateGoryList!=null && cateGoryList.size()>0){
	                    for (IoaRegCategory actAljoinFormCategory : cateGoryList) {
	                        cateGoryId+=actAljoinFormCategory.getId()+",";
	                    }                   
	                }
	            } while (!"".equals(cateGoryId));
	            where.in("category", tmpCateGoryId);//分类					
		}
		String circulaUser = obj.getRegistrationName();// 人名查询
		if (circulaUser != null && !"".equals(circulaUser)) {
			where.like("registration_name", circulaUser);
		}
		where.orderBy("create_time", false);
		List<IoaRegClosed> exportList=this.selectList(where);
		if(exportList!=null && exportList.size()>0){
			       String[][] headers = {{"Title", "标题"}, {"RegistrationName", "登记人员"}, {"RegistrationTime", "登记时间"}, {"ClosedNo", "收文文号"},{"ToNo", "来文文号"}, {"ToType", "来文类型"}, {"ToUnit", "来文单位"}, {"Level", "密级"}, {"ClosedDate", "收文日期"}, {"Priorities", "缓急"}};
		            List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
		            for (IoaRegClosed entity : exportList) {
		                Map<String, Object> map = new HashMap<String, Object>();
		                for (int i = 0; i < headers.length; i++) {
		                	IoaRegClosed schedulingVO = entity;
		                    String methodName = "get" + headers[i][0];
		                    Class<?> clazz = IoaRegClosed.class;
		                    Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
		                    if (null != method) {
		                        map.put(headers[i][0], method.invoke(schedulingVO));
		                    }
		                }
		                dataset.add(map);
		            }
		            ExcelUtil.exportExcel("收文登记"+format.format(new Date()), headers, dataset, response);
		}		
	}

}
