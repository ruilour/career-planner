package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.Token;
import org.apache.ibatis.annotations.*;

@Mapper
public interface TokenMapper {

    // 保存登录Token
    @Insert("INSERT INTO sys_token (user_id, token, expire_time, is_valid, create_time) " +
            "VALUES (#{userId}, #{token}, #{expireTime}, 1, NOW())")
    int insert(Token token);

    // 让旧Token失效（比如用户重新登录或退出时）
    @Update("UPDATE sys_token SET is_valid = 0 WHERE user_id = #{userId}")
    int invalidateByUserId(Long userId);

    // 根据Token字符串查询（后续做拦截器验证用）
    @Select("SELECT * FROM sys_token WHERE token = #{token} AND is_valid = 1")
    Token selectByToken(String token);
}