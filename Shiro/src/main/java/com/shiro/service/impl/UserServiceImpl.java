package com.shiro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shiro.entity.User;
import com.shiro.mapper.UserMapper;
import com.shiro.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserInfoByName(String name){
        QueryWrapper<User> wrapper =new QueryWrapper<>();
        wrapper.eq("username",name);
        User user=userMapper.selectOne(wrapper);
        return user;
    }
    //根据用户查询角色信息
    @Override
    public List<String> getUserRoleInfo(String principal) {
        return userMapper.getUserRoleInfoMapper((principal));
    }
    //获取用户权限信息
    @Override
    public List<String> getUserPermissionInfo(List<String> roles) {
        return userMapper.getUserPermissionInfoMapper(roles);
    }
    @Override
    public int registerUser(String username, String password) {
        return userMapper.registerUser(username, password);
    }
    @Override
    public int initialisingRoles(){return userMapper.initialisingRoles();};
}
