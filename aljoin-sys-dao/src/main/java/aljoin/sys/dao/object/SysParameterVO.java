package aljoin.sys.dao.object;

import java.util.Map;

import aljoin.sys.dao.entity.SysParameter;

/**
 * 
 * 系统参数表(实体类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-12
 */
public class SysParameterVO extends SysParameter {

    private static final long serialVersionUID = 1L;

    private Map<String, String> params;

    private String fullNames;

    public String getFullNames() {
        return fullNames;
    }

    public void setFullNames(String fullNames) {
        this.fullNames = fullNames;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
