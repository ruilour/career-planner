package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.InterviewRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InterviewRecordMapper {

    @Insert("INSERT INTO interview_record (user_id, job_position, difficulty, question, status) " +
            "VALUES (#{userId}, #{jobPosition}, #{difficulty}, #{question}, 'pending')")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InterviewRecord record);

    @Update("UPDATE interview_record SET user_answer = #{userAnswer}, ai_feedback = #{aiFeedback}, score = #{score}, status = 'completed' WHERE id = #{id}")
    int updateFeedback(InterviewRecord record);

    @Select("SELECT * FROM interview_record WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<InterviewRecord> selectByUserId(Long userId);

    @Select("SELECT * FROM interview_record WHERE user_id = #{userId} AND status = 'pending' AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    InterviewRecord selectPendingByUserId(Long userId);

    @Update("UPDATE interview_record SET deleted = 1 WHERE id = #{id}")
    int deleteById(Long id);
    // 根据ID查询（未删除）
    @Select("SELECT * FROM interview_record WHERE id = #{id} AND deleted = 0")
    InterviewRecord selectById(Long id);
}