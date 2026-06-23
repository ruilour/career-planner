package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.User;
import com.ruilour.careerplanner.entity.Video;
import com.ruilour.careerplanner.mapper.UserMapper;
import com.ruilour.careerplanner.service.VideoService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/pending")
    public String pendingPage(Model model) {
        List<Video> pendingList = videoService.getPendingVideos();
        // ✅ 避免 null，确保模板安全
        if (pendingList == null) {
            pendingList = new ArrayList<>();
        }
        model.addAttribute("pendingList", pendingList);
        return "admin-panel";
    }

    @PostMapping("/api/approve/{videoId}")
    @ResponseBody
    public Map<String, Object> approve(@PathVariable Long videoId,
                                       @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(authHeader)) {
            result.put("code", 403);
            result.put("message", "无权限，仅管理员可操作");
            return result;
        }
        boolean success = videoService.approveVideo(videoId);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "审核通过" : "操作失败");
        return result;
    }

    @PostMapping("/api/reject/{videoId}")
    @ResponseBody
    public Map<String, Object> reject(@PathVariable Long videoId,
                                      @RequestParam String reason,
                                      @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        if (!isAdmin(authHeader)) {
            result.put("code", 403);
            result.put("message", "无权限，仅管理员可操作");
            return result;
        }
        boolean success = videoService.rejectVideo(videoId, reason);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "已驳回" : "操作失败");
        return result;
    }

    private boolean isAdmin(String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            User user = userMapper.selectById(userId);
            return user != null && "ADMIN".equals(user.getRole());
        } catch (Exception e) {
            return false;
        }
    }
}