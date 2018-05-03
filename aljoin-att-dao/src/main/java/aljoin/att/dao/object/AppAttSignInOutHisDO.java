package aljoin.att.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 签到、退表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-09-27
 */
public class AppAttSignInOutHisDO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 上午签到补签状态
     */
    private Integer amSignInPatchStatus;

    /**
     * 上午签退补签状态
     */
    private Integer amSignOutPatchStatus;

    /**
     * 下午签到补签状态
     */
    private Integer pmSignInPatchStatus;

    /**
     * 下午签退补签状态
     */
    private Integer pmSignOutPatchStatus;

    /**
     * 补签原因
     */
    private String patchReason;

    /**
     * 状态
     */
    private String patchStatus;

    /**
     * 打卡信息
     */
    private String signPatchInfo;

    /**
     * 时间
     */
    private String signDateStr;

    /**
     * 补签时间
     */
    private String signPatchDate;

    /**
     * 签收状态
     */
    private Integer claimStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmSignInPatchStatus() {
        return amSignInPatchStatus;
    }

    public void setAmSignInPatchStatus(Integer amSignInPatchStatus) {
        this.amSignInPatchStatus = amSignInPatchStatus;
    }

    public Integer getAmSignOutPatchStatus() {
        return amSignOutPatchStatus;
    }

    public void setAmSignOutPatchStatus(Integer amSignOutPatchStatus) {
        this.amSignOutPatchStatus = amSignOutPatchStatus;
    }

    public Integer getPmSignInPatchStatus() {
        return pmSignInPatchStatus;
    }

    public void setPmSignInPatchStatus(Integer pmSignInPatchStatus) {
        this.pmSignInPatchStatus = pmSignInPatchStatus;
    }

    public Integer getPmSignOutPatchStatus() {
        return pmSignOutPatchStatus;
    }

    public void setPmSignOutPatchStatus(Integer pmSignOutPatchStatus) {
        this.pmSignOutPatchStatus = pmSignOutPatchStatus;
    }

    public String getPatchReason() {
        return patchReason;
    }

    public void setPatchReason(String patchReason) {
        this.patchReason = patchReason;
    }

    public String getPatchStatus() {
        return patchStatus;
    }

    public void setPatchStatus(String patchStatus) {
        this.patchStatus = patchStatus;
    }

    public String getSignPatchInfo() {
        return signPatchInfo;
    }

    public void setSignPatchInfo(String signPatchInfo) {
        this.signPatchInfo = signPatchInfo;
    }

    public String getSignDateStr() {
        return signDateStr;
    }

    public void setSignDateStr(String signDateStr) {
        this.signDateStr = signDateStr;
    }

    public String getSignPatchDate() {
        return signPatchDate;
    }

    public void setSignPatchDate(String signPatchDate) {
        this.signPatchDate = signPatchDate;
    }

    public Integer getClaimStatus() {
        return claimStatus;
    }

    public void setClaimStatus(Integer claimStatus) {
        this.claimStatus = claimStatus;
    }
}
