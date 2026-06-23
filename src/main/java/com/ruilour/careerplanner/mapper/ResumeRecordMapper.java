package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.ResumeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResumeRecordMapper {

    @Insert("INSERT INTO resume_record (user_id, file_name, file_path, parsed_content, diagnosis, score) " +
            "VALUES (#{userId}, #{fileName}, #{filePath}, #{parsedContent}, #{diagnosis}, #{score})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResumeRecord record);

    @Select("SELECT * FROM resume_record WHERE user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<ResumeRecord> selectByUserId(Long userId);

    @Select("SELECT * FROM resume_record WHERE id = #{id} AND deleted = 0")
    ResumeRecord selectById(Long id);

    @Update("UPDATE resume_record SET diagnosis = #{diagnosis}, score = #{score} WHERE id = #{id}")
    int updateDiagnosis(@Param("id") Long id, @Param("diagnosis") String diagnosis, @Param("score") Integer score);

    @Update("UPDATE resume_record SET deleted = 1 WHERE id = #{id}")
    int deleteById(Long id);
}