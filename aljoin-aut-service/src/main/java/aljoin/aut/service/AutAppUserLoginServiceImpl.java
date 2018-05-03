package aljoin.aut.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import aljoin.aut.dao.entity.AutAppUserLogin;
import aljoin.aut.dao.mapper.AutAppUserLoginMapper;
import aljoin.aut.iservice.AutAppUserLoginService;

/**
 * 
 * 移动端用户登录权限表（特殊表）(服务实现类).
 * 
 * @author：zhongjy.
 * 
 * @date： 2017-10-19
 */
@Service
public class AutAppUserLoginServiceImpl implements AutAppUserLoginService {

    @Resource
    private AutAppUserLoginMapper mapper;

    @Override
    public void add(AutAppUserLogin obj) throws Exception {
        mapper.add(obj);
    }

    @Override
    public void delete(String token) throws Exception {
        mapper.delete(token);
    }

    @Override
    public AutAppUserLogin getByToken(String token) throws Exception {
        return mapper.getByToken(token);
    }

    @Override
    public void update(AutAppUserLogin obj) throws Exception {
        mapper.update(obj);
    }

    @Override
    public AutAppUserLogin getByUserId(Long id) throws Exception {
        return mapper.getByUserId(id);
    }

}
