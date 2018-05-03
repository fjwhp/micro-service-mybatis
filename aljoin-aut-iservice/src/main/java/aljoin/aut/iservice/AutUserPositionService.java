package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUserPosition;
import aljoin.aut.dao.object.AutUserPositionVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 用户-岗位表(服务类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-01
 */
public interface AutUserPositionService extends IService<AutUserPosition> {

    /**
     * 
     * 用户-岗位表(分页列表).
     *
     * @return：Page<AutUserPosition>
     *
     * @author：laijy
     *
     * @date：2017-09-01
     */
    public Page<AutUserPosition> list(PageBean pageBean, AutUserPosition obj) throws Exception;

    /**
     * 
     * 前端执行新增的时候，拼接传过来的AutUser对象和List<AutPosition>
     *
     * @return：List<AutUserPosition>
     *
     * @author：laijy
     *
     * @date：2017年9月1日 下午1:32:48
     */
    public List<AutUserPosition> addUserPositionList(AutUserPositionVO userPositionVO) throws Exception;

    /**
     * 
     * 根据userId查询AutUserPosition(id,position_id,is_active)
     *
     * @return：List<AutUserPosition>
     *
     * @author：laijy
     *
     * @date：2017年9月1日 下午3:41:27
     */
    public List<AutUserPosition> getPositoinByUserId(AutUserPosition obj) throws Exception;

    /**
     * 
     * 比较用户新增岗位时，是否之前已分配
     *
     * @return：Boolean
     *
     * @author：laijy
     *
     * @date：2017年9月1日 下午4:20:25
     */
    public Boolean compareUserPosition(AutUserPosition obj) throws Exception;

    /**
     * 
     * 用户新增岗位
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月1日 下午4:51:07
     */
    public RetMsg addUserPosition(AutUserPosition obj) throws Exception;

    /**
     * 
     * 删除用户的岗位
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月4日 上午9:03:05
     */
    public RetMsg deleteUserPosition(AutUserPosition obj) throws Exception;

}
