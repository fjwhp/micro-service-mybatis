package aljoin.aut.iservice;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutUser;
import aljoin.aut.dao.entity.AutUserRank;
import aljoin.aut.dao.object.AutUserAndPubVo;
import aljoin.aut.dao.object.AutUserDO;
import aljoin.aut.dao.object.AutUserPubEditVO;
import aljoin.aut.dao.object.AutUserPubVO;
import aljoin.aut.dao.object.AutUserVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.object.SsoData;

/**
 * 
 * 用户表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-21
 */
public interface AutUserService extends IService<AutUser> {

    /**
     * 
     * 用户表(分页列表).
     *
     * @return：Page<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:52:01
     */
    public Page<AutUser> list(PageBean pageBean, AutUser obj) throws Exception;

    /**
     * 
     * 用户表(分页列表,包含部门、岗位信息).
     *
     * @return：Page<AutUserVO>
     *
     * @author：laijy
     *
     * @date：2017年10月16日 下午2:25:43
     */
    public Page<AutUserVO> voList(PageBean pageBean, AutUser obj) throws Exception;

    /**
     * 
     * 用户表(分页列表,包含部门、岗位信息).
     *
     * @return：Page<AutUserVO>
     *
     * @author：huanghz
     *
     * @date：2017年12月14日 下午2:40:43
     */
    public Page<AutUserVO> rankList(PageBean pageBean, AutUser obj) throws Exception;

    /**
     * 
     * 删除用户
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月8日 下午7:37:09
     */
    public void delete(AutUser autUser) throws Exception;

    /**
     * 
     * 新增用户
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月8日 下午7:37:09
     */
    public RetMsg add(AutUser autUser, String charPassword) throws Exception;

    /**
     * 
     * 获取用户列表（id和昵称）.
     *
     * @return：List<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年7月16日 下午8:43:20
     */
    public List<AutUser> getUserList();

    /**
     * 
     * 自定义mapper方法例子
     *
     * @return：List<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年8月17日 上午11:41:37
     */
    public List<AutUser> getMyUserList() throws Exception;

    /**
     * 
     * 查询不在【部门-用户】表里的所有用户
     *
     * @return：Page<AutUser>
     *
     * @author：laijy
     *
     * @date：2017年8月25日 下午4:14:22
     */
    public Page<AutUser> listNoDepartmentUser(PageBean pageBean, List<Long> userId) throws Exception;

    /**
     * 
     * 用户分配角色
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年8月31日 下午4:09:36
     */
    public RetMsg setAuth(int isActive, long id, long userId) throws Exception;

    /**
     *
     * 查询用户
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月11日 下午5:39:36
     */
    public Page<AutUserDO> getUserList(PageBean pageBean, AutUserVO autUserVO) throws Exception;

    /**
     *
     * 部门用户分页列表.
     *
     * @return：Page<AutUser>
     *
     * @author：wangj
     *
     * @date：2017年9月26日
     */
    public Page<AutUser> getDeptUserList(PageBean pageBean, AutUser obj) throws Exception;

    /**
     * 
     * 用户表(分页列表).
     *
     * @return：Page<AutUser>
     *
     * @author：zhongjy
     *
     * @date：2017年5月27日 下午4:52:01
     */
    public Page<AutUser> listIsAvtive(PageBean pageBean, AutUser obj) throws Exception;

    /**
     * 
     * 获取用户排序:rankType：dept-部门，position-岗位，role-角色 [注意：目前此参数可传空字符串，不分类型；userIdList必段指定]
     *
     * @return：Map<Long,Integer>
     *
     * @author：huanghz
     *
     * @date：2017年12月15日 下午7:47:04
     */
    public Map<Long, Integer> getUserRankList(List<Long> userIdList, String rankType) throws Exception;

    /**
     * 
     * 根据用户id条件查询出人员排序信息并分页
     * 
     * @param PageBean pageBean
     * @param List<Long> userIdList
     * @return：Page<AutUserRank>
     * @author：huanghz
     * @date：2018年1月12日 下午3:34:41
     */
    public Page<AutUserRank> getUserRankPage(PageBean pageBean, List<Long> userIdList) throws Exception;

    /**
     * 
     * 用户详情
     *
     * @return：AutUserPubVO
     *
     * @author：xuc
     *
     * @date：2017年11月21日 上午10:55:58
     */
    public AutUserPubVO getById(Long id);

    /**
     * 
     * 单点登录数据同步;oa系统提供接口，以便第三方新增、修改、查询用户信息。
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年11月21日 上午9:00:01
     */
    public RetMsg ssoDataSynch(SsoData ssoData) throws Exception;

    /**
     * OA调用第三方提供的接口，把单点登录数据同步给对方(对方根据操作类型做相应的数据新增、修改、查询);
     * 
     * @param ssoData
     * 
     * @return RetMsg
     * 
     * @author：huanghz
     *
     * @date：2017年12月18日 下午5:26:01
     * 
     * @throws Exception
     */
    public RetMsg ssoDataSynInvoke(SsoData ssoData) throws Exception;

    /**
     * 
     * 修改用户
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2018年1月10日 下午4:15:07
     */
    public RetMsg updateUser(AutUserPubEditVO obj) throws Exception;

    /**
     * 
     * 新增用户
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2018年1月10日 下午4:14:52
     */
    public RetMsg addUser(AutUserAndPubVo obj, Long customUserId) throws Exception;
    
    /**
     * 
     * 获得用户部门列表
     *
     * @return：List<String>
     *
     * @author：caizx
     *
     * @date：2018年4月04日 下午15:23:37
     */
    public List<Map<Long, Object>> getUserDeptList();
}
