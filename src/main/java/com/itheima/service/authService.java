package com.itheima.service;

import com.itheima.mapper.UserMapper;
import com.itheima.pojo.ApiMessage;
import com.itheima.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class authService {
    @Autowired
    private UserMapper userMapper;

    public ResponseEntity<?> registerService(String username, String password) {
        UserInfo user = userMapper.findByUsername(username); // Fetch data from database
        ApiMessage response = new ApiMessage();
        if (user == null) {
            // 新用户，可以注册
            UserInfo newUser = new UserInfo();
            newUser.setUsername(username);
            newUser.setPassword(password);
            int rowsAffected = userMapper.insertUser(newUser);
            if(rowsAffected > 0) {
                // 注册成功
                response.setSuccess(true);
                response.setMessage("registered successfully");
                return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
            } else{
                response.setSuccess(false);
                response.setMessage("User inserting failed");
                return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
            }
        } else {
            // 用户名已被占用
            response.setSuccess(false);
            response.setMessage("Username already exists");
            return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
        }
    }

    public ResponseEntity<?> loginService(String username, String password) {
        UserInfo user = userMapper.findByUsername(username); // Fetch data from database
        ApiMessage response = new ApiMessage();
        if (user == null) {
            // 用户不存在
            response.setSuccess(false);
            response.setMessage("Invalid username");
            return ResponseEntity.ok(response);
        } else if(!user.getPassword().equals(password)) {
            // 密码错误
            response.setSuccess(false);
            response.setMessage("Invalid password");
            return ResponseEntity.ok(response);
        } else {
            // 成功登录
            response.setSuccess(true);
            response.setMessage("Login successfully");
            return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
        }
    }
}
