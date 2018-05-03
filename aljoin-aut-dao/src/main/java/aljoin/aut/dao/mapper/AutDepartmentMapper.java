package aljoin.aut.dao.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import aljoin.aut.dao.entity.AutDepartment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * (Mapper接口).
 * 
 * @author：laijy.
 * 
 * @date： 2017-08-15
 */
public interface AutDepartmentMapper extends BaseMapper<AutDepartment> {

    /**
     * 根据部门ID批量查询部门信息.
     *
     * @return：List<AutDepartment>
     *
     * @author：wangj
     *
     * @date：2017-08-21
     */
    public List<AutDepartment> selectByIds(@Param("list") List<Long> ids);
}