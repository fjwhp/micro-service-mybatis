package aljoin.act.iservice;

/**
 * 
 * (服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActHisTaskService{

    /**
     * 修改历史任务
     * @param taskId
     * @throws Exception
     */
    public void updateHisTask(String taskId,String currentUserId) throws Exception;
}
