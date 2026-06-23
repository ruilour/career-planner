package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper  // 这个注解一定要加，让Spring识别
public interface UserMapper {
    // 根据用户名查询用户（含状态）（登录/注册校验用）
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    User selectByUsername(String username);

    // 查询待审批用户列表（管理员用）
    @Select("SELECT * FROM sys_user WHERE status = 'PENDING' AND deleted = 0 ORDER BY create_time DESC")
    List<User> selectPendingUsers();

    // 更新用户审批状态
    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    // 插入新用户（注册）
    @Insert("INSERT INTO sys_user (username, password, email, role, vip_status, create_time) " +
            "VALUES (#{username}, #{password}, #{email}, 'USER', 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // 返回自动生成的ID
    int insert(User user);
    // 根据 ID 查询用户
    @Select("SELECT * FROM sys_user WHERE id = #{id} AND deleted = 0")
    User selectById(Long id);

    // 更新 VIP 状态和过期时间
    @Update("UPDATE sys_user SET vip_status = #{vipStatus}, vip_expire_date = #{expireTime} WHERE id = #{userId}")
    int updateVipStatus(@Param("userId") Long userId,
                        @Param("vipStatus") Integer vipStatus,
                        @Param("expireTime") LocalDateTime expireTime);
}