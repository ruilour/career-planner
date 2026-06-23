package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.CareerRecommendation;
import com.ruilour.careerplanner.service.CareerRecommendationService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/career-recommend")
public class CareerRecommendationController {

    @Autowired
    private CareerRecommendationService service;

    // 页面
    @GetMapping
    public String page() {
        return "career-recommend";
    }

    // 生成推荐
    @PostMapping("/api/generate")
    @ResponseBody
    public Map<String, Object> generate(
            @RequestParam String jobTarget,
            @RequestParam String currentSkills,
            @RequestParam Integer experienceYears,
            @RequestParam String education,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            CareerRecommendation record = service.generateRecommendation(
                    userId, jobTarget, currentSkills, experienceYears, education
            );

            result.put("code", 200);
            result.put("data", record);
            result.put("message", "生成成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成失败：" + e.getMessage());
        }
        return result;
    }

    // 历史记录
    @GetMapping("/api/history")
    @ResponseBody
    public Map<String, Object> history(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            List<CareerRecommendation> list = service.getHistory(userId);
            result.put("code", 200);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

    // 查看详情
    @GetMapping("/api/detail/{id}")
    @ResponseBody
    public Map<String, Object> detail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        CareerRecommendation record = service.getDetail(id);
        result.put("code", 200);
        result.put("data", record);
        return result;
    }
    // 职业推荐历史记录页面
    @GetMapping("/history")
    public String historyPage() {
        return "career-recommend-history";
    }
    // 删除推荐记录
    @DeleteMapping("/api/delete/{id}")
    @ResponseBody
    public Map<String, Object> deleteRecord(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        boolean success = service.deleteRecord(id);
        result.put("code", success ? 200 : 500);
        result.put("message", success ? "删除成功" : "删除失败");
        return result;
    }
}