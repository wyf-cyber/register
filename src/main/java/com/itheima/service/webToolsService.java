package com.itheima.service;

import com.itheima.config.WebApiConfig;
import com.itheima.pojo.VerifyCodeAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class WebToolsService {

    private static final Logger logger = LoggerFactory.getLogger(WebToolsService.class);

    @Autowired
    private WebApiConfig webApiConfig;

    @Autowired
    private RestTemplate restTemplate;

    // 生成人机检测验证码
    public ResponseEntity<?> getCaptcha(){
        String captchaUrl = "https://www.mxnzp.com/api/verifycode/code";
        try {
            // 构建请求URL
            URI uri = UriComponentsBuilder.fromHttpUrl(captchaUrl)
                    .queryParam("len", 5)   // 验证码位数
                    .queryParam("type", 0)  // 返回类型，0-生成图片的地址链接 1-生成验证码图片的base64字符串
                    .queryParam("app_id", webApiConfig.getAppId())
                    .queryParam("app_secret", webApiConfig.getAppSecret())
                    .build()
                    .toUri();

            // 发送请求并获取响应
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<Map<String, Object>>() {});
            Map<String, Object> responseData = response.getBody();

            if (responseData != null && responseData.containsKey("data")) {
                // 获取data字段
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) responseData.get("data");
                
                // 检查data是否包含verifyCode和verifyCodeImgUrl
                if (data != null && data.containsKey("verifyCode") && data.containsKey("verifyCodeImgUrl")) {
                    String verifyCode = (String) data.get("verifyCode");
                    String verifyCodeImgUrl = (String) data.get("verifyCodeImgUrl");

                    VerifyCodeAPI captchaResponse = new VerifyCodeAPI();
                    captchaResponse.setVerifyCode(verifyCode);
                    captchaResponse.setVerifyCodeImgUrl(verifyCodeImgUrl);

                    return ResponseEntity.ok(captchaResponse);
                }
            }

            // 如果字段缺失
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Captcha response does not contain necessary fields.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate captcha: " + e.getMessage());
        }
    }

    // 生成付费二维码
    public String getQRCode(String username){
        String captchaUrl = "https://www.mxnzp.com/api/qrcode/create/logo";
        try {
            // 获取当前时间并格式化为字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String currentTime = LocalDateTime.now().format(formatter);

            String content = "[" + currentTime + "]: 欢迎使用智能挂号服务平台!" + " 您的账号 "+ username + " 已成功支付挂号费。";
            // 加载 logo 文件
            org.springframework.core.io.Resource logoResource = new ClassPathResource("static/QR.png");
            File logoFile = logoResource.getFile();
            FileSystemResource fileSystemResource = new FileSystemResource(logoFile);

            // 构造 POST 请求参数
            MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("content", content); // 二维码内容
            requestBody.add("size", 400);
            requestBody.add("logo_size", 100);
            requestBody.add("type", 0); // 0 - 图片链接；1 - Base64
            requestBody.add("logo_img", fileSystemResource); // 上传文件
            requestBody.add("app_id", webApiConfig.getAppId());
            requestBody.add("app_secret", webApiConfig.getAppSecret());

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 构造 HTTP 请求
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 发送 POST 请求
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                captchaUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>(){}
            );

            // 解析响应
            Map<String, Object> responseData = response.getBody();
            if (responseData == null) {
                return "QR code generation failed: Empty response.";
            }

            // 检查 data 字段
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseData.get("data");
            if (data == null || !data.containsKey("qrCodeUrl")) {
                return responseData.toString();
            }

            // 返回二维码图片 URL
            return (String) data.get("qrCodeUrl");

        } catch (Exception e) {
            logger.error("QR code generation failed", e);
            return "QR code generation failed: " + e.getMessage();
        }
    }
}