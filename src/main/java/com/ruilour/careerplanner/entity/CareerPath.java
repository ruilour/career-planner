package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data  // Lombok自动生成getter/setter
public class CareerPath {
    private Long id;
    private Long userId;
    private String jobName;
    private String steps;    // 对应数据库的 longtext-AI 生成的详细步骤（Markdown格式）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted; // 逻辑删除字段
}
