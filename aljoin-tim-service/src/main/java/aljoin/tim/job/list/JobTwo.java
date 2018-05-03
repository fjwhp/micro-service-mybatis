package aljoin.tim.job.list;

import java.util.Map;

import aljoin.tim.job.BaseJob;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class JobTwo implements BaseJob {

    @Override
    public void execute(Map<String, Object> map) {
        System.out.println("正在执行job-two");
        /*AutUserService autUserService = (AutUserService) SpringContextUtil.getBean("autUserServiceImpl");
        List<AutUser> list = autUserService.getUserList();
        System.out.println(list);*/
    }

}
