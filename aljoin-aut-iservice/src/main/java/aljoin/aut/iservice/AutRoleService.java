package aljoin.aut.iservice;

import aljoin.aut.dao.entity.AutRole;
import aljoin.aut.dao.entity.AutUserRole;
import aljoin.aut.dao.object.AutRoleDO;
import aljoin.aut.dao.object.AutRoleVO;
import aljoin.aut.dao.object.AutUserRoleVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * 角色表(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-05-27
 */
public interface AutRoleService extends IService<AutRole> {

    /**
     * 
     * 角色表(分页列表).
     *
     * @return：Page<AutRole>
     *
     * @author：zhongjy
     *
     * @date：2017-05-27
     */
    public Page<AutRole> list(PageBean pageBean, AutRole obj) throws Exception;

    /**
     * 
     * 新增角色
     *
     * @return：RetMsg
     *
     * @author：zhongjy
     *
     * @date：2017年7月13日 下午8:55:40
     */
    public RetMsg add(AutRole obj) throws Exception;

    /**
     * 
     * 删除角色
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年7月13日 下午9:01:02
     */
    public RetMsg delete(AutRole obj) throws Exception;

    /**
     *
     * 角色表(分页列表).
     *
     * @return：Page<AutRole>
     *
     * @author：wangj
     *
     * @date：2017-09-11
     */
    public Page<AutRoleDO> roleList(PageBean pageBean, AutRoleVO obj) throws Exception;

    /**
     * 
     * [权限管理]-[角色管理]-[查看角色下的用户]
     *
     * @return：List<AutUserRole>
     *
     * @author：laijy
     *
     * @date：2017年9月13日 下午2:06:46
     */
    public Page<AutUserRole> getUserByRoleId(PageBean pageBean, AutRole autRole) throws Exception;

    /**
     * 
     * [权限管理]-[角色管理]-[(批量)更新角色下部门排序]
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月13日 下午5:08:01
     */
    public RetMsg updateRoleUserRankList(AutUserRoleVO autUserRoleVO) throws Exception;

    /**
     * 
     * [权限管理]-[角色管理]-[查看角色下的用户] 使用VO，意在能显示aut_user表里的full_name
     *
     * @return：List<AutUserRoleVO>
     *
     * @author：laijy
     *
     * @date：2017年9月14日 下午3:57:10
     */
    public AutUserRoleVO getAutUserRoloVo(AutRole autRole) throws Exception;
    
    /**
     * 根据用户id获取角色列表
     * @param userId
     * @return
     * @throws Exception
     */
    public List<Long> getRoleIdByUserId(Long userId) throws Exception;
}
