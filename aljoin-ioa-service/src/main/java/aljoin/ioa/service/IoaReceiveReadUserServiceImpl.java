package aljoin.ioa.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.dao.config.Where;
import aljoin.ioa.dao.entity.IoaReceiveFile;
import aljoin.ioa.dao.entity.IoaReceiveReadObject;
import aljoin.ioa.dao.entity.IoaReceiveReadUser;
import aljoin.ioa.dao.mapper.IoaReceiveReadUserMapper;
import aljoin.ioa.dao.object.IoaReceiveReadUserDO;
import aljoin.ioa.iservice.IoaReceiveFileService;
import aljoin.ioa.iservice.IoaReceiveReadObjectService;
import aljoin.ioa.iservice.IoaReceiveReadUserService;
import aljoin.object.PageBean;
import aljoin.object.WebConstant;

/**
 * 
 * 收文阅件-用户评论(服务实现类).
 * 
 * @author：zhongjy
 * 
 * @date： 2017-11-08
 */
@Service
public class IoaReceiveReadUserServiceImpl extends
    ServiceImpl<IoaReceiveReadUserMapper, IoaReceiveReadUser> implements IoaReceiveReadUserService {

  @Resource
  private IoaReceiveReadUserMapper mapper;
  @Resource
  private IoaReceiveFileService ioaReceiveFileService;
  @Resource
  private IoaReceiveReadObjectService ioaReceiveReadObjectService;
  @Resource
  private AutUserRankService autUserRankService;

  @Override
  public Page<IoaReceiveReadUserDO> list(PageBean pageBean, IoaReceiveReadUser obj)
      throws Exception {
    Page<IoaReceiveReadUserDO> page = new Page<IoaReceiveReadUserDO>();
    // 原文件id
    Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
    filewhere.eq("process_instance_id", obj.getId());
    filewhere.setSqlSelect("id");
    IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
    // 查出所有传阅者
    Where<IoaReceiveReadObject> readObjectWhere = new Where<IoaReceiveReadObject>();
    readObjectWhere.setSqlSelect("id,read_user_ids");
    readObjectWhere.eq("receive_file_id", file.getId());
    List<IoaReceiveReadObject> objectList = ioaReceiveReadObjectService.selectList(readObjectWhere);
    Set<Long> readUserIdList = new HashSet<Long>();
    Boolean isAll = false;
    List<Long> readObjectIds = new ArrayList<Long>();
    for (IoaReceiveReadObject ioaReceiveReadObject : objectList) {
      readObjectIds.add(ioaReceiveReadObject.getId());
      // 如果该文件有一个传阅对象是全局，其他分组内容都不要了，就算再取也是重复的
      if (ioaReceiveReadObject.getReadUserIds().equals(WebConstant.DICT_READ_OBJECT_ALL)) {
        isAll = true;
        break;
      }
      if(ioaReceiveReadObject.getReadUserIds()!=null && !"".equals(ioaReceiveReadObject.getReadUserIds())){
      List<String> userIds = Arrays.asList(ioaReceiveReadObject.getReadUserIds().split(";"));     
      if(userIds!=null && userIds.size()>0){
      for (String userId : userIds) {
        readUserIdList.add(Long.valueOf(userId));
      }
      }
      }
    }
    // 传阅对象是全局，不得不去表里去取全局人员（不能直接去userRank里取所有人，因为查数据这个时候和文件传阅时的全局人员有可能不一样）
    if (isAll) {
      readUserIdList.clear();
      Where<IoaReceiveReadUser> where = new Where<IoaReceiveReadUser>();
      where.setSqlSelect("read_user_id");
      where.eq("receive_read_object_id", readObjectIds.get(0));
      where.eq("receive_file_id", file.getId());
      List<IoaReceiveReadUser> users = selectList(where);
      for (IoaReceiveReadUser ioaReceiveReadUser : users) {
        readUserIdList.add(ioaReceiveReadUser.getReadUserId());
      }
    }
    // 一般不会有传阅者为空的情况，还是判断一下
    if (readUserIdList.size() == 0) {
      return page;
    }
    // 传阅者排序-->就算user在不同的分类中选择了同一个传阅者，那个传阅者也只有一条记录
    Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
    autUserRankWhere.setSqlSelect("id,user_rank");
    autUserRankWhere.in("id", readUserIdList);
    autUserRankWhere.orderBy("user_rank", true);
    // 把对传阅情况的分页转换成对user的分页
    Page<AutUserRank> oldPpage = autUserRankService.selectPage(
        new Page<AutUserRank>(pageBean.getPageNum(), pageBean.getPageSize()), autUserRankWhere);
    List<AutUserRank> userRankList = oldPpage.getRecords();
    List<Long> readUserIds = new ArrayList<Long>();
    for (AutUserRank autUserRank : userRankList) {
      readUserIds.add(autUserRank.getId());
    }

    // 用原文件id查出这份文件的传阅情况
    Where<IoaReceiveReadUser> where = new Where<IoaReceiveReadUser>();
    where.setSqlSelect(
        "id,create_time,receive_read_object_id,receive_file_id,read_user_id,read_user_full_name,read_dept_id,read_dept_name,read_time,read_opinion,is_read");
    if (readObjectIds.size() > 1) {
      where.in("receive_read_object_id", readObjectIds);
    } else {
      where.eq("receive_read_object_id", readObjectIds.get(0));
    }
    where.eq("receive_file_id", file.getId());
    where.in("read_user_id", readUserIds);
    // Page<IoaReceiveReadUser> oldPpage = selectPage(
    // new Page<IoaReceiveReadUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
    // List<IoaReceiveReadUser> readUserList = oldPpage.getRecords();

    List<IoaReceiveReadUser> readUserList = selectList(where);
    List<IoaReceiveReadUserDO> readUserDOList = new ArrayList<IoaReceiveReadUserDO>();
    for (Long readUserId : readUserIds) {
      for (IoaReceiveReadUser readUser : readUserList) {
        if (readUserId.equals(readUser.getReadUserId())) {
          // 在set中找到readUserId才进入
          if(readUserIdList.contains(readUserId)){
            IoaReceiveReadUserDO readUserDO = new IoaReceiveReadUserDO();
            readUserDO.setOperationName(readUser.getReadUserFullName());
            readUserDO.setDeptName(readUser.getReadDeptName());
            readUserDO.setReadOpinion(readUser.getReadOpinion());
            if (null != readUser.getReadTime()) {
              DateTime dateTime = new DateTime(readUser.getReadTime());
              readUserDO.setReadTimeStr(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
            } else {
              readUserDO.setReadTimeStr("");
            }
            readUserDOList.add(readUserDO);
            // 对于一个readUserId只有一次add的机会，add以后就从队列里remove掉
            readUserIdList.remove(readUserId);
          }
        }
      }
    }
    page.setCurrent(oldPpage.getCurrent());
    page.setRecords(readUserDOList);
    page.setTotal(oldPpage.getTotal());
    page.setSize(oldPpage.getSize());
    return page;
  }

  @Override
  public void physicsDeleteById(Long id) throws Exception {
    mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(IoaReceiveReadUser obj) throws Exception {
    mapper.copyObject(obj);
  }

  @Override
  public IoaReceiveReadUserDO circulation(String processInstanceId) throws Exception {
    IoaReceiveReadUserDO readUserDO = null;
    Where<IoaReceiveFile> filewhere = new Where<IoaReceiveFile>();
    filewhere.eq("process_instance_id", processInstanceId);
    filewhere.setSqlSelect("id");
    IoaReceiveFile file = ioaReceiveFileService.selectOne(filewhere);
    if (null != file && null != file.getId()) {
      List<Long> readObjectIds = new ArrayList<Long>();
      Where<IoaReceiveReadObject> readObjectWhere = new Where<IoaReceiveReadObject>();
      readObjectWhere.setSqlSelect("id,read_user_ids");
      readObjectWhere.eq("receive_file_id", file.getId());
      // 去readObject里拿出所有传阅者的id
      List<IoaReceiveReadObject> objectList = ioaReceiveReadObjectService.selectList(readObjectWhere);
      for (IoaReceiveReadObject ioaReceiveReadObject : objectList) {
        // 如果该文件有一个传阅对象是全局，其他分组内容都不要了，就算再取也是重复的
        if(ioaReceiveReadObject.getReadUserIds().equals(WebConstant.DICT_READ_OBJECT_ALL)){
          readObjectIds.clear();
          readObjectIds.add(ioaReceiveReadObject.getId());
          break;
        }
        readObjectIds.add(ioaReceiveReadObject.getId());
      }
      if(readObjectIds.size() !=0){
        Where<IoaReceiveReadUser> where = new Where<IoaReceiveReadUser>();
        where.setSqlSelect(
            "id,receive_file_id,read_user_id,read_user_full_name,is_read");
        where.eq("receive_file_id", file.getId());
        if(readObjectIds.size()>1){
          where.in("receive_read_object_id", readObjectIds);
        }else{
          // 传阅对象是全局时 size = 1
          where.eq("receive_read_object_id", readObjectIds.get(0));
        }
      List<IoaReceiveReadUser> readUserList = selectList(where);
      Set<Long> readUserIdList = new HashSet<Long>();
      for (IoaReceiveReadUser readUser : readUserList) {
        readUserIdList.add(readUser.getReadUserId());
      }
      
      // 传阅者排序
      Where<AutUserRank> autUserRankWhere = new Where<AutUserRank>();
      autUserRankWhere.setSqlSelect("id,user_rank");
      autUserRankWhere.in("id", readUserIdList);
      autUserRankWhere.orderBy("user_rank", true);
      List<AutUserRank> userRankList = autUserRankService.selectList(autUserRankWhere);
      
      String inReadIds = "";
      String inReadNames = "";
      String toReadIds = "";
      String toReadNames = "";
      // 构造出已读未读数据
      for (AutUserRank autUserRank : userRankList) {
        for (IoaReceiveReadUser readUser : readUserList) {
          if (readUser.getReadUserId().equals(autUserRank.getId())) {
            if(readUserIdList.contains(autUserRank.getId())){
              if (readUser.getIsRead() == 0) {
                toReadIds += readUser.getReadUserId() + ";";
                toReadNames += readUser.getReadUserFullName() + "；";
              } else {
                inReadIds += readUser.getReadUserId() + ";";
                inReadNames += readUser.getReadUserFullName() + "；";
              }
            }
            // 对于一个readUserId只有一次add的机会，add以后就从队列里remove掉
            readUserIdList.remove(autUserRank.getId());
          }
        }
      }
      
      readUserDO = new IoaReceiveReadUserDO();
      readUserDO.setInReadIds(inReadIds);
      readUserDO.setInReadNames(inReadNames);
      readUserDO.setToReadIds(toReadIds);
      readUserDO.setToReadNames(toReadNames);
      }
    }
    return readUserDO;
  }
}
