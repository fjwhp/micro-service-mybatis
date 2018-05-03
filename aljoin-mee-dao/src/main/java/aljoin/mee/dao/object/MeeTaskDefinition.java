package aljoin.mee.dao.object;

import java.io.Serializable;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class MeeTaskDefinition implements Serializable {
    /**
     * TODO
     */
    private static final long serialVersionUID = -5638886361639219698L;
    private String key;
    private String nextNodeName;
    private String assignee;
    private String businessCalendarNameExpression;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNextNodeName() {
        return nextNodeName;
    }

    public void setNextNodeName(String nextNodeName) {
        this.nextNodeName = nextNodeName;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getBusinessCalendarNameExpression() {
        return businessCalendarNameExpression;
    }

    public void setBusinessCalendarNameExpression(String businessCalendarNameExpression) {
        this.businessCalendarNameExpression = businessCalendarNameExpression;
    }
}
