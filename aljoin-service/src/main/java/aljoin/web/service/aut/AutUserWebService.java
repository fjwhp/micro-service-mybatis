package aljoin.web.service.aut;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aljoin.att.iservice.AttSignInOutService;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.object.AutUserAndPubVo;
import aljoin.aut.iservice.AutUserRankService;
import aljoin.aut.iservice.AutUserService;
import aljoin.object.RetMsg;

/**
 * 用户服务
 * 
 * @author zhongjy
 * @date 2018/03/13
 */
@Service
public class AutUserWebService {

    @Resource
    private AutUserService autUserService;
    @Resource
    private AttSignInOutService attSignInOutService;
    @Resource
    private AutUserRankService autUserRankService;

    /**
     * 新增用户
     * 
     * @param obj
     * @param customUserid
     * @return
     * @throws Exception
     */
    @Transactional
    public RetMsg addUser(AutUserAndPubVo obj, Long customUserid) throws Exception {
        RetMsg retMsg = autUserService.addUser(obj, customUserid);
        AutUser autUser = (AutUser)retMsg.getObject();
        // 为新用户生成当月考勤数据
        attSignInOutService.addSign(autUser.getId());
        // 新增用户时，同时要往人员排序表中添加此人员的默认排序号；
        autUserRankService.add(autUser.getId());
        return retMsg;
    }

}
