package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.CareerPath;
import java.util.List;

public interface CareerPathService {
    // 生成规划（先查缓存，无则调用AI生成并缓存）
    String generatePlan(Long userId, String jobName) throws Exception;

    // 获取用户所有历史规划
    List<CareerPath> getUserHistory(Long userId);
    // 获取规划详情
    CareerPath getDetail(Long id);
    // 删除规划（逻辑删除）
    boolean deletePlan(Long planId);
}