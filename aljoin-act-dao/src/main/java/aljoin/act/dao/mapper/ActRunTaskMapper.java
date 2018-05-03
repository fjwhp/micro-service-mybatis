package aljoin.act.dao.mapper;

/**
 * 
 * (Mapper接口).
 * 
 * @author：wangj.
 * 
 * @date： 2017-12-17
 */
public interface ActRunTaskMapper{
    /**
     * 删除运行时任务
     * @param id 任务Id
     * @throws Exception
     */
    public void deleteByTaskId(String id) throws Exception;
}