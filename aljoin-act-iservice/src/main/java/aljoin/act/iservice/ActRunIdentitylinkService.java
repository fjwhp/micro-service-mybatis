package aljoin.act.iservice;

/**
 * 
 * @描述：(服务类).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2017-12-12
 */
public interface ActRunIdentitylinkService {
  /**
   * 删除identity关联的任务ID
   * @param taskId
   * @throws Exception
   */
  public void deleteByTaskId(String taskId)throws  Exception;
}
