package com.itheima.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import com.itheima.pojo.UserInfo;

@Mapper
public interface UserMapper {

    // 根据用户名查询用户
    @Select("SELECT id, username, password FROM users WHERE username = #{username}")
    UserInfo findByUsername(String username);

    // 插入新用户
    @Insert("INSERT INTO users (username, password) VALUES (#{username}, #{password})")
    int insertUser(UserInfo userInfo);

    // 更新用户密码
    @Update("UPDATE users SET password = #{password} WHERE username = #{username}")
    int updatePassword(String username, String password);

    // 更新用户名称
    @Update("Update users SET username = #{new_username} WHERE username = #{username}")
    int updateUsername(String username, String new_username);

    // 删除用户（可选）
    @Delete("DELETE FROM users WHERE username = #{username}")
    int deleteUser(String username);
}
