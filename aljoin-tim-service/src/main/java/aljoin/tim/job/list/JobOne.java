package aljoin.tim.job.list;

import java.util.Map;

import aljoin.tim.job.BaseJob;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class JobOne implements BaseJob {

    @Override
    public void execute(Map<String, Object> map) {
        System.out.println("正在执行job-one");
    }

}
