 package aljoin.sys.dao.object;

import aljoin.sys.dao.entity.SysSerialNumber;

/**
  * @作者：caizx
  * 
  * @时间: 2018-03-25
  */
public class SysSerialNumberVO extends SysSerialNumber{

    /**
     *TODO
     */
    private static final long serialVersionUID = 642064306798146812L;
    
    /**
     * 流水号名称
     */
    private String serialNum;
    
    /**
     * 流程名称
     */
    private String processNames;
    
    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getProcessNames() {
        return processNames;
    }

    public void setProcessNames(String processNames) {
        this.processNames = processNames;
    }
    
}
