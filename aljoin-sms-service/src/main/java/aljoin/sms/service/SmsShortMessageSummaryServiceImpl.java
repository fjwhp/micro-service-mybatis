package aljoin.sms.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutUser;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sms.dao.entity.SmsShortMessageSummary;
import aljoin.sms.dao.mapper.SmsShortMessageSummaryMapper;
import aljoin.sms.iservice.SmsShortMessageSummaryService;
import aljoin.sms.pubservice.SmsServiceUntils;

/**
 * 
 * 短信明细表(服务实现类).
 * 
 * @author：huangw
 * 
 * @date： 2018-01-15
 */
@Service
public class SmsShortMessageSummaryServiceImpl extends ServiceImpl<SmsShortMessageSummaryMapper, SmsShortMessageSummary> implements SmsShortMessageSummaryService {
	private final static Logger logger = LoggerFactory.getLogger(SmsShortMessageSummaryServiceImpl.class);
  @Resource
  private SmsShortMessageSummaryMapper mapper;

  @Override
  public Page<SmsShortMessageSummary> list(PageBean pageBean, SmsShortMessageSummary obj) throws Exception {
	Where<SmsShortMessageSummary> where = new Where<SmsShortMessageSummary>();
	where.orderBy("create_time", false);
	Page<SmsShortMessageSummary> page = selectPage(new Page<SmsShortMessageSummary>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	

	@Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


	@Override
  public void copyObject(SmsShortMessageSummary obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
	public RetMsg addOne(SmsShortMessageSummary obj,AutUser user){
		RetMsg retMsg = new RetMsg();
		try {			
			String[] userPoneIds=obj.getSendNumber().toString().split(";");
			retMsg = SmsServiceUntils.sendSmMT(userPoneIds, obj.getContent());			
		} catch (Exception e) {
			logger.error("", e);
			retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
			retMsg.setMessage(e.getMessage());
		}
		return retMsg;
	}

}
