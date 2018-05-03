package aljoin.aut.iservice;

import java.util.List;

import com.baomidou.mybatisplus.plugins.Page;
import aljoin.object.PageBean;
import aljoin.object.RetMsg;
import aljoin.aut.dao.entity.AutDepartmentRole;
import com.baomidou.mybatisplus.service.IService;

/**
 * 
 * (服务类).
 * 
 * @author：laijy.
 * 
 * @date： 2017-09-04
 */
public interface AutDepartmentRoleService extends IService<AutDepartmentRole> {

    /**
     * 
     * (分页列表).
     *
     * @return：Page<AutDepartmentRole>
     *
     * @author：laijy
     *
     * @date：2017-09-04
     */
    public Page<AutDepartmentRole> list(PageBean pageBean, AutDepartmentRole obj) throws Exception;

    /**
     * 
     * 部门分配角色
     *
     * @return：RetMsg
     *
     * @author：laijy
     *
     * @date：2017年9月4日 上午11:47:19
     */
    public RetMsg setAuth(int isActive, long roleId, long deptId) throws Exception;

    /**
     * 
     * 查询部门-角色关联表，isActive=1的所有数据
     *
     * @return：List<AutDepartmentRole>
     *
     * @author：laijy
     *
     * @date：2017年9月4日 下午3:49:21
     */
    public List<AutDepartmentRole> getDepartmentRoleList(AutDepartmentRole obj) throws Exception;

}
