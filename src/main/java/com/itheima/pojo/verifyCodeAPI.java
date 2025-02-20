package com.itheima.pojo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
// 封装验证码应答消息
public class verifyCodeAPI {
    private String verifyCode;
    private String verifyCodeImgUrl;
}