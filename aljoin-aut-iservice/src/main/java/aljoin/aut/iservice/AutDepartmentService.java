package aljoin.aut.iservice;

import java.util.List;

import aljoin.aut.dao.object.AutDepartmentDO;
import aljoin.aut.dao.object.AutDepartmentVO;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import aljoin.aut.dao.entity.AutDepartment;
import aljoin.aut.dao.entity.AutUser;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;

/**
 * 
 * (服务类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-15
 */
public interface AutDepartmentService extends IService<AutDepartment> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<AutDepartment>
     *
     * @author：laijy
     *
     * @date：2017-08-15
     */
    public Page<AutDepartment> list(PageBean pageBean, AutDepartment obj) throws Exception;

    /**
     * 
     * 根据部门级别和父级部门ID码获取下一个部门编码,如果isWidget=true, 表示生产一级部门或者二级部门( 根据deptLevel参数)的控件编码
     * 
     * @返回:String
     * 
     * @author：laijy
     * 
     * @date：2017年8月16日 上午10:21:54
     */
    public String getNextCode(int deptLevel, String parentCode, boolean isWidget) throws Exception;

    /**
     * 根据部门ID批量查询部门信息.
     *
     * @return：List<AutDepartment>
     *
     * @author：wangj
     *
     * @date：2017-08-21
     */
    public List<AutDepartment> selectByIds(List<Long> ids) throws Exception;

    /**
     * 
     * 获取部门列表
     *
     * @return：List<AutUser>
     *
     * @author：laijy
     *
     * @date：2017年8月22日 下午3:18:11
     */
    public List<AutDepartment> getDepartmentList();

    /**
     *
     * 获取部门列表
     *
     * @return：List<AutDepartment>
     *
     * @author：wangj
     *
     * @date：2017年09月11日 下午4:47:11
     */
    public List<AutDepartmentDO> getDepartmentList(AutDepartmentVO departmentVO);

    /**
     *
     * 通过选中的部门id递归获得自己及所有下属部门的id
     *
     * @return：List<Long>
     *
     * @author：laijy
     *
     * @date：2017年9月19日 上午8:28:34
     */
    public List<Long> selectChildIdListByParent(Long pid, List<Long> allDeptIdList) throws Exception;

    /**
     *
     * 通过选中的部门编号查询所有自身及下属部门idList
     *
     * @return：List<Long>
     *
     * @author：laijy
     *
     * @date：2017年9月19日 上午8:31:08
     */
    public List<Long> selectChildIdListByDeptCode(String deptCode) throws Exception;

    /**
     *
     * 权限管理-部门管理-删除部门-删除之前进行还有下属部门/用户的判断确认
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月19日 上午9:04:33
     */
    public RetMsg deleteConfirm(Long id, String deptCode) throws Exception;

    /**
     *
     * 部门跟流程表(act_id_group)添加关联
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月18日 下午4:47:11
     */
    public RetMsg add(AutDepartment obj) throws Exception;

    /**
     *
     * 部门跟流程表(act_id_group)解除关联
     *
     * @return：RetMsg
     *
     * @author：wangj
     *
     * @date：2017年09月18日 下午4:47:11
     */
    public RetMsg delete(AutDepartment obj) throws Exception;

    /**
     * 
     * (分页列表).
     *
     * @return：Page<AutDepartment>
     *
     * @author：laijy
     *
     * @date：2017-08-15
     */
    public Page<AutDepartment> listIsActive(PageBean pageBean, AutDepartment obj) throws Exception;

    /**
     * 
     * 获得部门及下属部门
     *
     * @return：List<AutDepartment>
     *
     * @author：laijy
     *
     * @date：2017年10月25日 下午10:22:48
     */
    public List<AutDepartment> getChildDeptList(AutDepartment obj) throws Exception;

    /**
     * 
     * 获得本部门及下属部门的所有用户
     *
     * @return：List<AutUser>
     *
     * @author：laijy
     *
     * @date：2017年10月25日 下午10:39:12
     */
    public List<AutUser> getChildDeptUserList(AutDepartment obj) throws Exception;

    /**
     * 
     * 根据部门ID获取当前部门及其子部门下的所有用户
     *
     * @return：List<AutUser>
     *
     * @author：huanghz
     *
     * @date：2018年1月12日 下午2:55:55
     */
    public List<AutUser> getDeptAndChildForUserListByDeptId(Long deptId) throws Exception;

    /**
     * 
     * 获得部门及下属部门（分页列表）
     *
     * @return：Page<AutDepartment>
     *
     * @author：laijy
     *
     * @date：2017年10月26日 上午8:29:43
     */
    public Page<AutDepartment> getChildDeptPage(AutDepartment obj, PageBean pageBean) throws Exception;
}