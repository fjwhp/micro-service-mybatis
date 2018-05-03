package aljoin.mai.dao.entity;

import aljoin.dao.entity.Entity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * 邮箱附件表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiAttachment extends Entity<MaiAttachment> {

    private static final long serialVersionUID = 1L;

    /**
     * 附件名称
     */
    private String attachName;
    /**
     * 附件路径
     */
    private String attachPath;
    /**
     * 附件大小KB
     */
    private Integer attachSize;
    /**
     * 发件箱ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long sendId;
    /**
     * 收件箱ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long receiveId;
    /**
     * 草稿箱ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long draftId;

    public String getAttachName() {
        return attachName;
    }

    public MaiAttachment setAttachName(String attachName) {
        this.attachName = attachName;
        return this;
    }

    public String getAttachPath() {
        return attachPath;
    }

    public MaiAttachment setAttachPath(String attachPath) {
        this.attachPath = attachPath;
        return this;
    }

    public Integer getAttachSize() {
        return attachSize;
    }

    public MaiAttachment setAttachSize(Integer attachSize) {
        this.attachSize = attachSize;
        return this;
    }

    public Long getSendId() {
        return sendId;
    }

    public MaiAttachment setSendId(Long sendId) {
        this.sendId = sendId;
        return this;
    }

    public Long getReceiveId() {
        return receiveId;
    }

    public MaiAttachment setReceiveId(Long receiveId) {
        this.receiveId = receiveId;
        return this;
    }

    public Long getDraftId() {
        return draftId;
    }

    public MaiAttachment setDraftId(Long draftId) {
        this.draftId = draftId;
        return this;
    }

}
