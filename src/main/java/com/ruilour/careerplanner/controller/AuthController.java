package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.User;
import com.ruilour.careerplanner.mapper.UserMapper;
import com.ruilour.careerplanner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public Map<String, Object> register(@RequestParam String username,
                                        @RequestParam String password,
                                        @RequestParam(required = false) String email) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userService.register(username, password, email);
            if (success) {
                result.put("code", 200);
                result.put("message", "注册成功，请等待管理员审核");
            } else {
                result.put("code", 400);
                result.put("message", "用户名已存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "注册失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = userService.login(username, password);

            if (token != null) {
                User user = userMapper.selectByUsername(username);
                result.put("code", 200);
                result.put("message", "登录成功");
                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("userId", user.getId());
                data.put("username", user.getUsername());
                data.put("role", user.getRole());
                data.put("vipStatus", user.getVipStatus());
                result.put("data", data);
            } else {
                User user = userMapper.selectByUsername(username);

                if (user == null) {
                    result.put("code", 401);
                    result.put("message", "用户名或密码错误");
                    return result;
                }
                if (!"APPROVED".equals(user.getStatus())) {
                    result.put("code", 403);
                    result.put("message", "账号待审核或已被驳回，请联系管理员");
                    return result;
                }
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录失败：" + e.getMessage());
        }
        return result;
    }

}