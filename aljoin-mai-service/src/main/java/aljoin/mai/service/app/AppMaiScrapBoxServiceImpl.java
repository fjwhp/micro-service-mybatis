package aljoin.mai.service.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiScrapBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiScrapBoxMapper;
import aljoin.mai.dao.object.MaiScrapValBoxVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.MaiDraftBoxService;
import aljoin.mai.iservice.MaiReceiveBoxSearchService;
import aljoin.mai.iservice.MaiReceiveBoxService;
import aljoin.mai.iservice.MaiScrapBoxService;
import aljoin.mai.iservice.MaiSendBoxService;
import aljoin.mai.iservice.app.AppMaiScrapBoxService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sys.iservice.SysParameterService;
import aljoin.util.DateUtil;

/**
 * 
 * 废件箱表(服务实现类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
@Service
public class AppMaiScrapBoxServiceImpl extends ServiceImpl<MaiScrapBoxMapper, MaiScrapBox>
    implements AppMaiScrapBoxService {

    private final static Logger logger = LoggerFactory.getLogger(AppMaiScrapBoxServiceImpl.class);

    @Resource
    private MaiSendBoxService maiSendBoxService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private MaiReceiveBoxService maiReceiveBoxService;
    @Resource
    private MaiDraftBoxService maiDraftBoxService;
    @Resource
    private AppMaiScrapBoxService appMaiScrapBoxService;
    @Resource
    private MaiScrapBoxService maiScrapBoxService;
    @Resource
    private MaiAttachmentService maiAttachmentService;
    @Resource
    private AutUserPubService autUserPubService;
    @Resource
    private MaiReceiveBoxSearchService maiReceiveBoxSearchService;

    @Override
    public Page<MaiScrapBox> list(PageBean pageBean, MaiScrapBox obj, Long userId, String time1, String time2)
        throws Exception {
        Where<MaiScrapBox> where = new Where<MaiScrapBox>();
        where.eq("create_user_id", userId);
        where.eq("is_delete", 0);
        if (obj.getMailSize() != null) {
            where.where("attachment_count>{0}", 0);
        }
        if (obj.getIsDelete() != null) {
            String ids = "";
            Where<MaiReceiveBox> rw = new Where<MaiReceiveBox>();
            rw.where("is_scrap={0}", 1);
            rw.eq("is_urgent", "1");
            rw.eq("receive_user_id", userId);
            List<MaiReceiveBox> mrb = maiReceiveBoxService.selectList(rw);
            for (MaiReceiveBox maiReceiveBox : mrb) {
                ids = ids + maiReceiveBox.getId() + ",";
            }
            Where<MaiSendBox> sw = new Where<MaiSendBox>();
            sw.where("is_scrap={0}", 1);
            sw.eq("is_urgent", "1");
            sw.eq("send_user_id", userId);
            List<MaiSendBox> msb = maiSendBoxService.selectList(sw);
            for (MaiSendBox maiReceiveBox : msb) {
                ids = ids + maiReceiveBox.getId() + ",";
            }
            Where<MaiDraftBox> dw = new Where<MaiDraftBox>();
            dw.where("is_scrap={0}", 1);
            dw.eq("is_urgent", "1");
            List<MaiDraftBox> mdb = maiDraftBoxService.selectList(dw);
            for (MaiDraftBox maiReceiveBox : mdb) {
                ids = ids + maiReceiveBox.getId() + ",";
            }
            if (ids.length() > 0) {
                where.notIn("orgnl_id", ids);
            }
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isNotEmpty(obj.getSubjectText())) {
            where.like("subject_text", obj.getSubjectText());
        }
        if (obj.getReceiveFullNames() != null && !"".equals((obj.getReceiveFullNames()))) {
            where.like("send_full_name", obj.getReceiveFullNames());
        }

        if (StringUtils.isNotEmpty(time1) && StringUtils.isNotEmpty(time2)) {
            c.setTime(format.parse(time2));
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            where.between("last_update_time", DateUtil.str2dateOrTime(time1), c.getTime());
        } else if (StringUtils.isNotEmpty(time1)) {
            where.ge("last_update_time", DateUtil.str2dateOrTime(time1));
        } else if (StringUtils.isNotEmpty(time2)) {
            c.setTime(format.parse(time2));
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            where.le("last_update_time", c.getTime());
        }

        // 按照邮件大小升序/降序或按收件时间排序，如果都默认按收件时间降序
        if (StringUtils.checkValNotNull(obj.getMailSize())) {
            if (obj.getMailSize().equals(1) || obj.getMailSize() == 1) {
                where.orderBy("mail_size", true);
            } else {
                where.orderBy("mail_size", false);
            }
        } else if (StringUtils.checkValNotNull(obj.getLastUpdateTime())) {
            if (obj.getLastUpdateTime().equals(1)) {
                where.orderBy("last_update_time", true);
            } else {
                where.orderBy("last_update_time", false);
            }
        } else {
            where.orderBy("last_update_time", false);
        }
        Page<MaiScrapBox> page =
            selectPage(new Page<MaiScrapBox>(pageBean.getPageNum(), pageBean.getPageSize()), where);
        return page;
    }


    @Override
    @Transactional
    public RetMsg recoverMai(String obj, AutAppUserLogin autAppUserLogin) throws Exception {
        RetMsg retMsg = new RetMsg();

        try {
            MaiScrapBox msb = this.selectById(Long.valueOf(obj));

            if (msb.getOrgnlType() == 2) {
                // 发件箱
                Where<MaiSendBox> w1 = new Where<MaiSendBox>();
                w1.eq("id", msb.getOrgnlId());
                // 查到原邮件，isScrap均set为0
                MaiSendBox msbs = maiSendBoxService.selectOne(w1);
                msbs.setIsScrap(0);
                msbs.setLastUpdateUserId(autAppUserLogin.getUserId());
                msbs.setLastUpdateUserName(autAppUserLogin.getUserName());
                if (msbs != null) {
                    maiSendBoxService.updateById(msbs);
                }
            } else {
                // 收件箱
                MaiReceiveBoxSearch maiReceiveSearch = maiReceiveBoxSearchService.selectById(msb.getId());
                maiReceiveSearch.setIsScrap(0);
                maiReceiveBoxSearchService.updateById(maiReceiveSearch);
            }

            // 删除废件箱中该邮件
            appMaiScrapBoxService.deleteById(Long.valueOf(obj));
            retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
            retMsg.setMessage("操作成功");
        } catch (Exception e) {
            retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
            retMsg.setMessage(e.getMessage());
            logger.error("", e);
        }
        return retMsg;
    }

    @Override
    @Transactional
    public RetMsg scrapAdd(MaiScrapBox objs) throws Exception {
        RetMsg retMsg = new RetMsg();
        MaiScrapBox obj = new MaiScrapBox();
        obj.setOrgnlId(objs.getOrgnlId());
        obj.setCreateUserId(objs.getCreateUserId());
        obj.setCreateUserName(objs.getCreateUserName());
        obj.setOrgnlType(objs.getOrgnlType());
        if (objs.getOrgnlType() == 0) {
            // 收件箱
            MaiReceiveBox oldRmai = maiReceiveBoxService.selectById(objs.getOrgnlId());
            oldRmai.setScrapTime(new Date());
            maiReceiveBoxService.updateById(oldRmai);
            MaiReceiveBoxSearch orgnMaiSearch = maiReceiveBoxSearchService.selectById(objs.getOrgnlId());
            orgnMaiSearch.setIsScrap(1);
            maiReceiveBoxSearchService.updateById(orgnMaiSearch);
            
            obj.setMailSize(oldRmai.getMailSize());
            obj.setReceiveFullNames(oldRmai.getReceiveFullNames());
            obj.setReceiveUserIds(oldRmai.getReceiveUserIds());
            obj.setSendUserName(oldRmai.getSendUserName());
            obj.setReceiveUserNames(oldRmai.getReceiveUserNames());
            obj.setSendUserId(oldRmai.getSendUserId());
            obj.setSubjectText(orgnMaiSearch.getSubjectText());
            obj.setSendFullName(orgnMaiSearch.getSendFullName());
        } else {
            // 发件箱
            Where<MaiSendBox> fWhere = new Where<MaiSendBox>();
            fWhere.eq("id", objs.getOrgnlId());
            MaiSendBox oldFmai = maiSendBoxService.selectOne(fWhere);
            oldFmai.setIsScrap(1);
            oldFmai.setScrapTime(new Date());
            maiSendBoxService.update(oldFmai, fWhere);

            obj.setMailSize(oldFmai.getMailSize());
            obj.setReceiveFullNames(oldFmai.getReceiveFullNames());
            obj.setReceiveUserIds(oldFmai.getReceiveUserIds());
            obj.setSendUserName(oldFmai.getSendUserName());
            obj.setReceiveUserNames(oldFmai.getReceiveUserNames());
            obj.setSendUserId(oldFmai.getSendUserId());
            obj.setSendFullName(oldFmai.getSendFullName());
            obj.setSubjectText(oldFmai.getSubjectText());
        }
        obj.setIsDelete(0);
        Boolean istrue = this.insert(obj);
        if (istrue) {
            retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
            retMsg.setMessage("操作成功");
        } else {
            retMsg.setCode(AppConstant.RET_CODE_PARAM_ERR);
            retMsg.setMessage("操作失败");
        }
        return retMsg;
    }

    @Override
    public MaiScrapValBoxVO getById(MaiScrapBox obj) throws Exception {
        MaiScrapValBoxVO maiDraftBoxVO = new MaiScrapValBoxVO();
        if (null != obj) {
            if (null != obj.getId()) {
                Where<MaiScrapBox> w1 = new Where<MaiScrapBox>();
                w1.eq("id", obj.getId());
                MaiScrapBox maiDraftBox = selectOne(w1);
                if (StringUtils.checkValNotNull(maiDraftBox)) {
                    maiDraftBoxVO.setMaiScrapBox(maiDraftBox);
                    Where<MaiAttachment> where = new Where<MaiAttachment>();
                    where.setSqlSelect("attach_name,attach_path,attach_size,send_id,receive_id,draft_id");
                    if (maiDraftBox.getOrgnlType() == 1) {
                        where.eq("receive_id", maiDraftBox.getOrgnlId());
                        MaiReceiveBox box = maiReceiveBoxService.selectById(maiDraftBox.getOrgnlId());
                        maiDraftBoxVO.setCopyFullNames(box.getCopyFullNames());
                        maiDraftBoxVO.setCopyUserIds(box.getCopyUserIds());
                        maiDraftBoxVO.setCopyUserNames(box.getCopyUserNames());
                        maiDraftBoxVO.setMailContent(box.getMailContent());
                    } else if (maiDraftBox.getOrgnlType() == 2) {
                        where.eq("send_id", maiDraftBox.getOrgnlId());
                        MaiSendBox box = maiSendBoxService.selectById(maiDraftBox.getOrgnlId());
                        maiDraftBoxVO.setCopyFullNames(box.getCopyFullNames());
                        maiDraftBoxVO.setCopyUserIds(box.getCopyUserIds());
                        maiDraftBoxVO.setCopyUserNames(box.getCopyUserNames());
                        maiDraftBoxVO.setMailContent(box.getMailContent());
                    } else {
                        where.eq("draft_id", maiDraftBox.getOrgnlId());
                        MaiDraftBox box = maiDraftBoxService.selectById(maiDraftBox.getOrgnlId());
                        maiDraftBoxVO.setCopyFullNames(box.getCopyFullNames());
                        maiDraftBoxVO.setCopyUserIds(box.getCopyUserIds());
                        maiDraftBoxVO.setCopyUserNames(box.getCopyUserNames());
                        maiDraftBoxVO.setMailContent(box.getMailContent());
                    }
                    /*
                     * SysParameter parameter = sysParameterService.selectBykey("ImgServer"); String rootPath
                     * = ""; if(null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())){
                     * rootPath = parameter.getParamValue(); } if(maiDraftBoxVO.getCopyUserIds()!=null &&
                     * !"".equals(maiDraftBoxVO.getCopyUserIds())){
                     * maiDraftBoxVO.setCopyImg(getImg(rootPath,maiDraftBoxVO.getMaiScrapBox().getSendUserId()
                     * .toString())); } if(maiDraftBoxVO.getMaiScrapBox().getSendUserId()!=null &&
                     * !"".equals(maiDraftBoxVO.getMaiScrapBox().getSendUserId())){
                     * maiDraftBoxVO.setsUserImg(getImg(rootPath,maiDraftBoxVO.getMaiScrapBox().getSendUserId(
                     * ).toString())); } if(maiDraftBoxVO.getMaiScrapBox().getReceiveUserIds()!=null &&
                     * !"".equals(maiDraftBoxVO.getMaiScrapBox().getReceiveUserIds())){
                     * maiDraftBoxVO.setrUserImg(getImg(rootPath,maiDraftBoxVO.getMaiScrapBox().
                     * getReceiveUserIds())); }
                     */
                    // Long suser=maiDraftBox.getSendUserId();
                    // String ruser=maiDraftBox.getReceiveUserIds();
                    // SysParameter parameter = sysParameterService.selectBykey("ImgServer");
                    //String rootPath = "";
                    List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                    //List<MaiAttachment> returnList = new ArrayList<MaiAttachment>();
                    if (!attachmentList.isEmpty()) {
                        /*for (MaiAttachment maiAttachment : attachmentList) {
                            String tmp = maiAttachment.getAttachPath();
                            // tmp = tmp.substring(2, tmp.length());
                            tmp = tmp.replaceAll("//", "/");
                            String path = rootPath + tmp;
                            returnList.add(maiAttachment.setAttachPath(path));
                        }*/
                        maiDraftBoxVO.setMaiAttachmentList(attachmentList);
                    }
                } else {
                    throw new Exception("找不到该草稿");
                }

            }

        }
        return maiDraftBoxVO;
    }

    public String getImg(String rootPath, String id) throws Exception {
        String path = "";

        if (id.length() > 0) {
            String[] ids = id.split(";");
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != null && !"".equals(ids[i])) {
                    if (!"".equals(rootPath)) {
                        AutUserPub autUserPub = autUserPubService.selectById(ids[i]);
                        if (autUserPub != null && autUserPub.getUserIcon() != null
                            && !"".equals(autUserPub.getUserIcon())) {
                            path = path + rootPath + autUserPub.getUserIcon() + ";";
                        } else {
                            // 默认地址
                            path = path + "web/images/0.jpg" + ";";
                        }
                    } else {
                        path = path + "web/images/0.jpg" + ";";
                    }
                }
            }
        }
        return path;

    }

}
