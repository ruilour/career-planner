package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.CareerRecommendation;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CareerRecommendationMapper {

    @Insert("INSERT INTO career_recommendation (user_id, job_target, current_skills, experience_years, education, recommendation) " +
            "VALUES (#{userId}, #{jobTarget}, #{currentSkills}, #{experienceYears}, #{education}, #{recommendation})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CareerRecommendation record);

    @Select("SELECT * FROM career_recommendation WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<CareerRecommendation> selectByUserId(Long userId);

    @Select("SELECT * FROM career_recommendation WHERE id = #{id} AND deleted = 0")
    CareerRecommendation selectById(Long id);

    @Update("UPDATE career_recommendation SET deleted = 1 WHERE id = #{id}")
    int deleteById(Long id);
}