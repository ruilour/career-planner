package com.ruilour.careerplanner.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String role;          // ADMIN 或 USER
    private Integer vipStatus;    // 0普通 1VIP
    private String status;  // PENDING, APPROVED, REJECTED
    private LocalDateTime vipExpireDate;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;

}