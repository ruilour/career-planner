package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.CheckIn;
import com.ruilour.careerplanner.service.CheckInService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    // 打卡页面
    @GetMapping("/checkin")
    public String checkInPage(Model model) {
        return "checkin";
    }

    // 【API】执行打卡
    @PostMapping("/api/checkin/do")
    @ResponseBody
    public Map<String, Object> doCheckIn(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            Map<String, Object> checkResult = checkInService.doCheckIn(userId);
            result.putAll(checkResult);

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "打卡失败：" + e.getMessage());
        }
        return result;
    }

    // 【API】获取打卡统计
    @GetMapping("/api/checkin/stats")
    @ResponseBody
    public Map<String, Object> getStats(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            Map<String, Object> stats = checkInService.getStats(userId);
            result.put("code", 200);
            result.put("data", stats);

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取统计失败：" + e.getMessage());
        }
        return result;
    }

    // 【API】获取本月打卡记录
    @GetMapping("/api/checkin/month")
    @ResponseBody
    public Map<String, Object> getMonthRecords(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            List<CheckIn> records = checkInService.getMonthRecords(userId);
            result.put("code", 200);
            result.put("data", records);

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取记录失败：" + e.getMessage());
        }
        return result;
    }
}