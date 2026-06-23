package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.User;

import java.util.List;

public interface UserService {
    // 注册
    boolean register(String username, String password, String email);

    // 登录（验证密码，返回生成的Token）
    String login(String username, String password);

    List<User> getPendingUsers();      // 获取待审批用户
    boolean approveUser(Long userId);  // 通过审批
    boolean rejectUser(Long userId);   // 驳回审批

    User getUserById(Long userId);
}