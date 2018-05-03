package aljoin.object;

/**
 * 
 * 参数对象
 *
 * @author：zhongjy
 * 
 * @date：2017年8月4日 下午4:53:02
 */
public class InterfaceParam {

    private String paramName;
    private String paramType;
    private String allowNull;
    private String paramDesc;
    private String isEncrypt;

    public InterfaceParam(String paramName, String paramType, String allowNull, String paramDesc, String isEncrypt) {
        super();
        this.paramName = paramName;
        this.paramType = paramType;
        this.allowNull = allowNull;
        this.paramDesc = paramDesc;
        this.isEncrypt = isEncrypt;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getAllowNull() {
        return allowNull;
    }

    public void setAllowNull(String allowNull) {
        this.allowNull = allowNull;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getIsEncrypt() {
        return isEncrypt;
    }

    public void setIsEncrypt(String isEncrypt) {
        this.isEncrypt = isEncrypt;
    }

}
