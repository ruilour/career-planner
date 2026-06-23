package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.InterviewRecord;
import com.ruilour.careerplanner.service.InterviewService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/interview")
public class InterviewController {

    @Autowired
    private InterviewService service;

    // 页面
    @GetMapping
    public String page(Model model) {
        return "interview";
    }

    // 面试历史记录页面
    @GetMapping("/history")
    public String historyPage() {
        return "interview-history";
    }

    // ========== API 接口 ==========

    // 生成面试题
    @PostMapping("/api/generate")
    @ResponseBody
    public Map<String, Object> generateQuestion(
            @RequestParam String jobPosition,
            @RequestParam(defaultValue = "medium") String difficulty,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            InterviewRecord record = service.generateQuestion(userId, jobPosition, difficulty);

            result.put("code", 200);
            result.put("data", record);
            result.put("message", "面试题生成成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "生成失败：" + e.getMessage());
        }
        return result;
    }

    // 提交答案
    @PostMapping("/api/submit")
    @ResponseBody
    public Map<String, Object> submitAnswer(
            @RequestParam Long recordId,
            @RequestParam String answer,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            JwtUtil.getUserIdFromToken(token);

            InterviewRecord record = service.submitAnswer(recordId, answer);

            result.put("code", 200);
            result.put("data", record);
            result.put("message", "提交成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "提交失败：" + e.getMessage());
        }
        return result;
    }

    // 历史记录列表
    @GetMapping("/api/history")
    @ResponseBody
    public Map<String, Object> history(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            List<InterviewRecord> list = service.getHistory(userId);
            result.put("code", 200);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

    // 🔥 新增：详情接口
    @GetMapping("/api/detail/{id}")
    @ResponseBody
    public Map<String, Object> detail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            InterviewRecord record = service.getDetail(id);
            if (record != null) {
                result.put("code", 200);
                result.put("data", record);
            } else {
                result.put("code", 404);
                result.put("message", "记录不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取详情失败：" + e.getMessage());
        }
        return result;
    }

    // 待回答的题目
    @GetMapping("/api/pending")
    @ResponseBody
    public Map<String, Object> pending(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            InterviewRecord record = service.getPendingQuestion(userId);
            result.put("code", 200);
            result.put("data", record);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }

    // 删除记录
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