package aljoin.aut.iservice;

import java.util.List;
import java.util.Set;

import aljoin.aut.dao.object.AutOrganVO;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutDepartmentUser;
import aljoin.aut.dao.object.AutDepartmentUserVO;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * (服务类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-21
 */
public interface AutDepartmentUserService extends IService<AutDepartmentUser> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<AutDepartmentUser>
     *
     * @author：laijy
     *
     * @date：2017-08-21
     */
    public Page<AutDepartmentUser> list(PageBean pageBean, AutDepartmentUser obj) throws Exception;

    /**
     * 
     * 新增部门用户
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年8月22日 下午2:29:57
     */
    public RetMsg addDepartmentUser(AutDepartmentUser departmentUser);

    /**
     * 
     * 给用户分配部门（1个用户可以多个部门）
     *
     * @return：List<AutDepartmentUser>
     *
     * @author：laijy
     *
     * @date：2017年8月23日 上午10:26:00
     */
    public List<AutDepartmentUser> addUserDepartment(AutDepartmentUserVO user);

    /**
     * 
     * 根据部门id查询用户id
     *
     * @return：List<AutDepartmentUser>
     *
     * @author：laijy
     *
     * @date：2017年8月25日 下午3:32:00
     */
    public List<AutDepartmentUser> getUserIdByDepartmentId(Long deptId);

    /**
     * 
     * 根据用户id查询AutDepartmentUser表是否有该记录(查询该用户被分配在了哪些部门)
     *
     * @return：void
     *
     * @author：laijy
     *
     * @date：2017年8月28日 上午11:12:27
     */
    public List<AutDepartmentUser> getDeptByUserId(Long userId) throws Exception;

    /**
     * 
     * 根据部门id查询AutDepartmentUser表(查询该部门下有哪些用户)
     *
     * @return：List<Long>
     *
     * @author：laijy
     *
     * @date：2017年8月29日 上午10:04:33
     */
    public AutDepartmentUserVO getUserByDeptId(AutDepartment obj) throws Exception;

    /**
     * 
     * 根据userId删掉部门
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年8月30日 下午2:36:11
     */
    public RetMsg deleteDeptByUserId(AutDepartmentUserVO departmentUserVO) throws Exception;

    /**
     * 
     * 根据deptId删除用户
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年8月30日 下午3:48:23
     */
    public RetMsg deleteUserByDeptId(AutDepartmentUserVO departmentUserVO) throws Exception;

    /**
     *
     * 用户添加部门
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年9月20日
     */
    public RetMsg userAddDepartment(AutDepartmentUserVO departmentUserVO) throws Exception;

    /**
     *
     * 获取部门用户树
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年9月22日
     */
    public AutOrganVO getOrganList(AutDepartmentUserVO autDepartmentUser) throws Exception;

    /**
     *
     * 获取部门用户树查询显示用户
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年9月22日
     */
    public List<AutDepartmentUser> getDeptUserList(AutDepartmentUser autDepartmentUser) throws Exception;

    /**
     * 
     * 删除部门用户(须同时删除该用户-岗位表里的记录)
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月25日 上午8:48:36
     */
    public RetMsg deleteDeptUser(AutDepartmentUser obj) throws Exception;

    /**
     *
     * 获取部门用户树(App)
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年11月2日
     */
    public RetMsg getAppOrganList() throws Exception;
}
