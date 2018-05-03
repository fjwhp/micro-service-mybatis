package aljoin.mai.dao.object;

import java.util.List;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AppMaiDraftBoxVO {

    private List<MaiDraftBox> draftBoxList;

    private MaiDraftBox maiDraftBox;

    private String copyURL;
    private String sendURL;
    private String receiveURL;

    public String getCopyURL() {
        return copyURL;
    }

    public void setCopyURL(String copyURL) {
        this.copyURL = copyURL;
    }

    public String getSendURL() {
        return sendURL;
    }

    public void setSendURL(String sendURL) {
        this.sendURL = sendURL;
    }

    public String getReceiveURL() {
        return receiveURL;
    }

    public void setReceiveURL(String receiveURL) {
        this.receiveURL = receiveURL;
    }

    /**
     * 附件
     */
    private List<MaiAttachment> maiAttachmentList;

    public List<MaiDraftBox> getDraftBoxList() {
        return draftBoxList;
    }

    public void setDraftBoxList(List<MaiDraftBox> draftBoxList) {
        this.draftBoxList = draftBoxList;
    }

    public MaiDraftBox getMaiDraftBox() {
        return maiDraftBox;
    }

    public void setMaiDraftBox(MaiDraftBox maiDraftBox) {
        this.maiDraftBox = maiDraftBox;
    }

    public List<MaiAttachment> getMaiAttachmentList() {
        return maiAttachmentList;
    }

    public void setMaiAttachmentList(List<MaiAttachment> maiAttachmentList) {
        this.maiAttachmentList = maiAttachmentList;
    }

}
