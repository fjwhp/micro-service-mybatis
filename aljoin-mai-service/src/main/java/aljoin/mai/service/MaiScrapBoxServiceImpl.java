package aljoin.mai.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiScrapBoxMapper;
import aljoin.mai.dao.object.MaiScrapBoxVO;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiScrapBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.util.DateUtil;

/**
 * 
 * 废件箱表(服务实现类).
 * 
 * @author：wangj
 * 
 * @date： 2017-09-20
 */
@Service
public class MaiScrapBoxServiceImpl extends ServiceImpl<MaiScrapBoxMapper, MaiScrapBox> implements MaiScrapBoxService {
   private final static Logger logger = LoggerFactory.getLogger(MaiScrapBoxServiceImpl.class);
  
	@Resource
	private MaiSendBoxService maiSendBoxService;
	@Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;
	@Resource
	private MaiScrapBoxService maiScrapBoxService;

	@Override
	public Page<MaiScrapBox> list(PageBean pageBean, MaiScrapBox obj, Long userId, String time1, String time2,String orderByTime)
			throws Exception {
		Where<MaiScrapBox> where = new Where<MaiScrapBox>();
		if (StringUtils.isNotEmpty(obj.getSubjectText())) {
			where.like("subject_text", obj.getSubjectText());
			where.or("send_full_name LIKE {0}", "%" + obj.getSubjectText() + "%");
		}
		if (StringUtils.isNotEmpty(time1) && StringUtils.isNotEmpty(time2)) {
		  where.andNew();
			where.between("last_update_time", DateUtil.str2dateOrTime(time1), DateUtil.str2dateOrTime(time2));
		} else if (StringUtils.isNotEmpty(time1)) {
		  where.andNew();
			where.ge("last_update_time", DateUtil.str2dateOrTime(time1));
		} else if (StringUtils.isNotEmpty(time2)) {
		  where.andNew();
			where.le("last_update_time", DateUtil.str2dateOrTime(time2));
		}
		where.andNew().eq("create_user_id", userId);
		//按照邮件大小升序/降序或按收件时间排序，如果都默认按收件时间降序
		if(StringUtils.checkValNotNull(obj.getMailSize())){
			if(obj.getMailSize().equals(1)||obj.getMailSize()==1){
				where.orderBy("mail_size",true);
			}else{
				where.orderBy("mail_size",false);
			}
		}else if(StringUtils.checkValNotNull(orderByTime)){
			if("1".equals(orderByTime)){
				where.orderBy("last_update_time",true);
			}else{
				where.orderBy("last_update_time",false);
			}
		}
		else{
			where.orderBy("last_update_time", false);
		}
		Page<MaiScrapBox> page = selectPage(new Page<MaiScrapBox>(pageBean.getPageNum(), pageBean.getPageSize()),
				where);
		return page;
	}

	@Override
	@Transactional
	public RetMsg recover(MaiScrapBoxVO obj) throws Exception{
		RetMsg retMsg = new RetMsg();

		try {
			List<MaiScrapBox> maiScrapBoxList = obj.getMaiScrapBoxList();
			List<Long> scrapBoxIdList = new ArrayList<Long>();
			for (MaiScrapBox maiScrapBox : maiScrapBoxList) {
				scrapBoxIdList.add(maiScrapBox.getId());
			}
			
			// 发件箱
			List<Long> receiveBoxIdList = new ArrayList<Long>();
			// 收件箱
			List<Long> sendBoxIdList = new ArrayList<Long>();
			for(MaiScrapBox maiScrapBox : maiScrapBoxList){
				if(maiScrapBox.getOrgnlType()==1  || "1".equals(maiScrapBox.getOrgnlType()) ){
					receiveBoxIdList.add(maiScrapBox.getOrgnlId());
				}
				if(maiScrapBox.getOrgnlType()==2 || "2".equals(maiScrapBox.getOrgnlType())){
					sendBoxIdList.add(maiScrapBox.getOrgnlId());
				}
			}
			List<MaiReceiveBoxSearch> receiveBoxSearchList = new ArrayList<MaiReceiveBoxSearch>();
			List<MaiSendBox> sendBoxList = new ArrayList<MaiSendBox>();
			if(null != receiveBoxIdList && !receiveBoxIdList.isEmpty() ){
				Where<MaiReceiveBoxSearch> w2 = new Where<MaiReceiveBoxSearch>();
				w2.in("id", receiveBoxIdList);
				receiveBoxSearchList = maiReceiveBoxSearchService.selectList(w2);
				for(MaiReceiveBoxSearch maiReceiveBox : receiveBoxSearchList){
					maiReceiveBox.setIsScrap(0);
				}
			}
			if(null != sendBoxIdList && !sendBoxIdList.isEmpty()){
				Where<MaiSendBox> w3 = new Where<MaiSendBox>();
				w3.in("id", sendBoxIdList);
				sendBoxList = maiSendBoxService.selectList(w3);
				for(MaiSendBox maiSendBox : sendBoxList){
					maiSendBox.setIsScrap(0);
				}
			}
			
			if (null != receiveBoxSearchList && !receiveBoxSearchList.isEmpty()) {
				// 恢复到收件箱
				maiReceiveBoxSearchService.updateBatchById(receiveBoxSearchList);
			}
			if (null != sendBoxList && !sendBoxList.isEmpty()) {
				// 恢复到发件箱
				maiSendBoxService.updateBatchById(sendBoxList);
			}
			// 删除废件箱中该邮件
			maiScrapBoxService.deleteBatchIds(scrapBoxIdList);
			
			retMsg.setCode(0);
			retMsg.setMessage("操作成功");
		} catch (Exception e) {
			retMsg.setCode(1);
			retMsg.setMessage(e.getMessage());
			  logger.error("",e);
		}
		return retMsg;
	}
}
