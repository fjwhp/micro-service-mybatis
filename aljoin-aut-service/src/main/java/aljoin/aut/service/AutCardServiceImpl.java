package aljoin.aut.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutCard;
import aljoin.aut.dao.entity.AutCardCategory;
import aljoin.aut.dao.mapper.AutCardMapper;
import aljoin.aut.dao.object.AutCardVO;
import aljoin.aut.iservice.AutCardCategoryService;
import aljoin.aut.iservice.AutCardService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

/**
 * 
 * 名片表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-10-10
 */
@Service
public class AutCardServiceImpl extends ServiceImpl<AutCardMapper, AutCard> implements AutCardService {

  @Resource
  private AutCardMapper mapper;
  @Resource
  private AutCardCategoryService autCardCategoryService;

  @Override
  public Page<AutCardVO> list(PageBean pageBean, AutCard obj,String userId)throws Exception {
	Where<AutCard> where = new Where<AutCard>();
	//搜索条件：姓名、手机号
	if(StringUtils.isNotEmpty(obj.getUserName())){
		where.like("user_name", obj.getUserName());
		where.or("phone_number LIKE {0}","%" + obj.getPhoneNumber() + "%");
		where.and();
	}
	if(StringUtils.checkValNotNull(obj.getCategoryId())){
		where.eq("category_id", obj.getCategoryId());
	}
	where.eq("create_user_id",userId);
	where.orderBy("create_time", true);
	Page<AutCard> page = selectPage(new Page<AutCard>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	
	List<AutCard> autCardList = page.getRecords();
	
	List<Long> cardCategoryIdList = new ArrayList<Long>();
	for(AutCard autCard : autCardList){
		cardCategoryIdList.add(autCard.getCategoryId());
	}
	
	List<AutCardCategory> cardCategoryList = new ArrayList<AutCardCategory>();
	if(null != cardCategoryIdList && !cardCategoryIdList.isEmpty()){
		cardCategoryList =  autCardCategoryService.selectBatchIds(cardCategoryIdList);
	}
	
	// 拼接名片+名片分类实体类
	List<AutCardVO> autCardVOList = new ArrayList<AutCardVO>();
	for(AutCard autCard : autCardList){
		for(AutCardCategory autCardCategory : cardCategoryList){
			if(autCard.getCategoryId() == autCardCategory.getId() || 
					autCard.getCategoryId().equals(autCardCategory.getId())){
				AutCardVO autCardVO = new AutCardVO();
				autCardVO.setAutCard(autCard);
				autCardVO.setAutCardCategory(autCardCategory);
				autCardVOList.add(autCardVO);
			}
		}
	}
	
	Page<AutCardVO> pageList = new Page<AutCardVO>();
	pageList.setCurrent(page.getCurrent());
	pageList.setSize(page.getSize());
	pageList.setTotal(page.getTotal());
	pageList.setRecords(autCardVOList);
	return pageList;
  }	

	@Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


	@Override
  public void copyObject(AutCard obj) throws Exception{
  	mapper.copyObject(obj);
  }

@Override
public AutCardVO getById(AutCard obj) throws Exception {
	
	AutCardVO autCardVO = new AutCardVO();
	
	Long cardId = obj.getId();
	AutCard autCard = selectById(cardId);
	AutCardCategory cardCategory = autCardCategoryService.selectById(autCard.getCategoryId());
	
	autCardVO.setAutCard(autCard);
	autCardVO.setAutCardCategory(cardCategory);
	
	return autCardVO;
}
}
