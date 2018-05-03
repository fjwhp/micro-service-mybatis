package aljoin.mai.iservice;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.mai.dao.entity.MaiDraftBox;
import aljoin.mai.dao.object.MaiDraftBoxVO;
import aljoin.mai.dao.object.MaiWriteVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 草稿箱表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-09-20
 */
public interface MaiDraftBoxService extends IService<MaiDraftBox> {

    /**
     * 
     * 草稿箱表(分页列表).
     *
     * @return：Page<MaiDraftBox>
     *
     * @author：laijy
     *
     * @date：2017-09-20
     */
    public Page<MaiDraftBox> list(PageBean pageBean, MaiDraftBox obj, Long userId, String time1, String time2,
        String orderByTime) throws Exception;

    /**
     * 
     * 草稿箱-新增
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月27日 下午7:08:24
     */
    public RetMsg add(MaiWriteVO obj, Long userId, String userName, String nickName) throws Exception;

    /**
     * 
     * 草稿箱-更新（编辑）
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月27日 下午7:17:30
     */
    public RetMsg update(MaiWriteVO obj, Long userId, String userName, String nickName) throws Exception;

    /**
     * 
     * 草稿箱-详情
     *
     * @return：MaiDraftBoxVO
     *
     * @author：laijy
     *
     * @date：2017年9月27日 下午8:22:52
     */
    public MaiDraftBoxVO getById(MaiDraftBox obj) throws Exception;
}
