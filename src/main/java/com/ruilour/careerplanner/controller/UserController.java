package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.User;
import com.ruilour.careerplanner.mapper.UserMapper;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    /**
     * 模拟 VIP 付费升级
     */
    @PostMapping("/upgrade-vip")
    @Transactional
    public Map<String, Object> upgradeVip(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 从 Header 中提取 Token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                result.put("code", 401);
                result.put("message", "未登录或 Token 无效");
                return result;
            }
            String token = authHeader.substring(7);

            // 2. 解析 Token 获取用户 ID
            Long userId = JwtUtil.getUserIdFromToken(token);

            // 3. 查询用户是否存在
            User user = userMapper.selectById(userId); // 注意：UserMapper 里需要加 selectById 方法
            if (user == null) {
                result.put("code", 404);
                result.put("message", "用户不存在");
                return result;
            }

            // 4. 如果已经是 VIP，直接返回提示（防止重复扣费模拟）
            if (user.getVipStatus() == 1) {
                result.put("code", 200);
                result.put("message", "您已经是尊贵的 VIP 会员！");
                return result;
            }

            // 5. 核心业务：更新状态为 VIP，过期时间设为 30 天后
            LocalDateTime expireTime = LocalDateTime.now().plusDays(30);
            int updateRows = userMapper.updateVipStatus(userId, 1, expireTime);

            if (updateRows > 0) {
                result.put("code", 200);
                result.put("message", "🎉 支付成功！您已升级为 VIP，有效期至：" + expireTime);
            } else {
                result.put("code", 500);
                result.put("message", "升级失败，请稍后重试");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "系统异常：" + e.getMessage());
        }
        return result;
    }
}