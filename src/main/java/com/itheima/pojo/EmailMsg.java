package com.itheima.pojo;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.mail.javamail.MimeMessageHelper;  // Import MimeMessageHelper

import javax.annotation.Resource;
import java.time.Duration;


@Component
public class EmailMsg {

    @Resource
    private JavaMailSenderImpl mailSender;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${spring.mail.username}")
    private String sender;

    public boolean mail(String username, String email, String code) throws MessagingException {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            //设置一个html邮件信息
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);  // 表示邮件内容为HTML格式

            helper.setText(
                    "<html>" +
                            "<body style='font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;'>" +
                            "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);'>" +
                            "<h2 style='color: #4CAF50; text-align: center;'>XXX智能挂号服务平台</h2>" +
                            "<p style='font-size: 16px; color: #333333;'>尊敬的用户，" + username + "：</p>" +
                            "<p style='font-size: 16px; color: #333333;'>感谢您使用我们的线上挂号服务平台！为了保护您的账户安全，请使用以下验证码进行验证：</p>" +
                            "<p style='font-size: 20px; font-weight: bold; color: #FF5722; text-align: center;'>验证码：<span style='font-size: 24px; color: #2196F3;'>" + code + "</span></p>" +
                            "<p style='font-size: 16px; color: #333333;'>该验证码有效期为10分钟，请尽快使用。</p>" +
                            "<p style='font-size: 16px; color: #333333;'>如果您没有进行注册操作，请忽略此邮件。</p>" +
                            "<hr style='border: 0; border-top: 1px solid #ccc;' />" +
                            "<p style='font-size: 14px; color: #888888; text-align: center;'>XXX 预约挂号系统 &copy; 2024</p>" +
                            "</div>" +
                            "</body>" +
                            "</html>",
                    true); // 表示邮件内容为HTML格式
            //设置邮件主题名
            helper.setSubject("XXX线上挂号服务平台登录验证");

            //发给谁->邮箱地址
            helper.setTo(email);

            //谁发的->发送人邮箱
            helper.setFrom(sender);

            mailSender.send(mimeMessage);
            // 只有邮件发送成功后才存储验证码到 Redis
            // 设置验证码的过期时间为10分钟
            redisTemplate.opsForValue().set(username, code, Duration.ofMinutes(10));
            return true;
        } catch (MessagingException e) {
            // 记录具体错误信息
            e.printStackTrace();
            return false;
        }
    }

    // 检查输入是否正确
    public boolean verifyCode(String username, String inputCode) {
        // 从 Redis 中获取存储的验证码
        String storedCode = redisTemplate.opsForValue().get(username);

        // 如果 Redis 中不存在验证码或已过期
        if (storedCode == null) {
            return false; // 验证码不存在或已过期
        }

        // 比较存储的验证码与输入的验证码
        if (storedCode.equals(inputCode)) {
            // 验证码匹配，删除验证码
            redisTemplate.delete(username);
            return true; // 验证码正确
        }

        // 验证码不匹配
        return false;
    }
}