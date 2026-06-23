package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.ResumeRecord;
import com.ruilour.careerplanner.service.ResumeService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private ResumeService service;

    // 页面
    @GetMapping
    public String page() {
        return "resume";
    }

    // 简历诊断历史记录页面
    @GetMapping("/history")
    public String historyPage() {
        return "resume-history";
    }

    // ========== API 接口 ==========

    // 上传并诊断
    @PostMapping("/api/upload")
    @ResponseBody
    public Map<String, Object> uploadAndDiagnose(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            if (file.isEmpty()) {
                result.put("code", 400);
                result.put("message", "请选择文件");
                return result;
            }

            ResumeRecord record = service.uploadAndDiagnose(userId, file);

            result.put("code", 200);
            result.put("data", record);
            result.put("message", "诊断成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "操作失败：" + e.getMessage());
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
            List<ResumeRecord> list = service.getHistory(userId);
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
            ResumeRecord record = service.getDetail(id);
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