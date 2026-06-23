package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.User;
import com.ruilour.careerplanner.service.UserService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    // 页面：待审批用户列表
    @GetMapping("/pending")
    public String pendingUsersPage(Model model) {
        List<User> pendingList = userService.getPendingUsers();
        model.addAttribute("pendingList", pendingList);
        return "admin-users";  // 对应 admin-users.html
    }

    // API：审批通过
    @PostMapping("/api/approve/{userId}")
    @ResponseBody
    public Map<String, Object> approve(@PathVariable Long userId,
                                       @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(authHeader)) {
            result.put("code", 403);
            result.put("message", "无权限");
            return result;
        }
        boolean success = userService.approveUser(userId);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "已通过审批" : "操作失败");
        return result;
    }

    // API：驳回
    @PostMapping("/api/reject/{userId}")
    @ResponseBody
    public Map<String, Object> reject(@PathVariable Long userId,
                                      @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(authHeader)) {
            result.put("code", 403);
            result.put("message", "无权限");
            return result;
        }
        boolean success = userService.rejectUser(userId);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "已驳回" : "操作失败");
        return result;
    }

    private boolean isAdmin(String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            User user = userService.getUserById(userId); // 你需要在 UserService 添加 getUserById
            return user != null && "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }
}