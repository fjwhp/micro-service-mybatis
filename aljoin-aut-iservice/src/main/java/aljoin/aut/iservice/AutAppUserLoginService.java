package aljoin.aut.iservice;

import aljoin.aut.dao.entity.AutAppUserLogin;

/**
 * 
 * 移动端用户登录权限表（特殊表）(服务类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-19
 */
public interface AutAppUserLoginService {

    /**
     * 
     * 新增
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月19日 上午9:25:17
     */
    public void add(AutAppUserLogin obj) throws Exception;

    /**
     * 
     * 删除
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-10-19
     */
    public void delete(String token) throws Exception;

    /**
     * 
     * 根据主键获取
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月19日 上午9:25:17
     */
    public AutAppUserLogin getByToken(String token) throws Exception;

    /**
     * 
     * 删除
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017-10-19
     */
    public void update(AutAppUserLogin obj) throws Exception;

    /**
     * 
     * 根据主键获取
     *
     * @return：void
     *
     * @author：zhongjy
     *
     * @date：2017年10月19日 上午9:25:17
     */
    public AutAppUserLogin getByUserId(Long id) throws Exception;
}
