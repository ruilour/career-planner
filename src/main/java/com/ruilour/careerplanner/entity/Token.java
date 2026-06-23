package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Token {
    private Long id;
    private Long userId;
    private String token;
    private LocalDateTime expireTime;
    private Integer isValid;      // 1有效 0失效
    private LocalDateTime createTime;
}