package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CheckIn {
    private Long id;
    private Long userId;
    private LocalDate checkInDate;
    private Integer continuousDays;
    private LocalDateTime createTime;
}