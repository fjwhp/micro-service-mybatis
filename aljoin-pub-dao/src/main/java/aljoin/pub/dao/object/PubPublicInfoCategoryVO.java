package aljoin.pub.dao.object;

import java.util.List;

import aljoin.pub.dao.entity.PubPublicInfoCategory;

/**
 * 
 * 公共信息分类表(实体类).
 * 
 * @author：sln.
 * 
 * @date： 2017-10-16
 */
public class PubPublicInfoCategoryVO extends PubPublicInfoCategory {

    private static final long serialVersionUID = 1L;

    private List<String> processCategory;

    public List<String> getProcessCategory() {
        return processCategory;
    }

    public void setProcessCategory(List<String> processCategory) {
        this.processCategory = processCategory;
    }
}
