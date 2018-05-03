 package aljoin.mai.dao.object;

import aljoin.mai.dao.entity.MaiReceiveBoxSearch;

public class MaiReceiveBoxListVO extends MaiReceiveBoxSearch{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 邮件大小(KB,正文+附件的大小)
     */
    private Integer mailSize;

    public Integer getMailSize() {
        return mailSize;
    }

    public void setMailSize(Integer mailSize) {
        this.mailSize = mailSize;
    }
}
