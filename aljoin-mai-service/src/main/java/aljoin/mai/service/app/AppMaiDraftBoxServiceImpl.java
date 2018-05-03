package aljoin.mai.service.app;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.StringUtils;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.iservice.AutUserPubService;
import aljoin.aut.iservice.AutUserService;
import aljoin.dao.config.Where;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.mapper.MaiDraftBoxMapper;
import aljoin.mai.dao.object.AppMaiDraftBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.mai.iservice.MaiAttachmentService;
import aljoin.mai.iservice.app.AppMaiDraftBoxService;
import aljoin.object.AppConstant;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.res.iservice.ResResourceService;
import aljoin.sys.iservice.SysParameterService;

/**
 * 
 * 草稿箱表(服务实现类).
 * 
 * @author：laijy
 * 
 * @date： 2017-09-20
 */
@Service
public class AppMaiDraftBoxServiceImpl extends ServiceImpl<MaiDraftBoxMapper, MaiDraftBox>
        implements AppMaiDraftBoxService {
  
  private final static Logger logger = LoggerFactory.getLogger(AppMaiDraftBoxServiceImpl.class);

    @Resource
    private AppMaiDraftBoxService appMaiDraftBoxService;
    @Resource
    private MaiAttachmentService maiAttachmentService;
    @Resource
    private AutUserService autUserService;
    @Resource
    private SysParameterService sysParameterService;
    @Resource
    private  AutUserPubService autUserPubService;
    @Resource
    private ResResourceService resResourceService;

    @SuppressWarnings("unused")
    @Override
    public Page<MaiDraftBox> list(PageBean pageBean, MaiDraftBox obj, Long userId) throws Exception {
        Where<MaiDraftBox> where = new Where<MaiDraftBox>();        
        where.eq("create_user_id", userId);
        where.eq("is_scrap", 0);
        where.eq("is_delete", 0);
        if (obj.getCreateTime()!=null) {            
            where.ge("last_update_time", obj.getCreateTime());
        }
        if (obj.getLastUpdateTime()!=null) {    
            Calendar c = Calendar.getInstance();  
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            c.setTime(obj.getLastUpdateTime()); 
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            where.le("last_update_time", c.getTime());
        }
        
        if (obj.getIsUrgent()!=null) {
            where.eq("is_urgent", obj.getIsUrgent());
        }
        if (obj.getIsDelete()!=null) {
            where.where("attachment_count > {0}", 0);
        }
        // 搜索条件:主题、收件人、操作时间
        if (StringUtils.isNotEmpty(obj.getSubjectText())) {
            where.and();
            where.like("subject_text", obj.getSubjectText());
        }
        if (StringUtils.isNotEmpty(obj.getReceiveFullNames())) {            
                where.like("receive_full_names", obj.getReceiveFullNames());                    
        }       
        where.orderBy("last_update_time", false);
        Page<MaiDraftBox> page = selectPage(new Page<MaiDraftBox>(pageBean.getPageNum(), pageBean.getPageSize()),
                where);
        return page;
    }

    @Override
    public RetMsg add(MaiWriteVO obj, AutAppUserLogin autAppUserLogin) throws Exception {

        RetMsg retMsg = new RetMsg();

        try {
            MaiDraftBox draftBox = new MaiDraftBox();
            //MaiReceiveBox receiveBox = obj.getMaiReceiveBox();
            MaiSendBox sendBox = obj.getMaiSendBox();
            if (null != autAppUserLogin) {
                draftBox.setSendUserId(autAppUserLogin.getUserId());
                draftBox.setSendUserName(autAppUserLogin.getUserName());
                AutUser autUser = autUserService.selectById(autAppUserLogin.getUserId());
                draftBox.setSendFullName(autUser.getFullName());
            }
            if (StringUtils.checkValNotNull(sendBox.getAttachmentCount())) {
                draftBox.setAttachmentCount(sendBox.getAttachmentCount());
            } else {
                draftBox.setAttachmentCount(0);
            }

            if (StringUtils.checkValNotNull(sendBox.getReceiveFullNames())) {
                draftBox.setReceiveFullNames(sendBox.getReceiveFullNames());
            }
            if (StringUtils.checkValNotNull(sendBox.getReceiveUserNames())) {
                draftBox.setReceiveUserNames(sendBox.getReceiveUserNames());
            }
            if (StringUtils.checkValNotNull(sendBox.getReceiveUserIds())) {
                draftBox.setReceiveUserIds(sendBox.getReceiveUserIds());
            }
            if (StringUtils.checkValNotNull(sendBox.getCopyFullNames())) {
                draftBox.setCopyFullNames(sendBox.getCopyFullNames());
            }
            draftBox.setReceiveFullNames(sendBox.getReceiveFullNames());

            if (StringUtils.checkValNotNull(sendBox.getIsReceiveSmsRemind())) {
                draftBox.setIsReceiveSmsRemind(sendBox.getIsReceiveSmsRemind());
            } else {
                draftBox.setIsReceiveSmsRemind(0);
            }
            if (StringUtils.checkValNotNull(sendBox.getCopyUserIds())) {
                draftBox.setCopyUserIds(sendBox.getCopyUserIds());
            }
            if (StringUtils.checkValNotNull(sendBox.getCopyUserNames())) {
                draftBox.setCopyUserNames(sendBox.getCopyUserNames());
            }
            if (StringUtils.checkValNotNull(sendBox.getScrapTime())) {
                draftBox.setScrapTime(sendBox.getScrapTime());
            }
            if (StringUtils.checkValNotNull(sendBox.getSubjectText())) {
                draftBox.setSubjectText(sendBox.getSubjectText());
            }
            if (StringUtils.checkValNotNull(sendBox.getMailContent())) {
                draftBox.setMailContent(sendBox.getMailContent());
            }
            draftBox.setIsScrap(0);
            draftBox.setScrapTime(new Date());
            if (StringUtils.checkValNotNull(sendBox.getIsCopySmsRemind())) {
                draftBox.setIsCopySmsRemind(sendBox.getIsCopySmsRemind());
            } else {
                draftBox.setIsCopySmsRemind(0);
            }
            if (StringUtils.checkValNotNull(sendBox.getIsReceipt())) {
                draftBox.setIsReceipt(sendBox.getIsReceipt());
            } else {
                draftBox.setIsReceipt(0);
            }
            if (StringUtils.checkValNotNull(sendBox.getIsUrgent())) {
                draftBox.setIsUrgent(sendBox.getIsUrgent());
            } else {
                draftBox.setIsUrgent(0);
            }
            // 附件表
            int size = 0;
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        if (null != attachment && null == attachment.getId()) {
                            size += attachment.getAttachSize();
                        }
                    }
                }
            }
            // 邮件size = 附件size+正文size
            if (null != sendBox.getMailContent()) {
                int len = sendBox.getMailContent().getBytes().length / 1024;
                draftBox.setMailSize((size + len));
            } else {
                draftBox.setMailSize(size);
            }
            // 数据插入草稿箱表
            insert(draftBox);

            // 保存为草稿，有附件的时候，往附件表MaiAttachment新增记录
            List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
            if (null != maiAttachmentList) {
                if (!maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        MaiAttachment maiAttachment = new MaiAttachment();
                        BeanUtils.copyProperties(attachment, maiAttachment);
                        maiAttachment.setSendId(0L);
                        maiAttachment.setReceiveId(0L);
                        maiAttachment.setDraftId(draftBox.getId());
                        newMaiAttachmentList.add(maiAttachment);
                    }
                }
            }
            if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                maiAttachmentService.insertBatch(newMaiAttachmentList);
            }
            
            retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
            retMsg.setMessage("保存草稿成功");
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(AppConstant.RET_CODE_ERROR);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    
    
    @Override
    public RetMsg update(MaiWriteVO obj, AutAppUserLogin autAppUserLogin) throws Exception {

        RetMsg retMsg = new RetMsg();
        try {
            // 根据前台传过来的草稿主键ID查出原草稿数据
            Where<MaiDraftBox> w1 = new Where<MaiDraftBox>();
            w1.eq("id", obj.getMaiDraftBox().getId());
            MaiDraftBox draftBox = selectOne(w1);
            MaiDraftBox mdb=obj.getMaiDraftBox();
            //MaiReceiveBox receiveBox = obj.getMaiReceiveBox();
            //MaiSendBox sendBox = obj.getMaiSendBox();
            
            if (null != autAppUserLogin) {
                draftBox.setSendUserId(autAppUserLogin.getUserId());
                draftBox.setSendUserName(autAppUserLogin.getUserName());
                AutUser autUser = autUserService.selectById(autAppUserLogin.getUserId());
                draftBox.setSendFullName(autUser.getFullName());
                draftBox.setLastUpdateUserId(autUser.getId());
                draftBox.setLastUpdateUserName(autUser.getUserName());
            }

            if (StringUtils.checkValNotNull(mdb.getAttachmentCount())) {
                draftBox.setAttachmentCount(mdb.getAttachmentCount());
            } else {
                draftBox.setAttachmentCount(0);
            }

        
            draftBox.setReceiveFullNames(mdb.getReceiveFullNames());
            
                if (StringUtils.checkValNotNull(mdb.getIsReceiveSmsRemind())) {
                    draftBox.setIsReceiveSmsRemind(mdb.getIsReceiveSmsRemind());
                } 
                if (StringUtils.checkValNotNull(mdb.getScrapTime())) {
                    draftBox.setScrapTime(mdb.getScrapTime());
                }
                if (StringUtils.checkValNotNull(mdb.getIsCopySmsRemind())) {
                    draftBox.setIsCopySmsRemind(mdb.getIsCopySmsRemind());
                } 
                if (StringUtils.checkValNotNull(mdb.getIsReceipt())) {
                    draftBox.setIsReceipt(mdb.getIsReceipt());
                }
                if (StringUtils.checkValNotNull(mdb.getIsUrgent())) {
                    draftBox.setIsUrgent(mdb.getIsUrgent());
                }           
            
            draftBox.setCopyUserIds(mdb.getCopyUserIds());
            draftBox.setCopyUserNames(mdb.getCopyUserNames());  
            draftBox.setSubjectText(mdb.getSubjectText());
            draftBox.setMailContent(mdb.getMailContent());          
            draftBox.setIsScrap(0);
            // 附件表
            int size = 0;
            List<MaiAttachment> maiAttachmentList = obj.getMaiAttachmentList();
            if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                if (null != maiAttachmentList && !maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        if (null != attachment && null == attachment.getId()) {
                            size += attachment.getAttachSize();
                        }
                    }
                }
            }
            if (null == mdb.getMailContent()) {
                int len = mdb.getMailContent().getBytes().length / 1024;
                draftBox.setMailSize((size + len));
            } else {
                draftBox.setMailSize(0);
            }
         
            updateById(draftBox);

            // 保存为草稿，有附件的时候，往附件表MaiAttachment新增记录
            List<MaiAttachment> newMaiAttachmentList = new ArrayList<MaiAttachment>();
            if (null != maiAttachmentList) {
                if (!maiAttachmentList.isEmpty()) {
                    for (MaiAttachment attachment : maiAttachmentList) {
                        MaiAttachment maiAttachment = new MaiAttachment();
                        BeanUtils.copyProperties(attachment, maiAttachment);
                        maiAttachment.setSendId(0L);
                        maiAttachment.setReceiveId(0L);
                        maiAttachment.setDraftId(draftBox.getId());
                        newMaiAttachmentList.add(maiAttachment);
                    }
                }
            }
            if (null != newMaiAttachmentList && !newMaiAttachmentList.isEmpty()) {
                maiAttachmentService.insertOrUpdateBatch(newMaiAttachmentList);
            }
            
            retMsg.setCode(AppConstant.RET_CODE_SUCCESS);
            retMsg.setMessage("修改成功");
        } catch (Exception e) {
            logger.error("",e);
            retMsg.setCode(AppConstant.RET_CODE_ERROR);
            retMsg.setMessage(e.getMessage());
        }
        return retMsg;
    }

    @Override
    public AppMaiDraftBoxVO getById(MaiDraftBox obj) throws Exception {

        AppMaiDraftBoxVO maiDraftBoxVO = new AppMaiDraftBoxVO();
        if (null != obj) {
            if (null != obj.getId()) {
                Where<MaiDraftBox> w1 = new Where<MaiDraftBox>();               
                w1.eq("id", obj.getId());
                MaiDraftBox maiDraftBox = selectOne(w1);
                if (StringUtils.checkValNotNull(maiDraftBox)) {
                    maiDraftBoxVO.setMaiDraftBox(maiDraftBox);
                    /*SysParameter parameter = sysParameterService.selectBykey("ImgServer");
                    String rootPath = "";
                    if(null != parameter && StringUtils.isNotEmpty(parameter.getParamValue())){
                        rootPath = parameter.getParamValue();
                    } */                  
                    /*if(maiDraftBox.getSendUserId()!=null){
                    maiDraftBoxVO.setSendURL(getImg(rootPath,maiDraftBox.getSendUserId().toString()));          
                    }
                    if(maiDraftBox.getCopyUserIds()!=null && !"".equals(maiDraftBox.getCopyUserIds())){
                    maiDraftBoxVO.setCopyURL(getImg(rootPath,maiDraftBox.getCopyUserIds().toString()));
                    }
                    if(maiDraftBox.getReceiveUserIds()!=null && !"".equals(maiDraftBox.getReceiveUserIds())){
                    maiDraftBoxVO.setReceiveURL(getImg(rootPath,maiDraftBox.getReceiveUserIds().toString()));
                    }*/
                    Where<MaiAttachment> where = new Where<MaiAttachment>();
                    where.setSqlSelect("attach_name,attach_path,attach_size,send_id,receive_id,draft_id");
                    where.eq("draft_id", maiDraftBox.getId());
                    List<MaiAttachment> attachmentList = maiAttachmentService.selectList(where);
                      //List<MaiAttachment> returnList =new ArrayList<MaiAttachment>();                   
                        if (!attachmentList.isEmpty()) {
                            /*for (MaiAttachment maiAttachment : attachmentList) {
                                String tmp=maiAttachment.getAttachPath();
                              //  tmp=tmp.substring(0, tmp.length());
                                tmp=tmp.replaceAll("//","/");
                                String path=rootPath+tmp;
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
    public String getImg(String rootPath,String id) throws Exception{
        String path="";
        
        if(id.length()>0){
            String[] ids=id.split(";");
            for (int i = 0; i < ids.length; i++) {
             if(ids[i]!=null && !"".equals(ids[i])){
                if(!"".equals(rootPath)){
                  AutUserPub    autUserPub=autUserPubService.selectById(ids[i]);
                  if(autUserPub!=null && autUserPub.getUserIcon()!=null && !"".equals(autUserPub.getUserIcon())){
                      path=path+rootPath+autUserPub.getUserIcon()+";";                    
                   }else{
                    //默认地址
                    path=path+"web/images/0.jpg"+";";
                  }
                }else{
                    path=path+"web/images/0.jpg"+";";
                }   
              }
            }
        }       
        return path;
        
    }

}
