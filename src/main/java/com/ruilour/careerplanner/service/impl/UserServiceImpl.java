package com.ruilour.careerplanner.service.impl;

import com.ruilour.careerplanner.entity.Token;
import com.ruilour.careerplanner.entity.User;
import com.ruilour.careerplanner.mapper.TokenMapper;
import com.ruilour.careerplanner.mapper.UserMapper;
import com.ruilour.careerplanner.service.UserService;
import com.ruilour.careerplanner.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TokenMapper tokenMapper;

    // 密码加密器（Spring Security提供）
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public boolean register(String username, String password, String email) {
        // 1. 检查用户名是否已存在
        if (userMapper.selectByUsername(username) != null) {
            return false; // 用户名已被占用
        }
        // 2. 创建用户对象，密码加密后存入
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password)); // 核心：加密存库
        user.setEmail(email);
        user.setStatus("PENDING");   // 新增：待审核
        // role默认USER, vip_status默认0, 在insert语句里写了
        return userMapper.insert(user) > 0;
    }

    @Override
    @Transactional  // 事务：登录插Token失败要回滚
    public String login(String username, String password) {
        // 1. 根据用户名查用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            return null; // 用户不存在
        }
        // 检查是否已审批通过
        if (!"APPROVED".equals(user.getStatus())) {
            return null;  // 登录失败，前端可提示“账号待审核或已驳回”
        }
        // 2. 校验密码（用BCrypt匹配）
        if (!encoder.matches(password, user.getPassword())) {
            return null; // 密码错误
        }
        // 3. 生成JWT Token
        String jwtToken = JwtUtil.generateToken(user.getId(), user.getUsername());

        // 4. 让该用户之前的所有Token失效（保证同一时间只有一个设备登录，企业级安全）
        tokenMapper.invalidateByUserId(user.getId());

        // 5. 将新Token存入数据库
        Token tokenEntity = new Token();
        tokenEntity.setUserId(user.getId());
        tokenEntity.setToken(jwtToken);
        // 计算过期时间（7天后）
        LocalDateTime expireTime = LocalDateTime.now().plusDays(7);
        tokenEntity.setExpireTime(expireTime);
        tokenMapper.insert(tokenEntity);

        return jwtToken;
    }
    @Override
    public List<User> getPendingUsers() {
        return userMapper.selectPendingUsers();
    }

    @Override
    public boolean approveUser(Long userId) {
        return userMapper.updateStatus(userId, "APPROVED") > 0;
    }

    @Override
    public boolean rejectUser(Long userId) {
        return userMapper.updateStatus(userId, "REJECTED") > 0;
    }


    @Override
    public User getUserById(Long userId) {
        // 调用 Mapper 根据 ID 查询用户
        return userMapper.selectById(userId);
    }
}