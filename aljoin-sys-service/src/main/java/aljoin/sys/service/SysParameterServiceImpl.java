package aljoin.sys.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.dao.mapper.SysParameterMapper;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 系统参数表(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-06-04
 */
@Service
public class SysParameterServiceImpl extends ServiceImpl<SysParameterMapper, SysParameter>
		implements SysParameterService {
  private final static Logger logger = LoggerFactory.getLogger(SysParameterServiceImpl.class);

	@Override
	public Page<SysParameter> list(PageBean pageBean, SysParameter obj) throws Exception {
		Where<SysParameter> where = new Where<SysParameter>();
		if (StringUtils.isNotEmpty(obj.getParamKey())) {
			where.like("param_key", obj.getParamKey());
			where.or("param_desc LIKE {0}", "%" + obj.getParamKey() + "%");
		}
		where.orderBy("create_time", false);
		Page<SysParameter> page = selectPage(new Page<SysParameter>(pageBean.getPageNum(), pageBean.getPageSize()),
				where);
		return page;
	}

	@Override
	public SysParameter selectBykey(String key) throws Exception {
		Where<SysParameter> where = new Where<SysParameter>();
		if (StringUtils.isNotEmpty(key)) {
			where.eq("param_key", key);
		}
		where.setSqlSelect("id,param_key,param_value");
		where.orderBy("create_time", false);
		SysParameter sysParameter = selectOne(where);
		if(sysParameter ==  null){
		  throw new Exception("系统参数["+key+"]为空，请先配置此参数");
		}
		return sysParameter;
	}

	/**
	 *
	 * 获得允许上传文件的大小、文件类型
	 *
	 * @return：Map<String,String>
	 *
	 * @author：wangj
	 *
	 * @date：2017年9月21日 上午8:52:44
	 */
	@Override
	public Map<String, String> allowFileType() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		try {
			// 允许最大上传文件,设置为100M
			SysParameter limitSize = selectBykey("sys_attachment_size");
			Long maxSize = Long.parseLong(limitSize.getParamValue().trim());//Long.parseLong(limitSize.getParamValue())*1024*1024;
			// 允许上传文件类型（从系统参数表里取）
			SysParameter allowType = selectBykey("allow_type");
			// 设置最大允许上传文件
			if (limitSize.getParamValue() != null) {
				map.put("limitSize", String.valueOf(maxSize));
			}
			// 设置允许上传文件类型
			if (allowType.getParamValue() != null) {
				map.put("allowType", allowType.getParamValue());
			}
		} catch (Exception e) {
		  logger.error("",e);
		}
		return map;
	}
}
