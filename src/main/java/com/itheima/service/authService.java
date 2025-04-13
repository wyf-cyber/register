package com.itheima.service;

import com.itheima.mapper.UserMapper;
import com.itheima.mapper.RegisterMapper;
import com.itheima.pojo.ApiMessage;
import com.itheima.pojo.EmailMsg;
import com.itheima.pojo.UserInfo;
import com.itheima.pojo.Appointment;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Random;
import java.util.List;

@Service
public class authService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RegisterMapper registerMapper;

    @Autowired
    private EmailMsg emailMsg;

    // 注册逻辑，注册成功后，将用户信息插入到数据库中，并返回注册成功信息
    // 普通用户需要注册，管理员用户直接插入到数据库中
    public ResponseEntity<?> registerService(String username, String password, String email) {
        UserInfo user = userMapper.findByUsername(username); // Fetch data from database
        ApiMessage response = new ApiMessage();
        if (user == null) {
            // 新用户，可以注册
            UserInfo newUser = new UserInfo();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setRole("user");      // 使用注册的只有普通用户
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

    // 登录逻辑，登录成功后，返回登录成功信息
    // 如果是管理员用户，则返回管理员用户身份确认
    // 如果是普通用户，则返回普通用户身份确认
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
            response.setMessage(user.getRole()); // 返回角色
            return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
        }
    }

    public String updateNameService(String username, String new_username) {
        // 修改用户登记信息
        int res = userMapper.updateUsername(username, new_username);
        if (res == 0) {
            // 用户不存在
            return "Invalid username";
        }
   
        return "Update username successfully";
    }

    public String updatePasswordService(String username, String new_password) {
        int res = userMapper.updatePassword(username, new_password);
        if (res == 0) {
            return "Invalid username";
        }
        return "Update password successfully";
    }

    public String deleteUserService(String username) {
        // 注销用户记录
        int res = userMapper.deleteUser(username);
        if (res == 0) {
            // 用户不存在
            return "Invalid username";
        }
        
        return "delete successfully";  // 返回响应
    }

    public String updateEmailService(String username, String new_email) {
        int res = userMapper.updateEmail(username, new_email);
        if (res == 0) {
            return "Invalid username";
        }
        return "Update email successfully";
    }

    // 利用绑定的邮箱向用户邮箱发送6位确认随机数字，用户接下来可以使用这个随机验证数登录
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);  // 生成一个6位数的随机数
        return String.valueOf(code);  // 返回字符串形式的验证码
    }

    // 发送验证码到用户邮箱
    public String sendVerificationEmail(String username) {
        String code = generateVerificationCode();
        String toEmail = userMapper.getEmail(username);

        UserInfo user = userMapper.findByUsername(username);
        if (user == null) {
            return "Invalid username";
        } else if (toEmail == null) {
            return "No email address found";
        }

        try {
            emailMsg.mail(username, toEmail, code);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return "Email sent successfully";
    }

    // 检查验证码是否正确
    public ResponseEntity<?> checkEmailCode(String username, String code) {
        if(emailMsg.verifyCode(username, code)) {
            // 验证码正确，查找用户是否是管理员
            UserInfo user = userMapper.findByUsername(username);
            if(user.getRole().equals("admin")) {
                // 构建消息
                ApiMessage response = new ApiMessage();
                response.setSuccess(true);
                response.setMessage("admin");
                return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
            } else {
                // 构建消息
                ApiMessage response = new ApiMessage();
                response.setSuccess(true);
                response.setMessage("user");
                return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
            }
        } else {
            // 验证码不正确
            ApiMessage response = new ApiMessage();
            response.setSuccess(false);
            response.setMessage("Invalid code");
            return ResponseEntity.ok(response);  // 返回 JSON 格式的响应
        }
    }

    // 获取用户信息，目前只有用户的绑定邮箱
    public String getUserInfoService(String username) {
        return userMapper.getEmail(username);
    }

    // 获取用户过往预约记录
    // 包含科室、医生、日期、时间、预约状态
    public List<Appointment> getUserAppointmentHistoryService(String username) {
        // 获取用户预约列表
        List<Appointment> history = registerMapper.getUserAppointmentHistory(username);
        // 遍历列表，利用时间检查是否完成
        LocalDate current = LocalDate.now();
        for(Appointment appointment : history) {
            // 根据day判断是否属于已完成的预约
            if (current.isBefore(LocalDate.parse(appointment.getDay()))) {
                // 如果预约日期大于当前日期，则设置为未完成
                appointment.setTime(0);
            } else {
                // 如果预约日期小于等于当前日期，则设置为已完成
                appointment.setTime(1);
            }
        }
        return history;
    }
}
