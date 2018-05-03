package aljoin.aut.service;

import aljoin.aut.dao.entity.AutUserInfo;
import aljoin.aut.dao.mapper.AutUserInfoMapper;
import aljoin.aut.dao.object.AutUserInfoVO;
import aljoin.aut.iservice.AutUserInfoService;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 用户信息表(服务实现类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-06
 */
@Service
public class AutUserInfoServiceImpl extends ServiceImpl<AutUserInfoMapper, AutUserInfo> implements AutUserInfoService {

    @Override
    public Page<AutUserInfo> list(PageBean pageBean, AutUserInfo obj) throws Exception {
        Where<AutUserInfo> where = new Where<AutUserInfo>();
        if (null != obj.getUserKey()) {
            where.like("user_key", obj.getUserKey());
        }
        if (null != obj.getUserValue()) {
            where.like("user_value", obj.getUserValue());
        }
        if (null != obj.getIsActive()) {
            where.eq("is_active", obj.getIsActive());
        }
        if (null != obj.getUserId()) {
            where.eq("user_id", obj.getUserId());
        }
        where.orderBy("create_time", false);
        where.setSqlSelect("id,user_id,is_active,user_key,user_value,description,create_time");
        Page<AutUserInfo> page =
            selectPage(new Page<AutUserInfo>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public List<AutUserInfo> list(AutUserInfo obj) throws Exception {
        Where<AutUserInfo> where = new Where<AutUserInfo>();
        if (null != obj.getId()) {
            where.eq("user_id", obj.getId());
        }
        where.setSqlSelect("id,user_id,is_active,user_key,user_value,description,create_time");
        where.orderBy("create_time", false);
        return selectList(where);
    }

    @Override
    public RetMsg addOrUpdateBatch(AutUserInfoVO userInfoVO) throws Exception {
        RetMsg retMsg = new RetMsg();
        // boolean flag = false;
        if (null != userInfoVO) {
            List<AutUserInfo> autUserInfoList = userInfoVO.getUserInfoList();
            List<Long> idList = new ArrayList<Long>();
            List<AutUserInfo> addOrUpdateList = new ArrayList<AutUserInfo>();
            List<AutUserInfo> newList = new ArrayList<AutUserInfo>();
            List<AutUserInfo> oldList = new ArrayList<AutUserInfo>();
            List<String> keyList = new ArrayList<String>();
            Long userId = null;
            if (!autUserInfoList.isEmpty()) {
                for (AutUserInfo userInfo : autUserInfoList) {
                    if (null != userInfo.getId()) {
                        idList.add(userInfo.getId());
                        newList.add(userInfo);
                    } else {
                        addOrUpdateList.add(userInfo);
                    }
                    if (StringUtils.isNotEmpty(userInfo.getUserKey())) {
                        keyList.add(userInfo.getUserKey());
                    }
                    if (null != userInfo.getUserId()) {
                        userId = userInfo.getUserId();
                    }

                }
                Where<AutUserInfo> userWhere = new Where<AutUserInfo>();
                userWhere.eq("user_id", userId);
                userWhere.in("user_key", keyList);
                if (selectList(userWhere).size() >= 2) {
                    retMsg.setCode(1);
                    retMsg.setMessage("存在重复的属性键值");
                    return retMsg;
                }
                if (!idList.isEmpty()) {
                    Where<AutUserInfo> where = new Where<AutUserInfo>();
                    where.in("id", idList);
                    oldList = selectList(where);

                    if (!oldList.isEmpty()) {
                        for (int i = 0; i < newList.size(); i++) {
                            oldList.get(i).setIsActive(newList.get(i).getIsActive());
                            oldList.get(i).setUserKey(newList.get(i).getUserKey());
                            oldList.get(i).setUserValue(newList.get(i).getUserValue());
                            oldList.get(i).setDescription(newList.get(i).getDescription());
                        }
                    }
                }
                addOrUpdateList.addAll(oldList);
                insertOrUpdateBatch(addOrUpdateList);
                retMsg.setCode(0);
                retMsg.setMessage("操作成功");
            }
        }
        return retMsg;
    }

    @Override
    public RetMsg validate(AutUserInfo obj) throws Exception {
        RetMsg retMsg = new RetMsg();
        List<AutUserInfo> keyList = null;
        if (null != obj) {
            if (null != obj.getUserKey() && null != obj.getUserId()) {
                Where<AutUserInfo> where = new Where<AutUserInfo>();
                where.eq("user_id", obj.getUserId());
                where.eq("user_key", obj.getUserKey());
                keyList = selectList(where);
            }
        }
        if (keyList.size() > 0) {
            retMsg.setCode(1);
            retMsg.setMessage("该属性键值已经存在！");
        }
        retMsg.setCode(0);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

}
