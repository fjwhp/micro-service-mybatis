package aljoin.aut.dao.object;

import aljoin.aut.dao.entity.AutCard;
import aljoin.aut.dao.entity.AutCardCategory;

/**
 * 
 * @author zhongjy
 * @date 2018年2月9日
 */
public class AutCardVO {

    private AutCard autCard;

    private AutCardCategory autCardCategory;

    public AutCard getAutCard() {
        return autCard;
    }

    public void setAutCard(AutCard autCard) {
        this.autCard = autCard;
    }

    public AutCardCategory getAutCardCategory() {
        return autCardCategory;
    }

    public void setAutCardCategory(AutCardCategory autCardCategory) {
        this.autCardCategory = autCardCategory;
    }

}
