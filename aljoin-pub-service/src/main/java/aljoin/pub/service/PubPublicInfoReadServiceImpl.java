package aljoin.pub.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.WebConstant;
import aljoin.pub.dao.entity.PubPublicInfoRead;
import aljoin.pub.dao.mapper.PubPublicInfoReadMapper;
import aljoin.pub.iservice.PubPublicInfoReadService;

/**
 * 
 * 公共信息阅读表(服务实现类).
 * 
 * @author：sln
 * 
 * @date： 2017-11-15
 */
@Service
public class PubPublicInfoReadServiceImpl extends ServiceImpl<PubPublicInfoReadMapper, PubPublicInfoRead>
		implements PubPublicInfoReadService {

	@Resource
	private PubPublicInfoReadMapper mapper;

	@Resource
	private AutDataStatisticsService autDataStatisticsService;
	
	@Override
	public Page<PubPublicInfoRead> list(PageBean pageBean, PubPublicInfoRead obj) throws Exception {
		Where<PubPublicInfoRead> where = new Where<PubPublicInfoRead>();
		where.orderBy("create_time", false);
		Page<PubPublicInfoRead> page = selectPage(
				new Page<PubPublicInfoRead>(pageBean.getPageNum(), pageBean.getPageSize()), where);
		return page;
	}

	@Override
	public void physicsDeleteById(Long id) throws Exception {
		mapper.physicsDeleteById(id);
	}

	@Override
	public void copyObject(PubPublicInfoRead obj) throws Exception {
		mapper.copyObject(obj);
	}

	@Override
	@Transactional
	public void insertList4pub(List<String> userIdList, Long infoId, List<String> userFullNameList) throws Exception {
		if (null != userIdList && null != infoId && !userIdList.isEmpty()) {
			List<PubPublicInfoRead> publicInfoReads = new ArrayList<PubPublicInfoRead>();
			for (int i = 0; i < userIdList.size(); i++) {
				PubPublicInfoRead pubread = new PubPublicInfoRead();
				pubread.setInfoId(infoId);
				pubread.setIsRead(0);
				pubread.setReadCount(0);
				pubread.setReadUserId(Long.valueOf(userIdList.get(i)));
				pubread.setReadUserFullName(userFullNameList.get(i));
				publicInfoReads.add(pubread);
			}
			insertBatch(publicInfoReads);
			//公告信息的已读未读+首页数据统计 都在这里维护
			AutDataStatistics aut = new AutDataStatistics();
			aut.setObjectKey(WebConstant.AUTDATA_PUBNOTICE_CODE);
			aut.setObjectName(WebConstant.AUTDATA_PUBNOTICE_NAME);
			autDataStatisticsService.addOrUpdateList(aut, userIdList);
		}
	}
	
	@Override
	@Transactional
	public void update4User(Long userId, Long objId) throws Exception{
		if(null != userId && null != objId){
			Where<PubPublicInfoRead> where = new Where<PubPublicInfoRead>();
			where.eq("info_id", objId);
			where.eq("read_user_id", userId);
			PubPublicInfoRead publicInfoRead = selectOne(where);
			if(publicInfoRead != null){
				Integer isRead = publicInfoRead.getIsRead();
				if(null != publicInfoRead){
					publicInfoRead.setReadTime(new Date());
					publicInfoRead.setIsRead(1);
					publicInfoRead.setReadCount(publicInfoRead.getReadCount()+1);
					updateById(publicInfoRead);
				}
				if(isRead == 0){//从未读变成已读
					AutDataStatistics autData = new AutDataStatistics();
					autData.setObjectKey(WebConstant.AUTDATA_PUBNOTICE_CODE);
					autData.setObjectName(WebConstant.AUTDATA_PUBNOTICE_NAME);
					autData.setBusinessKey(String.valueOf(userId));
					autData.setObjectCount(1);
					autDataStatisticsService.minus(autData);
				}
			}
		}
	}
	
	@Override
	public void deletePubRead(Long objId) throws Exception{
		if(null != objId){
			Where<PubPublicInfoRead> whereRead = new Where<PubPublicInfoRead>();
			whereRead.eq("info_id", objId);
			List<PubPublicInfoRead> readList = selectList(whereRead);
			List<String> userIds = new ArrayList<String>();
			List<Long> readIds = new ArrayList<Long>();
			if(readList!=null && !readList.isEmpty()){
				for(PubPublicInfoRead pubread : readList){
					readIds.add(pubread.getId());
					if(pubread.getIsRead() == 0){
						userIds.add(String.valueOf(pubread.getReadUserId()));
					}
				}
				deleteBatchIds(readIds);
			}
			if(!userIds.isEmpty()){
				AutDataStatistics autData = new AutDataStatistics();
				autData.setObjectKey(WebConstant.AUTDATA_PUBNOTICE_CODE);
				autDataStatisticsService.minusList(autData, userIds);
			}
		}
	}
}
