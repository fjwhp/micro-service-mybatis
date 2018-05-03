package aljoin.mai.dao.object;

import java.util.List;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiSendBox;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 发件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class AppMaiSendBoxVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 发件箱
     */
    @ApiModelProperty(hidden = true)
    private MaiSendBox maiSendBox;

    /**
     * 附件
     */
    @ApiModelProperty(hidden = true)
    private List<MaiAttachment> maiAttachmentList;

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

    public MaiSendBox getMaiSendBox() {
        return maiSendBox;
    }

    public void setMaiSendBox(MaiSendBox maiSendBox) {
        this.maiSendBox = maiSendBox;
    }

    public List<MaiAttachment> getMaiAttachmentList() {
        return maiAttachmentList;
    }

    public void setMaiAttachmentList(List<MaiAttachment> maiAttachmentList) {
        this.maiAttachmentList = maiAttachmentList;
    }

}
