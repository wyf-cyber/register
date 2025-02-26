package com.itheima.service;

import com.itheima.config.WebApiConfig;
import com.itheima.pojo.VerifyCodeAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;

import java.net.URI;
import java.util.Map;


@Service
public class webToolsService {

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
}