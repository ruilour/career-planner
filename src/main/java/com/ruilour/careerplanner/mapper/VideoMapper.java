package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.Video;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface VideoMapper {

    // ---- 原有方法 ----
    @Select("SELECT * FROM video WHERE status = 'APPROVED' AND deleted = 0 ORDER BY create_time DESC")
    List<Video> selectAllApproved();

    // ---- 新增方法（投稿+审核） ----
    @Insert("INSERT INTO video (title, description, cover_url, video_url, author, skill_tag, job_tag, is_vip_only, status, upload_user_id, create_time) " +
            "VALUES (#{title}, #{description}, #{coverUrl}, #{videoUrl}, #{author}, #{skillTag}, #{jobTag}, #{isVipOnly}, 'PENDING', #{uploadUserId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Video video);

    @Select("SELECT * FROM video WHERE upload_user_id = #{userId} AND deleted = 0 ORDER BY create_time DESC")
    List<Video> selectByUploadUserId(Long userId);

    @Select("SELECT * FROM video WHERE status = 'PENDING' AND deleted = 0 ORDER BY create_time ASC")
    List<Video> selectPendingVideos();

    @Update("UPDATE video SET status = #{status}, reject_reason = #{rejectReason} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("rejectReason") String rejectReason);
    // 根据ID查询视频
    @Select("SELECT * FROM video WHERE id = #{id} AND deleted = 0")
    Video selectById(Long id);

    // 增加观看数
    @Update("UPDATE video SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(Long id);
}