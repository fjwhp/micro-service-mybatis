package aljoin.ioa.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;

/**
 * 
 * 收文阅件-传阅对象表(分类)(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-11-08
 */
public class IoaReceiveReadObjectDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据字典 code
     */
    private String dictCode;

    /**
     * 数据字典 name
     */
    private String dictName;

    /**
     * 数据字典 key
     */
    private String dictKey;

    /**
     * 数据字典 value
     */
    private String dictValue;

    /**
     * 数据字典 文本值
     */
    private String text;

    /**
     * 选中人员显示值
     */
    private String readUserNames;

    /**
     * 选中人员ID值
     */
    private String readUserIds;

    /**
     * 数据字典 ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 数据字典 key(部门下的子部门)
     */
    private String dictKeyDetail;
    /**
     * 是否选中
     */
    private String isCheck;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictKey() {
        return dictKey;
    }

    public void setDictKey(String dictKey) {
        this.dictKey = dictKey;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReadUserNames() {
        return readUserNames;
    }

    public void setReadUserNames(String readUserNames) {
        this.readUserNames = readUserNames;
    }

    public String getReadUserIds() {
        return readUserIds;
    }

    public void setReadUserIds(String readUserIds) {
        this.readUserIds = readUserIds;
    }

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
    }

    public String getDictKeyDetail() {
        return dictKeyDetail;
    }

    public void setDictKeyDetail(String dictKeyDetail) {
        this.dictKeyDetail = dictKeyDetail;
    }
}
