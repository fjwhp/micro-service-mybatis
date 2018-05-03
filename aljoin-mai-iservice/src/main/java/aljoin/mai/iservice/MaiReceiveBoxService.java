package aljoin.mai.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.mai.dao.entity.MaiReceiveBox;
import aljoin.mai.dao.entity.MaiReceiveBoxSearch;
import aljoin.mai.dao.object.MaiReceiveBoxListVO;
import aljoin.mai.dao.object.MaiReceiveBoxVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 收件箱表(服务类).
 * 
 * @author：wangj
 * 
 *               @date： 2017-09-20
 */
public interface MaiReceiveBoxService extends IService<MaiReceiveBox> {

    /**
     * 
     * 收件箱表(分页列表).
     *
     * @return：Page<MaiReceiveBox>
     *
     * @author：wangj
     *
     * @date：2017-09-20
     */
    public Page<MaiReceiveBoxListVO> list(PageBean pageBean, MaiReceiveBoxVO obj) throws Exception;

    /**
     *
     * 收件箱新增
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月20日
     */
    public RetMsg add(MaiReceiveBoxVO obj) throws Exception;

    /**
     *
     * 收件箱详情
     *
     * @return：MaiReceiveBoxVO
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public MaiReceiveBoxVO detail(MaiReceiveBox objVo, Long userId, String userName, String nickName) throws Exception;

    /**
     *
     * 收件箱删除
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public RetMsg delete(MaiReceiveBoxVO obj) throws Exception;

    /**
     *
     * 收件箱撤回并删除
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月21日
     */
    public RetMsg revokeAndDelete(MaiReceiveBox obj) throws Exception;

    /**
     * 收件箱标记为已读
     * 
     * @param obj
     * @param userId
     * @param userName
     * @param nickName
     * @return
     * @throws Exception
     */
    public RetMsg sign(MaiReceiveBoxVO obj, Long userId, String userName, String nickName) throws Exception;

    /**
     *
     * 收件箱标记是否重要
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月28日
     */
    public RetMsg signImport(MaiReceiveBoxSearch obj) throws Exception;


}
