package aljoin.ioa.dao.object;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class DraftListVO {
    /**
     * 表单类型
     */
    @ApiModelProperty(hidden = true)
    private String type;
    /**
     * 表单类型
     */
    @ApiModelProperty(hidden = true)
    private String typeID;
    /**
     * 表单名称
     */
    @ApiModelProperty(hidden = true)
    private String typeName;
    /**
     * 创建时间
     */
    @ApiModelProperty(hidden = true)
    private Date statrTime;
    /**
     * 结束时间
     */
    @ApiModelProperty(hidden = true)
    private Date endTime;
    /**
     * 创建人
     */
    @ApiModelProperty(hidden = true)
    private String userID;
    @ApiModelProperty(hidden = true)
    private String userName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getStatrTime() {
        return statrTime;
    }

    public void setStatrTime(Date statrTime) {
        this.statrTime = statrTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
