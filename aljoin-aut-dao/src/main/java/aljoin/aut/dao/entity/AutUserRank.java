package aljoin.aut.dao.entity;

import java.math.BigDecimal;
import aljoin.dao.entity.Entity;

/**
 * 
 * 人员排序表(实体类).
 * 
 * @author：huanghz.
 * 
 * @date： 2017-12-13
 */
public class AutUserRank extends Entity<AutUserRank> {

    private static final long serialVersionUID = 1L;

    /**
     * 人员排序
     */
    private BigDecimal userRank;

    public BigDecimal getUserRank() {
        return userRank;
    }

    public AutUserRank setUserRank(BigDecimal userRank) {
        this.userRank = userRank;
        return this;
    }

}
