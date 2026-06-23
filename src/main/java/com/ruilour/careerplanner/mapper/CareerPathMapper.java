package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.CareerPath;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CareerPathMapper {

    // 保存AI生成的规划
    @Insert("INSERT INTO career_path (user_id, job_name, steps, create_time) VALUES (#{userId}, #{jobName}, #{steps}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CareerPath careerPath);

    // 根据用户ID和岗位名查询是否已有缓存
    @Select("SELECT * FROM career_path WHERE user_id = #{userId} AND job_name = #{jobName} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    CareerPath selectByUserIdAndJob(@Param("userId") Long userId, @Param("jobName") String jobName);

    // 查询用户的所有历史规划
    @Select("SELECT * FROM career_path WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<CareerPath> selectByUserId(Long userId);

    // 逻辑删除（软删除）
    @Update("UPDATE career_path SET deleted = 1 WHERE id = #{id}")
    int deleteById(Long id);
    // 根据ID查询规划（未删除）
    @Select("SELECT * FROM career_path WHERE id = #{id} AND deleted = 0")
    CareerPath selectById(Long id);
}