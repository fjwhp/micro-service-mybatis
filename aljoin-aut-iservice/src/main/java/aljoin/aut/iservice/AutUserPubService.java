package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUserPub;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * 用户公共信息表(服务类).
 * 
 * @author：laijy
 * 
 *               @date： 2017-10-10
 */
public interface AutUserPubService extends IService<AutUserPub> {

    /**
     * 
     * 用户公共信息表(分页列表).
     *
     * @return：Page<AutUserPub>
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    public Page<AutUserPub> list(PageBean pageBean, AutUserPubVO obj) throws Exception;

    /**
     * 
     * 根据ID删除对象(物理删除)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    public void physicsDeleteById(Long id) throws Exception;

    /**
     * 
     * 复制对象(需要完整的对象数据，包括所有的公共字段)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017-10-10
     */
    public void copyObject(AutUserPub obj) throws Exception;

    /**
     * 
     * @描述:用户修改密码
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月10日 下午4:36:21
     */
    public RetMsg updatePwd(AutUserPubVO obj, Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-通讯录
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月13日 上午9:54:36
     */
    public List<AutUserPubVO> getAutUserPubVOList(AutUserPubVO vo) throws Exception;

    /**
     * 
     * 个人中心-个人信息-新增
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月18日 上午9:48:47
     */
    public RetMsg add(AutUserPubVO obj, Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-个人信息-修改
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年10月18日 上午11:11:09
     */
    public RetMsg update(AutUserPubVO obj, Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-个人信息-详情（用户、部门、岗位、公共信息）
     *
     * @return：AutUserPubVO
     *
     * @author：laijy
     *
     * @date：2017年10月23日 上午10:45:09
     */
    public AutUserPubVO getById(Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-通讯录(本部门)
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月24日 下午4:11:43
     */
    public List<AutUserPubVO> getMyDeptAutUserPubVOList(Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-通讯录(本单位)
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     *
     * @date：2017年10月24日 下午7:47:43
     */
    public List<AutUserPubVO> getMyCompAutUserPubVOList(Long customUserId) throws Exception;

    /**
     * 
     * 个人中心-通讯录-查询
     *
     * @return：List<AutUserPubVO>
     *
     * @author：laijy
     * 
     * @date：2017年10月26日 上午9:59:26
     */
    // TODO
    public List<AutUserPubVO> searchUserPubInfo(AutUserPubVO vo, Long customUserId) throws Exception;
    // public List<AutUserPubVO> searchUserPubInfo(AutUserPubVO vo) throws Exception;

    /**
     * 
     * 个人中心-上传图片用户更新数据
     *
     * @return：List<AutUserPubVO>
     *
     * @author：huangw
     *
     * @date：2017年10月31日 上午9:59:26
     */
    public RetMsg updateImg(AutUserPubVO autUserPubVO, Long customUserId) throws Exception;


    /**
     * 
     * 个人中心-通讯录-查询
     *
     * @return：List<AutUserPubVO>
     *
     * @author：huangw
     *
     * @date：2017年10月31日 上午9:59:26
     */
    public Page<AutUserPubVO> searchValue(AutUserPubVO vo, Long customUserId, Integer num, Integer size)
        throws Exception;

    /**
     * 
     * 个人中心-通讯录-查询
     *
     * @return：List<AutUserPubVO>
     *
     * @author：huangw
     *
     * @date：2017年11月1日 上午13:59:26
     */
    public List<AutUserPubVO> searchquery(String queryType, String qurySearch, Long customUserId) throws Exception;

    /**
     * 
     * 修改密码
     *
     * @return：List<AutUserPubVO>
     *
     * @author：huangw
     *
     * @date：2017年11月16日 上午13:59:26
     */
    public RetMsg appUpdatePwd(String oldPwd, String newPwd, String newPwds, String userId) throws Exception;

    public AutUserPubVO appGetById(String string) throws Exception;

    /**
     * 
     * 修改个人信息
     *
     * @return：List<AutUserPubVO>
     *
     * @author：huangw
     *
     * @date：2017年11月16日 上午13:59:26
     */
    public RetMsg appUpdatePub(AutUserPubVO vo, String userId) throws Exception;


}
