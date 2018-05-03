package aljoin.res.dao.object;

import java.util.List;

import aljoin.res.dao.entity.ResResource;
import aljoin.res.dao.entity.ResResourceType;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class ResResourceVO {

    private ResResource resResource;

    private ResResourceType resResourceType;

    private List<ResResource> resResourceList;

    private List<ResResourceType> resResourceTypeList;
    
    private String orgnlFileName;
    
    private String fileDesc;

    public ResResource getResResource() {
        return resResource;
    }

    public void setResResource(ResResource resResource) {
        this.resResource = resResource;
    }

    public ResResourceType getResResourceType() {
        return resResourceType;
    }

    public void setResResourceType(ResResourceType resResourceType) {
        this.resResourceType = resResourceType;
    }

    public List<ResResource> getResResourceList() {
        return resResourceList;
    }

    public void setResResourceList(List<ResResource> resResourceList) {
        this.resResourceList = resResourceList;
    }

    public List<ResResourceType> getResResourceTypeList() {
        return resResourceTypeList;
    }

    public void setResResourceTypeList(List<ResResourceType> resResourceTypeList) {
        this.resResourceTypeList = resResourceTypeList;
    }

    public String getOrgnlFileName() {
        return orgnlFileName;
    }

    public void setOrgnlFileName(String orgnlFileName) {
        this.orgnlFileName = orgnlFileName;
    }

    public String getFileDesc() {
        return fileDesc;
    }

    public void setFileDesc(String fileDesc) {
        this.fileDesc = fileDesc;
    }
    
}
