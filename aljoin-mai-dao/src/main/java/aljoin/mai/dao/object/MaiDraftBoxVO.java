package aljoin.mai.dao.object;

import java.util.List;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class MaiDraftBoxVO {

    private List<MaiDraftBox> draftBoxList;

    private MaiDraftBox maiDraftBox;

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
