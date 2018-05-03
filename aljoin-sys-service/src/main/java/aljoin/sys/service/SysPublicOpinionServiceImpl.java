package aljoin.sys.service;

import aljoin.sys.dao.entity.SysPublicOpinion;
import aljoin.sys.dao.mapper.SysPublicOpinionMapper;
import aljoin.sys.dao.object.SysPublicOpinionVO;
import aljoin.sys.iservice.SysPublicOpinionService;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import javax.annotation.Resource;

import aljoin.object.RetMsg;

/**
 * 
 * @描述：公共意见表(服务实现类).
 * 
 * @作者：caizx
 * 
 * @时间: 2018-03-14
 */
@Service
public class SysPublicOpinionServiceImpl extends ServiceImpl<SysPublicOpinionMapper, SysPublicOpinion> implements SysPublicOpinionService {

  @Resource
  private SysPublicOpinionMapper mapper;

  @Override
  public void physicsDeleteById(Long id) throws Exception{
  	mapper.physicsDeleteById(id);
  }

  @Override
  public void copyObject(SysPublicOpinion obj) throws Exception{
  	mapper.copyObject(obj);
  }

  @Override
  @Transactional
  public RetMsg add(SysPublicOpinion obj) throws Exception {
    RetMsg retMsg = new RetMsg();
    if (null != obj) {
        if (null != obj.getContent()) {
            if (obj.getContent().length()>300) {
                retMsg.setCode(1);
                retMsg.setMessage("公共意见内容过长");
                return retMsg;
            }
            //默认激活
            obj.setIsActive(1);
            insert(obj);
        }
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  @Transactional
  public RetMsg update(SysPublicOpinion obj) {
    RetMsg retMsg = new RetMsg();
    if (null != obj && null != obj.getId()) {
        SysPublicOpinion orgnlObj = selectById(obj);
        if (null != obj.getContent()) {
            if (obj.getContent().length()>300) {
                retMsg.setCode(1);
                retMsg.setMessage("公共意见内容过长");
                return retMsg;
            }
            orgnlObj.setContent(obj.getContent());
        }
        if (null != obj.getContentRank()) {
            orgnlObj.setContentRank(obj.getContentRank());
        }
        updateById(orgnlObj);
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  @Transactional
  public RetMsg deleteByIds(SysPublicOpinionVO obj) {
    RetMsg retMsg = new RetMsg();
    String ids = obj.getIds();
    if (null != ids) {
        String[] arr = obj.getIds().split(";");
        deleteBatchIds(Arrays.asList(arr));
    } else {
        retMsg.setCode(1);
        retMsg.setMessage("未选择数据");
        return retMsg;
    }
    retMsg.setCode(0);
    retMsg.setMessage("操作成功");
    return retMsg;
  }

  @Override
  @Transactional
  public RetMsg delete(SysPublicOpinion obj) {
      RetMsg retMsg = new RetMsg();
      if (null != obj) {
         deleteById(obj.getId());
      }
      retMsg.setCode(0);
      retMsg.setMessage("操作成功");
      return retMsg;
  }
  
}
