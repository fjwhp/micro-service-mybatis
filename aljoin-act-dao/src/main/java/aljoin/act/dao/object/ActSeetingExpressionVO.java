package aljoin.act.dao.object;

import java.io.Serializable;

/**
 * @author：wangj
 * @date：2018年04月23日
 */
public class ActSeetingExpressionVO implements Serializable{
    /**
     * 域Id
     */
    private String Id;
    /**
     * 域名称
     */
    private String name;
    /**
     * 比较符名称
     */
    private String comparisonName;

    /**
     * 比较符值
     */
    private String comparisonValue;

    /**
     * 比较值
     */
    private String value;

    /**
     * 关系Key
     */
    private String relationShipValue;

    /**
     * 关系
     */
    private String relationShipName;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComparisonName() {
        return comparisonName;
    }

    public void setComparisonName(String comparisonName) {
        this.comparisonName = comparisonName;
    }

    public String getComparisonValue() {
        return comparisonValue;
    }

    public void setComparisonValue(String comparisonValue) {
        this.comparisonValue = comparisonValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRelationShipValue() {
        return relationShipValue;
    }

    public void setRelationShipValue(String relationShipValue) {
        this.relationShipValue = relationShipValue;
    }

    public String getRelationShipName() {
        return relationShipName;
    }

    public void setRelationShipName(String relationShipName) {
        this.relationShipName = relationShipName;
    }
}
