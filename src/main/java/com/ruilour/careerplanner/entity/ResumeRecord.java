package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;
//简历诊断记录
@Data
public class ResumeRecord {
    private Long id;
    private Long userId;
    private String fileName;
    private String filePath;
    private String parsedContent;
    private String diagnosis;
    private Integer score;
    private LocalDateTime createTime;
    private Integer deleted;
}