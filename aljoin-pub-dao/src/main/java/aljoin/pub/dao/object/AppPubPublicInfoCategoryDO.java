package aljoin.pub.dao.object;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 
 * 公共信息分类表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-16
 */
public class AppPubPublicInfoCategoryDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @ApiModelProperty(value = " 分类Id", hidden = true)
    public Long id;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = " 分类名称", hidden = true)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
