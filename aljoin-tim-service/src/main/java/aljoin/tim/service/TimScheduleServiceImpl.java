package aljoin.tim.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinDelegate;
import aljoin.act.iservice.ActAljoinDelegateService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.tim.dao.entity.TimSchedule;
import aljoin.tim.dao.mapper.TimScheduleMapper;
import aljoin.tim.iservice.JobScheduleService;
import aljoin.tim.iservice.TimScheduleService;

/**
 * 
 * 任务调度表(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-26
 */
@Service
public class TimScheduleServiceImpl extends ServiceImpl<TimScheduleMapper, TimSchedule> implements TimScheduleService {

    @Resource
    private TimScheduleMapper mapper;
    @Resource
    private JobScheduleService jobScheduleService;
    @Resource
    private ActAljoinDelegateService actAljoinDelegateService;

    @Override
    public Page<TimSchedule> list(PageBean pageBean, TimSchedule obj) throws Exception {
        Where<TimSchedule> where = new Where<TimSchedule>();
        where.orderBy("create_time", false);
        Page<TimSchedule> page =
            selectPage(new Page<TimSchedule>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(TimSchedule obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public void autoStart() throws Exception {
        Where<TimSchedule> where = new Where<TimSchedule>();
        where.eq("is_auto", 1);
        where.eq("is_active", 1);
        where.setSqlSelect("id,cron_expression,job_name,exe_class_name,is_auto,biz_type,biz_id");
        List<TimSchedule> timScheduleList = selectList(where);
        // 普通定时器和委托定时器分开处理
        List<TimSchedule> generalScheduleList = new ArrayList<TimSchedule>();
        List<TimSchedule> delegateScheduleList = new ArrayList<TimSchedule>();
        for (TimSchedule timSchedule : timScheduleList) {
            if ("act_aljoin_delegate".equals(timSchedule.getBizType())) {
                // 委托定时器
                delegateScheduleList.add(timSchedule);
            } else {
                // 普通定时器
                generalScheduleList.add(timSchedule);
            }
        }
        // 执行普通定时器
        jobScheduleService.doJob(generalScheduleList, null);
        // 对于委托任务的定时器需要做特殊处理
        for (TimSchedule timSchedule : delegateScheduleList) {
            List<TimSchedule> exeScheduleList = new ArrayList<TimSchedule>();
            exeScheduleList.add(timSchedule);
            ActAljoinDelegate actAljoinDelegate = actAljoinDelegateService.selectById(timSchedule.getBizId());
            if (actAljoinDelegate != null) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("actAljoinDelegate", actAljoinDelegate);
                map.put("timSchedule", timSchedule);
                jobScheduleService.doJob(exeScheduleList, map);
            }
        }

    }
}
