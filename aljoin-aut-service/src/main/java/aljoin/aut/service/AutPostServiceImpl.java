package aljoin.aut.service;

import aljoin.aut.dao.entity.AutPost;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPost;
import aljoin.aut.dao.mapper.AutPostMapper;
import aljoin.aut.dao.object.AutPostVO;
import aljoin.aut.iservice.AutPostService;
import aljoin.aut.iservice.AutUserPostService;
import aljoin.aut.iservice.AutUserService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * @描述：岗位表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
@Service
public class AutPostServiceImpl extends ServiceImpl<AutPostMapper, AutPost> implements AutPostService {

  @Resource
  private AutPostMapper mapper;
  @Resource
  private AutUserPostService autUserPostService;
  @Resource
  private AutUserService autUserService;

  @Override
  public Page<AutPost> list(PageBean pageBean, AutPost obj) throws Exception {
	Where<AutPost> where = new Where<AutPost>();
	where.orderBy("create_time", false);
	Page<AutPost> page = selectPage(new Page<AutPost>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	
  
  @Override	
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(AutPost obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  public List<AutPostVO> getUserPostList() {
     List<AutPostVO> list = new ArrayList<AutPostVO>();
     Where<AutPost> where = new Where<AutPost>();
     where.orderBy("post_rank,id",true);
     List<AutPost> postList = selectList(where);
     ArrayList<Long> postIds = new ArrayList<Long>();
     if (null != postList && postList.size() > 0) {
         for (AutPost autPost : postList) {
             postIds.add(autPost.getId());
         }
     }
     List<Long> autUserIds = new ArrayList<Long>();
     List<AutUserPost> userPostList = null;
     if (null != postIds && postIds.size() > 0) {
         Where<AutUserPost> userPostWhere = new Where<AutUserPost>();
         userPostWhere.in("post_id", postIds);
         userPostList = autUserPostService.selectList(userPostWhere);
         for (AutUserPost autUserPost : userPostList) {
             autUserIds.add(autUserPost.getUserId());
         }
     }
     List<AutUser> userList = null;
     if (null != autUserIds && autUserIds.size() > 0) {
         Where<AutUser> userWhere = new Where<AutUser>();
         userWhere.eq("is_active", 1);
         userWhere.in("id", autUserIds);
         userList = autUserService.selectList(userWhere);
     }
     for (AutPost autPost : postList) {
        AutPostVO autPostVO = new AutPostVO();
        List<AutUser> users = new ArrayList<AutUser>();
        autPostVO.setAutPost(autPost);
        if (null != userPostList && userPostList.size() > 0) {
            for (AutUserPost autUserPost : userPostList) {
                if (autPost.getId().equals(autUserPost.getPostId())) {
                    for (AutUser autUser : userList) {
                        if (autUser.getId().equals(autUserPost.getUserId())) {
                            users.add(autUser);
                        }
                    }
                }
            }
        }
        autPostVO.setUserList(users);
        list.add(autPostVO);
     }
     return list;
  }

  @Override
  @Transactional
  public RetMsg delete(AutPost obj) {
     RetMsg retMsg = new RetMsg();
     if (null != obj) {
         Where<AutUserPost> userPostWhere = new Where<AutUserPost>();
         userPostWhere.eq("post_id", obj.getId());
         List<AutUserPost> list = autUserPostService.selectList(userPostWhere);
         if (null != list && list.size() > 0) {
             retMsg.setCode(1);
             retMsg.setMessage("未移除岗位下的用户");
             return retMsg;
         }
         deleteById(obj);
     }
     retMsg.setCode(0);
     retMsg.setMessage("操作成功");
     return null;
  }
}
