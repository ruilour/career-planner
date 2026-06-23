package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.Video;
import java.util.List;

public interface VideoService {
    // 展示所有已通过视频（首页用）
    List<Video> getAllApprovedVideos();

    // 投稿
    boolean submitVideo(Video video, Long userId);

    // 我的投稿
    List<Video> getMySubmissions(Long userId);

    // 待审核列表（管理员）
    List<Video> getPendingVideos();

    // 审核通过
    boolean approveVideo(Long videoId);

    // 审核驳回
    boolean rejectVideo(Long videoId, String reason);
}