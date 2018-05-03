package aljoin.act.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.act.dao.entity.ActAljoinBpmnUser;
import aljoin.act.dao.mapper.ActAljoinBpmnUserMapper;
import aljoin.act.iservice.ActAljoinBpmnUserService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;

/**
 * 
 * 流程-用户关系表(服务实现类).
 * 
 * @author：pengsp.
 * 
 * @date： 2017-10-12
 */
@Service
public class ActAljoinBpmnUserServiceImpl extends ServiceImpl<ActAljoinBpmnUserMapper, ActAljoinBpmnUser>
    implements ActAljoinBpmnUserService {
    @Resource
    private ActAljoinBpmnUserMapper mapper;
    @Resource
    private ActAljoinBpmnUserService actAljoinBpmnUserService;

    @Override
    public Page<ActAljoinBpmnUser> list(PageBean pageBean, ActAljoinBpmnUser obj) throws Exception {
        Where<ActAljoinBpmnUser> where = new Where<ActAljoinBpmnUser>();
        where.orderBy("create_time", false);
        Page<ActAljoinBpmnUser> page =
            selectPage(new Page<ActAljoinBpmnUser>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(ActAljoinBpmnUser obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    @Transactional
    public RetMsg addUserAndMan(String userId, String manId, String bpmnId, String isCheck) throws Exception {
        RetMsg retMsg = new RetMsg();
        Where<ActAljoinBpmnUser> dwhere = new Where<ActAljoinBpmnUser>();
        Boolean isDelUser = false;
        // 清空使用者
        if (userId == null || "".equals(userId)) {
            Where<ActAljoinBpmnUser> delwhere = new Where<ActAljoinBpmnUser>();
            delwhere.eq("bpmn_id", bpmnId);
            delwhere.eq("is_active", 1);
            delwhere.eq("auth_type", 0);
            delwhere.eq("is_delete", 0);
            actAljoinBpmnUserService.delete(delwhere);
            dwhere.eq("auth_type", 1);
            isDelUser = true;
        }
        dwhere.eq("bpmn_id", bpmnId);
        dwhere.eq("is_active", 1);
        dwhere.eq("is_delete", 0);
        List<ActAljoinBpmnUser> leaList = actAljoinBpmnUserService.selectList(dwhere); // 获取所有该授权
        // List<ActAljoinBpmnUser> List = new ArrayList<ActAljoinBpmnUser>();
        // 插入批量处理
        List<ActAljoinBpmnUser> updateList = new ArrayList<ActAljoinBpmnUser>();
        List<ActAljoinBpmnUser> insertList = new ArrayList<ActAljoinBpmnUser>();
        // 对已经存在数据的
        if (leaList != null && leaList.size() > 0) {

            boolean isLeaChange = false;
            for (int i = 0; i < leaList.size(); i++) {
                ActAljoinBpmnUser actAljoinBpmnUser = leaList.get(i);
                String user = actAljoinBpmnUser.getUserId().toString();
                int type = actAljoinBpmnUser.getAuthType();
                if (!isDelUser && type == 0) {
                    if (userId.indexOf(",") == userId.length()) {
                        userId = userId.substring(0, userId.length() - 1);
                    }
                    String[] uids = userId.split(",");
                    Boolean isTrue = false;
                    for (String string : uids) {
                        if (string.equals(user)) {
                            // 存在不更新，不变更
                            isTrue = true;
                            if (userId.indexOf(string + ",") > -1) {
                                userId = userId.replaceAll(string + ",", "");
                                if (userId.indexOf(string) > -1) {
                                    userId = userId.replaceAll(string, "");
                                }
                            } else {
                                userId = userId.replaceAll(string, "");
                            }
                        }

                    }
                    if (!isTrue) {
                        actAljoinBpmnUser.setIsDelete(1);
                        updateList.add(actAljoinBpmnUser);
                    }
                } else if (type == 0) {
                    actAljoinBpmnUser.setIsDelete(1);
                    updateList.add(actAljoinBpmnUser);
                }
                if (type == 1 && manId != null && !"".equals(manId)) {
                    isLeaChange = true;
                    if (manId.indexOf(",") == manId.length()) {
                        manId = manId.substring(0, manId.length() - 1);
                    }
                    Boolean isTrue = false;
                    String[] mids = manId.split(",");
                    for (String string : mids) {
                        if (string.equals(user)) {
                            // 存在不更新，不变更
                            isTrue = true;
                            if (manId.indexOf(string + ",") > -1) {
                                manId = manId.replaceAll((string + ","), "");
                                if (manId.indexOf(string) > -1) {
                                    manId = manId.replaceAll(string, "");
                                }
                            } else {
                                manId = manId.replaceAll(string, "");
                            }
                        }
                    }
                    if (!isTrue) {
                        actAljoinBpmnUser.setIsDelete(1);
                        updateList.add(actAljoinBpmnUser);
                    }
                } else if (type == 1 && !isLeaChange) {
                    actAljoinBpmnUser.setIsDelete(1);
                    updateList.add(actAljoinBpmnUser);
                }
            }

        }
        if (userId.length() > 0) {
            if (userId.indexOf(",") > -1) {
                String[] ids = userId.split(",");
                for (String string : ids) {
                    ActAljoinBpmnUser actAljoinbpmnUser = new ActAljoinBpmnUser();
                    actAljoinbpmnUser.setBpmnId(Long.valueOf(bpmnId));
                    actAljoinbpmnUser.setAuthType(0);
                    actAljoinbpmnUser.setUserId(Long.valueOf(string));
                    actAljoinbpmnUser.setIsDelete(0);
                    actAljoinbpmnUser.setIsActive(1);
                    insertList.add(actAljoinbpmnUser);
                }
            } else {
                ActAljoinBpmnUser actAljoinbpmnUser = new ActAljoinBpmnUser();
                actAljoinbpmnUser.setBpmnId(Long.valueOf(bpmnId));
                actAljoinbpmnUser.setAuthType(0);
                actAljoinbpmnUser.setUserId(Long.valueOf(userId));
                actAljoinbpmnUser.setIsDelete(0);
                actAljoinbpmnUser.setIsActive(1);
                insertList.add(actAljoinbpmnUser);
            }
        }
        if (manId.length() > 0) {
            if (manId.indexOf(",") > -1) {
                String[] ids = manId.split(",");
                for (String string : ids) {
                    ActAljoinBpmnUser actAljoinbpmnUser = new ActAljoinBpmnUser();
                    actAljoinbpmnUser.setBpmnId(Long.valueOf(bpmnId));
                    actAljoinbpmnUser.setAuthType(1);
                    actAljoinbpmnUser.setUserId(Long.valueOf(string));
                    actAljoinbpmnUser.setIsDelete(0);
                    actAljoinbpmnUser.setIsActive(1);
                    insertList.add(actAljoinbpmnUser);
                }
            } else {
                ActAljoinBpmnUser actAljoinbpmnUser = new ActAljoinBpmnUser();
                actAljoinbpmnUser.setBpmnId(Long.valueOf(bpmnId));
                actAljoinbpmnUser.setAuthType(1);
                actAljoinbpmnUser.setUserId(Long.valueOf(manId));
                actAljoinbpmnUser.setIsDelete(0);
                actAljoinbpmnUser.setIsActive(1);
                insertList.add(actAljoinbpmnUser);
            }
        }
        if (updateList.size() > 0) {
            actAljoinBpmnUserService.updateBatchById(updateList);
        }
        if (insertList.size() > 0) {
            actAljoinBpmnUserService.insertBatch(insertList);
        }
        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage("操作成功");
        return retMsg;

    }

}
