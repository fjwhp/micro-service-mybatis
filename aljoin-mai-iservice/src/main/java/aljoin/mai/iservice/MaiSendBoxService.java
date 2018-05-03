package aljoin.mai.iservice;

import java.util.List;

import javax.servlet.http.HttpServletResponse;


import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.entity.AutUser;
import aljoin.mai.dao.entity.MaiSendBox;
import aljoin.mai.dao.object.MaiSendBoxCountDO;
import aljoin.mai.dao.object.MaiSendBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 发件箱表(服务类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
public interface MaiSendBoxService extends IService<MaiSendBox> {

    /**
     * 
     * 发件箱表(分页列表).
     *
     * @return：Page<MaiSendBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    public Page<MaiSendBox> list(PageBean pageBean, MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 撰写邮件
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public RetMsg add(MaiWriteVO obj, AutUser customUser) throws Exception;

    /**
     *
     * 发件箱详情
     *
     * @return：MaiReceiveBoxVO
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public MaiSendBoxVO detail(MaiSendBox obj, Long userId, String userName, String nickName) throws Exception;

    /**
     *
     * 发件箱删除
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public RetMsg delete(MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 发件箱撤回并删除
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public RetMsg revokeAndDelete(MaiSendBox obj) throws Exception;

    /**
     *
     * 发件箱撤回并删除
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public RetMsg update(MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 撰写邮件上传文件
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月25日
     */
    /*public RetMsg upload(MultipartHttpServletRequest request, boolean async, String fileModuleName) throws Exception;*/

    /**
     *
     * 邮件统计
     *
     * @return：List<MaiSendBoxCountDO>
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public List<MaiSendBoxCountDO> mailCountList(MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 邮件统计导出
     *
     * @return：void
     *
     * @author：wangj
     *
     * @date：2017年10月17日
     */
    public void export(HttpServletResponse response, MaiSendBoxVO obj) throws Exception;

    /**
     *
     * 邮件统计图标
     *
     * @return：List<MaiSendBoxCountDO>
     *
     * @author：wangj
     *
     * @date：2017年10月18日
     */
    public List<MaiSendBoxCountDO> mailChartList(MaiSendBoxVO obj) throws Exception;

    /**
     *
     * APP发件箱详情
     *
     * @return：MaiReceiveBoxVO
     *
     * @author：
     *
     * @date：2017年11月07日
     */
    public MaiSendBoxVO appdetail(MaiSendBox obj, AutUser user) throws Exception;

    /**
     * 
     * 发件箱表(分页列表).
     *
     * @return：Page<MaiSendBox>
     *
     * @author：hw
     *
     * @date：2017-11-07
     */
    public Page<MaiSendBox> applist(PageBean pageBean, MaiSendBox obj, String id) throws Exception;

    /**
     * 
     * 发件箱表(分页列表).
     *
     * @return：Page<MaiSendBox>
     *
     * @author：hw
     *
     * @date：2017-11-07
     */
    public RetMsg appAddMai(MaiWriteVO obj, AutUser customUser) throws Exception;

    /**
     * 
     * @throws Exception 发件箱表APP删除恢复
     *
     * @return：RetMsg
     *
     * @author：hw
     *
     * @date：2017-11-14
     */
    public RetMsg aPPrevokeAndDelete(MaiSendBox obj, String userid, String username) throws Exception;

    /**
     * 
     * @throws Exception 发件箱表APP删除恢复
     *
     * @return：RetMsg
     *
     * @author：hw
     *
     * @date：2017-11-14
     */
    public RetMsg appdelete(MaiSendBoxVO obj, Long userid, String userName) throws Exception;

    /**
     * 
     * @throws Exception 供会议调用推送邮件
     *
     * @return：RetMsg
     *
     * @author：xuc
     *
     * @date：2017年12月8日 下午4:24:25
     */

    public RetMsg add4Push(MaiWriteVO mai, AutUser createuser, List<AutDepartmentUser> departmentUserList,
        List<MaiSendBox> maiSendBoxList) throws Exception;

}
