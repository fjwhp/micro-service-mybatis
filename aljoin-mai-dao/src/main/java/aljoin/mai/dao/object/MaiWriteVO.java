package aljoin.mai.dao.object;

import aljoin.dao.entity.Entity;
import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.entity.MaiSendBox;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 
 * 发件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiWriteVO extends Entity<MaiWriteVO> {

    private static final long serialVersionUID = 1L;

    /**
     * 收件箱
     */
    @ApiModelProperty(hidden = true)
    private MaiReceiveBox maiReceiveBox;
    @ApiModelProperty(hidden = true)
    private MaiReceiveBoxSearch maiReceiveBoxSearch;

    /**
     * fa件箱
     */
    @ApiModelProperty(hidden = true)
    private MaiSendBox maiSendBox;

    /**
     * 附件
     */
    @ApiModelProperty(hidden = true)
    private List<MaiAttachment> maiAttachmentList;
    
    /**
     * 草稿箱
     */
    @ApiModelProperty(hidden = true)
    private MaiDraftBox maiDraftBox;

    public MaiReceiveBox getMaiReceiveBox() {
        return maiReceiveBox;
    }

    public void setMaiReceiveBox(MaiReceiveBox maiReceiveBox) {
        this.maiReceiveBox = maiReceiveBox;
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

    public MaiDraftBox getMaiDraftBox() {
        return maiDraftBox;
    }

    public void setMaiDraftBox(MaiDraftBox maiDraftBox) {
        this.maiDraftBox = maiDraftBox;
    }

    public MaiReceiveBoxSearch getMaiReceiveBoxSearch() {
        return maiReceiveBoxSearch;
    }

    public void setMaiReceiveBoxSearch(MaiReceiveBoxSearch maiReceiveBoxSearch) {
        this.maiReceiveBoxSearch = maiReceiveBoxSearch;
    }

}
