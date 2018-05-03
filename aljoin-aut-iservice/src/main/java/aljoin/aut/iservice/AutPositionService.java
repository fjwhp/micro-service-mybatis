package aljoin.aut.iservice;

import aljoin.aut.dao.entity.AutPosition;
import aljoin.aut.dao.object.AutPositionDO;
import aljoin.aut.dao.object.AutPositionVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * 
 * 岗位(服务类).
 * 
 * @author：wangj.
 * 
 * @date： 2017-08-17
 */
public interface AutPositionService extends IService<AutPosition> {

    /**
     * 
     * 岗位(分页列表).
     *
     * @return：Page<AutPosition>
     *
     * @author：wangj
     *
     * @date：2017-08-17
     */
    public Page<AutPositionDO> list(PageBean pageBean, AutPosition obj) throws Exception;

    /**
     *
     * 岗位(详情).
     *
     * @return：Page<AutPosition>
     *
     * @author：wangj
     *
     * @date：2017-08-21
     */
    public AutPositionVO selectPositionById(AutPosition obj) throws Exception;

    /**
     * 
     * 岗位列表接口(根据部门id查询)
     *
     * @return：List<AutPosition>
     *
     * @author：laijy
     *
     * @date：2017年9月11日 上午10:29:40
     */
    public List<AutPosition> getPositionListByDeptId(AutPosition obj) throws Exception;

    /**
     *
     * 岗位(分页列表,供流程设计调用).
     *
     * @return：Page<AutPosition>
     *
     * @author：wangj
     *
     * @date：2017-09-11
     */
    public Page<AutPositionDO> positionList(PageBean pageBean, AutPositionVO obj) throws Exception;

    /**
     *
     * 岗位跟流程表(act_id_group)添加关联
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月18日 下午4:47:11
     */
    public RetMsg add(AutPosition obj) throws Exception;

    /**
     *
     * 岗位跟流程表(act_id_group)解除关联
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月18日 下午4:47:11
     */
    public RetMsg delete(AutPosition obj) throws Exception;

    /**
     *
     * 岗位名称校验
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月20日
     */
    public RetMsg validate(AutPosition obj) throws Exception;

    /**
     * 
     * 激活的岗位(分页列表)
     *
     * @return：Page<AutPositionDO>
     *
     * @author：laijy
     *
     * @date：2017年10月9日 上午9:08:15
     */
    public Page<AutPositionDO> listIsActive(PageBean pageBean, AutPosition obj) throws Exception;

    public RetMsg update(AutPosition orgnlObj, AutPosition autPosition);
}
