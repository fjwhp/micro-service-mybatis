package aljoin.act.dao.object;

import aljoin.act.dao.entity.ActAljoinForm;
import aljoin.act.dao.entity.ActAljoinFormCategory;

/**
 * 
 * @author zhongjy
 * @date 2018年2月8日
 */
public class ActAljoinFormCategoryVO extends ActAljoinForm {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 存放表单
     */
    ActAljoinForm form;
    /**
     * 存放表单类型名称
     */
    ActAljoinFormCategory category;

    public ActAljoinForm getForm() {
        return form;
    }

    public void setForm(ActAljoinForm form) {
        this.form = form;
    }

    public ActAljoinFormCategory getCategory() {
        return category;
    }

    public void setCategory(ActAljoinFormCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ActAljoinFormCategoryVO [form=" + form + ", category=" + category + "]";
    }

}
