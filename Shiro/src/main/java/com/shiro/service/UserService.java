package com.shiro.service;
import com.shiro.entity.User;

import java.util.List;

public interface UserService {
    //1.用户登陆方法
    User getUserInfoByName(String name);
    //根据用户查询角色信息
    List<String> getUserRoleInfo(String principal);
    //获取用户角色的权限信息
    List<String> getUserPermissionInfo(List<String> roles);
    //2. 用户注册方法
    int registerUser(String name, String password);
    //3. 初始化角色的方法
    int initialisingRoles();
}
