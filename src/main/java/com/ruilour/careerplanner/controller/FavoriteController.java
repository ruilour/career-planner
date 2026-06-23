package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.Video;
import com.ruilour.careerplanner.mapper.FavoriteMapper;
import com.ruilour.careerplanner.mapper.VideoMapper;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FavoriteController {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private VideoMapper videoMapper;

    // 我的收藏页面
    @GetMapping("/my-favorites")
    public String myFavoritesPage() {
        return "my-favorites";
    }

    // 【API】获取我的收藏列表
    @GetMapping("/api/favorites")
    @ResponseBody
    public Map<String, Object> getFavorites(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                result.put("code", 401);
                result.put("message", "请先登录");
                return result;
            }
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            List<Video> list = favoriteMapper.selectFavoritesByUserId(userId);
            result.put("code", 200);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取收藏失败：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    @GetMapping("/api/favorite/check")
    @ResponseBody
    public Map<String, Object> checkFavorite(@RequestParam("videoid") Long videoId,
                                             @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("🔍 checkFavorite 收到 videoId: " + videoId + ", 类型: " + (videoId != null ? videoId.getClass().getName() : "null"));

            if (videoId == null || videoId <= 0) {
                result.put("code", 400);
                result.put("message", "无效的视频ID");
                result.put("favorited", false);
                result.put("favoriteCount", 0);
                return result;
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                result.put("code", 401);
                result.put("message", "请先登录");
                result.put("favorited", false);
                result.put("favoriteCount", 0);
                return result;
            }

            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            int exists = favoriteMapper.checkFavorite(userId, videoId);
            Integer count = favoriteMapper.getFavoriteCount(videoId);
            if (count == null) {
                count = 0;
            }

            result.put("code", 200);
            result.put("favorited", exists > 0);
            result.put("favoriteCount", count);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "检查失败：" + e.getMessage());
            result.put("favorited", false);
            result.put("favoriteCount", 0);
            e.printStackTrace();
        }
        return result;
    }

    @PostMapping("/api/favorite/toggle")
    @ResponseBody
    public Map<String, Object> toggleFavorite(@RequestParam("videoId") Long videoId,
                                              @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("🔄 toggleFavorite 收到 videoId: " + videoId);

            if (videoId == null || videoId <= 0) {
                result.put("code", 400);
                result.put("message", "无效的视频ID");
                result.put("favorited", false);
                result.put("favoriteCount", 0);
                return result;
            }

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                result.put("code", 401);
                result.put("message", "请先登录");
                result.put("favorited", false);
                result.put("favoriteCount", 0);
                return result;
            }

            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            int exists = favoriteMapper.checkFavorite(userId, videoId);

            boolean favorited;
            if (exists > 0) {
                favoriteMapper.delete(userId, videoId);
                favoriteMapper.decrementFavoriteCount(videoId);
                favorited = false;
                result.put("message", "已取消收藏");
            } else {
                favoriteMapper.insert(userId, videoId);
                favoriteMapper.incrementFavoriteCount(videoId);
                favorited = true;
                result.put("message", "收藏成功 ❤️");
            }

            Integer count = favoriteMapper.getFavoriteCount(videoId);
            if (count == null) {
                count = 0;
            }

            result.put("code", 200);
            result.put("favorited", favorited);
            result.put("favoriteCount", count);

        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "操作失败：" + e.getMessage());
            result.put("favorited", false);
            result.put("favoriteCount", 0);
        }
        return result;
    }
}