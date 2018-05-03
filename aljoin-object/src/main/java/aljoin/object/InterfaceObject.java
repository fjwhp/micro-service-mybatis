package aljoin.object;

import java.util.List;

/**
 * 
 * 接口对象
 *
 * @author：zhongjy
 * 
 * @date：2017年8月4日 下午4:53:02
 */
public class InterfaceObject {

    private String interfaceName;
    private String interfaceAddress;
    private List<InterfaceParam> paramList;
    private Object retObj;
    private String interfaceVersion;
    private String retEncryptProps;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getInterfaceAddress() {
        return interfaceAddress;
    }

    public void setInterfaceAddress(String interfaceAddress) {
        this.interfaceAddress = interfaceAddress;
    }

    public List<InterfaceParam> getParamList() {
        return paramList;
    }

    public void setParamList(List<InterfaceParam> paramList) {
        this.paramList = paramList;
    }

    public Object getRetObj() {
        return retObj;
    }

    public void setRetObj(Object retObj) {
        this.retObj = retObj;
    }

    public String getInterfaceVersion() {
        return interfaceVersion;
    }

    public void setInterfaceVersion(String interfaceVersion) {
        this.interfaceVersion = interfaceVersion;
    }

    public String getRetEncryptProps() {
        return retEncryptProps;
    }

    public void setRetEncryptProps(String retEncryptProps) {
        this.retEncryptProps = retEncryptProps;
    }

}
