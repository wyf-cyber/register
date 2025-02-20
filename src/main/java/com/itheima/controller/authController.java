package com.itheima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@CrossOrigin // Support cross-origin requests
public class authController {

    @Autowired
    private com.itheima.service.authService authService;

    // 注册逻辑
    @GetMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username, @RequestParam String password) {
        return authService.registerService(username, password);
    }

    // 登录逻辑
    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        return authService.loginService(username, password);
    }
        
    // 修改用户名
    @GetMapping("/updateName")
    public String updateName(@RequestParam String username, @RequestParam String new_username) {
        return authService.updateNameService(username, new_username);
    }
    
    // 修改用户密码
    @GetMapping("/updatePassword")
    public String updatePassword(@RequestParam String username, @RequestParam String new_password) {
        return authService.updatePasswordService(username, new_password);
    }
    
    // 注销逻辑
    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam String username) {
        return authService.deleteUserService(username);
    }
}
