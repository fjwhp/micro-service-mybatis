package aljoin.aut.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutDataDetail;
import aljoin.aut.dao.entity.AutDataStatistics;
import aljoin.aut.dao.mapper.AutDataStatisticsMapper;
import aljoin.aut.iservice.AutDataDetailService;
import aljoin.aut.iservice.AutDataStatisticsService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.WebConstant;

/**
 * 
 * 数据统计表(服务实现类).
 * 
 * @author：sln.
 * 
 * @date： 2017-11-09
 */
@Service
public class AutDataStatisticsServiceImpl extends ServiceImpl<AutDataStatisticsMapper, AutDataStatistics>
    implements AutDataStatisticsService {
    // 数据表维护策略：
    // 1.用户调用这个类里面的update方法时，其实是往detail表里加数据
    // 2.定时器以每10秒一次的频率，把detail表里的数据，往aut_data_statistics表里插

    @Resource
    private AutDataStatisticsMapper mapper;
    @Resource
    private AutDataDetailService autDataDetailService;

    @Override
    public Page<AutDataStatistics> list(PageBean pageBean, AutDataStatistics obj) throws Exception {
        Where<AutDataStatistics> where = new Where<AutDataStatistics>();
        where.orderBy("create_time", false);
        Page<AutDataStatistics> page =
            selectPage(new Page<AutDataStatistics>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutDataStatistics obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public List<AutDataStatistics> getUserCount(AutDataStatistics obj) throws Exception {
        List<AutDataStatistics> list = new ArrayList<AutDataStatistics>();
        if (null != obj && null != obj.getBusinessKey()) {
            Where<AutDataStatistics> where = new Where<AutDataStatistics>();
            where.setSqlSelect("create_user_id,object_name,object_key,object_count,business_key");
            where.eq("business_key", obj.getBusinessKey());
            list = selectList(where);
        }
        return list;
    }

    @Override
    public Integer getUserMsgCount(AutDataStatistics obj) throws Exception {
        Integer msgCount = 0;
        AutDataStatistics autDataStatistics = new AutDataStatistics();
        if (null != obj && null != obj.getBusinessKey()) {
            Where<AutDataStatistics> where = new Where<AutDataStatistics>();
            where.setSqlSelect("object_name,object_key,object_count,business_key");
            where.eq("object_key", WebConstant.AUTDATA_ONLINE_CODE);
            where.eq("business_key", obj.getBusinessKey());
            where.orderBy("id");
            List<AutDataStatistics> datalist = selectList(where);
            if (datalist != null && !datalist.isEmpty()) {
                autDataStatistics = datalist.get(0);
                if (null != autDataStatistics) {
                    msgCount = autDataStatistics.getObjectCount();
                }
            }
        }
        return msgCount;
    }

    @Override
    public void addOrUpdate(AutDataStatistics obj) throws Exception {
        // AutDataDetail autDataDetail = new AutDataDetail();
        // Long id = IdWorker.getId();
        // autDataDetail.setId(id);
        // autDataDetail.setUserId(Long.valueOf(obj.getBusinessKey()));
        // autDataDetail.setDataType(obj.getObjectKey());
        // if (null != obj.getObjectCount()) {
        // autDataDetail.setDataCount(obj.getObjectCount());
        // } else {
        // autDataDetail.setDataCount(1);
        // }
        // autDataDetailService.insert(autDataDetail);
    }

    @Override
    public void minus(AutDataStatistics obj) throws Exception {
        // AutDataDetail autDataDetail = new AutDataDetail();
        // Long id = IdWorker.getId();
        // autDataDetail.setId(id);
        // autDataDetail.setUserId(Long.valueOf(obj.getBusinessKey()));
        // autDataDetail.setDataType(obj.getObjectKey());
        // Integer count = obj.getObjectCount();
        // if (null != count) {
        // count = 0 - Math.abs(count);
        // autDataDetail.setDataCount(count); // 给值的时候一般给正数，放到表里要变成负的
        // } else {
        // autDataDetail.setDataCount(-1);
        // }
        // autDataDetailService.insert(autDataDetail);
    }

    @Override
    public void addOrUpdateList(AutDataStatistics obj, List<String> userIds) throws Exception {
        // Integer count = 1;
        // if (null != obj.getObjectCount()) {
        // count = obj.getObjectCount();
        // }
        // if (!userIds.isEmpty()) {
        // List<AutDataDetail> dataList = new ArrayList<AutDataDetail>();
        // for (String userId : userIds) {
        // AutDataDetail autDataDetail = new AutDataDetail();
        // Long id = IdWorker.getId();
        // autDataDetail.setId(id);
        // autDataDetail.setUserId(Long.valueOf(userId));
        // autDataDetail.setDataType(obj.getObjectKey());
        // autDataDetail.setDataCount(count);
        // dataList.add(autDataDetail);
        // }
        // autDataDetailService.insertBatch(dataList);
        // }
    }

    @Override
    public void minusList(AutDataStatistics obj, List<String> userIds) throws Exception {
        // Integer count = -1;
        // if (null != obj.getObjectCount()) {
        // count = 0 - Math.abs(obj.getObjectCount());
        // }
        // if (!userIds.isEmpty()) {
        // List<AutDataDetail> dataList = new ArrayList<AutDataDetail>();
        // for (String userId : userIds) {
        // Long id = IdWorker.getId();
        // AutDataDetail autDataDetail = new AutDataDetail();
        // autDataDetail.setId(id);
        // autDataDetail.setUserId(Long.valueOf(userId));
        // autDataDetail.setDataType(obj.getObjectKey());
        // autDataDetail.setDataCount(count);
        // dataList.add(autDataDetail);
        // }
        // autDataDetailService.insertBatch(dataList);
        // }
    }

    @Override
    public void pushData2Statistics() throws Exception {
        // 定时从detail表里读数据往statistics表里插
        // 1.每次从表里取1000条数据(1000也直接写在mapper文件里面了)
        List<AutDataDetail> detailList = new ArrayList<AutDataDetail>();
        detailList = autDataDetailService.selectList();
        List<String> userIds = new ArrayList<String>();
        List<Long> detailIds = new ArrayList<Long>();
        List<String> types = new ArrayList<String>();

        // 2.构造出这一批数据里需要更新的数据
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        if (!detailList.isEmpty()) {
            for (AutDataDetail autDataDetail : detailList) {
                detailIds.add(autDataDetail.getId());
                if (userIds.indexOf(String.valueOf(autDataDetail.getUserId())) < 0) {
                    userIds.add(String.valueOf(autDataDetail.getUserId()));// 把userId加到用户id列表中
                }
                if (types.indexOf(autDataDetail.getDataType()) < 0) {
                    types.add(autDataDetail.getDataType());// 把type加到用户type列表中
                }
                // 以用户id|type作为键值
                String key = autDataDetail.getUserId() + "|" + autDataDetail.getDataType();
                Integer count = map.get(key);
                if (count == null) {
                    map.put(key, autDataDetail.getDataCount());
                } else {
                    map.put(key, autDataDetail.getDataCount() + count);
                }
            }
            List<AutDataStatistics> statisticsList = new ArrayList<AutDataStatistics>();
            List<AutDataStatistics> updateStatisticsList = new ArrayList<AutDataStatistics>();// 更新数据
            List<AutDataStatistics> insertStatisticsList = new ArrayList<AutDataStatistics>();// 新增数据

            Where<AutDataStatistics> datawhere = new Where<AutDataStatistics>();
            datawhere.in("object_key", types);
            datawhere.in("business_key", userIds);
            statisticsList = selectList(datawhere);
            for (AutDataStatistics autDataStatistics : statisticsList) {
                String key = autDataStatistics.getBusinessKey() + "|" + autDataStatistics.getObjectKey();
                Integer count = map.get(key);
                if (count != null && count != 0) {
                    autDataStatistics.setObjectCount(autDataStatistics.getObjectCount() + count);
                    updateStatisticsList.add(autDataStatistics);
                    map.remove(key);
                }
            }
            for (String key : map.keySet()) {
                Integer count = map.get(key);
                if (count != null && count != 0) {
                    AutDataStatistics data = new AutDataStatistics();
                    data.setBusinessKey(key.substring(0, key.indexOf("|")));
                    data.setObjectKey(key.substring(key.indexOf("|") + 1, key.length()));
                    if (data.getObjectKey().equals(WebConstant.AUTDATA_ONLINE_CODE)) {
                        data.setObjectName(WebConstant.AUTDATA_ONLINE_NAME);
                    } else if (data.getObjectKey().equals(WebConstant.AUTDATA_PUBNOTICE_CODE)) {
                        data.setObjectName(WebConstant.AUTDATA_PUBNOTICE_NAME);
                    } else if (data.getObjectKey().equals(WebConstant.AUTDATA_TODOLIST_CODE)) {
                        data.setObjectName(WebConstant.AUTDATA_TODOLIST_NAME);
                    } else {
                        data.setObjectName(WebConstant.AUTDATA_TOREAD_NAME);
                    }
                    data.setIsActive(1);
                    data.setObjectCount(count);
                    // insertStatisticsList.add(data);
                }
            }
            // 该更新的更新，该新增的新增
            if (!updateStatisticsList.isEmpty()) {
                updateBatchById(updateStatisticsList, updateStatisticsList.size());
            }
            if (!insertStatisticsList.isEmpty()) {
                insertBatch(insertStatisticsList, insertStatisticsList.size());
            }
            // 3.数据更新以后，把这些数据删掉
            // autDataDetailService.physicsDeleteBatchById(detailIds);
        }
    }
}
