//package com.ruilour.careerplanner.service.impl;
//
//import com.ruilour.careerplanner.entity.Video;
//import com.ruilour.careerplanner.mapper.UserMapper;
//import com.ruilour.careerplanner.mapper.VideoMapper;
//import com.ruilour.careerplanner.service.VideoService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import java.util.List;
//
//@Service
//public class VideoServiceImpl implements VideoService {
//    @Autowired
//    private UserMapper userMapper; // 注入 UserMapper 用于校验角色
//
//    @Autowired
//    private VideoMapper videoMapper;
//
//    @Override
//    public List<Video> getAllApprovedVideos() {
//        return videoMapper.selectAllApproved();
//    }
//
//    @Override
//    public boolean submitVideo(Video video, Long userId) {
//        // 设置投稿人ID，状态默认为 PENDING（在 insert 语句中已写死）
//        video.setUploadUserId(userId);
//        // 若前端未传 isVipOnly，默认 0（免费）
//        if (video.getIsVipOnly() == null) {
//            video.setIsVipOnly(0);
//        }
//        return videoMapper.insert(video) > 0;
//    }
//
//    @Override
//    public List<Video> getMySubmissions(Long userId) {
//        return videoMapper.selectByUploadUserId(userId);
//    }
//
//    @Override
//    public List<Video> getPendingVideos() {
//        return videoMapper.selectPendingVideos();
//    }
//
//    @Override
//    public boolean approveVideo(Long videoId) {
//        // 通过：状态改为 APPROVED，驳回原因置空
//        return videoMapper.updateStatus(videoId, "APPROVED", null) > 0;
//    }
//
//    @Override
//    public boolean rejectVideo(Long videoId, String reason) {
//        // 驳回：状态改为 REJECTED，记录原因
//        return videoMapper.updateStatus(videoId, "REJECTED", reason) > 0;
//    }
//
//
//}
package com.ruilour.careerplanner.service.impl;

import com.alibaba.fastjson.JSON;
import com.ruilour.careerplanner.entity.Video;
import com.ruilour.careerplanner.mapper.VideoMapper;
import com.ruilour.careerplanner.service.VideoService;
import com.ruilour.careerplanner.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private RedisUtil redisUtil;

    private static final String VIDEO_CACHE_KEY = "videos:approved";
    @Override
    public List<Video> getAllApprovedVideos() {
        // 直接查数据库，暂时不用缓存
        return videoMapper.selectAllApproved();
    }
//    @Override
//    public List<Video> getAllApprovedVideos() {
//        // 1. 先从 Redis 获取
//        String cached = redisUtil.get(VIDEO_CACHE_KEY);
//        if (cached != null) {
//            System.out.println("✅ 命中 Redis 缓存，直接返回");
//            return JSON.parseArray(cached, Video.class);
//        }
//
//        // 2. 缓存未命中，查数据库
//        System.out.println("❌ 缓存未命中，查询数据库");
//        List<Video> videos = videoMapper.selectAllApproved();
//
//        // 3. 写入 Redis（5分钟过期）
//        if (videos != null && !videos.isEmpty()) {
//            redisUtil.set(VIDEO_CACHE_KEY, JSON.toJSONString(videos), 5, TimeUnit.MINUTES);
//        }
//
//        return videos;
//    }

    // 清除缓存（投稿/审核后调用）
    public void clearVideoCache() {
        redisUtil.delete(VIDEO_CACHE_KEY);
        System.out.println("🗑️ 已清除视频缓存");
    }

    @Override
    public boolean submitVideo(Video video, Long userId) {
        video.setUploadUserId(userId);
        video.setStatus("PENDING");
        if (video.getIsVipOnly() == null) video.setIsVipOnly(0);
        if (video.getViewCount() == null) video.setViewCount(0);
        int result = videoMapper.insert(video);
        // ✅ 投稿成功后清除缓存
        if (result > 0) {
            clearVideoCache();
        }
        return result > 0;
    }

    @Override
    public boolean approveVideo(Long videoId) {
        int result = videoMapper.updateStatus(videoId, "APPROVED", null);
        // ✅ 审核通过后清除缓存
        if (result > 0) {
            clearVideoCache();
        }
        return result > 0;
    }

    @Override
    public boolean rejectVideo(Long videoId, String reason) {
        int result = videoMapper.updateStatus(videoId, "REJECTED", reason);
        // ✅ 审核驳回后清除缓存
        if (result > 0) {
            clearVideoCache();
        }
        return result > 0;
    }

    @Override
    public List<Video> getMySubmissions(Long userId) {
        return videoMapper.selectByUploadUserId(userId);
    }

    @Override
    public List<Video> getPendingVideos() {
        return videoMapper.selectPendingVideos();
    }
}