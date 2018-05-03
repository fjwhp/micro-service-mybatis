package aljoin.act.service;

import aljoin.act.dao.mapper.ActRunIdentitylinkMapper;
import aljoin.act.iservice.ActRunIdentitylinkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 
 * @描述：(服务实现类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2017-12-12
 */
@Service
public class ActRunIdentitylinkServiceImpl implements ActRunIdentitylinkService {

  @Resource
  private ActRunIdentitylinkMapper mapper;

    @Override public void deleteByTaskId(String taskId) throws Exception {
        mapper.deleteByTaskId(taskId);
    }
}
