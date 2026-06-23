package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.CareerPath;
import com.ruilour.careerplanner.service.CareerPathService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/career")
public class CareerController {

    @Autowired
    private CareerPathService careerPathService;

    // 页面：AI 职业规划页面
    @GetMapping("/plan")
    public String planPage(Model model) {
        return "career-plan";  // 对应 career-plan.html
    }

    // 页面：历史规划列表
    @GetMapping("/history")
    public String historyPage(Model model) {
        return "career-history"; // 对应 career-history.html
    }

    // API：生成职业规划（需要 Token）
    @PostMapping("/api/generate")
    @ResponseBody
    public Map<String, Object> generatePlan(
            @RequestParam String jobName,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 解析 Token 获取用户ID
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            // 2. 生成规划（自动查缓存）
            String plan = careerPathService.generatePlan(userId, jobName);

            result.put("code", 200);
            result.put("message", "生成成功");
            result.put("data", plan);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成失败：" + e.getMessage());
        }
        return result;
    }

    // API：获取用户历史规划
    @GetMapping("/api/history")
    @ResponseBody
    public Map<String, Object> getHistory(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            List<CareerPath> history = careerPathService.getUserHistory(userId);
            result.put("code", 200);
            result.put("data", history);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取历史失败：" + e.getMessage());
        }
        return result;
    }

    // API：删除规划
    @DeleteMapping("/api/delete/{planId}")
    @ResponseBody
    public Map<String, Object> deletePlan(@PathVariable Long planId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = careerPathService.deletePlan(planId);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
    // 查看详情
    @GetMapping("/api/detail/{id}")
    @ResponseBody
    public Map<String, Object> detail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        CareerPath record = careerPathService.getDetail(id);
        if (record != null) {
            result.put("code", 200);
            result.put("data", record);
        } else {
            result.put("code", 404);
            result.put("message", "记录不存在");
        }
        return result;
    }
}