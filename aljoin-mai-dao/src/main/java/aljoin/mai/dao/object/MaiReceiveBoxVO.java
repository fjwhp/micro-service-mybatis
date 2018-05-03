package aljoin.mai.dao.object;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import aljoin.mai.dao.entity.MaiAttachment;
import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * 收件箱表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-20
 */
public class MaiReceiveBoxVO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    /**
     * 收件箱
     */
    @ApiModelProperty(hidden = true)
    private MaiReceiveBox maiReceiveBox;

    /**
     * 收件箱
     */
    @ApiModelProperty(hidden = true)
    private MaiReceiveBoxSearch maiReceiveBoxSearch;
    
    /**
     * 附件
     */
    @ApiModelProperty(hidden = true)
    private List<MaiAttachment> maiAttachmentList;

    /**
     * 当前登录用户ID
     */
    @ApiModelProperty(hidden = true)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 当前登录用户ID
     */
    @ApiModelProperty(hidden = true)
    private String userName;

    /**
     * 当前登录用户ID
     */
    @ApiModelProperty(hidden = true)
    private String fullName;

    /**
     * 选中记录的ID(多个以逗号分隔)
     */
    @ApiModelProperty(hidden = true)
    private String ids;

    /**
     * 开始发送时间
     */
    @ApiModelProperty(hidden = true)
    private String begSendTime;

    /**
     * 结束发送时间
     */
    @ApiModelProperty(hidden = true)
    private String endSendTime;

    /**
     * 发送时间排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String sendTimeSort;

    /**
     * 附件大小排序(1:降序 其它：升序)
     */
    @ApiModelProperty(hidden = true)
    private String maiSizeSort;

    public MaiReceiveBox getMaiReceiveBox() {
        return maiReceiveBox;
    }

    public void setMaiReceiveBox(MaiReceiveBox maiReceiveBox) {
        this.maiReceiveBox = maiReceiveBox;
    }

    public MaiReceiveBoxSearch getMaiReceiveBoxSearch() {
        return maiReceiveBoxSearch;
    }

    public void setMaiReceiveBoxSearch(MaiReceiveBoxSearch maiReceiveBoxSearch) {
        this.maiReceiveBoxSearch = maiReceiveBoxSearch;
    }

    public List<MaiAttachment> getMaiAttachmentList() {
        return maiAttachmentList;
    }

    public void setMaiAttachmentList(List<MaiAttachment> maiAttachmentList) {
        this.maiAttachmentList = maiAttachmentList;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getBegSendTime() {
        return begSendTime;
    }

    public void setBegSendTime(String begSendTime) {
        this.begSendTime = begSendTime;
    }

    public String getEndSendTime() {
        return endSendTime;
    }

    public void setEndSendTime(String endSendTime) {
        this.endSendTime = endSendTime;
    }

    public String getSendTimeSort() {
        return sendTimeSort;
    }

    public void setSendTimeSort(String sendTimeSort) {
        this.sendTimeSort = sendTimeSort;
    }

    public String getMaiSizeSort() {
        return maiSizeSort;
    }

    public void setMaiSizeSort(String maiSizeSort) {
        this.maiSizeSort = maiSizeSort;
    }

}
