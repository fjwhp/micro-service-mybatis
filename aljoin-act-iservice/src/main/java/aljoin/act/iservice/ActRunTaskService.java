package aljoin.act.iservice;

/**
 * 
 * (服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActRunTaskService {

    /**
     * 删除运行时任务
     * @param id
     * @throws Exception
     */
    public void deleteByTaskId(String id,String currentUserId) throws Exception;
} 
