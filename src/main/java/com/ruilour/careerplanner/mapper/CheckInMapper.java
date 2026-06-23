package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.CheckIn;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CheckInMapper {

    // 插入打卡记录
    @Insert("INSERT INTO check_in (user_id, check_in_date, continuous_days) VALUES (#{userId}, #{checkInDate}, #{continuousDays})")
    int insert(CheckIn checkIn);

    // 查询今日是否已打卡
    @Select("SELECT * FROM check_in WHERE user_id = #{userId} AND check_in_date = #{date}")
    CheckIn selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    // 查询用户本月所有打卡记录
    @Select("SELECT * FROM check_in WHERE user_id = #{userId} AND YEAR(check_in_date) = #{year} AND MONTH(check_in_date) = #{month} ORDER BY check_in_date DESC")
    List<CheckIn> selectByUserIdAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    // 查询用户最近一条打卡记录（用于计算连续天数）
    @Select("SELECT * FROM check_in WHERE user_id = #{userId} ORDER BY check_in_date DESC LIMIT 1")
    CheckIn selectLastByUserId(Long userId);

    // 查询用户总打卡天数
    @Select("SELECT COUNT(*) FROM check_in WHERE user_id = #{userId}")
    int countByUserId(Long userId);
}