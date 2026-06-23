package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;
//模拟面试记录实体类
@Data
public class InterviewRecord {
    private Long id;
    private Long userId;
    private String jobPosition;
    private String difficulty;
    private String question;
    private String userAnswer;
    private String aiFeedback;
    private Integer score;
    private String status;
    private LocalDateTime createTime;
    private Integer deleted;
}