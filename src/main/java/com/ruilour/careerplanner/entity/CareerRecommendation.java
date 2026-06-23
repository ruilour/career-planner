package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;
//职业推荐记录实体类
@Data
public class CareerRecommendation {
    private Long id;
    private Long userId;
    private String jobTarget;
    private String currentSkills;
    private Integer experienceYears;
    private String education;
    private String recommendation;
    private LocalDateTime createTime;
    private Integer deleted;
}