package com.shiro.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiro.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapper extends BaseMapper<User> {
//    @Select("SELECT NAME FROM role WHERE id IN (SELECT rid FROM role_user WHERE uid=(SELECT id FROM USER WHERE USERNAME=#{principal}))")
    @Select("SELECT name FROM role WHERE id IN (SELECT rid FROM role_user WHERE uid=(SELECT id FROM user WHERE username=#{principal}))")
    List<String> getUserRoleInfoMapper(@Param("principal")String principal);

    @Select({
            "<script>",
            "select info FROM permissions WHERE id IN",
            "(SELECT pid FROM role_ps WHERE rid IN(",
            "SELECT id FROM role WHERE NAME IN",
            "<foreach collection='roles' item='name' open='(' separator=',' close=')'>",
            "#{name}",
            "</foreach>",
            "))",
            "</script>"
    })
    List<String> getUserPermissionInfoMapper(@Param("roles")List<String> roles);
    @Insert("INSERT INTO user (username, password, id) SELECT #{username}, #{password}, (SELECT COALESCE(MAX(id), 0) + 1 FROM user) FROM DUAL")
    int registerUser(@Param("username") String username, @Param("password") String password);
    @Insert("INSERT INTO role_user (id, uid, rid) SELECT (SELECT COALESCE(MAX(id), 0) + 1 FROM role_user), (SELECT COALESCE(MAX(uid), 0) + 1 FROM role_user), 1 FROM DUAL")
    int initialisingRoles();
}
