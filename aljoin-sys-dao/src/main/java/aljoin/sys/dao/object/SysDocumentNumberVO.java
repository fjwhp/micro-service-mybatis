 package aljoin.sys.dao.object;

import aljoin.sys.dao.entity.SysDocumentNumber;

/**
 * @作者：caizx
 * 
 * @时间: 2018-03-26
 */
public class SysDocumentNumberVO extends SysDocumentNumber{

    /**
     *TODO
     */
    private static final long serialVersionUID = 8083634966402090638L;
    
    /**
     * 文号格式
     */
    private String[] docNumPattern;
    
    /**
     * 文号
     */
    private String documentNum;
    /**
     * 流程名称
     */
    private String processNames;
    
    public String getDocumentNum() {
        return documentNum;
    }

    public void setDocumentNum(String documentNum) {
        this.documentNum = documentNum;
    }

    public String[] getDocNumPattern() {
        return docNumPattern;
    }

    public void setDocNumPattern(String[] docNumPattern) {
        this.docNumPattern = docNumPattern;
    }

    public String getProcessNames() {
        return processNames;
    }

    public void setProcessNames(String processNames) {
        this.processNames = processNames;
    }

}
