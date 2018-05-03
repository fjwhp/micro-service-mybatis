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
import aljoin.ioa.dao.entity.IoaRegHair;
import aljoin.ioa.dao.mapper.IoaCirculaMapper;
import aljoin.ioa.dao.mapper.IoaRegHairMapper;
import aljoin.ioa.dao.object.IoaRegHairVO;
import aljoin.ioa.iservice.IoaRegCategoryService;
import aljoin.ioa.iservice.IoaRegHairService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.ExcelUtil;

/**
 * 发文登记(服务实现类).
 * 
 * 
 */
@Service
public class IoaRegHairServiceImpl extends ServiceImpl<IoaRegHairMapper, IoaRegHair> implements IoaRegHairService {
	@Resource
	private IoaCirculaMapper mapper;
	@Resource
	private IoaRegCategoryService ioaRegCategoryService;

	@Override
	public Page<IoaRegHair> list(PageBean pageBean, IoaRegHairVO obj) throws Exception {
		String startDate = null;
		String endDate = null;
		// 登记日期区间、文号、文件标题、来文单位、发往单位
		Where<IoaRegHair> where = new Where<IoaRegHair>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (obj.getStartTime() != null && obj.getEndTime() != null) {
			startDate = format.format(obj.getStartTime()) + " 00:00:00";
			endDate = format.format(obj.getEndTime()) + " 23:59:59";
			where.ge("create_time", startDate);
			where.lt("create_time", endDate);
		}

		if (obj.getTitle() != null && !"".equals(obj.getTitle())) {
			where.like("title", obj.getTitle());// 标题
		}

		if (obj.getHairType() != null && !"".equals(obj.getHairType())) {
			where.like("hair_type", obj.getHairType());// 发文类型
		}

		if (obj.getHairNo() != null && !"".equals(obj.getHairNo())) {
			where.like("hair_no", obj.getHairNo());// 发文文号
		}

		if (obj.getHairUnit() != null && !"".equals(obj.getHairUnit())) {
			where.like("hair_unit", obj.getHairUnit());// 发文单位
		}
        if (obj.getCategory()!= null && StringUtils.isNotEmpty(obj.getCategory())){
            where.eq("category", obj.getCategory());
        }
		String circulaUser = obj.getRegistrationName();// 登记人名查询
		if (circulaUser != null && !"".equals(circulaUser)) {
			where.like("registration_name", circulaUser);
		}
		
	      if(obj.getIshairNoAsc() != null && "1".equals(obj.getIshairNoAsc())){
	            where.orderBy("hair_no");
	        }else if(obj.getIshairNoAsc() != null && "2".equals(obj.getIshairNoAsc())){
	            where.orderBy("closed_no",false);
	        }else if(obj.getIsRegistrationTimeAsc() != null && "1".equals(obj.getIsRegistrationTimeAsc())){
	            where.orderBy("registration_time");
	        }else if(obj.getIsRegistrationTimeAsc() != null && "2".equals(obj.getIsRegistrationTimeAsc())){
	            where.orderBy("registration_time",false);
	        }else if(obj.getIsHairTimeAsc() != null && "1".equals(obj.getIsHairTimeAsc())){
	            where.orderBy("hair_time",false);
	        }else if(obj.getIsHairTimeAsc() != null && "2".equals(obj.getIsHairTimeAsc())){
	            where.orderBy("to_no",false);
	        }else{
	            where.orderBy("id", false);
	        }
		where.orderBy("create_time", false);
		Page<IoaRegHair> list = this.selectPage(new Page<IoaRegHair>(pageBean.getPageNum(), pageBean.getPageSize()),
				where);

		return list;
	}

	@Override
	public RetMsg addRegHair(IoaRegHair obj) throws Exception {
		RetMsg retMsg=new RetMsg();
		this.insert(obj);		
		retMsg.setCode(0);
		retMsg.setMessage("登记成功！");
		return retMsg;
	}

