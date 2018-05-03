package aljoin.mee.dao.object;

import java.util.List;

/**
 * 
 * 内部会议表(实体类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-10-12
 */
public class AppMeeMeetingCountDO {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private MeeMeetingCountDO meeMeetingCountDO;
    private List<MeeLeaderMeetingRoomCountDO> meeLeaderMeetingRoomCountDO;

    public List<MeeLeaderMeetingRoomCountDO> getMeeLeaderMeetingRoomCountDO() {
        return meeLeaderMeetingRoomCountDO;
    }

    public void setMeeLeaderMeetingRoomCountDO(List<MeeLeaderMeetingRoomCountDO> meeLeaderMeetingRoomCountDO) {
        this.meeLeaderMeetingRoomCountDO = meeLeaderMeetingRoomCountDO;
    }

    public MeeMeetingCountDO getMeeMeetingCountDO() {
        return meeMeetingCountDO;
    }

    public void setMeeMeetingCountDO(MeeMeetingCountDO meeMeetingCountDO) {
        this.meeMeetingCountDO = meeMeetingCountDO;
    }

}
