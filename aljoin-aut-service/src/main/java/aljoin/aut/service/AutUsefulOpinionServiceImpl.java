package aljoin.aut.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import aljoin.aut.dao.entity.AutUsefulOpinion;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.mapper.AutUsefulOpinionMapper;
import aljoin.aut.dao.object.AutUsefulOpinionVO;
import aljoin.aut.iservice.AutUsefulOpinionService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 常用意见表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
@Service
public class AutUsefulOpinionServiceImpl extends ServiceImpl<AutUsefulOpinionMapper, AutUsefulOpinion>
    implements AutUsefulOpinionService {

    @Resource
    private AutUsefulOpinionMapper mapper;
    @Resource
    private AutUserService autUserService;

    @Override
    public Page<AutUsefulOpinionVO> list(PageBean pageBean, AutUsefulOpinion obj, Long customUserId) throws Exception {
        Where<AutUsefulOpinion> where = new Where<AutUsefulOpinion>();
        where.orderBy("content_rank,id", true);
        where.eq("user_id", customUserId);
        Page<AutUsefulOpinion> page
            = selectPage(new Page<AutUsefulOpinion>(pageBean.getPageNum(), pageBean.getPageSize()), where);

        Page<AutUsefulOpinionVO> pageList = new Page<AutUsefulOpinionVO>();
        pageList.setCurrent(page.getCurrent());
        pageList.setSize(page.getSize());
        pageList.setTotal(page.getTotal());

        List<AutUsefulOpinion> usefulOpinionList = page.getRecords();
        List<Long> userIdList = new ArrayList<Long>();
        for (AutUsefulOpinion userfulOpinion : usefulOpinionList) {
            userIdList.add(userfulOpinion.getUserId());
        }
        // 查询用户名称
        List<AutUser> autUserList = new ArrayList<AutUser>();
        if (null != userIdList && !userIdList.isEmpty()) {
            Where<AutUser> userWhere = new Where<AutUser>();
            userWhere.setSqlSelect("id,full_name");
            userWhere.in("id", userIdList);
            autUserList = autUserService.selectList(userWhere);
        }

        List<AutUsefulOpinion> opinionPageList = page.getRecords();
        List<AutUsefulOpinionVO> voList = new ArrayList<AutUsefulOpinionVO>();
        // 拼接常用意见和用户名称
        for (AutUsefulOpinion optioin : opinionPageList) {
            for (AutUser user : autUserList) {
                if (optioin.getUserId() == user.getId() || optioin.getUserId().equals(user.getId())) {
                    AutUsefulOpinionVO vo = new AutUsefulOpinionVO();
                    vo.setAutUsefulOpinion(optioin);
                    vo.setAutUser(user);
                    voList.add(vo);
                }
            }
        }
        pageList.setRecords(voList);
        return pageList;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutUsefulOpinion obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public List<AutUsefulOpinion> list(AutUsefulOpinion obj) throws Exception {
        Where<AutUsefulOpinion> where = new Where<AutUsefulOpinion>();
        where.setSqlSelect("id,user_id,content,content_rank,is_active");
        where.eq("user_id", obj.getCreateUserId());
        where.orderBy("content_rank,id", true);
        return selectList(where);
    }

    @Override
    public RetMsg appAdd(AutUsefulOpinion obj) throws Exception {
        // TRetMsgODO Auto-generated method stub
        RetMsg retMsg = new RetMsg();
        if (StringUtils.isNotEmpty(obj.getContent())) {
            if (obj.getContent().toString().length() >= 100) {
                retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
                retMsg.setMessage("常用意见长度不能超过100个");
                return retMsg;
            }

            AutUser autUser = autUserService.selectById(obj.getCreateUserId());
            obj.setCreateUserName(autUser.getCreateUserName());
            if (null == obj.getContentRank()) {
                List<AutUsefulOpinion> list = this.list(obj);
                int count = 0;
                if (null != list && !list.isEmpty()) {
                    AutUsefulOpinion usefulOpinion = list.get(0);
                    if (null != usefulOpinion) {
                        count = usefulOpinion.getContentRank();
                    }
                }
                obj.setContentRank(count + 1);
            }
            obj.setIsActive(1);
            insert(obj);
        }
        retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    public List<AutUsefulOpinion> appList(Long userID) throws Exception {
        Where<AutUsefulOpinion> where = new Where<AutUsefulOpinion>();
        where.setSqlSelect("id,user_id,content,content_rank,is_active");
        where.eq("is_active", 1);
        where.eq("user_id", userID);
        where.orderBy("content_rank", true);
        return selectList(where);
    }
}
