package com.ruilour.careerplanner.mapper;

import com.ruilour.careerplanner.entity.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    // 收藏视频
    @Insert("INSERT IGNORE INTO user_favorite (user_id, video_id) VALUES (#{userId}, #{videoId})")
    int insert(@Param("userId") Long userId, @Param("videoId") Long videoId);

    // 取消收藏
    @Delete("DELETE FROM user_favorite WHERE user_id = #{userId} AND video_id = #{videoId}")
    int delete(@Param("userId") Long userId, @Param("videoId") Long videoId);

    // 检查是否已收藏
    @Select("SELECT COUNT(*) FROM user_favorite WHERE user_id = #{userId} AND video_id = #{videoId}")
    int checkFavorite(@Param("userId") Long userId, @Param("videoId") Long videoId);

    // 获取用户收藏的视频列表
    @Select("SELECT v.id, v.title, v.description, v.cover_url, v.video_url, v.author, v.skill_tag, v.job_tag, v.is_vip_only, v.status, v.view_count, v.favorite_count, v.create_time " +
            "FROM video v " +
            "INNER JOIN user_favorite f ON v.id = f.video_id " +
            "WHERE f.user_id = #{userId} AND v.deleted = 0 AND v.status = 'APPROVED' " +
            "ORDER BY f.create_time DESC")
    List<Video> selectFavoritesByUserId(Long userId);

    // ✅ 修复：使用 Integer 类型，处理 NULL 值
    @Select("SELECT IFNULL(favorite_count, 0) FROM video WHERE id = #{videoId}")
    Integer getFavoriteCount(Long videoId);

    // 更新视频收藏数（+1）
    @Update("UPDATE video SET favorite_count = IFNULL(favorite_count, 0) + 1 WHERE id = #{videoId}")
    int incrementFavoriteCount(Long videoId);

    // 更新视频收藏数（-1）
    @Update("UPDATE video SET favorite_count = IFNULL(favorite_count, 0) - 1 WHERE id = #{videoId} AND favorite_count > 0")
    int decrementFavoriteCount(Long videoId);
}