package com.ruilour.careerplanner.controller;

import com.ruilour.careerplanner.entity.User;
import com.ruilour.careerplanner.entity.Video;
import com.ruilour.careerplanner.mapper.UserMapper;
import com.ruilour.careerplanner.mapper.VideoMapper;
import com.ruilour.careerplanner.service.VideoService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private UserMapper userMapper;

    // 文件上传根目录（从配置文件读取，默认 ./uploads）
    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    // ==================== 页面跳转 ====================

    @GetMapping("/videos")
    public String videoList(Model model) {
        List<Video> videoList = videoService.getAllApprovedVideos();
        model.addAttribute("videoList", videoList);
        return "video";
    }

    @GetMapping("/submit")
    public String submitPage() {
        return "submit";
    }

    @GetMapping("/my-submissions")
    public String mySubmissionsPage() {
        return "my-submissions";
    }

    @GetMapping("/play/{videoId}")
    public String playVideo(@PathVariable Long videoId,
                            @RequestParam(required = false) String token,
                            Model model,
                            HttpServletRequest request) {
        Video video = videoMapper.selectById(videoId);
        if (video == null || !"APPROVED".equals(video.getStatus())) {
            return "redirect:/videos";
        }

        boolean canWatch = true;
        if (video.getIsVipOnly() == 1) {
            String authToken = token;
            if (authToken == null || authToken.isEmpty()) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authToken = authHeader.substring(7);
                }
            }
            if (authToken != null && !authToken.isEmpty()) {
                try {
                    Long userId = JwtUtil.getUserIdFromToken(authToken);
                    User user = userMapper.selectById(userId);
                    canWatch = user != null && user.getVipStatus() == 1;
                } catch (Exception e) {
                    canWatch = false;
                }
            } else {
                canWatch = false;
            }
        }

        videoMapper.incrementViewCount(videoId);
        model.addAttribute("video", video);
        model.addAttribute("canWatch", canWatch);
        return "video-play";
    }

    // ==================== API 接口 ====================

    /**
     * 投稿接口（支持文件上传）
     * 使用 @RequestParam 接收文本字段，MultipartFile 接收文件
     */
    @PostMapping("/api/submit")
    @ResponseBody
    public Map<String, Object> submitVideo(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("author") String author,
            @RequestParam("skillTag") String skillTag,
            @RequestParam("jobTag") String jobTag,
            @RequestParam("isVipOnly") Integer isVipOnly,
            @RequestParam("coverFile") MultipartFile coverFile,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> result = new HashMap<>();
        try {
            // 1. 验证 Token，获取用户 ID
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);

            // 2. 保存文件
            String coverUrl = saveFile(coverFile, "covers");
            String videoUrl = saveFile(videoFile, "videos");

            // 3. 构建 Video 对象
            Video video = new Video();
            video.setTitle(title);
            video.setDescription(description);
            video.setAuthor(author);
            video.setSkillTag(skillTag);
            video.setJobTag(jobTag);
            video.setIsVipOnly(isVipOnly);
            video.setCoverUrl(coverUrl);
            video.setVideoUrl(videoUrl);

            // 4. 调用 Service 保存到数据库
            boolean success = videoService.submitVideo(video, userId);

            if (success) {
                result.put("code", 200);
                result.put("message", "投稿成功，等待管理员审核");
            } else {
                result.put("code", 500);
                result.put("message", "投稿失败，请稍后重试");
            }

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "投稿异常：" + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 通用文件保存方法
     * @param file 上传的文件
     * @param subDir 子目录（如 covers / videos）
     * @return 可访问的相对路径（如 /uploads/covers/xxx.jpg）
     */
    /**
     * 通用文件保存方法
     * 使用项目根目录的绝对路径，确保文件保存到正确位置
     */
    private String saveFile(MultipartFile file, String subDir) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;

        // ✅ 关键修复：使用项目根目录的绝对路径
        String projectRoot = System.getProperty("user.dir");
        String uploadPathStr = projectRoot + File.separator + "uploads" + File.separator + subDir;

        Path savePath = Paths.get(uploadPathStr);
        if (!Files.exists(savePath)) {
            Files.createDirectories(savePath);
        }
        Path filePath = savePath.resolve(newFilename);

        // 保存文件
        file.transferTo(filePath.toFile());

        // 返回可访问的 URL 路径
        return "/uploads/" + subDir + "/" + newFilename;
    }
    // ========== 原有 API（保持不变） ==========

    @GetMapping("/api/my-submissions")
    @ResponseBody
    public Map<String, Object> getMySubmissions(@RequestHeader("Authorization") String authHeader) {
        Map<String, Object> result = new HashMap<>();
        try {
            String token = authHeader.substring(7);
            Long userId = JwtUtil.getUserIdFromToken(token);
            List<Video> list = videoService.getMySubmissions(userId);
            result.put("code", 200);
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
        }
        return result;
    }
}