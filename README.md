# 智能职业规划助手

基于 Spring Boot 3 + DeepSeek AI 的全栈职业规划平台。

## 核心功能

| 功能模块 | 描述 |
|---------|------|
| AI 职业规划 | 输入目标岗位，AI 生成个性化学习路线图 |
| 模拟面试 | AI 出题，用户回答，即时评分反馈 |
| 简历诊断 | 上传简历，AI 解析并给出优化建议 |
| 视频学习 | B站风格视频列表，支持 VIP 权限控制 |
| VIP 付费 | 模拟支付升级 VIP（模拟）|
| 视频收藏 | 收藏喜欢的视频 |
| 每日打卡 | 坚持打卡，养成学习习惯 |

## 技术栈

- Spring Boot 3.4.3
- Spring Security + JWT
- MyBatis + MySQL 8.0
- Redis 7
- DeepSeek API
- Thymeleaf + Bootstrap
- Docker Compose

## 快速开始

### Docker 一键部署

```bash
git clone https://github.com/你的用户名/career-planner.git
cd career-planner
docker compose up -d --build
