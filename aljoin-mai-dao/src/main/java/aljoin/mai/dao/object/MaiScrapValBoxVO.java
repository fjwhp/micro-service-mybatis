package aljoin.mai.dao.object;

import java.util.List;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiScrapBox;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class MaiScrapValBoxVO {

    private MaiScrapBox maiScrapBox;
    private String mailContent;
    private String copyUserNames;
    private String copyFullNames;
    private String copyUserIds;
    private String copyImg;
    private String rUserImg;
    private String sUserImg;

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String getCopyUserNames() {
        return copyUserNames;
    }

    public void setCopyUserNames(String copyUserNames) {
        this.copyUserNames = copyUserNames;
    }

    public String getCopyFullNames() {
        return copyFullNames;
    }

    public void setCopyFullNames(String copyFullNames) {
        this.copyFullNames = copyFullNames;
    }

    public String getCopyUserIds() {
        return copyUserIds;
    }

    public void setCopyUserIds(String copyUserIds) {
        this.copyUserIds = copyUserIds;
    }

    public String getCopyImg() {
        return copyImg;
    }

    public void setCopyImg(String copyImg) {
        this.copyImg = copyImg;
    }

    public String getrUserImg() {
        return rUserImg;
    }

    public void setrUserImg(String rUserImg) {
        this.rUserImg = rUserImg;
    }

    public String getsUserImg() {
        return sUserImg;
    }

    public void setsUserImg(String sUserImg) {
        this.sUserImg = sUserImg;
    }

    public MaiScrapBox getMaiScrapBox() {
        return maiScrapBox;
    }

    public void setMaiScrapBox(MaiScrapBox maiScrapBox) {
        this.maiScrapBox = maiScrapBox;
    }

    /**
     * 附件
     */
    private List<MaiAttachment> maiAttachmentList;

    public List<MaiAttachment> getMaiAttachmentList() {
        return maiAttachmentList;
    }

    public void setMaiAttachmentList(List<MaiAttachment> maiAttachmentList) {
        this.maiAttachmentList = maiAttachmentList;
    }

}
