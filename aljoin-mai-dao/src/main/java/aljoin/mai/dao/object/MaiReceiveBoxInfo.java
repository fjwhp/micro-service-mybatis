package aljoin.mai.dao.object;

import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;

/**
 * 收件箱信息表(收件箱 + 收件箱search信息).
 * 
 * @author sunlinan
 * @date 2018年4月2日
 */
public class MaiReceiveBoxInfo {

    /**
     * 收件箱
     */
    private MaiReceiveBox maiReceiveBox;

    /**
     * 收件箱search
     */
    private MaiReceiveBoxSearch maiReceiveBoxSearch;

    public MaiReceiveBox getMaiReceiveBox() {
        return maiReceiveBox;
    }

    public void setMaiReceiveBox(MaiReceiveBox maiReceiveBox) {
        this.maiReceiveBox = maiReceiveBox;
    }

    public MaiReceiveBoxSearch getMaiReceiveBoxSearch() {
        return maiReceiveBoxSearch;
    }

    public void setMaiReceiveBoxSearch(MaiReceiveBoxSearch maiReceiveBoxSearch) {
        this.maiReceiveBoxSearch = maiReceiveBoxSearch;
    }

}
