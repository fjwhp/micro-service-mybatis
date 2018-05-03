package aljoin.sma.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;

import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.sma.dao.entity.LeaStatisticsModule;
import aljoin.sys.dao.entity.SysParameter;
import aljoin.sys.dao.object.SysParameterVO;

/**
 * 
 * 系统维护(服务类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-10-12
 */
public interface SystemMaintainService {

    /**
     *
     * 系统设置
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public RetMsg sysSet(SysParameterVO obj) throws Exception;

    /**
     *
     * 系统设置详情
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public List<SysParameter> sysSetDetail(SysParameterVO obj) throws Exception;

    /**
     *
     * 领导会议设置
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public RetMsg leaderMetSet(SysParameter obj) throws Exception;

    /**
     *
     * 领导会议设置详情
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public SysParameterVO leaderMetSetDetail(SysParameter obj) throws Exception;

    /**
     *
     * 考勤打卡设置
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public RetMsg attCardSet(SysParameterVO obj) throws Exception;

    /**
     *
     * 考勤打卡设置详情
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public List<SysParameter> attCardSetDetail(SysParameterVO obj) throws Exception;

    /**
     *
     * 不参与考勤统计人员设置详情
     *
     * @return：String
     *
     * @author：sln
     *
     * @date：2017年10月18日
     */
    public SysParameterVO attCountPersonDetail(SysParameterVO obj) throws Exception;

    /**
     *
     * 不参与考勤统计人员设置（新增或更新）
     *
     * @return：String
     *
     * @author：sln
     *
     * @date：2017年10月18日
     */
    public RetMsg addOrUpdateAttcountPerson(SysParameterVO obj) throws Exception;

    public Page<LeaStatisticsModule> selectUser(String userid, PageBean pb) throws Exception;

    public RetMsg updateModule(LeaStatisticsModule module, Long userId) throws Exception;
}
