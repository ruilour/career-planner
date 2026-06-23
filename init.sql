-- =============================================
-- 智能职业规划助手 - 数据库初始化脚本
-- 适用于 Docker 容器首次启动
-- =============================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS career_planner
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE career_planner;

-- =============================================
-- 2. 用户表（已添加 status 字段）
-- =============================================
CREATE TABLE IF NOT EXISTS `sys_user` (
                                          `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
                                          `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '用户状态：PENDING-待审核，APPROVED-已通过，REJECTED-已驳回',
    `vip_status` TINYINT DEFAULT 0 COMMENT 'VIP状态：0-普通，1-VIP',
    `vip_expire_date` DATETIME DEFAULT NULL COMMENT 'VIP到期时间',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_deleted` (`deleted`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 3. Token 表
-- =============================================
CREATE TABLE IF NOT EXISTS `sys_token` (
                                           `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Token ID',
                                           `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                           `token` VARCHAR(500) NOT NULL COMMENT 'Token值',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `is_valid` TINYINT DEFAULT 1 COMMENT '是否有效：0-无效，1-有效',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_is_valid` (`is_valid`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token表';

-- =============================================
-- 4. 视频表
-- =============================================
CREATE TABLE IF NOT EXISTS `video` (
                                       `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '视频ID',
                                       `title` VARCHAR(200) NOT NULL COMMENT '视频标题',
    `description` TEXT COMMENT '视频描述',
    `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
    `video_url` VARCHAR(500) NOT NULL COMMENT '视频文件URL',
    `author` VARCHAR(100) NOT NULL COMMENT '作者',
    `skill_tag` VARCHAR(100) DEFAULT NULL COMMENT '技能标签',
    `job_tag` VARCHAR(100) DEFAULT NULL COMMENT '职位标签',
    `is_vip_only` TINYINT DEFAULT 0 COMMENT 'VIP专享：0-否，1-是',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审核，APPROVED-已通过，REJECTED-已拒绝',
    `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
    `upload_user_id` BIGINT DEFAULT NULL COMMENT '上传用户ID',
    `view_count` INT DEFAULT 0 COMMENT '观看次数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏次数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_upload_user_id` (`upload_user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_skill_tag` (`skill_tag`),
    KEY `idx_job_tag` (`job_tag`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频表';

-- =============================================
-- 5. 打卡表
-- =============================================
CREATE TABLE IF NOT EXISTS `check_in` (
                                          `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '打卡ID',
                                          `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                          `check_in_date` DATE NOT NULL COMMENT '打卡日期',
                                          `continuous_days` INT DEFAULT 1 COMMENT '连续打卡天数',
                                          `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                          UNIQUE KEY `uk_user_date` (`user_id`, `check_in_date`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_check_in_date` (`check_in_date`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='打卡记录表';

-- =============================================
-- 6. 收藏表
-- =============================================
CREATE TABLE IF NOT EXISTS `user_favorite` (
                                               `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
                                               `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                               `video_id` BIGINT NOT NULL COMMENT '视频ID',
                                               `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                               `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
                                               UNIQUE KEY `uk_user_video` (`user_id`, `video_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_deleted` (`deleted`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- =============================================
-- 7. AI 职业规划表
-- =============================================
CREATE TABLE IF NOT EXISTS `career_path` (
                                             `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '规划ID',
                                             `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                             `job_name` VARCHAR(50) NOT NULL COMMENT '目标职位',
    `steps` LONGTEXT NOT NULL COMMENT '规划步骤（JSON或结构化文本）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI职业规划表';

-- =============================================
-- 8. 职业推荐表
-- =============================================
CREATE TABLE IF NOT EXISTS `career_recommendation` (
                                                       `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '推荐ID',
                                                       `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                                       `job_target` VARCHAR(100) DEFAULT NULL COMMENT '职业目标',
    `current_skills` TEXT COMMENT '当前技能',
    `experience_years` INT DEFAULT 0 COMMENT '经验年限',
    `education` VARCHAR(50) DEFAULT NULL COMMENT '学历',
    `recommendation` LONGTEXT NOT NULL COMMENT '推荐内容（结构化）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI职业推荐表';

-- =============================================
-- 9. 面试记录表
-- =============================================
CREATE TABLE IF NOT EXISTS `interview_record` (
                                                  `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
                                                  `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                                  `job_position` VARCHAR(100) NOT NULL COMMENT '面试职位',
    `difficulty` VARCHAR(20) DEFAULT 'medium' COMMENT '难度：easy/medium/hard',
    `question` TEXT NOT NULL COMMENT '面试问题',
    `user_answer` TEXT COMMENT '用户回答',
    `ai_feedback` LONGTEXT COMMENT 'AI反馈',
    `score` INT DEFAULT 0 COMMENT '评分',
    `status` VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待处理，completed-已完成',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='面试记录表';

-- =============================================
-- 10. 简历诊断表
-- =============================================
CREATE TABLE IF NOT EXISTS `resume_record` (
                                               `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '简历ID',
                                               `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                               `file_name` VARCHAR(200) DEFAULT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) DEFAULT NULL COMMENT '文件存储路径',
    `parsed_content` LONGTEXT COMMENT '解析后的内容',
    `diagnosis` LONGTEXT COMMENT '诊断报告',
    `score` INT DEFAULT 0 COMMENT '评分',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='简历诊断记录表';

-- =============================================
-- 11. 插入管理员账号
-- =============================================
-- 管理员：admin / 密码：admin123（BCrypt加密）
INSERT IGNORE INTO `sys_user` (`username`, `password`, `role`, `email`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKDO1XHG', 'ADMIN', 'admin@careerplanner.com', 'APPROVED');

-- =============================================
-- 12. 插入示例数据（可选，方便测试）
-- =============================================
INSERT IGNORE INTO `video` (`title`, `description`, `author`, `skill_tag`, `job_tag`, `status`, `upload_user_id`) VALUES
('Spring Boot 3 快速入门', '从零开始学习 Spring Boot 3，手把手搭建企业级应用', '技术小课堂', 'Java', '后端开发', 'APPROVED', 1),
('Redis 实战指南', 'Redis 缓存、分布式锁、消息队列实战', '数据库大咖', 'Redis', '后端开发', 'APPROVED', 1),
('Vue3 组合式API 深度解析', 'Vue3 核心特性详解，从 Options API 到 Composition API', '前端老司机', 'Vue', '前端开发', 'APPROVED', 1),
('MySQL 索引优化与执行计划', '深入理解 MySQL 索引原理，SQL 性能调优', 'DBA 之路', 'MySQL', '数据库开发', 'APPROVED', 1);

-- 插入一个普通测试用户（密码：123456）
INSERT IGNORE INTO `sys_user` (`username`, `password`, `email`, `role`, `status`)
VALUES ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKDO1XHG', 'test@test.com', 'USER', 'APPROVED');

-- =============================================
-- 13. 验证
-- =============================================
SELECT '✅ 数据库初始化完成！' AS Status;
SELECT COUNT(*) AS '总表数' FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'career_planner';
SHOW TABLES;
