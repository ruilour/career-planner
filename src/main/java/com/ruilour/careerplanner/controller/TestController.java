package com.ruilour.careerplanner.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        // 从 Security 上下文中获取当前登录用户名
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // 这就是当前登录的用户名

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "恭喜！Token 验证通过！");
        result.put("username", username);
        return result;
    }
}