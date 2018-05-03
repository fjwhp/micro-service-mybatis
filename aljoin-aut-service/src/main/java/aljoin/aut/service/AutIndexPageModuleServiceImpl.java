package aljoin.aut.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutIndexPageModule;
import aljoin.aut.dao.mapper.AutIndexPageModuleMapper;
import aljoin.aut.iservice.AutIndexPageModuleService;
import aljoin.dao.config.Where;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.WebConstant;
import aljoin.sys.dao.entity.SysDataDict;
import aljoin.sys.iservice.SysDataDictService;

/**
 * 
 * 首页模块定制表(服务实现类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-12
 */
@Service
public class AutIndexPageModuleServiceImpl extends ServiceImpl<AutIndexPageModuleMapper, AutIndexPageModule>
    implements AutIndexPageModuleService {

    private final static Logger logger = LoggerFactory.getLogger(AutIndexPageModuleServiceImpl.class);

    @Resource
    private AutIndexPageModuleMapper mapper;
    @Resource
    private SysDataDictService sysDataDictService;

    @Override
    public Page<AutIndexPageModule> list(PageBean pageBean, AutIndexPageModule obj) throws Exception {
        Where<AutIndexPageModule> where = new Where<AutIndexPageModule>();
        if (StringUtils.isNotEmpty(obj.getModuleName())) {
            where.like("module_name", obj.getModuleName());
        }
        where.orderBy("module_rank", true);
        Page<AutIndexPageModule> page
            = selectPage(new Page<AutIndexPageModule>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }

    @Override
    public List<AutIndexPageModule> selectUser(String userid) throws Exception {
        List<AutIndexPageModule> ipm = new ArrayList<AutIndexPageModule>();
        Where<AutIndexPageModule> where = new Where<AutIndexPageModule>();
        where.where("create_user_id={0}", userid);
        where.eq("is_delete", 0);
        where.eq("is_active", 1);
        where.orderBy("module_rank", true);
        ipm = this.selectList(where);
        return ipm;
    }

    @Override
    public void physicsDeleteById(Long id) throws Exception {
        mapper.physicsDeleteById(id);
    }

    @Override
    public void copyObject(AutIndexPageModule obj) throws Exception {
        mapper.copyObject(obj);
    }

    @Override
    public RetMsg updateModule(AutIndexPageModule key, Long userId) throws Exception {
        // TODO Auto-generated method stub
        RetMsg retMsg = new RetMsg();
        if (key != null) {
            Where<AutIndexPageModule> ipWhere = new Where<AutIndexPageModule>();
            ipWhere.eq("module_code", key.getModuleCode());
            ipWhere.eq("create_user_id", userId);
            AutIndexPageModule oldipm = this.selectOne(ipWhere);
            if (oldipm.getModuleRank() != key.getModuleRank()) {
                ipWhere = new Where<AutIndexPageModule>();
                ipWhere.eq("module_rank", key.getModuleRank());
                ipWhere.eq("create_user_id", userId);
                String[] tmp = key.getModuleCode().split("&");
                ipWhere.like("module_code", tmp[0].toString());
                AutIndexPageModule replaceData = this.selectOne(ipWhere);
                replaceData.setModuleRank(oldipm.getModuleRank());
                this.updateById(replaceData);
                oldipm.setModuleRank(key.getModuleRank());
            }
            if (key.getIsHide() != null) {
                oldipm.setIsHide(key.getIsHide());
            }
            this.updateById(oldipm);
            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
            retMsg.setMessage("操作成功");
        } else {
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage("更改的传递数据为空");
        }

        return retMsg;
    }

    @Override
    public RetMsg removeUpdate(String codes, String isType, Long userId) throws Exception {
        // TODO Auto-generated method stub
        RetMsg retMsg = new RetMsg();
        Where<AutIndexPageModule> ipWhere = new Where<AutIndexPageModule>();
        ipWhere.eq("create_user_id", userId);
        ipWhere.eq("is_hide", 0);
        ipWhere.eq("is_active", 1);
        ipWhere.like("module_code", isType + "&%");
        ipWhere.orderBy("module_rank");
        List<AutIndexPageModule> autPageModuleList = this.selectList(ipWhere);
        List<AutIndexPageModule> tmpList = new ArrayList<AutIndexPageModule>();
        // List<Integer> tmpRank = new ArrayList<Integer>();
        String[] code = codes.split(",");
        for (int i = 0; i < code.length; i++) {
            for (AutIndexPageModule autIndexPageModule : autPageModuleList) {
                if ((isType + "&" + code[i]).equals(autIndexPageModule.getModuleCode())) {
                    tmpList.add(autIndexPageModule);
                    break;
                }
            }
        }
        List<Integer> intList = new ArrayList<Integer>();
        for (int i = 0; i < autPageModuleList.size(); i++) {
            intList.add(autPageModuleList.get(i).getModuleRank());
        }
        for (int i = 0; i < tmpList.size(); i++) {
            AutIndexPageModule autIndexPageModule = tmpList.get(i);
            autIndexPageModule.setModuleRank(intList.get(i));
            this.updateById(autIndexPageModule);
        }
        retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
        retMsg.setMessage("操作成功");
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg init(Long userId) throws Exception {
        RetMsg retMsg = new RetMsg();
        // 默认isActive=1
        // 生成个人默认配置
        List<AutIndexPageModule> adpmList = new ArrayList<AutIndexPageModule>();
        try {
            adpmList = selectUser(userId.toString());
            if (adpmList != null && adpmList.size() > 0) {

            } else {
                List<SysDataDict> lDataList = sysDataDictService.getByCode("adminLeftPage");
                if (lDataList != null && lDataList.size() > 0) {

                } else {
                    retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                    retMsg.setMessage("请设置数据字典 左页面配置编码为：adminLeftPage");
                    return retMsg;
                }
                List<SysDataDict> rDataList = sysDataDictService.getByCode("adminRightPage");
                if (rDataList != null && rDataList.size() > 0) {

                } else {
                    retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
                    retMsg.setMessage("请设置数据字典右页面配置编码为：adminRightPage");
                    return retMsg;
                }

                for (SysDataDict sysDataDict : rDataList) {
                    AutIndexPageModule obj = new AutIndexPageModule();
                    obj.setIsDelete(0);
                    obj.setIsActive(1);
                    obj.setIsHide(0);
                    obj.setModuleRank(sysDataDict.getDictRank());
                    obj.setModuleName(sysDataDict.getDictKey());
                    obj.setModuleCode("R&" + sysDataDict.getDictValue());
                    insert(obj);
                }
                for (SysDataDict sysDataDict : lDataList) {
                    AutIndexPageModule obj = new AutIndexPageModule();
                    obj.setIsDelete(0);
                    obj.setIsActive(1);
                    obj.setIsHide(0);
                    obj.setModuleRank(sysDataDict.getDictRank());
                    obj.setModuleName(sysDataDict.getDictKey());
                    obj.setModuleCode("L&" + sysDataDict.getDictValue());
                    insert(obj);
                }
                adpmList = selectUser(userId.toString());
            }
            retMsg.setCode(WebConstant.RETMSG_SUCCESS_CODE);
            retMsg.setMessage(WebConstant.RETMSG_OPERATION_SUCCESS);
            retMsg.setObject(adpmList);
        } catch (Exception e) {
            logger.error("", e);
            retMsg.setCode(WebConstant.RETMSG_FAIL_CODE);
            retMsg.setMessage(WebConstant.RETMSG_OPERATION_FAIL + e.getMessage());
        }
        return retMsg;
    }
}
