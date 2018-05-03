package aljoin.web.javaBean;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.ioa.dao.object.DoTaskShowVO;
import aljoin.ioa.dao.object.WaitTaskShowVO;
import aljoin.mai.dao.object.MaiReceiveBoxListVO;
import aljoin.object.RetMsg;
import aljoin.off.dao.entity.OffScheduling;
import aljoin.pub.dao.entity.PubPublicInfoCategory;
import aljoin.pub.dao.object.PubPublicInfoDO;

public class IndexDataBean {
    // 1.首页签到签退显示文本att/attSignInOut/getSignInOutStr
    private RetMsg getSignInOutStr;
    // 2.首页模块定制表(初始化首页及个性定制页数据).per/autIndexPageModule/init
    private RetMsg init;
    // 3.在线通知 待办 公告 文件 统计aut/autDataStatistics/indexCount
    private RetMsg indexCount;
    // 4.首页会议列表接口off/offScheduling/dateMeet
    private List<OffScheduling> dateMeet;
    // 5.检查未读消息数量aut/autDataStatistics/checkMsg
    private RetMsg checkMsg;
    // 6.首页月日程接口off/offScheduling/monthMeet
    private List<String> monthMeet;
    // 7.收件箱分页列表接口mai/maiReceiveBox/list
    private Page<MaiReceiveBoxListVO> list;
    // 8.公共信息分类列表pub/pubPublicInfoCategory/allList
    private List<PubPublicInfoCategory> allList;
    // 9.最新信息分页列表pub/pubPublicInfo/lastList
    private Page<PubPublicInfoDO> lastList;
    // 10.待办列表ioa/ioaWaitWork/selectWaitTask
    private Page<WaitTaskShowVO> selectWaitTask;
    // 11.在办列表ioa/ioaDoingWork/selectDoTask
    private Page<DoTaskShowVO> selectDoTask;
    // 12.通讯录查询per/autUserPub/searchquery
    private List<AutUserPubVO> searchquery;

    public RetMsg getGetSignInOutStr() {
        return getSignInOutStr;
    }

    public void setGetSignInOutStr(RetMsg getSignInOutStr) {
        this.getSignInOutStr = getSignInOutStr;
    }

    public RetMsg getInit() {
        return init;
    }

    public void setInit(RetMsg init) {
        this.init = init;
    }

    public RetMsg getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(RetMsg indexCount) {
        this.indexCount = indexCount;
    }

    public List<OffScheduling> getDateMeet() {
        return dateMeet;
    }

    public void setDateMeet(List<OffScheduling> dateMeet) {
        this.dateMeet = dateMeet;
    }

    public RetMsg getCheckMsg() {
        return checkMsg;
    }

    public void setCheckMsg(RetMsg checkMsg) {
        this.checkMsg = checkMsg;
    }

    public List<String> getMonthMeet() {
        return monthMeet;
    }

    public void setMonthMeet(List<String> monthMeet) {
        this.monthMeet = monthMeet;
    }

    public Page<MaiReceiveBoxListVO> getList() {
        return list;
    }

    public void setList(Page<MaiReceiveBoxListVO> list) {
        this.list = list;
    }

    public List<PubPublicInfoCategory> getAllList() {
        return allList;
    }

    public void setAllList(List<PubPublicInfoCategory> allList) {
        this.allList = allList;
    }

    public Page<PubPublicInfoDO> getLastList() {
        return lastList;
    }

    public void setLastList(Page<PubPublicInfoDO> lastList) {
        this.lastList = lastList;
    }

    public Page<WaitTaskShowVO> getSelectWaitTask() {
        return selectWaitTask;
    }

    public void setSelectWaitTask(Page<WaitTaskShowVO> selectWaitTask) {
        this.selectWaitTask = selectWaitTask;
    }

    public Page<DoTaskShowVO> getSelectDoTask() {
        return selectDoTask;
    }

    public void setSelectDoTask(Page<DoTaskShowVO> selectDoTask) {
        this.selectDoTask = selectDoTask;
    }

    public List<AutUserPubVO> getSearchquery() {
        return searchquery;
    }

    public void setSearchquery(List<AutUserPubVO> searchquery) {
        this.searchquery = searchquery;
    }

}
