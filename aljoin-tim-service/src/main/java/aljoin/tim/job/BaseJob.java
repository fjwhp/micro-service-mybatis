package aljoin.tim.job;

import java.util.Map;

/**
 * 
 * 所有具体执行的job需要继承本类
 *
 * @author：zhongjy
 * 
 * @date：2017年10月26日 上午9:56:47
 */
public interface BaseJob {
    /**
     * 
     * 重写本方法实现业务逻辑
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月26日 上午9:56:53
     */
    public void execute(Map<String, Object> map);
}
