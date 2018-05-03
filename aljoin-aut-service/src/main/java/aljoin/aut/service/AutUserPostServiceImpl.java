package aljoin.aut.service;

import aljoin.aut.dao.entity.AutUserPost;
import aljoin.aut.dao.mapper.AutUserPostMapper;
import aljoin.aut.dao.object.AutUserPostVO;
import aljoin.aut.iservice.AutUserPostService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.apache.commons.lang3.StringUtils;
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
 * @描述：用户-岗位表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-04-09
 */
@Service
public class AutUserPostServiceImpl extends ServiceImpl<AutUserPostMapper, AutUserPost> implements AutUserPostService {

  @Resource
  private AutUserPostMapper mapper;

  @Override
  public Page<AutUserPost> list(PageBean pageBean, AutUserPost obj) throws Exception {
	Where<AutUserPost> where = new Where<AutUserPost>();
	where.orderBy("create_time", false);
	Page<AutUserPost> page = selectPage(new Page<AutUserPost>(pageBean.getPageNum(), pageBean.getPageSize()), where);
	return page;
  }	
	
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }


  public void copyObject(AutUserPost obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  @Transactional
  public RetMsg add(AutUserPostVO obj) {
     RetMsg retMsg = new RetMsg();
     ArrayList<AutUserPost> list = new ArrayList<AutUserPost>();
     if (null != obj) {
        if (null != obj.getPostId()) {
            Where<AutUserPost> where = new Where<AutUserPost>();
            where.eq("post_id",obj.getPostId());
            List<AutUserPost> autUserPosts = selectList(where);
            if (autUserPosts != null) {
              //删除原来的绑定信息
              delete(where);
           }
        }
        if (StringUtils.isNotEmpty(obj.getUserIds())) {
            String[] userIds = obj.getUserIds().split(";");
            for (String userId : userIds) {
                AutUserPost autUserPost = new AutUserPost();
                autUserPost.setPostId(obj.getPostId());
                autUserPost.setUserId(Long.parseLong(userId));
                autUserPost.setIsActive(1);
                list.add(autUserPost);
            }
        }
    }
     insertBatch(list);
     retMsg.setCode(0);
     retMsg.setMessage("操作成功");
     return retMsg;
  }

}
