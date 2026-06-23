package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Video {
    private Long id;
    private String title;
    private String description;
    private String coverUrl;      // 封面路径
    private String videoUrl;      // 视频路径
    private String author;
    private String skillTag;
    private String jobTag;
    private Integer isVipOnly;
    private String status;
    private String rejectReason;
    private Long uploadUserId;
    private Integer viewCount;
    private Integer favoriteCount;  // 收藏数
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
}