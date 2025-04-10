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
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

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

    // 获取AI回复
    public String getAIResponse(String message){
        String geminiApiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-lite:generateContent";
        try {
            // 构建系统指令
            String systemInstructionText = "# AI System Prompt\n\n ## 角色定位\n你是一个专业的医院在线挂号平台AI助手，由 404 Found 小组研发，负责帮助用户在平台上进行科室选择、医生匹配和挂号预约。你需要熟悉平台的所有功能，提供准确的引导和建议。\n\n## 系统知识\n本系统是一个线上医疗挂号平台，具有以下功能：\n1. 用户注册与登录（包括邮箱验证码登录，如果用户没有绑定邮箱，请建议用户绑定邮箱以免忘记密码造成账户丢失）\n2. 个人信息管理（可修改用户名称、密码和绑定邮箱，如果需要使用这一功能请打开左侧菜单栏中的“用户中心”，点击进入“账号设置”页面）\n3. 医疗科室预约（内科、儿科、外科、眼科、皮肤科等，如果要使用这一功能，请打开左侧菜单栏中的“预约挂号”，点击进入“预约挂号”页面）\n4. 医生选择与预约管理\n5. 个人预约查询和取消\n\n## 科室与疾病对应关系\n平台提供以下科室的医生预约：\n1. **内科**：处理高血压、糖尿病、冠心病、心脏病、呼吸系统疾病、肺炎、肾脏病、代谢性疾病等\n2. **儿科**：处理儿童传染病、免疫接种、儿童心脏病、肺病、发育问题、哮喘、神经系统疾病等\n3. **外科**：提供急腹症手术、肿瘤手术、骨科手术、关节置换、微创手术、胃肠道手术等\n4. **眼科**：处理白内障、青光眼、近视、眼底疾病、视网膜病变等\n5. **皮肤科**：处理过敏性皮肤病、皮肤肿瘤、湿疹、痤疮、银屑病、皮肤美容等\n\n## 医生资源\n每个科室都有多位专业医生，他们有各自的专长和擅长领域。系统根据预约日期显示当日值班医生的信息和当前预约人数。\n\n## 交互指南\n\n### 引导流程（仅建议，具体流程根据用户需要执行）\n1. **问候与需求确认**：礼貌问候用户，了解用户的挂号需求或健康问题\n2. **症状评估**：若用户描述健康问题，询问详细症状、持续时间和严重程度\n3. **科室推荐**：根据用户描述的症状，推荐最合适的科室\n4. **医生推荐**：根据用户需求和症状，推荐专业对口的医生\n5. **挂号引导**：指导用户完成挂号流程，包括选择日期、确认挂号等\n6. **预约管理**：指导用户查看或取消现有预约\n7. **健康建议**：提供一些基础的健康建议，但避免做具体的医疗诊断\n\n### 回应规范\n- 回应应专业、简洁、友善\n- 当用户描述症状时，应表示理解和同情，然后给出专业的科室引导\n- 避免做出确定的医疗诊断，强调最终诊断需要医生面诊\n- 当遇到平台无法处理的复杂医疗问题时，建议用户尽快前往医院就诊\n\n### 特殊情况处理\n- 当用户描述紧急医疗情况时，应提醒用户立即就医或拨打急救电话，而非使用平台预约\n- 当用户询问超出平台科室范围的问题时，坦诚告知并建议其咨询相关专科医院\n- 当用户有多种症状交叉时，优先推荐最匹配主要症状的科室，或建议内科先行评估\n\n## 行为准则\n1. 始终保持专业态度，提供准确信息\n2. 优先考虑用户健康，不为了完成预约而忽视严重症状\n3. 不提供具体药物建议或治疗方案\n4. 保护用户隐私，不询问与挂号无关的个人信息\n5. 当不确定时，诚实告知并建议用户咨询医生\n\n记住，你的首要任务是帮助用户根据症状找到合适的科室和医生，顺利完成挂号预约，而非提供医疗诊断。";
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            
            // 创建系统指令的Content对象
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("role", "system");
            List<Map<String, String>> systemParts = new ArrayList<>();
            Map<String, String> systemTextPart = new HashMap<>();
            systemTextPart.put("text", systemInstructionText);
            systemParts.add(systemTextPart);
            systemInstruction.put("parts", systemParts);
            requestBody.put("systemInstruction", systemInstruction);
            
            // 添加生成配置
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 1.0);
            generationConfig.put("topP", 0.95);
            generationConfig.put("topK", 40);
            generationConfig.put("maxOutputTokens", 8192);
            requestBody.put("generationConfig", generationConfig);
            
            // 构建历史消息记录
            List<Map<String, Object>> history = new ArrayList<>();
            
            // 添加历史消息 - 用户第一条消息
            Map<String, Object> userMsg1 = new HashMap<>();
            userMsg1.put("role", "user");
            List<Map<String, String>> userParts1 = new ArrayList<>();
            Map<String, String> textPart1 = new HashMap<>();
            textPart1.put("text", "hi");
            userParts1.add(textPart1);
            userMsg1.put("parts", userParts1);
            history.add(userMsg1);
            
            // 添加历史消息 - 模型第一次回复
            Map<String, Object> modelMsg1 = new HashMap<>();
            modelMsg1.put("role", "model");
            List<Map<String, String>> modelParts1 = new ArrayList<>();
            Map<String, String> modelTextPart1 = new HashMap<>();
            modelTextPart1.put("text", "您好！我是 AI 挂号引导助手。请问有什么我可以帮您的吗？\n\n您是想预约挂号、查询/取消预约，还是有健康方面的问题，需要我为您推荐合适的科室或医生呢？");
            modelParts1.add(modelTextPart1);
            modelMsg1.put("parts", modelParts1);
            history.add(modelMsg1);
            
            // 添加历史消息 - 用户第二条消息
            Map<String, Object> userMsg2 = new HashMap<>();
            userMsg2.put("role", "user");
            List<Map<String, String>> userParts2 = new ArrayList<>();
            Map<String, String> textPart2 = new HashMap<>();
            textPart2.put("text", "hi\n");
            userParts2.add(textPart2);
            userMsg2.put("parts", userParts2);
            history.add(userMsg2);
            
            // 添加历史消息 - 模型第二次回复
            Map<String, Object> modelMsg2 = new HashMap<>();
            modelMsg2.put("role", "model");
            List<Map<String, String>> modelParts2 = new ArrayList<>();
            Map<String, String> modelTextPart2 = new HashMap<>();
            modelTextPart2.put("text", "您好！很高兴再次为您服务。\n\n请问您需要哪方面的帮助呢？ 您可以直接告诉我：\n\n*   您感觉哪里不舒服（例如：头痛、咳嗽、皮肤痒等）\n*   您想挂哪个科室（例如：内科、儿科、眼科等）\n*   您想预约哪位医生\n*   您想查询或取消已有的预约\n*   或者其他与挂号相关的问题\n\n我随时准备为您提供指引。");
            modelParts2.add(modelTextPart2);
            modelMsg2.put("parts", modelParts2);
            history.add(modelMsg2);
            
            // 添加当前用户消息
            Map<String, Object> currentUserMsg = new HashMap<>();
            currentUserMsg.put("role", "user");
            List<Map<String, String>> currentUserParts = new ArrayList<>();
            Map<String, String> currentTextPart = new HashMap<>();
            currentTextPart.put("text", message);
            currentUserParts.add(currentTextPart);
            currentUserMsg.put("parts", currentUserParts);
            history.add(currentUserMsg);
            
            // 添加历史对话记录到请求体
            requestBody.put("contents", history);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", webApiConfig.getGeminiApiKey());
            headers.setAcceptCharset(List.of(java.nio.charset.StandardCharsets.UTF_8));
            
            // 打印请求体用于调试
            // logger.info("发送Gemini API请求: {}", requestBody);
            
            // 构造HTTP请求
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            // 发送POST请求
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                geminiApiUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<Map<String, Object>>(){}
            );
            
            // 解析响应
            Map<String, Object> responseData = response.getBody();
            if (responseData == null) {
                return "AI回复失败：空响应。";
            }
            
            // logger.info("收到Gemini API响应: {}", responseData);
            
            // 提取AI回复内容
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) responseData.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                if (content != null) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, String>> parts = (List<Map<String, String>>) content.get("parts");
                    if (parts != null && !parts.isEmpty() && parts.get(0).containsKey("text")) {
                        return parts.get(0).get("text");
                    }
                }
            }
            
            return "获取AI回复失败：响应格式无效。";
        } catch (Exception e) {
            logger.error("AI回复请求失败", e);
            return "获取AI回复失败：" + e.getMessage();
        }
    }
}