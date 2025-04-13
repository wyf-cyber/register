package com.itheima.controller;

import com.itheima.service.webToolsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tools")
public class webToolsController {

    @Autowired
    private webToolsService webToolsService;

    @GetMapping("/Captcha")
    public ResponseEntity<?> generateCaptcha() {
        return webToolsService.getCaptcha();
    }

    @GetMapping("/QRCode")
    public String generateQRCode(@RequestParam String username) {
        return webToolsService.getQRCode(username);
    }

    @GetMapping("/getAIResponse")
    public String getAIResponse(@RequestParam String message) {
        return webToolsService.getAIResponse(message);
    }
}