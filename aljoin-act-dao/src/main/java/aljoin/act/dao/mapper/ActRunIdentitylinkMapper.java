package aljoin.act.dao.mapper;

/**
 * 
 * @描述：(Mapper接口).
 * 
 * @作者：zhongjy
 * 
 * @时间: 2017-12-12
 */
public interface ActRunIdentitylinkMapper {
  /**
   * 删除identity关联的任务ID
   * @param taskId
   * @throws Exception
   */
  public void deleteByTaskId(String taskId)throws  Exception;
}
