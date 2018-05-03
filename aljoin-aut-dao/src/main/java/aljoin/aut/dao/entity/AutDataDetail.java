package aljoin.aut.dao.entity;

import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 
 * (实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-12-14
 */
public class AutDataDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    /**
     * 用户ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;
    /**
     * 统计数据
     */
    private Integer dataCount;
    /**
     * 数据类型
     */
    private String dataType;

    public Long getId() {
        return id;
    }

    public AutDataDetail setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AutDataDetail setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public AutDataDetail setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
        return this;
    }

    public String getDataType() {
        return dataType;
    }

    public AutDataDetail setDataType(String dataType) {
        this.dataType = dataType;
        return this;
    }

}
