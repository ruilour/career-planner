package com.ruilour.careerplanner.service;

import com.ruilour.careerplanner.entity.CareerRecommendation;
import java.util.List;

public interface CareerRecommendationService {

    /**
     * 生成职业推荐
     */
    CareerRecommendation generateRecommendation(Long userId, String jobTarget,
                                                String currentSkills,
                                                Integer experienceYears,
                                                String education) throws Exception;

    /**
     * 获取用户历史推荐记录
     */
    List<CareerRecommendation> getHistory(Long userId);

    /**
     * 获取推荐详情
     */
    CareerRecommendation getDetail(Long id);

    /**
     * 删除推荐记录
     */
    boolean deleteRecord(Long id);
}