	@Override
	public void export(HttpServletResponse response, IoaRegHair obj) throws Exception {
		// TODO Auto-generated method stub
		String startDate = null;
		String endDate = null;
		// 登记日期区间、文号、文件标题、来文单位、发往单位
		Where<IoaRegHair> where = new Where<IoaRegHair>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (obj.getCreateTime() != null && obj.getRegistrationTime() != null) {
			startDate = format.format(obj.getCreateTime()) + " 00:00:00";
			endDate = format.format(obj.getRegistrationTime()) + " 23:59:59";
			where.ge("create_time", startDate);
			where.lt("create_time", endDate);
		}

		if (obj.getTitle() != null && !"".equals(obj.getTitle())) {
			where.like("title", obj.getTitle());// 标题
		}

		if (obj.getHairType() != null && !"".equals(obj.getHairType())) {
			where.like("hair_type", obj.getHairType());// 发文类型
		}

		if (obj.getHairNo() != null && !"".equals(obj.getHairNo())) {
			where.like("hair_no", obj.getHairNo());// 发文文号
		}

		if (obj.getHairUnit() != null && !"".equals(obj.getHairUnit())) {
			where.like("hair_unit", obj.getHairUnit());// 发文单位
		}
		if (obj.getLevel() != null && !"".equals(obj.getLevel())) {
			where.eq("secret_level", Integer.valueOf(obj.getLevel()));// 缓急
		}
		if (obj.getCategory() != null && !"".equals(obj.getCategory())) {
			String cateGoryId = obj.getCategory().toString() + ",";
			String tmpCateGoryId = "";
			do {
				tmpCateGoryId += cateGoryId;
				Where<IoaRegCategory> cateGoryWhere = new Where<IoaRegCategory>();
				cateGoryWhere.in("parent_id", cateGoryId);
				cateGoryWhere.eq("reg_type", "1");
				cateGoryWhere.setSqlSelect("id");
				List<IoaRegCategory> cateGoryList = ioaRegCategoryService.selectList(cateGoryWhere);
				cateGoryId = "";
				if (cateGoryList != null && cateGoryList.size() > 0) {
					for (IoaRegCategory actAljoinFormCategory : cateGoryList) {
						cateGoryId += actAljoinFormCategory.getId() + ",";
					}
				}
			} while (!"".equals(cateGoryId));
			where.in("category", tmpCateGoryId);// 分类
		}
		String circulaUser = obj.getRegistrationName();// 登记人名查询
		if (circulaUser != null && !"".equals(circulaUser)) {
			where.like("registration_name", circulaUser);
		}
		where.orderBy("create_time", false);
		List<IoaRegHair> exportList = this.selectList(where);
		if (exportList != null && exportList.size() > 0) {
			String[][] headers = { { "Title", "标题" }, { "RegistrationName", "登记人员" }, { "RegistrationTime", "登记时间" },
					{ "HairNo", "发文文号" }, { "HairUnit", "发文单位" }, { "HairType", "文件类型" }, { "HairTime", "签发时间" },
					{ "Level", "密级" }};
			List<Map<String, Object>> dataset = new ArrayList<Map<String, Object>>();
			for (IoaRegHair entity : exportList) {
				Map<String, Object> map = new HashMap<String, Object>();
				for (int i = 0; i < headers.length; i++) {
					IoaRegHair schedulingVO = entity;
					String methodName = "get" + headers[i][0];
					Class<?> clazz = IoaRegHair.class;
					Method method = ExcelUtil.getMethod(clazz, methodName, new Class[] {});
					if (null != method) {
						if(methodName.indexOf("Time")>=0){
							SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							map.put(headers[i][0], formats.format(method.invoke(schedulingVO)));
						}else{
							map.put(headers[i][0], method.invoke(schedulingVO));
						}
						
					}
				}
				dataset.add(map);
			}
			
			ExcelUtil.exportExcel("发文登记"+format.format(new Date()), headers, dataset, response);
		}
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void copyObject(IoaRegHair obj) throws Exception {
		// TODO Auto-generated method stub

	}

}
